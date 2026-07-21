package service;

import repository.FileSystemRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileOperationService {

    private final FileSystemRepository fileSystemRepository;

    public FileOperationService(FileSystemRepository fileSystemRepository) {
        this.fileSystemRepository = fileSystemRepository;
    }

    public void copy(File source, File destinationFolder) throws IOException {
        validateSource(source);
        validateDestinationFolder(destinationFolder);
        validateNotIntoItself(source, destinationFolder);

        File destination = new File(destinationFolder, source.getName());

        if (fileSystemRepository.isDirectory(source)) {
            copyFolder(source, destination);
        } else {
            fileSystemRepository.copyFile(source, destination);
        }
    }

    public void move(File source, File destinationFolder) throws IOException {
        validateSource(source);
        validateDestinationFolder(destinationFolder);
        validateNotIntoItself(source, destinationFolder);

        File destination = new File(destinationFolder, source.getName());

        try {
            fileSystemRepository.moveItem(source, destination);
        } catch (IOException exception) {
            // Moving a non-empty folder across file systems is not supported,
            // so fall back to copying and removing the source.
            copy(source, destinationFolder);
            deleteRecursively(source);
        }
    }

    public boolean rename(File source, String newName) {
        validateSource(source);

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be empty.");
        }

        File renamedFile = new File(source.getParentFile(), newName.trim());

        return fileSystemRepository.rename(source, renamedFile);
    }

    private void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        fileSystemRepository.createFolder(destinationFolder);

        for (File child : fileSystemRepository.getChildren(sourceFolder)) {
            File destinationChild = new File(destinationFolder, child.getName());

            if (fileSystemRepository.isDirectory(child)) {
                copyFolder(child, destinationChild);
            } else {
                fileSystemRepository.copyFile(child, destinationChild);
            }
        }
    }

    private void deleteRecursively(File file) throws IOException {
        for (File child : fileSystemRepository.getChildren(file)) {
            deleteRecursively(child);
        }

        fileSystemRepository.delete(file);
    }

    private void validateSource(File source) {
        if (!fileSystemRepository.exists(source)) {
            throw new IllegalArgumentException("Source does not exist.");
        }
    }

    private void validateDestinationFolder(File destinationFolder) {
        if (!fileSystemRepository.isDirectory(destinationFolder)) {
            throw new IllegalArgumentException("Invalid destination folder.");
        }
    }

    /**
     * Copying a folder into itself or into one of its own descendants would
     * recurse forever, because the copy keeps re-creating what it is reading.
     */
    private void validateNotIntoItself(File source, File destinationFolder) {
        if (!fileSystemRepository.isDirectory(source)) {
            return;
        }

        Path sourcePath = source.toPath().toAbsolutePath().normalize();
        Path destinationPath = destinationFolder.toPath().toAbsolutePath().normalize();

        if (destinationPath.startsWith(sourcePath)) {
            throw new IllegalArgumentException(
                    "Cannot copy or move a folder into itself."
            );
        }
    }
}
