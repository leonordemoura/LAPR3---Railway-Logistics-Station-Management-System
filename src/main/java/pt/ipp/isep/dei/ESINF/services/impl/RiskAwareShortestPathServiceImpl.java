package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.RiskAwareShortestPathService;
import pt.ipp.isep.dei.ESINF.services.dto.RiskAwarePathResult;

import java.util.*;
public class RiskAwareShortestPathServiceImpl implements RiskAwareShortestPathService {

    private static final double EPS = 1e-9;

    private final WarehouseRepository repository;

    public RiskAwareShortestPathServiceImpl() {
        this.repository = Repositories.getInstance().getWarehouseRepository();
    }

    @Override
    public RiskAwarePathResult computeRiskAwareShortestPath(Station source, Station target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target stations must not be null");
        }

        Graph<Station, Weight> graph = repository.getDirectedGraph();
        if (graph == null || graph.numVertices() == 0) {
            throw new IllegalStateException("Directed railway graph is not loaded. Import stations and lines first.");
        }

        if (!graph.validVertex(source)) {
            throw new IllegalArgumentException("Source station is not present in the graph: " + source);
        }
        if (!graph.validVertex(target)) {
            throw new IllegalArgumentException("Target station is not present in the graph: " + target);
        }

        if (source.equals(target)) {
            return new RiskAwarePathResult(
                    source,
                    target,
                    List.of(source),
                    List.of(),
                    0.0,
                    false,
                    false,
                    List.of()
            );
        }

        return bellmanFordWithNegativeCycles(graph, source, target);
    }

    @Override
    public List<Station> getAllStations() {
        return repository.getStationsDirectedGraph();
    }

    private RiskAwarePathResult bellmanFordWithNegativeCycles(Graph<Station, Weight> graph,
                                                              Station source,
                                                              Station target) {
        int n = graph.numVertices();
        double[] dist = new double[n];
        int[] parent = new int[n];

        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);

        int sourceKey = graph.key(source);
        int targetKey = graph.key(target);
        dist[sourceKey] = 0.0;

        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;
            for (Edge<Station, Weight> edge : graph.edges()) {
                if (edge == null || edge.getWeight() == null) continue;

                int u = graph.key(edge.getVOrig());
                int v = graph.key(edge.getVDest());

                if (dist[u] == Double.POSITIVE_INFINITY) continue;

                double w = edge.getWeight().getCost();
                double candidate = dist[u] + w;
                if (candidate + EPS < dist[v]) {
                    dist[v] = candidate;
                    parent[v] = u;
                    updated = true;
                }
            }
            if (!updated) {
                break; // No further improvements possible
            }
        }

        if (dist[targetKey] == Double.POSITIVE_INFINITY) {
            throw new IllegalStateException("Target station is unreachable from source.");
        }

        boolean hasNegativeCycle = false;
        Set<Integer> negativeCycleVerticesIdx = new LinkedHashSet<>();

        for (Edge<Station, Weight> edge : graph.edges()) {
            if (edge == null || edge.getWeight() == null) continue;

            int u = graph.key(edge.getVOrig());
            int v = graph.key(edge.getVDest());

            if (dist[u] == Double.POSITIVE_INFINITY) continue; // not reachable from source

            double w = edge.getWeight().getCost();
            if (dist[u] + w + EPS < dist[v]) {
                hasNegativeCycle = true;

                int y = v;
                for (int i = 0; i < n; i++) {
                    if (parent[y] == -1) break;
                    y = parent[y];
                }

                if (parent[y] == -1) {
                    continue; // safety guard: broken predecessor chain
                }

                int start = y;
                Set<Integer> seen = new LinkedHashSet<>();
                seen.add(start);
                int cur = parent[start];
                int guard = 0;
                while (cur != start && cur != -1 && guard < n + 1) {
                    seen.add(cur);
                    cur = parent[cur];
                    guard++;
                }
                if (cur == start) {
                    negativeCycleVerticesIdx.addAll(seen);
                }
            }
        }

        boolean negativeCycleAffectsTarget = false;
        if (hasNegativeCycle && !negativeCycleVerticesIdx.isEmpty()) {
            negativeCycleAffectsTarget = canReachTargetFromAny(graph, negativeCycleVerticesIdx, targetKey);
        }

        List<Station> path = new ArrayList<>();
        List<RiskAwarePathResult.PathSegment> segments = new ArrayList<>();
        double totalCost = Double.NaN;

        if (!negativeCycleAffectsTarget) {
            Deque<Integer> stack = new ArrayDeque<>();
            int cur = targetKey;
            while (cur != -1) {
                stack.push(cur);
                if (cur == sourceKey) break;
                cur = parent[cur];
            }

            if (stack.isEmpty() || stack.peek() != sourceKey) {
                throw new IllegalStateException("Inconsistent predecessor chain when rebuilding path.");
            }

            while (!stack.isEmpty()) {
                path.add(graph.vertex(stack.pop()));
            }

            double cumulative = 0.0;
            for (int i = 0; i < path.size() - 1; i++) {
                Station from = path.get(i);
                Station to = path.get(i + 1);
                Edge<Station, Weight> edge = graph.edge(from, to);
                if (edge == null || edge.getWeight() == null) {
                    throw new IllegalStateException("Missing edge while reconstructing path from "
                            + from + " to " + to);
                }
                double segCost = edge.getWeight().getCost();
                cumulative += segCost;
                segments.add(new RiskAwarePathResult.PathSegment(from, to, segCost, cumulative));
            }
            totalCost = cumulative;
        }

        List<Station> negativeCycleStations = new ArrayList<>();
        for (Integer idx : negativeCycleVerticesIdx) {
            Station s = graph.vertex(idx);
            if (s != null) negativeCycleStations.add(s);
        }

        return new RiskAwarePathResult(
                source,
                target,
                path,
                segments,
                totalCost,
                hasNegativeCycle,
                negativeCycleAffectsTarget,
                negativeCycleStations
        );
    }

    private boolean canReachTargetFromAny(Graph<Station, Weight> graph,
                                          Set<Integer> startVertices,
                                          int targetKey) {
        boolean[] visited = new boolean[graph.numVertices()];
        Deque<Integer> queue = new ArrayDeque<>();

        for (int idx : startVertices) {
            if (!visited[idx]) {
                visited[idx] = true;
                queue.add(idx);
            }
        }

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (cur == targetKey) {
                return true;
            }

            Station v = graph.vertex(cur);
            if (v == null) continue;

            for (Station adj : graph.adjVertices(v)) {
                int nextKey = graph.key(adj);
                if (nextKey >= 0 && !visited[nextKey]) {
                    visited[nextKey] = true;
                    queue.add(nextKey);
                }
            }
        }
        return false;
    }
}
