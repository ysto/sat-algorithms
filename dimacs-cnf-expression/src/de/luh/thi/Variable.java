package de.luh.thi;

import java.util.Random;

/**
 * Variable class implemented in Java.
 *
 * @author Yauheni Stoliar
 * @author Oliver Karras
 * @version 1.0
 */
public class Variable implements Comparable<Variable> {
	
	private Random random;
	
	private Integer index;
	private Boolean value;

	public Variable(Integer _index) {
		
		random = new Random();
		
		setIndex(_index);
		
		setValue(random.nextBoolean());
		
	}
	
	// custom methods
	/**
	 * Assigns random boolean value to this variable
	 */
	public void assignRandomValue() {
		
		setValue(random.nextBoolean());
		
	}
	
	// generic methods
	@Override
    public int compareTo(Variable _variable) {
		
        return this.getIndex().compareTo(_variable.getIndex());
        
    }
	
	public Integer getIndex() {
		
		return index;
		
	}

	public void setIndex(Integer _index) {
		
		this.index = _index;
		
	}

	public Boolean getValue() {
		
		return value;
		
	}

	public void setValue(Boolean _value) {
		
		this.value = _value;
		
	}

	@Override
	public String toString() {
		
		return "x" + String.valueOf(getIndex()) + ": " + String.valueOf(getValue());
		
	}
	
}
