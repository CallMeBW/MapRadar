package de.ip.mapradar.activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.*;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.widget.*;
import de.ip.mapradar.R;
import de.ip.mapradar.apputil.YelpQueryHelper;
import de.ip.mapradar.main.MapApplication;
import de.ip.mapradar.model.*;
import de.ip.mapradar.provider.*;

import java.util.*;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class SearchActivity2 extends BaseActivity implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, View.OnClickListener {
    YelpQueryHelper helper;
    private AsyncTask<Void, Void, Void> task;
    private SuggestionsDatabase database;
    private android.support.v7.widget.SearchView mSearchView;
    private boolean mFirstRunActivity = true;
    private ListView listView;
    private ArrayList<Business> searchResults;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search2);
        overridePendingTransition(0, 0);
        final android.support.v7.widget.Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        database = new SuggestionsDatabase(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = null;
        if (searchItem != null) {
            mSearchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSuggestionListener(this);
//            mSearchView.setOnCloseListener(this);
            mSearchView.setOnSearchClickListener(this);
            mSearchView.setSubmitButtonEnabled(true);
            mSearchView.setIconified(!mFirstRunActivity);
        }
        getSuggestions("",5);
        return true;
    }

    private void autocomplete(final String input) {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
        if (input.isEmpty()) {
            searchResults.clear();
            listView.setAdapter(null);
            return;
        }
        task = new AsyncTask<Void, Void, Void>() {
            private List<String> result;

            @Override
            protected Void doInBackground(Void... params) {
                // create a search
                YelpSearch search = new YelpSearch(MapApplication.getInstance().getLastKnownLocality())
                        .radius(5000)
                        .sorting(YelpSearch.Sorting.BEST_MATCH)
                        .maxSearchResults(10)
                        .searchTerm(input);
                searchResults.clear();
                searchResults.addAll(Arrays.asList(helper.searchBusiness(search, SearchActivity2.this)));
                result = new ArrayList<>(searchResults.size());
                for (Business business : searchResults) {
                    result.add(String.valueOf(business.name));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ArrayAdapter<String> adapter;
                if (result.isEmpty()) {
                    result.add("Keine Suchergebnisse gefunden.");
                }
                adapter = new ArrayAdapter<>(SearchActivity2.this, android.R.layout.simple_list_item_1, result);
                listView.setAdapter(adapter);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onSuggestionSelect(int i) {
        collapseSearchView();
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        SQLiteCursor cursor = (SQLiteCursor) mSearchView.getSuggestionsAdapter().getItem(position);
        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);
        mSearchView.setQuery(cursor.getString(indexColumnSuggestion), false);
        collapseSearchView();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        database.insertSuggestion(query);
        collapseSearchView();
        return false;
    }

    private void collapseSearchView() {
        String title = mSearchView.getQuery().toString();
        mSearchView.clearFocus();
        mSearchView.setIconified(true);
        getSupportActionBar().setTitle(title);
        mFirstRunActivity = false;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getSuggestions(newText, 5);
        return true;
    }



    @Override
    public void onClick(View v) {
       mSearchView.setQuery(getSupportActionBar().getTitle(),false);
    }

    private void getSuggestions(String query, int maxResults) {
        Cursor cursor = database.getSuggestions(query, maxResults);
        if (cursor.getCount() != 0) {
            String[] columns = new String[]{SuggestionsDatabase.FIELD_SUGGESTION};
            int[] columnTextId = new int[]{android.R.id.text1};
            SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(), mSearchView,
                    cursor, columns, columnTextId, 0);
            mSearchView.setSuggestionsAdapter(simple);
        }
    }
}
