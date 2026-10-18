package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.dataStruc.MaxFlow;

import java.util.ArrayList;

/**
 * Interface genérica para o serviço de cálculo de fluxo máximo
 *
 * @param <V> tipo do vértice do grafo
 */
public interface MaxFlowService<V> {

    /**
     * Calcula o fluxo máximo entre dois vértices
     *
     * @param source vértice de origem
     * @param sink vértice de destino
     * @return resultado do cálculo de fluxo máximo
     */
    MaxFlow.MaxFlowResult<V> computeMaxFlow(V source, V sink);

    /**
     * Retorna todos os vértices do grafo
     *
     * @return lista de vértices
     */
    ArrayList<V> getGraphVertices();

    /**
     * Verifica se source e sink são válidos
     *
     * @param source vértice de origem
     * @param sink vértice de destino
     * @return true se ambos são válidos
     */
    boolean isConnected(V source, V sink);
}