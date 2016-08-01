package com.jeffen.note;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.SearchRecentSuggestions;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffen.app.AlarmActivity;
import com.jeffen.app.HelpTabsActivity;
import com.jeffen.app.SetActivity;
import com.jeffen.fragments.JDialogFragment;
import com.jeffen.login.LoginActivity;
import com.jeffen.pojo.Note;
import com.jeffen.pojo.Option;
import com.jeffen.sys.SlidingMenu;
import com.util.CommonCheck;
import com.util.DataTypeUtil;
import com.util.SearchSuggestionsProvider;
import com.util.ViewUtility;
import com.widget.GroupListAdapter;
import com.widget.ItemMenuListAdapter;
import com.widget.item.SearchViewOnSuggestionListener;

/**
 * @author Jeffen Cheung
 * 
 */
@SuppressLint("NewApi")
public class NoteListActivity extends ListActivity implements
		OnItemLongClickListener {
	public static final String TAG = "NoteList";
	private Vibrator vibrator;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_LOGIN = 2;
	private static final int ACTIVITY_HELP = 3;
	private static final int ACTIVITY_IMAGE_SWITCH = 4;
	private static final int ACTIVITY_SET = 5;
	private static final int ACTIVITY_ALARM = 6;
	private static final int ACTIVITY_CHECK = 7;
	private static final int ACTIVITY_BOOKMARK = 8;

	private static final int LOGIN_ID = Menu.FIRST;
	private static final int EXIT_ID = Menu.FIRST + 1;
	private static final int DROP_ID = Menu.FIRST + 2;

	public Context mContext;
	public TextView mTitle;

	public android.widget.SearchView mSearchView;
	public String mSearchKey;
	public SearchRecentSuggestions mSuggestions;

	public boolean mCheckAll = true;
	public SlidingMenu mSlidingMenu;
	public NoteDbAdapter mDbHelper;
	private Cursor mNoteCursor;
	private int mNoteCount;

	// group list view data
	private GroupListAdapter mGroupListAdapter = null;
	private List<Object> list = new ArrayList<Object>();
	private List<Object> listTag = new ArrayList<Object>();
	private long mNoteId;
	private View mView;
	private String mNoteTitle;

	// dialog
	private static final int DIALOG1 = 1;
	private static final int DIALOG_ITEM_LONG_PRESS = 2;
	private static final int DIALOG4 = 4;
	private static final int DIALOG3 = 3;

	private int backButtonCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);
		mContext = this;

		// search widget by android 3.0+ and API+11
		// Get the intent, verify the action and get the query
		mSearchKey = null;
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// Adding Recent Query Suggestions.
			mSearchKey = intent.getStringExtra(SearchManager.QUERY);
			if (mSuggestions == null)
				mSuggestions = new SearchRecentSuggestions(this,
						SearchSuggestionsProvider.AUTHORITY,
						SearchSuggestionsProvider.MODE);
			mSuggestions.saveRecentQuery(mSearchKey, null);
		}

		// home button to return
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		// custom app title
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(R.layout.app_title);
		mTitle = (TextView) findViewById(R.id.txAppTitle);
		mTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = "UnChecked";
				if (mCheckAll)
					str = "Checked";
				// Toast.makeText(mContext, str + " all items!",
				// Toast.LENGTH_SHORT).show();
				chkAll();
			}
		});

		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();

		// reset the db
		if (false)
			mDbHelper.dropDb();

		// render list view
		// renderListView();
		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = getIntent().getExtras();
		boolean isAlarm = false;
		boolean isStar = false;
		if (extras != null) {
			// the extras always null! appwidget provider can not post data!
			if (extras.getBoolean("IS_ALARM")) {
				isAlarm = true;
			}
			if (extras.getBoolean("IS_STAR")) {
				isStar = true;
			}
			if (extras.getBoolean("IS_SEARCH")) {
				if (mSearchView != null) {
					mSearchView.callOnClick();
				}
			}
		}
		renderGroupListViewByPage(true, isAlarm, isStar, true);

		/*
		 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
		 */
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		doVibrator(new long[] { 0, 100 });

		// 添加悬浮标题
		// if (this.getListView().getChildCount() > 0) {
		// View floatTag = this.getLayoutInflater().inflate(
		// R.layout.note_row_tag, null);
		// TextView groupTitle = (TextView) floatTag
		// .findViewById(R.id.group_title);
		// groupTitle.setText("add titile");
		// TextView groupCount = (TextView) floatTag
		// .findViewById(R.id.group_count);
		// groupCount.setText("count");
		//
		// ViewGroup vg = (ViewGroup) this.getListView().getChildAt(0);
		// vg.addView(floatTag);
		// }

	}

	public void itemLongClickProcess(View v) {
		mView = v;

		TextView rowId = (TextView) v.findViewById(R.id.note_row_id);
		if (rowId != null) {
			mNoteId = Long.valueOf(rowId.getText().toString());
			Log.i(TAG, "rowId=" + mNoteId);

			if (v.findViewById(R.id.tableRow1) != null) {

			}
		}

		TextView rowTitle = (TextView) v.findViewById(R.id.note_row_title);
		if (rowTitle != null) {
			mNoteTitle = rowTitle.getText().toString();
		}

		doVibrator(new long[] { 0, 50 });
		showDialog(DIALOG_ITEM_LONG_PRESS);
	}

	/**
	 * check n uncheck all the items
	 */
	public void chkAll() {
		for (int i = 0; i < getListView().getChildCount(); i++) {
			View view = getListView().getChildAt(i);
			if (view.findViewById(R.id.item_cb) != null) {
				CheckBox c = (CheckBox) view.findViewById(R.id.item_cb);

				if (c != null) {
					c.setChecked(mCheckAll);
				}
			}
		}
		mCheckAll = !mCheckAll;
	}

	/**
	 * 统计选中个数
	 */
	public void cntChk() {
		int cnt = 0;
		for (int i = 0; i < getListView().getChildCount(); i++) {
			View view = getListView().getChildAt(i);
			if (view.findViewById(R.id.item_cb) != null) {
				CheckBox c = (CheckBox) view.findViewById(R.id.item_cb);

				if (c != null && c.isChecked()) {
					cnt++;
				}
			}
		}

		// setTitle("选中:" + cnt + "/" + mNoteCount + "个");
		// setTitleColor(Color.YELLOW);
		// getActionBar().setHomeButtonEnabled(true);
		mTitle.setText("选中:" + cnt + "/" + mNoteCount + "个");

		if (cnt == 0) {
			// setTitle(R.string.app_name);
			// setTitleColor(Color.WHITE);
			mTitle.setText(R.string.app_name);

			renderGroupListViewByPage(true, false, false, true);
			// getActionBar().setHomeButtonEnabled(false);
		}
	}

	/**
	 * 震动操作
	 */
	private void doVibrator(long[] pattern) {
		// 获取SharedPreferences对象
		SharedPreferences sp = this.getSharedPreferences(Option.SP,
				MODE_PRIVATE);
		if (!sp.getBoolean(Option.IS_VIBRATOR, false))
			return;

		// long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
		if (vibrator == null) {
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		}
		vibrator.vibrate(pattern, -1); // 重复n上面的pattern 如果只想震动一次，index设为-1
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
		vibrator.cancel();
	}

	/**
	 * 包装note_row样式的数据集到note_list里
	 */
	private void renderListView() {
		mNoteCursor = mDbHelper.getAllNotes(false, false, false, null);
		startManagingCursor(mNoteCursor);
		String[] from = new String[] { NoteDbAdapter.KEY_TITLE,
				NoteDbAdapter.KEY_CREATED };
		int[] to = new int[] { R.id.note_row_title, R.id.note_row_created };
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.note_row, mNoteCursor, from, to);
		setListAdapter(notes);
	}

	/**
	 * 分组显示数据
	 * 
	 * @param starsOnly
	 *            星标笔记
	 * @param desc
	 *            喜新厌旧
	 */
	private void renderGroupListView(boolean starsOnly, boolean desc) {
		list = new ArrayList<Object>();
		listTag = new ArrayList<Object>();
		if (starsOnly) {
			mNoteCursor = mDbHelper.getAllNotes(false, false, starsOnly, null);
		} else {
			mNoteCursor = mDbHelper.getAllNotes(desc, false, false, null);
		}

		ContentValues cv = new ContentValues();
		ContentValues cvT = new ContentValues();
		int si = 0;// section index (tag index 分组数)
		if (mNoteCursor != null && mNoteCursor.getCount() > 0) {
			// setTitle(R.string.app_name + "(" + mNoteCursor.getCount() + ")");
			mNoteCount = mNoteCursor.getCount();
			for (int i = 0; i < mNoteCursor.getCount(); i++) {
				cv = new ContentValues(); // 当前记录
				cvT = new ContentValues(); // 当前分组

				// 取得当前记录
				mNoteCursor.moveToPosition(i);
				long id = mNoteCursor.getLong(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
				String title = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE));
				if (CommonCheck.isEmpty(title))
					title = this.getResources()
							.getString(R.string.msgTitleHint);
				String body = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY));
				if (CommonCheck.isEmpty(body)) {
					body = this.getResources().getString(R.string.msgBodyHint);
				} else {
					String weather = mNoteCursor.getString(mNoteCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_WEATHER));
					body = weather + "\n" + body;
				}
				byte[] imageCover = mNoteCursor.getBlob(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.IK_BITMAP));
				long imageCnt = mNoteCursor.getLong(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.IK_BITMAP_CNT));
				String created = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_CREATED));
				String yyyymm = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_YYYYMM));
				String hhmm = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_HHMM));
				String updDatetime = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_UPD_DATETIME));
				String isStared = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_IS_STARED));
				String isDeleted = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_IS_DELETED));
				if (CommonCheck.isNotEmpty(updDatetime)) {
					// 修改信息
					updDatetime = DataTypeUtil.getDateDiff(updDatetime, 9)
							+ "修改";
					hhmm = updDatetime;
				}
				cv.put(mDbHelper.KEY_ROWID, id);
				cv.put(mDbHelper.KEY_TITLE, title);
				cv.put(mDbHelper.KEY_BODY, body);
				cv.put(mDbHelper.IK_BITMAP, imageCover);
				cv.put(mDbHelper.IK_BITMAP_CNT, imageCnt);
				cv.put(mDbHelper.KEY_CREATED, created);
				cv.put(mDbHelper.KEY_YYYYMM, yyyymm);
				cv.put(mDbHelper.KEY_HHMM, hhmm);
				cv.put(mDbHelper.KEY_IS_STARED,
						CommonCheck.null2black(isStared));
				cv.put(mDbHelper.KEY_IS_DELETED,
						CommonCheck.null2black(isDeleted));

				cv.put("position", i);// 当前记录总体位置
				cv.put(mDbHelper.SECTION_INDEX, si++);// 当前分组记录数
				Log.d(TAG, "cv:" + cv);

				list.add(cv);

				if (i == 0) {
					// 第一条记录
					cvT.put(mDbHelper.KEY_GROUPT_TITLE, yyyymm);
					cvT.put(mDbHelper.KEY_GROUPT_COUNT, "1\u0020\u0020件");
					cvT.put(mDbHelper.SECTION_POSITION,
							String.valueOf(list.size()));// 分组标题当前位置
					cvT.put("IS_TAG", true);
					listTag.add(cvT);
					si = 0;

					list.add(list.size() - 1, cvT);
				} else {
					ContentValues cvPre = (ContentValues) list.get(i - 1
							+ listTag.size());
					if (!yyyymm.equals(cvPre.get(mDbHelper.KEY_YYYYMM))) {
						// 分组记录
						cvT.put(mDbHelper.KEY_GROUPT_TITLE, yyyymm);
						cvT.put(mDbHelper.KEY_GROUPT_COUNT, "1\u0020\u0020件");
						cvT.put(mDbHelper.SECTION_POSITION,
								String.valueOf(list.size()));// 分组标题当前位置
						cvT.put("IS_TAG", true);
						listTag.add(cvT);

						ContentValues cvTPre = (ContentValues) listTag
								.get(listTag.size() - 2);
						cvTPre.put(mDbHelper.KEY_GROUPT_COUNT, "" + si
								+ "\u0020\u0020件");
						si = 0;
						list.add(list.size() - 1, cvT);

					}
				}

				if (i + 1 == mNoteCursor.getCount()) {
					// 最后一条记录,更新所属组记录数
					ContentValues cvTPre = (ContentValues) listTag.get(listTag
							.size() - 1);
					cvTPre.put(mDbHelper.KEY_GROUPT_COUNT, "" + (si + 1)
							+ "\u0020\u0020件");
				}
			}
		}

		mGroupListAdapter = new GroupListAdapter(this, list, listTag);
		setListAdapter(mGroupListAdapter);
		// 注册滚屏
		getListView().setOnScrollListener(mGroupListAdapter);
	}

	/**
	 * on search button requested
	 */
	@Override
	public boolean onSearchRequested() {
		// pauseSomeStuff
		return super.onSearchRequested();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// sub menu coding is a bad way ; xml layout is a good way
		// SubMenu sub = menu.addSubMenu("More");
		// sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// sub.getItem().setIcon(R.drawable.list_bullets_silver);
		// inflater.inflate(R.menu.main, sub);

		// create action bar menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// show note count hint
		// MenuItem mi = menu.getItem(2);
		// mi = (MenuItem)this.findViewById(R.id.menu_drop);
		// TextView tvNoteCount = new TextView(this);
		// Drawable mid = mi.getIcon();
		// View miv = mi.getActionView();
		// int top = 0, left = 0;
		// if (miv != null) {
		// top = mi.getActionView().getTop();
		// left = mi.getActionView().getLeft();
		// }
		// tvNoteCount.setTop(top);
		// tvNoteCount.setLeft(left);
		// tvNoteCount.setText(mNoteCount);
		// setContentView(tvNoteCount);

		// Configure the search info and add any event listeners
		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		if (true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			// android.support.v7.widget.SearchView |
			// com.actionbarsherlock.widget.SearchView
			mSearchView = (android.widget.SearchView) searchItem
					.getActionView();
			// with AVD the searchView is always null!
			if (mSearchView != null) {
				// Assumes current activity is the searchable activity
				mSearchView.setSearchableInfo(searchManager
						.getSearchableInfo(getComponentName()));
				mSearchView.setIconifiedByDefault(true);
				mSearchView.setSubmitButtonEnabled(true);
				mSearchView.setOnSearchClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// menu search icon click event! not the search button
						// click event!
					}
				});

				// on long click listener dose note work, never be called!
				mSearchView.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						if (mSuggestions == null)
							mSuggestions = new SearchRecentSuggestions(
									mContext,
									SearchSuggestionsProvider.AUTHORITY,
									SearchSuggestionsProvider.MODE);
						mSuggestions.clearHistory();
						return false;
					}
				});

				// set custom search view
				// customSearchView(searchView);
			}
		}

		return true;
	}

	/**
	 * Customizing android.widget.SearchView
	 * 
	 * @param searchView
	 */
	private void customSearchView(SearchView searchView) {
		try {
			Field searchField = SearchView.class
					.getDeclaredField("mSearchButton");
			searchField.setAccessible(true);
			ImageView searchBtn = (ImageView) searchField.get(searchView);
			searchBtn.setImageResource(R.drawable.search);
			searchField = SearchView.class.getDeclaredField("mSearchPlate");
			searchField.setAccessible(true);
			LinearLayout searchPlate = (LinearLayout) searchField
					.get(searchView);
			// SearchView$SearchAutoComplete cannot be case to ImageView
			// ((ImageView)searchPlate.getChildAt(0)).setImageResource(R.drawable.ic_launcher_d);
			searchPlate.setBackgroundResource(R.drawable.simple_text);

			int suggestionsCount = searchPlate.getChildCount() - 1;
			View lastChild = searchPlate
					.getChildAt(searchPlate.getChildCount() - 1);
			// never be called
			lastChild.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSuggestions == null)
						mSuggestions = new SearchRecentSuggestions(mContext,
								SearchSuggestionsProvider.AUTHORITY,
								SearchSuggestionsProvider.MODE);
					mSuggestions.clearHistory();
				}
			});

			// dose note worked, bad way!
			searchView
					.setOnSuggestionListener(new SearchViewOnSuggestionListener(
							this.mContext, this.mSuggestions, suggestionsCount));

		} catch (NoSuchFieldException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	/**
	 * item menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case LOGIN_ID:
			createNote();
			return true;
		case EXIT_ID:
			mDbHelper.deleteNote(getListView().getSelectedItemId());
			renderGroupListView(false, false);
			return true;
		case DROP_ID:
			mDbHelper.dropDb();
			renderGroupListView(false, false);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_insert:
			createNote();
			return true;
		case R.id.menu_stars:
			renderGroupListViewByPage(true, false, true, true);
			return true;

		case R.id.menu_copy:
			return batProcess(item.getItemId());

		case R.id.menu_delete:
			return batProcess(item.getItemId());

		case R.id.menu_alarm:

			renderGroupListViewByPage(true, true, false, true);
			return true;

		case R.id.menu_desc:
			renderGroupListViewByPage(true, false, false, false);
			return true;

		case R.id.menu_merge:
			return batProcess(item.getItemId());

		case R.id.menu_drop:
			// The method showDialog(int) from the type Activity is deprecated
			// showDialog(DIALOG1);
			showJDialog(R.string.alert_dialog_detele);

			return true;
		case R.id.menu_login:
			login();
			return true;
		case R.id.menu_help:
			help();
			return true;
		case R.id.menu_set:
			set();
			return true;
		case R.id.menu_kill:
			// Kill the App
			// android.os.Process.killProcess(android.os.Process.myPid());

			// finish();
			super.onBackPressed();

			return true;
		case android.R.id.home:
			mSearchKey = null;
			this.renderGroupListViewByPage(true, false, false, true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * help
	 */
	private void help() {
		Intent i = new Intent(this, HelpTabsActivity.class);
		startActivityForResult(i, ACTIVITY_HELP);
	}

	/**
	 * 设置
	 */
	private void set() {
		Intent i = new Intent(this, SetActivity.class);
		startActivityForResult(i, ACTIVITY_SET);
	}

	/**
	 * 登录
	 */
	private void login() {
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, ACTIVITY_LOGIN);
	}

	/**
	 * 创建
	 */
	private void createNote() {
		Intent i = new Intent(this, NoteEditActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/**
	 * 清单
	 */
	public void gotoCheckList(long noteId, String notetitle) {
		Intent i = new Intent(this, CheckListActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, noteId);
		i.putExtra(NoteDbAdapter.KEY_TITLE, notetitle);
		startActivityForResult(i, ACTIVITY_CHECK);
	}

	/**
	 * 闹钟
	 */
	public void alarm(long noteId) {
		Intent i = new Intent(this, AlarmActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, noteId);
		startActivityForResult(i, ACTIVITY_ALARM);
	}

	/**
	 * 清理数据
	 */
	private void dropDb() {
		mDbHelper.dropDb();
		renderGroupListViewByPage(true, false, false, true);
	}

	@Override
	// 需要对position和id进行一个很好的区分
	// position指的是点击的这个ViewItem在当前ListView中的位置
	// 每一个和ViewItem绑定的数据，肯定都有一个id，通过这个id可以找到那条数据。
	// when the getView() method of adapter is rendering the button,checkbox or
	// something,
	// the itemclicklistner must be immediately rebounded again ,and the
	// following method is unused.
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		itemClickProcess(position);
	}

	public void itemClickProcess(int position) {
		Cursor c = mNoteCursor;

		ContentValues cv = (ContentValues) list.get(position);
		if (cv.containsKey("position")) {
			c.moveToPosition(Integer.valueOf(cv.get("position").toString()));
		}

		goToNoteEdt(c.getLong(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
	}

	/**
	 * 跳转更新页面
	 * 
	 * @param rowId
	 */
	public void goToNoteEdt(long rowId) {
		Intent i = new Intent(this, NoteEditActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, rowId);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	/**
	 * 更新星标
	 * 
	 * @param rowId
	 * @param isStared
	 */
	public void updStar(long rowId, String isStared) {
		mDbHelper.updateStar(rowId, isStared);
	}

	/**
	 * 更新删除
	 * 
	 * @param rowId
	 * @param isDeleted
	 */
	public void updDele(long rowId, String isDeleted) {
		mDbHelper.updateDelete(rowId, isDeleted);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		renderGroupListViewByPage(true, false, false, true);
	}

	// DIALOG
	// ////////////////////////////////////////////////////////////////////////
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG1:
			return buildDialog1(NoteListActivity.this);
		case DIALOG_ITEM_LONG_PRESS:
			return buildDialogItemLongPress(NoteListActivity.this);
		}

		return null;
	}

	/**
	 * 确定清空数据
	 * 
	 * @param context
	 * @return
	 */
	private Dialog buildDialog1(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle(R.string.alert_dialog_processing_title);
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.d(TAG, "点击了对话框上的确定按钮");

					}
				});
		builder.setNegativeButton(R.string.btnCancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.d(TAG, "点击了对话框上的取消按钮");

					}
				});
		return builder.create();

	}

	/**
	 * item long click event to call dialog menu
	 * 
	 * @param context
	 * @return
	 */
	private Dialog buildDialogItemLongPress(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(null);
		builder.setTitle(null);
		ItemMenuListAdapter itemMenuListAdapter = new ItemMenuListAdapter(
				context);
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				String m = getResources().getStringArray(
						R.array.item_dialog_menu)[which];
				String i = "你选择了: " + m;

				switch (which) {
				case 0:// 清单
					gotoCheckList(mNoteId, mNoteTitle);
					return;
				case 1:// 置顶
					if (mDbHelper.updateTop(mNoteId)) {
						Toast.makeText(context,
								"The selected note is setted to TOP one!",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "The TOP note is canceled!",
								Toast.LENGTH_SHORT).show();
					}
					renderGroupListViewByPage(true, false, false, true);
					return;
				case 2:// 纸团
					Note n1 = mDbHelper.getSingleNote(mNoteId);
					int d = Integer.valueOf(n1.getIsDeleted());
					mGroupListAdapter.setItemPaintFlags(mView, d == 0 ? 1 : 0,
							true);
					return;
				case 3:// 闹钟
					alarm(mNoteId);
					return;
				case 4:// 分享
					Note n2 = mDbHelper.getSingleNote(mNoteId);
					Intent sharingIntent = new Intent(Intent.ACTION_SEND);
					sharingIntent.setType("text/html");
					sharingIntent.putExtra(
							android.content.Intent.EXTRA_TEXT,
							Html.fromHtml("Share from JeffenNote："
									+ n2.getTitle() + "\n" + n2.getWeather()
									+ "\n" + n2.getBody()));
					startActivity(Intent.createChooser(sharingIntent,
							"Share using"));
					return;
				}
			}
		};

		builder.setAdapter(itemMenuListAdapter, onClickListener);
		return builder.create();

	}

	private void showJDialog(int title) {
		JDialogFragment newFragment = JDialogFragment.newInstance(title);
		newFragment.show(getFragmentManager(), "dialog");
	}

	public void doPositiveClick() {
		// ---perform steps when user clicks on OK---
		Log.d("NoteListActivity", "User clicks on OK");
		dropDb();
	}

	public void doNegativeClick() {
		// ---perform steps when user clicks on Cancel---
		Log.d("NoteListActivity", "User clicks on Cancel");
	}

	// /////////////on key event
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// back running
			// moveTaskToBack(true);
			onBackPressed();

			// just closes the current activity but not the whole application.
			// finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// ///////////////image cover click
	/**
	 * 跳转到图片查看页面
	 * 
	 * @param imagePosition
	 */
	public void goToImageSwitch(long mRowId, int imagePosition) {
		Intent i = new Intent(this, ImageSwitchActivity.class);
		i.putExtra("image_position", imagePosition);
		i.putExtra(NoteDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i, ACTIVITY_IMAGE_SWITCH);
	}

	/**
	 * 跳转更新页面
	 * 
	 * @param rowId
	 */
	public void goToBookMark(long rowId) {
		Intent i = new Intent(this, BookMarkActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, rowId);
		startActivityForResult(i, ACTIVITY_BOOKMARK);
	}

	// /////////////////////////////////////// load page list view

	/**
	 * 分组显示数据
	 * 
	 * @param init
	 *            是否初始化
	 * @param alarmOnly
	 *            待办提醒
	 * @param starsOnly
	 *            星标笔记
	 * @param desc
	 *            喜新厌旧
	 */
	public void renderGroupListViewByPage(boolean init, boolean alarmOnly,
			boolean starsOnly, boolean desc) {
		// 初始化页码
		if (init) {
			mDbHelper.setPageNo(0);
			mDbHelper.setItemShowCount(0);
			mDbHelper.setItemTotalCount(0);
			mDbHelper.setPageCount(0);

			list = new ArrayList<Object>();
			listTag = new ArrayList<Object>();
		}

		// 保存现有显示的数据
		ArrayList<Object> _list = new ArrayList<Object>();
		ArrayList<Object> _listTag = new ArrayList<Object>();

		if (starsOnly) {
			mNoteCursor = mDbHelper.loadPage(false, alarmOnly, starsOnly,
					mSearchKey);
		} else {
			mNoteCursor = mDbHelper
					.loadPage(desc, alarmOnly, false, mSearchKey);
		}

		ContentValues cv = new ContentValues();
		ContentValues cvT = new ContentValues();
		int si = 0;// section index (tag index 分组数)
		if (mNoteCursor != null && mNoteCursor.getCount() > 0) {
			// setTitle(R.string.app_name + "(" + mNoteCursor.getCount() + ")");
			mNoteCount = mNoteCursor.getCount();
			for (int i = 0; i < mNoteCursor.getCount(); i++) {
				cv = new ContentValues(); // 当前记录
				cvT = new ContentValues(); // 当前分组

				// 取得当前记录
				mNoteCursor.moveToPosition(i);
				long id = mNoteCursor.getLong(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
				String title = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE));
				if (CommonCheck.isEmpty(title))
					title = "点击添加标题.";
				String body = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY));
				if (CommonCheck.isEmpty(body)) {
					body = "点击添加内容.";
				} else {
					String weather = mNoteCursor.getString(mNoteCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_WEATHER));
					body = weather + "\n" + body;
				}
				byte[] imageCover = mNoteCursor.getBlob(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.IK_BITMAP));
				long imageCnt = mNoteCursor.getLong(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.IK_BITMAP_CNT));
				String created = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_CREATED));
				String yyyymm = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_YYYYMM));
				String hhmm = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_HHMM));
				String updDatetime = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_UPD_DATETIME));
				String isStared = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_IS_STARED));
				String isDeleted = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_IS_DELETED));
				long showIndex = mNoteCursor.getLong(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_SHOW_INDEX));

				// alarm
				int year = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.AT_YEAR));
				int month = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.AT_MONTH));
				int day = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.AT_DAY));
				int hour = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.AT_HOUR));
				int minute = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.AT_MINUTE));

				// book mark
				String bookmark = mNoteCursor.getString(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.BT_BOOKMARK));

				// check
				int total_items = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.CT_TOTAL_ITEMS));
				int check_items = mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.CT_CHECK_ITEMS));

				if (CommonCheck.isNotEmpty(updDatetime)) {
					// 修改信息
					updDatetime = DataTypeUtil.getDateDiff(updDatetime, 9)
							+ "修改";
					hhmm = updDatetime;
				}
				cv.put(mDbHelper.KEY_ROWID, id);
				cv.put(mDbHelper.KEY_TITLE, title);
				cv.put(mDbHelper.KEY_BODY, body);
				cv.put(mDbHelper.IK_BITMAP, imageCover);
				cv.put(mDbHelper.IK_BITMAP_CNT, imageCnt);
				cv.put(mDbHelper.KEY_CREATED, created);
				cv.put(mDbHelper.KEY_YYYYMM, yyyymm);
				cv.put(mDbHelper.KEY_HHMM, hhmm);
				cv.put(mDbHelper.KEY_IS_STARED,
						CommonCheck.null2black(isStared));
				cv.put(mDbHelper.KEY_IS_DELETED,
						CommonCheck.null2black(isDeleted));
				cv.put(mDbHelper.KEY_SHOW_INDEX, showIndex);

				cv.put("position", i);// 当前记录总体位置
				cv.put(mDbHelper.SECTION_INDEX, si++);// 当前分组记录数

				// alarm
				cv.put(mDbHelper.AT_YEAR, year);
				cv.put(mDbHelper.AT_MONTH, month);
				cv.put(mDbHelper.AT_DAY, day);
				cv.put(mDbHelper.AT_HOUR, hour);
				cv.put(mDbHelper.AT_MINUTE, minute);

				// book mark
				boolean isBookMarked = true;
				if (CommonCheck.isEmpty(bookmark)) {
					isBookMarked = false;
					bookmark = "Click to insert a bookmark.";
				}
				cv.put("IS_BOOKMARKED", isBookMarked);
				cv.put(mDbHelper.BT_BOOKMARK, bookmark);

				// check
				String totalItemsHint = "点击或长按添加清单.";
				int isCheckedAll = 0;
				if (total_items > 0) {
					totalItemsHint = "总共" + total_items + "项，已完成" + check_items
							+ "项。";
					if (total_items == check_items) {
						isCheckedAll = 1;
						totalItemsHint += " √";
					}
				}
				cv.put(mDbHelper.CT_TOTAL_ITEMS, totalItemsHint);
				cv.put(mDbHelper.CT_IS_CHECKED_ALL, isCheckedAll);

				Log.d(TAG, "cv:" + cv);

				_list.add(cv);

				if (i == 0) {
					// 第一条记录
					cvT.put(mDbHelper.KEY_GROUPT_TITLE, yyyymm);
					cvT.put(mDbHelper.KEY_GROUPT_COUNT, "1\u0020\u0020件");
					cvT.put(mDbHelper.SECTION_POSITION,
							String.valueOf(_list.size()));// 分组标题当前位置
					cvT.put("IS_TAG", true);
					_listTag.add(cvT);
					si = 0;

					_list.add(_list.size() - 1, cvT);
				} else {
					ContentValues cvPre = (ContentValues) _list.get(i - 1
							+ _listTag.size());
					if (!yyyymm.equals(cvPre.get(mDbHelper.KEY_YYYYMM))) {
						// 分组记录
						cvT.put(mDbHelper.KEY_GROUPT_TITLE, yyyymm);
						cvT.put(mDbHelper.KEY_GROUPT_COUNT, "1\u0020\u0020件");
						cvT.put(mDbHelper.SECTION_POSITION,
								String.valueOf(_list.size()));// 分组标题当前位置
						cvT.put("IS_TAG", true);
						_listTag.add(cvT);

						ContentValues cvTPre = (ContentValues) _listTag
								.get(_listTag.size() - 2);
						cvTPre.put(mDbHelper.KEY_GROUPT_COUNT, "" + si
								+ "\u0020\u0020件");
						si = 0;
						_list.add(_list.size() - 1, cvT);

					}
				}

				if (i + 1 == mNoteCursor.getCount()) {
					// 最后一条记录,更新所属组记录数
					ContentValues cvTPre = (ContentValues) _listTag
							.get(_listTag.size() - 1);
					cvTPre.put(mDbHelper.KEY_GROUPT_COUNT, "" + (si + 1)
							+ "\u0020\u0020件");
				}
			}
		}

		if (init) {
			appendPage(_list, _listTag);
			mGroupListAdapter = new GroupListAdapter(this, list, listTag);
			setListAdapter(mGroupListAdapter);

			// 注册滚屏
			getListView().setOnScrollListener(mGroupListAdapter);
		} else {
			Map m = new HashMap();
			m.put("_list", _list);
			m.put("_listTag", _listTag);
			mGroupListAdapter.add(m);
			// Tell to the adapter that changes have been made, this will cause
			// the list to refresh
			mGroupListAdapter.notifyDataSetChanged();
		}

		// load more
		TextView load_title = (TextView) this
				.findViewById(R.id.note_row_load_title);
		load_title.setText("已加载" + mDbHelper.getItemShowCount() + "/"
				+ mDbHelper.getItemTotalCount() + "、点击更多");
		if (mDbHelper.hasLoadMore()) {
		} else {
			load_title.setText("没有更多，点击添加");
		}
		LinearLayout ll = (LinearLayout) this.findViewById(R.id.note_row_load);
		ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("getView-rowLoad-setOnClickListener",
						"new rowLoad click listener.");
				if (mDbHelper.hasLoadMore()) {
					renderGroupListViewByPage(false, false, false, true);
				} else {
					// add one note
					createNote();
				}
			}
		});
		ViewUtility.setListViewHeightBasedOnChildren(this.getListView());
	}

	/**
	 * 追加页面数据
	 * 
	 * @param _list
	 * @param _listTag
	 */
	private void appendPage(ArrayList<Object> _list, ArrayList<Object> _listTag) {
		for (int i = 0; i < _list.size(); i++) {
			list.add(_list.get(i));
		}
		for (int i = 0; i < _listTag.size(); i++) {
			listTag.add(_listTag.get(i));
		}
	}

	/**
	 * bound the item long click listener never ever be called!!! because of the
	 * adapter's OnTouchListener?or ,need to bound by getView of adapter?
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Log.d("setOnItemLongClickListener-onItemLongClick", view.toString()
				+ "position=" + position);

		itemLongClickProcess(view);

		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * ========================================================================
	 * initial the sliding menu ,but the on touch method is coverd by the item
	 * view of right list view!
	 */
	private void intSlidingEenu() {
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.addLeftView(createLeftView());
		View rightView = LayoutInflater.from(this).inflate(R.layout.note_list,
				null);
		mSlidingMenu.addRightView(rightView);
		setContentView(mSlidingMenu);
	}

	private LinearLayout createLeftView() {
		LinearLayout leftViewGroup = new LinearLayout(this);
		leftViewGroup.setLayoutParams(new LayoutParams(100,
				LayoutParams.FILL_PARENT));
		leftViewGroup.setBackgroundColor(Color.GREEN);
		TextView leftView = new TextView(this);
		leftView.setText("Left View");
		leftViewGroup.addView(leftView);
		return leftViewGroup;
	}

	/**
	 * ========================================================================
	 * 【menu】process checked notes
	 * 
	 * @param menu
	 */
	private boolean batProcess(int menuId) {
		int cnt = 0;
		List<Long> checkedIds = new ArrayList<Long>();
		for (int i = 0; i < getListView().getChildCount(); i++) {
			View view = getListView().getChildAt(i);
			CheckBox cb = (CheckBox) view.findViewById(R.id.item_cb);

			if (cb != null && cb.isChecked()) {
				ContentValues cv = (ContentValues) list.get(i);
				long id = (Long) cv.get(mDbHelper.KEY_ROWID);

				if (menuId == R.id.menu_delete) {
					mDbHelper.deleteNote(id);
				}

				if (menuId == R.id.menu_copy) {
					mDbHelper.copyNote(id, true, true, true);
				}
				cnt++;

				checkedIds.add(id);
			}
		}

		/**
		 * 判空
		 */
		if (cnt == 0) {
			Toast.makeText(this, "Please pick one or more items.",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (menuId == R.id.menu_merge) {
			mDbHelper.merge(checkedIds);
			Toast.makeText(this, "Merged Successfully!  (" + cnt + "个)",
					Toast.LENGTH_SHORT).show();

		}

		if (menuId == R.id.menu_delete) {
			Toast.makeText(this, "Deleted Successfully!  (" + cnt + "个)",
					Toast.LENGTH_SHORT).show();
		}

		if (menuId == R.id.menu_copy) {
			Toast.makeText(this, "Copy Successfully!  (" + cnt + "个)",
					Toast.LENGTH_SHORT).show();
		}
		// fresh list view
		renderGroupListViewByPage(true, false, false, true);

		return true;
	}

	/**
	 * Back button listener. Will close the application if the back button
	 * pressed twice.
	 */
	public void onBackPressed() {
		if (backButtonCount >= 1) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			Toast.makeText(
					this,
					"Press the back button once again to close the application.",
					Toast.LENGTH_SHORT).show();
			backButtonCount++;
		}
	}

}
