package breakout.radioactivity;

import java.awt.Color;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;


public class SuperchargedBall extends Ball {
	
	private int time;
	
	public SuperchargedBall(Point center, int diameter, Vector velocity, int time){
		super(center, diameter, velocity, Color.GREEN);
		this.time = time;
	}
	
	public void hitBlock(Rect rect, boolean destroyed) {
		if (destroyed == false) 
			super.hitBlock(rect, destroyed);
		else return;
	}
	
	public int getTime() {
		return time;
	}
	
	public Ball superchargedTimeHandler(int elapsedTime, int maxTime) {
		if (time + elapsedTime >= maxTime)
			return new NormalBall(getCenter(), getDiameter(), getVelocity());
		time += elapsedTime;
		return this;
	}
	
	public Ball cloneBallWithChangedVelocity(Vector addedVelocity) {
		return new SuperchargedBall(getCenter(), getDiameter(), getVelocity().plus(addedVelocity), time);
	}

	public boolean equals(Object obj) {
		return super.equals(obj) && 
				((SuperchargedBall)obj).getTime() == getTime();
	}
	
}


