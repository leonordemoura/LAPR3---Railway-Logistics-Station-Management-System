package pt.ipp.isep.dei.ESINF.dataStruc;

import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;

import java.util.*;

/**
 * Implementation of the Edmonds-Karp algorithm to compute the maximum flow in a network.
 * This version uses BFS to find augmenting paths.
 * @param <V> Vertex type
 */
public class MaxFlow<V> {

    private final Graph<V, Integer> originalGraph;
    private Map<V, Map<V, Integer>> residualCapacity;
    private int maxFlowValue;
    private final List<FlowPath<V>> augmentingPaths;

    public MaxFlow(Graph<V, Integer> graph) {
        this.originalGraph = graph;
        this.augmentingPaths = new ArrayList<>();
        this.maxFlowValue = 0;
    }

    private void initializeResidualCapacity() {
        residualCapacity = new HashMap<>();

        for (V u : originalGraph.vertices()) {
            residualCapacity.putIfAbsent(u, new HashMap<>());

            for (Edge<V, Integer> edge : originalGraph.outgoingEdges(u)) {
                V v = edge.getVDest();
                int capacity = edge.getWeight();

                if (capacity <= 0) continue;

                // Forward edge
                residualCapacity.get(u).put(v, capacity);

                // Backward edge
                residualCapacity.putIfAbsent(v, new HashMap<>());
                residualCapacity.get(v).putIfAbsent(u, 0);
            }
        }
    }

    public MaxFlowResult<V> computeMaxFlow(V source, V sink) {
        if (!originalGraph.validVertex(source) || !originalGraph.validVertex(sink))
            throw new IllegalArgumentException("Source or sink do not exist in the graph");

        if (source.equals(sink))
            throw new IllegalArgumentException("Source and sink must be different");

        initializeResidualCapacity();
        maxFlowValue = 0;
        augmentingPaths.clear();

        int iteration = 0;

        while (true) {
            iteration++;
            // Busca de caminho aumentante usando BFS
            List<V> path = findAugmentingPathBFS(source, sink);
            if (path == null || path.isEmpty()) break;

            int bottleneck = findMinCapacity(path);
            updateResidualCapacity(path, bottleneck);

            maxFlowValue += bottleneck;
            augmentingPaths.add(new FlowPath<>(iteration, path, bottleneck, maxFlowValue));
        }

        return new MaxFlowResult<>(source, sink, maxFlowValue, augmentingPaths);
    }

    /**
     * BFS para encontrar caminho aumentante (Edmonds-Karp)
     */
    private List<V> findAugmentingPathBFS(V source, V sink) {
        Set<V> visited = new HashSet<>();
        Map<V, V> parent = new HashMap<>();
        Queue<V> queue = new LinkedList<>();

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            V u = queue.poll();
            Map<V, Integer> neighbors = residualCapacity.get(u);
            if (neighbors == null) continue;

            for (Map.Entry<V, Integer> entry : neighbors.entrySet()) {
                V v = entry.getKey();
                int capacity = entry.getValue();

                if (capacity > 0 && !visited.contains(v)) {
                    visited.add(v);
                    parent.put(v, u);
                    if (v.equals(sink)) {
                        return reconstructPath(parent, source, sink);
                    }
                    queue.add(v);
                }
            }
        }
        return null;
    }

    /**
     * Reconstrução do caminho usando LinkedList (addFirst O(1))
     */
    private List<V> reconstructPath(Map<V, V> parent, V source, V sink) {
        LinkedList<V> path = new LinkedList<>();
        V current = sink;

        while (!current.equals(source)) {
            path.addFirst(current);
            current = parent.get(current);
        }
        path.addFirst(source);
        return path;
    }

    private int findMinCapacity(List<V> path) {
        int minCapacity = Integer.MAX_VALUE;

        for (int i = 0; i < path.size() - 1; i++) {
            V u = path.get(i);
            V v = path.get(i + 1);
            minCapacity = Math.min(minCapacity, residualCapacity.get(u).get(v));
        }
        return minCapacity;
    }

    private void updateResidualCapacity(List<V> path, int flow) {
        for (int i = 0; i < path.size() - 1; i++) {
            V u = path.get(i);
            V v = path.get(i + 1);

            residualCapacity.get(u).put(v, residualCapacity.get(u).get(v) - flow);
            residualCapacity.get(v).put(u, residualCapacity.get(v).getOrDefault(u, 0) + flow);
        }
    }

    /**
     * Classe que representa um caminho aumentante
     */
    public static class FlowPath<V> {
        public int iteration;
        public List<V> path;
        public int flow;
        public int totalFlow;

        public FlowPath(int iteration, List<V> path, int flow, int totalFlow) {
            this.iteration = iteration;
            this.path = new ArrayList<>(path);
            this.flow = flow;
            this.totalFlow = totalFlow;
        }
    }

    /**
     * Resultado final do cálculo do MaxFlow
     */
    public static class MaxFlowResult<V> {
        public V source;
        public V target;
        public int maxFlowValue;
        public List<FlowPath<V>> augmentingPaths;

        public MaxFlowResult(V source, V target, int maxFlowValue, List<FlowPath<V>> paths) {
            this.source = source;
            this.target = target;
            this.maxFlowValue = maxFlowValue;
            this.augmentingPaths = new ArrayList<>(paths);
        }
    }
}
