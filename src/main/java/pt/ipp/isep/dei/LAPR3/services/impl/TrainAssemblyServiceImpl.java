package pt.ipp.isep.dei.LAPR3.services.impl;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.database.ConnectionFactory;
import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.database.TrainAssemblyStore;
import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.LineSegment;
import pt.ipp.isep.dei.LAPR3.domain.Lines;
import pt.ipp.isep.dei.LAPR3.domain.Locomotive;
import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;
import pt.ipp.isep.dei.LAPR3.domain.Route;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.domain.Wagon;
import pt.ipp.isep.dei.LAPR3.domain.constants.Status;
import pt.ipp.isep.dei.LAPR3.services.TrainAssemblyService;
import pt.ipp.isep.dei.LAPR3.services.dto.LocomotiveAvailabilityDTO;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for train assembly and assignment operations.
 * Uses PL/SQL functions through TrainAssemblyStore to access the database as per USLP09 requirements.
 */
public class TrainAssemblyServiceImpl implements TrainAssemblyService {

    private final RailwayRepository repository;
    private final ConnectionFactory connectionFactory;
    private final TrainAssemblyStore trainAssemblyStore;

    public TrainAssemblyServiceImpl() {
        this.repository = Repositories.getInstance().getRailwayRepository();
        try {
            this.connectionFactory = ConnectionFactory.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database connection factory", e);
        }
        this.trainAssemblyStore = new TrainAssemblyStore();
    }

    @Override
    public List<LocomotiveAvailabilityDTO> getLocomotivesInTransit(Route route) {
        if (route == null) {
            return new ArrayList<>();
        }
        
        DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
        List<LocomotiveAvailabilityDTO> locomotives = trainAssemblyStore.getLocomotivesInTransit(
                dbConnection, route.getStartFacility().getFacilityID());
        
        // If PL/SQL function failed or returned empty, try fallback
        if (locomotives.isEmpty() && dbConnection.getLastError() == null) {
            // No error but empty result - might be no data, try fallback to be safe
            return getLocomotivesInTransitFallback(route);
        }
        
        // If there was an error, fallback
        if (dbConnection.getLastError() != null) {
            return getLocomotivesInTransitFallback(route);
        }
        
        return locomotives;
    }
    
    /**
     * Fallback method that uses in-memory repository data.
     * Used when PL/SQL function is not available or fails.
     * Ensures each locomotive appears only once, prioritizing by train status and startDate.
     */
    private List<LocomotiveAvailabilityDTO> getLocomotivesInTransitFallback(Route route) {
        // Map to store the best train association for each locomotive
        // Key: locomotiveID, Value: best train for this locomotive
        Map<Integer, Train> locomotiveToBestTrain = new HashMap<>();
        
        // Get all trains (any status - PLANNED, AVAILABLE, RUNNING)
        List<Train> allTrains = repository.getAllTrains();
        
        // For each train, determine if it's the best association for its locomotives
        for (Train train : allTrains) {
            if (train.getRoute() == null) continue;
            
            for (Locomotive locomotive : train.getLocomotives()) {
                int locomotiveId = locomotive.getLocomotiveId();
                Train existingTrain = locomotiveToBestTrain.get(locomotiveId);
                
                // If no existing train, or this train has higher priority, use this train
                if (existingTrain == null || isBetterTrain(train, existingTrain)) {
                    locomotiveToBestTrain.put(locomotiveId, train);
                }
            }
        }
        
        // Build the result list from the map
        List<LocomotiveAvailabilityDTO> locomotives = new ArrayList<>();
        for (Map.Entry<Integer, Train> entry : locomotiveToBestTrain.entrySet()) {
            Train train = entry.getValue();
            if (train.getRoute() == null) continue;
            
            // Find the locomotive to get its details
            Locomotive locomotive = train.getLocomotives().stream()
                    .filter(l -> l.getLocomotiveId() == entry.getKey())
                    .findFirst()
                    .orElse(null);
            
            if (locomotive != null) {
                Facility finalDestination = train.getRoute().getEndFacility();
                LocomotiveAvailabilityDTO dto = new LocomotiveAvailabilityDTO(
                        locomotive.getLocomotiveId(),
                        locomotive.getLocomotiveName(),
                        locomotive.getOperationalSpeed());
                dto.setInTransit(true);
                dto.setFinalDestination(finalDestination);
                locomotives.add(dto);
            }
        }
        
        return locomotives;
    }
    
    /**
     * Determines if train1 is better than train2 for locomotive association.
     * Priority: RUNNING > AVAILABLE > PLANNED, then by startDate (earlier is better).
     */
    private boolean isBetterTrain(Train train1, Train train2) {
        Status status1 = train1.getStatus();
        Status status2 = train2.getStatus();
        
        // Priority order: RUNNING (1) > AVAILABLE (2) > PLANNED (3)
        int priority1 = getStatusPriority(status1);
        int priority2 = getStatusPriority(status2);
        
        if (priority1 != priority2) {
            return priority1 < priority2; // Lower priority number = higher priority
        }
        
        // If same status priority, prefer earlier startDate
        return train1.getStartDate().compareTo(train2.getStartDate()) < 0;
    }
    
    /**
     * Returns priority number for status (lower = higher priority).
     */
    private int getStatusPriority(Status status) {
        if (status == Status.RUNNING) return 1;
        if (status == Status.AVAILABLE) return 2;
        if (status == Status.PLANNED) return 3;
        return 999; // Unknown status gets lowest priority
    }

    @Override
    public List<LocomotiveAvailabilityDTO> getLocomotivesParked(Route route) {
        if (route == null) {
            return new ArrayList<>();
        }
        
        DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
        List<LocomotiveAvailabilityDTO> locomotives = trainAssemblyStore.getLocomotivesParked(
                dbConnection, route.getStartFacility().getFacilityID());
        
        // If PL/SQL function failed, try fallback
        if (dbConnection.getLastError() != null) {
            return getLocomotivesParkedFallback(route);
        }
        
        // Results are already sorted by distance descending from SQL
        return locomotives;
    }
    
    /**
     * Fallback method that uses in-memory repository data.
     * Used when PL/SQL function is not available or fails.
     */
    private List<LocomotiveAvailabilityDTO> getLocomotivesParkedFallback(Route route) {
        List<LocomotiveAvailabilityDTO> locomotives = new ArrayList<>();
        
        // Get all locomotives
        List<Locomotive> allLocomotives = repository.getAllLocomotives();
        
        // Get IDs of locomotives in ANY train (any status - these are associated with a train)
        List<Integer> locomotivesInTrains = repository.getAllTrains().stream()
                .flatMap(t -> t.getLocomotives().stream())
                .map(Locomotive::getLocomotiveId)
                .collect(Collectors.toList());
        
        // Filter locomotives that are NOT associated with any train
        // Only these are considered "parked"
        for (Locomotive locomotive : allLocomotives) {
            if (locomotivesInTrains.contains(locomotive.getLocomotiveId())) {
                continue; // Skip locomotives associated with any train
            }
            
            LocomotiveAvailabilityDTO dto = new LocomotiveAvailabilityDTO(
                    locomotive.getLocomotiveId(),
                    locomotive.getLocomotiveName(),
                    locomotive.getOperationalSpeed());
            dto.setInTransit(false);
            dto.setCurrentStation(locomotive.getStartFacility());
            
            // Calculate distance from route start
            double distance = calculateDistance(route.getStartFacility(), locomotive.getStartFacility());
            dto.setDistanceFromStart(distance);
            
            locomotives.add(dto);
        }
        
        // Sort by distance descending
        locomotives.sort(Comparator.comparingDouble(LocomotiveAvailabilityDTO::getDistanceFromStart).reversed());
        
        return locomotives;
    }

    @Override
    public List<WagonAvailabilityDTO> getWagonsInTransit(Route route) {
        if (route == null) {
            return new ArrayList<>();
        }
        
        DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
        List<WagonAvailabilityDTO> wagons = trainAssemblyStore.getWagonsInTransit(
                dbConnection, route.getStartFacility().getFacilityID());
        
        // If PL/SQL function failed or returned empty, try fallback
        if (wagons.isEmpty() && dbConnection.getLastError() == null) {
            // No error but empty result - might be no data, try fallback to be safe
            return getWagonsInTransitFallback(route);
        }
        
        // If there was an error, fallback
        if (dbConnection.getLastError() != null) {
            return getWagonsInTransitFallback(route);
        }
        
        return wagons;
    }
    
    /**
     * Fallback method that uses in-memory repository data.
     * Used when PL/SQL function is not available or fails.
     * Ensures each wagon appears only once, prioritizing by train status and startDate.
     */
    private List<WagonAvailabilityDTO> getWagonsInTransitFallback(Route route) {
        // Map to store the best freight/train association for each wagon
        // Key: wagonID, Value: best freight for this wagon (with its train)
        Map<Integer, Freight> wagonToBestFreight = new HashMap<>();
        Map<Freight, Train> freightToTrain = new HashMap<>();
        
        // Get all trains (any status - PLANNED, AVAILABLE, RUNNING)
        List<Train> allTrains = repository.getAllTrains();
        
        // For each train, determine if it's the best association for its wagons
        for (Train train : allTrains) {
            for (Freight freight : train.getFreights()) {
                // Get the final destination from the freight's endFacility
                Facility finalDestination = freight.getEndFacility();
                if (finalDestination == null) {
                    // Fallback: if freight doesn't have endFacility, use route's endFacility
                    if (train.getRoute() != null) {
                        finalDestination = train.getRoute().getEndFacility();
                    } else {
                        continue; // Skip if no destination available
                    }
                }
                
                for (Wagon wagon : freight.getWagons()) {
                    int wagonId = wagon.getWagonID();
                    Freight existingFreight = wagonToBestFreight.get(wagonId);
                    Train existingTrain = existingFreight != null ? freightToTrain.get(existingFreight) : null;
                    
                    // If no existing freight, or this train has higher priority, use this freight
                    if (existingFreight == null || isBetterTrain(train, existingTrain)) {
                        wagonToBestFreight.put(wagonId, freight);
                        freightToTrain.put(freight, train);
                    }
                }
            }
        }
        
        // Build the result list from the map
        List<WagonAvailabilityDTO> wagons = new ArrayList<>();
        for (Map.Entry<Integer, Freight> entry : wagonToBestFreight.entrySet()) {
            Freight freight = entry.getValue();
            Train train = freightToTrain.get(freight);
            
            // Get the final destination
            Facility finalDestination = freight.getEndFacility();
            if (finalDestination == null && train != null && train.getRoute() != null) {
                finalDestination = train.getRoute().getEndFacility();
            }
            
            if (finalDestination != null) {
                // Find the wagon to get its details
                Wagon wagon = freight.getWagons().stream()
                        .filter(w -> w.getWagonID() == entry.getKey())
                        .findFirst()
                        .orElse(null);
                
                if (wagon != null) {
                    WagonAvailabilityDTO dto = new WagonAvailabilityDTO(
                            wagon.getWagonID(),
                            wagon.getPayloadTons());
                    dto.setInTransit(true);
                    dto.setFinalDestination(finalDestination);
                    wagons.add(dto);
                }
            }
        }
        
        return wagons;
    }

    @Override
    public List<WagonAvailabilityDTO> getWagonsParked(Route route) {
        if (route == null) {
            return new ArrayList<>();
        }
        
        DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
        List<WagonAvailabilityDTO> wagons = trainAssemblyStore.getWagonsParked(
                dbConnection, route.getStartFacility().getFacilityID());
        
        // If PL/SQL function failed, try fallback
        if (dbConnection.getLastError() != null) {
            return getWagonsParkedFallback(route);
        }
        
        // Results are already sorted by distance descending from SQL
        return wagons;
    }
    
    /**
     * Fallback method that uses in-memory repository data.
     * Used when PL/SQL function is not available or fails.
     */
    private List<WagonAvailabilityDTO> getWagonsParkedFallback(Route route) {
        List<WagonAvailabilityDTO> wagons = new ArrayList<>();
        
        // Get all wagons
        List<Wagon> allWagons = repository.getAllWagons();
        
        // Get IDs of wagons in ANY train (via freights, any status) - these are associated with a train
        List<Integer> wagonsInTrains = repository.getAllTrains().stream()
                .flatMap(t -> t.getFreights().stream())
                .flatMap(f -> f.getWagons().stream())
                .map(Wagon::getWagonID)
                .collect(Collectors.toList());
        
        // Filter wagons that are NOT associated with any freight/train
        // Only these are considered "parked"
        for (Wagon wagon : allWagons) {
            if (wagonsInTrains.contains(wagon.getWagonID())) {
                continue; // Skip wagons associated with any train
            }
            
            WagonAvailabilityDTO dto = new WagonAvailabilityDTO(
                    wagon.getWagonID(),
                    wagon.getPayloadTons());
            dto.setInTransit(false);
            dto.setCurrentStation(wagon.getStartFacility());
            
            // Calculate distance from route start
            double distance = calculateDistance(route.getStartFacility(), wagon.getStartFacility());
            dto.setDistanceFromStart(distance);
            
            wagons.add(dto);
        }
        
        // Sort by distance descending
        wagons.sort(Comparator.comparingDouble(WagonAvailabilityDTO::getDistanceFromStart).reversed());
        
        return wagons;
    }
    
    /**
     * Calculates the distance between two facilities using line segments.
     * Simplified: uses direct line if exists, otherwise returns a large value.
     */
    private double calculateDistance(Facility from, Facility to) {
        if (from == null || to == null || from.getFacilityID() == to.getFacilityID()) {
            return 0.0;
        }
        
        // Try to find a direct line between the two facilities
        List<Lines> lines = repository.getAllLines();
        for (Lines line : lines) {
            if ((line.getStartFacilityID() == from.getFacilityID() && 
                 line.getEndFacilityID() == to.getFacilityID()) ||
                (line.getStartFacilityID() == to.getFacilityID() && 
                 line.getEndFacilityID() == from.getFacilityID())) {
                
                // Calculate total length of line segments
                List<LineSegment> segments = repository.getSegmentsByLineId(line.getLineID());
                return segments.stream()
                        .mapToDouble(LineSegment::getLength)
                        .sum();
            }
        }
        
        // If no direct line found, return a large value (will be sorted last)
        return 999999.0;
    }
}

