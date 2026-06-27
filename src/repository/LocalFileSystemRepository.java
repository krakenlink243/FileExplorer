package repository;

import platform.RootProvider;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

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
}