package repository;

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
 * is allowed to use java.nio.file.
 */
public class LocalFileSystemRepository implements FileSystemRepository {

    private final RootProvider rootProvider;

    public LocalFileSystemRepository(RootProvider rootProvider) {
        this.rootProvider = rootProvider;
    }

    @Override
    public File[] getRoots() {
        return rootProvider.getRoots();
    }

    @Override
    public File[] getChildren(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            return new File[0];
        }

        File[] children = folder.listFiles();

        if (children == null) {
            return new File[0];
        }

        Arrays.sort(children, Comparator
                .comparing((File file) -> !file.isDirectory())
                .thenComparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        return children;
    }

    @Override
    public boolean exists(File file) {
        return file != null && file.exists();
    }

    @Override
    public boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    @Override
    public long getSize(File file) {
        return file == null ? 0 : file.length();
    }

    @Override
    public ItemTimes readTimes(File file) {
        try {
            BasicFileAttributes attributes =
                    Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            return new ItemTimes(
                    attributes.creationTime().toMillis(),
                    attributes.lastModifiedTime().toMillis()
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot read file attributes.");
        }
    }

    @Override
    public void copyFile(File source, File destination) throws IOException {
        Files.copy(
                source.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Override
    public void createFolder(File folder) throws IOException {
        if (!folder.exists()) {
            Files.createDirectories(folder.toPath());
        }
    }

    @Override
    public void moveItem(File source, File destination) throws IOException {
        Files.move(
                source.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    @Override
    public boolean rename(File source, File renamed) {
        return source.renameTo(renamed);
    }

    @Override
    public void delete(File file) throws IOException {
        Files.deleteIfExists(file.toPath());
    }
}
