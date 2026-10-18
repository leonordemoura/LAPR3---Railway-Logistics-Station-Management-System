package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;

import java.util.List;
import java.util.Map;

/**
 * Interface que define o serviço de busca por raio.
 * Procura estações dentro de um determinado raio usando KD-Tree.
 * 
 * - Caso médio: O(√n + k*log k)
 * - Pior caso: O(n + k*log k)
 * onde n = número total de estações, k = número de estações encontradas
 */
public interface RadiusSearchService {

    /**
     * Classe que guarda os resultados da busca.
     * 
     * Complexidade Espacial: O(k + p + 2)
     * - stations: O(k), onde k = número de estações encontradas
     * - countryCount: O(p), onde p = número de países únicos
     * - cityCount: O(2) - apenas dois valores possíveis (true/false)
     * - totalStations: O(1)
     */
    class RadiusSearchResult {
        private final List<ProximitySearch.StationDistance> stations; // lista das estações encontradas
        private final Map<String, Integer> countryCount; // quantas estações por país
        private final Map<Boolean, Integer> cityCount; // quantas são cidades vs não-cidades
        private final int totalStations; // total de estações encontradas

        // Construtor - monta o resultado com tudo o que precisamosO (1)
        public RadiusSearchResult(List<ProximitySearch.StationDistance> stations,
                                Map<String, Integer> countryCount, 
                                Map<Boolean, Integer> cityCount,
                                int totalStations) {
            this.stations = stations;
            this.countryCount = countryCount;
            this.cityCount = cityCount;
            this.totalStations = totalStations;
        }

        // Getters - métodos pra pegar os dados O(1)
        public List<ProximitySearch.StationDistance> getStations() {
            return stations;
        }

        public Map<String, Integer> getCountryCount() {
            return countryCount;
        }

        public Map<Boolean, Integer> getCityCount() {
            return cityCount;
        }

        public int getTotalStations() {
            return totalStations;
        }
    }

    // Método principal - encontra estações dentro de um raio em km
    // targetLat e targetLon = coordenadas do ponto central
    // radiusKm = raio em quilómetros
    // O(√n + k*log k)
    RadiusSearchResult findStationsWithinRadius(double targetLat, double targetLon, double radiusKm);
}
