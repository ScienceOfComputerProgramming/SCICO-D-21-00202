//================================================================================
//Peer Model Tool Chain
//Copyright (C) 2021 Eva Maria Kuehn
//--------------------------------------------------------------------------------
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as
//published by the Free Software Foundation, either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
//================================================================================
// SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
// AUTHOR:    Eva Maria Kuehn
// CREATED:   December 2020 
//================================================================================
// CODE REVIEWS: 20210113 (eK);
//================================================================================

package pmmm;

import java.util.Vector;

import qa.exceptions.NotFoundException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// operators that are supported in Peer Model expressions
// - all ops are assumed to be left associative
// - for precedence see e.g., https://www.programiz.com/java-programming/operator-precedence
public class OpDefs implements IOpDefs {
	//--------------------------------------------------------------------------------
	// for debug
	Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// all ops
	Vector<Op> ops = new Vector<Op>();
	//--------------------------------------------------------------------------------
	// max priority
	int maxPriority = -1;
	//--------------------------------------------------------------------------------
	// longest op name
	private int longestOpNameLen = 0;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// TBD: DOCU 
	public OpDefs() {
		//================================================================================
		// PEER MODEL OPERATOR CONFIGURATION
		//================================================================================
		//--------------------------------------------------------------------------------
		// dot (qualifier) operator:
		ops.add(new Op(DOT, Arity.BINARY, 100));
		//--------------------------------------------------------------------------------
		// hash (array access) operator: 
		// - must be much higher than "="
		// - must be higher than CONCAT
		// TBD: 
		// - must be ??? than "." 
		// examples:
		// - ctrls#WINDEX.1 = ctrls#WINDEX.1 + 1
		// --> (ctrls#(WINDEX.1)) = (ctrls#(WINDEX.1)) + 1
		// ---> less
		// - note = "and the winner is " CONCAT PMMM.gameInfo#WINDEX.1
		// --> note = "and the winner is " CONCAT ((PMMM.gameInfo)#(WINDEX.1))
		// ---> less
		// - firstPlayer = PMMM.players#1
		// --> firstPlayer = PMMM.players#1
		// ---> COMPLICATED: less (needed for type eval), but higher needed for qualifier val computation 
		ops.add(new Op(HASH, Arity.BINARY, 90)); 
		//--------------------------------------------------------------------------------
		// dots (range) operator:
		ops.add(new Op(DOTS, Arity.BINARY, 80));
		// concat string concatenation operator:
		// - must be higher than '=' 
		ops.add(new Op(CONCAT, Arity.BINARY, 80));
		//--------------------------------------------------------------------------------
		// colon operator:
		ops.add(new Op(COLON, Arity.BINARY, 70));
		//--------------------------------------------------------------------------------
		// unary arithmetic operators:
		ops.add(new Op(PLUS, Arity.UNARY, 60));
		ops.add(new Op(MINUS, Arity.UNARY, 60));
		// unary boolean operators:
		ops.add(new Op(NOT, Arity.UNARY, 60));
		ops.add(new Op(NOT_ALT, Arity.UNARY, 60));
		//--------------------------------------------------------------------------------
		// FOR* operators:
		ops.add(new Op(RANGE, Arity.UNARY, 55));
		ops.add(new Op(EXISTS, Arity.UNARY, 55));
		ops.add(new Op(FORALL, Arity.UNARY, 55));
		//--------------------------------------------------------------------------------
		// binary arithmetic operators:
		ops.add(new Op(MUL, Arity.BINARY, 50));
		ops.add(new Op(DIV, Arity.BINARY, 50));
		ops.add(new Op(MOD, Arity.BINARY, 50));
		ops.add(new Op(MOD_ALT, Arity.BINARY, 50));
		//--------------------------------------------------------------------------------
		// binary arithmetic operators:
		ops.add(new Op(ADD, Arity.BINARY, 40));
		ops.add(new Op(SUB, Arity.BINARY, 40));
		//--------------------------------------------------------------------------------
		// binary relational operators:
		ops.add(new Op(LESS, Arity.BINARY, 30));
		ops.add(new Op(LESS_EQUAL, Arity.BINARY, 30));
		ops.add(new Op(GREATER, Arity.BINARY, 30));
		ops.add(new Op(GREATER_EQUAL, Arity.BINARY, 30));
		//--------------------------------------------------------------------------------
		// binary relational operators:
		ops.add(new Op(EQUAL, Arity.BINARY, 20));
		ops.add(new Op(NOT_EQUAL, Arity.BINARY, 20));
		ops.add(new Op(NOT_EQUAL_ALT, Arity.BINARY, 20));
		//--------------------------------------------------------------------------------
		// binary boolean operators:
		ops.add(new Op(AND, Arity.BINARY, 11));
		ops.add(new Op(AND_ALT, Arity.BINARY, 11));
		//--------------------------------------------------------------------------------
		// binary boolean operators:
		ops.add(new Op(OR, Arity.BINARY, 10));
		ops.add(new Op(OR_ALT, Arity.BINARY, 10));
		//--------------------------------------------------------------------------------
		// assignment operator:
		ops.add(new Op(ASSIGN, Arity.BINARY, 6));
		// 'IN' operator:
		ops.add(new Op(IN, Arity.BINARY, 6));
		//--------------------------------------------------------------------------------
		// comma operator:
		ops.add(new Op(COMMA, Arity.BINARY, 3));
		//--------------------------------------------------------------------------------
		// implies operator '->':
		ops.add(new Op(IMPLIES, Arity.BINARY, 1));
		//--------------------------------------------------------------------------------
		//================================================================================
		// some class vars
		//================================================================================
		//--------------------------------------------------------------------------------
		// init max priority
		maxPriority = getHighestOpPriority();
		// init max op name len
		longestOpNameLen = getLongestOpNameLen();
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	public void add(Op op) {
		ops.add(op);
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// retrieve op by given arity and name
	public Op getOp(Arity arity, String opName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		Op op;
		for(int i = 0; i < ops.size(); i++) {
			op = ops.get(i);
			if((arity == op.arity) && opName.equals(op.name)) {
				return op;
			}
		}
		throw new NotFoundException("op '" + opName + "' does not exist", m);
	}
	//--------------------------------------------------------------------------------
	// retrieve longest op name len
	public int getLongestOpNameLen() {
		int maxLen = 0;
		int len;
		for(int i = 0; i < ops.size(); i++) {
			len = ops.get(i).name.length();
			if(len > maxLen) {
				maxLen = len;
			}
		}
		return maxLen;
	} 
	//--------------------------------------------------------------------------------
	// retrieve highest priority of op 
	// returs -1 if not found 
	public int getHighestOpPriority() {
		int hiPrio = -1;
		int prio;
		for(int i = 0; i < ops.size(); i++) {
			prio = ops.get(i).priority;
			if(prio > hiPrio) {
				hiPrio = prio;
			}
		}
		return hiPrio;
	} 
	//--------------------------------------------------------------------------------
	// retrieve next highest priority of op by given current highest priority
	// returs -1 if not found 
	public int getNextHighestOpPriority(int curHiPrio) {
		int nextHiPrio = -1;
		int prio;
		for(int i = 0; i < ops.size(); i++) {
			prio = ops.get(i).priority;
			if((prio < curHiPrio) && (prio > nextHiPrio)) {
				nextHiPrio = prio;
			}
		}
		return nextHiPrio;
	} 
	//--------------------------------------------------------------------------------
	// search for unary whose name is a prefix of the given string;
	// if found, return full op name, otherwise throw exception;
	// CAUTION: search for op with longest op name, because some op names subsume others, eg: ">=" subsumes ">";
	// TBD: if op is a name (ie no special chars) *after* the op there must be a white space or '(', eg FORblabla is a NAME but not an OP!
	public String getLongestUnaryOpNameThatIsPrefixOfS(String s) throws Exception {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		/**/ tracer.println("longestOpNameLen = " + longestOpNameLen, Level.NO, m);
		/**/ tracer.println("s = " + s, Level.NO, m);
		//--------------------------------------------------------------------------------
		int sLen = s.length();
		// a bit inefficient....
		for(int nextLen = longestOpNameLen; nextLen > 0; nextLen--) {
			if(sLen < nextLen)
				continue;
			for(int i = 0; i < ops.size(); i++) {	
				Op op = ops.get(i);
				if(((op.arity == IOpDefs.Arity.UNARY) && s.startsWith(op.name) && (op.name.length() == nextLen))) {
					return op.name;
				}
			}
		}
		throw new Exception("not found");
	}
	//--------------------------------------------------------------------------------
	// search for "longest" binary op whose name is a prefix of given string s;
	// if found, return full op name, otherwise throw exception;
	// CAUTION: search for op with longest op name, because some op names subsume others, eg: ">=" subsumes ">";
	// TBD: if op is a name (ie no special chars) *after* the op there must be a white space or '(', eg CONCATblabla is a NAME but not an OP!
	public String getLongestBinaryOpNameThatIsPrefixOfS(String s) throws Exception {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		/**/ tracer.println("longestOpNameLen = " + longestOpNameLen, Level.NO, m);
		/**/ tracer.println("s = " + s, Level.NO, m);
		//--------------------------------------------------------------------------------
		int sLen = s.length();
		// a bit inefficient....
		for(int nextLen = longestOpNameLen; nextLen > 0; nextLen--) {
			if(sLen < nextLen)
				continue;
			for(int i = 0; i < ops.size(); i++) {	
				Op op = ops.get(i);
				if(((op.arity == IOpDefs.Arity.BINARY) && s.startsWith(op.name) && (op.name.length() == nextLen))) {
					return op.name;
				}
			}
		}
		throw new Exception("not found");
	}

	//================================================================================
	//================================================================================
	// check operator names
	//================================================================================
	//================================================================================

	//--------------------------------------------------------------------------------
	//================================================================================
	// ARITHMETIC
	//================================================================================
	//--------------------------------------------------------------------------------
	// - binary
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticBOPName(String opName) {
		if(isArithmeticAddBOPName(opName) || 
				isArithmeticSubBOPName(opName) || 
				isArithmeticMulBOPName(opName) || 
				isArithmeticDivBOPName(opName) || 
				isArithmeticModBOPName(opName))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticAddBOPName(String opName) {
		if(opName.equals(ADD))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticSubBOPName(String opName) {
		if(opName.equals(SUB))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticMulBOPName(String opName) {
		if(opName.equals(MUL))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticDivBOPName(String opName) {
		if(opName.equals(DIV))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticModBOPName(String opName) {
		if(opName.equals(MOD) || opName.equals(MOD_ALT))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	// - unary
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticUOPName(String opName) {
		if(isArithmeticPlusUOPName(opName) || 
				isArithmeticMinusUOPName(opName))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticPlusUOPName(String opName) {
		if(opName.equals(PLUS))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isArithmeticMinusUOPName(String opName) {
		if(opName.equals(MINUS)) 
			return true;
		return false;
	}
	//================================================================================
	// RELATIONAL
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isRelationalBOPName(String opName) {
		if(isRelationalEqualBOPName(opName) || 
				isRelationalNotEqualBOPName(opName) || 
				isRelationalLessBOPName(opName) || 
				isRelationalLessEqualBOPName(opName) || 
				isRelationalGreaterBOPName(opName) || 
				isRelationalGreaterEqualBOPName(opName))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isRelationalEqualBOPName(String opName) {
		if(opName.equals(EQUAL))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isRelationalNotEqualBOPName(String opName) {
		if(opName.equals(NOT_EQUAL) || opName.equals(NOT_EQUAL_ALT))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isRelationalLessBOPName(String opName) {
		if(opName.equals(LESS)) 
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isRelationalLessEqualBOPName(String opName) {
		if(opName.equals(LESS_EQUAL)) 
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isRelationalGreaterBOPName(String opName) {
		if(opName.equals(GREATER))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isRelationalGreaterEqualBOPName(String opName) {
		if(opName.equals(GREATER_EQUAL))
			return true;
		return false;
	}
	//================================================================================
	// BOOLEAN
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isBooleanBOPName(String opName) {
		if(isBooleanAndBOPName(opName) || 
				isBooleanOrBOPName(opName))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	// - binary
	//--------------------------------------------------------------------------------
	public static boolean isBooleanAndBOPName(String opName) {
		if(opName.equals(AND) || opName.equals(AND_ALT)) 
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isBooleanOrBOPName(String opName) {
		if(opName.equals(OR) || opName.equals(OR_ALT))
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	// - unary
	//--------------------------------------------------------------------------------
	public static boolean isBooleanUOPName(String opName) {
		if(isBooleanNotUOPName(opName)) 
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	public static boolean isBooleanNotUOPName(String opName) {
		if(opName.equals(NOT) || opName.equals(NOT_ALT)) 
			return true;
		return false;
	}
	//================================================================================
	// ASSIGNMENT
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isAssignmenBOPName(String opName) {
		if(opName.equals(ASSIGN)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// DOTS
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isDotsBOPName(String opName) {
		if(opName.equals(DOTS)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// DOT
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isDotBOPName(String opName) {
		if(opName.equals(DOT)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// COLON
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isColonBOPName(String opName) {
		if(opName.equals(COLON)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// CONCAT
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isConcatBOPName(String opName) {
		if(opName.equals(CONCAT)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// HASH
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isHashBOPName(String opName) {
		if(opName.equals(HASH)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// IN
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isInBOPName(String opName) {
		if(opName.equals(IN)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// IMPLIES
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isImpliesBOPName(String opName) {
		if(opName.equals(IMPLIES)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// RANGE
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isRangeUOPName(String opName) {
		if(opName.equals(RANGE)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// FORALL
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isForAllUOPName(String opName) {
		if(opName.equals(FORALL)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// EXISTS
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isExistsUOPName(String opName) {
		if(opName.equals(EXISTS)) {
			return true;
		}
		return false;
	}
	//================================================================================
	// COMMA
	//================================================================================
	//--------------------------------------------------------------------------------
	public static boolean isCommaBOPName(String opName) {
		if(opName.equals(COMMA)) {
			return true;
		}
		return false;
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================


