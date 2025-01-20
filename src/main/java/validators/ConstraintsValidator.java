package validators;

import entities.EntityType;

/**
 * ConstraintsValidator provides a common API for business constraints checking.
 */
public interface ConstraintsValidator {

    /**
     * Checks if there is any null among the values.
     *
     * @param values the values to check.
     * @return true if any value is null and false otherwise.
     */
    boolean anyValueNull(Object... values);

    /**
     * Checks the whole path to find out if it exists or not.
     *
     * @param path the whole file path (WITHOUT the file to be created, if any).
     * @return true if the path does exist and false otherwise.
     */
    boolean pathExists(String path);

    /**
     * Checks if the entity of this type already exists under the provided path.
     *
     * @param type the EntityType to check for.
     * @param path the path to check, including the desired entity.
     * @return true if the entity already exists and false otherwise.
     */
    boolean entityAlreadyExists(EntityType type, String path);

    /**
     * Checks if the path can contain the provided entity type.
     *
     * @param type the type to check.
     * @param path the path to check, including the desired entity.
     * @return true if the path can contain the entity and false otherwise.
     */
    boolean isPathSuitableForEntityType(EntityType type, String path);
}
