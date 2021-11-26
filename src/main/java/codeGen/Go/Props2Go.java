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
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.*;

//================================================================================
public class Props2Go {
	//--------------------------------------------------------------------------------
	// /**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	private GoCodeWriter codeWriter;
	private TokenExpression props;
	private String propsKind;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Props2Go(GoCodeWriter codeWriter, TokenExpression props, String propsKind) {
		this.codeWriter = codeWriter;
		this.props = props;
		this.propsKind = propsKind;
	}

	//================================================================================
	// PROPS DEFS
	// - of PMMM, Peer, Wiring, Link or Entry (c.f. propsKind)
	// - <X>Props{"<labelName>": <expression>, ...}
	// - CAUTION: 
	// -- go automaton differentiates between eprops and vars, whereas modeler mixes them in var/set/set/get  
	// -- so pick the right ones out of the var/set/set/get pairs, recognized by <NAME> kind on the left hand side
	//--------------------------------------------------------------------------------
	// wprops example:
	//   - WProps{TTL: IVal(uc.wTtl), TXCC: SVal(PCC), MAX_THREADS: IVal(uc.maxThreadsProducer), REPEAT_COUNT: IVal(uc.wiringRepeatCountProducer)}
	// eprops examples: 
	//   - EProps{"from": SVar("$$PID"), FID: SVar("$FID"), TTL: IVal(uc.itemTtl)}
	//--------------------------------------------------------------------------------
	// propsKind .... "PMMM" | "P" | "W" | "L" | "E" 
	public void generateCode() throws SNHException, SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// - TBD: PMMM props are not yet implemented because not yet supported by go automaton
		if(propsKind.equals("PMMM")) {
			codeWriter.write("*** PMMM PROPS NOT YET IMPLEMENTED ***");
			return; 
		}
		//--------------------------------------------------------------------------------
		// for the comma writing trick: remember if any prop was written already
		boolean anyPropWrittenFlag = false;
		//--------------------------------------------------------------------------------
		// start of props
		codeWriter.write(propsKind + "Props{");
		//--------------------------------------------------------------------------------
		// any props out there?
		if(! props.isTVVEmpty()) {
			// TBD: replace by PropsDefs HASH MAP --- and make TVV protected again
			Vector<TV> tvv = props.getTVV();
			//--------------------------------------------------------------------------------
			// iterate over TVV
			for(int i = 0; i < tvv.size(); i++) {
				//--------------------------------------------------------------------------------
				// get root token of TV
				Token token = tvv.get(i).getRootToken();
				//--------------------------------------------------------------------------------
				// "<name> = <expression>" 
				if(OpDefs.isAssignmenBOPName(token.getRawText())) {
					//--------------------------------------------------------------------------------
					// is left side a VAR?
					if(token.getLeft().getKind() == IToken.Kind.VAR) {
						// just skip -> will be treated by Vars
						continue;
					}
					//--------------------------------------------------------------------------------
					// it must be an EPROP
					String errMsg = "prop assignment: " + token.toUserInfo() + "; detailed info: " + token.toTypedInfo();
					//--------------------------------------------------------------------------------
					// comma trick
					if(anyPropWrittenFlag) 
						codeWriter.write(GoDefs.COMMA);
					else
						anyPropWrittenFlag = true;
					//--------------------------------------------------------------------------------
					try {
						//--------------------------------------------------------------------------------
						// LEFT SIDE: label which could be name or array access
						GoUtil.writeCodeForLabelToken(true /* leftSideOfAssignment */, codeWriter, token.getLeft());
						//--------------------------------------------------------------------------------
						codeWriter.write(": ");
						//--------------------------------------------------------------------------------
						// RIGHT SIDE: can be anything
						//--------------------------------------------------------------------------------
						new Token2Go(codeWriter, token.getRight(), false /* isTopLevelSelArgP does not apply !!! */).generateCode();
						//--------------------------------------------------------------------------------
					} catch (SyntaxException e) {
						throw new SyntaxException(errMsg, m, e);
					} catch (SNHException e) {
						throw new SNHException(364648, errMsg, m, e);
					}
					//--------------------------------------------------------------------------------
					// IGNORE anything else -- especially this means ignore <var> = <right side>
					// - TBD: warning if not a var
				}
				//--------------------------------------------------------------------------------
				// syntax error
				else {
					throw new SyntaxException("ill. syntax in " + propsKind + " props/vars/set/get specification; expected: assignment(s), but found '" + 
							token.getRawText() + "' in expression: " + token.toTypedInfo(), m);
				}
			}
		}
		//--------------------------------------------------------------------------------
		// end of props
		codeWriter.write("}");
		//--------------------------------------------------------------------------------
	}
	

} // END OF CLASS


//================================================================================
// EOF
//================================================================================

