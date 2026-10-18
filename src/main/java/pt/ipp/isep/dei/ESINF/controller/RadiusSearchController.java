package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.RadiusSearchService;
import pt.ipp.isep.dei.ESINF.services.impl.RadiusSearchImpl;

public class RadiusSearchController {

    private final WarehouseRepository repository;
    private RadiusSearchService service;

    public RadiusSearchController() {
        this.repository = Repositories.getInstance().getWarehouseRepository();
    }

    public KDTree<TrainStation> getKDTree() {
        return repository.getKDTreeStation();
    }

    public boolean validateCoordinates(double lat, double lon) {
        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }

    /**
     * Método principal - faz a busca de estações dentro do raio.
     * Cria o serviço e delega a busca.
     * 
     * Complexidade: O(√n + k*log k)
     * - Obter KDTree: O(1)
     * - Criar serviço: O(1)
     * - Executar busca: O(√n + k*log k), onde n = total de estações, k = estações encontradas
     */
    public RadiusSearchService.RadiusSearchResult findStationsWithinRadius(
            double targetLat, 
            double targetLon, 
            double radiusKm) {
        
        KDTree<TrainStation> kdTree = getKDTree();
        this.service = new RadiusSearchImpl(kdTree); 
        return service.findStationsWithinRadius(targetLat, targetLon, radiusKm);
    }
}
