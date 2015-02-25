package de.ip.mapradar.activity;
import android.graphics.*;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import de.ip.mapradar.R;
import de.ip.mapradar.main.MapApplication;
import de.ip.mapradar.model.Business;
import org.gmarz.googleplaces.models.GBusiness;

import java.io.InputStream;
import java.util.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 23.02.2015            <br></code>
 * Description:                    <br>
 */
public class BusinessSmallCardRecyclerAdapter extends RecyclerView.Adapter<BusinessSmallCardRecyclerAdapter.ViewHolder> {
    private List<Business> data;
    private final OnItemClickListener onItemClickListener;

    public BusinessSmallCardRecyclerAdapter(List<Business> contactList, OnItemClickListener onItemClickListener) {
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
        vh.vCategory.setText(model.category);
        vh.vRatingBar.setNumStars(5);
        int random = new Random().nextInt(10);
        vh.vMeters.setText(random + " min");
        Location lastloc = MapApplication.getInstance().getLastKnownLocation();
        if(model instanceof GBusiness && lastloc != null){
            vh.vMeters.setText((int)((GBusiness) model).getDistanceTo(lastloc)+"m");
        }
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
                    Log.w("SmallCardAdapter","Image URL could not be opened");
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
                inflate(R.layout.card_loc_small, viewGroup, false);
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
        protected TextView vMeters, vCategory, vTitle;

        public ViewHolder(View v) {
            super(v);
            vRatingBar = (RatingBar) v.findViewById(R.id.card_ratingbar);
            vImg = (ImageView) v.findViewById(R.id.card_img);
            vTitle = (TextView) v.findViewById(R.id.card_title);
            vCategory = (TextView) v.findViewById(R.id.card_loc_detail_category);
        }
    }

    public static interface OnItemClickListener {
        void onItemClicked(Business busi, View v, int pos);
    }
}