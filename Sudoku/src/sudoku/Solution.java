package sudoku;

public class Solution {
	public int board[];
	public int minimalSetup[];
	public Solution(Sudoku s) {
		board = new int[s.conf.boardSize()];
		for(int i=0; i<s.conf.boardSize();++i)
		{
			board[i] = s.board[i].value;
		}
	}
	public Solution() {
	}
	public String toString() {
		String ret="";
		for(int item: board)
		{
			ret += item + " ";
		}
		ret += "\n";
		return ret;
	}
	public boolean equals(Solution s)
	{
		if(s.toString().equals(this.toString()))
			return true;
		return false;
	}
}