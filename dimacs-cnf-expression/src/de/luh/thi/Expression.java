package de.luh.thi;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Expression class implemented in Java.
 *
 * @author Yauheni Stoliar
 * @author Oliver Karras
 * @version 1.0
 */
public class Expression {
	
	private ArrayList<Clause> clauses;
	private ArrayList<Variable> variables;
	
	public Expression() {
		
		clauses = new ArrayList<>();
		variables = new ArrayList<>();
		
	}
	
	
	// custom methods
	/**
	 * Adds needed amount of variables.
	 * @param _maxIndex - amount of variables in this expression
	 */
	public void createVariables(Integer _maxIndex) {
		
		if (_maxIndex != null) {
			
			for (int i = 1; i <= _maxIndex; i++) {
				
				getVariables().add(new Variable(i));
				
			}
			
		}
		
	}
	
	
	/**
	 * Iterates through expression variables and searches for certain variable. 
	 * @param _index - search parameter
	 * @return variable which index equals _index search parameter
	 */
	public Variable getVariableByIndex(Integer _index) {
		
		Variable result = null;
		
		for (Variable variable : getVariables()) {
			
			if (variable.getIndex() == _index) {
				
				return variable;
				
			}
			
		}
		
		return result;
		
	}
	
	
	/**
	 * Evaluates the expression.
	 * @return {@code true} if expression evaluates to true, or {@code false} otherwise.
	 */
	public Boolean getValue() {
		
		for (Clause clause : getClauses()) {
			
			if (!clause.getValue()) {
				
				return false;
				
			}
			
		}
		
		return true;
		
	}
	
	
	/**
	 * Prints values of variables to console.
	 */
	public void printValues() {
		
		Collections.sort(getVariables());
		
		for (Variable variable : getVariables()) {
			
			System.out.println(variable.toString());
			
		}
		
	}
	
	
	// generic methods
	public ArrayList<Clause> getClauses() {
		
		return clauses;
		
	}

	
	public void setClauses(ArrayList<Clause> clauses) {
		
		this.clauses = clauses;
		
	}

	
	public ArrayList<Variable> getVariables() {
		
		return variables;
		
	}

	
	public void setVariables(ArrayList<Variable> variables) {
		
		this.variables = variables;
		
	}

	
	@Override
	public String toString() {
		
		String result = "";
		
		for (int i = 0; i < getClauses().size(); i++) {
			
			result += getClauses().get(i).toString();
			
			if (i < getClauses().size() - 1) {
				result += " ∧ ";
			}
			
		}
		
		return result;
	}
	
}
