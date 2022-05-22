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
		if (time + elapsedTime >= maxTime) {
			Ball retBall = new NormalBall(getCenter(), getDiameter(), getVelocity());
			for (Alpha alpha: getAlphas()) {
				retBall.linkTo(alpha);
				unLink(alpha);
			}
		}
		time += elapsedTime;
		return this;
	}
	
	public Ball cloneBallWithChangedVelocity(Vector addedVelocity) {
		return new SuperchargedBall(getCenter(), getDiameter(), getVelocity().plus(addedVelocity), time);
	}
	
	public Ball clone() {
		Ball retBall = new SuperchargedBall(getCenter(), getDiameter(), getVelocity(), time);
		for (Alpha alpha: getAlphas()) {
			retBall.linkTo(alpha);
		}
		return retBall;
	}
}


