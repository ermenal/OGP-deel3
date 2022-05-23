package breakout;

import java.awt.Color;
import java.util.stream.IntStream;
import java.util.Arrays;
import breakout.utils.*;
import breakout.radioactivity.*;

/**
 * This class represents a paddle on a 2D-grid.
 * 
 * @immutable
 *
 * @invar The paddle's center is not {@code null}
 * 		| getCenter() != null
 * @invar | getSize().equals(new Vector(1500, 250))
 * @invar The amount of replications this paddle will spawn when a ball collides with it is between 0 and 3, including 0 and 3
 * 		| getAmountOfReplications() >= 0 && getAmountOfReplications() <= 3
 * @invar | Arrays.equals(getAddedVelocities(), new Vector[] {new Vector(2, -2), new Vector(2, 2), new Vector(-2, 2)})
 */

public abstract class PaddleState {
	
	/**
	 * @invar | center != null
	 * @invar | SIZE.equals(new Vector(1500, 250))
	 * @invar | Arrays.equals(ADDED_VELOCITIES, new Vector[] {new Vector(2, -2), new Vector(2, 2), new Vector(-2, 2)})
	 */
	
	private final Point center;
	private static final Vector SIZE = new Vector(1500, 250);
	private static final Vector[] ADDED_VELOCITIES = {new Vector(2, -2), new Vector(2, 2), new Vector(-2, 2)};
	private final Color color;
	
	/**
	 * Initializes this object so that it stores the given {@code center}.
	 * 
	 * @pre The argument {@code center} is not {@code null}
	 *     	| center != null
	 *     
	 * @post | getCenter() == center
	 */
	
	public PaddleState(Point center, Color color){
		this.center = center;
		this.color = color;
	}
	
	
	/**
	 * Returns the top left coordinate of the paddle.
	 * 
	 * @post The result is not {@code null}
	 *     	| result != null
	 *     
	 * @post The result is a Point object representing the top left point of the paddle.
	 *      | result.getX() == getCenter().getX() - getSize().getX() &&
	 *      | result.getY() == getCenter().getY() - getSize().getY()
	 */
	
	public Point getTopLeft() {
		Point tl = center.minus(SIZE);
		return tl;
	}
	
	/**
	 * Returns the bottom right coordinate of the paddle.
	 * 
	 * @post The result is not {@code null} 
	 * 	   	| result != null
	 * 
	 * @post The result is a Point object representing the bottom right point of the paddle.
	 *     	| result.getX() == getCenter().getX() + getSize().getX() &&
	 *     	| result.getY() == getCenter().getY() + getSize().getY()
	 */
	
	public Point getBottomRight() {
		Point br = center.plus(SIZE);
		return br;
	}
	
	/**
	 * Returns the paddle's center.
	 */
	public Point getCenter() {
		return center;
	}
	
	/**
	* Returns the paddle's size.
	*/
	
	public Vector getSize() {
		return SIZE;
	}
	
	/**
	 * Returns the paddle's color.
	 * 
	 * @post The result is cyan if the paddle spawns 0 replications, otherwise it's green.
	 * 		| result == Color.CYAN || result == Color.PINK
	 */
	
	public Color getColor() {
		return color;
	}
	
	/**
	 * Returns the amount of balls that will be cloned on contact with this paddle.
	 * 
	 * @post The amount of replications is 0 for a normal paddle, and 1, 2 or 3 for a replicator paddle
	 * 		| result == 0 && getClass().equals(NormalPaddleState.class) || 
	 * 		| result >= 1 && result <= 3 && getClass().equals(ReplicatingPaddleState.class)
	 */
	
	public int getAmountOfReplications() {
		return 0;
	}
	
	/**
	 * Returns the velocities that will be added to any cloned balls.
	 * 
	 * @creates | result
	 */
	
	public Vector[] getAddedVelocities() {
		return ADDED_VELOCITIES.clone();
	}
	
	/**
	 * Returns a new paddle after this paddle was hit by a ball. The new paddle's properties depend on the current paddle's properties
	 * 
	 * @post The center has remained unchanged
	 * 		| result.getCenter() == getCenter()
	 * 
	 * @post If {@code this} is a normal paddle, the result is a normal paddle.
	 * 		 If {@code this} is a replicator paddle, the result is a either replicator paddle that has will spawn one less ball than this paddle
	 * 		 when a ball next collides with it, or a normal paddle if this was the last ball the replicator paddle could clone. 
	 * 		| result.equals(new NormalPaddleState(getCenter())) || 
	 * 		| result.equals(new ReplicatingPaddleState(getCenter(), getAmountOfReplications()-1)) && 
	 * 		|		getClass().equals(ReplicatingPaddleState.class)
	 */
	
	public abstract PaddleState ballHitPaddle();
	
	/**
	 * Returns {@code true} if {@code obj} is equal to {@code this}, {@code false} otherwise.
	 * 
	 * @post The result is {@code true} if {@code obj} is the same kind of paddle as {@code this} with the same properties.
	 * 		 If this is not the case or {@code obj} is {@code null}, the result is {@code false} 
	 * 		| result == ( (obj != null) && (
	 * 		| 	obj.getClass() == getClass() &&
	 * 		|	((PaddleState)obj).getCenter().equals(getCenter()) && (
	 * 		|		getClass() == NormalPaddleState.class ||
	 * 		|		getClass() == ReplicatingPaddleState.class &&
	 * 		|			((ReplicatingPaddleState)obj).getAmountOfReplications() == getAmountOfReplications() ) ) )
	 */
	
	@Override
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return getClass() == obj.getClass() &&
				((PaddleState)obj).getCenter().equals(getCenter());
	}
	
	/**
	 * Returns a new array, containing all balls in the given array {@code balls} as well as any balls that were cloned as a result of
	 * {@code ball} colliding with the paddle. If no balls were cloned, a copy of {@code balls} is returned.
	 * 
	 * @pre Argument {@code balls} should not be {@code null}.
	 * 		| balls != null
	 * @pre Argument {@code ball} should not be {@code null}.
	 * 		| ball != null
	 * 
	 * @inspects | balls, ball
	 * 
	 * @creates | result
	 * 
	 * @post The resulting array will have an amount of elements equal to the sum of the number of elements in {@code balls} and the amount of balls this paddle will clone.
	 * 		| result.length == balls.length + getAmountOfReplications()
	 * 
	 * @post All elements from {@code balls} are in the resulting array, and all of them are on the same index as they were in {@code balls}.
	 * 		| IntStream.range(0, balls.length).
	 * 		|	allMatch(i -> balls[i] == result[i])
	 * 
	 * @post None of the resulting array's elements are {@code null}, unless they were {@code null} in {@code balls}.
	 * 		| IntStream.range(0, result.length).
	 * 		|	allMatch(i -> result[i] != null || i <= balls.length)
	 * 
	 * @post All of the cloned balls in the resulting array are behind the elements from {@code balls}. The cloned balls' classes, time left supercharged, centers and diameters 
	 * 		 are all the same as {@code ball} and their velocities are equal to the sum of one of the Vectors from {@code AddedVelocities} and the velocitiy from {@code ball}.
	 * 		| IntStream.range(balls.length, result.length).allMatch(i ->
	 * 		| 	result[i].equalContent(ball.cloneBallWithChangedVelocity(getAddedVelocities()[0])) ||
	 * 		|	result[i].equalContent(ball.cloneBallWithChangedVelocity(getAddedVelocities()[1])) ||
	 * 		|	result[i].equalContent(ball.cloneBallWithChangedVelocity(getAddedVelocities()[2])) )
	 */
	
	public abstract Ball[] hitPaddleReplicationHandler(Ball[] balls, Ball ball);
	
	/**
	 * Returns a new paddle object that has moved {@code 10 * elapsedTime} units to the right, keeping in mind that it can't go outside of the field
	 * 
	 * @pre Argument {@code br} is not {@code null}
	 * 		| br != null
	 * @pre Argument {@code elapsedTime} is greater than 0
	 * 		| elapsedTime > 0
	 * 
	 * @post The result is not {@code null}
	 * 	    | result != null
	 * 
	 * @post The resulting paddle is the same kind of paddle as {@code this}
	 * 		| result.getClass().equals(getClass())
	 * 
	 * @post The resulting paddle's center's Y coordinate has remained the same
	 * 		| result.getCenter().getY() == old(getCenter()).getY()
	 * 
	 * @post The resulting paddle has moved right by {@code 20 * elapsedTime} units, unless it would have gone outside of the field,
	 * 		 in which case its center's x-coordinate has been adjusted keeping the paddle's size in mind 
	 * 		| result.getCenter().getX() == old(getCenter()).getX() + 20*elapsedTime ||
	 * 		| result.getCenter().getX() == br.getX() - getSize().getX()
	 */
	
	public abstract PaddleState movePaddleRight(Point br, int elapsedTime);
	
	/**
	 * Returns a new paddle object that has moved {@code 20 * elapsedTime} units to the left, keeping in mind that it can't go outside of the field
	 * 
	 * @pre Argument {@code elapsedTime} is greater than 0
	 * 		| elapsedTime > 0
	 * 
	 * @post The result is not {@code null}
	 * 		|  result != null
	 * 
	 * @post The resulting paddle is the same kind of paddle as {@code this}
	 * 		| result.getClass().equals(getClass())
	 * 
	 * @post The resulting paddle's center's Y coordinate has remained the same
	 * 		| result.getCenter().getY() == old(getCenter()).getY()
	 * 
	 * @post The resulting paddle has moved left by {@code 20 * elapsedTime} units, unless it would have gone outside of the field, 
	 * 		 in which case its center's x-coordinate has been adjusted keeping the paddle's size in mind
	 * 		| result.getCenter().getX() == old(getCenter()).getX() - 20*elapsedTime || 
	 * 		| result.getCenter().getX() == getSize().getX()
	 */
	
	public abstract PaddleState movePaddleLeft(int elapsedTime);
	
	
}
