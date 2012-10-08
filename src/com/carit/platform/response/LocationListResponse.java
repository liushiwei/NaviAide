package com.carit.platform.response;

import java.util.List;

public class LocationListResponse {

	private List<LocationResponse> lists;

	
	public LocationListResponse() {
		super();
	}

	public LocationListResponse(List<LocationResponse> lists) {
		super();
		this.lists = lists;
	}

	public List<LocationResponse> getLists() {
		return lists;
	}

	public void setLists(List<LocationResponse> lists) {
		this.lists = lists;
	}
	
}
