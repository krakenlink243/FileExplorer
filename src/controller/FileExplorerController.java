package controller;

import model.ItemProperty;
import repository.FileSystemRepository;
import service.FileOperationService;
import service.FilePropertyService;

import java.io.File;
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

    public File[] getRoots() {
        return fileSystemRepository.getRoots();
    }

    public File[] getChildren(File folder) {
        return fileSystemRepository.getChildren(folder);
    }

    public void copy(File source, File destinationFolder) throws IOException {
        fileOperationService.copy(source, destinationFolder);
    }

    public void move(File source, File destinationFolder) throws IOException {
        fileOperationService.move(source, destinationFolder);
    }

    public boolean rename(File source, String newName) {
        return fileOperationService.rename(source, newName);
    }

    public ItemProperty getProperties(File file) {
        validateExists(file);

        if (fileSystemRepository.isDirectory(file)) {
            return filePropertyService.getFolderProperty(file);
        }

        return filePropertyService.getFileProperty(file);
    }

    /**
     * Same as {@link #getProperties(File)} but never walks a folder tree, so it
     * returns fast enough to call on the event dispatch thread.
     */
    public ItemProperty getPropertiesSummary(File file) {
        validateExists(file);

        if (fileSystemRepository.isDirectory(file)) {
            return filePropertyService.getFolderSummary(file);
        }

        return filePropertyService.getFileProperty(file);
    }

    private void validateExists(File file) {
        if (!fileSystemRepository.exists(file)) {
            throw new IllegalArgumentException("File or folder does not exist.");
        }
    }
}