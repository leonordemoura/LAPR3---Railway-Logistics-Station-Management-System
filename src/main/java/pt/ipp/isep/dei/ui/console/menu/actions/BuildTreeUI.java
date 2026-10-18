package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.BuildTreeController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.*;

/**
 * UI action (USEI07) responsible for building a balanced K-D Tree containing
 * all train stations in the system and displaying detailed statistics about
 * the constructed tree.
 * <p>
 * This action:
 * <ul>
 *   <li>Retrieves all train stations from the repository</li>
 *   <li>Builds a balanced 2D tree (K-D Tree) using median splitting</li>
 *   <li>Stores the tree back in the repository for later spatial queries</li>
 *   <li>Displays construction time and tree structure analysis</li>
 * </ul>
 *
 * @author ESINF
 */
public class BuildTreeUI implements Runnable {

    private final BuildTreeController controller;

    /**
     * Creates a new instance of the UI action.
     * Initializes the controller that handles tree construction logic.
     */
    public BuildTreeUI() {
        this.controller = new BuildTreeController();
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("USEI07 - Build a Balanced 2D-Tree (K-D Tree)"));

        List<TrainStation> allStations = controller.getTrainStations();

        if (allStations == null || allStations.isEmpty()) {
            System.out.println("No train stations found in the system.");
            System.out.println("Please load station data before building the tree.");
            return;
        }

        System.out.println("Building balanced K-D Tree from " + allStations.size() + " train station(s)...");
        long startTime = System.nanoTime();

        KDTree<TrainStation> tree = controller.buildBalancedTree();

        // Store the tree for future use (e.g., nearest station queries)
        Repositories.getInstance().getWarehouseRepository().setKDTreeStation(tree);

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        System.out.println("K-D Tree successfully built and stored in repository!\n");

        displayTreeStatistics(allStations.size(), tree, durationMs);
    }

    /**
     * Displays comprehensive statistics about the constructed K-D Tree.
     *
     * @param totalStations   total number of train stations inserted
     * @param tree            the constructed K-D Tree
     * @param constructionTimeMs time taken to build the tree in milliseconds
     */
    private void displayTreeStatistics(int totalStations, KDTree<TrainStation> tree, double constructionTimeMs) {
        System.out.println("=== K-D TREE CONSTRUCTION REPORT ===");
        System.out.printf("Total number of stations          : %,d%n", totalStations);
        System.out.printf("Number of nodes in tree           : %,d%n", tree.size());
        System.out.printf("Tree height                       : %d%n", tree.height());
        System.out.printf("Construction time                 : %.3f ms%n", constructionTimeMs);
        System.out.println();

        // Bucket size distribution
        Map<Integer, Integer> bucketSizes = tree.getDistinctBucketSizes();
        if (bucketSizes.isEmpty()) {
            System.out.println("No bucket information available.");
            return;
        }

        System.out.println("Bucket size distribution (nodes with same coordinate):");
        List<Integer> sortedBucketSizes = new ArrayList<>(bucketSizes.keySet());
        Collections.sort(sortedBucketSizes);

        for (Integer size : sortedBucketSizes) {
            int count = bucketSizes.get(size);
            System.out.printf("   • Bucket size %2d → %4d node(s)%n", size, count);
        }

        // Optional: show balance quality
        int idealHeight = (int) Math.ceil(Math.log(totalStations + 1) / Math.log(2)) - 1;
        System.out.println();
        System.out.printf("Ideal height for %,d elements     : %d%n", totalStations, idealHeight);
        System.out.println(tree.height() <= idealHeight + 2
                ? "Tree is well-balanced!"
                : "Warning: Tree height suggests some imbalance (possible data clustering).");
        System.out.println("=====================================");
    }
}