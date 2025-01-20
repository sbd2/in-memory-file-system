package entities;

import exceptions.IllegalFileSystemOperationException;

/**
 * EntityFactory class provides an API to create new entities in the system.
 */
public class EntityFactory {

    /**
     * Creates an entity with the requested properties.
     *
     * @param type the type of the entity.
     * @param name the name of the entity.
     * @param path the path to the entity.
     * @return the entity.
     */
    public Entity createEntity(EntityType type, String name, String path) {
        String fullPath = path + "\\" + name;

        return switch (type) {
            case DRIVE -> new Drive(type, name, name);
            case FOLDER -> new Folder(type, name, fullPath);
            case ZIP_FILE -> new ZipFile(type, name, fullPath);
            case TEXT_FILE -> new TextFile(type, name, fullPath);
            default -> throw new IllegalFileSystemOperationException("Requested entity type not supported");
        };
    }
}
