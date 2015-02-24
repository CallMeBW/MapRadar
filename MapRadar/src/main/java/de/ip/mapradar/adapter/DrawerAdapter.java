package de.ip.mapradar.adapter;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import de.ip.mapradar.view.DrawerItem;

import java.util.List;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
    LayoutInflater inflater;
    List<DrawerItem> items;
    private Context context;

    public DrawerAdapter(Context context, List<DrawerItem> listItems) {
        super(context, 0, listItems);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = listItems;
        for (DrawerItem item : listItems) {
            item.onPrepare(context);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItem item = items.get(position);
        return item.drawView(convertView, inflater);
    }
}