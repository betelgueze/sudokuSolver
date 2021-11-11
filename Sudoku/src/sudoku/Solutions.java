package sudoku;

import java.util.LinkedList;
import java.util.Stack;

public class Solutions {
	public LinkedList<Solution> solutions;
	public Solutions() {
		solutions = new LinkedList<Solution>();
	}
	public void addSolution(Sudoku s)
	{
		solutions.add(new Solution(s));	
	}
	public void printSolutions() {
		System.out.println("List of solutions is following:");
		for(Solution s: solutions)
		{
			//print single solution
			System.out.println(s.toString());
		}		
	}
}
