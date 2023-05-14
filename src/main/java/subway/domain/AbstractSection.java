package subway.domain;

import java.util.List;
import java.util.Objects;

public abstract class AbstractSection {

    private final Station upstream;
    private final Station downstream;

    protected AbstractSection(Station upstream, Station downstream) {
        validateNotEqualStations(upstream, downstream);
        this.upstream = upstream;
        this.downstream = downstream;
    }

    public abstract List<AbstractSection> insertInTheMiddle(Station stationToAdd, int distance);

    public abstract AbstractSection merge(AbstractSection sectionToMerge);

    private void validateNotEqualStations(Station upstream, Station downstream) {
        if (upstream.equals(downstream)) {
            throw new IllegalArgumentException(String.format(
                    "디버깅: 구간에 같은 상행역과 하행역이 들어왔습니다. upstream: %s, downstream: %s",
                    upstream.getName(),
                    downstream.getName()
            ));
        }
    }

    protected void validateNotTerminalSection(AbstractSection sectionToMerge) {
        if (sectionToMerge.contains(DummyTerminalStation.getInstance())) {
            throw new IllegalArgumentException(String.format(
                    "디버깅: sectionToMerge: %s",
                    sectionToMerge
            ));
        }
    }

    public final boolean contains(Station station) {
        return upstream.equals(station) || downstream.equals(station);
    }

    public final boolean isCorrespondingSection(Station otherUpstream, Station otherDownstream) {
        return upstream.equals(otherUpstream) && downstream.equals(otherDownstream);
    }

    protected void validateSectionLinked(AbstractSection otherSection) {
        if (!areSectionsLinked(otherSection)) {
            throw new IllegalArgumentException(String.format(
                    "디버깅: 연결되어 있지 않은 구간들입니다. section1: %s, section2: %s",
                    this,
                    otherSection
            ));
        }
    }

    private boolean areSectionsLinked(AbstractSection otherSection) {
        return upstream.equals(otherSection.downstream)
                || downstream.equals(otherSection.upstream);
    }

    public Station getUpstream() {
        return upstream;
    }

    public Station getDownstream() {
        return downstream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractSection that = (AbstractSection) o;
        return Objects.equals(upstream, that.upstream) && Objects.equals(downstream, that.downstream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upstream, downstream);
    }

    @Override
    public String toString() {
        return "AbstractSection{" +
                "upstream=" + upstream +
                ", downstream=" + downstream +
                '}';
    }
}