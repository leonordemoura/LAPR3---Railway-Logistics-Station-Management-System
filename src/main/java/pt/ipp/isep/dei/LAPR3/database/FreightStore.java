package pt.ipp.isep.dei.LAPR3.database;

import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.domain.Wagon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FreightStore implements Persistable<Freight> {

    private static final Logger LOGGER = Logger.getLogger(FreightStore.class.getName());

    @Override
    public boolean save(DatabaseConnection databaseConnection, Freight freight) {
        boolean returnValue = false;

        try {
            saveFreightToDatabase(databaseConnection, freight);

            // Refresh wagons association
            deleteFreightWagons(databaseConnection, freight);
            saveFreightWagons(databaseConnection, freight);
            
            // Save Train_Freight relationship if freight has a train
            if (freight.hasTrain()) {
                saveTrainFreight(databaseConnection, freight);
            } else {
                deleteTrainFreight(databaseConnection, freight);
            }
            
            returnValue = true;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public boolean delete(DatabaseConnection databaseConnection, Freight freight) {
        boolean returnValue = false;

        try {
            Connection connection = databaseConnection.getConnection();

            // First delete Freight_Wagon entries
            String deleteFreightWagons = "DELETE FROM Freight_Wagon WHERE freightID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteFreightWagons)) {
                pstmt.setInt(1, freight.getFreightID());
                pstmt.executeUpdate();
            }

            // Then delete the freight
            String deleteFreight = "DELETE FROM Freight WHERE freightID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteFreight)) {
                pstmt.setInt(1, freight.getFreightID());
                int rowsAffected = pstmt.executeUpdate();
                returnValue = rowsAffected > 0;
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    @Override
    public List<Freight> findAll(DatabaseConnection databaseConnection) {
        return new ArrayList<>();
    }

    @Override
    public boolean update(DatabaseConnection databaseConnection, Freight freight) {
        boolean returnValue = false;

        try {
            updateFreightOnDatabase(databaseConnection, freight);
            
            // Update freight wagons (delete old, insert new)
            deleteFreightWagons(databaseConnection, freight);
            saveFreightWagons(databaseConnection, freight);
            
            returnValue = true;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
        }
        return returnValue;
    }

    /**
     * Checks if a freight is already registered on the dataset. If the freight
     * is registered, it updates it. If it is not, it inserts a new one.
     *
     * @param databaseConnection Database connection
     * @param freight Freight to save
     * @throws SQLException If database operation fails
     */
    private void saveFreightToDatabase(DatabaseConnection databaseConnection,
                                       Freight freight) throws SQLException {

        if (isFreightOnDatabase(databaseConnection, freight)) {
            updateFreightOnDatabase(databaseConnection, freight);
        } else {
            insertFreightOnDatabase(databaseConnection, freight);
        }
    }

    /**
     * Checks if a freight is registered on the Database by its ID.
     *
     * @param databaseConnection Database connection
     * @param freight Freight to check
     * @return True if the freight is registered, False otherwise
     * @throws SQLException If database operation fails
     */
    private boolean isFreightOnDatabase(DatabaseConnection databaseConnection,
                                       Freight freight) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        boolean isFreightOnDatabase = false;

        String sqlCommand = "SELECT * FROM Freight WHERE freightID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, freight.getFreightID());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isFreightOnDatabase = true;
                }
            }
        }
        return isFreightOnDatabase;
    }

    /**
     * Adds a new freight record to the database.
     *
     * @param databaseConnection Database connection
     * @param freight Freight to insert
     * @throws SQLException If database operation fails
     */
    private void insertFreightOnDatabase(DatabaseConnection databaseConnection,
                                        Freight freight) throws SQLException {
        String sqlCommand =
                "INSERT INTO Freight(freightID, startFacilityID, endFacilityID, \"date\") " +
                        "VALUES (?, ?, ?, ?)";

        executeFreightStatementOnDatabase(databaseConnection, freight, sqlCommand, false);
    }

    /**
     * Updates an existing freight record on the database.
     *
     * @param databaseConnection Database connection
     * @param freight Freight to update
     * @throws SQLException If database operation fails
     */
    private void updateFreightOnDatabase(DatabaseConnection databaseConnection,
                                         Freight freight) throws SQLException {
        String sqlCommand =
                "UPDATE Freight SET startFacilityID = ?, endFacilityID = ?, \"date\" = ? WHERE freightID = ?";

        executeFreightStatementOnDatabase(databaseConnection, freight, sqlCommand, true);
    }

    /**
     * Executes the freight statement on the database.
     *
     * @param databaseConnection Database connection
     * @param freight Freight to save
     * @param sqlCommand SQL command to execute
     * @param isUpdate True if this is an update operation, False if insert
     * @throws SQLException If database operation fails
     */
    private void executeFreightStatementOnDatabase(
            DatabaseConnection databaseConnection,
            Freight freight, String sqlCommand, boolean isUpdate) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            if (isUpdate) {
                setEndPointParameter(pstmt, 1, freight.getStartEndPoint());
                setEndPointParameter(pstmt, 2, freight.getEndEndPoint());
                setDateParameter(pstmt, 3, freight.getDepartureDate());
                pstmt.setInt(4, freight.getFreightID());
            } else {
                pstmt.setInt(1, freight.getFreightID());
                setEndPointParameter(pstmt, 2, freight.getStartEndPoint());
                setEndPointParameter(pstmt, 3, freight.getEndEndPoint());
                setDateParameter(pstmt, 4, freight.getDepartureDate());
            }

            pstmt.executeUpdate();
        }
    }

    /**
     * Saves the wagons associated with a freight.
     * Enforces availability checks: one-freight-per-wagon and 30-minute scheduling buffer.
     * Note: Wagons are associated with trains through freights (Train -> Train_Freight -> Freight -> Freight_Wagon -> Wagon).
     *
     * @param databaseConnection Database connection
     * @param freight Freight with wagons to save
     * @throws SQLException If database operation fails or validation fails
     */
    private void saveFreightWagons(DatabaseConnection databaseConnection,
                                   Freight freight) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        // If freight has a train, validate train status is PLANNED
        if (freight.hasTrain()) {
            String checkTrainStatus = "SELECT status FROM Train WHERE trainID = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(checkTrainStatus)) {
                pstmt.setInt(1, freight.getTrain().getTrainID());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if (!"PLANNED".equals(status)) {
                            throw new SQLException("Wagon can only be associated with a freight that belongs to a PLANNED train.");
                        }
                    }
                }
            }
        }

        String deleteCommand = "DELETE FROM Freight_Wagon WHERE wagonID = ?";
        String insertCommand = "INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (?, ?)";
        List<Wagon> persistedWagons = new ArrayList<>();

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteCommand);
             PreparedStatement insertStmt = connection.prepareStatement(insertCommand)) {
            for (Wagon wagon : freight.getWagons()) {
                // Validate wagon availability and scheduling if freight has a train
                if (freight.hasTrain()) {
                    validateWagonAvailability(connection, freight, wagon.getWagonID());
                }
                
                // Remove any existing assignment for this wagon (enforce one-freight-per-wagon rule)
                deleteStmt.setInt(1, wagon.getWagonID());
                deleteStmt.executeUpdate();
                
                // Insert new assignment
                insertStmt.setInt(1, wagon.getWagonID());
                insertStmt.setInt(2, freight.getFreightID());
                try {
                    insertStmt.executeUpdate();
                    
                    // Update wagon status to ASSIGNED if freight has a train
                    if (freight.hasTrain()) {
                        String updateStatus = "UPDATE Wagon SET status = 'ASSIGNED' WHERE wagonID = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateStatus)) {
                            updateStmt.setInt(1, wagon.getWagonID());
                            updateStmt.executeUpdate();
                        }
                    }
                    
                    persistedWagons.add(wagon);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, 
                                String.format("Error assigning wagon %d to freight %d: %s", 
                                    wagon.getWagonID(), freight.getFreightID(), e.getMessage()));
                    throw e;
                }
            }
        }

        // Keep the freight wagons aligned with what was persisted
        List<Wagon> freightWagons = freight.getWagons();
        freightWagons.clear();
        freightWagons.addAll(persistedWagons);
    }

    /**
     * Validates if freights can be assigned to a train with the given start date.
     * Checks wagon availability and scheduling constraints before assignment.
     *
     * @param databaseConnection Database connection
     * @param freights List of freights to validate
     * @param trainID Train ID that will be assigned (or -1 for new train)
     * @param plannedDepartureTime Planned departure time for the train
     * @return List of validation error messages (empty if validation passes)
     */
    public List<String> validateFreightsForTrain(DatabaseConnection databaseConnection,
                                                  List<Freight> freights,
                                                  int trainID,
                                                  java.util.Date plannedDepartureTime) {
        List<String> errors = new ArrayList<>();
        
        if (freights == null || freights.isEmpty()) {
            return errors; // Empty list is valid (no freights to validate)
        }
        
        try {
            Connection connection = databaseConnection.getConnection();
            
            // Create a temporary freight with train for validation
            Train tempTrain = trainID > 0 ? new Train(trainID, null, plannedDepartureTime, null) : null;
            
            for (Freight freight : freights) {
                if (freight == null) {
                    continue;
                }
                
                // Set temporary train for validation
                if (tempTrain != null) {
                    freight.setTrain(tempTrain);
                }
                
                // Validate each wagon in the freight
                for (Wagon wagon : freight.getWagons()) {
                    try {
                        validateWagonAvailabilityForCreation(connection, freight, wagon.getWagonID(), plannedDepartureTime);
                    } catch (SQLException e) {
                        errors.add("Wagon " + wagon.getWagonID() + " in freight " + freight.getFreightID() + 
                                ": " + e.getMessage());
                    }
                }
                
                // Clear temporary train
                if (tempTrain != null) {
                    freight.setTrain(null);
                }
            }
        } catch (Exception e) {
            errors.add("Database error during freight validation: " + e.getMessage());
        }
        
        return errors;
    }

    /**
     * Validates wagon availability for assignment to a freight.
     * Checks if wagon exists, is not in transit (via another freight/train), and respects 30-minute scheduling buffer.
     *
     * @param connection Database connection
     * @param freight Freight to assign wagon to
     * @param wagonID Wagon ID to validate
     * @throws SQLException If validation fails
     */
    private void validateWagonAvailability(Connection connection, Freight freight, int wagonID) throws SQLException {
        // Use the train's planned departure time if available
        java.util.Date plannedDepartureTime = null;
        if (freight.hasTrain() && freight.getTrain().getStartDate() != null) {
            plannedDepartureTime = freight.getTrain().getStartDate();
        }
        validateWagonAvailabilityForCreation(connection, freight, wagonID, plannedDepartureTime);
    }

    /**
     * Validates wagon availability for assignment to a freight with explicit departure date.
     * Checks if wagon exists, is not in transit (via another freight/train), and respects 30-minute scheduling buffer.
     *
     * @param connection Database connection
     * @param freight Freight to assign wagon to
     * @param wagonID Wagon ID to validate
     * @param plannedDepartureTime Planned departure time for the train
     * @throws SQLException If validation fails
     */
    private void validateWagonAvailabilityForCreation(Connection connection, Freight freight, int wagonID, java.util.Date plannedDepartureTime) throws SQLException {
        // Check if wagon exists
        String checkWagon = "SELECT COUNT(*) as cnt FROM Wagon WHERE wagonID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkWagon)) {
            pstmt.setInt(1, wagonID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("cnt") == 0) {
                    throw new SQLException("Wagon does not exist.");
                }
            }
        }

        // Check if wagon is already assigned to another freight that belongs to a train
        // First check without time columns (in case they don't exist yet)
        String checkAssignment = "SELECT t.trainID " +
                                "FROM Freight_Wagon fw " +
                                "INNER JOIN Freight fr ON fw.freightID = fr.freightID " +
                                "INNER JOIN Train_Freight tf ON fr.freightID = tf.freightID " +
                                "INNER JOIN Train t ON tf.trainID = t.trainID " +
                                "WHERE fw.wagonID = ? AND t.status IN ('PLANNED', 'RUNNING')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(checkAssignment)) {
            pstmt.setInt(1, wagonID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int currentTrainID = rs.getInt("trainID");
                    
                    // If it's the same train (through a different freight), allow (idempotent operation)
                    if (freight.hasTrain() && currentTrainID == freight.getTrain().getTrainID()) {
                        return;
                    }
                    
                    // Wagon is assigned to another train - check scheduling constraint if time columns exist
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
                        } else if (freight.hasTrain()) {
                            // Try to get from database if train already exists
                            String getNewTrainTimes = "SELECT planned_departure_time FROM Train WHERE trainID = ?";
                            try (PreparedStatement timeStmt = connection.prepareStatement(getNewTrainTimes)) {
                                timeStmt.setInt(1, freight.getTrain().getTrainID());
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
                                    String.format("Wagon is in transit on train %d. New train departure (%s) must be at least 30 minutes after previous train arrival (%s). Minimum required departure: %s.",
                                        currentTrainID,
                                        newDeparture.toString(),
                                        currentArrival.toString(),
                                        new Timestamp(minRequiredTime).toString()));
                            }
                        } else {
                            // Time columns don't exist or are NULL - just block assignment to different train
                            throw new SQLException(
                                String.format("Wagon is already assigned to train %d. Cannot assign to another train without scheduling information.", 
                                    currentTrainID));
                        }
                    } catch (SQLException e) {
                        // If it's a column not found error, just block the assignment
                        if (e.getErrorCode() == 904 || e.getMessage().contains("invalid identifier")) {
                            throw new SQLException(
                                String.format("Wagon is already assigned to train %d. Cannot assign to another train.", 
                                    currentTrainID));
                        }
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * Deletes all wagons associated with a freight.
     *
     * @param databaseConnection Database connection
     * @param freight Freight whose wagons to delete
     * @throws SQLException If database operation fails
     */
    private void deleteFreightWagons(DatabaseConnection databaseConnection,
                                    Freight freight) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "DELETE FROM Freight_Wagon WHERE freightID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, freight.getFreightID());
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets the next available freight ID from the database.
     *
     * @param databaseConnection Database connection
     * @return Next available freight ID
     * @throws SQLException If database operation fails
     */
    public int getNextFreightID(DatabaseConnection databaseConnection) throws SQLException {
        Connection connection = databaseConnection.getConnection();
        String sqlCommand = "SELECT NVL(MAX(freightID), 0) + 1 AS nextID FROM Freight";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextID");
            }
        }
        return 1; // Default if query fails
    }

    private void setEndPointParameter(PreparedStatement pstmt, int parameterIndex, Facility endPoint) throws SQLException {
        if (endPoint == null) {
            pstmt.setNull(parameterIndex, Types.INTEGER);
        } else {
            pstmt.setInt(parameterIndex, endPoint.getFacilityID());
        }
    }

    private void setDateParameter(PreparedStatement pstmt, int parameterIndex, java.util.Date date) throws SQLException {
        // Use current date as default if date is null, since the database column is NOT NULL
        if (date == null) {
            pstmt.setTimestamp(parameterIndex, new Timestamp(System.currentTimeMillis()));
        } else {
            pstmt.setTimestamp(parameterIndex, new Timestamp(date.getTime()));
        }
    }

    /**
     * Saves the Train_Freight relationship for a freight.
     *
     * @param databaseConnection Database connection
     * @param freight Freight with train to save
     * @throws SQLException If database operation fails
     */
    private void saveTrainFreight(DatabaseConnection databaseConnection, Freight freight) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        // Remove any existing train association for this freight (enforce one-train-per-freight rule)
        String deleteCommand = "DELETE FROM Train_Freight WHERE freightID = ?";
        String insertCommand = "INSERT INTO Train_Freight (trainID, freightID) VALUES (?, ?)";
        
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteCommand);
             PreparedStatement insertStmt = connection.prepareStatement(insertCommand)) {
            
            // Remove any existing association
            deleteStmt.setInt(1, freight.getFreightID());
            deleteStmt.executeUpdate();
            
            // Insert new association
            insertStmt.setInt(1, freight.getTrain().getTrainID());
            insertStmt.setInt(2, freight.getFreightID());
            insertStmt.executeUpdate();
        }
    }

    /**
     * Deletes the Train_Freight relationship for a freight.
     *
     * @param databaseConnection Database connection
     * @param freight Freight whose train relationship to delete
     * @throws SQLException If database operation fails
     */
    private void deleteTrainFreight(DatabaseConnection databaseConnection, Freight freight) throws SQLException {
        Connection connection = databaseConnection.getConnection();

        String sqlCommand = "DELETE FROM Train_Freight WHERE freightID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCommand)) {
            pstmt.setInt(1, freight.getFreightID());
            pstmt.executeUpdate();
        }
    }

}

