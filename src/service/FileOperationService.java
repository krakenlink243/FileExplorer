package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileOperationService {

    public void copy(File source, File destinationFolder) throws IOException {
        validateSource(source);
        validateDestinationFolder(destinationFolder);

        File destination = new File(destinationFolder, source.getName());

        if (source.isDirectory()) {
            copyFolder(source, destination);
        } else {
            Files.copy(
                    source.toPath(),
                    destination.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    public void move(File source, File destinationFolder) throws IOException {
        validateSource(source);
        validateDestinationFolder(destinationFolder);

        File destination = new File(destinationFolder, source.getName());

        Files.move(
                source.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public boolean rename(File source, String newName) {
        validateSource(source);

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be empty.");
        }

        File renamedFile = new File(source.getParentFile(), newName.trim());

        return source.renameTo(renamedFile);
    }

    private void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        if (!destinationFolder.exists()) {
            Files.createDirectories(destinationFolder.toPath());
        }

        File[] children = sourceFolder.listFiles();

        if (children == null) {
            return;
        }

        for (File child : children) {
            File destinationChild = new File(destinationFolder, child.getName());

            if (child.isDirectory()) {
                copyFolder(child, destinationChild);
            } else {
                Files.copy(
                        child.toPath(),
                        destinationChild.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        }
    }

    private void validateSource(File source) {
        if (source == null || !source.exists()) {
            throw new IllegalArgumentException("Source does not exist.");
        }
    }

    private void validateDestinationFolder(File destinationFolder) {
        if (destinationFolder == null || !destinationFolder.exists() || !destinationFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid destination folder.");
        }
    }
}