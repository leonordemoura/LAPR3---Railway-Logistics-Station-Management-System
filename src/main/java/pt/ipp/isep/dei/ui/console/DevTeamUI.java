package pt.ipp.isep.dei.ui.console;

import static pt.ipp.isep.dei.ui.console.utils.AsciiArt.drawTrain;

/**
 * Class for the representation of the developing team.
 */
public class DevTeamUI implements Runnable {

    /**
     * Constructor of the class
     */
    public DevTeamUI() {
    }

    /**
     * Prints the teams' information
     */
    @Override
    public void run() {
        System.out.println("\n");
        drawTrain();
        System.out.println("--- ALL (DEVELOPERS) ABOARD! -------------------");
        System.out.println("  Francisco - 1241341@isep.ipp.pt");
        System.out.println("  Sofia - 1240898@isep.ipp.pt");
        System.out.println("  Leonor - 1240914@isep.ipp.pt");
        System.out.println("  Mariana - 1240905@isep.ipp.pt");
        System.out.println("  Rafael - 1210827@isep.ipp.pt");
        System.out.println("\n");
    }
}