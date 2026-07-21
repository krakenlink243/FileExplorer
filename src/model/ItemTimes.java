package model;

/**
 * Creation and modification times of a file or folder, in milliseconds since
 * the epoch. Keeps the storage-specific attribute types out of the layers
 * above the repository.
 */
public class ItemTimes {
    private final long createdTime;
    private final long modifiedTime;

    public ItemTimes(long createdTime, long modifiedTime) {
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }
}
