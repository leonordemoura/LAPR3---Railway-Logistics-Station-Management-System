package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Algorithms;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class DirectedLineUpgradePlanUI implements Runnable {

    @Override
    public void run() {

        System.out.println("\n=== DIRECTED LINE UPGRADE PLAN ===");

        WarehouseRepository repo = new WarehouseRepository();
        CSVImporter importer = new CSVImporter(repo);

        // Import test data
        importer.importStations("dataFiles/stations.csv");
        importer.importLines("dataFiles/lines.csv");

        Map<String, Object> result =
                Algorithms.topologSort(repo.getDirectedGraph());

        if (result == null || !result.containsKey("hasCycle")) {
            System.out.println("Error processing graph.");
            return;
        }

        boolean hasCycle = (Boolean) result.get("hasCycle");

        System.out.println("\n=== RESULT ===");

        if (hasCycle) {
            // Cycle case
            System.out.println("Cycle detected in the directed graph.");
            System.out.println("Upgrade plan is NOT possible.");
            System.out.println("\nStations involved in cycle:");

            @SuppressWarnings("unchecked")
            Set<Station> cycleVertices =
                    (Set<Station>) result.get("cycleVertices");

            if (cycleVertices != null && !cycleVertices.isEmpty()) {
                for (Station s : cycleVertices) {
                    System.out.println(" - " + s);
                }
            } else {
                System.out.println(" [No cycle vertices returned]");
            }

        } else {
            // Topological order case
            System.out.println("No cycles detected.");
            System.out.println("Valid topological upgrade order:");

            @SuppressWarnings("unchecked")
            LinkedList<Station> order =
                    (LinkedList<Station>) result.get("topsort");

            if (order != null && !order.isEmpty()) {
                int i = 1;
                for (Station s : order) {
                    System.out.println(" " + (i++) + ") " + s);
                }
            } else {
                System.out.println(" [Empty order]");
            }
        }

        System.out.println("\n=== END ===\n");
    }
}
