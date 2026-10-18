package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.RadiusSearchController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.services.RadiusSearchService;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.*;

public class RadiusSearchUI implements Runnable {

    private final RadiusSearchController controller;

    public RadiusSearchUI() {
        this.controller = new RadiusSearchController();
    }

    @Override
    public void run() {
        System.out.println("\n--- Radius Search ---\n");

        KDTree<TrainStation> kdTree = controller.getKDTree();
        if (kdTree == null || kdTree.isEmpty()) {
            System.out.println("ERROR: KD-tree not built. Run USEI07 first.\n");
            return;
        }

        float targetLat = Utils.readFloatFromConsole("Latitude: ", -90.0f, 90.0f);
        float targetLon = Utils.readFloatFromConsole("Longitude: ", -180.0f, 180.0f);
        float radiusKm = Utils.readFloatFromConsole("Radius (km): ", 0.01f, 10000.0f);

        RadiusSearchService.RadiusSearchResult result = 
            controller.findStationsWithinRadius(targetLat, targetLon, radiusKm);

        displayResults(result);
    }

    private void displayResults(RadiusSearchService.RadiusSearchResult result) {
        
        System.out.println("\n=== RESULTS ===");
        System.out.printf("Found: %d stations\n\n", result.getTotalStations());

        if (result.getTotalStations() == 0) {
            return;
        }

        System.out.println("Stations:");
        int i = 1;
        for (ProximitySearch.StationDistance sd : result.getStations()) {
            System.out.printf("%d. %s (%s) - %.2f km - %s\n",
                i++, sd.station.getStation(), sd.station.getCountry(), 
                sd.distance, sd.station.isCity() ? "City" : "Non-city");
        }

        System.out.println("\nBy Country:");
        result.getCountryCount().forEach((country, count) -> 
            System.out.printf("  %s: %d\n", country, count));

        System.out.println("\nBy Type:");
        System.out.printf("  City: %d\n", result.getCityCount().getOrDefault(true, 0));
        System.out.printf("  Non-city: %d\n\n", result.getCityCount().getOrDefault(false, 0));
    }
}
