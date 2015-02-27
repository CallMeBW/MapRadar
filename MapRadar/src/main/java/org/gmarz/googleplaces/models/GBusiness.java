package org.gmarz.googleplaces.models;
import android.location.Location;
import android.util.Log;
import de.bwirth.mapradar.model.Business;
import org.json.*;

public class GBusiness extends Business {
    private PlaceDetails mDetails;
    public  final String LOG_TAG = getClass().getSimpleName();

    public GBusiness(JSONObject jsonPlace) {
        try {
            name = jsonPlace.getString("name");
            id = jsonPlace.getString("id");
            imageURL = jsonPlace.getString("icon");
            if(jsonPlace.has("photos")){
                photoRef = ((JSONObject) ((JSONArray) jsonPlace.get("photos")).get(0)).getString("photo_reference");
            }
            LATITUDE = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            LONGITUDE = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            if (jsonPlace.has("rating")) {
                RATING = (double) jsonPlace.get("rating");
            }
            if (jsonPlace.has("vicinity")) {
                address = jsonPlace.getString("vicinity");
            } else {
                address = jsonPlace.getString("formatted_address");
            }
            JSONArray types = jsonPlace.getJSONArray("types");
            if (types != null && types.length() > 0) {
                category = (String) types.get(0);
            }
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Error looking up Google Place "+name, e);
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