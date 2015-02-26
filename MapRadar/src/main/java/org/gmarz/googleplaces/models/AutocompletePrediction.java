package org.gmarz.googleplaces.models;
/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 26.02.2015            <br></code>
 * Description:                    <br>
 */
public class AutocompletePrediction {
    public final String description, placeID;
    public final String[] types;

    public AutocompletePrediction(String description, String placeID, String[] types) {
        this.description = description;
        this.placeID = placeID;
        this.types = types;
    }
}
