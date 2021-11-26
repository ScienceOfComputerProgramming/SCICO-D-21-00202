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
//SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
//AUTHOR:    Eva Maria Kuehn
//CREATED:   December 2020 
//================================================================================

package codeGen.Go;

import java.util.Vector;

import eval.tokens.*;
import pmmm.OpDefs;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
public class Vars2Go {
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private GoCodeWriter codeWriter;
	private TokenExpression vars;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Vars2Go(GoCodeWriter codeWriter, TokenExpression vars) {
		this.codeWriter = codeWriter;
		this.vars = vars;
	}

	//================================================================================
	// VARS 
	// -- go automaton differentiates between eprops and vars, whereas modeler mixes them in var/set/set/get  
	// -- so pick the right ones out of the var/set/set/get pairs, recognized by <VAR> kind on the left hand side
	//--------------------------------------------------------------------------------
	public void generateCode() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// start of vars
		codeWriter.write("Vars{");
		//--------------------------------------------------------------------------------
		// for the comma writing trick: remember if any var was written already
		boolean anyVarWrittenFlag = false;
		//--------------------------------------------------------------------------------
		// any vars out there?
		if(! vars.isTVVEmpty()) {
			// TBD: replace by PropsDefs HASH MAP --- and make TVV protected again
			Vector<TV> tvv = vars.getTVV();
			//--------------------------------------------------------------------------------
			// iterate over TVV
			for(int i = 0; i < tvv.size(); i++) {
				//--------------------------------------------------------------------------------
				//  iterate over TV
				TV tv = tvv.get(i);
				for(int j = 0; j < tv.size(); j++) {
					Token token = tv.get(j);
					//--------------------------------------------------------------------------------
					// token should have the form <name> = <expression>
					if(OpDefs.isAssignmenBOPName(token.getRawText())) {
						// !! check that it is a var !!
						// - this means: skip all other kinds 
						// -- especially names -- as they belong to a "<X>Props" section
						if(token.getLeft().getKind() == IToken.Kind.VAR) {
							//--------------------------------------------------------------------------------
							// trick :-)
							if(anyVarWrittenFlag && i > 0) {
								codeWriter.write(GoDefs.COMMA);
							}
							anyVarWrittenFlag = true;
							//--------------------------------------------------------------------------------
							codeWriter.write("\"" + token.getLeft().getRawText() + "\"");
							codeWriter.write(": ");
							try {
								new Token2Go(codeWriter, token.getRight(), false /* isTopLevelSelArgP does not apply */).generateCode();
							} catch (SyntaxException e) {
								throw new CodeGenException("var", m, e);
							} catch (SNHException e) {
								throw new SNHException(828384, "var", m, e);
							}
						}
					}
					else {
						// user error
						throw new CodeGenException("ill. props/vars/set/get specification" + token.toTypedInfo(), m);
					}
				}
			}
		}
		//--------------------------------------------------------------------------------
		// end of vars
		codeWriter.write("}");
		//--------------------------------------------------------------------------------
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

