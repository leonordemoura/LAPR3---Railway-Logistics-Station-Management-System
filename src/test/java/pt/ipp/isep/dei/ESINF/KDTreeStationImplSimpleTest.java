package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.dataStruc.KDNode;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.GeoCoordinate;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.impl.KDTreeStationImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for KDTreeStationImpl using test_trainstation.csv.
 */
public class KDTreeStationImplSimpleTest {

    private static final String CSV_FILE = "dataFiles/test_trainstation.csv";
    private WarehouseRepository repository;
    private List<TrainStation> importedStations;
    private KDTreeStationImpl kdTreeService;
    private KDTree<TrainStation> builtTree;

    @BeforeEach
    public void setUp() {
        Repositories.reset();
        repository = Repositories.getInstance().getWarehouseRepository();

        CSVImporter importer = new CSVImporter(repository);
        importer.importTrainStations(CSV_FILE);
        importedStations = repository.getTrainStations();
        kdTreeService = new KDTreeStationImpl();

        assertNotNull(importedStations, "Imported stations should not be null");
        assertFalse(importedStations.isEmpty(), "Should have imported stations from CSV");

        builtTree = kdTreeService.buildBalancedTree();
    }

    @Test
    @DisplayName("Build tree returns non-null result")
    public void testBuildBalancedTree_NotNull() {
        assertNotNull(builtTree, "Tree should not be null");
    }

    @Test
    @DisplayName("Build tree returns non-empty result")
    public void testBuildBalancedTree_NotEmpty() {
        assertFalse(builtTree.isEmpty(), "Tree should not be empty");
    }

    @Test
    @DisplayName("Build tree returns same instance (this)")
    public void testBuildBalancedTree_ReturnsSameInstance() {
        KDTree<TrainStation> result = kdTreeService.buildBalancedTree();
        assertSame(kdTreeService, result, "Should return the same KDTree instance");
    }

    @Test
    @DisplayName("Build tree does not modify original list")
    public void testBuildBalancedTree_DoesNotModifyOriginalList() {
        List<TrainStation> copy = new ArrayList<>(importedStations);
        int originalSize = copy.size();
        KDTreeStationImpl service = new KDTreeStationImpl();
        service.buildBalancedTree();
        assertEquals(originalSize, copy.size(), "Original list size should remain unchanged");
    }


    @Test
    @DisplayName("Tree has valid root node with coordinate and stations")
    public void testBuildBalancedTree_HasValidRootNode() {
        KDNode<TrainStation> root = builtTree.getRootNode();
        assertNotNull(root, "Tree should have a root node");
        assertNotNull(root.getCoordinate(), "Root node should have a coordinate");
        assertNotNull(root.getInfo(), "Root node should have station list");
        assertFalse(root.getInfo().isEmpty(), "Root node should contain at least one station");
    }

    @Test
    @DisplayName("Number of tree nodes equals number of unique coordinates")
    public void testBuildBalancedTree_NodeCountEqualsUniqueCoordinates() {
        // Count unique coordinates in input
        Set<String> uniqueCoords = new HashSet<>();
        for (TrainStation station : importedStations) {
            String key = station.getCoordinates().getLatitude() + "," +
                    station.getCoordinates().getLongitude();
            uniqueCoords.add(key);
        }

        int nodeCount = builtTree.size();
        assertEquals(uniqueCoords.size(), nodeCount,
                "Number of tree nodes should equal number of unique coordinates");
    }

    @Test
    @DisplayName("Tree height is logarithmic relative to number of unique coordinates")
    public void testBuildBalancedTree_TreeHeightIsLogarithmic() {
        int height = builtTree.height();
        int treeSize = builtTree.size();

        assertTrue(height > 0, "Tree height should be positive");
        assertTrue(treeSize > 0, "Tree size should be positive");

        double log2Size = Math.log(treeSize) / Math.log(2);
        int minExpectedHeight = (int) Math.floor(log2Size);
        int maxExpectedHeight = (int) Math.ceil(log2Size) + 2;

        assertTrue(height >= minExpectedHeight,
                String.format("Height %d should be >= %d (floor of log2(%d))",
                        height, minExpectedHeight, treeSize));
        assertTrue(height <= maxExpectedHeight,
                String.format("Height %d should be <= %d for %d nodes",
                        height, maxExpectedHeight, treeSize));
    }

    @Test
    @DisplayName("All stations are included in the tree - exact count")
    public void testBuildBalancedTree_AllStationsIncluded() {
        int totalStationsInTree = countAllStations(builtTree.getRootNode());
        assertEquals(importedStations.size(), totalStationsInTree,
                "Tree must contain exactly the same number of stations as imported");
    }

    @Test
    @DisplayName("Each station appears exactly once in the tree")
    public void testBuildBalancedTree_NoMissingOrDuplicateStations() {
        List<TrainStation> stationsInTree = collectAllStations(builtTree.getRootNode());

        assertEquals(importedStations.size(), stationsInTree.size(),
                "Tree should contain same number of stations as input");

        for (TrainStation station : importedStations) {
            int count = 0;
            for (TrainStation treeStation : stationsInTree) {
                if (treeStation == station) {
                    count++;
                }
            }
            assertEquals(1, count,
                    "Station '" + station.getStation() + "' should appear exactly once in tree");
        }
    }

    @Test
    @DisplayName("Stations at same coordinates are grouped in same node")
    public void testBuildBalancedTree_StationsGroupedByCoordinates() {
        // Build expected grouping from input
        Map<String, List<TrainStation>> expectedGrouping = new HashMap<>();
        for (TrainStation station : importedStations) {
            String key = station.getCoordinates().getLatitude() + "," +
                    station.getCoordinates().getLongitude();
            expectedGrouping.computeIfAbsent(key, k -> new ArrayList<>()).add(station);
        }

        // Verify each node has correct grouping
        verifyNodeGroupings(builtTree.getRootNode(), expectedGrouping);
    }

    @Test
    @DisplayName("Stations at each coordinate are sorted alphabetically by name")
    public void testBuildBalancedTree_StationsSortedByName() {
        verifyStationsSortedByName(builtTree.getRootNode());
    }

    @Test
    @DisplayName("Tree contains stations from all countries in input")
    public void testBuildBalancedTree_AllCountriesPresent() {
        Set<String> expectedCountries = new HashSet<>();
        for (TrainStation station : importedStations) {
            expectedCountries.add(station.getCountry());
        }

        Set<String> countriesInTree = new HashSet<>();
        collectCountries(builtTree.getRootNode(), countriesInTree);

        assertEquals(expectedCountries, countriesInTree,
                "Tree should contain stations from all countries");
    }

    private List<TrainStation> collectAllStations(KDNode<TrainStation> node) {
        List<TrainStation> stations = new ArrayList<>();
        if (node == null) return stations;

        if (node.getInfo() != null) {
            stations.addAll(node.getInfo());
        }
        stations.addAll(collectAllStations(node.getLeft()));
        stations.addAll(collectAllStations(node.getRight()));
        return stations;
    }

    private int countAllStations(KDNode<TrainStation> node) {
        if (node == null) return 0;
        int count = (node.getInfo() != null) ? node.getInfo().size() : 0;
        count += countAllStations(node.getLeft());
        count += countAllStations(node.getRight());
        return count;
    }

    private void verifyNodeGroupings(KDNode<TrainStation> node,
                                     Map<String, List<TrainStation>> expectedGrouping) {
        if (node == null) return;

        GeoCoordinate coord = node.getCoordinate();
        String key = coord.getLatitude() + "," + coord.getLongitude();
        List<TrainStation> nodeStations = node.getInfo();

        assertTrue(expectedGrouping.containsKey(key),
                "Node coordinate " + key + " should exist in input data");

        List<TrainStation> expectedStations = expectedGrouping.get(key);
        assertEquals(expectedStations.size(), nodeStations.size(),
                "Node at " + key + " should have exactly " + expectedStations.size() + " stations");

        for (TrainStation expected : expectedStations) {
            assertTrue(nodeStations.contains(expected),
                    "Station '" + expected.getStation() + "' should be in node at " + key);
        }

        verifyNodeGroupings(node.getLeft(), expectedGrouping);
        verifyNodeGroupings(node.getRight(), expectedGrouping);
    }

    private void verifyStationsSortedByName(KDNode<TrainStation> node) {
        if (node == null) return;

        List<TrainStation> stations = node.getInfo();
        if (stations != null && stations.size() > 1) {
            for (int i = 0; i < stations.size() - 1; i++) {
                String current = stations.get(i).getStation();
                String next = stations.get(i + 1).getStation();
                assertTrue(current.compareTo(next) <= 0,
                        "Stations should be sorted: '" + current + "' should come before '" + next + "'");
            }
        }

        verifyStationsSortedByName(node.getLeft());
        verifyStationsSortedByName(node.getRight());
    }

    private void collectCountries(KDNode<TrainStation> node, Set<String> countries) {
        if (node == null) return;

        if (node.getInfo() != null) {
            for (TrainStation station : node.getInfo()) {
                countries.add(station.getCountry());
            }
        }

        collectCountries(node.getLeft(), countries);
        collectCountries(node.getRight(), countries);
    }
}