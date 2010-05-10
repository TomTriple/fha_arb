package arb.mportal.util;

import android.location.Location;

/**
 * 
 * @author tom
 * Use this class to get a Boundingbox for a gps location.  
 */
public class BoundingBox {

	private Location point1 = null;
	private Location point2 = null;  

	
	public BoundingBox(Location centerLocation, double radius) {
		LocationWrapper lw = new LocationWrapper(centerLocation);
		point1 = lw.getNewLocation(45, radius);
		point2 = lw.getNewLocation(225, radius);
	}
	
	public Location getLocation1() {
		return point1; 
	}


	public Location getLocation2() {
		return point2;
	}
	
	
	public String urlEncode() {  
		return "a=" + point2.getLongitude() + "&b=" + point2.getLatitude() + "&c=" + point1.getLongitude() + "&d=" + point1.getLatitude();
	}
	
	
	public String toString() {  
		return point1.getLatitude() + " / " + point1.getLongitude() + " - " + point2.getLatitude() + " / " + point2.getLongitude();
	}
	
}
