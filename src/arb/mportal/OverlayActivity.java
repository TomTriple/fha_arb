package arb.mportal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
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
import arb.mportal.models.User;
import arb.mportal.util.IEach;
import arb.mportal.util.L;
import arb.mportal.views.DefaultPOIView;



@SuppressWarnings("deprecation")  
public class OverlayActivity extends Activity {
	 
	private LocationManager lm = null;
	private LocationListenerImpl locationListener = null;
	private Camera camera = null; 
	private AbsoluteLayout contentView = null; 
	public static TextView t = null;
	private static Handler handler = new Handler();
	private static float zRot = 0.0f; 
	private static float xRot = 0.0f; 
	

    public void onCreate(Bundle icicle) { 
        
        super.onCreate(icicle);
        
        // clear the title-bar 
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ensure that the user´s screen appears bright -> battery lifetime is heavily stressed...  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        int h = ViewGroup.LayoutParams.FILL_PARENT; 
        int w = ViewGroup.LayoutParams.WRAP_CONTENT;

        contentView = new AbsoluteLayout(this); 
        ArbSurface s = new ArbSurface(this);
        s.setCreationCallbacks(this); 
        contentView.addView(s, h, w); 

        t = new TextView(this); 
        contentView.addView(t, new ViewGroup.LayoutParams(h, w));

        setContentView(contentView); 
  
        locationListener = new LocationListenerImpl(new LocationReceivable() {
			public void receiveNewLocation(Location l) {
				User.getInstance().setUserLocation(l); 
			}
		});
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 / 25, 1, locationListener);
        
        initialDrawing();
        startDrawingUpdates();        
    }
    
    

    private void startDrawingUpdates() { 
        Runnable drawingRunnable = new Runnable() {
			public void run() { 
				while(true) { 
					try {
						for(int i = 0; i < POI.size(); i++) {
							final POI p = POI.get(i); 
							handler.post(new Runnable() {
								public void run() { 
									DefaultPOIView view = p.getView();
									if(view == null)
										return;
									AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams)view.getLayoutParams();
									//lp.x = (int)zRot;  
									// lp.y = (int)xRot + 65; 
									double dx = Math.abs(User.getInstance().getUserLocation().getLongitude() - p.getLongitude());
									double dy = Math.abs(User.getInstance().getUserLocation().getLatitude() - p.getLatitude());
									double angle = 180 / Math.PI * Math.atan2(dx, dy);
									double length = Math.sqrt(dx*dx + dy*dy); 
									
									// lp.x = (int)angle; 

									if(User.getInstance().getUserLocation() != null) {
										float dist = User.getInstance().getUserLocation().distanceTo(p.getLocation());
										p.getView().setDistance(dist); 
									}
									p.getView().setLayoutParams(lp);
								}
							});							
						}
						Thread.sleep(1000 / 15);
					} catch(InterruptedException e) {
						;
					}					
				}
			}
		};
		Thread drawingThread = new Thread(drawingRunnable);
		drawingThread.start();
 
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
        SensorEventListener listener = new SensorEventListener() {
			public void onSensorChanged(SensorEvent e) {
				zRot = e.values[0];
				xRot = e.values[2];
				//L.i(String.valueOf(xRot)); 
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
	    		p.setDistance(User.getInstance().getUserLocation().distanceTo(p.getLocation()));
	    		DefaultPOIView t = new DefaultPOIView(OverlayActivity.this, p); 
	    		p.setView(t);
	    		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(170, 42, 0, 120);
	    		contentView.addView(t, lp);				
			}
		});
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