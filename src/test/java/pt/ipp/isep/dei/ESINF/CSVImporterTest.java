package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVImporterTest {

    @TempDir
    Path tempDir; // para nao ter que criar ficheiros CSV reais

    private WarehouseRepository repository;
    private CSVImporter importer;

    @BeforeEach
    void setUp() {
        repository = new WarehouseRepository();
        importer = new CSVImporter(repository);
    }

    @AfterEach
    void tearDown() {
        importer.printSummary();
    }

    // ==================== ITEMS TESTS ====================

    @Test
    void testImportItems_ValidRecords() throws IOException {
        Path itemsFile = createItemsCSV(
                "SKU,Name,Category,Unit,Volume,UnitWeight",
                "SKU001,Product A,Category1,kg,1.5,2.0",
                "SKU002,Product B,Category2,L,3.0,1.5"
        );

        importer.importItems(itemsFile.toString());

        assertTrue(repository.itemExists("SKU001"));
        assertTrue(repository.itemExists("SKU002"));
        Item item = repository.getItems().get("SKU001");
        assertNotNull(item);
        Item item2 = repository.getItems().get("SKU002");
        assertNotNull(item2);
        assertEquals("Product A", item.getName());
        assertEquals("Product B", item2.getName());
    }

    @Test
    void testImportItems_EmptySKU() throws IOException {
        Path itemsFile = createItemsCSV(
                "SKU,Name,Category,Unit,Volume,UnitWeight",
                ",Product A,Category1,kg,1.5,2.0",
                "SKU002,Product B,Category2,L,3.0,1.5"
        );

        importer.importItems(itemsFile.toString());

        assertFalse(repository.itemExists(""));
        assertTrue(repository.itemExists("SKU002"));
    }

    @Test
    void testImportItems_NegativeVolume() throws IOException {
        Path itemsFile = createItemsCSV(
                "SKU,Name,Category,Unit,Volume,UnitWeight",
                "SKU001,Product A,Category1,kg,-1.5,2.0",
                "SKU002,Product B,Category2,L,3.0,1.5"
        );

        importer.importItems(itemsFile.toString());

        assertFalse(repository.itemExists("SKU001"));
        assertTrue(repository.itemExists("SKU002"));
    }

    @Test
    void testImportItems_NegativeWeight() throws IOException {
        Path itemsFile = createItemsCSV(
                "SKU,Name,Category,Unit,Volume,UnitWeight",
                "SKU001,Product A,Category1,kg,1.5,-2.0",
                "SKU002,Product B,Category2,L,3.0,1.5"
        );

        importer.importItems(itemsFile.toString());

        assertFalse(repository.itemExists("SKU001"));
        assertTrue(repository.itemExists("SKU002"));
    }

    @Test
    void testImportItems_InvalidNumericValues() throws IOException {
        Path itemsFile = createItemsCSV(
                "SKU,Name,Category,Unit,Volume,UnitWeight",
                "SKU001,Product A,Category1,kg,ABC,2.0",
                "SKU002,Product B,Category2,L,3.0,XYZ"
        );

        importer.importItems(itemsFile.toString());

        assertFalse(repository.itemExists("SKU001"));
        assertFalse(repository.itemExists("SKU002"));
    }

    @Test
    void testImportItems_InsufficientFields() throws IOException {
        Path itemsFile = createItemsCSV(
                "SKU,Name,Category,Unit,Volume,UnitWeight",
                "SKU001,Product A,Category1",
                "SKU002,Product B,Category2,L,3.0,1.5"
        );

        importer.importItems(itemsFile.toString());

        assertFalse(repository.itemExists("SKU001"));
        assertTrue(repository.itemExists("SKU002"));
    }

    // ==================== BAYS TESTS ====================

    @Test
    void testImportBays_ValidRecords() throws IOException {
        Path baysFile = createBaysCSV(
                "WarehouseId,Aisle,Bay,CapacityBoxes",
                "W1,1,1,10",
                "W1,1,2,15"
        );

        importer.importBays(baysFile.toString());

        assertTrue(repository.warehouseExists("W1"));
        assertTrue(repository.aisleExists("W1", 1));
        assertTrue(repository.bayExists("W1", 1, 1));
        assertTrue(repository.bayExists("W1", 1, 2));
    }

    @Test
    void testImportBays_NegativeAisle() throws IOException {
        Path baysFile = createBaysCSV(
                "WarehouseId,Aisle,Bay,CapacityBoxes",
                "W1,-1,1,10",
                "W1,2,1,15"
        );

        importer.importBays(baysFile.toString());

        assertFalse(repository.aisleExists("W1", -1));
        assertTrue(repository.aisleExists("W1", 2));
    }

    @Test
    void testImportBays_NegativeBay() throws IOException {
        Path baysFile = createBaysCSV(
                "WarehouseId,Aisle,Bay,CapacityBoxes",
                "W1,1,-1,10",
                "W1,1,2,15"
        );

        importer.importBays(baysFile.toString());

        assertTrue(repository.warehouseExists("W1"));
        assertTrue(repository.aisleExists("W1", 1));
        assertFalse(repository.bayExists("W1", 1, -1));
        assertTrue(repository.bayExists("W1", 1, 2));
    }

    @Test
    void testImportBays_ZeroCapacity() throws IOException {
        Path baysFile = createBaysCSV(
                "WarehouseId,Aisle,Bay,CapacityBoxes",
                "W1,1,1,0",
                "W1,1,2,15"
        );

        importer.importBays(baysFile.toString());

        assertTrue(repository.warehouseExists("W1"));
        assertFalse(repository.bayExists("W1", 1, 1)); // Bay with 0 capacity should be rejected
        assertTrue(repository.bayExists("W1", 1, 2));
    }

    @Test
    void testImportBays_InvalidNumericValues() throws IOException {
        Path baysFile = createBaysCSV(
                "WarehouseId,Aisle,Bay,CapacityBoxes",
                "W1,ABC,1,10",
                "W1,2,1,15"
        );

        importer.importBays(baysFile.toString());

        assertFalse(repository.aisleExists("W1", -1));
        assertTrue(repository.aisleExists("W1", 2));
    }

    // ==================== WAGONS TESTS ====================

    @Test
    void testImportWagons_ValidRecords() throws IOException {
        // Setup prerequisites
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertTrue(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_UnknownSKU() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,UNKNOWN_SKU,100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU001,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_DuplicateBoxId() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX001,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertTrue(repository.boxIdExists("BOX001"));

        List<Box> allBoxes = repository.getAllBoxes();
        int count = 0;
        for (Box box : allBoxes) {
            if (box.getBoxId().equals("BOX001")) {
                count++;
            }
        }
        assertEquals(1, count, "Should only have one BOX001");
    }


    @Test
    void testImportWagons_NegativeQuantity() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,-100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_ZeroQuantity() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,0,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_MissingReceivedAt() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,2025-12-31,",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_InvalidDateFormat() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,INVALID-DATE,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_InvalidReceivedAtFormat() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,2025-12-31,15/01/2025",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_EmptyWagonId() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                ",BOX001,SKU001,100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertFalse(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_EmptyBoxId() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,,SKU001,100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_OptionalExpiryDate() throws IOException {
        setupItemsAndBays();

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        assertTrue(repository.boxIdExists("BOX001"));
        assertTrue(repository.boxIdExists("BOX002"));
    }

    @Test
    void testImportWagons_NoAvailableBayCapacity() throws IOException {
        setupItems();

        // Create warehouse with small capacity
        Warehouse warehouse = new Warehouse("W1", "Warehouse 01", null);
        repository.addWarehouse(warehouse);
        Aisle aisle = new Aisle(1, "W1");
        repository.addAisle("W1", aisle);
        repository.addBay("W1", 1, 1, 1); // Only 1 box capacity

        Path wagonsFile = createWagonsCSV(
                "WagonId,BoxId,SKU,Quantity,ExpiryDate,ReceivedAt",
                "W1,BOX001,SKU001,100,2025-12-31,2025-01-15T10:30:00",
                "W2,BOX002,SKU002,50,2025-11-30,2025-01-16T14:00:00"
        );

        importer.importWagons(wagonsFile.toString());

        // Only first box should be added
        assertTrue(repository.boxIdExists("BOX001"));
        assertFalse(repository.boxIdExists("BOX002"));
    }

    // ==================== ORDERS TESTS ====================

    @Test
    void testImportOrders_ValidRecords() throws IOException {
        Path ordersFile = createOrdersCSV(
                "OrderId,DueDate,Priority",
                "ORD001,2025-02-01T10:00:00,1",
                "ORD002,2025-02-15T15:30:00,2"
        );

        importer.importOrders(ordersFile.toString());

        assertTrue(repository.orderExists("ORD001"));
        assertTrue(repository.orderExists("ORD002"));
    }

    @Test
    void testImportOrders_InvalidDueDate() throws IOException {
        Path ordersFile = createOrdersCSV(
                "OrderId,DueDate,Priority",
                "ORD001,INVALID-DATE,1",
                "ORD002,2025-02-15T15:30:00,2"
        );

        importer.importOrders(ordersFile.toString());

        assertFalse(repository.orderExists("ORD001"));
        assertTrue(repository.orderExists("ORD002"));
    }

    @Test
    void testImportOrders_NegativePriority() throws IOException {
        Path ordersFile = createOrdersCSV(
                "OrderId,DueDate,Priority",
                "ORD001,2025-02-01T10:00:00,-1",
                "ORD002,2025-02-15T15:30:00,2"
        );

        importer.importOrders(ordersFile.toString());

        assertFalse(repository.orderExists("ORD001"));
        assertTrue(repository.orderExists("ORD002"));
    }

    @Test
    void testImportOrders_InvalidPriorityFormat() throws IOException {
        Path ordersFile = createOrdersCSV(
                "OrderId,DueDate,Priority",
                "ORD001,2025-02-01T10:00:00,ABC",
                "ORD002,2025-02-15T15:30:00,2"
        );

        importer.importOrders(ordersFile.toString());

        assertFalse(repository.orderExists("ORD001"));
        assertTrue(repository.orderExists("ORD002"));
    }

    // ==================== ORDER LINES TESTS ====================

    @Test
    void testImportOrderLines_ValidRecords() throws IOException {
        setupOrdersAndItems();

        Path orderLinesFile = createOrderLinesCSV(
                "OrderId,LineNumber,SKU,Quantity",
                "ORD001,1,SKU001,10",
                "ORD001,2,SKU002,20"
        );

        importer.importOrderLines(orderLinesFile.toString());

        List<OrderLine> lines = repository.getOrderLines();
        assertNotNull(lines);
        assertEquals(2, lines.size());
    }

    @Test
    void testImportOrderLines_UnknownOrderId() throws IOException {
        setupOrdersAndItems();

        Path orderLinesFile = createOrderLinesCSV(
                "OrderId,LineNumber,SKU,Quantity",
                "UNKNOWN_ORDER,1,SKU001,10",
                "ORD001,2,SKU002,20"
        );

        importer.importOrderLines(orderLinesFile.toString());

        List<OrderLine> lines = repository.getOrderLines();
        assertEquals(1, lines.size());
    }

    @Test
    void testImportOrderLines_UnknownSKU() throws IOException {
        setupOrdersAndItems();

        Path orderLinesFile = createOrderLinesCSV(
                "OrderId,LineNumber,SKU,Quantity",
                "ORD001,1,UNKNOWN_SKU,10",
                "ORD001,2,SKU002,20"
        );

        importer.importOrderLines(orderLinesFile.toString());

        List<OrderLine> lines = repository.getOrderLines();
        assertEquals(1, lines.size());
    }

    @Test
    void testImportOrderLines_NegativeQuantity() throws IOException {
        setupOrdersAndItems();

        Path orderLinesFile = createOrderLinesCSV(
                "OrderId,LineNumber,SKU,Quantity",
                "ORD001,1,SKU001,-10",
                "ORD001,2,SKU002,20"
        );

        importer.importOrderLines(orderLinesFile.toString());

        List<OrderLine> lines = repository.getOrderLines();
        assertEquals(1, lines.size());
    }

    @Test
    void testImportOrderLines_NegativeLineNumber() throws IOException {
        setupOrdersAndItems();

        Path orderLinesFile = createOrderLinesCSV(
                "OrderId,LineNumber,SKU,Quantity",
                "ORD001,-1,SKU001,10",
                "ORD001,2,SKU002,20"
        );

        importer.importOrderLines(orderLinesFile.toString());

        List<OrderLine> lines = repository.getOrderLines();
        assertEquals(1, lines.size());
    }

    // ==================== RETURNS TESTS ====================

    @Test
    void testImportReturns_ValidRecords() throws IOException {
        setupItems();

        Path returnsFile = createReturnsCSV(
                "ReturnId,SKU,Quantity,Reason,Timestamp,ExpiryDate",
                "RET001,SKU001,5,Damaged,2025-01-20T10:00:00,2025-12-31",
                "RET002,SKU002,3,Defective,2025-01-21T11:00:00,"
        );

        importer.importReturns(returnsFile.toString());

        List<Return> returns = WarehouseRepository.getReturns();
        assertEquals(2, returns.size());
    }

    @Test
    void testImportReturns_UnknownSKU() throws IOException {
        setupItems();

        Path returnsFile = createReturnsCSV(
                "ReturnId,SKU,Quantity,Reason,Timestamp,ExpiryDate",
                "RET001,UNKNOWN_SKU,5,Damaged,2025-01-20T10:00:00,2025-12-31",
                "RET002,SKU002,3,Defective,2025-01-21T11:00:00,"
        );

        importer.importReturns(returnsFile.toString());

        List<Return> returns = WarehouseRepository.getReturns();
        assertEquals(1, returns.size());
    }

    @Test
    void testImportReturns_NegativeQuantity() throws IOException {
        setupItems();

        Path returnsFile = createReturnsCSV(
                "ReturnId,SKU,Quantity,Reason,Timestamp,ExpiryDate",
                "RET001,SKU001,-5,Damaged,2025-01-20T10:00:00,2025-12-31",
                "RET002,SKU002,3,Defective,2025-01-21T11:00:00,"
        );

        importer.importReturns(returnsFile.toString());

        List<Return> returns = WarehouseRepository.getReturns();
        assertEquals(1, returns.size());
    }

    @Test
    void testImportReturns_MissingReason() throws IOException {
        setupItems();

        Path returnsFile = createReturnsCSV(
                "ReturnId,SKU,Quantity,Reason,Timestamp,ExpiryDate",
                "RET001,SKU001,5,,2025-01-20T10:00:00,2025-12-31",
                "RET002,SKU002,3,Defective,2025-01-21T11:00:00,"
        );

        importer.importReturns(returnsFile.toString());

        List<Return> returns = WarehouseRepository.getReturns();
        assertEquals(1, returns.size());
    }

    @Test
    void testImportReturns_InvalidTimestamp() throws IOException {
        setupItems();

        Path returnsFile = createReturnsCSV(
                "ReturnId,SKU,Quantity,Reason,Timestamp,ExpiryDate",
                "RET001,SKU001,5,Damaged,INVALID-TIMESTAMP,2025-12-31",
                "RET002,SKU002,3,Defective,2025-01-21T11:00:00,"
        );

        importer.importReturns(returnsFile.toString());

        List<Return> returns = WarehouseRepository.getReturns();
        assertEquals(1, returns.size());
    }

    @Test
    void testImportReturns_InvalidExpiryDate() throws IOException {
        setupItems();

        Path returnsFile = createReturnsCSV(
                "ReturnId,SKU,Quantity,Reason,Timestamp,ExpiryDate",
                "RET001,SKU001,5,Damaged,2025-01-20T10:00:00,INVALID-DATE",
                "RET002,SKU002,3,Defective,2025-01-21T11:00:00,"
        );

        importer.importReturns(returnsFile.toString());

        List<Return> returns = WarehouseRepository.getReturns();
        assertEquals(1, returns.size());
    }

    // ==================== TRAIN STATIONS TESTS ====================

//    @Test
//    void testImportTrainStations_ValidRecords() throws IOException {
//        Path stationsFile = createTrainStationsCSV(
//            "country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport",
//            "FR,'Europe/Paris',CET,Chateau-Arnoux-St-Auban,44.08179,6.001625,true,false,false",
//            "DE,'Europe/Berlin',CET,Berlin Hbf,52.525084,13.369402,true,true,true"
//        );
//        importer.importTrainStations(stationsFile.toString());
//        List<TrainStation> stations = repository.getTrainStations();
//        assertEquals(2, stations.size());
//        assertEquals("FR", stations.get(0).getCountry());
//        assertEquals("DE", stations.get(1).getCountry());
//    }

//    @Test
//    void testImportTrainStations_InvalidBoolean() throws IOException {
//        Path stationsFile = createTrainStationsCSV("country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport",
//            "FR,'Europe/Paris',CET,Chateau-Arnoux-St-Auban,44.08179,6.001625,maybe,false,false", // is_city inválido
//            "DE,'Europe/Berlin',CET,Berlin Hbf,52.525084,13.369402,true,true,true"
//        );
//        importer.importTrainStations(stationsFile.toString());
//        List<TrainStation> stations = repository.getTrainStations();
//        assertEquals(1, stations.size());
//        assertEquals("DE", stations.get(0).getCountry());
//    }

    // ==================== HELPER METHODS ====================

    private Path createItemsCSV(String... lines) throws IOException {
        return createCSVFile("items.csv", lines);
    }

    private Path createBaysCSV(String... lines) throws IOException {
        return createCSVFile("bays.csv", lines);
    }

    private Path createWagonsCSV(String... lines) throws IOException {
        return createCSVFile("wagons.csv", lines);
    }

    private Path createOrdersCSV(String... lines) throws IOException {
        return createCSVFile("orders.csv", lines);
    }

    private Path createOrderLinesCSV(String... lines) throws IOException {
        return createCSVFile("order_lines.csv", lines);
    }

    private Path createReturnsCSV(String... lines) throws IOException {
        return createCSVFile("returns.csv", lines);
    }

    private Path createTrainStationsCSV(String... lines) throws IOException {
        return createCSVFile("train_stations_europe.csv", lines);
    }

    private Path createCSVFile(String filename, String... lines) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.write(file, List.of(lines));
        return file;
    }


    private void setupItems() {
        repository.addItem(new Item("SKU001", "Product A", "Category1", "kg", 1.5, 2.0));
        repository.addItem(new Item("SKU002", "Product B", "Category2", "L", 3.0, 1.5));
    }

    private void setupItemsAndBays() {
        setupItems();

        Warehouse warehouse = new Warehouse("W1", "Warehouse 01", null);
        repository.addWarehouse(warehouse);

        Aisle aisle = new Aisle(1, "W1");
        repository.addAisle("W1", aisle);

        repository.addBay("W1", 1, 1, 100);
        repository.addBay("W1", 1, 2, 100);
    }

    private void setupOrdersAndItems() {
        setupItems();

        Order order = new Order("ORD001", new Date(), 1);
        repository.addOrder(order);
    }
}