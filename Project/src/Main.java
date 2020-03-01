import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

	static Random r = new Random();
	static final int MAX_ITERS = 1000;
	static final int POPULATION_SIZE = 200;
	static final int WEIGHTS = 20;
	static final int TOP_K = 10;
	static final int PERCENTAGE = 90;
	static final int GAMES_TO_EVALUTE = 1000;
	
	public static void main(String[] args) {
		
		
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
				double score = Game.playGame(dealer, player, GAMES_TO_EVALUTE, false);
				averageFitness += score;
				q.add(new Tuple<>(population, score));	
			}
			
			System.out.println("Generation: " + i + " average fitness " + (averageFitness/POPULATION_SIZE));
			
			if(i == MAX_ITERS - 1) {
				observe(q.peek().key);
				break;
			}
			
			int[][] matingPool = new int[TOP_K][WEIGHTS];
			for(int k = 0; k < TOP_K; k++) {
				matingPool[k] = q.poll().key;
			}
			
			populations = crossoverAndMutate(matingPool);
			
		}
	}
	
	public static void observe(int[] population) {
		Player dealer = new Dealer();
		Player player = new Agent(population);
		Game.playGame(dealer, player, 10, true);
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
	
	public static int[][] crossoverAndMutate(int[][] matingPool){
		int[][] populations = new int[POPULATION_SIZE][WEIGHTS];
		
		for(int i = 0; i < matingPool.length; i++) {
			populations[i] = matingPool[i];
		}
		
		for(int i = matingPool.length; i < POPULATION_SIZE; i++) {
			int p1 = 0, p2 = 0;
			while(p1 == p2) {
				p1 = Deck.getRandomNumberInRange(0, matingPool.length - 1);
				p2 = Deck.getRandomNumberInRange(0, matingPool.length - 1);
			}
			
			int[] child = crossover(matingPool[p1], matingPool[p2]);
			mutate(child);
			
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
	
	public static double playGame(Player dealer, Player player, int gamesToPlay, boolean debug) {
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
			
			while(player.hit()) {
				player.cards.add(deck.getCard());
				print("Player hit:" + player.toString(), debug);
				if(player.total() > 21) {
					lost++;
					print("Player score over 21", debug);
					continue OUTER_LOOP;
				}
			}
			
			while(dealer.hit()) {
				dealer.cards.add(deck.getCard());
				print("Dealer hit:" + dealer.toString(), debug);
				if(dealer.total() > 21) {
					won++;
					print("Dealer score over 21", debug);
					continue OUTER_LOOP;
				}
			}
			
			if(dealer.total() > player.total()) {
				lost++;
			} else if(dealer.total() < player.total()) {
				won++;
			} else {
				tied++;
			}
		}
		
		//System.out.println("Won: " + won + " Lost: " + lost + " Tied: " + tied + " Out of total: " + (won + lost + tied));
		return (won / (double)(won + lost + tied));
	}
}

class Agent extends Player{
	final int[] weights;
	
	public Agent(int[] weights) {
		this.weights = weights;
	}
	
	public boolean hit() { 
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
	public abstract boolean hit();
	
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
	public boolean hit() {
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