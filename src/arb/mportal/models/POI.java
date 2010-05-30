package arb.mportal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;
import android.view.View;
import android.widget.AbsoluteLayout;
import arb.mportal.util.IEach;
import arb.mportal.views.DefaultPOIView;

public class POI {
	
	private String name = "";
	private float longitude = 0;
	private float latitude = 0; 
	private Location location = null;
	private float distance = 0.0f;
	private Map<String, String> tags = new HashMap<String, String>(); 
	private DefaultPOIView view = null;
	
	private static List<POI> all = new ArrayList<POI>(); 
	
	
	public static List<POI> findAll() {
		return all; 
	}
	
	
	public static void add(POI poi) {
		all.add(poi); 
	}
	
	
	public static void clear() {
		all.clear(); 
	}

	
	
	public String toString() {
		return name + " - " + latitude + " - " + longitude; 
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public DefaultPOIView getView() {
		return view;
	}
	public void setView(DefaultPOIView view) {
		this.view = view;
	}
	public void addTag(String key, String value) {
		tags.put(key, value);  
	}
	public Map<String, String> getTags() {
		return tags;  
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getDistance() {
		return distance; 
	}
	public Location getLocation()  {
		Location l = new Location("");
		l.setLatitude(getLatitude());
		l.setLongitude(getLongitude()); 
		return l; 
	}
	
	public String getTagStreet() {
		return getTags().get("addr:street");
	}
	public String getTagHousenumber() {
		return getTags().get("addr:housenumber");
	}
	public String getTagPostcode() {
		return getTags().get("addr:postcode"); 
	}
	public String getTagURL() {
		return getTags().get("url:official");
	}
	public String getTagDescription() {
		return getTags().get("description"); 
	}
	
	
	public String getDescription() {
		StringBuffer buf = new StringBuffer();
		int lines = 5; 
		if(getTagStreet() != null) {
			buf.append(getTagStreet());
		}
		if(getTagHousenumber() != null) {
			buf.append(" " + getTagHousenumber());
		}
		if(buf.toString().equals("") == false) {
			buf.append("\n");
			lines--;
		}
		if(getTagPostcode() != null) { 
			buf.append(getTagPostcode() + "\n");
			lines--;
		}
		if(getTagURL() != null && !getTagURL().equals("null") && !getTagURL().equals("")) { 
			buf.append(getTags().get("url:official") + "\n");
			lines--;
		}
		if(getTagDescription() != null && !getTagDescription().equals("null") && !getTagDescription().equals("")) {
			buf.append(getTags().get("description") + "\n");   
		}  
		return buf.toString();
	}
	
	
	public static void eachPoi(IEach each) {
    	List<POI> all = POI.findAll();
    	int i = 1; 
    	for(POI p : all) { 
    		each.each(p, i); 
    		i++; 
    	}		
	}


	public static void hideAll() {
		POI.eachPoi(new IEach() {
			public void each(Object item, int index) {
				POI poi = (POI)item;
				poi.getView().close();
			}
		});
	}

	
}
