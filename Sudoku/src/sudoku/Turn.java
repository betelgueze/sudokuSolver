package sudoku;

public class Turn {
	public Turn(int idofCell, boolean wasGuess, int val) {
		this.idOfCell = idofCell;
		this.wasGuess = wasGuess;
		this.value = val;
	}
	public int idOfCell;
	public int value;
	public boolean wasGuess;
	
	
}
