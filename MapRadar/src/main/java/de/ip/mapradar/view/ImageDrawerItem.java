package de.ip.mapradar.view;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.ImageView;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 13.01.2015            <br></code>
 * Description:                    <br>
 */
public class ImageDrawerItem extends DrawerItem {
    private final Drawable imgRes;

    public ImageDrawerItem(Drawable imgRes) {

        this.imgRes = imgRes;
    }

    @Override
    public View drawView(View source, LayoutInflater inflater) {
        View retView = inflater.inflate(R.layout.image_drawer_item, null);
        ImageView imgView = (ImageView) retView.findViewById(R.id.image_drawer_item_imgview);
        imgView.setImageDrawable(imgRes);
        return retView;
    }
}
