package exceptions;

/**
 * PathAlreadyExistsException is thrown when the creation of a new Entity in an already existent path is attempted (a.k.a. file already exists).
 */
public final class PathAlreadyExistsException extends RuntimeException {

    public PathAlreadyExistsException(String message) {
        super(message);
    }
}
