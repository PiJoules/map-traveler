import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Point;

public class TestMaze {
	private static final char TRAVELER = 'x';
	private static final char OBSTACLE = 'O';
	private static final char GROUND = '.';
	private static final char GOAL = '$';

	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		ArrayList<String> lines = new ArrayList<>();
		while (scan.hasNextLine()){
			lines.add(scan.nextLine());
		}
		char[][] map = new char[lines.size()][lines.get(0).length()];
		int initX = 0, initY = 0;
		for (int y = 0; y < map.length; y++){
			for (int x = 0; x < map[0].length; x++){
				char c = lines.get(y).charAt(x);
				map[y][x] = c;
				if (c == TRAVELER){
					initX = x;
					initY = y;
				}
			}
		}

		Traveler t = new Traveler(initX, initY);
		while (!travelerDidReachGoal(t, map) && t.canReachGoal()){ // each iteration represents 1 turn
			int x = (int)t.getPos().getX();
			int y = (int)t.getPos().getY();

			// Check for a certain condition to see if the traveler can move.
			// Let the example position be if the traveler can move another space
			boolean upCond = false, rightCond = false, downCond = false, leftCond = true;
			if (y > 0){
				if (map[y-1][x] != OBSTACLE){
					upCond = true;
				}
			}
			if (y < map.length-1){
				if (map[y+1][x] != OBSTACLE){
					downCond = true;
				}
			}
			if (x > 0){
				if (map[y][x-1] != OBSTACLE){
					leftCond = true;
				}
			}
			if (x < map[0].length-1){
				if (map[y][x+1] != OBSTACLE){
					rightCond = true;
				}
			}
			Direction[] possibleDirections = t.getPossibleDirections(map[0].length, map.length, upCond, rightCond, downCond, leftCond); // all possible directions the traveler can move

			// Some condition is met and the traveler can continue move.
			// In this case, the condition is if the traveler can move in a direction.
			if (possibleDirections.length > 0){
				t.moveTo(possibleDirections[0]); // from a list of possible directions
			}
			else {
				// The traveler cannot move and must retrace his steps.
				// He will need to move back and remember not to move in the previous direction he made.
				t.moveBack();
			}
		}

		int testX = initX, testY = initY;
		if (travelerDidReachGoal(t, map)){
			Direction[] path = t.getPath();
			for (Direction d : path){
				System.out.println(d);
				switch (d){
					case UP:
						printMap(new Point(initX, initY), new Point(testX, --testY), map);
						break;
					case RIGHT:
						printMap(new Point(initX, initY), new Point(++testX, testY), map);
						break;
					case DOWN:
						printMap(new Point(initX, initY), new Point(testX, ++testY), map);
						break;
					case LEFT:
						printMap(new Point(initX, initY), new Point(--testX, testY), map);
						break;
				}
			}
		}
		else {
			System.out.println("Could not find a path");
		}
	}

	private static boolean travelerDidReachGoal(Traveler t, char[][] map){
		int x = (int)t.getPos().getX();
		int y = (int)t.getPos().getY();
		return map[y][x] == GOAL;
	}

	private static void printMap(Point oldPos, Point newPos, char[][] map){
		for (int y = 0; y < map.length; y++){
			for (int x = 0; x < map[0].length; x++){
				if ((int)oldPos.getX() == x && (int)oldPos.getY() == y){
					System.out.print(GROUND);
				}
				else if ((int)newPos.getX() == x && (int)newPos.getY() == y){
					System.out.print(TRAVELER);
				}
				else {
					System.out.print(map[y][x]);
				}
			}
			System.out.println("");
		}
		System.out.println("");
	}
}