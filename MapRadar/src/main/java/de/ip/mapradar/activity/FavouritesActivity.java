package de.ip.mapradar.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.View;
import de.ip.mapradar.R;
import de.ip.mapradar.main.MapApplication;
import de.ip.mapradar.model.Business;

import java.util.ArrayList;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class FavouritesActivity extends BaseActivity implements FavsAdapter.OnItemClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favs_recycler);
        overridePendingTransition(0, 0);
        final Toolbar toolbar = getActionBarToolbar();

        toolbar.setTitle("Favoriten");


        RecyclerView recList = (RecyclerView) findViewById(R.id.favslist_recyclerview);
        recList.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(this,getResources().getInteger(R.integer.favs_grid_col_count),GridLayoutManager.VERTICAL,false);
        recList.setLayoutManager(llm);
        ArrayList<Business> favourites = new ArrayList<>(0);
        try {
            favourites = MapApplication.getInstance().prefs.getFavourites();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FavsAdapter ca = new FavsAdapter(favourites,this);
        recList.setAdapter(ca);
        enableActionBarAutoHide(recList);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_FAVORITES;
    }

    @Override
    public void onItemClicked(Business busi, View v, int pos) {
        if(busi == null){
            return;
        }

        Intent showDetail = new Intent(FavouritesActivity.this, DetailBusinessActivity.class);
        showDetail.putExtra(CategoryActivity.EXTRA_BUSINESS_COUNT, 1);
        showDetail.putExtra(CategoryActivity.EXTRA_BUSINESS_PREFIX + "0", busi.makeParcel());
        showDetail.putExtra(DetailBusinessActivity.EXTRA_SELECT_FIRST, true);
        startActivity(showDetail);

    }
}
