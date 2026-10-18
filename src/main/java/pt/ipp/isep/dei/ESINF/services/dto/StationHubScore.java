package pt.ipp.isep.dei.ESINF.services.dto;

/**
 * Immutable view model that carries the centrality metrics for a single station.
 * All centrality scores (betweenness, harmonic closeness and hub score) are already normalised to [0,1].
 */
public class StationHubScore {
    private final int stationId;
    private final String stationName;
    private final int degree;
    private final double strength;
    private final double betweenness;
    private final double harmonicCloseness;
    private final double hubscore;

    public StationHubScore(int stationId, String stationName, int degree, double strength,
                           double betweenness, double harmonicCloseness, double hubscore) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.degree = degree;
        this.strength = strength;
        this.betweenness = betweenness;
        this.harmonicCloseness = harmonicCloseness;
        this.hubscore = hubscore;
    }

    public int stationId() {
        return stationId;
    }

    public String stationName() {
        return stationName;
    }

    public int degree() {
        return degree;
    }

    public double strength() {
        return strength;
    }

    public double betweenness() {
        return betweenness;
    }

    public double harmonicCloseness() {
        return harmonicCloseness;
    }

    public double hubscore() {
        return hubscore;
    }
}

