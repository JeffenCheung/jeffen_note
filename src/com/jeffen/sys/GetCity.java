package com.jeffen.sys;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.jeffen.pojo.MLocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GetCity {
	/**
	 * ����Google MAP ͨ���û���ǰ��γ�� ����û���ǰ����
	 */
	static final String GOOGLE_MAPS_API_KEY = "abcdefg";

	private CityCallBack callBack;
	private LocationManager locationManager;
	private Location currentLocation;
	private String city = "ȫ��";

	public GetCity(CityCallBack callBack, Context context) {
		this.callBack = callBack;
		currentLocation = getLocation(context);

		start();
	}

	// Get the Location by GPS or WIFI
	public Location getLocation(Context context) {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locMan
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

	/**
	 * ��ʼ����
	 */
	public void start() {
		if (currentLocation != null) {
			new Thread() {
				public void run() {
					String temp = reverseGeocode(currentLocation);
					if (temp != null && temp.length() >= 2) {
						city = temp;
						callBack.onCurrentCity(city);
					} else {
						callBack.onCurrentCity("δȡ��-City");
					}
				}
			}.start();
		} else {
			System.out.println("GetCity.start()δ���location");
			callBack.onCurrentCity("δȡ��-Location");
		}
	}

	/**
	 * ��ó���
	 * 
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * ͨ��Google map api ����������
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
					+ "&output=xml&oe=utf8&sensor=true&key="
					+ GOOGLE_MAPS_API_KEY);
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
	 * google��s service. For this example I am using the java SAX (simple api
	 * for xml) parser. The final class to show here is
	 * GoogleReverseGeocodeXmlHandler. In my example, I only want the name of
	 * the city the user is in, so my XmlHandler class I��m about to show only
	 * parses that piece of information. If you want to grab more complete
	 * information (I��ll also give an example file that contains the XML
	 * returned by Google), you��ll have to add more to this class
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

	public interface CityCallBack {
		void onCurrentCity(String city);

	}

	private void initLocation(Context context) {

		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// ֻ�Ǽ򵥵Ļ�ȡ���� ����Ҫʵʱ���� ����������ע��
		// this.locationManager.requestLocationUpdates(
		// LocationManager.GPS_PROVIDER, 1000, 0,
		// new LocationListener() {
		// public void onLocationChanged(Location loc) {
		// //������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ����
		// // Save the latest location
		// currentLocation = loc;
		// // Update the latitude & longitude TextViews
		// System.out
		// .println("getCity()"
		// + (loc.getLatitude() + " " + loc
		// .getLongitude()));
		// }
		//
		// public void onProviderDisabled(String arg0) {
		// System.out.println(".onProviderDisabled(�ر�)"+arg0);
		// }
		//
		// public void onProviderEnabled(String arg0) {
		// System.out.println(".onProviderEnabled(����)"+arg0);
		// }
		//
		// public void onStatusChanged(String arg0, int arg1,
		// Bundle arg2) {
		// System.out.println(".onStatusChanged(Provider��ת̬�ڿ��á�" +
		// "��ʱ�����ú��޷�������״ֱ̬���л�ʱ�����˺���)"+
		// arg0+" "+arg1+" "+arg2);
		// }
		// });
		currentLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (currentLocation == null)
			currentLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

	}
}
