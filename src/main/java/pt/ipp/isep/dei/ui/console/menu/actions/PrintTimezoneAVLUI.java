package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

public class PrintTimezoneAVLUI implements Runnable {
    @Override
    public void run() {
        WarehouseRepository repo = Repositories.getInstance().getWarehouseRepository();
        System.out.println("\n--- Timezone and Country AVL ---");
        System.out.println(repo.getTimeZoneCountryIndex().toString());
    }
}
