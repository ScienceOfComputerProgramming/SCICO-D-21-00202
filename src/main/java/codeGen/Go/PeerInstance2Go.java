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

// import debug.*;
import pmmm.*;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.tracer.O;

//================================================================================
public class PeerInstance2Go {
	//--------------------------------------------------------------------------------
	// /**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	private GoCodeWriter codeWriter;
	private PeerInstance peerInstance;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PeerInstance2Go(GoCodeWriter codeWriter, PeerInstance peerInstance) {
		this.codeWriter = codeWriter;
		this.peerInstance = peerInstance;
	}
	
	//================================================================================
	// PEER
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
//		codeWriter.writelnInd("// PEER TYPE " + peerInstance.getPeerType().getName() + ":");
		//--------------------------------------------------------------------------------
		// gen code for all wirings
		for(int i = 0; i < peerInstance.getWiringInstances().size(); i++) {
			WiringInstance wiringInstance = peerInstance.getWiringInstances().get(i);
			try {
				//--------------------------------------------------------------------------------
				// !!! set wiring context in my static class var !!!
				GoCodeGen.currentWiringInstance = wiringInstance;
				//--------------------------------------------------------------------------------
				// do code gen for wiring
				new Wiring2Go(codeWriter, wiringInstance).generateCode();
				//--------------------------------------------------------------------------------
			} catch (CodeGenException e) {
				throw new CodeGenException("wiring instance " + wiringInstance.getWiringInstanceName(), m, e);
			} catch (SNHException e) {
				throw new SNHException(676543, "wiring instance " + wiringInstance.getWiringInstanceName(), m, e);
			}
		}
	}

	
} // END OF CLASS


//================================================================================
// EOF
//================================================================================

