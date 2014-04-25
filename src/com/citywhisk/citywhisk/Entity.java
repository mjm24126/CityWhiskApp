package com.citywhisk.citywhisk;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Entity implements Serializable{
	private String name;
	private boolean isPromo;
	private int tourId;
	private String catagory;
	private String address;
	private String description;
	private double lowPrice;
	private double highPrice;
	private double rating; 
	private double time;
	private String image;
	private String link;
	private String phoneNum;
	private String latitude;
	private String longitude;
	private boolean isLocked;
	private double savings;
	private String distance;
	private String duration;
	private String jsonOutput;
	private String images;
	private boolean isIndoorOnly;
	private boolean isOutdoorOnly;
	private String activities;
	private String demos;
	
	public double getSavings() {
		return savings;
	}
	
	public void setSavings(double s) {
		savings = s;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public boolean getPromo() {
		return isPromo;
	}
	
	public void setPromo(boolean promo) {
		isPromo = promo;
	}
	public int getTourId() {
		return tourId;
	}
	
	public void setTourId(int tourId) {
		this.tourId = tourId;
	}
	public String getCatagory() {
		return catagory;
	}
	
	public void setCatagory(String catagory) {
		this.catagory = catagory;
	}
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public double getLowPrice() {
		return lowPrice;
	}
	
	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public double getHighPrice() {
		return highPrice;
	}
	
	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}
	
	public double getRating() {
		return rating;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public double getTime() {
		return time;
	}
	
	public void setTime(double time) {
		this.time = time;
	}
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getDistance() {
		return distance;
	}
	
	public void setDistance(String d) {
		this.distance = d;
	}

	public String getJsonOutput() {
		return jsonOutput;
	}

	public void setJsonOutput(String jsonOutput) {
		this.jsonOutput = jsonOutput;
	}
	
	public String getImages() {
		return images;
	}

	public void setIndoorOnly(boolean b) {
		isIndoorOnly = b;
	}
	
	public boolean getIndoorOnly() {
		return isIndoorOnly;
	}
	
	public void setOutdoorOnly(boolean b) {
		isOutdoorOnly = b;
	}
	
	public boolean getOutdoorOnly() {
		return isOutdoorOnly;
	}

	public String getActivities() {
		return activities;
	}

	public void setActivities(String activities) {
		this.activities = activities;
	}

	public String getDemos() {
		return demos;
	}

	public void setDemos(String demos) {
		this.demos = demos;
	}

	public void setImages(String images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return getName();
	}

	/*public int getImage_max_size() {
		return image_max_size;
	}

	public void setImage_max_size(int image_max_size) {
		this.image_max_size = image_max_size;
	}*/

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}