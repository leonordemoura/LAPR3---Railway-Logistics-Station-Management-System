package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.RailHubAnalysisController;
import pt.ipp.isep.dei.ESINF.services.dto.RailHubAnalysisResult;
import pt.ipp.isep.dei.ESINF.services.dto.StationHubScore;

import java.util.List;

/**
 * Console action for USEI13 – Rail Hub Centrality Analysis.
 */
public class RailHubAnalysisUI implements Runnable {

    private static final int MAX_ROWS = 559;

    private final RailHubAnalysisController controller;

    public RailHubAnalysisUI() {
        this.controller = new RailHubAnalysisController();
    }

    @Override
    public void run() {
        System.out.println("\n--- USEI13: Rail Hub Centrality Analysis ---\n");
        try {
            RailHubAnalysisResult result = controller.runAnalysis();
            displayScores(result.stationScores());
            System.out.printf("%nComplexity:%n  %s%n%n", result.complexityNote());
        } catch (IllegalStateException e) {
            System.out.printf("ERROR: %s%n%n", e.getMessage());
        }
    }

    private void displayScores(List<StationHubScore> scores) {
        if (scores.isEmpty()) {
            System.out.println("No stations available.\n");
            return;
        }

        int limit = Math.min(scores.size(), MAX_ROWS);
        System.out.printf("Showing top %d of %d stations (sorted by HubScore).%n%n", limit, scores.size());

        System.out.printf("%-5s %-28s %7s %10s %12s %12s %12s%n",
                "ID", "Station", "Degree", "Strength", "Betweenness", "Harmonic", "HubScore");
        System.out.println("-".repeat(95));

        for (int i = 0; i < limit; i++) {
            StationHubScore score = scores.get(i);
            System.out.printf("%-5d %-28s %7d %10.2f %12.4f %12.4f %12.4f%n",
                    score.stationId(),
                    truncate(score.stationName(), 28),
                    score.degree(),
                    score.strength(),
                    score.betweenness(),
                    score.harmonicCloseness(),
                    score.hubscore());
        }

        if (scores.size() > limit) {
            System.out.printf("%n... %d more stations not shown. Export support can be added if required.%n",
                    scores.size() - limit);
        }
    }

    private String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value == null ? "" : value;
        }
        return value.substring(0, max - 1) + "…";
    }
}




