package com.widget;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffen.note.NoteDbAdapter;
import com.jeffen.note.NoteListActivity;
import com.jeffen.note.R;
import com.jeffen.pojo.Note;
import com.jeffen.pojo.Option;
import com.util.CommonCheck;
import com.widget.image.ImageOnClickListener;
import com.widget.item.ItemLongPressRunnable;
import com.widget.item.ItemOnCheckedChangeListener;
import com.widget.item.ItemOnClickListener;
import com.widget.item.ItemPagerAdapter;
import com.widget.star.StarButton;
import com.widget.star.StarButtonOnClickListener;
import com.widget.star.StarButtonOnLongClickListener;

public class GroupListAdapter extends ArrayAdapter<Object> implements
		OnScrollListener, OnTouchListener, OnClickListener {

	private final static String TAG = "GroupListAdapter";
	private float mFirstX;

	private static final int ACTIVITY_EDIT = 1;

	private Context context;
	private NoteListActivity a;
	private List<Object> listTag = null;
	private List<Object> list = null;
	private View floatTag = null;

	private View view = null;
	private int p = -1;

	private SharedPreferences sp;
	private boolean autoLoad = false;

	public GroupListAdapter(Context context, List<Object> objects,
			List<Object> tags) {
		super(context, 0, objects);

		this.context = context;
		this.a = (NoteListActivity) getContext();
		this.list = objects;
		this.listTag = tags;

		// ��ȡSharedPreferences����
		sp = context.getSharedPreferences(Option.SP, context.MODE_PRIVATE);
		autoLoad = sp.getBoolean(Option.AUTO_LOAD, false);
	}

	@Override
	public boolean isEnabled(int position) {
		if (listTag.contains(getItem(position))) {
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public void add(Object o) {
		Map m = (Map) o;
		ArrayList<Object> _list = (ArrayList<Object>) m.get("_list");
		for (int i = 0; i < _list.size(); i++) {
			list.add(_list.get(i));
		}
		ArrayList<Object> _listTag = (ArrayList<Object>) m.get("_listTag");
		for (int i = 0; i < _listTag.size(); i++) {
			listTag.add(_listTag.get(i));
		}
	}

	/**
	 * ����ʵ�����������������춨�Ƹ�item��viewЧ����ͨ��inflat�����tag
	 * view����section��װ������⣬������ʾ���������tag��ʽ��
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		p = position;
		view = convertView;

		LayoutInflater inflater = null;
		View mPagerView = null;// slide view pager
		if (listTag.contains(getItem(p))) {
			// set group view (row tag)
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.note_row_tag, null);

			ContentValues cv = (ContentValues) getItem(p);

			TextView groupTitle = (TextView) view
					.findViewById(R.id.group_title);
			if (cv.containsKey(NoteDbAdapter.KEY_GROUPT_TITLE)) {
				groupTitle.setText(cv.get(NoteDbAdapter.KEY_GROUPT_TITLE)
						.toString());
			}
			TextView groupCount = (TextView) view
					.findViewById(R.id.group_count);
			if (cv.containsKey(NoteDbAdapter.KEY_GROUPT_COUNT)) {
				groupCount.setText(cv.get(NoteDbAdapter.KEY_GROUPT_COUNT)
						.toString());
			}
		} else {

			// set row view
			inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.note_row, null);
			ContentValues cv = (ContentValues) getItem(p);

			TextView rowId = (TextView) view.findViewById(R.id.note_row_id);
			rowId.setText(cv.get(NoteDbAdapter.KEY_ROWID).toString());
			TextView title = (TextView) view.findViewById(R.id.note_row_title);
			title.setText(cv.get(NoteDbAdapter.KEY_TITLE).toString());
			TextView created = (TextView) view
					.findViewById(R.id.note_row_created);
			created.setText(cv.get(NoteDbAdapter.KEY_CREATED).toString());
			TextView hhmm = (TextView) view.findViewById(R.id.note_row_hhmm);
			hhmm.setText(cv.get(NoteDbAdapter.KEY_HHMM).toString());
			TextView body = (TextView) view.findViewById(R.id.note_row_body);
			body.setText(cv.get(NoteDbAdapter.KEY_BODY).toString());

			// row top hint
			long mRowTop = Long.valueOf(cv.get(NoteDbAdapter.KEY_SHOW_INDEX)
					.toString());
			if (mRowTop > 0) {

				ImageView ivRowTop = (ImageView) view
						.findViewById(R.id.note_row_top);
				ivRowTop.setVisibility(View.VISIBLE);
			}

			// note row
			long mRowId = Long.valueOf(cv.get(NoteDbAdapter.KEY_ROWID)
					.toString());
			Note note = new Note();
			note.setId(mRowId);
			note.setTitle(cv.get(NoteDbAdapter.KEY_TITLE).toString());

			// item check hint
			final TextView check = (TextView) view
					.findViewById(R.id.note_row_check);
			check.setText(cv.get(NoteDbAdapter.CT_TOTAL_ITEMS).toString());
			if ("1".equals(cv.get(NoteDbAdapter.CT_IS_CHECKED_ALL).toString())) {
				check.setTextColor(this.context.getResources().getColor(
						R.color.myGreen));
			}
			check.setOnClickListener(new ItemOnClickListener(note) {

				@Override
				public void onClick(View v) {
					super.onClick(v);
					check.setTextColor(context.getResources().getColor(
							R.color.myBlue));
					a.gotoCheckList(super.getNote().getId(), super.getNote()
							.getTitle());

				}

			});

			// image cover show
			TextView imageTitle = (TextView) view
					.findViewById(R.id.note_row_image_title);
			imageTitle.setText("");
			byte[] b = (byte[]) cv.get(NoteDbAdapter.IK_BITMAP);
			if (b != null && b.length > 0) {
				// linear layout background
				// LinearLayout ll = (LinearLayout) view
				// .findViewById(R.id.note_row_created_pad);
				// ByteArrayInputStream imageStream = new
				// ByteArrayInputStream(b);
				// BitmapDrawable bd = new BitmapDrawable(imageStream);
				// ll.setBackground(bd);
				// ll.setAlpha(100);
				ImageView iv = (ImageView) view
						.findViewById(R.id.note_row_image);
				ByteArrayInputStream imageStream = new ByteArrayInputStream(b);
				iv.setImageBitmap(BitmapFactory.decodeStream(imageStream));
				imageTitle.setText("" + cv.get(NoteDbAdapter.IK_BITMAP_CNT)
						+ " ��");

				iv.setOnClickListener(new ImageOnClickListener(mRowId, 0) {

					@Override
					public void onClick(View v) {
						super.onClick(v);

						a.goToImageSwitch(super.getRowId(), super.getImageId());

					}

				});

			}

			String isStared = (String) cv.get(NoteDbAdapter.KEY_IS_STARED);
			StarButton sb = (StarButton) view
					.findViewById(R.id.note_row_starbutton);
			if (CommonCheck.isNotEmpty(isStared)) {
				sb.setImageResource(R.drawable.star_pink);
			}

			// alarm hint
			int year = (Integer) cv.get(NoteDbAdapter.AT_YEAR);
			if (year > 0) {
				sb.setBackgroundResource(R.drawable.alarm);
				TextView alarmMD = (TextView) view
						.findViewById(R.id.txtAlarmMD);
				alarmMD.setText((Integer.valueOf(cv.get(NoteDbAdapter.AT_MONTH)
						.toString()) + 1)
						+ "-"
						+ (Integer.valueOf(cv.get(NoteDbAdapter.AT_DAY)
								.toString())));
				TextView alarmHM = (TextView) view
						.findViewById(R.id.txtAlarmHM);
				alarmHM.setText(cv.get(NoteDbAdapter.AT_HOUR).toString() + ":"
						+ cv.get(NoteDbAdapter.AT_MINUTE).toString());

			}

			// �߼�ɾ����ֽ�ţ�
			String isDeleted = (String) cv.get(NoteDbAdapter.KEY_IS_DELETED);
			if ("1".equals(isDeleted)) {
				this.setItemPaintFlags(view, 1, false);
			}

			// check box
			CheckBox cb = (CheckBox) view.findViewById(R.id.item_cb);
			cb.setOnCheckedChangeListener(new ItemOnCheckedChangeListener(view) {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					a.cntChk();
					this.getView().setSelected(isChecked);
				}

			});

			// star button
			sb.setOnClickListener(new StarButtonOnClickListener(mRowId,
					isStared) {

				@Override
				public void onClick(View v) {
					super.onClick(v);

					a.updStar(super.getRowId(), super.getIsStared());

					// ����ɹ�
					Toast.makeText(getContext(), "Stared Successfully!",
							Toast.LENGTH_SHORT).show();
				}

			});

			sb.setOnLongClickListener(new StarButtonOnLongClickListener(mRowId) {

				@Override
				public boolean onLongClick(View v) {
					a.alarm(this.getRowId());
					return true;
				}

			});

			view.setOnClickListener(this);

			// swipe-strike-able
			// view.setOnTouchListener(this);

			/**
			 * ��д��������(override by onTouch()!!!)
			 */
			view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub

					Log.d("getView-setOnLongClickListener",
							"new item long click listener.");

					a.itemLongClickProcess(v);
					return false;
				}

			});

			// item background draw able
			if (p % 2 == 0) {
				view.setBackgroundDrawable(context.getResources().getDrawable(
						R.drawable.list_rect_selector_evenrow));
			}

			// set book mark
			View bookmark = inflater.inflate(R.layout.note_row_bookmark, null);
			((TextView) bookmark.findViewById(R.id.note_row_bookmark))
					.setText(cv.get(NoteDbAdapter.BT_BOOKMARK).toString());
			bookmark.setOnClickListener(new ImageOnClickListener(mRowId, 0) {
				@Override
				public void onClick(View v) {
					a.goToBookMark(super.getRowId());
				}
			});
			// set book mark LinearLayout
			View bookmarkLL = view.findViewById(R.id.ll_note_row_bookmark);
			bookmarkLL.setOnClickListener(new ImageOnClickListener(mRowId, 0) {
				@Override
				public void onClick(View v) {
					a.goToBookMark(super.getRowId());
				}
			});
			// bookmarked
			if (cv.containsKey("IS_BOOKMARKED")
					&& cv.getAsBoolean("IS_BOOKMARKED")) {
				view.findViewById(R.id.note_row_bookmark_image).setVisibility(
						View.VISIBLE);
				bookmark.findViewById(R.id.note_row_bookmark_image)
						.setBackgroundDrawable(
								context.getResources().getDrawable(
										R.drawable.bookmark));
			}

			// set item row view pager
			List<View> views = new ArrayList<View>();
			views.add(inflater.inflate(R.layout.note_row_pocket, null));
			views.add(view);
			views.add(bookmark);

			mPagerView = inflater.inflate(R.layout.note_row_viewpager, null);
			ViewPager viewPager = (ViewPager) mPagerView
					.findViewById(R.id.viewpager);
			viewPager.setAdapter(new ItemPagerAdapter(views));
			viewPager.setCurrentItem(1);
			// view pager-height must be set!
			Log.d("getView()", "viewpager-height:" + viewPager.getHeight()
					+ ",itemshow-height:" + view.getHeight());

		}

		if (convertView != null && p == 0) {
			Log.d(TAG, "getView() p:" + p);

			// �ֶ����������������
			floatTag = LayoutInflater.from(getContext()).inflate(
					R.layout.note_row_tag, null);
			TextView groupTitle = (TextView) floatTag
					.findViewById(R.id.group_title);
			groupTitle.setText("add titile");
			TextView groupCount = (TextView) floatTag
					.findViewById(R.id.group_count);
			groupCount.setText("count");

			// ViewGroup vg = (ViewGroup) view.findViewById(R.id.row_tag);
			// vg.addView(floatTag);
			RelativeLayout mainLayout = (RelativeLayout) view
					.findViewById(R.id.row_tag);
			// mainLayout.addView(floatTag);//�ö���ʾ��-1��

			Log.d(TAG, "layoutID:row_tag=" + R.id.row_tag);

		}

		// bound the sliding menu event to the item view,but the on click of
		// item view is covered!!
		// view.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// return a.mSlidingMenu.onTouchEvent(event);
		// }
		// });

		if (!listTag.contains(getItem(p))) {
			return mPagerView;
		}
		return view;
	}

	// ����/////////////////////////////////////////////////////////////////////
	/**
	 * ����Section��Sections��λ�ã��ڼ���Section�����ظ�Section��һ��item��ȫ��listview�е�λ��
	 */
	public int getPositionForSection(int section) {
		if (list == null || list.size() == 0)
			return 0;
		int pos = -1;
		for (int i = 0; i < list.size(); i++) {
			ContentValues cv = (ContentValues) list.get(i);
			if (section >= 0) {
				if (cv.containsKey("IS_TAG")) {
					section--;
				}
			} else {
				break;
			}

			pos++;
		}
		return pos;
	}

	/**
	 * ����listviewĳ��item��λ�÷��ظ�item����Section��Sections��λ��
	 */
	public int getSectionForPosition(int position) {
		if (list == null || list.size() == 0)
			return 0;

		int sec = -1;
		for (int i = 0; i <= position; i++) {
			ContentValues cv = (ContentValues) list.get(i);
			if (cv.containsKey("IS_TAG")) {
				sec++;
			}
		}
		return sec;
	}

	/**
	 * ʵ�ʴ�����û���õ�
	 */
	public Object[] getSections() {
		if (list == null || list.size() == 0)
			return null;

		return list.toArray();
	}

	/**
	 * ����position ������ǵ�ǰ���ӵĵ�һ��item������listview�е�λ��
	 * 
	 * ����Title��TextView����״̬��ʲôʱ�����أ�INVISIBLE_STATE=0����ʲôʱ����ʾ��SHOW_STATE=1��
	 * �Լ�ʲôʱ�򴥷��ƶ���Ч����PUSHING_STATE=2����
	 * 
	 * @param position
	 * @return
	 */
	public int getTitleState(int position) {
		if (position < 0 || getCount() == 0) {

			return 0;
		}

		// ��ǰ���ӵĵ�һ��item���ڵ�section
		int sectionIndex = getSectionForPosition(position);
		if (sectionIndex == -1 || sectionIndex > list.size()) {

			return 0;
		}

		// ��һ��section����λ��
		int nextSectionPosition = getPositionForSection(sectionIndex + 1);
		// �����һ��section����λ�õ��ڵ�ǰ���ӵĵ�һ��item��λ��+1,���Է����ƶ�״̬��2����
		if (nextSectionPosition != -1 && nextSectionPosition == position + 1) {

			return 2;
		}

		return 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		Log.d("onScrollStateChanged", "par:" + view + "," + scrollState);
	}

	/**
	 * �»���M2�ϻ���Ч�����һ��itemtopΪ0�˾�һֱ��Ϊ�Ѿ��ö��ˣ� vm�µ�һ����¼������M2�µ�һ������������
	 * M2�����ˣ�����������������������������
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		// TODO Auto-generated method stub
		Log.d("onScroll", "par:" + view + "," + firstVisibleItem + ","
				+ visibleItemCount + "," + totalItemCount);

		int lastItem = firstVisibleItem + visibleItemCount;
		if (lastItem == totalItemCount) {
			// Last item is fully visible.
			if (autoLoad && a.mDbHelper.hasLoadMore()) {
				a.renderGroupListViewByPage(false, false, false, true);
			}
		}

		// ���������
		// tagFloat(view, firstVisibleItem);

	}

	/**
	 * ��ǰ�����������
	 * 
	 * @param firstVisibleItem
	 */
	public void tagFloat(AbsListView view, int firstVisibleItem) {

		// ȡ�õ�ǰ�����
		int sec = getSectionForPosition(firstVisibleItem);// ��ǰ��
		int tagP = getPositionForSection(sec);// ��ǰ�����
		View tag = view.getChildAt(tagP);
		if (tag == null)
			return;

		// ��ǰ�������ʾ״̬
		int s = getTitleState(firstVisibleItem);

		// ��ǰ�������ʾλ��
		int l = tag.getLeft(), t = tag.getTop(), r = tag.getMeasuredWidth(), b = tag
				.getMeasuredHeight();

		switch (s) {
		case 0:
			// ���أ�INVISIBLE_STATE=0��
			// tag.layout(l, t, r, b);
			tag.setVisibility(View.INVISIBLE);
			break;
		case 1:
			// ��ʾ��SHOW_STATE=1��
			if (t != 0) {
				// �ö�����
				tag.layout(0, 0, r, b);
			}
			break;
		case 2:
			// �����ƶ���Ч����PUSHING_STATE=2��
			View firstView = view.getChildAt(0);
			if (firstView != null) {
				int bottom = firstView.getBottom();
				int headerHeight = tag.getHeight();
				int top;
				if (bottom < headerHeight) {
					top = (bottom - headerHeight);
				} else {
					top = 0;
				}

				if (tag.getTop() != top) {
					tag.layout(0, top, r, b + top);
				}
			}
			break;
		}
	}

	/**
	 * one item is click listener
	 */
	@Override
	public void onClick(View v) {
		TextView rowId = (TextView) v.findViewById(R.id.note_row_id);
		a.goToNoteEdt(Long.valueOf(rowId.getText().toString()));
	}

	// //////////////////////////////swipe item/////////////
	/**
	 * http://nimroddayan.wordpress.com/2013/01/01/hooking-ontouchlistener-event
	 * -to-android-listviews-item/ <br/>
	 * Hooking OnTouchListener event to Android ListView��s Item
	 * <p/>
	 * the listener add some touch functionality to the basic Android��s ListView
	 * to make it the way they did it in Any.DO app. make each list item
	 * strike-able by just swiping my finger from left to right.
	 */
	// long press handler
	final Handler handler = new Handler();
	Runnable mLongPressed = null;

	private void setItemLongPressHandler(View v) {
		handler.removeCallbacks(mLongPressed);
		mLongPressed = new ItemLongPressRunnable(v) {
			public void run() {
				Log.i(TAG, "Long press!");
				TextView rowId = (TextView) this.getItem().findViewById(
						R.id.note_row_id);
				if (rowId != null) {
					Log.i(TAG, "rowId=" + rowId.getText());
				}

				a.itemLongClickProcess(this.getItem());
				handler.removeCallbacks(mLongPressed);
			}
		};
		handler.postDelayed(mLongPressed, 1000);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float currentX = event.getX();
		TextView body = (TextView) v.findViewById(R.id.note_row_body);
		switch (event.getAction()) {
		// ����
		case MotionEvent.ACTION_DOWN:
			// ACTION_DOWN �C when user touch the screen
			mFirstX = currentX;
			Log.v(TAG, String.format("onTouch: ACTION_DOWN for %s", body
					.getText().toString()));

			// item long time click handler
			// setItemLongPressHandler(v);
			break;
		// �ƶ�
		case MotionEvent.ACTION_MOVE:
			// ACTION_MOVE �C when user moves the finger
			Log.v(TAG, "onTouch: ACTION_MOVE " + currentX);

			break;
		// ̧��
		case MotionEvent.ACTION_UP:
			Log.v(TAG, "onTouch: ACTION_UP " + currentX);
			// ACTION_UP �C when user picks up the finger off the screen.
			if (currentX > mFirstX + v.getWidth() / 6) {
				Log.v(TAG, "Swiped! left-right");
				setItemPaintFlags(v, 1, true);
			} else if (currentX < mFirstX - v.getWidth() / 6) {
				Log.v(TAG, "Swiped! right-left");
				setItemPaintFlags(v, 0, true);

			} else {
				// on click event
				TextView rowId = (TextView) v.findViewById(R.id.note_row_id);
				a.goToNoteEdt(Long.valueOf(rowId.getText().toString()));
			}

			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * �趨��Ŀ����Ч��
	 * 
	 * @param v
	 * @param s
	 *            0:ȡ����1:����
	 * @param sync
	 *            �Ƿ�ͬ�����ݿ�
	 */
	public void setItemPaintFlags(View v, int s, boolean sync) {
		TextView title = (TextView) v.findViewById(R.id.note_row_title);
		TextView created = (TextView) v.findViewById(R.id.note_row_created);
		TextView hhmm = (TextView) v.findViewById(R.id.note_row_hhmm);
		TextView body = (TextView) v.findViewById(R.id.note_row_body);
		switch (s) {
		case 0:
			title.setPaintFlags(title.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			title.setTextColor(Color.BLACK);
			created.setPaintFlags(created.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			created.setTextColor(Color.GRAY);
			hhmm.setPaintFlags(hhmm.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			hhmm.setTextColor(Color.GRAY);
			body.setPaintFlags(body.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			break;
		case 1:
			title.setPaintFlags(title.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			title.setTextColor(Color.GRAY);
			created.setPaintFlags(created.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			created.setTextColor(Color.GRAY);
			hhmm.setPaintFlags(hhmm.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			hhmm.setTextColor(Color.GRAY);
			body.setPaintFlags(body.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			body.setTextColor(Color.GRAY);
			break;
		default:
			break;
		}

		if (sync) {
			// ͬ�����ݿ�
			TextView rowId = (TextView) v.findViewById(R.id.note_row_id);
			a.updDele(Long.valueOf(rowId.getText().toString()),
					String.valueOf(s));
		}

	}
}
