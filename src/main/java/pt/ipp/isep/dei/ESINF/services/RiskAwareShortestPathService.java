package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.services.dto.RiskAwarePathResult;

import java.util.List;

public interface RiskAwareShortestPathService {

    /**
     * Computes a risk-aware shortest path between two stations in the directed railway graph.   
     *
     * @param source departure station
     * @param target arrival station
     * @return structured result with path, costs and negative-cycle metadata
     */
    RiskAwarePathResult computeRiskAwareShortestPath(Station source, Station target);

    /**
     * Retrieves all stations present in the directed railway graph.
     * @return immutable snapshot of the station list
     */
    List<Station> getAllStations();
}
