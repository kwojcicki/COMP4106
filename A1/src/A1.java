import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

public class A1 {


	public static void doA(Function<State, Node> a, State t) {

		Node finalState = a.apply(t);

		System.out.println("\n\n-----------");
		while(finalState != null) {
			finalState.state.printBoard();
			finalState.state.printMoves();

			finalState = finalState.parent;
		}
	}

	public static void main(String[] args) {

		State s = new State();
		s.printBoard();

		final Function<Node, Double> heuristic1 = (state) -> {
			double ret = state.depth;
			for(int i = 0; i <  state.state.mice.size(); i++) {
				for(int j = 0; j < state.state.cats.size(); j++) {
					ret += (Math.pow(state.state.mice.get(i).x - state.state.cats.get(j).x, 2) +
							Math.pow(state.state.mice.get(i).y - state.state.cats.get(j).y, 2)); 
				}
			}
			return ret;
		};

		final Function<Node, Double> heuristic2 = (state) -> {
			double ret = state.depth;
			for(int i = 0; i <  state.state.mice.size(); i++) {
				for(int j = 0; j < state.state.cats.size(); j++) {
					double distance = (Math.pow(state.state.mice.get(i).x - state.state.cats.get(j).x, 2) +
							Math.pow(state.state.mice.get(i).y - state.state.cats.get(j).y, 2));

					double catVelocity = Math.sqrt(Math.pow(2, 2) + Math.pow(1, 2));

					int futureSteps = (int)(distance/catVelocity);
					State n = new State(state.state);
					for(int f = 0; f < futureSteps; f++) {
						n.simulateMice(n);
						n = new State(n);
						if(n.goal()) {
							return Double.MAX_VALUE;
						}
					}

					Tuple mouseLocation = n.mice.get(0);
					ret += (Math.pow(mouseLocation.x - state.state.cats.get(j).x, 2) +
							Math.pow(mouseLocation.y - state.state.cats.get(j).y, 2));
				}
			}
			return ret;
		};

		final Function<Node, Double> heuristic3 = (state) -> {
			return (heuristic1.apply(state) + heuristic2.apply(state))/2.0;
		};

		doA((state) -> dfs(state), s);
		//doA((state) -> bfs(state), s);
		//doA((state) -> ids(state), s);
		//doA((state) -> Astar(state, heuristic2), s);
	}

	private static Node dfs(State original) {
		Stack<Node> states = new Stack<>();
		Set<State> visited = new HashSet<>();

		states.add(new Node(original, null));
		visited.add(original);

		int i = 0;
		while(!states.isEmpty()) {
			Node e = states.pop();
			i++;
			if(e.state.goal()) {
				e.state.printBoard();
				e.state.printMoves();
				System.out.println("Complete, expanded: " + i);
				return e;
			}
			visited.add(e.state);
			List<Move> moves = e.state.possibleMoves();
			for(Move m: moves) {
				State newState = e.state.performMove(m);
				Node post = new Node(newState, e);
				if(!visited.contains(newState)) {
					states.push(post);	
				}
			}
		}

		System.out.println("No possible solutions, expanded: " + i);
		return null;
	}


	private static Node ids(State original) {
		final int MAX = 20;
		int expanded = 0;
		for(int i = 1; i < MAX; i++) {
			Stack<Node> states = new Stack<>();
			Set<State> visited = new HashSet<>();
			expanded++;
			states.add(new Node(original, null));
			visited.add(original);

			while(!states.isEmpty()) {
				Node e = states.pop();
				visited.add(e.state);
				List<Move> moves = e.state.possibleMoves();
				if(e.state.goal()) {
					e.state.printBoard();
					e.state.printMoves();
					System.out.println("Complete, expanded: " + expanded);
					return e;
				}
				if(e.depth == i) {
					continue;
				}
				for(Move m: moves) {
					State newState = e.state.performMove(m);
					Node post = new Node(newState, e);
					if(!visited.contains(newState)) {
						states.push(post);	
					}
				}
			}
		}

		System.out.println("No possible solutions in depth of " + MAX);
		return null;
	}

	private static Node bfs(State original) {
		Queue<Node> states = new LinkedList<>();
		Set<State> visited = new HashSet<>();

		states.add(new Node(original, null));
		visited.add(original);
		original.printBoard();

		int i = 0;
		while(!states.isEmpty()) {
			Node e = states.poll();
			i++;
			if(e.state.goal()) {
				e.state.printBoard();
				e.state.printMoves();
				System.out.println("Complete, expanded: " + i);
				return e;
			}
			visited.add(e.state);
			List<Move> moves = e.state.possibleMoves();
			for(Move m: moves) {
				State newState = e.state.performMove(m);
				Node post = new Node(newState, e);
				if(!visited.contains(newState)) {
					states.add(post);	
				}
			}
		}

		System.out.println("No possible solutions, expanded: " + i);
		return null;
	}

	private static Node Astar(State original, Function<Node, Double> heuristic){
		Comparator<Node> comparator = 
				(Node o1, Node o2) -> heuristic.apply(o1).compareTo(heuristic.apply(o2));

				PriorityQueue<Node> states = new PriorityQueue<>(comparator);
				Map<State, Node> closed = new HashMap<>();
				Map<State, Node> openMap = new HashMap<>();

				states.add(new Node(original, null));
				int i = 0;
				while(!states.isEmpty()) {
					Node e = states.poll();
					i++;

					if(e.bad) {
						continue;
					}

					if(e.state.goal()) {
						e.state.printBoard();
						e.state.printMoves();
						System.out.println("Complete, expanded: " + i);
						return e;
					}

					openMap.remove(e.state);
					closed.put(e.state, e);
					List<Move> moves = e.state.possibleMoves();
					for(Move m: moves) {
						Node post = new Node(e.state.performMove(m), e);
						if(!openMap.containsKey(post.state) && !closed.containsKey(post.state)) {
							states.add(post);
							openMap.put(post.state, post);							
						} else if(closed.containsKey(post.state) && closed.get(post.state).cost > post.cost){
							states.add(post);
							openMap.put(post.state, post);
							closed.get(post.state).bad = true;
						} else if(openMap.containsKey(post.state) && openMap.get(post.state).cost > post.cost){
							states.add(post);
							openMap.get(post.state).bad = true;
							openMap.put(post.state, post);
						}

						//states.add(post);
					}
				}

				System.out.println("No possible solutions, expanded: " + i);
				return null;
	}

	public static class Node {
		private final State state;
		private Node parent = null;
		private int depth, cost;
		private boolean bad = false;
		public Node(State state, Node parent) {
			this.state = state;
			this.parent = parent;
			this.depth = (parent == null ? 0: parent.depth + 1);
			this.cost = (parent == null ? 0 : parent.cost + 1 );
		}
	}

	public static class State {

		private static int[][] SHIFT = {{1,2}, {2,1}, {2, -1}, {1, -2}, 
				{-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};

		private static final int BOARD_SIZE = 12; // 12, 30, 50
		private static final boolean RANDOMIZE = true;
		private static final int N_CATS = 1;
		private static final int N_MOUSE = 1;
		private static final int N_CHEESE = 3;
		private static final int MOUSE_MOVE = 1;
		private List<Move> moves = new ArrayList<>(38);
		//int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		private List<Tuple> cats = new ArrayList<>();
		private List<Tuple> mice = new ArrayList<>();
		private List<Tuple> cheese = new ArrayList<>();
		//public int turn = -1;

		private void generateUnique(List<Tuple> list, int num) {
			IntStream.range(0, num).forEach(i -> {
				while(true) {
					boolean unique = true;
					Tuple proposed = new Tuple(ThreadLocalRandom.current().nextInt(0, BOARD_SIZE),
							ThreadLocalRandom.current().nextInt(0, BOARD_SIZE));
					for(Tuple c: cats) {
						if(c.x == proposed.x && c.y == proposed.y) {
							unique = false;
							break;
						}
					}
					for(Tuple c: cheese) {
						if(c.x == proposed.x && c.y == proposed.y) {
							unique = false;
							break;
						}
					}
					for(Tuple c: mice) {
						if(c.x == proposed.x && c.y == proposed.y) {
							unique = false;
							break;
						}
					}

					if(unique) {
						list.add(proposed);
						break;
					}
				}
			});
		}

		public State() {
			if(RANDOMIZE) {
				generateUnique(cats, N_CATS);
				generateUnique(cheese, N_CHEESE);
				generateUnique(mice, N_MOUSE);
			} else {
				cats.add(new Tuple(2,6));
				mice.add(new Tuple(7,1));
				cheese.add(new Tuple(9,1));
				cheese.add(new Tuple(9,6));
				cheese.add(new Tuple(6,10));

				//				cats.add(new Tuple(5, 1));
				//				mice.add(new Tuple(7, 1));
				//				cheese.add(new Tuple(0, 0));
				//				cheese.add(new Tuple(8,7));
				//				cheese.add(new Tuple(10,7));
			}
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
			//this.turn = other.turn;
			for(Tuple c: other.cheese) {
				this.cheese.add(new Tuple(c.x, c.y));
			}
			for(Move m: other.moves) {
				this.moves.add(new Move(m.result));
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
						System.out.print("c ");
					} else if(containsThing(mice, i, j)) {
						System.out.print("m ");
					} else if(containsThing(cheese, i, j)) {
						System.out.print("x ");
					} else {
						System.out.print("_ ");
					}
				}
				System.out.println();
			}
			System.out.println("Mice: " + this.mice);
			System.out.println("Cats: " + this.cats);
			System.out.println("Cheese: " + this.cheese);
			System.out.println("\n\n\n");
		}

		private void simulateMice(State s) {
			for(Tuple m: s.mice) {
				Tuple closest = null;
				for(Tuple c: s.cheese) {
					if(closest == null) {
						closest = c;
					}

					if(Math.pow(Math.abs(c.x - m.x), 2) +
							Math.pow(Math.abs(c.y - m.y), 2) <
							Math.pow(Math.abs(closest.x - m.x), 2) +
							Math.pow(Math.abs(closest.y - m.y), 2)){
						closest = c;
					}
				}

				if(closest == null) {
					break;
				}

				Tuple src = m;
				Tuple dest = closest;
				int diagnol = Math.min(Math.abs(src.x - dest.x),
						Math.abs(src.y - dest.y)); // min(3, 4) == 3
				int x = Math.abs(src.x - dest.x) - diagnol > 0 ? Math.abs(src.x - dest.x) - diagnol : 0; // 3 - 3 == 0
				int y = Math.abs(src.y - dest.y) - diagnol > 0 ? Math.abs(src.y - dest.y) - diagnol : 0; // 4 - 3 == 1

				int itr = 0;
				while(itr < MOUSE_MOVE) {
					if(diagnol > 0) {
						m.x = ((int)(src.x + Math.copySign(1, dest.x - src.x)));
						m.y = ((int)(src.y + Math.copySign(1, dest.y - src.y)));
						diagnol--;
					} else if(x > 0) {
						m.x = ((int)(src.x + Math.copySign(1, dest.x - src.x)));
						m.y = src.y;
						x--;
					} else if(y > 0) {
						m.x = src.x;
						m.y = ((int)(src.y + Math.copySign(1, dest.y - src.y)));
						y--;
					} 
					itr++;
				}

			}
			return;
		}

		public State performMove(Move m) {
			State newState = new State(this);
			//newState.cat.x = m.result.x;
			//newState.cat.y = m.result.y;

			for(int i = 0; i < m.result.size(); i++) {
				newState.cats.get(i).x = m.result.get(i).x;
				newState.cats.get(i).y = m.result.get(i).y;
			}

			newState.moves.add(m);

			//newState.turn++;
			//newState.mouse = mouseMoves.get(newState.turn).result;

			simulateMice(newState);

			List<Tuple> notCaughtMice = new ArrayList<>();
			for(Tuple mi: newState.mice) {
				boolean caught = false;
				for(Tuple c: newState.cats) {
					if(c.x == mi.x && c.y == mi.y) {
						caught = true;
					}
				}
				if(!caught) {
					notCaughtMice.add(mi);
				}
			}

			newState.mice = notCaughtMice;
			for(Tuple c: cheese) {
				for(Tuple mi: newState.mice) {
					if(c.x == mi.x &&
							c.y == mi.y) {
						newState.cheese.remove(c);
						break;
					}	
				}
			}

			return newState;
		}

		public void printMoves() {
			System.out.println(moves);
			System.out.println(moves.size());
		}

		public boolean valid(List<Tuple> positions) {
			for(Tuple i: positions) {
				for(Tuple j: positions) {
					if(i != j && i.x == j.x && i.y == j.y) {
						return false;
					}
				}
			}
			return true;
		}

		public void generateMoves(List<List<Tuple>> possibleMoves, List<Tuple> curr, List<Move> move, int i, int j){

			if(i == possibleMoves.size()) {
				//System.out.println("valid" + curr + "" + valid(curr));
				if(valid(curr)) {
					move.add(new Move(curr));
				}
				return;
			}

			for(Tuple p: possibleMoves.get(i)) {
				curr.add(p);
				generateMoves(possibleMoves, curr, move, i + 1, j);
				curr.remove(curr.size() - 1);
			}
		}

		public List<Move> possibleMoves(){
			List<List<Tuple>> possibleMoves = new ArrayList<>();

			if(cheese.size() == 0) {
				return new ArrayList<>(); 
			}

			for(Tuple cat: this.cats) {
				possibleMoves.add(new ArrayList<>());
				for(int[] possibility: SHIFT) {
					//System.out.println("here");
					if(cat.x + possibility[0] >= 0 && cat.x + possibility[0] < BOARD_SIZE &&
							cat.y + possibility[1] >= 0 && cat.y + possibility[1] < BOARD_SIZE) {
						//System.out.println("adding");
						Tuple newPosition = new Tuple(cat.x + possibility[0], cat.y + possibility[1]);
						possibleMoves.get(possibleMoves.size() - 1).add(newPosition);
						//System.out.println("After: " + possibleMoves);
					}
				}
			}

			//System.out.println(possibleMoves);
			List<Move> moves = new ArrayList<>();
			generateMoves(possibleMoves, new ArrayList<Tuple>(), moves, 0, 0);
			//System.out.println("Moves generated: " + moves + " " + moves.size());
			return moves;


			//return possibleMoves;
		}

		public boolean goal() {
			//return cat.x == mouse.x && cat.y == mouse.y;
			return mice.size() == 0;
		}
	}

	public static class Move {

		//public final Tuple t1;
		//public final Tuple t2;
		public final List<Tuple> result;

		public Move(List<Tuple> result) {
			//this.t1 = t1;
			//this.t2 = t2;
			this.result = new ArrayList<>();
			for(Tuple c: result) {
				this.result.add(new Tuple(c.x, c.y));
			}
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


/**
Which search worked best?
	A*
Which heuristics did you use?
	To mouse current location/to mouse future location
Why did you choose these heuristics?
	intuitive
Does the combination of the two heuristics work better or worse than they do individually?
	About the same
How well do the searches work if you increase the size of the board to 30x30 or 50x50.
	DFS much worse, BFS worse, A* relatively agnostic
How many nodes are searched for each of the searches on average with respective deviation.(BFS, DFS, and A* )
	b: branching factor, d: depth of least cost solution, m: max depth
	DFS O(b^m)
	BFS O(b^(d+1))
	A* O(d) / exponential
What is the average number of moves required for each type of search with respective deviation. (BFS, DFS, and A* )
	DFS between m and d
	BFS d
	A* close to d
Which search works best if you increase the speed of the Mouse to two steps per turn? Three steps?
	BFS and A*
 */