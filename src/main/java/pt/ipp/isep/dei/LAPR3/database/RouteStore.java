package pt.ipp.isep.dei.LAPR3.database;

import pt.ipp.isep.dei.LAPR3.domain.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RouteStore implements Persistable<Route> {

    @Override
    public boolean save(DatabaseConnection databaseConnection, Route route) {
        boolean returnValue = false;

        try {
            saveRouteToDatabase(databaseConnection, route);
            returnValue = true;

        } catch (SQLException ex) {
            Logger.getLogger(RouteStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public boolean delete(DatabaseConnection databaseConnection, Route route) {
        boolean returnValue = false;

        try {
            Connection connection = databaseConnection.getConnection();

            // First delete related Path_Route entries
            String deletePathRoutes = "DELETE FROM Path_Route WHERE routeID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deletePathRoutes)) {
                pstmt.setInt(1, route.getRouteId());
                pstmt.executeUpdate();
            }

            // Then delete the route
            String deleteRoute = "DELETE FROM Route WHERE routeID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteRoute)) {
                pstmt.setInt(1, route.getRouteId());
                int rowsAffected = pstmt.executeUpdate();
                returnValue = rowsAffected > 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(RouteStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public List<Route> findAll(DatabaseConnection databaseConnection) {
        return new ArrayList<>();
    }

    @Override
    public boolean update(DatabaseConnection databaseConnection, Route route) {
        boolean returnValue = false;

        try {
            updateRouteOnDatabase(databaseConnection, route);
            returnValue = true;

        } catch (SQLException ex) {
            Logger.getLogger(RouteStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    /**
     * Checks if a route is already registered on the dataset. If the route
     * is registered, it updates it. If it is not, it inserts a new one.
     *
     * @param databaseConnection Database connection
     * @param route Route to save
     * @throws SQLException If database operation fails
     */
    private void saveRouteToDatabase(DatabaseConnection databaseConnection,
                                     Route route) throws SQLException {

        if (isRouteOnDatabase(databaseConnection, route)) {
            updateRouteOnDatabase(databaseConnection, route);
        } else {
            insertRouteOnDatabase(databaseConnection, route);
        }
    }

    /**
     * Checks if a route is registered on the Database by its ID.
     *
     * @param databaseConnection Database connection
     * @param route Route to check
     * @return True if the route is registered, False otherwise
     * @throws SQLException If database operation fails
     */
    private boolean isRouteOnDatabase(DatabaseConnection databaseConnection,
                                      Route route) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        boolean isRouteOnDatabase = false;

        String sqlCommand = "SELECT * FROM Route WHERE routeID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, route.getRouteId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isRouteOnDatabase = true;
                }
            }
        }
        return isRouteOnDatabase;
    }

    /**
     * Adds a new route record to the database.
     *
     * @param databaseConnection Database connection
     * @param route Route to insert
     * @throws SQLException If database operation fails
     */
    private void insertRouteOnDatabase(DatabaseConnection databaseConnection,
                                       Route route) throws SQLException {
        // Ensure route has an ID before inserting
        if (route.getRouteId() == 0) {
            int nextId = getNextRouteID(databaseConnection);
            route.setRouteId(nextId);
        }
        
        String sqlCommand =
                "INSERT INTO Route(routeID, endPointAID, endPointBID) " +
                        "VALUES (?, ?, ?)";

        executeRouteStatementOnDatabase(databaseConnection, route, sqlCommand, false);
    }

    /**
     * Updates an existing route record on the database.
     *
     * @param databaseConnection Database connection
     * @param route Route to update
     * @throws SQLException If database operation fails
     */
    private void updateRouteOnDatabase(DatabaseConnection databaseConnection,
                                       Route route) throws SQLException {
        String sqlCommand =
                "UPDATE Route SET endPointAID = ?, endPointBID = ? " +
                        "WHERE routeID = ?";

        executeRouteStatementOnDatabase(databaseConnection, route, sqlCommand, true);
    }

    /**
     * Executes the route statement on the database.
     *
     * @param databaseConnection Database connection
     * @param route Route to save
     * @param sqlCommand SQL command to execute
     * @param isUpdate True if this is an update operation, False if insert
     * @throws SQLException If database operation fails
     */
    private void executeRouteStatementOnDatabase(
            DatabaseConnection databaseConnection,
            Route route, String sqlCommand, boolean isUpdate) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try {
            if (isUpdate) {
                try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
                    pstmt.setInt(1, route.getStartFacilityId());
                    pstmt.setInt(2, route.getEndFacilityId());
                    pstmt.setInt(3, route.getRouteId());
                    pstmt.executeUpdate();
                }
            } else {
                try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
                    pstmt.setInt(1, route.getRouteId());
                    pstmt.setInt(2, route.getStartFacilityId());
                    pstmt.setInt(3, route.getEndFacilityId());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            if (requiresLegacyRouteStatement(ex)) {
                executeLegacyRouteStatement(connection, route, isUpdate);
            } else {
                throw ex;
            }
        }
    }

    private boolean requiresLegacyRouteStatement(SQLException ex) {
        if (ex == null) return false;
        if (ex instanceof SQLSyntaxErrorException) {
            return true;
        }
        return ex.getErrorCode() == 904;
    }

    private void executeLegacyRouteStatement(Connection connection, Route route, boolean isUpdate) throws SQLException {
        if (isUpdate) {
            String legacyUpdate = "UPDATE Route SET endPointAID = ?, endPointBID = ? WHERE routeID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(legacyUpdate)) {
                pstmt.setInt(1, route.getStartFacilityId());
                pstmt.setInt(2, route.getEndFacilityId());
                pstmt.setInt(3, route.getRouteId());
                pstmt.executeUpdate();
            }
        } else {
            // Ensure route has an ID before inserting
            if (route.getRouteId() == 0) {
                int nextId = getNextRouteIDFromConnection(connection);
                route.setRouteId(nextId);
            }
            
            String legacyInsert = "INSERT INTO Route(routeID, endPointAID, endPointBID) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(legacyInsert)) {
                pstmt.setInt(1, route.getRouteId());
                pstmt.setInt(2, route.getStartFacilityId());
                pstmt.setInt(3, route.getEndFacilityId());
                pstmt.executeUpdate();
            }
        }
    }
    
    private int getNextRouteIDFromConnection(Connection connection) throws SQLException {
        String sqlCommand = "SELECT NVL(MAX(ROUTEID), 0) + 1 AS nextID FROM ROUTE";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextID");
            }
        }
        return 1; // Default if query fails
    }

    public int getNextRouteID(DatabaseConnection databaseConnection) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        String sqlCommand = "SELECT NVL(MAX(ROUTEID), 0) + 1 AS nextID FROM ROUTE";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextID");
            }
        }
        return 1; // Default if query fails
    }
}