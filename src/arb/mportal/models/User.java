package arb.mportal.models;

import android.location.Location;

public class User {

	private Location userLocation = null;
	private Location poiRequestLocation = null;
	private static User user = null;
	
	private User() {}
	
	public static User getInstance() {
		if(user == null) {
			user = new User();
		}
		return user;
	}
	
	
	public Location getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(Location userLocation) {
		this.userLocation = userLocation;
	}
	
	public Location getPoiRequestLocation() {
		return poiRequestLocation;
	}

	public void setPoiRequestLocation(Location poiRequestLocation) {
		this.poiRequestLocation = poiRequestLocation;
	}	
	
}
