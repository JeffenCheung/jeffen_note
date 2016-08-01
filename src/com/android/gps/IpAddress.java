package com.android.gps;

import android.content.Context;

/*
 import java.net.InetAddress;
 import java.net.NetworkInterface;
 import java.net.SocketException;
 import java.util.Enumeration;
 android.net.wifi.WifiInfo;
 import android.net.wifi.WifiManager;
 */

public class IpAddress {
	// 客户端不�?��取ip
	public static String GetIP(Context context) {
		return "";
		// 获取wifi服务
		/*
		 * WifiManager wifiManager = (WifiManager)
		 * context.getSystemService(context.WIFI_SERVICE);
		 * if(wifiManager.isWifiEnabled()) { WifiInfo wifiInfo =
		 * wifiManager.getConnectionInfo(); int ipAddress =
		 * wifiInfo.getIpAddress(); return intToIp(ipAddress); } return
		 * getNetworkIP();
		 */
	}
	/*
	 * private static String intToIp(int i) { return (i & 0xFF ) + "." + ((i >>
	 * 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ( i >> 24 & 0xFF) ; }
	 * 
	 * public static String getNetworkIP() { try { for
	 * (Enumeration<NetworkInterface> en =
	 * NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	 * NetworkInterface intf = en.nextElement(); for (Enumeration<InetAddress>
	 * enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	 * InetAddress inetAddress = enumIpAddr.nextElement(); if
	 * (!inetAddress.isLoopbackAddress()) { return
	 * inetAddress.getHostAddress().toString(); } } } } catch (SocketException
	 * ex) { } return ""; }
	 */
}
