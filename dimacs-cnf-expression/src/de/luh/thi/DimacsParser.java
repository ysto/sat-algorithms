package de.luh.thi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * DimacsParser class implemented in Java.
 *
 * @author Yauheni Stoliar
 * @author Oliver Karras
 * @version 1.0
 */
public class DimacsParser {
	
	/**
	 * 
	 * ParserState enum contains current state of this parser. 
	 * 
	 * {@code READ_CONF} - parser is expecting the preference line.
	 * 
	 * {@code READ_CLAUSE} - parser is expecting the clause line.
	 * 
	 * {@code ERROR} - parser encountered an error and stopped parsing.
	 *
	 */
	private enum ParserState {
		
		READ_CONF, 
		READ_CLAUSE, 
		ERROR
		
	}
	
	private static Charset ENCODING = StandardCharsets.UTF_8;
	
	private ParserState mParserState;
	private Path 		mFilePath;
	private int 		mNumberOfVariables;
	private int 		mMaxParsedIndex;
	private int 		mNumberOfClauses;
	private Expression 	mExpression;

	
	/**
	 * 
	 * @param _fileName - a String representation of cnf file's path.
	 */
	public DimacsParser(String _fileName) {
		
		mParserState = ParserState.READ_CONF;
		mFilePath = Paths.get(_fileName);
		
	}
	
	
	/**
	 * Processes a cnf file in DIMACS format.
	 * 
	 * @return parsed expression if successfully parsed or {@code null} otherwise.
	 * @throws IOException
	 */
	public final Expression processFile() throws IOException {
		
		mNumberOfVariables = 0;
		mNumberOfClauses = 0;
		mMaxParsedIndex = 0;
		
		try (Scanner scanner = new Scanner(mFilePath, ENCODING.name())) {
			
			while (scanner.hasNextLine() && !encounteredError()) {
				
				processLine(scanner.nextLine());
				
			}
			
			scanner.close();
			
		}
		
		if (encounteredError()) {
			
			return null;
			
		}
		
		if (wrongPreferencesAmounts()) {
			
			println("Error: parsed expression amounts differ from preference amounts.");
			return null;
			
		}
		
		return mExpression;
		
	}
	
	
	/**
	 * Processes a line in cnf file. Depending of current parser state 
	 * tries to parse a comment, preferences or clause line.
	 * 
	 * @param _line - a line to process.
	 */
	private void processLine(String _line) {
		
		Scanner scanner = new Scanner(_line);
		
		// explode the line to tokens using whitespace as delimiter
		scanner.useDelimiter(" ");
		
		// while there are more tokens and no errors, do
		if (scanner.hasNext() && !encounteredError()) {
			
			if (waitingForPreferencesLine()) {
				
				// line begins with this token
				String firstToken = scanner.next();
				
				if (isCommentToken(firstToken)) {
					
					parseCommentLine(scanner);
					
				} else if(isPreferencesToken(firstToken)) {
					
					parsePreferencesLine(scanner);
					
				} else {
					
					mParserState = ParserState.ERROR;
					println("Error: unknown line before preference line");
					
				}
				
			} else if(waitingForClauseLine()) {
				
				parseClauseLine(scanner);

			}
			
		} else {
			
			mParserState = ParserState.ERROR;
			println("Error: can not parse -> " + _line);
			
		}
		
		scanner.close();
		
	}
	
	
	/**
	 * After detecting comment line this function will print out the comment. 
	 * @param _scanner - scanner object containing exploded comment tokens
	 */
	private void parseCommentLine(Scanner _scanner) {
		
		print("Comment: ");
		
		while(_scanner.hasNext()) {
			
			print(_scanner.next()+" ");
			
		}
		
		System.out.println();
		
	}

	
	/**
	 * After detecting preferences line this function will process the preferences. 
	 * @param _scanner - scanner object containing exploded preferences tokens
	 */
	private void parsePreferencesLine(Scanner _scanner) {
		
		// preferences contain of 3 tokens
		String form = null;
		String numberOfVariablesString = null;
		String numberOfClausesString = null;
		
		// read form
		if(_scanner.hasNext()) {
			
			form = _scanner.next();
			
		} else {
			
			mParserState = ParserState.ERROR;
			println("Error: missing form info in preference line");
			return;
			
		}
		
		// read number of variables
		if (_scanner.hasNext()) {

			numberOfVariablesString = _scanner.next();

		} else {
			
			mParserState = ParserState.ERROR;
			println("Error: missing number of variables info in preferences line");
			return;
			
		}
		
		// read number of clauses
		if (_scanner.hasNext()) {

			numberOfClausesString = _scanner.next();

		} else {
			
			mParserState = ParserState.ERROR;
			println("Error: missing number of clauses info in preferences line");
			return;
			
		}
		
		// extract the numbers of strings
		try { 
			
			mNumberOfVariables = Integer.parseInt(numberOfVariablesString);
			
			mNumberOfClauses = Integer.parseInt(numberOfClausesString);
			
		} catch (NumberFormatException e) {
			
			mParserState = ParserState.ERROR;
			
		}
		
		// verify parsed preferences 
		if (parsedValidPreferencesLine(form)) {
			
			// preferences are ok, can create new expression
			createExpressionWithVariables();
			
			// from now on parser expects clauses
			mParserState = ParserState.READ_CLAUSE;
			
		} else {
			
			mParserState = ParserState.ERROR;
			
		}
		
	}
	
	
	/**
	 * After detecting clause line this function will extract literals. 
	 * @param _scanner - scanner object containing exploded clause tokens
	 */
	private void parseClauseLine(Scanner _scanner) {
		
		Clause currentClause = new Clause();
		
		String expectedLiteralString = "";
		
		while (_scanner.hasNext()) {
			
			expectedLiteralString = _scanner.next();
			
			if (expectedLiteralString.equalsIgnoreCase("0")) {
				break; // reached EOL
			}
			
			Integer signedLiteral = null;
			
			// extract literal index
			try { 
				
				signedLiteral = Integer.parseInt(expectedLiteralString);
				
			} catch (NumberFormatException e) {
				
				mParserState = ParserState.ERROR;
				println("Error: cannot parse literal index: " + expectedLiteralString);

			}
			
			if (validLiteral(signedLiteral)) {
				
				Variable variable = mExpression.getVariableByIndex(Math.abs(signedLiteral));
				boolean isNegative = signedLiteral < 0;
				
				// literal ok, can add to clause
				currentClause.getLiterals().add(new Literal(variable, isNegative));
				
				if (Math.abs(signedLiteral) > mMaxParsedIndex) {
					mMaxParsedIndex = Math.abs(signedLiteral);
				}
				
			} else {
				
				mParserState = ParserState.ERROR;
				println("Error: illegal literal: " + signedLiteral);
				
			}
			
		}
		
		if (currentClause.getLiterals().size() < 1) {
			
			mParserState = ParserState.ERROR;
			println("Error: clause contains no literals");
			
		} else if(!expectedLiteralString.equalsIgnoreCase("0")) {
			
			mParserState = ParserState.ERROR;
			println("Error: malformed clause EOL: " + expectedLiteralString);
			
		} else {
			
			mExpression.getClauses().add(currentClause);
			
		}
		
	}
	
	
	/**
	 * Creates new expression and adds needed variables to that expression.
	 */
	private void createExpressionWithVariables() {
		
		mExpression = new Expression();
		
		mExpression.createVariables(mNumberOfVariables);
		
	}
	
	
	/**
	 * Verifies if amounts in the preference line of the input file 
	 * correspond to the real amounts of the expression.
	 * 
	 * @return {@code true} if amounts are correct, or {@code false} otherwise
	 */
	private boolean wrongPreferencesAmounts() {
		
		boolean result = false;
		
		if (mExpression.getVariables().size() > mMaxParsedIndex) {
			
			result = true;
			
		}
		
		if (mExpression.getClauses().size() != mNumberOfClauses) {
			
			result = true;
			
		}
		
		return result;
		
	}
	
	
	/**
	 * Verifies if literal has been created and its index stays within maximal index for expression's variables
	 * @param _signedLiteral - literal to verify
	 * @return
	 */
	private boolean validLiteral(Integer _signedLiteral) {
		
		return _signedLiteral != null && _signedLiteral <= mNumberOfVariables;
		
	}
	
	
	/**
	 * Checks if preferences line delivers plausible values after parsing
	 * @param _form - form of expression according to cnf file preferences' line
	 * @return {@code true} if check ok, or {@code false} otherwise
	 */
	private boolean parsedValidPreferencesLine(String _form) {
		
		boolean result = true;
		
		// don't even check if parses encountered error already
		if (encounteredError()) {
			
			result = false;
			
		}
		
		// check form is cnf
		if (!_form.equalsIgnoreCase("cnf")) {
			
			result = false;
			println("Error: invalid form in preferences file. Expected 'cnf' got: '" + _form + "'");
			
		}
		
		// check for valid variables amount
		if (mNumberOfVariables < 1) {
			
			result = false;
			println("Error: invalid number of variables in preferences file. Expected at least 1 got: " + mNumberOfVariables);
			
		}
		
		// check for valid clauses amount
		if (mNumberOfClauses < 2) {
			
			result = false;
			println("Error: invalid number of clauses in preferences file. Expected at least 2 got: " + mNumberOfClauses);
			
		}
		
		return result;
		
	}
	
	
	/**
	 * 
	 * @param _firstToken - first token of the current line
	 * @return {@code true} if token is {@code c}, or {@code false} otherwise
	 */
	private boolean isCommentToken(String _firstToken) {
		
		return _firstToken.trim().equalsIgnoreCase("c");
		
	}
	
	
	/**
	 * 
	 * @param _firstToken - first token of the current line
	 * @return {@code true} if token is {@code p}, or {@code false} otherwise
	 */
	private boolean isPreferencesToken(String _firstToken) {
		
		return _firstToken.trim().equalsIgnoreCase("p");
		
	}

	
	/**
	 * 
	 * @return {@code true} if parser expects comment or preferences line, or {@code false} otherwise
	 */
	private boolean waitingForPreferencesLine() {
		
		return mParserState == ParserState.READ_CONF;
		
	}
	
	
	/**
	 * 
	 * @return {@code true} if parser expects clauses line, or {@code false} otherwise
	 */
	private boolean waitingForClauseLine() {
		
		return mParserState == ParserState.READ_CLAUSE;
		
	}
	
	/**
	 * 
	 * @return {@code true} if parser encountered an error, or {@code false} otherwise
	 */
	private boolean encounteredError() {
		
		return mParserState == ParserState.ERROR;
		
	}

	/**
	 * Prints to console and stay on the same line.
	 * @param _object - will print the string representation of this object
	 */
	private static void print(Object _object) {
		
		System.out.print(String.valueOf(_object));
		
	}
	
	
	/**
	 * Prints to console and jumps to next line.
	 * @param _object - will print the string representation of this object
	 */
	private static void println(Object _object) {
		
		System.out.println(String.valueOf(_object));
		
	}

}
