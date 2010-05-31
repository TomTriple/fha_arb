package tasks;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import arb.mportal.MainActivity;
import arb.mportal.models.POI;
import arb.mportal.util.BoundingBox;
import arb.mportal.util.L;


public class POILoadTask extends AsyncTask<BoundingBox, String, Integer> { 
 
	private MainActivity callbackActivity = null;
	
	public POILoadTask(MainActivity callbackActivity) {
		this.callbackActivity = callbackActivity; 
	}
	
	@Override
	protected void onProgressUpdate(String... params) {
		super.onProgressUpdate(params);
		callbackActivity.setStatusText(params[0]);
	} 
	
	@Override
	protected Integer doInBackground(BoundingBox... bb) {
		publishProgress("Starte Datenanfrage..."); 
		String u = "http://studwww.multimedia.hs-augsburg.de:3000/middleware?" + bb[0].urlEncode(); 		
		L.i(u);  
		int results = 0;
		URL url = null; 
		HttpURLConnection c = null;
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		try { 
			url = new URL(u); 
			c = (HttpURLConnection)url.openConnection();
			publishProgress("Datenabruf fertig, verarbeite Daten...");
			DocumentBuilder b = f.newDocumentBuilder();
			Document dom = b.parse(c.getInputStream()); 
			Element root = dom.getDocumentElement(); 
			NodeList nl = root.getElementsByTagName("poi");  
			if(nl.getLength() == 0) {
				publishProgress("Leider Keine Point of Interests vorhanden...");
				return 0; 
			} 
			results = nl.getLength();
			publishProgress("Es wurden " + results + " Point of Interests gefunden...");
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
			System.exit(-1);
		} 
		return results;  
	} 
	
	@Override
	protected void onPostExecute(Integer params) {
		super.onPostExecute(params); 
		callbackActivity.poiLoadTaskFinished();
	}
	
}
