package orchestrators;

import entities.Drive;
import entities.Entity;
import entities.EntityFactory;
import lombok.Getter;
import services.DefaultFileSystemManagementService;
import services.FileSystemManagementService;
import utils.NavigationUtils;
import utils.PathUtils;
import validators.ConstraintsValidator;
import validators.DefaultConstraintsValidator;

import java.util.List;

/**
 * This class is responsible for building the objects and injecting the dependencies in them.
 * It is also responsible for holding the references to the objects so that they can be reused, reducing overhead.
 */
@Getter
public class DependencyOrchestrator {
    private static DependencyOrchestrator instance;
    private final List<Entity> drives;
    private FileSystemManagementService fileSystemManagementService;
    private ConstraintsValidator constraintsValidator;
    private NavigationUtils navigationUtils;
    private PathUtils pathUtils;
    private EntityFactory entityFactory;

    public static DependencyOrchestrator getInstance(List<Entity> drives) {
        if (instance == null) instance = new DependencyOrchestrator(drives);
        return instance;
    }

    private DependencyOrchestrator(List<Entity> drives) {
        this.drives = drives;
        injectDependencies();
    }

    private void injectDependencies() {
        pathUtils = new PathUtils();
        navigationUtils = new NavigationUtils();
        entityFactory = new EntityFactory();
        constraintsValidator = new DefaultConstraintsValidator(drives, pathUtils, navigationUtils);
        fileSystemManagementService = new DefaultFileSystemManagementService(constraintsValidator, drives, pathUtils, navigationUtils, entityFactory);
    }
}
