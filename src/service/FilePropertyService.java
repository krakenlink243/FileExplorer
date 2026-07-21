package service;

import model.FileProperty;
import model.FolderProperty;
import model.ItemTimes;
import repository.FileSystemRepository;

import java.io.File;

public class FilePropertyService {

    private final FileSystemRepository fileSystemRepository;

    public FilePropertyService(FileSystemRepository fileSystemRepository) {
        this.fileSystemRepository = fileSystemRepository;
    }

    public FileProperty getFileProperty(File file) {
        if (!fileSystemRepository.exists(file) || fileSystemRepository.isDirectory(file)) {
            throw new IllegalArgumentException("Invalid file.");
        }

        ItemTimes times = fileSystemRepository.readTimes(file);

        return new FileProperty(
                file,
                fileSystemRepository.getSize(file),
                times.getCreatedTime(),
                times.getModifiedTime()
        );
    }

    public FolderProperty getFolderProperty(File folder) {
        validateFolder(folder);

        return buildFolderProperty(folder, countFolder(folder), true);
    }

    /**
     * Reads a folder without walking its children, so the caller can show
     * something immediately while the full count runs in the background.
     */
    public FolderProperty getFolderSummary(File folder) {
        validateFolder(folder);

        return buildFolderProperty(folder, new FolderCounter(), false);
    }

    private FolderProperty buildFolderProperty(
            File folder,
            FolderCounter counter,
            boolean contentCounted
    ) {
        ItemTimes times = fileSystemRepository.readTimes(folder);

        return new FolderProperty(
                "Folder",
                folder.getAbsolutePath(),
                times.getCreatedTime(),
                times.getModifiedTime(),
                counter.totalSize,
                counter.folderCount,
                counter.fileCount,
                contentCounted
        );
    }

    private FolderCounter countFolder(File folder) {
        FolderCounter counter = new FolderCounter();

        for (File child : fileSystemRepository.getChildren(folder)) {
            if (fileSystemRepository.isDirectory(child)) {
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

    private void validateFolder(File folder) {
        if (!fileSystemRepository.isDirectory(folder)) {
            throw new IllegalArgumentException("Invalid folder.");
        }
    }

    private static class FolderCounter {
        long totalSize = 0;
        int folderCount = 0;
        int fileCount = 0;
    }
}
