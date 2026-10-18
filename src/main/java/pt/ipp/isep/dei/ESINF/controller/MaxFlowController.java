package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.dataStruc.MaxFlow;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.map.MapGraph;
import pt.ipp.isep.dei.ESINF.repository.I_WarehouseRepository;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.services.MaxFlowService;
import pt.ipp.isep.dei.ESINF.services.impl.MaxFlowImpl;
import pt.ipp.isep.dei.LAPR3.domain.Facility;
import pt.ipp.isep.dei.LAPR3.domain.RailwayRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller para cálculos de MaxFlow usando o grafo do WarehouseRepository
 */
public class MaxFlowController {

    private MaxFlowService<Station> maxFlowService;
    private final I_WarehouseRepository warehouseRepository;
    private final RailwayRepository railwayRepository;

    /**
     * Constructor que obtém os repositórios pelo Singleton Repositories
     */
    public MaxFlowController() {
        Repositories repositories = Repositories.getInstance();
        this.warehouseRepository = repositories.getWarehouseRepository();
        this.railwayRepository = repositories.getRailwayRepository();

        if (warehouseRepository == null) {
            throw new IllegalStateException("WarehouseRepository não está disponível");
        }

        initializeMaxFlowService();
    }


    /**
     * Retorna todas as facilities do RailwayRepository
     *
     * @return lista de facilities
     */
    public List<Facility> getFacilities() {
        if (railwayRepository != null) {
            return railwayRepository.getAllFacilities();
        }
        return new ArrayList<>();
    }

    /**
     * Inicializa o serviço MaxFlow construindo o grafo de capacidades
     */
    private void initializeMaxFlowService() {
        try {
            Graph<Station, Integer> capacityGraph = buildCapacityGraph();
            this.maxFlowService = new MaxFlowImpl(capacityGraph);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Não foi possível inicializar o serviço MaxFlow: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula o fluxo máximo entre duas estações
     *
     * @param source estação de origem
     * @param sink estação de destino
     * @return resultado do algoritmo Edmonds-Karp
     * @throws IllegalArgumentException se source ou sink forem inválidos
     */
    public MaxFlow.MaxFlowResult<Station> calculateMaxFlow(Station source, Station sink) {
        if (source == null || sink == null) {
            throw new IllegalArgumentException("Source e sink não podem ser nulos");
        }

        if (source.equals(sink)) {
            throw new IllegalArgumentException("Source e sink devem ser estações diferentes");
        }

        if (!maxFlowService.isConnected(source, sink)) {
            throw new IllegalArgumentException("Source ou sink não são vértices válidos no grafo");
        }

        return maxFlowService.computeMaxFlow(source, sink);
    }

    /**
     * Constrói um grafo de capacidades a partir do grafo base
     * Converte Weight em Integer (capacidade)
     *
     * @return grafo com capacidades como pesos
     */
    private Graph<Station, Integer> buildCapacityGraph() {
        Graph<Station, Weight> baseGraph = warehouseRepository.getDirectedGraph();

        if (baseGraph == null || baseGraph.numVertices() == 0) {
            throw new IllegalStateException(
                    "Grafo base não disponível ou vazio. Carregue os dados primeiro.");
        }

        Graph<Station, Integer> capacityGraph = new MapGraph<>(true);

        for (Station station : baseGraph.vertices()) {
            capacityGraph.addVertex(station);
        }

        for (Station v : baseGraph.vertices()) {
            for (Station adj : baseGraph.adjVertices(v)) {
                Weight weight = baseGraph.edge(v, adj).getWeight();
                int capacity = calculateCapacityFromWeight(weight);
                capacityGraph.addEdge(v, adj, capacity);
            }
        }

        return capacityGraph;
    }

    /**
     * Calcula a capacidade a partir de um Weight
     * Extrai a capacidade do objeto Weight
     *
     * @param weight objeto Weight contendo a capacidade
     * @return capacidade como inteiro
     */
    private int calculateCapacityFromWeight(Weight weight) {
        if (weight == null) {
            throw new IllegalArgumentException("Weight não pode ser nulo");
        }
        // CORREÇÃO APLICADA: Usa Math.round() para garantir que capacidades positivas (mesmo fracionárias)
        // sejam arredondadas para o inteiro mais próximo (ou pelo menos 1 se for >= 0.5).
        return (int) Math.round(weight.getCapacity());
    }

    /**
     * Retorna todas as estações disponíveis no grafo
     *
     * @return lista de estações
     */
    public ArrayList<Station> getAllStations() {
        return maxFlowService.getGraphVertices();
    }

    /**
     * Atualiza o serviço MaxFlow recarregando o grafo
     */
    public void refreshGraph() {
        initializeMaxFlowService();
    }

    /**
     * Retorna o grafo de capacidades
     *
     * @return grafo com capacidades
     */
    public Graph<Station, Integer> getCapacityGraph() {
        return buildCapacityGraph();
    }
}