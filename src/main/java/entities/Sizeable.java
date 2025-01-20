package entities;

/**
 * This interface provides a common API to get the size of an entity.
 */
public interface Sizeable {

    /**
     * Returns the entity's size according to its type.
     * - DRIVE: the sum of its contents' sizes.
     * - FOLDER: the sum of its contents' sizes.
     * - ZIP_FILE: half of the sum of its contents' sizes.
     * - TEXT_FILE: the length of its text content.
     *
     * @return the size the entity.
     */
    int getSize();
}
