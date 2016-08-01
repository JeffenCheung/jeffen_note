package com.jeffen.sys;

import com.jeffen.pojo.Option;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BaseActivity extends Activity {

	private SharedPreferences sp = null;

	public void setAppValueBySharedPreferences(Context ctx, String key) {
		Editor editor = getSP(ctx).edit();
		editor.putString("STRING_KEY", key);
		editor.commit();
	}

	public String getAppValueBySharedPreferences(Context ctx, String key) {
		return getSP(ctx).getString(key, "");
	}

	private SharedPreferences getSP(Context ctx) {
		if (sp == null) {
			return ctx.getSharedPreferences(Option.SP,
					Context.MODE_PRIVATE);
		}
		return sp;
	}

}
