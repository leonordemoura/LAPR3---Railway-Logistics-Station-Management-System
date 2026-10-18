//package pt.ipp.isep.dei.LAPR3.services.impl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import pt.ipp.isep.dei.ESINF.repository.Repositories;
//import pt.ipp.isep.dei.LAPR3.domain.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TravelTimeServiceImplTest {
//
//    private TravelTimeServiceImpl service;
//    private RailwayRepository repo;
//
//    @BeforeEach
//    void setUp() {
//        Repositories.reset();
//        repo = Repositories.getInstance().getRailwayRepository();
//        service = new TravelTimeServiceImpl();
//    }
//
//    @Test
//    @DisplayName("Test 1: Calculate travel time with valid direct connection")
//    void testCalculateTravelTime_ValidConnection() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act
//        TravelTimeResult result = service.calculateTravelTime(1, 2, 1);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getTotalTimeMinutes() > 0, "Travel time should be positive");
//        assertTrue(result.getTotalDistanceKm() > 0, "Distance should be positive");
//        assertNotNull(result.getSegmentInfos(), "Segment info should not be null");
//        assertFalse(result.getSegmentInfos().isEmpty(), "Should have segment information");
//    }
//
//    @Test
//    @DisplayName("Test 2: Throw exception for non-existent locomotive")
//    void testCalculateTravelTime_InvalidLocomotive() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act & Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            service.calculateTravelTime(1, 2, 999);
//        });
//
//        assertTrue(exception.getMessage().contains("Locomotive"));
//        assertTrue(exception.getMessage().contains("not found"));
//    }
//
//    @Test
//    @DisplayName("Test 3: Throw exception for non-existent start facility")
//    void testCalculateTravelTime_InvalidStartFacility() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act & Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            service.calculateTravelTime(999, 2, 1);
//        });
//
//        assertTrue(exception.getMessage().contains("Start facility"));
//        assertTrue(exception.getMessage().contains("not found"));
//    }
//
//    @Test
//    @DisplayName("Test 4: Throw exception for non-existent end facility")
//    void testCalculateTravelTime_InvalidEndFacility() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act & Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            service.calculateTravelTime(1, 999, 1);
//        });
//
//        assertTrue(exception.getMessage().contains("End facility"));
//        assertTrue(exception.getMessage().contains("not found"));
//    }
//
//    @Test
//    @DisplayName("Test 5: Throw exception when no direct line exists")
//    void testCalculateTravelTime_NoDirectLine() {
//        // Arrange
//        setupScenarioWithoutLine();
//
//        // Act & Assert
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            service.calculateTravelTime(1, 2, 1);
//        });
//
//        assertTrue(exception.getMessage().contains("No direct line"));
//    }
//
//    @Test
//    @DisplayName("Test 6: Calculate time respects segment speed limits")
//    void testCalculateTravelTime_RespectsSpeedLimits() {
//        // Arrange
//        setupScenarioWithSpeedLimits();
//
//        // Act
//        TravelTimeResult result = service.calculateTravelTime(1, 2, 1);
//
//        // Assert
//        assertNotNull(result);
//        for (TravelTimeResult.SegmentTravelInfo info : result.getSegmentInfos()) {
//            assertTrue(info.effectiveSpeed <= info.segment.getSpeedLimit(),
//                    "Effective speed should not exceed segment limit");
//            assertTrue(info.effectiveSpeed <= result.getLocomotive().getOperationalSpeed(),
//                    "Effective speed should not exceed locomotive operational speed");
//        }
//    }
//
//    @Test
//    @DisplayName("Test 7: Calculate time with multiple segments")
//    void testCalculateTravelTime_MultipleSegments() {
//        // Arrange
//        setupScenarioWithMultipleSegments();
//
//        // Act
//        TravelTimeResult result = service.calculateTravelTime(1, 2, 1);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.getSegmentInfos().size() > 1, "Should have multiple segments");
//        assertTrue(result.getTotalTimeMinutes() > 0);
//
//        // Total time should be sum of all segment times
//        double sumOfSegmentTimes = result.getSegmentInfos().stream()
//                .mapToDouble(info -> info.timeInMinutes)
//                .sum();
//        assertEquals(sumOfSegmentTimes, result.getTotalTimeMinutes(), 0.01,
//                "Total time should equal sum of segment times");
//    }
//
//    @Test
//    @DisplayName("Test 8: Find direct line in forward direction")
//    void testFindDirectLine_ForwardDirection() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act
//        Lines line = service.findDirectLine(1, 2);
//
//        // Assert
//        assertNotNull(line, "Should find line in forward direction");
//    }
//
//    @Test
//    @DisplayName("Test 9: Find direct line in reverse direction")
//    void testFindDirectLine_ReverseDirection() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act
//        Lines line = service.findDirectLine(2, 1);
//
//        // Assert
//        assertNotNull(line, "Should find line in reverse direction");
//    }
//
//    @Test
//    @DisplayName("Test 10: Return null when no direct line exists")
//    void testFindDirectLine_NoConnection() {
//        // Arrange
//        setupScenarioWithoutLine();
//
//        // Act
//        Lines line = service.findDirectLine(1, 2);
//
//        // Assert
//        assertNull(line, "Should return null when no direct line exists");
//    }
//
//    @Test
//    @DisplayName("Test 11: Calculate correct distance from segments")
//    void testCalculateTravelTime_CorrectDistance() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act
//        TravelTimeResult result = service.calculateTravelTime(1, 2, 1);
//
//        // Assert
//        double expectedDistance = result.getSegments().stream()
//                .mapToDouble(seg -> seg.getLength() / 1000.0)
//                .sum();
//        assertEquals(expectedDistance, result.getTotalDistanceKm(), 0.01,
//                "Total distance should match sum of segment lengths");
//    }
//
//    @Test
//    @DisplayName("Test 12: Result contains all required information")
//    void testCalculateTravelTime_ResultCompleteness() {
//        // Arrange
//        setupBasicScenario();
//
//        // Act
//        TravelTimeResult result = service.calculateTravelTime(1, 2, 1);
//
//        // Assert
//        assertNotNull(result.getStartFacility(), "Should have start facility");
//        assertNotNull(result.getEndFacility(), "Should have end facility");
//        assertNotNull(result.getLocomotive(), "Should have locomotive");
//        assertNotNull(result.getLine(), "Should have line");
//        assertNotNull(result.getSegments(), "Should have segments");
//        assertFalse(result.getSegments().isEmpty(), "Segments should not be empty");
//    }
//
//    // Helper methods to setup test scenarios
//
//    private void setupBasicScenario() {
//        // Create facilities
//        Facility station1 = new Facility(1, "Station1", false, false, false);
//        Facility station2 = new Facility(2, "Station2", false, false, false);
//        repo.addFacility(station1);
//        repo.addFacility(station2);
//
//        // Create gauge
//        Gauge gauge = new Gauge( 1435);
//        repo.addGauge(gauge);
//
//        // Create line owner
//        LineOwner owner = new LineOwner(1, "VAT001", "Owner1");
//        repo.addLineOwner(owner);
//
//        // Create line
//        Lines line = new Lines(1, station1, station2, owner, gauge, false);
//        repo.addLine(line);
//
//        // Create segment
//        LineSegment segment = new LineSegment(1, line, 1, 100.0, 10000, true, 120);
//        repo.addLineSegment(segment);
//
//        // Create locomotive
//        Operator operator = new Operator(1, "VAT001", "Operator1");
//        repo.addOperator(operator);
//
//        Vehicle vehicle = new Vehicle(1, "Model1", 100000, 3.0, 4.0, 20.0, 4,120);
//        LocomotiveModel locoModel = new LocomotiveModel(1, vehicle, 5000);
//        Locomotive loco = new Locomotive(1, operator, locoModel, null, "Loco1", 2020, 140);
//        repo.addLocomotive(loco);
//    }
//
//    private void setupScenarioWithoutLine() {
//        // Create facilities without connecting line
//        Facility station1 = new Facility(1, "Station1", false, false, false);
//        Facility station2 = new Facility(2,  "Station2", false, false, false);
//        repo.addFacility(station1);
//        repo.addFacility(station2);
//
//        // Create locomotive
//        Operator operator = new Operator(1, "VAT001", "Operator1");
//        repo.addOperator(operator);
//
//        Vehicle vehicle = new Vehicle(1, "Model1", 100000, 3.0, 4.0, 20.0, 4, 120);
//        LocomotiveModel locoModel = new LocomotiveModel(1, vehicle, 5000);
//        Locomotive loco = new Locomotive(1, operator, locoModel, null, "Loco1", 2020, 140);
//        repo.addLocomotive(loco);
//    }
//
//    private void setupScenarioWithSpeedLimits() {
//        setupBasicScenario();
//
//        // The segment already has speed limit 120, locomotive has operational speed 140
//        // So effective speed should be 120
//    }
//
//    private void setupScenarioWithMultipleSegments() {
//        // Create facilities
//        Facility station1 = new Facility(1, "Station1", false, false, false);
//        Facility station2 = new Facility(2, "Station2", false, false, false);
//
//        repo.addFacility(station1);
//        repo.addFacility(station2);
//
//
//        // Create gauge
//        Gauge gauge = new Gauge(1435);
//        repo.addGauge(gauge);
//
//        // Create line owner
//        LineOwner owner = new LineOwner(1, "VAT001", "Owner1");
//        repo.addLineOwner(owner);
//
//        // Create line
//        Lines line = new Lines(1, station1, station2, owner, gauge, true);
//        repo.addLine(line);
//
//        // Create multiple segments
//        LineSegment segment1 = new LineSegment(1, line, 1,  100.0, 5000, true, 120);
//        LineSegment segment2 = new LineSegment(2, line, 2, 100.0, 8000, true, 100);
//        LineSegment segment3 = new LineSegment(3, line, 3, 100.0, 7000, true, 140);
//        repo.addLineSegment(segment1);
//        repo.addLineSegment(segment2);
//        repo.addLineSegment(segment3);
//
//        // Create locomotive
//        Operator operator = new Operator(1, "VAT001", "Operator1");
//        repo.addOperator(operator);
//
//        Vehicle vehicle = new Vehicle(1, "Model1", 100000, 3.0, 4.0, 20.0, 4, 120);
//        LocomotiveModel locoModel = new LocomotiveModel(1, vehicle, 5000);
//        Locomotive loco = new Locomotive(1, operator, locoModel, null, "Loco1", 2020, 140);
//        repo.addLocomotive(loco);
//    }
//}