package presentation;

import controller.FileExplorerController;
import model.FileItem;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {
    private final FileTreePanel fileTreePanel;
    private final FileListPanel fileListPanel;

    public MainFrame(FileExplorerController controller) {
        this.fileTreePanel = new FileTreePanel(controller);
        this.fileListPanel = new FileListPanel(controller);

        initFrame();
        initEvents();
        initContextMenus();
    }

    private void initFrame() {
        setTitle("MyFile Explorer");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                fileTreePanel,
                fileListPanel
        );

        splitPane.setDividerLocation(350);

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Both panels show the same file system, so a copy, move or rename started
     * from either one has to be reflected on both.
     */
    private void initContextMenus() {
        fileTreePanel.installContextMenu(this::refreshPanels);
        fileListPanel.installContextMenu(this::refreshPanels);
    }

    private void refreshPanels() {
        fileTreePanel.refreshTree();
        fileListPanel.refreshCurrentFolder();
    }

    private void initEvents() {
        fileTreePanel.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent event) {
                FileItem selectedItem = fileTreePanel.getSelectedItem();

                if (selectedItem != null && selectedItem.isDirectory()) {
                    fileListPanel.displayFiles(selectedItem);
                }
            }
        });
    }
}