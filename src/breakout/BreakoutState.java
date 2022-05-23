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
			
		this.balls = cloneBallsArray(balls);
		this.alphas = cloneAlphasArray(alphas);
		this.blocks = blocks.clone();
		this.bottomRight = bottomRight;
		this.paddle = paddle;
	}
	
	private Ball[] cloneBallsArray(Ball[] balls) {
		Ball[] res = new Ball[balls.length];
		for (int i = 0; i < balls.length; i++) {
			res[i] = balls[i].clone();
			for(Alpha alpha: balls[i].getAlphas()) {
				balls[i].unLink(alpha);
			}
		}
		this.balls = res;
		return res;
	}
	
	private Alpha[] cloneAlphasArray(Alpha[] alphas) {
		Alpha[] res = new Alpha[alphas.length];
		for (int i = 0; i < alphas.length; i++) {
			res[i] = alphas[i].clone();
			for (Ball ball: alphas[i].getBalls()) {
				ball.unLink(alphas[i]);
			}
		}
		this.alphas = res;
		return res;
	}
	
	public Ball[] getBalls() {
		return cloneBallsArray(balls);
	}
	
	public Alpha[] getAlphas() {
		return cloneAlphasArray(alphas);
	}
	
	public BlockState[] getBlocks() {
		return blocks.clone();
	}
	
	public PaddleState getPaddle() {
		return paddle;
	}
	
	public Point getBottomRight() {
		return bottomRight;
	}
	
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
			Ball tempAlpha = new NormalBall(alpha.getCenter(), alpha.getDiameter(), alpha.getVelocity());
			if (tempAlpha.raaktRechthoek(leftWall, 4)) {
				alpha.bounceWall(1);
				for (Ball ball: alpha.getBalls()) {
					ball.linkedAlphaHitWall(alpha);
				}
				continue;
			}
			if (tempAlpha.raaktRechthoek(topWall, 1)) {
				alpha.bounceWall(2);
				for (Ball ball: alpha.getBalls()) {
					ball.linkedAlphaHitWall(alpha);
				}
				continue;
			}
			if (tempAlpha.raaktRechthoek(rightWall, 2)) {
				alpha.bounceWall(3);
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
			Ball tempAlpha = new NormalBall(alphas[i].getCenter(), alphas[i].getDiameter(), alphas[i].getVelocity());
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
			if (ball.raaktRechthoek(paddleRect, 3)) {
				geraakt = true;
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
				ball.setVelocity(ball.getVelocity().plus(addedVelocity));
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
			Ball tempAlpha = new NormalBall(alpha.getCenter(), alpha.getDiameter(), alpha.getVelocity());
			if (tempAlpha.raaktRechthoek(paddleRect, 3)) {
				geraakt = true;
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
