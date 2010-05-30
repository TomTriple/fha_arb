package arb.mportal.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import arb.mportal.R;
import arb.mportal.models.POI;
import arb.mportal.util.L;


/*
 * Main view that displays a so called point of interest-overlay. Internally this class works according to 
 * the state pattern and uses inner classes to represent a state. 
 */
public class DefaultPOIView extends LinearLayout implements View.OnTouchListener { 

	/*
	 * Inner class that represents the state of a closed Layer. Gets delegated touch events 
	 * from the main view. 
	 */
	private class TouchStateClosed implements ITouchState { 
		
		private DefaultPOIView v = null;
		private TextView titleText = null;
		private POI poi = null;
		
		public TouchStateClosed(DefaultPOIView v, POI poi) {
			this.v = v;
			this.poi = poi;
			titleText = new TextView(getContext()); 
			titleText.setTextColor(Color.WHITE); 
			titleText.setTextSize(11); 
			titleText.setPadding(50, 6, 0, 0);  
			setTitle(poi.getName().toUpperCase()+"\n"+(int)poi.getDistance()+" m"); 
		} 
		public void draw(Canvas c) {
			c.drawBitmap(bc, 0,0,p);
		} 
		public boolean onTouch(View v, MotionEvent event) {
			openLayer(); 
			return true; 
		}
		public void setTitle(String title) {
			titleText.setText(title); 
		}
		public void stateTransitionTo() {
			v.addView(titleText, new AbsoluteLayout.LayoutParams(170, 42, 0, 0)); 
		}
		public void setDistance(float dist) { 
			setTitle(poi.getName().toUpperCase()+"\n"+(int)dist+" m");
		}		
	}
	
	
	/*
	 * Inner class that represents the state of an opened Layer. Gets delegated touch events  
	 * from the main view. 
	 */	
	private class TouchStateOpened implements ITouchState { 

		private DefaultPOIView v = null; 
		private TextView titleText = null;
		private TextView dataText = null; 
		private POI poi = null;
		
		public TouchStateOpened(DefaultPOIView v, POI poi) {
			this.v = v;
			this.poi = poi;
			titleText = new TextView(getContext());  
			titleText.setTextColor(Color.WHITE); 
			titleText.setTextSize(11);
			titleText.setPadding(50, 6, 0, 0);

			dataText = new TextView(getContext()); 
			dataText.setTextColor(Color.WHITE); 
			dataText.setTextSize(11); 
			dataText.setPadding(6, 6, 6, 6);
			
			setTitle(poi.getName().toUpperCase()+"\n"+(int)poi.getDistance()+" m"); 
			setData(poi.getDescription());
		}
		public void draw(Canvas c) {
			c.drawBitmap(bo, 0,0,p);
		}
		public boolean onTouch(View v, MotionEvent event) {
			closeLayer();   
			return true;  
		} 
		
		public void stateTransitionTo() {
			v.addView(titleText, new AbsoluteLayout.LayoutParams(170, 42, 0, 0)); 
			v.addView(dataText, new AbsoluteLayout.LayoutParams(170, 58, 0, 0));			
		}
		
		public void setTitle(String title) {
			titleText.setText(title); 
		}
		
		public void setData(String data) {
			dataText.setText(data);
		}
		
		public void setDistance(float dist) {
			setTitle(poi.getName().toUpperCase()+"\n"+(int)dist+" m");
		}
	}

	
	protected ITouchState currentState = null;
	protected boolean touchDown = false;
	protected ITouchState stateClosed = null; 
	protected ITouchState stateOpened = null; 
	
	// Inner classes have references to these static attributes 
	protected static Bitmap bo = null;
	protected static Bitmap bc = null;
	protected static Paint p = new Paint();
	
	public DefaultPOIView(Context c, POI poi) {
		super(c);
		// needed to perform onDraw() in a LinearLayout subclass 
		setWillNotDraw(false); 
		setOrientation(VERTICAL);
		stateClosed = new TouchStateClosed(this, poi);
		stateOpened = new TouchStateOpened(this, poi);
		setOnTouchListener(this);
		setLayoutParams(new AbsoluteLayout.LayoutParams(170, 42, 0, 0)); 
		if(bo == null) {
			bo = BitmapFactory.decodeResource(getResources(), R.drawable.arb_big);			
		}
		if(bc == null) {
			bc = BitmapFactory.decodeResource(getResources(), R.drawable.arb_small); 			
		} 
		closeLayer();  
	}

	
	public void stateTransitionTo(ITouchState newState) {
		currentState = newState;
		currentState.stateTransitionTo(); 
	}
	
	
	public void closeLayer() {
		AbsoluteLayout.LayoutParams l = (AbsoluteLayout.LayoutParams)getLayoutParams();
		l.width = 170;
		l.height = 42;
		setLayoutParams(l);
		stateTransitionTo(stateClosed); 
	} 
	
	
	public void openLayer() {
		AbsoluteLayout.LayoutParams l = (AbsoluteLayout.LayoutParams)getLayoutParams();
		l.width = 170;
		l.height = 100; 
		setLayoutParams(l);		
		stateTransitionTo(stateOpened);  
	}
	
	
	public boolean onTouch(View v, MotionEvent e) {
		if(touchDown == false) { 
			touchDown = true;
			removeAllViews(); 
			return currentState.onTouch(v, e);			
		} else if(e.getAction() == MotionEvent.ACTION_UP) { 
			touchDown = false;
		}
		return true; 
	} 
		
	
	@Override  
	public void onDraw(Canvas c) {  
		super.onDraw(c); 
		currentState.draw(c); 
	}
	
	
	public void setDistance(float dist) {
		currentState.setDistance(dist); 
	}
}
