package pt.ipp.isep.dei.ESINF.services.impl;

import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Algorithms;
import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.graph.map.MapGraph;
import pt.ipp.isep.dei.ESINF.services.MinimalBackboneService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MinimalBackboneImpl implements MinimalBackboneService {

    @Override
    public Graph<Station, Weight> minimalBackbone(Graph<Station, Weight> graph) {
        // Check if the graph is connected
        if(!graph.isConnected()){
            return null;
        }

        return getMinimalSpanningTree(graph);
    }

    public Graph<Station, Weight> getMinimalSpanningTree(Graph<Station, Weight> graph) {
        // Get minimal spanning tree using Kruskal's algorithm
        Graph<Station, Weight> mst = new MapGraph<>(false);

        // Add all vertices to the MST
        for (Station station : graph.vertices()) {
            mst.addVertex(station);
        }

        // Sort edges by weight in ascending order
        Collection<Edge<Station, Weight>> edges = graph.edges();
        List<Edge<Station, Weight>> edgeList = new ArrayList<>(edges);
        edgeList.sort(Comparator.comparing(Edge::getWeight, Comparator.comparingDouble(Weight::getDistance)));

        // Add edges to the MST if they don't create a cycle
        for (Edge<Station, Weight> edge : edgeList) {
            LinkedList<Station> connectedVertices = Algorithms.DepthFirstSearch(mst, edge.getVOrig());
            if (!connectedVertices.contains(edge.getVDest())) {
                mst.addEdge(edge.getVOrig(), edge.getVDest(), edge.getWeight());
            }
        }

        return mst;
    }

    @Override
    public void generateDotFile(Graph<Station, Weight> graph, String filename) {
        StringBuilder sb = new StringBuilder();
        // Graph is undirected
        sb.append("graph {\n");

        // Set layout to neato for positioning
        sb.append("  graph [layout=neato, overlap=false];\n\n");

        // Add Comment for better understanding
        sb.append("  // Define nodes with fixed positions\n");

        // Add position to nodes
        for (Station station : graph.vertices()) {
            sb.append("  \"").append(station.getName()).append("\" [pos=\"")
                    .append(station.getCoordinate().getCoordinateX()).append(",")
                    .append(station.getCoordinate().getCoordinateY()).append("!\"];\n");
        }

        sb.append("\n\n  // Define the edges for the graph\n");

        // Add edges
        for (Edge<Station, Weight> edge : graph.edges()) {
            sb.append("  \"").append(edge.getVOrig().getName()).append("\" -- \"")
                    .append(edge.getVDest().getName()).append("\";\n");
        }

        sb.append("}\n");

        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(sb.toString());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateSVGFile(Graph<Station, Weight> graph, String filePath) {
        String dotFilePath = "Project/Sprint/ESINF/graph/dot/graph.dot";
        generateDotFile(graph, dotFilePath);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("neato", "-Tsvg", dotFilePath, "-o", filePath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("SVG file generated successfully: " + filePath);
            } else {
                System.err.println("Error generating SVG file. Exit code: " + exitCode);
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
                errorReader.close();
            }
        } catch (IOException e) {
            System.err.println("Error: GRAPHVIZ not found. Please install GRAPHVIZ.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Process was interrupted.");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
