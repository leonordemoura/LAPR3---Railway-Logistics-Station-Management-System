package pt.ipp.isep.dei.ESINF.graph;


import pt.ipp.isep.dei.ESINF.graph.matrix.MatrixGraph;

import java.util.*;
import java.util.function.BinaryOperator;


public class Algorithms {

    /** Performs breadth-first search of a Graph starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    public static <V, E> LinkedList<V> BreadthFirstSearch(Graph<V, E> g, V vert) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Performs depth-first search starting in a vertex
     *
     * @param g Graph instance
     * @param vOrig vertex of graph g that will be the source of the search
     * @param visited set of previously visited vertices
     * @param qdfs return LinkedList with vertices of depth-first search
     */
    private static <V, E> void DepthFirstSearch(Graph<V, E> g, V vOrig, boolean[] visited, LinkedList<V> qdfs) {
        // Checks if node has been visited
        if(visited[g.key(vOrig)]) {
            return;
        }

        // Marks node as visited and adds to the result list
        qdfs.add(vOrig);
        visited[g.key(vOrig)] = true;

        // Recursively visits adjacent vertices
        for (V adj: g.adjVertices(vOrig)) {
            DepthFirstSearch(g, adj, visited, qdfs);
        }
    }

    /** Performs depth-first search starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex of graph g that will be the source of the search

     * @return a LinkedList with the vertices of depth-first search
     */
    public static <V, E> LinkedList<V> DepthFirstSearch(Graph<V, E> g, V vert) {
        boolean[] visited = new boolean[g.numVertices()];
        LinkedList<V> qdfs = new LinkedList<>();
        DepthFirstSearch(g, vert, visited, qdfs);
        return qdfs;
    }

//    /** Returns all paths from vOrig to vDest
//     *
//     * @param g       Graph instance
//     * @param vOrig   Vertex that will be the source of the path
//     * @param vDest   Vertex that will be the end of the path
//     * @param visited set of discovered vertices
//     * @param path    stack with vertices of the current path (the path is in reverse order)
//     * @param paths   ArrayList with all the paths (in correct order)
//     */
//    private static <V, E> void allPaths(Graph<V, E> g, V vOrig, V vDest, boolean[] visited,
//                                        LinkedList<V> path, ArrayList<LinkedList<V>> paths) {
//
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    /** Returns all paths from vOrig to vDest
//     *
//     * @param g     Graph instance
//     * @param vOrig information of the Vertex origin
//     * @param vDest information of the Vertex destination
//     * @return paths ArrayList with all paths from vOrig to vDest
//     */
//    public static <V, E> ArrayList<LinkedList<V>> allPaths(Graph<V, E> g, V vOrig, V vDest) {
//
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    /**
     * Computes shortest-path distance from a source vertex to all reachable
     * vertices of a graph g with non-negative edge weights
     * This implementation uses Dijkstra's algorithm
     *
     * @param g        Graph instance
     * @param vOrig    Vertex that will be the source of the path
     * @param visited  set of previously visited vertices
     * @param pathKeys minimum path vertices keys
     * @param dist     minimum distances
     */
    private static <V, E> void shortestPathDijkstra(Graph<V, E> g, V vOrig,
                                                    Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                                    boolean[] visited, V [] pathKeys, E [] dist) {
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    /** Shortest-path between two vertices
     *
     * @param g graph
     * @param vOrig origin vertex
     * @param vDest destination vertex
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @param zero neutral element of the sum in elements of type E
     * @param shortPath returns the vertices which make the shortest path
     * @return if vertices exist in the graph and are connected, true, false otherwise
     */
    public static <V, E> E shortestPath(Graph<V, E> g, V vOrig, V vDest,
                                        Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                        LinkedList<V> shortPath) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Shortest-path between a vertex and all other vertices
     *
     * @param g graph
     * @param vOrig start vertex
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @param zero neutral element of the sum in elements of type E
     * @param paths returns all the minimum paths
     * @param dists returns the corresponding minimum distances
     * @return if vOrig exists in the graph true, false otherwise
     */
    public static <V, E> boolean shortestPaths(Graph<V, E> g, V vOrig,
                                               Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                               ArrayList<LinkedList<V>> paths, ArrayList<E> dists) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Extracts from pathKeys the minimum path between voInf and vdInf
     * The path is constructed from the end to the beginning
     *
     * @param g        Graph instance
     * @param vOrig    information of the Vertex origin
     * @param vDest    information of the Vertex destination
     * @param pathKeys minimum path vertices keys
     * @param path     stack with the minimum path (correct order)
     */
    private static <V, E> void getPath(Graph<V, E> g, V vOrig, V vDest,
                                       V [] pathKeys, LinkedList<V> path) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Calculates the minimum distance graph using Floyd-Warshall
     * 
     * @param g initial graph
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @return the minimum distance graph
     */
    public static <V,E> MatrixGraph<V,E> minDistGraph(Graph <V,E> g, Comparator<E> ce, BinaryOperator<E> sum) {
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Performs a topological sort on a directed graph and detects cycles.
     *
     * @param g the directed graph to be sorted
     * @param <V> the type of vertices
     * @param <E> the type of edges
     * @return a map containing:
     *         - "hasCycle": Boolean indicating if a cycle exists,
     *         - "cycleVertices": Set of vertices in the cycle (if any),
     *         - "topsort": LinkedList with the topological order (if no cycles)
     */
    public static <V, E> Map<String, Object> topologSort(Graph<V, E> g) {

        // O(1)
        // Inicializa o mapa de resultados. Operação constante.
        Map<String, Object> result = new HashMap<>();

        if (g == null || g.numVertices() == 0) {
            result.put("hasCycle", false);               // O(1) — operação constante no HashMap
            result.put("topsort", new LinkedList<V>());  // O(1) — inicializa lista vazia
            return result;                               // O(1) — retorna imediatamente
        }


        // ---------- 1) DETEÇÃO DE CICLOS ----------

        // O(V) — inicializa array de cores com tamanho V (número de vértices)
        int[] color = new int[g.numVertices()]; // 0=white,1=gray,2=black

        // O(1) — inicialização de pilha e conjunto vazio
        Deque<V> stack = new ArrayDeque<>();
        Set<V> cycleVertices = new LinkedHashSet<>();

        // O(V) — itera sobre todos os vértices do grafo (V = número de vértices)
        for (V v : g.vertices()) {
            // O(1) — verifica se o vértice está branco (não visitado)
            if (color[g.key(v)] == 0) {
                // O(V.(V + E)) — A soma de todas as chamadas recursivas de colorDFS
                if (colorDFS(g, v, color, stack, cycleVertices)) {
                    result.put("hasCycle", true);                 // O(1)
                    result.put("cycleVertices", cycleVertices);   // O(1)
                    return result;                                // O(1) — retorna ao encontrar um ciclo
                }
            }
        }

        // ---------- 2) ORDENAÇÃO TOPOLÓGICA ----------

        // O(V) — inicializa array de vértices visitados (V = número de vértices)
        boolean[] visited = new boolean[g.numVertices()];
        // O(1) — inicializa lista vazia para armazenar ordem topológica
        LinkedList<V> topsort = new LinkedList<>();

        // O(V) — itera sobre todos os vértices
        for (V v : g.vertices()) {
            // O(1) — verifica se o vértice já foi visitado
            if (!visited[g.key(v)]) {
                // O(V.(V + E)) — chamada DFS para construir a ordem topológica
                topoDFS(g, v, visited, topsort);
            }
        }

        result.put("hasCycle", false);    // O(1)
        result.put("topsort", topsort);   // O(1)
        return result;                    // O(1)
    }

    /**
     * Recursive helper method for performing DFS to build a topological order.
     *
     * @param g the directed graph
     * @param vOrig the current vertex
     * @param visited boolean array indicating visited vertices
     * @param topsort LinkedList where vertices are added in topological order
     * @param <V> type of vertices
     * @param <E> type of edges
     */
    private static <V, E> void topoDFS(Graph<V, E> g, V vOrig,
                                       boolean[] visited, LinkedList<V> topsort) {

        // O(V)
        // Acesso ao índice do vértice via key(), que percorre a lista de vértices.
        visited[g.key(vOrig)] = true;

        // O(E)
        // Percorre todas as arestas que saem de vOrig
        for (V vAdj : g.adjVertices(vOrig)) {

            // O(V)
            // Verificação se o vértice adjacente já foi visitado.
            if (!visited[g.key(vAdj)]) {

                // Chamada recursiva.
                topoDFS(g, vAdj, visited, topsort);
            }
        }

        // O(1)
        // Inserção no início da LinkedList.
        topsort.addFirst(vOrig);
    }

    /**
     * Recursive helper method to detect cycles in a directed graph using DFS.
     *
     * @param g the directed graph
     * @param vOrig the current vertex being visited
     * @param color array storing the state of each vertex (WHITE, GRAY, BLACK)
     * @param stack stack storing the current DFS path
     * @param cycleVertices set to collect vertices involved in a cycle
     * @param <V> type of vertices
     * @param <E> type of edges
     * @return true if a cycle is detected starting from vOrig, false otherwise
     */
    private static <V, E> boolean colorDFS(Graph<V, E> g, V vOrig,
                                           int[] color, Deque<V> stack,
                                           Set<V> cycleVertices) {

        // O(1)
        // Definição de constantes (valores fixos)
        final int WHITE = 0, GRAY = 1, BLACK = 2;

        // O(V)
        // Acesso ao índice do vértice através de key(),
        // que percorre a lista de vértices (indexOf).
        color[g.key(vOrig)] = GRAY;

        // O(1)
        // Empilha o vértice atual (operação constante)
        stack.push(vOrig);

        // O(E)
        // Percorre todas as arestas que saem de vOrig
        for (V vAdj : g.adjVertices(vOrig)) {

            // O(V)
            // Obtém o índice do vértice adjacente (key usa indexOf)
            int adjKey = g.key(vAdj);

            // O(1)
            // Verifica se encontrou um vértice em processamento (back-edge)
            if (color[adjKey] == GRAY) {

                // O(V)
                // Percorre a pilha para recolher os vértices do ciclo.
                for (V v : stack) {
                    cycleVertices.add(v); // O(1)
                    if (v.equals(vAdj)) break; // O(1)
                }

                // O(1)
                // Ciclo encontrado, termina imediatamente
                return true;
            }

            // O(1)
            // Se o vértice ainda não foi visitado
            if (color[adjKey] == WHITE) {

                // Chamada recursiva.
                if (colorDFS(g, vAdj, color, stack, cycleVertices))
                    return true;
            }
        }

        // O(1)
        // Remove o vértice da pilha (backtracking)
        stack.pop();

        // O(V)
        // Marca o vértice como totalmente processado
        color[g.key(vOrig)] = BLACK;

        // O(1)
        // Nenhum ciclo encontrado neste ramo
        return false;
    }

}