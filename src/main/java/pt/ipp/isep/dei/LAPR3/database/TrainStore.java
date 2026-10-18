package pt.ipp.isep.dei.LAPR3.database;

import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.Locomotive;
import pt.ipp.isep.dei.LAPR3.domain.Route;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.domain.Wagon;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TrainStore implements Persistable<Train> {

    @Override
    public boolean save(DatabaseConnection databaseConnection, Train train) {
        boolean returnValue = false;

        try {
            saveTrainToDatabase(databaseConnection, train);
            
            // Save train locomotives
            saveTrainLocomotives(databaseConnection, train);
            
            returnValue = true;

        } catch (SQLException ex) {
            Logger.getLogger(TrainStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public boolean delete(DatabaseConnection databaseConnection, Train train) {
        boolean returnValue = false;

        try {
            Connection connection = databaseConnection.getConnection();

            // First delete Train_Locomotive entries
            String deleteTrainLocomotives = "DELETE FROM Train_Locomotive WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteTrainLocomotives)) {
                pstmt.setInt(1, train.getTrainID());
                pstmt.executeUpdate();
            }

            // Then delete the train
            String deleteTrain = "DELETE FROM Train WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteTrain)) {
                pstmt.setInt(1, train.getTrainID());
                int rowsAffected = pstmt.executeUpdate();
                returnValue = rowsAffected > 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(TrainStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public List<Train> findAll(DatabaseConnection databaseConnection) {
        return new ArrayList<>();
    }

    @Override
    public boolean update(DatabaseConnection databaseConnection, Train train) {
        boolean returnValue = false;

        try {
            updateTrainOnDatabase(databaseConnection, train);
            
            // Update train locomotives only if they have changed
            // Get current locomotives from database
            List<Integer> currentLocomotiveIDs = getTrainLocomotiveIDs(databaseConnection, train.getTrainID());
            List<Integer> newLocomotiveIDs = train.getLocomotives().stream()
                    .map(Locomotive::getLocomotiveId)
                    .collect(Collectors.toList());
            
            // Sort both lists for comparison (order doesn't matter)
            currentLocomotiveIDs.sort(Integer::compareTo);
            newLocomotiveIDs.sort(Integer::compareTo);
            
            // Only update if locomotives have changed
            if (!currentLocomotiveIDs.equals(newLocomotiveIDs)) {
                // Update train locomotives (delete old, insert new)
                deleteTrainLocomotives(databaseConnection, train);
                saveTrainLocomotives(databaseConnection, train);
            }
            
            returnValue = true;

        } catch (SQLException ex) {
            Logger.getLogger(TrainStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    /**
     * Checks if a train is already registered on the dataset. If the train
     * is registered, it updates it. If it is not, it inserts a new one.
     *
     * @param databaseConnection Database connection
     * @param train Train to save
     * @throws SQLException If database operation fails
     */
    private void saveTrainToDatabase(DatabaseConnection databaseConnection,
                                     Train train) throws SQLException {

        if (isTrainOnDatabase(databaseConnection, train)) {
            updateTrainOnDatabase(databaseConnection, train);
        } else {
            insertTrainOnDatabase(databaseConnection, train);
        }
    }

    /**
     * Checks if a train is registered on the Database by its ID.
     *
     * @param databaseConnection Database connection
     * @param train Train to check
     * @return True if the train is registered, False otherwise
     * @throws SQLException If database operation fails
     */
    private boolean isTrainOnDatabase(DatabaseConnection databaseConnection,
                                      Train train) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        boolean isTrainOnDatabase = false;

        String sqlCommand = "SELECT * FROM Train WHERE trainID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, train.getTrainID());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isTrainOnDatabase = true;
                }
            }
        }
        return isTrainOnDatabase;
    }

    /**
     * Adds a new train record to the database.
     *
     * @param databaseConnection Database connection
     * @param train Train to insert
     * @throws SQLException If database operation fails
     */
    private void insertTrainOnDatabase(DatabaseConnection databaseConnection,
                                       Train train) throws SQLException {
        String sqlCommand =
                "INSERT INTO Train(trainID, routeID, startDate, maxLenght, status) " +
                        "VALUES (?, ?, ?, ?, ?)";

        executeTrainStatementOnDatabase(databaseConnection, train, sqlCommand, false);
    }

    /**
     * Updates an existing train record on the database.
     *
     * @param databaseConnection Database connection
     * @param train Train to update
     * @throws SQLException If database operation fails
     */
    private void updateTrainOnDatabase(DatabaseConnection databaseConnection,
                                       Train train) throws SQLException {
        String sqlCommand =
                "UPDATE Train SET routeID = ?, startDate = ?, maxLenght = ?, status = ? WHERE trainID = ?";

        executeTrainStatementOnDatabase(databaseConnection, train, sqlCommand, true);
    }

    /**
     * Executes the train statement on the database.
     *
     * @param databaseConnection Database connection
     * @param train Train to save
     * @param sqlCommand SQL command to execute
     * @param isUpdate True if this is an update operation, False if insert
     * @throws SQLException If database operation fails
     */
    private void executeTrainStatementOnDatabase(
            DatabaseConnection databaseConnection,
            Train train, String sqlCommand, boolean isUpdate) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        int maxLenght = calculateMaxLength(train);
        String status = train.getStatus() != null ? train.getStatus().name() : "PLANNED";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            if (isUpdate) {
                pstmt.setInt(1, train.getRoute().getRouteId());
                pstmt.setTimestamp(2, new Timestamp(train.getStartDate().getTime()));
                pstmt.setInt(3, maxLenght);
                pstmt.setString(4, status);
                pstmt.setInt(5, train.getTrainID());
            } else {
                pstmt.setInt(1, train.getTrainID());
                pstmt.setInt(2, train.getRoute().getRouteId());
                pstmt.setTimestamp(3, new Timestamp(train.getStartDate().getTime()));
                pstmt.setInt(4, maxLenght);
                pstmt.setString(5, status);
            }

            pstmt.executeUpdate();
        }
    }

    /**
     * Calculates the maximum length of a train by summing the lengths of all locomotives and wagons.
     *
     * @param train Train to calculate length for
     * @return Total length in meters (rounded to integer)
     */
    private int calculateMaxLength(Train train) {
        double totalLength = 0.0;

        // Add length of all locomotives
        for (Locomotive locomotive : train.getLocomotives()) {
            if (locomotive.getLocomotiveModelID() != null &&
                locomotive.getLocomotiveModelID().getVehicle() != null) {
                totalLength += locomotive.getLocomotiveModelID().getVehicle().getLength();
            }
        }

        // Add length of all wagons from freights
        for (Freight freight : train.getFreights()) {
            for (Wagon wagon : freight.getWagons()) {
                if (wagon.wagonModel() != null &&
                    wagon.wagonModel().getVehicle() != null) {
                    totalLength += wagon.wagonModel().getVehicle().getLength();
                }
            }
        }

        // Return rounded integer (length is in meters, maxLenght is number(10) in DB)
        return (int) Math.round(totalLength);
    }

    /**
     * Saves the locomotives associated with a train.
     * Enforces availability checks: one-train-per-locomotive and 30-minute scheduling buffer.
     *
     * @param databaseConnection Database connection
     * @param train Train with locomotives to save
     * @throws SQLException If database operation fails or validation fails
     */
    private void saveTrainLocomotives(DatabaseConnection databaseConnection,
                                      Train train) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        // Validate train status is PLANNED
        String checkTrainStatus = "SELECT status FROM Train WHERE trainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkTrainStatus)) {
            pstmt.setInt(1, train.getTrainID());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Train does not exist.");
                }
                String status = rs.getString("status");
                if (!"PLANNED".equals(status)) {
                    throw new SQLException("Locomotive can only be associated with a PLANNED train.");
                }
            }
        }

        String deleteCommand = "DELETE FROM Train_Locomotive WHERE locomotiveID = ?";
        String insertCommand = "INSERT INTO Train_Locomotive (trainID, locomotiveID) VALUES (?, ?)";
        List<Locomotive> persistedLocomotives = new ArrayList<>();

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteCommand);
             PreparedStatement insertStmt = connection.prepareStatement(insertCommand)) {
            
            for (Locomotive locomotive : train.getLocomotives()) {
                // Validate locomotive availability and scheduling
                validateLocomotiveAvailability(connection, train, locomotive.getLocomotiveId());
                
                // Remove any existing association for this locomotive (enforce one-train-per-locomotive)
                deleteStmt.setInt(1, locomotive.getLocomotiveId());
                deleteStmt.executeUpdate();
                
                // Insert new association
                insertStmt.setInt(1, train.getTrainID());
                insertStmt.setInt(2, locomotive.getLocomotiveId());
                try {
                    int insertedRows = insertStmt.executeUpdate();
                    if (insertedRows == 0) {
                        throw new SQLException("Failed to insert locomotive association");
                    }
                    
                    // Update locomotive status to ASSIGNED
                    String updateStatus = "UPDATE Locomotive SET status = 'ASSIGNED' WHERE locomotiveID = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateStatus)) {
                        updateStmt.setInt(1, locomotive.getLocomotiveId());
                        int updatedRows = updateStmt.executeUpdate();
                        if (updatedRows == 0) {
                            Logger.getLogger(TrainStore.class.getName())
                                    .log(Level.WARNING, 
                                        String.format("Locomotive %d not found when updating status", 
                                            locomotive.getLocomotiveId()));
                        }
                    }
                    
                    persistedLocomotives.add(locomotive);
                } catch (SQLException e) {
                    Logger.getLogger(TrainStore.class.getName())
                            .log(Level.SEVERE, 
                                String.format("Error assigning locomotive %d to train %d: %s", 
                                    locomotive.getLocomotiveId(), train.getTrainID(), e.getMessage()));
                    throw e;
                }
            }
        }

        // Keep the in-memory representation aligned with what was persisted
        List<Locomotive> trainLocomotives = train.getLocomotives();
        trainLocomotives.clear();
        trainLocomotives.addAll(persistedLocomotives);
    }

    /**
     * Validates if a train can be created with the given locomotives and start date.
     * Checks availability and scheduling constraints before creation.
     *
     * @param databaseConnection Database connection
     * @param route Route for the train
     * @param locomotives List of locomotives to validate
     * @param startDate Start date for the train (used as planned departure time)
     * @return List of validation error messages (empty if validation passes)
     */
    public List<String> validateTrainCreation(DatabaseConnection databaseConnection, 
                                             Route route, 
                                             List<Locomotive> locomotives, 
                                             java.util.Date startDate) {
        List<String> errors = new ArrayList<>();
        
        if (route == null) {
            errors.add("Route cannot be null.");
            return errors;
        }
        
        if (locomotives == null || locomotives.isEmpty()) {
            errors.add("At least one locomotive is required.");
            return errors;
        }
        
        if (startDate == null) {
            errors.add("Start date cannot be null.");
            return errors;
        }
        
        try {
            Connection connection = databaseConnection.getConnection();
            
            // Create a temporary train object for validation (with a dummy ID)
            Train tempTrain = new Train(-1, route, startDate, null);
            
            for (Locomotive locomotive : locomotives) {
                try {
                    validateLocomotiveAvailabilityForCreation(connection, tempTrain, locomotive.getLocomotiveId(), startDate);
                } catch (SQLException e) {
                    errors.add("Locomotive " + locomotive.getLocomotiveId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("Database error during validation: " + e.getMessage());
        }
        
        return errors;
    }

    /**
     * Validates locomotive availability for assignment to a train.
     * Checks if locomotive exists, is not in transit, and respects 30-minute scheduling buffer.
     *
     * @param connection Database connection
     * @param train Train to assign locomotive to
     * @param locomotiveID Locomotive ID to validate
     * @throws SQLException If validation fails
     */
    private void validateLocomotiveAvailability(Connection connection, Train train, int locomotiveID) throws SQLException {
        // Use the train's planned departure time if available, otherwise use startDate
        Date departureDate = train.getStartDate();
        validateLocomotiveAvailabilityForCreation(connection, train, locomotiveID, departureDate);
    }

    /**
     * Validates locomotive availability for assignment to a train with explicit departure date.
     * Checks if locomotive exists, is not in transit, and respects 30-minute scheduling buffer.
     *
     * @param connection Database connection
     * @param train Train to assign locomotive to
     * @param locomotiveID Locomotive ID to validate
     * @param plannedDepartureTime Planned departure time for the new train
     * @throws SQLException If validation fails
     */
    private void validateLocomotiveAvailabilityForCreation(Connection connection, Train train, int locomotiveID, java.util.Date plannedDepartureTime) throws SQLException {
        // Check if locomotive exists
        String checkLocomotive = "SELECT COUNT(*) as cnt FROM Locomotive WHERE locomotiveID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkLocomotive)) {
            pstmt.setInt(1, locomotiveID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("cnt") == 0) {
                    throw new SQLException("Locomotive does not exist.");
                }
            }
        }

        // Check if locomotive is already assigned to another train
        // First check without time columns (in case they don't exist yet)
        String checkAssignment = "SELECT tl.trainID " +
                                "FROM Train_Locomotive tl " +
                                "INNER JOIN Train t ON tl.trainID = t.trainID " +
                                "WHERE tl.locomotiveID = ? AND t.status IN ('PLANNED', 'RUNNING')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(checkAssignment)) {
            pstmt.setInt(1, locomotiveID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int currentTrainID = rs.getInt("trainID");
                    
                    // If it's the same train, allow (idempotent operation)
                    if (currentTrainID == train.getTrainID()) {
                        return;
                    }
                    
                    // Locomotive is assigned to another train - check scheduling constraint if time columns exist
                    try {
                        // Try to get current train's arrival time
                        String getCurrentTrainTimes = "SELECT planned_arrival_time FROM Train WHERE trainID = ?";
                        Timestamp currentArrival = null;
                        try (PreparedStatement timeStmt = connection.prepareStatement(getCurrentTrainTimes)) {
                            timeStmt.setInt(1, currentTrainID);
                            try (ResultSet timeRs = timeStmt.executeQuery()) {
                                if (timeRs.next()) {
                                    currentArrival = timeRs.getTimestamp("planned_arrival_time");
                                }
                            }
                        }
                        
                        // Get new train's departure time (use provided plannedDepartureTime or try to get from database)
                        Timestamp newDeparture = null;
                        if (plannedDepartureTime != null) {
                            newDeparture = new Timestamp(plannedDepartureTime.getTime());
                        } else {
                            // Try to get from database if train already exists
                            String getNewTrainTimes = "SELECT planned_departure_time FROM Train WHERE trainID = ?";
                            try (PreparedStatement timeStmt = connection.prepareStatement(getNewTrainTimes)) {
                                timeStmt.setInt(1, train.getTrainID());
                                try (ResultSet timeRs = timeStmt.executeQuery()) {
                                    if (timeRs.next()) {
                                        newDeparture = timeRs.getTimestamp("planned_departure_time");
                                    }
                                }
                            }
                        }
                        
                        // If both times are available, validate scheduling constraint
                        if (currentArrival != null && newDeparture != null) {
                            // Calculate minimum required departure time (arrival + 30 minutes)
                            long minRequiredTime = currentArrival.getTime() + (30 * 60 * 1000); // 30 minutes in milliseconds
                            
                            if (newDeparture.getTime() < minRequiredTime) {
                                throw new SQLException(
                                    String.format("Locomotive is in transit on train %d. New train departure (%s) must be at least 30 minutes after previous train arrival (%s). Minimum required departure: %s.",
                                        currentTrainID,
                                        newDeparture.toString(),
                                        currentArrival.toString(),
                                        new Timestamp(minRequiredTime).toString()));
                            }
                        } else {
                            // Time columns don't exist or are NULL - just block assignment to different train
                            throw new SQLException(
                                String.format("Locomotive is already assigned to train %d. Cannot assign to another train without scheduling information.", 
                                    currentTrainID));
                        }
                    } catch (SQLException e) {
                        // If it's a column not found error, just block the assignment
                        if (e.getErrorCode() == 904 || e.getMessage().contains("invalid identifier")) {
                            throw new SQLException(
                                String.format("Locomotive is already assigned to train %d. Cannot assign to another train.", 
                                    currentTrainID));
                        }
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * Gets the list of locomotive IDs currently associated with a train in the database.
     *
     * @param databaseConnection Database connection
     * @param trainID Train ID to check
     * @return List of locomotive IDs
     * @throws SQLException If database operation fails
     */
    private List<Integer> getTrainLocomotiveIDs(DatabaseConnection databaseConnection, int trainID) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        List<Integer> locomotiveIDs = new ArrayList<>();

        String sqlCommand = "SELECT locomotiveID FROM Train_Locomotive WHERE trainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, trainID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    locomotiveIDs.add(rs.getInt("locomotiveID"));
                }
            }
        }
        return locomotiveIDs;
    }

    /**
     * Deletes all locomotives associated with a train.
     *
     * @param databaseConnection Database connection
     * @param train Train whose locomotives to delete
     * @throws SQLException If database operation fails
     */
    private void deleteTrainLocomotives(DatabaseConnection databaseConnection,
                                        Train train) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "DELETE FROM Train_Locomotive WHERE trainID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, train.getTrainID());
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets the next available train ID from the database.
     *
     * @param databaseConnection Database connection
     * @return Next available train ID
     * @throws SQLException If database operation fails
     */
    public int getNextTrainID(DatabaseConnection databaseConnection) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        String sqlCommand = "SELECT NVL(MAX(trainID), 0) + 1 AS nextID FROM Train";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextID");
            }
        }
        return 1; // Default if query fails
    }

    /**
     * Disassociates a train when its route completes.
     * Moves locomotives to endRoute facility and makes them available.
     * Moves wagons to their freight's end facility and makes them available.
     * Removes all train associations.
     * Removes freights completely (they have reached their destination).
     * Removes the train completely (it has completed its route).
     *
     * @param databaseConnection Database connection
     * @param trainID Train ID to disassociate
     * @return true if successful, false otherwise
     */
    public boolean disassociateTrainOnRouteCompletion(DatabaseConnection databaseConnection, int trainID) {
        try {
            Connection connection = databaseConnection.getConnection();
            
            // Get the end facility of the route
            String getRouteEnd = "SELECT r.endPointBID FROM Train t " +
                                "INNER JOIN Route r ON t.routeID = r.routeID " +
                                "WHERE t.trainID = ?";
            int endRouteFacilityID;
            
            try (PreparedStatement pstmt = connection.prepareStatement(getRouteEnd)) {
                pstmt.setInt(1, trainID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        Logger.getLogger(TrainStore.class.getName())
                                .log(Level.WARNING, "Train " + trainID + " not found or has no route");
                        return false;
                    }
                    endRouteFacilityID = rs.getInt("endPointBID");
                }
            }
            
            // Update locomotives: move them to the end route facility and make them available
            String updateLocomotives = "UPDATE Locomotive l " +
                                      "SET l.startFacilityID = ?, " +
                                      "    l.status = 'AVAILABLE' " +
                                      "WHERE EXISTS (" +
                                      "    SELECT 1 FROM Train_Locomotive tl " +
                                      "    WHERE tl.locomotiveID = l.locomotiveID " +
                                      "    AND tl.trainID = ?" +
                                      ")";
            try (PreparedStatement pstmt = connection.prepareStatement(updateLocomotives)) {
                pstmt.setInt(1, endRouteFacilityID);
                pstmt.setInt(2, trainID);
                pstmt.executeUpdate();
            }
            
            // Update wagons: move them to their freight's end facility and make them available
            String updateWagons = "UPDATE Wagon w " +
                                 "SET w.startFacility = (" +
                                 "    SELECT f.endFacilityID " +
                                 "    FROM Freight_Wagon fw " +
                                 "    INNER JOIN Freight f ON fw.freightID = f.freightID " +
                                 "    INNER JOIN Train_Freight tf ON f.freightID = tf.freightID " +
                                 "    WHERE fw.wagonID = w.wagonID " +
                                 "    AND tf.trainID = ? " +
                                 "    AND ROWNUM = 1" +
                                 "), " +
                                 "w.status = 'AVAILABLE' " +
                                 "WHERE EXISTS (" +
                                 "    SELECT 1 FROM Freight_Wagon fw " +
                                 "    INNER JOIN Freight f ON fw.freightID = f.freightID " +
                                 "    INNER JOIN Train_Freight tf ON f.freightID = tf.freightID " +
                                 "    WHERE fw.wagonID = w.wagonID " +
                                 "    AND tf.trainID = ?" +
                                 ")";
            try (PreparedStatement pstmt = connection.prepareStatement(updateWagons)) {
                pstmt.setInt(1, trainID);
                pstmt.setInt(2, trainID);
                pstmt.executeUpdate();
            }
            
            // Get all freight IDs associated with this train before deleting associations
            List<Integer> freightIDs = new ArrayList<>();
            String getFreightIDs = "SELECT freightID FROM Train_Freight WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(getFreightIDs)) {
                pstmt.setInt(1, trainID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        freightIDs.add(rs.getInt("freightID"));
                    }
                }
            }
            
            // Remove train associations (locomotives and freights)
            String deleteLocomotives = "DELETE FROM Train_Locomotive WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteLocomotives)) {
                pstmt.setInt(1, trainID);
                pstmt.executeUpdate();
            }
            
            String deleteTrainFreights = "DELETE FROM Train_Freight WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteTrainFreights)) {
                pstmt.setInt(1, trainID);
                pstmt.executeUpdate();
            }
            
            // Remove Freight_Wagon associations and delete freights completely
            // Freights have reached their destination, so they should be removed
            for (Integer freightID : freightIDs) {
                // Delete Freight_Wagon associations
                String deleteFreightWagons = "DELETE FROM Freight_Wagon WHERE freightID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteFreightWagons)) {
                    pstmt.setInt(1, freightID);
                    pstmt.executeUpdate();
                }
                
                // Delete the freight completely - it has reached its destination
                String deleteFreight = "DELETE FROM Freight WHERE freightID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(deleteFreight)) {
                    pstmt.setInt(1, freightID);
                    pstmt.executeUpdate();
                }
            }
            
            // Delete the train completely - it has completed its route
            String deleteTrain = "DELETE FROM Train WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteTrain)) {
                pstmt.setInt(1, trainID);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted == 0) {
                    Logger.getLogger(TrainStore.class.getName())
                            .log(Level.WARNING, "Train " + trainID + " not found when trying to delete");
                    return false;
                }
            }
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(TrainStore.class.getName())
                    .log(Level.SEVERE, "Error disassociating train on route completion", ex);
            databaseConnection.registerError(ex);
            return false;
        }
    }
}

