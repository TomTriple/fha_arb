package arb.mportal;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationListenerImpl implements LocationListener {

	private LocationReceivable r = null;
	
	public LocationListenerImpl(LocationReceivable r) {
		this.r = r; 
	}
	
	public void onLocationChanged(Location l) {
		r.receiveNewLocation(l); 
	}

	public void onProviderDisabled(String arg0) {
		;
	}

	public void onProviderEnabled(String arg0) {
		;
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		;
	}

}
