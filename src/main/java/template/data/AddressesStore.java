//package template.data;
//
//import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
//import pt.ipp.isep.dei.LAPR3.database.Persistable;
//import template.model.Address;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Addresses can only be manipulated by its parent object - Client -
// * therefore save, update and delete actions are only accessible from
// * inside ClientsStore class.
// *
// * @author nunocastro
// */
//public class AddressesStore implements Persistable {
//
//    @Override
//    public boolean save(DatabaseConnection databaseConnection,
//                        Object object) {
//
//        Connection connection = databaseConnection.getConnection();
//        Address address = (Address) object;
//
//        String sqlCommand = "select * from address where id = ?";
//        boolean returnValue = false;
//        try (PreparedStatement getAddressedPreparedStatement = connection.prepareStatement(
//                sqlCommand)) {
//            getAddressedPreparedStatement.setInt(1, address.getId());
//            try (ResultSet addressesResultSet = getAddressedPreparedStatement.executeQuery()) {
//                if (addressesResultSet.next()) {
//                    sqlCommand =
//                            "update address set street = ?, postalcode = ?, city = ? where id = ?";
//                } else {
//                    sqlCommand =
//                            "insert into address(street, postalcode, city, id) values (?, ?, ?, ?)";
//                }
//
//                try (PreparedStatement saveAddressPreparedStatement = connection.prepareStatement(
//                        sqlCommand)) {
//                    saveAddressPreparedStatement.setString(1,
//                            address.getStreet());
//                    saveAddressPreparedStatement.setString(2,
//                            address.getPostalcode());
//                    saveAddressPreparedStatement.setString(3,
//                            address.getCity());
//                    saveAddressPreparedStatement.setInt(4, address.getId());
//                    saveAddressPreparedStatement.executeUpdate();
//                    returnValue = true;
//                }
//            }
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
//
//        Connection conn = databaseConnection.getConnection();
//        Address address = (Address) object;
//
//        boolean returnValue = false;
//        try {
//            String sqlCommand;
//            sqlCommand = "delete from address where id = ?";
//            try (PreparedStatement deleteAddressPreparedStatement = conn.prepareStatement(
//                    sqlCommand)) {
//                deleteAddressPreparedStatement.setInt(1, address.getId());
//                deleteAddressPreparedStatement.executeUpdate();
//                returnValue = true;
//            }
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
//    public Object findById(DatabaseConnection databaseConnection, Object o) {
//        return null;
//    }
//
//    @Override
//    public List findAll(DatabaseConnection databaseConnection) {
//        return List.of();
//    }
//
//    @Override
//    public boolean update(DatabaseConnection databaseConnection, Object object) {
//        return false;
//    }
//
//    @Override
//    public boolean exists(DatabaseConnection databaseConnection, Object o) {
//        return false;
//    }
//}
