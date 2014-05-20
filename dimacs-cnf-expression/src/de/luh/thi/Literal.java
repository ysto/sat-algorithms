package de.luh.thi;

/**
 * Literal class implemented in Java.
 *
 * @author Yauheni Stoliar
 * @author Oliver Karras
 * @version 1.0
 */
public class Literal {
	
	private Variable variable;
	private Boolean isNegative;
	
	public Literal(Variable _variable, Boolean _isNegative) {
		
		setVariable(_variable);
		
		setIsNegative(_isNegative);
		
	}
	
	
	// custom methods
	/**
	 * Evaluates the literal.
	 * @return {@code true} if literal evaluates to true, or {@code false} otherwise.
	 */
	public Boolean getValue() {
		
		return getIsNegative() ? !getVariable().getValue() : getVariable().getValue();
		
	}
	
	// generic methods
	public Variable getVariable() {
		
		return variable;
		
	}

	public void setVariable(Variable variable) {
		
		this.variable = variable;
		
	}

	public Boolean getIsNegative() {
		
		return isNegative;
		
	}

	public void setIsNegative(Boolean isNegative) {
		
		this.isNegative = isNegative;
		
	}

	@Override
	public String toString() {
		
		String result = "";
		
		if (getIsNegative()) {
			
			result += "¬";
			
		}
		
		result += "x" + getVariable().getIndex();
		
		return result;
	}
	
	
	
}
