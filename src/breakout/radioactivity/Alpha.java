package breakout.radioactivity;

import java.awt.Color;
import java.util.*;

import breakout.utils.Point;
import breakout.utils.Vector;
import breakout.utils.Rect;

public class Alpha {
	
	private Point center;
	private final int diameter;
	private Vector velocity;
	
	private final Color color;
	
	Set<Ball> linkedBalls;
	int eCharge = 1;
	
	public Alpha(Point center, int diameter, Vector initVelocity, Color color) {
		this.center = center;
		this.diameter = diameter;
		this.velocity = initVelocity;
		this.color = color;
	}
	
	
	
	
}
