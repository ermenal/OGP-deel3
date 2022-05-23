package breakout.radioactivity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Color;
import java.util.Arrays;

import breakout.BreakoutFacade;
import breakout.utils.*;


public class BallTest {
	
	Ball normalBall1;
	Ball superchargedBall1;
	
	Ball normalBall2;
	Ball superchargedBall2;
	
	Ball normalBall3;
	Ball superchargedBall3;
	
	Point center1;
	int diameter1;
	Vector velocity1;
	
	Point center2;
	int diameter2;
	Vector velocity2;
	
	Point center3;
	int diameter3;
	Vector velocity3;
	
	Point br1;
	Point br2;
	Point br3;
	
	Vector zeroVector;
	int maxElapsedTime;
	
	
	@BeforeEach
	void setUp() throws Exception {
		BreakoutFacade facade = new BreakoutFacade();
		zeroVector = new Vector(0, 0);
		maxElapsedTime = 10000;
		
		center1 = new Point(25000, 25000);
		diameter1 = 1000;
		velocity1 = new Vector(10, 5);
		center2 = new Point(1000, 500);
		diameter2 = 500;
		velocity2 = new Vector(0, -10);
		center3 = new Point(10000, 10000);
		diameter3 = 700;
		velocity3 = new Vector(100, 100);
		
		normalBall1 = facade.createNormalBall(center1, diameter1, velocity1);
		superchargedBall1 = facade.createSuperchargedBall(center1, diameter1, velocity1, 0);
		
		normalBall2 = facade.createNormalBall(center2, diameter2, velocity2);
		superchargedBall2 = facade.createSuperchargedBall(center2, diameter2, velocity2, 0);
		
		normalBall3 = facade.createNormalBall(center3, diameter3, velocity3);
		superchargedBall3 = facade.createSuperchargedBall(center3, diameter3, velocity3, 5000);
		
		br1 = new Point(50000, 30000);
		br2 = new Point(100000, 100000);
		br3 = new Point(15000, 15000);
	
	}	
	
	@Test
	void testBallsEqualContent() {
		//Testing normal & supercharged ball1 properties
		assertTrue(normalBall1.getCenter().equals(center1));
		assertTrue(normalBall1.getDiameter() == diameter1);
		assertTrue(normalBall1.getVelocity().equals(velocity1));
		
		assertTrue(superchargedBall1.getCenter().equals(center1));
		assertTrue(superchargedBall1.getDiameter() == diameter1);
		assertTrue(superchargedBall1.getVelocity().equals(velocity1));
		
		//Testing normalBall & superchargedBall equalContent methods
		
		assertTrue(normalBall1.equalContent(new NormalBall(center1, diameter1, velocity1)));
		assertFalse(normalBall1.equalContent(superchargedBall1));
		
		assertTrue(superchargedBall1.equalContent(new SuperchargedBall(center1, diameter1, velocity1, 0)));
		
		assertFalse(normalBall1.equalContent(null));
		assertFalse(superchargedBall1.equalContent(null));
		
		assertEquals(Color.WHITE, normalBall1.getColor());
		assertEquals(Color.GREEN, superchargedBall1.getColor());
		
	}
	
	@Test
	void testMoveNormalBalls() {
		//NormalBall
		Ball ball1BeforeMoving = normalBall1.cloneBallWithChangedVelocity(zeroVector);
		Ball ball1AfterMoving = new NormalBall(normalBall1.getCenter().plus(normalBall1.getVelocity()), diameter1, velocity1);
		
		assertTrue(normalBall1.equalContent(ball1BeforeMoving));
		normalBall1.moveBall(br1, 1);
		assertFalse(normalBall1.equalContent(ball1BeforeMoving));
		assertTrue(normalBall1.equalContent(ball1AfterMoving));
		
		Ball ball1AfterMoving5Ms = new NormalBall(normalBall1.getCenter().plus(velocity1.scaled(5)), diameter1, velocity1);
		normalBall1.moveBall(br1, 5);
		assertTrue(normalBall1.equalContent(ball1AfterMoving5Ms));
		assertFalse(normalBall1.equalContent(ball1BeforeMoving));
		assertFalse(normalBall1.equalContent(ball1AfterMoving));
		ball1AfterMoving.moveBall(br1, 5);
		assertTrue(normalBall1.equalContent(ball1AfterMoving));
		
		Ball ball2AfterMoving30Ms = new NormalBall(new Point(center2.getX(), 250), diameter2, velocity2);
		normalBall2.moveBall(br2, 30);
		assertTrue(normalBall2.equalContent(ball2AfterMoving30Ms));
	}
	
	@Test
	void testMoveSuperchargedBalls() {
		//NormalBall
		Ball ball1BeforeMoving = superchargedBall1.cloneBallWithChangedVelocity(zeroVector);
		Ball ball1AfterMoving = new SuperchargedBall(superchargedBall1.getCenter().plus(superchargedBall1.getVelocity()), diameter1, velocity1, 0);
		
		assertTrue(superchargedBall1.equalContent(ball1BeforeMoving));
		superchargedBall1.moveBall(br1, 1);
		assertFalse(superchargedBall1.equalContent(ball1BeforeMoving));
		assertTrue(superchargedBall1.equalContent(ball1AfterMoving));
		
		Ball ball1AfterMoving5Ms = new SuperchargedBall(superchargedBall1.getCenter().plus(velocity1.scaled(5)), diameter1, velocity1, 0);
		superchargedBall1.moveBall(br1, 5);
		assertTrue(superchargedBall1.equalContent(ball1AfterMoving5Ms));
		assertFalse(superchargedBall1.equalContent(ball1BeforeMoving));
		assertFalse(superchargedBall1.equalContent(ball1AfterMoving));
		ball1AfterMoving.moveBall(br1, 5);
		assertTrue(superchargedBall1.equalContent(ball1AfterMoving));
	}
	
	@Test
	void testSuperchargedTimeHandler() {
		Ball ball1BeforeMoving = normalBall1.cloneBallWithChangedVelocity(zeroVector);
		assertTrue(normalBall1.superchargedTimeHandler(10, 10000).equalContent(ball1BeforeMoving));
		assertTrue(normalBall1.equalContent(ball1BeforeMoving));
		assertTrue(normalBall1.superchargedTimeHandler(10001, 10000).equalContent(ball1BeforeMoving));
		
		Ball superchargedBall1BeforeHandler = superchargedBall1.cloneBallWithChangedVelocity(zeroVector);
		Ball superchargedBall1AfterHandler = new SuperchargedBall(center1, diameter1, velocity1, 10);
		assertFalse(superchargedBall1.superchargedTimeHandler(10, 10000).equalContent(superchargedBall1BeforeHandler));
		assertTrue(superchargedBall1.equalContent(superchargedBall1AfterHandler));
		assertTrue(superchargedBall1.superchargedTimeHandler(10001, 10000).equalContent(ball1BeforeMoving));
	}
	
	@Test
	void testHitBlockNormalBall() {
		Point topLeft1 = new Point(23000, 25480);
		Point bottomRight1 = new Point(29000, 29000);
		Rect normalBlockRectangle1 = new Rect(topLeft1, bottomRight1);
		Point topLeft2 = new Point(0, 0);
		Point bottomRight2 = new Point(2000, 300);
		Rect normalBlockRectangle2 = new Rect(topLeft2, bottomRight2);
		
		Ball ball1BeforeBounce = new NormalBall(center1, diameter1, velocity1);
		Ball ball1AfterBounce = new NormalBall(center1, diameter1, velocity1.mirrorOver(new Vector (0, -1)));
		
		Ball ball2BeforeBounce = new NormalBall(center2, diameter2, velocity2);
		Ball ball2AfterBounce = new NormalBall(center2, diameter2, velocity2.mirrorOver(new Vector(0, 1)));
		
		assertTrue(normalBall1.equalContent(ball1BeforeBounce));
		assertFalse(normalBall1.equalContent(ball1AfterBounce));
		normalBall1.hitBlock(normalBlockRectangle1, true);
		assertFalse(normalBall1.equalContent(ball1BeforeBounce));
		assertTrue(normalBall1.equalContent(ball1AfterBounce));
		
		assertTrue(normalBall2.equalContent(ball2BeforeBounce));
		assertFalse(normalBall2.equalContent(ball2AfterBounce));
		normalBall2.hitBlock(normalBlockRectangle2, true);
		assertFalse(normalBall2.equalContent(ball2BeforeBounce));
		assertTrue(normalBall2.equalContent(ball2AfterBounce));
	}
	
	@Test
	void testHitBlockSuperchargedBall() {
		Point topLeft1 = new Point(23000, 25480);
		Point bottomRight1 = new Point(29000, 29000);
		Rect normalBlockRectangle1 = new Rect(topLeft1, bottomRight1);
		Point topLeft2 = new Point(0, 0);
		Point bottomRight2 = new Point(2000, 300);
		Rect sturdyBlockRectangle2 = new Rect(topLeft2, bottomRight2);
		
		Ball ball1BeforeBounce = new SuperchargedBall(center1, diameter1, velocity1, 0);
		Ball ball1AfterBounce = new SuperchargedBall(center1, diameter1, velocity1.mirrorOver(new Vector (0, -1)), 0);
		
		Ball ball2BeforeBounce = new SuperchargedBall(center2, diameter2, velocity2, 0);
		Ball ball2AfterBounce = new SuperchargedBall(center2, diameter2, velocity2.mirrorOver(new Vector(0, 1)), 0);
		
		assertTrue(superchargedBall1.equalContent(ball1BeforeBounce));
		assertFalse(superchargedBall1.equalContent(ball1AfterBounce));
		superchargedBall1.hitBlock(normalBlockRectangle1, true);
		assertTrue(superchargedBall1.equalContent(ball1BeforeBounce));
		assertFalse(superchargedBall1.equalContent(ball1AfterBounce));
		
		assertTrue(superchargedBall2.equalContent(ball2BeforeBounce));
		assertFalse(superchargedBall2.equalContent(ball2AfterBounce));
		superchargedBall2.hitBlock(sturdyBlockRectangle2, false);
		assertFalse(superchargedBall2.equalContent(ball2BeforeBounce));
		assertTrue(superchargedBall2.equalContent(ball2AfterBounce));
	}
	
}

