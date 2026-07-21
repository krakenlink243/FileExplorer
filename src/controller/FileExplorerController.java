package controller;

import model.FileItem;
import model.ItemProperty;
import repository.FileSystemRepository;
import service.FileOperationService;
import service.FilePropertyService;

import java.io.IOException;

public class FileExplorerController {
    private final FileSystemRepository fileSystemRepository;
    private final FileOperationService fileOperationService;
    private final FilePropertyService filePropertyService;

    public FileExplorerController(
            FileSystemRepository fileSystemRepository,
            FileOperationService fileOperationService,
            FilePropertyService filePropertyService
    ) {
        this.fileSystemRepository = fileSystemRepository;
        this.fileOperationService = fileOperationService;
        this.filePropertyService = filePropertyService;
    }

    public FileItem[] getRoots() {
        return fileSystemRepository.getRoots();
    }

    public FileItem[] getChildren(FileItem folder) {
        return fileSystemRepository.getChildren(folder);
    }

    /**
     * Turns a path picked outside the program, such as one coming back from a
     * file chooser, into an item the other methods accept.
     */
    public FileItem itemAt(String path) {
        return fileSystemRepository.itemAt(path);
    }

    public void copy(FileItem source, FileItem destinationFolder) throws IOException {
        fileOperationService.copy(source, destinationFolder);
    }

    public void move(FileItem source, FileItem destinationFolder) throws IOException {
        fileOperationService.move(source, destinationFolder);
    }

    public boolean rename(FileItem source, String newName) {
        return fileOperationService.rename(source, newName);
    }

    public ItemProperty getProperties(FileItem item) {
        validateExists(item);

        if (item.isDirectory()) {
            return filePropertyService.getFolderProperty(item);
        }

        return filePropertyService.getFileProperty(item);
    }

    /**
     * Same as {@link #getProperties(FileItem)} but never walks a folder tree,
     * so it returns fast enough to call on the event dispatch thread.
     */
    public ItemProperty getPropertiesSummary(FileItem item) {
        validateExists(item);

        if (item.isDirectory()) {
            return filePropertyService.getFolderSummary(item);
        }

        return filePropertyService.getFileProperty(item);
    }

    private void validateExists(FileItem item) {
        if (!fileSystemRepository.exists(item)) {
            throw new IllegalArgumentException("File or folder does not exist.");
        }
    }
}
