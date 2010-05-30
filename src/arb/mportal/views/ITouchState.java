package arb.mportal.views;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

interface ITouchState { 
  
	public void draw(Canvas c);
	public boolean onTouch(View v, MotionEvent e);
	public void setDistance(float dist); 
	public void stateTransitionTo(); 
}
