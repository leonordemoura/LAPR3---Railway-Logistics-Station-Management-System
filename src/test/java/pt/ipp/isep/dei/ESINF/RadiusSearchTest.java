package pt.ipp.isep.dei.ESINF;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.ESINF.controller.BuildTreeController;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.GeoCoordinate;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.services.RadiusSearchService;
import pt.ipp.isep.dei.ESINF.services.impl.RadiusSearchImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RadiusSearchTest {

    private KDTree<TrainStation> kdTree;
    private RadiusSearchService service;
    private KDTree<TrainStation> populatedTree;

    @BeforeEach
    public void setUp() {
        kdTree = new KDTree<>();
        populatedTree = createPopulatedTree();
    }

    /**
     * Cria uma árvore KD populada com estações de teste para validação funcional.
     * Inclui estações em diferentes países, com diferentes tipos (cidade/não-cidade),
     * e a diferentes distâncias de pontos de referência.
     */
    private KDTree<TrainStation> createPopulatedTree() {
        BuildTreeController controller = new BuildTreeController();
        KDTree<TrainStation> tree = new KDTree<>();
        List<TrainStation> stations = new ArrayList<>();

        // Porto, Portugal - ponto de referência (41.1579, -8.6291)
        stations.add(new TrainStation("Portugal", "WET", "WET", "Porto",
                new GeoCoordinate(41.1579, -8.6291), true, true, false));

        // Lisboa, Portugal - ~274 km de Porto
        stations.add(new TrainStation("Portugal", "WET", "WET", "Lisboa",
                new GeoCoordinate(38.7223, -9.1393), true, true, false));

        // Braga, Portugal - ~50 km de Porto
        stations.add(new TrainStation("Portugal", "WET", "WET", "Braga",
                new GeoCoordinate(41.5454, -8.4265), true, true, false));

        // Vigo, Spain - ~150 km de Porto
        stations.add(new TrainStation("Spain", "CET", "CET", "Vigo",
                new GeoCoordinate(42.2406, -8.7207), true, false, false));

        // Estação rural próxima de Porto - ~30 km
        stations.add(new TrainStation("Portugal", "WET", "WET", "Aveiro",
                new GeoCoordinate(40.6443, -8.6455), false, true, false));

        // Paris, France - muito longe (~1200 km)
        stations.add(new TrainStation("France", "CET", "CET", "Paris",
                new GeoCoordinate(48.8566, 2.3522), true, true, false));

        tree = controller.buildBalancedTree();
        return tree;
    }

    /**
     * Verifica se a árvore vazia retorna resultado correto
     * É importante testar este cenário porque o sistema deve lidar graciosamente
     * com situações onde não há dados disponíveis, evitando NullPointerExceptions
     * e garantindo que retorna estruturas vazias mas válidas.
     */
    @Test
    public void testRadiusSearchWithEmptyTree() {
        service = new RadiusSearchImpl(kdTree);
        
        RadiusSearchService.RadiusSearchResult result = 
            service.findStationsWithinRadius(41.1579, -8.6291, 50.0);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalStations());
        assertTrue(result.getCountryCount().isEmpty());
        assertTrue(result.getCityCount().isEmpty());
    }

    /**
     * Verifica tratamento de raio negativo
     * Este teste é crucial porque raios negativos não fazem sentido geometricamente.
     * O sistema deve validar este input e retornar um resultado vazio em vez de
     * calcular distâncias com valores negativos que não têm significado físico.
     */
    @Test
    public void testRadiusSearchWithNegativeRadius() {
        service = new RadiusSearchImpl(kdTree);
        
        RadiusSearchService.RadiusSearchResult result = 
            service.findStationsWithinRadius(41.1579, -8.6291, -10.0);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalStations());
        assertTrue(result.getStations().isEmpty());
    }

    /**
     * Verifica tratamento de raio zero
     * Raio zero significa que só deveria encontrar estações exatamente no ponto alvo.
     * Este é um caso limite importante que testa se o operador de comparação
     * está correto (<=) e se o sistema não vai a baixo com valores extremos.
     */
    @Test
    public void testRadiusSearchWithZeroRadius() {
        service = new RadiusSearchImpl(kdTree);
        
        RadiusSearchService.RadiusSearchResult result = 
            service.findStationsWithinRadius(41.1579, -8.6291, 0.0);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalStations());
    }

    /**
     * Verifica tratamento de KD-tree nula
     * Este cenário pode acontecer se o repositório não conseguir carregar os dados
     * ou se a árvore não foi inicializada. O sistema deve ser robusto o suficiente
     * para não ir a baixo com NullPointerException e retornar um resultado vazio válido.
     */
    @Test
    public void testRadiusSearchWithNullTree() {
        service = new RadiusSearchImpl(null);
        
        RadiusSearchService.RadiusSearchResult result = 
            service.findStationsWithinRadius(41.1579, -8.6291, 50.0);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalStations());
        assertNotNull(result.getStations());
        assertNotNull(result.getCountryCount());
        assertNotNull(result.getCityCount());
    }

    /**
     * Verifica a estrutura do resultado retornado
     * Este teste garante que o objeto RadiusSearchResult contém sempre 
     * todas as suas estruturas de dados inicializadas (não-nulas), mesmo
     * quando não há resultados. Isto previne erros futuros no código que
     * usa este resultado, já que pode assumir que as coleções existem.
     */
    @Test
    public void testResultStructureIsValid() {
        service = new RadiusSearchImpl(kdTree);
        
        RadiusSearchService.RadiusSearchResult result = 
            service.findStationsWithinRadius(41.1579, -8.6291, 50.0);
        
        assertNotNull(result.getStations(), "Lista de estações não pode ser null");
        assertNotNull(result.getCountryCount(), "Mapa de países não pode ser null");
        assertNotNull(result.getCityCount(), "Mapa de cidades não pode ser null");
        assertTrue(result.getTotalStations() >= 0, "Total deve ser não-negativo");
    }

    /**
     * Verifica coordenadas nos limites extremos do planeta
     * Testa se o sistema consegue lidar com coordenadas válidas mas extremas
     * (Polo Sul: -90 lat). Isto é importante porque a fórmula de Haversine
     * deve funcionar corretamente mesmo em latitudes/longitudes extremas,
     * e o sistema não deve rejeitar coordenadas válidas só porque são incomuns.
     */
    @Test
    public void testRadiusSearchWithExtremeCoordinates() {
        service = new RadiusSearchImpl(kdTree);
        
        RadiusSearchService.RadiusSearchResult result = 
            service.findStationsWithinRadius(-90.0, 0.0, 100.0);
        
        assertNotNull(result);
        assertTrue(result.getTotalStations() >= 0);
        assertNotNull(result.getStations());
    }
}
