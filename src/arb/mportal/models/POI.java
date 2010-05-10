package arb.mportal.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.TextView;

public class POI {
	
	private String name = "";
	private float longitude = 0;
	private float latitude = 0; 
	private Map<String, String> tags = new HashMap<String, String>(); 
	private View view = null;
	
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
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public void addTag(String key, String value) {
		tags.put(key, value);  
	}

	
}
