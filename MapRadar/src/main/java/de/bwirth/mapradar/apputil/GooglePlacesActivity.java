package de.bwirth.mapradar.apputil;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.*;
import com.google.android.gms.maps.model.LatLng;
import de.bwirth.mapradar.activity.*;
import de.ip.mapradar.R;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.model.Business;

import java.io.Serializable;
import java.util.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 24.02.2015            <br></code>
 * Description:                    <br>
 */
public class GooglePlacesActivity extends BaseActivity implements BusinessDetailCardRecyclerAdapter.OnItemClickListener {
    private Business[] businesses;
    private final String EXTRA_BUSINESSES = "EXTRA_BUSI";

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SEARCH;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favs_recycler);
        overridePendingTransition(0, 0);
        setTitle("Google Places API");

        final RecyclerView recList = (RecyclerView) findViewById(R.id.favslist_recyclerview);
        recList.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(this, getResources().getInteger(R.integer.favs_grid_col_count), GridLayoutManager.VERTICAL,
                false);
        recList.setLayoutManager(llm);
        recList.setItemViewCacheSize(20);
        restoreInstance(savedInstanceState, recList);
    }

    private void fetchPlaces(final RecyclerView recList) {
        new AndroidUtil.VoidAsyncTask() {
            @Override
            protected void doInBackground() {
                businesses = GoogleQueryHelper.searchNearby(new LatLng(49,9),50000,"food|cafe");
            }

            @Override
            protected void onPostExecute() {
                BusinessDetailCardRecyclerAdapter ca = new BusinessDetailCardRecyclerAdapter(new ArrayList<>(Arrays.asList(businesses)), GooglePlacesActivity.this);
                recList.setAdapter(ca);
            }
        }.run(true);
    }

    private void restoreInstance(Bundle savedInstanceState, RecyclerView recyclerView) {
        if (savedInstanceState == null) {
            fetchPlaces(recyclerView);
            return;
        }
        Serializable busiSer = savedInstanceState.getSerializable(EXTRA_BUSINESSES);
        if (busiSer == null) {
            fetchPlaces(recyclerView);
            return;
        }
        Object[] objs = (Object[]) busiSer;
        businesses = new Business[objs.length];
        for (int i = 0; i < objs.length; i++) {
            businesses[i] = (Business) objs[i];
        }
        BusinessDetailCardRecyclerAdapter ca = new BusinessDetailCardRecyclerAdapter(new ArrayList<>(Arrays.asList(businesses)), GooglePlacesActivity.this);
        recyclerView.setAdapter(ca);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (businesses != null) {
            outState.putSerializable(EXTRA_BUSINESSES, businesses);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClicked(Business busi, View v, int pos) {

    }
}
