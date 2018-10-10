/**
 * CS 4200
 * Professor: D. Atanasio
 *
 * Project #2 - simulatedAnnealingTest
 *
 *	Uses Genetic Algorithm to solve a NQueen Board.  
 *
 * @author Eric Schenck
 * last modified: 10/11/18
 */

import java.util.Random;


public class simulatedAnnealingTest {
	
	
	private final double SCHEDULE = 0.9997;					// mapping from time to "temperature"
	private final double TEMPERATURE = 1.0;				// a "Temperature" controlling probability of downward steps
	private final double MIN_TEMP = 0.001;
	private final int NUMBER_OF_TESTS = 500;
	private double totalSearchCost = 0;
	private double totalRunTime;
	private int maxFitness;
	private int gameSize; 
	private int failedPuzzleCount;
	private double avgRunTime;
	private double avgSearchCost;
	private double percentSolved;
	private double searchCost;
	
	NQueenState current;
	NQueenState next;
	
	public simulatedAnnealingTest(int gameSize) {
		this.gameSize = gameSize;		
		
		maxFitness = gameSize - 1;							// calculating maximum fitness level
		maxFitness /= 2;
		maxFitness = (int)(gameSize * maxFitness);
		
		totalSearchCost = 0;
		totalRunTime = 0;
		
		runTest();
	}
	

	public void runTest() {
		System.out.println("Simulated Annealing Solutions:");
		
		System.out.println("1) " + formatFinalGameboard(simulatedAnnealing()) + " Fitness: " + current.getFitness());
		System.out.println("2) " + formatFinalGameboard(simulatedAnnealing()) + " Fitness: " + current.getFitness());
		System.out.println("3) " + formatFinalGameboard(simulatedAnnealing()) + " Fitness: " + current.getFitness());
		
		final double startTime = System.currentTimeMillis();
		
		for(int i = 0; i < NUMBER_OF_TESTS; ++i) {
			simulatedAnnealing();
			
		}
		final double stopTime = System.currentTimeMillis();
		
		totalRunTime = stopTime - startTime;
		
		
		
		avgRunTime = totalRunTime / (NUMBER_OF_TESTS - failedPuzzleCount);
		avgSearchCost = totalSearchCost / (NUMBER_OF_TESTS - failedPuzzleCount);
	
		percentSolved = (1.0 * failedPuzzleCount / NUMBER_OF_TESTS) * 100 ;
		
		
		System.out.printf("Average RunTime = %.3f milliSeconds\n", avgRunTime);
		System.out.printf("Average Search Cost = %.1f%n", avgSearchCost);
		System.out.println("Percentage Solved = " + (100.0 - percentSolved) + "%"); 
		
		
		
		
	}
	
	// used to format gameboard into a string for printout
		public String formatFinalGameboard( int[] solution ) {
			String str = "[";
			
			for(int i = 0; i < solution.length; ++i) {
				str += solution[i];
				
				if(i < solution.length - 1) {
					str += ", ";
				}
			}
			str += "]";
			
			return str;
		}
	
	
	/**
	 * creates a single random Queen state in the form of int[] 
	 * @return
	 */	
	public int[] createRandomQueenState() {
		int [] state = new int[gameSize];
		
		Random rand = new Random();
		
		for(int i = 0; i < gameSize ; ++i) {
			state[i] = (rand.nextInt(gameSize) + 1);
			state[i] = 1;
		}
		return state;
	}
	

	
	boolean timeOver;
	
	public int[] simulatedAnnealing() {
		
		searchCost = 0;
		
		double temperature = TEMPERATURE;										// used to reset the temperature 
		
		double deltaE;															// used to store the change in fitness
		
		current = new NQueenState(createRandomQueenState());					// initial gameboard
		NQueenState next;														// used to hold successor of current
		
		while(temperature > MIN_TEMP) {													
			
			++searchCost;
			
			temperature *=  SCHEDULE;
			
			next = getNextState(current);										// getting next successor state
			
			deltaE = (next.getFitness() - current.getFitness());				// getting change in fitness
		
			if(deltaE > 0) {													// swapping if deltaE > 0 
				current = next;
			}
			else {
				
				double probability = Math.exp(deltaE/temperature) * 1;
	
				if(Math.random() < (probability)) {
					current = next;
				}
			}
		}
		
		if(current.getFitness() == maxFitness) {
			totalSearchCost += searchCost;
		}else
			++failedPuzzleCount;
		
		return current.getState();
	}
	
	
	
	
	
	public NQueenState getNextState(NQueenState current) {
		
		Random rand = new Random();
		
		int[] successor = new int[gameSize];
		for(int i = 0; i < gameSize; ++i) {
			successor[i] = current.getState()[i];
		}
		
		successor[rand.nextInt(gameSize)] = rand.nextInt(gameSize) + 1;
		
		return new NQueenState(successor);
		
	}
		
}
