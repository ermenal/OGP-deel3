package breakout.radioactivity;

import java.awt.Color;
import breakout.utils.Point;
import breakout.utils.Vector;

import java.util.stream.IntStream;

/**
 * This class represents a normal ball on a 2D-grid, as part of a ball-alpha graph
 *
 * @invar | getCenter() != null
 * @invar | getDiameter() >= 0
 * @invar | getVelocity() != null
 * @invar | getTime() == -1
 * @invar | getColor() == Color.WHITE
 * @invar | getEcharge() != 0
 * 
 * @invar | getAlphas() != null
 * @invar If a ball is linked to an alpha, that alpha is not null and that alpha is also linked to the ball
 * 		| getAlphas().stream().allMatch(a -> a != null && a.getBalls().contains(this))
 */

public class NormalBall extends Ball{
	
	/**
	 * Constructs a new normal ball with the given center, diameter and velocity
	 * 
	 * @pre | center != null
	 * @pre | velocity != null
	 * 
	 * @post | getCenter() == center
	 * @post | getDiameter() == Math.abs(diameter)
	 * @post | getVelocity() == velocity
	 * @post | getColor() == Color.WHITE
	 * 
	 * @post This ball isn't linked to any alphas
	 * 		| getAlphas().isEmpty()
	 * @post The ball's eCharge is 1
	 * 		| getEcharge() == 1
	 */
	
	public NormalBall(Point center, int diameter, Vector velocity) {
		super(center, diameter, velocity, Color.WHITE);
	}
	
	/**
	 * Returns a new normal ball that has an altered velocity in accordance with {@code addedVelocity}.
	 * 
	 * @pre Argument {@code addedVelocity} is not {@code null}
	 * 		| addedVelocity != null
	 * 
	 * @inspects | this 
	 * 
	 * @creates | result
	 * 
	 * @post The result is also a normal ball
	 * 		| result.getClass() == getClass()
	 * 
	 * @post The resulting ball's center, time it has been supercharged for and diameter are the same as {@code this}.
	 * 		| result.getCenter() == getCenter() &&
	 * 		| result.getDiameter() == getDiameter() && 
	 * 		| result.getTime() == getTime()
	 * 
	 * @post The resulting ball's velocity is the result of adding {@code addedVelocity} to this ball's velocity.
	 * 		| result.getVelocity().equals(getVelocity().plus(addedVelocity))
	 * 
	 * @post The resulting ball is linked to no alphas and it has an eCharge of 1
	 * 		| result.getEcharge() == 1 && result.getAlphas().size() == 0
	 */
	
	@Override
	
	public Ball cloneBallWithChangedVelocity(Vector addedVelocity) {
		return new NormalBall(getCenter(), getDiameter(), getVelocity().plus(addedVelocity));
	}
	
	/**
	 * Returns a new normal ball that is a clone of {@code this}.
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
	
	@Override
	
	public Ball clone() {
		Ball retBall = new NormalBall(getCenter(), getDiameter(), getVelocity());
		for (Alpha alpha: getAlphas()) {
			retBall.linkTo(alpha);
		}
		return retBall;
	}
	
	/**
	 * Checks if {@code obj} is the same class and has the same properties as {@code this}. 
	 * 
	 * @post The result is {@code true} if {@code obj} is a {@code SuperchargedBall} or a {@code NormalBall} with the same properties as {@code this}.
	 * 		 Returns {@code false} if this is not the case or if {@code obj} is {@code null}.
	 * 	| result == ( (obj != null) && 
	 * 	|		( obj.getClass() == getClass() &&
	 * 	|		((Ball)obj).getCenter().equals(getCenter()) &&
	 * 	|		((Ball)obj).getDiameter() == getDiameter() &&
	 * 	|		((Ball)obj).getVelocity().equals(getVelocity()) &&
	 * 	|		((Ball)obj).getAlphas().size() == getAlphas().size() && 
		|		((Ball)obj).getEcharge() == getEcharge() && (
	 * 	|			getClass() == NormalBall.class ) ) )
	 */
	
	@Override
	
	public boolean equalContent(Object obj) {
		return super.equalContent(obj);

	}
}
