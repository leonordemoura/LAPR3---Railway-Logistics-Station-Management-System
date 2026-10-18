package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.domain.Box;
import pt.ipp.isep.dei.ESINF.domain.Order;
import pt.ipp.isep.dei.ESINF.domain.OrderEligibilityResult;
import pt.ipp.isep.dei.ESINF.domain.OrderLine;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationMode;

import java.util.List;

/**
 * USEI02 - Order Eligibility and Allocation service API.
 * Provides operations to retrieve/sort input data and to execute the
 * eligibility/allocation process over orders and order lines.
 */
public interface OrderAllocationService {

    /**
     * Retrieves all orders available in the repository.
     * @return list of orders
     */
    List<Order> getAllOrders();

    /**
     * Retrieves all order lines available in the repository.
     * @return list of order lines
     */
    List<OrderLine> getAllOrderLines();

    /**
     * Retrieves all boxes (inventory) from the repository.
     * @return list of boxes
     */
    List<Box> getAllBoxes();

    /**
     * Processes orders.
     * @param mode allocation mode (STRICT or PARTIAL)
     * @return result with eligibility summary and produced allocations
     */
    OrderEligibilityResult processOrders(AllocationMode mode);
}
