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


public class PoiServiceFH { 

	
	public static void parseXML(String params) {
		String u = "http://studwww.multimedia.hs-augsburg.de:3000/middleware?" + params;
		
		L.i(u); 
		
		URL url = null; 
		HttpURLConnection c = null;
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try { 
			url = new URL(u); 
			c = (HttpURLConnection)url.openConnection();
			DocumentBuilder b = f.newDocumentBuilder();
			Document dom = b.parse(c.getInputStream()); 
			Element root = dom.getDocumentElement(); 
			NodeList nl = root.getElementsByTagName("poi");  
			if(nl.getLength() == 0) { 
				L.i("Fehler, keine POI´s vom Server"); 
			} 
			for(int i = 0; i < nl.getLength(); i++) {
				Element node = (Element)nl.item(i); 
				float lat = Float.parseFloat(node.getAttribute("lat")); 
				float lon = Float.parseFloat(node.getAttribute("long")); 
				NodeList ch = node.getElementsByTagName("tag"); 
				POI p = new POI(); 
				POI.add(p);
				p.setLatitude(lat);  
				p.setLongitude(lon);
				for(int j = 0; j < ch.getLength(); j++) {  
					Element tag = (Element)ch.item(j);
					if(tag.getAttribute("k").equals("name")) {
						p.setName(tag.getAttribute("v"));  
					} else {
						p.addTag(tag.getAttribute("k"), tag.getAttribute("v"));						
					}
				}     
			}
		} catch(Exception e) {
			e.printStackTrace();
		}  
	}
	
}
