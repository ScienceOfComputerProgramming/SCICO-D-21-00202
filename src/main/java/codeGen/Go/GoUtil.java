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
// CREATED:   Feber 2020 
//================================================================================

package codeGen.Go;

import eval.IData;
import eval.tokens.IToken;
import eval.tokens.Token;
import pmmm.OpDefs;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
public class GoUtil {

	//================================================================================
	// WRITE LABEL (might contain ('#) 
	//================================================================================
	//--------------------------------------------------------------------------------
	// write code for array access; there are 2 forms that depend on whether used on left side of assignment or not:
	// (1) leftSideOfAssignment == true (ie used for left side of a prop def):
	//   - eg: <labelName>#<i1>#<i2>#<i3>#<i4> = <right side>
	//   -- translate array access to: ArrayRef(ArrayRef(ArrayRef(ArrayRef(<labelName>, <i1>), <i2>), <i3>, i4>)
	// (2) else (ie used for right side of a prop def, or in a query selector):
	//   - eg: <left side> = <BLabelName>#<i1>#<i2>#<i3>#<i4>
	//   -- translate right side to: BArrayLabel(DynArrayRef(DynArrayRef(DynArrayRef(<labelName>, <i1>), <i2>), <i3>)
	//   - eg: <SLabelName>#<i1> AND <ILabelName>#<k1>#<k2> 
	//   -- translate array access to: SArrayLabel(DynArrayRef(<SLabelName>, <i1>)) and to IArrayLabel(DynArrayRef(DynArrayRef(<ILabelName>, <k1>), <k1>))
	// NB: typeLetter is only used in case (2)
	// NB: in case (1) Go-automaton must be statically evaluate it, whereas in case (2) dynamic evaluation is possible
	//     - in case (1) namely a hash map is used for entry type names and var names...
	// NB: in case (2) the surrounding "<T>ArrayLabel(" and ")" must have already been written by caller!
	public static void writeCodeForLabelToken(boolean leftSideOfAssignment /* ie static context */, GoCodeWriter codeWriter, Token labelToken) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// <labelToken> # <intExpr>
		if(OpDefs.isHashBOPName(labelToken.getRawText())) {
			//--------------------------------------------------------------------------------
			if(leftSideOfAssignment)
				codeWriter.write("ArrayRef(");
			else
				codeWriter.write("DynArrayRef(");
			//--------------------------------------------------------------------------------
			// left side must be label name or label with array access
			// - recursion
			writeCodeForLabelToken(leftSideOfAssignment, codeWriter, labelToken.getLeft());
			//--------------------------------------------------------------------------------
			codeWriter.write(GoDefs.COMMA);
			//--------------------------------------------------------------------------------
			// right side must be int expression
			Token right = labelToken.getRight();
			if(right.getType() != IData.Type.INT) 
				throw new SyntaxException("right side of '#' must have type INT, but has " + right.getType(), m);
			new Token2Go(codeWriter, right, false /* isTopLevelSelArgP does not apply !!! */).generateCode();
			//--------------------------------------------------------------------------------
			codeWriter.write(")");
		}
		//--------------------------------------------------------------------------------
		// <name> 
		else if(labelToken.getKind() == IToken.Kind.NAME) {
			codeWriter.write("\"" + labelToken.getRawText() + "\"");
		}
		//--------------------------------------------------------------------------------
		// otherwise 
		else
			throw new SyntaxException("ill. prop label specification", m);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
