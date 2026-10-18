package pt.ipp.isep.dei.LAPR3.database;

import java.util.List;

/**
 * Interface for database persistence operations (CRUD). Provided by the client.
 *
 * @author nunocastro
 */
public interface Persistable<T> {

    /**
     * Save an object to the database (INSERT or UPDATE).
     *
     * @param databaseConnection Database connection
     * @param object Object to save
     * @return Operation success
     */
    boolean save(DatabaseConnection databaseConnection, T object);

    /**
     * Delete an object from the database.
     *
     * @param databaseConnection Database connection
     * @param object Object to delete
     * @return Operation success
     */
    boolean delete(DatabaseConnection databaseConnection, T object);

    /**
     * Find all objects of this type.
     *
     * @param databaseConnection Database connection
     * @return List of all objects
     */
    List<T> findAll(DatabaseConnection databaseConnection);

    /**
     * Update an existing object in the database.
     *
     * @param databaseConnection Database connection
     * @param object Object to update
     * @return Operation success
     */
    boolean update(DatabaseConnection databaseConnection, T object);
}
