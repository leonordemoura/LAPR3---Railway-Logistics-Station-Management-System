package pt.ipp.isep.dei.LAPR3.database;

import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.constants.FacilityType;
import pt.ipp.isep.dei.LAPR3.services.dto.LocomotiveAvailabilityDTO;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Store class for train assembly operations.
 * Encapsulates database access for USLP09 requirements using direct SQL queries.
 */
public class TrainAssemblyStore {

    private static final Logger LOGGER = Logger.getLogger(TrainAssemblyStore.class.getName());

    /**
     * Gets locomotives already associated with a train (in transit).
     * Returns locomotives with their final destination from the route of the train they're assigned to.
     * Since each locomotive can only be in one train, no need for complex filtering.
     *
     * @param databaseConnection Database connection
     * @param routeStartFacilityID Starting facility ID of the route
     * @return List of locomotives associated with trains, with their final destination
     */
    public List<LocomotiveAvailabilityDTO> getLocomotivesInTransit(
            DatabaseConnection databaseConnection, int routeStartFacilityID) {
        List<LocomotiveAvailabilityDTO> locomotives = new ArrayList<>();

        try {
            Connection connection = databaseConnection.getConnection();
            String sql = "SELECT " +
                        "    l.locomotiveID, " +
                        "    l.name, " +
                        "    l.operationalSpeed, " +
                        "    l.status, " +
                        "    r.endPointBID AS finalDestinationID, " +
                        "    f_end.name AS finalDestinationName, " +
                        "    f_end.facilityTypeID AS facilityTypeID " +
                        "FROM Locomotive l " +
                        "INNER JOIN Train_Locomotive tl ON l.locomotiveID = tl.locomotiveID " +
                        "INNER JOIN Train t ON tl.trainID = t.trainID " +
                        "INNER JOIN Route r ON t.routeID = r.routeID " +
                        "INNER JOIN Facility f_end ON r.endPointBID = f_end.facilityID " +
                        "WHERE t.status IN ('PLANNED', 'RUNNING') " +
                        "ORDER BY l.locomotiveID";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int locomotiveID = rs.getInt("locomotiveID");
                    String name = rs.getString("name");
                    double operationalSpeed = rs.getDouble("operationalSpeed");
                    int finalDestinationID = rs.getInt("finalDestinationID");
                    String finalDestinationName = rs.getString("finalDestinationName");
                    int facilityTypeID = rs.getInt("facilityTypeID");

                    FacilityType facilityType = FacilityType.getFacilityTypeById(facilityTypeID);

                    Facility finalDestination = new Facility(finalDestinationID, finalDestinationName, facilityType);

                    LocomotiveAvailabilityDTO dto = new LocomotiveAvailabilityDTO(
                            locomotiveID, name, operationalSpeed);
                    dto.setInTransit(true);
                    dto.setFinalDestination(finalDestination);
                    locomotives.add(dto);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting locomotives in transit", e);
            databaseConnection.registerError(e);
        }

        return locomotives;
    }

    /**
     * Gets locomotives parked at stations (not associated with any train).
     * Returns locomotives ordered by distance from route start (descending).
     *
     * @param databaseConnection Database connection
     * @param routeStartFacilityID Starting facility ID of the route
     * @return List of parked locomotives ordered by distance (descending)
     */
    public List<LocomotiveAvailabilityDTO> getLocomotivesParked(
            DatabaseConnection databaseConnection, int routeStartFacilityID) {
        List<LocomotiveAvailabilityDTO> locomotives = new ArrayList<>();

        try {
            Connection connection = databaseConnection.getConnection();
            String sql = "SELECT " +
                        "    l.locomotiveID, " +
                        "    l.name, " +
                        "    l.operationalSpeed, " +
                        "    l.status, " +
                        "    l.startFacilityID AS currentStationID, " +
                        "    f.name AS currentStationName, " +
                        "    f.facilityTypeID , " +
                        "    CASE " +
                        "        WHEN l.startFacilityID = ? THEN 0 " +
                        "        ELSE NVL((" +
                        "            SELECT SUM(ls.length) " +
                        "            FROM LineSegment ls " +
                        "            INNER JOIN Line li ON ls.lineID = li.lineID " +
                        "            WHERE (li.startFacilityID = ? AND li.endFacilityID = l.startFacilityID) " +
                        "               OR (li.startFacilityID = l.startFacilityID AND li.endFacilityID = ?)" +
                        "        ), 999999) " +
                        "    END AS distanceFromStart " +
                        "FROM Locomotive l " +
                        "INNER JOIN Facility f ON l.startFacilityID = f.facilityID " +
                        "WHERE NOT EXISTS (" +
                        "    SELECT 1 FROM Train_Locomotive tl " +
                        "    INNER JOIN Train t ON tl.trainID = t.trainID " +
                        "    WHERE tl.locomotiveID = l.locomotiveID " +
                        "    AND t.status IN ('PLANNED', 'RUNNING')" +
                        ") " +
                        "ORDER BY distanceFromStart DESC, l.locomotiveID";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, routeStartFacilityID); // For CASE WHEN check
                stmt.setInt(2, routeStartFacilityID); // For first line condition
                stmt.setInt(3, routeStartFacilityID); // For second line condition
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int locomotiveID = rs.getInt("locomotiveID");
                        String name = rs.getString("name");
                        double operationalSpeed = rs.getDouble("operationalSpeed");
                        int currentStationID = rs.getInt("currentStationID");
                        String currentStationName = rs.getString("currentStationName");
                        double distanceFromStart = rs.getDouble("distanceFromStart");
                        int facilityTypeID = rs.getInt("facilityTypeID");

                        // If distance is 999999 (no direct line found), calculate shortest path
                        if (distanceFromStart >= 999999.0) {
                            distanceFromStart = calculateShortestDistance(connection, routeStartFacilityID, currentStationID);
                        }

                        FacilityType facilityType = FacilityType.getFacilityTypeById(facilityTypeID);

                        Facility currentStation = new Facility(currentStationID, currentStationName, facilityType);

                        LocomotiveAvailabilityDTO dto = new LocomotiveAvailabilityDTO(
                                locomotiveID, name, operationalSpeed);
                        dto.setInTransit(false);
                        dto.setCurrentStation(currentStation);
                        dto.setDistanceFromStart(distanceFromStart);
                        locomotives.add(dto);
                    }
                }
            }
            
            // Sort by distance descending (since some distances may have been recalculated)
            locomotives.sort((a, b) -> Double.compare(b.getDistanceFromStart(), a.getDistanceFromStart()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parked locomotives", e);
            databaseConnection.registerError(e);
        }

        return locomotives;
    }

    /**
     * Gets wagons already associated with a train (via freights, in transit).
     * Returns wagons with their final destination from the route of the train they're assigned to.
     * Since each wagon can only be in one freight, and a freight in one train, no need for complex filtering.
     *
     * @param databaseConnection Database connection
     * @param routeStartFacilityID Starting facility ID of the route
     * @return List of wagons associated with trains, with their final destination
     */
    public List<WagonAvailabilityDTO> getWagonsInTransit(
            DatabaseConnection databaseConnection, int routeStartFacilityID) {
        List<WagonAvailabilityDTO> wagons = new ArrayList<>();

        try {
            Connection connection = databaseConnection.getConnection();
            String sql = "SELECT " +
                        "    w.wagonID, " +
                        "    w.payloadTons, " +
                        "    w.status, " +
                        "    r.endPointBID AS finalDestinationID, " +
                        "    f_end.name AS finalDestinationName, " +
                        "    f_end.facilityTypeID " +
                        "FROM Wagon w " +
                        "INNER JOIN Freight_Wagon fw ON w.wagonID = fw.wagonID " +
                        "INNER JOIN Freight fr ON fw.freightID = fr.freightID " +
                        "INNER JOIN Train_Freight tf ON fr.freightID = tf.freightID " +
                        "INNER JOIN Train t ON tf.trainID = t.trainID " +
                        "INNER JOIN Route r ON t.routeID = r.routeID " +
                        "INNER JOIN Facility f_end ON r.endPointBID = f_end.facilityID " +
                        "WHERE t.status IN ('PLANNED', 'RUNNING') " +
                        "ORDER BY w.wagonID";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int wagonID = rs.getInt("wagonID");
                    double payloadTons = rs.getDouble("payloadTons");
                    int finalDestinationID = rs.getInt("finalDestinationID");
                    String finalDestinationName = rs.getString("finalDestinationName");
                    int facilityTypeID = rs.getInt("facilityTypeID");

                    FacilityType facilityType = FacilityType.getFacilityTypeById(facilityTypeID);

                    Facility finalDestination = new Facility(finalDestinationID, finalDestinationName, facilityType);

                    WagonAvailabilityDTO dto = new WagonAvailabilityDTO(wagonID, payloadTons);
                    dto.setInTransit(true);
                    dto.setFinalDestination(finalDestination);
                    wagons.add(dto);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting wagons in transit", e);
            databaseConnection.registerError(e);
        }

        return wagons;
    }

    /**
     * Gets wagons parked at stations (not associated with any freight/train).
     * Returns wagons ordered by distance from route start (descending).
     *
     * @param databaseConnection Database connection
     * @param routeStartFacilityID Starting facility ID of the route
     * @return List of parked wagons ordered by distance (descending)
     */
    public List<WagonAvailabilityDTO> getWagonsParked(
            DatabaseConnection databaseConnection, int routeStartFacilityID) {
        List<WagonAvailabilityDTO> wagons = new ArrayList<>();

        try {
            Connection connection = databaseConnection.getConnection();
            String sql = "SELECT " +
                        "    w.wagonID, " +
                        "    w.payloadTons, " +
                        "    w.status, " +
                        "    w.startFacility AS currentStationID, " +
                        "    f.name AS currentStationName, " +
                        "    f.facilityTypeID, " +
                        "    CASE " +
                        "        WHEN w.startFacility = ? THEN 0 " +
                        "        ELSE NVL((" +
                        "            SELECT SUM(ls.length) " +
                        "            FROM LineSegment ls " +
                        "            INNER JOIN Line li ON ls.lineID = li.lineID " +
                        "            WHERE (li.startFacilityID = ? AND li.endFacilityID = w.startFacility) " +
                        "               OR (li.startFacilityID = w.startFacility AND li.endFacilityID = ?)" +
                        "        ), 999999) " +
                        "    END AS distanceFromStart " +
                        "FROM Wagon w " +
                        "INNER JOIN Facility f ON w.startFacility = f.facilityID " +
                        "WHERE NOT EXISTS (" +
                        "    SELECT 1 FROM Freight_Wagon fw " +
                        "    INNER JOIN Freight fr ON fw.freightID = fr.freightID " +
                        "    INNER JOIN Train_Freight tf ON fr.freightID = tf.freightID " +
                        "    INNER JOIN Train t ON tf.trainID = t.trainID " +
                        "    WHERE fw.wagonID = w.wagonID " +
                        "    AND t.status IN ('PLANNED', 'RUNNING')" +
                        ") " +
                        "ORDER BY distanceFromStart DESC, w.wagonID";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, routeStartFacilityID); // For CASE WHEN check
                stmt.setInt(2, routeStartFacilityID); // For first line condition
                stmt.setInt(3, routeStartFacilityID); // For second line condition
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int wagonID = rs.getInt("wagonID");
                        double payloadTons = rs.getDouble("payloadTons");
                        int currentStationID = rs.getInt("currentStationID");
                        String currentStationName = rs.getString("currentStationName");
                        double distanceFromStart = rs.getDouble("distanceFromStart");
                        int facilityTypeID = rs.getInt("facilityTypeID");

                        // If distance is 999999 (no direct line found), calculate shortest path
                        if (distanceFromStart >= 999999.0) {
                            distanceFromStart = calculateShortestDistance(connection, routeStartFacilityID, currentStationID);
                        }

                        FacilityType facilityType = FacilityType.getFacilityTypeById(facilityTypeID);

                        Facility currentStation = new Facility(currentStationID, currentStationName, facilityType);

                        WagonAvailabilityDTO dto = new WagonAvailabilityDTO(wagonID, payloadTons);
                        dto.setInTransit(false);
                        dto.setCurrentStation(currentStation);
                        dto.setDistanceFromStart(distanceFromStart);
                        wagons.add(dto);
                    }
                }
            }
            
            // Sort by distance descending (since some distances may have been recalculated)
            wagons.sort((a, b) -> Double.compare(b.getDistanceFromStart(), a.getDistanceFromStart()));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting parked wagons", e);
            databaseConnection.registerError(e);
        }

        return wagons;
    }

    /**
     * Calculates the shortest distance between two facilities using Dijkstra's algorithm.
     * Builds a graph from lines and line segments, then finds the shortest path.
     *
     * @param connection Database connection
     * @param fromFacilityID Starting facility ID
     * @param toFacilityID Destination facility ID
     * @return Shortest distance in meters, or 999999 if no path exists
     */
    private double calculateShortestDistance(Connection connection, int fromFacilityID, int toFacilityID) {
        if (fromFacilityID == toFacilityID) {
            return 0.0;
        }

        try {
            // Build graph: Map<facilityID, Map<neighborFacilityID, distance>>
            Map<Integer, Map<Integer, Double>> graph = new HashMap<>();
            
            // Get all lines and their segments
            String lineQuery = "SELECT lineID, startFacilityID, endFacilityID FROM Line";
            try (PreparedStatement lineStmt = connection.prepareStatement(lineQuery);
                 ResultSet lineRs = lineStmt.executeQuery()) {
                
                while (lineRs.next()) {
                    int lineID = lineRs.getInt("lineID");
                    int startFacilityID = lineRs.getInt("startFacilityID");
                    int endFacilityID = lineRs.getInt("endFacilityID");
                    
                    // Calculate total length of line segments
                    String segmentQuery = "SELECT SUM(length) as totalLength FROM LineSegment WHERE lineID = ?";
                    try (PreparedStatement segStmt = connection.prepareStatement(segmentQuery)) {
                        segStmt.setInt(1, lineID);
                        try (ResultSet segRs = segStmt.executeQuery()) {
                            if (segRs.next()) {
                                double totalLength = segRs.getDouble("totalLength");
                                if (!segRs.wasNull() && totalLength > 0) {
                                    // Add bidirectional edges
                                    graph.computeIfAbsent(startFacilityID, k -> new HashMap<>())
                                         .put(endFacilityID, totalLength);
                                    graph.computeIfAbsent(endFacilityID, k -> new HashMap<>())
                                         .put(startFacilityID, totalLength);
                                }
                            }
                        }
                    }
                }
            }
            
            // Dijkstra's algorithm
            Map<Integer, Double> distances = new HashMap<>();
            PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(
                    Comparator.comparingDouble(Map.Entry::getValue));
            Set<Integer> visited = new HashSet<>();
            
            distances.put(fromFacilityID, 0.0);
            pq.offer(new AbstractMap.SimpleEntry<>(fromFacilityID, 0.0));
            
            while (!pq.isEmpty()) {
                Map.Entry<Integer, Double> current = pq.poll();
                int currentFacility = current.getKey();
                double currentDist = current.getValue();
                
                if (visited.contains(currentFacility)) {
                    continue;
                }
                visited.add(currentFacility);
                
                if (currentFacility == toFacilityID) {
                    return currentDist;
                }
                
                Map<Integer, Double> neighbors = graph.get(currentFacility);
                if (neighbors != null) {
                    for (Map.Entry<Integer, Double> neighbor : neighbors.entrySet()) {
                        int neighborFacility = neighbor.getKey();
                        double edgeWeight = neighbor.getValue();
                        double newDist = currentDist + edgeWeight;
                        
                        if (!distances.containsKey(neighborFacility) || 
                            newDist < distances.get(neighborFacility)) {
                            distances.put(neighborFacility, newDist);
                            pq.offer(new AbstractMap.SimpleEntry<>(neighborFacility, newDist));
                        }
                    }
                }
            }
            
            // No path found
            return 999999.0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error calculating shortest distance", e);
            return 999999.0;
        }
    }
}

