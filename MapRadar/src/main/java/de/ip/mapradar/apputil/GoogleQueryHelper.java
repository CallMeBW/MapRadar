package de.ip.mapradar.apputil;
import de.ip.mapradar.model.Business;
import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.models.*;
import org.json.JSONException;

import java.io.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 24.02.2015            <br></code>
 * Description:                    <br>
 */
public class GoogleQueryHelper {
    private final static String API_KEY = "AIzaSyBamxP_zAHM93Co50vhony9HCrAIPpW83A";

    public GoogleQueryHelper() {
    }

    public static Business[] searchBusiness() {
        PlacesResult result = null;
        try {
            GooglePlaces googlePlaces = new GooglePlaces(API_KEY);
            result = googlePlaces.getPlaces("food|restaurant|cafe|meal_delivery|meal_takeaway", 10000, 49.199931, 9.502949);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        if (result == null) {
            return new Business[0];
        }
        GBusiness[] businesses = result.getPlaces().toArray(new GBusiness[result.getPlaces().size()]);
        return businesses;
    }

    public static InputStream getImgStream(String photoRef){
        try {
            GooglePlaces googlePlaces = new GooglePlaces(API_KEY);
            return googlePlaces.getPlacePhotoStream(photoRef, 500);
        } catch (JSONException | IOException e) {
           return null;
        }
    }
}
