package breakout.radioactivity;

import java.awt.Color;
import java.util.*;

import breakout.utils.Point;
import breakout.utils.Vector;
import breakout.utils.Rect;

public class Alpha {
	
	private Point center;
	private int diameter;
	private Vector velocity;
	
	private final Color color;
	
	private Set<Ball> linkedBalls = new HashSet<Ball>();
	
	public Alpha(Point center, int diameter, Vector initVelocity) {
		this.center = center;
		this.diameter = diameter;
		this.velocity = initVelocity;
		this.color = Color.LIGHT_GRAY;
	}
	
	public void addBall(Ball ball) {
		linkedBalls.add(ball);
	}
	
	public void removeBall(Ball ball) {
		linkedBalls.remove(ball);
	}
	
	Set<Ball> getBallsInternal(){
		return Set.copyOf(linkedBalls);
	}
	
	public Set<Ball> getBalls(){
		return getBallsInternal();
	}
	
	public Point getCenter() {
		return center;
	}
	
	public int getDiameter() {
		return diameter;
	}
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setLocation(Point center, int diameter) {
		this.center = center;
		this.diameter = diameter;
	}
	
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	public Alpha clone() {
		Alpha retAlpha = new Alpha(center, diameter, velocity);
		for (Ball ball: getBalls()) {
			ball.linkTo(retAlpha);
		}
		return retAlpha;
	}
	
	public void changeAlphaFromBall(Ball ball) {
		// Bounce effecten op een temp ball uitvoeren, dan die stats naar de alpha zetten
		if (!center.equals(ball.getCenter()))
			center = ball.getCenter();
		if (!velocity.equals(ball.getVelocity()))
			velocity = ball.getVelocity();
	}
	
	public void bounceWall(int wallNumber) {
		
		if (wallNumber == 1) {
			// leftWall
			velocity = velocity.mirrorOver(new Vector(1, 0));
			return;
		}
		if (wallNumber == 2) {
			// topWall
			velocity = velocity.mirrorOver(new Vector(0, 1));
			return;
		}
		if (wallNumber == 3) {
			// righttWall
			velocity = velocity.mirrorOver(new Vector(-1, 0));
		}
	}
	
}
