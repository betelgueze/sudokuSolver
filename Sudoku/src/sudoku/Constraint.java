package sudoku;

import java.util.LinkedList;
import java.util.List;

public class Constraint {
	public LinkedList<Integer> arr; //list of idOfCells
	public int relevant[];			//array of relevant cells 0 means irrelevant
	
	public Constraint() {
		arr = new LinkedList<Integer>();
		return;
	}
	
	public static int maxConstraintSize = 0;
	public static void updateMaxConstraintSize(int i)
	{
		if(i > maxConstraintSize)
			maxConstraintSize = i;
	}
	
	public void setRelevantCellsMaps(int size) {
		relevant = new int[size];
		for(Integer i: arr)
		{
			relevant[i] = 1;
		}
	}
	public int size() 
	{
		return arr.size();
	}

	public boolean isRelevantWithCell(int i) {
		return (relevant[i] == 1)?true:false;
		
	}
}
