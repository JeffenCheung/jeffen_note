package com.jeffen.note;

import java.util.Timer;
import java.util.TimerTask;

import com.jeffen.pojo.Option;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Jeffen Cheung
 * 
 */
public class MainActivity extends Activity {
	private static final int ACTIVITY_NOTE_LIST = 0;

	private Timer mTimer = null;
	private TimerTask mTimerTask = null;

	private static int delay = 1000; // 1s
	private static int period = 1000; // 1s

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 获取SharedPreferences对象
		SharedPreferences sp = this.getSharedPreferences(Option.SP,
				MODE_PRIVATE);
		if (!sp.getBoolean(Option.IS_COVER, true)) {
			gotoNoteList();
			return;
		}

		View m = (View) findViewById(R.id.main_layout);
		m.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// gotoNoteList();

			}
		});

		startTimer();

	}

	/**
	 * 跳转到主页面
	 */
	public void gotoNoteList() {
		Intent i = new Intent(this, NoteListActivity.class);
		startActivityForResult(i, ACTIVITY_NOTE_LIST);
	}

	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					gotoNoteList();
					stopTimer();
				}
			};
		}

		if (mTimer != null && mTimerTask != null)
			mTimer.schedule(mTimerTask, delay, period);

	}

	private void stopTimer() {

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

	}
}
