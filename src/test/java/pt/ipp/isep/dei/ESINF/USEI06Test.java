package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.ESINF.dataStruc.AVL;
import pt.ipp.isep.dei.ESINF.domain.*;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.InventoryService;
import pt.ipp.isep.dei.ESINF.services.impl.InventoryServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class USEI06Test {

    private WarehouseRepository repo;
    private CSVImporter importer;

    @BeforeEach
    public void setUp() {
        repo = new WarehouseRepository();
        importer = new CSVImporter(repo);
    }

    @AfterEach
    void tearDown() {
        importer.printSummary();
    }

    @Test
    public void test_import_valid_stations_lat_long_null() throws Exception {
        importer.importTrainStations("dataFiles/test_trainstation1.csv");

        //Total expected (remover linhas com latitude/longitude vazias )
        //linhas 13, 14
        assertEquals(14, repo.getTrainStations().size());
    }

    @Test
    public void test_import_valid_stations_lat_long_range() throws Exception {
        importer.importTrainStations("dataFiles/test_trainstation2.csv");

        //Total expected (remover linhas com latitude/longitude fora da range)
        //linhas 13, 14
        assertEquals(14, repo.getTrainStations().size());
    }

    @Test
    public void test_import_valid_stations_mandatory_fileds_null() throws Exception {
        importer.importTrainStations("dataFiles/test_trainstation3.csv");

        //Total expected (remover linhas com mandatory fileds vazias)
        //linha 13 -> station
        //linha 14 -> country
        //linha 15 -> timezonegroup
        assertEquals(13, repo.getTrainStations().size());
    }

    @Test
    public void test_latitude_avl_is_sorted() throws Exception {
        importer.importTrainStations("dataFiles/test_trainstation.csv");
        //importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<Double> tree = repo.getLatitudeIndex();

        List<Double> ordered = new ArrayList<>();
        //o inOrder da-me em ordem crescente
        for (Double d : tree.inOrder()) {
            ordered.add(d);
        }

        // Verificar ordem crescente
        for (int i = 1; i < ordered.size(); i++) {
            //ver se a posição anterior é mais pequena que a seguinte
            assertTrue(ordered.get(i - 1) <= ordered.get(i));
        }
        System.out.println(ordered);

        AVL.printTree(tree.root(), 0);
    }

    @Test
    public void test_longitude_avl_is_sorted() throws Exception {
        importer.importTrainStations("dataFiles/test_trainstation.csv");
        //importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<Double> tree = repo.getLongitudeIndex();

        List<Double> ordered = new ArrayList<>();
        //o inOrder da-me em ordem crescente
        for (Double d : tree.inOrder()) {
            ordered.add(d);
        }

        // Verificar ordem crescente
        for (int i = 1; i < ordered.size(); i++) {
            //ver se a posição anterior é mais pequena que a seguinte
            assertTrue(ordered.get(i - 1) <= ordered.get(i));
        }
        System.out.println(ordered);
        AVL.printTree(tree.root(), 0);
    }

    @Test
    public void test_timezone_country_avl_is_sorted() throws Exception {
        importer.importTrainStations("dataFiles/test_trainstation.csv");
        //importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<String> tree = repo.getTimeZoneCountryIndex();

        List<String> ordered = new ArrayList<>();
        //o inOrder da-me em ordem crescente
        for (String d : tree.inOrder()) {
            ordered.add(d);
        }

        // Verificar ordem crescente
        for (int i = 1; i < ordered.size(); i++) {
            //ver se a posição anterior é mais pequena que a seguinte
            assertTrue(ordered.get(i - 1).compareTo(ordered.get(i))<0);
        }
        System.out.println(ordered);
        AVL.printTree(tree.root(), 0);

    }

    @Test
    public void test_search_latitude() {
        importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<Double> tree = repo.getLatitudeIndex();

        List<TrainStation> stations = tree.searchExact(38.71387);

        assertFalse(stations.isEmpty());

        for (TrainStation st : stations) {
            System.out.println(st);
        }
        // searchExact:
        // 1) findNode na AVL de latitude: O(log n)
        // 2) collectStations na inner AVL de TrainStation: O(m), m = nº de estações neste latitude
        // Total: O(log n + m)
    }

    @Test
    public void test_search_longitude() {
        importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<Double> tree = repo.getLongitudeIndex();

        List<TrainStation> stations = tree.searchExact(-9.122271); // ex: Lisboa

        assertFalse(stations.isEmpty());

        for (TrainStation st : stations) {
            assertEquals(-9.122271, st.getCoordinates().getLongitude(), 0.00001);
            System.out.println(st);
        }
    }

    @Test
    public void test_search_timezone_single() {
        importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<String> tree = repo.getTimeZoneCountryIndex();

        List<TrainStation> stations = tree.searchExact("CET");

        assertFalse(stations.isEmpty());

        for (TrainStation st : stations) {
            assertEquals("CET", st.getTimeZoneGroup());
            System.out.println(st);
        }
    }

    @Test
    public void test_search_timezone_multiple() {
        importer.importTrainStations("dataFiles/train_stations_europe.csv");

        AVL<String> tree = repo.getTimeZoneCountryIndex();

        String[] timeZones = {"CET", "WET/GMT"};
        List<TrainStation> results = new ArrayList<>();

        // Buscar todas as estações para cada timeZone
        for (String tz : timeZones) {
            results.addAll(tree.searchExact(tz));
            // searchExact: O(log n + m) por timeZone
        }

        assertFalse(results.isEmpty());

        // Verificar se cada estação pertence a algum dos timeZones
        for (TrainStation st : results) {
            boolean match = false;
            for (String tz : timeZones) {
                if (st.getTimeZoneGroup().equals(tz)) {
                    match = true;
                    break;
                }
            }
            assertTrue(match);
            System.out.println(st);
        }
    }

}
