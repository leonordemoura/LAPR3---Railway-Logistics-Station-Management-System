package pt.ipp.isep.dei.LAPR3.controller;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.LAPR3.database.ConnectionFactory;
import pt.ipp.isep.dei.LAPR3.database.DatabaseConnection;
import pt.ipp.isep.dei.LAPR3.database.FreightStore;
import pt.ipp.isep.dei.LAPR3.database.TrainStore;
import pt.ipp.isep.dei.LAPR3.domain.Freight;
import pt.ipp.isep.dei.LAPR3.domain.Locomotive;
import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;
import pt.ipp.isep.dei.LAPR3.domain.Route;
import pt.ipp.isep.dei.LAPR3.domain.Train;
import pt.ipp.isep.dei.LAPR3.services.TrainAssemblyService;
import pt.ipp.isep.dei.LAPR3.services.dto.LocomotiveAvailabilityDTO;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;
import pt.ipp.isep.dei.LAPR3.services.impl.TrainAssemblyServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTrainController {

    private final RailwayRepository repository;
    private final TrainStore trainStore;
    private final FreightStore freightStore;
    private final ConnectionFactory connectionFactory;
    private final TrainAssemblyService assemblyService;

    public CreateTrainController() {
        this.repository = Repositories.getInstance().getRailwayRepository();
        this.trainStore = new TrainStore();
        this.freightStore = new FreightStore();
        this.assemblyService = new TrainAssemblyServiceImpl();
        try {
            this.connectionFactory = ConnectionFactory.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database connection factory", e);
        }
    }

    /**
     * Gets all available routes.
     *
     * @return list of all routes
     */
    public List<Route> getAllRoutes() {
        return repository.getAllRoutes();
    }

    public List<Locomotive> getAvailableLocomotives() {
        return repository.getAvailableLocomotives();
    }

    public List<Freight> getUnassignedFreights() {
        return repository.getUnassignedFreights();
    }

    /**
     * Gets locomotives in transit for a given route (USLP09).
     *
     * @param route The route to check locomotives for
     * @return List of locomotives in transit with their final destination
     */
    public List<LocomotiveAvailabilityDTO> getLocomotivesInTransit(Route route) {
        return assemblyService.getLocomotivesInTransit(route);
    }

    /**
     * Gets locomotives parked at stations, ordered by distance from route start (USLP09).
     *
     * @param route The route to check locomotives for
     * @return List of parked locomotives ordered by distance (descending)
     */
    public List<LocomotiveAvailabilityDTO> getLocomotivesParked(Route route) {
        return assemblyService.getLocomotivesParked(route);
    }

    /**
     * Gets wagons in transit for a given route (USLP09).
     *
     * @param route The route to check wagons for
     * @return List of wagons in transit with their final destination
     */
    public List<WagonAvailabilityDTO> getWagonsInTransit(Route route) {
        return assemblyService.getWagonsInTransit(route);
    }

    /**
     * Gets wagons parked at stations, ordered by distance from route start (USLP09).
     *
     * @param route The route to check wagons for
     * @return List of parked wagons ordered by distance (descending)
     */
    public List<WagonAvailabilityDTO> getWagonsParked(Route route) {
        return assemblyService.getWagonsParked(route);
    }

    /**
     * Validates if a train can be created with the given parameters.
     * Checks availability and scheduling constraints before creation.
     *
     * @param route       the route for the train
     * @param locomotives list of locomotives to validate
     * @param freights    list of freights to validate (wagons are validated through freights)
     * @param startDate  the start date for the train (used as planned departure time)
     * @return List of validation error messages (empty if validation passes)
     */
    public List<String> validateTrainCreation(Route route, List<Locomotive> locomotives, List<Freight> freights, Date startDate) {
        List<String> errors = new ArrayList<>();
        
        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            
            // Validate locomotives
            List<String> locomotiveErrors = trainStore.validateTrainCreation(dbConnection, route, locomotives, startDate);
            errors.addAll(locomotiveErrors);
            
            // Validate freights and their wagons
            if (freights != null) {
                for (Freight freight : freights) {
                    if (freight == null) {
                        continue;
                    }
                    
                    // Check if freight is already assigned to another train
                    if (freight.getTrain() != null) {
                        errors.add("Freight " + freight.getFreightID() + 
                                " is already assigned to train " + freight.getTrain().getTrainID() + ".");
                    }
                }
                
                // Validate wagons in all freights
                List<String> freightErrors = freightStore.validateFreightsForTrain(dbConnection, freights, -1, startDate);
                errors.addAll(freightErrors);
            }
        } catch (Exception e) {
            errors.add("Error during validation: " + e.getMessage());
        }
        
        return errors;
    }

    /**
     * Creates a new train with the specified route, locomotives, wagons, and start date.
     *
     * @param route       the route for the train
     * @param locomotives list of locomotives to assign to the train
     * @param freights      list of wagons to assign to the train (will be associated via freight)
     * @param startDate   the start date for the train
     * @return the created train, or null if creation failed
     */
    public Train createTrain(Route route, List<Locomotive> locomotives, List<Freight> freights, Date startDate) {
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        if (locomotives == null || locomotives.isEmpty()) {
            throw new IllegalArgumentException("At least one locomotive is required");
        }
        if (freights == null || freights.isEmpty()) {
            throw new IllegalArgumentException("At least one freight is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        try {
            DatabaseConnection dbConnection = connectionFactory.getDatabaseConnection();
            int trainID = trainStore.getNextTrainID(dbConnection);

            Train train = new Train(trainID, route, startDate, null);

            // Add locomotives to train
            for (Locomotive locomotive : locomotives) {
                train.addLocomotive(locomotive);
            }

            // Save train to database
            boolean saved = trainStore.save(dbConnection, train);
            if (!saved) {
                return null;
            }

            // Add train to repository
            repository.addTrain(train);

            for (Freight freight : freights) {
                if (freight == null) {
                    continue;
                }

                if (freight.getTrain() != null) {
                    throw new IllegalArgumentException("Freight " + freight.getFreightID() +
                            " is already assigned to train " + freight.getTrain().getTrainID());
                }
                freight.setRoute(route);
                freight.setDepartureDate(startDate);
                freight.setTrain(train);
                if (!train.getFreights().contains(freight)) {
                    train.addFreight(freight);
                }

                boolean freightSaved = freightStore.save(dbConnection, freight);
                if (!freightSaved) {
                    System.err.println("Warning: Failed to assign freight " + freight.getFreightID() + " to train " + trainID);
                }
            }

            return train;
        } catch (Exception e) {
            System.err.println("Error creating train: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

