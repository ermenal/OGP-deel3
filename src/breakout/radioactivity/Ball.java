package breakout.radioactivity;

import java.awt.Color;

import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;
import logicalcollections.LogicalSet;

import java.util.stream.IntStream;

import java.util.*;

/**
 * This class represents a ball on a 2D-grid, as part of a ball-alpha graph
 *
 * @invar | getCenter() != null
 * @invar | getDiameter() >= 0
 * @invar | getVelocity() != null
 * @invar | getTime() == -1 || getTime() >= 0
 * @invar | getColor() == Color.GREEN || getColor() == Color.WHITE
 * @invar | getEcharge() != 0
 * 
 * @invar | getAlphas() != null
 * @invar If a ball is linked to an alpha, that alpha is not null and that alpha is also linked to the ball
 * 		| getAlphas().stream().allMatch(a -> a != null && a.getBalls().contains(this))
 */

public abstract class Ball {
	
	public static final Vector[] MIRROR_VECTORS = {new Vector(0, 1), new Vector(-1, 0), new Vector(0, -1), new Vector(1, 0)};
	
	/**
	 * @invar | center != null
	 * @invar | diameter >= 0
	 * @invar | velocity != null
	 * @invar | color != null
	 * @invar | color == Color.GREEN || color == Color.WHITE
	 * @invar | eCharge != 0
	 */
	
	private Point center;
	private int diameter;
	private Vector velocity;
	private final Color color;
	private int eCharge = 1;
	
	/**
     * @invar | linkedAlphas!= null
     * @invar | linkedAlphas.stream().allMatch(a -> a != null)
     * 
     * @representationObject
     */
	
	private Set<Alpha> linkedAlphas = new HashSet<Alpha>();
	
	/**
	 * Constructs a new ball with the given center, diameter, velocity and color
	 * 
	 * @pre | center != null
	 * @pre | velocity != null
	 * @pre | color == Color.WHITE || color == Color.GREEN
	 * 
	 * @post | getCenter() == center
	 * @post | getDiameter() == Math.abs(diameter)
	 * @post | getVelocity() == velocity
	 * @post | getColor() == color
	 * 
	 * @post This ball isn't linked to any alphas
	 * 		| getAlphas().isEmpty()
	 * @post The ball's eCharge is 1
	 * 		| getEcharge() == 1
	 */
	
	public Ball(Point center, int diameter, Vector velocity, Color color) {
		this.center = center;
		this.diameter = Math.abs(diameter);
		this.velocity = velocity;
		this.color = color;
	}
	
	/** Returns this ball's diameter */
	
	public int getDiameter() {
		return diameter;
	}
	
	/** Returns this ball's center point */
	
	public Point getCenter() {
		return center;
	}
	
	/** Returns this ball's velocity */
	
	public Vector getVelocity() {
		return velocity;
	}
	
	/** 
	 * Returns this ball's color 
	 * 
	 * @immutable This object is associated with the same color throughout its lifetime
	 */ 
	
	public Color getColor() {
		return color;
	}	
	
	/**
	 * Returns the amount of time the ball has been supercharged for in milliseconds. If the ball is not supercharged, the result will be {@code -1}.
	 * This method is used for formal documentation.
	 * 
	 * @post The result is -1 for a normal ball, or not smaller than 0 for a supercharged ball
	 * 		| result == -1 && getClass().equals(NormalBall.class) || 
	 * 		| result >= 0 && getClass().equals(SuperchargedBall.class)
	 */
	
	public int getTime() {
		return -1;
	}
	
	/** Returns this ball's echarge */
	
	public int getEcharge() {
		return eCharge;
	}
	
	/** 
	 * Changes the center and diameter of this ball to the given center and diameter
	 *  
	 *  @pre | center != null
	 *  
	 *  @post | getCenter() == center
	 *  @post | getDiameter() == diameter
	 */
	
	public void setLocation(Point center, int diameter) {
		this.center = center;
		this.diameter = diameter;
	}
	
	/**
	 * Changes the velocity of this ball to the given velocity
	 * 
	 * @pre velocity != null
	 * 
	 * @post getVelocity() == velocity
	 */
	
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Links this ball to the given alpha
	 * 
	 * @pre | alpha != null
	 * 
	 * @mutates_properties | this.getAlphas(), alpha.getBalls(), this.getEcharge()
	 * 
	 * @post The given alpha's linked balls set equals its old linked balls plus this ball
	 * 		| alpha.getBalls().equals(LogicalSet.plus(old(alpha.getBalls()), this))
	 * 
	 * @post This ball's linked alphas set equals its old linked alphas plus the given alpha
	 * 		| this.getAlphas().equals(LogicalSet.plus(old(this.getAlphas()), alpha))
	 * 
	 * @post This ball's eCharge has been re-calculated as well as the eCharge of all balls linked to the given alpha
	 * 		| alpha.getBalls().stream().allMatch(b -> b.getEcharge() == b.calculateAndSetEcharge())
	 */
	
	public void linkTo(Alpha alpha) {
		linkedAlphas.add(alpha);
		alpha.addBall(this);
		for(Ball ball: alpha.getBalls()) {
			ball.calculateAndSetEcharge();
		}
	}
	
	/**
	 * Unlinks this ball from the given alpha
	 * 
	 * @pre | alpha != null
	 * 
	 * @mutates_properties | this.getAlphas(), alpha.getBalls(), this.getEcharge()
	 * 
	 * @post The given alpha's linked balls set equals its old linked balls minus this ball
	 * 		| alpha.getBalls().equals(LogicalSet.minus(old(alpha.getBalls()), this))
	 * 
	 * @post This ball's linked alphas set equals its old linked alphas minus the given alpha
	 * 		| this.getAlphas().equals(LogicalSet.minus(old(this.getAlphas()), alpha))
	 * 
	 * @post This ball's eCharge has been re-calculated as well as the eCharge of all balls still linked to the given alpha
	 * 		| getEcharge() == calculateAndSetEcharge() && 
	 * 		| alpha.getBalls().stream().allMatch(b -> b.getEcharge() == b.calculateAndSetEcharge())
	 */
	
	public void unLink(Alpha alpha) {
		alpha.removeBall(this);
		linkedAlphas.remove(alpha);
		for(Ball ball: alpha.getBalls()) {
			ball.calculateAndSetEcharge();
		}
		calculateAndSetEcharge();
	}
	
	/**
	 * Returns a shallow copy of the set of the alphas linked to this ball
	 * 
	 * @invar | getAlphasInternal().stream().allMatch(a -> a.getBallsInternal().contains(this))
	 * 
	 * @post | result != null && result.stream().allMatch(a -> a != null)
	 * 
	 * @creates | result
	 * 
	 * @peerObjects (package-level)
	 */
	
	Set<Alpha> getAlphasInternal(){
		return Set.copyOf(linkedAlphas);
	}
	
	/**
	 * Returns a shallow copy of the set of the alphas linked to this ball
	 * 
	 * @post | result != null
	 * 
	 * @creates | result
	 * 
	 * @peerObjects
	 */
	
	public Set<Alpha> getAlphas(){
		return getAlphasInternal();
	}
	
	/**
	 * Calculates the ball's eCharge, changes its current eCharge to the newly calculated one and returns the newly calculated one 
	 * 
	 * @mutates_properties | this.getEcharge()
	 * 
	 * @inspects | ...getAlphas()
	 * 
	 * @post | result != 0
	 * 
	 * @post The new eCharge is positive if this ball is linked to an even amount of alphas, negative otherwise
	 * 		| result > 0 && (getAlphas().size() % 2 == 0) ||
	 * 		| result < 0 && (getAlphas().size() % 2 == 1)
	 * 
	 * @post The new eCharge's absolute value is the amount of links to balls of the linked alpha with the most amount of links to balls, 
	 * 		 or 1 if this ball isn't linked to any alphas.
	 * 		| IntStream.range(0, getAlphas().size()).anyMatch(i -> getAlphas().stream().toArray(Alpha[]::new)[i].getBalls().size() == Math.abs(result)) || 
	 * 		|	result == 1 && getAlphas().size() == 0
	 */
	
	public int calculateAndSetEcharge() {
		int newEcharge = 1;
		for (Alpha alpha: linkedAlphas) {
			newEcharge = Math.max(newEcharge, alpha.getBalls().size());
		}
		if (linkedAlphas.size() % 2 != 0) {
			eCharge = -newEcharge;
			return -newEcharge;
		}else{ eCharge = newEcharge;
			return newEcharge;
		}
	}
	
	/**
	 * Returns either a normal ball or a supercharged ball, that has an altered velocity in accordance with {@code addedVelocity}.
	 * 
	 * @pre Argument {@code addedVelocity} is not {@code null}
	 * 		| addedVelocity != null
	 * 
	 * @inspects | this 
	 * 
	 * @creates | result
	 * 
	 * @post The result is the same kind of ball as {@code this}
	 * 		| result.getClass() == getClass()
	 * 
	 * @post The resulting ball's center, time it has been supercharged for and diameter are the same as {@code this}.
	 * 		| result.getCenter().equals(getCenter()) &&
	 * 		| result.getDiameter() == getDiameter() && 
	 * 		| result.getTime() == getTime()
	 * 
	 * @post The resulting ball's velocity is the result of adding {@code addedVelocity} to this ball's velocity.
	 * 		| result.getVelocity().equals(getVelocity().plus(addedVelocity))
	 * 
	 * @post The resulting ball is linked to no alphas and it has an eCharge of 1
	 * 		| result.getEcharge() == 1 && result.getAlphas().size() == 0
	 */
	
	public abstract Ball cloneBallWithChangedVelocity(Vector addedVelocity);
	
	/**
	 * Returns a new ball that is a clone of {@code this}.
	 * 
	 * @mutates_properties This balls' linked alphas' linked balls sets have been mutated, as well as the eCharge of any ball linked to any alphas linked to this ball
	 * 		| (...getAlphas()).getBalls(), (...(getAlphas().stream().flatMap(a -> a.getBalls().stream()).toList())).getEcharge()
	 * 
	 * @inspects | this
	 * 
	 * @creates | result
	 * 
	 * @post The resulting ball is the same kind of ball with the same properties as {@code this}
	 * 		| result.getClass() == getClass() &&
	 * 		| result.getCenter() == getCenter() &&
	 * 		| result.getDiameter() == getDiameter() &&
	 * 		| result.getVelocity() == getVelocity()
	 * 
	 * @post The resulting ball is linked to the same alphas as {@code this} 
	 * 		| result.getAlphas().equals(getAlphas()) && 
	 * 		| result.getEcharge() == getEcharge()
	 * 
	 * @post every ball that is linked to an alpha that is linked to this ball, has had its eCharge recalculated and changed.
	 * 		| IntStream.range(0, getAlphas().size()).allMatch(i -> 
	 * 		|	IntStream.range(0, getAlphas().stream().toArray(Alpha[]::new)[i].getBalls().size()).
	 * 		|		allMatch(b -> 
	 * 		|			getAlphas().stream().toArray(Alpha[]::new)[i].getBalls().stream().toArray(Ball[]::new)[b].getEcharge() == 
	 * 		|			getAlphas().stream().toArray(Alpha[]::new)[i].getBalls().stream().toArray(Ball[]::new)[b].calculateAndSetEcharge() ) )
	 */
	
	public abstract Ball clone();
	
	/**
	 * Checks if {@code obj} is the same class and has the same properties as {@code this}. 
	 * 
	 * @post The result is {@code true} if {@code obj} is a {@code SuperchargedBall} or a {@code NormalBall} with the same properties as {@code this}.
	 * 		 Returns {@code false} if this is not the case or if {@code obj} is {@code null}.
	 * 		| result == ( (obj != null) && 
	 * 		|		( obj.getClass() == getClass() &&
	 * 		|		((Ball)obj).getCenter().equals(getCenter()) &&
	 * 		|		((Ball)obj).getDiameter() == getDiameter() &&
	 * 		|		((Ball)obj).getVelocity().equals(getVelocity()) &&
	 * 		|		((Ball)obj).getAlphas().size() == getAlphas().size() && 
			|		((Ball)obj).getEcharge() == getEcharge() && (
	 * 		|			getClass() == NormalBall.class ||
	 * 		|			getClass() == SuperchargedBall.class &&
	 * 		|				((SuperchargedBall)obj).getTime() == getTime() ) ) )
	 */
	
	public boolean equalContent(Object obj) {
		if (obj == null)
			return false;
		return obj.getClass() == getClass() && 
				((Ball)obj).getCenter().equals(center) &&
				((Ball)obj).getDiameter() == diameter &&
				((Ball)obj).getVelocity().equals(velocity) && 
				((Ball)obj).getAlphas().size() == linkedAlphas.size() && 
				((Ball)obj).getEcharge() == eCharge;	
				
	}
	
	/**
	 * Returns {@code this} or a new normal ball, depending on whether or not the ball is currently supercharged and for how long.
	 * 
	 * @pre Argument {@code maxTime} should not be less than 0.
	 * 		| maxTime >= 0
	 * @pre Argument {@code elapsedTime} should be greater than 0.
	 * 		| elapsedTime > 0
	 * 
	 * @mutates_properties | this.getTime(), this.getAlphas(), (...getAlphas()).getBalls()
	 * 
	 * @creates | result
	 * 
	 * @post The resulting ball's center, diameter and velocity have remained unchanged.
	 * 		| result.getCenter() == getCenter() && 
	 * 		| result.getDiameter() == getDiameter() && 
	 * 		| result.getVelocity() == getVelocity()
	 * 
	 * @post If this ball isn't supercharged, {@code this} is returned. 
	 * 		 If this ball is supercharged, a new normal ball is returned if adding the elapsed time to this ball's time 
	 * 		 it has been supercharged for would result in a value larger than or equal to {@code maxTime}. 
	 * 		 If this doesn't result in a value larger than or equal to {@code maxTime}, the elapsed time is added onto the ball's current time and {@code this} is returned.
	 * 		| result == this && getTime() < maxTime ||
	 * 		| result.getClass().equals(NormalBall.class) && getTime() + elapsedTime >= maxTime
	 *
	 * @post If this ball is supercharged and would have been supercharged for too long if {@code elapsedTime} was added, this ball gets unlinked from all alphas 
	 * 		 and the newly created ball gets linked to all alphas. Otherwise, its linked alphas remain unchanged.
	 * 		| result == this && getAlphas().equals(old(getAlphas())) || 
	 * 		| result.getClass() == NormalBall.class && 
	 * 		| 	result.getAlphas().equals(old(getAlphas())) && 
	 * 		|	getAlphas().isEmpty()
	 *
	 */
	
	public Ball superchargedTimeHandler(int elapsedTime, int maxTime) {
		return this;
	}
	
	/** 
	 * Changes the ball's center according with its velocity and the amount of milliseconds since the last time it moved.
	 * 
	 * @pre Argument {@code br} is not {@code null} 
	 * 		| br != null
	 * @pre Argument {@timeElapsed} is greater than 0
	 * 		| timeElapsed > 0
	 * 
	 * @mutates_properties | getCenter()
	 * 
	 * @inspects | this 
	 * 
	 * @post The ball has moved according to its velocity and the time since it last moved, keeping in mind that it can't go outside of the field.
	 * 		| getCenter().getX() == old(getCenter()).plus(getVelocity().scaled(timeElapsed)).getX()  || 
	 * 		| getCenter().getX() == getDiameter()/2  || 
	 * 		| getCenter().getX() == br.getX() - getDiameter()/2 ||
	 * 		| getCenter().getY() == getDiameter()/2  ||
	 * 		| getCenter().getY() == br.getY() - getDiameter()/2
	 * 
	 */
	
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
	
	/**
	 * Changes the ball's velocity after one of its linked alphas hit a wall.
	 * 
	 * @pre Argument {@code alpha} is not {@code null} 
	 * 		| alpha != null
	 * 
	 * @mutates_properties | getVelocity()
	 * 
	 * @inspects | alpha
	 * 
	 * @post The ball's new velocity has been calculated using the static method {@code magnetSpeed} in Vector. 
	 * 		| getVelocity().equals(Vector.magnetSpeed(alpha.getCenter(), getCenter(), getEcharge(), old(getVelocity())))
	 */
	
	public void linkedAlphaHitWall(Alpha alpha) {
		velocity = Vector.magnetSpeed(alpha.getCenter(), center, eCharge, velocity);
	}
	
	/**
	 * Returns the dot product of the vector {@code n} and the vector constructed by scaling {@code velocity} with -1. 
	 * 
	 * @pre {@code velocity} should not be null
	 * 		| velocity != null
	 * @pre {@code n} should not be null
	 * 		| n != null
	 * 
	 * @post The result is true if the angle between the velocity and vector v is sharp
	 * 		| result == velocity.scaled(-1).product(n) >= 0
	 */
	
	public boolean raakDotProduct(Vector velocity, Vector n) {
		Vector v = velocity.scaled(-1);
		return v.product(n) >= 0;
	}
	
	/**
	 * Changes this ball's velocity after it bounced against a block that is presented as {@code rect}, depending on what kind of ball this is.
	 * 
	 * @pre {@code rect} is not {@code null}
	 * 		| rect != null
	 * @pre This ball hit the block on one of its sides
	 * 		| raaktRechthoek(rect, 1) || 
	 * 		| raaktRechthoek(rect, 2) || 
	 * 		| raaktRechthoek(rect, 3) || 
	 * 		| raaktRechthoek(rect, 4)
	 * 
	 * @mutates_properties | this.getVelocity()
	 * 
	 * @post Depending on which side this ball hit the block on, what kind of ball this is
	 * 		 and if the block is destroyable or not, this ball's velocity got changed accordingly.
	 * 		| Arrays.stream(MIRROR_VECTORS).anyMatch(v -> getVelocity().equals(old(getVelocity()).mirrorOver(v))) ||
	 * 		| destroyed == true && getClass() == SuperchargedBall.class && getVelocity().equals(old(getVelocity()))
	 * 
	 */
	
	public void hitBlock(Rect rect, boolean destroyed) {
			if (raaktRechthoek(rect, 2)) {
				velocity = velocity.mirrorOver(MIRROR_VECTORS[1]);
				return;
			}
		
			if (raaktRechthoek(rect, 1)) {
				velocity = velocity.mirrorOver(MIRROR_VECTORS[0]);
				return;
			}
			
			if (raaktRechthoek(rect, 4)) {
				velocity = velocity.mirrorOver(MIRROR_VECTORS[3]);
				return;
			}
			
			if (raaktRechthoek(rect, 3)) {
				velocity = velocity.mirrorOver(MIRROR_VECTORS[2]);
			}
		
	}
	
	/**
	 * Returns true if the ball has hit {@code rechthoek} on the given side, indicated by {@code sideNumber}.
	 * 			(1 indicates the bottom side, 2 indicates the left side, 3 indicates the top side and 4 indicates the right side)
	 * 
	 * @pre Argument {@code sideNumber} should be 1, 2, 3 or 4
	 * 		| sideNumber == 1 || sideNumber == 2 || sideNumber == 3 || sideNumber == 4
	 * @pre Argument {@code rechthoek} should not be {@code null}
	 * 		| rechthoek != null
	 * 
	 * @inspects | this
	 * 
	 * @post The result is true if the distance between the circle's center to the given side is smaller than or equal to the circle's radius, 
	 * 		 and if the dot product of the direction of the ball and the vector perpendicular to the given side of the rectangle is positive.
	 * 		| result == ((Math.max(rechthoek.getTopLeft().getX(), Math.min(getCenter().getX(), rechthoek.getBottomRight().getX())) - getCenter().getX()) -
	 * 		|			(Math.max(rechthoek.getTopLeft().getX(), Math.min(getCenter().getX(), rechthoek.getBottomRight().getX())) - getCenter().getX()) + 
	 * 		|		(Math.max(rechthoek.getTopLeft().getY(), Math.min(getCenter().getY(), rechthoek.getBottomRight().getY())) - getCenter().getY()) * 
	 * 		|			(Math.max(rechthoek.getTopLeft().getY(), Math.min(getCenter().getY(), rechthoek.getBottomRight().getY())) - getCenter().getY())) 
	 * 		|	<= getDiameter()/2 * getDiameter()/2 &&
	 * 		| 		Arrays.stream(MIRROR_VECTORS).anyMatch(v -> raakDotProduct(getVelocity(), v)) ||
	 * 		| result == false
	 * 
	 */
	
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
					return raakDotProduct(velocity, MIRROR_VECTORS[0]);
				}
			}
		}
		// leftSide
		if (sideNumber == 2) {
			if (center.getX() <= rechthoek.getTopLeft().getX() && ballRechtsePunt.getX() >= rechthoek.getTopLeft().getX() && ballBovenstePunt.getY() <= rechthoek.getBottomRight().getY() && ballOnderstePunt.getY() >= rechthoek.getTopLeft().getY()) {
				Point linksBoven = rechthoek.getTopLeft();
				Point linksOnder = new Point(rechthoek.getTopLeft().getX(), rechthoek.getBottomRight().getY());
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(linksBoven, linksOnder)) {
					return raakDotProduct(velocity, MIRROR_VECTORS[1]);
				}
			}
		}
		//topSide
		if (sideNumber == 3) {
			if (ballRechtsePunt.getX() >= rechthoek.getTopLeft().getX() && ballLinksePunt.getX() <= rechthoek.getBottomRight().getX() && center.getY() <= rechthoek.getTopLeft().getY() && ballOnderstePunt.getY() >= rechthoek.getTopLeft().getY()) {
				Point rechthoekRechtsBovenPunt = new Point(rechthoek.getBottomRight().getX(), rechthoek.getTopLeft().getY());
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(rechthoek.getTopLeft(), rechthoekRechtsBovenPunt)) {
					return raakDotProduct(velocity, MIRROR_VECTORS[2]);
				}
			}
		}
		//rightSide
		if (sideNumber == 4) {
			if (ballOnderstePunt.getY() >= rechthoek.getTopLeft().getY() && ballBovenstePunt.getY() <= rechthoek.getBottomRight().getY() && center.getX() >= rechthoek.getBottomRight().getX() && ballLinksePunt.getX() <= rechthoek.getBottomRight().getX()) {
				Point rechthoekRechtsBovenPunt = new Point(rechthoek.getBottomRight().getX(), rechthoek.getTopLeft().getY());
				if (getDiameter()/2 * getDiameter()/2 >= distanceCenterTo2Points(rechthoekRechtsBovenPunt, rechthoek.getBottomRight())) {
					return raakDotProduct(velocity, MIRROR_VECTORS[3]);
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the distance of the center of the ball to a line constructed by 2 points to the power of 2.
	 * 
	 * @pre {@code punt1} is not {@code null}
	 * 		| punt1 != null
	 * @pre {@code punt2} is not {@code null}
	 * 		| punt2 != null
	 * @pre {@code punt1} must have either a different x-coordinate to {@code punt2}, a different y-coordinate or both.
	 * 		| punt1.getX() != punt2.getX() || 
	 * 		|	punt1.getY() != punt2.getY()
	 * 
	 * @inspects | this
	 * 
	 * @post The result is the distance between the center of the ball and the line constructed by the 2 given points.
	 * 		| result == 
	 * 		|	((punt2.getX() - punt1.getX()) * (punt1.getY() - getCenter().getY()) - 
	 * 		| 			(punt1.getX() - getCenter().getX()) * (punt2.getY() - punt1.getY())) * 
	 * 		|		((punt2.getX() - punt1.getX()) * (punt1.getY() - getCenter().getY()) - 
	 * 		| 			(punt1.getX() - getCenter().getX()) * (punt2.getY() - punt1.getY())) / 
	 * 		|	((punt2.getX() - punt1.getX()) * (punt2.getX() - punt1.getX()) + 
	 * 		|		(punt2.getY() - punt1.getY()) * (punt2.getY() - punt1.getY()))
	 */
	
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
