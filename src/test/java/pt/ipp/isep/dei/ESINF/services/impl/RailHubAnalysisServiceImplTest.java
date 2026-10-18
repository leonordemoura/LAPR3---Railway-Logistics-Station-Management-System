package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.map.MapGraph;
import pt.ipp.isep.dei.ESINF.services.dto.RailHubAnalysisResult;
import pt.ipp.isep.dei.ESINF.services.dto.StationHubScore;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RailHubAnalysisServiceImplTest {

    private static final String DATA_BASE = "dataFiles/";

    @BeforeEach
    void resetSingleton() {
        Repositories.reset();
    }

    @Test
    void analyzeNetwork_withRealCsv_returnsScoresForAllStations_sorted_and_normalized() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();

        RailHubAnalysisResult result = service.analyzeNetwork();
        assertNotNull(result);

        List<StationHubScore> scores = result.stationScores();
        assertFalse(scores.isEmpty(), "scores should not be empty");

        int expectedSize = Repositories.getInstance()
                .getWarehouseRepository()
                .getUndirectedGraph()
                .numVertices();
        assertEquals(expectedSize, scores.size(), "one score per station");

        // verify normalization [0,1] and ordering by hubscore desc
        double prevHub = Double.POSITIVE_INFINITY;
        for (StationHubScore s : scores) {
            assertTrue(s.hubscore() >= 0 && s.hubscore() <= 1, "hubscore in [0,1]");
            assertTrue(s.betweenness() >= 0 && s.betweenness() <= 1, "betweenness in [0,1]");
            assertTrue(s.harmonicCloseness() >= 0 && s.harmonicCloseness() <= 1, "harmonic in [0,1]");
            assertTrue(s.strength() >= 0 && s.strength() <= 1, "strength in [0,1]");
            assertTrue(prevHub + 1e-9 >= s.hubscore(), "sorted desc by hubscore");
            prevHub = s.hubscore();
        }
    }

    @Test
    void analyzeNetwork_withoutStations_throws() {
        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();
        IllegalStateException ex = assertThrows(IllegalStateException.class, service::analyzeNetwork);
        assertTrue(ex.getMessage().contains("No stations loaded"));
    }

    @Test
    void analyzeNetwork_withoutLines_throws() {
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
        CSVImporter importer = new CSVImporter(repository);
        importer.importStations(DATA_BASE + "stations.csv");

        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();
        IllegalStateException ex = assertThrows(IllegalStateException.class, service::analyzeNetwork);
        assertTrue(ex.getMessage().contains("No lines loaded"));
    }

    @Test
    void analyzeNetwork_withSmallCycleCsv_runs_and_returns_all_vertices() {
        loadGraphs(DATA_BASE + "stations_test_cycle.csv", DATA_BASE + "lines_test_cycle.csv");
        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();

        RailHubAnalysisResult result = service.analyzeNetwork();
        List<StationHubScore> scores = result.stationScores();

        int expectedSize = Repositories.getInstance()
                .getWarehouseRepository()
                .getUndirectedGraph()
                .numVertices();
        assertEquals(expectedSize, scores.size());
    }

    @Test
    void analyzeNetwork_withDisconnectedGraph_normalizesAndReturnsAllVertices() {
        loadGraphs(DATA_BASE + "stations_test_nocycle.csv", DATA_BASE + "lines_test_nocycle.csv");
        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();

        RailHubAnalysisResult result = service.analyzeNetwork();
        List<StationHubScore> scores = result.stationScores();

        int expectedSize = Repositories.getInstance()
                .getWarehouseRepository()
                .getUndirectedGraph()
                .numVertices();
        assertEquals(expectedSize, scores.size());

        // still normalized even if graph is disconnected
        for (StationHubScore s : scores) {
            assertTrue(s.betweenness() >= 0 && s.betweenness() <= 1, "betweenness in [0,1]");
            assertTrue(s.harmonicCloseness() >= 0 && s.harmonicCloseness() <= 1, "harmonic in [0,1]");
            assertTrue(s.strength() >= 0 && s.strength() <= 1, "strength in [0,1]");
            assertTrue(s.hubscore() >= 0 && s.hubscore() <= 1, "hubscore in [0,1]");
        }
    }

    @Test
    void analyzeNetwork_realCsv_noNaN_or_Infinite_values() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();

        RailHubAnalysisResult result = service.analyzeNetwork();
        List<StationHubScore> scores = result.stationScores();

        for (StationHubScore s : scores) {
            assertFalse(Double.isNaN(s.hubscore()) || Double.isInfinite(s.hubscore()), "hubscore finite");
            assertFalse(Double.isNaN(s.betweenness()) || Double.isInfinite(s.betweenness()), "betweenness finite");
            assertFalse(Double.isNaN(s.harmonicCloseness()) || Double.isInfinite(s.harmonicCloseness()), "harmonic finite");
            assertFalse(Double.isNaN(s.strength()) || Double.isInfinite(s.strength()), "strength finite");
        }
    }

    @Test
    void print_top5_scores_realCsv_for_reference() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();

        RailHubAnalysisResult result = service.analyzeNetwork();
        List<StationHubScore> scores = result.stationScores();

        int limit = Math.min(5, scores.size());
        System.out.printf("%nTop %d stations (stid, stname, degree, strength, betweenness, harmonic, hubscore)%n", limit);
        for (int i = 0; i < limit; i++) {
            StationHubScore s = scores.get(i);
            System.out.printf("%d, %s, %d, %.4f, %.4f, %.4f, %.4f%n",
                    s.stationId(),
                    s.stationName(),
                    s.degree(),
                    s.strength(),
                    s.betweenness(),
                    s.harmonicCloseness(),
                    s.hubscore());
        }
    }

    @Test
    void smallChainGraph_betweenness_and_harmonic_are_correct() {
        Repositories.reset();
        WarehouseRepository repo = Repositories.getInstance().getWarehouseRepository();
        MapGraph<Station, Weight> g = new MapGraph<>(false);

        // Build a 4-node chain A-B-C-D with unit distances and capacities
        Station a = station(1, "A");
        Station b = station(2, "B");
        Station c = station(3, "C");
        Station d = station(4, "D");
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        Weight w = new Weight(1.0, 1.0, 1.0);
        g.addEdge(a, b, w);
        g.addEdge(b, c, w);
        g.addEdge(c, d, w);

        repo.setUndirectedGraph(g);

        RailHubAnalysisServiceImpl service = new RailHubAnalysisServiceImpl();
        RailHubAnalysisResult result = service.analyzeNetwork();
        List<StationHubScore> scores = result.stationScores();

        assertEquals(4, scores.size(), "must have one score per vertex");

        StationHubScore sb = find(scores, "B");
        StationHubScore sc = find(scores, "C");
        StationHubScore sa = find(scores, "A");
        StationHubScore sd = find(scores, "D");

        // Betweenness in a 4-node chain: B and C equal, endpoints zero
        assertEquals(0.0, sa.betweenness(), 1e-9);
        assertEquals(0.0, sd.betweenness(), 1e-9);
        assertEquals(sb.betweenness(), sc.betweenness(), 1e-9);

        // Harmonic closeness: B and C equal and higher than endpoints
        assertEquals(sb.harmonicCloseness(), sc.harmonicCloseness(), 1e-9);
        assertTrue(sb.harmonicCloseness() > sa.harmonicCloseness());
        assertTrue(sc.harmonicCloseness() > sd.harmonicCloseness());

        // Strength: endpoints 1, middle 2
        assertEquals(1.0, sa.strength(), 1e-9);
        assertEquals(1.0, sd.strength(), 1e-9);
        assertEquals(1.0, sb.strength(), 1e-9); // normalized max strength = 1
        assertEquals(1.0, sc.strength(), 1e-9);

        // Hubscore: B and C should be the top and equal (within tolerance)
        assertEquals(sb.hubscore(), sc.hubscore(), 1e-9);
        assertTrue(sb.hubscore() > sa.hubscore());
        assertTrue(sc.hubscore() > sd.hubscore());
    }

    private StationHubScore find(List<StationHubScore> scores, String name) {
        return scores.stream()
                .filter(s -> s.stationName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private Station station(int id, String name) {
        return new Station(id, name, null, null);
    }

    private void loadGraphs(String stationsPath, String linesPath) {
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
        CSVImporter importer = new CSVImporter(repository);
        importer.importGraphs(stationsPath, linesPath);
    }
}


