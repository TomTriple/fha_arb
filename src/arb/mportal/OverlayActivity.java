package arb.mportal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.widget.Toast;
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
	
	private Thread drawingThread;
	
	public static volatile float kFilter = (float)0.1;
	public static float aboveOrBelow = (float)0;	
	

    public void onCreate(Bundle icicle) { 
        
        super.onCreate(icicle);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		    Toast.makeText(this, "Die Anwendung ist für den Landschafts-Modus optimiert.", Toast.LENGTH_LONG).show();
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} 
		
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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 / 25, 1, locationListener);
        
        initialDrawing();
        startDrawingUpdates();        
    }
    
    

    private void startDrawingUpdates() { 
        Runnable drawingRunnable = new Runnable() {
			public void run() { 
				float zRotLastIteration = zRot; 
				while(true) { 
					if(true) {
						zRotLastIteration = zRot; 
						try {
							for(int i = 0; i < POI.size(); i++) {
								final POI p = POI.get(i);
								final int imemo = i;
								handler.post(new Runnable() {
									public void run() { 
										DefaultPOIView view = p.getView();
										if(view == null)
											return;
										AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams)view.getLayoutParams();
										//lp.x = (int)zRot;  
										// lp.y = (int)xRot + 65; 
										double dx = p.getLongitude() - User.getInstance().getUserLocation().getLongitude();
										double dy = p.getLatitude() - User.getInstance().getUserLocation().getLatitude();
										// returns -PI..+PI
										// android-api: dx, dy, sun-api: dy, dx !!!!!
										double angle = Math.atan2(dx, dy);
										// normalize angle to match 0°..360° 
										if(angle < 0)
											angle = Math.PI + (Math.PI + angle);
										angle = Math.toDegrees(angle); 
										double length = Math.sqrt(dx*dx + dy*dy); 

										//if(imemo == 3)
											//t.setText(String.valueOf(zRot));
										
										// 45 ° sichtfeld ca. = 480px
										//lp.x = (int)angle; 
										double factor = 480 / 45;

										double offset = factor * (angle - 90);
										
										lp.x = (int) Math.round(offset - factor * zRot) - 80;
	
										if(User.getInstance().getUserLocation() != null) {
											float dist = User.getInstance().getUserLocation().distanceTo(p.getLocation());
											p.getView().setDistance(dist); 
										}
										p.getView().setLayoutParams(lp);
									}
								});							
							}
							Thread.sleep(1000 / 8); 
						} catch(InterruptedException e) {
							;
						}
					}
				}
			}
		};
		drawingThread = new Thread(drawingRunnable);
		drawingThread.start();
		
		
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
        SensorEventListener listener = new SensorEventListener() {
			public void onSensorChanged(SensorEvent e) {
				//zRot = e.values[0];
				//xRot = e.values[2];
				
				
         		float new_zRot = e.values[0];
				float new_yRot = e.values[2];

				if(e.sensor.getType() == Sensor.TYPE_ORIENTATION) {

        	 			zRot = (float) ((new_zRot * kFilter) + (zRot * (1.0 - kFilter)));

          				xRot = (float) ((new_yRot * kFilter) + (xRot * (1.0 - kFilter)));
                
         	 			if(aboveOrBelow > 0)
             				xRot = xRot * -1;
          		}

				if(e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            			aboveOrBelow = (float) ((new_yRot * kFilter) + (aboveOrBelow * (1.0 - kFilter)));
         		}

				//L.i(String.valueOf(xRot));
				//t.setText(String.valueOf(zRot));
				 
				 
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
	    		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(170, 42, 0, (int)(Math.random() * 200));
	    		contentView.addView(t, lp);				
			}
		});
    }
   
    
    public void onDestroy() {
    	super.onDestroy();
    	POI.clear(); 
    	if(drawingThread != null)
    		drawingThread.interrupt(); 
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