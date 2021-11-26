//================================================================================
// Peer Model Tool Chain
// Copyright (C) 2021 Eva Maria Kuehn
//--------------------------------------------------------------------------------
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
// 
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//================================================================================
// SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
// AUTHOR:    Eva Maria Kuehn
// CREATED:   December 2020 
//================================================================================

package eval.tokens;

import pmmm.OpDefs;
import pmmm.PmUtil;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// uses state automaton
// abbreviations:
//		LB .... left bracket '('
//		RB ... right bracket ')'
public class Raw2Tokens {
	//--------------------------------------------------------------------------------
	// tracer
	private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// INPUT:
	// current input string -- needed for exception messages
	private String raw;
	//--------------------------------------------------------------------------------
	// all peer model ops
	// - STATIC!!
	private static OpDefs opDefs = new OpDefs();
	//--------------------------------------------------------------------------------
	// sequence of tokens: 
	// - this is the current working vector for mincing!
	// - for internal use only!!!!
	private TV tokenV = new TV();
	//--------------------------------------------------------------------------------
	// count current nesting level
	private int nestingCnt = 0;
	//--------------------------------------------------------------------------------
	// OUTPUT:
	// nb: input could contain several expressions seperated by ';'
	// vector of token vectors
	private TokenExpression tokenVV = new TokenExpression();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// constructor
	public Raw2Tokens(String raw) {
		this.raw = raw;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	// get tokenVV
	public TokenExpression getTokenVV() {
		return tokenVV;
	}

	//================================================================================
	//================================================================================
	// MINCE
	// - TBD: there are 2 Bugs in mincing... see drawio docu
	// -- 1) check that after operators that consist of letters like "CONCAT" or "FOR" either a white space or '(' must follow 
	// -- 2) empty string after BOP must not be allowed
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// mince raw input string into TVV 
	// - nb: the statements are separated by ';' in the raw text
	// -- except for the last one, where the ';' might be omitted
	// - each statement is translated to a tv in the tvv
	public TokenExpression mince() throws SyntaxException {
		//--------------------------------------------------------------------------------
		// set phase of TVV
		tokenVV.setPhase("MINCING");
		//--------------------------------------------------------------------------------
		// call automaton, namely goto its "start" state
		startState(raw);
		//--------------------------------------------------------------------------------
		// set phase of TVV
		tokenVV.setPhase("MINCED");
		//--------------------------------------------------------------------------------
		// ok done
		return tokenVV;
	}
	//--------------------------------------------------------------------------------
	private void add (Token t) {
		tokenV.add(t);
	}

	//================================================================================
	// expression automaton
	//================================================================================

	//--------------------------------------------------------------------------------
	// start state
	//--------------------------------------------------------------------------------
	private void startState(String s) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		s = skipWhiteSpace(s);
		/**/ tracer.println("AUTOMATON startState: " + "\"" + s + "\"", Level.NO, m);
		// ==========
		// empty ?
		// ==========
		if(isEmpty(s)) {		
			// GOTO endState
			endState(s);
			return;
		}
		// ==========
		// not empty:
		// ==========
		// '(' ?
		// ==========
		if(s.charAt(0) == '(') {
			nestingCnt++;
			// TOKEN found = opening bracket
			add(new Token(IToken.Kind.LB, "("));
			startState(s.substring(1));
			return;
		}
		// ==========
		// unary op ?
		// ==========
		boolean unaryOpFlag;
		/**/ tracer.println("AUTOMATON startState: starts with UNARY OP? " + s, Level.NO, m);
		try {
			String opName = opDefs.getLongestUnaryOpNameThatIsPrefixOfS(s);
			/**/ tracer.println("AUTOMATON startState: UNARY OP", Level.NO, m);
			// TBD: assert that there are no unary OP names that subsume each other....
			// TOKEN found = unary op
			add(new Token(IToken.Kind.UOP, opName));
			// move pointer forward
			s = s.substring(opName.length());
			// set flag
			unaryOpFlag = true;
		} catch (Exception e) {
			// ok, no unary op
			// - set flag
			unaryOpFlag = false;
		}
		if(unaryOpFlag) {
			// GOTO state 2
			state2(s);
			return;
		}
		// ==========
		// GOTO state 1
		// ==========
		state1(s);
	}

	//--------------------------------------------------------------------------------
	// state 1
	//--------------------------------------------------------------------------------
	private void state1(String s) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("AUTOMATON state 1   : " + "\"" + s + "\"", Level.NO, m);
		//--------------------------------------------------------------------------------
		boolean done = false;
		s = skipWhiteSpace(s);
		// empty ?
		if(isEmpty(s)) {		
			throw new SyntaxException("Unexpected end of value = " + raw + ".", m);
		}
		// ==========
		// int?
		// ==========
		if(isNumber(s.charAt(0))) {
			/**/ tracer.println("AUTOMATON state 1   : INT", Level.NO, m);
			int k = 0; 
			do {
				k++;
				if(k >= s.length()) {
					break;
				}
			} while(isNumber(s.charAt(k)));
			// TOKEN found = int
			add(new Token(IToken.Kind.INT, s.substring(0, k)));
			// move pointer forward
			s = s.substring(k);
			// done
			done = true;
		}
		// ==========
		// string?
		// ==========
		if(! done && s.charAt(0) == '"') {
			/**/ tracer.println("AUTOMATON state 1   : STRING", Level.NO, m);
			// search for closing '"'; 1...start search after first '"';
			int k = s.indexOf('"', 1);
			// TOKEN found = int
			add(new Token(IToken.Kind.STRING, s.substring(1, k)));
			// move pointer forward
			s = s.substring(k + 1);
			// done
			done = true;
		}
		// ==========
		// (system)var?
		// ==========
		if(! done && s.charAt(0) == '$') {
			int i = 0;
			/**/ tracer.println("AUTOMATON state 1   : VAR", Level.NO, m);
			if(s.length() <= 1) {
				// there must be at least 1 char following '$'
				throw new SyntaxException("Incomplete string. String ends with '$'.", m);
			}
			i++;
			if(s.charAt(i) == '$') {
				/**/ tracer.println("AUTOMATON state 1   : is a SYSTEM VAR", Level.NO, m);
				// system variable
				i++;
				if(s.length() <= 2) {
					// there must be at least 1 char following '$$'
					throw new SyntaxException("Incomplete string. String ends with '$$'.", m);
				}
			}
			// check for first name char
			if(! isFirstCharOfName(s.charAt(i))) {
				throw new SyntaxException("Variable name is missing.", m);
			}					
			// get var name
			for(; i < s.length(); i++) {
				if(! isCharOfName(s.charAt(i))) {
					break;
				}
			}
			// TOKEN found = var
			add(new Token(IToken.Kind.VAR, s.substring(0, i)));
			// move pointer forward
			s = s.substring(i);
			// done
			done = true;
		}
		// ==========
		// true?
		// ==========
		// TBD: hard coded
		if(! done && s.startsWith("true")) {
			/**/ tracer.println("AUTOMATON state 1   : TRUE", Level.NO, m);
			// next char must not be a name char; eg "trueNews" is a name...
			String help = s.substring("true".length());
			if(! ((help.length() > 0) && PmUtil.isValidCharOfName(help.charAt(0)))) {
				// TOKEN found = true
				add(new Token(IToken.Kind.TRUE, "true"));
				// move pointer forward
				s = help;
				// done
				done = true;
			}
		}
		// ==========
		// false?
		// ==========
		// TBD: hard coded
		if(! done && s.startsWith("false")) {
			/**/ tracer.println("AUTOMATON state 1   : FALSE", Level.NO, m);
			// next char must not be a name char; eg "falseNews" is a name...
			String help = s.substring("false".length());
			if(! ((help.length() > 0) && PmUtil.isValidCharOfName(help.charAt(0)))) {
				// TOKEN found = false
				add(new Token(IToken.Kind.TRUE, "false"));
				// move pointer forward
				s = help;
				// done
				done = true;
			}
		}
		// ==========
		// name or fu?
		// ==========
		if(! done && isFirstCharOfName(s.charAt(0))) {
			/**/ tracer.println("AUTOMATON state 1   : label or fu", Level.NO, m);
			int j;
			for(j = 0; j < s.length(); j++) {
				if(! isCharOfName(s.charAt(j)) ) {
					break;
				}
			}
			if(j > 0) {
				/**/ tracer.println("AUTOMATON state 1   : name found: "+ "\"" + s + "\"; j = " + j, Level.NO, m);
				// name found; check if it is a label or a fu (i.e. is it followed by "()"?) 
				String s1 = s.substring(j);
				/**/ tracer.println("AUTOMATON state 1   : s1 = " + "\"" + s1 + "\"; j = " + j, Level.NO, m);
				s1 = skipWhiteSpace(s1);
				if(! isEmpty(s1) && s1.charAt(0) == '(') {
					// CAUTION: so far, only fus without args supported
					s1 = s1.substring(1);
					s1 = skipWhiteSpace(s1);
					if(s1.charAt(0) != ')') {
						throw new SyntaxException("ill. function: ')' is missing", m);
					}
					/**/ tracer.println("AUTOMATON state 1   : FU", Level.NO, m);
					// TOKEN found = fu
					add(new Token(IToken.Kind.FU, s.substring(0, j)));
					// move pointer forward
					s = s1.substring(1);
				}
				else {
					/**/ tracer.println("AUTOMATON state 1   : NAME", Level.NO, m);
					// TOKEN found = label
					add(new Token(IToken.Kind.NAME, s.substring(0, j)));
					// move pointer forward
					s = s.substring(j);
				}
			}
			// done
			done = true;
		}
		// ==========
		// error?
		// ==========
		if(! done) {
			// nb: for ill. expression like ": ->" the parser will eat up "-" thinking it's a unary op and report error at ">" 
			throw new SyntaxException("'" + raw + "'; error ca. at ***" + s + "***", m);			
		}
		// ==========
		// GOTO state 3
		// ==========
		state3(s);
	}

	//--------------------------------------------------------------------------------
	// state 2: 
	//--------------------------------------------------------------------------------
	private void state2(String s) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("AUTOMATON state 2   : " + "\"" + s + "\"", Level.NO, m);
		//--------------------------------------------------------------------------------
		// ==========
		// GOTO state 1
		// ==========
		state1(s);
		return;	
	}

	//--------------------------------------------------------------------------------
	// state 3: 
	//--------------------------------------------------------------------------------
	private void state3(String s) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("AUTOMATON state 3   : " + "\"" + s + "\"", Level.NO, m);
		//--------------------------------------------------------------------------------
		s = skipWhiteSpace(s);
		// empty ?
		if(isEmpty(s)) {		
			// GOTO endState
			endState(s);
			return;	
		}
		// ==========
		// ')' ?
		// ==========
		else if(s.charAt(0) == ')') {
			nestingCnt--;
			if(nestingCnt < 0) {
				throw new SyntaxException("ill. bracket", m);
			}
			// TOKEN found = closing bracket
			add(new Token(IToken.Kind.RB, ")"));
			// move pointer forward
			s = s.substring(1);
			// GOTO state3
			state3(s);
			return;	
		}
		// ==========
		// binary op ?
		// ==========
		else {
			boolean bopFlag;
			try {
				String opName = opDefs.getLongestBinaryOpNameThatIsPrefixOfS(s);
				/**/ tracer.println("AUTOMATON state 3   : BINARY OP = " + "\"" + opName + "\"", Level.NO, m);
				// TOKEN found = binary op
				add(new Token(IToken.Kind.BOP, opName));
				// move pointer forward
				s = s.substring(opName.length());
				// set flag to continue with start state
				bopFlag = true;
			} catch (Exception e) {
				// ok, no binary op; continue below with ";" test
				// - set flag 
				bopFlag = false;
			}
			if(bopFlag) {
				// GOTO startState
				startState(s);
				return;
			}
		}
		// ==========
		// ';' ?
		// ==========
		if(s.charAt(0) == ';') {
			// move pointer forward
			s = s.substring(1);
			// GOTO endState
			endState(s);
			return;
		}

		// ==========
		// error
		// ==========
		// better a rough message that a misleading detailed one
		throw new SyntaxException("'***" + s + "'", m);
	}


	//--------------------------------------------------------------------------------
	// state endState: 
	//--------------------------------------------------------------------------------
	private void endState(String s) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("AUTOMATON endState  : " + s, Level.NO, m);
		//--------------------------------------------------------------------------------
		s = skipWhiteSpace(s);
		if(nestingCnt != 0) {
			throw new SyntaxException("wrong usage of brackets in value = " + raw + "; value ends with nesting level = " + nestingCnt + ".", m);
		}
		// add built-up tokenV to tokensArray
		tokenVV.add(tokenV);

		// ==========
		// ';' ? ... repeat in loop
		// ==========
		while(! isEmpty(s) && s.charAt(0) == ';') {
			// move pointer forward
			s = s.substring(1);
			// skip white space
			s = skipWhiteSpace(s);
		}

		// ==========
		// not empty?
		// ==========
		if(! isEmpty(s)) {
			// there is yet another expression that was seperated by ';' 
			// "reset" tokens
			tokenV = new TV();
			// GOTO startState
			startState(s);
			return;
		}
		// ==========
		// the end
		// ==========
		/**/ tracer.println("AUTOMATON endState  : !!!", Level.NO, m);
	}

	//================================================================================
	// UTILS
	//================================================================================
	//--------------------------------------------------------------------------------
	// skip white space at beginning
	// nb: replaceFirst("^[\\s]*", "") did not work...
	private static String skipWhiteSpace(String s) {
		String sNeu = "";
		char c;
		if(s != null) {
			int k = 0;
			for(int i = 0; i < s.length(); i++) {
				c = s.charAt(i);
				if((c != ' ') && (c != '\n')) // TBD: was noch?
					break;
				k++;
			}
			sNeu = s.substring(k);
		}
		return sNeu;
	}
	//--------------------------------------------------------------------------------
	// test if string is empty 
	private static boolean isEmpty(String s) {
		if(0 == s.length()) {
			return true;
		}
		return false;
	}
	//--------------------------------------------------------------------------------
	// test if character is number
	private boolean isNumber(char c) {
		if((c >= '0') && (c <= '9')) {
			return true;
		}
		return false;
	}
	//--------------------------------------------------------------------------------
	// test if character char of a name
	// caution: use for 2., 3. etc. char; for 1. char use isFirstCharOfName
	private boolean isFirstCharOfName(char c) {
		if(((c >= 'a') && (c <= 'z')) || 
				((c >= 'A') && (c <= 'Z')) || 
				(c == '_')) {
			return true;
		}
		return false;
	}
	//--------------------------------------------------------------------------------
	// test if character is first char of a name
	private boolean isCharOfName(char c) {
		if(isFirstCharOfName(c) || isNumber(c)) {
			return true;
		}
		return false;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	public String toString() {
		return(tokenVV.toString());
	}

}


//================================================================================
// EOF
//================================================================================
