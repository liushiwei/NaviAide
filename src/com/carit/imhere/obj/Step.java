package com.carit.imhere.obj;

public class Step {
    private StringIntNode distance;
    private StringIntNode duration;
    private Location end_location;
    private String html_instructions;
    private Location start_location;
    private String travel_mode;
    private Polyline polyline;
    public StringIntNode getDistance() {
        return distance;
    }
    public void setDistance(StringIntNode distance) {
        this.distance = distance;
    }
    public StringIntNode getDuration() {
        return duration;
    }
    public void setDuration(StringIntNode duration) {
        this.duration = duration;
    }
    public Location getEnd_location() {
        return end_location;
    }
    public void setEnd_location(Location end_location) {
        this.end_location = end_location;
    }
    public String getHtml_instructions() {
        return html_instructions;
    }
    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }
    public Location getStart_location() {
        return start_location;
    }
    public void setStart_location(Location start_location) {
        this.start_location = start_location;
    }
    public String getTravel_mode() {
        return travel_mode;
    }
    public void setTravel_mode(String travel_mode) {
        this.travel_mode = travel_mode;
    }
    public Polyline getPolyline() {
        return polyline;
    }
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
    
    
    

}
