package de.bwirth.mapradar.view;
import android.app.Activity;
import android.graphics.*;
import android.os.AsyncTask;
import android.view.View;
import android.widget.*;
import de.ip.mapradar.*;
import de.bwirth.mapradar.model.Business;

import java.io.InputStream;
import java.util.Random;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class DetailCardView extends FrameLayout {
    private final TextView minuteTV;
    private ImageView img;
    private TextView title;
    private TextView subtitle;
    private RatingBar ratingBar;

    public DetailCardView(final Activity c, final Business model) {
        super(c);
        View v = c.getLayoutInflater().inflate(R.layout.card_loc_detail, this);
        this.img = (ImageView) v.findViewById(R.id.card_location_detail_title_img);
        this.title = (TextView) v.findViewById(R.id.card_location_detail_title);
        this.subtitle = (TextView) v.findViewById(R.id.card_loc_detail_category);
        ratingBar = (RatingBar) v.findViewById(R.id.card_loc_detail_ratingbar);
        minuteTV = (TextView) v.findViewById(R.id.card_location_detail_meters);
        this.title.setText(model.name);
        this.subtitle.setText(model.subCategory);
        this.ratingBar.setNumStars(5);
        int random = new Random().nextInt(10);
        this.minuteTV.setText(random + " min");
        this.ratingBar.setRating((float) model.RATING);
        if (ratingBar.getRating() == 0.0f) {
            ratingBar.setVisibility(INVISIBLE);
        }
        final String imgUrl = String.valueOf(model.imageURL);
        new AsyncTask<Void, Void, Void>() {
            private Bitmap bitMapImg;

            protected Void doInBackground(Void... o) {
                try {
                    InputStream in = new java.net.URL(imgUrl).openStream();
                    bitMapImg = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                if (bitMapImg != null) {
                    img.setImageBitmap(bitMapImg);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(RatingBar ratingBar) {
        this.ratingBar = ratingBar;
    }

    public TextView getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(TextView subtitle) {
        this.subtitle = subtitle;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }
}


