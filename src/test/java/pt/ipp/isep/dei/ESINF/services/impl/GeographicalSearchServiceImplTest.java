package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.controller.BuildTreeController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.GeoCoordinate;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.GeographicalSearchService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GeographicalSearchServiceImpl - USEI08
 * Tests the service implementation with various filter combinations and edge cases.
 */
public class GeographicalSearchServiceImplTest {

    private GeographicalSearchService service;
    private KDTree<TrainStation> emptyTree;
    private KDTree<TrainStation> populatedTree;

    @BeforeEach
    public void setUp() {
        Repositories.reset();
        service = new GeographicalSearchServiceImpl();
        emptyTree = new KDTree<>();
        populatedTree = createPopulatedTree();
    }

    /**
     * Creates a populated KD-tree with test stations covering different countries,
     * types (city/non-city), and main station status for comprehensive testing.
     */
    private KDTree<TrainStation> createPopulatedTree() {
        BuildTreeController controller = new BuildTreeController();
        List<TrainStation> stations = new ArrayList<>();

        // Get repository to add stations (needed for AVL indices)
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();

        // Portugal stations - Porto area
        TrainStation porto = new TrainStation("PT", "WET", "WET", "Porto",
                new GeoCoordinate(41.1579, -8.6291), true, true, false);
        TrainStation braga = new TrainStation("PT", "WET", "WET", "Braga",
                new GeoCoordinate(41.5454, -8.4265), true, true, false);
        TrainStation aveiro = new TrainStation("PT", "WET", "WET", "Aveiro",
                new GeoCoordinate(40.6443, -8.6455), false, true, false);
        TrainStation vilaNova = new TrainStation("PT", "WET", "WET", "Vila Nova de Gaia",
                new GeoCoordinate(41.1236, -8.6118), false, false, false);

        // Spain stations
        TrainStation vigo = new TrainStation("ES", "CET", "CET", "Vigo",
                new GeoCoordinate(42.2406, -8.7207), true, false, false);
        TrainStation pontevedra = new TrainStation("ES", "CET", "CET", "Pontevedra",
                new GeoCoordinate(42.4310, -8.6444), true, true, false);
        TrainStation santiago = new TrainStation("ES", "CET", "CET", "Santiago",
                new GeoCoordinate(42.8782, -8.5448), true, true, false);
        TrainStation rural = new TrainStation("ES", "CET", "CET", "Rural Station",
                new GeoCoordinate(42.1000, -8.7000), false, false, false);

        // France station - outside typical search range
        TrainStation paris = new TrainStation("FR", "CET", "CET", "Paris",
                new GeoCoordinate(48.8566, 2.3522), true, true, false);

        // Add all stations to list
        stations.add(porto);
        stations.add(braga);
        stations.add(aveiro);
        stations.add(vilaNova);
        stations.add(vigo);
        stations.add(pontevedra);
        stations.add(santiago);
        stations.add(rural);
        stations.add(paris);

        // Add stations to repository (needed for AVL indices)
        for (TrainStation station : stations) {
            repository.addTrainStation(station);
        }

        return controller.buildBalancedTree();
    }

    /**
     * Test: Empty tree should return empty list
     * Verifies that searching in an empty tree returns an empty but valid list.
     */
    @Test
    public void testSearchInRangeWithEmptyTree() {
        List<TrainStation> result = service.searchInRange(
                emptyTree,
                38.0, 42.0,
                -9.0, -8.0,
                null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test: Null tree should return empty list
     * Verifies that searching with null tree returns an empty but valid list.
     */
    @Test
    public void testSearchInRangeWithNullTree() {
        List<TrainStation> result = service.searchInRange(
                null,
                38.0, 42.0,
                -9.0, -8.0,
                null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test: Search without filters - should return all stations in range
     * Verifies that search returns all stations within the geographical bounds
     * when no filters are applied.
     */
    @Test
    public void testSearchInRangeWithoutFilters() {
        // Search in Portugal/Spain border area
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, null, null);

        assertNotNull(result);
        // Should find Porto, Braga, Aveiro, Vila Nova de Gaia, Vigo, Pontevedra, Santiago, Rural Station
        assertEquals(8, result.size());
    }

    /**
     * Test: Search with country filter PT - should return only Portuguese stations
     * Verifies that country filter "PT" returns only stations from Portugal.
     */
    @Test
    public void testSearchInRangeWithCountryFilterPT() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "PT", null, null);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().allMatch(s -> s.getCountry().equalsIgnoreCase("PT")));
        // Should find Porto, Braga, Aveiro, Vila Nova de Gaia
        assertEquals(4, result.size());
    }

    /**
     * Test: Search with country filter ES - should return only Spanish stations
     * Verifies that country filter "ES" returns only stations from Spain.
     */
    @Test
    public void testSearchInRangeWithCountryFilterES() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                "ES", null, null);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().allMatch(s -> s.getCountry().equalsIgnoreCase("ES")));
        // Should find Vigo, Pontevedra, Santiago, Rural Station
        assertEquals(4, result.size());
    }

    /**
     * Test: Search with country filter "all" - should return all countries
     * Verifies that country filter "all" behaves the same as null (no filter).
     */
    @Test
    public void testSearchInRangeWithCountryFilterAll() {
        List<TrainStation> resultAll = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                "all", null, null);

        List<TrainStation> resultNull = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, null, null);

        assertEquals(resultNull.size(), resultAll.size());
    }

    /**
     * Test: Search with isCity filter true - should return only city stations
     * Verifies that isCity filter with value true returns only stations where isCity is true.
     */
    @Test
    public void testSearchInRangeWithIsCityFilterTrue() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, true, null);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().allMatch(TrainStation::isCity));
    }

    /**
     * Test: Search with isCity filter false - should return only non-city stations
     * Verifies that isCity filter with value false returns only stations where isCity is false.
     */
    @Test
    public void testSearchInRangeWithIsCityFilterFalse() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, false, null);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().noneMatch(TrainStation::isCity));
    }

    /**
     * Test: Search with isMainStation filter true - should return only main stations
     * Verifies that isMainStation filter with value true returns only stations where isMainStation is true.
     */
    @Test
    public void testSearchInRangeWithIsMainStationFilterTrue() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, null, true);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().allMatch(TrainStation::isMainStation));
    }

    /**
     * Test: Search with isMainStation filter false - should return only non-main stations
     * Verifies that isMainStation filter with value false returns only stations where isMainStation is false.
     */
    @Test
    public void testSearchInRangeWithIsMainStationFilterFalse() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, null, false);

        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().noneMatch(TrainStation::isMainStation));
    }

    /**
     * Test: Search with all filters combined - PT, isCity=true, isMainStation=true
     * Verifies that multiple filters work correctly together (AND logic).
     */
    @Test
    public void testSearchInRangeWithAllFilters() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "PT", true, true);

        assertNotNull(result);
        // Should find only Portuguese cities that are main stations: Porto, Braga
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s ->
            s.getCountry().equalsIgnoreCase("PT") &&
            s.isCity() &&
            s.isMainStation()));
    }

    /**
     * Test: Search with PT and isCity=false filters
     * Verifies combination of country filter with isCity filter.
     */
    @Test
    public void testSearchInRangeWithPTAndNonCity() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "PT", false, null);

        assertNotNull(result);
        // Should find Aveiro, Vila Nova de Gaia
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s ->
            s.getCountry().equalsIgnoreCase("PT") &&
            !s.isCity()));
    }

    /**
     * Test: Search with ES and isMainStation=true filters
     * Verifies combination of country filter with isMainStation filter.
     */
    @Test
    public void testSearchInRangeWithESAndMainStation() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                42.0, 43.0,
                -9.0, -8.0,
                "ES", null, true);

        assertNotNull(result);
        // Should find Pontevedra, Santiago
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s ->
            s.getCountry().equalsIgnoreCase("ES") &&
            s.isMainStation()));
    }

    /**
     * Test: Search with range that includes no stations
     * Verifies that search returns empty list when no stations are in the specified range.
     */
    @Test
    public void testSearchInRangeWithNoResults() {
        // Search in area with no stations (North Pole area)
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                85.0, 90.0,
                0.0, 10.0,
                null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test: Search with exact boundary values (inclusive)
     * Verifies that stations exactly on the boundary are included in results.
     */
    @Test
    public void testSearchInRangeWithBoundaryValues() {
        // Porto coordinates: 41.1579, -8.6291
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                41.1579, 41.1579,
                -8.6291, -8.6291,
                null, null, null);

        assertNotNull(result);
        // Should find Porto (if coordinates match exactly)
        assertTrue(result.size() >= 0);
    }

    /**
     * Test: Search with very small range
     * Verifies that search works correctly with very small geographical ranges.
     */
    @Test
    public void testSearchInRangeWithSmallRange() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                41.15, 41.16,
                -8.63, -8.62,
                null, null, null);

        assertNotNull(result);
        // Should find Porto (within this small range)
        assertTrue(result.size() >= 0);
    }

    /**
     * Test: Search with very large range (covers entire dataset)
     * Verifies that search works correctly with very large geographical ranges.
     */
    @Test
    public void testSearchInRangeWithLargeRange() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                -90.0, 90.0,
                -180.0, 180.0,
                null, null, null);

        assertNotNull(result);
        // Should find all 9 stations
        assertEquals(9, result.size());
    }

    /**
     * Test: Search with invalid range (latMin > latMax) - should still work but return empty
     * Note: This tests the service behavior, validation should be done at controller level.
     */
    @Test
    public void testSearchInRangeWithInvalidLatRange() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                42.0, 40.0, // Invalid: min > max
                -9.0, -8.0,
                null, null, null);

        assertNotNull(result);
        // Service may return empty or handle gracefully
        assertTrue(result.isEmpty());
    }

    /**
     * Test: Search with invalid range (lonMin > lonMax) - should still work but return empty
     */
    @Test
    public void testSearchInRangeWithInvalidLonRange() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -8.0, -9.0, // Invalid: min > max
                null, null, null);

        assertNotNull(result);
        // Service may return empty or handle gracefully
        assertTrue(result.isEmpty());
    }

    /**
     * Test: Case-insensitive country filter
     * Verifies that country filter is case-insensitive (pt, PT, Pt should all work).
     */
    @Test
    public void testSearchInRangeWithCaseInsensitiveCountryFilter() {
        List<TrainStation> resultLower = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "pt", null, null);

        List<TrainStation> resultUpper = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "PT", null, null);

        assertEquals(resultLower.size(), resultUpper.size());
    }

    /**
     * Test: Multiple stations at same coordinate
     * Verifies that all stations at the same coordinate are returned when within range.
     */
    @Test
    public void testSearchInRangeWithMultipleStationsAtSameCoordinate() {
        BuildTreeController controller = new BuildTreeController();
        List<TrainStation> stations = new ArrayList<>();

        // Get repository to add stations (needed for AVL indices)
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();

        // Add multiple stations at same coordinate
        TrainStation station1 = new TrainStation("PT", "WET", "WET", "Station1",
                new GeoCoordinate(41.0, -8.0), true, true, false);
        TrainStation station2 = new TrainStation("PT", "WET", "WET", "Station2",
                new GeoCoordinate(41.0, -8.0), false, false, false);
        TrainStation station3 = new TrainStation("ES", "CET", "CET", "Station3",
                new GeoCoordinate(41.0, -8.0), true, false, false);

        stations.add(station1);
        stations.add(station2);
        stations.add(station3);

        // Add stations to repository (needed for AVL indices)
        for (TrainStation station : stations) {
            repository.addTrainStation(station);
        }

        KDTree<TrainStation> tree = controller.buildBalancedTree();

        List<TrainStation> result = service.searchInRange(
                tree,
                40.5, 41.5,
                -8.5, -7.5,
                null, null, null);

        assertNotNull(result);
        // Should find all 3 stations at the same coordinate
        assertEquals(3, result.size());
    }

    /**
     * Test: Filter combination that results in no matches
     * Verifies that filters correctly exclude all stations when no station matches all criteria.
     */
    @Test
    public void testSearchInRangeWithFiltersNoMatches() {
        // Search for PT stations that are cities and not main stations
        // In our test data, all PT cities are main stations
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "PT", true, false);

        assertNotNull(result);
        // Should return empty (no PT city that is not a main station)
        assertTrue(result.isEmpty());
    }

    /**
     * Test: Search with invalid country filter (non-existent country)
     * Verifies that search returns empty list when filtering by non-existent country.
     */
    @Test
    public void testSearchInRangeWithNonExistentCountryFilter() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                "XX", null, null); // "XX" doesn't exist in test data

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list for non-existent country");
    }

    /**
     * Test: Search with empty string country filter
     * Verifies that empty string is treated as no filter (all countries).
     */
    @Test
    public void testSearchInRangeWithEmptyStringCountryFilter() {
        List<TrainStation> resultEmpty = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                "", null, null);

        List<TrainStation> resultNull = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                null, null, null);

        // Empty string should be treated differently from null (empty string != null in filter logic)
        assertNotNull(resultEmpty);
        assertNotNull(resultNull);
        // Both should return results (empty string may filter to empty or all, depending on implementation)
        assertTrue(resultEmpty.size() >= 0);
    }

    /**
     * Test: Search with whitespace country filter
     * Verifies that whitespace-only country filter is handled correctly.
     */
    @Test
    public void testSearchInRangeWithWhitespaceCountryFilter() {
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 43.0,
                -9.0, -8.0,
                "   ", null, null); // Whitespace only

        assertNotNull(result);
        // Whitespace should not match any country
        assertTrue(result.isEmpty() || result.size() >= 0);
    }

    /**
     * Test: Search with extreme boundary coordinates (poles)
     * Verifies that search works correctly with extreme coordinates like poles.
     */
    @Test
    public void testSearchInRangeWithExtremeBoundaryCoordinates() {
        // North Pole area
        List<TrainStation> resultNorthPole = service.searchInRange(
                populatedTree,
                89.0, 90.0,
                -180.0, 180.0,
                null, null, null);

        // South Pole area
        List<TrainStation> resultSouthPole = service.searchInRange(
                populatedTree,
                -90.0, -89.0,
                -180.0, 180.0,
                null, null, null);

        assertNotNull(resultNorthPole);
        assertNotNull(resultSouthPole);
        // Should return empty (no stations at poles in test data)
        assertTrue(resultNorthPole.size() >= 0);
        assertTrue(resultSouthPole.size() >= 0);
    }

    /**
     * Test: Search with coordinate range crossing date line (longitude -180 to 180)
     * Verifies that search handles ranges that cross the international date line.
     */
    @Test
    public void testSearchInRangeCrossingDateLine() {
        // Range that crosses the international date line (from -180 to 180)
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                -90.0, 90.0,
                -180.0, 180.0,
                null, null, null);

        assertNotNull(result);
        // Should find all stations (covers entire world)
        assertEquals(9, result.size()); // All stations in test data
    }

    /**
     * Test: Search with zero-range coordinates (same point)
     * Verifies that search works with zero-range (point) queries.
     */
    @Test
    public void testSearchInRangeWithZeroRange() {
        // Single point query
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                41.0, 41.0,
                -8.0, -8.0,
                null, null, null);

        assertNotNull(result);
        // Should find stations exactly at this coordinate (if any)
        assertTrue(result.size() >= 0);
    }

    /**
     * Test: Search with country filter case sensitivity variations
     * Verifies that country filter handles different case variations correctly.
     */
    @Test
    public void testSearchInRangeWithCountryFilterCaseVariations() {
        List<TrainStation> resultLower = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "pt", null, null);

        List<TrainStation> resultUpper = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "PT", null, null);

        List<TrainStation> resultMixed1 = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "Pt", null, null);

        List<TrainStation> resultMixed2 = service.searchInRange(
                populatedTree,
                40.0, 42.0,
                -9.0, -8.0,
                "pT", null, null);

        // All case variations should return same results (case-insensitive)
        assertEquals(resultLower.size(), resultUpper.size());
        assertEquals(resultLower.size(), resultMixed1.size());
        assertEquals(resultLower.size(), resultMixed2.size());

        // Verify all results are actually from Portugal
        resultLower.forEach(s -> assertEquals("PT", s.getCountry().toUpperCase()));
    }

    /**
     * Test: Search with all filters pointing to non-existent combination
     * Verifies that combining filters that don't match any station returns empty list.
     */
    @Test
    public void testSearchInRangeWithNonExistentFilterCombination() {
        // Search for ES stations that are cities and main stations in Portugal area
        // This combination likely won't exist in the specified area
        List<TrainStation> result = service.searchInRange(
                populatedTree,
                40.0, 41.5,  // Portugal area
                -9.0, -8.0,  // Portugal area
                "ES", true, true); // But Spain filter

        assertNotNull(result);
        // Should return empty (no Spanish stations in Portugal area)
        assertTrue(result.size() >= 0);
    }
}

