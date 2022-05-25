package breakout.radioactivity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Color;

import breakout.BreakoutFacade;
import breakout.utils.*;


public class AlphaTest {
	
	Alpha alpha1;
	Alpha alpha2;
	Alpha alpha3;
	
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
		velocity3 = new Vector(50, 100);
		
		alpha1 = facade.createAlpha(center1, diameter1, velocity1);
		alpha2 = facade.createAlpha(center2, diameter2, velocity2);
		alpha3 = facade.createAlpha(center3, diameter3, velocity3);
		
		br1 = new Point(50000, 30000);
		br2 = new Point(100000, 100000);
		br3 = new Point(15000, 15000);
	
	}	
	
	@Test
	void testAlphaEqualContent() {
		//Testing alpha properties
		assertTrue(alpha1.getCenter().equals(center1));
		assertTrue(alpha1.getDiameter() == diameter1);
		assertTrue(alpha1.getVelocity().equals(velocity1));
		
		assertTrue(alpha2.getCenter().equals(center2));
		assertTrue(alpha2.getDiameter() == diameter2);
		assertTrue(alpha2.getVelocity().equals(velocity2));
		
		assertTrue(alpha3.getCenter().equals(center3));
		assertTrue(alpha3.getDiameter() == diameter3);
		assertTrue(alpha3.getVelocity().equals(velocity3));
		
		//Testing alpha equalContent methods
		
		assertTrue(alpha1.equalContent(new Alpha(center1, diameter1, velocity1)));
		assertFalse(alpha1.equalContent(alpha2));
		assertFalse(alpha1.equalContent(new NormalBall(center1, diameter1, velocity1)));
		
		assertTrue(alpha2.equalContent(new Alpha(center2, diameter2, velocity2)));
		assertFalse(alpha2.equalContent(alpha3));
		assertFalse(alpha2.equalContent(new NormalBall(center2, diameter2, velocity2)));
		
		assertTrue(alpha3.equalContent(new Alpha(center3, diameter3, velocity3)));
		assertFalse(alpha3.equalContent(alpha2));
		assertFalse(alpha3.equalContent(new NormalBall(center3, diameter3, velocity3)));
		
		assertFalse(alpha1.equalContent(null));
		assertFalse(alpha2.equalContent(null));
		assertFalse(alpha3.equalContent(null));
		
		assertEquals(Color.LIGHT_GRAY, alpha1.getColor());
	}
	
	@Test
	void testClone() {
		Alpha alpha1Clone = alpha1.clone();
		assertTrue(alpha1.equalContent(alpha1Clone));
		assertTrue(alpha1Clone.equalContent(alpha1));
		assertTrue(alpha1 != alpha1Clone);
		
		Ball tb = alpha1.createNormalBallFromAlpha();
		tb.moveBall(br1, 1);
		alpha1.changeAlphaFromBall(tb);
		assertFalse(alpha1.equalContent(alpha1Clone));
	}
	
	
	@Test
	void testMoveAlphas() {
		Alpha alpha1BeforeMoving = alpha1.clone();
		Alpha alpha1AfterMoving = new Alpha(alpha1.getCenter().plus(alpha1.getVelocity()), diameter1, velocity1);
		
		assertTrue(alpha1.equalContent(alpha1BeforeMoving));
		Ball tb = alpha1.createNormalBallFromAlpha();
		tb.moveBall(br1, 1);
		alpha1.changeAlphaFromBall(tb);
		assertFalse(alpha1.equalContent(alpha1BeforeMoving));
		assertTrue(alpha1.equalContent(alpha1AfterMoving));
		
		Alpha alpha2AfterMoving5Ms = new Alpha(alpha2.getCenter().plus(velocity2.scaled(5)), diameter2, velocity2);
		Alpha alpha2BeforeMoving = alpha2.clone();
		assertTrue(alpha2.equalContent(alpha2BeforeMoving));
		Ball tb2 = alpha2.createNormalBallFromAlpha();
		tb2.moveBall(br1, 5);
		alpha2.changeAlphaFromBall(tb2);
		assertTrue(alpha2.equalContent(alpha2AfterMoving5Ms));
		assertFalse(alpha2.equalContent(alpha2BeforeMoving));
		assertFalse(alpha2.equalContent(alpha1AfterMoving));
		Ball tb2bm = alpha2BeforeMoving.createNormalBallFromAlpha();
		tb2bm.moveBall(br1, 5);
		alpha2BeforeMoving.changeAlphaFromBall(tb2bm);
		assertTrue(alpha2.equalContent(alpha2BeforeMoving));
		
		Alpha alpha2AfterMoving30Ms = new Alpha(new Point(center2.getX(), 250), diameter2, velocity2);
		Ball tb3 = alpha2.createNormalBallFromAlpha();
		tb3.moveBall(br2, 30);
		alpha2.changeAlphaFromBall(tb3);
		assertTrue(alpha2.equalContent(alpha2AfterMoving30Ms));
	}
	
	@Test
	void testHitWall() {
		Alpha alphaBottomWall = alpha3.clone();
		Alpha alphaLeftWall = new Alpha(new Point(100, 1000), 500, new Vector(-100, 5));
		Alpha alphaTopWall = new Alpha(new Point(5000, 500), 500, new Vector(5, -100));
		Alpha alphaRightWall = new Alpha(new Point(14500, 7000), 500, new Vector(100, 5));
		Rect leftWall = new Rect(new Point(-1, 0), new Point(0, br3.getY()));
		Rect topWall = new Rect(new Point(0, -1), new Point(br3.getX(), 0));
		Rect rightWall = new Rect(new Point(br3.getX(), 0), new Point(br3.getX()+1, br3.getY()));
		Rect bottomWall = new Rect(new Point(0, br3.getY()), new Point(br3.getX(), br3.getY() + 1));
		
		Ball tb = alphaBottomWall.createNormalBallFromAlpha();
		tb.moveBall(br3, 50);
		alphaBottomWall.changeAlphaFromBall(tb);
		assertTrue(tb.raaktRechthoek(bottomWall, 3));
		
		Ball tb2 = alphaLeftWall.createNormalBallFromAlpha();
		tb2.moveBall(br3, 5);
		alphaLeftWall.changeAlphaFromBall(tb2);
		assertTrue(tb2.raaktRechthoek(leftWall, 4));
		
		Ball tb3 = alphaTopWall.createNormalBallFromAlpha();
		tb3.moveBall(br3, 5);
		alphaTopWall.changeAlphaFromBall(tb3);
		assertTrue(tb3.raaktRechthoek(topWall, 1));
		
		Ball tb4 = alphaRightWall.createNormalBallFromAlpha();
		tb4.moveBall(br3, 5);
		alphaRightWall.changeAlphaFromBall(tb4);
		assertTrue(tb4.raaktRechthoek(rightWall, 2));
	}
	
}

