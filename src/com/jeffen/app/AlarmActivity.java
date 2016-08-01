package com.jeffen.app;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jeffen.note.NoteDbAdapter;
import com.jeffen.note.NoteListActivity;
import com.jeffen.note.R;
import com.jeffen.sys.AlarmReceiver;
import com.util.DataTypeUtil;

@SuppressLint("NewApi")
public class AlarmActivity extends Activity {
	protected static final int ACTIVITY_LIST = 100;
	protected static final int ACTIVITY_EDIT = 101;
	protected NoteDbAdapter mDbHelper;
	protected Long mRowId;
	protected String mTitle;

	private DatePicker dp;
	private TimePicker tp;

	private int year;
	private int monthOfYear;
	private int dayOfMonth;

	private int currentHour;
	private int currentMinute;

	public static AlarmManager alarmManager;
	public static Map<Long, PendingIntent> pendingIntentMap = new HashMap<Long, PendingIntent>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("JeffenNote Alarm");
		setContentView(R.layout.alarm_time);

		// home button to return
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();
		mRowId = null;

		dp = (DatePicker) findViewById(R.id.datePicker1);
		tp = (TimePicker) findViewById(R.id.timePicker1);

		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// 数据库查询
			mRowId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			mTitle = mDbHelper.getSingleNote(mRowId).getTitle();

			Cursor c = mDbHelper.getAlarm(mRowId);
			if (c != null && c.moveToFirst()) {

				year = Integer.valueOf(DataTypeUtil
						.getCurrentTimeStrByFormat("yyyy"));// 当前年
				year = c.getInt(c.getColumnIndexOrThrow(mDbHelper.AT_YEAR));
				monthOfYear = Integer.valueOf(DataTypeUtil
						.getCurrentTimeStrByFormat("M"));// 当前月
				monthOfYear = c.getInt(c
						.getColumnIndexOrThrow(mDbHelper.AT_MONTH));
				dayOfMonth = Integer.valueOf(DataTypeUtil
						.getCurrentTimeStrByFormat("d"));// 当前日
				dayOfMonth = c
						.getInt(c.getColumnIndexOrThrow(mDbHelper.AT_DAY));
				dp.init(year, monthOfYear, dayOfMonth, null);

				currentHour = Integer.valueOf(DataTypeUtil
						.getCurrentTimeStrByFormat("HH"));// 当前时
				currentHour = c.getInt(c
						.getColumnIndexOrThrow(mDbHelper.AT_HOUR));
				currentMinute = Integer.valueOf(DataTypeUtil
						.getCurrentTimeStrByFormat("mm"));// 当前分
				currentMinute = c.getInt(c
						.getColumnIndexOrThrow(mDbHelper.AT_MINUTE));
				tp.setCurrentHour(currentHour);
				tp.setCurrentMinute(currentMinute);
				tp.setIs24HourView(false);
			}

		}

		Button openButton = (Button) findViewById(R.id.btnOpen);
		Button closeButton = (Button) findViewById(R.id.btnClose);
		openButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onSubmitProcess(1);

			}

		});

		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				onSubmitProcess(0);
			}
		});
	}

	/**
	 * action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onCancelPorcess();
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}

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
	 * 提交
	 * 
	 * @param i
	 */
	private void onSubmitProcess(int i) {
		getDateTime();
		if (mRowId != null) {
			if (alarmManager == null) {
				alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			}
			if (i == 1) {
				openAlarm();
			} else {
				closeAlarm();
			}
		}

		Intent mIntent = new Intent(this, NoteListActivity.class);
		setResult(RESULT_OK, mIntent);
		finish();

		startActivityForResult(mIntent, ACTIVITY_LIST);
	}

	public void closeAlarm() {
		mDbHelper.deleteAlarm(mRowId);

		// cancel alarm
		alarmManager.cancel(pendingIntentMap.get(mRowId));

		Toast.makeText(this, "Alarm closed!", Toast.LENGTH_SHORT).show();
	}

	public void openAlarm() {
		mDbHelper.updateAlarm(mRowId, year, monthOfYear, dayOfMonth,
				currentHour, currentMinute);

		// set alarm
		// why the time of service called is wrong!
		// Intent myIntent = new Intent(this, MyAlarmService.class);
		// pendingIntent = PendingIntent.getService(this, 0, myIntent,
		// 0);
		Intent myIntent = new Intent(this, AlarmReceiver.class);
		myIntent.putExtra(NoteDbAdapter.KEY_ROWID, mRowId);
		myIntent.putExtra("DATE_TIME", year + "-" + monthOfYear + "-"
				+ (dayOfMonth + 1) + " " + currentHour + ":" + currentMinute);
		myIntent.putExtra("TITLE", mTitle);

		pendingIntentMap.put(mRowId,
				PendingIntent.getBroadcast(this, 0, myIntent, 0));
		alarmManager.cancel(pendingIntentMap.get(mRowId));

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(year, monthOfYear, dayOfMonth, currentHour,
				currentMinute - 3);
		calendar.add(Calendar.SECOND, 0);

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pendingIntentMap.get(mRowId));
		// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		// calendar.getTimeInMillis(), 10 * 1000, pendingIntent);

		Toast.makeText(this, "Alarm Set Successfully!", Toast.LENGTH_SHORT)
				.show();
	}

	private void getDateTime() {
		year = dp.getYear();
		monthOfYear = dp.getMonth();
		dayOfMonth = dp.getDayOfMonth();

		currentHour = tp.getCurrentHour();
		currentMinute = tp.getCurrentMinute();
	}

}
