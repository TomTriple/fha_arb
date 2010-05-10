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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import arb.mportal.MainActivity;
import arb.mportal.R;
import arb.mportal.models.POI;
 

public class DefaultPOIView extends LinearLayout implements View.OnTouchListener { 

	
	private class TouchStateClosed implements ITouchState { 
		private DefaultPOIView v = null;
		public TouchStateClosed(DefaultPOIView v) {
			this.v = v; 
		} 
		public void draw(Canvas c) {    
			//c.drawBitmap(bc, 0,0,p);
		}
		public boolean onTouch(View v, MotionEvent event) {
			AbsoluteLayout.LayoutParams l = (AbsoluteLayout.LayoutParams)this.v.getLayoutParams();
			l.width = 170;
			l.height = 100;  
			this.v.setLayoutParams(l);
			this.v.stateTransitionTo(DefaultPOIView.STATE_OPENED); 
			return true;
		}
	}
	
	
	private class TouchStateOpened implements ITouchState { 
		private DefaultPOIView v = null; 
		public TouchStateOpened(DefaultPOIView v) {
			this.v = v; 
		} 
		public void draw(Canvas c) {       
			//c.drawBitmap(bo, 0,0,p);  
		}
		public boolean onTouch(View v, MotionEvent event) {  
			AbsoluteLayout.LayoutParams l = (AbsoluteLayout.LayoutParams)this.v.getLayoutParams();
			l.width = 170;
			l.height = 42;
			this.v.setLayoutParams(l);
			this.v.stateTransitionTo(DefaultPOIView.STATE_CLOSED);  
			return true; 
		} 
	}

	
	protected TextView titleText = null;
	protected TextView data = null; 
	 
	protected ITouchState currentState = null;
	protected boolean touchDown = false;
	public static ITouchState STATE_CLOSED = null;
	public static ITouchState STATE_OPENED = null; 
	private static Paint p = new Paint(); 
	private static Bitmap bc = null; 
	private static Bitmap bo = null;	
	
	 
	public DefaultPOIView(Context c, POI poi) {
		super(c);
		setOrientation(VERTICAL);  

		bo = BitmapFactory.decodeResource(getResources(), R.drawable.arb_big);
		bc = BitmapFactory.decodeResource(getResources(), R.drawable.arb_small);

		titleText = new TextView(getContext()) { 
			@Override
			public void onDraw(Canvas c) {
				c.drawBitmap(bc, 0, 0, p);
				super.onDraw(c); 
			}
		}; 
		titleText.setTextColor(Color.WHITE); 
		titleText.setTextSize(11); 
		setTitle(poi.getName().toUpperCase()+"\n"+(int)poi.getDistance()+" m");     
		titleText.setPadding(50, 6, 0, 0); 

		data = new TextView(getContext()) {
			@Override 
			public void onDraw(Canvas c) {
				c.drawBitmap(bo, 0, 0, p);  
				super.onDraw(c); 
			}
		}; 
		data.setTextColor(Color.WHITE); 
		data.setTextSize(11); 
		data.setPadding(6, 6, 6, 6);
		data.setText(poi.getDescription()); 
		
		addView(titleText, new AbsoluteLayout.LayoutParams(170, 42, 0, 0)); 
		addView(data, new AbsoluteLayout.LayoutParams(170, 58, 0, 0));
		
		
		STATE_CLOSED = new TouchStateClosed(this);
		STATE_OPENED = new TouchStateOpened(this);
		setOnTouchListener(this);
		setLayoutParams(new ViewGroup.LayoutParams(170, 42));   
		stateTransitionTo(STATE_CLOSED); 
		
		setDrawingCacheEnabled(true); 
	}
	
	
	public void stateTransitionTo(ITouchState newState) {
		currentState = newState; 
	}
	
	
	public boolean onTouch(View v, MotionEvent e) {
		if(touchDown == false) { 
			touchDown = true; 
			// MainActivity.t.setText(String.valueOf(System.currentTimeMillis())); 
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
	
	
	@Override
	public void draw(Canvas c) {
		super.draw(c);  
		/*Bitmap cache = getDrawingCache();
		if(cache != null) 
			c.drawBitmap(cache, 0, 0, p);*/   
	}
	
	
	public void setTitle(String text) { 
		titleText.setText(text);
	}
	
	public void setDistance() {
		;
	}
}
