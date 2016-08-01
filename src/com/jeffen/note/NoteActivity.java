package com.jeffen.note;

import com.widget.image.ImageGridAdapter;
import com.widget.star.StarButton;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Jeffen Cheung
 * 
 */
public class NoteActivity extends Activity {
	protected static final int ACTIVITY_LIST = 100;
	protected static final int ACTIVITY_EDIT = 101;
	protected static final int ACTIVITY_VIEW = 102;
	protected static final int ACTIVITY_IMAGE_SWITCH = 103;
	protected static final int ACTIVITY_ALARM = 104;

	protected EditText mTitleText;
	protected TextView mNoteWeather;
	protected StarButton mStarButton;
	protected EditText mBodyText;
	protected TextView mTvProcessing;
	protected Long mRowId;
	
	protected NoteDbAdapter mDbHelper;
	protected GridView mPhotoGrid;
	protected ImageGridAdapter mImageAdapter;

	protected ImageView mChooseImage;
	protected int mChooseImagePosition;
	
	/**
	 * 跳转到图片查看页面
	 * 
	 * @param imagePosition
	 */
	public void goToImageSwitch(int imagePosition) {
		Intent i = new Intent(this, ImageSwitchActivity.class);
		i.putExtra("image_position", imagePosition);
		i.putExtra(NoteDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i, ACTIVITY_IMAGE_SWITCH);
	}

	/**
	 * 取消按钮动作，放到onClick体会intent创建不能！因为this指代对象不同
	 * 
	 * @param view
	 */
	void onCancelPorcess() {
		Intent i = new Intent(this, NoteListActivity.class);
		startActivityForResult(i, ACTIVITY_LIST);
	}
	
}
