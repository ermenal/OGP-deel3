package breakout;


import java.awt.Color;
import java.util.Set;

import radioactivity.Alpha;
import radioactivity.Ball;
import utils.Point;
import utils.Rect;
import utils.Vector;

//No documentation required for this class
public class BreakoutFacade {
	
	public PaddleState createNormalPaddleState(Point center) {
		return null;
	}
	
	public PaddleState createReplicatingPaddleState(Point p, int count) {
		return null;
	}

	/**
	 * newly created balls / alphas have an empty peer set.
	 */
	public Ball createNormalBall(Point center, int diameter, Vector initBallVelocity) {
		return null;
	}

	public Ball createSuperchargedBall(Point center, int diameter, Vector initBallVelocity, int lifetime) {
		return null;
	}
	
	public Alpha createAlpha(Point center, int diameter, Vector speed) {
		return null;
	}

	/**
	 * pre: balls have no peer alphas.
	 */
	public BreakoutState createBreakoutState(Ball[] balls, BlockState[] blocks, Point bottomRight,
			PaddleState paddle) {
		return null;
	}
	
	/**
	 * Here balls and alphas are allowed to have peers. (a defensive check of exhaustiveness
	 * must be performed
	 */
	public BreakoutState createBreakoutState(
			Alpha[] alphas,
			Ball[] balls,
			BlockState[] blocks,
			Point bottomRight,
			PaddleState paddle) {
		return null;
	}

	public BlockState createNormalBlockState(Point topLeft, Point bottomRight) {
		return null;
	}

	public BlockState createSturdyBlockState(Point topLeft, Point bottomRight, int i) {
		return null;
	}

	public BlockState createReplicatorBlockState(Point topLeft, Point bottomRight) {
		return null;
	}

	public BlockState createPowerupBallBlockState(Point topLeft, Point bottomRight) {
		return null;
	}
	
	public Color getColor(PaddleState paddle) {
		return null;
	}

	public Color getColor(Ball ball) {
		return null;
	}
	
	public Color getColor(Alpha alpha) {
		return null;
	}

	public Rect getLocation(PaddleState paddle) {
		return null;
	}

	public Point getCenter(Ball ball) {
		return null;
	}
	
	public Point getCenter(Alpha alpha) {
		return null;
	}
	

	public int getDiameter(Ball ball) {
		return 0; //TODO
	}
	
	public int getDiameter(Alpha alpha) {
		return 0; //TODO
	}

	public Ball[] getBalls(BreakoutState breakoutState) {
		return null;
	}
	
	public Alpha[] getAlphas(BreakoutState breakoutState) {
		return null;
	}

	public Color getColor(BlockState block) {
		return null;
	}

	public Rect getLocation(BlockState block) {
		return null;
	}
	
	/**
	 * Returns the peer balls of alpha.
	 * (Clients of the Alpha - Ball abstraction are allowed to have access to
	 * the peer references.)
	 */
	public Set<Ball> getBalls(Alpha alpha) {
		return null;
	}
	
	public Set<Alpha> getAlphas(Ball ball) {
		return null;
	}
	
	/**
	 * note: re-adding a link does nothing
	 */
	public void addLink(Ball ball, Alpha alpha) {

	}
	
	/**
	 * note: re-removing a link does nothing.
	 */
	public void removeLink(Ball ball, Alpha alpha) {

	}
	
	/**
	 * should be in constant time (forwarding private charge)
	 */
	public int getEcharge(Ball ball) {
		return 0; //TODO
	}
	
	/**
	 * mutates the position and diam of ball
	 */
	public void setLocation(Ball ball, Point center, int diam) {

	}
	
	public void setLocation(Alpha alpha, Point center, int diam) {

	}

	/**
	 * mutates the velocity of ball
	 */
	public void setSpeed(Ball ball, Vector speed) {

	}
	
	public void setSpeed(Alpha alpha, Vector speed) {

	}
	

	public Vector getVelocity(Ball ball) {
		return null;
	}
	
	public Vector getVelocity(Alpha alpha) {
		return null;
	}
	
	public void hitBlock(Ball ball, Rect rect, boolean destroyed) {

	}
	

	
	public BlockState[] getBlocks(BreakoutState state) {
		return null;
	}
	
	public Point getBottomRight(BreakoutState state) {
		return null;
	}
	
	public PaddleState getPaddle(BreakoutState state) {
		return null;
	}
	
	public void tick(BreakoutState state, int paddleDir, int elapsedTime) {

	}
	
	public void tickDuring(BreakoutState state, int elapsedTime) {
		for (int i = 0 ; i + 20 <= elapsedTime ; i += 20) {
			tick(state, 0, 20);
		}
		if( elapsedTime % 20 != 0) { 
		  tick(state, 0, elapsedTime % 20);
		}
	}
	
	public boolean isWon(BreakoutState state) {
		return true; //TODO
	}
	
	public boolean isDead(BreakoutState state) {
		return true; //TODO
	}
	
	//for GameMap
	//createStateFromDescription
	public BreakoutState createStateFromDescription(String string) {
		return GameMap.createStateFromDescription(string);
	}
	
	
	public int getBallsLen(BreakoutState state) {
		return getBalls(state).length;
	}
	
	public int getBlocksLen(BreakoutState state) {
		return getBlocks(state).length;
	}
	
	/**
	 * @pre | getBalls(state) != null
	 * @pre | getBalls(state).length >= 1
	 */
	public Vector getBall0Vel(BreakoutState state) {
		return getVelocity( getBalls(state)[0] );
	}
	
	// for Rect
	public Point getRectTL(Rect rect) {
		return rect.getTopLeft();
	}
	
	public Point getRectBR(Rect rect) {
		return rect.getBottomRight();
	}
	
	// for blocks
	public Point getBlockTL(BlockState block) {
		return  getRectTL( getLocation(block) );
	}
	
	public Point getBlockBR(BlockState block) {
		return  getRectBR( getLocation(block) );
	}
	
	public void movePaddleRight(BreakoutState state, int elapsedTime) {

	}
	
	public void movePaddleLeft(BreakoutState state, int elapsedTime) {

	}
	
	public boolean collidesWith(Ball ball, Rect rect) {
		return true; //TODO
	}
	
	public boolean collidesWith(Alpha alpha, Rect rect) {
		return true; //TODO
	}
	
	
	
	
}
