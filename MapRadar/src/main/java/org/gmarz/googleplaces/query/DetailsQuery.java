package org.gmarz.googleplaces.query;
public class DetailsQuery extends Query {
    public DetailsQuery(String placeID) {
        setPlaceID(placeID);
    }

    public void setPlaceID(String placeID) {
        mQueryBuilder.addParameter("placeid ", placeID);
    }

    @Override
    public String getUrl() {
        return "https://maps.googleapis.com/maps/api/place/details/json";
    }
}