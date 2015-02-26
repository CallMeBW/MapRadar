package org.gmarz.googleplaces.query;
import com.google.android.gms.maps.model.LatLng;

public class AutocompleteQuery extends SearchQuery {


	public AutocompleteQuery(String input) {
        setInput(input);
    }
    public AutocompleteQuery(String input, int caretPosition) {
        setInput(input);
        setOffset(caretPosition);
    }
    public AutocompleteQuery(String input, int caretPosition, LatLng location) {
        setInput(input);
        setOffset(caretPosition);
        setLocation(location);
    }
    public AutocompleteQuery(String input, int caretPosition, LatLng location, String types) {
        setInput(input);
        setOffset(caretPosition);
        setLocation(location);
        setTypes(types);
    }

    private void setTypes(String types) {
        mQueryBuilder.addParameter("types", types);
    }

    private void setOffset(int caretPosition) {
        mQueryBuilder.addParameter("offset", caretPosition+"");
    }
    private void setLocation(LatLng location) {
        mQueryBuilder.addParameter("location", location.latitude+","+location.longitude);
    }

    //https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRnAAAAtOWKX17fP1dy0dbP3_KmFNis97hQLL3gEJu-_63uXONqTkh-gXGkwngRnfGZ07DCh27Kf2iA1ChlqvRLOEInC5OOJ-G1bSN8GZiwoT5xFi7A7PF430hsqP5jrU1KmoGNyUaPIaV80JdAeK-Yzjv6PhIQZMkw9RoGdjTnalyDuzvX4BoUfmI66-pIheQHz_CzHgrYJFVe9aM&key=AIzaSyBamxP_zAHM93Co50vhony9HCrAIPpW83A
	public void setInput(String input)	{
		mQueryBuilder.addParameter("input", input);
	}

	@Override
	public String getUrl() {
		return "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	}
}
