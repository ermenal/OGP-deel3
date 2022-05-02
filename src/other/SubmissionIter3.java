package other;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import breakout.BreakoutFacade;
import breakout.BreakoutState;
import breakout.BlockState;
import breakout.PaddleState;
import radioactivity.Alpha;
import radioactivity.Ball;
import utils.Point;
import utils.Rect;
import utils.Vector;

class SubmissionIter3 {

	static final Point BR = new Point(50000, 30000);
	static final Point origin = new Point(0, 0);
	static final BreakoutFacade fac = new BreakoutFacade();
	static final Point defPoint = new Point(5000, 5000);
	static final Vector downSpeed = new Vector(0, 5);
	
//	@BeforeEach
//	void setUp() throws Exception {
//	}
	
	@Test
	void noEquals() {
		Alpha alph1 = fac.createAlpha(defPoint, 700, downSpeed);
		Alpha alph2 = fac.createAlpha(defPoint, 700, downSpeed);
		Ball ball1 = fac.createNormalBall(defPoint, 700, downSpeed);
		Ball ball2 = fac.createNormalBall(defPoint, 700, downSpeed);
		
		//because equals should do the default reference check and nothing more.
		assertNotEquals(alph1, alph2);
		assertNotEquals(ball1, ball2);
	}

	@Test
	void paddleSize() {
		Rect paddleRect = fac.getLocation( fac.createNormalPaddleState(defPoint) );
		assertTrue( paddleRect.getHeight() == 500);
		assertTrue( paddleRect.getWidth() == 3000);
	}
	
	@Test
	void simpleTick() {
		Alpha alpha = fac.createAlpha(defPoint.plus(downSpeed), 700, downSpeed);
		Ball ball = fac.createSuperchargedBall(defPoint, 700, downSpeed, 10000);
		fac.addLink(ball, alpha);
		BlockState tlBlock = fac.createNormalBlockState(origin, new Point(5000, 3750));
		PaddleState paddle = fac.createNormalPaddleState(defPoint.plus(new Vector(10000,10000)));
		
		
		BreakoutState state = fac.createBreakoutState(
				new Alpha[] {alpha}, new Ball[] {ball}, new BlockState[] {tlBlock}, BR, paddle);
		fac.tickDuring(state, 50000); //the ball and the alpha disappear below the bot line.
		assertTrue( fac.isDead(state) );
	}
	
	@Test
	void simplePaddleBounce() {
		//To the left, a ball. To the right an alpha. Both are going down.
		Alpha alpha = fac.createAlpha(defPoint.plus(new Vector(1000,0)), 700, downSpeed);
		Ball ball = fac.createSuperchargedBall(defPoint, 700, downSpeed, 10000);
		Vector oldBallSpeed = fac.getVelocity(ball);
		Vector oldAlphaSpeed = fac.getVelocity(alpha);
		fac.addLink(ball, alpha); //(besides they are linked)
		BlockState tlBlock = fac.createNormalBlockState(origin, new Point(5000, 3750));
		// the paddle is below our ball/alpha and does not collide them.
		PaddleState paddle = fac.createNormalPaddleState(defPoint.plus(new Vector(0,351 + 250)));//fails with 249
		BreakoutState state = fac.createBreakoutState(
				new Alpha[] {alpha}, new Ball[] {ball}, new BlockState[] {tlBlock}, BR, paddle);
		
		assertFalse( fac.collidesWith(
				fac.getAlphas(state)[0],
				fac.getLocation(paddle))) ;
		assertFalse( fac.collidesWith(
				fac.getBalls(state)[0],
				fac.getLocation(paddle))) ;
		
		fac.tickDuring(state, 20); //the ball/alpha bounce and move away from the paddle
		
		assertFalse( fac.collidesWith(
				fac.getAlphas(state)[0],
				fac.getLocation(paddle))) ;
		assertFalse( fac.collidesWith(
				fac.getBalls(state)[0],
				fac.getLocation(paddle))) ;
		
		assertNotEquals( oldBallSpeed, fac.getBall0Vel(state) ); //speeds have been mirrored.
		assertNotEquals( oldAlphaSpeed, fac.getVelocity(fac.getAlphas(state)[0]) );
		
		assertFalse( fac.isDead(state) );
	}

}
