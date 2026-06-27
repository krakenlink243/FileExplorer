package model;

import java.io.File;

public class FileItem {
    private final File file;

    public FileItem(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        String name = file.getName();

        if (name == null || name.isEmpty()) {
            return file.getAbsolutePath();
        }

        return name;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return getName();
    }
}