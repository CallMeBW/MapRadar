package de.bwirth.mapradar.provider;
import android.content.Context;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import de.bwirth.mapradar.androidutil.AndroidUtil;
import de.bwirth.mapradar.apputil.GoogleQueryHelper;
import de.ip.mapradar.R;
import org.gmarz.googleplaces.models.*;

import java.util.*;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 19.02.2015            <br></code>
 * Description:                    <br>
 */
public class SuggestionAdapter  extends android.support.v4.widget.CursorAdapter {
    private final Context mContext;
    private final android.support.v7.widget.SearchView mSearchView;
    private final SuggestionsDatabase mDatabase;
    private Cursor mRecentQueriesCursor;
    private List<AutocompletePrediction> mGooglePredictions;
    private AndroidUtil.VoidAsyncTask mCurrentTask;

    public SuggestionAdapter(Context context, android.support.v7.widget.SearchView searchView, SuggestionsDatabase db) {
        super(context,db.getSuggestions("",0),0);
        mContext = context;
        this.mSearchView = searchView;
        this.mDatabase = db;
        mGooglePredictions = new ArrayList<>(0);
    }

    public void invalidate() {
        final String newQuery = mSearchView.getQuery().toString();
        mRecentQueriesCursor = mDatabase.getSuggestions(newQuery, 5);
        mGooglePredictions.clear();
        notifyDataSetChanged();
        // fetch google suggestions
        if(mCurrentTask != null){
            mCurrentTask.cancel(true);
        }
        mCurrentTask = new AndroidUtil.VoidAsyncTask() {
            @Override
            protected void doInBackground() {
                AutoCompleteResult result = GoogleQueryHelper.getAutocompletePredictions(newQuery, 0);
                mGooglePredictions = result.getPredictions();
            }

            @Override
            protected void onPostExecute() {
                notifyDataSetChanged();
            }
        };
        mCurrentTask.run(true);
    }

    @Override
    public int getCount() {
        int count = mGooglePredictions.size();
        if (mRecentQueriesCursor != null) {
            return count + mRecentQueriesCursor.getCount();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.suggestion_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewItem = (TextView) v.findViewById(R.id.searchterm);
            viewHolder.arrow = (ImageView) v.findViewById(R.id.imageView88);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        final String suggestion;
        if (position < mRecentQueriesCursor.getCount()) {
            mRecentQueriesCursor.moveToPosition(position);
            int indexColumnSuggestion = mRecentQueriesCursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);
            suggestion = mRecentQueriesCursor.getString(indexColumnSuggestion);
        } else {
            suggestion = mGooglePredictions.get(position - mRecentQueriesCursor.getCount()).description;
        }
        viewHolder.textViewItem.setText(suggestion);
        viewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // write suggestion into searchview but do not submit
                mSearchView.setQuery(suggestion, false);
            }
        });
        return v;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    private static class ViewHolder {
        TextView textViewItem;
        ImageView arrow;
    }
}
