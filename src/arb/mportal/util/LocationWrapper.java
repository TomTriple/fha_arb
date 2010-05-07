package arb.mportal.util;

import android.location.Location;

public class LocationWrapper {  

	private Location location = null;  
	private final static double METER_PER_UNIT_LON = 108874.945;
	private final static double METER_PER_UNIT_LAT = 110618.97; 
	
	public LocationWrapper(Location location) {
		this.location = new Location(location);  
	} 
	
	
	public void addDeltaLatitude(double meter) { 
		location.setLatitude(location.getLatitude() + (1 / METER_PER_UNIT_LAT * meter)); 
	}
	
	 
	public void addDeltaLongitude(double meter) {
		location.setLongitude(location.getLongitude() + (1 / METER_PER_UNIT_LON * meter));
	}
	
	public Location getNewLocation() {
		return location; 
	}
	
}
