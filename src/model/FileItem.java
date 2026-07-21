package model;

/**
 * One file or folder, as it was when the repository read it.
 *
 * This is the type every layer passes around. It deliberately has no method
 * that touches storage: whoever holds a FileItem can read what it says, but
 * cannot copy, rename or delete anything with it. Those operations exist only
 * on the repository.
 */
public class FileItem {
    private final String path;
    private final String name;
    private final boolean directory;

    public FileItem(String path, String name, boolean directory) {
        this.path = path;
        this.name = name;
        this.directory = directory;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FileItem item && path.equals(item.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
