package com.jeffen.sys;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class CityLocationListener implements LocationListener {
	private Activity context;
	private List<Address> addresses;
	private String locationValue;
	private EditText gpsLocationView;

	public CityLocationListener(Activity context, EditText text) {
		this.context = context;
		gpsLocationView = text;
	}

	@Override
	public void onLocationChanged(Location loc) {
		getLocation(loc);
	}

	/**
	 * get location info
	 * 
	 * @param loc
	 */
	public void getLocation(Location loc) {
		if (loc == null)
			return;
		
		loc.getLatitude();
		loc.getLongitude();
		Geocoder gcd = new Geocoder(context, Locale.getDefault());
		try {
			addresses = gcd.getFromLocation(loc.getLatitude(),
					loc.getLongitude(), 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String text = (addresses != null) ? "City : "
				+ addresses.get(0).getSubLocality() + "\n Country : "
				+ addresses.get(0).getCountryName() : "Unknown Location";

		locationValue = "My current location is: " + text;
		Toast.makeText(context, locationValue, Toast.LENGTH_SHORT).show();
		gpsLocationView.setText(locationValue);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(context, "Gps Disabled", Toast.LENGTH_SHORT).show();
		gpsLocationView.setText("Gps Disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(context, "Gps Enabled", Toast.LENGTH_SHORT).show();
		gpsLocationView.setText("Gps Enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		gpsLocationView.setText("onStatusChanged");
	}

	public String getLocationValue() {
		return locationValue;
	}

	public void setLocationValue(String locationValue) {
		this.locationValue = locationValue;
	}

}