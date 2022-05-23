package breakout;

import java.awt.Color;
import java.util.stream.IntStream;
import java.util.Arrays;
import breakout.utils.*;
import breakout.radioactivity.*;

	/**
	 * This class represents a replicator paddle on a 2D-grid
	 * 
 	* @invar | getCenter() != null
 	* @invar | getSize().equals(new Vector(1500, 250))
 	* @invar The amount of replications this paddle will spawn when a ball collides with it is at least 1 and at most 3 
 	* 		| getAmountOfReplications() >= 1 && getAmountOfReplications() <= 3
 	* @invar | getAddedVelocities().length == 3
 	* @invar | Arrays.equals(getAddedVelocities(), new Vector[] {new Vector(2, -2), new Vector(2, 2), new Vector(-2, 2)})
 	*/
	
	
public class ReplicatingPaddleState extends PaddleState{
	
	private final int amountOfReplications;
	
	/**
	 * Initializes this object so that it stores the given center and amount of replications the replicator paddle has left.
	 * 
	 * @pre The argument {@code center} is not {@code null}
	 *     	| center != null
	 * @pre The value of {@code amountOfReplications} is a value between 1 and 3, including 1 and 3.
	 * 		This number indicates the amount of clones that will be spawned when a ball makes contact with the paddle.
	 * 		| amountOfReplications >= 1 && amountOfReplications <= 3
	 * 
	 * @post | getCenter() == center
	 * @post | getAmountOfReplications() == amountOfReplications
	 */
	
	public ReplicatingPaddleState(Point center, int amountOfReplications) {
		super(center, Color.PINK);
		this.amountOfReplications = amountOfReplications;
	}
	
	/**
	 * Returns the amount of balls that will be cloned on contact with this paddle.
	 * 
	 * @post The amount of replications is at least 1 and at most 3 for this replication paddle
	 * 		| result >= 1 && result <= 3 && getClass().equals(ReplicatingPaddleState.class)
	 */
	
	@Override
	
	public int getAmountOfReplications() {
		return amountOfReplications;
	}
	
	/**
	 * Returns a new replicator paddle after this paddle was hit by a ball. 
	 * 
	 * @post The center has remained unchanged
	 * 		| result.getCenter() == getCenter()
	 * 
	 * @post If this paddle was only able to clone one more ball, the result is a normal paddle.
	 * 		 If this paddle was able to clone 2 or 3 more balls, the result is a replicator paddle that is able to clone 1 less ball than this paddle.
	 * 		| result.equals(new NormalPaddleState(getCenter())) && 
	 * 		|		getAmountOfReplications() == 1 || 
	 * 		| result.equals(new ReplicatingPaddleState(getCenter(), getAmountOfReplications()-1)) && 
	 * 		|		getAmountOfReplications() > 1
	 */
	
	@Override
	
	public PaddleState ballHitPaddle() {
		if (amountOfReplications - 1 <= 0)
			return new NormalPaddleState(getCenter());
		return new ReplicatingPaddleState(getCenter(), amountOfReplications-1);
	}
	
	/**
	 * Returns {@code true} if {@code obj} is equal to {@code this}, {@code false} otherwise.
	 * 
	 * @post The result is {@code true} if {@code obj} is a replicator paddle with the same properties as {@code this}.
	 * 		 The result is {@code false} if this is not the case or if {@code obj} is {@code null}
	 * 		| result == ( (obj != null) && (obj.getClass() == getClass() && 
	 * 		|	((PaddleState)obj).getCenter().equals(getCenter()) && 
	 * 		|	((ReplicatingPaddleState)obj).getAmountOfReplications() == getAmountOfReplications() ) )
	 */
	
	@Override
	
	public boolean equals(Object obj) {
		return super.equals(obj) && 
				((ReplicatingPaddleState)obj).getAmountOfReplications() == amountOfReplications;
	}
	
	/**
	 * Returns a new array, containing all balls in the given array {@code balls} as well as any balls that were cloned as a result of
	 * {@code ball} colliding with the paddle.
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
	
	@Override
	
	public Ball[] hitPaddleReplicationHandler(Ball[] balls, Ball ball) {
		Ball[] newBalls = new Ball[balls.length + amountOfReplications];
		
		int i = 0;
		for (;i<balls.length; i++) {
			newBalls[i] = balls[i];
		}
		
		Ball[] clonesToBeAdded = new Ball[amountOfReplications];
		
		for (int idx=0; idx<amountOfReplications;idx++) {
			clonesToBeAdded[idx] = ball.cloneBallWithChangedVelocity(getAddedVelocities()[idx]);
		}
		
		for (int j=0;j<clonesToBeAdded.length;j++) {
			newBalls[i] = clonesToBeAdded[j];
			i++;
		}
		
		return newBalls;
	}
	
	/**
	 * Returns a new replicator paddle that has moved {@code 20 * elapsedTime} units to the right, keeping in mind that it can't go outside of the field
	 * 
	 * @pre Argument {@code br} is not {@code null}
	 * 		| br != null
	 * @pre Argument {@code elapsedTime} should be greater than 0
	 * 		| elapsedTime > 0
	 * 
	 * @post The result is not {@code null}
	 * 	    | result != null
	 * @post The resulting paddle is the same kind of paddle as {@code this}, which in this case is a replicator paddle
	 * 		| result.getClass().equals(getClass())
	 * @post The resulting paddle's center's Y coordinate has remained the same
	 * 		| result.getCenter().getY() == old(getCenter()).getY()
	 * @post The resulting paddle has moved right by {@code 20 * elapsedTime} units, unless it would have gone outside of the field,
	 * 		 in which case its center's x-coordinate has been adjusted keeping the paddle's size in mind 
	 * 		| result.getCenter().getX() == old(getCenter()).getX() + 20*elapsedTime ||
	 * 		| result.getCenter().getX() == br.getX() - getSize().getX()
	 */
	
	@Override
	
	public PaddleState movePaddleRight(Point br, int elapsedTime) {
		int moveBy = 20 * elapsedTime;
		Point newCenter = new Point(getCenter().getX() + moveBy, getCenter().getY());
		if (newCenter.getX() + getSize().getX() > br.getX()){
			newCenter = new Point(br.getX() - getSize().getX(), newCenter.getY());
		}
		return new ReplicatingPaddleState(newCenter, amountOfReplications);
	}
	
	/**
	 * Returns a new replicator paddle  that has moved {@code 20 * elapsedTime} units to the left, keeping in mind that it can't go outside of the field
	 * 
	 * @pre Argument {@code elapsedTime} is greater than 0
	 * 		| elapsedTime > 0
	 * 
	 * @post The result is not {@code null}
	 * 		|  result != null
	 * @post The resulting paddle is the same kind of paddle as {@code this}, which in this case is a replicator paddle
	 * 		| result.getClass().equals(getClass())
	 * @post The resulting paddle's center's Y coordinate has remained the same
	 * 		| result.getCenter().getY() == old(getCenter()).getY()
	 * @post The resulting paddle has moved left by {@code 20 * elapsedTime} units, unless it would have gone outside of the field, 
	 * 		 in which case its center's x-coordinate has been adjusted keeping the paddle's size in mind
	 * 		| result.getCenter().getX() == old(getCenter()).getX() - 20*elapsedTime || 
	 * 		| result.getCenter().getX() == getSize().getX()
	 */
	
	@Override
	
	public PaddleState movePaddleLeft(int elapsedTime) {
		int moveBy = 20 * elapsedTime;
		Point newCenter = new Point(getCenter().getX() - moveBy, getCenter().getY());
		if (newCenter.getX() - getSize().getX() < 0){
			newCenter = new Point(getSize().getX(), newCenter.getY());
		}
		return new ReplicatingPaddleState(newCenter, amountOfReplications);
	}
	
}
