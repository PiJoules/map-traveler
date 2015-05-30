import java.util.ArrayList;
import java.awt.Point;

/**
 * This class will traverse the maze on each turn, remembering where he went.
 * At the end of it, once a goal/condition is met, the previousDirections will
 * be the successful path taken from the starting to end point.
 */
public class Traveler {
	private Point pos;

	/**
	 * Keep track of the places you have been and do not go there.
	 */
	private ArrayList<Point> previousPositions = new ArrayList<>();

	/**
	 * A list of (bad) directions the traveler made in the previous turns.
	 * If the traveler must retrace his steps, after moving back, he should ignore these directions.
	 * Each nested array is the bad directions the traveler took that turn. These are ignored.
	 * The lenght of badDirections is always greater than or equal to that of the previousDirections.
	 */
	private ArrayList<ArrayList<Direction>> badDirections = new ArrayList<>();

	/**
	 * An array of all the previous directions the traveler took.
	 * When the traveler moves on a new spot, the direction is added onto this,
	 * but if it turns out the spot is bad, then he goes back to the previous spot
	 * and the last element in this is removed and added to the list of bad directions
	 * at that move.
	 */
	private ArrayList<Direction> previousDirections = new ArrayList<>();

	/**
	 * An variable that must always be checked at each iteration to make sure the maze is
	 * traversable to reach a goal.
	 */
	private boolean reachableGoal = true;

	public Traveler(int x, int y){
		this.pos = new Point(x,y);
	}

	/**
	 * Move to a specified direction. It is assumed this direction is a legal one.
	 * @param d the direction in which to move
	 */
	public void moveTo(Direction d){
		// Update the traveler's position.
		int x = (int)this.pos.getX(), y = (int)this.pos.getY();
		switch (d){
			case UP:
				this.pos.move(x, y-1);
				break;
			case RIGHT:
				this.pos.move(x+1, y);
				break;
			case DOWN:
				this.pos.move(x, y+1);
				break;
			case LEFT:
				this.pos.move(x-1, y);
				break;
		}

		// Remember this position in case the traveler is unable to move in the next turn.
		this.previousDirections.add(d);
		this.previousPositions.add(new Point(x,y));

		// If the traveler just moved to this square, he does not know of any bad directions
		// and makes a new (empty) list of bad directions.
		// If he moves, then moves back to this square, do not make a new list since he already
		// has one for recording bad squares.
		if (badDirections.size() < previousDirections.size()){
			badDirections.add(new ArrayList<Direction>());
		}
	}

	/**
	 * Move back because cannot make a legal move anymore from the current pos.
	 */
	public void moveBack(){
		if (previousDirections.size() > 0){
			// Remove the last direction from the list of previous directions.
			// After this point, the list of bad directions will be greater than 
			// the list of previous directions (by either 1 or 2, though if it is
			// 2, that means the traveler has just moved back and will be moving back
			// once more.)
			Direction removedDirection = this.previousDirections.remove(this.previousDirections.size()-1);

			// The list of bad directions should always be at most 1 greater than that
			// of the list of previous directions. If it is greater, cut off the last nested list
			// of bad directions from the parent list, badDirections.
			if (this.badDirections.size() - this.previousDirections.size() > 1){
				this.badDirections.remove(this.badDirections.size()-1);
			}

			// Add the last direction made to the list of the latest bad directions.
			this.badDirections.get(this.badDirections.size()-1).add(removedDirection);

			// Update the position and delete the last pos.
			this.pos.setLocation( this.previousPositions.remove(this.previousPositions.size()-1) );
		}
		else{
			// The traveler was either forced back to the starting point and cannot move further
			// or the traveler could not move at all to start with. In this case, the goal is not
			// reachable.
			this.reachableGoal = false;
		}
	}

	/**
	 * Return all possible directions the traveler can move. This excludes
	 * all the ones in badDirections and the latest previousDirection. Also
	 * exclude a direction if it leads the traveler to a square he has previously
	 * been on.
	 * 
	 * This is checked BEFORE the traveler makes his move.
	 * @param mapWidth  Traveler cannot go past the map width.
	 * @param mapHeight Traveler cannot go past the map height.
	 * @param mapConditions Any optional conditions set by the map regarding certain directions.
	 *                      [up condition, right condition, down condition, left condition]
	 * @return all that
	 */
	public Direction[] getPossibleDirections(int mapWidth, int mapHeight, boolean... mapConditions){
		ArrayList<Direction> possibleDirections = new ArrayList<>();
		int x = (int)this.pos.getX(), y = (int)this.pos.getY();
		ArrayList<Direction> latestBadDirections = ( this.badDirections.size() > 0 ? this.badDirections.get(this.badDirections.size()-1) : new ArrayList<Direction>() );
		Direction latestDirection = ( this.previousPositions.size() > 0 ? this.previousDirections.get(this.previousDirections.size()-1) : null );

		boolean upCond = (mapConditions.length > 0 ? mapConditions[0] : true);
		boolean rightCond = (mapConditions.length > 1 ? mapConditions[1] : true);
		boolean downCond = (mapConditions.length > 2 ? mapConditions[2] : true);
		boolean leftCond = (mapConditions.length > 3 ? mapConditions[3] : true);

		// UP
		if (y > 0 &&
			!latestBadDirections.contains(Direction.UP) &&
			latestDirection != getOppositeDirection(Direction.UP) &&
			!this.wasOnPoint(x, y-1) &&
			upCond){
			possibleDirections.add(Direction.UP);
		}

		// DOWN
		if (y < mapHeight-1 &&
			!latestBadDirections.contains(Direction.DOWN) &&
			latestDirection != getOppositeDirection(Direction.DOWN) &&
			!this.wasOnPoint(x, y+1) &&
			downCond){
			possibleDirections.add(Direction.DOWN);
		}

		// LEFT
		if (x > 0 &&
			!latestBadDirections.contains(Direction.LEFT) &&
			latestDirection != getOppositeDirection(Direction.LEFT) &&
			!this.wasOnPoint(x-1, y) &&
			leftCond){
			possibleDirections.add(Direction.LEFT);
		}

		// RIGHT
		if (x < mapWidth-1 &&
			!latestBadDirections.contains(Direction.RIGHT) &&
			latestDirection != getOppositeDirection(Direction.RIGHT) &&
			!this.wasOnPoint(x+1, y) &&
			rightCond){
			possibleDirections.add(Direction.RIGHT);
		}

		return possibleDirections.toArray(new Direction[possibleDirections.size()]);
	}

	/**
	 * Check to see if the destination was already reached
	 * @param  x coord
	 * @param  y coord
	 * @return   did you go on it?
	 */
	private boolean wasOnPoint(int x, int y){
		return previousPositions.contains(new Point(x, y));
	}

	/**
	 * Returns the opposite direction of a given one
	 * @param  d ank
	 * @return   memes yo
	 */
	private static Direction getOppositeDirection(Direction d){
		switch (d){
			case UP: return Direction.DOWN;
			case RIGHT: return Direction.LEFT;
			case DOWN: return Direction.UP;
			case LEFT: return Direction.RIGHT;
		}
		return null; // Shouldn't be able to reach this
	}

	/**
	 * Check if the traveler can even make it to his goal.
	 * @return reachableGoal
	 */
	public boolean canReachGoal(){
		return this.reachableGoal;
	}

	/**
	 * Return the current position
	 */
	public Point getPos(){
		return this.pos;
	}

	/**
	 * After traversing the map, get the path of the traveler.
	 * @return previousDirections
	 */
	public Direction[] getPath(){
		return this.previousDirections.toArray(new Direction[this.previousDirections.size()]);
	}

	/**
	 * Same as the last but getting a list of coordinates
	 * @return previousPositions
	 */
	public Point[] getFootprints(){
		return this.previousPositions.toArray(new Point[this.previousPositions.size()]);
	}
}