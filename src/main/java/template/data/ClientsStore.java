///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package template.data;
//
//import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
//import pt.ipp.isep.dei.LAPR3.database.Persistable;
//import template.model.Address;
//import template.model.Client;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Objects;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * @author nunocastro
// */
//public abstract class ClientsStore implements Persistable{
//
//    @Override
//    public boolean save(DatabaseConnection databaseConnection, Object object) {
//        Client client = (Client) object;
//        boolean returnValue = false;
//
//        try {
//            saveClientToDatabase(databaseConnection, client);
//
//            //Delete addresses.
//            deleteClientAddresses(databaseConnection, client);
//
//            //Post new addresses.
//            addClientAddresses(databaseConnection, client);
//
//            //Save changes.
//            returnValue = true;
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ClientsStore.class.getName())
//                    .log(Level.SEVERE, null, ex);
//            databaseConnection.registerError(ex);
//            returnValue = false;
//        }
//        return returnValue;
//    }
//
//    @Override
//    public boolean delete(DatabaseConnection databaseConnection,
//                          Object object) {
//        boolean returnValue = false;
//        Connection connection = databaseConnection.getConnection();
//        Client client = (Client) object;
//
//        try {
//            //TODO: Não devia também apagar todos os endereços em Address?
//            // Por que motivo é que ficam lá pendurados?
//            String sqlCommand;
//            sqlCommand = "delete from clientaddress where cli_id = ?";
//            try (PreparedStatement deleteClienteAddressesPreparedStatement = connection.prepareStatement(
//                    sqlCommand)) {
//                deleteClienteAddressesPreparedStatement.setInt(1,
//                        client.getId());
//                deleteClienteAddressesPreparedStatement.executeUpdate();
//            }
//
//            sqlCommand = "delete from client where id = ?";
//            try (PreparedStatement deleteClientPreparedStatement = connection.prepareStatement(
//                    sqlCommand)) {
//                deleteClientPreparedStatement.setInt(1, client.getId());
//                deleteClientPreparedStatement.executeUpdate();
//            }
//
//            returnValue = true;
//
//        } catch (SQLException exception) {
//            Logger.getLogger(ClientsStore.class.getName())
//                    .log(Level.SEVERE, null, exception);
//            databaseConnection
//                    .registerError(exception);
//            returnValue = false;
//        }
//
//        return returnValue;
//    }
//
//    /**
//     * Checks is a cliente is already registered on the datase. If the client
//     * is registered, it updates it. If it is not, it inserts a new one.
//     *
//     * @param databaseConnection
//     * @param client
//     * @throws SQLException
//     */
//    private void saveClientToDatabase(DatabaseConnection databaseConnection,
//                                      Client client) throws SQLException {
//
//        if (isClientOnDatabase(databaseConnection, client)) {
//            updateClientOnDatabase(databaseConnection, client);
//        } else {
//            insertClientOnDatabase(databaseConnection, client);
//        }
//
//    }
//
//    /**
//     * Checks if a client is registered on the Database by its ID.
//     *
//     * @param databaseConnection
//     * @param client
//     * @return True if the cliente is registered, False if otherwise.
//     * @throws SQLException
//     */
//    private boolean isClientOnDatabase(DatabaseConnection databaseConnection,
//                                       Client client) throws SQLException {
//        Connection connection = databaseConnection.getConnection();
//
//        boolean isClientOnDatabase = false;
//
//        String sqlCommand = "select * from client where id = ?";
//
//        PreparedStatement getClientsPreparedStatement =
//                connection.prepareStatement(sqlCommand);
//
//        getClientsPreparedStatement.setInt(1, client.getId());
//
//        try (ResultSet clientsResultSet = getClientsPreparedStatement.executeQuery()) {
//
//            if (clientsResultSet.next()) {
//                // if client already exists in the database
//                isClientOnDatabase = true;
//            } else {
//
//                // if client does not exist in the database
//                isClientOnDatabase = false;
//            }
//        }
//        return isClientOnDatabase;
//    }
//
//    /**
//     * Adds a new client record to the database.
//     *
//     * @param databaseConnection
//     * @param client
//     * @throws SQLException
//     */
//    private void insertClientOnDatabase(DatabaseConnection databaseConnection,
//                                        Client client) throws SQLException {
//        Connection connection = databaseConnection.getConnection();
//        String sqlCommand =
//                "insert into client(name, vatnr, id) values (?, ?, ?)";
//
//        executeClientStatementOnDatabase(databaseConnection, client,
//                sqlCommand);
//    }
//
//    /**
//     * Updates an existing client record on the database.
//     *
//     * @param databaseConnection
//     * @param client
//     * @throws SQLException
//     */
//    private void updateClientOnDatabase(DatabaseConnection databaseConnection,
//                                        Client client) throws SQLException {
//        Connection connection = databaseConnection.getConnection();
//        String sqlCommand =
//                "update client set name = ?, vatnr = ? where id = ?";
//
//        executeClientStatementOnDatabase(databaseConnection, client,
//                sqlCommand);
//    }
//
//    /**
//     * Executes the save Client Statement.
//     *
//     * @param databaseConnection
//     * @param client
//     * @throws SQLException
//     */
//    private void executeClientStatementOnDatabase(
//            DatabaseConnection databaseConnection,
//            Client client, String sqlCommand) throws SQLException {
//        Connection connection = databaseConnection.getConnection();
//
//        PreparedStatement saveClientPreparedStatement =
//                connection.prepareStatement(
//                        sqlCommand);
//        saveClientPreparedStatement.setString(1, client.getName());
//        saveClientPreparedStatement.setString(2, client.getVatnr());
//        saveClientPreparedStatement.setInt(3, client.getId());
//        saveClientPreparedStatement.executeUpdate();
//    }
//
//    private void deleteClientAddresses(DatabaseConnection databaseConnection,
//                                       Client client) throws SQLException {
//        Connection connection = databaseConnection.getConnection();
//        String sqlCommand;
//
//        //Delete addresses.
//        sqlCommand = "select * from clientaddress where cli_id = ?";
//        try (PreparedStatement getClientAddressesPreparedStatement = connection.prepareStatement(
//                sqlCommand)) {
//            getClientAddressesPreparedStatement.setInt(1, client.getId());
//            try (ResultSet clientAddressesResultSet = getClientAddressesPreparedStatement.executeQuery()) {
//                //TODO: isto podia ser simplificado caso se fizesse o
//                // override do equals na classe Address
//                while (clientAddressesResultSet.next()) {
//                    boolean found = false;
//                    for (int i = 0;
//                         i < client.getAddressList().size() && !found;
//                         i++) {
//                        Address address =
//                                client.getAddressList().get(i);
//
//                        Integer addr_id =
//                                clientAddressesResultSet.getInt(
//                                        "addr_id");
//
//                        if (Objects.equals(addr_id, address.getId())) {
//                            found = true;
//                        }
//                    }
//                    if (!found) {
//                        sqlCommand =
//                                "delete from clientaddress where cli_id = ? and addr_id = ?";
//
//                        try (PreparedStatement clienteAddressDeletePreparedStatement = connection.prepareStatement(
//                                sqlCommand)) {
//                            clienteAddressDeletePreparedStatement.setInt(
//                                    1,
//                                    client.getId());
//                            clienteAddressDeletePreparedStatement.setInt(
//                                    2,
//                                    clientAddressesResultSet.getInt(
//                                            "addr_id"));
//                            clienteAddressDeletePreparedStatement.executeUpdate();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void addClientAddresses(DatabaseConnection databaseConnection,
//                                    Client client) throws SQLException {
//        Connection connection = databaseConnection.getConnection();
//        String sqlCommand = "";
//
//        AddressesStore addressesStore = new AddressesStore();
//
//        for (int i = 0; i < client.getAddressList().size(); i++) {
//            Address address = client.getAddressList().get(i);
//            if (!addressesStore.save(databaseConnection, address)) {
//                throw databaseConnection.getLastError();
//            }
//
//            //Post association between clients and addresses.
//            sqlCommand =
//                    "select * from clientaddress where cli_id = ? and addr_id = ?";
//            try (PreparedStatement clientAddressesPreparedStatement = connection.prepareStatement(
//                    sqlCommand)) {
//                clientAddressesPreparedStatement.setInt(1,
//                        client.getId());
//                clientAddressesPreparedStatement.setInt(2,
//                        address.getId());
//
//                try (ResultSet clientAddressesResultSet = clientAddressesPreparedStatement.executeQuery()) {
//                    if (!clientAddressesResultSet.next()) {
//                        sqlCommand =
//                                "insert into clientaddress(cli_id, addr_id) values (?, ?)";
//                        try (PreparedStatement insertClientAddressPreparedStatement = connection.prepareStatement(
//                                sqlCommand)) {
//                            insertClientAddressPreparedStatement.setInt(
//                                    1, client.getId());
//                            insertClientAddressPreparedStatement.setInt(
//                                    2, address.getId());
//                            insertClientAddressPreparedStatement.executeUpdate();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}