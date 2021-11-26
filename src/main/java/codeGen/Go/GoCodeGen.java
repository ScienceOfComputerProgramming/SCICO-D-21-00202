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

import codeGen.*;
import pmmm.*;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.tracer.O;

//================================================================================
// generate use case go code for the go automaton from a Peer Model Meta Model
// and then go to test directory and start it with "go test" :-)
//================================================================================
public class GoCodeGen extends BasicCodeGen implements IBasicCodeGen {
	//--------------------------------------------------------------------------------
	// my code writer
	codeGen.Go.GoCodeWriter codeWriter;
	//--------------------------------------------------------------------------------
	String dir;
	//--------------------------------------------------------------------------------
	// CONTEXT INFO FOR CODE GEN
	// - tricky: needed to resolve qualifier expressions
	//--------------------------------------------------------------------------------
	// - current peer
	// --- needed to resolve <PEER>.<prop> expressions
	public static PeerInstance currentPeerInstance; 
	//--------------------------------------------------------------------------------
	// - current wiring instance
	// --- needed to resolve <WIRING>.<prop> expressions
	public static WiringInstance currentWiringInstance; 
	//--------------------------------------------------------------------------------
	private String relativeUcPath = "";

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// dir ... the directory name under "src" in go automaton, where the compiled use cases are generated in
	//--------------------------------------------------------------------------------
	public GoCodeGen(PmmmInstance pmmmInstance, String relativeUcPath, String absoluteUcTargetPath, String dir, boolean writeAlsoToConsoleFlag) throws SNHException, CodeGenException {
		//--------------------------------------------------------------------------------
		super(pmmmInstance, absoluteUcTargetPath, writeAlsoToConsoleFlag);
		this.relativeUcPath = relativeUcPath;
		this.dir = dir;
		//--------------------------------------------------------------------------------
		codeWriter = new codeGen.Go.GoCodeWriter(useCaseNameAndConfigName, relativeUcPath, absoluteUcTargetPath, writeAlsoToConsoleFlag);
		try {
			codeWriter.openFiles();
		} catch (CodeGenException e1) {
			throw e1;
		} catch (Exception e1) {
			throw e1;
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// DO THE GO CODE GENERATION FOR THE PMMM
	// - there are different files to be generated
	// - cf. docu of the go automaton :-)
	//================================================================================
	//--------------------------------------------------------------------------------
	// translation of pmmm 2 go code
	public void generateUcCode() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// debug:
		// /**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// full use case name = pmmm name + config name 
		GoFixGoCode autoGoCode = new GoFixGoCode(codeWriter, relativeUcPath, absoluteUcTargetPath, useCaseNameAndConfigName, dir);
		//--------------------------------------------------------------------------------
		// generate code for the following files: 

		//================================================================================
		// TEST:
		//================================================================================
		//--------------------------------------------------------------------------------
		try {
			// - nb: generate fus switch automatically to right file !!! keep this code as is !!!
			autoGoCode.generateTestFile();
		} catch (SNHException e) {
			throw new SNHException(783465, "can't generate test file", m, e);
		}

		//================================================================================
		// TESTCASE:
		//================================================================================
		try {
			//--------------------------------------------------------------------------------
			autoGoCode.generateTestCaseFile();
		} catch (SNHException e) {
			throw new SNHException(265874, "can't generate testcase file", m, e);
		}

		//===========:=====================================================================
		// USE CASE
		//================================================================================
		//--------------------------------------------------------------------------------
		// header
		try {
			autoGoCode.generateUseCaseHeader();
		} catch (SNHException e) {
			throw new SNHException(220093, "can't generate use case header", m, e);
		}
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(GoDefs.USE_CASE_FILE_ID);
		//--------------------------------------------------------------------------------
		// meta model
		codeWriter.writelnInd("///////////////////////////////////////////////////////////");
		codeWriter.writelnInd("// add meta model for " + useCaseNameAndConfigName);
		codeWriter.writelnInd("///////////////////////////////////////////////////////////");
		codeWriter.writelnInd("");
		codeWriter.writelnInd("func (uc *UseCase" + useCaseNameAndConfigName + ") AddMetaModel(ps *PeerSpace) { // <<<<<< ");
		codeWriter.incInd();
		codeWriter.writelnInd("");
		codeWriter.writelnInd("p := new(Peer)");
		codeWriter.writelnInd("w := new(Wiring)");
		codeWriter.writelnInd("");
		//================================================================================
		// PEERS:
		//================================================================================
		//--------------------------------------------------------------------------------
		// gen code for all peers
		for(int i = 0; i < pmmm.getPeerInstances().size(); i++) {
			// nb: peer is stored statically in my class -- as context info !!!
			GoCodeGen.currentPeerInstance = pmmm.getPeerInstances().get(i);
			String peerTypeName = currentPeerInstance.getPeerTypeName(); 
			String peerName = currentPeerInstance.getPeerInstanceName(); 
			codeWriter.writelnInd("//============================================================");
			codeWriter.writelnInd("// PEER " + peerTypeName + ":" + peerName);
			codeWriter.writelnInd("//============================================================");
			codeWriter.writelnInd("");
			codeWriter.writelnInd("p = NewPeer(\"" + peerName + "\")");
			codeWriter.writelnInd("");
			//--------------------------------------------------------------------------------
			try {
				//--------------------------------------------------------------------------------
				// gen code for peer instance
				new PeerInstance2Go(codeWriter, currentPeerInstance).generateCode();
				//--------------------------------------------------------------------------------
				// gen code for adding peer to peer space
				codeWriter.writelnInd("//------------------------------------------------------------");
				codeWriter.writelnInd("// add peer to peer space:");
				codeWriter.writelnInd("ps.AddPeer(p)");
				codeWriter.writelnInd("//------------------------------------------------------------");
				codeWriter.writelnInd("");
				//--------------------------------------------------------------------------------
			} catch (CodeGenException e1) {
				try {
					this.codeWriter.closeFiles();
				} catch (Exception e2) {}
				throw new CodeGenException("peer type " + currentPeerInstance.getPeerTypeName(), m, e1);
			}
		}
		//--------------------------------------------------------------------------------
		codeWriter.decInd();
		codeWriter.writelnInd("}");
		codeWriter.writelnInd("");
		codeWriter.writelnInd("");
		//--------------------------------------------------------------------------------
		String errMsg = "";
		try {
			//--------------------------------------------------------------------------------
			// init method
			errMsg = "generate init method";
			autoGoCode.generateUseCaseInit();
			//--------------------------------------------------------------------------------
			// built-in services
			errMsg = "generate built-in services";
			autoGoCode.generateBuiltInServices();
			//--------------------------------------------------------------------------------
			// TBD: user defined services
			//--------------------------------------------------------------------------------
			// tail
			errMsg = "generate tail";
			autoGoCode.writeAutoFileTail();
			//--------------------------------------------------------------------------------
			// close files
			errMsg = "close files";
			codeWriter.closeFiles();
			//--------------------------------------------------------------------------------
		} catch (SNHException e) {
			throw new SNHException(299477, "can't " + errMsg, m, e);
		}
		//--------------------------------------------------------------------------------
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
