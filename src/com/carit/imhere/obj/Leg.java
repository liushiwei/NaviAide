package com.carit.imhere.obj;

public class Leg {
    private StringIntNode distance;
    private StringIntNode duration;
    
    private String end_address;
    
    private Location end_location;
    
    private String start_address;
    
    private Location start_location;
    
    private Step[] steps;
    
    private String [] via_waypoint;

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

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public void setEnd_location(Location end_location) {
        this.end_location = end_location;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    public Location getStart_location() {
        return start_location;
    }

    public void setStart_location(Location start_location) {
        this.start_location = start_location;
    }

    
    public String[] getVia_waypoint() {
        return via_waypoint;
    }

    public void setVia_waypoint(String[] via_waypoint) {
        this.via_waypoint = via_waypoint;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }
    
    
    
    
    
    
    
    
    
    

}
