package model;

import java.io.File;

public class FileProperty {
    private final String kind;
    private final String name;
    private final String extension;
    private final String location;
    private final String createdTime;
    private final String modifiedTime;
    private final long size;

    public FileProperty(
            File file,
            String createdTime,
            String modifiedTime
    ) {
        this.kind = "File";
        this.name = file.getName();
        this.extension = extractExtension(file);
        this.location = file.getParent();
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.size = file.length();
    }

    private String extractExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1);
    }

    public String toDisplayString() {
        return "General:\n\n"
                + "Kind: " + kind + "\n"
                + "Name: " + name + "\n"
                + "Extension: " + extension + "\n"
                + "Size: " + String.format("%,d", size) + " bytes\n"
                + "Where: " + location + "\n"
                + "Created: " + createdTime + "\n"
                + "Modified: " + modifiedTime;
    }
}