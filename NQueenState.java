/**
 * CS 4200
 * Professor: D. Atanasio
 *
 * Project #2 - NQueenState.java
 *
 *	QueenState Object of given gameboard size. Overridden CompareTo in order to work with PriorityQueue
 *
 * @author Eric Schenck
 * last modified: 10/8/18
 */


public class NQueenState implements Comparable<NQueenState>{
	
	private int[] state;
	private int fitness;

	/**
	 * constructor for QueenState
	 * @param n
	 */
	public NQueenState(int[] state) {
		this.state = state;
		this.fitness = fitnessFunction();			// setting fitness of new State
	}
	
	public void setState(int[] state) {
		this.state = state;
	}
	
	public int[] getState() {
		return state;
	}
	
//	public void setFitness(int fitness) {
//		this.fitness = fitness;
//	}
	
	public int getFitness() {
		return fitnessFunction();								// 
	}
	
	/**
	 * This is the Fitness Function which counts total number of attacking queens, ideal number or goal state is = 0
	 * @param currentState state in question
	 * @return the total count of attacking queens in current game state
	 */
	public int fitnessFunction() {
		int fitnessCount = 0;
		int stateSize = state.length;
		
		double maxValue = stateSize - 1;
		maxValue /= 2;
		maxValue *= stateSize;
		
		for(int i = 0 ; i< stateSize - 1; ++i) {
			for(int j = i + 1 ; j < stateSize; ++j) {
			
				// checking for queens in same row, or at a vertical attack position for both / and \ directed attacks
				if( state[i] == state[j]	|| (state[i] == state[j] + (j - i)) 
						|| (state[i] == state[j] - (j - i))){
					
					++fitnessCount;		// for each hit increase the count
				}			
			}
		}
		fitnessCount = (int)maxValue - fitnessCount;
		return fitnessCount;
	}
	
	
	public String toString() {
		String str = "";
	
		str = "State : [ ";
		for(int i = 0; i < state.length; ++i) {
			str += state[i] + ", ";
		}
		str += "]  & Fitness : " + fitness;
		
		return str;
	}
	
	
	/**
	 * compares two NQueenState objects by their fitness. To be used for priorityQueue
	 */
	@Override
	public int compareTo(NQueenState state) {
		if(this.getFitness() > state.getFitness()) {
			return -1;
		} else if (this.getFitness() < state.getFitness()) {
			return 1;
		} else {
			return 0;
		}
	}

	
} // end of class
