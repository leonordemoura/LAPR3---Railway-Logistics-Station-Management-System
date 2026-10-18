package pt.ipp.isep.dei.ESINF.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationMode;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationStatus;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderAllocationImplTest {

    private OrderAllocationImpl service;
    private WarehouseRepository repo;

    @BeforeEach
    void setUp() {
        Repositories.reset();
        repo = Repositories.getInstance().getWarehouseRepository();
        service = new OrderAllocationImpl();
        setupWarehouse();
    }


    @Test
    @DisplayName("Test 1: Test if full allocation is done")
    void testProcessOrdersStrictModeFullAllocation() {

        // Arrange
        Date now = new Date();

        // Add box with sufficient quantity
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 100, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Add order
        Order order = new Order("ORD001", new Date(), 1);
        repo.addOrder(order);

        // Add order line requesting less than available
        OrderLine line = new OrderLine("ORD001", 1, "SKU001", 50);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        assertNotNull(result, "Result should not be null");
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(1, eligibilities.size(), "Should have 1 eligibility record");

        LineEligibility lineElig = eligibilities.get(0);

        assertEquals("ORD001", lineElig.getOrderId(), "Order ID should match");
        assertEquals(AllocationStatus.ELIGIBLE, lineElig.getStatus(), "Order with sufficient stock should be ELIGIBLE");
        assertEquals(50, lineElig.getAllocatedQty(), "Should allocate requested quantity");

        List<Allocation> allocations = result.getAllocationList();
        assertEquals(1, allocations.size(), "Should have 1 allocation");
    }

    @Test
    @DisplayName("Test 2: Test if allocation is not made")
    void testProcessOrdersStrictModeInsufficientStock() {
        // Arrange
        Date now = new Date();

        // Add box with insufficient quantity
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 30, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Add order requesting more than available
        Order order = new Order("ORD001", new Date(), 1);
        repo.addOrder(order);

        OrderLine line = new OrderLine("ORD001", 1, "SKU001", 50);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(1, eligibilities.size(), "Should have 1 eligibility record");

        LineEligibility lineElig = eligibilities.get(0);

        assertEquals(AllocationStatus.UNDISPATCHABLE, lineElig.getStatus(), "Order with insufficient stock should be UNDISPATCHABLE in strict mode");
        assertEquals(0, lineElig.getAllocatedQty(), "No allocation should be made in strict mode");

        // No allocations should be kept in strict mode
        List<Allocation> allocations = result.getAllocationList();
        assertEquals(0, allocations.size(), "No allocations should be kept in strict mode for insufficient stock");
    }

    @Test
    @DisplayName("Test 3: Test if partial allocation is done")
    void testProcessOrdersPartialModePartialAllocation() {
        // Arrange
        Date now = new Date();

        // Add box with partial quantity
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 30, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Add order
        Order order = new Order("ORD001", new Date(), 1);
        repo.addOrder(order);

        OrderLine line = new OrderLine("ORD001", 1, "SKU001", 50);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.PARTIAL);

        // Assert
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(1, eligibilities.size(), "Should have 1 eligibility record");

        LineEligibility lineElig = eligibilities.get(0);

        assertEquals(AllocationStatus.PARTIAL, lineElig.getStatus(), "Order with insufficient stock should be PARTIAL in partial mode");
        assertEquals(30, lineElig.getAllocatedQty(), "Should allocate available quantity");

        // Allocations should be kept in partial mode
        List<Allocation> allocations = result.getAllocationList();
        assertEquals(1, allocations.size(), "Allocations should be kept in partial mode");
    }

    @Test
    @DisplayName("Test 4: Test if partial allocation is not done")
    void testProcessOrdersPartialModeNoAllocation() {
        // Arrange
        Date now = new Date();

        // Add box with partial quantity
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 0, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Add order
        Order order = new Order("ORD001", new Date(), 1);
        repo.addOrder(order);

        OrderLine line = new OrderLine("ORD001", 1, "SKU001", 50);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.PARTIAL);

        // Assert
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(1, eligibilities.size(), "Should have 1 eligibility record");

        LineEligibility lineElig = eligibilities.get(0);

        assertEquals(AllocationStatus.UNDISPATCHABLE, lineElig.getStatus(), "Order with no stock should be UNDISPATCHABLE in partial mode");
        assertEquals(0, lineElig.getAllocatedQty(), "Doesn't have available quantity");

        // Allocations should be kept in partial mode
        List<Allocation> allocations = result.getAllocationList();
        assertEquals(0, allocations.size(), "Allocations should be kept in partial mode");
    }

    @Test
    @DisplayName("Test 5: Test if order is Undispatchable goes to the next one ELIGIBLE")
    void testProcessOrdersStrictModeNextAllocation() {
        // Arrange
        Date now = new Date();

        // Add box with partial quantity
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 10, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Add order
        Order order1 = new Order("ORD001", new Date(), 1);
        repo.addOrder(order1);
        Order order2 = new Order("ORD002", new Date(), 2);
        repo.addOrder(order2);

        OrderLine line1 = new OrderLine("ORD001", 1, "SKU001", 50);
        repo.addOrderLine(line1);
        OrderLine line2 = new OrderLine("ORD002", 1, "SKU001", 5);
        repo.addOrderLine(line2);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(2, eligibilities.size(), "Should have 2 eligibility record");

        LineEligibility lineElig1 = eligibilities.get(0);
        LineEligibility lineElig2 = eligibilities.get(1);

        assertEquals(AllocationStatus.UNDISPATCHABLE, lineElig1.getStatus(), "Order with insufficient stock should be UNDISPATCHABLE in strict mode");
        assertEquals(0, lineElig1.getAllocatedQty(), "Doesn't have available quantity");
        assertEquals(AllocationStatus.ELIGIBLE, lineElig2.getStatus(), "Order with stock should be ELIGIBLE in strict mode");
        assertEquals(5, lineElig2.getAllocatedQty(), "Should allocate available quantity");

        // Allocations should be kept in partial mode
        List<Allocation> allocations = result.getAllocationList();
        assertEquals(1, allocations.size(), "1 allocation should be kept in strict mode");
    }

    @Test
    @DisplayName("Test 6: No allocation when stock does not exist")
    void testProcessOrdersNoStock() {
        // Arrange
        Order order = new Order("ORD004", new Date(), 1);
        repo.addOrder(order);

        OrderLine line = new OrderLine("ORD004", 1, "SKU001", 50);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(1, eligibilities.size(), "Should have 1 eligibility record");

        LineEligibility lineElig = eligibilities.get(0);

        assertEquals(AllocationStatus.UNDISPATCHABLE, lineElig.getStatus(), "When there is no stock available the order is UNDISPATCHABLE");
        assertEquals(0, result.getAllocationList().size(), "Should have no allocations");
    }

    @Test
    @DisplayName("Test 7: Test if boxes are in FIFO order")
    void testProcessOrdersMultipleBoxesFIFO() {
        // Arrange
        Date now = new Date();
        Date earlier = new Date(now.getTime() - 86400000);

        // Add boxes
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 20, null, earlier);
        Box box2 = new Box("BOX002", "WGN002", "SKU001", 20, null, now);

        repo.addBox(box1, "W1", 1, 1);
        repo.addBox(box2, "W1", 1, 1);

        // Add order
        Order order = new Order("ORD001", new Date(), 1);
        repo.addOrder(order);

        OrderLine line = new OrderLine("ORD001", 1, "SKU001", 25);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        List<Allocation> allocations = result.getAllocationList();
        assertEquals(2, allocations.size(), "Should have 2 allocations");

        // First allocation should be from box with expiry date
        assertEquals("BOX001", allocations.get(0).getBoxId(), "First box should be BOX001");
    }

    @Test
    @DisplayName("Test 8: Test if boxes are in FEFO order")
    void testProcessOrdersMultipleBoxesFEFO() {
        // Arrange
        Date now = new Date();
        Date earlier = new Date(now.getTime() - 86400000);

        // Add boxes
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 20, now, now);
        Box box2 = new Box("BOX002", "WGN002", "SKU001", 20, earlier, now);
        repo.addBox(box1, "W1", 1, 1);
        repo.addBox(box2, "W1", 1, 1);

        // Add order
        Order order = new Order("ORD001", new Date(), 1);
        repo.addOrder(order);

        OrderLine line = new OrderLine("ORD001", 1, "SKU001", 25);
        repo.addOrderLine(line);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        List<Allocation> allocations = result.getAllocationList();
        assertEquals(2, allocations.size(), "Should have 2 allocations");

        // First allocation should be from box with earlier expiry
        assertEquals("BOX002", allocations.get(0).getBoxId(), "First box should be BOX002");
    }

    @Test
    @DisplayName("Test 9: Test if order are presented according to acceptance criteria")
    void testProcessOrdersMultipleOrdersPrioritySorting() {
        // Arrange
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 86400000);

        Box box1 = new Box("BOX001", "WGN001", "SKU001", 100, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Order with lower priority (higher number = lower priority)
        Order order1 = new Order("ORD001", tomorrow, 2);
        repo.addOrder(order1);
        OrderLine line1 = new OrderLine("ORD001", 1, "SKU001", 50);
        repo.addOrderLine(line1);

        // Order with higher priority (lower number = higher priority)
        Order order2 = new Order("ORD002", tomorrow, 1);
        repo.addOrder(order2);
        OrderLine line2 = new OrderLine("ORD002", 1, "SKU001", 30);
        repo.addOrderLine(line2);

        // Act
        OrderEligibilityResult result = service.processOrders(AllocationMode.STRICT);

        // Assert
        List<LineEligibility> eligibilities = result.getEligibilityList();
        assertEquals(2, eligibilities.size(), "Should have 2 order to classify eligibilities.");

        // Higher priority order (ORD002) should be processed first
        assertEquals("ORD002", eligibilities.get(0).getOrderId(), "First order classified should be ORD002");
        assertEquals("ORD001", eligibilities.get(1).getOrderId(), "Second order classified should be ORD001");
    }

    @Test
    @DisplayName("Test 10: Test if method gets all orders from Repository")
    void testGetAllOrders() {
        // Arrange
        Order order1 = new Order("ORD001", new Date(), 1);
        Order order2 = new Order("ORD002", new Date(), 2);
        repo.addOrder(order1);
        repo.addOrder(order2);

        // Act
        List<Order> orders = service.getAllOrders();

        // Assert
        assertEquals(2, orders.size(), "Should have 2 orders");
    }

    @Test
    @DisplayName("Test 11: Test if method gets all order lines from Repository")
    void testGetAllOrderLines() {
        // Arrange
        OrderLine line1 = new OrderLine("ORD001", 1, "SKU001", 10);
        OrderLine line2 = new OrderLine("ORD001", 2, "SKU002", 20);
        repo.addOrderLine(line1);
        repo.addOrderLine(line2);

        // Act
        List<OrderLine> lines = service.getAllOrderLines();

        // Assert
        assertEquals(2, lines.size(), "Should have 2 order lines");
    }

    @Test
    @DisplayName("Test 12: Test if method gets all boxes from Repository")
    void testGetAllBoxes() {
        // Arrange
        Date now = new Date();
        Box box1 = new Box("BOX001", "WGN001", "SKU001", 50, null, now);
        repo.addBox(box1, "W1", 1, 1);

        // Act
        List<Box> boxes = service.getAllBoxes();

        // Assert
        assertEquals(1, boxes.size(), "Should have 1 box");
        assertEquals("BOX001", boxes.get(0).getBoxId(), "The box stored should be BOX001");
    }

    private void setupWarehouse() {
        // Create warehouse with name and empty aisles map
        Warehouse w1 = new Warehouse("W1", "Test Warehouse", new HashMap<>());
        repo.addWarehouse(w1);

        // Create aisle
        Aisle a1 = new Aisle(1, "W1");
        repo.addAisle("W1", a1);

        // Create bay
        repo.addBay("W1", 1, 1, 10);

        // Create item
        Item item = new Item("SKU001", "Test Item", "Category1", "unit", 1.0, 1.0);
        repo.addItem(item);
    }

}