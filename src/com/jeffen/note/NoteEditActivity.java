/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.savedInstanceState
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jeffen.note;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffen.app.AlarmActivity;
import com.jeffen.pojo.MLocation;
import com.jeffen.pojo.Note;
import com.jeffen.pojo.Option;
import com.jeffen.sys.CityLocationListener;
import com.jeffen.sys.MyLocation;
import com.jeffen.sys.MyLocation.LocationCallBack;
import com.util.CommonCheck;
import com.util.DataTypeUtil;
import com.widget.image.ImageGridAdapter;
import com.widget.star.StarButton;
import com.widget.star.StarButtonOnClickListener;
import com.widget.star.StarButtonOnLongClickListener;

/**
 * @author Jeffen Cheung
 * 
 */
public class NoteEditActivity extends NoteActivity implements LocationCallBack,
		OnGestureListener {

	public static final String TAG = "NoteEditActivity";

	public static final int PHOTOGRAPH = 1;// ����
	public static final int PHOTOZOOM = 2; // ����
	public static final int PHOTORESOULT = 3;// ���
	public static final int NOTIFICATION = 99;// ֪ͨ

	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final String IMAGE_PATH = "/sdcard/jeffenNoteImage/";

	private LocationManager mlocManager;
	private CityLocationListener mlocListene;
	private Ringtone ringtone;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// home button to return
		ActionBar actionBar = getActionBar();
		// actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// or Intent.FLAG_ACTIVITY_NEW_TASK
		getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.note_edit);

		mStarButton = (StarButton) findViewById(R.id.starButton);
		mTitleText = (EditText) findViewById(R.id.noteTitleET);
		mNoteWeather = (TextView) findViewById(R.id.noteWeather);
		mBodyText = (EditText) findViewById(R.id.noteBodyET);
		mTvProcessing = (TextView) findViewById(R.id.tvMsgProcessing);

		// ͼƬ��
		mPhotoGrid = (GridView) findViewById(R.id.photoGrid);
		mImageAdapter = new ImageGridAdapter(this);

		Button confirmButton = (Button) findViewById(R.id.btnSubmitNote);
		Button cancelButton = (Button) findViewById(R.id.btnCancelNote);

		mRowId = null;
		// ÿһ��intent�����һ��Bundle�͵�extras���ݡ�
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			if (extras.getBoolean("FROM_NOTIFICATION")) {
				// remove the notification at
				removeNotification();
			}

			// ���ݿ��ѯ
			mRowId = extras.getLong(NoteDbAdapter.KEY_ROWID);
			Note n = mDbHelper.getSingleItemNote(mRowId);

			if (n == null) {
				Toast.makeText(this, "<NG> The Note[" + mRowId + "] is gone!",
						Toast.LENGTH_LONG).show();
				onCancelPorcess();
				return;
			}
			if (CommonCheck.isNotEmpty(n.getTitle())) {
				mTitleText.setText(n.getTitle());
			}
			if (CommonCheck.isNotEmpty(n.getWeather())) {
				mNoteWeather.setText(n.getWeather());
			}
			if (CommonCheck.isNotEmpty(n.getBody())) {
				mBodyText.setText(n.getBody());
			}

			if (CommonCheck.isNotEmpty(n.getIsStared())) {
				mStarButton.setImageResource(R.drawable.star_pink);
			}

			// ȡ�ò��趨ͼƬ��
			mImageAdapter.synByte2Bit(mPhotoGrid,
					mDbHelper.getByteImages(mRowId));

			// alarm hint
			if (n.getYearAlarm() > 0) {
				mStarButton.setBackgroundResource(R.drawable.alarm);

			}

			mStarButton
					.setOnLongClickListener(new StarButtonOnLongClickListener(
							mRowId) {

						@Override
						public boolean onLongClick(View v) {
							alarm(this.getRowId());
							return true;
						}

					});

			if (extras.getBoolean("IS_ALARM")) {

				// Android activity over default lock screen
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

				// �ر������
				mTitleText.setInputType(InputType.TYPE_NULL);
				mBodyText.setInputType(InputType.TYPE_NULL);

				// play the ringtone
				ringtone = RingtoneManager.getRingtone(getApplicationContext(),
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_ALARM));
				if (ringtone != null) {
					ringtone.play();
				}

				View noteAlarm = LayoutInflater.from(this).inflate(
						R.layout.note_alarm,
						(ViewGroup) findViewById(R.id.llNoteAlarmBlank));
				final Button stopAlarm = (Button) noteAlarm
						.findViewById(R.id.btnStopAlarm);
				stopAlarm.setVisibility(View.VISIBLE);
				confirmButton.setVisibility(View.INVISIBLE);
				cancelButton.setVisibility(View.INVISIBLE);
				stopAlarm.setOnClickListener(new StarButtonOnClickListener(
						mRowId, null) {
					@Override
					public void onClick(View v) {
						ringtone.stop();

						mStarButton.setBackground(null);

						stopAlarm.setText("���������ѹر�");
						stopAlarm.setEnabled(false);

						mDbHelper.deleteAlarm(mRowId);

					}
				});
			}
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onConfirmPorcess();

			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				onCancelPorcess();
			}
		});

		/**
		 * ���Ǳ�
		 */
		mStarButton.setOnClickListener(new StarButtonOnClickListener(-1, null) {

			@Override
			public void onClick(View v) {
				super.onClick(v);

				// �ύ
				onConfirmPorcess();

			}
		});

		if (mRowId == null) {
			// new GetCity(this, this);
			new MyLocation(this, this);
		}

		final NoteEditActivity act = this;
		mNoteWeather.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mNoteWeather.setText("������ȡ��...");
				new MyLocation(act, act);

			}
		});

	}

	// ����ȡ�ûص�����
	public void onCurrentCity(String city) {
		mBodyText.setText(city);

	}

	// ��γ��ȡ�ûص�����
	public void onCurrentLocation(final MLocation mLocation, String pipe) {
		if (mLocation == null)
			return;
		String weather = "��ǰ���У�" + mLocation.getCity() + "\n��ǰ������"
				+ mLocation.getWeather() + "\n��ǰ���ȣ�" + mLocation.getLongitude()
				+ "\n��ǰγ�ȣ�" + mLocation.getLatitude() + "\n(" + pipe + ")";

		/**
		 * ����ԭ��������runOnUiThread() <br/>
		 * Do not block the UI thread ����Ҫ����UI�̣߳� Do not access the Android UI
		 * toolkit from outside the UI thread ����Ҫ�ڹ����߳��в���UIԪ��
		 */
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				mNoteWeather.setText(Html.fromHtml("<u>"
						+ mLocation.getWeather() + "</u>"));
			}
		});
	}

	/**
	 * ���水ť
	 */
	public void onConfirmPorcess() {
		// ������
		mTvProcessing.setVisibility(View.VISIBLE);

		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		String weather = mNoteWeather.getText().toString();
		String isStared = "";
		if (mStarButton.getImageResourceId() == R.drawable.star_pink) {
			isStared = "1";
		}
		if (mRowId != null) {
			mDbHelper.updateNote(mRowId, title, weather, body, isStared,
					mImageAdapter.mByteImages);
		} else
			mRowId = mDbHelper.createNote(title, weather, body, isStared,
					mImageAdapter.mByteImages);

		// show customize notification
		// showCustomizeNotification(mRowId, title, weather, body, isStared);
		// show default notification
		showDefaultNotification(title, body, isStared);

		// ����ɹ�
		Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();

		Intent mIntent = new Intent(this, NoteListActivity.class);
		setResult(RESULT_OK, mIntent);
		finish();

		startActivityForResult(mIntent, ACTIVITY_LIST);
	}

	/**
	 * ����
	 */
	public void alarm(long noteId) {
		Intent i = new Intent(this, AlarmActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, noteId);
		startActivityForResult(i, ACTIVITY_ALARM);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// create action bar menus
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_edit, menu);

		return true;
	}

	/**
	 * action bar menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.note_edit_menu_save:
			onConfirmPorcess();
			return true;
		case R.id.note_edit_menu_camera:
			Intent iC = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(iC, PHOTOGRAPH);
			return true;
		case R.id.note_edit_menu_album:
			Intent iA = new Intent(Intent.ACTION_PICK, null);
			iA.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					IMAGE_UNSPECIFIED);
			startActivityForResult(iA, PHOTOZOOM);
			return true;
		case android.R.id.home:
			onCancelPorcess();
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PHOTOGRAPH:
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // ���sd�Ƿ����
					Log.v(TAG, "SD card is not avaiable/writeable right now.");
					Toast.makeText(this,
							"SD card is not avaiable/writeable right now.",
							Toast.LENGTH_SHORT).show();
					return;
				}

				File file = new File(IMAGE_PATH);
				file.mkdirs();// �����ļ���
				String fileName = "/"
						+ DataTypeUtil.getCurrentTimeStrByFormat() + ".jpg";
				file = new File(Environment.getExternalStorageDirectory()
						+ fileName);
				FileOutputStream b = null;

				// д�ļ�
				try {
					b = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ fileName);
					Bundle bundle = data.getExtras();
					Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// ������д���ļ�
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (b != null) {
							b.flush();
							b.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				startPhotoZoom(Uri.fromFile(file));
				return;
			case PHOTOZOOM:
				// ��ȡ�������ͼƬ
				startPhotoZoom(data.getData());
				return;
			case PHOTORESOULT:
				setImgView(data);
				return;
			}

		}
	}

	/**
	 * ��ʽ��ͼƬ
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			Log.v("cropImage", "Heigh=" + bitmap.getHeight() + " Width="
					+ bitmap.getWidth());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (bitmap == null) {

			Toast.makeText(this, "Image Error!", Toast.LENGTH_SHORT).show();
			return;
		}

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		// WindowManager manage = getWindowManager();
		// Display display = manage.getDefaultDisplay();
		// width = display.getWidth();
		// height = display.getHeight();

		if (width < 500 || height < 500) {
			if (width <= height) {
				intent.putExtra("outputX", width);
				intent.putExtra("outputY", width);
				Log.v("cropImage", "outputX=" + width + " outputY=" + width);
			} else {
				intent.putExtra("outputX", height);
				intent.putExtra("outputY", height);
				Log.v("cropImage", "outputX=" + height + " outputY=" + height);
			}
		} else {
			intent.putExtra("outputX", 360);
			intent.putExtra("outputY", 360);
		}

		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTORESOULT);

	}

	/**
	 * �趨ͼƬ
	 * 
	 * @param data
	 */
	private void setImgView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			byte[] b;
			// (0-100)ѹ���ļ�
			ByteArrayOutputStream stream = new ByteArrayOutputStream(
					photo.getWidth() * photo.getHeight());
			photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
			b = stream.toByteArray();
			// b = mImageAdapter.bitmapToByteArray(photo);
			mImageAdapter.addBitmapImage(mPhotoGrid, photo, b);

		}

	}

	/**
	 * ɾ��ͼƬ,called by adapter
	 * 
	 * @param position
	 */
	public void removeImage(int position) {
		mImageAdapter.removeBitmapImage(mPhotoGrid, position);
	}

	// /////////////on key event
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// �Զ�����
			onConfirmPorcess();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.d(TAG, "onFling - velocityX:" + velocityX + ",velocityX:"
				+ velocityY);
		if (e1.getX() > e2.getX()) {// move to left
			onConfirmPorcess();
		} else if (e1.getX() < e2.getX()) {
			return false;
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	// /////////////////////////////////////// �Զ�����ʾ��֪ͨ ������RemoteView����
	@SuppressWarnings("deprecation")
	public void showCustomizeNotification(long rowId, String title,
			String weather, String body, String isStared) {

		long when = System.currentTimeMillis();
		Notification noti = new Notification(R.drawable.ic_launcher, this
				.getResources().getString(R.string.app_name), when + 10000);
		noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;

		// 1������һ���Զ������Ϣ���� view.xml
		// 2���ڳ��������ʹ��RemoteViews�ķ���������image��text��Ȼ���RemoteViews���󴫵�contentView�ֶ�
		RemoteViews remoteView = new RemoteViews(this.getPackageName(),
				R.layout.note_notification);
		remoteView.setTextViewText(R.id.note_row_title, title);
		remoteView.setTextViewText(R.id.note_row_body, body);

		noti.contentView = remoteView;
		// 3��ΪNotification��contentIntent�ֶζ���һ��Intent(ע�⣬ʹ���Զ���View����ҪsetLatestEventInfo()����)

		// �������������Settingsģ��
		Intent i = new Intent(this, NoteEditActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, this.mRowId);
		PendingIntent contentIntent = PendingIntent.getActivity(
				NoteEditActivity.this, 0, i, 0);
		noti.contentIntent = contentIntent;

		NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mnotiManager.notify(NOTIFICATION, noti);

	}

	private void removeNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// ȡ����ֻ�ǵ�ǰContext��Notification
		mNotificationManager.cancel(NOTIFICATION);
	}

	// Ĭ����ʾ�ĵ�Notification
	@SuppressWarnings("deprecation")
	private void showDefaultNotification(String title, String body,
			String isStared) {
		// ��ȡSharedPreferences����
		SharedPreferences sp = this.getSharedPreferences(Option.SP,
				MODE_PRIVATE);
		if (!sp.getBoolean(Option.NOTIFICATION, false)) {
			return;
		}

		// ����һ��֪ͨ
		Notification mNotification = new Notification();

		// ��������ֵ
		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = "Jeffen Note Notification : " + title;
		mNotification.when = System.currentTimeMillis() + 10000; // ����������֪ͨ

		// �������Ĺ��캯��,����ֵ����
		// Notification mNotification = = new
		// Notification(R.drawable.icon,"NotificationTest",
		// System.currentTimeMillis()));

		// �������Ч��
		mNotification.defaults |= Notification.DEFAULT_SOUND;

		// �����,������֪��Ҫ�����Ȩ�� : Virbate Permission
		// mNotification.defaults |= Notification.DEFAULT_VIBRATE ;

		// ���״̬��־

		// FLAG_AUTO_CANCEL ��֪ͨ�ܱ�״̬���������ť�������
		// FLAG_NO_CLEAR ��֪ͨ�ܱ�״̬���������ť�������
		// FLAG_ONGOING_EVENT ֪ͨ��������������
		// FLAG_INSISTENT ֪ͨ������Ч��һֱ����
		mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;

		// ����֪ͨ��ʾΪĬ��View
		Intent i = new Intent(this, NoteEditActivity.class);
		i.putExtra(NoteDbAdapter.KEY_ROWID, this.mRowId);
		i.putExtra("FROM_NOTIFICATION", true);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				NOTIFICATION, i, 0);
		if (CommonCheck.isEmpty(title))
			title = this.getResources().getString(R.string.msgTitleHint);
		if (CommonCheck.isEmpty(body)) {
			body = this.getResources().getString(R.string.msgBodyHint);
		}
		mNotification.setLatestEventInfo(NoteEditActivity.this, title, body,
				contentIntent);

		// ����setLatestEventInfo����,��������û�App�����쳣
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// ע���֪ͨ
		// �����NOTIFICATION_ID��֪ͨ�Ѵ��ڣ�����ʾ����֪ͨ�������Ϣ ������tickerText ��
		mNotificationManager.notify(NOTIFICATION, mNotification);

	}
}
