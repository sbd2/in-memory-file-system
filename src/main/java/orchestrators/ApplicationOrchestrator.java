package orchestrators;

import entities.Entity;
import entities.EntityType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for orchestrating the application's workflow and dispatching the request down to the service.
 */
public class ApplicationOrchestrator {
    private static final List<Entity> DRIVES = new ArrayList<>();
    private static ApplicationOrchestrator instance;
    private final DependencyOrchestrator dependencyOrchestrator;
    private BufferedReader reader;

    public static ApplicationOrchestrator getInstance() {
        if (instance == null) instance = new ApplicationOrchestrator();
        return instance;
    }

    private ApplicationOrchestrator() {
        dependencyOrchestrator = DependencyOrchestrator.getInstance(DRIVES);
    }

    public void start() throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            printMainMenu();
            String input = reader.readLine();
            if ("0".equals(input)) break;
            processInput(input);
            printStatementsSeparator();
        }

        reader.close();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("In-Memory file system");
        System.out.println("----------------------");
        System.out.println("Please enter the number of your desired operation:");
        System.out.println("(1) Create an entity (Drive, Folder, Zip File or Text File)");
        System.out.println("(2) Add content to an existent Text File");
        System.out.println("(3) Move an entity to a different (existent) location");
        System.out.println("(4) Get entity size");
        System.out.println("(5) Delete an existent entity");
        System.out.println("(0) Exit");
        System.out.println();
    }

    private void printStatementsSeparator() {
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private void processInput(String input) throws IOException {
        int selectedOption;

        try {
            selectedOption = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Only numbers between 1 and 5 are valid inputs.");
            return;
        }

        switch (selectedOption) {
            case 1 -> processEntityCreation();
            case 2 -> processContentAppend();
            case 3 -> processEntityMove();
            case 4 -> processEntitySize();
            case 5 -> processEntityDeletion();
        }
    }

    private void processEntityCreation() throws IOException {
        System.out.println("Please enter the entity type you want to create (DRIVE|FOLDER|ZIP_FILE|TEXT_FILE): ");
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(reader.readLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Only following values are accepted: DRIVE|FOLDER|ZIP_FILE|TEXT_FILE");
            return;
        }
        System.out.println("Please enter the entity name: ");
        String name = reader.readLine();
        System.out.println("Please enter the entity path without its name (leave empty for DRIVE): ");
        String path = reader.readLine();
        printStatementsSeparator();
        try {
            dependencyOrchestrator.getFileSystemManagementService().create(entityType, name, path);
            System.out.println("Created new " + entityType + " " + name + " under " + dependencyOrchestrator.getPathUtils().trimPath(path + "\\" + name));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void processContentAppend() throws IOException {
        System.out.println("Please enter the entity path where the file is located, including file name: ");
        String path = reader.readLine();
        System.out.println("Please enter the content to be appended: ");
        String content = reader.readLine();
        printStatementsSeparator();
        try {
            dependencyOrchestrator.getFileSystemManagementService().writeToFile(path, content);
            System.out.println("Appended content to file under " + path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void processEntityMove() throws IOException {
        System.out.println("Please enter the entity type you want to move (FOLDER|ZIP_FILE|TEXT_FILE): ");
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(reader.readLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Only following values are accepted: FOLDER|ZIP_FILE|TEXT_FILE");
            return;
        }
        System.out.println("Please enter the path under which the entity is located, including entity name: ");
        String sourcePath = reader.readLine();
        System.out.println("Please enter the destination path, including entity name: ");
        String destinationPath = reader.readLine();
        printStatementsSeparator();
        try {
            dependencyOrchestrator.getFileSystemManagementService().move(entityType, sourcePath, destinationPath);
            System.out.println("Moved entity to " + destinationPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void processEntitySize() throws IOException {
        System.out.println("Please enter the entity type you want to get the size of (DRIVE|FOLDER|ZIP_FILE|TEXT_FILE): ");
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(reader.readLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Only following values are accepted: DRIVE|FOLDER|ZIP_FILE|TEXT_FILE");
            return;
        }
        System.out.println("Please enter the path under which the entity is located, including entity name: ");
        String path = reader.readLine();
        printStatementsSeparator();
        try {
            int size = dependencyOrchestrator.getFileSystemManagementService().getEntitySize(entityType, path);
            System.out.println("Size of " + entityType + " is " + size);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void processEntityDeletion() throws IOException {
        System.out.println("Please enter the entity type you want to delete (DRIVE|FOLDER|ZIP_FILE|TEXT_FILE): ");
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(reader.readLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Only following values are accepted: DRIVE|FOLDER|ZIP_FILE|TEXT_FILE");
            return;
        }
        System.out.println("Please enter the path under which the entity is located, including entity name: ");
        String path = reader.readLine();
        printStatementsSeparator();
        try {
            dependencyOrchestrator.getFileSystemManagementService().delete(entityType, path);
            System.out.println("Deleted " + entityType + " from " + path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
