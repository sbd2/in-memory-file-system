package utils;

import entities.Composable;
import entities.Drive;
import entities.Entity;
import entities.EntityType;
import exceptions.PathNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Utility class that provides functions to ease navigation throughout the FileSystem hierarchy.
 */
public class NavigationUtils {

    /**
     * Finds a Drive by its name.
     *
     * @param drives the list of drives to search in.
     * @param driveName the name of the Drive to look for.
     * @return an Optional with the Drive (empty if not found).
     */
    public Optional<Drive> findDrive(List<Entity> drives, String driveName) {
        return  drives.stream()
                .filter(it -> it.getName().equals(driveName))
                .map(it -> (Drive) it)
                .findFirst();
    }

    /**
     * Navigates the whole hierarchy down to the last Composable entity and returns it.
     * The last element in the path must be a Composable entity as well.
     *
     * @param drives the list of drives containing the root to start the search.
     * @param pathParts a String list with the path.
     * @throws PathNotFoundException if either the drive does not exist or if any of the path elements is not a Composable entity.
     * @return the Composable entity at the end of the path.
     */
    public Composable navigateToComposableEntity(List<Entity> drives, String[] pathParts) {
        Composable nextEntity = findDrive(drives, pathParts[0]).orElseThrow(() -> new PathNotFoundException("Drive not found."));

        // Move through the path to reach the end and insert the new element
        for (int i = 1; i < pathParts.length; i++) {
            int currentPathElement = i;

            nextEntity = (Composable) nextEntity.getContents()
                    .stream()
                    .filter(it -> it.getName().equals(pathParts[currentPathElement]) && it instanceof Composable)
                    .findFirst().orElseThrow(() -> new PathNotFoundException("The path contains a nonexistent component: " + pathParts[currentPathElement]));
        }
        return nextEntity;
    }

    /**
     * Finds the child of a specific type contained in the parent Composable entity.
     *
     * @param type the type of Entity to look for.
     * @param name the name of the requested Entity.
     * @param parent the parent of the requested Entity.
     * @return an Optional with the Entity (empty if not found).
     */
    public Optional<Entity> getChildOfType(EntityType type, String name, Composable parent) {
        return parent.getContents()
                .stream()
                .filter(it -> it.getName().equals(name) && it.getEntityType() == type)
                .findFirst();
    }
}
