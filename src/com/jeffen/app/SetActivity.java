package com.jeffen.app;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.SearchRecentSuggestions;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeffen.note.NoteDbAdapter;
import com.jeffen.note.R;
import com.jeffen.pojo.Option;
import com.util.CommonCheck;
import com.util.SearchSuggestionsProvider;

public class SetActivity extends Activity {

	private NoteDbAdapter mDbHelper;
	private Cursor mNoteCursor;
	private Context mContext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_set);

		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();
		mContext = this;

		TextView tvMemorySize1 = (TextView) findViewById(R.id.tvMemorySize1);
		TextView tvMemorySize2 = (TextView) findViewById(R.id.tvMemorySize2);
		TextView tvMemorySize3 = (TextView) findViewById(R.id.tvMemorySize3);

		String total = fileSizeStr(getTotalExternalMemorySize());
		String available = fileSizeStr(getAvailableExternalMemorySize());

		String text = "SD Card: �ܹ� " + total + ", ";
		text += "���� " + available;
		tvMemorySize1.setText(text);

		total = fileSizeStr(getTotalInternalMemorySize());
		available = fileSizeStr(getAvailableInternalMemorySize());
		text = "Mobile: �ܹ� " + total + ", ";
		text += "���� " + available;
		tvMemorySize2.setText(text);

		String usage_info = "Runtime Memory: " + fileSizeStr(runtimeMemory())
				+ "\n";
		usage_info += "JeffenNote App Memory: "
				+ fileSizeStr(getThisProcessMemeryInfo()) + "\n\n";
		mNoteCursor = mDbHelper.measureUsage();
		if (mNoteCursor != null && mNoteCursor.getCount() > 0) {
			mNoteCursor.moveToPosition(0);
			usage_info += mNoteCursor.getString(mNoteCursor
					.getColumnIndexOrThrow("usage_info"));
		}
		tvMemorySize3.setText(usage_info);

		// �������趨����ӭҳ/////////////////////////
		final CheckBox cbCover = (CheckBox) findViewById(R.id.cbCover);
		// ��ȡSharedPreferences����
		final SharedPreferences sp = this.getSharedPreferences(Option.SP,
				MODE_PRIVATE);
		cbCover.setChecked(sp.getBoolean(Option.IS_COVER, true));
		OnClickListener oclCover = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��������
				Editor editor = sp.edit();
				editor.putBoolean(Option.IS_COVER, cbCover.isChecked());
				editor.commit();
			}
		};
		cbCover.setOnClickListener(oclCover);
		LinearLayout llCover = (LinearLayout) findViewById(R.id.llCover);
		llCover.setOnClickListener(oclCover);

		// �������趨��������/////////////////////////
		final CheckBox cbVibrator = (CheckBox) findViewById(R.id.cbVibrator);
		cbVibrator.setChecked(sp.getBoolean(Option.IS_VIBRATOR, false));
		OnClickListener ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��������
				Editor editor = sp.edit();
				editor.putBoolean(Option.IS_VIBRATOR, cbVibrator.isChecked());
				editor.commit();
			}
		};
		cbVibrator.setOnClickListener(ocl);
		LinearLayout ll = (LinearLayout) findViewById(R.id.llVibrator);
		ll.setOnClickListener(ocl);

		// �������趨����ʾ֪ͨ/////////////////////////
		final CheckBox cbNotification = (CheckBox) findViewById(R.id.cbNotification);
		cbNotification.setChecked(sp.getBoolean(Option.NOTIFICATION, false));
		OnClickListener oclNotification = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��������
				Editor editor = sp.edit();
				editor.putBoolean(Option.NOTIFICATION,
						cbNotification.isChecked());
				editor.commit();
			}
		};
		cbNotification.setOnClickListener(oclNotification);

		// �������趨����ʾֽ��/////////////////////////
		final CheckBox cbPaperBall = (CheckBox) findViewById(R.id.cbPaperBall);
		cbPaperBall.setChecked(sp.getBoolean(Option.PAPER_BALL, false));
		OnClickListener oclPb = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��������
				Editor editor = sp.edit();
				editor.putBoolean(Option.PAPER_BALL, cbPaperBall.isChecked());
				editor.commit();
			}
		};
		cbPaperBall.setOnClickListener(oclPb);

		// �������趨���Զ����ظ���/////////////////////////
		final CheckBox cbAutoLoad = (CheckBox) findViewById(R.id.cbAutoLoad);
		cbAutoLoad.setChecked(sp.getBoolean(Option.AUTO_LOAD, false));
		OnClickListener oclAL = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��������
				Editor editor = sp.edit();
				editor.putBoolean(Option.AUTO_LOAD, cbAutoLoad.isChecked());
				editor.commit();
			}
		};
		cbAutoLoad.setOnClickListener(oclAL);

		// �������趨��ÿҳ��Ŀ/////////////////////////
		final EditText etLimit = (EditText) findViewById(R.id.etLimit);
		etLimit.setText(String.valueOf(sp.getInt(Option.LIMIT, 5)));
		etLimit.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				int i = 5;
				String txt = etLimit.getText().toString();
				if (CommonCheck.isEmpty(txt)) {
					return;
				}
				try {
					i = Integer.valueOf(txt);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// ��������
				Editor editor = sp.edit();
				editor.putInt(Option.LIMIT, i);
				editor.commit();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		// clear suggestions
		Button btnClearSuggestions = (Button) this
				.findViewById(R.id.btnClearSuggestions);
		btnClearSuggestions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SearchRecentSuggestions mSuggestions = new SearchRecentSuggestions(
						mContext, SearchSuggestionsProvider.AUTHORITY,
						SearchSuggestionsProvider.MODE);
				mSuggestions.clearHistory();
			}
		});

	}

	// ������ֻ��ڴ���ܿռ��С
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	// ������ֻ��ڴ�Ŀ��ÿռ��С
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	// ������ⲿ�洢���ܿռ��С
	public static long getAvailableExternalMemorySize() {
		long availableExternalMemorySize = 0;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			availableExternalMemorySize = availableBlocks * blockSize;
		} else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_REMOVED)) {
			availableExternalMemorySize = -1;

		}

		return availableExternalMemorySize;
	}

	// ������ⲿ�洢���ܿռ��С
	public static long getTotalExternalMemorySize() {
		long totalExternalMemorySize = 0;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			totalExternalMemorySize = totalBlocks * blockSize;
		} else if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_REMOVED)) {
			totalExternalMemorySize = -1;

		}

		return totalExternalMemorySize;
	}

	/**
	 * file size
	 * 
	 * @param size
	 * @return
	 */
	private String fileSizeStr(long size) {
		String[] fs = fileSize(size);
		return fs[0] + fs[1];
	}

	/* ����Ϊ�ַ�������[0]Ϊ��С[1]Ϊ��λKB��MB */
	private String[] fileSize(long size) {
		String str = "";
		if (size >= 1024) {
			str = "KB";
			size /= 1024;
			if (size >= 1024) {
				str = "MB";
				size /= 1024;
				if (size >= 1024) {
					str = "GB";
					size /= 1024;
				}
			}
		}
		DecimalFormat formatter = new DecimalFormat();
		/* ÿ3��������,�ָ��磺1,000 */
		formatter.setGroupingSize(3);
		String result[] = new String[2];
		result[0] = formatter.format(size);
		result[1] = str;
		return result;
	}

	/**
	 * Ӧ�ó�����ռ�õ��ڴ�Ĵ�С
	 * 
	 * @return
	 */
	private long runtimeMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Ӧ�ó�����ռ�õ��ڴ�Ĵ�С
	 * 
	 * @return
	 */
	private long getThisProcessMemeryInfo() {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager
				.getProcessMemoryInfo(new int[] { pid });
		return memoryInfoArray[0].getTotalPrivateDirty();
	}
}
