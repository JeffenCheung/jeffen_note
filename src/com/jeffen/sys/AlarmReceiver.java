package com.jeffen.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jeffen.app.AlarmActivity;
import com.jeffen.note.NoteDbAdapter;
import com.jeffen.note.NoteEditActivity;

public class AlarmReceiver extends BroadcastReceiver {
	protected static final int ACTIVITY_LIST = 100;
	protected static final int ACTIVITY_EDIT = 101;

	protected Context mContext;
	protected NoteDbAdapter mDbHelper;
	protected Long mRowId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context ctx, Intent i) {
		Log.d("AlarmReceiver", "the time is up,start the alarm...");
		mContext = ctx;

		//Toast.makeText(ctx, "闹钟时间到了！", Toast.LENGTH_LONG).show();

		// 每一个intent都会带一个Bundle型的extras数据。
		Bundle extras = i.getExtras();
		if (extras != null) {

			mRowId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			String dateTime = extras.getString("DATE_TIME");
			String title = extras.getString("TITLE");
			//Toast.makeText(ctx, "[" + dateTime + "]" + title, Toast.LENGTH_LONG)
			//		.show();
			goToNoteEdt(mRowId);
		}
	}
	

	/**
	 * 跳转更新页面
	 * 
	 * @param rowId
	 */
	public void goToNoteEdt(long rowId) {
		Intent i = new Intent(mContext, NoteEditActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, rowId);
		i.putExtra("IS_ALARM", true);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);
	}
}