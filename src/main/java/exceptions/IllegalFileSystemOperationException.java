package exceptions;

/**
 * IllegalFileSystemOperationException is thrown when any of the following conditions is not met:
 * - Attempted to create a new Entity with null EntityType, Name or Path.
 * - Attempted to create a new descendant for a TextFile.
 * - Attempted to create a new Drive in a Folder or ZipFile.
 * - Attempted to create a Folder, TextFile or ZipFile outside the scope of a Drive.
 */
public final class IllegalFileSystemOperationException extends RuntimeException {

    public IllegalFileSystemOperationException(String message) {
        super(message);
    }
}
