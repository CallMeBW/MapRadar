package de.bwirth.mapradar.activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 02.03.2015            <br></code>
 * Description:                    <br>
 */
public class ObservableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;
    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

    }
}


