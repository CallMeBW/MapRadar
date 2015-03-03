package de.bwirth.mapradar.activity;
import android.app.Activity;
import android.content.Intent;
import android.location.*;
import android.net.*;
import android.os.*;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.apputil.GoogleQueryHelper;
import de.bwirth.mapradar.main.MapApplication;
import de.bwirth.mapradar.model.Business;
import de.bwirth.mapradar.provider.CategoriesDatabase;
import de.bwirth.mapradar.view.*;
import de.ip.mapradar.R;

import java.util.*;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class ExploreActivity extends BaseActivity {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LinearLayout container;
    private List<Business> businesses;
    private Activity thisActivity;
    private boolean viewDestroyed;
    private Handler mHandler;
    private GoogleProgressBar googleProgressBar;
    private BusinessSmallCardRecyclerAdapter.OnItemClickListener onCardClickListener = new BusinessSmallCardRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(Business busi, View v, int pos) {
            onCardClick(busi);
        }
    };
    private boolean mMakeNewQuery = true;
    private FrameLayout mMapContainer;
    private View clickableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_explore);
        overridePendingTransition(0, 0);
        setTitle("");
        mMapContainer = (FrameLayout) findViewById(R.id.map_home_container);
        final ObservableScrollView scroller = (ObservableScrollView) findViewById(R.id.scrollView);
        enableFadingActionBar(scroller, 200, getResources().getColor(R.color.theme_primary));
        clickableView = findViewById(R.id.parallax_click_view);
        clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExploreActivity.this, "onclick", Toast.LENGTH_SHORT).show();
            }
        });
//        scroller.setOnTouchListener(new View.OnTouchListener() {
//            boolean hasMoved =false;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                float y = event.getY();
//                return false;
//            }
//        });
        mMapContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExploreActivity.this, "kkk", Toast.LENGTH_SHORT).show();
            }
        });
        mHandler = new Handler();
        Business[] lastFoundBusis = MapApplication.getInstance().getFoundBusinesses();
        mMakeNewQuery = lastFoundBusis == null || lastFoundBusis.length == 0;
        mMapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                mMap = mMapFragment.getMap();
                if (mMap != null) {
                    initMap();
                }
            }
        };
        getSupportFragmentManager().beginTransaction().add(R.id.map_home_container, mMapFragment).commit();
        thisActivity = this;
        businesses = new ArrayList<>();
        container = (LinearLayout) findViewById(R.id.frg_home_linearlay);
        googleProgressBar = (GoogleProgressBar) findViewById(R.id.google_progress);
        tryRemoveProgressBar();
        viewDestroyed = false;
        ConnectivityManager connManager = (ConnectivityManager) thisActivity.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo g3Info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (!mMakeNewQuery) {
            reuseOldData();
        }
        if (wifiInfo.isConnected() || g3Info.isConnected()) {
            searchLocationAgain(0, 0);
        } else {
            final MaterialDialog.Builder builder = new MaterialDialog.Builder(thisActivity);
            builder.
                    title("Keine Internetverbindung")
                    .content("Sie sind nicht mit dem Internet verbunden. Um die App zu nutzen, benötigen Sie eine Internetverbindung.")
                    .cancelable(false)
                    .negativeText("Wifi-Einstellungen")
                    .positiveText("Erneut versuchen")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onNegative(MaterialDialog materialDialog) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            builder.build().show();
                        }

                        @Override
                        public void onPositive(MaterialDialog materialDialog) {
                            materialDialog.dismiss();
                            ConnectivityManager conMan = (ConnectivityManager) thisActivity.getSystemService(CONNECTIVITY_SERVICE);
                            final NetworkInfo wifiInf = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            final NetworkInfo g3Inf = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                            if (wifiInf.isConnected() || g3Inf.isConnected()) {
                                searchLocationAgain(0, 0);
                            } else {
                                builder.build().show();
                            }
                        }
                    });
            builder.build().show();
        }
    }

    private void reuseOldData() {
        final MapApplication mapApp = MapApplication.getInstance();
        final String[] categories = mapApp.getGoogleCategories();
        final TitleView[] titleViews = new TitleView[categories.length];
        final MultipleCardView[] cardViewContainer = new MultipleCardView[categories.length];
        final Business[] businessesOld = mapApp.getFoundBusinesses();

        for (int i = 0; i < categories.length; i++) {
            final String currCat = categories[i];
            titleViews[i] = new TitleView(ExploreActivity.this, currCat, "ALLE", mapApp.getCategoryColor(currCat));
            cardViewContainer[i] = new MultipleCardView(ExploreActivity.this, onCardClickListener);
            titleViews[i].getMoreButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent showCategory = new Intent(thisActivity, CategoryActivity.class);
                    showCategory.putExtra(CategoryActivity.EXTRA_CATEGORYID, currCat); //TODO
                    showCategory.putExtra(CategoryActivity.EXTRA_CATEGORY, currCat);
                    startActivity(showCategory);
                }
            });
            ArrayList<Business> fittingBussis = new ArrayList<>();
            for (Business possibleFittingBusiness : businessesOld) {
                if (currCat.contains(possibleFittingBusiness.category)) {
                    fittingBussis.add(possibleFittingBusiness);
                    Log.i("POSFIT", "fitted: " + possibleFittingBusiness.category + " into " + currCat);
                } else {
                    Log.i("POSFIT", possibleFittingBusiness.category + " didn't fit into " + currCat);
                }
            }
            cardViewContainer[i].setAdapter(fittingBussis.toArray(new Business[fittingBussis.size()]));
            if (fittingBussis.size() > 0) {
                container.addView(titleViews[i]);
                container.addView(cardViewContainer[i]);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy Explore Activity", Toast.LENGTH_SHORT);
        //todo use destroy
        viewDestroyed = true;
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HOME;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void searchLocationAgain(final int numberOfTries, final long mSecs) {
        new AsyncTask<Void, Void, Void>() {
            Location location;
            String name;
            String searchLocality;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(mSecs);
                } catch (InterruptedException ignored) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (viewDestroyed) {
                    return;
                }
                location = mMap.getMyLocation();
                if (location == null) { // try to use last known locaiton
                    location = MapApplication.getInstance().getLastKnownLocation();
                }
                if (location == null) { // if is still null
                    if (numberOfTries < 4) {
                        searchLocationAgain(numberOfTries + 1, 1500);
                    } else {
                        final MaterialDialog.Builder matDialog = new MaterialDialog.Builder(thisActivity);
                        matDialog.title("Standort nicht gefunden")
                                .content("Ihr Standort konnte nicht ermittelt werden.\nBitte aktivieren Sie GPS und WLAN und starten Sie die App " +
                                        "erneut.")
                                .positiveText("GPS einschalten")
                                .negativeText("Erneut versuchen")
                                .cancelable(false)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog materialDialog) {
                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        matDialog.build().show();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog materialDialog) {
                                        searchLocationAgain(0, 0);
                                    }
                                })
                                .build().show();
                    }
                    return;
                } else {
                    MapApplication.getInstance().setLastKnownLocation(location);
                }

                if (MapApplication.getInstance().getLastKnownLocality() != null) {
                    name = MapApplication.getInstance().getLastKnownLocality();
                    searchLocality = name;
                } else {
                    try {
                        Address place = new Geocoder(thisActivity).getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                        if (place.getMaxAddressLineIndex() >= 1) {
                            name = place.getAddressLine(0) + ", " + place.getAddressLine(1);
                        } else {
                            name = place.getLocality();
                        }
                        searchLocality = place.getLocality();
                        MapApplication.getInstance().setLastKnownLocality(searchLocality);
                    } catch (Exception e) {
                        Log.e("Home", "exception:", e);
                    }
                }
                TextView locationTitle = (TextView) thisActivity.findViewById(R.id.home_frg_location);
                if (locationTitle != null) {
                    locationTitle.setText(name);
                }
                LatLng mylLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylLatLng, 15));
                try {
                    if (mMakeNewQuery) {
                        query(mylLatLng);
                    }
                } catch (Exception e) {
                    Log.e("HomeFragment", "exception on query", e);
                }
            }
        }.execute();
    }

    private void initMap() {
        mMap = mMapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (businesses != null && businesses.size() > 0) {
                    showMapFragment();
                }
            }
        });
    }

    @Override
    protected void onContentScrolled(float percentage, int newY) {
        setTitle("MapRadar");
        mMapContainer.setPadding(0, 0, 0, (int) (percentage * 300));
        ViewGroup.LayoutParams params = clickableView.getLayoutParams();
        if(newY <= 360){
            params.height = 360 - newY; // TODO convert to dp
        }
        clickableView.setLayoutParams(params);
        getTitleView().setAlpha(percentage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MapApplication.Preference mapAppPref = MapApplication.getInstance().prefs;
        switch (item.getItemId()) {
            case R.id.mnu_filter_categories:
                createCategoryChooserDialog().show();
                break;
            case R.id.mnu_explore_search:
                startActivity(new Intent(this, SearchActivity2.class));
                break;
        }
        thisActivity.invalidateOptionsMenu();
        return true;
    }

    @Override
    protected void onCategoriesSelected(Integer[] integers) {
        super.onCategoriesSelected(integers);
        MapApplication.getInstance().setFoundBusinesses(null);
        startActivity(new Intent(this, ExploreActivity.class));
        finish();
    }

    @Override
    protected void onRefreshPulled(final SwipeRefreshLayout mSwipeRefreshLayout) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
        MapApplication.getInstance().setFoundBusinesses(null);
        startActivity(new Intent(this, ExploreActivity.class));
        finish();
    }

    private void query(final LatLng loc) {
        final CategoriesDatabase db = new CategoriesDatabase(this);
        final MapApplication mapApp = MapApplication.getInstance();
        final String[] categories = mapApp.getGoogleCategories();
        final String[] categoryNames = mapApp.getGoogleCategoryNames();
        final TitleView[] titleViews = new TitleView[categories.length];
        final MultipleCardView[] cardViewContainer = new MultipleCardView[categories.length];

        tryAddProgressBar();
        for (int i = 0; i < categories.length; i++) {
            final String cat = categories[i];
            final String categoryName = categoryNames[i];
            titleViews[i] = new TitleView(ExploreActivity.this, categoryName, "Alle", mapApp.getCategoryColor(cat));
            cardViewContainer[i] = new MultipleCardView(ExploreActivity.this, onCardClickListener);
            titleViews[i].getMoreButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent showCategory = new Intent(thisActivity, CategoryActivity.class);
                    showCategory.putExtra(CategoryActivity.EXTRA_CATEGORYID, cat); //TODO
                    showCategory.putExtra(CategoryActivity.EXTRA_CATEGORY, cat);
                    startActivity(showCategory);
                }
            });

            titleViews[i].setVisibility(View.GONE);
            cardViewContainer[i].setVisibility(View.GONE);
            container.addView(titleViews[i]);
            container.addView(cardViewContainer[i]);
        }

        final int[] finishedQueriesCount = {0, categories.length};
        final ArrayList<Business> allBusinessesFound = new ArrayList<>(50);
        for (int i = 0; i < categories.length; i++) {
            final String category = categories[i];
            final int finalI = i;
            if (!db.isFavourite(category)) {
                finishedQueriesCount[1]--;
                continue;
            }
            new AndroidUtil.VoidAsyncTask() {
                Business[] businessesForCurCat;

                @Override
                protected void doInBackground() {
                    businessesForCurCat = GoogleQueryHelper.searchNearby(loc, 10000, category);
                }

                @Override
                protected void onPostExecute() {
                    finishedQueriesCount[0]++;
                    if (businessesForCurCat == null
                            || businessesForCurCat.length == 0
                            || viewDestroyed
                            ) {
                        return;
                    }
                    allBusinessesFound.addAll(Arrays.asList(businessesForCurCat));
                    TitleView header = titleViews[finalI];
                    MultipleCardView cardContainer = cardViewContainer[finalI];
                    header.getMapButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent showMap = new Intent(thisActivity, DetailBusinessActivity.class);
                            showMap
                                    .putExtra("BusiCount", businessesForCurCat.length)
                                    .putExtra(DetailBusinessActivity.EXTRA_TITLE, category)
                                    .putExtra(DetailBusinessActivity.EXTRA_SELECT_FIRST, false);
                            for (int i = 0; i < businessesForCurCat.length; i++) {
                                Business busiToShow = businessesForCurCat[i];
                                showMap.putExtra("busi" + i, busiToShow.makeParcel());
                            }
                            startActivity(showMap);
                        }
                    });
                    cardContainer.setAdapter(businessesForCurCat);
                    cardContainer.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    if (finishedQueriesCount[0] == finishedQueriesCount[1]) {
                        tryRemoveProgressBar();
                        mapApp.setFoundBusinesses(allBusinessesFound.toArray(new Business[allBusinessesFound.size()]));
                    }
                }
            }.run(true);
        }
    }

    private void tryAddProgressBar() {
        try {
            container.addView(googleProgressBar);
        } catch (Exception e) {
            Log.e("ExploreActivity", "Could not add googleprogressbar", e);
        }
    }

    private void tryRemoveProgressBar() {
        try {
            container.removeView(googleProgressBar);
        } catch (Exception e) {
            Log.e("ExploreActivity", "Could not add googleprogressbar", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public void onCardClick(Business b) {
        startActivity(new Intent(thisActivity, DetailBusinessActivity.class)
                        .putExtra("BusiCount", 1)
                        .putExtra("busi0", b.makeParcel())
                        .putExtra(DetailBusinessActivity.EXTRA_SELECT_FIRST, true)
        );
    }

    public void showMapFragment() {
        Intent showMap = new Intent(thisActivity, DetailBusinessActivity.class);
        Bundle b = new Bundle();
        b.putInt("BusiCount", businesses.size());
        for (int i = 0; i < businesses.size(); i++) {
            b.putStringArrayList("busi" + i, businesses.get(i).makeParcel());
        }
        b.putBoolean(DetailBusinessActivity.EXTRA_SELECT_FIRST, false);
        showMap.putExtras(b);
        startActivity(showMap);
    }
}
