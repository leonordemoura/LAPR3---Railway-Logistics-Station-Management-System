package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

/**
 * Console-based UI action for initializing stock by unloading wagons.
 * <p>
 * Implements {@link Runnable} to allow execution as part of a menu option.
 * </p>
 * <p>
 * When executed, this UI performs the following steps:
 * <ol>
 *     <li>Accesses the {@link WarehouseRepository} via {@link Repositories} singleton.</li>
 *     <li>If the repository has no boxes loaded yet, imports wagon data from the CSV file
 *     <code>dataFiles/wagons.csv</code> using {@link CSVImporter}.</li>
 *     <li>Prints a summary of imported boxes.</li>
 *     <li>If boxes are already present, it skips importing and notifies the user.</li>
 *     <li>Finally, displays the assigned contents of all bays in FEFO/FIFO order
 *     using {@link ListAllBaysUI}.</li>
 * </ol>
 */
public class StockInitializationUI implements Runnable {

	/**
	 * Executes the stock initialization action.
	 * <p>
	 * Ensures that boxes from wagons are imported into the repository if not already present,
	 * and displays the current inventory per bay.
	 * </p>
	 */
	@Override
	public void run() {
		WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
		CSVImporter importer = new CSVImporter(repository);
		System.out.println("\n=== Stock initialization (wagons unloading) ===");
		// Import only if not already loaded
		if (repository.getAllBoxes().isEmpty()) {
			importer.importWagons("dataFiles/wagons.csv");
			importer.printSummary();
		} else {
			System.out.println("Stock already initialized (boxes exist). Showing assigned contents per bay.");
		}

		// Always show the assigned contents per bay in FEFO/FIFO order
		new ListAllBaysUI().run();
	}
}


