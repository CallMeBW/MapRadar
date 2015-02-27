package de.bwirth.mapradar.apputil;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import de.bwirth.mapradar.model.Business;
import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.models.*;
import org.gmarz.googleplaces.query.AutocompleteQuery;
import org.json.JSONException;

import java.io.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 24.02.2015            <br></code>
 * Description:                    <br>
 */
public class GoogleQueryHelper {
    private final static String API_KEY = "AIzaSyA1IbiQIiFZms3XsJze3IdrBAm0zkXQl-Q";
    private final static GooglePlaces googlePlaces = new GooglePlaces(API_KEY);;

    public static Business[] searchNearby(LatLng location, int radius, String category) {
        PlacesResult result = null;
        try {
            result = googlePlaces.getPlaces(category, radius, location.latitude, location.longitude);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        if (result == null) {
            return new Business[0];
        }
        return result.getPlaces().toArray(new GBusiness[result.getPlaces().size()]);
    }

    public static AutoCompleteResult getAutocompletePredictions(String searchTerm, int cursorPos){
        AutocompleteQuery query = new AutocompleteQuery(searchTerm,cursorPos);
        try {
            return googlePlaces.getAutocompletePredictions(query);
        } catch (JSONException | IOException e) {
            Log.e("AUTOCOMPLETE","fehler",e);
            return null;
        }
    }


    public static InputStream getImgStream(String photoRef){
        try {
            return googlePlaces.getPlacePhotoStream(photoRef, 500);
        } catch (JSONException | IOException e) {
           return null;
        }
    }
}
