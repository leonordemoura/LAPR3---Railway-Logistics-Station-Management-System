package pt.ipp.isep.dei.LAPR3.controller;

import pt.ipp.isep.dei.LAPR3.domain.TravelTimeResult;
import pt.ipp.isep.dei.LAPR3.services.TravelTimeService;
import pt.ipp.isep.dei.LAPR3.services.impl.TravelTimeServiceImpl;

/**
 * Controller class for handling travel time calculations.
 */
public class TravelTimeController {
    private TravelTimeService service;

    /**
     * Constructs a new TravelTimeController with a default TravelTimeService implementation.
     */
    public TravelTimeController() {
        service = new TravelTimeServiceImpl();
    }

    /**
     * Calculates the travel time between two facilities for a given locomotive.
     *
     * @param startFacility the ID of the starting facility
     * @param endFacility   the ID of the destination facility
     * @param locomotiveID  the ID of the locomotive
     * @return a TravelTimeResult object containing the travel time details
     */
    public TravelTimeResult calculateTravelTime(int startFacility, int endFacility, int locomotiveID) {
        return service.calculateTravelTime(startFacility, endFacility, locomotiveID);
    }
}
