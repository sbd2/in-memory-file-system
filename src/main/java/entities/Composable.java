package entities;

/**
 * Composable interface is meant for elements that may contain other elements.
 */
public sealed interface Composable permits Drive, Folder, ZipFile {
    /**
     * Returns the entity contents, if any.
     *
     * @return the entity contents.
     */
    ExclusionEntityList<Entity> getContents();

    /**
     * Removes entity's descendants contents and then its own contents.
     */
    void clearContents();
}
