import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class A2 {

	
	public static final int N_s = 4;
	public static Set<Tuple> validBlue = new HashSet<>();
	public static Set<Tuple> mancalaBlue = new HashSet<>();
	
	public static Set<Tuple> validRed = new HashSet<>();
	public static Set<Tuple> mancalaRed = new HashSet<>();
	
	static {
		validBlue.add(new Tuple(3, 0));
		validBlue.add(new Tuple(4, 0));
		mancalaBlue.add(new Tuple(3, 0));
		mancalaBlue.add(new Tuple(4, 0));
		
		validBlue.add(new Tuple(3, 1));
		validBlue.add(new Tuple(4, 1));
		
		validBlue.add(new Tuple(3, 2));
		validBlue.add(new Tuple(4, 2));
		
		validBlue.add(new Tuple(4, 3));
		
		validBlue.add(new Tuple(3, 4));
		
		validBlue.add(new Tuple(3, 5));
		validBlue.add(new Tuple(4, 5));
		
		validBlue.add(new Tuple(3, 6));
		validBlue.add(new Tuple(4, 6));
		
		validBlue.add(new Tuple(3, 7));
		validBlue.add(new Tuple(4, 7));
		mancalaBlue.add(new Tuple(3, 7));
		mancalaBlue.add(new Tuple(4, 7));
		
		validRed.add(new Tuple(0, 3));
		validRed.add(new Tuple(0, 4));
		mancalaRed.add(new Tuple(0, 3));
		mancalaRed.add(new Tuple(0, 4));
		
		validRed.add(new Tuple(1, 3));
		validRed.add(new Tuple(1, 4));
		
		validRed.add(new Tuple(2, 3));
		validRed.add(new Tuple(2, 4));
		
		validRed.add(new Tuple(3, 3));

		validRed.add(new Tuple(4, 4));
		
		validRed.add(new Tuple(5, 3));
		validRed.add(new Tuple(5, 4));
		
		validRed.add(new Tuple(6, 3));
		validRed.add(new Tuple(6, 4));
		
		validRed.add(new Tuple(7, 3));
		validRed.add(new Tuple(7, 4));
		mancalaRed.add(new Tuple(7, 3));
		mancalaRed.add(new Tuple(7, 4));
	}
	
	public static void main(String[] args) {
		State s = new State();
		
		s.printBoard();
	}
	
	
	static class Move {
		public final int x, y, direction;
		public final boolean ccw, take;
		
		public Move(int x, int y, int direction, boolean ccw, boolean take) {
			this.x = x;
			this.y = y;
			this.direction = direction;
			this.ccw = ccw;
			this.take = take;
		}
	}
	
	static class State {
		
		private boolean redTurn = true;
		
		List<Integer> stones = new ArrayList<>();
		
		public State() {
			IntStream.range(0, 8 * 8).forEach(i -> {				
				Tuple p = new Tuple(i % 8, i / 8);
				if( (validBlue.contains(p) && !mancalaBlue.contains(p))  ||
						(validRed.contains(p) && !mancalaRed.contains(p))) {
					stones.add(N_s);
				} else {
					stones.add(0);
				}
				
			});
		}
		
		public State(State s) {
			this.redTurn = !s.redTurn;
			s.stones.forEach(i -> this.stones.add(i));
		}
		
		public State performMove(Move m) {
			State newState = new State(this);
			
			
			
			return newState;
		}
		
		public List<Move> generateMoves() {
			List<Move> moves = new ArrayList<>();
			
			Set<Tuple> stonesToUse = validBlue;
			if(redTurn) {
				stonesToUse = validRed;
			}
			
			
			for(Tuple position: stonesToUse) {
				if(mancalaBlue.contains(position) || 
						mancalaRed.contains(position)) {
					continue;
				}
				
				if(stones.get(position.x + position.y * 8) == 0) {
					continue;
				}
				
				Move n = new Move(position.x, position.y, 0, true, true);
				//Move n1 = new Move(position.x, position.y, 0, false, false);
				moves.add(n);
			}
			
			
			return moves;
		}
		
		public void printBoard() {
			for(int y = 0; y < 8; y++) {
				for(int x = 0; x < 8; x++) {
					Tuple p = new Tuple(x, y);
					if( (validBlue.contains(p) && !mancalaBlue.contains(p))  ||
							(validRed.contains(p) && !mancalaRed.contains(p))) {
						System.out.print(stones.get(x + y * 8));
					} else {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
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
