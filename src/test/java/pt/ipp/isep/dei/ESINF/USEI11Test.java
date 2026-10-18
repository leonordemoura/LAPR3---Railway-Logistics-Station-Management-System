package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Algorithms;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.matrix.MatrixGraph;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for USEI11 – Directed Line Upgrade Plan.
 * Validates topological sorting and cycle detection
 * in directed railway graphs.
 */
public class USEI11Test {

    private WarehouseRepository repo;
    private CSVImporter importer;

    /**
     * Initializes repository and importer before each test.
     */
    @BeforeEach
    public void setUp() {
        repo = new WarehouseRepository();
        importer = new CSVImporter(repo);
    }

    /**
     * Tests topological sorting on a simple acyclic graph.
     * Expected: no cycles and a valid ordering of all stations.
     */
    @Test
    public void testTopologSortNoCycleSimple() throws Exception {

        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();

        Map<String, Object> result = Algorithms.topologSort(graph);

        assertNotNull(result);
        assertFalse((Boolean) result.get("hasCycle"));

        @SuppressWarnings("unchecked")
        LinkedList<Station> topsort =
                (LinkedList<Station>) result.get("topsort");

        assertNotNull(topsort);
        assertEquals(graph.numVertices(), topsort.size());

        System.out.println("Topological order:");
        for (int i = 0; i < topsort.size(); i++) {
            System.out.println((i + 1) + ". " + topsort.get(i));
        }

    }

    /**
     * Tests cycle detection in a graph containing a directed cycle.
     * Expected: cycle detected and vertices involved returned.
     */
    @Test
    public void testTopologSortWithCycle() throws Exception {

        importer.importStations("dataFiles/stations_test_cycle.csv");
        importer.importLines("dataFiles/lines_test_cycle.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();

        Map<String, Object> result = Algorithms.topologSort(graph);

        assertNotNull(result);
        assertTrue((Boolean) result.get("hasCycle"));

        @SuppressWarnings("unchecked")
        Set<Station> cycleVertices =
                (Set<Station>) result.get("cycleVertices");

        assertNotNull(cycleVertices);
        assertFalse(cycleVertices.isEmpty());

        System.out.println("Cycle vertices:");
        int i = 1;
        for (Station s : cycleVertices) {
            System.out.println(i++ + ". " + s);
        }

    }

    /**
     * Tests that the algorithm detects the first cycle in a graph with multiple cycles.
     */
    @Test
    public void testTopologSortMultipleCycles() throws Exception {
        importer.importStations("dataFiles/stations_test_multiplecycle.csv");
        importer.importLines("dataFiles/lines_test_multiplecycle.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();
        Map<String, Object> result = Algorithms.topologSort(graph);

        assertNotNull(result);
        assertTrue((Boolean) result.get("hasCycle"));

        @SuppressWarnings("unchecked")
        Set<Station> cycleVertices = (Set<Station>) result.get("cycleVertices");

        assertNotNull(cycleVertices);
        assertFalse(cycleVertices.isEmpty());

        System.out.println("Cycle vertices (first cycle detected):");
        int i = 1;
        for (Station s : cycleVertices) {
            System.out.println(i++ + ". " + s);
        }
    }

    /**
     * Tests behavior with an empty graph.
     * Expected: no cycle and empty order.
     */
    @Test
    public void testTopologSortEmptyGraph() {

        Graph<Station, Weight> graph = new MatrixGraph<>(true, 0);

        Map<String, Object> result = Algorithms.topologSort(graph);

        assertNotNull(result);
        assertFalse((Boolean) result.get("hasCycle"));

        // Verifica que a lista de ordem topológica está vazia
        @SuppressWarnings("unchecked")
        LinkedList<Station> topsort = (LinkedList<Station>) result.get("topsort");
        assertNotNull(topsort);
        assertTrue(topsort.isEmpty());

        // Para consistência, cycleVertices deve vazio
        @SuppressWarnings("unchecked")
        Set<Station> cycleVertices = (Set<Station>) result.get("cycleVertices");
        if (cycleVertices != null) {
            assertTrue(cycleVertices.isEmpty());
        }
    }


    /**
     * Tests topological sorting on a graph with a single vertex.
     * Expected: no cycle and order containing exactly one station.
     */
    @Test
    public void testTopologSortSingleVertex() throws Exception {

        importer.importStations("dataFiles/stations_test_single.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();

        Map<String, Object> result = Algorithms.topologSort(graph);

        assertNotNull(result);
        assertFalse((Boolean) result.get("hasCycle"));

        @SuppressWarnings("unchecked")
        LinkedList<Station> topsort =
                (LinkedList<Station>) result.get("topsort");

        assertEquals(1, topsort.size());

        System.out.println("Topological order:");
        for (int i = 0; i < topsort.size(); i++) {
            System.out.println((i + 1) + ". " + topsort.get(i));
        }
    }

    /**
     * Validates that a topological order contains all vertices in the graph.
     */
    @Test
    public void testTopologSortMatchesGraphStations() throws Exception {

        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();

        Map<String, Object> result = Algorithms.topologSort(graph);

        if (!(Boolean) result.get("hasCycle")) {

            @SuppressWarnings("unchecked")
            LinkedList<Station> topsort =
                    (LinkedList<Station>) result.get("topsort");

            List<Station> graphStations = repo.getStationsDirectedGraph();

            // Verifica que todos os vértices do grafo aparecem no topsort
            assertTrue(topsort.containsAll(graphStations));

            // Verifica que topsort não contém vértices extras
            assertEquals(graphStations.size(), topsort.size());
        }
    }

    /**
     * Tests topological sorting on a graph with multiple disconnected components.
     */
    @Test
    public void testTopologSortMultipleComponents() throws Exception {
        // Import CSVs que representam componentes desconexos
        importer.importStations("dataFiles/stations_test_disconnected.csv");
        importer.importLines("dataFiles/lines_test_disconnected.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();
        Map<String, Object> result = Algorithms.topologSort(graph);

        assertNotNull(result);
        assertFalse((Boolean) result.get("hasCycle"));

        @SuppressWarnings("unchecked")
        LinkedList<Station> topsort = (LinkedList<Station>) result.get("topsort");

        // Todos os vértices devem aparecer
        List<Station> graphStations = repo.getStationsDirectedGraph();
        assertTrue(topsort.containsAll(graphStations));
        assertEquals(graphStations.size(), topsort.size());

        System.out.println("Topological order:");
        for (int i = 0; i < topsort.size(); i++) {
            System.out.println((i + 1) + ". " + topsort.get(i));
        }
    }

    /**
     * Validates that topological order respects all edge dependencies.
     */
    @Test
    public void testTopologSortOrderRespectsEdges() throws Exception {
        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Weight> graph = repo.getDirectedGraph();
        Map<String, Object> result = Algorithms.topologSort(graph);

        if (!(Boolean) result.get("hasCycle")) {
            @SuppressWarnings("unchecked")
            LinkedList<Station> topsort = (LinkedList<Station>) result.get("topsort");

            // Cria mapa de posição no topsort
            Map<Station, Integer> position = new HashMap<>();
            for (int i = 0; i < topsort.size(); i++) {
                position.put(topsort.get(i), i);
            }

            // Para cada aresta, verifica se origem vem antes do destino
            for (Station origin : graph.vertices()) {
                for (Station dest : graph.adjVertices(origin)) {
                    assertTrue(position.get(origin) < position.get(dest),
                            "Origin " + origin + " must come before destination " + dest);
                }
            }
        }
    }

}
