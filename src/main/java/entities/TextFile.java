package entities;

import exceptions.IllegalFileSystemOperationException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class TextFile extends Entity {
    private String content = "";

    public TextFile(@NonNull EntityType entityType, @NonNull String name, @NonNull String path) {
        super(entityType, name, path);
    }

    @Override
    public int getSize() {
        return content.length();
    }

    @Override
    public ExclusionEntityList<Entity> getContents() {
        throw new IllegalFileSystemOperationException("Text files are not Composable, and therefore, have no children.");
    }

    @Override
    public void updateDescendantsPath(String parentPath) {
        setPath(parentPath + "\\" + getName());
    }
}
