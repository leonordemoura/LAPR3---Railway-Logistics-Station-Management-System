package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.services.RiskAwareShortestPathService;
import pt.ipp.isep.dei.ESINF.services.dto.RiskAwarePathResult;
import pt.ipp.isep.dei.ESINF.services.impl.RiskAwareShortestPathServiceImpl;

import java.util.List;

/**
 * Thin controller that wires the UI to the USEI15 service.
 */
public class RiskAwareShortestPathController {

    private final RiskAwareShortestPathService service;

    public RiskAwareShortestPathController() {
        this.service = new RiskAwareShortestPathServiceImpl();
    }

    public List<Station> getAllStations() {
        return service.getAllStations();
    }

    public RiskAwarePathResult computeRiskAwareShortestPath(Station source, Station target) {
        return service.computeRiskAwareShortestPath(source, target);
    }
}
