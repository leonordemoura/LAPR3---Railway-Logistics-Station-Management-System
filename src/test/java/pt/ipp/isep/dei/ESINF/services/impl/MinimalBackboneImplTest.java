package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MinimalBackboneImplTest {

    private static final String DATA_BASE = "dataFiles/";
    private MinimalBackboneImpl minimalBackbone;
    private WarehouseRepository repository;

    @BeforeEach
    void setUp() {
        Repositories.reset();
        minimalBackbone = new MinimalBackboneImpl();
        repository = Repositories.getInstance().getWarehouseRepository();
    }

    @Test
    void testMinimalBackbone_returnsValidMST() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        assertNotNull(originalGraph, "Original graph should be loaded");
        assertTrue(originalGraph.numVertices() > 0, "Graph should have vertices");
        assertTrue(originalGraph.numEdges() > 0, "Graph should have edges");

        Graph<Station, Weight> mst = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst, "MST should not be null for connected graph");
        assertEquals(originalGraph.numVertices(), mst.numVertices(),
                "MST should preserve all vertices from original graph");
        assertEquals(originalGraph.numVertices() - 1, mst.numEdges()/2,
                "MST should have exactly n-1 edges");
    }

    @Test
    void testMinimalBackbone_allVerticesPresent() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        Graph<Station, Weight> mst = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst);

        Set<Station> originalStations = new HashSet<>(originalGraph.vertices());
        Set<Station> mstStations = new HashSet<>(mst.vertices());

        assertEquals(originalStations, mstStations,
                "MST should contain exactly the same vertices as original graph");
    }

    @Test
    void testMinimalBackbone_noCycles() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        Graph<Station, Weight> mst = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst);

        int numVertices = mst.numVertices();
        int numEdges = mst.numEdges();

        assertEquals(numVertices - 1, numEdges/2,
                "MST must have exactly V-1 edges to be acyclic");
    }

    @Test
    void testMinimalBackbone_minimizesTotalWeight() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        Graph<Station, Weight> mst = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst);

        double mstTotalWeight = 0;
        for (Edge<Station, Weight> edge : mst.edges()) {
            mstTotalWeight += edge.getWeight().getDistance();
        }

        assertTrue(mstTotalWeight > 0, "MST should have positive total weight");

        double originalTotalWeight = 0;
        for (Edge<Station, Weight> edge : originalGraph.edges()) {
            originalTotalWeight += edge.getWeight().getDistance();
        }

        assertTrue(mstTotalWeight <= originalTotalWeight,
                "MST total weight should be less than or equal to original graph");
    }

    @Test
    void testMinimalBackbone_isConnected() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        Graph<Station, Weight> mst = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst);
        assertTrue(mst.isConnected(), "MST should be a connected graph");
    }

    @Test
    void testMinimalBackbone_allEdgesFromOriginal() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        Graph<Station, Weight> mst = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst);

        // Verify all edges in MST exist in original graph
        for (Edge<Station, Weight> mstEdge : mst.edges()) {
            boolean foundInOriginal = false;
            for (Edge<Station, Weight> originalEdge : originalGraph.edges()) {
                if ((originalEdge.getVOrig().equals(mstEdge.getVOrig()) &&
                        originalEdge.getVDest().equals(mstEdge.getVDest())) ||
                        (originalEdge.getVOrig().equals(mstEdge.getVDest()) &&
                                originalEdge.getVDest().equals(mstEdge.getVOrig()))) {
                    foundInOriginal = true;
                    break;
                }
            }
            assertTrue(foundInOriginal,
                    "All MST edges should exist in original graph");
        }
    }

    @Test
    void testGetMinimalSpanningTree_Metrics() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        Graph<Station, Weight> mst = minimalBackbone.getMinimalSpanningTree(originalGraph);

        assertNotNull(mst);
        assertEquals(originalGraph.numVertices(), mst.numVertices());
        assertTrue(mst.numEdges() < originalGraph.numEdges(),
                "MST should have fewer edges than original graph");
    }

    @Test
    void testMinimalBackbone_deterministic() {
        loadGraphs(DATA_BASE + "stations.csv", DATA_BASE + "lines.csv");
        Graph<Station, Weight> originalGraph = repository.getUndirectedGraph();

        // Run MST algorithm twice
        Graph<Station, Weight> mst1 = minimalBackbone.minimalBackbone(originalGraph);
        Graph<Station, Weight> mst2 = minimalBackbone.minimalBackbone(originalGraph);

        assertNotNull(mst1);
        assertNotNull(mst2);

        assertEquals(mst1.numVertices(), mst2.numVertices(),
                "Both MSTs should have same number of vertices");
        assertEquals(mst1.numEdges(), mst2.numEdges(),
                "Both MSTs should have same number of edges");

        // Calculate total weights
        double weight1 = mst1.edges().stream()
                .mapToDouble(e -> e.getWeight().getDistance())
                .sum();
        double weight2 = mst2.edges().stream()
                .mapToDouble(e -> e.getWeight().getDistance())
                .sum();

        assertEquals(weight1, weight2, 0.001,
                "Both MSTs should have same total weight");
    }

    private void loadGraphs(String stationsPath, String linesPath) {
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
        CSVImporter importer = new CSVImporter(repository);
        importer.importGraphs(stationsPath, linesPath);
    }
}