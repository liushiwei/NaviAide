package com.carit.imhere.obj;

public class Directions {
    private Route[] routes;
    private String status;

    public Route[] getRoutes(){
        return this.routes;
    }
    public void setRoutes(Route[] routes){
        this.routes = routes;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }
}
