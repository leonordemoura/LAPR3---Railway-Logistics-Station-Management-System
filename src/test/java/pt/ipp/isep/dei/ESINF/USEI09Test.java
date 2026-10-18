package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import pt.ipp.isep.dei.ESINF.controller.BuildTreeController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * USEI09 - Proximity Search: 10 Essential Tests
 * Uses real data from train_stations_europe.csv (62,142 stations)
 * No mocks, no synthetic data
 */
@DisplayName("USEI09 - Proximity Search (10 Essential Tests)")
public class USEI09Test {

    private static final String CSV_FILE = "dataFiles/train_stations_europe.csv";
    private static WarehouseRepository repository;
    private static KDTree<TrainStation> kdTree;
    private static List<TrainStation> stations;

    @BeforeAll
    public static void setUp() {
        repository = Repositories.getInstance().getWarehouseRepository();
        CSVImporter importer = new CSVImporter(repository);
        importer.importTrainStations(CSV_FILE);

        BuildTreeController buildController = new BuildTreeController();
        stations = repository.getTrainStations();

        if (stations != null && !stations.isEmpty()) {
            kdTree = buildController.buildBalancedTree();
            repository.setKDTreeStation(kdTree);
        }
    }

    // ==================== TEST 1: Haversine Formula ====================
    @Test
    @DisplayName("Test 1 - Haversine: Distância zero para coordenadas iguais")
    public void test01_haversine_same_point() {
        double distance = ProximitySearch.haversineDistance(40.0, -8.0, 40.0, -8.0);
        assertEquals(0.0, distance, 0.0001, "Mesma coordenada = distância zero");
    }

    // ==================== TEST 2: Haversine Symmetry ====================
    @Test
    @DisplayName("Test 2 - Haversine: Comutatividade")
    public void test02_haversine_symmetry() {
        double d1 = ProximitySearch.haversineDistance(38.7223, -9.1393, 40.4168, -3.7038);
        double d2 = ProximitySearch.haversineDistance(40.4168, -3.7038, 38.7223, -9.1393);
        assertEquals(d1, d2, 0.0001, "Haversine deve ser simétrica");
    }

    // ==================== TEST 3: Haversine Earth Radius ====================
    @Test
    @DisplayName("Test 3 - Haversine: Usa raio terrestre correto")
    public void test03_haversine_earth_radius() {
        // 1 grau no equador ≈ 111 km
        double distance = ProximitySearch.haversineDistance(0.0, 0.0, 1.0, 0.0);
        assertTrue(distance > 110 && distance < 112,
                "1 grau deve ser ~111km, obteve: " + distance);
    }

    // ==================== TEST 4: CSV Data Import ====================
    @Test
    @DisplayName("Test 4 - CSV: Dados importados com sucesso")
    public void test04_csv_import_success() {
        assertNotNull(stations, "Estações não podem ser null");
        assertTrue(stations.size() > 0, "Deve ter importado estações");
        assertTrue(stations.size() > 60000, "Deve ter ~62k estações");
        assertNotNull(kdTree, "KD-tree não pode ser null");
    }

    // ==================== TEST 5: Find N Nearest ====================
    @Test
    @DisplayName("Test 5 - Find N: Retorna N vizinhos mais próximos")
    public void test05_find_n_nearest() {
        List<ProximitySearch.StationDistance> results =
                ProximitySearch.findNearestN(kdTree, 40.0, -8.0, 5, null);

        assertEquals(5, results.size(), "Deve retornar exatamente 5 vizinhos");
        assertTrue(results.stream().allMatch(r -> r.distance >= 0),
                "Todas distâncias devem ser não-negativas");
    }

    // ==================== TEST 6: Results Sorted ====================
    @Test
    @DisplayName("Test 6 - Sorting: Resultados em ordem crescente")
    public void test06_results_sorted_ascending() {
        List<ProximitySearch.StationDistance> results =
                ProximitySearch.findNearestN(kdTree, 38.7223, -9.1393, 20, null);

        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).distance <= results.get(i + 1).distance,
                    "Resultados devem estar em ordem crescente");
        }
    }

    // ==================== TEST 7: Timezone Filter CET ====================
    @Test
    @DisplayName("Test 7 - Filter: Timezone CET")
    public void test07_filter_cet() {
        List<ProximitySearch.StationDistance> results =
                ProximitySearch.findNearestN(kdTree, 48.8566, 2.3522, 10, "CET");

        assertTrue(results.stream()
                        .allMatch(sd -> sd.station.getTimeZoneGroupAndCountry().equalsIgnoreCase("CET")),
                "Todas estações devem ter timezone CET");
    }

    // ==================== TEST 8: Timezone Filter WET/GMT ====================
    @Test
    @DisplayName("Test 8 - Filter: Timezone WET/GMT")
    public void test08_filter_wet_gmt() {
        List<ProximitySearch.StationDistance> results =
                ProximitySearch.findNearestN(kdTree, 40.0, -8.0, 10, "WET/GMT");

        assertTrue(results.stream()
                        .allMatch(sd -> sd.station.getTimeZoneGroupAndCountry().equalsIgnoreCase("WET/GMT")),
                "Todas estações devem ter timezone WET/GMT");
    }

    // ==================== TEST 9: No Filter vs Filter ====================
    @Test
    @DisplayName("Test 9 - Filter: Sem filtro retorna mais resultados")
    public void test09_no_filter_vs_filter() {
        List<ProximitySearch.StationDistance> noFilter =
                ProximitySearch.findNearestN(kdTree, 40.0, -8.0, 10, null);

        List<ProximitySearch.StationDistance> withFilter =
                ProximitySearch.findNearestN(kdTree, 40.0, -8.0, 10, "CET");

        assertTrue(noFilter.size() >= withFilter.size(),
                "Sem filtro deve ter >= resultados que com filtro");
    }

    // ==================== TEST 10: results ascending by distance ====================
    @Test
    @DisplayName("Resultados ordenados por distância")
    public void testResultsSortedByDistance() {
        List<ProximitySearch.StationDistance> results =
                ProximitySearch.findNearestN(kdTree, 38.7223, -9.1393, 20, null);

        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).distance <= results.get(i + 1).distance,
                    "Resultados devem estar em ordem crescente de distância");
        }
    }

}