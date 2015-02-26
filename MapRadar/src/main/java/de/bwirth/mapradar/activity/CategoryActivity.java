package de.bwirth.mapradar.activity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import de.bwirth.mapradar.model.*;
import de.bwirth.mapradar.view.DetailCardView;
import de.ip.mapradar.R;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.apputil.YelpQueryHelper;
import de.bwirth.mapradar.main.MapApplication;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class CategoryActivity extends BaseActivity {
    public static final String EXTRA_CATEGORY = "extra_categ";
    public static final String EXTRA_CATEGORYID = "extra_categ_id";
    public static final String EXTRA_BUSINESS_COUNT = "BusiCount";
    public static final String EXTRA_BUSINESS_PREFIX = "busi";
    private MapApplication mapApp;
    private String category, categoryID;
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapApp = MapApplication.getInstance();
        category = getIntent().getStringExtra(EXTRA_CATEGORY);
        categoryID = getIntent().getStringExtra(EXTRA_CATEGORYID);
        setContentView(R.layout.act_single_category);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.category_toolbar));
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        container = (ViewGroup) findViewById(R.id.container_for_category_detail_cards);
        ab.setTitle(category);
        final int categoryColor = mapApp.getCategoryColor(categoryID);
        findViewById(R.id.category_toolbar).setBackgroundColor(categoryColor);
        setStatusBarColor(darkerColorOf(categoryColor));

        lookUp(0, 5, true);
        lookUp(6, 20, true);
    }

    private int darkerColorOf(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.85f; // value component
        return Color.HSVToColor(hsv);
    }

    private void lookUp(final int lo, final int maxResult, final boolean runInParallel) {
        new AndroidUtil.VoidAsyncTask() {
            Business[] businesses;

            @Override
            public void doInBackground() {
                YelpSearch search = new YelpSearch(mapApp.getLastKnownLocality())
                        .radius(5000)
                        .maxSearchResults(maxResult)
                        .category(categoryID)
                        .sorting(YelpSearch.Sorting.DISTANCE);
                YelpQueryHelper queryHelper = new YelpQueryHelper(mapApp.getApi());
                businesses = queryHelper.searchBusiness(search);
            }

            @Override
            public void onPostExecute() {
                if (businesses == null || businesses.length == 0) {
                    return;
                }
                for (int i = 0; i < businesses.length; i++) {
                    final Business business = businesses[i];
                    if (i < lo) {
                        continue;
                    }
                    DetailCardView card = new DetailCardView(CategoryActivity.this, business);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent showDetail = new Intent(CategoryActivity.this, DetailBusinessActivity.class);
                            showDetail.putExtra(CategoryActivity.EXTRA_BUSINESS_COUNT, 1);
                            showDetail.putExtra(CategoryActivity.EXTRA_BUSINESS_PREFIX + "0", business.makeParcel());
                            showDetail.putExtra(DetailBusinessActivity.EXTRA_SELECT_FIRST, true);
                            startActivity(showDetail);
                        }
                    });
                    container.addView(card);
                }
            }
        }.run(runInParallel);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}
