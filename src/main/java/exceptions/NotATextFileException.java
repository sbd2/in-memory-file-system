package exceptions;


/**
 * NotATextFileException is thrown when the content modification of a non-TextFile is attempted.
 */
public final class NotATextFileException extends RuntimeException {

    public NotATextFileException(String message) {
        super(message);
    }
}
