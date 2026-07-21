package repository;

import model.ItemTimes;

import java.io.File;
import java.io.IOException;

/**
 * Every read from and write to the underlying storage goes through this
 * interface, so the layers above never touch the storage API directly.
 *
 * The operations here are deliberately primitive: copy one file, create one
 * folder, delete one item. Walking a folder tree is business logic and belongs
 * to the service layer.
 */
public interface FileSystemRepository {
    File[] getRoots();

    File[] getChildren(File folder);

    boolean exists(File file);

    boolean isDirectory(File file);

    long getSize(File file);

    ItemTimes readTimes(File file);

    void copyFile(File source, File destination) throws IOException;

    void createFolder(File folder) throws IOException;

    void moveItem(File source, File destination) throws IOException;

    boolean rename(File source, File renamed);

    void delete(File file) throws IOException;
}
