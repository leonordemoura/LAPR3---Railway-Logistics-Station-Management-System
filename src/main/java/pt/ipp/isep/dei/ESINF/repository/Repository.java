package pt.ipp.isep.dei.ESINF.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining basic data operations.
 *
 * @param <T> the type of objects managed by the repository
 */
public interface Repository<T> {

    /**
     * Retrieves all items from the repository.
     *
     * @return a list of all items
     */
    List<T> getAll();

    /**
     * Saves an item to the repository.
     *
     * @param item the item to save
     * @return an Optional containing the saved item
     */
    Optional<T> save(T item);
}
