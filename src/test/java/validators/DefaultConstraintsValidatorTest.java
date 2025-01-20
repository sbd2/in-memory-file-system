package validators;

import entities.*;
import exceptions.PathNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.NavigationUtils;
import utils.PathUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultConstraintsValidatorTest {

    private DefaultConstraintsValidator validator;

    @BeforeEach
    void setUp() {
        Drive defaultDrive = new Drive(EntityType.DRIVE, "C:", "");
        Folder defaultFolder = new Folder(EntityType.FOLDER, "sample", "C:\\");
        ZipFile defaultZipFile = new ZipFile(EntityType.ZIP_FILE, "compressed", "C:\\sample");
        TextFile defaultTextFile = new TextFile(EntityType.TEXT_FILE, "letter", "C:\\sample\\compressed");

        defaultDrive.getContents().add(defaultFolder);
        defaultFolder.getContents().add(defaultZipFile);
        defaultZipFile.getContents().add(defaultTextFile);

        PathUtils pathUtils = new PathUtils();
        NavigationUtils navigationUtils = new NavigationUtils();
        validator = new DefaultConstraintsValidator(List.of(defaultDrive), pathUtils, navigationUtils);
    }

    @Test
    void anyValueNull() {
        assertTrue(validator.anyValueNull((Object) null));
        assertTrue(validator.anyValueNull("", 1, new Object(), null));
        assertFalse(validator.anyValueNull(("")));
        assertFalse(validator.anyValueNull("", 1.0f, new Object(), true));
    }

    @Test
    void pathExists() {
        assertTrue(validator.pathExists(""));
        assertTrue(validator.pathExists("C:\\"));
        assertTrue(validator.pathExists("C:\\sample"));
        assertTrue(validator.pathExists("C:\\sample\\compressed"));
        assertFalse(validator.pathExists("C:\\sample\\letter"));
        assertFalse(validator.pathExists("C:\\example\\compressed"));
        assertFalse(validator.pathExists("D:\\sample\\compressed"));
        assertFalse(validator.pathExists("D:\\"));
    }

    @Test
    void entityAlreadyExists() {
        assertTrue(validator.entityAlreadyExists(EntityType.DRIVE, "C:\\"));
        assertFalse(validator.entityAlreadyExists(EntityType.DRIVE, "D:\\"));

        assertTrue(validator.entityAlreadyExists(EntityType.FOLDER, "C:\\sample"));
        assertFalse(validator.entityAlreadyExists(EntityType.FOLDER, "C:\\examples"));

        assertThrows(PathNotFoundException.class, () -> validator.entityAlreadyExists(EntityType.FOLDER, "D:\\examples"));

        assertTrue(validator.entityAlreadyExists(EntityType.ZIP_FILE, "C:\\sample\\compressed"));
        assertFalse(validator.entityAlreadyExists(EntityType.TEXT_FILE, "C:\\sample\\compressed"));
        assertFalse(validator.entityAlreadyExists(EntityType.ZIP_FILE, "C:\\sample\\letter"));
        assertFalse(validator.entityAlreadyExists(EntityType.TEXT_FILE, "C:\\sample\\letter"));
        assertThrows(PathNotFoundException.class, () -> validator.entityAlreadyExists(EntityType.ZIP_FILE, "C:\\examples\\compressed"));
    }

    @Test
    void isPathSuitableForEntityType() {
        assertTrue(validator.isPathSuitableForEntityType(EntityType.FOLDER, "C:\\example"));
        assertFalse(validator.isPathSuitableForEntityType(EntityType.FOLDER, "C:\\sample"));

        assertTrue(validator.isPathSuitableForEntityType(EntityType.TEXT_FILE, "C:\\sample"));
        assertFalse(validator.isPathSuitableForEntityType(EntityType.ZIP_FILE, "C:\\sample"));

        assertTrue(validator.isPathSuitableForEntityType(EntityType.TEXT_FILE, "C:\\sample\\compressed"));
        assertFalse(validator.isPathSuitableForEntityType(EntityType.FOLDER, "C:\\sample\\compressed"));

        assertFalse(validator.isPathSuitableForEntityType(EntityType.TEXT_FILE, "C:\\sample\\compressed\\letter"));

        assertTrue(validator.isPathSuitableForEntityType(EntityType.DRIVE, "D:\\"));
        assertFalse(validator.isPathSuitableForEntityType(EntityType.DRIVE, "D:\\drive"));

        assertFalse(validator.isPathSuitableForEntityType(EntityType.TEXT_FILE, "D:\\example"));
        assertFalse(validator.isPathSuitableForEntityType(EntityType.FOLDER, "sample"));
        assertFalse(validator.isPathSuitableForEntityType(EntityType.TEXT_FILE, "letter"));
    }
}