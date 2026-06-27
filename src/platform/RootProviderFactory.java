package platform;

public class RootProviderFactory {

    public static RootProvider createRootProvider() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            return new MacRootProvider();
        }

        if (osName.contains("win")) {
            return new WindowsRootProvider();
        }

        return new MacRootProvider();
    }
}