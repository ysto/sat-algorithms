package de.luh.thi;

import java.util.ArrayList;

/**
 * Clause class implemented in Java.
 *
 * @author Yauheni Stoliar
 * @author Oliver Karras
 * @version 1.0
 */
public class Clause {
	
	
	private ArrayList<Literal> literals;
	
	
	public Clause() {
		
		literals = new ArrayList<>();
		
	}

	
	/**
	 * 
	 * @return Returns {@code true} if clause evaluates to true, {@code false} otherwise.
	 */
	public Boolean getValue() {
		
		for (Literal literal : getLiterals()) {
			
			if (literal.getValue()) {
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	
	/**
	 * Assigns clause literals with random values.
	 */
	public void assignRandomValues() {
		
		for (Literal literal : getLiterals()) {
			
			literal.getVariable().assignRandomValue();
			
		}
		
	}
	
	
	/**
	 * 
	 * @param _clause another clause to compare literals with.
	 * @return Returns {@code true} if the set of variables of current clause 
	 * 			intersects with that of {@code _clause}, or {@code false} otherwise.
	 */
	public boolean sharesVariablesWith(Clause _clause) {
		
		if (intersection(collectUsedVariables(), _clause.collectUsedVariables())) {
			
			return true;
			
		}
		
		return false;
		
	}
	
	
	/**
	 * 
	 * @return Iterates through literals of a clause and collects contained variables. 
	 * 			Returns variables.
	 */
	public ArrayList<Variable> collectUsedVariables() {
		
		ArrayList<Variable> result = new ArrayList<>();
		
		for (Literal literal : getLiterals()) {
			
			result.add(literal.getVariable());
			
		}
		
		return result;
		
	}
	
	
	/**
	 * 
	 * @param _list1 - variables
	 * @param _list2 - another variables
	 * @return {@code true} if {@code _list1} and {@code _list2} intersect, 
	 * 			or {@code false} otherwise.
	 */
	public boolean intersection(ArrayList<Variable> _list1, ArrayList<Variable> _list2) {
		
        for (Variable variable : _list1) {
        	
            if(_list2.contains(variable)) {
            	
                return true;
                
            }
            
        }

        return false;
        
    }
	
	// generic methods
	public ArrayList<Literal> getLiterals() {
		
		return literals;
		
	}
	
	public void setLiterals(ArrayList<Literal> _literals) {
		
		this.literals = _literals;
		
	}

	@Override
	public String toString() {
		
		String result = "";
		
		if (getLiterals().size() > 1) {
			
			result += "(";
			
			for (int i = 0; i < getLiterals().size(); i++) {
				
				result += getLiterals().get(i).toString();
				
				if (i < getLiterals().size() - 1) {
					result += " ∨ ";
				}
				
			}
			
			result += ")";
			
		} else {
			
			return getLiterals().get(0).toString();
			
		}
		
		return result;
	}
	
}
