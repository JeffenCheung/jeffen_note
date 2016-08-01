package com.android.gps;

import com.android.gps.GpsTask.GpsData;

public interface GpsTaskCallBack {

	public void gpsConnected(GpsData gpsdata);

	public void gpsConnectedTimeOut();

}
