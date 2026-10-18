package pt.ipp.isep.dei.ESINF;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.map.MapGraph;
import pt.ipp.isep.dei.ESINF.repository.I_WarehouseRepository;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.InventoryService;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles the importation and validation of CSV files containing warehouse data.
 * This class processes various types of warehouse-related data including items, bays, wagons, orders, order lines, and returns.
 * <p>
 * The importer validates each record according to business rules and logs any errors encountered during the import process.
 */
public class CSVImporter {
    /**
     * Repository for storing and accessing warehouse data.
     */
    private I_WarehouseRepository repository;
    private InventoryService inventoryService;
    /**
     * List of error messages encountered during import operations.
     */
    private List<String> errors;
    /**
     * Counter for successfully imported records.
     */
    private int validRecords;
    /**
     * Counter for rejected records due to validation errors.
     */
    private int invalidRecords;

    /**
     * Constructs a new CSVImporter with the specified repository.
     *
     * @param repository the warehouse repository to store imported data
     */
    public CSVImporter(WarehouseRepository repository) {
        this.repository = repository;
        this.errors = new ArrayList<>();
        this.validRecords = 0;
        this.invalidRecords = 0;
        this.inventoryService = new InventoryServiceImpl(repository);
    }


    /**
     * Imports items from a CSV file.
     * <p>
     * Expected CSV format: SKU, Name, Category, Unit, Volume, UnitWeight
     * </p>
     *
     * @param filePath the path to the items CSV file
     */
    public void importItems(String filePath) {
        System.out.println("\n=== Importing items.csv ===");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(",") ? "," : ";";
                String[] fields = line.split(separator);

                if (fields.length < 6) {
                    logError(lineNumber, "Insufficient fields in items.csv");
                    continue;
                }

                System.out.printf(
                        "Line %d -> SKU: %s | Name: %s | Category: %s | Unit: %s | Volume: %s | Weight: %s%n",
                        lineNumber,
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim(),
                        fields[4].trim(),
                        fields[5].trim()
                );

                ValidationResult result = validateItem(fields, lineNumber);
                if (result.isValid()) {
                    Item item = new Item(
                            fields[0].trim(), //sku
                            fields[1].trim(), //name
                            fields[2].trim(), //category
                            fields[3].trim(), //unit
                            Double.parseDouble(fields[4].trim()), //volume
                            Double.parseDouble(fields[5].trim()) //unitwieght
                    );
                    if (repository.addItem(item)){
                        System.out.println("Item added");
                    } else {
                        System.out.println("Item could not be added");
                    }
                    validRecords++;
                } else {
                    logError(lineNumber, result.getErrorMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading items.csv: " + e.getMessage());
        }
    }

    /**
     * Imports storage bays from a CSV file.
     * <p>
     * Expected CSV format: WarehouseId, Aisle, Bay, CapacityBoxes
     * </p>
     *
     * @param filePath the path to the bays CSV file
     */
    public void importBays(String filePath) {
        System.out.println("\n=== Importing bays.csv ===");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(";") ? ";" : ",";
                String[] fields = line.split(separator);

                if (fields.length < 4) {
                    logError(lineNumber, "Insufficient fields in bays.csv");
                    continue;
                }

                System.out.printf(
                        "Line %d -> warehouseId: %s | aisle: %s | bay: %s | capacityboxes: %s%n",
                        lineNumber,
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim()
                );

                ValidationResult result = validateBay(fields, lineNumber);
                if (result.isValid()) {
                    String warehouseId = fields[0].trim();
                    int aisleNumber = Integer.parseInt(fields[1].trim());
                    int bayNumber = Integer.parseInt(fields[2].trim());
                    int capacity = Integer.parseInt(fields[3].trim());
                    
                    // Create warehouse if it doesn't exist
                    if (!repository.warehouseExists(warehouseId)) {
                        Warehouse warehouse = new Warehouse(warehouseId, "Warehouse " + warehouseId, null);
                        repository.addWarehouse(warehouse);
                        System.out.println("Created warehouse: " + warehouseId);
                    }
                    
                    // Create aisle if it doesn't exist
                    if (!repository.aisleExists(warehouseId, aisleNumber)) {
                        Aisle aisle = new Aisle(aisleNumber, warehouseId);
                        repository.addAisle(warehouseId, aisle);
                        System.out.println("Created aisle: " + aisleNumber + " in warehouse: " + warehouseId);
                    }
                    
                    // Create bay
                    repository.addBay(warehouseId, aisleNumber, bayNumber, capacity);
                    validRecords++;
                } else {
                    logError(lineNumber, result.getErrorMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading bays.csv: " + e.getMessage());
        }
    }

    /**
     * Imports wagons (incoming shipments) from a CSV file.
     * <p>
     * Expected CSV format: WagonId, BoxId, SKU, Quantity, ExpiryDate (optional), ReceivedAt
     * Supports multiple date formats for flexibility.
     * </p>
     *
     * @param filePath the path to the wagons CSV file
     */
    public void importWagons(String filePath) {
        System.out.println("\n=== Importing wagons.csv ===");

        int lineNumber = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String separator = line.contains(",") ? "," : ";";
                    String[] fields = line.split(separator,-1);

                    if (fields.length < 6) {
                        logError(lineNumber, "Insufficient fields in wagons.csv");
                        continue;
                    }

                    // Print incoming row info
                    System.out.printf(
                            "Line %d -> wagonId: %s | boxId: %s | sku: %s | qty: %s | expiryDate: %s | receivedAt: %s%n",
                            lineNumber,
                            fields[0].trim(),
                            fields[1].trim(),
                            fields[2].trim(),
                            fields[3].trim(),
                            fields[4].trim(),
                            fields[5].trim()
                    );

                    // Validate row first to keep behavior consistent
                    ValidationResult validation = validateWagon(fields, lineNumber);
                    if (!validation.isValid()) {
                        logError(lineNumber, validation.getErrorMessage());
                        continue;
                    }

                    String wagonId = fields[0].trim();
                    String boxId = fields[1].trim();
                    String sku = fields[2].trim();
                    int qty = Integer.parseInt(fields[3].trim());

                    // expiryDate (optional)
                    java.sql.Date expiryDate = null;
                    if (!fields[4].trim().isEmpty()) {
                        try {
                            expiryDate = java.sql.Date.valueOf(LocalDateTime.parse(fields[4].trim() + "T00:00:00", dateTimeFormatter).toLocalDate());
                        } catch (DateTimeParseException e) {
                            expiryDate = java.sql.Date.valueOf(LocalDate.parse(fields[4].trim(), dateFormatter));
                        } catch (Exception e) {
                            logError(lineNumber, "Invalid expiryDate: " + fields[4].trim());
                            continue;
                        }
                    }

                    // receivedAt (required)
                    Timestamp receivedAt;
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(fields[5].trim(), dateTimeFormatter);
                        receivedAt = Timestamp.valueOf(dateTime);
                    } catch (DateTimeParseException e) {
                        logError(lineNumber, "Invalid receivedAt date-time format: " + fields[5].trim());
                        continue;
                    }

                    Wagon wagon = new Wagon(wagonId, boxId, sku, qty, expiryDate, receivedAt);
                    repository.addWagon(wagon);

                    // Create box from wagon row
                    Box box = new Box(boxId, wagonId, sku, qty,
                            expiryDate != null ? new Date(expiryDate.getTime()) : null,
                            new Date(receivedAt.getTime()));

                    // Select first bay with free capacity by ascending key
                    String[] target = findFirstBayWithCapacity();
                    if (target == null) {
                        logError(lineNumber, "No bay has free capacity for boxId: " + boxId);
                        continue;
                    }
                    String warehouseId = target[0];
                    int aisle = Integer.parseInt(target[1]);
                    int bay = Integer.parseInt(target[2]);
                    inventoryService.unloadBox(box, warehouseId, aisle, bay);
                    validRecords++;

                } catch (Exception e) {
                    logError(lineNumber, "Unexpected error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading wagons.csv: " + e.getMessage());
        }
    }

    private String[] findFirstBayWithCapacity() {
        // Get all warehouses and iterate through their structure
        for (Warehouse warehouse : repository.getWarehouses().values()) {
            for (Aisle aisle : warehouse.getAisles().values()) {
                for (Bay bay : aisle.getBays().values()) {
                    int size = bay.getCurrentBoxCount();

                    if (size < bay.getCapacityBoxes()) {
                        return new String[]{
                                warehouse.getWarehouseId(),
                                String.valueOf(aisle.getAisleNumber()),
                                String.valueOf(bay.getBay())
                        };
                    }
                }
            }
        }

        return null;
    }

    /**
     * Imports orders from a CSV file.
     * <p>
     * Expected CSV format: OrderId, DueDate, Priority
     * </p>
     *
     * @param filePath the path to the orders CSV file
     */
    public void importOrders(String filePath) {
        System.out.println("\n=== Importing orders.csv ===");

        DateTimeFormatter orderDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(",") ? "," : ";";
                String[] fields = line.split(separator,-1);

                if (fields.length < 3) {
                    logError(lineNumber, "Insufficient fields in orders.csv");
                    continue; // vai para a próxima linha
                }

                // Print incoming row info
                System.out.printf(
                        "Line %d -> orderId: %s | dueDate: %s | priority: %s%n",
                        lineNumber,
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim()
                );

                // Validate row first
                ValidationResult orderValidation = validateOrder(fields, lineNumber);
                if (!orderValidation.isValid()) {
                    logError(lineNumber, orderValidation.getErrorMessage());
                    continue;
                }

                String orderId = fields[0].trim();
                String dueDateStr = fields[1].trim();
                String priorityStr = fields[2].trim();

                // Parse dueDate (ISO local date-time)
                Date dueDate;
                try {
                    LocalDateTime dt = LocalDateTime.parse(dueDateStr, orderDateTimeFormatter);
                    dueDate = Timestamp.valueOf(dt);
                } catch (Exception e) {
                    logError(lineNumber, "Invalid dueDate format for order: " + orderId);
                    continue; // rejeita só esta linha
                }

                // Parse priority
                int priority;
                try {
                    priority = Integer.parseInt(priorityStr);
                    if (priority < 0) {
                        logError(lineNumber, "Priority cannot be negative for order: " + orderId);
                        continue; // rejeita só esta linha
                    }
                } catch (NumberFormatException e) {
                    logError(lineNumber, "Invalid priority for order: " + orderId);
                    continue; // rejeita só esta linha
                }

                // Criar e adicionar a ordem
                Order order = new Order(orderId, dueDate, priority);
                repository.addOrder(order);
                validRecords++;
            }
        } catch (IOException e) {
            System.err.println("Error reading orders.csv: " + e.getMessage());
        }
    }

    /**
     * Imports order lines from a CSV file.
     * <p>
     * Expected CSV format: OrderId, LineNumber, SKU, Quantity.
     * Validates that both the order and SKU exist before importing.
     * </p>
     *
     * @param filePath the path to the order lines CSV file
     */
    public void importOrderLines(String filePath) {
        System.out.println("\n=== Importing order_lines.csv ===");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(",") ? "," : ";";
                String[] fields = line.split(separator);

                if (fields.length < 4) {
                    logError(lineNumber, "Insufficient fields in order_lines.csv");
                    continue;
                }

                // Print incoming row info
                System.out.printf(
                        "Line %d -> orderId: %s | lineNo: %s | sku: %s | qty: %s%n",
                        lineNumber,
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim()
                );

                ValidationResult result = validateOrderLine(fields, lineNumber);
                if (result.isValid()) {
                    OrderLine orderLine = new OrderLine(
                            fields[0].trim(),
                            Integer.parseInt(fields[1].trim()),
                            fields[2].trim(),
                            Integer.parseInt(fields[3].trim())
                    );
                    repository.addOrderLine(orderLine);
                    validRecords++;
                } else {
                    logError(lineNumber, result.getErrorMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading order_lines.csv: " + e.getMessage());
        }
    }

    /**
     * Imports product returns from a CSV file.
     * <p>
     * Expected CSV format: ReturnId, SKU, Quantity, Reason, Timestamp, ExpiryDate (optional)
     * </p>
     *
     * @param filePath the path to the returns CSV file
     */
    public void importReturns(String filePath) {
        System.out.println("\n=== Importing returns.csv ===");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(",") ? "," : ";";
                String[] fields = line.split(separator, -1);

                if (fields.length < 6) {
                    logError(lineNumber, "Insufficient fields in returns.csv");
                    continue;
                }

                // Print incoming row info
                System.out.printf(
                        "Line %d -> returnId: %s | sku: %s | qty: %s | reason: %s | timestamp: %s | expiryDate: %s%n",
                        lineNumber,
                        fields[0].trim(),
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim(),
                        fields[4].trim(),
                        fields.length > 5 ? fields[5].trim() : ""
                );

                ValidationResult result = validateReturn(fields, lineNumber);
                if (result.isValid()) {
                    String returnId = fields[0].trim();
                    String sku = fields[1].trim();
                    int qty = Integer.parseInt(fields[2].trim());
                    String reason = fields[3].trim();

                    // Parse timestamp
                    Date timestamp = null;
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(fields[4].trim());
                        timestamp = java.sql.Timestamp.valueOf(dateTime);
                    } catch (Exception e) {
                        logError(lineNumber, "Invalid timestamp format for returnId: " + returnId);
                        continue;
                    }

                    // Parse expiryDate (optional)
                    Date expiryDate = null;
                    if (fields.length > 5 && !fields[5].trim().isEmpty()) {
                        try {
                            LocalDate date = LocalDate.parse(fields[5].trim());
                            expiryDate = java.sql.Date.valueOf(date);
                        } catch (Exception e) {
                            logError(lineNumber, "Invalid expiryDate format for returnId: " + returnId);
                            continue;
                        }
                    }

                    Return ret = new Return(returnId, sku, qty, reason, timestamp, expiryDate);
                    repository.addReturn(ret);
                    validRecords++;
                } else {
                    logError(lineNumber, result.getErrorMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading returns.csv: " + e.getMessage());
        }
    }

    /**
     * Imports train stations from CSV file train_stations_europe.csv
     * @param filePath path to the stations CSV file
     */
    public void importTrainStations(String filePath) {
        System.out.println("\n=== Importing train_stations_europe.csv ===");
        System.out.println("This action may take some time to import... ");
        int lineNumber = 1;
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] fields;
            reader.readNext(); // skip header
            while ((fields = reader.readNext()) != null) {
                lineNumber++;
                try {
                    if (fields.length < 9) {
                        logError(lineNumber, "Insufficient fields in train_stations_europe.csv");
                        continue;
                    }
                    String country = fields[0].trim();
                    String timeZone = fields[1].trim();
                    String timeZoneGroup = fields[2].trim();
                    String station = fields[3].trim();
                    Double latitude = fields[4].isEmpty() ? null : Double.valueOf(fields[4].trim());
                    Double longitude = fields[5].isEmpty() ? null : Double.valueOf(fields[5].trim());
                    boolean isCity = parseStrictBoolean(fields[6].trim(), lineNumber, "is_city");
                    boolean isMainStation = parseStrictBoolean(fields[7].trim(), lineNumber, "is_main_station");
                    boolean isAirport = parseStrictBoolean(fields[8].trim(), lineNumber, "is_airport");

                    if (country.isEmpty()) {
                        logError(lineNumber, "Missing mandatory fields country");
                        continue;
                    }

                    if (timeZoneGroup.isEmpty()) {
                        logError(lineNumber, "Missing mandatory fields timeZoneGroup");
                        continue;
                    }

                    if (station.isEmpty()) {
                        logError(lineNumber, "Missing mandatory fields stationName)");
                        continue;
                    }

                    if (latitude == null || longitude == null) {
                        logError(lineNumber, "Latitude or longitude missing");
                        continue;
                    }

                    if (latitude < -90 || latitude > 90) {
                        logError(lineNumber, "Invalid latitude: " + latitude);
                        continue;
                    }

                    if (longitude < -180 || longitude > 180) {
                        logError(lineNumber, "Invalid longitude: " + longitude);
                        continue;
                    }

                    TrainStation trainStation = new TrainStation(
                        country, timeZone, timeZoneGroup, station, new GeoCoordinate(latitude, longitude), isCity, isMainStation, isAirport
                    );

                    repository.addTrainStation(trainStation);
                    validRecords++;
                } catch (IllegalArgumentException e) {
                }
            }
        } catch (CsvValidationException e) {
            System.err.println("Error reading train_stations_europe.csv (CSV format): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error reading train_stations_europe.csv: " + e.getMessage());
        }
    }

    /**
     * Imports both stations and lines from their respective CSV files directly into the graphs.
     *
     * @param stationsFilePath the path to the stations CSV file
     * @param linesFilePath    the path to the lines CSV file
     */
    public void importGraphs(String stationsFilePath, String linesFilePath) {
        importStations(stationsFilePath);
        importLines(linesFilePath);
    }

    /**
     * Imports stations from a CSV file directly into both graphs as vertices.
     * Expected CSV format: Station id,Station,Lat,Lon,CoordX,CoordY
     *
     * @param filePath the path to the stations CSV file
     */
    public void importStations(String filePath) {
        System.out.println("\n=== Importing stations.csv directly into graphs ===");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(",") ? "," : ";";
                String[] fields = line.split(separator);

                if (fields.length < 6) {
                    logError(lineNumber, "Insufficient fields in stations.csv");
                    continue;
                }

                ValidationResult result = validateStation(fields, lineNumber);
                if (result.isValid()) {
                    int stationID = Integer.parseInt(fields[0].trim());

                    // Check if station ID already exists in directed graph
                    if (findStationById(repository.getDirectedGraph(), stationID) != null) {
                        logError(lineNumber, "Duplicate station ID: " + stationID);
                        continue;
                    }

                    String name = fields[1].trim();

                    if (stationNameExists(repository.getDirectedGraph(), name)) {
                        logError(lineNumber, "Duplicate station name: " + name);
                        continue;
                    }

                    double lat = Double.parseDouble(fields[2].trim());
                    double lon = Double.parseDouble(fields[3].trim());
                    double coordX = Double.parseDouble(fields[4].trim());
                    double coordY = Double.parseDouble(fields[5].trim());

                    GeoCoordinate location = new GeoCoordinate(lat, lon);
                    Coordinate coordinate = new Coordinate(coordX, coordY);
                    Station station = new Station(stationID, name, location, coordinate);

                    // Add station to both graphs as a vertex
                    boolean addedDirected = repository.getDirectedGraph().addVertex(station);
                    boolean addedUndirected = repository.getUndirectedGraph().addVertex(station);

                    if (addedDirected && addedUndirected) {
                        System.out.println("Station added to both graphs: " + name);
                        validRecords++;
                    } else {
                        logError(lineNumber, "Failed to add station to graphs: " + name);
                    }
                } else {
                    logError(lineNumber, result.getErrorMessage());
                }
            }

            System.out.println("\nStations import complete: " + validRecords + " stations added to graphs");
        } catch (IOException e) {
            System.err.println("Error reading stations.csv: " + e.getMessage());
        }
    }

    /**
     * Imports lines from a CSV file directly into both graphs as edges.
     * Expected CSV format: departure_stid,arrival_stid,dist,capacity,cost
     * Note: Must be called AFTER importStations()
     *
     * @param filePath the path to the lines CSV file
     */
    public void importLines(String filePath) {
        System.out.println("\n=== Importing lines.csv directly into graphs ===");

        if (repository.getDirectedGraph().numVertices() == 0) {
            System.err.println("ERROR: No stations loaded. Please import stations first!");
            return;
        }

        int linesAdded = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String separator = line.contains(",") ? "," : ";";
                String[] fields = line.split(separator);

                if (fields.length < 5) {
                    logError(lineNumber, "Insufficient fields in lines.csv");
                    continue;
                }

                ValidationResult result = validateLine(fields, lineNumber);
                if (result.isValid()) {
                    int departureStationID = Integer.parseInt(fields[0].trim());
                    int arrivalStationID = Integer.parseInt(fields[1].trim());
                    double distance = Double.parseDouble(fields[2].trim());
                    double capacity = Double.parseDouble(fields[3].trim());
                    double cost = Double.parseDouble(fields[4].trim());

                    // Find stations by ID in directed graph
                    Station departureStation = findStationById(repository.getDirectedGraph(), departureStationID);
                    Station arrivalStation = findStationById(repository.getDirectedGraph(), arrivalStationID);

                    if (departureStation == null) {
                        logError(lineNumber, "Departure station not found: " + departureStationID);
                        continue;
                    }

                    if (arrivalStation == null) {
                        logError(lineNumber, "Arrival station not found: " + arrivalStationID);
                        continue;
                    }

                    // Create weight object
                    Weight weight = new Weight(distance, capacity, cost);

                    // Add edge to both graphs
                    boolean addedDirected = repository.getDirectedGraph().addEdge(departureStation, arrivalStation, weight);
                    boolean addedUndirected = repository.getUndirectedGraph().addEdge(departureStation, arrivalStation, weight);

                    if (addedDirected && addedUndirected) {
                        System.out.println("Line added to both graphs: " + departureStation.getName() +
                                " -> " + arrivalStation.getName());
                        linesAdded++;
                    } else {
                        logError(lineNumber, "Failed to add line to graphs (edge may already exist)");
                    }
                } else {
                    logError(lineNumber, result.getErrorMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading lines.csv: " + e.getMessage());
        }
    }

    /**
     * Finds a station in the graph by its ID.
     * Uses the graph's vertex() method with a predicate.
     *
     * @param graph the graph to search
     * @param stationID the station ID to find
     * @return the Station object if found, null otherwise
     */
    private Station findStationById(Graph<Station, Weight> graph, int stationID) {
        return graph.vertex(station -> station.getStationID() == stationID);
    }

    /**
     * Checks if a station with the given name already exists in the graph.
     *
     * @param graph the graph to search
     * @param stationName the station name to find
     * @return true if a station with this name exists, false otherwise
     */
    private boolean stationNameExists(Graph<Station, Weight> graph, String stationName) {
        return graph.vertex(station -> station.getName().equalsIgnoreCase(stationName)) != null;
    }

    /**
     * Validates a station record from CSV data.
     */
    private ValidationResult validateStation(String[] fields, int lineNumber) {
        try {
            // Validate Station ID
            int stationID = Integer.parseInt(fields[0].trim());
            if (stationID < 0) {
                return ValidationResult.error("Station ID cannot be negative");
            }

            // Validate Station Name
            String name = fields[1].trim();
            if (name.isEmpty()) {
                return ValidationResult.error("Station name cannot be empty");
            }

            // Validate Latitude
            double lat = Double.parseDouble(fields[2].trim());
            if (lat < -90 || lat > 90) {
                return ValidationResult.error("Invalid latitude: " + lat + " (must be between -90 and 90)");
            }

            // Validate Longitude
            double lon = Double.parseDouble(fields[3].trim());
            if (lon < -180 || lon > 180) {
                return ValidationResult.error("Invalid longitude: " + lon + " (must be between -180 and 180)");
            }

            // Validate Coordinates
            Double.parseDouble(fields[4].trim()); // CoordX
            Double.parseDouble(fields[5].trim()); // CoordY

        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid numeric value in station data");
        }

        return ValidationResult.success();
    }

    /**
     * Validates a line record from CSV data.
     */
    private ValidationResult validateLine(String[] fields, int lineNumber) {
        try {
            // Validate Station IDs
            int departureStationID = Integer.parseInt(fields[0].trim());
            int arrivalStationID = Integer.parseInt(fields[1].trim());

            if (departureStationID < 0) {
                return ValidationResult.error("Departure station ID cannot be negative");
            }

            if (arrivalStationID < 0) {
                return ValidationResult.error("Arrival station ID cannot be negative");
            }

            if (departureStationID == arrivalStationID) {
                return ValidationResult.error("Departure and arrival stations cannot be the same");
            }

            // Validate Distance
            double distance = Double.parseDouble(fields[2].trim());
            if (distance <= 0) {
                return ValidationResult.error("Distance must be positive: " + distance);
            }

            // Validate Capacity
            double capacity = Double.parseDouble(fields[3].trim());
            if (capacity <= 0) {
                return ValidationResult.error("Capacity must be positive: " + capacity);
            }

            // Validate Cost (optional - only parse to check format)
            Double.parseDouble(fields[4].trim());

        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid numeric value in line data");
        }

        return ValidationResult.success();
    }

    private boolean parseStrictBoolean(String value, int lineNumber, String fieldName) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        logError(lineNumber, "Invalid boolean value for " + fieldName + ": '" + value + "'");
        throw new IllegalArgumentException("Invalid boolean value for " + fieldName + ": " + value);
    }

    // Validation Methods
    /**
     * Validates an item record from CSV data.
     *
     * @param fields the array of field values from the CSV line
     * @param lineNumber the line number being validated (for error reporting)
     * @return a ValidationResult indicating success or containing an error message
     */
    private ValidationResult validateItem(String[] fields, int lineNumber) {
        String sku = fields[0].trim();

        if (sku.isEmpty()) {
            return ValidationResult.error("SKU cannot be empty");
        }

        double volume;
        double unitWeight;

        try {
            volume = Double.parseDouble(fields[4].trim());
        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid numeric value for VOLUME (SKU: " + sku + ")");
        }

        try {
            unitWeight = Double.parseDouble(fields[5].trim());
        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid numeric value for WEIGHT (SKU: " + sku + ")");
        }

        if (volume <= 0) {
            return ValidationResult.error("Volume must be positive for SKU: " + sku);
        }
        if (unitWeight <= 0) {
            return ValidationResult.error("Weight must be positive for SKU: " + sku);
        }

        return ValidationResult.success();
    }

    /**
     * Validates a bay record from CSV data.
     *
     * @param fields the array of field values from the CSV line
     * @param lineNumber the line number being validated (for error reporting)
     * @return a ValidationResult indicating success or containing an error message
     */
    private ValidationResult validateBay(String[] fields, int lineNumber) {
        try {
            String warehouseId = fields[0].trim();

            int aisle;
            try {
                aisle = Integer.parseInt(fields[1].trim());
            } catch (NumberFormatException e) {
                return ValidationResult.error("Invalid numeric value for AISLE in warehouse: " + warehouseId);
            }

            int bay;
            try {
                bay = Integer.parseInt(fields[2].trim());
            } catch (NumberFormatException e) {
                return ValidationResult.error("Invalid numeric value for BAY in warehouse: " + warehouseId);
            }

            int capacity;
            try {
                capacity = Integer.parseInt(fields[3].trim());
            } catch (NumberFormatException e) {
                return ValidationResult.error("Invalid numeric value for CAPACITY in warehouse: " + warehouseId);
            }

            if (aisle < 0) {
                return ValidationResult.error("Aisle number cannot be negative (warehouse: " + warehouseId + ")");
            }
            if (bay < 0) {
                return ValidationResult.error("Bay number cannot be negative (warehouse: " + warehouseId + ")");
            }
            if (capacity <= 0) {
                return ValidationResult.error("Capacity must be positive (warehouse: " + warehouseId + ")");
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            return ValidationResult.error("Missing required fields in bay data");
        }

        return ValidationResult.success();

    }

    /**
     * Validates a wagon record from CSV data.
     * <p>
     * Checks for duplicate box IDs, validates SKU existence, quantity values, and date formats.
     * </p>
     *
     * @param fields the array of field values from the CSV line
     * @param lineNumber the line number being validated (for error reporting)
     * @return a ValidationResult indicating success or containing an error message
     */
    private ValidationResult validateWagon(String[] fields, int lineNumber) {
        String wagonId = fields[0].trim();
        String boxId = fields[1].trim();
        String sku = fields[2].trim();
        String qtyStr = fields[3].trim();
        String expiryStr = fields[4].trim();
        String receivedStr = fields[5].trim();

        if (wagonId.isEmpty()) return ValidationResult.error("WagonId cannot be empty");
        if (boxId.isEmpty()) return ValidationResult.error("BoxId cannot be empty in wagon: " + wagonId);
        if (sku.isEmpty()) return ValidationResult.error("SKU cannot be empty in wagon: " + wagonId);

        // Check if receivedAt is missing or empty
        if (receivedStr.isEmpty()) {
            return ValidationResult.error("Missing receivedAt in wagon: " + wagonId);
        }

        // Unique boxId and known SKU
        if (repository.boxIdExists(boxId)) {
            return ValidationResult.error("Duplicate boxId: " + boxId + " in wagon: " + wagonId);
        }
        if (!repository.itemExists(sku)) {
            return ValidationResult.error("Unknown SKU: " + sku + " in wagon: " + wagonId);
        }

        // Quantity > 0
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) return ValidationResult.error("Invalid quantity (" + qty + ") in wagon: " + wagonId);
        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid quantity format in wagon: " + wagonId);
        }

        // Dates must match importWagons parsing contract
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // receivedAt required: ISO_LOCAL_DATE_TIME
        try {
            LocalDateTime.parse(receivedStr, dateTimeFormatter);
        } catch (Exception e) {
            return ValidationResult.error("Invalid receivedAt date-time format: " + receivedStr);
        }

        // expiryDate optional: accepts ISO date or parsed as yyyy-MM-dd
        if (!expiryStr.isEmpty()) {
            try {
                try {
                    LocalDateTime.parse(expiryStr + "T00:00:00", dateTimeFormatter).toLocalDate();
                } catch (DateTimeParseException e) {
                    LocalDate.parse(expiryStr, dateFormatter);
                }
            } catch (Exception e) {
                return ValidationResult.error("Invalid expiryDate: " + expiryStr);
            }
        }

        return ValidationResult.success();
    }

    /**
     * Validates an order record from CSV data.
     *
     * @param fields the array of field values from the CSV line
     * @param lineNumber the line number being validated (for error reporting)
     * @return a ValidationResult indicating success or containing an error message
     */
    private ValidationResult validateOrder(String[] fields, int lineNumber) {
        String orderId = fields[0].trim();

        try {
            LocalDateTime.parse(fields[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return ValidationResult.error("Invalid dueDate for order: " + orderId);
        }

        try {
            int priority = Integer.parseInt(fields[2].trim());
            if (priority < 0) {
                return ValidationResult.error("Priority cannot be negative for order: " + orderId);
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid priority for order: " + orderId);
        }

        return ValidationResult.success();
    }

    /**
     * Validates an order line record from CSV data.
     * <p>
     * Ensures that the referenced order and SKU exist in the repository.
     * </p>
     *
     * @param fields the array of field values from the CSV line
     * @param lineNumber the line number being validated (for error reporting)
     * @return a ValidationResult indicating success or containing an error message
     */
    private ValidationResult validateOrderLine(String[] fields, int lineNumber) {
        String orderId = fields[0].trim();
        String sku = fields[2].trim();

        // Check order exists
        if (!repository.orderExists(orderId)) {
            return ValidationResult.error("Unknown orderId: " + orderId + " in order line");
        }

        // Check SKU exists
        if (!repository.itemExists(sku)) {
            return ValidationResult.error("Unknown SKU: " + sku + " in order line for order: " + orderId);
        }

        try {
            int lineNo = Integer.parseInt(fields[1].trim());
            int qty = Integer.parseInt(fields[3].trim());

            if (lineNo < 0) {
                return ValidationResult.error("LineNo cannot be negative for order: " + orderId);
            }
            if (qty <= 0) {
                return ValidationResult.error("Invalid quantity (" + qty + ") in order: " + orderId + ", line: " + lineNo);
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid numeric value in order line for order: " + orderId);
        }

        return ValidationResult.success();
    }

    /**
     * Validates a return record from CSV data.
     * <p>
     * Validates SKU existence, quantity, reason, timestamp, and optional expiry date.
     * </p>
     *
     * @param fields the array of field values from the CSV line
     * @param lineNumber the line number being validated (for error reporting)
     * @return a ValidationResult indicating success or containing an error message
     */
    private ValidationResult validateReturn(String[] fields, int lineNumber) {
        String returnId = fields[0].trim();
        String sku = fields[1].trim();

        // SKU must exist
        if (!repository.itemExists(sku)) {
            return ValidationResult.error("Unknown SKU: " + sku + " in returnId: " + returnId);
        }

        // Quantity must be positive
        try {
            int qty = Integer.parseInt(fields[2].trim());
            if (qty <= 0) {
                return ValidationResult.error("Invalid quantity (" + qty + ") in returnId: " + returnId);
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error("Invalid quantity format in returnId: " + returnId);
        }

        // Reason cannot be empty
        if (fields[3].trim().isEmpty()) {
            return ValidationResult.error("Missing reason in returnId: " + returnId);
        }

        // Timestamp must be valid
        try {
            LocalDateTime.parse(fields[4].trim());
        } catch (Exception e) {
            return ValidationResult.error("Invalid or missing timestamp in returnId: " + returnId);
        }

        // expiryDate (optional) must be valid
        if (fields.length > 5 && !fields[5].trim().isEmpty()) {
            try {
                LocalDate.parse(fields[5].trim());
            } catch (Exception e) {
                return ValidationResult.error("Invalid expiryDate in returnId: " + returnId);
            }
        }

        return ValidationResult.success();
    }

    /**
     * Logs an error message for a specific line and increments the invalid record counter.
     *
     * @param lineNumber the line number where the error occurred
     * @param message the error message describing the issue
     */
    private void logError(int lineNumber, String message) {
        String error = "Line " + lineNumber + ": " + message;
        errors.add(error);
        invalidRecords++;
        // comentar a linha abaixo para evitar mensagens a vermelho durante a importação
        // System.err.println("ERROR - " + error);
    }

    /**
     * Prints a summary of the import operation to the console.
     * <p>
     * Displays the number of valid records imported, invalid records rejected,
     * and detailed error messages for each rejected record.
     * </p>
     */
    public void printSummary() {
        System.out.println("\n=== IMPORT SUMMARY ===");
        System.out.println("Valid records imported: " + validRecords);
        System.out.println("Invalid records rejected: " + invalidRecords);
        System.out.println("Total errors: " + errors.size());

        if (!errors.isEmpty()) {
            System.out.println("\nDetailed errors:");
            for (String error : errors) {
                System.out.println("  - " + error);
            }
        }
    }
}