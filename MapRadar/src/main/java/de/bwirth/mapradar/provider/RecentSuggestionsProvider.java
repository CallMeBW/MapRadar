package de.bwirth.mapradar.provider;
import android.content.SearchRecentSuggestionsProvider;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 19.02.2015            <br></code>
 * Description:                    <br>
 */
public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = RecentSuggestionsProvider.class.getName();
    public static final int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, DATABASE_MODE_QUERIES);
    }

}
