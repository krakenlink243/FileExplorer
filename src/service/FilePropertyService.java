package service;

import model.FileProperty;
import model.FolderProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class FilePropertyService {

    public FileProperty getFileProperty(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file.");
        }

        BasicFileAttributes attributes = readAttributes(file);

        return new FileProperty(
                file,
                attributes.creationTime().toMillis(),
                attributes.lastModifiedTime().toMillis()
        );
    }

    public FolderProperty getFolderProperty(File folder) {
        validateFolder(folder);

        BasicFileAttributes attributes = readAttributes(folder);
        FolderCounter counter = countFolder(folder);

        return buildFolderProperty(folder, attributes, counter, true);
    }

    /**
     * Reads a folder without walking its children, so the caller can show
     * something immediately while the full count runs in the background.
     */
    public FolderProperty getFolderSummary(File folder) {
        validateFolder(folder);

        return buildFolderProperty(folder, readAttributes(folder), new FolderCounter(), false);
    }

    private FolderProperty buildFolderProperty(
            File folder,
            BasicFileAttributes attributes,
            FolderCounter counter,
            boolean contentCounted
    ) {
        return new FolderProperty(
                "Folder",
                folder.getAbsolutePath(),
                attributes.creationTime().toMillis(),
                attributes.lastModifiedTime().toMillis(),
                counter.totalSize,
                counter.folderCount,
                counter.fileCount,
                contentCounted
        );
    }

    private FolderCounter countFolder(File folder) {
        FolderCounter counter = new FolderCounter();

        File[] children = folder.listFiles();

        if (children == null) {
            return counter;
        }

        for (File child : children) {
            if (child.isDirectory()) {
                counter.folderCount++;

                FolderCounter childCounter = countFolder(child);
                counter.totalSize += childCounter.totalSize;
                counter.folderCount += childCounter.folderCount;
                counter.fileCount += childCounter.fileCount;
            } else {
                counter.fileCount++;
                counter.totalSize += child.length();
            }
        }

        return counter;
    }

    private void validateFolder(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder.");
        }
    }

    private BasicFileAttributes readAttributes(File file) {
        try {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot read file attributes.");
        }
    }

    private static class FolderCounter {
        long totalSize = 0;
        int folderCount = 0;
        int fileCount = 0;
    }
}
