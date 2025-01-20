package entities;

import exceptions.IllegalFileSystemOperationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ExclusionEntityList is a custom List type that allows one type to be prohibited in the list.
 * This List type can be used when we want a list of type T, but we don't want to allow any items of type E in the list
 * and E extends T.
 *
 * @param <T> the (super)type for all list elements.
 */
@RequiredArgsConstructor
public class ExclusionEntityList<T> extends ArrayList<T> {

    private final Class<?> excludedType;

    @Override
    public boolean add(T element) {
        if (excludedType.isInstance(element)) {
            throw new IllegalFileSystemOperationException(getErrorMessage());
        }
        return super.add(element);
    }

    @Override
    public void add(int index, T element) {
        if (excludedType.isInstance(element)) {
            throw new IllegalFileSystemOperationException(getErrorMessage());
        }
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        for (T e : collection) {
            if (excludedType.isInstance(e)) {
                throw new IllegalFileSystemOperationException(getErrorMessage());
            }
        }
        return super.addAll(collection);
    }

    private String getErrorMessage() {
        return "Instances of " + excludedType.getName() + " are not allowed in this list.";
    }
}
