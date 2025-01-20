package entities;

import lombok.NonNull;

public final class ZipFile extends Entity implements Composable {

    public ZipFile(@NonNull EntityType entityType, @NonNull String name, @NonNull String path) {
        super(entityType, name, path);
    }

    @Override
    public int getSize() {
        return contents.stream().map(it -> it.getSize() / 2).reduce(0, Integer::sum);
    }

    @Override
    public void clearContents() {
        contents.forEach(it -> {
            if (it instanceof Composable) ((Composable) it).clearContents();
        });
        contents.clear();
    }
}
