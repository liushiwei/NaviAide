package com.carit.platform.response;


public class LocationResponse {
	private double lat;
	private double lng;
	private long time;
	
	public LocationResponse() {
		
	}
	
	public LocationResponse(double lat, double lng, long time) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.time = time;
	}


	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
}
