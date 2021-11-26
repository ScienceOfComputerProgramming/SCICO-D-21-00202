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

// import debug.*;
import pmmm.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================

//================================================================================
public class PeerType2DslXml{
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private PmDslCodeWriter codeWriter;
	private PeerType peerType;
	private util.replace.Director replacementsDirector;
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PeerType2DslXml(PmDslCodeWriter codeWriter, util.replace.Director replacementsDirector, PeerType peerType) {
		this.codeWriter = codeWriter;
		this.peerType = peerType;
		this.replacementsDirector = replacementsDirector;
	}
	
	//================================================================================
	// PEER
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
		codeWriter.writeInd("<" + PmDslDefs.PEER_TYPE__ELEMENT_NAME);
		codeWriter.write(" " + PmDslDefs.NAME__ATTRIBUTE_NAME + "=\"" + peerType.getPeerTypeName() + "\"");
		codeWriter.writeln(">");
		codeWriter.incInd();
		//--------------------------------------------------------------------------------
		// pprops
		String s = peerType.getPeerTypePPropsTypes().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		if(! util.Util.isEmptyString(s)) {
			codeWriter.writeInd("<" + PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(s));
			codeWriter.writeln("</" + PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME + ">");
		}
		//--------------------------------------------------------------------------------
		// gen code for all wirings
		for(int i = 0; i < peerType.getWirings().size(); i++) {
			Wiring wiring = peerType.getWirings().get(i);
			errMsg = "WIRING " + wiring.getRawWiringNames();
			try {
				new Wiring2DslXml(codeWriter, replacementsDirector, wiring).generateCode();
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			} catch (SNHException e) {
				throw new SNHException(486322, errMsg, m, e);
			}
		}
		codeWriter.decInd();
		//--------------------------------------------------------------------------------
		// closing tag
		codeWriter.writelnInd("</" + PmDslDefs.PEER_TYPE__ELEMENT_NAME + ">");
		//--------------------------------------------------------------------------------
	}

	
} // END OF CLASS


//================================================================================
// EOF
//================================================================================

