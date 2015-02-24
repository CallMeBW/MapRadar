package de.ip.mapradar.provider;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 19.02.2015            <br></code>
 * Description:                    <br>
 */
public class SuggestionSimpleCursorAdapter
        extends android.support.v4.widget.SimpleCursorAdapter {
    private final android.support.v7.widget.SearchView mSearchView;

    public SuggestionSimpleCursorAdapter(Context context, android.support.v7.widget.SearchView searchView, Cursor c,
                                         String[] from, int[] to, int flags) {
        super(context, R.layout.suggestion_item, c, from, to, flags);
        this.mSearchView = searchView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);
        final String suggestion = cursor.getString(indexColumnSuggestion);
        ((TextView) view.findViewById(R.id.searchterm)).setText(suggestion);
        ImageView imgv = (ImageView) view.findViewById(R.id.imageView88);
        imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // write suggestion into searchview but do not submit
                mSearchView.setQuery(suggestion, false);
            }
        });
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);
        return cursor.getString(indexColumnSuggestion);
    }
}
