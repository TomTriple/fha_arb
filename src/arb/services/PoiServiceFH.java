package arb.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import arb.mportal.models.POI;
import arb.mportal.util.L; 


public class PoiServiceFH extends Service { 

	public static final String POI_LIST_LOADED = "poi_list_loaded"; 
	
	@Override   
	public void onStart(Intent i, int startId) { 
		
		super.onStart(i, startId); 
		//String u = "http://www.hs-augsburg.de/~thoefer/data.txt"; 
		String u = "http://studwww.multimedia.hs-augsburg.de:3000/middleware?" + i.getStringExtra("params");

		L.i(u); 
		
		try {
			URL url = new URL(u); 
			HttpURLConnection c = (HttpURLConnection)url.openConnection();
			parseXML(c.getInputStream());
			sendBroadcast(new Intent(POI_LIST_LOADED));  
			stopSelf(); 
		}  catch(Exception e) {
			;
		}  
	}
	
	
	private void parseXML(InputStream in) {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder b = f.newDocumentBuilder();
			Document dom = b.parse(in); 
			Element root = dom.getDocumentElement(); 
			NodeList nl = root.getElementsByTagName("poi");  
			if(nl.getLength() == 0) { 
				L.i("Fehler, keine POI´s vom Server"); 
			} 
			for(int i = 0; i < nl.getLength(); i++) {
				POI p = new POI(); 
				POI.add(p); 
				Element node = (Element)nl.item(i); 
				float lat = Float.parseFloat(node.getAttribute("lat")); 
				float lon = Float.parseFloat(node.getAttribute("long")); 
				p.setLatitude(lat);  
				p.setLongitude(lon); 
				NodeList ch = node.getElementsByTagName("tag"); 
				for(int j = 0; j < ch.getLength(); j++) { 
					Element tag = (Element)ch.item(j);
					p.addTag(tag.getAttribute("k"), tag.getAttribute("v"));  					
				}     
			}
		} catch(Exception e) {
			e.printStackTrace();
		}  
	}
	
	
	@Override
	public IBinder onBind(Intent i) {
		return null;
	}
	
	
}
