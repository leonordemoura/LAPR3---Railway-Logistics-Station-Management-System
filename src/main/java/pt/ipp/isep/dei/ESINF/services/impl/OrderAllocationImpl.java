package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.controller.PickingPlanController;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationMode;
import pt.ipp.isep.dei.ESINF.domain.constants.AllocationStatus;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.OrderAllocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * USEI02 — Order Eligibility and Allocations
 * <p>
 * Service implementation that creates allocation and classifies each order line.
 * </p>
 */
public class OrderAllocationImpl implements OrderAllocationService {
    private static WarehouseRepository warehouseRepository;

    /**
     * Constructor that initiates the Warehouse Repository
     */
    public OrderAllocationImpl() {
        warehouseRepository = Repositories.getInstance().getWarehouseRepository();
    }

    /**
     * Gets all orders from csv.
     *
     * @return list of all orders (unsorted)
     */
    @Override
    public List<Order> getAllOrders() {
        Map<String, Order> ordersMap = warehouseRepository.getOrders();
        return new ArrayList<>(ordersMap.values());
    }

    /**
     * Gets all order lines from csv.
     *
     * @return list of all order lines
     */
    @Override
    public List<OrderLine> getAllOrderLines() {
        return warehouseRepository.getOrderLines();
    }

    /**
     * Get boxes already sorted by FEFO/FIFO
     *
     * @return list of boxes sorted by FEFO/FIFO
     */
    @Override
    public List<Box> getAllBoxes(){
        return warehouseRepository.getAllBoxes();
    }

    /**
     * Processes orders and allocates inventory.
     *
     * @param mode allocation mode (STRICT or PARTIAL)
     * @return order eligibility result
     */
    @Override
    public OrderEligibilityResult processOrders(AllocationMode mode) {
        // Get all orders
        List<Order> orders = getAllOrders();

        // Sort Orders according to acceptance criteria
        List<Order> sortedOrders = Order.sortOrders(orders);

        // Get all Order Lines
        List<OrderLine> orderLines = getAllOrderLines();

        // Get Boxes
        List<Box> allBoxes = getAllBoxes();

        // Build box location map once for all allocations
        Map<String, Location> boxLocationMap = buildBoxLocationMap();

        // Create Result
        OrderEligibilityResult result = new OrderEligibilityResult();

        // Process each order
        for (Order order : sortedOrders) {
            processOrder(order, orderLines, allBoxes, boxLocationMap, mode, result);
        }

        // Store allocations for USEI03
        PickingPlanController.setAllocations(result.getAllocationList());

        return result;
    }

    /**
     * Builds a map of box locations.
     * This is done once at the start to avoid repeated nested loops.
     *
     * @return map of boxId to BoxLocation
     */
    private Map<String, Location> buildBoxLocationMap() {
        Map<String, Location> locationMap = new HashMap<>();

        for (Warehouse warehouse : warehouseRepository.getWarehouses().values()) {
            for (Aisle aisleObj : warehouse.getAisles().values()) {
                for (Bay bayObj : aisleObj.getBays().values()) {
                    for (Box box : bayObj.getBoxes().values()) {
                        locationMap.put(box.getBoxId(), new Location(
                                warehouse.getWarehouseId(),
                                aisleObj.getAisleNumber(),
                                bayObj.getBay()
                        ));
                    }
                }
            }
        }

        return locationMap;
    }

    /**
     * Processes a single order.
     *
     * @param order the order that will the executed
     * @param allOrderLines all the order lines
     * @param allBoxes all boxes that exist
     * @param boxLocationMap map for O(1) box location lookup
     * @param mode mode that was chosen
     * @param result empty instance that will be filled during the process
     */
    private void processOrder(Order order, List<OrderLine> allOrderLines,
                              List<Box> allBoxes, Map<String, Location> boxLocationMap,
                              AllocationMode mode, OrderEligibilityResult result) {
        // Get and sort order lines for this order
        List<OrderLine> orderLines = OrderLine.filterByOrderId(allOrderLines, order.getOrderId());
        List<OrderLine> sortedLines = OrderLine.sortOrderLines(orderLines);

        // Process each line
        for (OrderLine orderLine : sortedLines) {
            processOrderLine(order, orderLine, allBoxes, boxLocationMap, mode, result);
        }
    }

    /**
     * Processes a single order line.
     *
     * @param order order that will be executed
     * @param orderLine order line that will be executed
     * @param allBoxes all boxes that exist
     * @param boxLocationMap map for O(1) box location lookup
     * @param mode mode that was chosen
     * @param result empty instance that will be filled during the process
     */
    private void processOrderLine(Order order, OrderLine orderLine, List<Box> allBoxes,
                                  Map<String, Location> boxLocationMap, AllocationMode mode,
                                  OrderEligibilityResult result) {
        // Filter boxes by SKU
        List<Box> skuBoxes = filterBoxesBySKU(allBoxes, orderLine.getSku());

        // Allocate
        AllocationResult allocationResult = allocateForOrderLine(orderLine, skuBoxes, boxLocationMap, mode);

        // Create line eligibility
        LineEligibility lineEligibility = new LineEligibility(
                order.getOrderId(),
                orderLine.getLineNo(),
                orderLine.getSku(),
                orderLine.getQty(),
                allocationResult.allocatedQty,
                allocationResult.status
        );
        result.addLineEligibility(lineEligibility);

        // Add allocations if eligible or partial
        AllocationStatus status = allocationResult.status;
        if (status == AllocationStatus.ELIGIBLE ||
                (status == AllocationStatus.PARTIAL && mode == AllocationMode.PARTIAL)) {

            List<Allocation> allocationsWithOrderId = setOrderIdInAllocations(allocationResult.allocations, order.getOrderId());
            result.addAllocations(allocationsWithOrderId);
        }
    }

    /**
     * Allocates inventory for an order line across multiple warehouses.
     *
     * @param orderLine order line that will be allocated
     * @param boxes boxes with the sku requested on the order line
     * @param boxLocationMap map for O(1) box location lookup
     * @param mode mode that was chosen
     */
    private AllocationResult allocateForOrderLine(OrderLine orderLine, List<Box> boxes,
                                                  Map<String, Location> boxLocationMap,
                                                  AllocationMode mode) {
        int requestedQty = orderLine.getQty();
        int remainingQty = requestedQty;
        List<Allocation> allocations = new ArrayList<>();

        for (Box box : boxes) {
            if (remainingQty <= 0) {
                break;
            }

            if (!box.getSku().equals(orderLine.getSku()) || !box.hasAvailableQty()) {
                continue;
            }

            int toAllocate = Math.min(remainingQty, box.getQtyAvailable());
            int actuallyAllocated = box.allocate(toAllocate);

            // Get box location from pre-built map - O(1) lookup instead of O(n³)
            Location location = boxLocationMap.get(box.getBoxId());

            if (location == null) {
                // Box not found in map - should not happen, but handle gracefully
                continue;
            }

            // Create allocation record
            Allocation allocation = new Allocation(
                    null,
                    orderLine.getLineNo(),
                    orderLine.getSku(),
                    actuallyAllocated,
                    box.getBoxId(),
                    location.getWarehouseId(),
                    location.getAisle(),
                    location.getBay()
            );

            if(allocation.getQty() != 0) {
                allocations.add(allocation);
            }

            remainingQty -= actuallyAllocated;
        }

        // Calculate total allocated
        int allocatedQty = requestedQty - remainingQty;

        // Determine status based on mode
        AllocationStatus status = determineStatus(requestedQty, allocatedQty, mode);

        // If UNDISPATCHABLE or STRICT mode with partial allocation, clear allocations
        if (status == AllocationStatus.UNDISPATCHABLE || (mode == AllocationMode.STRICT && allocatedQty < requestedQty)) {
            for (Allocation allocation : allocations) {
                Box box = findBoxById(allocation.getBoxId(), boxes);
                if (box != null) {
                    box.reallocate(allocation.getQty());
                }
            }
            allocatedQty = 0;
            allocations.clear();
        }

        return new AllocationResult(status, allocatedQty, allocations);
    }

    /**
     * Determines allocation status based on mode.
     *
     * @param requestedQty requested quantity of the order line
     * @param allocatedQty quantity available in the box for allocation
     * @param mode mode that was chosen
     */
    private AllocationStatus determineStatus(int requestedQty, int allocatedQty, AllocationMode mode) {
        if (allocatedQty == requestedQty) {
            return AllocationStatus.ELIGIBLE;
        } else if (allocatedQty > 0 && mode == AllocationMode.PARTIAL) {
            return AllocationStatus.PARTIAL;
        } else {
            return AllocationStatus.UNDISPATCHABLE;
        }
    }

    /**
     * Inner class to hold allocation results.
     */
    private static class AllocationResult {
        AllocationStatus status;
        int allocatedQty;
        List<Allocation> allocations;

        /**
         * Constructor of an Allocation Result
         *
         * @param status the status of the allocation
         * @param allocatedQty allocated quantity
         * @param allocations list of allocations
         */
        AllocationResult(AllocationStatus status, int allocatedQty, List<Allocation> allocations) {
            this.status = status;
            this.allocatedQty = allocatedQty;
            this.allocations = allocations;
        }
    }

    /**
     * Filters boxes by SKU.
     *
     * @param boxes all boxes that exist
     * @param sku SKU requested in that order line
     */
    private List<Box> filterBoxesBySKU(List<Box> boxes, String sku) {
        List<Box> filtered = new ArrayList<>();
        for (Box box : boxes) {
            if (box.getSku().equals(sku)) {
                filtered.add(box);
            }
        }
        return filtered;
    }

    /**
     * Finds the box of the given SKU.
     *
     * @param boxId boxID of the box that needs to be found
     * @param boxes boxes with the sku requested on the order line
     */
    private Box findBoxById(String boxId, List<Box> boxes) {
        for (Box box : boxes) {
            if (box.getBoxId().equals(boxId)) {
                return box;
            }
        }
        return null;
    }

    /**
     * Sets orderId in allocations.
     *
     * @param allocations list of allocations
     * @param orderId orderId of the allocation
     */
    private List<Allocation> setOrderIdInAllocations(List<Allocation> allocations, String orderId) {
        List<Allocation> newAllocations = new ArrayList<>();

        for (Allocation allocation : allocations) {
            Allocation newAllocation = new Allocation(
                    orderId,
                    allocation.getLineNo(),
                    allocation.getSku(),
                    allocation.getQty(),
                    allocation.getBoxId(),
                    allocation.getWarehouseId(),
                    allocation.getAisle(),
                    allocation.getBay()
            );
            newAllocations.add(newAllocation);
        }

        return newAllocations;
    }
}