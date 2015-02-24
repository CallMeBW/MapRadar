package de.ip.mapradar.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;
import de.ip.mapradar.R;
import de.ip.mapradar.androidutil.AndroidUtil;
import de.ip.mapradar.main.MapApplication;

import java.util.*;

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

    private MaterialDialog createCategoryDialog() {
        final MapApplication application = MapApplication.getInstance();
        Iterator it = MapApplication.getInstance().getMainCategories().entrySet().iterator();
        ArrayList<String> cats = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            final String title = (String) pairs.getKey();
            cats.add(title);
        }
        final Integer[] selectedIndizes = application.prefs.getSelectedIndizes();
        return new MaterialDialog.Builder(this)
                .items(cats.toArray(new String[cats.size()]))
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .title(R.string.choose_categories)
                .itemsCallbackMultiChoice(selectedIndizes, new MaterialDialog.ListCallbackMulti() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        application.prefs.setSelectedIndizes(integers);
                    }
                })
                .build();
    }

    public void chooseCats(View view) {
        createCategoryDialog().show();
    }
}
