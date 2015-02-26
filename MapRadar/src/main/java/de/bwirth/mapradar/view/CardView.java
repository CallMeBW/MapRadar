package de.bwirth.mapradar.view;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.*;
import android.os.AsyncTask;
import android.view.View;
import android.widget.*;
import de.ip.mapradar.R;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.model.Business;

import java.io.InputStream;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class CardView extends FrameLayout {
    private ImageView img;
    private TextView title;
    private TextView subtitle;
    private RatingBar ratingBar;

    public CardView(final Activity c, final Business model) {
        super(c);
        View v = c.getLayoutInflater().inflate(R.layout.card_loc_small, this);
        this.img = (ImageView) v.findViewById(R.id.card_img);
        this.title = (TextView) v.findViewById(R.id.card_title);
        this.subtitle = (TextView) v.findViewById(R.id.card_subtitle);
        ratingBar = (RatingBar) v.findViewById(R.id.card_ratingbar);
        this.title.setText(model.name);
        if (model.subCategory == null) {
            this.subtitle.setText(model.category);
        } else {
            this.subtitle.setText(model.subCategory);
        }
        this.ratingBar.setNumStars(5);
        this.ratingBar.setRating((float) model.RATING);
        if (ratingBar.getRating() == 0.0f) {
            ratingBar.setVisibility(INVISIBLE);
        }
        final String imgUrl = String.valueOf(model.imageURL);
        final Resources res = c.getResources();
        new AndroidUtil.VoidAsyncTask() {
            private Bitmap bitMapImg;

            @Override
            protected void doInBackground() {
                try {
                    InputStream in = new java.net.URL(imgUrl).openStream();
                    bitMapImg = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                }
            }

            @Override
            protected void onPostExecute() {
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


