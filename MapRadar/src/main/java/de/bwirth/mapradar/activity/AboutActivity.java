package de.bwirth.mapradar.activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 14.01.2015            <br></code>
 * Description:                    <br>
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frg_about);
        final View toolBar = getActionBarToolbar();
        setSupportActionBar((android.support.v7.widget.Toolbar) toolBar);
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle("Über");
    }

    public void openYelp(View view) {
        String url = "http://www.yelp.de/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void onClickEmail(View view) {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("support@mapradar.com") +
                "?subject=" + Uri.encode("Supportanfrage v1.01") +
                "&body=" + Uri.encode("[Ihre Anfrage hier]");
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Email senden per..."));
    }
}
