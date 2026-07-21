package service;

import model.FileItem;
import repository.FileSystemRepository;

/**
 * Reading the folder tree: the drives to start from, what a folder contains,
 * and whether an item is still there.
 *
 * It keeps the controller from reaching into the repository itself, so every
 * layer only ever calls the one directly below it.
 */
public class FileBrowsingService {

    private final FileSystemRepository fileSystemRepository;

    public FileBrowsingService(FileSystemRepository fileSystemRepository) {
        this.fileSystemRepository = fileSystemRepository;
    }

    public FileItem[] getRoots() {
        return fileSystemRepository.getRoots();
    }

    public FileItem[] getChildren(FileItem folder) {
        return fileSystemRepository.getChildren(folder);
    }

    /**
     * Turns a path that came from outside the program into an item the rest of
     * the layers accept.
     */
    public FileItem itemAt(String path) {
        return fileSystemRepository.itemAt(path);
    }

    public boolean exists(FileItem item) {
        return fileSystemRepository.exists(item);
    }
}
