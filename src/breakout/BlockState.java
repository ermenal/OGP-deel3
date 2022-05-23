package breakout;

import java.awt.Color;
import breakout.utils.*;
import breakout.radioactivity.*;

/**
 * This class represents a block on a 2D-grid
 * 
 * @immutable
 * 
 * @invar | getBottomRight() != null
 * @invar | getTopLeft() != null
 * @invar | getHealth() == -1 || getHealth() > 0 && getHealth() <= 3
 * 
 * @invar The bottom right coordinates of the block are below and to the right of top left coordinates of the block
 *     | getTopLeft().getX() < getBottomRight().getX() &&
 *     | getTopLeft().getY() < getBottomRight().getY()
 */

public abstract class BlockState {
	
	/**
	 * @invar | bottomRight != null
	 * @invar | topLeft != null
	 * 
	 * @invar {@code bottomRight} coordinates of the block are below and to the right of {@code topLeft} coordinates of the block
	 *     | topLeft.getX() < bottomRight.getX() &&
	 *     | topLeft.getY() < bottomRight.getY()
	 */
	
	private final Point topLeft;
	private final Point bottomRight;
	private final Color color;

	/**
	 * Initializes this object so that it stores the given topLeft and bottomRight coordinates of the block
	 * 
	 * @pre {@code topLeft} is not {@code null}
	 *     | topLeft != null
	 * @pre {@code bottomRight} is not {@code null}
	 *     | bottomRight != null
	 * 
	 * @pre {@code bottomRight} coordinates of the block are below and to the right of its {@code topLeft} coordinates
	 *     | bottomRight.getX() > topLeft.getX() && 
	 *     | bottomRight.getY() > topLeft.getY()
	 * 
	 * @post | getTopLeft() == topLeft
	 * @post | getBottomRight() == bottomRight
	 */
	
	public BlockState(Point topLeft, Point bottomRight, Color color) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.color = color;
	}
	
	
	/** Return this block's topLeft coordinate. 
	 */
	public Point getTopLeft() {
		return topLeft;
	}
	
	/** Return this block's bottomRight coordinate. 
	 */
	public Point getBottomRight() {
		return bottomRight;
	}
	
	/**
	 * Returns {@code true} if the block gets destroyed when a ball collides with it, otherwise it returns {@code false}.
	 * 
	 * @post Only a sturdy block with enough health health can survive a hit from a ball.
	 * 		 Returns {@code false} if the block is a sturdy block with more than 1 health, {@code true} otherwise. 
	 * 		| result == true  || 
	 * 		| result == false && getClass().equals(SturdyBlockState.class) && getHealth() > 1
	 */
	
	public boolean getsDestroyedOnCollision() {
		return true;
	}
	
	/**
	 * Returns the block's color, depending on what kind of block it is.
	 * 
	 * @post 
	 * 		| result == Color.WHITE || result == Color.RED || result == Color.ORANGE || 
	 * 		| result == Color.YELLOW || result == Color.GREEN || result == Color.BLUE
	 */
	
	public Color getColor() {
		return color;
	}
	
	/**
	 * Returns the amount of health the block has or -1 if the block is not a sturdy block.
	 * This method is used for formal documentation.
	 */
	
	public int getHealth() {
		return -1;
	}
	
	/**
	 * Returns whether or not {@code obj} is equal to {@code this}
	 * 
	 * @post The result is {@code true} if {@code obj} is the same kind of block as {@code this} with the same properties.
	 * 		 If this is not the case or {@code obj} is {@code null}, the result is {@code false}
	 * 		| result == ( (obj != null) && (
	 * 		|	obj.getClass() == getClass() && 
	 * 		|	((BlockState)obj).getTopLeft().equals(getTopLeft()) &&
	 * 		|	((BlockState)obj).getBottomRight().equals(getBottomRight()) && (
	 * 		|		getClass() == NormalBlockState.class ||
	 * 		|		getClass() == PowerupBlockState.class ||
	 * 		|		getClass() == ReplicatorBlockState.class ||
	 * 		|		getClass() == SturdyBlockState.class &&
	 * 		|			((SturdyBlockState)obj).getHealth() == getHealth() ) ) )
	 * 
	 */
	
	@Override
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return obj.getClass() == getClass() && 
				((BlockState)obj).getTopLeft().equals(getTopLeft()) && 
				((BlockState)obj).getBottomRight().equals(getBottomRight());
	}
	
	/**
	 * Returns {@code ball}, unless {@code this} is a powerup block.
	 * 
	 * @pre Argument {@code ball} is not {@code null}.
	 * 		| ball != null
	 * 
	 * @inspects | ball
	 * 
	 * @creates | result 
	 * 
	 * @post If {@code this} is a powerup block, a new supercharged ball is returned. 
	 * 		 If {@code this} is not a powerup block, {@code ball} is returned.
	 * 		| result == ball && !getClass().equals(PowerupBlockState.class) || 
	 * 		| result.getClass().equals(SuperchargedBall.class) && getClass().equals(PowerupBlockState.class)
	 * 
	 * @post The resulting ball's center, diameter and velocity are the same as {@code ball}.
	 * 		| result.getCenter() == ball.getCenter() && 
	 * 		| result.getDiameter() == ball.getDiameter() && 
	 * 		| result.getVelocity() == ball.getVelocity()
	 */
	
	public Ball specialBlockHandler(Ball ball) {
		return ball;
	}
	
	/**
	 * Returns {@code paddle}, unless {@code this} is a replicator block.
	 * 
	 * @pre Argument {@code paddle} is not {@code null}.
	 * 		| paddle != null
	 * 
	 * @post If {@code this} is a replicatorblock, a new replicator paddle is returned. Otherwise, {@code paddle} is returned
	 * 		| result == paddle && !getClass().equals(ReplicatorBlockState.class) || 
	 * 		| getClass().equals(ReplicatorBlockState.class) && result.getClass().equals(ReplicatingPaddleState.class)
	 * 
	 * @post The resulting paddle's center has remained unchanged.
	 * 		| result.getCenter() == paddle.getCenter() 
	 */
	
	public PaddleState specialBlockHandler(PaddleState paddle) {
		return paddle;
	}
	
	/**
	 * Returns {@code null}, unless {@code this} is a sturdy block with more than 1 health.
	 * 
	 * @post The result is {@code null}, unless {@code this} is a sturdy block with more than 1 health.
	 * 		| result == null || 
	 * 		| getClass().equals(SturdyBlockState.class) && result.getHealth() == getHealth()-1
	 */
	
	public BlockState specialBlockHandler() {
		return null;
	}
	
}
