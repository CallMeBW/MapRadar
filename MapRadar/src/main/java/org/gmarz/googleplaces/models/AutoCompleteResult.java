package org.gmarz.googleplaces.models;
import org.json.*;

import java.util.*;

public class AutoCompleteResult extends Result {
    private List<AutocompletePrediction> mPredictions = new ArrayList<>();

    public AutoCompleteResult(JSONObject jsonResponse) throws JSONException {
        super(jsonResponse);

        if (jsonResponse.has("predictions")) {
            JSONArray results = jsonResponse.getJSONArray("predictions");
            for (int i = 0; i < results.length(); i++) {
                JSONObject predictionJSON = results.getJSONObject(i);
                String description = predictionJSON.getString("description");
                String placeID = predictionJSON.getString("place_id");
                JSONArray typesJSON = predictionJSON.getJSONArray("types");
                String[] types = new String[typesJSON.length()];
                for (int t = 0; t < types.length; t++) {
                    types[i] = typesJSON.getString(i);
                }
                mPredictions.add(
                        new AutocompletePrediction(description, placeID, types)
                );
            }
        }
    }

    public List<AutocompletePrediction> getPredictions() {
        return mPredictions;
    }
}
