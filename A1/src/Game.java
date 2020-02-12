import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.IntStream;

import sun.security.x509.UniqueIdentity;


public class Game {

	static List<Integer> p = new ArrayList<Integer>();
	public static void main(String[] args) {
		//dfs();
		//bfs();
		//test();
		
		final int n = 5;
		IntStream.range(0, 2 * n).forEach(p::add);
		List<List<Integer>> permuts = new ArrayList<List<Integer>>();
		permutations(0, permuts);
		System.out.println(permuts.size());
		
		List<List<Integer>> uniques = new ArrayList<>();
		
		for(List<Integer> pc: permuts) {
			boolean isUnique = true;
			for(List<Integer> u: uniques) {
				boolean hasUniquePair = false;
				for(int i = 0; i < pc.size(); i += 2) {
					boolean isUniquePair = true;
					for(int j = 0; j < u.size(); j+= 2) {
						if((pc.get(i) == u.get(j) && pc.get(i + 1) == u.get(j + 1)) || 
								(pc.get(i) == u.get(j + 1) && pc.get(i + 1) == u.get(j))) {
									isUniquePair = false;
									break;
								}
					}
					if(isUniquePair) {
						hasUniquePair = true;
						break;
					}
				}
				if(!hasUniquePair) {
					isUnique = false;
					break;
				}
			}
			if(isUnique) {
				uniques.add(pc);
			}
		}
		
		System.out.println(uniques.size());
	}
	
	public static void permutations(int index, List<List<Integer>> permuts) {
		if(index == p.size()) {
			ArrayList<Integer> dup = new ArrayList<>();
			p.forEach(dup::add);
			permuts.add(dup);
		}
		
		for(int i = index; i < p.size(); i++) {
			Collections.swap(p, index, i);
			permutations(index + 1, permuts);
			Collections.swap(p, index, i);
		}
	}

	private static void test() {
		State s = new State();
		s.printBoard();

		List<Move> moves = s.possibleMoves();
		for(Move m: moves) {
			System.out.println(m.t1 + " " + m.t2);
		}

		s = s.performMove(moves.get(2));
		s.printBoard();

		moves = s.possibleMoves();
		for(Move m: moves) {
			System.out.println(m.t1 + " " + m.t2);
		}
	}

	private static final boolean FULLBOARD = false;
	private static final Set<Tuple> VALID_POSITIONS = new HashSet<>();
	static {
		VALID_POSITIONS.add(new Tuple(0, 2));
		VALID_POSITIONS.add(new Tuple(0, 3));
		VALID_POSITIONS.add(new Tuple(0, 4));

		VALID_POSITIONS.add(new Tuple(1, 1));
		VALID_POSITIONS.add(new Tuple(1, 2));
		VALID_POSITIONS.add(new Tuple(1, 3));
		VALID_POSITIONS.add(new Tuple(1, 4));
		VALID_POSITIONS.add(new Tuple(1, 5));

		VALID_POSITIONS.add(new Tuple(2, 0));
		VALID_POSITIONS.add(new Tuple(2, 1));
		VALID_POSITIONS.add(new Tuple(2, 2));
		VALID_POSITIONS.add(new Tuple(2, 3));
		VALID_POSITIONS.add(new Tuple(2, 4));
		VALID_POSITIONS.add(new Tuple(2, 5));
		VALID_POSITIONS.add(new Tuple(2, 6));

		VALID_POSITIONS.add(new Tuple(3, 0));
		VALID_POSITIONS.add(new Tuple(3, 1));
		VALID_POSITIONS.add(new Tuple(3, 2));
		VALID_POSITIONS.add(new Tuple(3, 3));
		VALID_POSITIONS.add(new Tuple(3, 4));
		VALID_POSITIONS.add(new Tuple(3, 5));
		VALID_POSITIONS.add(new Tuple(3, 6));

		VALID_POSITIONS.add(new Tuple(4, 0));
		VALID_POSITIONS.add(new Tuple(4, 1));
		VALID_POSITIONS.add(new Tuple(4, 2));
		VALID_POSITIONS.add(new Tuple(4, 3));
		VALID_POSITIONS.add(new Tuple(4, 4));
		VALID_POSITIONS.add(new Tuple(4, 5));
		VALID_POSITIONS.add(new Tuple(4, 6));

		VALID_POSITIONS.add(new Tuple(5, 1));
		VALID_POSITIONS.add(new Tuple(5, 2));
		VALID_POSITIONS.add(new Tuple(5, 3));
		VALID_POSITIONS.add(new Tuple(5, 4));
		VALID_POSITIONS.add(new Tuple(5, 5));

		VALID_POSITIONS.add(new Tuple(6, 2));
		VALID_POSITIONS.add(new Tuple(6, 3));
		VALID_POSITIONS.add(new Tuple(6, 4));
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

	private static void bfs() {
		State original = new State();
		Queue<State> states = new LinkedList<>();

		states.add(original);
		original.printBoard();

		while(!states.isEmpty()) {
			State e = states.poll();
			List<Move> moves = e.possibleMoves();
			for(Move m: moves) {
				State post = e.performMove(m);
				if(post.goal()) {
					post.printBoard();
					post.printMoves();
					System.out.println("Complete");
					return;
				}
				states.add(post);
			}
		}

		System.out.println("No possible solutions");
	}

	public static class Move {

		public final Tuple t1;
		public final Tuple t2;
		public final Tuple result;

		public Move(Tuple t1, Tuple t2, Tuple result) {
			this.t1 = t1;
			this.t2 = t2;
			this.result = result;
		}

		@Override
		public String toString() {
			return t1 + " " + t2;
		}
	}

	public static class State {
		private Set<Tuple> rocks = new HashSet<>();
		private List<Move> moves = new ArrayList<>(38); 

		public void printMoves() {
			System.out.println(moves);
			System.out.println(moves.size());
		}

		public void printBoard() {
			for(int i = 0; i < 7; i++) {
				for(int j = 0;j < 7; j++) {
					if(rocks.contains(new Tuple(i, j))) {
						System.out.print("X");		
					} else if (VALID_POSITIONS.contains(new Tuple(i, j))) {
						System.out.print("_");
					} else {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
		}

		public State() {
			if(FULLBOARD) {
				rocks.add(new Tuple(0, 2));
				rocks.add(new Tuple(0, 3));
				rocks.add(new Tuple(0, 4));

				rocks.add(new Tuple(1, 1));
				rocks.add(new Tuple(1, 2));
				rocks.add(new Tuple(1, 3));
				rocks.add(new Tuple(1, 4));
				rocks.add(new Tuple(1, 5));

				rocks.add(new Tuple(2, 0));
				rocks.add(new Tuple(2, 1));
				rocks.add(new Tuple(2, 2));
				// rocks.add(new Tuple(2, 3));
				rocks.add(new Tuple(2, 4));
				rocks.add(new Tuple(2, 5));
				rocks.add(new Tuple(2, 6));

				rocks.add(new Tuple(3, 0));
				rocks.add(new Tuple(3, 1));
				rocks.add(new Tuple(3, 2));
				rocks.add(new Tuple(3, 3));
				rocks.add(new Tuple(3, 4));
				rocks.add(new Tuple(3, 5));
				rocks.add(new Tuple(3, 6));

				rocks.add(new Tuple(4, 0));
				rocks.add(new Tuple(4, 1));
				rocks.add(new Tuple(4, 2));
				rocks.add(new Tuple(4, 3));
				rocks.add(new Tuple(4, 4));
				rocks.add(new Tuple(4, 5));
				rocks.add(new Tuple(4, 6));

				rocks.add(new Tuple(5, 1));
				rocks.add(new Tuple(5, 2));
				rocks.add(new Tuple(5, 3));
				rocks.add(new Tuple(5, 4));
				rocks.add(new Tuple(5, 5));

				rocks.add(new Tuple(6, 2));
				rocks.add(new Tuple(6, 3));
				rocks.add(new Tuple(6, 4));
			} else {
				rocks.add(new Tuple(1, 5));

				rocks.add(new Tuple(2, 0));
				rocks.add(new Tuple(2, 1));
				rocks.add(new Tuple(2, 4));

				rocks.add(new Tuple(3, 0));
				rocks.add(new Tuple(3, 1));
				rocks.add(new Tuple(3, 2));
				rocks.add(new Tuple(3, 4));

				rocks.add(new Tuple(4, 0));
				rocks.add(new Tuple(4, 3));
				rocks.add(new Tuple(4, 4));

				rocks.add(new Tuple(5, 4));
				rocks.add(new Tuple(5, 5));

				rocks.add(new Tuple(6, 4));

			}
		}

		public State(State s) {
			for(Tuple t: s.rocks) {
				this.rocks.add(new Tuple(t.x, t.y));
			}
			for(Move m: s.moves) {
				this.moves.add(m);
			}
		}

		public State performMove(Move m) {
			State newState = new State(this);
			newState.moves.add(m);
			newState.rocks.remove(m.t2);

			Tuple rockToChange = null;

			for(Tuple t: newState.rocks) {
				if(t.equals(m.t1)) {
					rockToChange = t;
					break;
				}
			}

			newState.rocks.remove(rockToChange);

			rockToChange.x = m.result.x;
			rockToChange.y = m.result.y;

			newState.rocks.add(rockToChange);

			return newState;
		}

		public boolean goal() {
			return rocks.size() == 1 && 
					rocks.contains(new Tuple(1, 3));
		}

		public List<Move> possibleMoves(){
			List<Move> moves = new ArrayList<>();

			for(Tuple rock1: rocks) {
				for(Tuple rock2: rocks) {
					if(rock1.x + 1 == rock2.x && 
							rock1.y == rock2.y && 
							VALID_POSITIONS.contains(new Tuple(rock1.x + 2, rock1.y))
							&& !rocks.contains(new Tuple(rock1.x + 2, rock1.y))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x + 2, rock1.y)));
					}
					if(rock1.y + 1 == rock2.y && 
							rock1.x == rock2.x && 
							VALID_POSITIONS.contains(new Tuple(rock1.x, rock1.y + 2))
							&& !rocks.contains(new Tuple(rock1.x, rock1.y + 2))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x, rock1.y + 2)));
					}
					if(rock1.y - 1 == rock2.y && 
							rock1.x == rock2.x && 
							VALID_POSITIONS.contains(new Tuple(rock1.x, rock1.y - 2))
							&& !rocks.contains(new Tuple(rock1.x, rock1.y - 2))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x, rock1.y - 2)));
					}
					if(rock1.x - 1 == rock2.x && 
							rock1.y == rock2.y && 
							VALID_POSITIONS.contains(new Tuple(rock1.x - 2, rock1.y))
							&& !rocks.contains(new Tuple(rock1.x - 2, rock1.y))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x - 2, rock1.y)));
					}
				}
			}

			return moves;
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
