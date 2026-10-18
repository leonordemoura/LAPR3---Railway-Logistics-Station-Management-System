package pt.ipp.isep.dei.LAPR3.services;

import pt.ipp.isep.dei.LAPR3.domain.Route;
import pt.ipp.isep.dei.LAPR3.services.dto.LocomotiveAvailabilityDTO;
import pt.ipp.isep.dei.LAPR3.services.dto.WagonAvailabilityDTO;

import java.util.List;

/**
 * Service interface for train assembly and assignment operations.
 */
public interface TrainAssemblyService {
    /**
     * Gets locomotives in transit for a given route.
     * 
     * @param route The route to check locomotives for
     * @return List of locomotives in transit with their final destination
     */
    List<LocomotiveAvailabilityDTO> getLocomotivesInTransit(Route route);

    /**
     * Gets locomotives parked at stations, ordered by distance from route start.
     * 
     * @param route The route to check locomotives for
     * @return List of parked locomotives ordered by distance (descending)
     */
    List<LocomotiveAvailabilityDTO> getLocomotivesParked(Route route);

    /**
     * Gets wagons in transit for a given route.
     * 
     * @param route The route to check wagons for
     * @return List of wagons in transit with their final destination
     */
    List<WagonAvailabilityDTO> getWagonsInTransit(Route route);

    /**
     * Gets wagons parked at stations, ordered by distance from route start.
     * 
     * @param route The route to check wagons for
     * @return List of parked wagons ordered by distance (descending)
     */
    List<WagonAvailabilityDTO> getWagonsParked(Route route);
}


