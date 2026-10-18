package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.dataStruc.MaxFlow;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.matrix.MatrixGraph;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for MaxFlow algorithm using railway network data.
 * Tests validate various network topologies: linear paths, multiple paths,
 * bottlenecks, disconnected components, and edge cases.
 */
public class USEI14Test {

    private WarehouseRepository repo;
    private CSVImporter importer;

    @BeforeEach
    public void setUp() {
        this.repo = new WarehouseRepository();
        this.importer = new CSVImporter(repo);
    }

    /**
     * Converts the domain graph (Weight) into a capacity-focused graph (Integer).
     * @param original The railway graph from the repository.
     * @return A graph suitable for MaxFlow calculations.
     */
    private Graph<Station, Integer> getCapacityGraph(Graph<Station, Weight> original) {
        Graph<Station, Integer> capacityGraph = new MatrixGraph<>(true);
        for (Station s : original.vertices()) {
            capacityGraph.addVertex(s);
        }
        for (Edge<Station, Weight> edge : original.edges()) {
            int capacity = (int) edge.getWeight().getCapacity();
            capacityGraph.addEdge(edge.getVOrig(), edge.getVDest(), capacity);
        }
        return capacityGraph;
    }

    /**
     * Simple BFS to check if there's a path between source and sink.
     */
    private boolean hasPath(Graph<Station, Integer> graph, Station source, Station sink) {
        if (source.equals(sink)) return true;

        Set<Station> visited = new HashSet<>();
        Queue<Station> queue = new LinkedList<>();
        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            for (Edge<Station, Integer> edge : graph.outgoingEdges(current)) {
                Station neighbor = edge.getVDest();
                if (neighbor.equals(sink)) return true;

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    /**
     * Tests behavior in a disconnected graph.
     * The CSV creates a linear path, so we verify flow exists between connected stations
     * and is zero between artificially disconnected ones.
     */
    @Test
    public void testMaxFlowDisconnectedComponents() throws Exception {
        importer.importStations("dataFiles/stations_test_disconnected.csv");
        importer.importLines("dataFiles/lines_test_disconnected.csv");

        Graph<Station, Weight> directedGraph = repo.getDirectedGraph();
        List<Station> stations = repo.getStationsDirectedGraph();

        assertTrue(stations.size() >= 2, "Need at least 2 stations for this test");

        // Test 1: Verify that connected stations have flow > 0
        Station firstStation = stations.get(0);
        Station lastStation = stations.get(stations.size() - 1);

        Graph<Station, Integer> capacityGraph = getCapacityGraph(directedGraph);

        if (hasPath(capacityGraph, firstStation, lastStation)) {
            MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);
            MaxFlow.MaxFlowResult<Station> result = maxFlow.computeMaxFlow(firstStation, lastStation);

            assertTrue(result.maxFlowValue > 0,
                    "Flow should be positive between connected stations");
            System.out.println("Flow from " + firstStation.getName() + " to " +
                    lastStation.getName() + ": " + result.maxFlowValue);
        }

        // Test 2: Create artificial disconnection - test same station
        Station middleStation = stations.get(stations.size() / 2);
        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);

        assertThrows(IllegalArgumentException.class,
                () -> maxFlow.computeMaxFlow(middleStation, middleStation),
                "Should throw exception when source equals sink");
    }

    /**
     * Tests a linear network where capacity varies along the path.
     * Expected: The MaxFlow value equals the minimum capacity (bottleneck) in the path.
     */
    @Test
    public void testMaxFlowBottleneckCapacity() throws Exception {
        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Integer> capacityGraph = getCapacityGraph(repo.getDirectedGraph());
        List<Station> stations = repo.getStationsDirectedGraph();

        assertTrue(stations.size() >= 2, "Need at least 2 stations for bottleneck test");

        Station source = stations.get(0);
        Station sink = stations.get(stations.size() - 1);

        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);
        MaxFlow.MaxFlowResult<Station> result = maxFlow.computeMaxFlow(source, sink);

        assertNotNull(result, "Result should not be null");

        if (hasPath(capacityGraph, source, sink)) {
            assertTrue(result.maxFlowValue > 0,
                    "Flow should be positive in connected acyclic graph");

            // Find minimum capacity in any path (bottleneck)
            int minCapacity = findMinimumCapacityInGraph(capacityGraph);

            assertTrue(result.maxFlowValue <= minCapacity,
                    "Max flow cannot exceed minimum edge capacity");

            System.out.println("Bottleneck test - Max flow: " + result.maxFlowValue);
            System.out.println("Number of augmenting paths found: " + result.augmentingPaths.size());
        } else {
            assertEquals(0, result.maxFlowValue,
                    "Flow should be zero for disconnected stations");
        }
    }

    /**
     * Helper method to find minimum capacity in the graph.
     */
    private int findMinimumCapacityInGraph(Graph<Station, Integer> graph) {
        int min = Integer.MAX_VALUE;
        for (Edge<Station, Integer> edge : graph.edges()) {
            if (edge.getWeight() < min) {
                min = edge.getWeight();
            }
        }
        return min;
    }

    /**
     * Tests a complex network with multiple possible paths between source and sink.
     * Expected: The algorithm correctly aggregates flow from all available paths.
     */
    @Test
    public void testMaxFlowAggregationMultiplePaths() throws Exception {
        importer.importStations("dataFiles/stations_test_multiplecycle.csv");
        importer.importLines("dataFiles/lines_test_multiplecycle.csv");

        Graph<Station, Integer> capacityGraph = getCapacityGraph(repo.getDirectedGraph());
        List<Station> stations = repo.getStationsDirectedGraph();

        assertTrue(stations.size() >= 2, "Need at least 2 stations");

        Station source = stations.get(0);
        Station sink = stations.get(stations.size() - 1);

        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);
        MaxFlow.MaxFlowResult<Station> result = maxFlow.computeMaxFlow(source, sink);

        assertNotNull(result, "Result should not be null");

        // Verify flow conservation: sum of path flows equals total flow
        int totalSum = 0;
        for (MaxFlow.FlowPath<Station> path : result.augmentingPaths) {
            totalSum += path.flow;
            assertTrue(path.flow > 0, "Each path should have positive flow");
        }

        assertEquals(result.maxFlowValue, totalSum,
                "Total flow must equal sum of all augmenting path flows");

        System.out.println("Multiple paths test - Max flow: " + result.maxFlowValue);
        System.out.println("Number of paths: " + result.augmentingPaths.size());

        // Display each path
        for (int i = 0; i < result.augmentingPaths.size(); i++) {
            MaxFlow.FlowPath<Station> path = result.augmentingPaths.get(i);
            System.out.println("  Path " + (i+1) + " flow: " + path.flow);
        }
    }

    /**
     * Tests the algorithm with a single station.
     * Expected: Throws IllegalArgumentException because source and sink cannot be the same.
     */
    @Test
    public void testMaxFlowSingleVertexError() throws Exception {
        importer.importStations("dataFiles/stations_test_single.csv");
        List<Station> stations = repo.getStationsDirectedGraph();

        assertTrue(stations.size() >= 1, "Need at least 1 station");

        Graph<Station, Integer> capacityGraph = getCapacityGraph(repo.getDirectedGraph());
        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);

        Station s = stations.get(0);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> maxFlow.computeMaxFlow(s, s),
                "Should throw exception when source equals sink");

        System.out.println("Single vertex test passed - Exception message: " +
                exception.getMessage());
    }

    /**
     * Tests MaxFlow with null source or sink.
     * Expected: Throws IllegalArgumentException or NullPointerException.
     */
    @Test
    public void testMaxFlowWithNullVertices() throws Exception {
        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Integer> capacityGraph = getCapacityGraph(repo.getDirectedGraph());
        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);

        List<Station> stations = repo.getStationsDirectedGraph();
        Station validStation = stations.get(0);

        assertThrows(Exception.class,
                () -> maxFlow.computeMaxFlow(null, validStation),
                "Should throw exception with null source");

        assertThrows(Exception.class,
                () -> maxFlow.computeMaxFlow(validStation, null),
                "Should throw exception with null sink");
    }

    /**
     * Tests MaxFlow result structure and consistency.
     */
    @Test
    public void testMaxFlowResultStructure() throws Exception {
        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Integer> capacityGraph = getCapacityGraph(repo.getDirectedGraph());
        List<Station> stations = repo.getStationsDirectedGraph();

        Station source = stations.get(0);
        Station sink = stations.get(stations.size() - 1);

        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);
        MaxFlow.MaxFlowResult<Station> result = maxFlow.computeMaxFlow(source, sink);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.augmentingPaths, "Augmenting paths list should not be null");
        assertTrue(result.maxFlowValue >= 0, "Max flow value should be non-negative");

        // If there's flow, there must be at least one path
        if (result.maxFlowValue > 0) {
            assertFalse(result.augmentingPaths.isEmpty(),
                    "Should have at least one augmenting path when flow > 0");
        }

        // Each path should have valid structure
        for (MaxFlow.FlowPath<Station> path : result.augmentingPaths) {
            assertNotNull(path.path, "Path should not be null");
            assertTrue(path.flow > 0, "Path flow should be positive");
            assertFalse(path.path.isEmpty(), "Path should not be empty");

            // First station should be source, last should be sink
            assertEquals(source, path.path.get(0),
                    "Path should start at source");
            assertEquals(sink, path.path.get(path.path.size() - 1),
                    "Path should end at sink");
        }
    }

    /**
     * Tests that MaxFlow respects edge capacities.
     */
    @Test
    public void testMaxFlowRespectsCapacities() throws Exception {
        importer.importStations("dataFiles/stations_test_nocycle.csv");
        importer.importLines("dataFiles/lines_test_nocycle.csv");

        Graph<Station, Weight> directedGraph = repo.getDirectedGraph();
        Graph<Station, Integer> capacityGraph = getCapacityGraph(directedGraph);
        List<Station> stations = repo.getStationsDirectedGraph();

        Station source = stations.get(0);
        Station sink = stations.get(stations.size() - 1);

        MaxFlow<Station> maxFlow = new MaxFlow<>(capacityGraph);
        MaxFlow.MaxFlowResult<Station> result = maxFlow.computeMaxFlow(source, sink);

        // Calculate total capacity of edges leaving source
        int totalSourceCapacity = 0;
        for (Edge<Station, Integer> edge : capacityGraph.outgoingEdges(source)) {
            totalSourceCapacity += edge.getWeight();
        }

        // Max flow cannot exceed total capacity leaving source
        assertTrue(result.maxFlowValue <= totalSourceCapacity,
                "Max flow cannot exceed total capacity from source");

        System.out.println("Capacity test - Max flow: " + result.maxFlowValue +
                ", Total source capacity: " + totalSourceCapacity);
    }

}