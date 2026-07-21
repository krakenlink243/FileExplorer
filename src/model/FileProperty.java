package model;

public class FileProperty implements ItemProperty {
    private final String kind;
    private final String name;
    private final String extension;
    private final String location;
    private final long createdTime;
    private final long modifiedTime;
    private final long size;

    public FileProperty(
            String name,
            String location,
            long size,
            long createdTime,
            long modifiedTime
    ) {
        this.kind = "File";
        this.name = name;
        this.extension = extractExtension(name);
        this.location = location;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.size = size;
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1);
    }

    @Override
    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public long getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public long getSize() {
        return size;
    }
}
