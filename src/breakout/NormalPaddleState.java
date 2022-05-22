package breakout;

import java.awt.Color;
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
 	* @invar The amount of replications this paddle will spawn when a ball collides with it is 0
 	* 		| getAmountOfReplications() == 0
 	* @invar | Arrays.equals(getAddedVelocities(), new Vector[] {new Vector(2, -2), new Vector(2, 2), new Vector(-2, 2)})
 	*/

public class NormalPaddleState extends PaddleState{

	public NormalPaddleState(Point center) {
		super(center);
	}
	
	/**
	 * Returns the paddle's color, which for a normal paddle is cyan.
	 * 
	 * @post | result == Color.CYAN
	 */
	
	@Override
	
	public Color getColor() {
		return Color.CYAN;
	}
	
	/**
	 * Returns a new normal paddle after this paddle was hit by a ball. 
	 * 
	 * @post The center has remained unchanged
	 * 		| result.getCenter() == getCenter()
	 * 
	 * @post The result is a normal paddle with the same center as this paddle.
	 * 		| result.equals(new NormalPaddleState(getCenter())) 
	 */
	
	@Override
	
	public PaddleState ballHitPaddle() {
		return new NormalPaddleState(getCenter());
	}
	
	/**
	 * Returns {@code true} if {@code obj} is equal to {@code this}, {@code false} otherwise.
	 * 
	 * @post The result is {@code true} if {@code obj} is a normal paddle with the same properties as {@code this}.
	 * 		 The result is {@code false} if this is not the case or if {@code obj} is {@code null}
	 * 		| result == ( (obj != null) && (obj.getClass() == getClass() && 
	 * 		|	((PaddleState)obj).getCenter().equals(getCenter()) ) )
	 */
	
	@Override
	
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	/**
	 * Returns a copy of {@code balls}
	 * 
	 * @pre Argument {@code balls} should not be {@code null}
	 * 		| balls != null
	 * 
	 * @inspects | balls
	 * 
	 * @creates | result
	 * 
	 * @post | Arrays.equals(result, balls)
	 */
	
	@Override
	
	public Ball[] hitPaddleReplicationHandler(Ball[] balls, Ball ball) {
		return balls.clone();
	}
	
	/**
	 * Returns a new normal paddle that has moved {@code 10 * elapsedTime} units to the right, keeping in mind that it can't go outside of the field
	 * 
	 * @pre Argument {@code br} is not {@code null}
	 * 		| br != null
	 * @pre Argument {@code elapsedTime} is greater than 0
	 * 		| elapsedTime > 0
	 * 
	 * @post The result is not {@code null}
	 * 	    | result != null
	 * @post The resulting paddle is the same kind of paddle as {@code this}, which in this case is a normal paddle
	 * 		| result.getClass().equals(getClass())
	 * @post The resulting paddle's center's Y coordinate has remained the same
	 * 		| result.getCenter().getY() == old(getCenter()).getY()
	 * @post The resulting paddle has moved right by {@code 10 * elapsedTime} units, unless it would have gone outside of the field,
	 * 		 in which case its center's x-coordinate has been adjusted keeping the paddle's size in mind 
	 * 		| result.getCenter().getX() == old(getCenter()).getX() + 10*elapsedTime ||
	 * 		| result.getCenter().getX() == br.getX() - getSize().getX()
	 */
	
	@Override
	
	public PaddleState movePaddleRight(Point br, int elapsedTime) {
		int moveBy = 10 * elapsedTime;
		Point newCenter = new Point(getCenter().getX() + moveBy, getCenter().getY());
		if (newCenter.getX() + getSize().getX() > br.getX()){
			newCenter = new Point(br.getX() - getSize().getX(), newCenter.getY());
		}
		return new NormalPaddleState(newCenter);
	}
	
	/**
	 * Returns a new normal paddle that has moved {@code 10 * elapsedTime} units to the left, keeping in mind that it can't go outside of the field
	 * 
	 * @pre Argument {@code elapsedTime} is greater than 0
	 * 		| elapsedTime > 0
	 * 
	 * @post The result is not {@code null}
	 * 		|  result != null
	 * @post The resulting paddle is the same kind of paddle as {@code this}, which in this case is a normal paddle
	 * 		| result.getClass().equals(getClass())
	 * @post The resulting paddle's center's Y coordinate has remained the same
	 * 		| result.getCenter().getY() == old(getCenter()).getY()
	 * @post The resulting paddle has moved left by {@code 10 * elapsedTime} units, unless it would have gone outside of the field, 
	 * 		 in which case its center's x-coordinate has been adjusted keeping the paddle's size in mind
	 * 		| result.getCenter().getX() == old(getCenter()).getX() - 10*elapsedTime || 
	 * 		| result.getCenter().getX() == getSize().getX()
	 */
	
	@Override
	
	public PaddleState movePaddleLeft(int elapsedTime) {
		int moveBy = 10 * elapsedTime;
		Point newCenter = new Point(getCenter().getX() - moveBy, getCenter().getY());
		if (newCenter.getX() - getSize().getX() < 0){
			newCenter = new Point(getSize().getX(), newCenter.getY());
		}
		return new NormalPaddleState(newCenter);
	}
	
}
