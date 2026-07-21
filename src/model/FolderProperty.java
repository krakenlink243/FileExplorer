package model;

public class FolderProperty implements ItemProperty {
    private final String kind;
    private final String location;
    private final long createdTime;
    private final long modifiedTime;
    private final long size;
    private final int folderCount;
    private final int fileCount;
    private final boolean contentCounted;

    public FolderProperty(
            String kind,
            String location,
            long createdTime,
            long modifiedTime,
            long size,
            int folderCount,
            int fileCount,
            boolean contentCounted
    ) {
        this.kind = kind;
        this.location = location;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.size = size;
        this.folderCount = folderCount;
        this.fileCount = fileCount;
        this.contentCounted = contentCounted;
    }

    @Override
    public String getKind() {
        return kind;
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

    public int getFolderCount() {
        return folderCount;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getTotalItems() {
        return folderCount + fileCount;
    }

    /**
     * False when the folder was read without walking its children, so size and
     * counts are not meaningful yet.
     */
    public boolean isContentCounted() {
        return contentCounted;
    }
}
