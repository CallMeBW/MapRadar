package org.gmarz.googleplaces.models;

import org.json.*;

import java.util.*;

public class PlacesResult extends Result {

	private List<GBusiness> mGBusinesses = new ArrayList<GBusiness>();
	
	public PlacesResult(JSONObject jsonResponse) throws JSONException {
		super(jsonResponse);
		
		if (jsonResponse.has("results")) {
			JSONArray results = jsonResponse.getJSONArray("results");
			for(int i = 0; i < results.length(); i++) {
				GBusiness GBusiness = new GBusiness(results.getJSONObject(i));
				mGBusinesses.add(GBusiness);
			}	
		}
	}

	public List<GBusiness> getPlaces() {
		return mGBusinesses;
	}
}
