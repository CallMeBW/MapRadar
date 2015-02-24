package de.ip.mapradar.activity;
import android.content.*;
import android.os.*;
import android.text.*;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import de.ip.mapradar.R;
import de.ip.mapradar.apputil.YelpQueryHelper;
import de.ip.mapradar.main.MapApplication;
import de.ip.mapradar.model.*;

import java.util.*;

/**
 * <code>
 * Project: MapRadar <br>
 * Date: 07.12.2014            <br></code>
 * Description:                    <br>
 */
public class SearchActivity extends BaseActivity {
    YelpQueryHelper helper;
    private ListView listView;
    private ArrayList<Business> searchResults;
    private EditText editText;
    private AsyncTask<Void, Void, Void> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
        overridePendingTransition(0,0);
        final android.support.v7.widget.Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("In Umgebung finden");

        editText = (EditText) findViewById(R.id.et_location);
        final Button butonFind = (Button) findViewById(R.id.btn_find);
        final ImageView butonDelete = (ImageView) findViewById(R.id.search_edit_delete_button);

        listView = (ListView) findViewById(R.id.listviewmap);
        helper = new YelpQueryHelper(MapApplication.getInstance().getApi());

        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                autocomplete(s.toString());
                butonDelete.setVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
                if (s.toString().isEmpty()) {
                    listView.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, new String[]{"Suchen Sie nach " +
                            "Restaurants, Kinos oder Banken in Ihrer Umgebung."}));
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });
        butonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                if (task != null) {
                    task.cancel(true);
                }
                autocomplete(editText.getText().toString());
            }
        });
        butonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (searchResults.size() <= position) {
                    return;
                }
                Intent showMap = new Intent(SearchActivity.this, DetailBusinessActivity.class)
                        .putExtra("BusiCount", 1)
                        .putExtra("busi0", searchResults.get(position).makeParcel())
                        .putExtra(DetailBusinessActivity.EXTRA_SELECT_FIRST, true);
                startActivity(showMap);
            }
        });
        searchResults = new ArrayList<>();
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager
                        .HIDE_IMPLICIT_ONLY);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Suchen Sie nach Restaurants," +
                " Kinos oder Banken in Ihrer Umgebung."}));
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        hideKeyBoard();
        super.onDestroy();
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SEARCH;
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
                searchResults.addAll(Arrays.asList(helper.searchBusiness(search, SearchActivity.this)));
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
                adapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, result);
                listView.setAdapter(adapter);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
