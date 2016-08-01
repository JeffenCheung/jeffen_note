package com.util;

import android.content.SearchRecentSuggestionsProvider;

/**
 * The saveRecentQuery() method takes the search query string as the first
 * parameter and, optionally, a second string to include as the second line of
 * the suggestion (or null). The second parameter is only used if you've enabled
 * two-line mode for the search suggestions with DATABASE_MODE_2LINES. If you
 * have enabled two-line mode, then the query text is also matched against this
 * second line when the system looks for matching suggestions. </p>
 * 
 * The call to setupSuggestions() passes the name of the search authority and a
 * database mode. The search authority can be any unique string, but the best
 * practice is to use a fully qualified name for your content provider (package
 * name followed by the provider's class name; for example,
 * "com.example.MySuggestionProvider"). </p>
 * 
 * SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
 * MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
 * suggestions.saveRecentQuery(query, null); </p>
 * 
 * @author Jeffen
 * 
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "com.util.SearchSuggestionsProvider";

	public final static int MODE2 = DATABASE_MODE_QUERIES
			| DATABASE_MODE_2LINES;
	public final static int MODE = DATABASE_MODE_QUERIES;

	public SearchSuggestionsProvider() {
		super();
		setupSuggestions(AUTHORITY, MODE);
	}

	public SearchSuggestionsProvider(boolean line2) {
		super();
		// The database mode must include DATABASE_MODE_QUERIES and can
		// optionally
		// include DATABASE_MODE_2LINES, which adds another column to the
		// suggestions table that allows you to provide a second line of text
		// with
		// each suggestion. For example, if you want to provide two lines in
		// each
		// suggestion:
		setupSuggestions(AUTHORITY, MODE2);
	}
}