package com.jeffen.pojo;

/**
 * 
 * http://api.map.baidu.com/geocoder?location=39.98765,116.360114&output=json&
 * key=d36c5b47fbc58eff0e52bf6a7fa3668a <br>
 * { "status":"OK", "result":{ "location":{ "lng":116.360114, "lat":39.98765 },
 * "formatted_address":"�����к�������԰��·223��", "business":"ѧԺ·,����,��̫ƽׯ",
 * "addressComponent":{ "city":"������", "district":"������", "province":"������",
 * "street":"��԰��·", "street_number":"223��" }, "cityCode":131 } }
 * 
 * @author Jeffen
 * 
 */
public class BaiduLocation {
	public String Access_token;
	public Location location;
	public String formatted_address;
	public AddressComponent addressComponent;
	public String business;
	public String cityCode;

	public String getAccess_token() {
		return Access_token;
	}

	public void setAccess_token(String access_token) {
		Access_token = access_token;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public AddressComponent getAddressComponent() {
		return addressComponent;
	}

	public void setAddressComponent(AddressComponent addressComponent) {
		this.addressComponent = addressComponent;
	}

}
