package de.luh.thi;

import java.io.IOException;

public class Randomisiert {
	
	Expression mExpression;
	
	public static void main(String[] args) {
		
		try{
            
			String path = args[0];
			
			if (path == null) {
				System.out.println("Terminating: expect path to cnf file in DIMACS format as first parameter!");
			}
			
			Randomisiert r = new Randomisiert();
			
			r.parseExpression(path);
			
			r.processExpression();
			
        } catch(StackOverflowError e) {
            System.err.println("Too many recursive calls");
        }
	}
	
	public void parseExpression(String _path) {
		
		DimacsParser dp = new DimacsParser(_path);
		
		try {
			
			mExpression = dp.processFile();
			
		} catch (IOException e) {
			
			System.out.println("CNF file not found.");
			
		}
		
	}
	
	private void processExpression() {
		
		int counter = 0;
		
		if (mExpression != null) {
			
			System.out.println("\nSuccessfully parsed expression: \n\n" + mExpression.toString() + "\n");
			
			System.out.println("Trying random variable values: \n");
			
			mExpression.printValues();
			
			System.out.println("\nExpression evaluates to: " + mExpression.getValue());
			
			if (!mExpression.getValue()) {
				
				for (Clause clause : mExpression.getClauses()) {
					
					if (!clause.getValue()) {
						
						this.repair(clause, counter);
						
					}
					
				}
				
				System.out.println("\n--------------------------------------------\n");
				
				mExpression.printValues();
				
				System.out.println("\nExpression after repair evaluates to: " + mExpression.getValue() + "\n");
				
			}
			
		}
		
	}

	public void repair(Clause _clause, int counter) {
		
		counter += 1;
		
		System.out.println("Counter: " + counter);
		
		System.out.println("\nRepairing clause: " + _clause.toString() + "\n");
		
		_clause.assignRandomValues();
		
		mExpression.printValues();
		
		for (Clause clause : mExpression.getClauses()) {
			
			if (clause.sharesVariablesWith(_clause)) {
				
				if (!mExpression.getValue()) {
					
					this.repair(clause, counter);
					
				}
				
			}
			
		}
		
	}

}
