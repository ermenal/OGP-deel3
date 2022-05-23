package breakout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Ball;
import breakout.radioactivity.NormalBall;
import breakout.utils.Point;
import breakout.utils.Vector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

class PaddleStateTest {

	private Point center1 = new Point(1000, 1000);
	
	private Point ballCenter1 = new Point(2000, 1500);
	private Vector ballVelocity1 = new Vector(5, 5);
	private int ballDiameter1 = 5;
	
	private Point ballCenter2 = new Point(30000, 30000);
	private Vector ballVelocity2 = new Vector(2, -10);
	private int ballDiameter2 = 5;
	
	private Point ballCenter3 = new Point(10000, 15000);
	private Vector ballVelocity3 = new Vector(-13, 1);
	private int ballDiameter3 = 5;
	
	private PaddleState paddle1;
	private PaddleState paddle2;
	private Ball ball1;
	private Ball ball2;
	private Ball ball3;
	
	
	
	@BeforeEach
	void setUp() {
		paddle1 = new NormalPaddleState(center1);
		paddle2 = new ReplicatingPaddleState(center1, 2);
		ball1 = new NormalBall(ballCenter1, ballDiameter1, ballVelocity1);
		ball2 = new NormalBall(ballCenter2, ballDiameter2, ballVelocity2);
		ball3 = new NormalBall(ballCenter3, ballDiameter3, ballVelocity3);
		
		
	}
	
	@Test
	void testPaddleEquals() {
		
		assertEquals(paddle1.getTopLeft(), new Point(-500,750));
		assertEquals(paddle2.getTopLeft(), new Point(-500,750));
		assertTrue(paddle1.getTopLeft().equals(paddle2.getTopLeft()));
		
		assertEquals(paddle1.getBottomRight(), new Point(2500,1250));
		assertEquals(paddle2.getBottomRight(), new Point(2500,1250));
		assertTrue(paddle1.getBottomRight().equals(paddle2.getBottomRight()));
		
		assertEquals(paddle1.getCenter(), new Point(1000,1000));
		assertEquals(paddle2.getCenter(), new Point(1000,1000));
		assertTrue(paddle1.getCenter().equals(paddle2.getCenter()));
		
		assertEquals(paddle1.getSize(), paddle2.getSize());
		
		assertEquals(paddle1.getColor(), Color.CYAN);
		assertEquals(paddle2.getColor(), Color.PINK);
	}
	
	@Test
	void testGetAmountOfReplications() {
		
		assertEquals(paddle1.getAmountOfReplications(), 0);
		assertEquals(paddle2.getAmountOfReplications(), 2);
	}
	
	@Test 
	void testGetAddedVelocities() {
		
		assertEquals(paddle1.getAddedVelocities()[0], new Vector(2, -2));
		assertEquals(paddle1.getAddedVelocities()[1], new Vector(2, 2));
		assertEquals(paddle1.getAddedVelocities()[2], new Vector(-2, 2));
		
		assertEquals(paddle2.getAddedVelocities()[0], new Vector(2, -2));
		assertEquals(paddle2.getAddedVelocities()[1], new Vector(2, 2));
		assertEquals(paddle2.getAddedVelocities()[2], new Vector(-2, 2));
	}
	
	@Test
	void testBallHitPaddle() {
		Ball[] balls = {ball1, ball2, ball3};
		
		assertTrue(paddle1.equals(new NormalPaddleState(center1)));
		assertFalse(paddle1.equals(paddle2));
		
		assertTrue(paddle2.equals(new ReplicatingPaddleState(center1, 2)));
		assertFalse(paddle2.equals(paddle1));
		
		assertEquals(3, paddle1.hitPaddleReplicationHandler(balls, ball1).length);
	}

}