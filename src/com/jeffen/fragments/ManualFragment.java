package com.jeffen.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jeffen.note.R;
import com.widget.ItemListAdapter;

public class ManualFragment extends Fragment {
	private ListView list1;
	private ListView list2;
	private ListView list3;

	String array1[] = { "This is the manual tab" };
	String array2[] = {
			"long click the item of list view then pop up the item menu.",
			"long click the star button shall be excited!" };
	String array3[] = { "..." };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.help_tab_manual, container, false);

		list1 = (ListView) v.findViewById(R.id.list1);
		list2 = (ListView) v.findViewById(R.id.list2);
		list3 = (ListView) v.findViewById(R.id.list3);
		list1.setAdapter(new ItemListAdapter(inflater, this.getResources(),
				array1));
		list2.setAdapter(new ItemListAdapter(inflater, this.getResources(),
				array2));
		list3.setAdapter(new ItemListAdapter(inflater, this.getResources(),
				array3));

		return v;
	}

}
