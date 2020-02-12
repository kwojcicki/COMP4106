import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class A2 {


	public static final int N_s = 4;
	public static Set<Tuple> validBlue = new HashSet<>();
	public static Set<Tuple> mancalaBlue = new HashSet<>();

	public static Set<Tuple> validRed = new HashSet<>();
	public static Set<Tuple> mancalaRed = new HashSet<>();

	// TODO:
	public static Map<Tuple, Tuple> stoneOrderCCW = new HashMap<>();
	public static Map<Tuple, Tuple> stoneOrderCW = new HashMap<>();

	static {
		stoneOrderCCW.put(new Tuple(3, 0), new Tuple(3, 1));
		stoneOrderCCW.put(new Tuple(3, 1), new Tuple(3, 2));
		stoneOrderCCW.put(new Tuple(3, 2), new Tuple(3, 3));
		stoneOrderCCW.put(new Tuple(3, 3), new Tuple(2, 3));
		stoneOrderCCW.put(new Tuple(2, 3), new Tuple(1, 3));
		stoneOrderCCW.put(new Tuple(1, 3), new Tuple(0, 4));
		stoneOrderCCW.put(new Tuple(0, 4), new Tuple(1, 4));
		stoneOrderCCW.put(new Tuple(1, 4), new Tuple(2, 4));
		stoneOrderCCW.put(new Tuple(2, 4), new Tuple(3, 4));
		stoneOrderCCW.put(new Tuple(2, 4), new Tuple(3, 4));
		stoneOrderCCW.put(new Tuple(3, 4), new Tuple(3, 5));
		stoneOrderCCW.put(new Tuple(3, 5), new Tuple(3, 6));
		stoneOrderCCW.put(new Tuple(3, 6), new Tuple(4, 7));
		stoneOrderCCW.put(new Tuple(4, 7), new Tuple(4, 6));
		stoneOrderCCW.put(new Tuple(4, 6), new Tuple(4, 5));
		stoneOrderCCW.put(new Tuple(4, 5), new Tuple(4, 4));
		stoneOrderCCW.put(new Tuple(4, 4), new Tuple(5, 4));
		stoneOrderCCW.put(new Tuple(5, 4), new Tuple(6, 4));
		stoneOrderCCW.put(new Tuple(6, 4), new Tuple(7, 3));
		stoneOrderCCW.put(new Tuple(7, 3), new Tuple(6, 3));
		stoneOrderCCW.put(new Tuple(6, 3), new Tuple(5, 3));
		stoneOrderCCW.put(new Tuple(5, 3), new Tuple(4, 3));
		stoneOrderCCW.put(new Tuple(5, 3), new Tuple(4, 3));
		stoneOrderCCW.put(new Tuple(4, 3), new Tuple(4, 2));
		stoneOrderCCW.put(new Tuple(4, 2), new Tuple(4, 1));
		stoneOrderCCW.put(new Tuple(4, 1), new Tuple(3, 0));
		
		// TODO: stoneOrderCW
	}
	
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
		
		List<State> states = s.performMove(new Move(3, 2, 1, true, false));
		System.out.println("---");
		states.get(0).printBoard();
		System.out.println(states.size());
		
		//System.out.println(s.generateMoves());
		//System.out.println(s.generateMoves().size());
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

		@Override
		public String toString() {
			return "[" + x + "," + y + "," + String.valueOf(ccw) + "," + String.valueOf(take) + "]";
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
			// TODO: ?
		}

		public final Tuple nextPosition(final Tuple prev, boolean ccw) {
			if(ccw) {
				return stoneOrderCCW.get(prev);
			}
			return stoneOrderCW.get(prev);
		}

		public boolean mancala(Tuple position) {
			return mancalaBlue.contains(position) || mancalaRed.contains(position);
		}

		public boolean opponent() {
			// TODO:
			return false;
		}

		public List<State> performMove(Move m) {
			List<State> newStates = new ArrayList<>();
			State newState = new State(this);

			Tuple pos = new Tuple(m.x, m.y);
			int count = newState.stones.get(m.x + m.y * 8);
			newState.stones.set(m.x + m.y * 8, 0);

			for(int i = 1; i <= count; i++) {
				System.out.println("before: " + pos);
				pos = nextPosition(pos, m.ccw);
				System.out.println(pos);
				if(mancala(pos) && opponent()) {
					if(m.take) {
						// TODO:
					} else {
						i--;
						continue;
					}
				} else {
					newState.stones.set(pos.x + pos.y*8, newState.stones.get(pos.x + pos.y * 8) + 1);	
				}
			}
			
			// TODO: what if blue
			System.out.println(mancala(pos) + " " + pos);
			if(mancala(pos) && validRed.contains(pos)) {
				newStates.addAll(generateMoves());
			} else {
				newStates.add(newState);
			}


			return newStates;
		}

		public List<State> generateMoves() {
			List<State> newStates = new ArrayList<>();

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

				// TODO:
				Move n = new Move(position.x, position.y, 0, true, false);
				newStates.addAll(performMove(n));
			}

			return newStates;
		}

		public void printBoard() {
			for(int y = 0; y < 8; y++) {
				for(int x = 0; x < 8; x++) {
					Tuple p = new Tuple(x, y);
					//if( (validBlue.contains(p) && !mancalaBlue.contains(p))  ||
							//(validRed.contains(p) && !mancalaRed.contains(p))) {
					if( (validBlue.contains(p))  ||
							(validRed.contains(p))) {
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

		public Tuple(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return String.format("(%s, %s)", x, y);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x + y * 8;  
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
