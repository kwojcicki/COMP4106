import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;

public class A1 {

	private static List<Move> mouseMoves;

	public static void main(String[] args) {
		State s = new State();
		s.printBoard();
		generateMouseMoves(s);

		System.out.println("Mouse path: " + mouseMoves + " " + mouseMoves.size());

		//dfs(s);
		//bfs(s);

		final Function<State, Double> heuristic1 = (state) -> {
			return (Math.pow(state.mouse.x - state.cat.x, 2) +
					Math.pow(state.mouse.y - state.cat.y, 2)); 
		};
		
		final Function<State, Double> heuristic2 = (state) -> {
			double distance = (Math.pow(state.mouse.x - state.cat.x, 2) +
					Math.pow(state.mouse.y - state.cat.y, 2));

			double catVelocity = Math.pow(2, 1) + Math.pow(1, 1);


			if(state.turn + (int)(distance/catVelocity) >= mouseMoves.size()) {
				return Double.MAX_VALUE;
			}

			Tuple mouseLocation = mouseMoves.get(state.turn + (int)(distance/catVelocity)).result;
			return (Math.pow(mouseLocation.x - state.cat.x, 2) +
					Math.pow(mouseLocation.y - state.cat.y, 2));
		};

		final Function<State, Double> heuristic3 = (state) -> {
			return (heuristic1.apply(state) + heuristic2.apply(state))/2.0;
		};
		
		
		Astar(s, heuristic1);
		Astar(s, heuristic2);
		Astar(s, heuristic3);
	}

	private static void generateMoves(Tuple src, Tuple dest) {
		Tuple curr = new Tuple(src.x, src.y);
		// 9,6  6,10
		int diagnol = Math.min(Math.abs(src.x - dest.x),
				Math.abs(src.y - dest.y)); // min(3, 4) == 3
		int x = Math.abs(src.x - dest.x) - diagnol > 0 ? Math.abs(src.x - dest.x) - diagnol : 0; // 3 - 3 == 0
		int y = Math.abs(src.y - dest.y) - diagnol > 0 ? Math.abs(src.y - dest.y) - diagnol : 0; // 4 - 3 == 1

		for(int i = 0; i < diagnol; i++) {
			Tuple newLocation = new Tuple(
					((int)(curr.x + Math.copySign(1, dest.x - curr.x))),
					((int)(curr.y + Math.copySign(1, dest.y - curr.y)))
					);
			mouseMoves.add(new Move(newLocation));
			curr = newLocation;
		}

		for(int i = 0; i < x; i++) {
			Tuple newLocation = new Tuple(
					((int)(curr.x + Math.copySign(1, dest.x - curr.x))),
					curr.y
					);
			mouseMoves.add(new Move(newLocation));
			curr = newLocation;
		}

		for(int i = 0; i < y; i++) {
			Tuple newLocation = new Tuple(
					curr.x,
					((int)(curr.y + Math.copySign(1, dest.y - curr.y)))
					);
			mouseMoves.add(new Move(newLocation));
			curr = newLocation;
		}
	}

	private static void generateMouseMoves(State s){
		mouseMoves = new ArrayList<>();

		List<Tuple> cheeseToGet = new ArrayList<>();
		Tuple mouse = new Tuple(s.mouse.x, s.mouse.y);
		for(Tuple c: s.cheese) {
			cheeseToGet.add(new Tuple(c.x, c.y));
		}

		while(true) {
			Tuple closest = null;
			for(Tuple c: cheeseToGet) {
				if(closest == null) {
					closest = c;
				}

				if(Math.pow(Math.abs(c.x - mouse.x), 2) +
						Math.pow(Math.abs(c.y - mouse.y), 2) <
						Math.pow(Math.abs(closest.x - mouse.x), 2) +
						Math.pow(Math.abs(closest.y - mouse.y), 2)){
					closest = c;
				}
			}

			if(closest == null) {
				break;
			}

			System.out.println("Closest cheese: " + closest);
			generateMoves(mouse, closest);
			cheeseToGet.remove(closest);
			mouse.x = closest.x;
			mouse.y = closest.y;
		}
	}

	private static void bfs(State original) {
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

	private static void Astar(State original, Function<State, Double> heuristic){
		Comparator<State> comparator = 
				(State o1, State o2) -> heuristic.apply(o1).compareTo(heuristic.apply(o2));

				PriorityQueue<State> states = new PriorityQueue<>(comparator);
				states.add(original);

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

	private static void dfs(State original) {
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
		private List<Move> moves = new ArrayList<>(38);
		int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		// Tuple cat, mouse;
		private List<Tuple> cats = new ArrayList<>();
		private List<Tuple> mice = new ArrayList<>();
		private List<Tuple> cheese = new ArrayList<>();
		public int turn = -1;

		public State() {
			//cat = new Tuple(2,6);
			cats.add(new Tuple(2,6));
			//mouse = new Tuple(7,1);
			mice.add(new Tuple(7,1));
			// TODO: randomize
			cheese.add(new Tuple(9,1));
			cheese.add(new Tuple(9,6));
			cheese.add(new Tuple(6,10));
		}

		public State(State other) {
			//this.cat = new Tuple(other.cat.x, other.cat.y);
			for(Tuple c: other.cats) {
				this.cats.add(new Tuple(c.x, c.y));
			}
			for(Tuple m: other.mice) {
				this.mice.add(new Tuple(m.x, m.y));
			}
			//this.mouse = new Tuple(other.mouse.x, other.mouse.y);
			this.turn = other.turn;
			for(Tuple c: other.cheese) {
				this.cheese.add(new Tuple(c.x, c.y));
			}
			for(Move m: other.moves) {
				this.moves.add(new Move(new Tuple(m.result.x, m.result.y)));
			}
		}
		
		public boolean containsThing(List<Tuple> things, int i, int j) {
			for(Tuple c: things) {
				if(c.x == j && c.y == i) {
					return true;
				}
			}
			return false;
		}

		public void printBoard() {
			for(int i = 0; i < BOARD_SIZE; i++) {
				for(int j = 0;j < BOARD_SIZE; j++) {
					if(containsThing(cats, i, j)) {
						System.out.print("c");
					} else if(containsThing(mice, i, j)) {
						System.out.print("m");
					} else if(containsThing(cheese, i, j)) {
						System.out.print("x");
					} else {
						System.out.print("_");
					}
				}
				System.out.println();
			}
			System.out.println("\n\n\n");
		}

		public State performMove(Move m) {
			State newState = new State(this);
			newState.cat.x = m.result.x;
			newState.cat.y = m.result.y;
			newState.moves.add(m);

			newState.turn++;
			newState.mouse = mouseMoves.get(newState.turn).result;
			for(Tuple c: newState.cheese) {
				if(c.x == newState.mouse.x &&
						c.y == newState.mouse.y) {
					newState.cheese.remove(c);
					break;
				}
			}

			return newState;
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
