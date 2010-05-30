package arb.mportal.util;

import android.location.Location;

/**
 *  
 * @author tom
 *
 * Private class that wraps a Location-Object. The purpose of this class is to add the ability to calculate
 * a new gps-position based on a given location, a direction and a length. 
 */

class LocationWrapper {  

	private Location location = null;  

	protected LocationWrapper(Location location)  {
		this.location = location; 
	} 
	
	
    protected Location getNewLocation(double brng, double dist) {
    	Location l = new Location(""); 
    	final double R = 6371; 
    	double lat1 = Math.toRadians(location.getLatitude());  
    	double lon1 = Math.toRadians(location.getLongitude());  

        dist = dist / R;   
    	brng = Math.toRadians(brng);   

    	double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
    	double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));
    	lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

    	l.setLatitude(Math.toDegrees(lat2)); 
    	l.setLongitude(Math.toDegrees(lon2));   

    	return l; 
    }	
	
	
}
