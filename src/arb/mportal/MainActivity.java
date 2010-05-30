package arb.mportal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import arb.mportal.models.POI;
import arb.mportal.util.BoundingBox;
import arb.mportal.util.IEach;
import arb.mportal.util.L;
import arb.mportal.views.DefaultPOIView;
import arb.services.PoiServiceFH;



@SuppressWarnings("deprecation")  
public class MainActivity extends Activity implements LocationReceivable {
	
	private boolean initial = true; 
	private Location currentLocation = null;
	private LocationManager lm = null;
	private LocationListenerImpl locationListener = null;
	private Camera camera = null; 
	private AbsoluteLayout contentView = null;
	private BroadcastReceiver poiBroadcastReceiver = null;
	public static TextView t = null;
	private int queryAttempt = 1;
	private static Handler handler = new Handler();
	private static float zRot = 0.0f;
	
		
    public void onCreate(Bundle icicle) { 
    	
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        int h = ViewGroup.LayoutParams.FILL_PARENT; 
        int w = ViewGroup.LayoutParams.WRAP_CONTENT;
        
        contentView = new AbsoluteLayout(this); 
         
        ArbSurface s = new ArbSurface(this);
        s.setCreationCallbacks(this); 
        contentView.addView(s, h, w); 

        t = new TextView(this); 
        contentView.addView(t, new ViewGroup.LayoutParams(h, w));        

        //setContentView(R.layout.main);
        setContentView(contentView); 
         
        // daheim: 47.768924832344055 12.081044912338257 
        
        //ArbSurface surface = (ArbSurface)findViewById(R.id.surface); 
        //surface.setCreationCallbacks(this); 
  
        locationListener = new LocationListenerImpl(this);  
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 / 25, 1, locationListener);

        /*poiBroadcastReceiver = new BroadcastReceiver() {
			@Override 
			public void onReceive(Context context, Intent intent) {
				if(POI.size() >= 10) {
					POI.clear(); 
					poiListReceived();
				} else { 
					startPOIService();
				}
			} 
		};
		registerReceiver(poiBroadcastReceiver, new IntentFilter(PoiServiceFH.POI_LIST_LOADED));*/ 
    }
    
    
    private void poiListReceived() { 
        initialDrawing();
        Runnable drawingRunnable = new Runnable() {
			public void run() {
				while(true) { 
					try { 
						POI.eachPoi(new IEach() { 
							public void each(Object item, int index) {
								final POI p = (POI)item;  
								handler.post(new Runnable() {
									public void run() { 
										DefaultPOIView view = p.getView(); 
										AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams)view.getLayoutParams();
										lp.x = (int)zRot;
										float dist = currentLocation.distanceTo(p.getLocation());
										p.getView().setDistance(dist);
										p.getView().setLayoutParams(lp);
									}
								}); 
							}
						});
						Thread.sleep(1000 / 25); 
					} catch(InterruptedException e) {
						;
					}					
				}
			}
		};
		Thread drawingThread = new Thread(drawingRunnable);
		drawingThread.start();
		
        // unregisterReceiver(poiBroadcastReceiver); 
        // final TextView view = (TextView)findViewById(R.id.myLocationText); 
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
        SensorEventListener listener = new SensorEventListener() {
			public void onSensorChanged(SensorEvent e) {
				zRot = e.values[0];
			}
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				;
			}
		}; 
        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);       	
    } 
    
    
    @SuppressWarnings("deprecation") 
	private void initialDrawing() {
    	POI.eachPoi(new IEach() { 
			public void each(Object item, int index) {
				POI p = (POI)item;
	    		p.setDistance(currentLocation.distanceTo(p.getLocation()));
	    		DefaultPOIView t = new DefaultPOIView(MainActivity.this, p); 
	    		p.setView(t);
	    		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(170, 42, 0, 40 * index);
	    		contentView.addView(t, lp);				
			}
		});
    }


    public void receiveNewLocation(Location loc) {
    	currentLocation = loc;
    	if(initial) { 
    		initial = false; 
	        TextView t = (TextView)findViewById(R.id.myLocationText);
	        startPOIService();
	        //loc.setLatitude(10.906298216666668);
	        //loc.setLongitude(48.361505866666676);
    	}
    }
    
    
    private void startPOIService() {
    	if(queryAttempt == 4) { 
    		System.exit(-1); 
    	}
    	L.i("Versuch: " + queryAttempt + ", nur " + POI.size() + " Resultate");  
        BoundingBox bb = new BoundingBox(currentLocation, 0.5 * queryAttempt);
        PoiServiceFH.parseXML(bb.urlEncode());
		if(POI.size() >= 3) { 
			L.i("genügend POIs vorhanden: " + POI.size()); 
			poiListReceived();
		} else { 
			POI.clear(); 
			queryAttempt++;
			startPOIService();
		}        
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