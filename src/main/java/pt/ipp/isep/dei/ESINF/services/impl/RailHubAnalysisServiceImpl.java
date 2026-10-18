package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.RailHubAnalysisService;
import pt.ipp.isep.dei.ESINF.services.dto.RailHubAnalysisResult;
import pt.ipp.isep.dei.ESINF.services.dto.StationHubScore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * USEI13 — Service implementation that calculates betweenness, harmonic closeness and strength in order to
 * build the composite hub score for each Belgian station.
 */
public class RailHubAnalysisServiceImpl implements RailHubAnalysisService {

    private static final double HUB_WEIGHT_BETWEENNESS = 0.35;
    private static final double HUB_WEIGHT_HARMONIC = 0.35;
    private static final double HUB_WEIGHT_STRENGTH = 0.30;
    private static final double EPS = 1e-9;

    private final WarehouseRepository repository;

    public RailHubAnalysisServiceImpl() {
        this.repository = Repositories.getInstance().getWarehouseRepository();
    }

    @Override
    public RailHubAnalysisResult analyzeNetwork() {
        Graph<Station, Weight> graph = repository.getUndirectedGraph();
        if (graph == null || graph.numVertices() == 0) {
            throw new IllegalStateException("No stations loaded. Import stations.csv first.");
        }
        if (graph.numEdges() == 0) {
            throw new IllegalStateException("No lines loaded. Import lines.csv first.");
        }

        ArrayList<Station> vertices = graph.vertices();
        Map<Integer, NodeMetrics> metrics = initMetrics(vertices);

        computeDegreeAndStrength(graph, metrics);
        computeCentralities(graph, vertices, metrics);

        List<StationHubScore> scores = buildScores(vertices, metrics);
        scores.sort(Comparator.comparingDouble(StationHubScore::hubscore).reversed());

        String complexityNote = "Graph build runs in O(|V| + |E|). "
                + "Centrality relies on Brandes with Dijkstra per source, yielding O(|V||E| log|V|).";

        return new RailHubAnalysisResult(scores, complexityNote);
    }

    private Map<Integer, NodeMetrics> initMetrics(Collection<Station> stations) {
        Map<Integer, NodeMetrics> metrics = new HashMap<>();
        for (Station station : stations) {
            metrics.put(station.getStationID(), new NodeMetrics());
        }
        return metrics;
    }

    private void computeDegreeAndStrength(Graph<Station, Weight> graph, Map<Integer, NodeMetrics> metrics) {
        Set<Integer> knownIds = metrics.keySet();
        Map<Integer, Set<Integer>> neighbours = new HashMap<>();
        for (Integer id : knownIds) {
            neighbours.put(id, new HashSet<>());
        }

        Set<String> seenEdges = new HashSet<>();
        for (Edge<Station, Weight> edge : graph.edges()) {
            if (edge == null) continue;
            Station dep = edge.getVOrig();
            Station arr = edge.getVDest();
            if (dep == null || arr == null) continue;

            int depId = dep.getStationID();
            int arrId = arr.getStationID();
            NodeMetrics depMetrics = metrics.get(depId);
            NodeMetrics arrMetrics = metrics.get(arrId);
            if (depMetrics == null || arrMetrics == null) continue;

            String key = depId < arrId ? depId + "-" + arrId : arrId + "-" + depId;
            if (seenEdges.add(key)) {
                if (neighbours.get(depId).add(arrId)) {
                    depMetrics.degree++;
                }
                if (neighbours.get(arrId).add(depId)) {
                    arrMetrics.degree++;
                }

                double capacity = edge.getWeight() != null ? edge.getWeight().getCapacity() : 0.0;
                depMetrics.strength += capacity;
                arrMetrics.strength += capacity;
            }
        }
    }

    private void computeCentralities(Graph<Station, Weight> graph, ArrayList<Station> vertices,
                                     Map<Integer, NodeMetrics> metrics) {
        int n = vertices.size();
        if (n == 0) {
            return;
        }

        //criar um mapa onde as estações estão ordenadas pelo seu índice na lista
        Map<Station, Integer> indexOf = buildVertexIndexMap(vertices);
        double[] betweenness = new double[n];
        double[] harmonic = new double[n];

        //para cada estação, calcular as centralidades
        for (int s = 0; s < n; s++) {
            computeSingleSourceCentralities(graph, vertices, indexOf, s, betweenness, harmonic);
        }

        updateMetricsFromArrays(vertices, metrics, betweenness, harmonic);
        normalizeAndComputeHubScore(vertices, metrics, n);
    }

    /**
     * Cria um mapeamento entre cada objeto Estação e o seu índice (posição) na lista.
     * Útil para converter rapidamente uma Estação num índice para acesso a matrizes.
     *
     * @param vertices Lista de estações a indexar.
     * @return Um mapa onde a chave é a Estação e o valor é a sua posição na lista.
     */

    private Map<Station, Integer> buildVertexIndexMap(ArrayList<Station> vertices) {
        Map<Station, Integer> indexOf = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            indexOf.put(vertices.get(i), i);
        }
        return indexOf;
    }

    private void computeSingleSourceCentralities(Graph<Station, Weight> graph, ArrayList<Station> vertices,
                                                 Map<Station, Integer> indexOf, int source,
                                                 double[] betweenness, double[] harmonic) {
        int n = vertices.size();
        //criar uma stack para a ordem dos nós visitados
        Deque<Integer> stack = new ArrayDeque<>();
        //predecessors[w] = lista de nós vazia que precedem w em caminhos mais curtos
        List<List<Integer>> predecessors = initializePredecessors(n);
        //sigma[t] = número de caminhos mais curtos de s a t
        double[] sigma = new double[n];
        //dist[t] = distância de s a t
        double[] dist = new double[n];

        runDijkstraWithBrandes(graph, vertices, indexOf, source, stack, predecessors, sigma, dist);
        computeHarmonicCloseness(source, dist, harmonic, n);
        computeBetweennessFromStack(source, stack, predecessors, sigma, betweenness);
    }

    private List<List<Integer>> initializePredecessors(int n) {
        List<List<Integer>> predecessors = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            predecessors.add(new ArrayList<>());
        }
        return predecessors;
    }

    private void runDijkstraWithBrandes(Graph<Station, Weight> graph, ArrayList<Station> vertices,
                                        Map<Station, Integer> indexOf, int source,
                                        Deque<Integer> stack, List<List<Integer>> predecessors,
                                        double[] sigma, double[] dist) {
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        sigma[source] = 1.0;
        dist[source] = 0.0;

        // priority queue ordenada por distância mínima
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));
        //começar pela fonte(source)
        queue.add(new NodeDistance(source, 0.0));

        // enquanto a fila não estiver vazia
        while (!queue.isEmpty()) {
            // retirar o nó com a menor distância
            NodeDistance current = queue.poll();
            // se a distância retirada for maior que a distância conhecida, ignorar
            // exemplo : se já encontramos um caminho mais curto para esse nó antes de processá-lo novamente
            if (current.distance > dist[current.index] + EPS) {
                continue;
            }
            // adicionar o nó à stack
            stack.push(current.index);

            // para cada vizinho w de current (obter arestas de saída)
            Collection<Edge<Station, Weight>> outgoing = graph.outgoingEdges(vertices.get(current.index));
            if (outgoing == null) continue;

            for (Edge<Station, Weight> edge : outgoing) {
                // obter o peso da aresta
                Weight weight = edge.getWeight();
                // se o peso for nulo, ignorar
                if (weight == null) continue;

                // obter o índice do vizinho w
                Integer wObj = indexOf.get(edge.getVDest());
                if (wObj == null) continue; // safety: skip edges referencing unknown vertices
                // converter Integer para int
                int w = wObj;

                // calcular a distância através de current para w
                double newDist = dist[current.index] + weight.getDistance();

                // se a nova distância for menor que a distância conhecida para w
                if (newDist + EPS < dist[w]) {
                    // atualizar a distância para w
                    dist[w] = newDist;
                    // adicionar w à fila com a nova distância
                    queue.add(new NodeDistance(w, newDist));
                    // atualizar o número de caminhos mais curtos para w
                    sigma[w] = sigma[current.index];
                    // atualizar os predecessores de w para ser apenas current
                    predecessors.get(w).clear();
                    predecessors.get(w).add(current.index);
                    // se a nova distância for igual à distância conhecida para w
                } else if (Math.abs(newDist - dist[w]) <= EPS) {
                    // incrementar o número de caminhos mais curtos para w
                    sigma[w] += sigma[current.index];
                    // adicionar current aos predecessores de w
                    predecessors.get(w).add(current.index);
                }
            }
        }
    }

    //calcular a centralidade de proximidade harmónica para o nó source
    private void computeHarmonicCloseness(int source, double[] dist, double[] harmonic, int n) {
        // para cada nó i no grafo
        for (int i = 0; i < n; i++) {
            // ignorar o nó source
            if (i == source) continue;
            // se a distância de source a i for finita e maior que 0 (para evitar divisão por zero)
            if (dist[i] < Double.POSITIVE_INFINITY && dist[i] > 0) {
                harmonic[source] += 1.0 / dist[i];
            }
        }
    }

    //calcular a centralidade de intermediação para o nó source
    private void computeBetweennessFromStack(int source, Deque<Integer> stack, List<List<Integer>> predecessors,
                                             double[] sigma, double[] betweenness) {
        int n = sigma.length;
        // delta[v] = dependência de source em v
        double[] delta = new double[n];
        // enquanto a stack não estiver vazia
        while (!stack.isEmpty()) {
            // retirar o nó w do topo da stack
            int w = stack.pop();
            // para cada predecessor v de w
            for (int v : predecessors.get(w)) {
                // evitar divisão por zero
                if (sigma[w] == 0) continue;
                // calcular o coeficiente de dependência de v em w e atualizar delta[v]
                double coeff = (sigma[v] / sigma[w]) * (1.0 + delta[w]);
                delta[v] += coeff;
            }
            if (w != source) {
                betweenness[w] += delta[w];
            }
        }
    }

    //atualizar as métricas dos nós a partir dos arrays de betweenness e harmonic
    private void updateMetricsFromArrays(ArrayList<Station> vertices, Map<Integer, NodeMetrics> metrics,
                                         double[] betweenness, double[] harmonic) {
        // para cada vértice na lista
        for (int i = 0; i < vertices.size(); i++) {
            betweenness[i] /= 2.0; // undirected graph
            // obter as métricas do nó correspondente
            NodeMetrics nodeMetrics = metrics.get(vertices.get(i).getStationID());
            // atualizar as métricas de betweenness e harmonic
            if (nodeMetrics != null) {
                nodeMetrics.betweenness = betweenness[i];
                nodeMetrics.harmonic = harmonic[i];
            }
        }
    }

    //normalizar as métricas e calcular o hub score para cada nó
    private void normalizeAndComputeHubScore(ArrayList<Station> vertices, Map<Integer, NodeMetrics> metrics, int n) {
        // Use average incident capacity as the strength basis so nodes with more
        // edges but identical capacities are not unfairly favoured.
        // encontrar a força máxima média entre todos os nós para normalização
        double maxStrength = metrics.values().stream()
                .mapToDouble(m -> m.degree == 0 ? 0.0 : m.strength / m.degree)
                .max()
                .orElse(0.0);
        
        double harmonicNormalizer = (n > 1) ? (n - 1d) : 1d;

        double betweennessNormalizer = (n > 2) ? ((n - 1d) * (n - 2d) / 2d) : 1d;

        for (Station station : vertices) {
            NodeMetrics nodeMetrics = metrics.get(station.getStationID());
            if (nodeMetrics == null) continue;

            double betwNorm = betweennessNormalizer == 0 ? 0 : nodeMetrics.betweenness / betweennessNormalizer;
            double harmonicNorm = harmonicNormalizer == 0 ? 0 : nodeMetrics.harmonic / harmonicNormalizer;
            double averageStrength = nodeMetrics.degree == 0 ? 0.0 : nodeMetrics.strength / nodeMetrics.degree;
            double strengthNorm = maxStrength == 0 ? 0 : averageStrength / maxStrength;

            nodeMetrics.betweenness = clamp01(betwNorm);
            nodeMetrics.harmonic = clamp01(harmonicNorm);
            nodeMetrics.strength = clamp01(strengthNorm); // Store normalized average strength
            nodeMetrics.hubScore = clamp01(
                    HUB_WEIGHT_BETWEENNESS * nodeMetrics.betweenness
                            + HUB_WEIGHT_HARMONIC * nodeMetrics.harmonic
                            + HUB_WEIGHT_STRENGTH * nodeMetrics.strength);
        }
    }

    private List<StationHubScore> buildScores(Collection<Station> stations,
                                              Map<Integer, NodeMetrics> metrics) {
        List<StationHubScore> scores = new ArrayList<>();
        for (Station station : stations) {
            NodeMetrics nodeMetrics = metrics.get(station.getStationID());
            if (nodeMetrics == null) continue;

            scores.add(new StationHubScore(
                    station.getStationID(),
                    station.getName(),
                    nodeMetrics.degree,
                    nodeMetrics.strength,
                    nodeMetrics.betweenness,
                    nodeMetrics.harmonic,
                    nodeMetrics.hubScore));
        }
        return scores;
    }

    private double clamp01(double value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }

    private static class NodeMetrics {
        int degree;
        double strength;
        double betweenness;
        double harmonic;
        double hubScore;
    }

    private static class NodeDistance {
        final int index;
        final double distance;

        NodeDistance(int index, double distance) {
            this.index = index;
            this.distance = distance;
        }
    }
}

