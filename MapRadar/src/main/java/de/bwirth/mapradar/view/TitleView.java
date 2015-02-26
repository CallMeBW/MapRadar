package de.bwirth.mapradar.view;
import android.app.Activity;
import android.view.View;
import android.widget.*;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class TitleView extends RelativeLayout {
    private TextView titleView;
    private Button moreButton;
    private ImageButton mapButton;

    public Button getMoreButton() {
        return moreButton;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public TitleView(Activity c, String title, String moreButtontext, int accent) {
        super(c);
        View v = c.getLayoutInflater().inflate(R.layout.layout_title_header, this);
        titleView = (TextView) v.findViewById(R.id.title_header_titleview);
        moreButton = (Button) v.findViewById(R.id.title_header_more_btn);
        moreButton.setTextColor(accent);
        mapButton = (ImageButton) v.findViewById(R.id.title_header_map_button);
        titleView.setText(title);
        titleView.setTextColor(accent);
        moreButton.setText(moreButtontext);
        mapButton.setColorFilter(accent);
    }

    public TitleView(Activity c, String title, int accent) {
        this(c, title, "Mehr", accent);
    }

    public ImageButton getMapButton() {
        return mapButton;
    }
}
