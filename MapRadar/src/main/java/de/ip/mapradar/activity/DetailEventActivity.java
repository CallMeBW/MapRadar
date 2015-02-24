package de.ip.mapradar.activity;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.Toast;
import de.ip.mapradar.R;

public class DetailEventActivity extends ActionBarActivity {
    public static final String EXTRA_EVENT = "extra_event";
    public static final String EXTRA_SELECT_FIRST = "extra_sel_first";
    public static final String EXTRA_TITLE = "extra_title";
    private String defaultTitle;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detail_event);
        final View toolBar = findViewById(R.id.act_detail_evevnt_toolbar);
        setSupportActionBar((android.support.v7.widget.Toolbar) toolBar);
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.FROYO) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailmenu_event, menu);
        final MenuItem menuFav = menu.findItem(R.id.mnu_favourite_event);
        isFavorite = false;
//            for (Business busi : MapApplication.getInstance().prefs.getFavourites()) {
//                if (curSelPlace != null && busi.id.equals(curSelPlace.id)) {
//                    isFavorite = true;
//                    break;
//                }
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
            case R.id.mnu_favourite:
                Toast.makeText(this, "Favorisieren", Toast.LENGTH_LONG).show();

                break;
        }
        return true;
    }

    public void onClickPhone(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        startActivity(callIntent);
    }

    public void onClickURL(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        startActivity(i);
    }

    public void onClickCalendarEntry(View view) {
        Toast.makeText(this, "In Kalender eintragen", Toast.LENGTH_LONG).show();
    }

    public void onClickShare(MenuItem item) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sehen Sie sich diese Veranstaltung an:\n"); //+ curSelPlace.name + "\n" + curSelPlace.yelpURL);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void onClickBuy(View view) {
        Toast.makeText(this, "Tickets kaufen", Toast.LENGTH_LONG).show();
    }

    public void onClickNavigation(View view) {
        Toast.makeText(this, "Navigieren", Toast.LENGTH_LONG).show();
    }
}