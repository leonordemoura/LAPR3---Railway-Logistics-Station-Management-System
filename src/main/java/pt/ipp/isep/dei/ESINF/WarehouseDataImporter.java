package pt.ipp.isep.dei.ESINF;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

/**
 * Runnable task responsible for loading initial warehouse data from CSV files
 * into the application's {@link WarehouseRepository}.
 * <p>
 * This importer runs sequentially through all key datasets (items, bays, wagons,
 * orders, order lines, and returns), using the {@link CSVImporter} to parse
 * and persist the corresponding records.
 * </p>
 *
 * <p>
 * Expected file locations (relative to project root):
 * <ul>
 *   <li><code>dataFiles/items.csv</code></li>
 *   <li><code>dataFiles/bays.csv</code></li>
 *   <li><code>dataFiles/wagons.csv</code></li>
 *   <li><code>dataFiles/orders.csv</code></li>
 *   <li><code>dataFiles/order_lines.csv</code></li>
 *   <li><code>dataFiles/returns.csv</code></li>
 * </ul>
 *
 * <p>
 * After importing all files, a summary of import results and loaded data
 * counts is printed to the console.
 * </p>
 *
 * @see CSVImporter
 * @see WarehouseRepository
 * @see Repositories
 */
public class WarehouseDataImporter implements Runnable {

    /**
     * Executes the data import process.
     * <p>
     * This method obtains the singleton {@link WarehouseRepository} instance
     * from {@link Repositories}, creates a {@link CSVImporter}, and imports
     * all warehouse-related CSV files in the following order:
     * </p>
     *
     * <ol>
     *   <li>Items</li>
     *   <li>Bays</li>
     *   <li>Wagons</li>
     *   <li>Orders</li>
     *   <li>Order lines</li>
     *   <li>Returns</li>
     * </ol>
     *
     * <p>
     * After completion, the method prints both the import summary (from
     * {@link CSVImporter#printSummary()}) and a short data overview showing
     * the number of entities loaded into the repository.
     * </p>
     */
    @Override
    public void run() {
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
        CSVImporter importer = new CSVImporter(repository);

        importer.importItems("dataFiles/items.csv");
        importer.importBays("dataFiles/bays.csv");
        importer.importWagons("dataFiles/wagons.csv");
        importer.importOrders("dataFiles/orders.csv");
        importer.importOrderLines("dataFiles/order_lines.csv");
        importer.importReturns("dataFiles/returns.csv");
        importer.importTrainStations("dataFiles/train_stations_europe.csv");

        importer.printSummary();

        // Display loaded data summary
        System.out.println("\n=== DATA LOADED ===");
        System.out.println("Items: " + repository.getItems().size());
        System.out.println("Bays: " + repository.getTotalBayCount());
        System.out.println("Wagons: " + repository.getWagons().size());
        System.out.println("Orders: " + repository.getOrders().size());
        System.out.println("Orders Lines: " + repository.getOrderLines().size());
        System.out.println("Returns: " + repository.getReturns().size());
        System.out.println("Train Stations: " + repository.getTrainStations().size());
    }
}
