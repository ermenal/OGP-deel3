package breakout.utils;

import java.util.Objects;

/**
 * An object of this class represents a rectangle on a 2D-grid
 * 
 * @immutable
 * 
 * @invar The bottom right point is not {@code null}
 *    | getBottomRight() != null
 * @invar The top left point is not {@code null}
 *    | getTopLeft() != null
 *    
 * @invar The bottom right point of the rectangle is not positioned above or to the left of the top left point
 *    | getBottomRight().getX() >= getTopLeft().getX() &&
 *    | getBottomRight().getY() >= getTopLeft().getY()
 */

public class Rect {

	/**
	 * @invar {@code topLeft} is not {@code null}
	 *    | topLeft != null
	 * @invar {@code bottomRight} is not {@code null} 
	 *    | bottomRight != null
	 *
	 * @invar The {@code bottomRight} point is not positioned above or to the left of the {@code topLeft} point
	 *     | bottomRight.getX() >= topLeft.getX() && 
	 *	   | bottomRight.getY() >= topLeft.getY()
	 */
	
	private final Point topLeft;
	private final Point bottomRight;
	
	public static final Vector[] COLLISSION_DIRS = new Vector[] {
			Vector.UP, Vector.DOWN, Vector.LEFT, Vector.RIGHT
			}; 

	/**
	 * Initializes the object with the given topLeft and bottomRight points 
	 * 
	 * @pre Parameter {@code topLeft} is not {@code null}
	 *     | topLeft != null
	 * @pre Parameter {@code bottomRight} is not {@code null}
	 *     | bottomRight != null
	 *     
	 * @pre {@code bottomRight} is not positioned above or to the left of {@code topLeft}
	 *     | bottomRight.getX() >= topLeft.getX() && 
	 *     | bottomRight.getY() >= topLeft.getY()
	 *     
	 * @post | getBottomRight() == bottomRight
	 * @post | getTopLeft() == topLeft
	 */
	
	public Rect(Point topLeft, Point bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	/** Return the top-left point of this rectangle */
	public Point getTopLeft() {
		return topLeft;
	}

	/** Return the bottom-right point of this rectangle */
	public Point getBottomRight() {
		return bottomRight;
	}

	/**
	 * Return whether given point `loc` is inside this rectangle.
	 * 
	 * @pre | loc != null
	 * @post | result == (getTopLeft().isUpAndLeftFrom(loc) && loc.isUpAndLeftFrom(getBottomRight()))
	 */
	public boolean contains(Point loc) {
		return getTopLeft().isUpAndLeftFrom(loc) && loc.isUpAndLeftFrom(getBottomRight());
	}

	/**
	 * Return whether this rectangle contains a given circle.
	 * 
	 * @post | result == (getTopLeft().plus(new Vector(loc.getDiameter(),loc.getDiameter())).isUpAndLeftFrom(getBottomRight()) &&
	 * 		 |				minusMargin(loc.getRadius()).contains(loc.getCenter()))
	 */
	public boolean contains(Circle loc) {
		return getTopLeft().plus(new Vector(loc.getDiameter(),loc.getDiameter())).isUpAndLeftFrom(getBottomRight()) &&
				minusMargin(loc.getRadius()).contains(loc.getCenter());
	}

	/**
	 * Return whether this rectangle contains a given other rectangle.
	 * 
	 * @post | result == (getTopLeft().isUpAndLeftFrom(other.getTopLeft()) && 
	 *       |            other.getBottomRight().isUpAndLeftFrom(getBottomRight()))
	 */
	public boolean contains(Rect other) {
		return getTopLeft().isUpAndLeftFrom(other.getTopLeft()) && 
				other.getBottomRight().isUpAndLeftFrom(getBottomRight());
	}

	/**
	 * Check whether this rectangle intersects with the given ball and if so, return the direction from the ball to the rectangle.
	 * This direction may be an approximation for simplicity.
	 * 
	 * @pre | ball != null
	 * @post | result == null || (result.getSquareLength() == 1 && this.contains(ball.getOutermostPoint(result)))
	 */
	public Vector collideWith(Circle ball) {
		for (Vector coldir : COLLISSION_DIRS) {
			Point c = ball.getOutermostPoint(coldir);
			if(contains(c)) {
				return coldir;
			}
		}
		return null;
	}

	/**
	 * Return the rectangle obtained by subtracting an inner margin from all sides of this rectangle.
	 * 
	 * @pre getTopLeft().plus(new Vector(2*dx,2*dy)).isUpAndLeftFrom(getBottomRight())
	 * @post | result != null
	 * @post | result.getTopLeft().equals(getTopLeft().plus(new Vector(dx,dy)))
	 * @post | result.getBottomRight().equals(getBottomRight().minus(new Vector(dx,dy)))
	 */
	public Rect minusMargin(int dx, int dy) {
		Vector dv = new Vector(dx, dy);
		return new Rect( topLeft.plus(dv),
						 bottomRight.minus(dv));
	}
	
	/**
	 * Return the rectangle obtained by subtracting an inner margin from all sides of this rectangle.
	 * 
	 * @pre getTopLeft().plus(new Vector(2*d,2*d)).isUpAndLeftFrom(getBottomRight())
	 * @post | result != null
	 * @post | result.getTopLeft().equals(getTopLeft().plus(new Vector(d,d)))
	 * @post | result.getBottomRight().equals(getBottomRight().minus(new Vector(d,d)))
	 */
	public Rect minusMargin(int d) {
		Vector dv = new Vector(d,d);
		return new Rect( topLeft.plus(dv),
						 bottomRight.minus(dv));
	}

	/**
	 * Return the point inside this rectangle that is as close as possible to a given point p.
     * 
	 * @pre | p != null
	 * @post | result.getX() == Math.min(getBottomRight().getX(), Math.max(getTopLeft().getX(), p.getX()))
	 * @post | result.getY() == Math.min(getBottomRight().getY(), Math.max(getTopLeft().getY(), p.getY()))
	 */
	public Point constrain(Point p) {
		int nx = Math.min(getBottomRight().getX(), Math.max(getTopLeft().getX(), p.getX()));
		int ny = Math.min(getBottomRight().getY(), Math.max(getTopLeft().getY(), p.getY()));
		return new Point(nx, ny);
	}

	/**
	 * Return the width of this rectangle.
	 * 
	 * post | getBottomRight().getX() - getTopLeft().getX()
	 */
	public int getWidth() {
		return bottomRight.getX() - topLeft.getX();
	}
	
	/**
	 * Return the height of this rectangle.
	 * 
	 * @post | result == (getBottomRight().getY() - getTopLeft().getY())
	 */
	public int getHeight() {
		return bottomRight.getY() - topLeft.getY();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(bottomRight, topLeft);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rect other = (Rect) obj;
		return Objects.equals(bottomRight, other.bottomRight) && Objects.equals(topLeft, other.topLeft);
	}

	/**
	 * Move the argument circle by the least amount so that it falls entirely within this rect.
	 * 
	 * @pre | c.getDiameter() < getWidth()
	 * @post | contains(result)
	 * @post | result.getCenter().equals(this.minusMargin(c.getRadius()).constrain(c.getCenter()))
	 */
	public Circle constrain(Circle c) {
		Rect r = this.minusMargin(c.getRadius());
		Point nc = r.constrain(c.getCenter());
		return new Circle(nc,c.getDiameter());
	}
}