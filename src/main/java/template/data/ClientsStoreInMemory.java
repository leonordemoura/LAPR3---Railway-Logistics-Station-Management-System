/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template.data;

import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.database.Persistable;
import template.model.Address;
import template.model.Client;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author nunocastro
 */
public abstract class ClientsStoreInMemory implements Persistable {

    public static HashMap<Integer, Client> clients = new HashMap<>();

    @Override
    public boolean save(DatabaseConnection databaseConnection, Object object) {
        Client client = (Client) object;
        boolean returnValue = false;

        /**
         * simulate db validation behaviour
         */
        for (Address address : client.getAddressList()) {
            if (address.getStreet().length() > 30) {
                return false;
            }
        }
        saveClientToDatabase(databaseConnection, client);

        return true;
    }

    @Override
    public boolean delete(DatabaseConnection databaseConnection,
                          Object object) {

        Client client = (Client) object;

        clients.remove(client.getId());

        return true;
    }

    /**
     * Checks is a cliente is already registered on the datase. If the client
     * is registered, it updates it. If it is not, it inserts a new one.
     *
     * @param databaseConnection
     * @param client
     * @throws SQLException
     */
    private void saveClientToDatabase(DatabaseConnection databaseConnection,
                                      Client client) {

        if (isClientOnDatabase(databaseConnection, client)) {
            updateClientOnDatabase(databaseConnection, client);
        } else {
            insertClientOnDatabase(databaseConnection, client);
        }

    }

    /**
     * Checks if a client is registered on the Database by its ID.
     *
     * @param databaseConnection
     * @param client
     * @return True if the cliente is registered, False if otherwise.
     * @throws SQLException
     */
    private boolean isClientOnDatabase(DatabaseConnection databaseConnection,
                                       Client client) {
        return clients.containsKey(client.getId());
    }

    /**
     * Adds a new client record to the database.
     *
     * @param databaseConnection
     * @param client
     * @throws SQLException
     */
    private void insertClientOnDatabase(DatabaseConnection databaseConnection,
                                        Client client) {
        clients.put(client.getId(), client);
    }

    /**
     * Updates an existing client record on the database.
     *
     * @param databaseConnection
     * @param client
     * @throws SQLException
     */
    private void updateClientOnDatabase(DatabaseConnection databaseConnection,
                                        Client client) {
        clients.replace(client.getId(), client);
    }
}