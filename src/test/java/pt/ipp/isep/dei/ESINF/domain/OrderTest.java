package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import pt.ipp.isep.dei.ESINF.controller.OrderAllocationController;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.*;

/**
 * Unit tests for Order sorting functionality.
 * Tests Step 1: Sorting Orders by priority, dueDate, orderId
 */
class OrderTest {

    @Test
    @DisplayName("Test 1: Sort orders by priority ASC")
    void testSortByPriority() {
        // Arrange
        Date sameDate = new Date(2025 - 1900, 9, 29); // Oct 29, 2025
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD001", sameDate, 3)); // priority 3
        orders.add(new Order("ORD002", sameDate, 1)); // priority 1
        orders.add(new Order("ORD003", sameDate, 2)); // priority 2

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert
        assertEquals(3, sorted.size());
        assertEquals("ORD002", sorted.get(0).getOrderId(), "First should be priority 1");
        assertEquals("ORD003", sorted.get(1).getOrderId(), "Second should be priority 2");
        assertEquals("ORD001", sorted.get(2).getOrderId(), "Third should be priority 3");
    }

    @Test
    @DisplayName("Test 2: Sort orders by dueDate when priority is same")
    void testSortByDueDateWhenPriorityEqual() {
        // Arrange
        Date date1 = new Date(2025 - 1900, 9, 25); // Oct 25
        Date date2 = new Date(2025 - 1900, 9, 29); // Oct 29
        Date date3 = new Date(2025 - 1900, 9, 30); // Oct 30

        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD001", date3, 1)); // Oct 30
        orders.add(new Order("ORD002", date1, 1)); // Oct 25
        orders.add(new Order("ORD003", date2, 1)); // Oct 29

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert
        assertEquals(3, sorted.size());
        assertEquals("ORD002", sorted.get(0).getOrderId(), "First should be Oct 25");
        assertEquals("ORD003", sorted.get(1).getOrderId(), "Second should be Oct 29");
        assertEquals("ORD001", sorted.get(2).getOrderId(), "Third should be Oct 30");
    }

    @Test
    @DisplayName("Test 3: Sort orders by orderId when priority and date are same")
    void testSortByOrderIdWhenPriorityAndDateEqual() {
        // Arrange
        Date sameDate = new Date(2025 - 1900, 9, 29);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD003", sameDate, 1));
        orders.add(new Order("ORD001", sameDate, 1));
        orders.add(new Order("ORD002", sameDate, 1));

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert
        assertEquals(3, sorted.size());
        assertEquals("ORD001", sorted.get(0).getOrderId(), "First alphabetically");
        assertEquals("ORD002", sorted.get(1).getOrderId(), "Second alphabetically");
        assertEquals("ORD003", sorted.get(2).getOrderId(), "Third alphabetically");
    }

    @Test
    @DisplayName("Test 4: Complete sorting with all criteria (priority > date > orderId)")
    void testCompleteSorting() {
        // Arrange - intentionally out of order
        Date date1 = new Date(2025 - 1900, 9, 25); // Oct 25
        Date date2 = new Date(2025 - 1900, 9, 29); // Oct 29
        Date date3 = new Date(2025 - 1900, 9, 30); // Oct 30

        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD003", date3, 1)); // priority 1, Oct 30
        orders.add(new Order("ORD001", date2, 2)); // priority 2, Oct 29
        orders.add(new Order("ORD002", date2, 2)); // priority 2, Oct 29
        orders.add(new Order("ORD004", date1, 1)); // priority 1, Oct 25

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert
        assertEquals(4, sorted.size());
        assertEquals("ORD004", sorted.get(0).getOrderId(), "ORD004: priority 1, Oct 25");
        assertEquals("ORD003", sorted.get(1).getOrderId(), "ORD003: priority 1, Oct 30");
        assertEquals("ORD001", sorted.get(2).getOrderId(), "ORD001: priority 2, Oct 29, ORD001 < ORD002");
        assertEquals("ORD002", sorted.get(3).getOrderId(), "ORD002: priority 2, Oct 29, ORD002 > ORD001");
    }

    @Test
    @DisplayName("Test 5: Empty list returns empty list")
    void testEmptyList() {
        // Arrange
        List<Order> orders = new ArrayList<>();

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert
        assertNotNull(sorted);
        assertTrue(sorted.isEmpty());
    }

    @Test
    @DisplayName("Test 6: Single order returns same order")
    void testSingleOrder() {
        // Arrange
        Date date = new Date(2025 - 1900, 9, 25);
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD001", date, 1));

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert
        assertEquals(1, sorted.size());
        assertEquals("ORD001", sorted.get(0).getOrderId());
    }

    @Test
    @DisplayName("Test 7: Null list returns empty list")
    void testNullList() {
        // Act
        List<Order> sorted = Order.sortOrders(null);

        // Assert
        assertNotNull(sorted);
        assertTrue(sorted.isEmpty());
    }

    @Test
    @DisplayName("Test 8: Controller integration - getSortedOrders")
    void testControllerIntegration() {
        // Arrange
        Date date1 = new Date(2025 - 1900, 9, 25);
        Date date2 = new Date(2025 - 1900, 9, 29);
        Date date3 = new Date(2025 - 1900, 9, 30);

        WarehouseRepository repo = new WarehouseRepository();
        repo.addOrder(new Order("ORD003", date3, 1));
        repo.addOrder(new Order("ORD001", date2, 2));
        repo.addOrder(new Order("ORD002", date2, 2));
        repo.addOrder(new Order("ORD004", date1, 1));

        OrderAllocationController controller = new OrderAllocationController();
        List<Order> orders = new ArrayList<>(repo.getOrders().values());

        // Act
        List<Order> sorted = controller.getSortedOrders(orders);

        // Assert
        assertEquals(4, sorted.size());
        assertEquals("ORD004", sorted.get(0).getOrderId());
        assertEquals("ORD003", sorted.get(1).getOrderId());
        assertEquals("ORD001", sorted.get(2).getOrderId());
        assertEquals("ORD002", sorted.get(3).getOrderId());
    }

    @Test
    @DisplayName("Test 9: Verify sorting is stable")
    void testSortingStability() {
        // Arrange - orders with identical sort keys
        Date sameDate = new Date(2025 - 1900, 9, 29);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order("ORD001", sameDate, 1));
        orders.add(new Order("ORD002", sameDate, 1));
        orders.add(new Order("ORD003", sameDate, 1));

        // Act
        List<Order> sorted = Order.sortOrders(orders);

        // Assert - should be sorted by orderId (last criteria)
        assertEquals("ORD001", sorted.get(0).getOrderId());
        assertEquals("ORD002", sorted.get(1).getOrderId());
        assertEquals("ORD003", sorted.get(2).getOrderId());
    }
}