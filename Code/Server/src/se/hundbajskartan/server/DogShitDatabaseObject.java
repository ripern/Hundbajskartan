package se.hundbajskartan.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DogShitDatabaseObject {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Double longitude;

	@Persistent
	private Double latitude;

	@Persistent
	private Date date;

	public DogShitDatabaseObject(Double longitude, Double latitude, Date date) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.date = date;
	}

	public Key getKey() {
		return key;
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
