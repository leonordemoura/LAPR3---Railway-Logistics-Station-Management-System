package pt.ipp.isep.dei.ESINF.repository;

import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.graph.Graph;

import java.util.List;
import java.util.Map;

/**
 * Interface defining operations for managing warehouses, aisles, bays, boxes, items, orders, wagons, and returns.
 * Provides methods to add, retrieve, check existence, and delete entities in the warehouse system.
 */
public interface I_WarehouseRepository {

    /**
     * Retrieves all boxes stored in a specific bay.
     *
     * @param warehouseId the warehouse ID
     * @param aisle       the aisle number
     * @param bay         the bay number
     * @return a list of boxes in the specified bay; empty list if the bay does not exist or has no boxes
     */
    public List<Box> getBoxesInBay(String warehouseId, int aisle, int bay);

    /**
     * Adds an item to the repository.
     *
     * @param item the item to add
     * @return true if added successfully
     */
    public boolean addItem(Item item);

    /**
     * Adds a bay to a specific warehouse and aisle.
     *
     * @param warehouseId the warehouse ID
     * @param aisleNumber the aisle number
     * @param bayNumber   the bay number
     * @param capacity    the maximum number of boxes in the bay
     * @return true if added successfully
     */
    public boolean addBay(String warehouseId, int aisleNumber, int bayNumber, int capacity);

    /**
     * Adds a wagon to the repository.
     *
     * @param wagon the wagon to add
     * @return true if added successfully
     */
    public boolean addWagon(Wagon wagon);

    /**
     * Adds a box to a specific bay in a warehouse.
     *
     * @param box         the box to add
     * @param warehouseId the warehouse ID
     * @param aisle       the aisle number
     * @param bay         the bay number
     * @return true if added successfully
     */
    public boolean addBox(Box box, String warehouseId, int aisle, int bay);

    /**
     * Adds an order to the repository.
     *
     * @param order the order to add
     * @return true if added successfully
     */
    public boolean addOrder(Order order);

    /**
     * Adds an order line to the repository.
     *
     * @param orderLine the order line to add
     * @return true if added successfully
     */
    public boolean addOrderLine(OrderLine orderLine);

    /**
     * Adds a return to the repository.
     *
     * @param ret the return to add
     * @return true if added successfully
     */
    public boolean addReturn(Return ret);

    /**
     * Adds a warehouse to the repository.
     *
     * @param warehouse the warehouse to add
     * @return true if added successfully
     */
    public boolean addWarehouse(Warehouse warehouse);

    /**
     * Adds an aisle to a specific warehouse.
     *
     * @param warehouseId the warehouse ID
     * @param aisle       the aisle to add
     * @return true if added successfully
     */
    public boolean addAisle(String warehouseId, Aisle aisle);

    /**
     * Retrieves all warehouses.
     *
     * @return a map of warehouses keyed by warehouse ID
     */
    public Map<String, Warehouse> getWarehouses();

    /**
     * Checks if an order exists in the repository.
     *
     * @param orderId the order ID
     * @return true if the order exists
     */
    public boolean orderExists(String orderId);

    /**
     * Checks if a warehouse exists in the repository.
     *
     * @param warehouseId the warehouse ID
     * @return true if the warehouse exists
     */
    public boolean warehouseExists(String warehouseId);

    /**
     * Checks if an aisle exists in a specific warehouse.
     *
     * @param warehouseId the warehouse ID
     * @param aisle       the aisle number
     * @return true if the aisle exists
     */
    public boolean aisleExists(String warehouseId, int aisle);

    /**
     * Checks if a bay exists in a specific aisle and warehouse.
     *
     * @param warehouseId the warehouse ID
     * @param aisle       the aisle number
     * @param bay         the bay number
     * @return true if the bay exists
     */
    public boolean bayExists(String warehouseId, int aisle, int bay);

    /**
     * Checks if an item exists in the repository.
     *
     * @param itemId the item SKU
     * @return true if the item exists
     */
    public boolean itemExists(String itemId);

    /**
     * Deletes a box by its ID.
     *
     * @param boxId the box ID
     * @return true if deleted successfully
     */
    public boolean deleteBox(String boxId);

    /**
     * Retrieves a warehouse by its ID.
     *
     * @param warehouseId the warehouse ID
     * @return the warehouse or null if not found
     */
    public Warehouse getWarehouse(String warehouseId);

    /**
     * Retrieves an aisle by its number and warehouse.
     *
     * @param warehouseId the warehouse ID
     * @param aisleNumber the aisle number
     * @return the aisle or null if not found
     */
    public Aisle getAisle(String warehouseId, int aisleNumber);

    /**
     * Retrieves a bay by its number, aisle, and warehouse.
     *
     * @param warehouseId the warehouse ID
     * @param aisleNumber the aisle number
     * @param bayNumber   the bay number
     * @return the bay or null if not found
     */
    public Bay getBay(String warehouseId, int aisleNumber, int bayNumber);

    /**
     * Retrieves a box by its ID.
     *
     * @param boxId the box ID
     * @return the box or null if not found
     */
    public Box getBox(String boxId);

    /**
     * Checks if a box with the given ID exists.
     *
     * @param boxId the box ID
     * @return true if the box exists
     */
    public boolean boxIdExists(String boxId);

    /**
     * Adds a train station to the repository.
     * @param station the train station to add
     * @return true if the station was added successfully
     */
    boolean addTrainStation(TrainStation station);

    /**
     * Set Directed Graph
     */
    public void setDirectedGraph(Graph<Station, Weight> directedGraph);

    /**
     * Get Directed Graph
     */
    public Graph<Station, Weight> getDirectedGraph();

    /**
     * Set Undirected Graph
     */
    public void setUndirectedGraph(Graph<Station, Weight> undirectedGraph);

    /**
     * Get Undirected Graph
     */
    public Graph<Station, Weight> getUndirectedGraph();
}
