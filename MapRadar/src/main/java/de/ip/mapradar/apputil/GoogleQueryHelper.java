package de.ip.mapradar.apputil;
import android.app.Activity;
import de.ip.mapradar.model.*;
import org.gmarz.googleplaces.GooglePlaces;
import org.gmarz.googleplaces.models.*;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

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

    public static Business[] searchBusiness(YelpSearch search, Activity activity) {
        PlacesResult result = null;
        try {
            GooglePlaces googlePlaces = new GooglePlaces(API_KEY);
            result = googlePlaces.getPlaces("food", 5000, 40.764941, -73.984886);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        if (result == null) {
            return new Business[0];
        }
        List<Place> places = result.getPlaces();
        Business[] ret = new Business[places.size()];
        for (int i = 0; i < ret.length; i++) {
            Place p = places.get(i);
            String[] values = new String[19];
            boolean detailNull = p.getDetail() == null;

            values[0] = "0";
            values[1] = p.getAddress();
            values[2] = p.getName();
            values[3] = "https://lh6.ggpht.com/eX8ouZqxQgu_BB9rGfOuERLmlK-4XLjbb96RS_OJMNbxTT-b9Nb-pQ4RTk7qHdPq0C_S=w300-rw";
            values[4] = detailNull ? "abc.de" : p.getDetail().getWebsite();
            values[5] = detailNull ? "012345" : p.getDetail().getPhoneNumber();
            values[6] = detailNull ? "123455" : p.getDetail().getPhoneNumber();
            values[7] = p.getLongitude() + "";
            values[8] = p.getLatitude() + "";
            values[9] = p.getAddress();
            values[10] = "74613";
            values[11] = "74613";
            values[12] = "DE";
            values[13] = ((int) p.getRating()) + "";
            values[14] = p.getDetail().getReviews().size() + "";
            values[15] = "";
            values[16] = "restaurant";
            values[17] = "2";
            values[18] = "3";

            ret[i] = new Business(values);
        }
        return ret;
    }
}
