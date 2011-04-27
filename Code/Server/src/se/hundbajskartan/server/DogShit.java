package se.hundbajskartan.server;

import java.util.Date;

public class DogShit {

	private Double longitude;

	private Double latitude;

	private Date date;

	public DogShit(Double longitude, Double latitude, Date date) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.date = date;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Date getDate() {
		return date;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
