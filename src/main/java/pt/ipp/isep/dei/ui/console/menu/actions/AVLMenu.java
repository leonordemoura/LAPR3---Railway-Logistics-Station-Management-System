package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ui.console.menu.MenuItem;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AVLMenu implements Runnable {

	@Override
	public void run() {
		WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
		CSVImporter importer = new CSVImporter(repository);
		System.out.println("\n=== train stations unloading ===");
		// Import only if not already loaded
		if (repository.getTrainStations().isEmpty()) {
			long start = System.nanoTime();
			importer.importTrainStations("dataFiles/train_stations_europe.csv");
			long end = System.nanoTime();
			importer.printSummary();
			System.out.println("Tempo total de importação + inserção nas 3 AVL: "
					+ (end - start) / 1_000_000 + " ms");
		} else {
			System.out.println("Trains stations already initialized (boxes exist).");
		}

		List<MenuItem> options = new ArrayList<>();
		options.add(new MenuItem("Ver AVL de Latitude", new PrintLatitudeAVLUI()));
		options.add(new MenuItem("Ver AVL de Longitude", new PrintLongitudeAVLUI()));
		options.add(new MenuItem("Ver AVL de TimeZone", new PrintTimezoneAVLUI()));
		options.add(new MenuItem("Ver todas as AVL", new ListAllAVL()));

		int option = 0;
		do {
			option = Utils.showAndSelectIndex(options, "\n\n--- AVL MENU -------------------------");

			if ((option >= 0) && (option < options.size())) {
				options.get(option).run();
			}
		} while (option != -1);
	}
}



