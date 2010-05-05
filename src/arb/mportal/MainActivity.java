package arb.mportal;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import arb.mportal.models.POI;
import arb.mportal.views.DefaultPOIView;
import arb.services.PoiServiceFH;



public class MainActivity extends Activity implements LocationReceivable { 


	private LocationManager lm = null;
	private LocationListenerImpl locationListener = null;
	private Camera camera = null; 
	@SuppressWarnings("deprecation") 
	private AbsoluteLayout contentView = null;
	private BroadcastReceiver poiBroadcastReceiver = null;
	private TextView t = null; 
	private static Handler handler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() { 
			calc();
		} 
	};		
	
	private static float val = 0.0f;
	
	
    public void onCreate(Bundle icicle) { 
    	
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   

        int h = ViewGroup.LayoutParams.FILL_PARENT; 
        int w = ViewGroup.LayoutParams.WRAP_CONTENT;
        
        contentView = new AbsoluteLayout(this);  
         
        ArbSurface s = new ArbSurface(this);
        s.setCreationCallbacks(this); 
        contentView.addView(s, h, w); 

        t = new TextView(this); 
        t.setText("hallo"); 
        contentView.addView(t, new ViewGroup.LayoutParams(h, w));        
        
        //setContentView(R.layout.main);
        setContentView(contentView);   
        
        // daheim: 47.768924832344055 12.081044912338257 
        
        //ArbSurface surface = (ArbSurface)findViewById(R.id.surface); 
        //surface.setCreationCallbacks(this); 

        locationListener = new LocationListenerImpl(this);  
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);

        // service starten 
        startService(new Intent(this, PoiServiceFH.class));
        poiBroadcastReceiver = new BroadcastReceiver() {
			@Override 
			public void onReceive(Context context, Intent intent) {
				poiListReceived();
			}
		}; 
		registerReceiver(poiBroadcastReceiver, new IntentFilter(PoiServiceFH.POI_LIST_LOADED)); 
    } 
    
    
    private static void calc() {
    	handler.post(new Runnable() {
			public void run() { 
				for(POI p : POI.findAll()) {
					AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams)p.getView().getLayoutParams();
					lp.x = (int)val;
					p.getView().setLayoutParams(lp); 
				} 
			}
		}); 
    }
    
    
    private void poiListReceived() {
        draw();        
        unregisterReceiver(poiBroadcastReceiver); 
        // final TextView view = (TextView)findViewById(R.id.myLocationText);
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
        SensorEventListener listener = new SensorEventListener() {
			public void onSensorChanged(SensorEvent e) {
				val = e.values[0];  
				new Thread(r).start(); 
			}
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				;
			} 
		}; 
        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);     	
    }
    
    
    
    @SuppressWarnings("deprecation") 
	private void draw() { 
    	List<POI> all = POI.findAll();
    	int w = ViewGroup.LayoutParams.WRAP_CONTENT;
    	int h = ViewGroup.LayoutParams.FILL_PARENT;
    	int i = 1; 
    	for(POI p : all) { 
    		DefaultPOIView t = new DefaultPOIView(this);   
    		t.setText(p.getName() + " - " + p.getLatitude() + " - " + p.getLongitude());
    		p.setView(t);  
    		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(150, 100, 0, 15 * i);
    		contentView.addView(t, lp);
    		i++; 
    	}
    }
    
    
    
    public void receiveNewLocation(Location loc) {
        TextView t = (TextView)findViewById(R.id.myLocationText);
        t.setText("Pos: " + loc.getLatitude() + " / " + loc.getLongitude()); 
    	//Log.i("location", loc.toString());  
    	//System.out.println("Loc: " + loc);
    }
    
    
    public void surfaceCreated(SurfaceHolder holder) {
    	camera = Camera.open(); 
    	try { 
	    	camera.setPreviewDisplay(holder);  
	    	camera.startPreview();  
    	} catch(Exception e) {
    		Log.d("CAMERA", e.getMessage()); 
    	}
    }
    
    
    public void surfaceDestroyed(SurfaceHolder holder) {
    	camera.stopPreview();
    	camera.release();
    }

}