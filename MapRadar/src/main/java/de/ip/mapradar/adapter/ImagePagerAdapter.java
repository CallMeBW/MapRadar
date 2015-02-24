package de.ip.mapradar.adapter;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.*;
import android.widget.*;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 14.01.2015            <br></code>
 * Description:                    <br>
 */
public class ImagePagerAdapter extends PagerAdapter {
    private final int[] titleArray;
    int[] imageArray;
    private final int[] descriptionArray;

    public ImagePagerAdapter(int[] titleArray, int[] imgArray, int[] descriptionArray) {
        this.titleArray = titleArray;
        imageArray = imgArray;
        this.descriptionArray = descriptionArray;
    }

    public int getCount() {
        return titleArray.length + 1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position == imageArray.length) {
            return container; // last item is empty
        }
        LayoutInflater inflater = (LayoutInflater) container.getContext
                ().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.tutorial_viewpager_item, null);

        ImageView image = (ImageView) layout.findViewById(R.id.tutorial_viewpager_image);
        TextView title = (TextView) layout.findViewById(R.id.tutorial_viewpager_title);
        TextView description = (TextView) layout.findViewById(R.id.tutorial_viewpager_description);

        image.setImageResource(imageArray[position]);
        title.setText(titleArray[position]);
        description.setText(descriptionArray[position]);

        container.addView(layout, 0);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}