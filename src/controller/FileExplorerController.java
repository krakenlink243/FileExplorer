package controller;

import model.FileProperty;
import model.FolderProperty;
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

    public String getProperties(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File or folder does not exist.");
        }

        if (file.isDirectory()) {
            FolderProperty folderProperty = filePropertyService.getFolderProperty(file);
            return folderProperty.toDisplayString();
        }

        FileProperty fileProperty = filePropertyService.getFileProperty(file);
        return fileProperty.toDisplayString();
    }
    public String getQuickProperties(File file) {
        return filePropertyService.getQuickPropertyText(file);
    }
}