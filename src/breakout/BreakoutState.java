package breakout;

import breakout.utils.Vector;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import breakout.radioactivity.*;
import breakout.utils.Point;
import breakout.utils.Rect;

public class BreakoutState {
	
	private static final Vector PADDLE_VEL = new Vector(20, 0);
	public static final int MAX_BALL_REPLICATE = 5;
	private static final Vector[] BALL_VEL_VARIATIONS = new Vector[] { new Vector(0, 0), new Vector(2, -2),
			new Vector(-2, 2), new Vector(2, 2), new Vector(-2, -2) };
	public static int MAX_ELAPSED_TIME = 50;
	private static final int MAX_SUPERCHARGED_TIME = 10000;
	
	/**
	 * @invar | balls != null
	 * @invar | Arrays.stream(balls).noneMatch(e -> e == null)
	 * 
	 * 
	 * @invar | blocks != null
	 * @invar | Arrays.stream(blocks).noneMatch(e -> e == null)
	 * @invar | Arrays.stream(blocks).noneMatch(e -> 
	 * 		  |		e.getTopLeft().getX() < 0 || 
	 *        | 	e.getBottomRight().getX() > bottomRight.getX() ||
	 *        |		e.getTopLeft().getY() < 0 || 
	 *        |		e.getBottomRight().getY() > bottomRight.getY() )
	 * 
	 * @invar | bottomRight != null
	 * @invar | bottomRight.getX() >= 0 && bottomRight.getY() >= 0
	 * 
	 * @invar | paddle != null
	 * @invar | (new Rect(new Point(0, 0), bottomRight)).contains(new Rect(paddle.getTopLeft(), paddle.getBottomRight()))
	 * @representationObject
	 */
	
	private Ball[] balls;
	private Alpha[] alphas;
	private BlockState[] blocks;
	private final Point bottomRight;
	private PaddleState paddle;
	
	
	public BreakoutState(Ball[] balls, BlockState[] blocks, Point bottomRight, PaddleState paddle) {
		
		if (balls == null || blocks == null || bottomRight == null || paddle == null) {
			throw new IllegalArgumentException("BreakoutState arguments can't be null");
		}
		if (Arrays.stream(balls).anyMatch(b -> b == null) || Arrays.stream(blocks).anyMatch(b -> b == null)) {
			throw new IllegalArgumentException("balls and blocks may not have elements that are null");
		}
		
		if (Arrays.stream(balls).anyMatch(e -> e.getCenter().getX() - e.getDiameter()/2 < 0 || 
				e.getCenter().getX() + e.getDiameter()/2 > bottomRight.getX() ||  
				e.getCenter().getY() + e.getDiameter()/2 > bottomRight.getY() || 
				e.getCenter().getY() - e.getDiameter()/2 < 0 ) || 
			Arrays.stream(blocks).anyMatch(e -> e.getTopLeft().getX() < 0 || 
					e.getBottomRight().getX() > bottomRight.getX() || 
					e.getBottomRight().getY() > bottomRight.getY() ||
					e.getTopLeft().getY() < 0 ) ) {
			throw new IllegalArgumentException("balls and blocks may not have elements that are outside of the field");
		}
		
		if (paddle.getBottomRight().getX() > bottomRight.getX() || paddle.getBottomRight().getY() > bottomRight.getY() || paddle.getTopLeft().getX() < 0 || paddle.getTopLeft().getY() < 0) {
			throw new IllegalArgumentException("paddle should be inside of the field");
		}
			
		if (bottomRight.getX() < 0 || bottomRight.getY() < 0) {
			throw new IllegalArgumentException("bottomRight should not be to the left of or above (0, 0)");
		}
			
		Alpha[] alphas = new Alpha[0];
		
		this.balls = balls.clone();
		this.alphas = alphas;
		this.blocks = blocks.clone();
		this.bottomRight = bottomRight;
		this.paddle = paddle;
	}
	
	
	public BreakoutState(Ball[] balls, Alpha[] alphas, BlockState[] blocks, Point bottomRight, PaddleState paddle) {
		
		if (balls == null || alphas == null || blocks == null || bottomRight == null || paddle == null) {
			throw new IllegalArgumentException("BreakoutState arguments can't be null");
		}
		if (Arrays.stream(balls).anyMatch(b -> b == null) || Arrays.stream(alphas).anyMatch(a -> a == null) ||  Arrays.stream(blocks).anyMatch(b -> b == null)) {
			throw new IllegalArgumentException("balls, alphas and blocks may not have elements that are null");
		}
		
		for (Ball ball: balls) {
			for (Alpha alpha: ball.getAlphas()) {
				if (!alpha.getBalls().contains(ball))
					throw new IllegalArgumentException("Bidirectional association error: Ball is linked to alpha, but alpha isn't linked to ball");
			}
		}
		
		for (Alpha alpha: alphas) {
			for (Ball ball: alpha.getBalls()) {
				if (!ball.getAlphas().contains(alpha))
					throw new IllegalArgumentException("Bidirectional association error: Alpha is linked to ball, but ball isn't linked to alpha");
			}
		}
		
		if (IntStream.range(0, balls.length).anyMatch(i -> IntStream.range(0, balls.length).anyMatch(e -> balls[i] == balls[e] && i != e) ))
			throw new IllegalArgumentException("There should be no duplicate balls");
		
		if (IntStream.range(0, alphas.length).anyMatch(i -> IntStream.range(0, alphas.length).anyMatch(e -> alphas[i] == alphas[e] && i != e) ))
			throw new IllegalArgumentException("There should be no duplicate alphas");
			
			
		if (Arrays.stream(balls).anyMatch(e -> e.getCenter().getX() - e.getDiameter()/2 < 0 || 
				e.getCenter().getX() + e.getDiameter()/2 > bottomRight.getX() ||  
				e.getCenter().getY() + e.getDiameter()/2 > bottomRight.getY() || 
				e.getCenter().getY() - e.getDiameter()/2 < 0 ) ||
			Arrays.stream(alphas).anyMatch(e -> e.getCenter().getX() - e.getDiameter()/2 < 0 || 
					e.getCenter().getX() + e.getDiameter()/2 > bottomRight.getX() ||  
					e.getCenter().getY() + e.getDiameter()/2 > bottomRight.getY() || 
					e.getCenter().getY() - e.getDiameter()/2 < 0 ) ||
			Arrays.stream(blocks).anyMatch(e -> e.getTopLeft().getX() < 0 || 
					e.getBottomRight().getX() > bottomRight.getX() || 
					e.getBottomRight().getY() > bottomRight.getY() ||
					e.getTopLeft().getY() < 0 ) ) {
			throw new IllegalArgumentException("balls, alphas and blocks may not have elements that are outside of the field");
		}
		
		if (paddle.getBottomRight().getX() > bottomRight.getX() || paddle.getBottomRight().getY() > bottomRight.getY() || paddle.getTopLeft().getX() < 0 || paddle.getTopLeft().getY() < 0) {
			throw new IllegalArgumentException("paddle should be inside of the field");
		}
			
		if (bottomRight.getX() < 0 || bottomRight.getY() < 0) {
			throw new IllegalArgumentException("bottomRight should not be to the left of or above (0, 0)");
		}
			
		initialClone(balls, alphas);
		this.blocks = blocks.clone();
		this.bottomRight = bottomRight;
		this.paddle = paddle;
	}
	
	private void initialClone(Ball[] balls, Alpha[] alphas) {
		Ball[] ballRes = new Ball[balls.length];
		Alpha[] alphaRes = new Alpha[alphas.length];
		
		for (int i=0; i<balls.length; i++) {
			ballRes[i] = balls[i].cloneBallWithChangedVelocity(BALL_VEL_VARIATIONS[0]);
		}
		for (int i=0; i<alphas.length; i++) {
			alphaRes[i] = new Alpha(alphas[i].getCenter(), alphas[i].getDiameter(), alphas[i].getVelocity());
		}
		for (int i=0; i<balls.length; i++) {
			for (int j=0; j<alphas.length; j++) {
				if (balls[i].getAlphas().contains(alphas[j])) {
					ballRes[i].linkTo(alphaRes[j]);
				}
			}
		}
		this.balls = ballRes;
		this.alphas = alphaRes;
	}
	
	private Ball[] cloneBallsArray(Ball[] balls) {
		Ball[] ballRes = new Ball[balls.length];
		Alpha[] alphaRes = new Alpha[alphas.length];
		
		for (int i=0; i<balls.length; i++) {
			ballRes[i] = balls[i].cloneBallWithChangedVelocity(BALL_VEL_VARIATIONS[0]);
		}
		for (int i=0; i<alphas.length; i++) {
			alphaRes[i] = new Alpha(alphas[i].getCenter(), alphas[i].getDiameter(), alphas[i].getVelocity());
		}
		for (int i=0; i<balls.length; i++) {
			for (int j=0; j<alphas.length; j++) {
				if (balls[i].getAlphas().contains(alphas[j])) {
					ballRes[i].linkTo(alphaRes[j]);
				}
			}
		}
		return ballRes;
	}
	
	private Alpha[] cloneAlphas(Alpha[] alphas) {
		Ball[] ballRes = new Ball[balls.length];
		Alpha[] alphaRes = new Alpha[alphas.length];
		
		for (int i=0; i<balls.length; i++) {
			ballRes[i] = balls[i].cloneBallWithChangedVelocity(BALL_VEL_VARIATIONS[0]);
		}
		for (int i=0; i<alphas.length; i++) {
			alphaRes[i] = new Alpha(alphas[i].getCenter(), alphas[i].getDiameter(), alphas[i].getVelocity());
		}
		for (int i=0; i<balls.length; i++) {
			for (int j=0; j<alphas.length; j++) {
				if (balls[i].getAlphas().contains(alphas[j])) {
					ballRes[i].linkTo(alphaRes[j]);
				}
			}
		}
		return alphaRes;
	}
	
	/**
	 * Returns a new array that is a deep clone of the balls array
	 * 
	 * @inspects | this
	 * 
	 * @creates | result
	 */
	
	public Ball[] getBalls() {
		return cloneBallsArray(balls);
	}
	
	/**
	 * Returns a new array that is a deep clone of the alphas array
	 * 
	 * @inspects | this
	 * 
	 * @creates | result
	 */
	
	public Alpha[] getAlphas() {
		return cloneAlphas(alphas);
	}
	
	/**
	 * Returns a new array containing all the blocks 
	 * 
	 * @creates | result
	 */
	
	public BlockState[] getBlocks() {
		return blocks.clone();
	}
	
	/** Returns the paddle */
	
	public PaddleState getPaddle() {
		return paddle;
	}
	
	/**
	 * Returns the coordinates of the bottom right of the field
	 * 
	 * @immutable This object is associated with the same bottom right point throughout its lifetime
	 */

	
	public Point getBottomRight() {
		return bottomRight;
	}
	
	/**
	 * Returns the maximum amount of time a ball can be supercharged for after hitting a powerup block. 
	 * 
	 * @immutable This object is associated with the same maximum supercharged time throughout its lifetime
	 */
	
	public int maxSuperchargedTime() {
		return MAX_SUPERCHARGED_TIME;
	}
	
	
	
	public void tick(int paddleDir, int elapsedTime) {

		int tickCutOff = 20;
		
		if (elapsedTime > tickCutOff) {
			tick(paddleDir, tickCutOff);
			tick(paddleDir, elapsedTime-tickCutOff);
		}else {
		
		superchargedTimeHandler(elapsedTime);
	
		moveAllBalls(elapsedTime);
		
		moveAllAlphas(elapsedTime);
	
		wallCollisionHandler();
		
		lowerWallCollisionHandler();
		
		blockCollisionHandler();
		
		paddleBallCollisionHandler(paddleDir);
		
		paddleAlphaCollisionHandler(paddleDir);
		}
}
	
	
	
	
	
	private void superchargedTimeHandler(int elapsedTime) {
		for (int i=0;i<balls.length;i++) {
			balls[i] = balls[i].superchargedTimeHandler(elapsedTime, MAX_SUPERCHARGED_TIME);
		}
	}
	
	private void moveAllBalls(int elapsedTime) {		
		for (int i=0; i<balls.length; i++) {
			balls[i].moveBall(getBottomRight(), elapsedTime);
		}
	}
	
	private void moveAllAlphas(int elapsedTime) {
		for (int i=0; i<alphas.length; i++) {
			Ball tempBall = new NormalBall(alphas[i].getCenter(), alphas[i].getDiameter(), alphas[i].getVelocity());
			tempBall.moveBall(bottomRight, elapsedTime);
			alphas[i].changeAlphaFromBall(tempBall);
		}
	}
	
	private void wallCollisionHandler() {
		Rect leftWall = new Rect(new Point(-1, 0), new Point(0, bottomRight.getY()));
		Rect topWall = new Rect(new Point(0, -1), new Point(getBottomRight().getX(), 0));
		Rect rightWall = new Rect(new Point(bottomRight.getX(), 0), new Point(bottomRight.getX() + 1, bottomRight.getY()));
		for (Ball ball: balls) {
			if (ball.raaktRechthoek(leftWall, 4)) {
				ball.hitBlock(leftWall, false);
				continue;
			}
			if (ball.raaktRechthoek(topWall, 1)) {
				ball.hitBlock(topWall, false);
				continue;
			}
			if (ball.raaktRechthoek(rightWall, 2)) {
				ball.hitBlock(rightWall, false);
				continue;
			}
		}
		
		for (Alpha alpha: alphas) {
			Ball tempAlpha = alpha.createNormalBallFromAlpha();
			if (tempAlpha.raaktRechthoek(leftWall, 4)) {
				tempAlpha.hitBlock(leftWall, false);
				alpha.changeAlphaFromBall(tempAlpha);
				for (Ball ball: alpha.getBalls()) {
					ball.linkedAlphaHitWall(alpha);
				}
				continue;
			}
			if (tempAlpha.raaktRechthoek(topWall, 1)) {
				tempAlpha.hitBlock(topWall, false);
				alpha.changeAlphaFromBall(tempAlpha);
				for (Ball ball: alpha.getBalls()) {
					ball.linkedAlphaHitWall(alpha);
				}
				continue;
			}
			if (tempAlpha.raaktRechthoek(rightWall, 2)) {
				tempAlpha.hitBlock(rightWall, false);
				alpha.changeAlphaFromBall(tempAlpha);
				for (Ball ball: alpha.getBalls()) {
					ball.linkedAlphaHitWall(alpha);
				}
				continue;
			}
		}
	}
	
	private void lowerWallCollisionHandler() {
		Rect bottomWall = new Rect(new Point(0, bottomRight.getY()), new Point(bottomRight.getX(), bottomRight.getY()+1));
		for (int i=0; i<balls.length; i++) {
			if (balls[i].raaktRechthoek(bottomWall, 3)) {
				for (Alpha alpha: balls[i].getAlphas()) {
					balls[i].unLink(alpha);
				}
				balls[i] = null;
			}
		}
		balls = Arrays.stream(balls).filter(b -> b != null).toArray(Ball[]::new);
		for (int i=0; i<alphas.length; i++) {
			Ball tempAlpha = alphas[i].createNormalBallFromAlpha();
			if (tempAlpha.raaktRechthoek(bottomWall, 3)) {
				for (Ball ball: alphas[i].getBalls()) {
					ball.unLink(alphas[i]);
				}
				alphas[i] = null;
			}
		}
		alphas = Arrays.stream(alphas).filter(a -> a != null).toArray(Alpha[]::new);
	}
	
	private void blockCollisionHandler() {
		for (int j=0; j < balls.length;j++) {
			BlockState[] tempBlocks = getBlocks();
			for (int i=0; i<tempBlocks.length; i++) {
				boolean geraakt = false;
				Rect blockRechthoek = new Rect(tempBlocks[i].getTopLeft(), tempBlocks[i].getBottomRight());
				if (balls[j].raaktRechthoek(blockRechthoek, 1)) {
					geraakt = true;
				}
				if (balls[j].raaktRechthoek(blockRechthoek, 2)) {
					geraakt = true;
				}
				if (balls[j].raaktRechthoek(blockRechthoek, 3)) {
					geraakt = true;
				}
				if (balls[j].raaktRechthoek(blockRechthoek, 4)) {
					geraakt = true;
				}
				if (geraakt) {
					balls[j].hitBlock(blockRechthoek, tempBlocks[i].getsDestroyedOnCollision());
					paddle = tempBlocks[i].specialBlockHandler(paddle);
					balls[j] = tempBlocks[i].specialBlockHandler(balls[j]);
					tempBlocks[i] = tempBlocks[i].specialBlockHandler();
				}
			}
			blocks = Arrays.stream(tempBlocks).filter(b -> b != null).toArray(BlockState[]::new);
		}
	}
	
	private void paddleBallCollisionHandler(int paddleDir) {
		Rect paddleRect = new Rect(paddle.getTopLeft(), paddle.getBottomRight());
		Vector addedVelocity = PADDLE_VEL.scaledDiv(5).scaled(paddleDir);
		for (Ball ball: balls) {
			boolean geraakt = false;
			// Make it so the ball's velocity only gets addedVelocity when it hit the paddle on the top side
			// Adding this velocity when the ball hit the paddle on another side is meaningless and causes slight problems
			boolean topSide = false;
			if (ball.raaktRechthoek(paddleRect, 3)) {
				geraakt = true;
				topSide = true;
			}
			if (ball.raaktRechthoek(paddleRect, 2)) {
				geraakt = true;
			}
			if (ball.raaktRechthoek(paddleRect, 4)) {
				geraakt = true;
			}
			if (ball.raaktRechthoek(paddleRect, 1)) {
				geraakt = true;
			}
			
			if (geraakt) {
				ball.hitBlock(paddleRect, false);
				if (topSide) {
					ball.setVelocity(ball.getVelocity().plus(addedVelocity));
				}
				balls = Arrays.stream(paddle.hitPaddleReplicationHandler(balls, ball)).filter(b -> b != null).toArray(Ball[]::new);
				paddle = paddle.ballHitPaddle();
				Alpha newAlpha = new Alpha(ball.getCenter(), ball.getDiameter(), ball.getVelocity().plus(BALL_VEL_VARIATIONS[4]));
				ball.linkTo(newAlpha);
				alphas = Stream.concat(Arrays.stream(alphas), Stream.of(newAlpha)).toArray(Alpha[]::new);
				
			}
		}
	} 
	
	private void paddleAlphaCollisionHandler(int paddleDir) {
		Rect paddleRect = new Rect(paddle.getTopLeft(), paddle.getBottomRight());
		Vector addedVelocity = PADDLE_VEL.scaledDiv(5).scaled(paddleDir);
		for (Alpha alpha: alphas) {
			boolean geraakt = false;
			// Make it so the alpha's velocity only gets addedVelocity when it hit the paddle on the top side
			// Adding this velocity when the alpha hit the paddle on another side is meaningless and causes slight problems
			boolean topSide = false;
			Ball tempAlpha = alpha.createNormalBallFromAlpha();
			if (tempAlpha.raaktRechthoek(paddleRect, 3)) {
				geraakt = true;
				topSide = true;
			}
			if (tempAlpha.raaktRechthoek(paddleRect, 2)) {
				geraakt = true;
			}
			if (tempAlpha.raaktRechthoek(paddleRect, 4)) {
				geraakt = true;
			}
			if (tempAlpha.raaktRechthoek(paddleRect, 1)) {
				geraakt = true;
			}
			if (geraakt) {
				tempAlpha.hitBlock(paddleRect, false);
				if (topSide) {
					tempAlpha.setVelocity(tempAlpha.getVelocity().plus(addedVelocity));
				}
				alpha.changeAlphaFromBall(tempAlpha);
				Ball newBall = new NormalBall(alpha.getCenter(), alpha.getDiameter(), alpha.getVelocity().plus(BALL_VEL_VARIATIONS[4]));
				newBall.linkTo(alpha);
				balls = Stream.concat(Arrays.stream(balls), Stream.of(newBall)).toArray(Ball[]::new);
			}
		}
	}
	
	public void movePaddleRight(int elapsedTime) {
		paddle = paddle.movePaddleRight(getBottomRight(), elapsedTime);
	}
	
	public void movePaddleLeft(int elapsedTime) {
		paddle = paddle.movePaddleLeft(elapsedTime);
	}
	
	public boolean isWon() {
		return blocks.length == 0 && balls.length > 0;
	}
	
	public boolean isDead() {
		return balls.length == 0;
	}
	
}
