package service;

import model.FileItem;
import model.FileProperty;
import model.FolderProperty;
import model.ItemTimes;
import repository.FileSystemRepository;

public class FilePropertyService {

    private final FileSystemRepository fileSystemRepository;

    public FilePropertyService(FileSystemRepository fileSystemRepository) {
        this.fileSystemRepository = fileSystemRepository;
    }

    public FileProperty getFileProperty(FileItem file) {
        if (file == null || file.isDirectory() || !fileSystemRepository.exists(file)) {
            throw new IllegalArgumentException("Invalid file.");
        }

        ItemTimes times = fileSystemRepository.readTimes(file);
        FileItem parent = fileSystemRepository.getParent(file);

        return new FileProperty(
                file.getName(),
                parent == null ? "" : parent.getPath(),
                fileSystemRepository.getSize(file),
                times.getCreatedTime(),
                times.getModifiedTime()
        );
    }

    public FolderProperty getFolderProperty(FileItem folder) {
        validateFolder(folder);

        return buildFolderProperty(folder, countFolder(folder), true);
    }

    /**
     * Reads a folder without walking its children, so the caller can show
     * something immediately while the full count runs in the background.
     */
    public FolderProperty getFolderSummary(FileItem folder) {
        validateFolder(folder);

        return buildFolderProperty(folder, new FolderCounter(), false);
    }

    private FolderProperty buildFolderProperty(
            FileItem folder,
            FolderCounter counter,
            boolean contentCounted
    ) {
        ItemTimes times = fileSystemRepository.readTimes(folder);

        return new FolderProperty(
                "Folder",
                folder.getPath(),
                times.getCreatedTime(),
                times.getModifiedTime(),
                counter.totalSize,
                counter.folderCount,
                counter.fileCount,
                contentCounted
        );
    }

    private FolderCounter countFolder(FileItem folder) {
        FolderCounter counter = new FolderCounter();

        for (FileItem child : fileSystemRepository.getChildren(folder)) {
            if (child.isDirectory()) {
                counter.folderCount++;

                FolderCounter childCounter = countFolder(child);
                counter.totalSize += childCounter.totalSize;
                counter.folderCount += childCounter.folderCount;
                counter.fileCount += childCounter.fileCount;
            } else {
                counter.fileCount++;
                counter.totalSize += fileSystemRepository.getSize(child);
            }
        }

        return counter;
    }

    private void validateFolder(FileItem folder) {
        if (folder == null
                || !folder.isDirectory()
                || !fileSystemRepository.exists(folder)) {
            throw new IllegalArgumentException("Invalid folder.");
        }
    }

    private static class FolderCounter {
        long totalSize = 0;
        int folderCount = 0;
        int fileCount = 0;
    }
}
