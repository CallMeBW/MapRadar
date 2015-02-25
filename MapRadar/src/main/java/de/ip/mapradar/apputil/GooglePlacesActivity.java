package de.ip.mapradar.apputil;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.*;
import de.ip.mapradar.R;
import de.ip.mapradar.activity.*;
import de.ip.mapradar.androidutil.AndroidUtil;
import de.ip.mapradar.model.Business;

import java.util.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 24.02.2015            <br></code>
 * Description:                    <br>
 */
public class GooglePlacesActivity extends BaseActivity implements FavsAdapter.OnItemClickListener {
    private Business[] businesses;

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SEARCH;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favs_recycler);
        overridePendingTransition(0, 0);
        final Toolbar toolbar = getActionBarToolbar();

        toolbar.setTitle("Google Places API");


        final RecyclerView recList = (RecyclerView) findViewById(R.id.favslist_recyclerview);
        recList.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(this,getResources().getInteger(R.integer.favs_grid_col_count),GridLayoutManager.VERTICAL,false);
        recList.setLayoutManager(llm);
        recList.setItemViewCacheSize(20);

        new AndroidUtil.VoidAsyncTask(){

            @Override
            protected void doInBackground() {
                businesses = GoogleQueryHelper.searchBusiness(null,null);
            }

            @Override
            protected void onPostExecute() {
                FavsAdapter ca = new FavsAdapter(new ArrayList<Business>(Arrays.asList(businesses)),GooglePlacesActivity.this);
                recList.setAdapter(ca);
            }
        }.run(true);
        enableActionBarAutoHide(recList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClicked(Business busi, View v, int pos) {

    }
}
