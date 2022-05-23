package breakout;

import java.awt.Color;
import breakout.utils.*;

/**
 * This class represents a sturdy block on a 2D-grid
 * 
 * @immutable
 * 
 * @invar | getBottomRight() != null
 * @invar | getTopLeft() != null
 * @invar | getHealth() > 0 && getHealth() <= 3
 * 
 * @invar The bottom right coordinates of the block are below and to the right of top left coordinates of the block
 *     | getTopLeft().getX() < getBottomRight().getX() &&
 *     | getTopLeft().getY() < getBottomRight().getY()
 */

public class SturdyBlockState extends BlockState {
	/**
	 * @invar | health > 0 && health <= 3
	 */
	
	private final int health;
	
	/**
	 * Initializes this object so that it stores the given topLeft and bottomRight coordinates and the given health of the sturdy block
	 * 
	 * @pre {@code topLeft} is not {@code null}
	 *     	| topLeft != null
	 * @pre {@code bottomRight} is not {@code null}
	 *     	| bottomRight != null
	 * @pre {@code hp} is a value between 0 and 3, excluding 0.
	 * 		| health > 0 && health <= 3
	 * 
	 * @pre {@code bottomRight} coordinates of the block are below and to the right of its {@code topLeft} coordinates
	 *     	| bottomRight.getX() > topLeft.getX() && 
	 *     	| bottomRight.getY() > topLeft.getY()
	 * 
	 * @post | getTopLeft() == topLeft
	 * @post | getBottomRight() == bottomRight
	 * @post | getHealth() == health
	 */
	
	public SturdyBlockState(Point topLeft, Point bottomRight, int health){
		super(topLeft, bottomRight);
		this.health = health;
	}
	
	/**
	 * Returns true if the sturdy block has 1 health left, false otherwise
	 * 
	 * @post | result == getHealth() <= 1
	 */
	
	@Override
	
	public boolean getsDestroyedOnCollision() {
		return health <= 1;
	}
	
	/**
	 * Returns the block's color, which for a sturdy block is dependent on the amount of health the block has left.
	 * 
	 * @post If the block has 1 health, it is yellow. If the block has 2 health, it is orange. If the block has 3 health, it is red.
	 * 		| result == Color.YELLOW && getHealth() == 1 || 
	 * 		| result == Color.ORANGE && getHealth() == 2 || 
	 * 		| result == Color.RED && getHealth() == 3
	 */
	
	@Override
	
	public Color getColor() {
		switch(health) {
		case 1: 
			return Color.YELLOW;
		case 2:
			return Color.ORANGE;
		default:
			return Color.RED;
		}
	}
	
	/**
	 * Returns the block's health.
	 */
	
	@Override
	
	public int getHealth() {
		return health;
	}
	
	/**
	 * Returns whether or not {@code obj} is equal to {@code this}
	 * 
	 * @post The result is {@code true} if {@code obj} is a sturdy block with the same properties as this block. 
	 * 		 The result is {@code false} if this is not the case or {@code obj} is {@code null} 
	 * 		| result == ( (obj != null) && ( getClass() == obj.getClass() &&
	 * 		|	((BlockState)obj).getTopLeft().equals(getTopLeft()) &&
	 * 		|	((BlockState)obj).getBottomRight().equals(getBottomRight()) &&
	 * 		| 	((SturdyBlockState)obj).getHealth() == getHealth() ) )
	 */
	
	@Override
	
	public boolean equals(Object obj) {
		return super.equals(obj) && 
				((SturdyBlockState)obj).getHealth() == getHealth();
	}
	
	/**
	 * Returns {@code null}, unless this sturdy block has more than 1 health, in which case a new sturdy block that has 1 less health than this one is returned.
	 * 
	 * @post 
	 * 		| result == null && getHealth() <= 1 || 
	 * 		| result.getClass().equals(SturdyBlockState.class) && 
	 * 		| 	result.getHealth() == getHealth() - 1
	 */
	
	@Override
	
	public BlockState specialBlockHandler() {
		if (health > 1)
			return new SturdyBlockState(getTopLeft(), getBottomRight(), health-1);
		return null;
	}
}

