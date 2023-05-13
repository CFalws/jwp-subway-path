package subway.dto;

import javax.validation.constraints.NotNull;

public class AddLineRequest {

    @NotNull
    private String lineName;
    @NotNull
    private String upstreamName;
    @NotNull
    private String downstreamName;
    @NotNull
    private int distance;

    public AddLineRequest(String lineName, String upstreamName, String downstreamName, int distance) {
        this.lineName = lineName;
        this.upstreamName = upstreamName;
        this.downstreamName = downstreamName;
        this.distance = distance;
    }

    public String getLineName() {
        return lineName;
    }

    public String getUpstreamName() {
        return upstreamName;
    }

    public String getDownstreamName() {
        return downstreamName;
    }

    public int getDistance() {
        return distance;
    }
}
