package breakout.radioactivity;

import java.awt.Color;

import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

import java.util.*;


public abstract class Ball {
	
	private Point center;
	private final int diameter;
	private Vector velocity;
	
	private final Color color;
	
	private int eCharge = 1;
	private Set<Alpha> linkedAlphas = new HashSet<Alpha>();
	
	public Ball(Point center, int diameter, Vector velocity, Color color) {
		this.center = center;
		this.diameter = Math.abs(diameter);
		this.velocity = velocity;
		this.color = color;
	}
	
	public int getDiameter() {
		return diameter;
	}
	
	public Point getCenter() {
		return center;
	}
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Ball superchargedTimeHandler(int elapsedTime, int maxTime) {
		return this;
	}
	
	public int getTime() {
		return -1;
	}
	
	public int getEcharge() {
		return eCharge;
	}
	
	public void linkTo(Alpha alpha) {
		linkedAlphas.add(alpha);
		alpha.addBall(this);
		for(Ball ball: alpha.getBalls()) {
			ball.calculateAndSetEcharge();
		}
	}
	
	public void unLink(Alpha alpha) {
		alpha.removeBall(this);
		linkedAlphas.remove(alpha);
		for(Ball ball: alpha.getBalls()) {
			ball.calculateAndSetEcharge();
		}
	}
	
	Set<Alpha> getAlphasInternal(){
		return Set.copyOf(linkedAlphas);
	}
	
	public Set<Alpha> getAlphas(){
		return getAlphasInternal();
	}
	
	public void calculateAndSetEcharge() {
		int newEcharge = 1;
		for (Alpha alpha: linkedAlphas) {
			newEcharge = Math.max(newEcharge, alpha.getBalls().size());
		}
		if (linkedAlphas.size() % 2 != 0)
			eCharge = -newEcharge;
		else eCharge = newEcharge;
		
	}
	
	public abstract Ball cloneBallWithChangedVelocity(Vector addedVelocity);
	
	public abstract Ball clone();
	
	public void moveBall(Point br, int timeElapsed) {
		Point newCenter = center.plus(velocity.scaled(timeElapsed));
		if (newCenter.getX() - diameter/2 < 0) {
			newCenter = new Point(diameter/2, newCenter.getY());
		}
		if (newCenter.getX() + diameter/2 > br.getX()) {
			newCenter = new Point(br.getX() - diameter/2, newCenter.getY());
		}
		
		if (newCenter.getY() + diameter/2 > br.getY()) {
			newCenter = new Point(newCenter.getX(), br.getY() - diameter/2);
		}
		
		if (newCenter.getY() - diameter/2 < 0) {
			newCenter = new Point(newCenter.getX(), diameter/2);
		}
		center = newCenter;
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
	
	public void linkedAlphaHitWall(Alpha alpha) {
		velocity = Vector.magnetSpeed(alpha.getCenter(), center, eCharge, velocity);
	}
	
	public void bouncePaddle(Vector addedVelocity, int paddleSideNumber) {
		if (paddleSideNumber == 1) {
			// leftSide
			velocity = velocity.mirrorOver(new Vector(-1, 0)).plus(addedVelocity);
			return;
		}
		if (paddleSideNumber == 2) {
			// topSide
			velocity = velocity.mirrorOver(new Vector(0, -1)).plus(addedVelocity);
			return;
		}
		if (paddleSideNumber == 3) {
			// rightSide
			velocity = velocity.mirrorOver(new Vector(1, 0)).plus(addedVelocity);
		}
		if (paddleSideNumber == 4) {
			// bottomSide
			velocity = velocity.mirrorOver(new Vector(0, 1).plus(addedVelocity));
		}
	}
	
	public boolean raakDottedProduct(Vector velocity, Vector n) {
		Vector v = velocity.scaled(-1);
		return v.product(n) >= 0;
	}
	
	public void hitBlock(Rect rect, boolean destroyed) {
			if (raaktRechthoek(rect, 1)) {
				velocity = velocity.mirrorOver(new Vector(0, 1));
				return;
			}
			if (raaktRechthoek(rect, 2)) {
				velocity = velocity.mirrorOver(new Vector(-1, 0));
				return;
			}
			if (raaktRechthoek(rect, 3)) {
				velocity = velocity.mirrorOver(new Vector(0, -1));
				return;
			}
			if (raaktRechthoek(rect, 4))
				velocity = velocity.mirrorOver(new Vector(1, 0));
		
	}
	
	public boolean raaktRechthoek(Rect rechthoek, int sideNumber) {
		Point ballOnderstePunt = new Point(center.getX(), center.getY() + diameter/2);
		Point ballBovenstePunt = new Point(center.getX(), center.getY() - diameter/2);
		Point ballLinksePunt = new Point(center.getX() - diameter/2, center.getY());
		Point ballRechtsePunt = new Point(center.getX() + diameter/2, center.getY());
		// bottomSide
		if (sideNumber == 1) {
			if (ballRechtsePunt.getX() >= rechthoek.getTopLeft().getX() && ballLinksePunt.getX() <= rechthoek.getBottomRight().getX() && center.getY() >= rechthoek.getBottomRight().getY() && ballBovenstePunt.getY() <= rechthoek.getBottomRight().getY()) {
				Point linksOnder = new Point(rechthoek.getTopLeft().getX(), rechthoek.getBottomRight().getY());
				Point rechtsOnder = rechthoek.getBottomRight();
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(linksOnder, rechtsOnder)) {
					return raakDottedProduct(velocity, new Vector(0, 1));
				}
			}
		}
		// leftSide
		if (sideNumber == 2) {
			if (center.getX() <= rechthoek.getTopLeft().getX() && ballRechtsePunt.getX() >= rechthoek.getTopLeft().getX() && ballBovenstePunt.getY() <= rechthoek.getBottomRight().getY() && ballOnderstePunt.getY() >= rechthoek.getTopLeft().getY()) {
				Point linksBoven = rechthoek.getTopLeft();
				Point linksOnder = new Point(rechthoek.getTopLeft().getX(), rechthoek.getBottomRight().getY());
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(linksBoven, linksOnder)) {
					return raakDottedProduct(velocity, new Vector(-1, 0));
				}
			}
		}
		//topSide
		if (sideNumber == 3) {
			if (ballRechtsePunt.getX() >= rechthoek.getTopLeft().getX() && ballLinksePunt.getX() <= rechthoek.getBottomRight().getX() && center.getY() <= rechthoek.getTopLeft().getY() && ballOnderstePunt.getY() >= rechthoek.getTopLeft().getY()) {
				Point rechthoekRechtsBovenPunt = new Point(rechthoek.getBottomRight().getX(), rechthoek.getTopLeft().getY());
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(rechthoek.getTopLeft(), rechthoekRechtsBovenPunt)) {
					return raakDottedProduct(velocity, new Vector(0, -1));
				}
			}
		}
		//rightSide
		if (sideNumber == 4) {
			if (ballOnderstePunt.getY() >= rechthoek.getTopLeft().getY() && ballBovenstePunt.getY() <= rechthoek.getBottomRight().getY() && center.getX() >= rechthoek.getBottomRight().getX() && ballLinksePunt.getX() <= rechthoek.getBottomRight().getX()) {
				Point rechthoekRechtsBovenPunt = new Point(rechthoek.getBottomRight().getX(), rechthoek.getTopLeft().getY());
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(rechthoekRechtsBovenPunt, rechthoek.getBottomRight())) {
					return raakDottedProduct(velocity, new Vector(1, 0));
				}
			}
		}
		return false;
	}
	
	private int distanceCenterTo2Points(Point punt1, Point punt2) {
		int x1 = punt1.getX();
		int x2 = punt2.getX();
		int y1 = punt1.getY();
		int y2 = punt2.getY();
		int x0 = center.getX();
		int y0 = center.getY();
		int bovenEquation = (x2 - x1)*(y1 - y0) - (x1 - x0)*(y2 - y1);
		int onderEquation = (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1);
		int equation = (bovenEquation)*(bovenEquation) / (onderEquation);
		
		return equation;
	}
	
}
