package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.matrix.MatrixGraph;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.dto.RiskAwarePathResult;
import pt.ipp.isep.dei.ESINF.services.impl.RiskAwareShortestPathServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class USEI15Test {

    private WarehouseRepository repo;

    @BeforeEach
    public void setUp() {
        Repositories.reset();
        repo = Repositories.getInstance().getWarehouseRepository();
    }

    @Test
    public void testSimplePositiveGraph_shortestPathAndCosts() {
        Graph<Station, Weight> g = new MatrixGraph<>(true);

        Station a = new Station(1, "A", null, null);
        Station b = new Station(2, "B", null, null);
        Station c = new Station(3, "C", null, null);
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);

        g.addEdge(a, b, new Weight(10.0, 0.0, 5.0));
        g.addEdge(a, c, new Weight(5.0, 0.0, 2.0));
        g.addEdge(c, b, new Weight(3.0, 0.0, 1.0));

        repo.setDirectedGraph(g);

        RiskAwareShortestPathServiceImpl service = new RiskAwareShortestPathServiceImpl();
        RiskAwarePathResult result = service.computeRiskAwareShortestPath(a, b);

        assertNotNull(result);
        assertFalse(result.hasNegativeCycle());
        assertFalse(result.negativeCycleAffectsTarget());

        List<Station> path = result.path();
        assertEquals(List.of(a, c, b), path, "Expected path A → C → B");

        assertEquals(2, result.segments().size());
        assertEquals(2.0, result.segments().get(0).segmentCost(), 1e-6);
        assertEquals(2.0, result.segments().get(0).cumulativeCost(), 1e-6);
        assertEquals(1.0, result.segments().get(1).segmentCost(), 1e-6);
        assertEquals(3.0, result.segments().get(1).cumulativeCost(), 1e-6);

        assertEquals(3.0, result.totalCost(), 1e-6);
    }

    @Test
    public void testNegativeEdgeNoCycle_pathUsesNegativeCost() {
        Graph<Station, Weight> g = new MatrixGraph<>(true);

        Station a = new Station(1, "A", null, null);
        Station b = new Station(2, "B", null, null);
        Station c = new Station(3, "C", null, null);
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);

        g.addEdge(a, b, new Weight(5.0, 0.0, 4.0));
        g.addEdge(a, c, new Weight(3.0, 0.0, 1.0));
        g.addEdge(c, b, new Weight(2.0, 0.0, -5.0));

        repo.setDirectedGraph(g);

        RiskAwareShortestPathServiceImpl service = new RiskAwareShortestPathServiceImpl();
        RiskAwarePathResult result = service.computeRiskAwareShortestPath(a, b);

        assertNotNull(result);
        assertFalse(result.hasNegativeCycle());

        List<Station> path = result.path();
        assertEquals(List.of(a, c, b), path);
        assertEquals(-4.0, result.totalCost(), 1e-6);

        assertEquals(2, result.segments().size());
        assertEquals(1.0, result.segments().get(0).segmentCost(), 1e-6);
        assertEquals(-5.0, result.segments().get(1).segmentCost(), 1e-6);
    }

    @Test
    public void testNegativeCycleAffectsTarget_flaggedAndNoFinitePath() {
        Graph<Station, Weight> g = new MatrixGraph<>(true);

        Station a = new Station(1, "A", null, null);
        Station b = new Station(2, "B", null, null);
        Station c = new Station(3, "C", null, null);
        Station d = new Station(4, "D", null, null);
        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);

        g.addEdge(a, b, new Weight(1.0, 0.0, 1.0));
        g.addEdge(b, c, new Weight(1.0, 0.0, -2.0));
        g.addEdge(c, b, new Weight(1.0, 0.0, -2.0));
        g.addEdge(c, d, new Weight(1.0, 0.0, 2.0));

        repo.setDirectedGraph(g);

        RiskAwareShortestPathServiceImpl service = new RiskAwareShortestPathServiceImpl();
        RiskAwarePathResult result = service.computeRiskAwareShortestPath(a, d);

        assertTrue(result.hasNegativeCycle(), "Negative cycle should be detected");
        assertTrue(result.negativeCycleAffectsTarget(),
                "Negative cycle should affect the path to target");

        assertTrue(result.path().isEmpty(), "Path should be empty when no finite optimum exists");
        assertTrue(result.segments().isEmpty());
        assertTrue(Double.isNaN(result.totalCost()));
        assertFalse(result.negativeCycleStations().isEmpty(),
                "Stations involved in the cycle should be reported");
    }

    @Test
    public void testNegativeCycleElsewhere_pathStillReturned() {
        Graph<Station, Weight> g = new MatrixGraph<>(true);

        Station a = new Station(1, "A", null, null);
        Station d = new Station(2, "D", null, null);
        Station x = new Station(3, "X", null, null);
        Station y = new Station(4, "Y", null, null);
        g.addVertex(a);
        g.addVertex(d);
        g.addVertex(x);
        g.addVertex(y);

        // Safe path component: A → D (5)
        g.addEdge(a, d, new Weight(5.0, 0.0, 5.0));

        // Separate negative cycle X ↔ Y
        g.addEdge(x, y, new Weight(1.0, 0.0, -3.0));
        g.addEdge(y, x, new Weight(1.0, 0.0, -3.0));

        repo.setDirectedGraph(g);

        RiskAwareShortestPathServiceImpl service = new RiskAwareShortestPathServiceImpl();
        RiskAwarePathResult result = service.computeRiskAwareShortestPath(a, d);

        // Cycle is not reachable from A, so it should not affect A→D
        assertFalse(result.hasNegativeCycle(),
                "Negative cycle unreachable from source should not be reported for this query");
        assertFalse(result.negativeCycleAffectsTarget());

        assertEquals(List.of(a, d), result.path());
        assertEquals(5.0, result.totalCost(), 1e-6);
    }

    @Test
    public void testUnreachableTarget_throws() {
        Graph<Station, Weight> g = new MatrixGraph<>(true);

        Station a = new Station(1, "A", null, null);
        Station b = new Station(2, "B", null, null);
        g.addVertex(a);
        g.addVertex(b);

        // No edge between A and B
        repo.setDirectedGraph(g);

        RiskAwareShortestPathServiceImpl service = new RiskAwareShortestPathServiceImpl();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.computeRiskAwareShortestPath(a, b));
        assertTrue(ex.getMessage().toLowerCase().contains("unreachable"));
    }

    @Test
    public void testInvalidStation_throws() {
        Graph<Station, Weight> g = new MatrixGraph<>(true);

        Station a = new Station(1, "A", null, null);
        Station b = new Station(2, "B", null, null);
        g.addVertex(a);
        // b is not added to graph

        repo.setDirectedGraph(g);

        RiskAwareShortestPathServiceImpl service = new RiskAwareShortestPathServiceImpl();

        assertThrows(IllegalArgumentException.class,
                () -> service.computeRiskAwareShortestPath(a, b));
    }
}
