package com.jeffen.note;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jeffen.pojo.Note;
import com.jeffen.pojo.Option;
import com.util.CommonCheck;
import com.util.DataTypeUtil;

/**
 * @author Jeffen Cheung
 * 
 */
public class NoteDbAdapter {

	private static final String TAG = "NoteDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private Note mNote;
	private List<Note> mListNote = new ArrayList<Note>();
	private List<byte[]> mNoteImages = new ArrayList<byte[]>();

	private static final String CREATE_TABLE = "CREATE VIRTUAL TABLE ";
	private static final String FTS = " USING fts3";
	private static final String DATABASE_NAME = "j_note";
	private static final String DATABASE_TABLE = "note";
	private static final String IMAGE_TABLE = "image";
	private static final String ALARM_TABLE = "alarm";
	private static final String CHECK_TABLE = "t_check";
	private static final String BOOKMARK_TABLE = "bookmark";
	private static final int DATABASE_VERSION = 1;

	public static final String KEY_TITLE = "title";
	public static final String KEY_WEATHER = "weather";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "rowid";
	public static final String KEY_CREATED = "created";

	public static final String KEY_YYYYMM = "yyyymm";
	public static final String KEY_HHMM = "hhmm";
	public static final String KEY_ADD_DATETIME = "add_datetime";
	public static final String KEY_UPD_DATETIME = "upd_datetime";
	public static final String KEY_IS_STARED = "is_stared";
	public static final String KEY_IS_DELETED = "is_deleted";
	public static final String KEY_SHOW_INDEX = "show_index";
	public static final String KEY_SHARED_CNT = "shared_cnt";

	// image table
	public static final String IK_ROWID = "rowid";
	public static final String IK_NOTE_ROWID = "note_id";
	public static final String IK_BITMAP = "bitmap";
	public static final String IK_BITMAP_CNT = "bitmap_count";
	public static final String IK_SHOW_INDEX = "show_index";

	// alarm table
	public static final String AT_ROWID = "rowid";
	public static final String AT_NOTE_ROWID = "note_id";
	public static final String AT_YEAR = "year";
	public static final String AT_MONTH = "month";
	public static final String AT_DAY = "day";
	public static final String AT_HOUR = "hour";
	public static final String AT_MINUTE = "minute";

	// check list table
	public static final String CT_ROWID = "rowid";
	public static final String CT_NOTE_ROWID = "note_id";
	public static final String CT_ITEM_TITLE = "item_title";
	public static final String CT_ITEM_CHECKED = "item_checked";
	public static final String CT_SHOW_INDEX = "show_index";
	public static final String CT_SCORE = "score";
	public static final String CT_TOTAL_ITEMS = "total_items";
	public static final String CT_CHECK_ITEMS = "check_items";
	public static final String CT_IS_CHECKED_ALL = "is_checked_all";

	// book mark table
	public static final String BT_ROWID = "rowid";
	public static final String BT_NOTE_ROWID = "note_id";
	public static final String BT_BOOKMARK = "bookmark_text";

	// others
	public static final String SECTION_INDEX = "section_index";
	public static final String SECTION_POSITION = "section_position";

	public static final String KEY_GROUPT_TITLE = "group_title";
	public static final String KEY_GROUPT_COUNT = "group_count";

	// the real table PK【 " + CT_ROWID + " integer primary key autoincrement, 】
	private static final String DATABASE_CREATE = CREATE_TABLE + DATABASE_TABLE
			+ FTS + "(" + KEY_TITLE + " text null, " + KEY_WEATHER
			+ " text null, " + KEY_BODY + " text null, " + KEY_CREATED
			+ " text null, " + KEY_YYYYMM + " text null, " + KEY_HHMM
			+ " text null, " + KEY_ADD_DATETIME + " text null, "
			+ KEY_UPD_DATETIME + " text null, " + KEY_IS_STARED
			+ " text null, " + KEY_IS_DELETED + " text null, " + KEY_SHOW_INDEX
			+ " integer null, " + KEY_SHARED_CNT + " integer null);";
	private static final String DATABASE_CREATE_IMAGE = CREATE_TABLE
			+ IMAGE_TABLE + FTS + "(" + IK_NOTE_ROWID + " integer not null, "
			+ IK_BITMAP + " BLOB not null, " + IK_BITMAP_CNT
			+ " integer null, " + IK_SHOW_INDEX + " integer null);";
	private static final String DATABASE_CREATE_ALARM = CREATE_TABLE
			+ ALARM_TABLE + FTS + "(" + AT_NOTE_ROWID + " integer not null, "
			+ AT_YEAR + " integer null, " + AT_MONTH + " integer null, "
			+ AT_DAY + " integer null, " + AT_HOUR + " integer null, "
			+ AT_MINUTE + " integer null);";
	private static final String DATABASE_CREATE_CHECK = CREATE_TABLE
			+ CHECK_TABLE + FTS + "(" + CT_NOTE_ROWID + " integer not null, "
			+ CT_ITEM_TITLE + " text null, " + CT_ITEM_CHECKED
			+ " integer null, " + CT_SHOW_INDEX + " integer null, " + CT_SCORE
			+ " integer null);";
	private static final String DATABASE_CREATE_BOOKMARK = CREATE_TABLE
			+ BOOKMARK_TABLE + FTS + "(" + BT_NOTE_ROWID
			+ " integer not null, " + BT_BOOKMARK + " text null, "
			+ KEY_CREATED + " text null, " + KEY_YYYYMM + " text null, "
			+ KEY_HHMM + " text null, " + KEY_ADD_DATETIME + " text null, "
			+ KEY_UPD_DATETIME + " text null);";
	private final Context mCtx;
	private SharedPreferences sp = null;

	private int limit = 5;// 显示5行数据
	private int pageNo = 1;// 页码
	private int pageCount = 0;// 页面总数
	private int itemShowCount = 0;// 页面显示条目
	private int itemTotalCount = 0;// 数据总条目

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * 第一次生成数据库调用
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE_IMAGE);
			db.execSQL(DATABASE_CREATE_ALARM);
			db.execSQL(DATABASE_CREATE_CHECK);
			db.execSQL(DATABASE_CREATE_BOOKMARK);
		}

		/**
		 * 当数据库需要升级时，自动被调用
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS note");
			onCreate(db);
		}
	}

	public NoteDbAdapter(Context ctx) {
		this.mCtx = ctx;

		// 获取SharedPreferences对象
		sp = mCtx.getSharedPreferences(Option.SP, mCtx.MODE_PRIVATE);
		limit = sp.getInt(Option.LIMIT, 5);
	}

	/**
	 * 自动创建数据库或返回实例
	 * 
	 * @return
	 * @throws SQLException
	 */
	public NoteDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);

		// 如果没有数据则自动创建数据库
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void closeclose() {
		mDbHelper.close();
	}

	/**
	 * 创建笔记
	 * 
	 * @param title
	 *            标题
	 * @param weather
	 *            天气
	 * @param body
	 *            内容
	 * @param isStared
	 *            星标
	 * @param images
	 *            图片组
	 * @return
	 */
	public long createNote(String title, String weather, String body,
			String isStared, List<byte[]> images) {
		// content values key-value, make a value-object to insert a record
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_WEATHER, weather);
		initialValues.put(KEY_BODY, body);
		initialValues
				.put(KEY_CREATED, DataTypeUtil.getCurrentDateStrByFormat());
		initialValues.put(KEY_YYYYMM, DataTypeUtil.getCurrentYM());
		initialValues.put(KEY_HHMM, DataTypeUtil.getCurrentHM());
		initialValues.put(KEY_ADD_DATETIME,
				DataTypeUtil.getCurrentTimeStrByFormat());
		initialValues.put(KEY_IS_STARED, isStared);
		initialValues.put(KEY_IS_DELETED, "0");
		initialValues.put(KEY_SHOW_INDEX, 0);

		long noteRowId = mDb.insert(DATABASE_TABLE, null, initialValues);

		if (images != null && images.size() > 0) {
			for (int i = 0; i < images.size(); i++) {
				ContentValues cv = new ContentValues();
				cv.put(IK_NOTE_ROWID, noteRowId);
				cv.put(IK_BITMAP, images.get(i));
				cv.put(IK_BITMAP_CNT, images.size());
				cv.put(IK_SHOW_INDEX, i);
				mDb.insert(IMAGE_TABLE, null, cv);
			}
		}

		// 成功返回记录ID失败返回-1
		return noteRowId;
	}

	public boolean deleteNote(long rowId) {
		boolean delNote = mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId,
				null) > 0;
		boolean delImage = mDb.delete(IMAGE_TABLE, IK_NOTE_ROWID + "=" + rowId,
				null) > 0;
		boolean delAlarm = mDb.delete(ALARM_TABLE, AT_NOTE_ROWID + "=" + rowId,
				null) > 0;

		boolean delCheck = mDb.delete(CHECK_TABLE, AT_NOTE_ROWID + "=" + rowId,
				null) > 0;

		return delNote && delImage && delAlarm && delCheck;

	}

	/**
	 * 取得所有笔记
	 * 
	 * @param desc
	 *            喜新厌旧
	 * @return
	 */
	public Cursor getAllNotesNoImg(boolean desc) {
		Cursor ca = null;
		try {
			ca = mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
					KEY_WEATHER, KEY_BODY, KEY_CREATED, KEY_YYYYMM, KEY_HHMM,
					KEY_ADD_DATETIME, KEY_UPD_DATETIME, KEY_IS_STARED,
					KEY_IS_DELETED, KEY_SHOW_INDEX, KEY_SHARED_CNT }, null,
					null, null, null, desc ? KEY_UPD_DATETIME + ","
							+ KEY_ADD_DATETIME + " desc" : null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());

			dropDb();
		}

		return ca;
	}

	/**
	 * make a query sql </p> Apparently sqlite doesn't support combining OR
	 * queries with MATCH conditions.(FTS3)
	 * 
	 * @param desc
	 * @param alarmOnly
	 * @param star
	 * @param searchKey
	 * @return
	 */
	public String sqlGetAllNotes(boolean desc, boolean alarmOnly, boolean star,
			String searchKey) {
		boolean paperBall = false;
		paperBall = sp.getBoolean(Option.PAPER_BALL, paperBall);
		String sql = "select n."
				+ KEY_ROWID
				+ ",n.*,i."
				+ IK_BITMAP
				+ ",i."
				+ IK_BITMAP_CNT
				+ ",a."
				+ AT_YEAR
				+ ",a."
				+ AT_MONTH
				+ ",a."
				+ AT_DAY
				+ ",a."
				+ AT_HOUR
				+ ",a."
				+ AT_MINUTE
				+ ",ct."
				+ CT_TOTAL_ITEMS
				+ ",cc."
				+ CT_CHECK_ITEMS
				+ ",b."
				+ BT_BOOKMARK

				// note table
				+ " from "
				+ (CommonCheck.isEmpty(searchKey) ? DATABASE_TABLE
						: "(SELECT * FROM " + DATABASE_TABLE + " WHERE "
								+ KEY_TITLE + " MATCH '*" + searchKey
								+ "*' UNION SELECT * from " + DATABASE_TABLE
								+ " WHERE " + KEY_BODY + " MATCH '*"
								+ searchKey + "*')")
				+ " n"

				// images
				+ " left outer join "
				+ "(select * from "
				+ IMAGE_TABLE
				+ " where "
				+ IK_SHOW_INDEX
				+ "=0) i "
				+ "on n."
				+ KEY_ROWID
				+ "=i.note_id "

				// alarm
				+ " left outer join "
				+ ALARM_TABLE
				+ " a "
				+ "on n."
				+ KEY_ROWID
				+ "=a.note_id "

				// check
				+ " left outer join " + "(select note_id , count(0) "
				+ CT_TOTAL_ITEMS
				+ " from "
				+ CHECK_TABLE
				+ " group by note_id"
				+ ") ct "
				+ "on n."
				+ KEY_ROWID
				+ "=ct.note_id "
				+ " left outer join "
				+ "(select note_id , count(0) "
				+ CT_CHECK_ITEMS
				+ " from "
				+ CHECK_TABLE
				+ " where "
				+ CT_ITEM_CHECKED
				+ " >0 group by note_id"
				+ ") cc "
				+ "on n."
				+ KEY_ROWID
				+ "=cc.note_id "

				// bookmark
				+ " left outer join "
				+ BOOKMARK_TABLE
				+ " b "
				+ "on n."
				+ KEY_ROWID
				+ "=b.note_id "

				+ "where 1=1 "
				+ (paperBall ? " " : "and n." + KEY_IS_DELETED + "=\"0\" ")
				+ (star ? "and n." + KEY_IS_STARED + "=\"1\" " : " ")
				+ (alarmOnly ? "and a." + AT_YEAR + ">0 " : " ")
				+ "order by n."
				+ KEY_SHOW_INDEX
				+ " desc "
				+ (desc ? ",n." + KEY_UPD_DATETIME + " desc ,n."
						+ KEY_ADD_DATETIME + " desc " : " ");

		return sql;
	}

	/**
	 * 组装sql
	 * 
	 * @param noteId
	 * @return
	 */
	public String sqlGetSingleNote(long noteId) {
		boolean paperBall = false;
		paperBall = sp.getBoolean(Option.PAPER_BALL, paperBall);
		String sql = "select n." + KEY_ROWID + ",n.*,i." + IK_BITMAP + ",i."
				+ IK_BITMAP_CNT + ",a." + AT_YEAR + ",a." + AT_MONTH + ",a."
				+ AT_DAY + ",a." + AT_HOUR + ",a."
				+ AT_MINUTE
				+ " from "
				+ DATABASE_TABLE

				// images
				+ " n left outer join " + "(select * from " + IMAGE_TABLE
				+ " where " + IK_SHOW_INDEX + "=0) i " + "on n." + KEY_ROWID
				+ "=i.note_id "

				// alarm
				+ " left outer join " + ALARM_TABLE + " a " + "on n."
				+ KEY_ROWID + "=a.note_id "

				+ "where 1=1 " + "and n." + KEY_ROWID + "=" + noteId;

		return sql;
	}

	/**
	 * 取得所有笔记
	 * 
	 * @param desc
	 *            喜新厌旧
	 * @param alarmOnly
	 *            待办提醒
	 * @param star
	 *            星标
	 * @param searchKey
	 *            搜索关键字
	 * @return
	 */
	public Cursor getAllNotes(boolean desc, boolean alarmOnly, boolean star,
			String searchKey) {
		Cursor ca = null;
		try {

			ca = mDb.rawQuery(sqlGetAllNotes(desc, alarmOnly, star, searchKey),
					null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());

		}

		return ca;
	}

	/**
	 * 取得所有笔记<br/>
	 * 
	 * 读取指定的分页数据 <br/>
	 * SQL:Select * From TABLE_NAME Limit 5 Offset 10; <br/>
	 * 表示从TABLE_NAME表获取数据，跳过10行，取5行
	 * 
	 * @param desc
	 *            喜新厌旧
	 * @param alarmOnly
	 *            待办提醒
	 * @param star
	 *            星标
	 * @param searchKey
	 *            搜索关键字
	 * @return
	 */
	public Cursor loadPage(boolean desc, boolean alarmOnly, boolean star,
			String searchKey) {

		// 数据总条目
		Cursor ca = getAllNotes(desc, alarmOnly, star, searchKey);
		if (ca != null) {
			this.setItemTotalCount(ca.getCount());
		}

		try {
			String sql = "";
			if (this.getPageNo() >= 0) {
				sql = "select * from ("
						+ sqlGetAllNotes(desc, alarmOnly, star, searchKey)
						+ ") " + " Limit " + String.valueOf(this.getLimit())
						+ " Offset "
						+ String.valueOf(this.getPageNo() * this.getLimit());

				this.setPageNo(this.getPageNo() + 1);
			}
			ca = mDb.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());

			dropDb();
		}

		if (ca != null) {
			// 页面显示条目
			this.setItemShowCount((this.getPageNo() - 1) * this.getLimit()
					+ ca.getCount());
		}

		return ca;
	}

	/**
	 * 是否可以加载更多
	 * 
	 * @return
	 */
	public boolean hasLoadMore() {
		return this.getItemShowCount() < this.getItemTotalCount();
	}

	private Cursor getNote(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_WEATHER, KEY_BODY, KEY_CREATED, KEY_YYYYMM, KEY_HHMM,
				KEY_ADD_DATETIME, KEY_UPD_DATETIME, KEY_IS_STARED,
				KEY_IS_DELETED, KEY_SHOW_INDEX, KEY_SHARED_CNT }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;

	}

	/**
	 * 取得图片列表
	 * 
	 * @param rowId
	 * @return
	 * @throws SQLException
	 */
	public List<byte[]> getByteImages(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, IMAGE_TABLE, new String[] { IK_ROWID,
				IK_NOTE_ROWID, IK_BITMAP, IK_BITMAP_CNT, IK_SHOW_INDEX },
				IK_NOTE_ROWID + "=" + rowId, null, null, null, null, null);

		mNoteImages = new ArrayList<byte[]>();
		while (mCursor.moveToNext()) {
			mNoteImages.add(mCursor.getBlob(mCursor
					.getColumnIndexOrThrow(IK_BITMAP)));
		}

		return mNoteImages;

	}

	public Cursor getStarNotesNoImage() throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_WEATHER, KEY_BODY, KEY_CREATED, KEY_YYYYMM, KEY_HHMM,
				KEY_ADD_DATETIME, KEY_UPD_DATETIME, KEY_IS_STARED,
				KEY_IS_DELETED, KEY_SHOW_INDEX, KEY_SHARED_CNT }, KEY_IS_STARED
				+ "=\"1\"", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;

	}

	/**
	 * single note data
	 * 
	 * @param rowId
	 * @return
	 */
	public Note getSingleNote(long rowId) {
		setNote(getNote(rowId), false);
		return mNote;
	}

	/**
	 * list view item note data(extends top image,alarm eg.)
	 * 
	 * @param rowId
	 * @return
	 */
	public Note getSingleItemNote(long rowId) {
		Cursor mCursor = null;
		try {

			mCursor = mDb.rawQuery(sqlGetSingleNote(rowId), null);
			setNote(mCursor, true);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());

		}

		return mNote;
	}

	/**
	 * 
	 * @param mCursor
	 * @param isMore
	 *            more info like alarm,image eg.
	 */
	private void setNote(Cursor mCursor, boolean isMore) {
		if (mCursor != null && mCursor.moveToFirst()) {
			mNote = new Note();
			mNote.setId(mCursor.getLong(mCursor
					.getColumnIndexOrThrow(KEY_ROWID)));
			mNote.setTitle(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_TITLE)));
			mNote.setWeather(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_WEATHER)));
			mNote.setBody(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_BODY)));
			mNote.setCreated(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_CREATED)));
			mNote.setYyyymm(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_YYYYMM)));
			mNote.setHhmm(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_HHMM)));
			mNote.setAddDatetime(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_ADD_DATETIME)));
			mNote.setUpdDatetime(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_UPD_DATETIME)));
			mNote.setIsStared(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_IS_STARED)));
			mNote.setIsDeleted(mCursor.getString(mCursor
					.getColumnIndexOrThrow(KEY_IS_DELETED)));
			mNote.setShowIndex(mCursor.getLong(mCursor
					.getColumnIndexOrThrow(KEY_SHOW_INDEX)));

			if (isMore) {
				mNote.setYearAlarm(mCursor.getInt(mCursor
						.getColumnIndexOrThrow(AT_YEAR)));
				mNote.setMonthAlarm(mCursor.getInt(mCursor
						.getColumnIndexOrThrow(AT_MONTH)));
				mNote.setDayAlarm(mCursor.getInt(mCursor
						.getColumnIndexOrThrow(AT_DAY)));
				mNote.setHourAlarm(mCursor.getInt(mCursor
						.getColumnIndexOrThrow(AT_HOUR)));
				mNote.setMinuteAlarm(mCursor.getInt(mCursor
						.getColumnIndexOrThrow(AT_MINUTE)));
			}
		}
	}

	/**
	 * update the note
	 * 
	 * @param rowId
	 * @param title
	 * @param weather
	 * @param body
	 * @param isStared
	 * @param images
	 * @return
	 */
	public boolean updateNote(long rowId, String title, String weather,
			String body, String isStared, List<byte[]> images) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_WEATHER, weather);
		args.put(KEY_BODY, body);
		args.put(KEY_CREATED, DataTypeUtil.getCurrentDateStrByFormat());
		args.put(KEY_HHMM, DataTypeUtil.getCurrentHM());
		args.put(KEY_UPD_DATETIME, DataTypeUtil.getCurrentTimeStrByFormat());
		args.put(KEY_IS_STARED, isStared);
		boolean upd = mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;

		if (images != null && images.size() > 0) {
			mDb.delete(IMAGE_TABLE, IK_NOTE_ROWID + "=" + rowId, null);
			for (int i = 0; i < images.size(); i++) {
				ContentValues cv = new ContentValues();
				cv.put(IK_NOTE_ROWID, rowId);
				cv.put(IK_BITMAP, images.get(i));
				cv.put(IK_BITMAP_CNT, images.size());
				cv.put(IK_SHOW_INDEX, i);
				mDb.insert(IMAGE_TABLE, null, cv);
			}
		}

		return upd;
	}

	// ////////////////////////////alarm table operate
	/**
	 * update the alarm of one note
	 * 
	 * @param rowId
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param v
	 * @return
	 */
	public boolean updateAlarm(long rowId, int year, int month, int day,
			int hour, int minute) {

		mDb.delete(ALARM_TABLE, AT_NOTE_ROWID + "=" + rowId, null);
		ContentValues cv = new ContentValues();
		cv.put(AT_NOTE_ROWID, rowId);
		cv.put(AT_YEAR, year);
		cv.put(AT_MONTH, month);
		cv.put(AT_DAY, day);
		cv.put(AT_HOUR, hour);
		cv.put(AT_MINUTE, minute);
		mDb.insert(ALARM_TABLE, null, cv);
		return true;
	}

	public boolean deleteAlarm(long rowId) {
		boolean delAlarm = mDb.delete(ALARM_TABLE, AT_NOTE_ROWID + "=" + rowId,
				null) > 0;

		return delAlarm;

	}

	public Cursor getAlarm(long rowId) {
		Cursor ca = null;
		try {
			ca = mDb.query(ALARM_TABLE, new String[] { AT_ROWID, AT_NOTE_ROWID,
					AT_YEAR, AT_MONTH, AT_DAY, AT_HOUR, AT_MINUTE },
					AT_NOTE_ROWID + "=" + rowId, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());

		}
		if (ca != null) {
			ca.moveToFirst();
		}
		return ca;
	}

	// ////////////////////////////check table operate
	/**
	 * update one check item of one note
	 * 
	 * @param checkId
	 * @param itemTitle
	 * @return
	 */
	public boolean updateCheck(long checkId, String itemTitle) {

		return true;
	}

	/**
	 * update the check list of one note
	 * 
	 * @param checkId
	 * @param checkList
	 * @return
	 */
	public boolean updateCheckList(long checkId, List<Object> checkList) {
		boolean rtn = deleteCheckItem(checkId);
		for (int i = 0; i < checkList.size(); i++) {
			mDb.insert(CHECK_TABLE, null, (ContentValues) checkList.get(i));
		}
		return rtn;
	}

	public boolean deleteCheckItem(long checkId) {
		boolean delAlarm = mDb.delete(CHECK_TABLE, CT_NOTE_ROWID + "="
				+ checkId, null) > 0;

		return delAlarm;

	}

	/**
	 * get all check list items of single note
	 * 
	 * @param rowId
	 * @return
	 */
	public Cursor getCheckItems(long rowId) {
		Cursor ca = null;
		try {
			ca = mDb.query(CHECK_TABLE, new String[] { CT_ROWID, CT_NOTE_ROWID,
					CT_ITEM_TITLE, CT_ITEM_CHECKED, CT_SHOW_INDEX, CT_SCORE },
					CT_NOTE_ROWID + "=" + rowId, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());

		}
		return ca;
	}

	public List<Object> getCheckList(long rowId) {
		Cursor c = this.getCheckItems(rowId);
		List<Object> _list = new ArrayList<Object>();
		if (c != null && c.getCount() > 0) {
			ContentValues cv = new ContentValues();
			for (int i = 0; i < c.getCount(); i++) {
				cv = new ContentValues(); // 当前记录

				// 取得当前记录
				c.moveToPosition(i);
				long noteId = c.getLong(c
						.getColumnIndexOrThrow(NoteDbAdapter.CT_NOTE_ROWID));
				String itemTitle = c.getString(c
						.getColumnIndexOrThrow(NoteDbAdapter.CT_ITEM_TITLE));
				int checked = c.getInt(c
						.getColumnIndexOrThrow(NoteDbAdapter.CT_ITEM_CHECKED));
				int showIndex = c.getInt(c
						.getColumnIndexOrThrow(NoteDbAdapter.CT_SHOW_INDEX));
				int score = c.getInt(c
						.getColumnIndexOrThrow(NoteDbAdapter.CT_SCORE));

				cv.put(NoteDbAdapter.CT_NOTE_ROWID, noteId);
				cv.put(NoteDbAdapter.CT_ITEM_TITLE, itemTitle);
				cv.put(NoteDbAdapter.CT_ITEM_CHECKED, checked);
				cv.put(NoteDbAdapter.CT_SHOW_INDEX, showIndex);
				cv.put(NoteDbAdapter.CT_SCORE, score);
				_list.add(cv);
			}
		}
		return _list;
	}

	// ///////////////////////////////other table operate
	/**
	 * 加星标
	 * 
	 * @param rowId
	 * @param isStared
	 * @return
	 */
	public boolean updateStar(long rowId, String isStared) {
		ContentValues args = new ContentValues();
		args.put(KEY_IS_STARED, isStared);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * 置顶
	 * 
	 * @param rowId
	 * @return
	 */
	public boolean updateTop(long rowId) {

		boolean rtn = true;

		int showIndex = 999;
		Note n = this.getSingleNote(rowId);
		if (n != null && n.getShowIndex() > 0) {
			showIndex = 0;
			rtn = false;
		}

		ContentValues args = new ContentValues();
		args.put(KEY_SHOW_INDEX, showIndex);

		mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null);

		return rtn;
	}

	/**
	 * 逻辑删除
	 * 
	 * @param rowId
	 * @param isDeleted
	 * @return
	 */
	public boolean updateDelete(long rowId, String isDeleted) {
		ContentValues args = new ContentValues();
		args.put(KEY_IS_DELETED, isDeleted);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean dropDb() {

		try {
			mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			mDb.execSQL(DATABASE_CREATE);

			mDb.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
			mDb.execSQL(DATABASE_CREATE_IMAGE);

			mDb.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE);
			mDb.execSQL(DATABASE_CREATE_ALARM);

			mDb.execSQL("DROP TABLE IF EXISTS " + CHECK_TABLE);
			mDb.execSQL(DATABASE_CREATE_CHECK);

			mDb.execSQL("DROP TABLE IF EXISTS " + BOOKMARK_TABLE);
			mDb.execSQL(DATABASE_CREATE_BOOKMARK);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("dropDb-exception", e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 使用情况
	 * 
	 * @return
	 */
	public Cursor measureUsage() {
		Cursor ca = null;
		try {
			String sql = "select (" + "select count(0) || ' 条笔记, ' from "
					+ DATABASE_TABLE
					+ ") || (select count(0) || ' 张图片, ' from " + IMAGE_TABLE
					+ ") || (select count(0) || ' 个星标, ' from "
					+ DATABASE_TABLE + " where " + KEY_IS_STARED
					+ "=\"1\") || (select count(0) || ' 个置顶, ' from "
					+ DATABASE_TABLE + " where " + KEY_SHOW_INDEX
					+ ">0) || (select count(0) || ' 坨纸团' from "
					+ DATABASE_TABLE + " where " + KEY_IS_DELETED
					+ "=\"1\") as usage_info";
			ca = mDb.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		}

		return ca;
	}

	/**
	 * merge the notes
	 * 
	 * @param checkedIds
	 */
	public boolean merge(List<Long> checkedIds) {
		if (checkedIds == null || checkedIds.size() < 2)
			return false;
		Object[] p = new Object[checkedIds.size()];
		String sqlWhere = " where " + CT_NOTE_ROWID + "=?";
		for (int i = 0; i < checkedIds.size(); i++) {
			p[i] = checkedIds.get(i);
			if (i > 1)
				sqlWhere += " or " + CT_NOTE_ROWID + "=?";
		}

		String columns = CT_NOTE_ROWID + "," + CT_ITEM_CHECKED + ","
				+ CT_ITEM_TITLE + "," + CT_SCORE;
		String sql = "insert into " + CHECK_TABLE + " (" + columns
				+ ") select ? as " + columns + " from " + CHECK_TABLE
				+ sqlWhere;
		try {
			mDb.execSQL(sql, p);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * copy note data
	 * 
	 * @param rowId
	 * @param copyCheckTable
	 * @param copyAlarmTable
	 * @param copyBookMarkTable
	 * @return
	 */
	public void copyNote(Long rowId, boolean copyCheckTable,
			boolean copyAlarmTable, boolean copyBookMarkTable) {
		// copy note table
		Note original = this.getSingleNote(rowId);
		Long newRowId = this.createNote(original.getTitle(),
				original.getWeather(), original.getBody(),
				original.getIsStared(), this.getByteImages(rowId));

		// copy check table
		if (copyCheckTable) {
			String columns = CT_NOTE_ROWID + "," + CT_ITEM_CHECKED + ","
					+ CT_ITEM_TITLE + "," + CT_SCORE;
			copyTableByRowId(CHECK_TABLE, rowId, newRowId, columns);
		}

		// copy alarm table
		if (copyAlarmTable) {
			String columns = AT_NOTE_ROWID + "," + AT_YEAR + "," + AT_MONTH
					+ "," + AT_DAY + "," + AT_HOUR + "," + AT_MINUTE;
			copyTableByRowId(ALARM_TABLE, rowId, newRowId, columns);
		}
		// copy bookmark table
		if (copyBookMarkTable) {
			String columns = BT_NOTE_ROWID + "," + BT_BOOKMARK + ","
					+ KEY_CREATED + "," + KEY_YYYYMM + "," + KEY_HHMM + ","
					+ KEY_ADD_DATETIME + "," + KEY_UPD_DATETIME;
			copyTableByRowId(BOOKMARK_TABLE, rowId, newRowId, columns);
		}

	}

	private void copyTableByRowId(String tableName, long rowId, long newRowId,
			String columns) {
		String sql = "insert into " + tableName + " (" + columns
				+ ") select ? as " + columns + " from " + tableName + " where "
				+ IK_NOTE_ROWID + "=?";
		try {
			mDb.execSQL(sql, new Object[] { newRowId, rowId });
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		}
	}

	// ////////////////////////////bookmark table operate

	/**
	 * when the VIRTUAL table of fts3 is query object ,the String selectionArgs
	 * dose note work on the integer column! but the normal real table can do
	 * it!
	 * 
	 * @param rowId
	 * @return
	 */
	public Cursor getBookMark(long rowId) {
		Cursor ca = null;
		try {
			ca = mDb.rawQuery("select * from " + BOOKMARK_TABLE + " where "
					+ BT_NOTE_ROWID + "=" + rowId, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, e.getMessage());
		}

		return ca;
	}

	/**
	 * update the bookmark of note
	 * 
	 * @param rowId
	 * @param bookmark
	 * @return
	 */
	public boolean updateBookMark(long rowId, String bookmark) {
		mDb.delete(BOOKMARK_TABLE, BT_NOTE_ROWID + "=" + rowId, null);
		ContentValues cv = new ContentValues();
		cv.put(BT_NOTE_ROWID, rowId);
		cv.put(BT_BOOKMARK, bookmark);
		mDb.insert(BOOKMARK_TABLE, null, cv);
		return true;
	}

	// /////////////////////////////search widget
	/**
	 * http://developer.android.com/guide/topics/search/search-dialog.html#
	 * UsingSearchWidget </br> If your data is stored in a SQLite database on
	 * the device, performing a full-text search (using FTS3, rather than a LIKE
	 * query) can provide a more robust search across text data and can produce
	 * results significantly faster. See sqlite.org for information about FTS3
	 * and the SQLiteDatabase class for information about SQLite on Android.
	 * Also look at the Searchable Dictionary sample application to see a
	 * complete SQLite implementation that performs searches with FTS3. </br>
	 * http://sqlite.org/fts3.html
	 * 
	 * @param key
	 */
	public void doSearchByFTS3(String key) {

	}

	// set n get
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getItemShowCount() {
		return itemShowCount;
	}

	public void setItemShowCount(int itemShowCount) {
		this.itemShowCount = itemShowCount;
	}

	public int getItemTotalCount() {
		return itemTotalCount;
	}

	public void setItemTotalCount(int itemTotalCount) {
		this.itemTotalCount = itemTotalCount;
	}

}
