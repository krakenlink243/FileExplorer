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
            File file = fileItem.getFile();

            label.setText(fileItem.getName());

            Icon icon = fileChooser.getIcon(file);
            label.setIcon(icon);
        }

        return label;
    }
}