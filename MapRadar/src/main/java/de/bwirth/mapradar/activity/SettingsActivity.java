package de.bwirth.mapradar.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 14.01.2015            <br></code>
 * Description:                    <br>
 */
public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        Spinner radiusSpinner = (Spinner) findViewById(R.id.settings_radius_spinner);
        Spinner reminderSpinner = (Spinner) findViewById(R.id.settings_reminder_spinner);
        final View toolBar = getActionBarToolbar();
        setSupportActionBar((android.support.v7.widget.Toolbar) toolBar);
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.settings);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        String[] radiusValues = getResources().getStringArray(R.array.radius_values);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, radiusValues);
        radiusSpinner.setAdapter(spinnerAdapter);

        String[] reminderValues = getResources().getStringArray(R.array.reminder_values);
        SpinnerAdapter spinnerAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reminderValues);
        reminderSpinner.setAdapter(spinnerAdapter2);
    }

    public void deletePrivateData(View view) {
        new AndroidUtil.VoidAsyncTask() {
            @Override
            protected void doInBackground() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute() {
                Toast.makeText(SettingsActivity.this, "Daten wurden gelöscht.", Toast.LENGTH_LONG).show();
            }
        }.run(true);
    }

    public void chooseCats(View view) {
        createCategoryChooserDialog().show();
    }
}
