package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.ui.console.menu.MainMenuUI;
import pt.ipp.isep.dei.ui.console.utils.Bootstrap;

public class Main {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();

        try {
            System.out.println();
            MainMenuUI menu = new MainMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}