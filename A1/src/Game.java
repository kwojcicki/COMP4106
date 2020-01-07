import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Game {

	public static void main(String[] args) {
		bfs();
	}
	
	private static final Set<Tuple> validPositions = new HashSet<>();
	static {
		validPositions.add(new Tuple(0, 2));
		validPositions.add(new Tuple(0, 3));
		validPositions.add(new Tuple(0, 4));
	}

	private static void bfs() {
		State original = new State();
		Queue<State> states = new LinkedList<>();
		
		states.add(original);
		
		while(!states.isEmpty()) {
			State e = states.poll();
			List<Move> moves = e.possibleMoves();
			for(Move m: moves) {
				State post = e.performMove(m);
				if(post.goal()) {
					System.out.println("Complete");
					return;
				}
				states.add(post);
			}
		}
	}

	public static class Move {
		
	}
	
	public static class State {
		private Set<Tuple> rocks = new HashSet<>();

		public State() {
			// TODO: set rocks to all validPositions
		}

		public State(State s) {
			// TODO: copy
		}
		
		public State performMove(Move m) {
			// TOOD:
			State newState = new State();
			newState.rocks = new HashSet<>();
			
			return newState;
		}
		
		public boolean goal() {
			// TODO:
			return false;
		}
		
		public List<Move> possibleMoves(){
			List<Move> moves = new ArrayList<>();
			// TODO:
			return moves;
		}
	}

	public static class Tuple {
		public final int x;
		public final int y;

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
			if (x!= other.x || y != other.y) {
				return false;
			}
			return true;
		}
	}
}
