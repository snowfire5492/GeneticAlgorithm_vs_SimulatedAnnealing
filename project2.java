/**
 * CS 4200
 * Professor: D. Atanasio
 *
 * Project #2
 *
 *	use local search algorithms to solve n-Queen problems. Implement both 1) Simulated Annealing
 *	and 2.) Genetic Algorithm to solve the n-queen problem where n=21
 *
 *	generate a large number of n-queen instances (>500) and solve them. Measure the percentage of solved
 *	problems search costs and the average running time. Explain why you get such results, for example, why
 *	the algorithm can only solve the percentage of the problems that it did. 
 
 * @author Eric Schenck
 * last modified: 10/8/18
 */


public class project2 {

	
	private static final int SIZE_OF_GAME = 21;		// change value to set size of n for n-queen game board size 
	
	
	/**
	 * creates simulatedAnnealingTest object which sets size of game AND runs testing for project 2
	 */
	public static void runSimulatedAnnealingTests() {
		
		@SuppressWarnings("unused")
		simulatedAnnealingTest test = new simulatedAnnealingTest(SIZE_OF_GAME);
	}
	
	
	/**
	 * creates geneticAlgorithmTest object which sets size of game AND runs testing for project 2
	 */
	public static void runGeneticTests() {
		 
		new geneticAlgorithmTest(SIZE_OF_GAME);
	}
	
	
	public static void main(String[] args) {
	
		runSimulatedAnnealingTests();
		System.out.println("****************************************************************************************************");
		runGeneticTests();
	}
	
} // end of class
