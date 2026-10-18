package pt.ipp.isep.dei.ESINF.services.dto;

import java.util.List;

/**
 * Result wrapper for USEI13 that contains the ordered station scores and the textual complexity analysis.
 */
public class RailHubAnalysisResult {
    private final List<StationHubScore> stationScores;
    private final String complexityNote;

    public RailHubAnalysisResult(List<StationHubScore> stationScores, String complexityNote) {
        this.stationScores = stationScores;
        this.complexityNote = complexityNote;
    }

    public List<StationHubScore> stationScores() {
        return stationScores;
    }

    public String complexityNote() {
        return complexityNote;
    }
}

