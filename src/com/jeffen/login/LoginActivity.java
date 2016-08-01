package com.jeffen.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.jeffen.note.R;
import com.util.CommonCheck;

public class LoginActivity extends Activity {
	public static final String TAG = "Login";

	private EditText username;
	private EditText password;

	private Button btnLogin;
	private Button btnCancel;

	private static final int DIALOG_CHK_NG1 = 1;
	private static final int DIALOG_CHK_NG2 = 2;
	private static final int DIALOG_PROCESSING = 999;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CHK_NG1:
			return buildDialogChkNg(LoginActivity.this, R.string.alert_dialog_ng_title1);
		case DIALOG_CHK_NG2:
			return buildDialogChkNg(LoginActivity.this, R.string.alert_dialog_ng_title2);
		case DIALOG_PROCESSING:
			return buildDilogProcessing(LoginActivity.this,
					R.string.alert_dialog_processing_title,
					R.string.alert_dialog_processing_msg);
		}
		return null;
	}

	/**
	 * 等待对话框
	 * 
	 * @param context
	 * @param alertDialogProcessingTitle
	 * @param alertDialogProcessingMsg
	 * @return
	 */
	private Dialog buildDilogProcessing(Context context,
			int alertDialogProcessingTitle, int alertDialogProcessingMsg) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(alertDialogProcessingTitle);
		dialog.setMessage(this.getResources().getString(
				alertDialogProcessingMsg));
		return dialog;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Obtain handles to UI objects
		username = (EditText) this.findViewById(R.id.username);
		password = (EditText) this.findViewById(R.id.password);

		btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnCancel = (Button) this.findViewById(R.id.btnCancle);
		// Register handler for UI elements
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "btnLogin clicked");
				Log.d(TAG, "username :" + username.getText().toString());
				Log.d(TAG, "password :" + password.getText().toString());

				// input check null
				if (CommonCheck.isEmpty(username)
						|| CommonCheck.isEmpty(username.getText().toString())
						|| CommonCheck.isEmpty(password)
						|| CommonCheck.isEmpty(password.getText().toString())) {
					Log.e(TAG, "input invalid chars!");

					showDialog(DIALOG_CHK_NG1);
				} else {

					// 合法性验证
					showDialog(DIALOG_PROCESSING);
					if (!"username".equals(username.getText().toString())
							&& !"password"
									.equals(password.getText().toString())) {
						showDialog(DIALOG_CHK_NG2);
					}
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "btnCancel clicked");
			}
		});

		username.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					// 回车执行动作
					password.forceLayout();
				}

				if (keyCode == EditorInfo.IME_ACTION_SEARCH
						|| keyCode == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (!event.isShiftPressed()) {
						Log.v("AndroidEnterKeyActivity", "Enter Key Pressed!");
					}
				}
				return false;

			}
		});

	}

	/**
	 * 验证失败对话框
	 * 
	 * @param context
	 * @return
	 */
	private Dialog buildDialogChkNg(Context context, int msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle(msg);
		builder.setPositiveButton(R.string.OK,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
		return builder.create();

	}
}