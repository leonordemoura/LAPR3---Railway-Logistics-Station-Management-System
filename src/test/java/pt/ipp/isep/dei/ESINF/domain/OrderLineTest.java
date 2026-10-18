package pt.ipp.isep.dei.ESINF.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for OrderLine sorting and filtering.
 */
class OrderLineTest {

    @Test
    @DisplayName("Test 1: Sort order lines by lineNo ASC")
    void testSortByLineNoAscending() {
        // Arrange
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 3, "SKU003", 100));
        lines.add(new OrderLine("ORD001", 1, "SKU001", 50));
        lines.add(new OrderLine("ORD001", 2, "SKU002", 75));

        // Act
        List<OrderLine> sorted = OrderLine.sortOrderLines(lines);

        // Assert
        assertEquals(3, sorted.size());
        assertEquals(1, sorted.get(0).getLineNo(), "First should be line 1");
        assertEquals(2, sorted.get(1).getLineNo(), "Second should be line 2");
        assertEquals(3, sorted.get(2).getLineNo(), "Third should be line 3");
    }

    @Test
    @DisplayName("Test 2: Filter order lines by orderId")
    void testFilterByOrderId() {
        // Arrange
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 1, "SKU001", 50));
        lines.add(new OrderLine("ORD002", 1, "SKU002", 60));
        lines.add(new OrderLine("ORD001", 2, "SKU003", 70));
        lines.add(new OrderLine("ORD003", 1, "SKU004", 80));
        lines.add(new OrderLine("ORD001", 3, "SKU005", 90));

        // Act
        List<OrderLine> filtered = OrderLine.filterByOrderId(lines, "ORD001");

        // Assert
        assertEquals(3, filtered.size());
        for (OrderLine line : filtered) {
            assertEquals("ORD001", line.getOrderId());
        }
    }

    @Test
    @DisplayName("Test 3: Filter and then sort")
    void testFilterAndSort() {
        // Arrange - mixed order lines
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 3, "SKU003", 100));
        lines.add(new OrderLine("ORD002", 1, "SKU001", 50));
        lines.add(new OrderLine("ORD001", 1, "SKU001", 75));
        lines.add(new OrderLine("ORD002", 2, "SKU002", 60));
        lines.add(new OrderLine("ORD001", 2, "SKU002", 85));

        // Act - filter ORD001 and sort
        List<OrderLine> filtered = OrderLine.filterByOrderId(lines, "ORD001");
        List<OrderLine> sorted = OrderLine.sortOrderLines(filtered);

        // Assert
        assertEquals(3, sorted.size());
        assertEquals("ORD001", sorted.get(0).getOrderId());
        assertEquals(1, sorted.get(0).getLineNo());
        assertEquals("ORD001", sorted.get(1).getOrderId());
        assertEquals(2, sorted.get(1).getLineNo());
        assertEquals("ORD001", sorted.get(2).getOrderId());
        assertEquals(3, sorted.get(2).getLineNo());
    }

    @Test
    @DisplayName("Test 4: Sort with gaps in lineNo")
    void testSortWithGaps() {
        // Arrange
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 10, "SKU010", 100));
        lines.add(new OrderLine("ORD001", 2, "SKU002", 50));
        lines.add(new OrderLine("ORD001", 5, "SKU005", 75));
        lines.add(new OrderLine("ORD001", 1, "SKU001", 25));

        // Act
        List<OrderLine> sorted = OrderLine.sortOrderLines(lines);

        // Assert
        assertEquals(4, sorted.size());
        assertEquals(1, sorted.get(0).getLineNo());
        assertEquals(2, sorted.get(1).getLineNo());
        assertEquals(5, sorted.get(2).getLineNo());
        assertEquals(10, sorted.get(3).getLineNo());
    }

    @Test
    @DisplayName("Test 5: Empty list returns empty")
    void testEmptyList() {
        List<OrderLine> lines = new ArrayList<>();
        List<OrderLine> sorted = OrderLine.sortOrderLines(lines);
        assertNotNull(sorted);
        assertTrue(sorted.isEmpty());
    }

    @Test
    @DisplayName("Test 6: Null list returns empty")
    void testNullList() {
        List<OrderLine> sorted = OrderLine.sortOrderLines(null);
        assertNotNull(sorted);
        assertTrue(sorted.isEmpty());
    }

    @Test
    @DisplayName("Test 7: Single line returns same line")
    void testSingleLine() {
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 5, "SKU005", 100));

        List<OrderLine> sorted = OrderLine.sortOrderLines(lines);

        assertEquals(1, sorted.size());
        assertEquals(5, sorted.get(0).getLineNo());
    }

    @Test
    @DisplayName("Test 8: Filter non-existent orderId returns empty")
    void testFilterNonExistent() {
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 1, "SKU001", 50));
        lines.add(new OrderLine("ORD002", 1, "SKU002", 60));

        List<OrderLine> filtered = OrderLine.filterByOrderId(lines, "ORD999");

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    @DisplayName("Test 9: Reverse order sorting")
    void testReverseOrder() {
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ORD001", 5, "SKU005", 100));
        lines.add(new OrderLine("ORD001", 4, "SKU004", 90));
        lines.add(new OrderLine("ORD001", 3, "SKU003", 80));
        lines.add(new OrderLine("ORD001", 2, "SKU002", 70));
        lines.add(new OrderLine("ORD001", 1, "SKU001", 60));

        List<OrderLine> sorted = OrderLine.sortOrderLines(lines);

        assertEquals(5, sorted.size());
        for (int i = 0; i < sorted.size(); i++) {
            assertEquals(i + 1, sorted.get(i).getLineNo());
        }
    }

    @Test
    @DisplayName("Test 10: Complete workflow - multiple orders")
    void testCompleteWorkflow() {
        // Arrange - simulate real data
        List<OrderLine> allLines = new ArrayList<>();
        // Order ORD001
        allLines.add(new OrderLine("ORD001", 3, "SKU_C", 100));
        allLines.add(new OrderLine("ORD001", 1, "SKU_A", 50));
        allLines.add(new OrderLine("ORD001", 2, "SKU_B", 75));
        // Order ORD002
        allLines.add(new OrderLine("ORD002", 2, "SKU_Y", 200));
        allLines.add(new OrderLine("ORD002", 1, "SKU_X", 150));

        // Act - Process ORD001
        List<OrderLine> ord001Lines = OrderLine.filterByOrderId(allLines, "ORD001");
        List<OrderLine> ord001Sorted = OrderLine.sortOrderLines(ord001Lines);

        // Assert ORD001
        assertEquals(3, ord001Sorted.size());
        assertEquals(1, ord001Sorted.get(0).getLineNo());
        assertEquals("SKU_A", ord001Sorted.get(0).getSku());
        assertEquals(2, ord001Sorted.get(1).getLineNo());
        assertEquals("SKU_B", ord001Sorted.get(1).getSku());
        assertEquals(3, ord001Sorted.get(2).getLineNo());
        assertEquals("SKU_C", ord001Sorted.get(2).getSku());

        // Act - Process ORD002
        List<OrderLine> ord002Lines = OrderLine.filterByOrderId(allLines, "ORD002");
        List<OrderLine> ord002Sorted = OrderLine.sortOrderLines(ord002Lines);

        // Assert ORD002
        assertEquals(2, ord002Sorted.size());
        assertEquals(1, ord002Sorted.get(0).getLineNo());
        assertEquals("SKU_X", ord002Sorted.get(0).getSku());
        assertEquals(2, ord002Sorted.get(1).getLineNo());
        assertEquals("SKU_Y", ord002Sorted.get(1).getSku());
    }
}