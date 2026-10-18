package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.controller.BuildTreeController;
import pt.ipp.isep.dei.ESINF.controller.GeographicalSearchController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sample Queries Test for USEI08 - Search by Geographical Area
 * 
 * Este ficheiro contém 3-5 queries de exemplo conforme os critérios de aceitação.
 * Todas as queries usam dados reais do ficheiro train_stations_europe.csv.
 * 
 * Acceptance Criteria:
 * - The region: lat ∈ [latMin, latMax], lon ∈ [lonMin, lonMax] (inclusive)
 * - Optional filters: isCity (true | false), isMainStation (true | false), country (PT | ES | all)
 * - Returns: 3–5 sample queries; Temporal analysis complexity
 * 
 * Note: Usa @BeforeAll para importar CSV e construir KD-tree uma vez, partilhada por todos os testes.
 * Isto melhora significativamente o tempo de execução dos testes.
 */
public class GeographicalSearchSampleQueriesTest {

    private static final String CSV_FILE = "dataFiles/train_stations_europe.csv";
    private static GeographicalSearchController controller;
    private static WarehouseRepository repository;
    private static KDTree<TrainStation> kdTree;

    @BeforeAll
    public static void setUp() {
        Repositories.reset();
        repository = Repositories.getInstance().getWarehouseRepository();
        controller = new GeographicalSearchController();
        
        // Import train stations from CSV (feito uma vez para todos os testes)
        CSVImporter importer = new CSVImporter(repository);
        importer.importTrainStations(CSV_FILE);
        
        // Build KD-tree from imported stations (feito uma vez para todos os testes)
        BuildTreeController buildController = new BuildTreeController();
        List<TrainStation> stations = repository.getTrainStations();
        
        if (stations != null && !stations.isEmpty()) {
            kdTree = buildController.buildBalancedTree();
            repository.setKDTreeStation(kdTree);
        }
        
        // Verify setup
        assertNotNull(kdTree, "KD-tree should be built");
        assertFalse(kdTree.isEmpty(), "KD-tree should not be empty");
        assertTrue(repository.getTrainStations().size() > 0, "Should have imported stations from CSV");
    }

    /**
     * Query de Exemplo 1: Encontrar todas as cidades principais em Portugal
     * 
     * Use Case: Um planner quer identificar todos os hubs ferroviários urbanos principais
     * em Portugal para planear ligações intercidades.
     * 
     * Query Parameters:
     * - Region: Portugal (lat: 36.8-42.2, lon: -9.5 to -6.2)
     * - Country Filter: PT
     * - isCity Filter: true (apenas cidades)
     * - isMainStation Filter: true (apenas estações principais)
     * 
     * Expected: Retorna todas as cidades portuguesas que são estações principais
     */
    @Test
    public void queryExemplo1_TodasCidadesPrincipaisPortugal() {
        System.out.println("\n=== Query de Exemplo 1: Cidades Principais em Portugal ===");
        
        long startTime = System.nanoTime();
        
        List<TrainStation> result = controller.searchInRange(
                36.8, 42.2,  // Bounds de Portugal (latitude)
                -9.5, -6.2,  // Bounds de Portugal (longitude)
                "PT",         // Filtro país: Portugal
                true,         // isCity: true (apenas cidades)
                true);        // isMainStation: true (apenas estações principais)
        
        long endTime = System.nanoTime();
        double elapsedTimeMs = (endTime - startTime) / 1_000_000.0;
        
        // Display results
        System.out.println("Query executada em " + String.format("%.3f", elapsedTimeMs) + " ms");
        System.out.println("Encontradas " + result.size() + " estações:");
        
        // Mostrar primeiras 10 estações
        result.stream().limit(10).forEach(station -> {
            System.out.println("  - " + station.getStation() + 
                " (" + station.getCoordinates().getLatitude() + ", " + 
                station.getCoordinates().getLongitude() + ")");
        });
        
        if (result.size() > 10) {
            System.out.println("  ... e mais " + (result.size() - 10) + " estações");
        }
        
        // Assertions
        assertNotNull(result, "Resultado não deve ser null");
        
        if (result.size() > 0) {
            // Verificar que todos os resultados correspondem aos filtros
            result.forEach(station -> {
                assertEquals("PT", station.getCountry().toUpperCase(), 
                    "Todas as estações devem ser de Portugal");
                assertTrue(station.isCity(), 
                    "Todas as estações devem ser cidades: " + station.getStation());
                assertTrue(station.isMainStation(), 
                    "Todas as estações devem ser estações principais: " + station.getStation());
                
                // Verificar intervalo geográfico
                double lat = station.getCoordinates().getLatitude();
                double lon = station.getCoordinates().getLongitude();
                assertTrue(lat >= 36.8 && lat <= 42.2, 
                    "Latitude deve estar dentro dos bounds de Portugal");
                assertTrue(lon >= -9.5 && lon <= -6.2, 
                    "Longitude deve estar dentro dos bounds de Portugal");
            });
        }
        
        System.out.println("Query 1 completada com sucesso.\n");
    }

    /**
     * Query de Exemplo 2: Encontrar todas as estações não-cidade no Norte de Espanha
     * 
     * Use Case: Um planner precisa identificar estações rurais ou suburbanas no norte de Espanha
     * para planear rotas locais.
     * 
     * Query Parameters:
     * - Region: Norte de Espanha (lat: 41.0-43.5, lon: -9.5 to -1.0)
     * - Country Filter: ES
     * - isCity Filter: false (não-cidades)
     * - isMainStation Filter: null (todos os tipos de estação)
     * 
     * Expected: Retorna todas as estações não-cidade no norte de Espanha
     */
    @Test
    public void queryExemplo2_EstacoesNaoCidadeNorteEspanha() {
        System.out.println("\n=== Query de Exemplo 2: Estações Não-Cidade no Norte de Espanha ===");
        
        long startTime = System.nanoTime();
        
        List<TrainStation> result = controller.searchInRange(
                41.0, 43.5,  // Bounds do Norte de Espanha (latitude)
                -9.5, -1.0,  // Bounds do Norte de Espanha (longitude)
                "ES",         // Filtro país: Espanha
                false,        // isCity: false (não-cidades)
                null);        // isMainStation: null (todos os tipos)
        
        long endTime = System.nanoTime();
        double elapsedTimeMs = (endTime - startTime) / 1_000_000.0;
        
        // Display results
        System.out.println("Query executada em " + String.format("%.3f", elapsedTimeMs) + " ms");
        System.out.println("Encontradas " + result.size() + " estações:");
        
        // Mostrar primeiras 10 estações
        result.stream().limit(10).forEach(station -> {
            System.out.println("  - " + station.getStation() + 
                " (Principal: " + station.isMainStation() + 
                ", " + station.getCoordinates().getLatitude() + ", " + 
                station.getCoordinates().getLongitude() + ")");
        });
        
        if (result.size() > 10) {
            System.out.println("  ... e mais " + (result.size() - 10) + " estações");
        }
        
        // Assertions
        assertNotNull(result, "Resultado não deve ser null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertEquals("ES", station.getCountry().toUpperCase(), 
                    "Todas as estações devem ser de Espanha");
                assertFalse(station.isCity(), 
                    "Todas as estações devem ser não-cidades: " + station.getStation());
                
                // Verificar intervalo geográfico
                double lat = station.getCoordinates().getLatitude();
                double lon = station.getCoordinates().getLongitude();
                assertTrue(lat >= 41.0 && lat <= 43.5, 
                    "Latitude deve estar dentro dos bounds do Norte de Espanha");
                assertTrue(lon >= -9.5 && lon <= -1.0, 
                    "Longitude deve estar dentro dos bounds do Norte de Espanha");
            });
        }
        
        System.out.println("Query 2 completada com sucesso.\n");
    }

    /**
     * Query de Exemplo 3: Encontrar todas as estações na região fronteiriça Ibérica
     * 
     * Use Case: Um planner quer analisar estações perto da fronteira Portugal-Espanha
     * para planear rotas transfronteiriças, independentemente do país.
     * 
     * Query Parameters:
     * - Region: Região fronteiriça Ibérica (lat: 41.0-42.5, lon: -9.0 to -6.0)
     * - Country Filter: "all" (todos os países)
     * - isCity Filter: null (todos os tipos)
     * - isMainStation Filter: null (todos os tipos)
     * 
     * Expected: Retorna todas as estações na região fronteiriça de ambos os países
     */
    @Test
    public void queryExemplo3_TodasEstacoesRegiaoFronteiricaIberica() {
        System.out.println("\n=== Query de Exemplo 3: Todas as Estações na Região Fronteiriça Ibérica ===");
        
        long startTime = System.nanoTime();
        
        List<TrainStation> result = controller.searchInRange(
                41.0, 42.5,  // Bounds da região fronteiriça (latitude)
                -9.0, -6.0,  // Bounds da região fronteiriça (longitude)
                "all",        // Filtro país: todos os países
                null,         // isCity: null (todos os tipos)
                null);        // isMainStation: null (todos os tipos)
        
        long endTime = System.nanoTime();
        double elapsedTimeMs = (endTime - startTime) / 1_000_000.0;
        
        // Agrupar por país
        long ptCount = result.stream()
            .filter(s -> s.getCountry().equalsIgnoreCase("PT"))
            .count();
        long esCount = result.stream()
            .filter(s -> s.getCountry().equalsIgnoreCase("ES"))
            .count();
        long otherCount = result.size() - ptCount - esCount;
        
        // Display results
        System.out.println("Query executada em " + String.format("%.3f", elapsedTimeMs) + " ms");
        System.out.println("Encontradas " + result.size() + " estações:");
        System.out.println("  - Portugal (PT): " + ptCount);
        System.out.println("  - Espanha (ES): " + esCount);
        if (otherCount > 0) {
            System.out.println("  - Outros países: " + otherCount);
        }
        
        // Mostrar exemplos
        System.out.println("\nExemplos de estações:");
        result.stream().limit(10).forEach(station -> {
            System.out.println("  - " + station.getStation() + 
                " (" + station.getCountry() + 
                ", Cidade: " + station.isCity() + 
                ", Principal: " + station.isMainStation() + ")");
        });
        
        // Assertions
        assertNotNull(result, "Resultado não deve ser null");

        result.forEach(station -> {
            // Verificar intervalo geográfico
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 41.0 && lat <= 42.5, 
                "Latitude deve estar dentro dos bounds da região fronteiriça");
            assertTrue(lon >= -9.0 && lon <= -6.0, 
                "Longitude deve estar dentro dos bounds da região fronteiriça");
        });
        
        System.out.println("Query 3 completada com sucesso.\n");
    }

    /**
     * Query de Exemplo 4: Encontrar todas as estações principais no Centro de Espanha
     * 
     * Use Case: Um planner precisa identificar todos os hubs ferroviários principais no centro de Espanha
     * para entender a rede de infraestrutura ferroviária.
     * 
     * Query Parameters:
     * - Region: Centro de Espanha (lat: 39.0-42.0, lon: -6.0 to -2.0)
     * - Country Filter: ES
     * - isCity Filter: null (todos os tipos)
     * - isMainStation Filter: true (apenas estações principais)
     * 
     * Expected: Retorna todas as estações principais no centro de Espanha, independentemente do status de cidade
     */
    @Test
    public void queryExemplo4_TodasEstacoesPrincipaisCentroEspanha() {
        System.out.println("\n=== Query de Exemplo 4: Todas as Estações Principais no Centro de Espanha ===");
        
        long startTime = System.nanoTime();
        
        List<TrainStation> result = controller.searchInRange(
                39.0, 42.0,  // Bounds do Centro de Espanha (latitude)
                -6.0, -2.0,  // Bounds do Centro de Espanha (longitude)
                "ES",         // Filtro país: Espanha
                null,         // isCity: null (todos os tipos)
                true);        // isMainStation: true (apenas estações principais)
        
        long endTime = System.nanoTime();
        double elapsedTimeMs = (endTime - startTime) / 1_000_000.0;
        
        // Agrupar por status de cidade
        long cityCount = result.stream()
            .filter(TrainStation::isCity)
            .count();
        long nonCityCount = result.size() - cityCount;
        
        // Display results
        System.out.println("Query executada em " + String.format("%.3f", elapsedTimeMs) + " ms");
        System.out.println("Encontradas " + result.size() + " estações principais:");
        System.out.println("  - Cidades: " + cityCount);
        System.out.println("  - Não-cidades: " + nonCityCount);
        
        // Mostrar exemplos
        System.out.println("\nExemplos de estações:");
        result.stream().limit(10).forEach(station -> {
            System.out.println("  - " + station.getStation() + 
                " (Cidade: " + station.isCity() + 
                ", " + station.getCoordinates().getLatitude() + ", " + 
                station.getCoordinates().getLongitude() + ")");
        });
        
        // Assertions
        assertNotNull(result, "Resultado não deve ser null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                assertEquals("ES", station.getCountry().toUpperCase(), 
                    "Todas as estações devem ser de Espanha");
                assertTrue(station.isMainStation(), 
                    "Todas as estações devem ser estações principais: " + station.getStation());
                
                // Verificar intervalo geográfico
                double lat = station.getCoordinates().getLatitude();
                double lon = station.getCoordinates().getLongitude();
                assertTrue(lat >= 39.0 && lat <= 42.0, 
                    "Latitude deve estar dentro dos bounds do Centro de Espanha");
                assertTrue(lon >= -6.0 && lon <= -2.0, 
                    "Longitude deve estar dentro dos bounds do Centro de Espanha");
            });
        }
        
        System.out.println("Query 4 completada com sucesso.\n");
    }

    /**
     * Query de Exemplo 5: Encontrar todas as estações na área metropolitana do Porto
     * 
     * Use Case: Um planner quer analisar todas as estações na área metropolitana do Porto
     * para otimização de rotas locais, incluindo cidades e subúrbios.
     * 
     * Query Parameters:
     * - Region: Área metropolitana do Porto (lat: 41.0-41.3, lon: -8.7 to -8.5)
     * - Country Filter: null (todos os países, mas principalmente PT nesta região)
     * - isCity Filter: null (todos os tipos)
     * - isMainStation Filter: null (todos os tipos)
     * 
     * Expected: Retorna todas as estações na área do Porto
     */
    @Test
    public void queryExemplo5_TodasEstacoesAreaMetropolitanaPorto() {
        System.out.println("\n=== Query de Exemplo 5: Todas as Estações na Área Metropolitana do Porto ===");
        
        long startTime = System.nanoTime();
        
        List<TrainStation> result = controller.searchInRange(
                41.0, 41.3,  // Bounds da área metropolitana do Porto (latitude)
                -8.7, -8.5,  // Bounds da área metropolitana do Porto (longitude)
                null,         // Filtro país: null (todos os países)
                null,         // isCity: null (todos os tipos)
                null);        // isMainStation: null (todos os tipos)
        
        long endTime = System.nanoTime();
        double elapsedTimeMs = (endTime - startTime) / 1_000_000.0;
        
        // Estatísticas
        long cityCount = result.stream().filter(TrainStation::isCity).count();
        long mainStationCount = result.stream().filter(TrainStation::isMainStation).count();
        long ptCount = result.stream()
            .filter(s -> s.getCountry().equalsIgnoreCase("PT"))
            .count();
        
        // Display results
        System.out.println("Query executada em " + String.format("%.3f", elapsedTimeMs) + " ms");
        System.out.println("Encontradas " + result.size() + " estações na área do Porto:");
        System.out.println("  - Cidades: " + cityCount);
        System.out.println("  - Estações principais: " + mainStationCount);
        System.out.println("  - Estações portuguesas: " + ptCount);
        
        // Mostrar todas as estações (deve ser gerível para uma área pequena)
        System.out.println("\nTodas as estações:");
        result.forEach(station -> {
            System.out.println("  - " + station.getStation() + 
                " (" + station.getCountry() + 
                ", Cidade: " + station.isCity() + 
                ", Principal: " + station.isMainStation() + 
                ", " + station.getCoordinates().getLatitude() + ", " + 
                station.getCoordinates().getLongitude() + ")");
        });
        
        // Assertions
        assertNotNull(result, "Resultado não deve ser null");
        
        if (result.size() > 0) {
            result.forEach(station -> {
                // Verificar intervalo geográfico (área pequena)
                double lat = station.getCoordinates().getLatitude();
                double lon = station.getCoordinates().getLongitude();
                assertTrue(lat >= 41.0 && lat <= 41.3, 
                    "Latitude deve estar dentro da área metropolitana do Porto");
                assertTrue(lon >= -8.7 && lon <= -8.5, 
                    "Longitude deve estar dentro da área metropolitana do Porto");
            });
        }
        
        System.out.println("Query 5 completada com sucesso.\n");
    }

    /**
     * Análise de Performance: Comparar tempos de execução das queries
     * 
     * Este teste mede e compara o tempo de execução de diferentes tipos de queries
     * para demonstrar a complexidade temporal mencionada nos critérios de aceitação.
     */
    @Test
    public void analisePerformance_CompararTemposExecucaoQueries() {
        System.out.println("\n=== Análise de Performance: Tempos de Execução das Queries ===");
        
        // Query 1: Sem filtros (deve ser mais rápida)
        long start1 = System.nanoTime();
        List<TrainStation> result1 = controller.searchInRange(
                35.0, 50.0, -10.0, 10.0, null, null, null);
        long end1 = System.nanoTime();
        double time1 = (end1 - start1) / 1_000_000.0;
        
        // Query 2: Apenas filtro de país
        long start2 = System.nanoTime();
        List<TrainStation> result2 = controller.searchInRange(
                35.0, 50.0, -10.0, 10.0, "PT", null, null);
        long end2 = System.nanoTime();
        double time2 = (end2 - start2) / 1_000_000.0;
        
        // Query 3: Todos os filtros
        long start3 = System.nanoTime();
        List<TrainStation> result3 = controller.searchInRange(
                35.0, 50.0, -10.0, 10.0, "PT", true, true);
        long end3 = System.nanoTime();
        double time3 = (end3 - start3) / 1_000_000.0;
        
        // Display results
        System.out.println("Tipo de Query                          | Tempo (ms) | Resultados");
        System.out.println("----------------------------------------|-----------|-----------");
        System.out.printf("Sem filtros                             | %8.3f  | %9d%n", 
            time1, result1.size());
        System.out.printf("Apenas filtro de país (PT)              | %8.3f  | %9d%n", 
            time2, result2.size());
        System.out.printf("Todos os filtros (PT, cidade, principal) | %8.3f  | %9d%n", 
            time3, result3.size());
        
        // Todas as queries devem completar rapidamente (< 1 segundo)
        assertTrue(time1 < 1000, "Query sem filtros deve ser rápida");
        assertTrue(time2 < 1000, "Query com filtro de país deve ser rápida");
        assertTrue(time3 < 1000, "Query com todos os filtros deve ser rápida");
        
        System.out.println("\nTodas as queries completadas dentro de limites de tempo aceitáveis.\n");
    }
}
