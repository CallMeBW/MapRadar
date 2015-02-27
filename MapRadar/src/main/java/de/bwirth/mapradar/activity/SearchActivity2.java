package de.bwirth.mapradar.activity;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.*;
import de.bwirth.mapradar.provider.*;
import de.ip.mapradar.R;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class SearchActivity2 extends BaseActivity implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener, View.OnClickListener {
    private SuggestionsDatabase database;
    private android.support.v7.widget.SearchView mSearchView;
    private boolean mFirstRunActivity = true;
    private SuggestionAdapter mSuggestionsAdapter;

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
            mSuggestionsAdapter = new SuggestionAdapter(this, mSearchView, database);
            mSearchView.setSuggestionsAdapter(mSuggestionsAdapter);
        }
        mSuggestionsAdapter.invalidate();
        return true;
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
       if (mSuggestionsAdapter != null) {
           mSuggestionsAdapter.invalidate();
       }
        return true;
    }

    @Override
    public void onClick(View v) {
        mSearchView.setQuery(getSupportActionBar().getTitle(), false);
    }
}
