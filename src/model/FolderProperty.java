package model;

public class FolderProperty {
    private final String kind;
    private final String location;
    private final String createdTime;
    private final String modifiedTime;
    private final long size;
    private final int folderCount;
    private final int fileCount;

    public FolderProperty(
            String kind,
            String location,
            String createdTime,
            String modifiedTime,
            long size,
            int folderCount,
            int fileCount
    ) {
        this.kind = kind;
        this.location = location;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.size = size;
        this.folderCount = folderCount;
        this.fileCount = fileCount;
    }

    public String getKind() {
        return kind;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public long getSize() {
        return size;
    }

    public int getFolderCount() {
        return folderCount;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getTotalItems() {
        return folderCount + fileCount;
    }

    public String toDisplayString() {
        return "General:\n\n"
                + "Kind: " + kind + "\n"
                + "Size: " + String.format("%,d", size) + " bytes for "
                + String.format("%,d", getTotalItems()) + " items\n"
                + "Where: " + location + "\n"
                + "Created: " + createdTime + "\n"
                + "Modified: " + modifiedTime + "\n"
                + "Contains: " + String.format("%,d", folderCount) + " folders, "
                + String.format("%,d", fileCount) + " files";
    }
}