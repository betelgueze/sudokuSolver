package sudoku;

import java.util.ArrayList;
import java.util.List;

public class Cell {
	public Cell() {
		value = 0;
		possibilities = new ArrayList<Integer>();
		forbidden = false;
	}

	Integer value; // value of a cell in SUDOKU game
	List<Integer> possibilities;	//list of possibilities in case cell is unfilled
	boolean forbidden;	//forbidden cell is out of game
	
	public String toString()
	{
		if(value != 0)
			return Integer.toString(this.value);
		else
			return possibilities.toString();
	}
	
	public int setToFirstPossibility() {
		value = possibilities.remove(possibilities.size()-1);
		return value;
	}
	
	public void unsetValue() {
		possibilities.add(value);
		value = 0;
	}

	public void generatePossibilities(int maxConstraintSize) {
		for(int i=1; i< maxConstraintSize+1;++i)
		{
			possibilities.add(i);
		}
		
	}
	
	public boolean isEmpty()
	{
		return forbidden == false && value == 0;
	}

	public void setForbidden() {
		forbidden = true;
	}

	public boolean isFilled() {
		return forbidden == false && value != 0;
	}

	public boolean isFilledFast() {
		return value != 0;
	}

	public void removePossibility(Integer value2) {
		this.possibilities.remove(value2);
	}
}
