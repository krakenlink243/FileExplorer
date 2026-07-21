package presentation;

import controller.FileExplorerController;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.io.File;

public class MainFrame extends JFrame {
    private final FileTreePanel fileTreePanel;
    private final FileListPanel fileListPanel;

    public MainFrame(FileExplorerController controller) {
        this.fileTreePanel = new FileTreePanel(controller);
        this.fileListPanel = new FileListPanel(controller);

        initFrame();
        initEvents();
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

    private void initEvents() {
        fileTreePanel.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent event) {
                File selectedFile = fileTreePanel.getSelectedFile();

                if (selectedFile != null && selectedFile.isDirectory()) {
                    fileListPanel.displayFiles(selectedFile);
                }
            }
        });
    }
}