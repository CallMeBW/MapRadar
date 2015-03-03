package de.bwirth.mapradar.main;
import android.app.Application;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.apputil.ObjectSerializer;
import de.bwirth.mapradar.model.*;
import static de.bwirth.mapradar.model.SortingMethod.ALPHABET;
import static de.bwirth.mapradar.model.SortingMethod.valueOf;
import de.bwirth.mapradar.provider.CategoriesDatabase;
import de.ip.mapradar.R;
import org.json.*;

import java.io.InputStream;
import java.util.*;

public class MapApplication extends Application {
    private static final String CONSUMER_KEY = "qD1SlAz3VH8WOf0YlDhJgQ";
    private static final String CONSUMER_SECRET = "OpuRzGSL76QcSNq4_tIxUTASQUU";
    private static final String TOKEN = "4Wkyzbe0bQXBAOapejGZe_9vrRtkrz93";
    private static final String TOKEN_SECRET = "s-URHk30dL_XOYEW1ukCaZaMzcs";
    private static final int[] icons = {
            R.drawable.mapradar_rosa,
            R.drawable.mapradar_blau,
            R.drawable.mapradar_gelb,
            R.drawable.mapradar_orange,
            R.drawable.mapradar_dblau,
            R.drawable.mapradar_gruen,
            R.drawable.mapradar_lila,
            R.drawable.mapradar_rot
    };
    private static final int[] colors = {
            R.color.rosa,
            R.color.blau,
            R.color.gelb,
            R.color.orange,
            R.color.dblau,
            R.color.gruen,
            R.color.lila,
            R.color.rot
    };
    private static MapApplication sInstance;
    private static Business[] foundBusinesses;
    public final Event[] sampleEvents = {
            new Event(new Event.LatLong(49.1, 48.6), "Stuttgart", "21. FEB. 15", "Maroon 5", "ab 75€", R.drawable.event_1),
            new Event(new Event.LatLong(49.1, 48.7), "Öhringen", "10. FEB. 15", "Cro", "ab 60€", R.drawable.event_1),
            new Event(new Event.LatLong(49.3, 48.5), "Heilbronn", "12. FEB. 15", "Dieter Nuhr", "ab 30€", R.drawable.event_nuhr),
            new Event(new Event.LatLong(49.2, 48.2), "Ludwigsburg", "21. MÄR. 15", "Theater", "ab 9,95€", R.drawable.event_2),
            new Event(new Event.LatLong(49.1, 48.1), "Stuttgart", "30. APR. 15", "Stadtfest", "gratis", R.drawable.event_2),
    };
    public Preference prefs;
    private SharedPreferences mPref;
    private YelpAPI api;
    private String lastKnownLocality;
    private Location lastKnownLocation;
    private HashMap<String, Integer> categoryIconMap, categoryColorMap;
    private CategoriesDatabase mCategoryDB;
    private Map<String, String> yelpCategories;
    private String[] googleCategories;
    private String[] googleCategoryStrings;

    public  Business[] getFoundBusinesses() {
        return foundBusinesses;
    }

    public static void setFoundBusinesses(Business[] foundBusinesses) {
        MapApplication.foundBusinesses = foundBusinesses;
    }

    public int getCategoryIconID(String categoryID) {
        Integer ret = categoryIconMap.get(categoryID);
        if (ret == null) {
            return R.drawable.mapradar_weiss;
        }
        return ret;
    }

    /**
     * Indizes fit to googlecategory arraylist
     *
     * @return
     */
    public Integer[] getSelectedCategoryIndices() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        String[] googleCategories1 = getGoogleCategories();
        for (int i = 0; i < googleCategories1.length; i++) {
            String catName = googleCategories1[i];
            if (mCategoryDB.isFavourite(catName)) {
                result.add(i);
            }
        }
        return result.toArray(new Integer[result.size()]);
    }

    public void setSelectedCategoryIndices(final List<Integer> selectedIndizes) {
        new AndroidUtil.VoidAsyncTask() {
            @Override
            protected void doInBackground() {
                for (int i = 0; i < googleCategories.length; i++) {
                    mCategoryDB.insertOrChangeCategory(getGoogleCategories()[i], selectedIndizes.contains(i));
                }
            }
        }.run(true);
    }

    public String[] getGoogleCategories() {
        return googleCategories;
    }

    public int getCategoryColor(String categoryID) {
        Integer ret = categoryColorMap.get(categoryID);
        if (ret == null) {
            return getResources().getColor(R.color.theme_primary);
        }
        return ret;
    }

    public String getMatchingCategory(String categoryID) {
        Iterator it = MapApplication.getInstance().getYelpCategories().entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            final String id = (String) pairs.getValue();
            if (id.equals(categoryID)) {
                return (String) pairs.getKey();
            }
        }
        return null;
    }

    public Map<String, String> getYelpCategories() {
        return yelpCategories;
    }

    public static MapApplication getInstance() {
        return sInstance;
    }

    public YelpAPI getApi() {
//        return api;
        return new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeApp();
    }

    private void initializeApp() {
        prefs = new Preference();
        api = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        yelpCategories = new LinkedHashMap<>(22);
        categoryColorMap = new HashMap<>(22);
        categoryIconMap = new HashMap<>(22);
        loadJSONCategories();
        mPref = this.getApplicationContext().getSharedPreferences("default", MODE_PRIVATE);
        mPref
                .edit()
                .putString("transport", prefs.getTransportation().toString())
                .putString("sort", prefs.getSortingMethod().toString())
                .apply();
    }

    private void loadJSONCategories() {
        // YELP
        try {
            InputStream is = getAssets().open(getString(R.string.file_name_yelp_categories));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONArray yelpCategoriesJSONArray = new JSONArray(json);
            for (int i = 0; i < yelpCategoriesJSONArray.length(); i++) {
                JSONObject yelpCategory = (JSONObject) yelpCategoriesJSONArray.get(i);
                String title = yelpCategory.getString("title");
                String alias = yelpCategory.getString("alias");
                yelpCategories.put(title, alias);
                categoryColorMap.put(alias, i < colors.length ? getResources().getColor(colors[i]) : getResources().getColor(R.color.theme_primary));
                categoryIconMap.put(alias, i < icons.length ? icons[i] : R.drawable.mapradar_weiss);
            }
        } catch (Exception ex) {
            Log.e("MapApplication", "could not red json addCategoriesToMap", ex);
            ex.printStackTrace();
        }

        //GOOGLE
        try {
            mCategoryDB = new CategoriesDatabase(getBaseContext());
            InputStream is = getAssets().open("googlecategories/"+getString(R.string.file_name_google_categories));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONArray googleCategoriesJSONArray = new JSONObject(json).getJSONArray("types");
            googleCategories = new String[googleCategoriesJSONArray.length()];
            googleCategoryStrings = new String[googleCategoriesJSONArray.length()];
            for (int i = 0; i < googleCategoriesJSONArray.length(); i++) {
                JSONArray mapTypeAndString = googleCategoriesJSONArray.getJSONArray(i);
                googleCategories[i] = mapTypeAndString.getString(0);
                googleCategoryStrings[i] = mapTypeAndString.getString(1);
                mCategoryDB.insertIfNotAlready(googleCategories[i]);
                categoryColorMap.put(googleCategories[i], i < colors.length ? getResources().getColor(colors[i]) : getResources().getColor(R.color
                        .theme_primary));
                categoryIconMap.put(googleCategories[i], i < icons.length ? icons[i] : R.drawable.mapradar_weiss);
            }
        } catch (Exception ex) {
            Log.e("MapApplication", "could not red json addCategoriesToMap", ex);
            ex.printStackTrace();
        }
    }

    public boolean isFirstRun() {
        return mPref.getBoolean("is_first_run", true);
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public String getLastKnownLocality() {
        return lastKnownLocality;
    }

    public void setLastKnownLocality(String lastKnownLocality) {
        this.lastKnownLocality = lastKnownLocality;
    }

    public void setFirstRunned() {
        SharedPreferences.Editor edit = mPref.edit();
        edit.putBoolean("is_first_run", false);

        edit.apply();
    }

    public String[] getGoogleCategoryNames() {
        return googleCategoryStrings;
    }

    public final class Preference {
        public Transportation getTransportation() {
            return Transportation.valueOf(mPref.getString("transport", Transportation.FOOT.toString()));
        }

        public void setTransportation(Transportation transportation) {
            mPref
                    .edit()
                    .putString("transport", transportation.toString())
                    .commit(); // absichtlich commit
        }

        public SortingMethod getSortingMethod() {
            return valueOf(mPref.getString("sort", ALPHABET.toString()));
        }

        public ArrayList<Business> getFavourites() throws Exception {
            String serializedString = mPref.getString("favours", null);
            if (serializedString == null) {
                return new ArrayList<>(0);
            }
            return (ArrayList<Business>) ObjectSerializer.deserialize(serializedString);
        }

        public void setFavourites(ArrayList<Business> favourites) throws Exception {
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString("favours", ObjectSerializer.serialize(favourites));
            editor.apply();
        }
    }
}