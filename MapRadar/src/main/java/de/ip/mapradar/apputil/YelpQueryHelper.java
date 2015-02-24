package de.ip.mapradar.apputil;
import android.app.Activity;
import android.location.*;
import android.os.AsyncTask;
import android.util.Log;
import de.ip.mapradar.main.MapApplication;
import de.ip.mapradar.model.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.IOException;

/**
 * <code>
 * Project: YELP <br>
 * Date: 20.12.2014            <br></code>
 * Description:                    <br>
 */
public class YelpQueryHelper {

    private final YelpAPI API;
    private Geocoder coder;

    public YelpQueryHelper(YelpAPI api) {
        this.API = api;
    }

    public Business[] searchBusiness(YelpSearch search, Activity activity) {
        if (coder == null && activity != null) {
            coder = new Geocoder(activity);
        }
        String responseJ = API.searchForBusiness(
                search.searchTerm, search.location, search.categoryID, search.limit, search.sort.getIndex(), search.radius);
        JSONParser parser = new JSONParser();
        MapApplication mapApp = MapApplication.getInstance();
        try {
            JSONObject response = (JSONObject) parser.parse(responseJ);
            JSONArray businesses = (JSONArray) response.get("businesses");
            JSONObject region = (JSONObject) response.get("region");
            final Business[] ret = new Business[businesses.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new Business(mapApp.getMatchingCategory(search.categoryID), search.categoryID, (JSONObject) businesses.get(i), region);
                if (coder != null) {
                    final int finalI = i;
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            pimpBusinessWithGoogleData(ret[finalI]);
                            return null;
                        }
                    }.execute();
                }
            }
            return ret;
        } catch (Exception e) {
            Log.e("Query", "error:", e);

            System.err.println("Error: could not parse JSON response:");
            return new Business[0];
        }
    }

    public Business searchByBusiness(String businessID, Activity activity) {
        if (coder == null && activity != null) {
            coder = new Geocoder(activity);
        }
        String responseJ = API.searchByBusinessId(businessID);
        JSONParser parser = new JSONParser();
        MapApplication mapApp = MapApplication.getInstance();
        Business retBusi = null;
        try {
            JSONObject response = (JSONObject) parser.parse(responseJ);
            retBusi = new Business(null, null, response);
            final Business finalRetBusi = retBusi;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    pimpBusinessWithGoogleData(finalRetBusi);
                    return null;
                }
            }.execute();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retBusi;
    }

    public Business[] searchBusiness(YelpSearch search) {
        return searchBusiness(search, null);
    }

    private void pimpBusinessWithGoogleData(Business business) {
        try {
            Address address = coder.getFromLocationName(business.address + ", " + business.city, 1).get(0);
            if (address.getUrl() != null) {
                business.yelpURL = address.getUrl();
            }
            business.LONGITUDE = address.getLongitude();
            business.LATITUDE = address.getLatitude();
        } catch (IOException | IndexOutOfBoundsException | NullPointerException ignored) {

        }
    }
}
