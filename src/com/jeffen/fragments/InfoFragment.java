package com.jeffen.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffen.note.R;

public class InfoFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.help_tab_info, container, false);
		TextView textview = (TextView) v.findViewById(R.id.infoText);
		textview.setText("This is Jeffen's Note Application, and thanks:\n\n"
				+ "0.Email me : jeffencheung@gmail.com\n\t"
				+ "1.BaiduMap API:\n\t"
				+ "http://api.map.baidu.com/geocoder?location=x,y&output=json&key=mykey\n"
				+ "\n" + "2.Weather API:\n\t"
				+ "http://www.weather.com.cn/data/cityinfo/citycode.html\n"
				+ "\n" + "3.Yves Klein's International Klein Blue:\n\t"
				+ "#002FA7  RGB (0£¬47£¬167) All Rights Recivied\n" + "\n"
				+ "4.Statck Over Flow:\n\t" + "http://stackoverflow.com/\n"
				+ "\n" + "5.Git Hub:\n\t" + "https://github.com\n" + "\n");
		textview.setTextColor(this.getResources().getColor(
				com.jeffen.note.R.color.KleinBlue));
		return v;
	}

}
