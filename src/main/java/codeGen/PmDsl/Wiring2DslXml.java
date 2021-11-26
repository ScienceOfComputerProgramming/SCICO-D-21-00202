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

package codeGen.PmDsl;

import pmmm.Action;
// import debug.*;
import pmmm.Guard;
import pmmm.Wiring;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================

//================================================================================
public class Wiring2DslXml {
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private PmDslCodeWriter codeWriter;
	private Wiring wiring;
	private util.replace.Director replacementsDirector;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Wiring2DslXml(PmDslCodeWriter codeWriter, util.replace.Director replacementsDirector, Wiring wiring) {
		this.codeWriter = codeWriter;
		this.wiring = wiring;
		this.replacementsDirector = replacementsDirector;
	}

	//================================================================================
	// WIRING INSTANCE
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		// opening tag
		// - nb: a possible service attribute is set below
		codeWriter.writeInd("<" + PmDslDefs.WIRING__ELEMENT_NAME);
		codeWriter.write(" " + PmDslDefs.NAME__ATTRIBUTE_NAME + "=\"" + wiring.getRawWiringNames() + "\"");
		//--------------------------------------------------------------------------------
		// gen code for service (if defined)
		String serviceName = wiring.getServiceName();
		if((serviceName != null) && (serviceName.length() > 0)) {
			codeWriter.write(" " + PmDslDefs.SERVICE__ATTRIBUTE_NAME + "=\"" + serviceName + "\"");
		}
		//--------------------------------------------------------------------------------
		codeWriter.writeln(">");
		//--------------------------------------------------------------------------------
		// wprops
		String s = wiring.getWiringWPropsDefs().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		if(! util.Util.isEmptyString(s)) {
			codeWriter.incInd();
			codeWriter.writeInd("<" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(s));
			codeWriter.writeln("</" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
			codeWriter.decInd();
		}
		//--------------------------------------------------------------------------------
		// gen code for guards
		for(int i = 0; i < wiring.getGuards().size(); i++) {
			Guard guard = wiring.getGuards().get(i);
			errMsg = "GUARD " + guard.getNumberAsString();
			try {
				codeWriter.incInd();
				new Link2DslXml(codeWriter, replacementsDirector, PmDslDefs.GUARD__ELEMENT_NAME, guard).generateCode((i + 1) /* readable guard number */);				
				codeWriter.decInd();
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			} catch (SNHException e) {
				throw new SNHException(266355, errMsg, m, e);
			}
		}
		//--------------------------------------------------------------------------------
		// gen code for actions
		for(int i = 0; i < wiring.getActions().size(); i++) {
			Action action = wiring.getActions().get(i);
			errMsg = "ACTION" + action.getNumberAsString();
			try {
				codeWriter.incInd();
				new Link2DslXml(codeWriter, replacementsDirector, PmDslDefs.ACTION__ELEMENT_NAME, action).generateCode((i + 1) /* readable action number */);
				codeWriter.decInd();
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			} catch (SNHException e) {
				throw new SNHException(631111, errMsg, m, e);
			}
		}
		//--------------------------------------------------------------------------------
		// closing tag
		// closing tag
		codeWriter.writelnInd("</" + PmDslDefs.WIRING__ELEMENT_NAME + ">");
		//--------------------------------------------------------------------------------
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

