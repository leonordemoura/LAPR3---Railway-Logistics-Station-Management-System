package pt.ipp.isep.dei.LAPR3.services.impl;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.domain.*;
import pt.ipp.isep.dei.LAPR3.services.DispatchSchedulerService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class DispatchSchedulerServiceImpl implements DispatchSchedulerService {

    private static final double MIN_SPEED_KMH = 20.0;
    private static final double MAX_SPEED_KMH = 160.0;
    private static final double DEFAULT_SPEED_KMH = 40.0;
    private static final double STOP_DWELL_MINUTES = 10.0;

    private final RailwayRepository repository;
    private final SpeedCalculationServiceImpl speedCalculator = new SpeedCalculationServiceImpl();
    private final CrossingDetectionServiceImpl crossingDetector;

    public DispatchSchedulerServiceImpl() {
        this.repository = Repositories.getInstance().getRailwayRepository();
        this.crossingDetector = new CrossingDetectionServiceImpl();
    }

    @Override
    public DispatchSchedule scheduleTrains(List<TrainDispatchPlan> plans) {
        if (plans == null || plans.isEmpty()) {
            throw new IllegalArgumentException("At least one train must be selected for dispatch.");
        }

        // Build initial schedules for all trains
        List<TrainSchedule> schedules = new ArrayList<>();
        for (TrainDispatchPlan plan : plans) {
            schedules.add(buildSchedule(plan));
        }

        // Detect and resolve crossings using enhanced service
        List<CrossingOperation> crossings = crossingDetector.detectAndResolveCrossings(schedules);

        return new DispatchSchedule(schedules, crossings);
    }

    private TrainSchedule buildSchedule(TrainDispatchPlan plan) {
        Train train = plan.getTrain();
        if (train.getLocomotives().isEmpty()) {
            throw new IllegalArgumentException("Train " + train.getTrainID() + " has no locomotives assigned.");
        }

        Path path = plan.getPath();
        if (path.getFacilities().size() < 2) {
            throw new IllegalArgumentException("Path " + path.getPathID() + " must include at least two facilities.");
        }

        TrainPerformanceMetrics metrics = computeMetrics(train);
        TrainSchedule schedule = new TrainSchedule(train, path);

        Route route = train.getRoute();
        Facility expectedStart = route != null ? route.getStartFacility() : null;
        Facility expectedEnd = route != null ? route.getEndFacility() : null;

        LocalDateTime currentTime = plan.getDepartureTime();
        Path.PathFacilityStep firstStep = path.getFacilities().get(0);
        Facility currentFacility = resolveFacility(firstStep.getFacility());

        validateEndPoints(train, currentFacility, expectedStart, "start");

        schedule.addPoint(new TrainSchedulePoint(currentFacility, currentTime, currentTime, true));

        for (int i = 1; i < path.getFacilities().size(); i++) {
            Path.PathFacilityStep nextStep = path.getFacilities().get(i);
            Facility nextFacility = resolveFacility(nextStep.getFacility());

            validateEndPoints(train, nextFacility, expectedEnd, i == path.getFacilities().size() - 1 ? "end" : null);

            Lines line = findLineBetween(currentFacility, nextFacility);
            if (line == null) {
                throw new IllegalArgumentException(String.format("No direct line found between %s and %s for train %d.",
                        currentFacility.getName(), nextFacility.getName(), train.getTrainID()));
            }

            List<LineSegment> segments = repository.getSegmentsByLineId(line.getLineID());
            if (segments.isEmpty()) {
                throw new IllegalArgumentException("Line " + line.getLineID() + " has no segments.");
            }

            for (LineSegment segment : segments) {
                validateSegmentWeight(segment, metrics.totalWeightTons, train);

                double effectiveSpeed = speedCalculator.calculateEffectiveSpeed(train, segment);
                schedule.setEffectiveSpeedKmH(effectiveSpeed);
                double minutes = computeTravelMinutes(segment, effectiveSpeed);

                LocalDateTime entryTime = currentTime;
                currentTime = addMinutes(currentTime, minutes);

                schedule.addSegmentOccupancy(
                        new TrainSchedule.SegmentOccupancy(segment, entryTime, currentTime, currentFacility, nextFacility));
                schedule.addDistance(segment.getLength() / 1000.0);
                schedule.addDuration(minutes);
            }

            LocalDateTime arrivalTime = currentTime;
            LocalDateTime departureTime = arrivalTime;
            boolean isLastFacility = (i == path.getFacilities().size() - 1);

            if (!isLastFacility && nextStep.isScheduledStop()) {
                departureTime = addMinutes(arrivalTime, STOP_DWELL_MINUTES);
                currentTime = departureTime;
                schedule.addDuration(STOP_DWELL_MINUTES);
            }

            schedule.addPoint(new TrainSchedulePoint(nextFacility, arrivalTime, departureTime, nextStep.isScheduledStop()));
            currentFacility = nextFacility;
        }

        return schedule;
    }

    private Facility resolveFacility(Facility facility) {
        if (facility == null) {
            return null;
        }
        Facility repoFacility = repository.getFacilityById(facility.getFacilityID());
        return repoFacility != null ? repoFacility : facility;
    }

    private void validateEndPoints(Train train, Facility facility, Facility expected, String label) {
        if (expected == null || facility == null || label == null) {
            return;
        }
        if (facility.getFacilityID() != expected.getFacilityID()) {
            throw new IllegalArgumentException(String.format(
                    "Train %d path %s facility %s does not match route %s facility %s.",
                    train.getTrainID(),
                    label,
                    facility.getName(),
                    label,
                    expected.getName()));
        }
    }

    private TrainPerformanceMetrics computeMetrics(Train train) {
        double totalPower = 0;
        double totalWeight = 0;
        double operationalLimit = Double.MAX_VALUE;

        for (Locomotive locomotive : train.getLocomotives()) {
            totalPower += Optional.ofNullable(locomotive.getLocomotiveModelID())
                    .map(LocomotiveModel::getPower)
                    .orElse(0.0);

            double locoWeight = Optional.ofNullable(locomotive.getLocomotiveModelID())
                    .map(LocomotiveModel::getVehicle)
                    .map(Vehicle::getWeight)
                    .orElse(0.0);
            totalWeight += locoWeight;
            operationalLimit = Math.min(operationalLimit, locomotive.getOperationalSpeed());
        }

        List<Freight> freights = new ArrayList<>();
        Set<Integer> processedFreights = new HashSet<>();
        for (Freight freight : train.getFreights()) {
            if (processedFreights.add(freight.getFreightID())) {
                freights.add(freight);
            }
        }
        for (Freight freight : repository.getFreightsByTrainId(train.getTrainID())) {
            if (processedFreights.add(freight.getFreightID())) {
                freights.add(freight);
            }
        }

        for (Freight freight : freights) {
            for (Wagon wagon : freight.getWagons()) {
                double tare = Optional.ofNullable(wagon.wagonModel())
                        .map(WagonModel::getTareTons)
                        .orElse(0.0);
                totalWeight += tare;
                totalWeight += Math.max(0, wagon.getPayloadTons());
            }
        }

        TrainPerformanceMetrics metrics = new TrainPerformanceMetrics();
        metrics.totalPowerKw = totalPower;
        metrics.totalWeightTons = totalWeight > 0 ? totalWeight : 1;
        metrics.operationalSpeedLimit = operationalLimit == Double.MAX_VALUE ? MAX_SPEED_KMH : operationalLimit;
        return metrics;
    }

    private double computeTravelMinutes(LineSegment segment, double speedKmh) {
        double distanceKm = segment.getLength() / 1000.0;
        return (distanceKm / speedKmh) * 60.0;
    }

    private LocalDateTime addMinutes(LocalDateTime time, double minutes) {
        long seconds = Math.max(1, Math.round(minutes * 60));
        return time.plusSeconds(seconds);
    }

    private Lines findLineBetween(Facility start, Facility end) {
        for (Lines line : repository.getAllLines()) {
            if ((line.getStartFacilityID() == start.getFacilityID() && line.getEndFacilityID() == end.getFacilityID()) ||
                    (line.getStartFacilityID() == end.getFacilityID() && line.getEndFacilityID() == start.getFacilityID())) {
                return line;
            }
        }
        return null;
    }

    private void validateSegmentWeight(LineSegment segment, double totalWeight, Train train) {
        if (segment.getWeight() > 0 && totalWeight > segment.getWeight()) {
            throw new IllegalArgumentException(String.format(
                    "Train %d exceeds weight limit for segment %d (limit %.2f, train %.2f).",
                    train.getTrainID(), segment.getSegmentID(), segment.getWeight(), totalWeight));
        }
    }

    private static class TrainPerformanceMetrics {
        double totalPowerKw;
        double totalWeightTons;
        double operationalSpeedLimit;
    }

    public LocalDateTime getDefaultDeparture(Train train) {
        Date date = train.getStartDate();
        if (date == null) {
            return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public Path calculateAutomaticPath(Train train) {
        Facility start = train.getRoute().getStartFacility();
        Facility end = train.getRoute().getEndFacility();

        List<Facility> facilityPath = findPath(start, end);

        int pathID = generatePathID();
        Path path = new Path(pathID);
        path.setRoute(train.getRoute());

        for (int i = 0; i < facilityPath.size(); i++) {
            boolean isStop = (i == 0 || i == facilityPath.size() - 1);
            path.addFacility(facilityPath.get(i), isStop);
        }

        return path;
    }

    private int generatePathID() {
        int maxID = 0;
        List<Path> allPaths = repository.getAllPaths(); // pega todos os paths do repositório
        for (Path path : allPaths) {
            if (path.getPathID() > maxID) {
                maxID = path.getPathID();
            }
        }
        return maxID + 1;
    }

    private List<Facility> findPath(Facility start, Facility end) {

        if (start.getFacilityID() == end.getFacilityID()) {
            return List.of(start);
        }

        if (findLineBetween(start, end) != null) {
            return List.of(start, end);
        }

        Map<Integer, Facility> facilityMap = new HashMap<>();
        for (Facility f : repository.getAllFacilities()) {
            facilityMap.put(f.getFacilityID(), f);
        }

        Map<Integer, List<Integer>> adjacency = buildAdjacencyList();

        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> parent = new HashMap<>();

        queue.add(start.getFacilityID());
        parent.put(start.getFacilityID(), null);

        while (!queue.isEmpty()) {
            Integer current = queue.poll();

            for (Integer neighbor : adjacency.getOrDefault(current, List.of())) {
                if (!parent.containsKey(neighbor)) {
                    parent.put(neighbor, current);
                    queue.add(neighbor);

                    // opcional: parar cedo quando chega ao destino
                    if (neighbor.equals(end.getFacilityID())) {
                        queue.clear();
                        break;
                    }
                }
            }
        }

        // Se não encontrou caminho nenhum = erro
        if (!parent.containsKey(end.getFacilityID())) {
            throw new IllegalStateException(
                    "No path found between " + start.getName() + " and " + end.getName()
            );
        }

        // Reconstrução do caminho
        List<Facility> path = new LinkedList<>();
        Integer current = end.getFacilityID();
        while (current != null) {
            path.add(0, facilityMap.get(current));
            current = parent.get(current);
        }

        return path;
    }



    private Map<Integer, List<Integer>> buildAdjacencyList() {
        Map<Integer, List<Integer>> adjacency = new HashMap<>();

        for (Lines line : repository.getAllLines()) {
            adjacency.computeIfAbsent(line.getStartFacilityID(), k -> new ArrayList<>())
                    .add(line.getEndFacilityID());

            adjacency.computeIfAbsent(line.getEndFacilityID(), k -> new ArrayList<>())
                    .add(line.getStartFacilityID());
        }

        return adjacency;
    }

    public Path calculateAutomaticPathWithStops(Train train) {
        Path originalPath = calculateAutomaticPath(train);
        if (originalPath == null) return null;

        Set<Integer> mandatoryStopIds = new HashSet<>();

        Route route = train.getRoute();
        if (route != null) {
            if (route.getStartFacility() != null) mandatoryStopIds.add(route.getStartFacility().getFacilityID());
            if (route.getEndFacility() != null) mandatoryStopIds.add(route.getEndFacility().getFacilityID());
        }

        if (train.getFreights() != null) {
            for (Freight freight : train.getFreights()) {
                if (freight.getStartFacility() != null) mandatoryStopIds.add(freight.getStartFacility().getFacilityID());
                if (freight.getEndFacility() != null) mandatoryStopIds.add(freight.getEndFacility().getFacilityID());

                if (freight.getWagons() != null) {
                    for (Wagon wagon : freight.getWagons()) {
                        if (wagon.getStartFacility() != null) mandatoryStopIds.add(wagon.getStartFacility().getFacilityID());
                    }
                }
            }
        }

        Path newPath = new Path(originalPath.getPathID());
        newPath.setRoute(originalPath.getRoute());

        List<Path.PathFacilityStep> steps = originalPath.getFacilities();
        for (int i = 0; i < steps.size(); i++) {
            Facility f = steps.get(i).getFacility();

            boolean isStop = (i == 0 || i == steps.size() - 1 || mandatoryStopIds.contains(f.getFacilityID()));

            newPath.addFacility(f, isStop);
        }

        return newPath;
    }

}