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

//================================================================================
// operators that are supported in Peer Model expressions;
// suffix "_ALT"...alternative name for the same operator
//CAUTION: check for all usages if you change these defines.... ad add ones....
// TBD: the operator name(s) to be used in the input are fixed here... 
public interface IOpDefs {
	//================================================================================
	// OP NAME
	//================================================================================
	//--------------------------------------------------------------------------------
	// arithmetic operators:
	// - binary:
	public static final String ADD = "+";
	public static final String SUB = "-";
	public static final String MUL = "*";
	public static final String DIV = "/";
	public static final String MOD = "%";
	public static final String MOD_ALT = "MOD";
	// - unary:
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	//--------------------------------------------------------------------------------
	// relational operators:
	// - binary:
	public static final String EQUAL = "==";
	public static final String NOT_EQUAL = "!=";
	public static final String NOT_EQUAL_ALT = "<>";
	public static final String LESS = "<";
	public static final String LESS_EQUAL = "<=";
	public static final String GREATER = ">";
	public static final String GREATER_EQUAL = ">=";
	//--------------------------------------------------------------------------------
	// boolean operators:
	// - binary:
	public static final String AND = "AND";
	public static final String AND_ALT = "&&";
	public static final String OR = "OR";
	public static final String OR_ALT = "||";
	// - unary:
	public static final String NOT = "NOT";	
	public static final String NOT_ALT = "!";	
	//--------------------------------------------------------------------------------
	// assignment operators:
	// - binary:
	public static final String ASSIGN = "=";
	//--------------------------------------------------------------------------------
	// colon operator:
	// - binary:
	public static final String COLON = ":";
	//--------------------------------------------------------------------------------
	// dot operator:
	// - binary:
	public static final String DOT = ".";
	//--------------------------------------------------------------------------------
	// dots operator:
	// - binary:
	public static final String DOTS = "..";
	//--------------------------------------------------------------------------------
	// string operators:
	// - binary
	public static final String CONCAT = "CONCAT";
	//--------------------------------------------------------------------------------
	// array access operators:
	// - binary
	public static final String HASH = "#";
	public static final String IN = "IN";
	public static final String IMPLIES = "->";
	// - unary
	public static final String RANGE = "RANGE";
	public static final String EXISTS = "EXISTS";
	public static final String FORALL = "FORALL";
	//--------------------------------------------------------------------------------
	// comma
	// - binary
	public static final String COMMA = ",";
	//--------------------------------------------------------------------------------

	//================================================================================
	// OP ARITY
	//================================================================================
	//--------------------------------------------------------------------------------
	// arity: number of args: 1...unary, 2...binary, 3...ternary (still unused; e.g. ? : op)
	public enum Arity {UNARY, BINARY, TERNARY};
	
	
} // END OF INTERFACE


//================================================================================
//EOF
//================================================================================
