package com.team.actor;

public class Location {

	private int locationId;
	private String category;
	private String unit;
	private String address;
	private String city;
	private String postalCode;
	private String phone;
	private String name;
	private Double lat;
	private Double longi;
	private String website;
	private String type; 

	public Location(int locationId, String category, String unit, String address, String city,
			String postalCode, String phone, String name,  Double lat, Double longi, String website, String type)
	{
		this.locationId = locationId;
		this.category = category;
		this.unit = unit;
		this.address = address;
		this.name = name;
		this.city = city;
		this.postalCode = postalCode;
		this.lat = lat;
		this.longi = longi;
		this.website = website;
		this.type = type;
		this.phone = phone;
	}
	
	
	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLongi() {
		return longi;
	}

	public void setLongi(Double longi) {
		this.longi = longi;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
