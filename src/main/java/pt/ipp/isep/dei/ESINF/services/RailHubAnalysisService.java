package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.services.dto.RailHubAnalysisResult;

/**
 * USEI13 – Rail Hub Centrality Analysis.
 * Provides the composite HubScore ranking together with the complexity note.
 */
public interface RailHubAnalysisService {

    /**
     * Computes the hub scores for every loaded Belgian station.
     *
     * @return ordered result with individual station metrics and the complexity description.
     */
    RailHubAnalysisResult analyzeNetwork();
}




