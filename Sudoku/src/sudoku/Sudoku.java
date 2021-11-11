package sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Sudoku {
	public Sudoku() {
		history = new Stack<Turn>();
		
		filledCells = 0;
		emptyCells = 0;
		cellsTotal = 0;
		guessesCount=0;
	}

	public Config conf;
	public Cell board[];
	
	private int filledCells;
	private int emptyCells;
	private int cellsTotal;
	
	private Stack<Turn> history;
	private Guess guessMap[];
	private int guessesCount;
	public Solutions solutions;
	
	public static boolean verbose = false;
	public boolean findAllSolutions = false; //solver searches for all solutions
	public boolean checkSolutionsForSolvability = false; //solver nalayzes solution solvability
	public int bulkCount = 1; // number of solutions required to be printed out during solving
	private int currentSolutionNumber = 0; 
	
	public void generate() {
		//TODO fix generate method
	}

	public void loadConfig(Config config) {
		conf = config;
		guessMap = new Guess[conf.dimX*conf.dimY];
		for(Integer i: conf.forbiddenCells)
		{
			board[i].setForbidden();
		}
	}
	
	public void findAllSolutions()
	{
		while(solve())
		{
			//System.out.println(solutions.solutions.getLast().toString());
			this.reinitialize();
			this.repairPossibilitiesGlobally();
			
			if(verbose == true)
			{
				System.out.print("Trying to find another solution\n");
				this.printSolution();
			}
		}
	}

	private void reinitialize() {
		Turn t;
		while((t = history.pop()).wasGuess != true && !history.isEmpty())
		{
			board[t.idOfCell].unsetValue();
			this.removeCell();
		}
		board[t.idOfCell].unsetValue();
		this.removeCell();
		guessesCount--;
	}

	public boolean solve() {
		if(verbose == true)
		{
			//System.out.print("This is a verbose output:\n");
			//this.printSolution();
		}
		//check all constraints
		if(emptyCells == 0)
		{
			//just to be sure check once more all constraints
			boolean isValid = this.checkConstriants();
			if(isValid == true)
			{
				if(this.isUnique())
				{
					solutions.addSolution(this);
					++currentSolutionNumber;
					if(currentSolutionNumber == bulkCount)
					{
						printLastBulkOfSolutions();
						currentSolutionNumber = 0;
					}
					return true;
				}
				return false;
			}
			return backTrackToLastGuess();
			
		}
		else if(this.hasZeroMoves())
		{	
			return backTrackToLastGuess();
		}
		
		//move simple
		//for every cell analyze the one with the smallest possibilities and smallest index
		int minPossibilities = conf.dimX*conf.dimY;
		int minIndex = 0;
		for(int i=0; i< conf.boardSize();++i)
		{
			Cell c = board[i];
			if(c.isEmpty())
			{
				int val = c.possibilities.size();
				if(val < minPossibilities)
				{
					minPossibilities = val;
					minIndex = i;
				}						
			}		
		}
		
		if(minPossibilities == 0)
			return this.backTrackToLastGuess();
		
		//move simple
		if(minPossibilities == 1)
		{
			this.fillCell();
			int val = board[minIndex].setToFirstPossibility();
			history.push(new Turn(minIndex,false,val));
		}
		//move stochastic
		else
		{
			//pick a guess
			Integer res;
			res = this.pickGuess(minIndex);
			if(res == -1) //no guess left for this item
			{
				if(verbose == true)
				{
					System.out.println("No guessing left at i="+Integer.toString(minIndex));
				}

				guessMap[minIndex].guesses.clear(); //remove all guesses
				return this.backTrackToLastGuess(); //backtrack
			}
			else
			{
				if(verbose == true)
				{
					
					int gscount = 0;
					if(guessMap[minIndex] != null)
						gscount = guessMap[minIndex].guesses.size();
					//if(minIndex == 0)
					//{
					//this.printSolution();
					System.out.println("picking a guess at i="+Integer.toString(minIndex) + " value=" + Integer.toString(res) + " guessCount=" + Integer.toString(guessesCount) + " this cell has "+Integer.toString(gscount)+" guesses used");
					//}
				}
				this.addGuess();
				board[minIndex].value = res;
				if(guessMap[minIndex] == null)
					guessMap[minIndex] = new Guess();
				guessMap[minIndex].guesses.add(res);
				history.push(new Turn(minIndex,true,res));
				//board[minIndex].possibilities.remove(res);
			}			
		}
		
		this.unsetPossibilitesOnBoard(minIndex,board[minIndex].value);
		
		return solve();
	}
	
	private void addGuess() {
		guessesCount++;
		this.fillCell();
	}

	private void fillCell() {
		--emptyCells;
		filledCells++;		
	}

	private int lastPrintedSolutionsIndex = 0;

	private void printLastBulkOfSolutions() {
		for(int i = lastPrintedSolutionsIndex; i < lastPrintedSolutionsIndex + bulkCount; ++i)
		{
			System.out.println(solutions.solutions.get(i).toString());
		}
		lastPrintedSolutionsIndex += bulkCount;
	}

	private boolean isUnique() {
		Solution found = new Solution(this);
		for(Solution s :this.solutions.solutions)
		{
			if(found.equals(s))
				return false;
		}
		if(verbose == true)
			System.out.println("found a new unique solution!");
		
		return true;
	}

	private int pickGuess(int i) {
		for(int poss: board[i].possibilities)
		{
			if(this.isNotInGuessListForPosition(i,poss))
			{
				return poss;
			}
		}
		return -1;
	}

	private boolean isNotInGuessListForPosition(int idOfCell, Integer value) {
		if(guessMap[idOfCell] == null)
			return true;
		
		if(guessMap[idOfCell].guesses.contains(value))
			return false;
		
		return true;
	}

	private void unsetPossibilitesOnBoard(int i, Integer value) {
		//for every constraint that is regarded board[i] cell
		for(Constraint c: conf.constraints)
		{
			if(c.relevant[i] == 0)
				continue;
			
			//for every constraint element different than board[i] cell
			for(Integer ii: c.arr)
			{
				if(i == ii)
					continue;
							
				//unset value from possibilities
				board[ii].possibilities.remove(value);
			}
		}
	}

	private boolean checkConstriants() {
		//for every constraint
		for(Constraint c: conf.constraints)
		{
			int[] map = new int[c.arr.size()];
			//for every constraint element
			for(Integer i: c.arr)
			{
				//check if value is present more than a once
				int value = board[i].value - 1;
				if(value != -1 && map[value] != 0)
				{
					if(verbose == true)
						System.out.println("Inconsistency found on constraint:"+c.arr.toString());
					return false;
				}
				else
					map[value] = 1;
			}
		}
		return true;
	}

	private boolean hasZeroMoves() {
		// check if there is any cell without possibility
		for(Cell c: board)
		{
			if(c.isEmpty() && c.possibilities.isEmpty())
			{
				return true;				
			}
		}
		return false;
	}

	private boolean backTrackToLastGuess() {
		if(history.empty() == true)
			return false;
		
		if(verbose == true)
		{
			System.out.print("backtracking history from size="+Integer.toString(history.size()));
		}

		{
			Turn t;
			while((t = history.pop()).wasGuess != true && history.empty() == false)
			{
				board[t.idOfCell].unsetValue();
				this.removeCell();
			}
			this.removeCell();
			board[t.idOfCell].unsetValue();
			guessesCount--;
		}
		if(verbose == true)
		{
			System.out.println(" to size="+Integer.toString(history.size()) + " guessCount=" + Integer.toString(guessesCount));
			
		}
			
		this.repairPossibilitiesGlobally();
		
		//printSolution();
		
		return this.solve();
	}

	private void removeCell() {
		++emptyCells;
		filledCells--;
	}

	private void repairPossibilitiesGlobally() {
		//for every non-filled cell
		for(int i=0; i<conf.boardSize();++i)
		{
			if(board[i].isFilled())
				continue;

			board[i].possibilities.clear();
			board[i].generatePossibilities(Constraint.maxConstraintSize);
			//for every constraint it is in
			for(Constraint c: conf.constraints)
			{
				if(c.isRelevantWithCell(i))
				{
					//check this constraint items
					//for every constraint item
					for(Integer item: c.arr)
					{
						if(item == i)
							continue;
						
						if(board[item].isFilled())
							//remove this value from list of possibilities
							board[i].removePossibility(board[item].value);
					}
				}
			}
		}
		
	}

	public void printSolution() {
		for(int i=0;i<conf.boardSize();++i)
		{
			//check newline
			if(i % conf.dimX == 0)
				System.out.print("\n");
			System.out.print(board[i].toString()+" ");
			
			//System.out.println(board[i].possibilities.toString());
		}
		System.out.print("\n");
		
		/*
		System.out.println("filledCells are " + Double.toString(((double)filledCells)/cellsTotal));
		System.out.println("emptyCells are " + Double.toString(((double)emptyCells)/cellsTotal));
		System.out.println("cellsTotal are " + cellsTotal);*/
	}

	public void readBoardFromFile(Path inputFilePath) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath.toString()));
		
		board = new Cell[conf.boardSize()];
		
		String line;
		int idOfCell = 0;
		while((line = reader.readLine()) != null)
		{
			String[] arr = line.trim().split(" ");
			for(String str: arr)
			{
				board[idOfCell] = new Cell();
				try
				{
					board[idOfCell].value = Integer.parseInt(str);
					++this.filledCells;
				}
				catch (NumberFormatException e) 
				{
					++this.emptyCells;
					board[idOfCell].value = 0;
					board[idOfCell].generatePossibilities(Constraint.maxConstraintSize);
				}
				finally {
					++cellsTotal;
					idOfCell++;
				}
			}
		}
		this.solutions = new Solutions();
		this.repairPossibilitiesGlobally();		
	}
}
