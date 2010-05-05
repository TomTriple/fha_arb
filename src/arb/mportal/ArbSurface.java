package arb.mportal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ArbSurface extends SurfaceView implements SurfaceHolder.Callback {

	private MainActivity m = null;
	
	
	public ArbSurface(Context c) {
		super(c);   
		init(); 
	}
	
	public ArbSurface(Context c, AttributeSet s) {
		super(c, s);
		init();
	}	
	
	public ArbSurface(Context c, AttributeSet s, int d) {
		super(c, s, d);
		init();
	}	
	 
	private void init() {
        SurfaceHolder holder = getHolder();  
        holder.addCallback(this);  
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        holder.setFixedSize(100, 100);
	}		
	
	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		;
	}

	
	public void surfaceCreated(SurfaceHolder holder) {
		m.surfaceCreated(holder);
	} 

	
	public void surfaceDestroyed(SurfaceHolder holder) {
		m.surfaceDestroyed(holder); 
	}
	
	
	public void setCreationCallbacks(MainActivity m) {
		this.m = m;
	}
	
}
