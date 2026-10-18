package pt.ipp.isep.dei.ESINF.controller;

import pt.ipp.isep.dei.ESINF.domain.Line;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Graph;
import pt.ipp.isep.dei.ESINF.repository.Repositories;
import pt.ipp.isep.dei.ESINF.repository.WarehouseRepository;
import pt.ipp.isep.dei.ESINF.services.MinimalBackboneService;
import pt.ipp.isep.dei.ESINF.services.impl.MinimalBackboneImpl;

import java.util.List;
import java.util.Map;

public class MinimalBackBoneController {
    private WarehouseRepository repository;
    private MinimalBackboneService service;

    public MinimalBackBoneController() {
        this.repository = Repositories.getInstance().getWarehouseRepository();
        this.service = new MinimalBackboneImpl();
    }

    public Graph<Station, Weight> getGraph() {
        return repository.getUndirectedGraph();
    }

    public Graph<Station, Weight> minimalBackbone(Graph<Station, Weight> graph) {
        return service.minimalBackbone(graph);
    }

    public void generateDotFile(Graph<Station, Weight> graph, String filePath) {
        service.generateDotFile(graph, filePath);
    }

    public void generateSVGFile(Graph<Station, Weight> graph, String filePath) {
        service.generateSVGFile(graph, filePath);
    }
}
