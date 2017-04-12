package p3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This file needs to hold your solver to be tested. 
 * You can alter the class to extend any class that extends MazeSolver.
 * It must have a constructor that takes in a Maze.
 * It must have a solve() method that returns the datatype List<Direction>
 *   which will either be a reference to a list of steps to take or will
 *   be null if the maze cannot be solved.
 */

public class StudentMTMazeSolver extends SkippingMazeSolver
{

	public ExecutorService pool;
	int num_Of_moves=0;
	public StudentMTMazeSolver(Maze maze)
	{
		super(maze);
	}

	public List<Direction> solve() 
	{
		// TODO: Implement your code here
		LinkedList<StudentDFSMazeSolver> task = new LinkedList<StudentDFSMazeSolver>();
		List<Future<List<Direction>>> future = new LinkedList<Future<List<Direction>>>();
		List<Direction> solution = null;
		int processors = Runtime.getRuntime().availableProcessors();
		pool = Executors.newFixedThreadPool(processors);
		try{
			Choice entry = firstChoice(maze.getStart());
			
			int size = entry.choices.size();
			for(int index = 0; index < size; index++){
				Choice currChoice = follow(entry.at, entry.choices.peek());
				
				
				task.add(new StudentDFSMazeSolver(currChoice, entry.choices.pop()));
				
			}
		}catch (SolutionFound e){
			System.out.println("Solution found");
		}
		try {
			future = pool.invokeAll(task);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pool.shutdown();
		for(Future<List<Direction>> dir : future){
			try {
				
				if(dir.get() != null){
					solution = dir.get();
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return solution;
	}

private class StudentDFSMazeSolver implements Callable<List<Direction>>{
		Choice start;
		Direction dir;
		public StudentDFSMazeSolver(Choice start, Direction dir){
			this.start = start;
			this.dir = dir;
			
		}

		@Override
		public List<Direction> call() {
			// TODO Auto-generated method stub
			LinkedList<Choice> choiceStack = new LinkedList<Choice>();
			Choice currentChoice;

			try{
				choiceStack.push(this.start);
				
				while(!choiceStack.isEmpty()){
					currentChoice = choiceStack.peek();

					if(currentChoice.isDeadend()){
						//backtrack
						choiceStack.pop();
						if (!choiceStack.isEmpty()) choiceStack.peek().choices.pop();
						continue;
					}
					num_Of_moves++;
					choiceStack.push(follow(currentChoice.at, currentChoice.choices.peek()));
				}
				return null;
			}catch (SolutionFound e){
				
				Iterator<Choice> iter = choiceStack.iterator();
	            LinkedList<Direction> solutionPath = new LinkedList<Direction>();
	        
	           
	            while (iter.hasNext())
	            {
	            	currentChoice = iter.next();
	                solutionPath.push(currentChoice.choices.peek());
	            }
	            solutionPath.push(dir);
	            if (maze.display != null) maze.display.updateDisplay();
	            
	           
	            System.out.println("Length of solution path: "+solutionPath.size());
	            System.out.println("Moves count: " + pathToFullPath(solutionPath).size());
	            return pathToFullPath(solutionPath);
			}

		}

	}
}
