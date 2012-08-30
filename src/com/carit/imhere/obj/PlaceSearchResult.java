package com.carit.imhere.obj;

public class PlaceSearchResult {
    
    private String [] html_attributions;
    private String next_page_token;
    private String status;
    private Place [] results;
    public String[] getHtml_attributions() {
        return html_attributions;
    }
    public void setHtml_attributions(String[] html_attributions) {
        this.html_attributions = html_attributions;
    }
    public String getNext_page_token() {
        return next_page_token;
    }
    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Place[] getResults() {
        return results;
    }
    public void setResults(Place[] results) {
        this.results = results;
    }
    
    

}
