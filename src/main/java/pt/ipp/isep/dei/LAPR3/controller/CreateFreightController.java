package pt.ipp.isep.dei.LAPR3.controller;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.database.ConnectionFactory;
import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.database.FreightStore;
import pt.ipp.isep.dei.LAPR3.database.TrainAssemblyStore;
import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;
import pt.ipp.isep.dei.LAPR3.domain.Wagon;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;

import java.io.IOException;
import java.util.List;

public class CreateFreightController {

    private final RailwayRepository repository;
    private final FreightStore freightStore;
    private final ConnectionFactory connectionFactory;
    private final TrainAssemblyStore trainAssemblyStore;

    public CreateFreightController() {
        this.repository = Repositories.getInstance().getRailwayRepository();
        this.freightStore = new FreightStore();
        this.trainAssemblyStore = new TrainAssemblyStore();
        try {
            this.connectionFactory = ConnectionFactory.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database connection factory", e);
        }
    }

    public List<Facility> getFacilities() {
        return repository.getAllFacilities();
    }

    public Facility getEndPointForFacility(int facilityId) {
        return repository.getFacilityById(facilityId);
    }

    public List<Wagon> getAvailableWagons() {
        return repository.getAvailableWagons();
    }

    /**
     * Gets available wagons ordered by distance from the start facility (descending).
     * This is used when creating a freight to show wagons sorted by distance from the freight's start point.
     *
     * @param startFacility The start facility of the freight
     * @return List of wagons ordered by distance from start facility (descending)
     */
    public List<Wagon> getAvailableWagons(Facility startFacility) {
        if (startFacility == null) {
            return getAvailableWagons(); // Fallback to unsorted list
        }

        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            List<WagonAvailabilityDTO> wagonDTOs = trainAssemblyStore.getWagonsParked(
                    dbConnection, startFacility.getFacilityID());

            // Convert DTOs to Wagons using repository
            List<Wagon> wagons = new java.util.ArrayList<>();
            for (WagonAvailabilityDTO dto : wagonDTOs) {
                Wagon wagon = repository.getWagonById(dto.getWagonID());
                if (wagon != null) {
                    wagons.add(wagon);
                }
            }
            return wagons;
        } catch (Exception e) {
            // If there's an error, fallback to unsorted list
            return getAvailableWagons();
        }
    }

    /**
     * Gets available wagons with distance information, ordered by distance from the start facility (descending).
     * This is used when creating a freight to show wagons sorted by distance from the freight's start point.
     *
     * @param startFacility The start facility of the freight
     * @return List of WagonAvailabilityDTO with distance information, ordered by distance (descending)
     */
    public List<WagonAvailabilityDTO> getAvailableWagonsWithDistance(Facility startFacility) {
        if (startFacility == null) {
            return new java.util.ArrayList<>(); // Return empty list if no start facility
        }

        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            return trainAssemblyStore.getWagonsParked(dbConnection, startFacility.getFacilityID());
        } catch (Exception e) {
            return new java.util.ArrayList<>(); // Return empty list on error
        }
    }

    /**
     * Gets all freights with their wagons, sorted by freight ID.
     * This is used to display wagons grouped by freight.
     *
     * @return List of freights sorted by freight ID, each containing their wagons
     */
    public List<Freight> getFreightsWithWagons() {
        List<Freight> allFreights = repository.getAllFreights();
        // Filter to only freights that have wagons
        return allFreights.stream()
                .filter(f -> f.getWagons() != null && !f.getWagons().isEmpty())
                .sorted((f1, f2) -> Integer.compare(f1.getFreightID(), f2.getFreightID()))
                .collect(java.util.stream.Collectors.toList());
    }

    public Freight createFreight(Facility startEndPoint, Facility endEndPoint, List<Wagon> wagons) {
        if (startEndPoint == null || endEndPoint == null) {
            throw new IllegalArgumentException("Freight must define both start and end facilities");
        }
        if (wagons == null || wagons.isEmpty()) {
            throw new IllegalArgumentException("At least one wagon is required");
        }

        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            int freightID = freightStore.getNextFreightID(dbConnection);

            Freight freight = new Freight(freightID, startEndPoint, endEndPoint);
            for (Wagon wagon : wagons) {
                freight.addWagon(wagon);
            }

            boolean saved = freightStore.save(dbConnection, freight);
            if (!saved) {
                return null;
            }

            repository.addFreight(freight);
            return freight;
        } catch (Exception e) {
            System.err.println("Error creating freight: " + e.getMessage());
            return null;
        }
    }
}

