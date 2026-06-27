package service;

import model.FileProperty;
import model.FolderProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FilePropertyService {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'at' HH:mm", Locale.ENGLISH);

    public FileProperty getFileProperty(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file.");
        }

        BasicFileAttributes attributes = readAttributes(file);

        return new FileProperty(
                file,
                formatTime(attributes.creationTime().toMillis()),
                formatTime(attributes.lastModifiedTime().toMillis())
        );
    }

    public FolderProperty getFolderProperty(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder.");
        }

        BasicFileAttributes attributes = readAttributes(folder);
        FolderCounter counter = countFolder(folder);

        return new FolderProperty(
                "Folder",
                folder.getAbsolutePath(),
                formatTime(attributes.creationTime().toMillis()),
                formatTime(attributes.lastModifiedTime().toMillis()),
                counter.totalSize,
                counter.folderCount,
                counter.fileCount
        );
    }

    public String getQuickPropertyText(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File or folder does not exist.");
        }

        BasicFileAttributes attributes = readAttributes(file);

        if (file.isDirectory()) {
            return "General:\n\n"
                    + "Kind: Folder\n"
                    + "Size: Calculating...\n"
                    + "Where: " + file.getAbsolutePath() + "\n"
                    + "Created: " + formatTime(attributes.creationTime().toMillis()) + "\n"
                    + "Modified: " + formatTime(attributes.lastModifiedTime().toMillis());
        }

        return getFileProperty(file).toDisplayString();
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

    private BasicFileAttributes readAttributes(File file) {
        try {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot read file attributes.");
        }
    }

    private String formatTime(long millis) {
        return java.time.Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .format(DATE_FORMATTER);
    }

    private static class FolderCounter {
        long totalSize = 0;
        int folderCount = 0;
        int fileCount = 0;
    }
}