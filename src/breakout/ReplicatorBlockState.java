package breakout;

import java.awt.Color;
import breakout.utils.*;

/**
 * This class represents a replicator block on a 2D-grid
 * 
 * @immutable
 * 
 * @invar | getBottomRight() != null
 * @invar | getTopLeft() != null
 * @invar | getHealth() == -1
 * 
 * @invar The bottom right coordinates of the block are below and to the right of top left coordinates of the block
 *     | getTopLeft().getX() < getBottomRight().getX() &&
 *     | getTopLeft().getY() < getBottomRight().getY()
 */

public class ReplicatorBlockState extends BlockState {

	/**
	 * Initializes this object so that it stores the given topLeft and bottomRight coordinates of the replicator block
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
	
	public ReplicatorBlockState(Point topLeft, Point bottomRight){
		super(topLeft, bottomRight);
	}
	
	/**
	 * Returns the block's color, which for a replicator block is green.
	 * 
	 * @post | result == Color.GREEN
	 */
	
	@Override
	
	public Color getColor() {
		return Color.GREEN;
	}
	
	/**
	 * Returns whether or not {@code obj} is equal to {@code this}
	 * 
	 * @post The result is {@code true} if {@code obj} is a replicator block with the same properties as this block. 
	 * 		 The result is {@code false} if this is not the case or {@code obj} is {@code null} 
	 * 		| result == ( (obj != null) && ( getClass() == obj.getClass() &&
	 * 		|	((BlockState)obj).getTopLeft().equals(getTopLeft()) &&
	 * 		|	((BlockState)obj).getBottomRight().equals(getBottomRight()) ) )
	 */
	
	@Override
	
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	/**
	 * Returns a new paddle with 3 replications.
	 * 
	 * @pre Argument {@code paddle} is not {@code null}.
	 * 		| paddle != null
	 * 
	 * @post | result.equals(new ReplicatingPaddleState(paddle.getCenter(), 3)) && getClass().equals(ReplicatorBlockState.class)
	 * 
	 * @post The resulting paddle's center has remained unchanged and it has 3 replications remaining.
	 * 		| result.getCenter() == paddle.getCenter() &&
	 * 		| result.getAmountOfReplications() == 3
	 */
	
	@Override
	
	public PaddleState specialBlockHandler(PaddleState paddle) {
		return new ReplicatingPaddleState(paddle.getCenter(), 3);
	}
	
}
