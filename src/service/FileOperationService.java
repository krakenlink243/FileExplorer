package service;

import model.FileItem;
import repository.FileSystemRepository;

import java.io.IOException;
import java.nio.file.Path;

public class FileOperationService {

    private final FileSystemRepository fileSystemRepository;

    public FileOperationService(FileSystemRepository fileSystemRepository) {
        this.fileSystemRepository = fileSystemRepository;
    }

    public void copy(FileItem source, FileItem destinationFolder) throws IOException {
        validateSource(source);
        validateDestinationFolder(destinationFolder);
        validateNotIntoItself(source, destinationFolder);

        FileItem destination =
                fileSystemRepository.resolveChild(destinationFolder, source.getName());

        if (source.isDirectory()) {
            copyFolder(source, destination);
        } else {
            fileSystemRepository.copyFile(source, destination);
        }
    }

    public void move(FileItem source, FileItem destinationFolder) throws IOException {
        validateSource(source);
        validateDestinationFolder(destinationFolder);
        validateNotIntoItself(source, destinationFolder);

        FileItem destination =
                fileSystemRepository.resolveChild(destinationFolder, source.getName());

        try {
            fileSystemRepository.moveItem(source, destination);
        } catch (IOException exception) {
            // Moving a non-empty folder across file systems is not supported,
            // so fall back to copying and removing the source.
            copy(source, destinationFolder);
            deleteRecursively(source);
        }
    }

    public boolean rename(FileItem source, String newName) {
        validateSource(source);

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be empty.");
        }

        FileItem renamed =
                fileSystemRepository.resolveSibling(source, newName.trim());

        return fileSystemRepository.rename(source, renamed);
    }

    /**
     * Opens the item with whatever application the operating system has
     * registered for it.
     */
    public void open(FileItem item) throws IOException {
        validateSource(item);

        fileSystemRepository.open(item);
    }

    private void copyFolder(FileItem sourceFolder, FileItem destinationFolder) throws IOException {
        fileSystemRepository.createFolder(destinationFolder);

        for (FileItem child : fileSystemRepository.getChildren(sourceFolder)) {
            FileItem destinationChild =
                    fileSystemRepository.resolveChild(destinationFolder, child.getName());

            if (child.isDirectory()) {
                copyFolder(child, destinationChild);
            } else {
                fileSystemRepository.copyFile(child, destinationChild);
            }
        }
    }

    private void deleteRecursively(FileItem item) throws IOException {
        for (FileItem child : fileSystemRepository.getChildren(item)) {
            deleteRecursively(child);
        }

        fileSystemRepository.delete(item);
    }

    private void validateSource(FileItem source) {
        if (!fileSystemRepository.exists(source)) {
            throw new IllegalArgumentException("Source does not exist.");
        }
    }

    private void validateDestinationFolder(FileItem destinationFolder) {
        if (destinationFolder == null
                || !destinationFolder.isDirectory()
                || !fileSystemRepository.exists(destinationFolder)) {
            throw new IllegalArgumentException("Invalid destination folder.");
        }
    }

    /**
     * Copying a folder into itself or into one of its own descendants would
     * recurse forever, because the copy keeps re-creating what it is reading.
     *
     * This compares paths only, which is arithmetic on text and never reads
     * from storage.
     */
    private void validateNotIntoItself(FileItem source, FileItem destinationFolder) {
        if (!source.isDirectory()) {
            return;
        }

        Path sourcePath = Path.of(source.getPath()).toAbsolutePath().normalize();
        Path destinationPath = Path.of(destinationFolder.getPath()).toAbsolutePath().normalize();

        if (destinationPath.startsWith(sourcePath)) {
            throw new IllegalArgumentException(
                    "Cannot copy or move a folder into itself."
            );
        }
    }
}
