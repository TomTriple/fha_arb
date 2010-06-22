package arb.mportal;

import tasks.POILoadTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import arb.mportal.models.POI;
import arb.mportal.models.User;
import arb.mportal.util.BoundingBox;



public class MainActivity extends Activity {

	private LocationManager lm = null;
	private LocationListenerImpl locationListener = null;	
	private TextView statusText = null;  
	private int queryAttempt = 0; 
	private POILoadTask task = null; 
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		    Toast.makeText(this, "Die Anwendung ist für den Landschafts-Modus optimiert.", Toast.LENGTH_LONG).show();
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		setContentView(R.layout.main); 
		statusText = (TextView)findViewById(R.id.statusText);
		statusText.setText("Starte...");
		
        locationListener = new LocationListenerImpl(new LocationReceivable() {
			public void receiveNewLocation(Location l) {
				User.getInstance().setUserLocation(l); 
				setStatusText("Aktuellen Standort empfangen..."); 
				lm.removeUpdates(locationListener);
				startTasks();
			}
		});  
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        
        ImageView imageView = (ImageView) findViewById(R.id.weltbildLogo);
        imageView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				openURL("http://www.weltbild.de");
				return false;
			}
		});       
	} 
	
	
	private void openURL(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
		startActivity(intent); 
	}	

	
	public void onDestroy() {
		super.onDestroy();
		task.cancel(true);
	}
	
	private void startTasks() {
		setStatusText("Starte AsyncTasks..."); 
		queryAttempt++;
		task = new POILoadTask(this);  
		task.execute(new BoundingBox(User.getInstance().getUserLocation(), 0.5 * queryAttempt));
	}
	
	public void setStatusText(String text) {
		statusText.setText(text); 
	}
	
	public void poiLoadTaskFinished() {
		if(POI.size() >= 3) { 
			startActivity(new Intent(this, OverlayActivity.class));  
		} else { 
			POI.clear(); 
			setStatusText("Keine Point of Interests gefunden, starte nochmal mit größerem Radius..."); 
			startTasks(); 
		} 		
	}
}
