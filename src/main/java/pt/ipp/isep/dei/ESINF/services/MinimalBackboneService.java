package pt.ipp.isep.dei.ESINF.services;

import pt.ipp.isep.dei.ESINF.domain.Line;
import pt.ipp.isep.dei.ESINF.domain.Station;
import pt.ipp.isep.dei.ESINF.domain.Weight;
import pt.ipp.isep.dei.ESINF.graph.Graph;

import java.util.List;
import java.util.Map;

public interface MinimalBackboneService {

    Graph<Station, Weight> minimalBackbone(Graph<Station, Weight> graph);

    void generateDotFile(Graph<Station, Weight> graph, String filePath);

    void generateSVGFile(Graph<Station, Weight> graph, String filePath);
}
