package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.Location;
import pt.ipp.isep.dei.ESINF.domain.PickingItem;
import pt.ipp.isep.dei.ESINF.domain.Trolley;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PickingPathServiceImplTest {

    private PickingPathServiceImpl service;
    private List<Location> locations;

    @BeforeEach
    void setUp() {
        service = new PickingPathServiceImpl();
        locations = Arrays.asList(
                new Location("MAIN", 1, 3),
                new Location("MAIN", 2, 1),
                new Location("MAIN", 1, 1),
                new Location("MAIN", 3, 2)
        );
    }

    @Test
    @DisplayName("Calculate total distance between sequential locations")
    void testCalculateTotalDistanceNormalCase() {
        double totalDistance = service.calculateTotalDistance(locations);
        assertEquals(27.0, totalDistance, "Total distance should be correctly calculated between all sequential locations including origin (0,0)");
    }

    @Test
    @DisplayName("Empty list returns zero distance")
    void testCalculateTotalDistanceEmptyList() {
        double totalDistance = service.calculateTotalDistance(Collections.emptyList());
        assertEquals(0.0, totalDistance, "Empty list should return distance 0.0");
    }

    @Test
    @DisplayName("Single location returns distance from (0,0)")
    void testCalculateTotalDistanceSingleLocation() {
        List<Location> single = Collections.singletonList(new Location("MAIN", 3, 4));
        double total = service.calculateTotalDistance(single);
        assertEquals(13.0, total, "Distance should include path from (0,0) to single location (3,4)");
    }

    @Test
    @DisplayName("Find optimal picking path (nearest neighbor order)")
    void testFindOptimalPickingPathNormal() {
        List<Location> path = service.findOptimalPickingPath(locations);
        assertNotNull(path, "Optimal path should not be null");
        assertEquals(locations.size(), path.size(), "Path should include all locations");
        assertTrue(path.containsAll(locations), "Path should contain all original locations");
    }

    @Test
    @DisplayName("Empty list returns empty path")
    void testFindOptimalPickingPathEmpty() {
        List<Location> path = service.findOptimalPickingPath(Collections.emptyList());
        assertTrue(path.isEmpty(), "Empty input list should return an empty path");
    }

    @Test
    @DisplayName("Path starts with closest location to (0,0)")
    void testFindOptimalPickingPathStartsClosestToOrigin() {
        List<Location> path = service.findOptimalPickingPath(locations);
        Location first = path.get(0);
        assertEquals(1, first.getAisle(), "First location should have aisle 1 (closest to origin)");
        assertEquals(1, first.getBay(), "First location should have bay 1 (closest to origin)");
    }

    @Test
    @DisplayName("Ascending path orders by aisle then bay")
    void testGetAscendingPathSorting() {
        List<Location> sorted = service.getAscendingPath(locations);
        assertEquals(1, sorted.get(0).getAisle(), "First location should have smallest aisle");
        assertEquals(1, sorted.get(0).getBay(), "First location should have smallest bay in that aisle");
        assertEquals(3, sorted.get(sorted.size() - 1).getAisle(), "Last location should have largest aisle");
    }

    @Test
    @DisplayName("Ascending path with empty list returns empty list")
    void testGetAscendingPathEmpty() {
        List<Location> sorted = service.getAscendingPath(Collections.emptyList());
        assertTrue(sorted.isEmpty(), "Empty input should return an empty sorted list");
    }

    @Test
    @DisplayName("Ascending path keeps same size as input")
    void testGetAscendingPathSameSize() {
        List<Location> sorted = service.getAscendingPath(locations);
        assertEquals(locations.size(), sorted.size(), "Sorted list should have same number of elements as input");
    }

    @Test
    @DisplayName("Extract unique locations from trolley")
    void testExtractLocationsFromTrolleyNormal() {
        List<PickingItem> items = Arrays.asList(
                new PickingItem("ORD1", 1, 1, 1, "BOX1", "SKU1", 2, 1.5),
                new PickingItem("ORD1", 2, 2, 1, "BOX2", "SKU2", 2, 2.0),
                new PickingItem("ORD1", 3, 1, 1, "BOX3", "SKU3", 1, 0.5) // duplicate aisle-bay
        );
        Trolley trolley = new Trolley("T1", 10, 0, items);

        List<Location> result = service.extractLocationsFromTrolley(trolley);
        assertEquals(2, result.size(), "Should return 2 unique locations (duplicates removed)");
    }

    @Test
    @DisplayName("Trolley with null items returns empty list")
    void testExtractLocationsFromTrolleyWithNullItems() {
        Trolley trolley = new Trolley("T1", 10, 0, null);
        List<Location> result = service.extractLocationsFromTrolley(trolley);
        assertTrue(result.isEmpty(), "Null item list should return an empty location list");
    }

    @Test
    @DisplayName("Null trolley returns empty list")
    void testExtractLocationsFromNullTrolley() {
        List<Location> result = service.extractLocationsFromTrolley(null);
        assertTrue(result.isEmpty(), "Null trolley should return an empty location list");
    }

    @Test
    @DisplayName("ExtractLocations maintains unique (aisle,bay) pairs")
    void testExtractLocationsUniqueKeys() {
        List<PickingItem> items = Arrays.asList(
                new PickingItem("ORD1", 1, 2, 3, "BOX1", "SKU1", 3, 2.0),
                new PickingItem("ORD2", 2, 2, 3, "BOX2", "SKU2", 1, 1.0),
                new PickingItem("ORD3", 3, 3, 1, "BOX3", "SKU3", 4, 3.5)
        );
        Trolley trolley = new Trolley("T2", 15, 0, items);

        List<Location> result = service.extractLocationsFromTrolley(trolley);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(l -> l.getAisle() == 2 && l.getBay() == 3));
        assertTrue(result.stream().anyMatch(l -> l.getAisle() == 3 && l.getBay() == 1));
    }
}
