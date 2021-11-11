package sudoku;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * This class specifies the whole game it has dimensions dimX,dimY and keeps track of the rules
 * in constraints and forbiddenCElls lists.
 * */
public class Config {
	
	public Config(int x, int y) {
		dimX = x;
		dimY = y;
		
		constraints = new ArrayList<Constraint>();
		forbiddenCells = new ArrayList<Integer>();
	}

	public int dimX;
	public int dimY;
	
	public List<Constraint> constraints;
	public List<Integer> forbiddenCells; //list of ids of cells that are OUTside of da game

	public static Config readConfigFromFile(Path configFilePath, int x, int y) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(configFilePath.toString()));
		
		Config cret = new Config(x,y);
				
		String line;
		while((line = reader.readLine()) != null)
		{
			//analyze command
			if(line.startsWith("forbidden"))
			{
				if(line.substring(9).trim().isEmpty())
					continue;
				
				String[] arr = line.substring(9).trim().split(" ");
				
				for(String item:arr)
				{
					int idOfCell = Integer.parseInt(item);
					if(idOfCell >= 0 && idOfCell <= x*y)
						cret.forbiddenCells.add(idOfCell);
				}
				
			}
			else if(line.startsWith("unique"))
			{
				line = line.substring(6);
				
				Constraint ccc = new Constraint();
				if(line.contains("-"))
				{
					String[] arr = line.split("-");
					
					for(int i=Integer.parseInt(arr[0].trim()); i<Integer.parseInt(arr[1].trim())+1;++i)
						ccc.arr.add(i);
					
					ccc.setRelevantCellsMaps(x*y);
					Constraint.updateMaxConstraintSize(ccc.size());
					cret.constraints.add(ccc);
				}
				else if(line.contains("step"))
				{
					String[] arr = line.split("step");
					// contains cell Ids
					String[] cellIds = arr[0].trim().split(" "); 
					//contains [step_size,repeat_count]
					String[] parameters = arr[1].trim().split("times");
					
					int repeat_count = Integer.parseInt(parameters[1].trim());
					int step_size = Integer.parseInt(parameters[0].trim());
					
					List<Integer> pattern = new ArrayList<Integer>();
					for (String str: cellIds) {
					    pattern.add(Integer.parseInt(str.trim()));
					}
					
					
					for(int i=0; i < repeat_count;++i)
					{
						for(int j=0; j<pattern.size();++j)
						{
							ccc.arr.add(pattern.get(j));
							
							pattern.set(j,pattern.get(j)+step_size);
						}
					}
					ccc.setRelevantCellsMaps(x*y);
					Constraint.updateMaxConstraintSize(ccc.size());
					cret.constraints.add(ccc);
				}
				
			}
			//TODO analyze data
		}
		
		return cret;
	}

	public void validate() throws Exception {
		// TODO 
		// check consistency		
	}

	public static Config defaultConfig() {
		Config c = new Config(9,9);
		c.forbiddenCells = Collections.<Integer>emptyList();
		//TODO defaultconfig
		for(int i=0;i<9;++i)
		{
			
		}
		
		for(int i=0;i<9;++i)
		{
			
		}
		
		for(int i=0;i<9;++i)
		{
			
		}
		
		return c;
	}
	
	public int boardSize() {return dimX*dimY;}

}
