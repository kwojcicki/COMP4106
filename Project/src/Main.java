import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

	static Random r = new Random();
	static final int MAX_ITERS = 50;
	static final int WEIGHTS = 20;

	static final int POPULATION_SIZE = 500;
	static final int TOP_K = 5;
	static final int PERCENTAGE = 80;

	static final int GAMES_TO_EVALUTE = 1000;

	static final boolean GA_ROULETTE_SELECTION = true;
	
	static final boolean FL_GA_GUASSIAN_MUTATION = true;

	static final int MC_SEARCH_ITERATIONS = 5;

	static final double Q_LR = 0.99;

	public static void main(String[] args) {
		qlearn();
		genetics();
		fuzzy_genetics(FL_GA_GUASSIAN_MUTATION);
		bestStrategy();
		mc();
	}

	public static void mc() {
		System.out.println("Method mc");
		Player dealer = new Dealer();
		Player player = new MCAgent(null);
		double score = Game.playGame(dealer, player, 10000, false, true);
		System.out.println(score);
	}

	public static void bestStrategy() {
		System.out.println("Method preset");
		Player dealer = new Dealer();
		Player player = new BestStrategy(null);
		double score = Game.playGame(dealer, player, 100000, false, true);
		System.out.println(score);
	}

	public static void qlearn() {
		System.out.println("Method QLearn");
		Player dealer = new Dealer();
		Player player = new QLearnAgent(null);
		double score = Game.playGame(dealer, player, 100000, false, true);
		System.out.println(score);
	}

	public static void fuzzy_genetics(boolean guassian) {
		System.out.println("Method Fuzzy Genetics");
		double[][] populations = new double[POPULATION_SIZE][WEIGHTS * 2];
		for(int i = 0; i < populations.length; i++) {
			populations[i] = r.doubles(WEIGHTS * 2, 0, 1).toArray();
		}

		for(int i = 0; i < MAX_ITERS; i++) {
			PriorityQueue<Tuple<double[], Double>> q = new PriorityQueue<>();
			double averageFitness = 0;

			for(double[] population: populations) {
				Player dealer = new Dealer();
				Player player = new FuzzyAgent(population);
				double score = Game.playGame(dealer, player, GAMES_TO_EVALUTE, false, false);
				averageFitness += score;
				q.add(new Tuple<>(population, score));	
			}

			System.out.println("Generation: " + i + " average fitness " + (averageFitness/POPULATION_SIZE));

			if(i == MAX_ITERS - 1) {
				observeFuzzy(q.peek().key);
				Game.playGame(new Dealer(), new FuzzyAgent(q.peek().key), 100000, false, true);
				break;
			}

			double[][] matingPool = new double[TOP_K][WEIGHTS * 2];
			for(int k = 0; k < TOP_K; k++) {
				matingPool[k] = q.poll().key;
			}

			populations = crossoverAndMutateFuzzy(matingPool, guassian);
		}
	}

	public static void genetics() {
		System.out.println("Method Genetics");
		int[][] populations = new int[POPULATION_SIZE][WEIGHTS];
		for(int i = 0; i < populations.length; i++) {
			populations[i] = r.ints(WEIGHTS, 0, 2).toArray();
		}

		for(int i = 0; i < MAX_ITERS; i++) {
			PriorityQueue<Tuple<int[], Double>> q = new PriorityQueue<>();
			double averageFitness = 0;

			for(int[] population: populations) {
				Player dealer = new Dealer();
				Player player = new Agent(population);
				double score = Game.playGame(dealer, player, GAMES_TO_EVALUTE, false, false);
				averageFitness += score;
				q.add(new Tuple<>(population, score));	
			}

			System.out.println("Generation: " + i + " average fitness " + (averageFitness/POPULATION_SIZE));

			if(i == MAX_ITERS - 1) {
				observe(q.peek().key);
				Game.playGame(new Dealer(), new Agent(q.peek().key), 100000, false, true);
				break;
			}

			int[][] matingPool = new int[TOP_K][WEIGHTS];
			double[] matingScores = new double[TOP_K];
			for(int k = 0; k < TOP_K; k++) {
				Tuple<int[], Double> p = q.poll();
				matingPool[k] = p.key;
				matingScores[k] = p.value;
			}

			populations = crossoverAndMutate(matingPool, matingScores);

		}
	}

	public static void observe(int[] population) {
		Player dealer = new Dealer();
		Player player = new Agent(population);
		Game.playGame(dealer, player, 10, true, true);
	}

	public static void observeFuzzy(double[] population) {
		Player dealer = new Dealer();
		Player player = new FuzzyAgent(population);
		Game.playGame(dealer, player, 10, true, true);
	}

	public static double[] crossoverFuzzy(double[] p1, double[] p2) {
		double[] result = new double[p1.length];

		int splitPoint = Deck.getRandomNumberInRange(0, p1.length - 1);
		for(int i = 0; i < p1.length; i++) {
			if(i > splitPoint) {
				result[i] = p1[i]; 
			} else {
				result[i] = p2[i];
			}
		}

		return result;
	}

	public static int[] crossover(int[] p1, int[] p2) {
		int[] result = new int[p1.length];

		int splitPoint = Deck.getRandomNumberInRange(0, p1.length - 1);
		for(int i = 0; i < p1.length; i++) {
			if(i > splitPoint) {
				result[i] = p1[i]; 
			} else {
				result[i] = p2[i];
			}
		}

		return result;
	}

	public static void mutate(int[] population) {
		for(int i = 0; i < population.length; i++) {
			if(Deck.getRandomNumberInRange(1, 100) > PERCENTAGE) {
				population[i] = 1 - population[i];
			}
		}
	}

	public static void mutateFuzzy(double[] population, boolean guassian) {
		for(int i = 0; i < population.length; i++) {
			if(Deck.getRandomNumberInRange(1, 100) > PERCENTAGE) {
				if(guassian) {
					Math.min(Math.max(0, r.nextGaussian()), 0.9999999999);
				} else {
					population[i] = r.nextDouble();	
				}
			}
		}
	}


	public static int[][] crossoverAndMutate(int[][] matingPool, double[] matingScores){
		int[][] populations = new int[POPULATION_SIZE][WEIGHTS];

		for(int i = 0; i < matingPool.length; i++) {
			populations[i] = matingPool[i];
		}

		double totalFitness = Arrays.stream(matingScores).sum(); 
		
		for(int i = matingPool.length; i < POPULATION_SIZE; i++) {
			int p1 = 0, p2 = 0;
			if(GA_ROULETTE_SELECTION) {
				while(p1 == p2) {
					double random1 = Deck.getRandomDoubleInRange(0, totalFitness);
					double random2 = Deck.getRandomDoubleInRange(0, totalFitness);
					p1 = -1; p2 = -1;
					while(random1 > 0 && p1 < matingScores.length) {
						p1++;
						random1 -= matingScores[p1];
					}
					while(random2 > 0 && p2 < matingScores.length) {
						p2++;
						random2 -= matingScores[p2];
					}
				}
			} else {
				while(p1 == p2) {
					p1 = Deck.getRandomNumberInRange(0, matingPool.length - 1);
					p2 = Deck.getRandomNumberInRange(0, matingPool.length - 1);
				}
			}

			int[] child = crossover(matingPool[p1], matingPool[p2]);
			mutate(child);

			populations[i] = child;

		}

		return populations;
	}

	public static double[][] crossoverAndMutateFuzzy(double[][] matingPool, boolean guassian){
		double[][] populations = new double[POPULATION_SIZE][WEIGHTS * 2];

		for(int i = 0; i < matingPool.length; i++) {
			populations[i] = matingPool[i];
		}

		for(int i = matingPool.length; i < POPULATION_SIZE; i++) {
			int p1 = 0, p2 = 0;
			while(p1 == p2) {
				p1 = Deck.getRandomNumberInRange(0, matingPool.length - 1);
				p2 = Deck.getRandomNumberInRange(0, matingPool.length - 1);
			}

			double[] child = crossoverFuzzy(matingPool[p1], matingPool[p2]);
			mutateFuzzy(child, guassian);

			populations[i] = child;

		}

		return populations;
	}

}

class Tuple<K, V extends Comparable<V>> implements Comparable<Tuple<K, V>>{
	final K key;
	final V value;

	public Tuple(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(Tuple<K, V> o) {
		return o.value.compareTo(this.value);
	}
}

class Game {

	public static void print(String s, boolean debug) {
		if(debug) {
			System.out.println(s);
		}
	}

	public static double playGame(Player dealer, Player player, int gamesToPlay, boolean debug, boolean printResult) {
		int won = 0, tied = 0, lost = 0;
		Deck deck = new Deck();
		OUTER_LOOP: for(int i = 0; i < gamesToPlay; i++) {
			print("--------", debug);
			deck.resetDeck();
			player.cards.clear(); dealer.cards.clear();

			dealer.cards.add(deck.getCard());
			dealer.cards.add(deck.getCard());

			player.cards.add(deck.getCard());
			player.cards.add(deck.getCard());

			print("Player: " + player.toString(), debug);
			print("Dealer: " + dealer.toString(), debug);

			if(player.total() > 21) {
				lost++;
				print("Player score over 21", debug);
				continue;
			} else if(player.total() == 21 && dealer.total() != 21) {
				won++;
				print("Player score equal to 21", debug);
				continue;
			}

			List<Tuple<Integer, Integer>> states = new ArrayList<>();
			while(true) {
				if(player.hit(dealer)) {
					states.add(new Tuple<>(player.total(), 1));
					player.cards.add(deck.getCard());
					print("Player hit:" + player.toString(), debug);
					if(player.total() > 21) {
						player.update(-1, states);
						lost++;
						print("Player score over 21", debug);
						continue OUTER_LOOP;
					}	
				} else {
					states.add(new Tuple<>(player.total(), 0));
					break;
				}
			}

			while(dealer.hit(dealer)) {
				dealer.cards.add(deck.getCard());
				print("Dealer hit:" + dealer.toString(), debug);
				if(dealer.total() > 21) {
					won++;
					print("Dealer score over 21", debug);
					player.update(1, states);
					continue OUTER_LOOP;
				}
			}

			if(dealer.total() > player.total()) {
				lost++;
				player.update(-1, states);
			} else if(dealer.total() < player.total()) {
				player.update(1, states);
				won++;
			} else {
				player.update(0, states);
				tied++;
			}
		}

		if(printResult) {
			System.out.println("Won: " + won + " Lost: " + lost + " Tied: " + tied + " Out of total: " + (won + lost + tied));	
		}
		return (won / (double)(won + lost + tied));
	}
}

class BestStrategy extends Player {

	public BestStrategy(int[] weights) {}

	public boolean hit(Player dealer) {
		// https://en.wikipedia.org/wiki/Blackjack
		if(this.total() == 16 && dealer.cards.get(0).value >= 7) {
			return true;
		}
		if(this.total() == 15 && dealer.cards.get(0).value >= 7) {
			return true;
		}
		if(this.total() == 14 && dealer.cards.get(0).value >= 7) {
			return true;
		}
		if(this.total() == 13 && dealer.cards.get(0).value >= 7) {
			return true;
		}
		if(this.total() == 12 && dealer.cards.get(0).value >= 7) {
			return true;
		}
		if(this.total() == 16 && dealer.cards.get(0).value <= 3) {
			return true;
		}

		if(this.total() <= 11) {
			return true;
		}

		return false;
	}

}

class MCAgent extends Player {
	Random random = new Random();
	public MCAgent(int[] weights) {}

	public int score(Player player, Player dealer) {

		if(player.total() > 21) {
			return -1;
		}

		if(dealer.total() > 21) {
			return 1;
		}

		if(dealer.total() > player.total()) {
			return -1;
		} else if(dealer.total() < player.total()) {
			return 1;
		}
		return 0;
	}

	public boolean hit(Player dealer, int depth) {

		if(this.total() > 21) {
			return false;
		}
		//System.out.println("hit");
		int numCards = this.cards.size();
		int dealerCards = dealer.cards.size();
		int totalScoreHit = 0;
		int totalScoreStand = 0;
		Deck deck = new Deck();
		// simulate hits

		for(int i = 0; i < Main.MC_SEARCH_ITERATIONS; i++) {
			deck.resetDeck();
			// hit
			if(random.nextBoolean()) {

				this.cards.add(deck.getCard());
				while(this.hit(dealer, 1)) {
					this.cards.add(deck.getCard());
				}
				while(dealer.hit(dealer)) {
					dealer.cards.add(deck.getCard());
				}
				int score = score(this, dealer);

				totalScoreHit += score;

				while(this.cards.size() != numCards) {
					this.cards.remove(this.cards.size() - 1);
				}
				while(dealer.cards.size() != dealerCards) {
					dealer.cards.remove(dealer.cards.size() - 1);
				}
			} 
			// stand
			else {
				Player dealer1 = new Dealer();

				// TODO: set first card the same

				while(dealer1.hit(dealer1)) {
					dealer1.cards.add(deck.getCard());
				}
				int score = score(this, dealer1);
				totalScoreStand += score;
			}
		}

		//		for(int i = 0; i < 20; i++) {
		//			deck.resetDeck();
		//			this.cards.add(deck.getCard());
		//			while(this.hit(dealer, 1)) {
		//				this.cards.add(deck.getCard());
		//			}
		//			while(dealer.hit(dealer)) {
		//				dealer.cards.add(deck.getCard());
		//			}
		//			int score = score(this, dealer);
		//			
		//			totalScoreHit += score;
		//			
		//			//this.cards = this.cards.subList(0, numCards);
		//			//dealer.cards = dealer.cards.subList(0, dealerCards);
		//			while(this.cards.size() != numCards) {
		//				this.cards.remove(this.cards.size() - 1);
		//			}
		//			while(dealer.cards.size() != dealerCards) {
		//				dealer.cards.remove(dealer.cards.size() - 1);
		//			}
		//		}
		//		
		//		// simulate stands
		//		for(int i = 0; i < 100; i++) {
		//			deck.resetDeck();
		//			Player dealer1 = new Dealer();
		//
		//			while(dealer1.hit(dealer1)) {
		//				dealer1.cards.add(deck.getCard());
		//			}
		//			int score = score(this, dealer1);
		//			totalScoreStand += score;
		//		}

		//if(depth == 0) {
		//System.out.println("Result: " + totalScoreHit + " " + totalScoreStand);	
		//}

		return totalScoreHit > totalScoreStand;
	}

	public boolean hit(Player dealer) {
		return hit(dealer, 0);
	}

}

class QLearnAgent extends Player {
	double[][] weights = new double[22][2];
	double lr = Main.Q_LR;

	public QLearnAgent(int[] weights) {
		for(int i = 0; i < this.weights.length; i++) {
			this.weights[i][0] = 0;
			this.weights[i][1] = 0;
		}
	}

	public boolean hit(Player dealer) {
		if(weights[total()][1] > weights[total()][0]) {
			return true;
		}

		return false;
	}

	/**
	 * won = 1, lost = -1, tied = 0
	 * states <total, action> 1 for hit 0 for stay
	 * @param won
	 * @param states
	 */
	@Override
	public void update(int won, List<Tuple<Integer, Integer>> states) {
		double reward = won;
		for(int i = states.size() - 1; i >= 0; i--) {
			int state = states.get(i).key;
			int action = states.get(i).value;
			reward = this.weights[state][action] + this.lr * (reward - this.weights[state][action]);
			this.weights[state][action] = reward;
		}
	}

}

class FuzzyAgent extends Player {
	final double[] weights;
	Random random = new Random();

	public FuzzyAgent(double[] weights) {
		this.weights = weights;
	}


	public boolean hit(Player dealer) {
		double hit = random.nextDouble();
		double stand = random.nextDouble();

		try {
			double hitProb = weights[total() * 2 - 4];
			double standProb = weights[total() * 2 - 3];

			if(hit > hitProb && stand > standProb) {
				return random.nextBoolean();
			} else if(hit > hitProb) {
				return true;
			} else if(stand > standProb) {
				return false;

			}
		} catch(Exception e) {
			System.out.println(this.toString());
			System.out.println(weights.length);
			throw e;
		}

		return random.nextBoolean();
	}
}

class Agent extends Player{
	final int[] weights;

	public Agent(int[] weights) {
		this.weights = weights;
	}

	public boolean hit(Player dealer) { 
		try {
			return weights[total() - 2] == 1;
		} catch(Exception e) {
			System.out.println(this.toString());
		}
		throw new RuntimeException();
	}
}

abstract class Player {
	List<Card> cards = new ArrayList<>();
	public abstract boolean hit(Player dealer);

	public void update(int won, List<Tuple<Integer, Integer>> states) {

	}

	@Override
	public String toString() {
		return cards.toString() + " " + total();
	}

	public int total() {
		return cards.stream().mapToInt(i -> i.value).sum();
	}
}

class Deck {
	List<Card> cards = new ArrayList<>(52);

	public Deck() {
		resetDeck();
	}

	public Card getCard() {
		return cards.remove(getRandomNumberInRange(0, cards.size() - 1));
	}

	public static double getRandomDoubleInRange(double min, double max) {
		return Main.r.doubles(min, max).findFirst().getAsDouble();
	}

	public static int getRandomNumberInRange(int min, int max) {
		return Main.r.ints(min, (max + 1)).findFirst().getAsInt();

	}

	public void resetDeck() {
		cards = new ArrayList<>(52);
		for(int i = 1; i <= 10; i++) {
			cards.add(new Card(i, Suit.HEART));
			cards.add(new Card(i, Suit.SPADE));
			cards.add(new Card(i, Suit.CLUB));
			cards.add(new Card(i, Suit.DIAMOND));
		}

		for (Face face : Face.values()) { 
			cards.add(new Card(face, Suit.HEART));
			cards.add(new Card(face, Suit.SPADE));
			cards.add(new Card(face, Suit.CLUB));
			cards.add(new Card(face, Suit.DIAMOND));
		}
	}
}

class Dealer extends Player {
	public boolean hit(Player dealer) {
		return cards.stream().mapToInt(i -> i.value).sum() < 17;
	}
}

class Card {
	final int value;
	final Suit suit;
	final Face face;

	public Card(int value, Suit suit) {
		this.value = value;
		this.suit = suit;
		this.face = null;
	}

	public Card(Face face, Suit suit) {
		this.value = 10;
		this.suit = suit;
		this.face = face;
	}

	@Override
	public String toString() {
		return "[" + suit + ", " + value + "]";
	}
}

enum Face {
	JACK, QUEEN, KING, ACE 
}

enum Suit {
	HEART, SPADE, CLUB, DIAMOND
}