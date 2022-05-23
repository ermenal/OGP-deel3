package breakout.radioactivity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.BlockState;
import breakout.BreakoutFacade;
import breakout.BreakoutState;
import breakout.PaddleState;
import breakout.utils.Point;
import breakout.utils.Vector;

class BallAlphaLinkTest {

	private Point center1;
	private Point center2;
	private Point center3;
	
	private int diameter;
	
	private Vector velocity1;
	private Vector velocity2;
	private Vector velocity3;
	
	private Point br;
	
	private Alpha alpha1;
	private Alpha alpha2;
	private Alpha alpha3;
	
	private Ball normalBall1;
	private Ball normalBall2;
	private Ball normalBall3;
	
	private Ball superchargedBall1;
	private Ball superchargedBall2;
	private Ball superchargedBall3;
	
	private Ball[] balls1;
	private Ball[] balls2;
	
	private Alpha[] alphas1;
	private Alpha[] alphas2;
	
	private BlockState normalBlock;
	private BlockState sturdyBlock;
	private BlockState powerupBlock;
	private BlockState replicatorBlock;
	
	private Point tl1;
	private Point tl2;
	private Point tl3;
	private Point tl4;
	
	private Point br1;
	private Point br2;
	private Point br3;
	private Point br4;
	
	private BlockState[] blocks;
	
	private PaddleState paddle;
	
	private static final int MAX_SUPERCHARGED_TIME = 10000;
	
	private BreakoutFacade fac;
	
	private BreakoutState state1;
	
	
	@BeforeEach
	void setUp() throws Exception {
		fac = new BreakoutFacade();
		
		center1 = new Point(5000, 5000);
		center2 = new Point(1000, 1000);
		center3 = new Point(15000, 15000);
		
		diameter = 700;
		
		velocity1 = new Vector(-20, 20);
		velocity2 = new Vector(15, 15);
		velocity3 = new Vector(10, -10);
		
		br = new Point(100000, 100000);
		
		alpha1 = fac.createAlpha(center1, diameter, velocity1);
		alpha2 = fac.createAlpha(center2, diameter, velocity2);
		alpha3 = fac.createAlpha(center3, diameter, velocity3);
		
		normalBall1 = fac.createNormalBall(center1, diameter, velocity1);
		normalBall2 = fac.createNormalBall(center2, diameter, velocity2);
		normalBall3 = fac.createNormalBall(center3, diameter, velocity3);
		
		superchargedBall1 = fac.createSuperchargedBall(center1, diameter, velocity1, 0);
		superchargedBall2 = fac.createSuperchargedBall(center2, diameter, velocity2, 5000);
		superchargedBall3 = fac.createSuperchargedBall(center3, diameter, velocity3, MAX_SUPERCHARGED_TIME);
		
		alphas1 = Stream.of(alpha1).toArray(Alpha[]::new);
		alphas2 = Stream.of(alpha2, alpha3).toArray(Alpha[]::new);
		
		balls1 = Stream.of(normalBall1).toArray(Ball[]::new);
		balls2 = Stream.of(normalBall2, normalBall3, superchargedBall1, superchargedBall2, superchargedBall3).toArray(Ball[]::new);
		
		tl1 = new Point(3000, 3000);
		br1 = new Point(4200, 6000);
		
		tl2 = new Point(2000, 500);
		br2 = new Point(5000, 2500);
		
		tl3 = new Point(16000, 12000);
		br3 = new Point(20000, 18000);
		
		tl4 = new Point(50000, 50000);
		br4 = new Point(60000, 60000);
		
		normalBlock = fac.createNormalBlockState(tl1, br1);
		sturdyBlock = fac.createSturdyBlockState(tl2, br2, 3);
		powerupBlock = fac.createPowerupBallBlockState(tl3, br3);
		replicatorBlock = fac.createReplicatorBlockState(tl4, br4);
		
		blocks = Stream.of(normalBlock, sturdyBlock, powerupBlock, replicatorBlock).toArray(BlockState[]::new);
		
		paddle = fac.createNormalPaddleState(new Point(30000, 30000));
		
		state1 = fac.createBreakoutState(balls1, blocks, br, paddle);
	}

	@Test
	void testBreakoutStateThrows() {
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(alphas1, balls1, blocks, new Point(10000, 10000), paddle));
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(alphas1, balls1, blocks, null, paddle));
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(alphas1, balls1, blocks, br, null));
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(alphas1, balls1, null, br, paddle));
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(alphas1, null, blocks, br, paddle));
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(null, balls1, blocks, br, paddle));
		
		try {fac.createBreakoutState(alphas1, balls1, blocks, br, paddle);} catch (Exception e) {fail();}
		
		alpha1.addBall(normalBall1);
		
		assertThrows(IllegalArgumentException.class, () -> fac.createBreakoutState(alphas1, balls1, blocks, br, paddle));
		
		normalBall1.linkTo(alpha1);
		
		try {fac.createBreakoutState(alphas1, balls1, blocks, br, paddle);} catch (Exception e) {fail();}
	}
	
	@Test
	void testBallAlphaLinks() {
		assertTrue(normalBall1.getAlphas().size() == 0);
		assertTrue(alpha1.getBalls().size() == 0);
		
		normalBall1.linkTo(alpha1);
		
		assertTrue(normalBall1.getAlphas().size() == 1);
		assertTrue(alpha1.getBalls().size() == 1);
		
		Alpha alpha1Clone = alpha1.clone();
		
		assertTrue(alpha1.getBalls().size() == 1);
		assertTrue(alpha1Clone.getBalls().size() == 1);
		
		assertTrue(normalBall1.getAlphas().size() == 2);
		assertTrue(normalBall1.getAlphas().contains(alpha1Clone) && normalBall1.getAlphas().contains(alpha1));
		
		normalBall1.unLink(alpha1Clone);
		assertTrue(normalBall1.getAlphas().size() == 1);
		assertFalse(normalBall1.getAlphas().contains(alpha1Clone));
		assertTrue(alpha1.getBalls().size() == 1);
		assertTrue(alpha1Clone.getBalls().size() == 0);
	}
	
	@Test
	void testBreakoutStateTick() {
		Ball[] balls = Stream.concat(Arrays.stream(balls1), Arrays.stream(balls2)).toArray(Ball[]::new);
		Alpha[] alphas = Stream.concat(Arrays.stream(alphas1), Arrays.stream(alphas2)).toArray(Alpha[]::new);
		
		BreakoutState state2 = fac.createBreakoutState(alphas, balls, blocks, br, paddle);
		
		assertTrue(state2.getBlocks().length == 4);
		assertTrue(state2.getAlphas().length == 3);
		assertTrue(state2.getBalls().length == 6);
		
		for (int i=0; i<100; i++) {
			state2.tick(1, 1);
		}
		
		assertTrue(state2.getBlocks().length == 2);
		assertTrue(state2.getAlphas().length == 3);
		assertTrue(state2.getBalls().length == 6);
		
		assertTrue(state1.getBlocks().length == 4);
		for (int i=0; i<100; i++) {
			state1.tick(1, 1);
		}
		
		assertTrue(state1.getBlocks().length == 3);
	}

}
