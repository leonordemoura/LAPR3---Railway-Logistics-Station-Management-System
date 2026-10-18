package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.dataStruc.MaxFlow;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.map.MapGraph;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.MaxFlowService;

import java.util.ArrayList;

public class MaxFlowImpl implements MaxFlowService<Station> {

    private final Graph<Station, Integer> capacityGraph;

    /**
     * Constructor que recebe o WarehouseRepository e constrói o grafo de capacidades
     *
     * @param warehouseRepository o repositório contendo o grafo base
     */
    public MaxFlowImpl(WarehouseRepository warehouseRepository) {
        this.capacityGraph = buildCapacityGraph(warehouseRepository);
    }

    /**
     * Constructor alternativo que recebe um grafo já construído
     *
     * @param graph grafo com capacidades como inteiros
     */
    public MaxFlowImpl(Graph<Station, Integer> graph) {
        this.capacityGraph = graph;
    }

    /**
     * Constrói um grafo de capacidades a partir do grafo base do repositório
     * Converte Weight em Integer (capacidade)
     *
     * @param warehouseRepository o repositório que contém o grafo base
     * @return um novo grafo com capacidades como pesos das arestas
     */
    private Graph<Station, Integer> buildCapacityGraph(WarehouseRepository warehouseRepository) {
        Graph<Station, Weight> baseGraph = warehouseRepository.getDirectedGraph();

        if (baseGraph == null || baseGraph.numVertices() == 0) {
            throw new IllegalStateException(
                    "Grafo base não disponível ou vazio. Carregue os dados primeiro.");
        }

        Graph<Station, Integer> newCapacityGraph = new MapGraph<>(true);

        // Adiciona vértices
        for (Station station : baseGraph.vertices()) {
            newCapacityGraph.addVertex(station);
        }

        // Adiciona arestas com as capacidades extraídas do Weight
        for (Station v : baseGraph.vertices()) {
            for (Station adj : baseGraph.adjVertices(v)) {
                Weight weight = baseGraph.edge(v, adj).getWeight();

                // CORREÇÃO APLICADA: Usa Math.round() para evitar truncamento para 0.
                int capacity = (int) Math.round(weight.getCapacity());

                newCapacityGraph.addEdge(v, adj, capacity);
            }
        }

        return newCapacityGraph;
    }

    @Override
    public MaxFlow.MaxFlowResult<Station> computeMaxFlow(Station source, Station sink) {
        if (capacityGraph == null || capacityGraph.vertices().isEmpty()) {
            throw new IllegalArgumentException("Grafo de capacidades não pode ser nulo ou vazio");
        }

        if (!capacityGraph.validVertex(source)) {
            throw new IllegalArgumentException("Station de origem não existe no grafo: " + source);
        }

        if (!capacityGraph.validVertex(sink)) {
            throw new IllegalArgumentException("Station de destino não existe no grafo: " + sink);
        }

        MaxFlow<Station> maxFlowCalculator = new MaxFlow<>(capacityGraph);
        return maxFlowCalculator.computeMaxFlow(source, sink);
    }

    @Override
    public ArrayList<Station> getGraphVertices() {
        return new ArrayList<>(this.capacityGraph.vertices());
    }

    @Override
    public boolean isConnected(Station source, Station sink) {
        return capacityGraph.validVertex(source) && capacityGraph.validVertex(sink);
    }
}