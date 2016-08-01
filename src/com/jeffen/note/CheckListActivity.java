package com.jeffen.note;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.widget.CheckListAdapter;

/**
 * @author Jeffen Cheung
 * 
 */
@SuppressLint("NewApi")
public class CheckListActivity extends ListActivity {
	public static final String TAG = "CheckList";

	protected static final int ACTIVITY_LIST = 100;

	public NoteDbAdapter mDbHelper;
	private Cursor mCheckCursor;
	private int mCheckCount;

	// check list view data
	private CheckListAdapter mCheckListAdapter = null;
	private List<Object> list = new ArrayList<Object>();
	private Long mNoteId;
	private View mView;

	// elements
	private ListView listView;
	private EditText eNoteTitleET;
	private TextView eTotalScoreTV;
	private Button eCheckAllBTN;
	private Button eShiftSortBTN;
	private int mTotalScore = 0;
	private int mCheckScore = 0;
	private int checkedAll = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_list);

		eNoteTitleET = (EditText) findViewById(R.id.noteTitleET);
		eTotalScoreTV = (TextView) findViewById(R.id.total_score);
		eCheckAllBTN = (Button) findViewById(R.id.check_all);
		eShiftSortBTN = (Button) findViewById(R.id.shift_sort);

		// home button to return
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();

		mNoteId = null;
		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// 数据库查询
			mNoteId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			eNoteTitleET.setText(extras.getString(NoteDbAdapter.KEY_TITLE));
		}

		// render list view
		renderCheckListView(true, false);

		// set check all item button
		eCheckAllBTN.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// check on/off all items
				checkAllItems();
				eCheckAllBTN.setSelected(false);
			}
		});
		eCheckAllBTN.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				renderCheckListView(true, true);
				return false;
			}
		});
		eShiftSortBTN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shiftSort();
				if ("卐".equals(eShiftSortBTN.getText())) {
					eShiftSortBTN.setText("卍");
				} else {
					eShiftSortBTN.setText("卐");
				}

			}
		});
		eShiftSortBTN.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				sortDescent();
				return false;
			}
		});

		listView = this.getListView();

	}

	public void itemLongClickProcess(View v) {
		mView = v;

	}

	/**
	 * resume process
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 终止操作
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// create action bar menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.check_list, menu);
		return true;
	}

	/**
	 * action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.check_list_menu_plus:
			plusItem();
			return true;
		case R.id.check_list_menu_save:
			saveList();
			return true;
		case R.id.check_list_menu_shift:
			shiftSort();
			return true;
		case R.id.check_list_menu_desc:
			sortDescent();
			return true;
		case R.id.check_list_menu_reset:
			renderCheckListView(true, true);
			return true;
		case android.R.id.home:
			onCancelPorcess();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	// /////////////////////////////////////// load check list view
	/**
	 * 显示数据
	 */
	public void renderCheckListView(boolean init, boolean blank) {

		if (blank) {
			list = new ArrayList<Object>();
		} else if (init) {
			list = mDbHelper.getCheckList(mNoteId);
			if (list == null || list.size() <= 0) {
				// initially the check list
				list = newItem();
			}
		}

		if (init || blank) {
			// adapter the check list
			mCheckListAdapter = new CheckListAdapter(this, list, mNoteId);
			setListAdapter(mCheckListAdapter);

			calTotalScoreInit();
		} else {
			// mCheckListAdapter.add(null);
			// Tell to the adapter that changes have been made, this will cause
			// the list to refresh
			mCheckListAdapter.notifyDataSetChanged();

			// calculate the scores but initially does not work!
			// calTotalScore();
		}

		if (blank) {
			Toast.makeText(this, "Deleted all items!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void calTotalScoreInit() {

		// calculate the total scores
		mCheckListAdapter.calTotalScore();
		eTotalScoreTV.setText(mCheckListAdapter.getCheckScore() + " | "
				+ mCheckListAdapter.getTotalSocre());
	}

	public void calTotalScore() {
		getCheckListData();
		// calculate the total scores
		eTotalScoreTV.setText(mCheckScore + " | " + mTotalScore);
	}

	/**
	 * 保存处理
	 */
	public void saveList() {
		mDbHelper.updateCheckList(mNoteId, getCheckListData());
		// 保存成功
		Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
		onCancelPorcess();
	}

	public List<Object> getCheckListData() {
		return getCheckListData(false);
	}

	private List<Object> getCheckListData(boolean isCheckedAll) {
		List<Object> listData = new ArrayList<Object>();
		// set score
		mTotalScore = 0;
		mCheckScore = 0;
		for (int i = 0; i < mCheckListAdapter.getCount(); i++) {
			ContentValues cv = (ContentValues) mCheckListAdapter.getItem(i);

			// set total score
			int score = cv.getAsInteger(NoteDbAdapter.CT_SCORE);
			mTotalScore += score;

			if (isCheckedAll) {
				// check all items
				cv.put(NoteDbAdapter.CT_ITEM_CHECKED, checkedAll);
			}

			// normal single check score
			mCheckScore += cv.getAsInteger(NoteDbAdapter.CT_ITEM_CHECKED) == 1 ? score
					: 0;

			listData.add(cv);
		}

		// check all case
		if (isCheckedAll) {
			if (checkedAll == 1) {
				Toast.makeText(this, "Checked all items!", Toast.LENGTH_SHORT)
						.show();
				checkedAll = 0;
				eCheckAllBTN.setTextColor(this.getResources().getColor(
						R.color.cItemTitle));

				mCheckScore = mTotalScore;
			} else {
				Toast.makeText(this, "UnChecked all items!", Toast.LENGTH_SHORT)
						.show();
				checkedAll = 1;
				eCheckAllBTN.setTextColor(this.getResources().getColor(
						R.color.myBlue));

				mCheckScore = 0;
			}
		}

		return listData;

	}

	private List<Object> getCheckListDataByViewingGroup(boolean isCheckedAll) {
		List<Object> listData = new ArrayList<Object>();
		mTotalScore = 0;
		mCheckScore = 0;
		// ergodic the child items of viewing group
		for (int i = 0; i < getListView().getChildCount(); i++) {
			// get the child item of viewing group by position
			View view = getListView().getChildAt(i);
			EditText checkItem = (EditText) view.findViewById(R.id.check_item);
			CheckBox itemCb = (CheckBox) view.findViewById(R.id.item_cb);
			EditText checkScore = (EditText) view
					.findViewById(R.id.check_score);
			String scoreStr = checkScore.getText().toString();
			int score = 0;
			try {
				score = Integer.valueOf(scoreStr);
			} catch (Exception e) {
				score = 0;
			}

			ContentValues cv = new ContentValues();
			cv.put(NoteDbAdapter.CT_NOTE_ROWID, mNoteId);
			cv.put(NoteDbAdapter.CT_ITEM_TITLE, checkItem.getText().toString());
			cv.put(NoteDbAdapter.CT_ITEM_CHECKED, isCheckedAll ? checkedAll
					: itemCb.isChecked() ? 1 : 0);
			cv.put(NoteDbAdapter.CT_SHOW_INDEX, i);
			cv.put(NoteDbAdapter.CT_SCORE, score);
			listData.add(cv);

			mTotalScore += score;
			mCheckScore += itemCb.isChecked() ? score : 0;
		}

		if (isCheckedAll) {
			if (checkedAll == 1) {
				Toast.makeText(this, "Checked all items!", Toast.LENGTH_SHORT)
						.show();
				checkedAll = 0;
				eCheckAllBTN.setTextColor(this.getResources().getColor(
						R.color.cItemTitle));
			} else {
				Toast.makeText(this, "UnChecked all items!", Toast.LENGTH_SHORT)
						.show();
				checkedAll = 1;
				eCheckAllBTN.setTextColor(this.getResources().getColor(
						R.color.myBlue));
			}
		}

		return listData;
	}

	/**
	 * 添加条目
	 */
	public void plusItem() {
		list = this.getCheckListData();
		list.add(newCV());
		mCheckListAdapter = new CheckListAdapter(this, list, mNoteId);
		setListAdapter(mCheckListAdapter);
		focusItemByScroll(-1);
	}

	/**
	 * check all items on / off
	 */
	private void checkAllItems() {
		this.list = this.getCheckListData(true);
		this.mCheckListAdapter.clear();
		this.mCheckListAdapter.addAll(this.list);
		this.mCheckListAdapter.notifyDataSetChanged();

		// calculate the total scores
		eTotalScoreTV.setText(mCheckScore + " | " + mTotalScore);
	}

	/**
	 * 初始化空白条目
	 * 
	 * @return
	 */
	private ArrayList<Object> newItem() {
		ArrayList<Object> _list = new ArrayList<Object>();
		_list.add(newCV());
		return _list;
	}

	private ContentValues newCV() {
		ContentValues cv = new ContentValues();
		cv.put(NoteDbAdapter.CT_NOTE_ROWID, mNoteId);
		cv.put(NoteDbAdapter.CT_ITEM_TITLE, "");
		cv.put(NoteDbAdapter.CT_ITEM_CHECKED, 0);
		cv.put(NoteDbAdapter.CT_SCORE, 0);
		return cv;
	}

	/**
	 * 取消按钮动作，放到onClick体会intent创建不能！因为this指代对象不同
	 * 
	 * @param view
	 */
	private void onCancelPorcess() {
		Intent i = new Intent(this, NoteListActivity.class);
		startActivityForResult(i, ACTIVITY_LIST);
	}

	/**
	 * 排序
	 */
	public void shiftSort() {
		mCheckListAdapter.isSort = !mCheckListAdapter.isSort;
		renderCheckListView(false, false);
	}

	/**
	 * sort descent
	 */
	public void sortDescent() {
		List<Object> descList = new ArrayList<Object>();
		for (int i = list.size() - 1; i >= 0; i--) {
			descList.add(list.get(i));
		}

		list = descList;
		mCheckListAdapter.clear();
		mCheckListAdapter.addAll(list);
		mCheckListAdapter.notifyDataSetChanged();
		Toast.makeText(this, "Descented!", Toast.LENGTH_SHORT).show();
	}

	// /////////////on key event
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 自动保存
			saveList();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * focus the item of list view(include hidden view)
	 * 
	 * @param position
	 *            [-1]:focus the last item of list view(scroll the bottom of
	 *            list view) ; [>=0]:scroll the item of position
	 */
	public void focusItemByScroll(final int position) {
		listView.post(new Runnable() {
			@Override
			public void run() {
				if (position == -1 || listView.getCount() <= position) {
					// Select the last row so it will scroll into view...
					listView.setSelection(listView.getCount() - 1);
				}
			}
		});
	}
}
