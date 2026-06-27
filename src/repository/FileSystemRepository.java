package repository;

import java.io.File;

public interface FileSystemRepository {
    File[] getRoots();

    File[] getChildren(File folder);
}