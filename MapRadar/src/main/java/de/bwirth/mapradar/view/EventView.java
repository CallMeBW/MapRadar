package de.bwirth.mapradar.view;
import android.app.Activity;
import android.view.View;
import android.widget.*;
import de.bwirth.mapradar.model.Event;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class EventView extends FrameLayout {
    private TextView title, subtitle, date, price;
    private ImageView img;

    public EventView(final Activity c, final Event event) {
        super(c);
        View v = c.getLayoutInflater().inflate(R.layout.card_evnt, this);
        this.img = (ImageView) v.findViewById(R.id.card_img_event);
        this.title = (TextView) v.findViewById(R.id.card_title_event);
        this.subtitle = (TextView) v.findViewById(R.id.card_event_place);
        this.date = (TextView) v.findViewById(R.id.card_event_date);
        this.price = (TextView) v.findViewById(R.id.card_event_price);
        this.price.setText(event.getPrice());
        this.img.setImageResource(event.getImgResID());
        this.title.setText(event.getTitle());
        this.subtitle.setText(event.getPlace());
        this.date.setText(event.getDate());
    }

    public TextView getDate() {
        return date;
    }

    public ImageView getImg() {
        return img;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public void setSubtitle(TextView subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getSubtitle() {
        return subtitle;
    }

    public TextView getTitle() {
        return title;
    }
}


