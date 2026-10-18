package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.controller.BuildTreeController;
import pt.ipp.isep.dei.ESINF.controller.GeographicalSearchController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for USEI08 - Search by Geographical Area
 * Tests with real data from train_stations_europe.csv file.
 * 
 * These tests verify the system works correctly with actual station data,
 * ensuring the implementation handles real-world scenarios properly.
 * 
 * Note: Uses @BeforeAll to import CSV and build KD-tree once, shared by all tests.
 * This significantly improves test execution time.
 */
public class GeographicalSearchRealDataTest {

    private static final String CSV_FILE = "dataFiles/train_stations_europe.csv";
    private static GeographicalSearchController controller;
    private static WarehouseRepository repository;
    private static KDTree<TrainStation> kdTree;

    @BeforeAll
    public static void setUp() {
        repository = Repositories.getInstance().getWarehouseRepository();
        controller = new GeographicalSearchController();
        
        // Import train stations from CSV (done once for all tests)
        CSVImporter importer = new CSVImporter(repository);
        importer.importTrainStations(CSV_FILE);
        
        // Build KD-tree from imported stations (done once for all tests)
        BuildTreeController buildController = new BuildTreeController();
        List<TrainStation> stations = repository.getTrainStations();
        
        if (stations != null && !stations.isEmpty()) {
            kdTree = buildController.buildBalancedTree();
            repository.setKDTreeStation(kdTree);
        }
    }

    /**
     * Test: Verify data was imported successfully
     * Ensures that the CSV import and KD-tree construction worked correctly.
     */
    @Test
    public void testDataImportSuccess() {
        assertNotNull(repository.getTrainStations(), "Train stations list should not be null");
        assertTrue(repository.getTrainStations().size() > 0, "Should have imported stations from CSV");
        assertNotNull(kdTree, "KD-tree should be built");
        assertFalse(kdTree.isEmpty(), "KD-tree should not be empty");
    }

    /**
     * Test: Search in Portugal area without filters
     * Searches for all stations in Portugal geographical bounds using real data.
     */
    @Test
    public void testSearchInPortugalAreaWithoutFilters() {
        // Portugal approximate bounds: lat 36.8-42.2, lon -9.5 to -6.2
        List<TrainStation> result = controller.searchInRange(
                36.8, 42.2,
                -9.5, -6.2,
                null, null, null);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find stations in Portugal");
        
        // Verify all results are within the geographical range
        result.forEach(station -> {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 36.8 && lat <= 42.2, 
                "Latitude should be within range: " + station.getStation());
            assertTrue(lon >= -9.5 && lon <= -6.2, 
                "Longitude should be within range: " + station.getStation());
        });
    }

    /**
     * Test: Search in Spain area without filters
     * Searches for all stations in Spain geographical bounds using real data.
     */
    @Test
    public void testSearchInSpainAreaWithoutFilters() {
        // Spain approximate bounds: lat 35.9-43.8, lon -9.3 to 4.3
        List<TrainStation> result = controller.searchInRange(
                35.9, 43.8,
                -9.3, 4.3,
                null, null, null);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find stations in Spain");
        
        // Verify all results are within the geographical range
        result.forEach(station -> {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 35.9 && lat <= 43.8, 
                "Latitude should be within range: " + station.getStation());
            assertTrue(lon >= -9.3 && lon <= 4.3, 
                "Longitude should be within range: " + station.getStation());
        });
    }

    /**
     * Test: Search in Portugal area with country filter PT
     * Verifies that country filter correctly filters results to Portuguese stations only.
     */
    @Test
    public void testSearchInPortugalWithCountryFilterPT() {
        // Search in Portugal area with PT filter
        List<TrainStation> result = controller.searchInRange(
                36.8, 42.2,
                -9.5, -6.2,
                "PT", null, null);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            // All results should be from Portugal
            result.forEach(station -> {
                assertTrue(station.getCountry().equalsIgnoreCase("PT"), 
                    "All stations should be from Portugal: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search in Spain area with country filter ES
     * Verifies that country filter correctly filters results to Spanish stations only.
     */
    @Test
    public void testSearchInSpainWithCountryFilterES() {
        // Search in Spain area with ES filter
        List<TrainStation> result = controller.searchInRange(
                35.9, 43.8,
                -9.3, 4.3,
                "ES", null, null);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            // All results should be from Spain
            result.forEach(station -> {
                assertTrue(station.getCountry().equalsIgnoreCase("ES"), 
                    "All stations should be from Spain: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with isCity filter true
     * Verifies that isCity filter correctly returns only city stations from real data.
     */
    @Test
    public void testSearchWithIsCityFilterTrue() {
        // Search in Europe area for city stations
        List<TrainStation> result = controller.searchInRange(
                35.0, 50.0,
                -10.0, 10.0,
                null, true, null);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertTrue(station.isCity(), 
                    "All stations should be cities: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with isCity filter false
     * Verifies that isCity filter correctly returns only non-city stations from real data.
     */
    @Test
    public void testSearchWithIsCityFilterFalse() {
        // Search in Europe area for non-city stations
        List<TrainStation> result = controller.searchInRange(
                35.0, 50.0,
                -10.0, 10.0,
                null, false, null);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertFalse(station.isCity(), 
                    "All stations should not be cities: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with isMainStation filter true
     * Verifies that isMainStation filter correctly returns only main stations from real data.
     */
    @Test
    public void testSearchWithIsMainStationFilterTrue() {
        // Search in Europe area for main stations
        List<TrainStation> result = controller.searchInRange(
                35.0, 50.0,
                -10.0, 10.0,
                null, null, true);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertTrue(station.isMainStation(), 
                    "All stations should be main stations: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with isMainStation filter false
     * Verifies that isMainStation filter correctly returns only non-main stations from real data.
     */
    @Test
    public void testSearchWithIsMainStationFilterFalse() {
        // Search in Europe area for non-main stations
        List<TrainStation> result = controller.searchInRange(
                35.0, 50.0,
                -10.0, 10.0,
                null, null, false);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertFalse(station.isMainStation(), 
                    "All stations should not be main stations: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with all filters combined - PT, isCity=true, isMainStation=true
     * Verifies that multiple filters work correctly together with real data.
     */
    @Test
    public void testSearchWithAllFiltersPTCityMain() {
        List<TrainStation> result = controller.searchInRange(
                36.8, 42.2,
                -9.5, -6.2,
                "PT", true, true);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertTrue(station.getCountry().equalsIgnoreCase("PT"), 
                    "All stations should be from Portugal: " + station.getStation());
                assertTrue(station.isCity(), 
                    "All stations should be cities: " + station.getStation());
                assertTrue(station.isMainStation(), 
                    "All stations should be main stations: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with all filters combined - ES, isCity=true, isMainStation=true
     * Verifies that multiple filters work correctly together with real data.
     */
    @Test
    public void testSearchWithAllFiltersESCityMain() {
        List<TrainStation> result = controller.searchInRange(
                35.9, 43.8,
                -9.3, 4.3,
                "ES", true, true);
        
        assertNotNull(result, "Result should not be null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertTrue(station.getCountry().equalsIgnoreCase("ES"), 
                    "All stations should be from Spain: " + station.getStation());
                assertTrue(station.isCity(), 
                    "All stations should be cities: " + station.getStation());
                assertTrue(station.isMainStation(), 
                    "All stations should be main stations: " + station.getStation());
            });
        }
    }

    /**
     * Test: Search with small range around Porto
     * Tests search with a small geographical area using real coordinates.
     */
    @Test
    public void testSearchSmallRangeAroundPorto() {
        // Porto area: approximately 41.0-41.3, -8.7 to -8.5
        List<TrainStation> result = controller.searchInRange(
                41.0, 41.3,
                -8.7, -8.5,
                null, null, null);
        
        assertNotNull(result, "Result should not be null");
        
        // Verify all results are within the small range
        result.forEach(station -> {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 41.0 && lat <= 41.3, 
                "Latitude should be within Porto range: " + station.getStation());
            assertTrue(lon >= -8.7 && lon <= -8.5, 
                "Longitude should be within Porto range: " + station.getStation());
        });
    }

    /**
     * Test: Search with large range covering Europe
     * Tests search with a very large geographical area covering most of Europe.
     */
    @Test
    public void testSearchLargeRangeCoveringEurope() {
        // Europe approximate bounds
        List<TrainStation> result = controller.searchInRange(
                35.0, 70.0,
                -10.0, 40.0,
                null, null, null);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find many stations in Europe");
        
        // Verify all results are within the range
        result.forEach(station -> {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 35.0 && lat <= 70.0, 
                "Latitude should be within Europe range: " + station.getStation());
            assertTrue(lon >= -10.0 && lon <= 40.0, 
                "Longitude should be within Europe range: " + station.getStation());
        });
    }

    /**
     * Test: Search with no results (area with no stations)
     * Tests search in an area that should contain no stations (e.g., ocean area).
     */
    @Test
    public void testSearchInAreaWithNoStations() {
        // Middle of Atlantic Ocean (no stations)
        List<TrainStation> result = controller.searchInRange(
                30.0, 35.0,
                -40.0, -35.0,
                null, null, null);
        
        assertNotNull(result, "Result should not be null");
        // Should return empty list or very few results
        assertTrue(result.size() == 0 || result.size() < 10, 
            "Should find very few or no stations in Atlantic Ocean");
    }

    /**
     * Test: Validate range with real data context
     * Verifies that validation works correctly before performing searches.
     */
    @Test
    public void testValidateRangeWithRealDataContext() {
        // Valid range
        assertTrue(controller.validateRange(36.8, 42.2, -9.5, -6.2));
        
        // Invalid ranges
        assertFalse(controller.validateRange(42.2, 36.8, -9.5, -6.2)); // latMin > latMax
        assertFalse(controller.validateRange(36.8, 42.2, -6.2, -9.5)); // lonMin > lonMax
        assertFalse(controller.validateRange(91.0, 42.2, -9.5, -6.2)); // latMin > 90
        assertFalse(controller.validateRange(36.8, 42.2, 181.0, -6.2)); // lonMin > 180
    }

    /**
     * Test: Performance test - measure search time
     * Verifies that search completes in reasonable time with large dataset.
     */
    @Test
    public void testSearchPerformance() {
        long startTime = System.nanoTime();
        
        List<TrainStation> result = controller.searchInRange(
                35.0, 50.0,
                -10.0, 10.0,
                null, null, null);
        
        long endTime = System.nanoTime();
        double elapsedTimeMs = (endTime - startTime) / 1_000_000.0;
        
        assertNotNull(result, "Result should not be null");
        // Search should complete in reasonable time (e.g., less than 1 second)
        assertTrue(elapsedTimeMs < 1000, 
            "Search should complete quickly: " + elapsedTimeMs + "ms");
        
        System.out.println("Search completed in " + elapsedTimeMs + " ms, found " + 
            result.size() + " stations");
    }
}

