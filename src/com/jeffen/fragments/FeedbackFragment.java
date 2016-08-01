package com.jeffen.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffen.note.R;

public class FeedbackFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.help_tab_feedback, container, false);
		TextView textview = (TextView) v.findViewById(R.id.feedbackText);
		textview.setText("This is the feedback tab");
		return v;
	}

}
