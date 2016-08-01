package com.widget.item;

import android.view.View;
import android.view.View.OnClickListener;

import com.jeffen.pojo.Note;

public class ItemOnClickListener implements OnClickListener {
	private Note note;

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public ItemOnClickListener(Note note) {
		this.note = note;
	}

	@Override
	public void onClick(View v) {

	}

}