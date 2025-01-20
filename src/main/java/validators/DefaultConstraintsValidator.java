package validators;

import entities.Composable;
import entities.Drive;
import entities.Entity;
import entities.EntityType;
import exceptions.PathNotFoundException;
import lombok.RequiredArgsConstructor;
import utils.NavigationUtils;
import utils.PathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class DefaultConstraintsValidator implements ConstraintsValidator {

    private final List<Entity> drives;
    private final PathUtils pathUtils;
    private final NavigationUtils navigationUtils;

    @Override
    public boolean anyValueNull(Object... values) {
        return Arrays.stream(values).anyMatch(Objects::isNull);
    }

    @Override
    public boolean pathExists(String path) {
        String trimmmedPath = pathUtils.trimPath(path);

        boolean pathExists;

        try {
            pathExists = "".equals(path) ||
                    entityAlreadyExists(EntityType.DRIVE, trimmmedPath) ||
                    entityAlreadyExists(EntityType.FOLDER, trimmmedPath) ||
                    entityAlreadyExists(EntityType.ZIP_FILE, trimmmedPath);
        } catch (Exception e) {
            pathExists = false;
        }

        return pathExists;
    }

    @Override
    public boolean entityAlreadyExists(EntityType type, String path) {
        String[] pathParts = pathUtils.trimAndSplitPath(path); // this is the whole full path up to the desired new element
        String lastElement = pathParts[pathParts.length - 1];
        String[] pathWithoutNewElement = pathParts.length > 1 ? pathUtils.excludeLastElement(pathParts) : pathParts;

        Optional<Drive> drive = navigationUtils.findDrive(drives, pathParts[0]);

        if (drive.isEmpty()) {
            if (pathParts.length == 1) {
                // If there is only one element, we are checking for a Drive, and if we get here, the Drive does not exist
                return false;
            } else {
                throw new PathNotFoundException("Drive not found.");
            }
        }

        Composable currentEntity = navigationUtils.navigateToComposableEntity(drives, pathWithoutNewElement);

        // If we only have one element, we are checking for a drive, and if we reached this far, the drive does exist.
        // Otherwise, the last check is to make sure there are no other entity of the same type with the same name in the location.
        boolean entityExists = pathParts.length == 1 || currentEntity.getContents().stream()
                .anyMatch(it -> it.getName().equals(lastElement) && it.getEntityType() == type);;

        return entityExists;
    }

    @Override
    public boolean isPathSuitableForEntityType(EntityType type, String path) {
        boolean isPathSuitable;

        try {
            isPathSuitable = switch (type) {
                case TEXT_FILE -> !entityAlreadyExists(type, path);
                case ZIP_FILE, FOLDER -> !entityAlreadyExists(EntityType.ZIP_FILE, path) && !entityAlreadyExists(EntityType.FOLDER, path);
                case DRIVE -> pathUtils.trimAndSplitPath(path).length == 1 && !entityAlreadyExists(type, path);
            };
        } catch (Exception e) {
            isPathSuitable = false;
        }

        return isPathSuitable;
    }
}
