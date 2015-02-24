package de.ip.mapradar.activity;
import android.graphics.*;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import de.ip.mapradar.R;
import de.ip.mapradar.model.Business;

import java.io.InputStream;
import java.util.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 23.02.2015            <br></code>
 * Description:                    <br>
 */
public class FavsAdapter extends RecyclerView.Adapter<FavsAdapter.ViewHolder> {
    private List<Business> data;
    private final OnItemClickListener onItemClickListener;

    public FavsAdapter(List<Business> contactList, OnItemClickListener onItemClickListener) {
        this.data = contactList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, int i) {
        Business model = data.get(i);
        vh.vTitle.setText(model.name);
        vh.vCategory.setText(model.subCategory);
        vh.vRatingBar.setNumStars(5);
        int random = new Random().nextInt(10);
        vh.vMinutes.setText(random + " min");
        vh.vRatingBar.setRating((float) model.RATING);
        if (vh.vRatingBar.getRating() == 0.0f) {
            vh.vRatingBar.setVisibility(View.INVISIBLE);
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
                    vh.vImg.setImageBitmap(bitMapImg);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup,final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_loc_detail, viewGroup, false);
        if(onItemClickListener != null){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClicked(data.get(i),itemView,i);
                }
            });
        }
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected RatingBar vRatingBar;
        protected ImageView vImg;
        protected TextView vMinutes, vCategory, vTitle;

        public ViewHolder(View v) {
            super(v);
            vRatingBar = (RatingBar) v.findViewById(R.id.card_loc_detail_ratingbar);
            vImg = (ImageView) v.findViewById(R.id.card_location_detail_title_img);
            vTitle = (TextView) v.findViewById(R.id.card_location_detail_title);
            vCategory = (TextView) v.findViewById(R.id.card_loc_detail_category);
            vMinutes = (TextView) v.findViewById(R.id.card_location_detail_minutes);
        }
    }

    public static interface OnItemClickListener {
        void onItemClicked(Business busi, View v, int pos);
    }
}