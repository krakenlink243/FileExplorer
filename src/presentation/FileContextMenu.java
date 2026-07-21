package presentation;

import controller.FileExplorerController;
import model.FileItem;

import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.function.Supplier;

public class FileContextMenu extends JPopupMenu {
    private final FileExplorerController controller;
    private final Supplier<FileItem> selectedItemSupplier;
    private final Runnable refreshAction;

    public FileContextMenu(
            FileExplorerController controller,
            Supplier<FileItem> selectedItemSupplier,
            Runnable refreshAction
    ) {
        this.controller = controller;
        this.selectedItemSupplier = selectedItemSupplier;
        this.refreshAction = refreshAction;

        initMenuItems();
    }

    private void initMenuItems() {
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem moveItem = new JMenuItem("Move");
        JMenuItem renameItem = new JMenuItem("Rename");
        JMenuItem propertiesItem = new JMenuItem("Properties");

        copyItem.addActionListener(event -> copySelectedFile());
        moveItem.addActionListener(event -> moveSelectedFile());
        renameItem.addActionListener(event -> renameSelectedFile());
        propertiesItem.addActionListener(event -> showSelectedFileProperties());

        add(copyItem);
        add(moveItem);
        add(renameItem);
        addSeparator();
        add(propertiesItem);
    }

    private FileItem getSelectedItem() {
        return selectedItemSupplier.get();
    }

    private void copySelectedFile() {
        FileItem selectedFile = getSelectedItem();

        if (selectedFile == null) {
            showMessage("Please select a file or folder.");
            return;
        }

        FileItem destinationFolder = chooseDestinationFolder();

        if (destinationFolder == null) {
            return;
        }

        try {
            controller.copy(selectedFile, destinationFolder);
            showMessage("Copied successfully.");
            refreshAction.run();
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void moveSelectedFile() {
        FileItem selectedFile = getSelectedItem();

        if (selectedFile == null) {
            showMessage("Please select a file or folder.");
            return;
        }

        FileItem destinationFolder = chooseDestinationFolder();

        if (destinationFolder == null) {
            return;
        }

        try {
            controller.move(selectedFile, destinationFolder);
            showMessage("Moved successfully.");
            refreshAction.run();
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void renameSelectedFile() {
        FileItem selectedFile = getSelectedItem();

        if (selectedFile == null) {
            showMessage("Please select a file or folder.");
            return;
        }

        String newName = JOptionPane.showInputDialog(
                this,
                "Enter new name:",
                selectedFile.getName()
        );

        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        try {
            boolean success = controller.rename(selectedFile, newName.trim());

            if (success) {
                showMessage("Renamed successfully.");
                refreshAction.run();
            } else {
                showError("Cannot rename this file or folder.");
            }
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void showSelectedFileProperties() {
        FileItem selectedFile = getSelectedItem();

        if (selectedFile == null) {
            showMessage("Please select a file or folder.");
            return;
        }

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        try {
            textArea.setText(
                    PropertyFormatter.format(controller.getPropertiesSummary(selectedFile))
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle(selectedFile.getName() + " Properties");
        dialog.setModal(false);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
        dialog.setSize(new Dimension(520, 320));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (!selectedFile.isDirectory()) {
            return;
        }

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return PropertyFormatter.format(controller.getProperties(selectedFile));
            }

            @Override
            protected void done() {
                try {
                    textArea.setText(get());
                } catch (Exception exception) {
                    textArea.setText("Cannot calculate folder properties.\n" + exception.getMessage());
                }
            }
        };

        worker.execute();
    }

    private FileItem chooseDestinationFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose destination folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        // JFileChooser hands back a java.io.File, so ask the controller to turn
        // the path into the item type the rest of the program speaks.
        return controller.itemAt(fileChooser.getSelectedFile().getAbsolutePath());
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Message",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}