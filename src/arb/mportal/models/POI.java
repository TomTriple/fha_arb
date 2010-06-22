package arb.mportal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.location.Location;
import arb.mportal.util.IEach;
import arb.mportal.views.DefaultPOIView;

public class POI {
	
	private String name = "";
	private double longitude = 0;
	private double latitude = 0; 
	private Location location = null;
	private float distance = 0.0f;
	private Map<String, String> tags = new HashMap<String, String>(); 
	private DefaultPOIView view = null;
	private String amenityBuffer = "";
	private String descriptionBuffer = "";	
	
	private static Map<String,String> keyMap = new HashMap<String, String>();
	
	static {
		keyMap.put("fee", "Gebühr");
		keyMap.put("wheelchair", "Rollstuhl");
		keyMap.put("male", "Männlich");
		keyMap.put("female", "Weiblich");
		keyMap.put("parking", "Parktyp");
		keyMap.put("amenity", "Einrichtung");
		keyMap.put("operator", "Betreiber");
		keyMap.put("opening_hours", "Öffnungszeit");
		keyMap.put("cuisine", "Küche");
	}	
	
	private static List<POI> all = new ArrayList<POI>(); 
	
	public POI() {
	}
	
	
	public static List<POI> findAll() {
		return all; 
	}
	
	
	public static void add(POI poi) {
		all.add(poi); 
	}
	
	
	public static int size() {
		return all.size(); 
	}
	
	public static void clear() {
		all.clear();  
	}

	public static POI get(int index) {
		return all.get(index); 
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
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
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
	public String getTagAmenity() {
		return getTags().get("amenity"); 
	}	
	
	
	public String getDescription() {
		if(!descriptionBuffer.equals(""))
			return descriptionBuffer;
		StringBuffer buf = new StringBuffer();
		int lines = 5; 
		if(getTagStreet() != null) {
			buf.append(getTagStreet());
			getTags().remove("addr:street");
			lines--;
		}
		if(getTagHousenumber() != null) {
			buf.append(" " + getTagHousenumber());
			getTags().remove("addr:housenumber");
			lines--;
		}
		if(buf.toString().equals("") == false) {
			buf.append("\n");
			lines--;
		}
		if(getTagAmenity() != null) {
			amenityBuffer = getTagAmenity().substring(0,1).toUpperCase()+getTagAmenity().substring(1); 
			buf.append("Einrichtung: " + amenityBuffer + "\n");  
			getTags().remove("amenity"); 
			lines--;
		}		
		if(getTagURL() != null && !getTagURL().equals("null") && !getTagURL().equals("")) { 
			buf.append("Web: " + getTags().get("url:official") + "\n");
			getTags().remove("url:official");
			lines--;
		}
		if(getTagDescription() != null && !getTagDescription().equals("null") && !getTagDescription().equals("")) {
			buf.append(getTags().get("description") + "\n"); 
			getTags().remove("description");
			lines--;
		}
			
		
		Set<String> keys = getTags().keySet();
		Iterator<String> keyIterator = keys.iterator();
		while(keyIterator.hasNext()) {
			if(lines == 0)
				break; 
			String key = keyIterator.next();
			buf.append(keyMap.get(key) + ": " + getTags().get(key) + "\n"); 
			lines--;
		}
		
		descriptionBuffer = buf.toString();
		return descriptionBuffer;
	}
	
	
	public static void eachPoi(IEach each) {
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
				poi.getView().closeLayer();
			}
		});
	}
	
	public String getTitle() {
		String title = name;
		if(title.equals(""))
			title = amenityBuffer;
		if(title == null)
			return "---";
		if(title.length() >= 15) {
			return title.substring(0, 13) + "...";
		}
		return title;
	}

	
}
