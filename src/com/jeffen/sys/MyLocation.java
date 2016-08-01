package com.jeffen.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.widget.Toast;

import com.android.gps.AddressTask;
import com.android.gps.CellIDInfo;
import com.android.gps.GpsTask;
import com.android.gps.GpsTask.GpsData;
import com.android.gps.GpsTaskCallBack;
import com.android.gps.IAddressTask;
import com.jeffen.pojo.AddressComponent;
import com.jeffen.pojo.BaiduLocation;
import com.jeffen.pojo.MLocation;
import com.util.CommonCheck;

@SuppressLint("NewApi")
public class MyLocation {

	private LocationCallBack callBack;
	private Activity cxt;
	private boolean status = false;
	private static String BAIDU_KEY = "d36c5b47fbc58eff0e52bf6a7fa3668a";

	public MyLocation(LocationCallBack callBack, Activity cxt) {

		this.callBack = callBack;
		this.cxt = cxt;

		start();
	}

	/**
	 * 开始解析
	 */
	public void start() {
		if (callBack != null) {
			new Thread() {
				public void run() {
					boolean baiduLocationTest = false;
					if (baiduLocationTest) {
						String city = baiduLocation(39.98765, 116.360114)
								.getAddressComponent().getCity();

						MLocation ml = new MLocation();
						ml.setLongitude(116.360114);
						ml.setLatitude(39.98765);
						ml.setCity(city);

						ml = getBaiduLocation(ml);
						callBack.onCurrentLocation(ml, "test");

						return;
					}

					if (getMobileType(cxt) == 3) {

						TelephonyManager tm = (TelephonyManager) cxt
								.getSystemService(Context.TELEPHONY_SERVICE);
						if (tm == null)
							return;
						CdmaCellLocation location = (CdmaCellLocation) tm
								.getCellLocation();
						if (location == null)
							return;
						int sid = location.getSystemId();// 系统标识
															// mobileNetworkCode
						int bid = location.getBaseStationId();// 基站小区号 cellId
						int nid = location.getNetworkId();// 网络标识
															// locationAreaCode
						ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
						CellIDInfo info = new CellIDInfo();
						info.cellId = bid;
						info.locationAreaCode = nid;
						info.mobileNetworkCode = String.valueOf(sid);
						info.mobileCountryCode = tm.getNetworkOperator()
								.substring(0, 3);
						info.mobileCountryCode = tm.getNetworkOperator()
								.substring(3, 5);
						info.radioType = "cdma";
						CellID.add(info);
						Location mLocation = callGear(CellID);
						MLocation ml = new MLocation(mLocation);
						if (mLocation != null) {
							callBack.onCurrentLocation(ml, "mobile");
						}
					} else {
						Location mLocation = getLocation(cxt);
						if (mLocation != null) {
							MLocation ml = new MLocation(mLocation);
							ml = getBaiduLocation(ml);
							callBack.onCurrentLocation(ml, "gps/wifi");
						} else {
							getMeLocation(cxt);
						}
					}
				}
			}.start();
		}
	}

	public interface LocationCallBack {
		void onCurrentLocation(MLocation mLocaiton, String pipe);

	}

	// 调用google gears的方法，该方法调用gears来获取经纬度
	private Location callGear(ArrayList<CellIDInfo> cellID) {
		if (cellID == null)
			return null;

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();

		try {
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("home_mobile_country_code",
					cellID.get(0).mobileCountryCode);
			holder.put("home_mobile_network_code",
					cellID.get(0).mobileNetworkCode);
			holder.put("radio_type", cellID.get(0).radioType);
			holder.put("request_address", true);
			if ("460".equals(cellID.get(0).mobileCountryCode))
				holder.put("address_language", "zh_CN");
			else
				holder.put("address_language", "en_US");

			JSONObject data, current_data;

			JSONArray array = new JSONArray();

			current_data = new JSONObject();
			current_data.put("cell_id", cellID.get(0).cellId);
			current_data.put("location_area_code",
					cellID.get(0).locationAreaCode);
			current_data.put("mobile_country_code",
					cellID.get(0).mobileCountryCode);
			current_data.put("mobile_network_code",
					cellID.get(0).mobileNetworkCode);
			current_data.put("age", 0);
			current_data.put("signal_strength", -60);
			current_data.put("timing_advance", 5555);
			array.put(current_data);

			holder.put("cell_towers", array);

			StringEntity se = new StringEntity(holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);

			HttpEntity entity = resp.getEntity();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				sb.append(result);
				result = br.readLine();
			}

			data = new JSONObject(sb.toString());
			data = (JSONObject) data.get("location");

			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
			loc.setLatitude((Double) data.get("latitude"));
			loc.setLongitude((Double) data.get("longitude"));
			loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
			loc.setTime(System.currentTimeMillis());// AppUtil.getUTCTime());
			return loc;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void getMeLocation(final Activity cxt) {
		do_apn(cxt);
		if (status) {
			status = false;
			do_wifi(cxt);
		} else if (status) {
			status = false;
			GpsTask gpstask = new GpsTask(cxt, new GpsTaskCallBack() {

				public void gpsConnectedTimeOut() {
					Toast.makeText(cxt, "Error", Toast.LENGTH_LONG).show();
				}

				public void gpsConnected(GpsData gpsdata) {
					do_gps(gpsdata, cxt);
				}

			}, 3000);
			gpstask.execute();
		}

	}

	private void do_apn(final Activity cxt) {

		new AsyncTask<Void, Void, MLocation>() {

			protected MLocation doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask(cxt, IAddressTask.DO_APN)
							.doApnPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (location != null)
					return location;
				else
					return null;
			}

			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(MLocation result) {
				if (result != null) {
					setData(result, IAddressTask.PIPE_DO_APN);
					status = true;
				} else {
					status = false;
				}
				super.onPostExecute(result);
			}

		}.execute();

	}

	private void do_gps(final GpsData gpsdata, final Activity cxt) {
		new AsyncTask<Void, Void, MLocation>() {

			protected MLocation doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask(cxt, IAddressTask.DO_GPS)
							.doGpsPost(gpsdata.getLatitude(),
									gpsdata.getLongitude());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return location;
			}

			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(MLocation result) {
				// gps_tip.setText(result);
				if (result != null) {
					setData(result, IAddressTask.PIPE_DO_GPS);
					status = true;
				} else {
					status = false;
				}
				super.onPostExecute(result);
			}

		}.execute();

	}

	private void do_wifi(final Activity cxt) {
		new AsyncTask<Void, Void, MLocation>() {

			protected MLocation doInBackground(Void... params) {
				MLocation location = null;
				try {
					location = new AddressTask(cxt, IAddressTask.DO_WIFI)
							.doWifiPost();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (location == null) {
					return null;
				} else
					return location;
			}

			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(MLocation result) {
				if (result != null) {
					setData(result, IAddressTask.PIPE_DO_WIFI);
					status = true;
				} else {
					status = false;
				}
				super.onPostExecute(result);
			}

		}.execute();

	}

	private void setData(MLocation result, String pipe) {

		callBack.onCurrentLocation(result, pipe);
	}

	// Get the Location by GPS or WIFI
	public Location getLocation(Context context) {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locMan == null)
			return null;
		Location location = locMan
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

	private int getMobileType(Context context) {
		TelephonyManager iPhoneManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (iPhoneManager != null) {
			String iNumeric = iPhoneManager.getSimOperator();
			if (iNumeric.length() > 0) {
				if (iNumeric.equals("46000") || iNumeric.equals("46002")) {
					return 1;
				} else if (iNumeric.equals("46001")) {
					return 2;
				} else if (iNumeric.equals("46003")) {
					return 3;
				}

			}
		}
		return 1;

	}

	// //////////////////////////Google Map
	/**
	 * 通过Google map api 解析出城市
	 * 
	 * @param loc
	 * @return
	 */
	public String reverseGeocode(Location loc) {
		// http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
		String localityName = "";
		HttpURLConnection connection = null;
		URL serverAddress = null;

		try {
			// build the URL using the latitude & longitude you want to lookup
			// NOTE: I chose XML return format here but you can choose something
			// else
			serverAddress = new URL("https://maps.google.com/maps/geo?q="
					+ Double.toString(loc.getLatitude()) + ","
					+ Double.toString(loc.getLongitude())
					+ "&output=xml&oe=utf8&sensor=true&key=Jeffen");
			// set up out communications stuff
			connection = null;

			// Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(10000);

			connection.connect();

			try {
				InputStreamReader isr = new InputStreamReader(
						connection.getInputStream());
				InputSource source = new InputSource(isr);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xr = parser.getXMLReader();
				GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();

				xr.setContentHandler(handler);
				xr.parse(source);

				localityName = handler.getLocalityName();
				System.out.println("GetCity.reverseGeocode()" + localityName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("GetCity.reverseGeocode()" + ex);
		}

		return localityName;
	}

	/**
	 * The final piece of this puzzle is parsing the xml that is returned from
	 * google’s service. For this example I am using the java SAX (simple api
	 * for xml) parser. The final class to show here is
	 * GoogleReverseGeocodeXmlHandler. In my example, I only want the name of
	 * the city the user is in, so my XmlHandler class I’m about to show only
	 * parses that piece of information. If you want to grab more complete
	 * information (I’ll also give an example file that contains the XML
	 * returned by Google), you’ll have to add more to this class
	 * 
	 * @author Administrator
	 * 
	 */
	public class GoogleReverseGeocodeXmlHandler extends DefaultHandler {
		private boolean inLocalityName = false;
		private boolean finished = false;
		private StringBuilder builder;
		private String localityName;

		public String getLocalityName() {
			return this.localityName;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			if (this.inLocalityName && !this.finished) {
				if ((ch[start] != '\n') && (ch[start] != ' ')) {
					builder.append(ch, start, length);
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			super.endElement(uri, localName, name);

			if (!this.finished) {
				if (localName.equalsIgnoreCase("LocalityName")) {
					this.localityName = builder.toString();
					this.finished = true;
				}

				if (builder != null) {
					builder.setLength(0);
				}
			}
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);

			if (localName.equalsIgnoreCase("LocalityName")) {
				this.inLocalityName = true;
			}
		}
	}

	// //////////////////////////////Baidu Map && City Weather
	/**
	 * 通过百度地图取得坐标城市
	 * 
	 * @param ml
	 * @return
	 */
	private MLocation getBaiduLocation(MLocation ml) {
		String cityWeather = "天气取得失败!";
		BaiduLocation bl = baiduLocation(ml.getLatitude(), ml.getLongitude());
		if (bl != null) {
			String cityName = bl.getAddressComponent().getCity();
			MWeather mw = new MWeather(cityName);
			cityWeather = mw.weatherSync();
			if (CommonCheck.isEmpty(cityWeather)) {
				cityWeather = cityName + "天气取得失败!";
			}
			ml.setCity(cityName);
		}
		ml.setWeather(cityWeather);

		return ml;
	}

	/**
	 * 调用baidu map geocoder的方法，该方法调用gears来获取经纬度
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	private static BaiduLocation baiduLocation(double lat, double lng) {

		String url = "http://api.map.baidu.com/geocoder?location="
				+ Double.toString(lat) + "," + Double.toString(lng)
				+ "&output=json&key=" + BAIDU_KEY;

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject holder = new JSONObject();
		BaiduLocation bl = new BaiduLocation();

		try {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			JSONObject data;

			StringEntity se = new StringEntity(holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				sb.append(result);
				result = br.readLine();
			}

			// json to java pojo(by manual),another way by jar
			data = new JSONObject(sb.toString());
			JSONObject r = (JSONObject) data.get("result");
			bl.setCityCode(r.getString("cityCode"));
			bl.setFormatted_address(r.getString("formatted_address"));
			bl.setBusiness(r.getString("business"));

			JSONObject d = r.getJSONObject("addressComponent");
			AddressComponent ac = new AddressComponent();
			ac.setProvince(d.getString("province"));
			ac.setStreet_number(d.getString("street_number"));
			ac.setDistrict(d.getString("district"));
			ac.setStreet(d.getString("street"));
			ac.setCity(d.getString("city"));

			bl.setAddressComponent(ac);

			return bl;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String args[]) {
		// http://api.map.baidu.com/geocoder?location=39.98765,116.360114&output=json&key=d36c5b47fbc58eff0e52bf6a7fa3668a
		// {
		// "status":"OK",
		// "result":{
		// "location":{
		// "lng":116.360114,
		// "lat":39.98765
		// },
		// "formatted_address":"北京市海淀区花园北路223号",
		// "business":"学院路,北航,北太平庄",
		// "addressComponent":{
		// "city":"北京市",
		// "district":"海淀区",
		// "province":"北京市",
		// "street":"花园北路",
		// "street_number":"223号"
		// },
		// "cityCode":131
		// }
		// }
		String city = baiduLocation(39.98765, 116.360114).getAddressComponent()
				.getCity();
		System.out.println(city);

	}
}
