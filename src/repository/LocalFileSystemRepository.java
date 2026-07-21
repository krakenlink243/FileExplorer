package repository;

import model.FileItem;
import model.ItemTimes;
import platform.RootProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Reads and writes the local disk. This is the only class in the program that
 * is allowed to use java.io.File and java.nio.file: toFile and toItem are the
 * border where those types are created and discarded.
 */
public class LocalFileSystemRepository implements FileSystemRepository {

    private final RootProvider rootProvider;

    public LocalFileSystemRepository(RootProvider rootProvider) {
        this.rootProvider = rootProvider;
    }

    private File toFile(FileItem item) {
        return new File(item.getPath());
    }

    private FileItem toItem(File file) {
        String name = file.getName();

        // A drive root such as C:\ has an empty name, so fall back to the path.
        if (name.isEmpty()) {
            name = file.getAbsolutePath();
        }

        return new FileItem(file.getAbsolutePath(), name, file.isDirectory());
    }

    private FileItem[] toItems(File[] files) {
        FileItem[] items = new FileItem[files.length];

        for (int index = 0; index < files.length; index++) {
            items[index] = toItem(files[index]);
        }

        return items;
    }

    @Override
    public FileItem[] getRoots() {
        return toItems(rootProvider.getRoots());
    }

    @Override
    public FileItem[] getChildren(FileItem folder) {
        if (folder == null) {
            return new FileItem[0];
        }

        File file = toFile(folder);

        if (!file.exists() || !file.isDirectory()) {
            return new FileItem[0];
        }

        File[] children = file.listFiles();

        if (children == null) {
            return new FileItem[0];
        }

        Arrays.sort(children, Comparator
                .comparing((File child) -> !child.isDirectory())
                .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        return toItems(children);
    }

    @Override
    public FileItem itemAt(String path) {
        return toItem(new File(path));
    }

    @Override
    public FileItem resolveChild(FileItem parent, String name) {
        return toItem(new File(toFile(parent), name));
    }

    @Override
    public FileItem resolveSibling(FileItem item, String name) {
        return toItem(new File(toFile(item).getParentFile(), name));
    }

    @Override
    public FileItem getParent(FileItem item) {
        File parent = toFile(item).getParentFile();

        return parent == null ? null : toItem(parent);
    }

    @Override
    public boolean exists(FileItem item) {
        return item != null && toFile(item).exists();
    }

    @Override
    public long getSize(FileItem item) {
        return item == null ? 0 : toFile(item).length();
    }

    @Override
    public ItemTimes readTimes(FileItem item) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(
                    toFile(item).toPath(), BasicFileAttributes.class);

            return new ItemTimes(
                    attributes.creationTime().toMillis(),
                    attributes.lastModifiedTime().toMillis()
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot read file attributes.");
        }
    }

    @Override
    public void copyFile(FileItem source, FileItem destination) throws IOException {
        Files.copy(
                toFile(source).toPath(),
                toFile(destination).toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Override
    public void createFolder(FileItem folder) throws IOException {
        File file = toFile(folder);

        if (!file.exists()) {
            Files.createDirectories(file.toPath());
        }
    }

    @Override
    public void moveItem(FileItem source, FileItem destination) throws IOException {
        Files.move(
                toFile(source).toPath(),
                toFile(destination).toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Override
    public boolean rename(FileItem source, FileItem renamed) {
        return toFile(source).renameTo(toFile(renamed));
    }

    @Override
    public void delete(FileItem item) throws IOException {
        Files.deleteIfExists(toFile(item).toPath());
    }
}
