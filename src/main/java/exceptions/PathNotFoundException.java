package exceptions;

/**
 * PathNotFoundException is thrown when the creation of a new Entity in a non-existent path is attempted.
 */
public final class PathNotFoundException extends RuntimeException {

    public PathNotFoundException(String message) {
        super(message);
    }
}
