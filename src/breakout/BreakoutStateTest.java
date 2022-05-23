package breakout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Ball;
import breakout.utils.Point;
import breakout.utils.Vector;

class BreakoutStateTest {

	private BreakoutFacade facade = new BreakoutFacade();
	
	private Point paddleCenter1 = new Point(5000, 5000);
	private Point center1 = new Point(10000, 10000);
	private Vector Velocity1 = new Vector(10, 10);
	private int diameter1 = 500;
	
	private Point br1 = new Point(50000, 50000);
	
	private Ball ball1 = facade.createNormalBall(center1, diameter1, Velocity1);
	private PaddleState paddle = facade.createNormalPaddleState(paddleCenter1);
	private BlockState block1 = facade.createNormalBlockState(new Point(2000, 2000), new Point(5000, 5000));
	private BlockState[] oneBlock = {block1};
	private Ball[] oneBall = {ball1};
	private BreakoutState state1 = facade.createBreakoutState(oneBall, oneBlock, br1, paddle);
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testPaddleMoveMethod() {
		//Enige methode in BreakoutState die getest moet worden
		PaddleState paddleAfter25Ms = new NormalPaddleState(paddleCenter1.plus(new Vector(500, 0)));
		assertTrue(state1.getPaddle() == paddle);
		state1.movePaddleRight(25);
		assertFalse(state1.getPaddle().equals(paddle));
		assertTrue(state1.getPaddle().equals(paddleAfter25Ms));
		
		state1.movePaddleLeft(25);
		assertFalse(state1.getPaddle().equals(paddleAfter25Ms));
		assertTrue(state1.getPaddle().equals(paddle));
	}
	
	@Test
	void testBreakoutConstructor() {
		assertThrows(IllegalArgumentException.class, () -> new BreakoutState(null, oneBlock, br1, paddle));
		assertThrows(IllegalArgumentException.class, () -> new BreakoutState(oneBall, null, br1, paddle));
		assertThrows(IllegalArgumentException.class, () -> new BreakoutState(oneBall, oneBlock, null, paddle));
		assertThrows(IllegalArgumentException.class, () -> new BreakoutState(oneBall, oneBlock, br1, null));
		
		BlockState blockOutOfField = facade.createNormalBlockState(new Point(100000, 100000), new Point(110000, 110000));
		BlockState[] illegalBlocks = {block1, blockOutOfField};
		assertThrows(IllegalArgumentException.class, () -> new BreakoutState(oneBall, illegalBlocks, br1, paddle));
	}

}