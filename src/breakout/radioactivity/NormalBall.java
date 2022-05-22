package breakout.radioactivity;

import java.awt.Color;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;


public class NormalBall extends Ball{
	
	public NormalBall(Point center, int diameter, Vector velocity) {
		super(center, diameter, velocity, Color.WHITE);
	}
	
	public Ball cloneBallWithChangedVelocity(Vector addedVelocity) {
		return new NormalBall(getCenter(), getDiameter(), getVelocity().plus(addedVelocity));
	}
	
	public boolean equals(Object obj) {
		return super.equals(obj);
	
	}
	
}
