package pt.ipp.isep.dei.ESINF.services.dto;

import pt.ipp.isep.dei.ESINF.domain.Station;

import java.util.Collections;
import java.util.List;
public class RiskAwarePathResult {

    public static class PathSegment {
        private final Station from;
        private final Station to;
        private final double segmentCost;
        private final double cumulativeCost;

        public PathSegment(Station from, Station to,
                           double segmentCost, double cumulativeCost) {
            this.from = from;
            this.to = to;
            this.segmentCost = segmentCost;
            this.cumulativeCost = cumulativeCost;
        }

        public Station from() {
            return from;
        }

        public Station to() {
            return to;
        }

        public double segmentCost() {
            return segmentCost;
        }

        public double cumulativeCost() {
            return cumulativeCost;
        }
    }

    private final Station source;
    private final Station target;
    private final List<Station> path;
    private final List<PathSegment> segments;
    private final double totalCost;

    private final boolean hasNegativeCycle;
    private final boolean negativeCycleAffectsTarget;
    private final List<Station> negativeCycleStations;

    public RiskAwarePathResult(Station source,
                               Station target,
                               List<Station> path,
                               List<PathSegment> segments,
                               double totalCost,
                               boolean hasNegativeCycle,
                               boolean negativeCycleAffectsTarget,
                               List<Station> negativeCycleStations) {
        this.source = source;
        this.target = target;
        this.path = path == null ? List.of() : List.copyOf(path);
        this.segments = segments == null ? List.of() : List.copyOf(segments);
        this.totalCost = totalCost;
        this.hasNegativeCycle = hasNegativeCycle;
        this.negativeCycleAffectsTarget = negativeCycleAffectsTarget;
        this.negativeCycleStations = negativeCycleStations == null
                ? List.of()
                : List.copyOf(negativeCycleStations);
    }

    public Station source() {
        return source;
    }

    public Station target() {
        return target;
    }

    public List<Station> path() {
        return Collections.unmodifiableList(path);
    }

    public List<PathSegment> segments() {
        return Collections.unmodifiableList(segments);
    }

    public double totalCost() {
        return totalCost;
    }

    public boolean hasNegativeCycle() {
        return hasNegativeCycle;
    }

    public boolean negativeCycleAffectsTarget() {
        return negativeCycleAffectsTarget;
    }

    public List<Station> negativeCycleStations() {
        return Collections.unmodifiableList(negativeCycleStations);
    }
}
