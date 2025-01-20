import orchestrators.ApplicationOrchestrator;

import java.io.IOException;

public class FileSystemApplication {

    public static void main(String[] args) throws IOException {
        ApplicationOrchestrator.getInstance().start();
    }
}
