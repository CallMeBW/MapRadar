package de.bwirth.mapradar.activity;
import android.app.Activity;
import android.content.*;
import android.database.Cursor;
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
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.apputil.GoogleQueryHelper;
import de.bwirth.mapradar.model.*;
import de.bwirth.mapradar.view.*;
import de.ip.mapradar.R;
import de.bwirth.mapradar.main.MapApplication;
import static de.bwirth.mapradar.model.Transportation.BIKE;
import static de.bwirth.mapradar.model.Transportation.CAR;
import static de.bwirth.mapradar.model.Transportation.FOOT;

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
    private MenuItem menuTransport;
    private boolean viewDestroyed;
    private TextView locationTitle;
    private Handler mHandler;
    private MenuItem menuFoot, menuBike, menuCar;
    private boolean mSearching;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_explore);
        overridePendingTransition(0, 0);
        mHandler = new Handler();
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
        android.support.v7.widget.Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle(R.string.title_home);
        locationTitle = (TextView) thisActivity.findViewById(R.id.home_frg_location);
        container = (LinearLayout) findViewById(R.id.frg_home_linearlay);
        ViewGroup curPlaceContainer = (ViewGroup) findViewById(R.id.frg_home_cur_place_container);
        viewDestroyed = false;
        ConnectivityManager connManager = (ConnectivityManager) thisActivity.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo g3Info = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo.isConnected() || g3Info.isConnected()) {
            searchLocationAgain(0, 0);
        } else {
            final MaterialDialog.Builder builder = new MaterialDialog.Builder(thisActivity);
            builder.
                    title("Keine Internetverbindung")
                    .content("Sie sind nicht mit dem Internet verbunden. Um die App zu nutzen, benötigen Sie eine Internetverbindung.")
                    .cancelable(false)
                    .negativeText("Wifi-Einstlg.")
                    .positiveText("Erneut versuchen")
                    .callback(new MaterialDialog.Callback() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                                .positiveText("Erneut versuchen")
                                .negativeText("GPS einschalten")
                                .cancelable(false)
                                .forceStacking(true)
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        locationTitle = (TextView) thisActivity.findViewById(R.id.home_frg_location);
                                        locationTitle.setText("");
                                    }
                                })
                                .callback(new MaterialDialog.Callback() {
                                    @Override
                                    public void onPositive(MaterialDialog materialDialog) {
                                        searchLocationAgain(0, 0);
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog materialDialog) {
                                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        matDialog.build().show();
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
                    query(mylLatLng);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        MapApplication.Preference mapAppPref = MapApplication.getInstance().prefs;
        switch (item.getItemId()) {
            case R.id.mnu_transport_foot:
                mapAppPref.setTransportation(FOOT);
                Toast.makeText(thisActivity, "Zu Fuß unterwgs", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnu_transport_car:
                mapAppPref.setTransportation(CAR);
                Toast.makeText(thisActivity, "Mit dem Auto unterwegs", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnu_filter_categories:
                createCategoryChooserDialog().show();
                break;
            case R.id.mnu_explore_search:
                startActivity(new Intent(this,SearchActivity2.class));
                break;
        }
        thisActivity.invalidateOptionsMenu();
        return true;
    }

    @Override
    protected void onRefreshPulled(final SwipeRefreshLayout mSwipeRefreshLayout) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void query(final LatLng loc) {
        for (final String category : MapApplication.getInstance().getGoogleCategories()) {
            new AndroidUtil.VoidAsyncTask() {
                Business[] businessesFound;

                @Override
                protected void doInBackground() {
                    businessesFound = GoogleQueryHelper.searchNearby(loc,10000,category);
                }

                @Override
                protected void onPostExecute() {
                    if (businessesFound == null
                            || businessesFound.length == 0
                            || viewDestroyed
                            ) {
                        return;
                    }
                    TitleView header = new TitleView(thisActivity, category, MapApplication.getInstance().getCategoryColor(category));
                    header.getMoreButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent showCategory = new Intent(thisActivity, CategoryActivity.class);
                            showCategory.putExtra(CategoryActivity.EXTRA_CATEGORYID, category); //TODO
                            showCategory.putExtra(CategoryActivity.EXTRA_CATEGORY, category);
                            startActivity(showCategory);
                        }
                    });
                    header.getMapButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent showMap = new Intent(thisActivity, DetailBusinessActivity.class);
                            showMap
                                    .putExtra("BusiCount", businessesFound.length)
                                    .putExtra(DetailBusinessActivity.EXTRA_TITLE, category)
                                    .putExtra(DetailBusinessActivity.EXTRA_SELECT_FIRST, false);
                            for (int i = 0; i < businessesFound.length; i++) {
                                Business busiToShow = businessesFound[i];
                                showMap.putExtra("busi" + i, busiToShow.makeParcel());
                            }
                            startActivity(showMap);
                        }
                    });
                    container.addView(header, container.getChildCount() - 1);
                    container.addView(new MultipleCardView(thisActivity, businessesFound), container.getChildCount() - 1);
                }
            }.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore, menu);
        Transportation curTrans = MapApplication.getInstance().prefs.getTransportation();
        menuTransport = menu.findItem(R.id.mnu_transport);
        menuTransport = menu.findItem(R.id.mnu_transport);
        menuFoot = menu.findItem(R.id.mnu_transport);
        menuBike = menu.findItem(R.id.mnu_transport);
        menuCar = menu.findItem(R.id.mnu_transport);
        menuTransport.setIcon(createTransportationIcon());
        menuFoot.setChecked(curTrans == FOOT);
        menuBike.setChecked(curTrans == BIKE);
        menuCar.setChecked(curTrans == CAR);
        return true;
    }

    @Override
    public boolean onSearchRequested() {
        Toast.makeText(this, "onsearchRequested", Toast.LENGTH_LONG).show();
        return super.onSearchRequested();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuTransport = menu.findItem(R.id.mnu_transport);
        menuTransport.setIcon(createTransportationIcon());
        return true;
    }

    private int createTransportationIcon() {
        Transportation curr = MapApplication.getInstance().prefs.getTransportation();
        return curr == FOOT
                ? R.drawable.ic_tb_person_white
                : curr == BIKE
                ? R.drawable.ic_tb_bike_white
                : R.drawable.ic_tb_car_white;
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
