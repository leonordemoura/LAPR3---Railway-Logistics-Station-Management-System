package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Aisle;
import pt.ipp.isep.dei.ESINF.domain.Bay;
import pt.ipp.isep.dei.ESINF.domain.Box;
import pt.ipp.isep.dei.ESINF.domain.Warehouse;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.InventoryService;

import java.util.*;

/**
 * USEI01 — Wagons Unloading (Inventory replenishment)
 * <p>
 * Service implementation that does all the inventory related actions.
 * </p>
 */
public class InventoryServiceImpl implements InventoryService {

    private final WarehouseRepository repository;

    public InventoryServiceImpl(WarehouseRepository repository) {
        this.repository = repository;
    }

    /**
     * Unloads a box from a wagon and stores it in the specified warehouse location.
     * This operation is part of the wagon unloading process (AC1) and performs the following:
     * <ol>
     *   <li>Assigns the box to the specified warehouse, aisle, and bay</li>
     *   <li>Adds the box to the repository in the correct FEFO/FIFO position</li>
     * </ol>
     *
     * @param box         the box to unload and store
     * @param warehouseId the destination warehouse identifier
     * @param aisle       the destination aisle number
     * @param bay         the destination bay number
     * @throws NullPointerException     if box or warehouseId is null
     * @throws IllegalArgumentException if the specified bay does not exist (thrown by repository)
     * @see WarehouseRepository
     */
    @Override
    public boolean unloadBox(Box box, String warehouseId, int aisle, int bay) {
        if (repository.addBox(box, warehouseId, aisle, bay)){
            System.out.println("box added");
            return true;
        }
        System.out.println("box not added");
        return false;
    }

    /**
     * Compares two boxes to determine their order within a bay according to FEFO/FIFO rules.
     * <p>
     * The comparison follows a three-level priority system:
     * <ol>
     *   <li><b>Expiry Date (FEFO)</b>: Boxes with earlier expiry dates come first.
     *       Boxes without expiry dates (null) are placed last to prioritize perishable goods.</li>
     *   <li><b>Received Date (FIFO)</b>: If expiry dates are equal (or both null),
     *       boxes received earlier come first.</li>
     *   <li><b>Box ID</b>: If both expiry and received dates are equal, boxes are ordered
     *       alphabetically by their unique box ID for deterministic ordering.</li>
     * </ol>
     * <p>
     * This comparator ensures that:
     * <ul>
     *   <li>Perishable goods closest to expiration are dispatched first (minimizing waste)</li>
     *   <li>Non-perishable goods follow a fair rotation based on arrival time</li>
     *   <li>All ordering decisions are deterministic and reproducible</li>
     * </ul>
     *
     * @param a the first box to compare
     * @param b the second box to compare
     * @return a negative integer if {@code a} should come before {@code b},
     *         or a positive integer if {@code a} should come after {@code b}
     * @throws NullPointerException if either box, their receivedAt date, or boxId is null
     * @see Box#getExpiryDate()
     * @see Box#getReceivedAt()
     * @see Box#getBoxId()
     */
    // FEFO/FIFO comparator: expiry asc (null last), receivedAt asc, boxId asc
    public static int compareBoxesForBayOrder(Box a, Box b) {
        Date aExp = a.getExpiryDate();
        Date bExp = b.getExpiryDate();
        if (aExp == null && bExp != null) return 1;
        if (aExp != null && bExp == null) return -1;
        if (aExp != null && bExp != null) {
            int c = aExp.compareTo(bExp);
            if (c != 0) return c;
        }
        // c2 guarda o resultado da comparação das datas
        int c2 = a.getReceivedAt().compareTo(b.getReceivedAt());
        if (c2 != 0) return c2;
        return a.getBoxId().compareTo(b.getBoxId());
    }

    /**
     * Dispatches a specified quantity of products identified by SKU from a warehouse.
     *
     * <p>
     * <b>Multi-Bay Support:</b> If the requested quantity exceeds what's available in a single bay,
     * the dispatch operation automatically continues to subsequent bays (in ascending order) that
     * contain the same SKU until the order is fulfilled or stock is exhausted.
     * <p>
     * <b>Partial Fulfillment:</b> If insufficient stock is available, the method dispatches
     * as much as possible and returns the actual dispatched quantity (which may be less than requested).
     *
     * @param sku         the Stock Keeping Unit identifier of the product to dispatch
     * @param qty         the requested quantity to dispatch
     * @param warehouseId the warehouse identifier from which to dispatch
     * @return the actual quantity dispatched (0 ≤ result ≤ qty); may be less than requested
     *         if insufficient stock is available
     * @throws NullPointerException if sku or warehouseId is null
     * @see WarehouseRepository
     * @see Box#reduceQuantity(int)
     * @see Box#isEmpty()
     */
    @Override
    public int dispatch(String sku, int qty, String warehouseId) {
        return dispatch(sku, qty, warehouseId, false);
    }

    public int dispatch(String sku, int qty, String warehouseId, boolean verbose) {
        int remaining = qty;
        int totalDispatched = 0;
        List<String> dispatchLog = new ArrayList<>();

        // Get warehouse and iterate through its structure
        Warehouse warehouse = repository.getWarehouse(warehouseId);
        if (warehouse == null) {
            if (verbose) {
                System.out.println("Warehouse " + warehouseId + " not found");
            }
            return 0;
        }

        for (Aisle aisle : warehouse.getAisles().values()) {
            if (remaining == 0) break;
            
            for (Bay bay : aisle.getBays().values()) {
                if (remaining == 0) break;
                
                // Use orderedBoxes which is already sorted by FIFO/FEFO
                List<Box> boxes = bay.getOrderedBoxes().stream()
                    .filter(box -> Objects.equals(box.getSku(), sku))
                    .sorted(InventoryServiceImpl::compareBoxesForBayOrder)
                    .collect(java.util.stream.Collectors.toList());
                
                List<Box> toRemove = new ArrayList<>();

                for (Box box : boxes) {
                    if (remaining == 0) break;

                    int take = Math.min(remaining, box.getQtyAvailable());
                    int beforeQty = box.getQtyAvailable();

                    box.reduceQuantity(take);
                    remaining -= take;
                    totalDispatched += take;

                    // Log dispatch details if verbose
                    if (verbose) {
                        String status = box.isEmpty() ? "[EMPTIED]" : String.format("[%d left]", box.getQtyAvailable());
                        dispatchLog.add(String.format("Box: %s | Aisle %d, Bay %d | Dispatched: %d units %s",
                                box.getBoxId(), aisle.getAisleNumber(), bay.getBay(), take, status));
                    }

                    if (box.isEmpty()) {
                        toRemove.add(box);
                    }
                }

                // Remove empty boxes from bay
                for (Box box : toRemove) {
                    bay.removeBox(box.getBoxId());
                    repository.getAllBoxes().remove(box.getBoxId());
                }
            }
        }

        // Print detailed report if verbose
        if (verbose) {
            printDispatchReport(sku, qty, totalDispatched, warehouseId, dispatchLog);
        }

        return totalDispatched;
    }

    /**
     * Prints a formatted dispatch report to the console.
     *
     * @param sku            the SKU that was dispatched
     * @param requested      the quantity requested
     * @param dispatched     the quantity actually dispatched
     * @param warehouseId    the warehouse ID
     * @param dispatchLog    detailed log entries for each box
     */
    private void printDispatchReport(String sku, int requested, int dispatched, String warehouseId, List<String> dispatchLog) {
        System.out.println("\n========== DISPATCH REPORT ==========");

        if (dispatched == 0) {
            System.out.printf("No stock available for SKU '%s' in warehouse '%s'\n", sku, warehouseId);
        } else if (dispatched < requested) {
            System.out.printf("PARTIAL DISPATCH: Requested %d units, but only %d were available\n",
                    requested, dispatched);
            System.out.printf("Missing: %d units\n", requested - dispatched);
        } else {
            System.out.printf("Successfully dispatched %d units of '%s' from warehouse '%s'\n",
                    dispatched, sku, warehouseId);
        }

        if (!dispatchLog.isEmpty()) {
            System.out.println("\n--- Dispatch Details ---");
            dispatchLog.forEach(System.out::println);
        }

        System.out.println("====================================\n");
    }

    /**
     * Relocates a box to a new warehouse location by its unique identifier.
     * <p>
     * This method implements the relocation operation (AC3) by:
     * <ol>
     *   <li>Retrieving the box from the repository using its unique boxId</li>
     *   <li>Validating that the box exists in the system</li>
     *   <li>Delegating the physical relocation to the repository</li>
     * </ol>
     * <p>
     * <b>FEFO/FIFO Preservation:</b> The box is removed from its current bay and inserted
     * into the destination bay at the correct FEFO/FIFO position based on its expiry date
     * and received date. The ordering in both source and destination bays is maintained.
     * </p>
     *
     * @param boxId          the unique identifier of the box to relocate
     * @param newWarehouseId the destination warehouse identifier
     * @param newAisle       the destination aisle number
     * @param newBay         the destination bay number
     * @throws IllegalArgumentException if the boxId does not exist in the system
     *                                  or if the destination bay does not exist (thrown by repository)
     * @throws NullPointerException     if boxId or newWarehouseId is null
     * @see WarehouseRepository#relocateBox(Box, String)
     */
    public void relocate(String boxId, String newWarehouseId, int newAisle, int newBay) {
        // Get the box first
        Box box = repository.getBox(boxId);
        
        if (box == null) {
            throw new IllegalArgumentException("Box with ID " + boxId + " does not exist");
        }

        // Find the current location of the box
        String currentLocation = findBoxLocation(boxId);
        if (currentLocation == null) {
            throw new IllegalArgumentException("Box with ID " + boxId + " is not in any bay");
        }

        // Remove box from old location
        String[] parts = currentLocation.split("-");
        String oldWarehouseId = parts[0];
        int oldAisle = Integer.parseInt(parts[1]);
        int oldBay = Integer.parseInt(parts[2]);
        
        repository.removeBoxFromBay(box, oldWarehouseId, oldAisle, oldBay);

        // Add box to new location
        String newKey = newWarehouseId + "-" + newAisle + "-" + newBay;
        repository.relocateBox(box, newKey);
    }

    /**
     * Finds the current location of a box in the warehouse.
     */
    private String findBoxLocation(String boxId) {
        for (Warehouse warehouse : repository.getWarehouses().values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                for (Bay bay : aisle.getBays().values()) {
                    if (bay.getBox(boxId) != null) {
                        return warehouse.getWarehouseId() + "-" + aisle.getAisleNumber() + "-" + bay.getBay();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all boxes stored in a specific bay of a warehouse.
     *
     * @param warehouseId the ID of the warehouse
     * @param aisle       the aisle number within the warehouse
     * @param bay         the bay number within the aisle
     * @return a list of boxes in the specified bay; returns an empty list if
     *         the bay does not exist or contains no boxes
     * @throws NullPointerException if {@code warehouseId} is null
     */
    public List<Box> getBoxesInBay(String warehouseId, int aisle, int bay) {
        return new ArrayList<>(repository.getBoxesInBay(warehouseId, aisle, bay));
    }
}



