package presentation;

import controller.FileExplorerController;
import model.FileItem;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FileListPanel extends JPanel {
    private final FileExplorerController controller;
    private final DefaultListModel<FileItem> listModel;
    private final JList<FileItem> fileList;
    private File currentFolder;

    public FileListPanel(FileExplorerController controller) {
        this.controller = controller;
        this.listModel = new DefaultListModel<>();
        this.fileList = new JList<>(listModel);

        this.fileList.setCellRenderer(new FileItemRenderer());

        setLayout(new BorderLayout());
        add(new JLabel(" Files and Folders"), BorderLayout.NORTH);
        add(new JScrollPane(fileList), BorderLayout.CENTER);

        initContextMenu();
    }

    public void displayFiles(File folder) {
        currentFolder = folder;
        listModel.clear();

        File[] children = controller.getChildren(folder);

        for (File child : children) {
            listModel.addElement(new FileItem(child));
        }
    }

    public void refreshCurrentFolder() {
        if (currentFolder != null && currentFolder.exists()) {
            displayFiles(currentFolder);
        }
    }

    private void initContextMenu() {
        FileContextMenu contextMenu = new FileContextMenu(
                controller,
                this::getSelectedFile,
                this::refreshCurrentFolder
        );

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handlePopupEvent(event, contextMenu);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                handlePopupEvent(event, contextMenu);
            }
        });
    }

    private void handlePopupEvent(MouseEvent event, FileContextMenu contextMenu) {
        if (!event.isPopupTrigger()) {
            return;
        }

        int index = fileList.locationToIndex(event.getPoint());

        if (index < 0) {
            return;
        }

        Rectangle cellBounds = fileList.getCellBounds(index, index);

        if (cellBounds == null || !cellBounds.contains(event.getPoint())) {
            return;
        }

        fileList.setSelectedIndex(index);
        contextMenu.show(fileList, event.getX(), event.getY());
    }

    public JList<FileItem> getFileList() {
        return fileList;
    }

    public File getSelectedFile() {
        FileItem selectedItem = fileList.getSelectedValue();

        if (selectedItem == null) {
            return null;
        }

        return selectedItem.getFile();
    }
}