package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.services.RailHubAnalysisService;
import pt.ipp.isep.dei.ESINF.services.dto.RailHubAnalysisResult;
import pt.ipp.isep.dei.ESINF.services.impl.RailHubAnalysisServiceImpl;

/**
 * Thin controller that wires the UI to the USEI13 service.
 */
public class RailHubAnalysisController {

    private final RailHubAnalysisService service;

    public RailHubAnalysisController() {
        this.service = new RailHubAnalysisServiceImpl();
    }

    public RailHubAnalysisResult runAnalysis() {
        return service.analyzeNetwork();
    }
}




