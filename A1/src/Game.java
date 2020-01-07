import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class Game {

	public static void main(String[] args) {
		//bfs();
		State s = new State();
		s.printBoard();

		List<Move> moves = s.possibleMoves();
		for(Move m: moves) {
			System.out.println(m.t1 + " " + m.t2);
		}
	}

	private static final Set<Tuple> validPositions = new HashSet<>();
	static {
		validPositions.add(new Tuple(0, 2));
		validPositions.add(new Tuple(0, 3));
		validPositions.add(new Tuple(0, 4));

		validPositions.add(new Tuple(1, 1));
		validPositions.add(new Tuple(1, 2));
		validPositions.add(new Tuple(1, 3));
		validPositions.add(new Tuple(1, 4));
		validPositions.add(new Tuple(1, 5));

		validPositions.add(new Tuple(2, 0));
		validPositions.add(new Tuple(2, 1));
		validPositions.add(new Tuple(2, 2));
		validPositions.add(new Tuple(2, 3));
		validPositions.add(new Tuple(2, 4));
		validPositions.add(new Tuple(2, 5));
		validPositions.add(new Tuple(2, 6));

		validPositions.add(new Tuple(3, 0));
		validPositions.add(new Tuple(3, 1));
		validPositions.add(new Tuple(3, 2));
		validPositions.add(new Tuple(3, 3));
		validPositions.add(new Tuple(3, 4));
		validPositions.add(new Tuple(3, 5));
		validPositions.add(new Tuple(3, 6));

		validPositions.add(new Tuple(4, 0));
		validPositions.add(new Tuple(4, 1));
		validPositions.add(new Tuple(4, 2));
		validPositions.add(new Tuple(4, 3));
		validPositions.add(new Tuple(4, 4));
		validPositions.add(new Tuple(4, 5));
		validPositions.add(new Tuple(4, 6));

		validPositions.add(new Tuple(5, 1));
		validPositions.add(new Tuple(5, 2));
		validPositions.add(new Tuple(5, 3));
		validPositions.add(new Tuple(5, 4));
		validPositions.add(new Tuple(5, 5));

		validPositions.add(new Tuple(6, 2));
		validPositions.add(new Tuple(6, 3));
		validPositions.add(new Tuple(6, 4));
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

		public final Tuple t1;
		public final Tuple t2;
		public final Tuple result;

		public Move(Tuple t1, Tuple t2, Tuple result) {
			this.t1 = t1;
			this.t2 = t2;
			this.result = result;
		}
	}

	public static class State {
		private Set<Tuple> rocks = new HashSet<>();

		public void printBoard() {
			for(int i = 0; i < 7; i++) {
				for(int j = 0;j < 7; j++) {

					if(rocks.contains(new Tuple(i, j))) {
						System.out.print("X");		
					} else if (validPositions.contains(new Tuple(i, j))) {
						System.out.print("_");
					} else {
						System.out.print(" ");
					}

				}
				System.out.println();
			}
		}

		public State() {
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
		}

		public State(State s) {
			// TODO: copy
		}

		public State performMove(Move m) {
			State newState = new State();
			newState.rocks = new HashSet<>();
			newState.rocks.remove(m.t2);

			Tuple rockToChange = null;

			for(Tuple t: newState.rocks) {
				if(t.equals(m.t1)) {
					rockToChange = t;
					break;
				}
			}

			newState.rocks.remove(rockToChange);

			rockToChange.x = 2; // TODO:

			newState.rocks.add(rockToChange);

			return newState;
		}

		public boolean goal() {
			// TODO:
			return false;
		}

		public List<Move> possibleMoves(){
			List<Move> moves = new ArrayList<>();

			for(Tuple rock1: rocks) {
				for(Tuple rock2: rocks) {
					if(rock1.x + 1 == rock2.x && 
							rock1.y == rock2.y && 
							validPositions.contains(new Tuple(rock1.x + 2, rock1.y))
							&& !rocks.contains(new Tuple(rock1.x + 2, rock1.y))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x + 2, rock1.y)));
					}
					if(rock1.y + 1 == rock2.y && 
							rock1.x == rock2.x && 
							validPositions.contains(new Tuple(rock1.x, rock1.y + 2))
							&& !rocks.contains(new Tuple(rock1.x, rock1.y + 2))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x, rock1.y + 2)));
					}
					if(rock1.y - 1 == rock2.y && 
							rock1.x == rock2.x && 
							validPositions.contains(new Tuple(rock1.x, rock1.y - 2))
							&& !rocks.contains(new Tuple(rock1.x, rock1.y - 2))) {
						moves.add(new Move(rock1, rock2, new Tuple(rock1.x, rock1.y - 2)));
					}
					if(rock1.x - 1 == rock2.x && 
							rock1.y == rock2.y && 
							validPositions.contains(new Tuple(rock1.x - 2, rock1.y))
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
			if (x!= other.x || y != other.y) {
				return false;
			}
			return true;
		}
	}
}
