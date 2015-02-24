package de.ip.mapradar.model;
import android.util.Log;
import org.json.simple.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 21.12.2014            <br></code>
 * Description:                    <br>
 */
public class Business implements Serializable {
    private static final long serialVersionUID = 7526472295622776148L;
    public String
            id, address, name, imageURL, yelpURL, displayPhone, phone, city, stateCode,
            postalCode, countryCode, subCategory, subCategoryID, category, categoryID;
    public double
            LONGITUDE, LATITUDE, RATING;
    public long reviewCount;

    public Business() {

    }

    public Business(final String category, final String categoryID, final JSONObject business, JSONObject region) {
        final JSONObject location = (JSONObject) business.get("location");
        final JSONArray address = (JSONArray) location.get("address");
        final JSONArray categories = (JSONArray) business.get("categories");
        id = (String) business.get("id");
        if (address.size() > 0) {
            this.address = (String) address.get(0);
        } else {
            this.address = "";
        }
        final Object is_closed = business.get("is_closed");
        name = (String) business.get("name");
        if (categories != null && categories.size() >= 2) {
            subCategory = (String) ((JSONArray) categories.get(0)).get(0);
            subCategoryID = (String) ((JSONArray) categories.get(0)).get(1);
        }
        imageURL = (String) business.get("image_url");
        String url = (String) business.get("url");
        this.category = category;
        this.categoryID = categoryID;
        try {
            url = java.net.URLDecoder.decode((String) business.get("url"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("Error decoding URL", e.getMessage());
        } finally {
            yelpURL = url;
        }
        displayPhone = (String) business.get("display_phone");
        phone = (String) business.get("phone");
        if (region != null) {
            LONGITUDE = (Double) ((JSONObject) region.get("center")).get("longitude");
            LATITUDE = (Double) ((JSONObject) region.get("center")).get("latitude");
        }
        RATING = (double) business.get("rating");
        city = (String) location.get("city");
        stateCode = (String) location.get("state_code");
        postalCode = (String) location.get("postal_code");
        countryCode = (String) location.get("country_code");
        reviewCount = (long) business.get("review_count");
    }

    public Business(final String category, final String categoryID, final JSONObject business) {
        this(category, categoryID, business, null);
    }

    public Business(String[] values) {
        this.id = values[0];
        this.address = values[1];
        this.name = values[2];
        this.imageURL = values[3];
        this.yelpURL = values[4];
        this.displayPhone = values[5];
        this.phone = values[6];
        LONGITUDE = Double.parseDouble(values[7]);
        LATITUDE = Double.parseDouble(values[8]);
        this.city = values[9];
        this.stateCode = values[10];
        this.postalCode = values[11];
        this.countryCode = values[12];
        RATING = Double.parseDouble(values[13]);
        reviewCount = Integer.parseInt(values[14]);
        subCategory = values[15];
        category = values[16];
        categoryID = values[17];
        subCategoryID = values[18];
    }

    public final ArrayList<String> makeParcel() {
        ArrayList<String> parceableList = new ArrayList<>(17);
        parceableList.addAll(Arrays.asList(
                id, address, name, imageURL, yelpURL, displayPhone, phone, "" + LONGITUDE, "" + LATITUDE, city, stateCode, postalCode, countryCode,
                RATING + "", "" + reviewCount, subCategory, category, categoryID, subCategoryID));
        return parceableList;
    }

    public static Business getInstance(final ArrayList<String> parceableList) throws ParseException {
        try {
            return new Business(parceableList.toArray(new String[parceableList.size()]));
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            throw new ParseException("given list cannot be converted to a Business", -1);
        }
    }

    @Override
    public String toString() {
        return String.format("\"%s\":\nname: %s\naddress: %s\nimageURL: %s\n" +
                        "url: %s\ndisplayphone: %s\nphone: %s\ncity: %s\nstatecode: %s\npostalcode: %s\ncountrycode: %s\n" +
                        "longitude: %s\nlatitude: %s\nreviewCount: %s\ncategory: %s\nsubCategory: %s\ncategoryID: %s\nsubCategoryID: " +
                        "%s",
                id, name, address, imageURL, yelpURL,
                displayPhone, phone, city, stateCode, postalCode, countryCode,
                LONGITUDE, LATITUDE, reviewCount, category, subCategory, categoryID, subCategoryID);
    }
}
