package com.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jeffen.note.CheckListActivity;
import com.jeffen.note.NoteDbAdapter;
import com.jeffen.note.R;
import com.jeffen.pojo.Note;
import com.widget.item.ItemOnCheckedChangeListener;
import com.widget.item.ItemOnClickListener;
import com.widget.item.ItemOnFocusChangeListener;
import com.widget.item.ItemTextWatcher;
import com.widget.item.ScoreOnFocusChangeListener;

public class CheckListAdapter extends ArrayAdapter<Object> {

	private final static String TAG = "CheckListAdapter";
	private static final int ACTIVITY_LIST = 0;
	private static final int ACTIVITY_EDIT = 1;

	private Context context;
	private CheckListActivity a;
	private long mNoteId;
	private List<Object> list = null;

	private int totalSocre;
	private int checkScore;

	private View view = null;
	private int p = -1;
	private int c = 0;
	private int plusItemPosition = -1;

	public boolean isSort = false;

	public CheckListAdapter(Context context, List<Object> objects, long noteId) {
		super(context, 0, objects);

		this.context = context;
		this.a = (CheckListActivity) getContext();
		this.list = objects;
		if (list != null)
			this.c = list.size();
		this.mNoteId = noteId;
	}

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		return 0;
	}

	@Override
	public void add(Object o) {
		if (o != null) {
			ArrayList<Object> _list = (ArrayList<Object>) o;
			for (int i = 0; i < _list.size(); i++) {
				list.add(_list.get(i));
			}
		}

		this.c = list.size();
	}

	/**
	 * 核心实例化方法，逐条构造定制该item的view效果，通过inflat插入的tag
	 * view进行section组装分组标题，而非显示隐藏冗余的tag方式。
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// options
		p = position;
		Note note = new Note();
		note.setPosition(p);

		// set row view
		view = LayoutInflater.from(getContext()).inflate(
				R.layout.check_list_row, null);
		ContentValues cv = (ContentValues) getItem(p);

		// set the score edit text
		EditText score = (EditText) view.findViewById(R.id.check_score);
		if (cv.get(NoteDbAdapter.CT_SCORE) != null) {
			score.setText(((Integer) cv.get(NoteDbAdapter.CT_SCORE)).toString());
		}
		score.addTextChangedListener(new ItemTextWatcher(p) {
			@Override
			public void afterTextChanged(Editable s) {
				a.calTotalScore();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// save data
				ContentValues cv = (ContentValues) getItem(this.getPosition());
				int score = 0;
				try {
					score = Integer.valueOf(s.toString());
				} catch (Exception e) {
					score = 0;
				}
				cv.put(NoteDbAdapter.CT_SCORE, score);
			}
		});
		score.setOnFocusChangeListener(new ScoreOnFocusChangeListener(score));

		// set item edit text
		EditText item = (EditText) view.findViewById(R.id.check_item);
		item.setText((String) cv.get(NoteDbAdapter.CT_ITEM_TITLE));
		item.setOnFocusChangeListener(new ItemOnFocusChangeListener(
				(TextView) view.findViewById(R.id.item_line)));
		
		if(plusItemPosition == p){
			// focus the new insert blank item,but dose note work!
			item.requestFocus();
			plusItemPosition = -1;
		}

		if (p + 1 == c) {
			// the last item
			item.addTextChangedListener(new ItemTextWatcher(p) {
				boolean autoAddItem = false;

				@Override
				public void afterTextChanged(Editable s) {
					if (!autoAddItem) {
						autoAddItem = true;

						a.plusItem();
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// save data
					ContentValues cv = (ContentValues) getItem(this
							.getPosition());
					cv.put(NoteDbAdapter.CT_ITEM_TITLE, s.toString());
				}
			});
			item.requestFocus();
		} else {
			item.addTextChangedListener(new ItemTextWatcher(p) {
				@Override
				public void afterTextChanged(Editable s) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// save data
					ContentValues cv = (ContentValues) getItem(this
							.getPosition());
					cv.put(NoteDbAdapter.CT_ITEM_TITLE, s.toString());
				}
			});
		}

		// set the check box status
		CheckBox cb = (CheckBox) view.findViewById(R.id.item_cb);
		int s = (Integer) cv.get(NoteDbAdapter.CT_ITEM_CHECKED);
		cb.setChecked(s > 0);
		setItemPaintFlags(view, s, false);
		cb.setOnCheckedChangeListener(new ItemOnCheckedChangeListener(view, p) {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				// save data
				ContentValues cv = (ContentValues) getItem(this.getPosition());
				cv.put(NoteDbAdapter.CT_ITEM_CHECKED, isChecked ? 1 : 0);

				// set paint
				setItemPaintFlags(this.getView(), isChecked ? 1 : 0, false);

				// calculate score
				a.calTotalScore();

			}

		});

		// set the delete button
		Button delBtn = (Button) view.findViewById(R.id.check_item_delete);
		delBtn.setOnClickListener(new ItemOnClickListener(note) {

			@Override
			public void onClick(View v) {
				// ①set changed old data
				list = a.getCheckListData();

				// ②prepare to remove item
				clear();
				addAll(list);
				remove(list.get(this.getNote().getPosition()));

				// ③remove data and item
				list.remove(this.getNote().getPosition());
				notifyDataSetChanged();
				a.calTotalScoreInit();

			}
		});

		// set the add button
		Button addBtn = (Button) view.findViewById(R.id.check_item_add);
		addBtn.setOnClickListener(new ItemOnClickListener(note) {

			@Override
			public void onClick(View v) {
				// set changed old data
				list = a.getCheckListData();
				insertItem(this.getNote().getPosition(), newCV());

				clear();
				addAll(list);
				notifyDataSetChanged();

				// focus the item edit text by scroll the list view, dose note work!
				// a.focusItemByScroll(this.getNote().getPosition() + 1);
				
				// save the plus Item Position
				plusItemPosition = this.getNote().getPosition() + 1;
			}
		});

		if (isSort) {
			delBtn.setText("↓");
			delBtn.setOnClickListener(new ItemOnClickListener(note) {

				@Override
				public void onClick(View v) {
					// set changed old data
					list = a.getCheckListData();
					shiftItem(this.getNote().getPosition(), 1);

					clear();
					addAll(list);
					notifyDataSetChanged();
				}
			});

			addBtn.setText("↑");
			addBtn.setOnClickListener(new ItemOnClickListener(note) {

				@Override
				public void onClick(View v) {
					// set changed old data
					list = a.getCheckListData();
					shiftItem(this.getNote().getPosition(), -1);

					clear();
					addAll(list);
					notifyDataSetChanged();
				}
			});
		}

		if (p == list.size() - 1) {
			// the last view
		}

		// request focus
		if (cv != null && cv.containsKey("REQUEST_FOCUS")
				&& cv.getAsBoolean("REQUEST_FOCUS")) {
			item.requestFocus();
			cv.put("REQUEST_FOCUS", false);
		}

		return view;
	}

	/**
	 * 设定项目划掉效果
	 * 
	 * @param v
	 * @param s
	 *            0:取消；1:划掉
	 * @param sync
	 *            是否同步数据库
	 */
	public void setItemPaintFlags(View v, int s, boolean sync) {
		EditText item = (EditText) v.findViewById(R.id.check_item);
		EditText score = (EditText) v.findViewById(R.id.check_score);
		switch (s) {
		case 0:
			item.setPaintFlags(item.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			item.setTextColor(Color.BLACK);

			score.setPaintFlags(item.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			score.setTextColor(context.getResources().getColor(
					com.jeffen.note.R.color.KleinBlue));
			break;
		case 1:
			item.setPaintFlags(item.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			item.setTextColor(Color.GRAY);

			score.setPaintFlags(item.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			score.setTextColor(Color.GRAY);
			break;
		default:
			break;
		}

		if (sync) {
			// 同步数据库
			TextView rowId = (TextView) v.findViewById(R.id.note_row_id);
		}

	}

	/**
	 * 初始化空白条目
	 * 
	 * @return
	 */
	private ArrayList<Object> newItem() {
		ArrayList<Object> _list = new ArrayList<Object>();
		_list.add(newCV());
		return _list;
	}

	private ContentValues newCV() {

		ContentValues cv = new ContentValues();
		cv.put(NoteDbAdapter.CT_NOTE_ROWID, mNoteId);
		cv.put(NoteDbAdapter.CT_ITEM_TITLE, "");
		cv.put(NoteDbAdapter.CT_ITEM_CHECKED, 0);
		cv.put(NoteDbAdapter.CT_SCORE, 0);
		cv.put("REQUEST_FOCUS", true);
		return cv;
	}

	/**
	 * 总分
	 * 
	 * @return
	 */
	public void calTotalScore() {
		totalSocre = 0;
		checkScore = 0;
		for (int i = 0; i < list.size(); i++) {
			ContentValues cv = new ContentValues();
			cv = (ContentValues) list.get(i);
			int score = 0;
			if (cv.get(NoteDbAdapter.CT_SCORE) != null) {
				score = (Integer) cv.get(NoteDbAdapter.CT_SCORE);
				totalSocre += score;
			}
			if (cv.get(NoteDbAdapter.CT_ITEM_CHECKED) != null) {
				checkScore += (Integer) cv.get(NoteDbAdapter.CT_ITEM_CHECKED) > 0 ? score
						: 0;
			}
		}

	}

	/**
	 * 插入条目
	 * 
	 * @param p
	 * @param o
	 */
	private void insertItem(int p, Object o) {
		List<Object> _list = new ArrayList<Object>();
		_list.addAll(list);
		_list.add(o);
		for (int i = 0; i < _list.size(); i++) {
			if (i == p + 1) {
				_list.set(i, o);
			}
			if (i > p + 1) {
				_list.set(i, list.get(i - 1));
			}
		}
		list.clear();
		list.addAll(_list);
	}

	/**
	 * 上下移动
	 * 
	 * @param p
	 * @param func
	 *            1:down; -1:up
	 */
	public void shiftItem(int p, int func) {
		if (p + func < 0 || list.size() - 1 < p + func)
			return;
		Object o1 = list.get(p);
		Object o2 = list.get(p + func);
		list.set(p, o2);
		list.set(p + func, o1);
	}

	public int getTotalSocre() {
		return totalSocre;
	}

	public void setTotalSocre(int totalSocre) {
		this.totalSocre = totalSocre;
	}

	public int getCheckScore() {
		return checkScore;
	}

	public void setCheckScore(int checkScore) {
		this.checkScore = checkScore;
	}

}
