package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.dataStruc.KDNode;
import pt.ipp.isep.dei.ESINF.dataStruc.KDTree;
import pt.ipp.isep.dei.ESINF.dataStruc.ProximitySearch;
import pt.ipp.isep.dei.ESINF.domain.TrainStation;
import pt.ipp.isep.dei.ESINF.services.RadiusSearchService;

import java.util.*;

/**
 * Implementação da busca de estações dentro de um raio usando KD-Tree.
 * 
 * COMPLEXIDADE GERAL DA US (User Story):
 * - Melhor caso: O(log n + k), onde n = número total de estações e k = número de estações encontradas
 *   Ocorre quando a KD-Tree está balanceada e o raio é pequeno
 * 
 * - Caso médio: O(√n + k*log k), onde n = número total de estações e k = número de estações encontradas
 *   A busca em KD-Tree 2D tem complexidade O(√n) para encontrar pontos em média
 *   O termo k*log k vem da ordenação dos resultados
 * 
 * - Pior caso: O(n + k*log k), onde n = número total de estações e k = número de estações encontradas
 *   Ocorre quando o raio é muito grande (cobrindo quase todas as estações) ou a árvore está desbalanceada
 *   O termo k*log k vem da ordenação dos resultados
 */
public class RadiusSearchImpl implements RadiusSearchService {

    // A KDTree divide o espaço entre latitude e longitude alternadamente
    // Latitude - linhas horizontais (Norte/Sul)
    // Longitude - linhas verticais (Este/Oeste)
    // ou seja cada nível da árvore foca-se numa direção diferente do mapa.
    private final KDTree<TrainStation> kdTree;

    public RadiusSearchImpl(KDTree<TrainStation> kdTree) {
        this.kdTree = kdTree;
    }

    /**
     * Encontra todas as estações dentro de um raio especificado.
     * - Lista de resultados: O(k)
     * - HashMaps (countryCount, cityCount): O(p + 2), onde p = número de países únicos
     * - Pilha de recursão: O(h), onde h = altura da árvore [O(log n) em média, O(n) no pior caso]
     */
    @Override
    public RadiusSearchResult findStationsWithinRadius(double targetLat, double targetLon, double radiusKm) {
        // Validações: O(1)
        if (kdTree == null || kdTree.isEmpty() || radiusKm <= 0) {
            return new RadiusSearchResult(new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        }

        List<ProximitySearch.StationDistance> results = new ArrayList<>();
        
        Map<String, Integer> countryCount = new HashMap<>();
        Map<Boolean, Integer> cityCount = new HashMap<>();

        // Começa a busca a partir da raiz da árvore
        // Complexidade: O(√n) em média, O(n) no pior caso
        KDNode<TrainStation> root = kdTree.getRootNode();
        searchWithinRadius(root, targetLat, targetLon, radiusKm, results, countryCount, cityCount, 0);

        // Ordena os resultados por distância (mais perto primeiro)
        // Se houver empate, ordena pelo nome da estação
        // Complexidade: O(k*log k), onde k = número de estações encontradas
        results.sort((a, b) -> {
            int distCompare = Double.compare(a.distance, b.distance);
            if (distCompare != 0) return distCompare;
            return b.station.getStation().compareTo(a.station.getStation());
        });

        int totalStations = results.size();

        return new RadiusSearchResult(results, countryCount, cityCount, totalStations);
    }

    /**
     * Método recursivo que procura estações dentro do raio usando a KD-Tree.
     * 
     * - Melhor caso: O(log n + k)
     *   Quando a árvore está balanceada e apenas um caminho é explorado por nível
     * 
     * - Caso médio: O(√n + k)
     *   Para busca de raio em KD-Tree 2D, a complexidade média é O(√n)
     *   Isso ocorre porque em cada nível podemos precisar explorar ambos os lados
     *   se o raio intersecta o plano de divisão
     * 
     * - Pior caso: O(n + k)
     *   Quando o raio é muito grande ou a árvore está completamente desbalanceada
     *   Precisa visitar todos os nós da árvore
     */
    private void searchWithinRadius(
            KDNode<TrainStation> node,
            double targetLat,
            double targetLon,
            double radiusKm,
            List<ProximitySearch.StationDistance> results,
            Map<String, Integer> countryCount,
            Map<Boolean, Integer> cityCount,
            int depth) {

        // Se o nó é nulo, já não há mais nada pra procurar
        if (node == null) {
            return;
        }

        double nodeLat = node.getCoordinate().getLatitude();
        double nodeLon = node.getCoordinate().getLongitude();

        // Calcula a distância real usando a fórmula de Haversine (StackOverflow :) )
        double distance = ProximitySearch.haversineDistance(targetLat, targetLon, nodeLat, nodeLon);

        // Se tá dentro do raio, adiciona aos resultados
        if (distance <= radiusKm) {
            List<TrainStation> stations = node.getInfo();
            if (stations != null) {
                for (TrainStation station : stations) {
                    results.add(new ProximitySearch.StationDistance(station, distance));

                    String country = station.getCountry();
                    countryCount.put(country, countryCount.getOrDefault(country, 0) + 1);

                    boolean isCity = station.isCity();
                    cityCount.put(isCity, cityCount.getOrDefault(isCity, 0) + 1);
                }
            }
        }

        // Decide se neste nível dividimos por latitude ou longitude
        // Níveis pares = latitude, níveis ímpares = longitude
        // A cada nível da árvore, alterno a direção da linha que corta o mapa.
        boolean splitByLatitude = (depth % 2 == 0);

        // Ao verificar se o raio intersecta o plano de divisão, evitamos explorar
        // subárvores inteiras que não podem conter pontos dentro do raio
        
        if (splitByLatitude) {
            // qdo dividimos por latitude é mais fácil imaginar um mapa assim:
            //      lat-40º
            //    |         |
            //  lat<40 | lat>40

            // isto é igual a uma árvore binária onde vai sempre ao lado onde o ponto seria se fosse inserido
            // (em teoria seria na esquerda)
            // Explora o lado "correto" primeiro: O(√n/2) em média
            if (targetLat < nodeLat) {
                searchWithinRadius(node.getLeft(), targetLat, targetLon, radiusKm,
                                 results, countryCount, cityCount, depth + 1);
            } else {
                searchWithinRadius(node.getRight(), targetLat, targetLon, radiusKm,
                                 results, countryCount, cityCount, depth + 1);
            }

            // aqui calcula-se a distância teórica ao target, ou seja de latitude a latitude, porque mesmo se o target
            // estiver num lado e estivermos a procurar noutro, o raio (sendo a terra uma esfera) pode chegar a esse ponto
            // por exemplo se o target está a uma distância teórica de 10km e o raio é 50km, logo o círculo atravessa
            // essa linha e podem haver estações no outro lado dentro do raio,
            // verifica se precisa explorar o outro lado
            double axisDistance = Math.abs(targetLat - nodeLat);
            
            // apenas se o raio intersecta o plano: O(√n/2) em média
            if (axisDistance <= radiusKm) {
                if (targetLat < nodeLat) {
                    searchWithinRadius(node.getRight(), targetLat, targetLon, radiusKm,
                                     results, countryCount, cityCount, depth + 1);
                } else {
                    searchWithinRadius(node.getLeft(), targetLat, targetLon, radiusKm,
                                     results, countryCount, cityCount, depth + 1);
                }
            }
        } else {
            // O mesmo raciocínio é usado na longitude
            double axisDistance = Math.abs(targetLon - nodeLon);
            
            // Primeiro vai pro lado onde o target deve estar: O(√n/2) em média
            if (targetLon < nodeLon) {
                searchWithinRadius(node.getLeft(), targetLat, targetLon, radiusKm,
                                 results, countryCount, cityCount, depth + 1);
            } else {
                searchWithinRadius(node.getRight(), targetLat, targetLon, radiusKm,
                                 results, countryCount, cityCount, depth + 1);
            }
            
            // Se o raio cruza o limite, tem que procurar no outro lado também: O(√n/2) em média
            if (axisDistance <= radiusKm) {
                if (targetLon < nodeLon) {
                    searchWithinRadius(node.getRight(), targetLat, targetLon, radiusKm,
                                     results, countryCount, cityCount, depth + 1);
                } else {
                    searchWithinRadius(node.getLeft(), targetLat, targetLon, radiusKm,
                                     results, countryCount, cityCount, depth + 1);
                }
            }
        }
    }

    /**
     * Cada nó da KD-Tree desenha uma linha no mapa (alternando entre horizontal e vertical).
     * 1. Primeiro segue pelo lado onde o target está (busca direcionada)
     * 2. Verifica se o raio atravessa a linha divisória (poda da árvore)
     * 3. Se sim → pesquisa também o outro lado
     * 4. Se não → ignora o outro lado completamente (PODA - esta é a otimização chave!)
     */
}
