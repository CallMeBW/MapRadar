package de.ip.mapradar.model;
import java.io.Serializable;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 15.01.2015            <br></code>
 * Description:                    <br>
 */
public class Event implements Serializable {
    private final LatLong latLng;
    private String place;
    private String title;
    private String date;
    private String price;

    public LatLong getLatLng() {
        return latLng;
    }

    private final int imgResID;

    public Event(LatLong latLng, String place, String date, String title, String price, int imgResID) {
        this.latLng = latLng;
        this.place = place;
        this.date = date;
        this.title = title;
        this.price = price;
        this.imgResID = imgResID;
    }

    public int getImgResID() {
        return imgResID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class LatLong implements Serializable {
        public final double lat;
        public final double lng;

        public LatLong(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
