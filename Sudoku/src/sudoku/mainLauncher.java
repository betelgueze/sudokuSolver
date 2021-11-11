package sudoku;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class mainLauncher {

	public static void main(String[] args) {
		List<String> tmp = Arrays.asList(args);
		
		//analyze command line arguments
		Sudoku s = new Sudoku();
		
		Sudoku.verbose = false;
		if(tmp.contains("-v"))
		{
			Sudoku.verbose = true;
		}
		
		if(tmp.contains("-a") || tmp.contains("--all"))
		{
			int bulkCountIndex = tmp.indexOf("-a") + 1;
			s.bulkCount = Integer.parseInt(tmp.get(bulkCountIndex));
			s.findAllSolutions = true;
		}
		
		if(tmp.contains("-c") || tmp.contains("--check"))
		{
			s.checkSolutionsForSolvability = true;
		}
				
		int x,y; //dimensions of a board
		String inputFileName;
		String configFileName = "";
		if(args.length == 1 || args.length == 2)
		{
			x = y = 9;
			if(args.length == 1)
				inputFileName = args[0];
			else
				inputFileName = args[1];
				configFileName = args[0];
		}
		else if(args.length >= 3)
		{
			x = Integer.parseInt(args[0]);
			y = Integer.parseInt(args[1]);
			
			if(args.length >= 4)
			{
				configFileName = args[2];
				//#TODO add support for multiple input sudoku-s
				inputFileName = args[3];
			}
			else
				inputFileName = args[1];
			
		}
		else
			return;
		
		//if given config file initialize the rules
		Path configFilePath = Paths.get(configFileName);
		
		if(Files.exists(configFilePath))
		{
			Config conf = new Config(x,y);
			
			try {
				conf  = Config.readConfigFromFile(configFilePath,x,y);
				
				conf.validate();
			} catch (Exception e) {
				System.err.println("Error parsing config file using default");
				conf = Config.defaultConfig();
			}
			finally{
				s.loadConfig(conf);		
			}
		}
		
		//if none input file is given generate sudoku
		Path inputFilePath = Paths.get(inputFileName);
		if(!Files.exists(inputFilePath))
			s.generate();
		else
		{
			try {
				s.readBoardFromFile(inputFilePath);
			}
			catch (Exception e)
			{
				System.err.println("Error parsing input file \n aborting");
				System.exit(1);
			}
		}
		
		if(s.findAllSolutions == true)
		{
			s.findAllSolutions();
			
			System.out.println( Integer.toString(s.solutions.solutions.size()) + " Solutions has been found !!!");
		}
		// search for first appropriate solution
		else if(s.solve())
		{
			System.out.println("A Single Solution HAS BEEN FOUND !!!");
		}
		
		s.solutions.printSolutions();
	}

}
