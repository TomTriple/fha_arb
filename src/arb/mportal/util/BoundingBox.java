package arb.mportal.util;

import android.location.Location;

public class BoundingBox {

	private Location point1 = null;
	private Location point2 = null;	 
	
	
	public static BoundingBox getDefault(Location location, double radius) {
		
		Location point1 = null;
		Location point2 = null;
		BoundingBox bb = new BoundingBox();
 
		LocationWrapper lw1 = new LocationWrapper(location);
		lw1.addDeltaLatitude(radius); 
		lw1.addDeltaLongitude(-radius); 
		point1 = lw1.getNewLocation(); 
		
		LocationWrapper lw2 = new LocationWrapper(location);
		lw2.addDeltaLatitude(-radius);  
		lw2.addDeltaLongitude(radius); 
		point2 = lw2.getNewLocation(); 
		
		bb.setPoint1(point1); 
		bb.setPoint2(point2); 
		
		return bb; 
		
	}
	
	
	public Location getLocation1() {
		return point1;
	}

	public void setPoint1(Location point1) {
		this.point1 = point1;
	}

	public Location getLocation2() {
		return point2;
	}

	public void setPoint2(Location point2) {
		this.point2 = point2;
	}
	
	
	public String toString() { 
		return point1.getLatitude() + " / " + point1.getLongitude() + " - " + point2.getLatitude() + " / " + point2.getLongitude();
	}
	
}
