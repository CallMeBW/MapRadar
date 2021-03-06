package de.bwirth.mapradar.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import de.bwirth.mapradar.main.MapApplication;
import de.bwirth.mapradar.model.Event;
import de.bwirth.mapradar.view.EventView;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class EventsActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.act_events);
        setTitle(R.string.title_events);
        ViewGroup container = (ViewGroup) findViewById(R.id.frg_events_container);
        for (final Event ev : MapApplication.getInstance().sampleEvents) {
            EventView eventView = new EventView(this, ev);
            container.addView(eventView);
            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EventsActivity.this, DetailEventActivity.class);
                    intent.putExtra(DetailEventActivity.EXTRA_EVENT, MapApplication.getInstance().sampleEvents[0]);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_EVENTS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        return true;
    }

}