package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.controller.PickingPathController;
import pt.ipp.isep.dei.ESINF.controller.PickingPlanController;
import pt.ipp.isep.dei.ESINF.domain.Location;
import pt.ipp.isep.dei.ESINF.domain.PickingItem;
import pt.ipp.isep.dei.ESINF.domain.PickingPlan;
import pt.ipp.isep.dei.ESINF.domain.Trolley;
import pt.ipp.isep.dei.ESINF.services.impl.PickingPathServiceImpl;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PickPathUI implements Runnable {
    private static PickingPathServiceImpl pickingPathServiceimpl;
    private static PickingPathController pickingPathController;
    private static Scanner scanner;

    /**
     * Constructor receives the existing OrderAllocationImpl.
     *
     */
    public PickPathUI () {
        pickingPathServiceimpl = new PickingPathServiceImpl();
        pickingPathController = new PickingPathController();
        scanner = new Scanner(System.in);
    }

    public void run() {

        System.out.println(Utils.createHeader("USEI04: Pick Path Sequencing"));
        PickingPlan pickingPlan = PickingPlanController.getLastPickingPlan();

        if (pickingPlan == null) {
            System.out.println("No picking plan available. Please run USEI03 first.");
            return;
        }

        List<Trolley> trolleysFromPlan = pickingPlan.getTrolleys();


        // em vez de:
// List<Location> allLocations = pickingPathController.getLocationsForTrolley(trolleysFromPlan);

// faz isto:
        for (Trolley trolley : trolleysFromPlan) {
            System.out.println("\n==============================");
            System.out.println("Trolley: " + trolley.getTrolleyId());
            System.out.println("==============================");

            List<Location> locations = pickingPathController.getLocationsForTrolley(trolley);

            // ---------- Strategy A: Ascending ----------
            System.out.println("---------- Strategy A ----------");
            List<Location> pathA = pickingPathController.getAscendingPath(locations);
            System.out.print("Path: (0,0) ");
            for (Location loc : pathA) {
                System.out.printf("-> (%d,%d) ", loc.getAisle(), loc.getBay());
            }
            System.out.println();
            double distanceA = pickingPathController.calculateTotalDistance(pathA);
            System.out.printf("Total Distance: %.0f units%n%n", distanceA);

            // ---------- Strategy B: Nearest-Neighbour ----------
            System.out.println("---------- Strategy B ----------");
            List<Location> pathB = pickingPathController.getOptimalPath(locations);
            System.out.print("Path: (0,0) ");
            for (Location loc : pathB) {
                System.out.printf("-> (%d,%d) ", loc.getAisle(), loc.getBay());
            }
            System.out.println();
            double distanceB = pickingPathController.calculateTotalDistance(pathB);
            System.out.printf("Total Distance: %.0f units%n", distanceB);
        }
    }


        public void showTrolleyContents(Trolley trolley) {
        System.out.println("Picker view for trolley: " + trolley.getTrolleyId());
        for (PickingItem item : trolley.getItems()) {
            System.out.printf("%s,%d - quantity: %d%n",
                    item.getAisle(), item.getBay(), item.getQuantity());
        }
    }

}
