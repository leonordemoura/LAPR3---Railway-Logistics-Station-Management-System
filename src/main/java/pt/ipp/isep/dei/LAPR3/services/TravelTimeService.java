package pt.ipp.isep.dei.LAPR3.services;

import pt.ipp.isep.dei.LAPR3.domain.TravelTimeResult;

/**
 * Interface defining the service for calculating travel time between facilities for a given locomotive (USLP03).
 */
public interface TravelTimeService {

    /**
     * Calculates the travel time between two facilities for a specified locomotive.
     *
     * @param startFacilityId the ID of the starting facility
     * @param endFacilityId   the ID of the ending facility
     * @param locomotiveId    the ID of the locomotive used for the travel
     * @return a TravelTimeResult containing the travel time, distance, and related details
     */
    TravelTimeResult calculateTravelTime(int startFacilityId, int endFacilityId, int locomotiveId);
}