import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

public class A1 {

	//private static List<Move> mouseMoves;

	public static void main(String[] args) {
		State s = new State();
		s.printBoard();

		System.out.println(s.possibleMoves());

		//s = s.performMove(s.possibleMoves().get(2));

		//s.printBoard();


		//generateMouseMoves(s);

		//System.out.println("Mouse path: " + mouseMoves + " " + mouseMoves.size());

		Node finalState = null;
		// finalState = dfs(s);
		finalState = bfs(s);

		final Function<State, Double> heuristic1 = (state) -> {
			return (Math.pow(state.mice.get(0).x - state.cats.get(0).x, 2) +
					Math.pow(state.mice.get(0).y - state.cats.get(0).y, 2)); 
		};
		final Function<State, Double> heuristic2 = (state) -> {
			double distance = (Math.pow(state.mice.get(0).x - state.cats.get(0).x, 2) +
					Math.pow(state.mice.get(0).y - state.cats.get(0).y, 2));

			double catVelocity = Math.pow(2, 1) + Math.pow(1, 1);


			//if(state.turn + (int)(distance/catVelocity) >= mouseMoves.size()) {
			//				return Double.MAX_VALUE;
			//}

			int futureSteps = (int)(distance/catVelocity);
			State n = new State(state);
			for(int i = 0; i < futureSteps; i++) {
				n.simulateMice(n);
				n = new State(n);
				if(n.goal()) {
					return Double.MAX_VALUE;
				}
			}

			Tuple mouseLocation = n.mice.get(0);
			return (Math.pow(mouseLocation.x - state.cats.get(0).x, 2) +
					Math.pow(mouseLocation.y - state.cats.get(0).y, 2));
		};

		final Function<State, Double> heuristic3 = (state) -> {
			return (heuristic1.apply(state) + heuristic2.apply(state))/2.0;
		};
		//
		//
		//Astar(s, heuristic1);
		//Astar(s, heuristic2);
		//Astar(s, heuristic3);
		
		System.out.println("\n\n-----------");
		while(finalState != null) {
			finalState.state.printBoard();
			finalState.state.printMoves();
			
			finalState = finalState.parent;
		}
	}

	private static Node bfs(State original) {
		Queue<Node> states = new LinkedList<>();

		states.add(new Node(original));
		original.printBoard();

		while(!states.isEmpty()) {
			Node e = states.poll();
			List<Move> moves = e.state.possibleMoves();
			for(Move m: moves) {
				Node post = new Node(e.state.performMove(m));
				post.parent = e;
				if(post.state.goal()) {
					post.state.printBoard();
					post.state.printMoves();
					System.out.println("Complete");
					return post;
				}
				states.add(post);
			}
		}

		System.out.println("No possible solutions");
		return null;
	}

	private static Node Astar(State original, Function<State, Double> heuristic){
		Comparator<Node> comparator = 
				(Node o1, Node o2) -> heuristic.apply(o1.state).compareTo(heuristic.apply(o2.state));

				PriorityQueue<Node> states = new PriorityQueue<>(comparator);
				states.add(new Node(original));

				while(!states.isEmpty()) {
					Node e = states.poll();
					List<Move> moves = e.state.possibleMoves();
					for(Move m: moves) {
						Node post = new Node(e.state.performMove(m));
						post.parent = e;
						if(post.state.goal()) {
							post.state.printBoard();
							post.state.printMoves();
							System.out.println("Complete");
							return post;
						}
						states.add(post);
					}
				}

				System.out.println("No possible solutions");
				return null;
	}

	private static Node dfs(State original) {
		Stack<Node> states = new Stack<>();

		states.add(new Node(original));

		while(!states.isEmpty()) {
			Node e = states.pop();
			List<Move> moves = e.state.possibleMoves();
			//System.out.println(moves);
			for(Move m: moves) {
				Node post = new Node(e.state.performMove(m));
				post.parent = e;
				if(post.state.goal()) {
					post.state.printBoard();
					post.state.printMoves();
					System.out.println("Complete");
					return post;
				}
				states.push(post);
			}
		}

		System.out.println("No possible solutions");
		return null;
	}

	public static class Node {
		private final State state;
		private Node parent = null;
		public Node(State state) {
			this.state = state;
		}
	}

	public static class State {

		private static int[][] SHIFT = {{1,2}, {2,1}, {2, -1}, {1, -2}, 
				{-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};

		private static final int BOARD_SIZE = 12;
		private static final boolean RANDOMIZE = true;
		private static final int N_CATS = 1;
		private static final int N_MOUSE = 2;
		private static final int N_CHEESE = 3;
		private static final int MOUSE_MOVE = 1;
		private List<Move> moves = new ArrayList<>(38);
		int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		private List<Tuple> cats = new ArrayList<>();
		private List<Tuple> mice = new ArrayList<>();
		private List<Tuple> cheese = new ArrayList<>();
		public int turn = -1;

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
			this.turn = other.turn;
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

			newState.turn++;
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
