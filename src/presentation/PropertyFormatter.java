package presentation;

import model.FileProperty;
import model.FolderProperty;
import model.ItemProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Turns property models into the text shown in the properties dialog. Lives in
 * the presentation layer so the model and service layers stay free of display
 * concerns.
 */
public final class PropertyFormatter {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'at' HH:mm", Locale.ENGLISH);

    private PropertyFormatter() {
    }

    public static String format(ItemProperty property) {
        if (property instanceof FolderProperty folderProperty) {
            return formatFolder(folderProperty);
        }

        if (property instanceof FileProperty fileProperty) {
            return formatFile(fileProperty);
        }

        return "";
    }

    private static String formatFile(FileProperty property) {
        return "General:\n\n"
                + "Kind: " + property.getKind() + "\n"
                + "Name: " + property.getName() + "\n"
                + "Extension: " + property.getExtension() + "\n"
                + "Size: " + formatBytes(property.getSize()) + "\n"
                + "Where: " + property.getLocation() + "\n"
                + formatTimes(property);
    }

    private static String formatFolder(FolderProperty property) {
        String size = property.isContentCounted()
                ? formatBytes(property.getSize())
                        + " for " + String.format("%,d", property.getTotalItems()) + " items"
                : "Calculating...";

        String contains = property.isContentCounted()
                ? String.format("%,d", property.getFolderCount()) + " folders, "
                        + String.format("%,d", property.getFileCount()) + " files"
                : "Calculating...";

        return "General:\n\n"
                + "Kind: " + property.getKind() + "\n"
                + "Size: " + size + "\n"
                + "Where: " + property.getLocation() + "\n"
                + formatTimes(property) + "\n"
                + "Contains: " + contains;
    }

    private static String formatTimes(ItemProperty property) {
        return "Created: " + formatTime(property.getCreatedTime()) + "\n"
                + "Modified: " + formatTime(property.getModifiedTime());
    }

    private static String formatBytes(long size) {
        return String.format("%,d", size) + " bytes";
    }

    private static String formatTime(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .format(DATE_FORMATTER);
    }
}
