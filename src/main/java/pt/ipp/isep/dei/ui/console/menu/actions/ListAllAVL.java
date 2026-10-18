package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ui.console.menu.MenuItem;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ListAllAVL implements Runnable {

	@Override
	public void run() {
		WarehouseRepository repository = Repositories.getInstance().getWarehouseRepository();
		Scanner sc = new Scanner(System.in);

		int option;

		do {
			System.out.println("\n=== AVL Viewer ===");
			System.out.println("1. Ver AVL de Latitude");
			System.out.println("2. Ver AVL de Longitude");
			System.out.println("3. Ver AVL de TimeZone");
			System.out.println("4. Ver todas as AVL");
			System.out.println("0. Voltar");

			System.out.print("\nEscolha: ");
			option = Integer.parseInt(sc.nextLine());

			switch (option) {
				case 1:
					printAVL("Latitude AVL", repository.getLatitudeIndex());
				case 2:
					printAVL("Longitude AVL", repository.getLongitudeIndex());
				case 3:
					printAVL("TimeZone AVL", repository.getTimeZoneCountryIndex());
				case 4: {
					printAVL("Latitude AVL", repository.getLatitudeIndex());
					printAVL("Longitude AVL", repository.getLongitudeIndex());
					printAVL("TimeZone AVL", repository.getTimeZoneCountryIndex());
				}
				case 0:
					System.out.println("A voltar...");
				default:
					System.out.println("Opção inválida");
			}

		} while (option != 0);
	}

	private void printAVL(String title, Object avl) {
		System.out.println("\n--- " + title + " ---");
		System.out.println(avl.toString());
	}
}



