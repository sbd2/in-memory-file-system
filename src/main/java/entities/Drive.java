package entities;

import lombok.NonNull;

public final class Drive extends Entity implements Composable {

    public Drive(@NonNull EntityType entityType, @NonNull String name, @NonNull String path) {
        super(entityType, name, path);
    }

    @Override
    public int getSize() {
        return contents.stream().map(Sizeable::getSize).reduce(0, Integer::sum);
    }

    @Override
    public void clearContents() {
        contents.forEach(it -> {
            if (it instanceof Composable) ((Composable) it).clearContents();
        });
        contents.clear();
    }
}
