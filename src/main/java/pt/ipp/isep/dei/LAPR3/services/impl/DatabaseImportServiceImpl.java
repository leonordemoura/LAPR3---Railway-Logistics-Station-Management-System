package pt.ipp.isep.dei.LAPR3.services.impl;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.LAPR3.domain.constants.FacilityType;
import pt.ipp.isep.dei.LAPR3.domain.constants.Status;
import pt.ipp.isep.dei.LAPR3.domain.constants.TrackType;
import pt.ipp.isep.dei.LAPR3.domain.constants.WagonType;
import pt.ipp.isep.dei.LAPR3.database.ConnectionFactory;
import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.services.DatabaseImporterService;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseImportServiceImpl implements DatabaseImporterService {
    private static final Logger LOGGER = Logger.getLogger("MainLog");

    private final RailwayRepository repository;
    private final ConnectionFactory connectionFactory;

    public DatabaseImportServiceImpl() throws IOException {
        this.repository = Repositories.getInstance().getRailwayRepository();
        this.connectionFactory = ConnectionFactory.getInstance();
    }

    @Override
    public void importAllFromDatabase() {
        DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();

        try {
            Connection conn = dbConnection.getConnection();

            importGauges(conn);
            importLineOwners(conn);
            importOperators(conn);
            importFacilities(conn);
            importLines(conn);
            importLineSegments(conn);
            importSimpleTrackSegments(conn);
            importSidings(conn);
            importLocomotiveMakes(conn);
            importWagonMakers(conn);
            importVehicles(conn);
            importLocomotiveModels(conn);
            importLocomotives(conn);
            importWagonTypes(conn);
            importWagonModels(conn);
            importWagons(conn);
            importRoutes(conn);
            importPaths(conn);
            importTrains(conn);
            importFreights(conn);
            importTrainFreights(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database import failed", e);
            System.err.println("ERROR: Database import failed - " + e.getMessage());
        }
    }

    private void importGauges(Connection conn) throws SQLException {
        String query = "SELECT dimension, name FROM Gauge ORDER BY dimension";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int dimension = rs.getInt("dimension");
                String name = rs.getString("name");
                Gauge gauge = new Gauge(dimension, name);
                repository.addGauge(gauge);
            }
        }
    }

    private void importLineOwners(Connection conn) throws SQLException {
        String query = "SELECT lineOwnerID, vatNumber, name FROM LineOwner ORDER BY lineOwnerID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int lineOwnerID = rs.getInt("lineOwnerID");
                String vatNumber = rs.getString("vatNumber");
                String name = rs.getString("name");

                LineOwner lineOwner = new LineOwner(lineOwnerID, vatNumber, name);
                repository.addLineOwner(lineOwner);
            }
        }
    }

    private void importOperators(Connection conn) throws SQLException {
        String query = "SELECT operatorID, vatNumber, name FROM Operator ORDER BY operatorID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int operatorID = rs.getInt("operatorID");
                String vatNumber = rs.getString("vatNumber");
                String name = rs.getString("name");

                Operator operator = new Operator(operatorID, vatNumber, name);
                repository.addOperator(operator);
            }
        }
    }

    private void importFacilities(Connection conn) throws SQLException {
        String query = "SELECT facilityID, name, facilityTypeID FROM Facility ORDER BY facilityID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int facilityID = rs.getInt("facilityID");
                String name = rs.getString("name");
                int facilityTypeID = rs.getInt("facilityTypeID");

                FacilityType facilityType = FacilityType.getFacilityTypeById(facilityTypeID);

                Facility facility = new Facility(facilityID, name, facilityType);
                repository.addFacility(facility);
            }
        }
    }

    private void importLines(Connection conn) throws SQLException {
        String query = "SELECT lineID, startFacilityID, endFacilityID, lineOwnerID, " +
                "gaugeID, isElectrified FROM Line ORDER BY lineID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int lineID = rs.getInt("lineID");
                int startFacilityID = rs.getInt("startFacilityID");
                int endFacilityID = rs.getInt("endFacilityID");
                int lineOwnerID = rs.getInt("lineOwnerID");
                int gaugeID = rs.getInt("gaugeID");
                boolean isElectrified = "Y".equalsIgnoreCase(rs.getString("isElectrified"));

                Facility startFacility = repository.getFacilityById(startFacilityID);
                Facility endFacility = repository.getFacilityById(endFacilityID);
                LineOwner lineOwner = repository.getAllLineOwners().stream()
                        .filter(lo -> lo.getLineOwnerID() == lineOwnerID)
                        .findFirst().orElse(null);
                Gauge gauge = repository.getAllGauges().stream()
                        .filter(g -> g.getDimension() == gaugeID)
                        .findFirst().orElse(null);

                if (startFacility != null && endFacility != null &&
                        lineOwner != null && gauge != null) {
                    Lines line = new Lines(lineID, startFacility, endFacility,
                            lineOwner, gauge, isElectrified);
                    repository.addLine(line);
                }
            }
        }
    }

    private void importLineSegments(Connection conn) throws SQLException {
        String query = "SELECT segmentID, lineID, orderLine, maxWeight, length, " +
                "maintenanceStatus, speedLimit " +
                "FROM LineSegment ORDER BY lineID, orderLine";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int segmentID = rs.getInt("segmentID");
                int lineID = rs.getInt("lineID");
                int orderLine = rs.getInt("orderLine");
                double maxWeight = rs.getDouble("maxWeight");
                double length = rs.getDouble("length");
                boolean maintenanceStatus = "Y".equalsIgnoreCase(rs.getString("maintenanceStatus"));
                int speedLimit = rs.getInt("speedLimit");

                Lines line = repository.getLineById(lineID);

                if (line != null) {
                    LineSegment segment = new LineSegment(segmentID, line, orderLine,
                            maxWeight, length, maintenanceStatus, speedLimit);

                    // Check if it's in DoubleTrack table
                    if (isDoubleTrack(conn, segmentID)) {
                        segment.setTrackType(TrackType.DOUBLE);
                    }

                    repository.addLineSegment(segment);
                }
            }
        }
    }

    private boolean isDoubleTrack(Connection conn, int segmentID) throws SQLException {
        String query = "SELECT 1 FROM DoubleTrack WHERE segmentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, segmentID);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void importSimpleTrackSegments(Connection conn) throws SQLException {
        String query = "SELECT segmentID FROM SimpleTrack ORDER BY segmentID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int segmentID = rs.getInt("segmentID");

                LineSegment segment = repository.getLineSegmentById(segmentID);

                if (segment != null) {
                    segment.setTrackType(TrackType.SINGLE);
                }
            }
        }
    }

    private void importSidings(Connection conn) throws SQLException {
        String query = "SELECT sidingID, segmentID, position, length FROM Siding ORDER BY sidingID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int sidingID = rs.getInt("sidingID");
                int segmentID = rs.getInt("segmentID");
                int position = rs.getInt("position");
                int length = rs.getInt("length");

                LineSegment segment = repository.getLineSegmentById(segmentID);

                if (segment != null) {
                    Siding siding = new Siding(sidingID, segment, position, length);
                    repository.addSiding(siding);
                }
            }
        }
    }

    private void importLocomotiveMakes(Connection conn) throws SQLException {
        String query = "SELECT makeID, name FROM LocomotiveMake ORDER BY makeID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int makeID = rs.getInt("makeID");
                String name = rs.getString("name");

                LocomotiveMake make = new LocomotiveMake(makeID, name);
                repository.addLocomotiveMake(make);
            }
        }
    }

    private void importWagonMakers(Connection conn) throws SQLException {
        String query = "SELECT wagonMakerID, name FROM WagonMaker ORDER BY wagonMakerID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int wagonMakerID = rs.getInt("wagonMakerID");
                String name = rs.getString("name");

                WagonMaker maker = new WagonMaker(wagonMakerID, name);
                repository.addWagonMaker(maker);
            }
        }
    }

    private void importVehicles(Connection conn) throws SQLException {
        String query = "SELECT vehicleID, name, weight, width, height, length, " +
                "numberOfWheels, maxSpeed FROM Vehicle ORDER BY vehicleID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int vehicleID = rs.getInt("vehicleID");
                String name = rs.getString("name");
                double weight = rs.getDouble("weight");
                double width = rs.getDouble("width");
                double height = rs.getDouble("height");
                double length = rs.getDouble("length");
                int numberOfWheels = rs.getInt("numberOfWheels");
                int maxSpeed = rs.getInt("maxSpeed");

                Vehicle vehicle = new Vehicle(vehicleID, name, weight, width, height,
                        length, numberOfWheels, maxSpeed);
                repository.addVehicle(vehicle);
            }
        }

        importGaugeVehicles(conn);
    }

    private void importGaugeVehicles(Connection conn) throws SQLException {
        String query = "SELECT dimension, vehicleID FROM Gauge_Vehicle";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int dimension = rs.getInt("dimension");
                int vehicleID = rs.getInt("vehicleID");

                Gauge gauge = repository.getAllGauges().stream()
                        .filter(g -> g.getDimension() == dimension)
                        .findFirst().orElse(null);
                Vehicle vehicle = repository.getVehicleById(vehicleID);

                if (gauge != null && vehicle != null) {
                    vehicle.addGauge(gauge);
                }
            }
        }
    }

    private void importLocomotiveModels(Connection conn) throws SQLException {
        String query = "SELECT lm.locomotiveModelID, lm.vehicleID, lm.power " +
                "FROM LocomotiveModel lm ORDER BY lm.locomotiveModelID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int locomotiveModelID = rs.getInt("locomotiveModelID");
                int vehicleID = rs.getInt("vehicleID");
                double power = rs.getDouble("power");

                Vehicle vehicle = repository.getVehicleById(vehicleID);

                if (vehicle != null) {
                    LocomotiveModel locModel = new LocomotiveModel(locomotiveModelID,
                            vehicle, power);
                    repository.addLocomotiveModel(locModel);

                    importLocomotiveModelElectric(conn, locomotiveModelID, locModel);
                    importLocomotiveModelDiesel(conn, locomotiveModelID, locModel);
                }
            }
        }
    }

    private void importLocomotiveModelElectric(Connection conn, int locomotiveModelID,
                                               LocomotiveModel locModel) throws SQLException {
        String query = "SELECT frequency, voltage FROM LocomotiveModel_Electric " +
                "WHERE locomotiveModelID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, locomotiveModelID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int frequency = rs.getInt("frequency");
                    int voltage = rs.getInt("voltage");
                    ElectricLocomotive electricLocomotive = new ElectricLocomotive(locModel, frequency, voltage);
                    locModel.setElectricLocomotive(electricLocomotive);
                }
            }
        }
    }

    private void importLocomotiveModelDiesel(Connection conn, int locomotiveModelID,
                                             LocomotiveModel locModel) throws SQLException {
        String query = "SELECT fuelCapacity FROM LocomotiveModel_Diesel " +
                "WHERE locomotiveModelID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, locomotiveModelID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int fuelCapacity = rs.getInt("fuelCapacity");
                    DieselLocomotive dieselLocomotive = new DieselLocomotive(locModel, fuelCapacity);
                    locModel.setDieselLocomotive(dieselLocomotive);
                }
            }
        }
    }

    private void importLocomotives(Connection conn) throws SQLException {
        String query = "SELECT l.locomotiveID, l.operatorID, l.locomotiveModelID, " +
                "l.makeId, l.startFacilityID, l.name, l.serviceYear, l.operationalSpeed, l.status " +
                "FROM Locomotive l ORDER BY l.locomotiveID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int locomotiveID = rs.getInt("locomotiveID");
                int operatorID = rs.getInt("operatorID");
                int locomotiveModelID = rs.getInt("locomotiveModelID");
                int makeID = rs.getInt("makeId");
                int startFacilityID = rs.getInt("startFacilityID");
                String name = rs.getString("name");
                int serviceYear = rs.getInt("serviceYear");
                double operationalSpeed = rs.getDouble("operationalSpeed");
                String statusStr = rs.getString("status");

                Operator operator = repository.getAllOperators().stream()
                        .filter(o -> o.getOperatorId() == operatorID)
                        .findFirst().orElse(null);
                LocomotiveModel locomotiveModel = repository.getLocomotiveModelById(locomotiveModelID);
                LocomotiveMake make = repository.getAllLocomotiveMakes().stream()
                        .filter(m -> m.getMakeID() == makeID)
                        .findFirst().orElse(null);
                Facility startFacility = repository.getFacilityById(startFacilityID);
                Status status = parseStatus(statusStr);

                if (operator != null && locomotiveModel != null && make != null && startFacility != null) {
                    Locomotive locomotive = new Locomotive(locomotiveID, operator,
                            locomotiveModel, make, name, serviceYear, operationalSpeed,
                            startFacility, status);
                    repository.addLocomotive(locomotive);
                }
            }
        }
    }

    private void importWagonTypes(Connection conn) throws SQLException {
        String query = "SELECT wagonTypeID, name FROM WagonType ORDER BY wagonTypeID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Just loading for reference, not storing separately
            }
        }
    }

    private void importWagonModels(Connection conn) throws SQLException {
        String query = "SELECT wm.wagonModelID, wm.vehicleID, wt.name as wagonType, " +
                "wm.wagonMakerID, wm.tareTons, wm.maxPayload, wm.volume " +
                "FROM WagonModel wm " +
                "JOIN WagonType wt ON wm.wagonTypeID = wt.wagonTypeID " +
                "ORDER BY wm.wagonModelID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int wagonModelID = rs.getInt("wagonModelID");
                int vehicleID = rs.getInt("vehicleID");
                String wagonTypeStr = rs.getString("wagonType");
                int wagonMakerID = rs.getInt("wagonMakerID");
                double tareTons = rs.getDouble("tareTons");
                int maxPayload = rs.getInt("maxPayload");
                double volume = rs.getDouble("volume");

                Vehicle vehicle = repository.getVehicleById(vehicleID);
                WagonType wagonType = parseWagonType(wagonTypeStr);
                WagonMaker wagonMaker = repository.getAllWagonMakers().stream()
                        .filter(m -> m.getWagonMakerID() == wagonMakerID)
                        .findFirst().orElse(null);

                if (vehicle != null && wagonMaker != null) {
                    WagonModel wagonModel = new WagonModel(wagonModelID, vehicle,
                            wagonType, wagonMaker, tareTons, maxPayload, volume);
                    repository.addWagonModel(wagonModel);
                }
            }
        }
    }

    private void importWagons(Connection conn) throws SQLException {
        String query = "SELECT w.wagonID, w.operatorID, w.wagonModelID, w.startFacility, " +
                "w.serviceYear, w.payloadTons, w.status " +
                "FROM Wagon w ORDER BY w.wagonID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int wagonID = rs.getInt("wagonID");
                int operatorID = rs.getInt("operatorID");
                int wagonModelID = rs.getInt("wagonModelID");
                int startFacilityID = rs.getInt("startFacility");
                int serviceYear = rs.getInt("serviceYear");
                double payloadTons = rs.getDouble("payloadTons");
                String statusStr = rs.getString("status");

                Operator operator = repository.getAllOperators().stream()
                        .filter(o -> o.getOperatorId() == operatorID)
                        .findFirst().orElse(null);
                WagonModel wagonModel = repository.getWagonModelById(wagonModelID);
                Facility startFacility = repository.getFacilityById(startFacilityID);
                Status status = parseStatus(statusStr);

                if (operator != null && wagonModel != null && startFacility != null) {
                    Wagon wagon = new Wagon(wagonID, operator, wagonModel,
                            serviceYear, payloadTons, startFacility, status);
                    repository.addWagon(wagon);
                }
            }
        }
    }

    private void importRoutes(Connection conn) throws SQLException {
        String query = "SELECT routeID, endPointAID, endPointBID FROM Route ORDER BY routeID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int routeID = rs.getInt("routeID");
                int endPointAID = rs.getInt("endPointAID");
                int endPointBID = rs.getInt("endPointBID");

                Facility endPointA = repository.getFacilityById(endPointAID);
                Facility endPointB = repository.getFacilityById(endPointBID);

                if (endPointA != null && endPointB != null) {
                    Route route = new Route(routeID, endPointA, endPointB);
                    repository.addRoute(route);
                }
            }
        }
    }

    private void importPaths(Connection conn) throws SQLException {
        String query = "SELECT pathID FROM Path ORDER BY pathID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int pathID = rs.getInt("pathID");
                Path path = new Path(pathID);
                repository.addPath(path);

                importPathFacilities(conn, pathID, path);
                importPathRoutes(conn, pathID, path);
            }
        }
    }

    private void importPathFacilities(Connection conn, int pathID, Path path)
            throws SQLException {
        String query = "SELECT facilityID, \"order\", arrivalTime, departureTime " +
                "FROM Path_Facility WHERE pathID = ? ORDER BY \"order\"";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pathID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int facilityID = rs.getInt("facilityID");
                    int order = rs.getInt("order");
                    // arrivalTime and departureTime are read but not used as Path class doesn't store them
                    rs.getTimestamp("arrivalTime");
                    rs.getTimestamp("departureTime");

                    Facility facility = repository.getFacilityById(facilityID);
                    if (facility != null) {
                        // For now, we'll consider all stops as scheduled stops
                        // You can add logic to determine this from the data if needed
                        path.addFacility(facility, order, true);
                    }
                }
            }
        }
    }

    private void importPathRoutes(Connection conn, int pathID, Path path)
            throws SQLException {
        String query = "SELECT routeID FROM Path_Route WHERE pathID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pathID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int routeID = rs.getInt("routeID");
                    Route route = repository.getRouteById(routeID);
                    if (route != null) {
                        path.setRoute(route);
                    }
                }
            }
        }
    }

    private void importTrains(Connection conn) throws SQLException {
        String query = "SELECT trainID, routeID, startDate, maxLenght, status FROM Train ORDER BY trainID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int trainID = rs.getInt("trainID");
                int routeID = rs.getInt("routeID");
                Timestamp startDate = rs.getTimestamp("startDate");
                // maxLenght is read but not used as Train class doesn't have this field
                rs.getInt("maxLenght");
                String statusStr = rs.getString("status");

                Route route = repository.getRouteById(routeID);
                Status status = parseStatus(statusStr);

                if (route != null) {
                    Train train = new Train(trainID, route, startDate, status);
                    repository.addTrain(train);

                    importTrainLocomotives(conn, trainID, train);
                }
            }
        }
    }

    private void importTrainLocomotives(Connection conn, int trainID, Train train)
            throws SQLException {
        String query = "SELECT locomotiveID FROM Train_Locomotive WHERE trainID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, trainID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int locomotiveID = rs.getInt("locomotiveID");
                    Locomotive locomotive = repository.getLocomotiveById(locomotiveID);
                    if (locomotive != null) {
                        train.addLocomotive(locomotive);
                    } else {
                        LOGGER.log(Level.WARNING, 
                            String.format("Locomotive %d not found in repository when importing train %d", 
                                locomotiveID, trainID));
                    }
                }
            }
        }
    }

    private void importFreights(Connection conn) throws SQLException {
        String query = "SELECT freightID, startFacilityID, endFacilityID, \"date\" " +
                "FROM Freight ORDER BY freightID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int freightID = rs.getInt("freightID");
                int startFacilityID = rs.getInt("startFacilityID");
                int endFacilityID = rs.getInt("endFacilityID");
                Timestamp date = rs.getTimestamp("date");

                Facility startFacility = repository.getFacilityById(startFacilityID);
                Facility endFacility = repository.getFacilityById(endFacilityID);

                if (startFacility != null && endFacility != null) {
                    Freight freight = new Freight(freightID, startFacility, endFacility);
                    if (date != null) {
                        freight.setDepartureDate(new Date(date.getTime()));
                    }
                    repository.addFreight(freight);

                    importFreightWagons(conn, freightID, freight);
                }
            }
        }
    }

    private void importFreightWagons(Connection conn, int freightID, Freight freight)
            throws SQLException {
        String query = "SELECT wagonID FROM Freight_Wagon WHERE freightID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, freightID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int wagonID = rs.getInt("wagonID");
                    Wagon wagon = repository.getWagonById(wagonID);
                    if (wagon != null) {
                        freight.addWagon(wagon);
                    }
                }
            }
        }
    }

    private void importTrainFreights(Connection conn) throws SQLException {
        String query = "SELECT trainID, freightID FROM Train_Freight";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int trainID = rs.getInt("trainID");
                int freightID = rs.getInt("freightID");

                Train train = repository.getTrainById(trainID);
                Freight freight = repository.getFreightById(freightID);

                if (train != null && freight != null) {
                    train.addFreight(freight);
                    freight.setTrain(train);
                }
            }
        }
    }

    private Status parseStatus(String statusStr) {
        if (statusStr == null) return Status.AVAILABLE;

        String normalized = statusStr.trim().toUpperCase();
        try {
            return Status.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Unknown status: " + statusStr + ", defaulting to AVAILABLE");
            return Status.AVAILABLE;
        }
    }

    private WagonType parseWagonType(String type) {
        if (type == null) return WagonType.BOXCAR;

        String normalized = type.trim().toUpperCase().replace(" ", "_");
        try {
            return WagonType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return WagonType.BOXCAR;
        }
    }
}