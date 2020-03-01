import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

	static Random r = new Random();
	static final int MAX_ITERS = 100;
	static final int POPULATION_SIZE = 100;
	static final int WEIGHTS = 19;
	static final int TOP_K = 10;
	public static void main(String[] args) {
		
		
		int[][] populations = new int[POPULATION_SIZE][WEIGHTS];
		for(int i = 0; i < populations.length; i++) {
			populations[i] = r.ints(WEIGHTS, 0, 2).toArray();
		}
		
		
		for(int i = 0; i < MAX_ITERS; i++) {
			PriorityQueue<Tuple<int[], Double>> q = new PriorityQueue<>();
			
			for(int[] population: populations) {
				Player dealer = new Dealer();
				Player player = new Agent(population);
				q.add(new Tuple<>(population, Game.playGame(dealer, player)));	
			}
			
			for(int k = 0; i < TOP_K; i++) {
				
			}
		}
		
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
	
	public static double playGame(Player dealer, Player player) {
		int won = 0, tied = 0, lost = 0;
		Deck deck = new Deck();
		OUTER_LOOP: for(int i = 0; i < 1; i++) {
			deck.resetDeck();
			player.cards.clear(); dealer.cards.clear();
			
			dealer.cards.add(deck.getCard());
			dealer.cards.add(deck.getCard());
			
			player.cards.add(deck.getCard());
			player.cards.add(deck.getCard());
			
			if(player.total() > 21) {
				lost++;
				continue;
			} else if(player.total() == 21) {
				won++;
				continue;
			}
			
			while(player.hit()) {
				player.cards.add(deck.getCard());
				if(player.total() > 21) {
					lost++;
					continue OUTER_LOOP;
				}
			}
			
			while(dealer.hit()) {
				dealer.cards.add(deck.getCard());
				if(dealer.total() > 21) {
					won++;
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
		
		System.out.println("Won: " + won + " Lost: " + lost + " Tied: " + tied + " Out of total: " + (won + lost + tied));
		return (lost / (double)(won + lost + tied));
	}
}

class Agent extends Player{
	final int[] weights;
	
	public Agent(int[] weights) {
		this.weights = weights;
	}
	
	public boolean hit() {
		return weights[total() - 2] == 1;
	}
}

abstract class Player {
	List<Card> cards = new ArrayList<>();
	public abstract boolean hit();
	
	@Override
	public String toString() {
		return cards.toString();
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
	
	private static int getRandomNumberInRange(int min, int max) {
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