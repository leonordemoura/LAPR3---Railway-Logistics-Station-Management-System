package pt.ipp.isep.dei.ui.console.menu.actions;

import pt.ipp.isep.dei.ESINF.CSVImporter;
import pt.ipp.isep.dei.ESINF.controller.MinimalBackBoneController;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Edge;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MinimalBackBoneUI implements Runnable{
    private MinimalBackBoneController controller;
    private WarehouseRepository repository;
    private CSVImporter importer;

    public MinimalBackBoneUI() {
        controller = new MinimalBackBoneController();
        repository = Repositories.getInstance().getWarehouseRepository();
        importer = new CSVImporter(repository);
    }

    @Override
    public void run() {
        System.out.println(Utils.createHeader("USEI12 - Minimal Backbone Network"));

        // Get graph from repository
        Graph<Station, Weight> graph= controller.getGraph();
        if (graph.vertices().isEmpty()) {
            System.out.println("No graph available. Loading Files...");
            importer.importStations("dataFiles/stations.csv");
            importer.importLines("dataFiles/lines.csv");
        }

        // Create a MST for Belgium
        Graph<Station, Weight> minimalBackboneGraph = controller.minimalBackbone(graph);

        if(minimalBackboneGraph == null) {
            System.out.println("The graph is not connected. Minimal Backbone cannot be created.");
            return;
        }

        System.out.println("\nMinimal Backbone Graph created successfully!");

        printGraph(minimalBackboneGraph);

        // Print the graph into dot file
        System.out.println("Generating DOT file for the Minimal Backbone Graph...");
        controller.generateDotFile(minimalBackboneGraph, "Project/Sprint/ESINF/graph/dot/graph.dot");

        // Print the graph into SVG file
        System.out.println("Generating SVG file for the Minimal Backbone Graph...");
        controller.generateSVGFile(minimalBackboneGraph, "Project/Sprint/ESINF/graph/svg/graph.svg");
    }

    public static void printGraph(Graph<Station, Weight> graph) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("GRAPH OVERVIEW");
        System.out.println("=".repeat(80));
        System.out.println("Type: Undirected");
        System.out.println("Total Stations: " + graph.numVertices());
        System.out.println("Total connections: " + graph.numEdges()/2);
        System.out.println("=".repeat(80));

        // Print all edges
        System.out.println("\nConnections:");
        System.out.println("-".repeat(80));
        Collection<Edge<Station, Weight>> edges = graph.edges();
        if (edges.isEmpty()) {
            System.out.println("No connections in the graph.");
        } else {
            int count = 1;

            Set<Edge<Station, Weight>> uniqueEdges = new HashSet<>();

            for (Edge<Station, Weight> edge : edges) {
                if (!uniqueEdges.contains(edge) && !uniqueEdges.contains(new Edge<>(edge.getVDest(), edge.getVOrig(), edge.getWeight()))) {
                    String arrow = "←→";
                    System.out.printf("%d. %s [%d] %s %s [%d]%n",
                            count++,
                            edge.getVOrig().getName(),
                            edge.getVOrig().getStationID(),
                            arrow,
                            edge.getVDest().getName(),
                            edge.getVDest().getStationID());
                    System.out.printf("   Distance: %.2f | Capacity: %.2f | Cost: %.2f%n",
                            edge.getWeight().getDistance(),
                            edge.getWeight().getCapacity(),
                            edge.getWeight().getCost());
                    uniqueEdges.add(edge);
                }
            }
        }

        System.out.println("=".repeat(80) + "\n");
    }
}
