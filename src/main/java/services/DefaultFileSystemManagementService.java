package services;

import entities.*;
import exceptions.IllegalFileSystemOperationException;
import exceptions.NotATextFileException;
import exceptions.PathAlreadyExistsException;
import exceptions.PathNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.NavigationUtils;
import utils.PathUtils;
import validators.ConstraintsValidator;

import java.util.List;

@RequiredArgsConstructor
public class DefaultFileSystemManagementService implements FileSystemManagementService {

    private final ConstraintsValidator validator;
    private final List<Entity> drives;
    private final PathUtils pathUtils;
    private final NavigationUtils navigationUtils;
    private final EntityFactory entityFactory;
    private final Logger log = LogManager.getLogger(DefaultFileSystemManagementService.class);

    @Override
    public void create(EntityType type, String name, String path) {
        String fullPath = pathUtils.trimPath(path + "\\" + name);
        if (validator.anyValueNull(type, name, path)) throw new IllegalFileSystemOperationException("Type, Name and Path cannot be null.");
        if (validator.entityAlreadyExists(type, fullPath)) throw new PathAlreadyExistsException("The entity already exists under that path.");
        if (!validator.isPathSuitableForEntityType(type, fullPath)) throw new IllegalFileSystemOperationException("The provided path does not apply to this entity type.");
        if (!validator.pathExists(path)) throw new PathNotFoundException("Path does not exist.");

        log.info("Creating {} under {}", type, fullPath);

        String[] pathParts = pathUtils.trimAndSplitPath(path);

        if (type == EntityType.DRIVE) {
            drives.add(entityFactory.createEntity(type, name, path));
        } else {
            Composable parentEntity = navigationUtils.navigateToComposableEntity(drives, pathParts);

            parentEntity.getContents().add(entityFactory.createEntity(type, name, path));
        }

        log.info("Successfully created {} under {}", type, fullPath);
    }

    @Override
    public void delete(EntityType type, String path) {
        if (validator.anyValueNull(path)) throw new IllegalFileSystemOperationException("Path cannot be null.");
        if (!validator.entityAlreadyExists(type, path)) throw new PathNotFoundException("The provided path does not exist.");

        log.info("Deleting {} from {}", type, path);

        String[] pathParts = pathUtils.trimAndSplitPath(path);

        List<Entity> parentList;
        Entity elementToDelete;
        if (EntityType.DRIVE == type) {
            parentList = drives;
            elementToDelete = drives.stream().filter(it -> it.getPath().equals(path)).findFirst().get();
        } else {
            Composable parent = navigationUtils.navigateToComposableEntity(drives, pathUtils.excludeLastElement(pathParts));
            parentList = parent.getContents();
            elementToDelete = navigationUtils.getChildOfType(type, pathParts[pathParts.length - 1], parent).get();
        }

        if (elementToDelete instanceof Composable) ((Composable) elementToDelete).clearContents();
        parentList.remove(elementToDelete);

        log.info("Successfully delete {} from {}", type, path);
    }

    @Override
    public void move(EntityType type, String sourcePath, String destinationPath) {
        if (validator.anyValueNull(sourcePath, destinationPath)) throw new IllegalFileSystemOperationException("Path cannot be null.");
        if (!validator.entityAlreadyExists(type, sourcePath)) throw new PathNotFoundException("The requested entity does not exist.");
        String[] destinationWithoutNewEntity = pathUtils.excludeLastElement(pathUtils.trimAndSplitPath(destinationPath));
        if (!validator.pathExists(pathUtils.pathToString(destinationWithoutNewEntity))) throw new PathNotFoundException("The destination path does not exist.");
        if (validator.entityAlreadyExists(type, destinationPath)) throw new PathAlreadyExistsException("The requested entity already exists in destination path.");
        if (EntityType.DRIVE == type) throw new IllegalFileSystemOperationException("Drives may not be moved around.");

        log.info("Moving {} from {}", type, sourcePath);

        String[] sourcePathParts = pathUtils.trimAndSplitPath(sourcePath);
        String[] sourcePathWithoutEntity = pathUtils.excludeLastElement(sourcePathParts);
        Composable oldParent = navigationUtils.navigateToComposableEntity(drives, sourcePathWithoutEntity);

        Entity movingEntity = navigationUtils.getChildOfType(type, sourcePathParts[sourcePathParts.length - 1], oldParent).get();

        Composable newParent = navigationUtils.navigateToComposableEntity(drives, destinationWithoutNewEntity);
        newParent.getContents().add(movingEntity);
        movingEntity.updateDescendantsPath(((Entity) newParent).getPath());
        oldParent.getContents().remove(movingEntity);

        log.info("Successfully moved {} to {}", type, destinationPath);
    }

    @Override
    public void writeToFile(String path, String content) {
        if (validator.anyValueNull(path, content)) throw new IllegalFileSystemOperationException("Path and content cannot be null.");
        if (!validator.entityAlreadyExists(EntityType.TEXT_FILE, path)) throw new NotATextFileException("The provided path is not a text file.");

        log.info("Writing {} to file in {}", content, path);

        String[] pathParts = pathUtils.trimAndSplitPath(path);

        Composable parent = navigationUtils.navigateToComposableEntity(drives, pathUtils.excludeLastElement(pathParts));

        TextFile textFile = (TextFile) navigationUtils.getChildOfType(EntityType.TEXT_FILE, pathParts[pathParts.length - 1], parent).get();

        textFile.setContent(textFile.getContent() + content);

        log.info("Successfully appended content. New content is {}", textFile.getContent());
    }

    @Override
    public int getEntitySize(EntityType type, String path) {
        if (validator.anyValueNull(type, path)) throw new IllegalFileSystemOperationException("Entity type and path cannot be null.");
        if (!validator.entityAlreadyExists(type, path)) throw new PathNotFoundException("The requested entity does not exist.");

        log.info("Getting size info for {} under {}", type, path);

        String[] pathParts = pathUtils.trimAndSplitPath(path);

        Composable parent = navigationUtils.navigateToComposableEntity(drives, pathUtils.excludeLastElement(pathParts));

        Entity entity = type == EntityType.DRIVE ?
                navigationUtils.findDrive(drives, path).get() :
                navigationUtils.getChildOfType(type, pathParts[pathParts.length - 1], parent).get();

        return entity.getSize();
    }
}
