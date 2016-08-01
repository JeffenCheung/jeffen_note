package com.jeffen.note;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class BookMarkActivity extends Activity {
	private static final int ACTIVITY_NOTE_LIST = 0;
	public NoteDbAdapter mDbHelper;
	public Cursor mCursor;
	public EditText mBookmark;
	private Long mNoteId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_bookmark_edit);

		// home button to return
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mBookmark = (EditText) this.findViewById(R.id.bookmarkET);
		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();

		mNoteId = null;
		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// 数据库查询
			mNoteId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			mCursor = mDbHelper.getBookMark(mNoteId);
			if (mCursor != null && mCursor.moveToFirst()) {
				mBookmark.setText(mCursor.getString(mCursor
						.getColumnIndexOrThrow(mDbHelper.BT_BOOKMARK)));
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// create action bar menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookmark, menu);
		return true;
	}

	/**
	 * action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bookmark_menu_save:
			saveBookMark();
			return true;
		case android.R.id.home:
			gotoNoteList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * 保存处理
	 */
	public void saveBookMark() {
		mDbHelper.updateBookMark(mNoteId, mBookmark.getText().toString());
		// 保存成功
		Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
		gotoNoteList();
	}

	/**
	 * 跳转到主页面
	 */
	public void gotoNoteList() {
		Intent i = new Intent(this, NoteListActivity.class);
		startActivityForResult(i, ACTIVITY_NOTE_LIST);
	}

}
