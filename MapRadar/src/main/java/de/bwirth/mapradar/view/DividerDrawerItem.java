package de.bwirth.mapradar.view;
import android.view.*;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 13.01.2015            <br></code>
 * Description:                    <br>
 */
public class DividerDrawerItem extends DrawerItem {
    @Override
    public View drawView(View source, LayoutInflater inflater) {
        return inflater.inflate(R.layout.divider_drawer_item, null);
    }
}
