package pt.ipp.isep.dei.LAPR3.database;

import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.Path;
import pt.ipp.isep.dei.LAPR3.domain.constants.FacilityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PathStore implements Persistable<Path> {

    @Override
    public boolean save(DatabaseConnection databaseConnection, Path path) {
        boolean returnValue = false;

        try {
            savePathToDatabase(databaseConnection, path);

            // Save associated facilities
            savePathFacilities(databaseConnection, path);

            // Save associated route if exists
            if (path.getRoute() != null) {
                savePathRoute(databaseConnection, path);
            }

            returnValue = true;

        } catch (SQLException ex) {
            Logger.getLogger(PathStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public boolean delete(DatabaseConnection databaseConnection, Path path) {
        boolean returnValue = false;

        try {
            Connection connection = databaseConnection.getConnection();

            // First delete Path_Facility entries
            String deletePathFacilities = "DELETE FROM Path_Facility WHERE pathID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deletePathFacilities)) {
                pstmt.setInt(1, path.getPathID());
                pstmt.executeUpdate();
            }

            // Delete Path_Route entries
            String deletePathRoutes = "DELETE FROM Path_Route WHERE pathID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deletePathRoutes)) {
                pstmt.setInt(1, path.getPathID());
                pstmt.executeUpdate();
            }

            // Then delete the path
            String deletePath = "DELETE FROM Path WHERE pathID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deletePath)) {
                pstmt.setInt(1, path.getPathID());
                int rowsAffected = pstmt.executeUpdate();
                returnValue = rowsAffected > 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PathStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public List<Path> findAll(DatabaseConnection databaseConnection) {
        List<Path> paths = new ArrayList<>();

        try {
            Connection connection = databaseConnection.getConnection();
            String sqlCommand = "SELECT pathID FROM Path ORDER BY pathID";

            try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int pathID = rs.getInt("pathID");
                    Path path = new Path(pathID);

                    // Load facilities for this path
                    loadPathFacilities(databaseConnection, path);

                    // Load route for this path
                    loadPathRoute(databaseConnection, path);

                    paths.add(path);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(PathStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return paths;
    }

    @Override
    public boolean update(DatabaseConnection databaseConnection, Path path) {
        boolean returnValue = false;

        try {
            // Path table itself has no updateable fields besides pathID (which is PK)
            // So we update the associated facilities and routes

            // First, delete existing associations
            deletePathFacilities(databaseConnection, path);
            deletePathRoutes(databaseConnection, path);

            // Then insert new associations
            savePathFacilities(databaseConnection, path);
            if (path.getRoute() != null) {
                savePathRoute(databaseConnection, path);
            }

            returnValue = true;

        } catch (SQLException ex) {
            Logger.getLogger(PathStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    /**
     * Checks if a path is already registered on the dataset. If the path
     * is registered, it updates it. If it is not, it inserts a new one.
     *
     * @param databaseConnection Database connection
     * @param path Path to save
     * @throws SQLException If database operation fails
     */
    private void savePathToDatabase(DatabaseConnection databaseConnection,
                                    Path path) throws SQLException {

        if (path.getPathID() > 0 && isPathOnDatabase(databaseConnection, path)) {
            // already exists
        } else {
            insertPathOnDatabase(databaseConnection, path);
        }
    }

    /**
     * Checks if a path is registered on the Database by its ID.
     *
     * @param databaseConnection Database connection
     * @param path Path to check
     * @return True if the path is registered, False otherwise
     * @throws SQLException If database operation fails
     */
    private boolean isPathOnDatabase(DatabaseConnection databaseConnection,
                                     Path path) throws SQLException {
        if (path.getPathID() <= 0) {
            return false;
        }
        Connection connection = databaseConnection.getConnection();
        boolean isPathOnDatabase = false;

        String sqlCommand = "SELECT * FROM Path WHERE pathID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, path.getPathID());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isPathOnDatabase = true;
                }
            }
        }
        return isPathOnDatabase;
    }

    /**
     * Adds a new path record to the database.
     *
     * @param databaseConnection Database connection
     * @param path Path to insert
     * @throws SQLException If database operation fails
     */
    private void insertPathOnDatabase(DatabaseConnection databaseConnection,
                                      Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        // Get the next pathID from the database
        int nextPathID = getNextPathID(connection);
        path.setPathID(nextPathID);

        String sqlCommand = "INSERT INTO Path (pathID) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, nextPathID);
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets the next available pathID by querying the database.
     *
     * @param connection Database connection
     * @return The next pathID
     * @throws SQLException If database operation fails
     */
    private int getNextPathID(Connection connection) throws SQLException {
        String sqlCommand = "SELECT NVL(MAX(pathID), 0) + 1 as nextID FROM Path";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextID");
            }
        }
        return 1;
    }

    /**
     * Saves the facilities associated with a path.
     *
     * @param databaseConnection Database connection
     * @param path Path with facilities to save
     * @throws SQLException If database operation fails
     */
    private void savePathFacilities(DatabaseConnection databaseConnection,
                                    Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        // Path_Facility requires: pathID, facilityID, "order", arrivalTime, departureTime (all NOT NULL)
        String sqlCommand = "INSERT INTO Path_Facility (pathID, facilityID, \"order\", arrivalTime, departureTime) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            if (path.getPathID() <= 0) {
                throw new IllegalStateException("Path must have a valid ID before saving facilities.");
            }
            
            // Use current timestamp as default for arrivalTime and departureTime
            // Since Path domain object doesn't store these times, we use the current time
            java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
            
            for (Path.PathFacilityStep step : path.getFacilities()) {
                pstmt.setInt(1, path.getPathID());
                pstmt.setInt(2, step.getFacility().getFacilityID());
                pstmt.setInt(3, step.getOrder());
                // Use current time for both arrival and departure (can be updated later via schedule)
                pstmt.setTimestamp(4, currentTime);
                pstmt.setTimestamp(5, currentTime);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Saves the route association with a path.
     *
     * @param databaseConnection Database connection
     * @param path Path with route to save
     * @throws SQLException If database operation fails
     */
    private void savePathRoute(DatabaseConnection databaseConnection,
                               Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        // Check if association already exists
        String checkSql = "SELECT * FROM Path_Route WHERE pathID = ? AND routeID = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            if (path.getPathID() <= 0) {
                throw new IllegalStateException("Path must have a valid ID before saving route association.");
            }
            checkStmt.setInt(1, path.getPathID());
            checkStmt.setInt(2, path.getRoute().getRouteId());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    // Insert if doesn't exist
                    String insertSql = "INSERT INTO Path_Route (pathID, routeID) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, path.getPathID());
                        insertStmt.setInt(2, path.getRoute().getRouteId());
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }

    /**
     * Loads facilities for a path from the database.
     *
     * @param databaseConnection Database connection
     * @param path Path to load facilities into
     * @throws SQLException If database operation fails
     */
    private void loadPathFacilities(DatabaseConnection databaseConnection,
                                    Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "SELECT pf.facilityID, pf.\"order\", pf.stopOrder, f.name, f.facilityTypeID " +
                "FROM Path_Facility pf " +
                "JOIN Facility f ON pf.facilityID = f.facilityID " +
                "WHERE pf.pathID = ? " +
                "ORDER BY pf.\"order\"";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, path.getPathID());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int facilityID = rs.getInt("facilityID");
                    int order = rs.getInt("order");
                    boolean stopOrder = "Y".equalsIgnoreCase(rs.getString("stopOrder"));
                    String name = rs.getString("name");
                    int facilityTypeID = rs.getInt("facilityTypeID");

                    FacilityType facilityType = FacilityType.getFacilityTypeById(facilityTypeID);

                    Facility facility = new Facility(facilityID, name, facilityType);
                    path.addFacility(facility, order, stopOrder);
                }
            }
        }
    }

    /**
     * Loads route for a path from the database.
     *
     * @param databaseConnection Database connection
     * @param path Path to load route into
     * @throws SQLException If database operation fails
     */
    private void loadPathRoute(DatabaseConnection databaseConnection,
                               Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "SELECT pr.routeID, r.startFacilityID, r.endfacilityID, " +
                "r.departureTime, r.isComplex " +
                "FROM Path_Route pr " +
                "JOIN Route r ON pr.routeID = r.routeID " +
                "WHERE pr.pathID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, path.getPathID());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Note: This creates a simplified Route object
                    // You might need to load full facility objects from repository
                    // Set route if you have full route object available
                    // path.setRoute(route);
                }
            }
        }
    }

    /**
     * Deletes all facilities associated with a path.
     *
     * @param databaseConnection Database connection
     * @param path Path whose facilities to delete
     * @throws SQLException If database operation fails
     */
    private void deletePathFacilities(DatabaseConnection databaseConnection,
                                      Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "DELETE FROM Path_Facility WHERE pathID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, path.getPathID());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes route association with a path.
     *
     * @param databaseConnection Database connection
     * @param path Path whose route to delete
     * @throws SQLException If database operation fails
     */
    private void deletePathRoutes(DatabaseConnection databaseConnection,
                                  Path path) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "DELETE FROM Path_Route WHERE pathID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, path.getPathID());
            pstmt.executeUpdate();
        }
    }

    public int getNextPathID(DatabaseConnection databaseConnection) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        String sqlCommand = "SELECT NVL(MAX(pathID), 0) + 1 AS nextID FROM PATH";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextID");
            }
        }
        return 1; // Default if query fails
    }
}
