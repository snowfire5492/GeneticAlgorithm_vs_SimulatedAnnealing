/**
 * CS 4200
 * Professor: D. Atanasio
 *
 * Project #2 - geneticAlgorithmTest.java
 *
 *	Uses Genetic Algorithm to solve a NQueen Board.  
 *
 * @author Eric Schenck
 * last modified: 10/8/18
 */


import java.util.Random;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class geneticAlgorithmTest {

	private final double ELITE_PERCENT = 0.2; //  MAY NEED TO PLAY WITH THESE VALUES FOR OPTIMALITY
	private final int POPULATION_SIZE = 20;
	private final long TIME_LIMIT = 1000 * 10;						// 1000 ms -> 20 sec
	private final int MUTATION_LEVEL = 20;							// 20% mutation level 
	private final int NUMBER_OF_TESTS = 600;
	
	
	private double totalSearchCost;
	private double avgSearchCost;
	private double totalRunTime;
	private double avgRunTime;
	private double maxFitness;
	private int searchCost;
	private int gameSize;
	private double percentSolved;
	private int failedPuzzleCount;
	private int elitePopulationSize;
	private boolean withinTimeLimit = true;
	
	PriorityQueue<NQueenState> population = new PriorityQueue<>();
	
	
	/**
	 * constructor
	 */
	public geneticAlgorithmTest(int gameSize) {
		this.gameSize = gameSize;
		
		
		maxFitness = gameSize - 1;							// calculating maximum fitness level
		maxFitness /= 2;
		maxFitness = (int)(gameSize * maxFitness);
		
		totalSearchCost = 0;
		totalRunTime = 0;
		
		runTest();											
	}
	
	
	/**
	 * this is the function that runs the entire test. used to repeat algorithm >500 times and measure
	 * percentage of solved problems, search costs and the average running time
	 * 
	 */
	public void runTest() {
		
		System.out.println("Genetic Sample Solutions: \n1) " + formatFinalGameboard(geneticAlgorithm()) 
			+ "  Fitness = " + population.peek().getFitness());
		population.clear();
		System.out.println("2) " + formatFinalGameboard(geneticAlgorithm()) + "  Fitness = " + population.peek().getFitness());
		population.clear();
		System.out.println("3) " + formatFinalGameboard(geneticAlgorithm()) + "  Fitness = " + population.peek().getFitness());		
		
		final double startTime = System.currentTimeMillis();
		
		for(int i = 0; i < NUMBER_OF_TESTS; ++i) {
			geneticAlgorithm();
			population.clear();
			//System.out.println("Failed Puzzle Count = " + failedPuzzleCount); // for testing
		}
		final double stopTime = System.currentTimeMillis();
		
		totalRunTime = stopTime - startTime;
		totalRunTime -= (failedPuzzleCount * TIME_LIMIT);
		
		
		
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
	 * performs crossover in which two parents are taken, a "crossoverPoint" is chosen
	 * and the DNA is spliced into one single strain which will become a child NQueenState
	 * @param mother
	 * @param father
	 * @return NQueenState child
	 */
	public NQueenState crossover(NQueenState mother, NQueenState father) {
				
		Random rand = new Random();
		int[] motherDNA;
		int[] fatherDNA;
		int[] childDNA = new int[gameSize];
		NQueenState child;
		
		if(mother.getState() != null && father.getState() != null) {
			motherDNA = mother.getState();
			fatherDNA = father.getState();
			
			int crossoverPoint = rand.nextInt(gameSize - 1) + 1;			// choosing a random crossoverPoint

			for(int i = 0; i < crossoverPoint; ++i) {
				childDNA[i] = motherDNA[i];
			}
			for(int i = crossoverPoint; i < gameSize; ++i) {
				childDNA[i] = fatherDNA[i];
			}
		}
		child = new NQueenState(childDNA);
		
		return child;								// returning child of crossover function
	}
	

	
	/**
	 * 			******************** MAY HAVE TO ADJUST CHANCE OF MUTATION - WE WILL SEE ********************
	 * 
	 * Used to mutate a single cell within a DNA sequence of a child. There is a small chance that this mutation will not occur
	 * @param child
	 */
	public void mutate(NQueenState child) {
		
		Random rand = new Random();										// used for integer random
		
		int cellToMutate;
		int chanceToMutate = rand.nextInt(100);
		
		if(chanceToMutate < MUTATION_LEVEL) {												
			
			int[] DNA = child.getState();
			
			cellToMutate = rand.nextInt(gameSize);						// picking random number [0 - (gameSize - 1)]
			
			DNA[cellToMutate] = rand.nextInt(gameSize) + 1;				// mutating cell in DNA sequence
		}
	}
	
	
	
	/**
	 * reproduce function performs the reproduction process
	 * @param mother 
	 * @param father
	 * @return NQueenState child
	 */
	public NQueenState reproduce(NQueenState mother, NQueenState father) {

		NQueenState child = crossover(mother, father);
	
		mutate(child);
		
		return child;
	}
	
	
	
	/**
	 * This is the primary function that searches for a solution
	 * @return int[] solution state
	 */
	public int[] geneticAlgorithm() {
		
		Timer geneticTimer = new Timer();			// Timer to keep track of time elapsed	
		searchCost = 0;
		
		Random rand = new Random();					// used to randomly choose parents for procreation
		
		int[] solution = new int[gameSize];			// used to store and return solution board
		NQueenState[] elitePopulation;				// used to hold elite population during the mass procreation
		PriorityQueue<NQueenState> tempQueue;		// used as a tempQueue to hold new_population
		
		generatePopulation();						// generating initial population
													// take the top % of the population and populate the next generation 
		elitePopulationSize = (int)(POPULATION_SIZE * ELITE_PERCENT);
		elitePopulation = new NQueenState[elitePopulationSize];	//initialize NQueen Array to store elitePopulation
		withinTimeLimit = true;
		repeat: while( withinTimeLimit ) {			// will repeat until break statement or time limit is reached
			
			geneticTimer.schedule(new TimerTask() {
				public void run() {
					withinTimeLimit = false;  		// too much time has elapsed so set false 
					searchCost = 0;
					++failedPuzzleCount;
					geneticTimer.cancel();
				}
			}, TIME_LIMIT);
				
			tempQueue = new PriorityQueue<>();		// creating a new PriorityQueue to add new_population
			
			for( int i = 0; i < elitePopulationSize; ++i) {				// gets Elite Citizens from population and place into a NQueen Array
				elitePopulation[i] = population.poll();
			}
			
			for(int i = 0; i < POPULATION_SIZE; ++i) {					// going through Elite citizens 
				
				int motherIndex = rand.nextInt(elitePopulationSize);
				int fatherIndex = rand.nextInt(elitePopulationSize);
				
				NQueenState mother = elitePopulation[motherIndex];					// getting first parent
				NQueenState father = elitePopulation[fatherIndex];  				// getting second parent
				
				if(motherIndex != fatherIndex) {									// making sure we dont have a hermaphrodite
					NQueenState child = reproduce(mother,father);
					tempQueue.add(child);								// adding child to tempQueue until all children are created and store
				}else if(motherIndex != elitePopulationSize - 1){		// 
					mother = elitePopulation[++motherIndex];
					NQueenState child = reproduce(mother,father);
					tempQueue.add(child);
				}else if(motherIndex == elitePopulationSize - 1) {
					mother = elitePopulation[--motherIndex];
					NQueenState child = reproduce(mother,father);
					tempQueue.add(child);
				}		
				 ++searchCost;											
			}
			population = tempQueue;										// population is now the next generation
																		// peaking at top of Queue to see if a solution is in the set
			if(population.peek().getFitness() ==  maxFitness ) {		// if solution found then break loop
				solution = population.peek().getState();
				break repeat;
			}
		}
		geneticTimer.cancel();
		totalSearchCost += searchCost;
		return solution;
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
			//state[i] = 1;
		}
		return state;
	}
	

	/**
	 * generates initial population 
	 */
	public void generatePopulation() {
		
		for(int i = 0; i < POPULATION_SIZE ; ++i) {
			population.add(new NQueenState(createRandomQueenState()));
		}
	}
	
}	// end of class
