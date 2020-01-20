import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class A1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	private static void dfs() {
		State original = new State();
		Stack<State> states = new Stack<>();

		states.add(original);

		while(!states.isEmpty()) {
			State e = states.pop();
			List<Move> moves = e.possibleMoves();
			for(Move m: moves) {
				State post = e.performMove(m);
				if(post.goal()) {
					post.printBoard();
					post.printMoves();
					System.out.println("Complete");
					return;
				}
				states.push(post);
			}
		}

		System.out.println("No possible solutions");
	}

	public static class State {

		private static int[][] SHIFT = {{1,2}, {2,1}, {2, -1}, {1, -2}, 
				{-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};

		private static final int BOARD_SIZE = 12;

		public void printBoard() {
			for(int i = 0; i < BOARD_SIZE; i++) {
				for(int j = 0;j < BOARD_SIZE; j++) {
					if(cat.x == i && cat.y == j) {
						System.out.print("c");
					} else if(mouse.x == i && mouse.y == j) {
						System.out.print("m");
					} else {
						// TODO: cheese
						System.out.print("_");
					}
				}
				System.out.println();
			}
		}

		public State performMove(Move m) {
			State newState = new State(this);
			newState.cat.x = m.result.x;
			newState.cat.y = m.result.y;
			
			// TODO: move mouse
			
			return newState;
		}

		private List<Move> moves = new ArrayList<>(38);
		int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		Tuple cat, mouse;
		private List<Tuple> cheese = new ArrayList<>();

		public State() {
			// TODO:
			cat = new Tuple(0,0);
			mouse = new Tuple(1,1);
			cheese.add(new Tuple(0,0));
			cheese.add(new Tuple(0,0));
			cheese.add(new Tuple(0,0));
		}

		public State(State other) {
			this.cat = new Tuple(other.cat.x, other.cat.y);
			this.mouse = new Tuple(other.mouse.x, other.mouse.y);
			for(Tuple c: other.cheese) {
				this.cheese.add(new Tuple(c.x, c.y));
			}
			for(Move m: other.moves) {
				this.moves.add(new Move(new Tuple(m.result.x, m.result.y)));
			}
		}

		public void printMoves() {
			System.out.println(moves);
			System.out.println(moves.size());
		}

		public List<Move> possibleMoves(){
			List<Move> possibleMoves = new ArrayList<>();

			if(cheese.size() == 0) {
				return possibleMoves; 
			}

			for(int[] possibility: SHIFT) {
				if(cat.x + possibility[0] >= 0 && cat.x + possibility[0] < BOARD_SIZE &&
						cat.y + possibility[1] >= 0 && cat.y + possibility[1] < BOARD_SIZE) {
					possibleMoves.add(new Move(new Tuple(cat.x + possibility[0], cat.y + possibility[1])));
				}
			}

			return possibleMoves;
		}

		public boolean goal() {
			return cat.x == mouse.x && cat.y == mouse.y;
		}
	}

	public static class Move {

		//public final Tuple t1;
		//public final Tuple t2;
		public final Tuple result;

		public Move(Tuple result) {
			//this.t1 = t1;
			//this.t2 = t2;
			this.result = result;
		}

		@Override
		public String toString() {
			//return t1 + " " + t2;
			return this.result.toString();
		}
	}

	public static class Tuple {
		public int x;
		public int y;

		public Tuple(int integerPart, int decimalPart) {
			super();
			this.x = integerPart;
			this.y = decimalPart;
		}

		@Override
		public String toString() {
			return String.format("(%s, %s)", x, y);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x + y;  
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple other = (Tuple) obj;
			if (x != other.x || y != other.y) {
				return false;
			}
			return true;
		}
	}

}
