package com.widget.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.SearchRecentSuggestions;
import android.widget.SearchView.OnSuggestionListener;

import com.util.SearchSuggestionsProvider;

@SuppressLint("NewApi")
public class SearchViewOnSuggestionListener implements OnSuggestionListener {
	int suggestionsCount = 0;
	SearchRecentSuggestions mSuggestions;
	Context mContext;

	public SearchViewOnSuggestionListener(Context mContext,
			SearchRecentSuggestions mSuggestions, int suggestionsCount) {
		this.mContext = mContext;
		this.mSuggestions = mSuggestions;
		this.suggestionsCount = suggestionsCount;
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		if (suggestionsCount == position) {
			if (mSuggestions == null)
				mSuggestions = new SearchRecentSuggestions(mContext,
						SearchSuggestionsProvider.AUTHORITY,
						SearchSuggestionsProvider.MODE);
			mSuggestions.clearHistory();
			return true;
		}
		return false;
	}
}