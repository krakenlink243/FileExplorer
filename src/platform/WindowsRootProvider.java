package platform;

import java.io.File;

public class WindowsRootProvider implements RootProvider {

    @Override
    public File[] getRoots() {
        return File.listRoots();
    }
}