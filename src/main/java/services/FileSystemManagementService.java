package services;

import entities.EntityType;

/**
 * FileSystemManagementService provides an API for the operations that may take place within the system.
 */
public interface FileSystemManagementService {

    /**
     * Creates a new Entity.
     *
     * @param type the type of the entity.
     * @param name the name of the entity.
     * @param path the path of the entity.
     */
    void create(EntityType type, String name, String path);

    /**
     * Deletes an existent entity.
     *
     * @param type the type of the entity to be deleted.
     * @param path the path to the entity.
     */
    void delete(EntityType type, String path);

    /**
     * Moves an entity from one location to another.
     *
     * @param type the type of the entity to be moved.
     * @param sourcePath the path where the entity is currently located.
     * @param destinationPath the path where the entity should be moved to.
     */
    void move(EntityType type, String sourcePath, String destinationPath);

    /**
     * Adds content to a TextFile.
     *
     * @param path the path to the text file.
     * @param content the content to be added.
     */
    void writeToFile(String path, String content);

    /**
     * Gets the size of the requested entity.
     *
     * @param type the entity type.
     * @param path the path to the entity.
     * @return the size of the requested entity.
     */
    int getEntitySize(EntityType type, String path);
}
