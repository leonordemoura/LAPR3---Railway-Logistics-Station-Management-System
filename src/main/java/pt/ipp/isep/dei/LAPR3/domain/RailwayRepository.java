package pt.ipp.isep.dei.LAPR3.domain;

import java.util.*;

/**
 * A repository for managing railway-related entities such as facilities, lines, locomotives, and more.
 */
public class RailwayRepository {
    private final Map<Integer, Facility> facilities;
    private final Map<Integer, Lines> lines;
    private final Map<Integer, LineOwner> lineOwners;
    private final Map<Integer, Gauge> gauges;
    private final Map<Integer, Operator> operators;
    private final Map<Integer, LineSegment> lineSegments;
    private final Map<Integer, Siding> sidings;
    private final Map<Integer, Vehicle> vehicles;
    private final Map<Integer, Locomotive> locomotives;
    private final Map<Integer, LocomotiveMake> locomotiveMakes;
    private final Map<Integer, LocomotiveModel> locomotiveModels;
    private final Map<Integer, Wagon> wagons;
    private final Map<Integer, WagonModel> wagonModels;
    private final Map<Integer, WagonMaker> wagonMakers;
    private final Map<Integer, Route> routes;
    private final Map<Integer, Path> paths;
    private final Map<Integer, Train> trains;
    private final Map<Integer, Freight> freights;

    /**
     * Constructs a new RailwayRepository with initialized empty maps for all entity types.
     */
    public RailwayRepository() {
        facilities = new HashMap<>();
        lines = new HashMap<>();
        lineOwners = new HashMap<>();
        gauges = new HashMap<>();
        operators = new HashMap<>();
        lineSegments = new HashMap<>();
        sidings = new HashMap<>();
        vehicles = new HashMap<>();
        locomotives = new HashMap<>();
        locomotiveMakes = new HashMap<>();
        locomotiveModels = new HashMap<>();
        wagons = new HashMap<>();
        wagonModels = new HashMap<>();
        wagonMakers = new HashMap<>();
        routes = new HashMap<>();
        paths = new HashMap<>();
        trains = new HashMap<>();
        freights = new HashMap<>();
    }

    // Facility methods
    /**
     * Adds a facility to the repository.
     *
     * @param facility the facility to add
     */
    public void addFacility(Facility facility) {
        facilities.put(facility.getFacilityID(), facility);
    }

    /**
     * Retrieves a facility by its ID.
     *
     * @param id the ID of the facility
     * @return the facility with the specified ID, or null if not found
     */
    public Facility getFacilityById(int id) {
        return facilities.get(id);
    }

    /**
     * Retrieves all facilities in the repository.
     *
     * @return a list of all facilities
     */
    public List<Facility> getAllFacilities() {
        return new ArrayList<>(facilities.values());
    }

    // Line methods
    /**
     * Adds a railway line to the repository.
     *
     * @param line the line to add
     */
    public void addLine(Lines line) {
        lines.put(line.getLineID(), line);
    }

    /**
     * Retrieves a railway line by its ID.
     *
     * @param id the ID of the line
     * @return the line with the specified ID, or null if not found
     */
    public Lines getLineById(int id) {
        return lines.get(id);
    }

    /**
     * Retrieves all railway lines in the repository.
     *
     * @return a list of all lines
     */
    public List<Lines> getAllLines() {
        return new ArrayList<>(lines.values());
    }

    // LineOwner methods
    /**
     * Adds a line owner to the repository.
     *
     * @param lineOwner the line owner to add
     */
    public void addLineOwner(LineOwner lineOwner) {
        lineOwners.put(lineOwner.getLineOwnerID(), lineOwner);
    }

    /**
     * Retrieves a line owner by their VAT number.
     *
     * @param vatNumber the VAT number of the line owner
     * @return the line owner with the specified VAT number, or null if not found
     */
    public LineOwner getLineOwnerByVAT(String vatNumber) {
        return lineOwners.values().stream()
                .filter(lo -> lo.getVatNumber().equals(vatNumber))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all line owners in the repository.
     *
     * @return a list of all line owners
     */
    public List<LineOwner> getAllLineOwners() {
        return new ArrayList<>(lineOwners.values());
    }

    // Gauge methods
    /**
     * Adds a gauge to the repository.
     *
     * @param gauge the gauge to add
     */
    public void addGauge(Gauge gauge) {
        gauges.put(gauge.getDimension(), gauge);
    }

    /**
     * Retrieves a gauge by its dimension.
     *
     * @param dimension the dimension of the gauge
     * @return the gauge with the specified dimension, or null if not found
     */
    public Gauge getGaugeByDimension(int dimension) {
        return gauges.get(dimension);
    }

    /**
     * Retrieves all gauges in the repository.
     *
     * @return a list of all gauges
     */
    public List<Gauge> getAllGauges() {
        return new ArrayList<>(gauges.values());
    }

    // Operator methods
    /**
     * Adds an operator to the repository.
     *
     * @param operator the operator to add
     */
    public void addOperator(Operator operator) {
        operators.put(operator.getOperatorId(), operator);
    }

    /**
     * Retrieves an operator by their name.
     *
     * @param name the name of the operator (case-insensitive)
     * @return the operator with the specified name, or null if not found
     */
    public Operator getOperatorByName(String name) {
        return operators.values().stream()
                .filter(o -> o.getOperatorName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all operators in the repository.
     *
     * @return a list of all operators
     */
    public List<Operator> getAllOperators() {
        return new ArrayList<>(operators.values());
    }

    // LineSegment methods
    /**
     * Adds a line segment to the repository.
     *
     * @param segment the line segment to add
     */
    public void addLineSegment(LineSegment segment) {
        lineSegments.put(segment.getSegmentID(), segment);
    }

    /**
     * Retrieves a line segment by its ID.
     *
     * @param segmentID the ID of the segment
     * @return the line segment with the specified ID, or null if not found
     */
    public LineSegment getLineSegmentById(int segmentID) {
        return lineSegments.get(segmentID);
    }

    /**
     * Retrieves all line segments for a given line ID, sorted by order.
     *
     * @param lineId the ID of the line
     * @return a sorted list of line segments associated with the line ID
     */
    public List<LineSegment> getSegmentsByLineId(int lineId) {
        List<LineSegment> result = new ArrayList<>();
        for (LineSegment segment : lineSegments.values()) {
            if (segment.getLineID() == lineId) {
                result.add(segment);
            }
        }
        result.sort(Comparator.comparingInt(LineSegment::getOrderLine));
        return result;
    }

    /**
     * Retrieves all line segments in the repository.
     *
     * @return a list of all line segments
     */
    public List<LineSegment> getAllLineSegments() {
        return new ArrayList<>(lineSegments.values());
    }

    // Siding methods
    /**
     * Adds a siding to the repository.
     *
     * @param siding the siding to add
     */
    public void addSiding(Siding siding) {
        sidings.put(siding.getSidingID(), siding);
    }

    /**
     * Retrieves a siding by its ID.
     *
     * @param id the ID of the siding
     * @return the siding with the specified ID, or null if not found
     */
    public Siding getSidingById(int id) {
        return sidings.get(id);
    }

    /**
     * Retrieves all sidings for a given line segment.
     *
     * @param segmentID the ID of the line segment
     * @return a list of sidings associated with the segment ID
     */
    public List<Siding> getSidingsBySegmentId(int segmentID) {
        List<Siding> result = new ArrayList<>();
        for (Siding siding : sidings.values()) {
            if (siding.getLineSegment().getSegmentID() == segmentID) {
                result.add(siding);
            }
        }
        return result;
    }

    /**
     * Retrieves all sidings in the repository.
     *
     * @return a list of all sidings
     */
    public List<Siding> getAllSidings() {
        return new ArrayList<>(sidings.values());
    }

    // Vehicle methods
    /**
     * Adds a vehicle to the repository.
     *
     * @param vehicle the vehicle to add
     */
    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getVehicleID(), vehicle);
    }

    /**
     * Retrieves a vehicle by its ID.
     *
     * @param id the ID of the vehicle
     * @return the vehicle with the specified ID, or null if not found
     */
    public Vehicle getVehicleById(int id) {
        return vehicles.get(id);
    }

    /**
     * Retrieves a vehicle by its name.
     *
     * @param name the name of the vehicle (case-insensitive)
     * @return the vehicle with the specified name, or null if not found
     */
    public Vehicle getVehicleByName(String name) {
        return vehicles.values().stream()
                .filter(v -> v.getVehicleName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all vehicles in the repository.
     *
     * @return a list of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    // Locomotive methods
    /**
     * Adds a locomotive to the repository.
     *
     * @param locomotive the locomotive to add
     */
    public void addLocomotive(Locomotive locomotive) {
        locomotives.put(locomotive.getLocomotiveId(), locomotive);
    }

    /**
     * Retrieves a locomotive by its ID.
     *
     * @param id the ID of the locomotive
     * @return the locomotive with the specified ID, or null if not found
     */
    public Locomotive getLocomotiveById(int id) {
        return locomotives.get(id);
    }

    /**
     * Retrieves all locomotives in the repository.
     *
     * @return a list of all locomotives
     */
    public List<Locomotive> getAllLocomotives() {
        return new ArrayList<>(locomotives.values());
    }

    public List<Locomotive> getAvailableLocomotives() {
        return new ArrayList<>(locomotives.values());
    }

    // LocomotiveMake methods
    /**
     * Retrieves all locomotives makes in the repository.
     *
     * @return a list of all locomotives makes
     */
    public List<LocomotiveMake> getAllLocomotiveMakes() {
        return new ArrayList<>(locomotiveMakes.values());
    }

    /**
     * Adds a locomotive make to the repository.
     *
     * @param make the locomotive make to add
     */
    public void addLocomotiveMake(LocomotiveMake make) {
        locomotiveMakes.put(make.getMakeID(), make);
    }

    /**
     * Retrieves a locomotive make by its name.
     *
     * @param name the name of the locomotive make (case-insensitive)
     * @return the locomotive make with the specified name, or null if not found
     */
    public LocomotiveMake getLocomotiveMakeByName(String name) {
        return locomotiveMakes.values().stream()
                .filter(m -> m.getMakeName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a locomotive make by its id.
     *
     * @param id the id of the locomotive make
     * @return the locomotive make with the specified id
     */
    public LocomotiveMake getLocomotiveMakeById(int id) {
        return locomotiveMakes.get(id);
    }

    // LocomotiveModel methods
    /**
     * Adds a locomotive model to the repository.
     *
     * @param model the locomotive model to add
     */
    public void addLocomotiveModel(LocomotiveModel model) {
        locomotiveModels.put(model.getLocomotiveModelId(), model);
    }

    /**
     * Retrieves a locomotive model by its ID.
     *
     * @param id the ID of the locomotive model
     * @return the locomotive model with the specified ID, or null if not found
     */
    public LocomotiveModel getLocomotiveModelById(int id) {
        return locomotiveModels.get(id);
    }

    /**
     * Retrieves a locomotive model by its vehicle name.
     *
     * @param vehicleName the name of the vehicle (case-insensitive)
     * @return the locomotive model with the specified vehicle name, or null if not found
     */
    public LocomotiveModel getLocomotiveModelByVehicleName(String vehicleName) {
        return locomotiveModels.values().stream()
                .filter(m -> m.getVehicle().getVehicleName().equalsIgnoreCase(vehicleName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all locomotive models in the repository.
     *
     * @return a list of all locomotive models
     */
    public List<LocomotiveModel> getAllLocomotiveModels() {
        return new ArrayList<>(locomotiveModels.values());
    }

    // Wagon methods
    /**
     * Adds a wagon to the repository.
     *
     * @param wagon the wagon to add
     */
    public void addWagon(Wagon wagon) {
        wagons.put(wagon.getWagonID(), wagon);
    }

    /**
     * Retrieves a wagon by its ID.
     *
     * @param id the ID of the wagon
     * @return the wagon with the specified ID, or null if not found
     */
    public Wagon getWagonById(int id) {
        return wagons.get(id);
    }

    /**
     * Retrieves all wagons in the repository.
     *
     * @return a list of all wagons
     */
    public List<Wagon> getAllWagons() {
        return new ArrayList<>(wagons.values());
    }

    /**
     * Retrieves wagons that can be scheduled for new freights.
     * <p>
     * Previously this method filtered out wagons that were already referenced by a freight.
     * That prevented staging multiple future freights for the same physical consist, which is
     * required for advanced planning (e.g., scheduling the same wagons on consecutive routes).
     * To support that workflow we now expose every wagon, leaving the scheduling logic to
     * higher-level services that decide when a wagon can actually depart.
     *
     * @return a list of wagons that can be selected when creating freights
     */
    public List<Wagon> getAvailableWagons() {
        return new ArrayList<>(wagons.values());
    }

    // WagonModel methods
    /**
     * Adds a wagon model to the repository.
     *
     * @param model the wagon model to add
     */
    public void addWagonModel(WagonModel model) {
        wagonModels.put(model.getWagonModelID(), model);
    }

    /**
     * Retrieves a wagon model by its ID.
     *
     * @param id the ID of the wagon model
     * @return the wagon model with the specified ID, or null if not found
     */
    public WagonModel getWagonModelById(int id) {
        return wagonModels.get(id);
    }

    /**
     * Retrieves a wagon model by its vehicle name.
     *
     * @param vehicleName the name of the vehicle (case-insensitive)
     * @return the wagon model with the specified vehicle name, or null if not found
     */
    public WagonModel getWagonModelByVehicleName(String vehicleName) {
        return wagonModels.values().stream()
                .filter(wm -> wm.getVehicle().getVehicleName().equalsIgnoreCase(vehicleName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all wagon models in the repository.
     *
     * @return a list of all wagon models
     */
    public List<WagonModel> getAllWagonModels() {
        return new ArrayList<>(wagonModels.values());
    }

    // WagonMaker methods
    /**
     * Retrieves all wagon makers in the repository.
     *
     * @return a list of all wagon makers
     */
    public List<WagonMaker> getAllWagonMakers() {
        return new ArrayList<>(wagonMakers.values());
    }

    /**
     * Adds a wagon maker to the repository.
     *
     * @param maker the wagon maker to add
     */
    public void addWagonMaker(WagonMaker maker) {
        wagonMakers.put(maker.getWagonMakerID(), maker);
    }

    /**
     * Retrieves a wagon maker by its name.
     *
     * @param name the name of the wagon maker (case-insensitive)
     * @return the wagon maker with the specified name, or null if not found
     */
    public WagonMaker getWagonMakerByName(String name) {
        return wagonMakers.values().stream()
                .filter(m -> m.getWagonMakerName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a wagon maker by its id.
     *
     * @param id the id of the wagon maker
     * @return the wagon maker with the specified id
     */
    public WagonMaker getWagonMakerById(int id) {
        return wagonMakers.get(id);
    }

    // Route methods
    /**
     * Adds a route to the repository.
     *
     * @param route the route to add
     */
    public void addRoute(Route route) {
        routes.put(route.getRouteId(), route);
    }

    /**
     * Retrieves a route by its ID.
     *
     * @param id the ID of the route
     * @return the route with the specified ID, or null if not found
     */
    public Route getRouteById(int id) {
        return routes.get(id);
    }

    /**
     * Retrieves all routes in the repository.
     *
     * @return a list of all routes
     */
    public List<Route> getAllRoutes() {
        return new ArrayList<>(routes.values());
    }

    // Path methods
    /**
     * Adds a path to the repository.
     *
     * @param path the path to add
     */
    public void addPath(Path path) {
        paths.put(path.getPathID(), path);
    }

    /**
     * Retrieves a path by its ID.
     *
     * @param id the ID of the path
     * @return the path with the specified ID, or null if not found
     */
    public Path getPathById(int id) {
        return paths.get(id);
    }

    /**
     * Retrieves all paths in the repository.
     *
     * @return a list of all paths
     */
    public List<Path> getAllPaths() {
        return new ArrayList<>(paths.values());
    }

    /**
     * Retrieves all paths associated with a specific route.
     *
     * @param routeId the ID of the route
     * @return list of paths for the route
     */
    public List<Path> getPathsByRouteId(int routeId) {
        List<Path> result = new ArrayList<>();
        for (Path path : paths.values()) {
            if (path.getRoute() != null && path.getRoute().getRouteId() == routeId) {
                result.add(path);
            }
        }
        return result;
    }

    // Train methods
    /**
     * Adds a train to the repository.
     *
     * @param train the train to add
     */
    public void addTrain(Train train) {
        trains.put(train.getTrainID(), train);
    }

    /**
     * Retrieves a train by its ID.
     *
     * @param id the ID of the train
     * @return the train with the specified ID, or null if not found
     */
    public Train getTrainById(int id) {
        return trains.get(id);
    }

    /**
     * Retrieves all trains in the repository.
     *
     * @return a list of all trains
     */
    public List<Train> getAllTrains() {
        return new ArrayList<>(trains.values());
    }

    /**
     * Removes a train from the repository.
     *
     * @param trainID the ID of the train to remove
     * @return true if the train was removed, false if it was not found
     */
    public boolean removeTrain(int trainID) {
        return trains.remove(trainID) != null;
    }

    // Freight methods
    /**
     * Adds a freight to the repository.
     *
     * @param freight the freight to add
     */
    public void addFreight(Freight freight) {
        freights.put(freight.getFreightID(), freight);
        if (freight.getTrain() != null) {
            Train train = trains.get(freight.getTrain().getTrainID());
            if (train != null && !train.getFreights().contains(freight)) {
                train.addFreight(freight);
            }
        }
    }

    /**
     * Retrieves a freight by its ID.
     *
     * @param id the ID of the freight
     * @return the freight with the specified ID, or null if not found
     */
    public Freight getFreightById(int id) {
        return freights.get(id);
    }

    /**
     * Retrieves all freights for a given train ID.
     *
     * @param trainId the ID of the train
     * @return a list of freights associated with the train ID
     */
    public List<Freight> getFreightsByTrainId(int trainId) {
        List<Freight> result = new ArrayList<>();
        for (Freight freight : freights.values()) {
            if (freight.getTrain() != null && freight.getTrain().getTrainID() == trainId) {
                result.add(freight);
            }
        }
        return result;
    }

    /**
     * Retrieves all freights in the repository.
     *
     * @return a list of all freights
     */
    public List<Freight> getAllFreights() {
        return new ArrayList<>(freights.values());
    }

    public List<Freight> getUnassignedFreights() {
        List<Freight> result = new ArrayList<>();
        for (Freight freight : freights.values()) {
            if (freight.getTrain() == null) {
                result.add(freight);
            }
        }
        return result;
    }

    /**
     * Clears all data from the repository.
     */
    public void clear() {
        facilities.clear();
        lines.clear();
        lineOwners.clear();
        gauges.clear();
        operators.clear();
        lineSegments.clear();
        sidings.clear();
        vehicles.clear();
        locomotives.clear();
        locomotiveMakes.clear();
        locomotiveModels.clear();
        wagons.clear();
        wagonModels.clear();
        wagonMakers.clear();
        routes.clear();
        paths.clear();
        trains.clear();
        freights.clear();
    }
}