package pt.ipp.isep.dei.LAPR3.services;

/**
 * Interface defining the service for importing railway data from database.
 */
public interface DatabaseImporterService {

    /**
     * Imports all railway data from OracleDatabase
     *
     */
    void importAllFromDatabase();
}