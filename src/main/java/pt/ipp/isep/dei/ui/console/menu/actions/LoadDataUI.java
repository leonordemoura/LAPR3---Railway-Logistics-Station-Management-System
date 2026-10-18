package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ui.console.menu.MenuItem;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Console-based UI action for loading CSV data files.
 * <p>
 * Allows users to selectively choose which CSV files to import:
 * <ul>
 *     <li>Items</li>
 *     <li>Bays</li>
 *     <li>Wagons</li>
 *     <li>Orders</li>
 *     <li>Order Lines</li>
 *     <li>Returns</li>
 *     <li>Train Stations</li>
 * </ul>
 * <p>
 * Implements {@link Runnable} to allow execution as part of a menu option.
 * </p>
 */
public class LoadDataUI implements Runnable {

    /**
     * Represents a CSV import configuration with its name, check function, import action, and count supplier.
     */
    private static class CSVImportConfig {
        final String displayName;
        final BooleanSupplier isEmptyCheck;
        final Consumer<CSVImporter> importAction;
        final Supplier<Integer> countSupplier;
        final String csvTypeName;

        CSVImportConfig(String displayName, BooleanSupplier isEmptyCheck,
                       Consumer<CSVImporter> importAction, Supplier<Integer> countSupplier, String csvTypeName) {
            this.displayName = displayName;
            this.isEmptyCheck = isEmptyCheck;
            this.importAction = importAction;
            this.countSupplier = countSupplier;
            this.csvTypeName = csvTypeName;
        }
    }

    /**
     * Executes the data loading action.
     * <p>
     * Displays a menu allowing the user to select which CSV files to import.
     * After selection, imports the chosen files and displays a summary.
     * </p>
     */
    @Override
    public void run() {
        WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();

        System.out.println("\n=== Load CSV Data ===");
        System.out.println("Select which CSV files you want to load:");

        // Define all CSV import configurations
        List<CSVImportConfig> csvConfigs = createCSVConfigs(repository);

        // Create menu items from configurations
        List<MenuItem> csvOptions = new ArrayList<>();
        for (CSVImportConfig config : csvConfigs) {
            csvOptions.add(new MenuItem(config.displayName, 
                () -> importCSV(repository, config)));
        }

        // Add "Load All" option
        csvOptions.add(new MenuItem("Load All CSV Files", 
            () -> importAllCSVs(repository, csvConfigs)));

        // Display menu and handle user selection
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(csvOptions, "\n\n--- Load CSV Data Menu -------------------------");

            if ((option >= 0) && (option < csvOptions.size())) {
                csvOptions.get(option).run();
            }
        } while (option != -1);
    }

    /**
     * Creates the list of CSV import configurations.
     *
     * @param repository the warehouse repository
     * @return list of CSV import configurations
     */
    private List<CSVImportConfig> createCSVConfigs(WarehouseRepository repository) {
        List<CSVImportConfig> configs = new ArrayList<>();

        configs.add(new CSVImportConfig(
            "Items (items.csv)",
            () -> repository.getItems().isEmpty(),
            importer -> importer.importItems("dataFiles/items.csv"),
            () -> repository.getItems().size(),
            "Items"
        ));

        configs.add(new CSVImportConfig(
            "Bays (bays.csv)",
            () -> repository.getTotalBayCount() == 0,
            importer -> importer.importBays("dataFiles/bays.csv"),
            () -> repository.getTotalBayCount(),
            "Bays"
        ));

        configs.add(new CSVImportConfig(
            "Wagons (wagons.csv)",
            () -> repository.getWagons().isEmpty(),
            importer -> importer.importWagons("dataFiles/wagons.csv"),
            () -> repository.getWagons().size(),
            "Wagons"
        ));

        configs.add(new CSVImportConfig(
            "Orders (orders.csv)",
            () -> repository.getOrders().isEmpty(),
            importer -> importer.importOrders("dataFiles/orders.csv"),
            () -> repository.getOrders().size(),
            "Orders"
        ));

        configs.add(new CSVImportConfig(
            "Order Lines (order_lines.csv)",
            () -> repository.getOrderLines().isEmpty(),
            importer -> importer.importOrderLines("dataFiles/order_lines.csv"),
            () -> repository.getOrderLines().size(),
            "Order Lines"
        ));

        configs.add(new CSVImportConfig(
            "Returns (returns.csv)",
            () -> WarehouseRepository.getReturns().isEmpty(),
            importer -> importer.importReturns("dataFiles/returns.csv"),
            () -> WarehouseRepository.getReturns().size(),
            "Returns"
        ));

        configs.add(new CSVImportConfig(
            "Train Stations (train_stations_europe.csv)",
            () -> repository.getTrainStations().isEmpty(),
            importer -> importer.importTrainStations("dataFiles/train_stations_europe.csv"),
            () -> repository.getTrainStations().size(),
            "Train Stations"
        ));

        configs.add(new CSVImportConfig("Upload Graph (stations.csv & lines.csv)",
            () -> repository.getUndirectedGraph().vertices().isEmpty(),
            importer -> importer.importGraphs("dataFiles/stations.csv", "dataFiles/lines.csv"),
            () -> repository.getUndirectedGraph().vertices().size(),
            "Graph Data"));

        return configs;
    }

    /**
     * Imports a single CSV file if it hasn't been loaded yet.
     *
     * @param repository the warehouse repository
     * @param config the CSV import configuration
     */
    private void importCSV(WarehouseRepository repository, CSVImportConfig config) {
        if (!config.isEmptyCheck.getAsBoolean()) {
            System.out.println("\nWarning: " + config.csvTypeName + " CSV has already been uploaded!");
            System.out.println(config.csvTypeName + " currently loaded: " + config.countSupplier.get());
            return;
        }

        CSVImporter importer = new CSVImporter(repository);
        config.importAction.accept(importer);
        importer.printSummary();
        System.out.println("\n" + config.csvTypeName + " loaded successfully!");
    }

    /**
     * Imports all CSV files that haven't been loaded yet.
     *
     * @param repository the warehouse repository
     * @param configs the list of CSV import configurations
     */
    private void importAllCSVs(WarehouseRepository repository, List<CSVImportConfig> configs) {
        System.out.println("\n=== Loading all CSV files ===");
        CSVImporter importer = new CSVImporter(repository);
        boolean anyLoaded = false;

        for (CSVImportConfig config : configs) {
            if (config.isEmptyCheck.getAsBoolean()) {
                config.importAction.accept(importer);
                anyLoaded = true;
            } else {
                System.out.println("Warning: " + config.csvTypeName + " CSV has already been uploaded. Ignoring...");
            }
        }

        if (anyLoaded) {
            importer.printSummary();
        }

        System.out.println("\n=== DATA LOADED ===");
        for (CSVImportConfig config : configs) {
            System.out.println(config.csvTypeName + ": " + config.countSupplier.get());
        }
    }
}
