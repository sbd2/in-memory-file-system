package services;

import entities.*;
import exceptions.IllegalFileSystemOperationException;
import exceptions.NotATextFileException;
import exceptions.PathAlreadyExistsException;
import exceptions.PathNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.NavigationUtils;
import utils.PathUtils;
import validators.ConstraintsValidator;
import validators.DefaultConstraintsValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultFileSystemManagementServiceTest {

    private DefaultFileSystemManagementService fileSystemManagementService;
    private List<Entity> drives;
    private EntityFactory entityFactory;
    private PathUtils pathUtils;
    private NavigationUtils navigationUtils;
    private ConstraintsValidator validator;

    @BeforeEach
    void setUp() {
        pathUtils = new PathUtils();
        navigationUtils = new NavigationUtils();
        drives = new ArrayList<>();
        validator = new DefaultConstraintsValidator(drives, pathUtils, navigationUtils);
        entityFactory = new EntityFactory();
        fileSystemManagementService = new DefaultFileSystemManagementService(validator, drives, pathUtils, navigationUtils, entityFactory);
        fileSystemManagementService.create(EntityType.DRIVE, "C:", "");
    }

    @Test
    void createNewDrive() {
        fileSystemManagementService.create(EntityType.DRIVE, "D:", "");
        assertEquals(2, drives.size());

        Entity newEntity = navigationUtils.findDrive(drives, "D:").get();

        assertInstanceOf(Drive.class, newEntity);
        assertEquals(EntityType.DRIVE, newEntity.getEntityType());
        assertEquals("D:", newEntity.getName());
        assertEquals("D:", newEntity.getPath());
        assertEquals(0, newEntity.getSize());
    }

    @Test
    void createNewFolder() {
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        assertEquals(1, drives.getFirst().getContents().size());

        Entity newFolder = drives.getFirst().getContents().getFirst();

        assertInstanceOf(Folder.class, newFolder);
        assertEquals(EntityType.FOLDER, newFolder.getEntityType());
        assertTrue(newFolder.getContents().isEmpty());
        assertEquals("sample", newFolder.getName());
        assertEquals("C:\\sample", newFolder.getPath());
        assertEquals(0, newFolder.getSize());
    }

    @Test
    void createNewTextFile() {
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        Entity newFolder = drives.getFirst().getContents().getFirst();

        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:\\sample");
        assertEquals(1, newFolder.getContents().size());

        Entity newTextFile = newFolder.getContents().getFirst();

        assertInstanceOf(TextFile.class, newTextFile);
        assertEquals(EntityType.TEXT_FILE, newTextFile.getEntityType());
        assertEquals("letter", newTextFile.getName());
        assertEquals("C:\\sample\\letter", newTextFile.getPath());
        assertEquals(0, newTextFile.getSize());
    }

    @Test
    void failToCreateNewDriveInDrive() {
        assertThrows(IllegalFileSystemOperationException.class, () -> fileSystemManagementService.create(EntityType.DRIVE, "D:", "C:"));
    }

    @Test
    void failToCreateNewFolderInNonExistentDrive() {
        assertThrows(PathNotFoundException.class, () -> fileSystemManagementService.create(EntityType.FOLDER, "sample", "D:"));
    }

    @Test
    void failToCreateDuplicatedFolder() {
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        assertThrows(PathAlreadyExistsException.class, () -> fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:"));
    }

    @Test
    void failToCreateTwoComposablesWithSameNameInSameLocation() {
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        assertThrows(IllegalFileSystemOperationException.class, () -> fileSystemManagementService.create(EntityType.ZIP_FILE, "sample", "C:"));
    }

    @Test
    void deleteDrive() {
        fileSystemManagementService.delete(EntityType.DRIVE, "C:");
        assertEquals(0, drives.size());
    }

    @Test
    void deleteNestedFolder() {
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        fileSystemManagementService.create(EntityType.ZIP_FILE, "compressed", "C:\\sample");
        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:\\sample\\compressed");

        fileSystemManagementService.delete(EntityType.FOLDER, "C:\\sample");

        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, new String[] {"C:\\sample"}));
        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, new String[] {"C:\\sample\\compressed"}));
    }

    @Test
    void deleteTextFile() {
        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:");

        fileSystemManagementService.delete(EntityType.TEXT_FILE, "C:\\letter");
        assertEquals(0, navigationUtils.navigateToComposableEntity(drives, new String[] {"C:"}).getContents().size());
    }

    @Test
    void moveFolderBetweenDrives() {
        fileSystemManagementService.create(EntityType.DRIVE, "D:", "");
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");

        fileSystemManagementService.move(EntityType.FOLDER, "C:\\sample", "D:\\sample");
        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, new String[] {"C:", "sample"}));

        Composable movedFolder = navigationUtils.navigateToComposableEntity(drives, new String[]{"D:", "sample"});
        assertInstanceOf(Folder.class, movedFolder);
        assertEquals("D:\\sample", ((Folder) movedFolder).getPath());
        assertEquals(0, movedFolder.getContents().size());
    }

    @Test
    void moveFolderWithContentBetweenDrives() {
        fileSystemManagementService.create(EntityType.DRIVE, "D:", "");
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:\\sample");

        fileSystemManagementService.move(EntityType.FOLDER, "C:\\sample", "D:\\sample");
        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, new String[] {"C:", "sample"}));

        Composable movedFolder = navigationUtils.navigateToComposableEntity(drives, new String[]{"D:", "sample"});
        assertInstanceOf(Folder.class, movedFolder);
        assertEquals(1, movedFolder.getContents().size());
        assertEquals("D:\\sample", ((Folder) movedFolder).getPath());

        Entity file = navigationUtils.getChildOfType(EntityType.TEXT_FILE, "letter", movedFolder).get();
        assertInstanceOf(TextFile.class, file);
        assertEquals("D:\\sample\\letter", movedFolder.getContents().getFirst().getPath());
    }

    @Test
    void moveFileBetweenFolders() {
        fileSystemManagementService.create(EntityType.DRIVE, "D:", "");
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        fileSystemManagementService.create(EntityType.FOLDER, "example", "D:");

        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:\\sample");

        fileSystemManagementService.move(EntityType.TEXT_FILE, "C:\\sample\\letter", "D:\\example\\letter");

        Composable oldFolder = navigationUtils.navigateToComposableEntity(drives, new String[]{"C:", "sample"});
        Composable newFolder = navigationUtils.navigateToComposableEntity(drives, new String[]{"D:", "example"});
        assertEquals(0, oldFolder.getContents().size());
        assertEquals(1, newFolder.getContents().size());
        assertEquals("D:\\example\\letter", newFolder.getContents().getFirst().getPath());
    }

    @Test
    void failToMoveFolderWhenFolderAlreadyExists() {
        fileSystemManagementService.create(EntityType.DRIVE, "D:", "");
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "D:");

        assertThrows(PathAlreadyExistsException.class, () -> fileSystemManagementService.move(EntityType.FOLDER, "C:\\sample", "D:\\sample"));
    }

    @Test
    void failToMoveDrive() {
        fileSystemManagementService.create(EntityType.DRIVE, "D:", "");
        fileSystemManagementService.create(EntityType.FOLDER, "sample", "C:");

        assertThrows(IllegalFileSystemOperationException.class, () -> fileSystemManagementService.move(EntityType.DRIVE, "D:", "C:\\sample\\D:"));
    }

    @Test
    void writeToTextFile() {
        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:");
        fileSystemManagementService.writeToFile("C:\\letter", "Test content");

        String fileContent = ((TextFile) drives.getFirst().getContents().getFirst()).getContent();
        assertFalse(fileContent.isBlank());
        assertEquals("Test content".length(), fileContent.length());
        assertEquals("Test content", fileContent);
    }

    @Test
    void failToWriteToNonTextFile() {
        fileSystemManagementService.create(EntityType.ZIP_FILE, "compressed", "C:");
        assertThrows(NotATextFileException.class, () -> fileSystemManagementService.writeToFile("C:\\compressed", "Test content"));
    }

    @Test
    void failToWriteToNonExistentFile() {
        assertThrows(NotATextFileException.class, () -> fileSystemManagementService.writeToFile("C:\\compressed", "Test content"));
    }

    @Test
    void getEntitySize() {
        fileSystemManagementService.create(EntityType.ZIP_FILE, "compressed", "C:");
        fileSystemManagementService.create(EntityType.TEXT_FILE, "letter", "C:\\compressed");
        fileSystemManagementService.create(EntityType.TEXT_FILE, "invoice", "C:");

        fileSystemManagementService.writeToFile("C:\\compressed\\letter", "compressed content");
        fileSystemManagementService.writeToFile("C:\\invoice", "content");

        assertEquals("compressed content".length() / 2 + "content".length(), fileSystemManagementService.getEntitySize(EntityType.DRIVE, "C:"));
    }
}