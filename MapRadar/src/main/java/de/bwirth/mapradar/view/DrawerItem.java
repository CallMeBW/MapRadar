package de.bwirth.mapradar.view;
import android.content.Context;
import android.view.*;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 10.11.2014            <br></code>
 * Description:                    <br>
 */
public abstract class DrawerItem<T extends View> {
    protected T mView;
    protected Context mContext;

    public Context getmContext() {
        return mContext;
    }

    /**
     * When called, inflate a view and return it.
     *
     * @param source
     * @param inflater
     */
    public abstract View drawView(View source, LayoutInflater inflater);

    /**
     * onPrepare is called once when the DrawerAdapter is created.
     * Please call super.onPrepare() as first statement.
     *
     * @param context
     */
    public void onPrepare(Context context) {
        this.mContext = context;
    }
}
