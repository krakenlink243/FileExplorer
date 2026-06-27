package platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MacRootProvider implements RootProvider {

    @Override
    public File[] getRoots() {
        List<File> roots = new ArrayList<>();

        File home = new File(System.getProperty("user.home"));
        File volumes = new File("/Volumes");
        File applications = new File("/Applications");

        if (home.exists()) {
            roots.add(home);
        }

        if (volumes.exists()) {
            roots.add(volumes);
        }

        if (applications.exists()) {
            roots.add(applications);
        }

        return roots.toArray(new File[0]);
    }
}