package breakout;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Ball;
import breakout.radioactivity.NormalBall;
import breakout.radioactivity.SuperchargedBall;
import breakout.utils.Point;
import breakout.utils.Vector;

class BlockStateTest {
	
	private Point topLeft1;
	private Point topLeft2;
	private Point bottomRight1;
	private Point bottomRight2;
	
	private BlockState normalBlock1;
	private BlockState normalBlock2;
	private BlockState powerupBlock1;
	private BlockState powerupBlock2;
	private BlockState replicatorBlock1;
	private BlockState replicatorBlock2;
	private BlockState sturdyBlock1;
	private BlockState sturdyBlock2;

	@BeforeEach
	void setUp() throws Exception {
		topLeft1 = new Point(1000, 1000);
		bottomRight1 = new Point(5000, 5000);
		
		topLeft2 = new Point(20000, 15000);
		bottomRight2 = new Point(21000, 15500);
		
		normalBlock1 = new NormalBlockState(topLeft1, bottomRight1);
		normalBlock2 = new NormalBlockState(topLeft2, bottomRight2);
		
		powerupBlock1 = new PowerupBlockState(topLeft1, bottomRight1);
		powerupBlock2 = new PowerupBlockState(topLeft2, bottomRight2);
		
		replicatorBlock1 = new ReplicatorBlockState(topLeft1, bottomRight1);
		replicatorBlock2 = new ReplicatorBlockState(topLeft2, bottomRight2);
		
		sturdyBlock1 = new SturdyBlockState(topLeft1, bottomRight1, 3, Color.RED);
		sturdyBlock2 = new SturdyBlockState(topLeft2, bottomRight2, 1, Color.YELLOW);
	}

	@Test
	void testEquals() {
		
		assertTrue(normalBlock1.equals(normalBlock1));
		assertTrue(normalBlock1.equals(new NormalBlockState(topLeft1, bottomRight1)));
		assertFalse(normalBlock1.equals(normalBlock2));
		assertFalse(normalBlock1.equals(powerupBlock1));
		
		assertTrue(powerupBlock1.equals(powerupBlock1));
		assertTrue(powerupBlock1.equals(new PowerupBlockState(topLeft1, bottomRight1)));
		assertFalse(powerupBlock1.equals(powerupBlock2));
		assertFalse(powerupBlock1.equals(replicatorBlock1));

		assertTrue(replicatorBlock1.equals(replicatorBlock1));
		assertTrue(replicatorBlock1.equals(new ReplicatorBlockState(topLeft1, bottomRight1)));
		assertFalse(replicatorBlock1.equals(replicatorBlock2));
		assertFalse(replicatorBlock1.equals(sturdyBlock1));
		
		assertTrue(sturdyBlock1.equals(sturdyBlock1));
		assertTrue(sturdyBlock1.equals(new SturdyBlockState(topLeft1, bottomRight1, 3, Color.RED)));
		assertFalse(sturdyBlock1.equals(sturdyBlock2));
		assertFalse(sturdyBlock1.equals(normalBlock1));
	}
	
	@Test
	void testGetsDestroyed() {
		assertTrue(normalBlock1.getsDestroyedOnCollision());
		assertTrue(powerupBlock1.getsDestroyedOnCollision());
		assertTrue(replicatorBlock1.getsDestroyedOnCollision());
		assertFalse(sturdyBlock1.getsDestroyedOnCollision());
		assertTrue(sturdyBlock2.getsDestroyedOnCollision());
	}
	
	@Test
	void testSpecialBlockHandlerBall() {
		Ball normalBall = new NormalBall(new Point(1000, 1000), 500, new Vector(10, 10));
		Ball superchargedBall1 = new SuperchargedBall(new Point(1000, 1000), 500, new Vector(10, 10), 0);
		Ball superchargedBall2 = new SuperchargedBall(new Point(1000, 1000), 500, new Vector(10, 10), 5000);
		
		assertTrue(normalBlock1.specialBlockHandler(normalBall) == normalBall);
		assertTrue(normalBlock2.specialBlockHandler(superchargedBall1) == superchargedBall1);
		assertTrue(sturdyBlock1.specialBlockHandler(superchargedBall2) == superchargedBall2);
		assertTrue(sturdyBlock2.specialBlockHandler(superchargedBall1) == superchargedBall1);
		assertTrue(replicatorBlock1.specialBlockHandler(normalBall) == normalBall);
		assertTrue(replicatorBlock2.specialBlockHandler(superchargedBall2) == superchargedBall2);
		
		assertTrue(powerupBlock1.specialBlockHandler(normalBall).equalContent(superchargedBall1));
		assertTrue(powerupBlock1.specialBlockHandler(superchargedBall1).equalContent(superchargedBall1));
		assertTrue(powerupBlock2.specialBlockHandler(superchargedBall2).equalContent(superchargedBall1));
	}
	
	@Test
	void specialBlockHandlerPaddle() {
		PaddleState normalPaddle = new NormalPaddleState(new Point(5000, 5000));
		PaddleState replicatorPaddle1 = new ReplicatingPaddleState(new Point(5000, 5000), 3);
		PaddleState replicatorPaddle2 = new ReplicatingPaddleState(new Point(5000, 5000), 1);
		
		assertTrue(normalBlock1.specialBlockHandler(normalPaddle) == normalPaddle);
		assertTrue(normalBlock2.specialBlockHandler(replicatorPaddle1) == replicatorPaddle1);
		assertTrue(sturdyBlock1.specialBlockHandler(replicatorPaddle2) == replicatorPaddle2);
		assertTrue(sturdyBlock2.specialBlockHandler(normalPaddle) == normalPaddle);
		assertTrue(powerupBlock1.specialBlockHandler(replicatorPaddle1) == replicatorPaddle1);
		assertTrue(powerupBlock2.specialBlockHandler(replicatorPaddle2) == replicatorPaddle2);
		
		assertTrue(replicatorBlock1.specialBlockHandler(normalPaddle).equals(replicatorPaddle1));
		assertTrue(replicatorBlock1.specialBlockHandler(replicatorPaddle1).equals(replicatorPaddle1));
		assertTrue(replicatorBlock2.specialBlockHandler(replicatorPaddle2).equals(replicatorPaddle1));
	}
	
	@Test 
	void specialBlockHandlerBlock() {
		assertTrue(normalBlock1.specialBlockHandler() == null);
		assertTrue(normalBlock2.specialBlockHandler() == null);
		assertTrue(powerupBlock1.specialBlockHandler() == null);
		assertTrue(replicatorBlock2.specialBlockHandler() == null);
		
		assertTrue(sturdyBlock1.specialBlockHandler().equals(new SturdyBlockState(topLeft1, bottomRight1, 2, Color.ORANGE)));
		assertTrue(sturdyBlock2.specialBlockHandler() == null);
	}

}