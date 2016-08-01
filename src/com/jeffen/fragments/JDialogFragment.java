package com.jeffen.fragments;

//import android.support.v4.app.DialogFragment;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.jeffen.note.NoteListActivity;
import com.jeffen.note.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class JDialogFragment extends DialogFragment {
	
	/**
	 * instance method
	 * @param title
	 * @return
	 */
	public static JDialogFragment newInstance(int title) {
		JDialogFragment jdf = new JDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		jdf.setArguments(args);
		return jdf;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt("title");
		return new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle(title)
				.setPositiveButton(R.string.OK,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((NoteListActivity) getActivity())
										.doPositiveClick();
							}
						})
				.setNegativeButton(R.string.btnCancle,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((NoteListActivity) getActivity())
										.doNegativeClick();
							}
						}).create();
	}
}
