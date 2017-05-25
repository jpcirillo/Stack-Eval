//Just for the record, this Java Project is called "CyaLater_Evaluator"


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.lang.Character;

public class StackCalculator {
	private HashMap<Character, Integer> vars; // stores variables
	private Stack<Object> theExpression; // stores the expression in postfix
	private char[] expCharArr; // stores a character array representation of the
								// expression
	private ArrayList<Character> validOperators; // stores all valid operators
	private final Stack<Object> ERROR; // if the expression equals this Stack,
										// it indicates that an error has
										// occurred

	public StackCalculator() {
		// constructor method, initializes fields.
		ERROR = new Stack<Object>();
		ERROR.add("ERROR");
		validOperators = new ArrayList<Character>();
		theExpression = new Stack<Object>();
		vars = new HashMap<Character, Integer>();
		validOperators.add('+');
		validOperators.add('-');
		validOperators.add('*');
		validOperators.add('/');
		validOperators.add('%');
		validOperators.add('^');
	}

	public void processInput(String s) {
		// the only method which is called by the driver, calls other methods to
		// actually perform most operations
		// Precondition: s is an expression
		// Postcondition: s has been evaluated and the result has been output,
		// unless s was an invalid expression, in which case an error has been
		// printed
		if (validOperators.contains('_')) { // if _ is still considered a valid
											// operator from the last call, it
											// no longer is until after unaries
											// is called
			validOperators.remove((Character) '_');
		}

		if (!checkBalanced(s)) {
			return;
		}

		// once the parentheses, brackets, and braces are ensured to be valid,
		// they can all be treated as parentheses with no issue. we also remove
		// all blank space from the expression, just to make it easier to work
		// with
		s = s.replaceAll("\\s", "");
		s = s.replaceAll("\\{", "(");
		s = s.replaceAll("\\}", ")");
		s = s.replaceAll("\\[", "(");
		s = s.replaceAll("\\]", ")");

		s = unaries(s);
		validOperators.add('_'); // once we have called unaries, _ is now a
									// valid operator. 

		if (!checkValid(s)) {
			return;
		}
		


		if (s.contains("=")) { // if there is the = operator in the expression,
								// we treat it as if it is setting a variable.
			if (!Character.isLetter(s.charAt(0))) { // if the first character is
													// not a letter, it is not a
													// valid variable name.
				System.out.println("Invalid Variable Name " + s.charAt(0));
				return;
			}
			if (s.charAt(1) != '=') { // as variables can only be one letter
										// long, if the second character of the
										// expression is not =, it means the
										// variable is invalid.
				String temporary = "";
				for (int i = 0; s.charAt(i) != '='; i++) { // assembles all
															// characters before
															// the = so they can
															// be output as the
															// invalid variable
															// name
					temporary += s.charAt(i);
				}
				System.out.println("Invalid Variable Name " + temporary);
				return;
			}
			theExpression = toPostfix(s.substring(2)); // everything after the =
														// is treated as an
														// expression, and its
														// result is assigned to
														// the variable
			if (theExpression == ERROR) {
				return;
			}
			vars.put(s.charAt(0), fromPostfix(theExpression));
			System.out.println(s.charAt(0) + " is set to " + vars.get(s.charAt(0)));

		} else { // the expression is not a variable assignment

			theExpression = toPostfix(s);

			if (theExpression == ERROR) {
				return;
			}

			System.out.println(fromPostfix(theExpression)); // Prints the final
															// result, assuming
															// no error was ever
															// encountered.

		}
		return;
	}

	private boolean checkValid(String str) {
		// ensures that the expression is valid (only valid characters are
		// included and there is never multiple operators in a row
		// Precondition: str is an expression
		// Postcondition: str has been ensured to be a valid expression, or an
		// error has been thrown
		boolean aOKAY = true;
		for (int i = 0; i < str.length() && aOKAY; i++) { // loops through the
															// string until the
															// end of the string
															// or until an error
															// has been
															// encountered
			if (!(validOperators.contains(str.charAt(i)) || Character.isLetter(str.charAt(i)) // checks
																								// whether
																								// each
																								// character
																								// is
																								// valid
					|| Character.isDigit(str.charAt(i)) || str.charAt(i) == '(' || str.charAt(i) == ')'
					|| str.charAt(i) == '=')) {
				System.out.println("Invalid Symbol " + str.charAt(i));
				aOKAY = false;
			}
			if (i == 0) { // the string cannot start with any operator, except the negative operator, _
				if (validOperators.contains(str.charAt(i)) && str.charAt(i) != '_') {
					System.out.println("Nonsenical Input " + str);
					aOKAY = false;
				}
			} else { // if there are 2 operators in a row, and the second of the
						// two is not unary, it means that the expression is
						// nonsensical
				if (validOperators.contains(str.charAt(i)) && validOperators.contains(str.charAt(i - 1))
						&& str.charAt(i) != '_') {
					System.out.println("Nonsenical Input " + str);
					aOKAY = false;
				}
			}
		}
		return aOKAY;
	}

	private Stack<Object> toPostfix(String s) {
		// Converts the expression to postfix
		// Precondition: s is an expression
		// Postcondition: s has been represented as a stack in postfix, or an
		// error has been found
		Stack<Object> expression = new Stack<Object>(); // holds the expression
		Stack<Character> operators = new Stack<Character>(); // holds the
																// operators
		expCharArr = s.toCharArray();
		String temp = "";
		for (int i = 0; i < expCharArr.length; i++) {
			if (Character.isDigit(expCharArr[i])) { // an operand has been found
				temp = "";
				while (i < expCharArr.length && i < expCharArr.length ? Character.isDigit(expCharArr[i]) ? true : false
						: false) { // if the operand is multiple digits, this
									// goes until it finds a non-digit, to
									// ensure the entire operand is found.
					temp += expCharArr[i];
					i++;
				}
				expression.push(Integer.parseInt(temp)); // add the operand to
															// the expression
			}
			if (i < expCharArr.length && Character.isLetter(expCharArr[i])) { // if
																				// a
																				// letter
																				// is
																				// found,
																				// a
																				// variable
																				// (or
																				// an
																				// invalid
																				// variable)
																				// has
																				// been
																				// found
				if (!vars.containsKey(expCharArr[i])) {
					System.out.println("Undefined Variable " + expCharArr[i]);
					return ERROR;
				} else {
					expression.push(vars.get(expCharArr[i])); // adds the
																// variable's
																// value to the
																// expression
				}
			}
			if (i < expCharArr.length && expCharArr[i] == '(') { // open
																	// parentheses
																	// is found
				operators.push(expCharArr[i]); // it is put on the operators
												// stack
			}
			if (i < expCharArr.length && expCharArr[i] == ')') { // close
																	// parentheses
																	// is found
				while (!operators.isEmpty() && operators.peek() != '(') { // operators
																			// are
																			// added
																			// to
																			// the
																			// expression
																			// until
																			// the
																			// open
																			// parentheses
																			// is
																			// found
					expression.push(operators.pop());
				}
				operators.pop();
			}
			if (i < expCharArr.length && validOperators.contains(expCharArr[i])) { // an
																					// operator
																					// is
																					// found
				if (!operators.isEmpty() || !operators.isEmpty() ? operators.peek() == '(' ? true : false : false) { // if
																														// the
																														// operators
																														// stack
																														// isnt
																														// empty,
																														// or
																														// an
																														// open
																														// parentheses
																														// is
																														// on
																														// top
																														// of
																														// it
					operators.push(expCharArr[i]); // the operator is put on the
													// operators stack
				} else {
					while (!operators.isEmpty() && operators.peek() != '('
							&& !lowerPrecOrEqIfRtAssoc(operators.peek(), expCharArr[i])) { // until
																							// the
																							// operators
																							// stack
																							// is
																							// empty,
																							// an
																							// open
																							// parentheses
																							// is
																							// found,
																							// or
																							// an
																							// operator
																							// of
																							// lower
																							// precedence
																							// (or
																							// equal
																							// if
																							// right
																							// associative
																							// operator),
																							// operators
																							// are
																							// popped
																							// from
																							// the
																							// operators
																							// stack
																							// and
																							// added
																							// to
																							// the
																							// expression
																							// stack
						expression.push(operators.pop());
					}
					operators.push(expCharArr[i]);
				}
			}
		}
		while (!operators.isEmpty()) { // all operators in the operators stack
										// are moved to the expression stack
			expression.push(operators.pop());
		}

		return expression;
	}

	private int fromPostfix(Stack<Object> theExpression) {
		// Evaluates the postfix expression
		// Precondition: theExpression is a Stack of Objects which respresents a
		// postfix expression
		// Postcondition: the integer value of the expression is returned
		// I want to reverse the stack, as to evaluate it I want to read it
		// left-to-right
		Stack<Object> hold = new Stack<Object>();
		while (!theExpression.isEmpty()) {
			hold.push(theExpression.pop());
		}
		theExpression = hold;
		
		Stack<Integer> output = new Stack<Integer>(); //in the end, this will hold only one integer, the end value.
		
		while (!theExpression.isEmpty()) { //goes through the full expression
			if (theExpression.peek() instanceof Integer) { //if an operand is found it is pushed to the output stack
				output.push((int) theExpression.pop()); 
			} else if (validOperators.contains(theExpression.peek())) { //if an operator is found, it is applied
				if ((char) theExpression.peek() == '_') { //the _ operator designates a negative, so a value is popped from output, multiplied by -1, and then the product is put back
					int a = output.pop();
					output.push(a * -1);
					theExpression.pop();
				} else { //all other operators require 2 integers to be evaluated using that operation (evalOperator method is called to do this)
					int a = output.pop();
					int b = output.pop();
					output.push(evalOperator(a, b, (char) theExpression.pop()));
				}
			}
		}
			return output.pop();
		}
	

	private String unaries(String str) {
		//Handles the unary operators, + is basically ignored, as it does nothing to the operand after it. -, when it denotes negative and not subtract, is replaced with _, which will later be treated as the negative operator
		//Precondition: str is an expression
		//Postcondition: the unary operators have been appropriately handled
		ArrayList<Character> chars = new ArrayList<Character>();
		for (int i = 0; i < str.length(); i++) { //to easily handle the expression, I represent it as an ArrayList of characters
			chars.add(str.charAt(i));
		}

		for (int i = chars.size() - 1; i > 0; i--) { 
			if (chars.get(i) == '-' && validOperators.contains(chars.get(i - 1))) { //- means negative and not subtractive if the character before it is also an operator
				chars.set(i, '_');
			} else if (chars.get(i) == '+' && validOperators.contains(chars.get(i - 1))) { //+ means the positive unary operator and not addition
				chars.remove(i);
			}
		}
		if (chars.get(0) == '+') { //the first character is always a unary operator if it is + or -
			chars.remove(0);
		} else if (chars.get(0) == '-') {
			chars.set(0, '_');
		}
		String fixed = ""; //put the arraylist back into string form
		for (char element : chars) {
			fixed += element;
		}
		if (fixed.contains("__")) { //__ can be ignored, as it is a double negative
			fixed = fixed.replaceAll("__", "");
		}
		return fixed;
	}

	private boolean lowerPrecOrEqIfRtAssoc(char a, char b) {
		//Precondition: a, b are operators
		//Postcondition: the boolean value representing whether a is a lower precedence than b, or equal precedence if b is a right associative operator.
		if (a == '+' || a == '-') { //if a is + or -, it is lower precedence unless b is also + or -
			if (b != '+' && b != '-') { 
				return true;
			} else {
				return false;
			}
		} else if (a == '^') { //if a is ^, it is never of lower precedence than b, but is of equal precedence if b is also ^
			if (b == '^') {
				return true;
			} else {
				return false;
			}
		} else { // The other possibility is that a is multiplication, division, or modulus, which is a lower precedence than only ^
			if (b == '^') {
				return true;
			} else {
				return false;
			}
		}
	}

	private static int evalOperator(int a, int b, char theOp) {
		//Preconditions: a, b are operands and theOp is an operator
		//Postcondition: the result of b (theOp) a is returned
		switch (theOp) {
		case '+':
			return b + a;
		case '-':
			return b - a;
		case '%':
			return b % a;
		case '/':
			return b / a;
		case '*':
			return b * a;
		case '^':
			return (int) Math.pow(b, a);
		}
		System.out.println("Something very silly has happened!"); 
		return 0; //this should never be reached, but the compiler required it to be here
	}

	private static boolean checkBalanced(String maybeItIs) {
		//Precondition: maybeItIs is an expression
		//Postcondition: true is returned if the parentheses, brackets, and braces are balanced, otherwise an error message is printed and false is returned
		//This is literally copied and pasted from the BalancedProject assignment from 10/26/15
		Stack<Character> chars = new Stack<>();
		char[] theString = maybeItIs.toCharArray(); // create a char array of
													// the characters in the
													// string (purely for
													// neatness' sake)
		for (int i = 0; i < theString.length; i++) { // loops through each
														// character in the
														// array
			switch (theString[i]) {
			case '(': // identical cases for { [ and (, pushes that char
				chars.push(theString[i]);
				break;
			case '[':
				chars.push(theString[i]);
				break;
			case '{':
				chars.push(theString[i]);
				break;
			case ')': // almost identical cases for } ] and ), checks to make
						// sure that bracket was opened. Continues if it was,
						// else returns false
				if (chars.isEmpty()) {
					System.out.println("Unbalanced Parentheses Error, Mismatched Parentheses");
					return false;
				} else {
					if (chars.pop() == '(') {
						break;
					} else {
						System.out.println("Unbalanced Parentheses Error, Mismatched Parentheses");
						return false;
					}
				}
			case ']':
				if (chars.isEmpty()) {
					System.out.println("Unbalanced Parentheses Error, Mismatched Parentheses");
					return false;
				} else {
					if (chars.pop() == '[') {
						break;
					} else {
						System.out.println("Unbalanced Parentheses Error, Mismatched Parentheses");
						return false;
					}
				}
			case '}':
				if (chars.isEmpty()) {
					System.out.println("Unbalanced Parentheses Error, Mismatched Parentheses");
					return false;
				} else {
					if (chars.pop() == '{') {
						break;
					} else {
						System.out.println("Unbalanced Parentheses Error, Mismatched Parentheses");
						return false;
					}
				}
			}
		}
		if (!chars.empty()) { // if chars isn't empty, it means there is still a
								// symbol that was never closed, so false is
								// returned
			System.out.println("Unbalanced Parentheses Error, Too Many Left Parentheses");
			return false;
		} else {
			return true; // everything has gone smoothly
		}
	}
}