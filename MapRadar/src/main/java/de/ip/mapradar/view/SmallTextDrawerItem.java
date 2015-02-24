package de.ip.mapradar.view;
import android.view.*;
import android.widget.*;
import de.ip.mapradar.R;

public class SmallTextDrawerItem extends DrawerItem<TextView> {
    protected final String txt;
    protected int iconDrawable;

    public SmallTextDrawerItem(String text) {
        this.txt = text;
        this.iconDrawable = 0;
    }

    public SmallTextDrawerItem(String text, int iconDrawable) {
        this.txt = text;
        this.iconDrawable = iconDrawable;
    }

    public int getIconDrawable() {
        return iconDrawable;
    }

    public SmallTextDrawerItem setIconDrawable(int iconDrawable) {
        this.iconDrawable = iconDrawable;
        return this;
    }

    public String getTxt() {
        return txt;
    }

    @Override
    public View drawView(View source, LayoutInflater inflater) {
        View ret = inflater.inflate(R.layout.smalltext_list_item, null);
        TextView t = (TextView) ret.findViewById(R.id.list_item_text_view);
        t.setText(txt);
        t.setTextColor(mContext.getResources().getColorStateList(R.color.nav_drawer_text_color));
        ImageView icon = (ImageView) ret.findViewById(R.id.plaintext_item_icon);
        if (iconDrawable == 0) {
            icon.setVisibility(View.GONE);
        } else {
            icon.setVisibility(View.VISIBLE);
            icon.setImageDrawable(mContext.getResources().getDrawable(iconDrawable));
        }
        return ret;
    }
}
