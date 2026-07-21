package repository;

import model.FileItem;
import model.ItemTimes;

import java.io.IOException;

/**
 * The only way in or out of the underlying storage. Everything above this
 * interface speaks in FileItem, which carries no storage operations, so the
 * compiler stops any layer from reaching the disk on its own.
 *
 * The operations here are deliberately primitive: copy one file, create one
 * folder, delete one item. Walking a folder tree is business logic and belongs
 * to the service layer.
 */
public interface FileSystemRepository {
    FileItem[] getRoots();

    FileItem[] getChildren(FileItem folder);

    /** Reads whatever currently sits at the given absolute path. */
    FileItem itemAt(String path);

    /** The item named {@code name} inside {@code parent}, existing or not. */
    FileItem resolveChild(FileItem parent, String name);

    /** The item named {@code name} next to {@code item}, existing or not. */
    FileItem resolveSibling(FileItem item, String name);

    /** The folder containing {@code item}, or null when it has no parent. */
    FileItem getParent(FileItem item);

    boolean exists(FileItem item);

    long getSize(FileItem item);

    ItemTimes readTimes(FileItem item);

    void copyFile(FileItem source, FileItem destination) throws IOException;

    void createFolder(FileItem folder) throws IOException;

    void moveItem(FileItem source, FileItem destination) throws IOException;

    boolean rename(FileItem source, FileItem renamed);

    void delete(FileItem item) throws IOException;

    /** Hands the item to the operating system's default application. */
    void open(FileItem item) throws IOException;
}
