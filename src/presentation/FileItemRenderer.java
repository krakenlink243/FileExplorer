package presentation;

import model.FileItem;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Component;
import java.io.File;

public class FileItemRenderer extends DefaultListCellRenderer {
    private final JFileChooser fileChooser;

    public FileItemRenderer() {
        this.fileChooser = new JFileChooser();
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
        );

        if (value instanceof FileItem fileItem) {
            label.setText(fileItem.getName());

            // Swing's icon lookup only accepts java.io.File, so this renderer
            // is the one place above the repository that still builds one. It
            // is read-only: the File is handed straight to Swing and dropped.
            Icon icon = fileChooser.getIcon(new File(fileItem.getPath()));
            label.setIcon(icon);
        }

        return label;
    }
}