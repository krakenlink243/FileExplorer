package presentation;

import controller.FileExplorerController;
import model.FileItem;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FileTreePanel extends JPanel {
    private final FileExplorerController controller;
    private final DefaultMutableTreeNode rootNode;
    private final DefaultTreeModel treeModel;
    private final JTree tree;

    public FileTreePanel(FileExplorerController controller) {
        this.controller = controller;
        this.rootNode = new DefaultMutableTreeNode("MyFile Explorer");
        this.treeModel = new DefaultTreeModel(rootNode);
        this.tree = new JTree(treeModel);

        setLayout(new BorderLayout());
        add(new JLabel(" Folders"), BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);

        loadRoots();
        initLazyLoading();
        initContextMenu();
    }

    private void loadRoots() {
        rootNode.removeAllChildren();

        File[] roots = controller.getRoots();

        for (File root : roots) {
            rootNode.add(createTreeNode(root));
        }

        treeModel.reload();
    }

    private DefaultMutableTreeNode createTreeNode(File file) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new FileItem(file));

        if (file.isDirectory()) {
            treeNode.add(new DefaultMutableTreeNode("Loading..."));
        }

        return treeNode;
    }

    private void initLazyLoading() {
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                TreePath path = event.getPath();
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) path.getLastPathComponent();

                loadChildrenIfNeeded(node);
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {
                // No action needed
            }
        });
    }

    private void loadChildrenIfNeeded(DefaultMutableTreeNode node) {
        if (node.getChildCount() != 1) {
            return;
        }

        DefaultMutableTreeNode firstChild =
                (DefaultMutableTreeNode) node.getChildAt(0);

        if (!"Loading...".equals(firstChild.getUserObject())) {
            return;
        }

        Object userObject = node.getUserObject();

        if (!(userObject instanceof FileItem fileItem)) {
            return;
        }

        File folder = fileItem.getFile();

        node.removeAllChildren();

        File[] children = controller.getChildren(folder);

        for (File child : children) {
            if (child.isDirectory()) {
                node.add(createTreeNode(child));
            }
        }

        treeModel.reload(node);
    }

    private void initContextMenu() {
        FileContextMenu contextMenu = new FileContextMenu(
                controller,
                this::getSelectedFile,
                this::refreshTree
        );

        tree.addMouseListener(new MouseAdapter() {
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

        int row = tree.getRowForLocation(event.getX(), event.getY());

        if (row == -1) {
            return;
        }

        tree.setSelectionRow(row);
        contextMenu.show(tree, event.getX(), event.getY());
    }

    private void refreshTree() {
        loadRoots();
    }

    public JTree getTree() {
        return tree;
    }

    public File getSelectedFile() {
        DefaultMutableTreeNode selectedNode =
                (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (selectedNode == null) {
            return null;
        }

        Object userObject = selectedNode.getUserObject();

        if (userObject instanceof FileItem fileItem) {
            return fileItem.getFile();
        }

        return null;
    }
}