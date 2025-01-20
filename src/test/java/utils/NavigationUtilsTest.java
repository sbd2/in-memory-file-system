package utils;

import entities.*;
import exceptions.PathNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NavigationUtilsTest {

    private NavigationUtils navigationUtils;
    private List<Entity> drives;
    private Folder defaultFolder;

    @BeforeEach
    void setUp() {
        Drive defaultDrive = new Drive(EntityType.DRIVE, "C:", "");
        defaultFolder = new Folder(EntityType.FOLDER, "sample", "C:\\");
        ZipFile defaultZipFile = new ZipFile(EntityType.ZIP_FILE, "compressed", "C:\\sample");
        TextFile defaultTextFile = new TextFile(EntityType.TEXT_FILE, "letter", "C:\\sample\\compressed");
        TextFile secondaryTextFile = new TextFile(EntityType.TEXT_FILE, "compressed", "C:\\sample");

        defaultDrive.getContents().add(defaultFolder);
        defaultFolder.getContents().add(defaultZipFile);
        defaultFolder.getContents().add(secondaryTextFile);
        defaultZipFile.getContents().add(defaultTextFile);

        drives = List.of(defaultDrive);

        navigationUtils = new NavigationUtils();
    }

    @Test
    void findDrive() {
        assertTrue(navigationUtils.findDrive(drives, "C:").isPresent());
        assertTrue(navigationUtils.findDrive(drives, "D:").isEmpty());
        assertTrue(navigationUtils.findDrive(List.of(), "C:").isEmpty());
    }

    @Test
    void navigateToComposableEntity() {
        String[] existentPathParts = "C:\\sample\\compressed".split("\\\\");
        String[] nonExistentPathParts = "C:\\example\\compressed".split("\\\\");
        String[] textFilePathParts = "C:\\sample\\compressed\\letter".split("\\\\");

        assertInstanceOf(ZipFile.class, navigationUtils.navigateToComposableEntity(drives, existentPathParts));
        assertInstanceOf(Drive.class, navigationUtils.navigateToComposableEntity(drives, new String[] {"C:"}));
        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, new String[] {"D:"}));
        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, nonExistentPathParts));
        assertThrows(PathNotFoundException.class, () -> navigationUtils.navigateToComposableEntity(drives, textFilePathParts));
    }

    @Test
    void getChildOfType() {
        assertTrue(navigationUtils.getChildOfType(EntityType.TEXT_FILE, "compressed", defaultFolder).isPresent());
        assertInstanceOf(TextFile.class, navigationUtils.getChildOfType(EntityType.TEXT_FILE, "compressed", defaultFolder).get());
        assertTrue(navigationUtils.getChildOfType(EntityType.TEXT_FILE, "letter", defaultFolder).isEmpty());
    }
}