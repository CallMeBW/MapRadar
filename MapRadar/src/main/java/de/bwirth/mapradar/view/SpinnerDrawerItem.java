package de.bwirth.mapradar.view;
import android.content.Context;
import android.view.*;
import android.widget.*;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 10.11.2014            <br></code>
 * Description:                    <br>
 */
public class SpinnerDrawerItem extends DrawerItem<Spinner> {
    private final SpinnerAdapter adapter;

    public SpinnerDrawerItem(BaseAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Spinner drawView(View source, LayoutInflater inflater) {
        return mView;
    }

    @Override
    public void onPrepare(Context context) {
        super.onPrepare(context);
        mView = new Spinner(context);
        mView.setAdapter(adapter);
    }
}
