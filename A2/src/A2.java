import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

public class A2 {


	public static final int N_s = 4;
	public static final int ALLOWED_DEPTH = 4;
	public static final boolean HUMAN_PLAYER = false;
	public static final boolean ALPHA_BETA = true;

	public static Set<Tuple> validBlue = new HashSet<>();
	public static Set<Tuple> mancalaBlue = new HashSet<>();

	public static Set<Tuple> validRed = new HashSet<>();
	public static Set<Tuple> mancalaRed = new HashSet<>();

	public static Map<Tuple, Tuple> stoneOrderCCW = new HashMap<>();
	public static Map<Tuple, Tuple> stoneOrderCW = new HashMap<>();

	public static Map<State, Set<State>> expanded = new HashMap<>();

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

		for(Entry<Tuple, Tuple> entry: stoneOrderCCW.entrySet()) {
			stoneOrderCW.put(entry.getValue(), entry.getKey());
		}

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

	public static void main(String[] args) throws InterruptedException {
		Scanner in = new Scanner(System.in);
		State s = new State();
		Player p = new Player(3);
		Player p1 = new Player(4);

		while(true) {
			s.printBoard();
			System.out.println("---");
			s = p.computerPlay(s);

			s.printBoard();
			System.out.println("---");

			if(HUMAN_PLAYER) {
				while(true) {
					int x = in.nextInt();
					int y = in.nextInt();
					boolean ccw = in.nextBoolean();
					if(x == -1 || y == -1) {
						break;
					}

					KeyPair<Boolean, State> r = s.performMovePlayer(new Move(x, y, 1, ccw, false));
					s = r.value;
					if(!r.key) {
						break;
					}
				}

				s.redTurn = !s.redTurn;	
			} else {
				s = p1.computerPlay(s);
				//Thread.sleep(5000);
			}

			if(s.goal()) {
				break;
			}

		}

		// List<State> states = s.performMove(new Move(3, 2, 1, true, false));

		//states.get(0).printBoard();
		//System.out.println(states.size());

		in.close();
		//System.out.println(s.generateMoves());
		//System.out.println(s.generateMoves().size());
	}


	static class Player {

		private final int HEURISTIC;
		private int expanded = 0;
		
		public Player(final int h) {
			this.HEURISTIC = h;
		}

		final Function<State, Integer> heuristic1 = (state) -> {
			return mancalaRed.stream().mapToInt(i -> state.stones.get(i.x + i.y * 8)).sum();
		};
		
		final Function<State, Integer> heuristic2 = (state) -> {
			return mancalaBlue.stream().mapToInt(i -> state.stones.get(i.x + i.y * 8)).sum();
		};

		final Function<State, Integer> heuristic3 = (state) -> {
			return validRed.stream().mapToInt(i -> state.stones.get(i.x + i.y * 8)).sum();
		};
		
		final Function<State, Integer> heuristic4 = (state) -> {
			return validBlue.stream().mapToInt(i -> state.stones.get(i.x + i.y * 8)).sum();
		};
		
		private State computerPlay(State initialState) {
			this.expanded = 0;
			KeyPair<Integer, State> best = maxValue(new State(initialState, initialState.redTurn), Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
			System.out.println("best: " + best.key);
			//			for(State s: initialState.generateMoves()) {
			//				if(heuristic(s) == best) {
			//					return s;
			//				}
			//			}
			System.out.println("Expanded: " + this.expanded);
			return best.value;
		}

		private KeyPair<Integer, State> maxValue(State state, int alpha, int beta, int currDepth) {
			if(currDepth > ALLOWED_DEPTH || state.goal()) {
				return new KeyPair<>(heuristic(state), state);
			}
			int bestValue = Integer.MIN_VALUE;
			State bestState = null;
			Set<State> possibleMoves = state.generateMoves();
			//int possibleMovesSize = possibleMoves.size();
			//if(possibleMovesSize > 10000) {
			//state.generateMoves();	
			//}
			for(State node : possibleMoves){
				KeyPair<Integer, State> mv;
				this.expanded++;
				//if(minValues.containsKey(node)) {
				//	        		mv = new KeyPair<>(minValues.get(node), node); 
				//} else {
				mv  = minValue(node, alpha, beta, currDepth + 1);
				//minValues.put(node, mv.key);
				//}

				if(mv.key > bestValue) {
					bestValue = mv.key;
					bestState = node;
				}

				if(ALPHA_BETA && bestValue >= beta) {
					break;
				}
				alpha = Math.max(alpha, bestValue);
			}
			return new KeyPair<>(bestValue, bestState);
		}

		private int heuristic(State state) {
			int heuristicValue = -1;
			if(HEURISTIC == 1) {
				heuristicValue = heuristic1.apply(state);	
			} else if(HEURISTIC == 2) {
				heuristicValue = heuristic2.apply(state);	
			} else if(HEURISTIC == 3) {
				heuristicValue = heuristic3.apply(state);	
			} else if(HEURISTIC == 4) {
				heuristicValue = heuristic4.apply(state);	
			}

			return heuristicValue;
		}

		private KeyPair<Integer, State> minValue(State state, int alpha, int beta, int currDepth){
			if(currDepth > ALLOWED_DEPTH || state.goal()) {
				return new KeyPair<>(heuristic(state), state);
			}

			int bestValue = Integer.MAX_VALUE;
			State bestState = null;

			Set<State> possibleMoves = state.generateMoves();
			for(State node : possibleMoves){
				KeyPair<Integer, State> mv;
				//if(maxValues.containsKey(node)) {
				//					mv = new KeyPair<>(maxValues.get(node), node); 
				//} else {
				mv = maxValue(node, alpha, beta, currDepth + 1);
				this.expanded++;
				//					maxValues.put(node, mv.key);
				//}
				if(bestValue > mv.key) {
					bestValue = mv.key;
					bestState = node;
				}
				if(ALPHA_BETA && bestValue <= alpha) {
					break;
				}
				beta = Math.min(beta, bestValue);
			}
			return new KeyPair<>(bestValue, bestState);
		}

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

		boolean redTurn = true;

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

			//stones.add(e)
			//			List<Integer> x = Arrays.asList(0,0,0,2,0,0,0,0,
			//					0,0,0,0,0,0,0,0,
			//					0,0,0,8,0,0,0,0,
			//					0,0,1,8,6,6,7,4,
			//					2,6,6,1,0,0,0,0,
			//					0,0,0,2,5,0,0,0,
			//					0,0,0,7,7,0,0,0, 
			//					0,0,0,0,2,0,0,0);
			//
			//			x.stream().forEach(i -> stones.add(i));
		}

		public boolean goal() {
			boolean flag = true, flag1 = true;
			for(Tuple s: validRed) {
				if(!mancala(s) && stones.get(s.x + s.y * 8) != 0) {
					flag = false;
				}
			}

			for(Tuple s: validBlue) {
				if(!mancala(s) && stones.get(s.x + s.y * 8) != 0) {
					flag1 = false;
				}
			}
			return flag || flag1;
		}

		public State(State s, boolean redTurn) {
			this.redTurn = redTurn;
			s.stones.forEach(i -> this.stones.add(i));
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

		public boolean opponentMancala(Tuple position) {
			if(this.redTurn) {
				return mancalaBlue.contains(position);
			}

			return mancalaRed.contains(position);
		}

		public KeyPair<Boolean, State> performMovePlayer(Move m) {
			State newState = new State(this, this.redTurn);

			Tuple pos = new Tuple(m.x, m.y);
			int count = newState.stones.get(m.x + m.y * 8);
			newState.stones.set(m.x + m.y * 8, 0);

			for(int i = 1; i <= count; i++) {
				pos = nextPosition(pos, m.ccw);
				if(mancala(pos) && opponentMancala(pos)) {
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

			if(this.redTurn && mancala(pos) && validRed.contains(pos)) {
				return new KeyPair<>(true, newState);
			} else if(!this.redTurn && mancala(pos) && validBlue.contains(pos)) {
				return new KeyPair<>(true, newState);
			}

			int increase = 0;
			if(!mancala(new Tuple(pos.x + 1, pos.y)) && pos.x + 1 + pos.y * 8 < 64 && pos.x + 1 + pos.y * 8 >= 0) {
				increase += newState.stones.get(pos.x + 1 + pos.y * 8);	
				newState.stones.set(pos.x + 1 + pos.y * 8, 0);
			}
			if(!mancala(new Tuple(pos.x, pos.y + 1)) && pos.x + (pos.y + 1) * 8 < 64 && pos.x + (pos.y + 1) * 8 >= 0) {
				increase += newState.stones.get(pos.x + (pos.y + 1) * 8);
				newState.stones.set(pos.x + (pos.y + 1) * 8, 0);
			}
			if(!mancala(new Tuple(pos.x - 1, pos.y)) && pos.x - 1 + pos.y * 8 < 64 && pos.x - 1 + pos.y * 8 >= 0) {
				increase += newState.stones.get(pos.x - 1 + pos.y * 8);
				newState.stones.set(pos.x - 1 + pos.y * 8, 0);
			}
			if(!mancala(new Tuple(pos.x, pos.y - 1)) && pos.x + (pos.y - 1) * 8 < 64 && pos.x + (pos.y - 1) * 8 >= 0) {
				increase += newState.stones.get(pos.x + (pos.y - 1) * 8);
				newState.stones.set(pos.x + (pos.y - 1) * 8, 0);
			}

			newState.stones.set(pos.x + pos.y*8, newState.stones.get(pos.x + pos.y * 8) + increase);

			return new KeyPair<>(false, newState);
		}

		public List<State> performMove(Move m) {
			List<State> newStates = new ArrayList<>();

			KeyPair<Boolean, State> result = performMovePlayer(m);

			if(result.key) {
				//result.value.redTurn = !result.value.redTurn;
				newStates.addAll(result.value.generateMoves());
			} else {
				result.value.redTurn = !result.value.redTurn;
				newStates.add(result.value);
			}

			return newStates;
		}

		public Set<State> generateMoves() {
			Set<State> newStates = new HashSet<>();

			Set<Tuple> stonesToUse = validBlue;
			if(redTurn) {
				stonesToUse = validRed;
			}

			if(goal()) {
				return newStates;
			}

			if(expanded.containsKey(this)) {
				return expanded.get(this);
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
				newStates.addAll(performMove(new Move(position.x, position.y, 0, true, false)));
				newStates.addAll(performMove(new Move(position.x, position.y, 0, false, false)));
				
				if(ALPHA_BETA) {
					newStates.addAll(performMove(new Move(position.x, position.y, 0, true, true)));
					newStates.addAll(performMove(new Move(position.x, position.y, 1, true, true)));
					newStates.addAll(performMove(new Move(position.x, position.y, 2, true, true)));
					newStates.addAll(performMove(new Move(position.x, position.y, 3, true, true)));

					newStates.addAll(performMove(new Move(position.x, position.y, 0, false, true)));
					newStates.addAll(performMove(new Move(position.x, position.y, 1, false, true)));
					newStates.addAll(performMove(new Move(position.x, position.y, 2, false, true)));
					newStates.addAll(performMove(new Move(position.x, position.y, 3, false, true)));
				}
				
			}

			expanded.put(this, newStates);

			return newStates;
		}

		public void printBoard() {
			for(int y = 0; y < 8; y++) {
				for(int x = 0; x < 8; x++) {
					Tuple p = new Tuple(x, y);
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


		@Override
		public int hashCode() {
			return stones.hashCode() * 10 + (this.redTurn ? 1 : 2);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;

			if(other.redTurn != this.redTurn) {
				return false;
			}

			for(int i = 0; i < stones.size(); i++) {
				if(stones.get(i) != other.stones.get(i)) {
					return false;
				}
			}

			return true;
		}

	}

	public static class KeyPair<T, V> {

		public final T key;
		public final V value;

		public KeyPair(T x, V y) {
			this.key = x;
			this.value = y;
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
