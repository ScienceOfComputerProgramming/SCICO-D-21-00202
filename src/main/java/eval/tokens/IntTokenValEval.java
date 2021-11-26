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
// CREATED:   January 2022 
//================================================================================

package eval.tokens;

import eval.*;
import pmmm.*;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// compute the value of the given token 
// only for INTs!
// NB: considers also ALL and NONE
// TBD: expressions on all and none are not allowed... check this
public class IntTokenValEval {
	//--------------------------------------------------------------------------------
	public final static int ALL = -100;
	public final static int NONE = -200;
	//--------------------------------------------------------------------------------
	public final static int MINMAX_NULL = 0;
	//--------------------------------------------------------------------------------

	//--------------------------------------------------------------------------------
	// compute int val of token
	// - can be arbitrary arithmetic expression, only all values must have been statically resolved before
	// NB: consider the special treatment of ALL and NONE
	public static int compute(Token token) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Tracer tracer = new Tracer(); 
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		int val;
		try {
			val = compute_intern(token);
		} catch (SyntaxException e) {
			throw new SyntaxException("can't compute int val of expression '" + token.toUserInfo() + "'", m, e);
		}
		/**/ tracer.println("val = " + val, Level.NO, m);
		return val;
	}

	//--------------------------------------------------------------------------------
	private static int compute_intern(Token token) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local vars:
		int leftVal;
		int rightVal;
		String opName;
		String errMsg = "";
		//--------------------------------------------------------------------------------
		// if token is empty, just return UNDEFINED
		// - CAUTION: do not raise exception
		if(token == null)
			return MINMAX_NULL;
		//--------------------------------------------------------------------------------
		// plausi check
		if(token.getType() != IData.Type.INT)
			throw new SyntaxException("ill. token type = " + token.getType(), m);
		//--------------------------------------------------------------------------------
		// all descendants must be int, too
		switch(token.getKind()) {
		//================================================================================
		// NAME
		//================================================================================
		case NAME:
			String txt = token.getRawText();
			//--------------------------------------------------------------------------------
			// ALL
			// - TBD: hard coded
			if(txt.equals("ALL")) {
				return(ALL);
			}
			//--------------------------------------------------------------------------------
			// NONE
			// - TBD: hard coded
			if(txt.equals("NONE")) {
				return(NONE);
			}
			//--------------------------------------------------------------------------------
			// ILL. NAME
			else
				throw new SyntaxException("ill. name = " + txt, m);

			//================================================================================
			// INT
			//================================================================================
		case INT:
			try {
				return Integer.parseInt(token.getRawText());
			} catch (NumberFormatException e) {
				throw new SyntaxException("ill. int number = '" + token.getRawText() + "'", m);
			}

			//================================================================================
			// BOP
			//================================================================================
		case BOP:
			try {
				errMsg = "can't compute left arg of BOP '" + token.rawText + "'";
				leftVal = compute_intern(token.getLeft());
				errMsg = "can't compute right arg of BOP '" + token.rawText + "'";
				rightVal = compute_intern(token.getRight());
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			}
			// check for ALL or NONE, which both have negative vals (see their defines)
			if(leftVal < 0 || rightVal < 0)
				throw new SyntaxException("names are not allowed in BOP expression", m);
			opName = token.getRawText();
			//--------------------------------------------------------------------------------
			// ADD
			if(OpDefs.isArithmeticAddBOPName(opName)) {
				return leftVal + rightVal;
			}
			//--------------------------------------------------------------------------------
			// SUB
			if(OpDefs.isArithmeticSubBOPName(opName)) {
				return leftVal - rightVal;
			}
			//--------------------------------------------------------------------------------
			// MUL
			if(OpDefs.isArithmeticMulBOPName(opName)) {
				return leftVal * rightVal;
			}
			//--------------------------------------------------------------------------------
			// DIV
			if(OpDefs.isArithmeticDivBOPName(opName)) {
				return leftVal / rightVal;
			}
			//--------------------------------------------------------------------------------
			// MOD
			if(OpDefs.isArithmeticModBOPName(opName)) {
				return leftVal % rightVal;
			}
			//--------------------------------------------------------------------------------
			// ILL. BOP NAME
			else
				throw new SyntaxException("ill. binary op = " + opName, m);

			//================================================================================
			// UOP
			//================================================================================
		case UOP:
			rightVal = compute_intern(token.getRight());
			// check for ALL or NONE
			if(rightVal < 0)
				throw new SyntaxException("names are not allowed in UOP expression", m);
			opName = token.getRawText();
			//--------------------------------------------------------------------------------
			// PLUS
			if(OpDefs.isArithmeticPlusUOPName(opName)) {
				return (+ rightVal);
			}
			//--------------------------------------------------------------------------------
			// MINUS
			if(OpDefs.isArithmeticMinusUOPName(opName)) {
				return (- rightVal);
			}
			//--------------------------------------------------------------------------------
			// ILL. UOP NAME
			else
				throw new SyntaxException("ill. unary op = " + opName, m);

			//================================================================================
			// DEFAULT
			//================================================================================
		default:
			throw new SyntaxException("ill. token kind = " + token.getKind(), m);
		}
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

