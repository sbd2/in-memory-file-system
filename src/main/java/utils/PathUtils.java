package utils;

import java.util.Arrays;

/**
 * Utility class that provides functions to manipulate entities' paths.
 */
public class PathUtils {

    /**
     * Removes the last \ from a path string if present.
     *
     * @param path the path String.
     * @return the path String without the lasting \, if any.
     */
    public String trimPath(String path) {
        String trimmedPath = path;

        if (trimmedPath.startsWith("\\")) trimmedPath = trimmedPath.substring(1);
        if (trimmedPath.endsWith("\\")) trimmedPath = trimmedPath.substring(0, trimmedPath.length() - 1);


        return trimmedPath;
    }

    /**
     * Removes the trailing \ from a String path and splits it using \ as delimiter.
     *
     * @param path the String path.
     * @return a String array with the parts of the path.
     */
    public String[] trimAndSplitPath(String path) {
        String trimmedPath = trimPath(path);

        return trimmedPath.split("\\\\");
    }

    /**
     * Removes the last element of a path parts array and returns the rest.
     * If the array has only one element, it returns the same array.
     *
     * @param pathParts the path parts array.
     * @return a copy of the same array excluding the last element or the same array if it is a single element array.
     */
    public String[] excludeLastElement(String[] pathParts) {
        if (pathParts.length > 1) {
            return Arrays.copyOf(pathParts, pathParts.length - 1);
        } else {
            return pathParts;
        }
    }

    /**
     * Joins a path parts array using \ as delimiter.
     *
     * @param pathParts the path parts array.
     * @return a String representation of the path parts array.
     */
    public String pathToString(String[] pathParts) {
        return String.join("\\", pathParts);
    }
}
