import controller.FileExplorerController;
import platform.RootProvider;
import platform.RootProviderFactory;
import presentation.MainFrame;
import repository.FileSystemRepository;
import repository.LocalFileSystemRepository;
import service.FileBrowsingService;
import service.FileOperationService;
import service.FilePropertyService;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RootProvider rootProvider = RootProviderFactory.createRootProvider();

            FileSystemRepository fileSystemRepository =
                    new LocalFileSystemRepository(rootProvider);

            FileBrowsingService fileBrowsingService =
                    new FileBrowsingService(fileSystemRepository);

            FileOperationService fileOperationService =
                    new FileOperationService(fileSystemRepository);

            FilePropertyService filePropertyService =
                    new FilePropertyService(fileSystemRepository);

            FileExplorerController controller =
                    new FileExplorerController(
                            fileBrowsingService,
                            fileOperationService,
                            filePropertyService
                    );

            MainFrame mainFrame = new MainFrame(controller);
            mainFrame.setVisible(true);
        });
    }
}