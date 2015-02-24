package de.ip.mapradar.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import de.ip.mapradar.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class MultipleCardView extends LinearLayout {
    public MultipleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultipleCardView(final Activity c, CardView... cards) {
        super(c);
        LinearLayout container = (LinearLayout) c.getLayoutInflater().inflate(R.layout.card_holder, this).findViewById(R.id
                .card_holder_linear_layout_container);
//        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        for (CardView card : cards) {
            container.addView(card);
        }
    }
}
