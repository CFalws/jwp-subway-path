package subway.line.domain;

import subway.line.exception.DuplicateStationInLineException;
import subway.line.exception.SectionNotFoundException;
import subway.station.domain.Station;
import subway.station.exception.StationNotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Line {

    private final Long id;
    private final LineName name;
    private final LinkedList<AbstractSection> sections;
    private final int additionalFare;

    public Line(Long id, String name, List<MiddleSection> sections, int additionalFare) {
        this.id = id;
        this.name = new LineName(name);
        this.sections = new LinkedList<>(sections);
        this.additionalFare = additionalFare;
        addTerminalSections();
    }

    public Line(String name, List<MiddleSection> sections, int additionalFare) {
        this(null, name, sections, additionalFare);
    }

    public Line(Line otherLine) {
        this(otherLine.getId(), otherLine.getName(), otherLine.getSections(), otherLine.getAdditionalFare());
    }

    private void addTerminalSections() {
        sections.addFirst(new UpstreamTerminalSection(getUpstreamTerminal()));
        sections.addLast(new DownstreamTerminalSection(getDownstreamTerminal()));
    }

    private Station getUpstreamTerminal() {
        return sections.get(0).getUpstream();
    }

    private Station getDownstreamTerminal() {
        return sections.get(sections.size() - 1).getDownstream();
    }

    public void addStation(Station stationToAdd, Station upstream, Station downstream, int distanceToUpstream) {
        validateStationNotExist(stationToAdd);

        final AbstractSection correspondingSection = findCorrespondingSection(upstream, downstream);
        final List<AbstractSection> sectionsToAdd = correspondingSection.insertInTheMiddle(stationToAdd, distanceToUpstream);
        addStation(correspondingSection, sectionsToAdd);
    }

    private void validateStationNotExist(Station stationToAdd) {
        if (isStationExist(stationToAdd)) {
            throw new DuplicateStationInLineException("노선에 이미 존재하는 역입니다.");
        }
    }

    private AbstractSection findCorrespondingSection(Station upstream, Station downstream) {
        return sections.stream()
                       .filter(section -> section.isCorrespondingSection(upstream, downstream))
                       .findAny()
                       .orElseThrow(() -> new SectionNotFoundException("노선에 해당하는 구간이 존재하지 않습니다."));
    }

    private void addStation(AbstractSection sectionToDelete, List<AbstractSection> sectionsToAdd) {
        final int indexToAdd = sections.indexOf(sectionToDelete);
        sections.add(indexToAdd, sectionsToAdd.get(1));
        sections.add(indexToAdd, sectionsToAdd.get(0));

        sections.remove(sectionToDelete);
    }

    public void deleteStation(Station stationToDelete) {
        validateStationExist(stationToDelete);

        if (areOnlyTwoStationsInLine()) {
            sections.removeIf(section -> section.getClass() == MiddleSection.class);
            return;
        }
        List<AbstractSection> sectionsToMerge = findSectionsToMerge(stationToDelete);
        mergeSections(sectionsToMerge);
    }

    private boolean areOnlyTwoStationsInLine() {
        return getSections().size() == 1;
    }

    private void validateStationExist(Station stationToDelete) {
        if (!isStationExist(stationToDelete)) {
            throw new StationNotFoundException("노선에 존재하지 않는 역입니다.");
        }
    }

    private List<AbstractSection> findSectionsToMerge(Station stationToDelete) {
        if (sections.getLast().contains(stationToDelete)) {
            return List.of(sections.getLast(), sections.get(sections.size() - 2));
        }
        return sections.stream()
                       .filter(section -> section.contains(stationToDelete))
                       .collect(Collectors.toList());
    }

    private void mergeSections(List<AbstractSection> sectionsToMerge) {
        final AbstractSection section = sectionsToMerge.get(0);
        final AbstractSection sectionToMerge = sectionsToMerge.get(1);
        final AbstractSection mergedSection = section.merge(sectionToMerge);

        sections.add(sections.indexOf(section), mergedSection);

        sections.remove(section);
        sections.remove(sectionToMerge);
    }

    private boolean isStationExist(Station station) {
        return sections.stream()
                       .anyMatch(section -> section.contains(station));
    }

    public boolean isLineEmpty() {
        return getSections().size() == 0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public List<MiddleSection> getSections() {
        return sections.subList(1, sections.size() - 1)
                       .stream()
                       .map(section -> (MiddleSection) section)
                       .collect(Collectors.toList());
    }

    public int getAdditionalFare() {
        return additionalFare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        if (Objects.isNull(id) || Objects.isNull(line.id)) {
            return Objects.equals(name, line.name) && Objects.equals(sections, line.sections);
        }
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(id)) {
            return Objects.hash(name, sections);
        }
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "name=" + name +
                ", sections=" + sections +
                '}';
    }
}
