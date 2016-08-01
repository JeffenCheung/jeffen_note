package com.android.gps;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.gps.GpsTask.GpsData;
import com.jeffen.pojo.MLocation;

public class LocationAddress {

	private static Map<String, String> gps_tip;
	private static Context context;

	@SuppressWarnings("unchecked")
	public static Map<String, String> getLocation(Context cont) {
		context = cont;
		gps_tip = new HashMap<String, String>();
		gps_tip = do_apn();
		if (gps_tip == null) {
			gps_tip = do_wifi();
		} else {
			GpsTask gpstask = new GpsTask((Activity) context,
					new GpsTaskCallBack() {

						public void gpsConnectedTimeOut() {
							Toast.makeText(context, "", Toast.LENGTH_LONG)
									.show();
						}

						public void gpsConnected(GpsData gpsdata) {
							gps_tip = do_gps(gpsdata);
						}

					}, 3000);
			gpstask.execute();
		}

		return gps_tip;
	}

	private static Map<String, String> do_apn() {

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask((Activity) context,
							IAddressTask.DO_APN).doApnPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return location.toString();
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				// gps_tip.setText(result);
				gps_tip = getData(result);
				super.onPostExecute(result);
			}

		}.execute();

		return gps_tip;
	}

	private static Map<String, String> getData(String result) {
		String[] sp = result.split("\n");
		for (int i = 0; i < sp.length; i++) {
			String[] str = sp[i].split(":");
			if (str != null)
				gps_tip.put(str[0], str[1]);
		}
		return gps_tip;
	}

	private static Map<String, String> do_gps(final GpsData gpsdata) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask((Activity) context,
							IAddressTask.DO_GPS).doGpsPost(
							gpsdata.getLatitude(), gpsdata.getLongitude());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (location == null)
					return "GPS��Ϣ��ȡ����";
				return location.toString();
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				// gps_tip.setText(result);
				gps_tip = getData(result);
				super.onPostExecute(result);
			}

		}.execute();

		return gps_tip;
	}

	private static Map<String, String> do_wifi() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask((Activity) context,
							IAddressTask.DO_WIFI).doWifiPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (location == null) {
					return "";
				} else
					return location.toString();
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				// gps_tip.setText(result);
				gps_tip = getData(result);
				super.onPostExecute(result);
			}

		}.execute();

		return gps_tip;
	}

}
