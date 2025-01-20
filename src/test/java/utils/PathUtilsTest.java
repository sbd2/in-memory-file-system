package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    private PathUtils pathUtils;

    @BeforeEach
    void setUp() {
        pathUtils = new PathUtils();
    }

    @Test
    void trimPath() {
        assertEquals("C:", pathUtils.trimPath("C:\\"));
        assertEquals("C:", pathUtils.trimPath("\\C:"));
        assertEquals("C:", pathUtils.trimPath("\\C:\\"));
        assertEquals("C:", pathUtils.trimPath("C:"));
    }

    @Test
    void trimAndSplitPath() {
        assertArrayEquals(new String[] {"C:", "test"}, pathUtils.trimAndSplitPath("C:\\test\\"));
        assertArrayEquals(new String[] {"C:", "test"}, pathUtils.trimAndSplitPath("C:\\test"));
        assertArrayEquals(new String[] {"C:"}, pathUtils.trimAndSplitPath("C:\\"));
    }

    @Test
    void excludeLastElement() {
        assertArrayEquals(new String[] {"C:", "test"}, pathUtils.excludeLastElement(new String[] {"C:", "test", "letter"}));
        assertArrayEquals(new String[] {"C:"}, pathUtils.excludeLastElement(new String[] {"C:"}));
    }

    @Test
    void pathToString() {
        assertEquals("C:\\test\\letter", pathUtils.pathToString(new String[] {"C:", "test", "letter"}));
        assertEquals("C:", pathUtils.pathToString(new String[] {"C:"}));
    }
}