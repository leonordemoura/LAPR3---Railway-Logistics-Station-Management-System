package pt.ipp.isep.dei.ESINF.repository;

import pt.ipp.isep.dei.ESINF.dataStruc.AVL;
import pt.ipp.isep.dei.ESINF.dataStruc.MixedComparator;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.map.MapGraph;

import java.util.*;

/**
 * Repository class for managing warehouse-related data, including items, warehouses, orders, wagons, and returns.
 */
public class WarehouseRepository implements I_WarehouseRepository {
    private Map<String, Item> items;
    private Map<String, Warehouse> warehouses;
    private Map<String, Order> orders;
    private Map<String, Wagon> wagons;
    private List<OrderLine> orderLines;
    private static List<Return> returns;
    // Three AVL trees with custom comparators
    private AVL<Double> latitudeIndex;
    private AVL<Double> longitudeIndex;
    private AVL<String> timeZoneCountryIndex;
    private List<TrainStation> allStations;
    // KD-tree for spatial queries (USEI07)
    private KDTree<TrainStation> kdTreeStation;
    private Graph<Station, Weight> directedGraph;
    private Graph<Station, Weight> undirectedGraph;

    /**
     * Constructs a new instance of {@link WarehouseRepository}.
     * <p>
     * Initializes all internal data structures used to manage the warehouse system:
     * <ul>
     *     <li>{@code items}: a map of SKU to {@link Item} objects</li>
     *     <li>{@code warehouses}: a map of warehouse ID to {@link Warehouse} objects</li>
     *     <li>{@code orders}: a map of order ID to {@link Order} objects</li>
     *     <li>{@code wagons}: a map of wagon ID to {@link Wagon} objects</li>
     *     <li>{@code orderLines}: a list of all {@link OrderLine} objects</li>
     *     <li>{@code returns}: a list of returned {@link Item} objects or boxes</li>
     * </ul>
     * This constructor ensures that the repository is ready to store and manage warehouses,
     * items, orders, and related entities.
     */
    public WarehouseRepository() {
        this.items = new HashMap<>();
        this.warehouses = new HashMap<>();
        this.orders = new HashMap<>();
        this.wagons = new HashMap<>();
        this.orderLines = new ArrayList<>();
        this.returns = new ArrayList<>();
        // Initialize AVL trees with the comparators
        latitudeIndex = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        longitudeIndex = new AVL<>(MixedComparator.MIXED_COMPARATOR);
        timeZoneCountryIndex = new AVL<>(String::compareTo);
        this.allStations = new ArrayList<>();
        this.directedGraph = new MapGraph<>(true);
        this.undirectedGraph = new MapGraph<>(false);
    }

    // ============= ADD METHODS =============

    /**
     * Adds a new item to the system's item repository.
     * <p>
     * The item is stored in a map where the key is the item's SKU, ensuring
     * that each SKU is unique. If an item with the same SKU already exists,
     * this operation will overwrite it.
     * </p>
     *
     * @param item the {@link Item} to add; must not be {@code null}
     * @return {@code true} if the item was successfully added
     * @throws NullPointerException if {@code item} is {@code null}
     *
     * @see Item#getSku()
     */
    public boolean addItem(Item item) {
        items.put(item.getSku(), item);
        return true;
    }

    /**
     * Adds a new warehouse to the system's warehouse repository.
     * <p>
     * The warehouse is stored in a map where the key is the warehouse's unique ID.
     * If a warehouse with the same ID already exists, this operation will overwrite it.
     * </p>
     *
     * @param warehouse the {@link Warehouse} to add; must not be {@code null}
     * @return {@code true} if the warehouse was successfully added
     * @throws NullPointerException if {@code warehouse} is {@code null}
     *
     * @see Warehouse#getWarehouseId()
     */
    public boolean addWarehouse(Warehouse warehouse) {
        warehouses.put(warehouse.getWarehouseId(), warehouse);
        return true;
    }

    /**
     * Adds a new aisle to a specified warehouse.
     * <p>
     * The aisle is added to the warehouse identified by {@code warehouseId}.
     * If the warehouse does not exist, the method returns {@code false}.
     * </p>
     *
     * @param warehouseId the unique identifier of the warehouse where the aisle will be added; must not be {@code null}
     * @param aisle the {@link Aisle} object to add; must not be {@code null}
     * @return {@code true} if the aisle was successfully added to the warehouse,
     *         {@code false} if the warehouse with the given ID does not exist
     *
     * @see Warehouse#addAisle(Aisle)
     * @see Warehouse#getAisles()
     */
    public boolean addAisle(String warehouseId, Aisle aisle) {
        Warehouse warehouse = warehouses.get(warehouseId);
        if (warehouse != null) {
            warehouse.addAisle(aisle);
            return true;
        }
        return false;
    }

    /**
     * Adds a new {@link Bay} to a specific {@link Aisle} within a {@link Warehouse}.
     * <p>
     * This method locates the warehouse and aisle by their identifiers, creates a new bay
     * with the given bay number and capacity, and inserts it into the aisle.
     *
     * @param warehouseId the ID of the warehouse where the bay will be added
     * @param aisleNumber the number of the aisle within the warehouse
     * @param bayNumber   the number of the bay to create
     * @param capacity    the maximum number of boxes that the bay can hold
     * @return {@code true} if the bay was successfully added;
     *         {@code false} if the warehouse or aisle does not exist
     */
    public boolean addBay(String warehouseId, int aisleNumber, int bayNumber, int capacity) {
        Warehouse warehouse = warehouses.get(warehouseId);
        if (warehouse != null) {
            Aisle aisle = warehouse.getAisle(aisleNumber);
            if (aisle != null) {
                Bay bay = new Bay(bayNumber, capacity);
                aisle.addBay(bay);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a {@link Box} to a specific {@link Bay} within an {@link Aisle} of a {@link Warehouse}.
     * <p>
     * This method locates the warehouse, aisle, and bay by their identifiers.
     * If all exist, the box is added to the bay.
     * <p>
     * The box is stored directly in the bay's collection and can later be retrieved
     * in its stored order (or in an ordered list if the bay maintains ordering logic).
     *
     * @param box          the {@link Box} object to add
     * @param warehouseId  the ID of the warehouse where the box will be stored
     * @param aisle        the number of the aisle within the warehouse
     * @param bay          the number of the bay within the aisle
     * @return {@code true} if the box was successfully added;
     *         {@code false} if the warehouse, aisle, or bay does not exist
     */
    public boolean addBox(Box box, String warehouseId, int aisle, int bay) {
        Warehouse warehouse = warehouses.get(warehouseId);
        if (warehouse != null) {
            Aisle aisleObj = warehouse.getAisle(aisle);
            if (aisleObj != null) {
                Bay bayObj = aisleObj.getBay(bay);
                if (bayObj != null) {
                    bayObj.addBox(box);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds an order to the repository.
     * @param order The order to add.
     * @return True if the order was added successfully.
     */
    public boolean addOrder(Order order) {
        orders.put(order.getOrderId(), order);
        return true;
    }

    /**
     * Adds an order line to the repository.
     * @param line The order line to add.
     * @return True if the order line was added successfully.
     */
    public boolean addOrderLine(OrderLine line) {
        orderLines.add(line);
        return true;
    }

    /**
     * Adds a return to the repository.
     * @param ret The return to add.
     * @return True if the return was added successfully.
     */
    public boolean addReturn(Return ret) {
        returns.add(ret);
        return true;
    }

    /**
     * Adds a wagon to the repository.
     * @param wagon The wagon to add.
     * @return True if the wagon was added successfully.
     */
    public boolean addWagon(Wagon wagon) {
        if (wagon != null && wagon.getWagonId() != null) {
            wagons.put(wagon.getWagonId(), wagon);
        }
        return true;
    }

    /**
     * Adiciona uma estação de comboio às estruturas da aplicação.
     *
     * Cada estação é inserida em três AVL trees:
     * 1) latitudeIndex: AVL por latitude → inner AVL de estações
     * 2) longitudeIndex: AVL por longitude → inner AVL de estações
     * 3) timeZoneCountryIndex: AVL por timezone -> AVL por país -> inner AVL de estações
     *
     * Complexidade temporal:
     * - Cada insertAVL percorre h níveis da hierarquia, cada nível custando O(log n),
     *   onde n é o número de nós naquele nível.
     * - Para hierarquias curtas (2 ou 3 níveis), o custo é O(h * log n), constante na prática.
     * - Como fazemos 3 inserções separadas, o custo total ainda é O(h_total * log n),
     *   com h_total ≈ 7 no exemplo das 3 árvores.
     *
     * Uso de Object[]:
     * - Utilizamos Object[] para representar uma hierarquia de chaves de forma genérica.
     * - Ex: [latitude, nomeEstacao] ou [timezone, country, nomeEstacao]
     * - Isso permite que insertAVL funcione de forma recursiva para qualquer nível da hierarquia.
     */
    public boolean addTrainStation(TrainStation trainStation) {
        if (trainStation == null) {
            return false;
        }

        try {
            allStations.add(trainStation);
            // Insert into all three AVL trees

                latitudeIndex.insertAVL(new Object[]{trainStation.getCoordinates().getLatitude(), trainStation.getStation()}, trainStation);
                longitudeIndex.insertAVL(new Object[]{trainStation.getCoordinates().getLongitude(), trainStation.getStation()}, trainStation);
                timeZoneCountryIndex.insertAVL(new String[]{trainStation.getTimeZoneGroup(), trainStation.getCountry(), trainStation.getStation()}, trainStation);


            return true;
        } catch (Exception e) {
            System.err.println("Error adding train station: " + e.getMessage());
            return false;
        }
    }

    public AVL<Double> getLatitudeIndex() {
        return latitudeIndex;
    }

    public AVL<Double> getLongitudeIndex() {
        return longitudeIndex;
    }

    public AVL<String> getTimeZoneCountryIndex() {
        return timeZoneCountryIndex;
    }

    public List<TrainStation> getTrainStations() {
        return allStations;
    }

    /**
     * Gets the KD-tree for train stations.
     * If the tree hasn't been built yet, it will be built automatically.
     * This connects USEI07 (2D-tree build) with USEI08 (geographical search).
     *
     * @return the KDTreeStation instance, or null if no stations are available
     */
    public KDTree<TrainStation> getKDTreeStation() {
        return kdTreeStation;
    }

    public void setKDTreeStation(KDTree<TrainStation> tree) {
        this.kdTreeStation = tree;
    }


    // ============= CHECK METHODS =============

    /**
     * Checks if an item exists in the repository.
     * @param sku The SKU of the item.
     * @return True if the item exists, false otherwise.
     */
    public boolean itemExists(String sku) {
        return items.containsKey(sku);
    }

    /**
     * Deletes a box with the given ID from all warehouses, aisles, and bays.
     * <p>
     * The method iterates over all warehouses, aisles, and bays, searching
     * for a box that matches the provided {@code boxId}. Once found, it removes
     * the box from the bay.
     *
     * @param boxId the unique identifier of the box to remove
     * @return {@code true} if the box was found and removed; {@code false} if no box with the given ID exists
     */
    public boolean deleteBox(String boxId) {
        for (Warehouse warehouse : warehouses.values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                for (Bay bay : aisle.getBays().values()) {
                    if (bay.getBox(boxId) != null) {
                        // Found the box, remove it
                        bay.removeBox(boxId);
                        System.out.println("Removed box!");
                    }
                }
            }
        }

        System.out.println("Failed to remove box: " + boxId);
        return false;
    }

    /**
     * Checks if an order exists in the repository.
     * @param orderId The ID of the order.
     * @return True if the order exists, false otherwise.
     */
    public boolean orderExists(String orderId) {
        return orders.containsKey(orderId);
    }

    /**
     * Checks if a wagon exists in the repository.
     * @param wagonId The ID of the wagon.
     * @return True if the wagon exists, false otherwise.
     */
    public boolean wagonExists(String wagonId) {
        return wagons.containsKey(wagonId);
    }

    /**
     * Checks if there is any structure registered for a given warehouse id.
     */
    public boolean warehouseExists(String warehouseId) {
        return warehouseId != null && warehouses.containsKey(warehouseId);
    }

    /**
     * Checks if an aisle exists within a given warehouse.
     */
    public boolean aisleExists(String warehouseId, int aisle) {
        Warehouse warehouse = warehouses.get(warehouseId);
        return warehouse != null && warehouse.getAisle(aisle) != null;
    }

    /**
     * Checks if a specific bay exists within a given warehouse and aisle.
     */
    public boolean bayExists(String warehouseId, int aisle, int bay) {
        Aisle aisleObj = getAisle(warehouseId, aisle);
        return aisleObj != null && aisleObj.getBay(bay) != null;
    }

    /**
     * Returns true if the destination bay exists and its number of boxes has
     * reached the bay capacity; false otherwise. If the bay does not exist,
     * this method returns false.
     */
    public boolean isBayFull(String warehouseId, int aisle, int bay) {
        Bay bayObj = getBay(warehouseId, aisle, bay);
        if (bayObj == null) return false;
        return bayObj.getCurrentBoxCount() >= bayObj.getCapacityBoxes();
    }

    /**
     * Checks whether a box with the given unique identifier exists in any warehouse.
     * <p>
     * This method iterates through all warehouses, their aisles, and bays,
     * and examines every box stored in each bay. If a box with a matching {@code boxId}
     * is found, the method returns {@code true}, indicating that the box already exists.
     * </p>
     *
     * @param boxId the unique identifier of the box to check
     * @return {@code true} if a box with the given ID exists in any warehouse, {@code false} otherwise
     * @throws NullPointerException if {@code boxId} is {@code null}
     *
     * @see Warehouse
     * @see Aisle
     * @see Bay
     * @see Box
     * @see Bay#getOrderedBoxes()
     */
    public boolean boxIdExists(String boxId) {
        for (Warehouse warehouse : warehouses.values()) {                 // percorre todas as warehouses
            for (Aisle aisle : warehouse.getAisles().values()) {          // percorre todos os aisles
                for (Bay bay : aisle.getBays().values()) {                // percorre todas as bays
                    for (Box box : bay.getOrderedBoxes()) {               // percorre todas as boxes dessa bay
                        if (box.getBoxId().equals(boxId)) {
                            return true; // encontrou -> existe
                        }
                    }
                }
            }
        }
        return false; // não encontrou -> não existe
    }


    // ============= GETTERS =============

    /**
     * Gets all items in the repository.
     * @return A map of items keyed by SKU.
     */
    public Map<String, Item> getItems() { return items; }
    public Map<String, Warehouse> getWarehouses() { return warehouses; }

    /**
     * Gets all wagons in the repository.
     * @return A map of wagons keyed by wagon ID.
     */
    public Map<String, Wagon> getWagons() { return wagons; }

    /**
     * Gets all orders in the repository.
     * @return A map of orders keyed by order ID.
     */
    public Map<String, Order> getOrders() { return orders; }

    /**
     * Gets all order lines in the repository.
     * @return A list of order lines.
     */
    public List<OrderLine> getOrderLines() { return orderLines; }

    /**
     * Gets all returns in the repository.
     * @return A list of returns.
     */
    public static List<Return> getReturns() { return returns; }


    /**
     * Gets the total count of bays across all warehouses.
     * @return The total number of bays.
     */
    public int getTotalBayCount() {
        int count = 0;
        for (Warehouse warehouse : warehouses.values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                count += aisle.getBays().size();
            }
        }
        return count;
    }

    /**
     * Gets a wagon by its ID.
     * @param wagonId The ID of the wagon.
     * @return The wagon, or null if not found.
     */
    public Wagon getWagonById(String wagonId) {
        return wagons.get(wagonId);
    }

    /**
     * Gets a warehouse by its ID.
     * @param warehouseId The ID of the warehouse.
     * @return The warehouse, or null if not found.
     */
    public Warehouse getWarehouse(String warehouseId) {
        return warehouses.get(warehouseId);
    }

    /**
     * Gets an aisle from a specified warehouse.
     * @param warehouseId The ID of the warehouse.
     * @param aisleNumber The aisle number.
     * @return The aisle, or null if not found.
     */
    public Aisle getAisle(String warehouseId, int aisleNumber) {
        Warehouse warehouse = warehouses.get(warehouseId);
        return warehouse != null ? warehouse.getAisle(aisleNumber) : null;
    }

    /**
     * Gets a bay from a specified aisle and warehouse.
     * @param warehouseId The ID of the warehouse.
     * @param aisleNumber The aisle number.
     * @param bayNumber The bay number.
     * @return The bay, or null if not found.
     */
    public Bay getBay(String warehouseId, int aisleNumber, int bayNumber) {
        Aisle aisle = getAisle(warehouseId, aisleNumber);
        return aisle != null ? aisle.getBay(bayNumber) : null;
    }

    /**
     * Retrieves a box by its unique identifier from all warehouses, aisles, and bays.
     * <p>
     * The method iterates through all warehouses, each aisle within the warehouse,
     * and each bay within the aisle, checking all boxes in each bay.
     * If a box with the matching {@code boxId} is found, it is returned immediately.
     *
     * @param boxId the unique identifier of the box to retrieve
     * @return the {@link Box} object if found; {@code null} if no box with the given ID exists
     */
    public Box getBox(String boxId) {
        for (Warehouse warehouse : warehouses.values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                for (Bay bay : aisle.getBays().values()) {
                    for (Box box : bay.getOrderedBoxes()) {
                        if (box.getBoxId().equals(boxId)) {
                            return box; // Encontrou, devolve
                        }
                    }
                }
            }
        }
        return null; // Não encontrado
    }

    // ============= INVENTORY HELPERS =============

    /**
     * Retrieves all boxes stored across all warehouses, aisles, and bays.
     * <p>
     * This method iterates through the complete hierarchy of warehouses, aisles, and bays
     * to collect every {@link Box} currently stored in the system. The boxes are returned
     * in the order they are stored within each bay, preserving any internal ordering (e.g., FEFO/FIFO).
     * </p>
     *
     * @return a {@link List} containing all boxes across all warehouses
     * @throws IllegalArgumentException if no warehouses exist in the repository
     *
     * @see Warehouse
     * @see Aisle
     * @see Bay
     * @see Box
     * @see #getBoxesInBay(String, int, int)
     */
    public List<Box> getAllBoxes() {
        Map<String, Warehouse> warehouses = getWarehouses();

        if (warehouses.isEmpty()) {
            throw new IllegalArgumentException("No warehouses available.");
        }
        List<Box> allBoxes = new ArrayList<>();

        for (Warehouse warehouse : warehouses.values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                for (Bay bay : aisle.getBays().values()) {
                    List<Box> boxes = getBoxesInBay(warehouse.getWarehouseId(), aisle.getAisleNumber(), bay.getBay());
                    allBoxes.addAll(boxes);
                }
            }
        }
        return allBoxes;
    }

    /**
     * Removes a box from its current bay and from the warehouse management system.
     */
    public void removeBoxFromBay(Box box, String warehouseId, int aisle, int bay) {

        Warehouse warehouse = warehouses.get(warehouseId);
        if (warehouse != null) {
            Aisle aisleObj = warehouse.getAisle(aisle);
            if (aisleObj != null) {
                Bay bayObj = aisleObj.getBay(bay);
                if (bayObj != null) {
                    bayObj.removeBox(box.getBoxId());
                }
            }
        }
    }

    /**
     * Relocates a box from its current bay to a new warehouse location.
     */
    public void relocateBox(Box box, String newKey) {
        // Parse the new key to get warehouse, aisle, bay
        String[] parts = newKey.split("-");
        String warehouseId = parts[0];
        int aisle = Integer.parseInt(parts[1]);
        int bay = Integer.parseInt(parts[2]);

        // Add to hierarchical structure
        Warehouse warehouse = warehouses.get(warehouseId);
        if (warehouse != null) {
            Aisle aisleObj = warehouse.getAisle(aisle);
            if (aisleObj != null) {
                Bay bayObj = aisleObj.getBay(bay);
                if (bayObj != null) {
                    bayObj.addBox(box);
                }
            }
        }
    }

    /**
     * Retrieves all boxes stored in a specific bay of a warehouse.
     * <p>
     * The method first locates the specified bay using the given warehouse ID, aisle number, and bay number.
     * If the bay exists, it returns a list containing all boxes in that bay.
     * The returned list is a copy of the internal collection, so modifications to it
     * do not affect the internal storage of the bay.
     *
     * @param warehouseId the ID of the warehouse containing the bay
     * @param aisle       the aisle number within the warehouse
     * @param bay         the bay number within the aisle
     * @return a {@link List} of {@link Box} objects in the specified bay;
     *         returns an empty list if the bay does not exist or contains no boxes
     */
    public List<Box> getBoxesInBay(String warehouseId, int aisle, int bay) {
        Bay bayObj = getBay(warehouseId, aisle, bay);
        if (bayObj != null) {
            List<Box> boxes = new ArrayList<>(bayObj.getBoxes().values());
            //boxes.sort(InventoryServiceImpl::compareBoxesForBayOrder);
            return boxes;
        }
        return new ArrayList<>();
    }

    /**
     * Set Directed Graph
     */
    public void setDirectedGraph(Graph<Station, Weight> directedGraph) {
        this.directedGraph = directedGraph;
    }

    /**
     * Get Directed Graph
     */
    public Graph<Station, Weight> getDirectedGraph() {
        return directedGraph;
    }

    /**
     * Returns a list of stations present in the directed graph.
     * If the directed graph is null, returns an empty list.
     * changes to this list do not affect the internal graph.
     * The order of the stations follows the order returned by directedGraph.vertices().
     *
     * @return list of stations in the directed graph or empty list if the graph is null
     */
    public List<Station> getStationsDirectedGraph() {
        if (directedGraph == null) {
            return new ArrayList<>();
        }
        List<Station> stations = new ArrayList<>();
        for (Station s : directedGraph.vertices()) {
            stations.add(s);
        }
        return stations;
    }


    /**
     * Set Undirected Graph
     */
    public void setUndirectedGraph(Graph<Station, Weight> undirectedGraph) {
        this.undirectedGraph = undirectedGraph;
    }

    /**
     * Get Undirected Graph
     */
    public Graph<Station, Weight> getUndirectedGraph() {
        return undirectedGraph;
    }
}