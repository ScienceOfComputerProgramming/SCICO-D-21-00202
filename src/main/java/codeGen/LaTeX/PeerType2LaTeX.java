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
// CREATED:   January 2021 
//================================================================================

package codeGen.LaTeX;

//import debug.*;
import pmmm.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;

//================================================================================

//================================================================================
public class PeerType2LaTeX{
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private LaTeXCodeWriter codeWriter;
	private PeerType peerType;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PeerType2LaTeX(LaTeXCodeWriter codeWriter, PeerType peerType) {
		this.codeWriter = codeWriter;
		this.peerType = peerType;
	}

	//================================================================================
	// PEER
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		//================================================================================
		codeWriter.writelnInd("%%=======================================================================");
		
		codeWriter.writeInd("\\subsection{");
		codeWriter.writeLaTeXCode(peerType.getPeerTypeName());
		codeWriter.writeln("} \n");
		//================================================================================
		// PEER PROPS
		//================================================================================
		//--------------------------------------------------------------------------------
		// TBD:
		// // pprops
		// String s = peerType.getPPropsDeclarations().toUserInfo();
		// if(! Util.isEmpty(s)) {
		//	...
		// }	
		//================================================================================
		// WIRINGS
		//================================================================================
		//--------------------------------------------------------------------------------
		// gen code for all wirings
		for(int i = 0; i < peerType.getWirings().size(); i++) {
			Wiring wiring = peerType.getWirings().get(i);
			errMsg = "WIRING " + wiring.getRawWiringNames();
			try {
				new Wiring2LaTeX(codeWriter, wiring).generateCode();
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, e);
			} catch (SNHException e) {
				throw new SNHException(985421, errMsg, e);
			}
		}
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

