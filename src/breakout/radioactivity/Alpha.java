package breakout.radioactivity;

import java.awt.Color;
import java.util.*;

import breakout.utils.Point;
import breakout.utils.Vector;

import logicalcollections.LogicalSet;

/**
 * Each instance of this class represents an alpha particle on a 2D-grid, as part of a ball-alpha graph
 * 
 * @invar | getCenter() != null
 * @invar | getDiameter() >= 0
 * @invar | getVelocity() != null
 * @invar | getColor() == Color.LIGHT_GRAY
 * 
 * @invar | getBalls() != null
 * 
 * @invar If an alpha is linked to a ball, that ball is not null and that ball is also linked to the alpha
 * 		| getBalls().stream().allMatch(b -> b!=null && b.getAlphas().contains(this))
 */

public class Alpha {
	
	/**
	 * @invar | center != null
	 * @invar | diameter >= 0
	 * @invar | velocity != null
	 * @invar | color == Color.LIGHT_GRAY
	 */
	
	private Point center;
	private int diameter;
	private Vector velocity;
	private final Color color;
	
	/**
     * @invar | linkedBalls!= null
     * @invar | linkedBalls.stream().allMatch(b -> b != null)
     * 
     * @representationObject
     */
	
	private Set<Ball> linkedBalls = new HashSet<Ball>();
	
	/**
	 * Constructs a new alpha particle with the given center, diameter and velocity
	 * 
	 * @pre | center != null
	 * @pre | initVelocity != null
	 * 
	 * @post | getCenter() == center
	 * @post | getDiameter() == Math.abs(diameter)
	 * @post | getVelocity() == initVelocity
	 * @post | getColor() == Color.LIGHT_GRAY
	 * 
	 * @post | getBalls() != null
	 * @post This new alpha particle isn't linked to any balls 
	 * 		| getBalls().isEmpty()
	 */
	
	public Alpha(Point center, int diameter, Vector initVelocity) {
		this.center = center;
		this.diameter = Math.abs(diameter);	
		this.velocity = initVelocity;
		this.color = Color.LIGHT_GRAY;
	}
	
	/**
	 * Adds the given ball to this alpha's set of linked balls
	 * 
	 * @pre Argument {@code ball} is not {@code null}
	 * 		| ball != null
	 * 
	 * @mutates_properties | getBalls()
	 * 
	 * @post This alpha's set of linked balls is equal to its old set of linked balls plus the given ball
	 * 		| getBallsInternal().equals(LogicalSet.plus(old(getBallsInternal()), ball))
	 */
	
	void addBall(Ball ball) {
		linkedBalls.add(ball);
	}
	
	/**
	 * Removes the given ball from this alpha's set of linked balls
	 * 
	 * @pre Argument {@code ball} is not {@code null}
	 * 		| ball != null
	 * 
	 * @mutates_properties | getBalls()
	 * 
	 * @post This alpha's set of linked balls is equal to its old set of linked balls minus the given ball
	 * 		| getBallsInternal().equals(LogicalSet.minus(old(getBallsInternal()), ball))
	 */
	
	void removeBall(Ball ball) {
		linkedBalls.remove(ball);
	}
	
	/**
	 * Returns a shallow copy of the set of the balls linked to this alpha
	 * 
	 * @invar | getBallsInternal().stream().allMatch(b -> b.getAlphasInternal().contains(this))
	 * 
	 * @post | result != null && result.stream().allMatch(b -> b != null)
	 * 
	 * @creates | result
	 * 
	 * @peerObjects (package-level)
	 */
	
	Set<Ball> getBallsInternal(){
		return Set.copyOf(linkedBalls);
	}
	
	/**
	 * Returns a shallow copy of the set of the balls linked to this alpha
	 * 
	 * @post | result != null
	 * 
	 * @creates | result
	 * 
	 * @peerObjects
	 */
	
	public Set<Ball> getBalls(){
		return getBallsInternal();
	}
	
	/** Returns this alpha's center */
	
	public Point getCenter() {
		return center;
	}
	
	/** Returns this alpha's center */
	
	public int getDiameter() {
		return diameter;
	}
	
	/** Returns this alpha's velocity */
	
	public Vector getVelocity() {
		return velocity;
	}
	
	/** Returns this alpha's color
	 * 
	 * 
	 * @immutable This object is associated with the same color throughout its lifetime
	 */
	
	public Color getColor() {
		return color;
	}
	
	/**
	 * Changes the center and diameter of this ball to the given center and diameter
	 * 
	 * @pre | center != null
	 * 
	 * @post | getCenter() == center
	 * @post | getDiameter() == Math.abs(diameter)
	 */
	
	public void setLocation(Point center, int diameter) {
		this.center = center;
		this.diameter = diameter;
	}
	
	/**
	 * Changes the velocity of this alpha to the given velocity
	 * 
	 * @pre velocity != null
	 * 
	 * @post getVelocity() == velocity
	 */
	
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Returns a new alpha that is a clone of {@code this}.
	 * 
	 * @mutates_properties This alpha's linked balls' linked alphas set has been mutated, as well as the eCharge of any ball linked to this alpha
	 * 		| (...getBalls()).getAlphas(), (... getBalls()).getEcharge()
	 * 
	 * @inspects | this
	 * 
	 * @creates | result
	 * 
	 * @post The resulting alpha has the same properties as {@code this}
	 * 		| result.getCenter() == getCenter() &&
	 * 		| result.getDiameter() == getDiameter() &&
	 * 		| result.getVelocity() == getVelocity()
	 * 
	 * @post The resulting alpha is linked to the same balls as {@code this} 
	 * 		| result.getBalls().equals(getBalls()) 
	 * 
	 * @post every ball that is linked to this alpha has had its eCharge recalculated and changed.
	 * 		| getBalls().stream().allMatch(b -> b.getEcharge() == b.calculateAndSetEcharge())
	 */
	
	public Alpha clone() {
		Alpha retAlpha = new Alpha(center, diameter, velocity);
		for (Ball ball: getBalls()) {
			ball.linkTo(retAlpha);
		}
		return retAlpha;
	}
	
	/**
	 * Checks if {@code obj} is the same class and has the same properties as {@code this}. 
	 * 
	 * @post The result is {@code true} if {@code obj} is a {@code SuperchargedBall} or a {@code NormalBall} with the same properties as {@code this}.
	 * 		 Returns {@code false} if this is not the case or if {@code obj} is {@code null}.
	 * 		| result == ( (obj != null) && 
	 * 		|		( obj.getClass() == getClass() &&
	 * 		|		((Alpha)obj).getCenter().equals(getCenter()) &&
	 * 		|		((Alpha)obj).getDiameter() == getDiameter() &&
	 * 		|		((Alpha)obj).getVelocity().equals(getVelocity()) &&
	 * 		|		((Alpha)obj).getBalls().size() == getBalls().size() ))
	 */
	
	public boolean equalContent(Object obj) {
		if (obj == null)
			return false;
		return obj.getClass() == getClass() && 
				((Alpha)obj).getCenter().equals(center) &&
				((Alpha)obj).getDiameter() == diameter &&
				((Alpha)obj).getVelocity().equals(velocity) && 
				((Alpha)obj).getBalls().size() == linkedBalls.size();
	}
	
	/**
	 * Changes this alpha's properties according to the given ball's properties.
	 * We use this method to avoid code duplication. Since Alpha functions in much the same way as Ball, 
	 * we can use that to temporarily put the alpha's properties in a ball, call certain methods on the ball, and then copy the ball's changed properties if needed.
	 * 
	 * @pre Argument {@code ball} is not {@code null}
	 * 		| ball != null
	 * 
	 * @mutates_properties | getCenter(), getVelocity()
	 * 
	 * @inspects | this, ball
	 * 
	 * @post If this alpha's center or velocity differ from the given ball's center or velocity, they get changed accordingly
	 * 		| getCenter() == ball.getCenter() && 
	 * 		|		! ( old(getCenter()).equals(ball.getCenter()) ) ||
	 * 		| getVelocity() == ball.getVelocity() && 
	 * 		| 		! ( old(getVelocity()).equals(ball.getVelocity()) ) ||
	 * 		| getVelocity() == old(getVelocity()) && 
	 * 		|		getCenter() == old(getCenter())
	 */
	
	public void changeAlphaFromBall(Ball ball) {
		// There are other solutions to solving e.g. wall bounces on alphas, but this seemed the easiest and least error-prone way to do it, 
		//	since all methods from ball have been tested extensively and alpha reacts the same way when it bounces against a wall as a ball.
		if (!center.equals(ball.getCenter()))
			center = ball.getCenter();
		if (!velocity.equals(ball.getVelocity()))
			velocity = ball.getVelocity();
	}
	
	/**
	 * Creates a ball using this alpha's properties.
	 * 
	 * @inspects | this
	 * 
	 * @creates | result
	 * 
	 * @post The resuling ball is a new normal ball with the same center, diameter and velocity as this alpha
	 * 		| result.getCenter() == getCenter() && 
	 * 		| result.getDiameter() == getDiameter() && 
	 * 		| result.getVelocity() == getVelocity()
	 */
	
	public Ball createNormalBallFromAlpha() {
		// We use this method for the same reason as the changeFromAlpha method. 
		return new NormalBall(center, diameter, velocity);
	}
	
}
