package org.gmarz.googleplaces.models;
import android.location.Location;
import de.ip.mapradar.model.Business;
import org.json.*;

public class GBusiness extends Business {
    private PlaceDetails mDetails;


    public GBusiness(JSONObject jsonPlace) {
        try {
            name = jsonPlace.getString("name");
            id = jsonPlace.getString("id");
            imageURL = jsonPlace.getString("icon");
            LATITUDE = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            LONGITUDE = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            RATING = jsonPlace.getDouble("rating");
            if (jsonPlace.has("vicinity")) {
                address = jsonPlace.getString("vicinity");
            } else {
                address = jsonPlace.getString("formatted_address");
            }
            JSONArray types = jsonPlace.getJSONArray("types");
            if(types != null && types.length() > 0){
                category = (String) types.get(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the approximate distance in meters between this location and the given location.
     */
    public double getDistanceTo(Location location) {
        Location source = new Location("");
        source.setLatitude(LATITUDE);
        source.setLongitude(LONGITUDE);
        return source.distanceTo(location);
    }

    public PlaceDetails getDetail() {
        return mDetails;
    }
}