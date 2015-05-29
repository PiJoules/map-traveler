import java.util.ArrayList;

/**
 * This class will traverse the maze on each turn, remembering where he went.
 * At the end of it, once a goal/condition is met, the previousDirections will
 * be the successful path taken from the starting to end point.
 */
public class Traveler {
	private int x,y;

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
		this.x = x;
		this.y = y;
	}

	/**
	 * Move to a specified direction. It is assumed this direction is a legal one.
	 * @param d the direction in which to move
	 */
	public void moveTo(Direction d){
		// Update the traveler's position.
		switch (d){
			case UP:
				this.y--;
				break;
			case RIGHT:
				this.x++;
				break;
			case DOWN:
				this.y++;
				break;
			case LEFT:
				this.x--;
				break;
		}

		// Remember this position in case the traveler is unable to move in the next turn.
		this.previousDirections.add(d);

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
			this.previousDirections.remove(this.previousDirections.size()-1);

			// The list of bad directions should always be at most 1 greater than that
			// of the list of previous directions. If it is greater, cut off the last nested list
			// of bad directions from the parent list, badDirections.
			if (this.badDirections.size() - this.previousDirections.size() > 1){
				this.badDirections.removeLast();
			}

			// Add the last direction made to the list of the latest bad directions.
			this.badDirections.last().add(this.lastDirection);

			// Update the position.
			this.x = old x;
			this.y = old y;
		}
		else{
			// The traveler was either forced back to the starting point and cannot move further
			// or the traveler could not move at all to start with. In this case, the goal is not
			// reachable.
			this.canReachGoal = false;
		}
	}
}