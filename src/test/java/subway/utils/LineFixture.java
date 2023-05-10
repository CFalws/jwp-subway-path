package subway.utils;

import subway.domain.Line;

import java.util.LinkedList;
import java.util.List;

import static subway.utils.SectionFixture.JAMSIL_TO_JAMSILNARU;

public class LineFixture {

    public static final Line LINE_NUMBER_TWO = new Line("2호선", new LinkedList(List.of(JAMSIL_TO_JAMSILNARU)));
}
