package de.bwirth.mapradar.activity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.main.MapApplication;
import de.bwirth.mapradar.model.Business;
import de.ip.mapradar.R;

import java.text.ParseException;
import java.util.ArrayList;

public class DetailBusinessActivity extends ActionBarActivity {
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private TextView phone, addressview;
    private TextView ratingTextView;
    private RatingBar ratingBar;
    private ScrollView scrollView;
    private TextView url;
    private SlidingUpPanelLayout mLayout;
    private Business curSelPlace;
    private ArrayList<Business> businesses;
    public static final String EXTRA_SELECT_FIRST = "extra_sel_first";
    public static final String EXTRA_TITLE = "extra_title";
    private String defaultTitle;
    private ImageView mainButtonImage;
    private TextView mainButtonText;
    private ViewGroup navigationContainer;
    private TextView addressviewExpanded;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
//        setTaskDescription(new ActivityManager.TaskDescription("Activity 2",bmp,getResources().getColor(R.color.theme_primary)));



        setContentView(R.layout.act_detail_busi);
        final View toolBar = findViewById(R.id.sliding_toolbar);
        setSupportActionBar((android.support.v7.widget.Toolbar) toolBar);
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        restoreBundle();
        mainButtonImage = (ImageView) findViewById(R.id.map_slider_imgview_navigate);
        mainButtonText = (TextView) findViewById(R.id.map_slider_txtview_navigate);
        navigationContainer = (ViewGroup) findViewById(R.id.map_slider_navigation_expanded_container);
        phone = (TextView) findViewById(R.id.detail_phone);
        ratingTextView = (TextView) findViewById(R.id.detail_tv_rating);
        addressview = (TextView) findViewById(R.id.detail_address);
        addressviewExpanded = (TextView) findViewById(R.id.detail_address_expanded);
        ratingBar = (RatingBar) findViewById(R.id.detail_ratingbar);
        url = (TextView) findViewById(R.id.detail_url);
        scrollView = (ScrollView) findViewById(R.id.scrollView_in_slider);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        SimplePanelSlideListener panelSlideListener = (new SimplePanelSlideListener() {
            private float slideOffset = 0;

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                this.slideOffset = slideOffset;
                if (slideOffset < 0.6) {
                    int height = AndroidUtil.getScreenHeight(DetailBusinessActivity.this) - AndroidUtil.dipInPixels(68, getResources());
                    setMapHeight(height);
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {
                setMapHeight((int) (mLayout.getHeight() * (1 - slideOffset * 1.1)));
                addressview.setText(curSelPlace.address + ", " + curSelPlace.city);
                mainButtonImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_tb_navigate_white));
                mainButtonText.setText("Navigieren");
                navigationContainer.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelExpanded(View panel) {
                addressview.setText(curSelPlace.category);
                mainButtonImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse));
                mainButtonText.setText("Einklappen");
                navigationContainer.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelAnchored(View panel) {
                scrollView.scrollTo(0, 0);
                setMapHeight((int) (mLayout.getHeight() * (1 - slideOffset * 1.1)));
                addressview.setText(curSelPlace.address + ", " + curSelPlace.city);
                mainButtonImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_tb_navigate_white));
                mainButtonText.setText("Navigieren");
                navigationContainer.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        });
        mLayout.setPanelSlideListener(panelSlideListener);
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

        getSupportFragmentManager().beginTransaction().replace(R.id.sliding_map_container, mMapFragment).commit();
        defaultTitle = getIntent().getStringExtra(EXTRA_TITLE);
        if (defaultTitle == null) {
            defaultTitle = getString(R.string.title_home);
        }
        if (getIntent().getBooleanExtra(EXTRA_SELECT_FIRST, false)) {
            curSelPlace = businesses.get(0);
            updateValues();
            int height = AndroidUtil.getScreenHeight(DetailBusinessActivity.this);
            setMapHeight((int) (height * 0.4));
        } else {
            setTitle(defaultTitle);
            mLayout.hidePanel();
        }
    }

    private void setMapHeight(int height) {
        View mv = findViewById(R.id.sliding_map_container);
        mv.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        );
    }

    private void updateValues() {
        if (curSelPlace == null) {
            mLayout.hidePanel();
            setTitle(defaultTitle);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.theme_primary));
            int color = getResources().getColor(R.color.theme_primary700);
            setStatusBarColor(color);
            return;
        }
        navigationContainer.setVisibility(View.GONE);
        int color = MapApplication.getInstance().getCategoryColor(curSelPlace.categoryID);
        findViewById(R.id.slider_bottom_draggable).setBackgroundColor(color);
        findViewById(R.id.sliding_toolbar).setBackgroundColor(color);
        setStatusBarColor(darkerColorOf(color));
        mLayout.showPanel();
        setTitle(curSelPlace.name);
        if (curSelPlace.displayPhone != null && !curSelPlace.displayPhone.isEmpty()) {
            phone.setText(curSelPlace.displayPhone);
            phone.setVisibility(View.VISIBLE);
        } else {
            phone.setVisibility(View.GONE);
        }

        ratingBar.setRating((float) curSelPlace.RATING);
        ViewGroup containerForRatings = (ViewGroup) findViewById(R.id.container_for_ratings);
        if (curSelPlace.RATING == 0) {
            ratingBar.setVisibility(View.GONE);
            ratingTextView.setText("Keine Bewertungen vorhanden");
            TextView tvNoRatings = new TextView(this);
            tvNoRatings.setText("Keine Bewertungen vorhanden");
            tvNoRatings.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            containerForRatings.addView(tvNoRatings);
        } else {
            ratingBar.setVisibility(View.VISIBLE);
            ratingTextView.setText(" (" + curSelPlace.reviewCount + ")");
            for (int i = 0; i < curSelPlace.reviewCount; i++) {
                containerForRatings.addView(getLayoutInflater().inflate(R.layout.rating, null));
            }
        }
        url.setText(curSelPlace.yelpURL);
        addressview.setText(curSelPlace.address + ", " + curSelPlace.city);
        addressviewExpanded.setText(curSelPlace.address + ", " + curSelPlace.city);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailmenu_location, menu);
        menu.findItem(R.id.mnu_layer_normal).setChecked(true);
        if (mLayout.getPanelState() == PanelState.EXPANDED) {
            menu.findItem(R.id.mnu_all_layers).setVisible(false);
        }
        final MenuItem menuFav = menu.findItem(R.id.mnu_favourite);
        menuFav.setVisible(curSelPlace != null);
        try {
            isFavorite = false;
            for (Business busi : MapApplication.getInstance().prefs.getFavourites()) {
                if (curSelPlace != null && busi.id.equals(curSelPlace.id)) {
                    isFavorite = true;
                    break;
                }
            }
        } catch (Exception e) {
            isFavorite = false;
        }
        Drawable iconFav;
        if (isFavorite) {
            iconFav = getResources().getDrawable(R.drawable.ic_favorite_white);
        } else {
            iconFav = getResources().getDrawable(R.drawable.ic_favorite_outline_white);
        }
        menuFav.setIcon(iconFav);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.mnu_layer_normal:
                item.setChecked(!item.isChecked());
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mnu_layer_satellite:
                item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.mnu_favourite:
                MapApplication.Preference mapAppPref = MapApplication.getInstance().prefs;
                Drawable nextDrawable;
                try {
                    ArrayList<Business> favs = mapAppPref.getFavourites();
                    if (isFavorite) {
                        nextDrawable = getResources().getDrawable(R.drawable.ic_favorite_outline_white);
                        Toast.makeText(this, "Favorit entfernt", Toast.LENGTH_SHORT).show();
                        favs.remove(curSelPlace);
                        int indexToDelete = -1;
                        for (int i = 0; i < favs.size(); i++) {
                            Business busi = favs.get(i);
                            if (curSelPlace != null && busi.id.equals(curSelPlace.id)) {
                                indexToDelete = i;
                                break;
                            }
                        }
                        if (indexToDelete >= 0) {
                            favs.remove(indexToDelete);
                        }
                    } else {
                        nextDrawable = getResources().getDrawable(R.drawable.ic_favorite_white);
                        Toast.makeText(this, "Favorit hinzugefügt", Toast.LENGTH_SHORT).show();
                        favs.add(curSelPlace);
                    }
                    mapAppPref.setFavourites(favs);
                    isFavorite = !isFavorite;
                    item.setIcon(nextDrawable);
                } catch (Exception e) {
                    Toast.makeText(this, "Fehler beim Markieren.", Toast.LENGTH_LONG).show();
                    Log.e("SERIALIZE", "", e);
                }

                break;
        }
        return true;
    }

    private int darkerColorOf(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.85f; // value component
        return Color.HSVToColor(hsv);
    }

    private void initMap() {
        mMap = mMapFragment.getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mMap.getMyLocation() != null) {
            builder.include(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()));
        }
        int markerCount = 0;
        Marker lastMarker = null;
        MapApplication mapApp = MapApplication.getInstance();
        for (int i = 0; i < businesses.size(); i++, markerCount++) {
            Business business = businesses.get(i);

            lastMarker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(mapApp.getCategoryIconID(business.categoryID)))
                            .position(new LatLng(business.LATITUDE, business.LONGITUDE))

            );
            lastMarker.setTitle("" + i);
            builder.include(lastMarker.getPosition());
        }
        final int finalMarkerCount = markerCount;
        final Marker finalLastMarker = lastMarker;
        new AndroidUtil.VoidAsyncTask() {
            @Override
            protected void doInBackground() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute() {
                try {
                    CameraUpdate cu = null;
                    if (finalMarkerCount > 1) {
                        LatLngBounds bounds = builder.build();
                        cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                        mMap.animateCamera(cu, 2000, null);
                    } else if (finalLastMarker != null) {
                        cu = CameraUpdateFactory.newLatLng(finalLastMarker.getPosition());
                    }
                    mMap.animateCamera(cu, 2000, null);
                    if (finalMarkerCount > 1) {
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                    }
                } catch (Exception e) {
                    Log.e("Animate camera", "exception:", e);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (mLayout.getPanelState() == PanelState.ANCHORED) {
                    mLayout.collapsePanel();
                }
                int index = Integer.parseInt(marker.getTitle());
                curSelPlace = businesses.get(index);
                int zoom = (int) Math.min(15, mMap.getCameraPosition().zoom);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoom), 500, null);
                updateValues();
                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mLayout.getPanelState() == PanelState.ANCHORED) {
                    mLayout.collapsePanel();
                } else if (mLayout.getPanelState() == PanelState.COLLAPSED) {
                    curSelPlace = null;
                    invalidateOptionsMenu();
                    updateValues();
                }
            }
        });
    }

    public void onClickPhone(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + curSelPlace.phone));
        startActivity(callIntent);
    }

    public void onClickURL(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(curSelPlace.yelpURL));
        startActivity(i);
    }

    public void onClickRate(View view) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.positiveText("Bewerten");
        builder.negativeText("Abbrechen");
        builder.customView(R.layout.rate_view);
        builder.title("Bewerten Sie disen Ort:");
        builder.callback(new MaterialDialog.SimpleCallback() {
            @Override
            public void onPositive(MaterialDialog materialDialog) {
                Toast.makeText(DetailBusinessActivity.this, "Bewertung abgegeben", Toast.LENGTH_SHORT).show();
            }
        });
        builder.build().show();
    }

    public void onClickShare(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sehen Sie sich diese Location an:\n" + curSelPlace.name + "\n" + curSelPlace.yelpURL);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void onClickNavigation(View view) {
        if (mLayout.getPanelState() == PanelState.EXPANDED) {
        } else {
            onClickNavigationExpanded(view);
        }
    }

    public void onClickNavigationExpanded(View view) {
        Intent intent =
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" +
                                "&daddr=" + String.valueOf(curSelPlace.LATITUDE) + ","
                                + String.valueOf(curSelPlace.LONGITUDE)));
        startActivity(intent);
    }

    private void restoreBundle() {
        Bundle args = getIntent().getExtras();
        businesses = new ArrayList<>();
//        latLongs = new ArrayList<>();
        int busiCount = args.getInt("BusiCount", 0);
        for (int i = 0; i < busiCount; i++) {
            ArrayList<String> parcel = args.getStringArrayList("busi" + i);
            if (parcel == null) {
                continue;
            }
            try {
                businesses.add(Business.getInstance(parcel));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}