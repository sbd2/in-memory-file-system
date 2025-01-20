package entities;

import lombok.Data;
import lombok.NonNull;

/**
 * Entity base class that models common entity's properties and behavior.
 */
@Data
public abstract class Entity implements Sizeable {

    @NonNull
    protected final ExclusionEntityList<Entity> contents = new ExclusionEntityList<>(Drive.class);
    @NonNull
    protected final EntityType entityType;
    @NonNull
    protected String name;
    @NonNull
    protected String path;

    /**
     * Updates all children paths.
     *
     * @param parentPath the path to the entity's parent.
     */
    public void updateDescendantsPath(String parentPath) {
        setPath(parentPath + "\\" + getName());
        contents.forEach(it -> it.updateDescendantsPath(getPath()));
    }
}
