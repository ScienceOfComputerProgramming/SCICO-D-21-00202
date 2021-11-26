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

import pmmm.*;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// generate use case dsl xml code from a Peer Model Meta Model
//================================================================================
public class PmDslCodeGen extends codeGen.BasicCodeGen implements codeGen.IBasicCodeGen {
	//--------------------------------------------------------------------------------
	// use xml replacements
	private util.replace.Director replacementsDirector = new util.replace.Director(new util.replace.xml.XmlReplacements());
	//--------------------------------------------------------------------------------
	// my code writer
	codeGen.PmDsl.PmDslCodeWriter codeWriter;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// dslXmlUseCaseDirFileName ... the directory name, where the compiled use cases are generated in
	// caution: do not use tracer in constructor!
	//--------------------------------------------------------------------------------
	public PmDslCodeGen(PmmmInstance configuredPmmm, String absoluteUcTargetPath, boolean writeAlsoToConsoleFlag) throws CodeGenException {
		//--------------------------------------------------------------------------------
		super(configuredPmmm, absoluteUcTargetPath, writeAlsoToConsoleFlag);
		//--------------------------------------------------------------------------------
		this.codeWriter = new codeGen.PmDsl.PmDslCodeWriter(useCaseNameAndConfigName, absoluteUcTargetPath, writeAlsoToConsoleFlag);
		try {
			codeWriter.openFiles();
		} catch (CodeGenException e1) {
			throw e1;
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// CODE GENERATION 
	//================================================================================
	//--------------------------------------------------------------------------------
	// translation of pmmm 2 code
	public void generateUcCode() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		// full use case name = pmmm name + config name 
		//--------------------------------------------------------------------------------
		// full use case name = pmmm name + config name 
		PmDslFixGoCode fixCodeGen = new PmDslFixGoCode(codeWriter);
		//--------------------------------------------------------------------------------
		// XML header
		// - nb: generate fus switch automatically to right file !!! keep this code as is !!!
		fixCodeGen.generateUseCaseHeader();
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(PmDslDefs.DSL_XML_FILE_ID);
		//================================================================================
		// WRITE CODE:
		//================================================================================
		//--------------------------------------------------------------------------------
		// opening tag for PM_DSL
		codeWriter.writelnInd("<" + PmDslDefs.PMMM__ELEMENT_NAME + " name=\"" + pmmm.getPmmmTypeName() + "\">");
		codeWriter.incInd();
		//--------------------------------------------------------------------------------
		// pmmm props
		String s = pmmm.getPmmmTypePropsTypes().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		if(! util.Util.isEmptyString(s)) {
			codeWriter.writeInd("<" + PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(s));
			codeWriter.writeln("</" + PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME + ">");
		}
		//--------------------------------------------------------------------------------
		// opening tag for TYPES
		codeWriter.writelnInd("<" + PmDslDefs.TYPES__ELEMENT_NAME + ">");
		codeWriter.incInd();
		//================================================================================
		// ENTRY TYPES:
		//================================================================================
		//--------------------------------------------------------------------------------
		// entry types
		for(int i = 0; i < pmmm.getPmmmType().getEntryTypes().getSize(); i++) {
			EntryType entryType = pmmm.getPmmmType().getEntryTypes().get(i);
			//--------------------------------------------------------------------------------
			// skip system entry types
			// TBD: hard coded ... cf. pmmm/PMMM.java... the same problem
			if(entryType.getEntryTypeName().equals("INIT") || entryType.getEntryTypeName().equals("EXCEPTION_WRAP"))
				continue;
			//--------------------------------------------------------------------------------
			// do code gen
			//--------------------------------------------------------------------------------
			codeWriter.writeInd("<" + PmDslDefs.ENTRY_TYPE__ELEMENT_NAME);
			//--------------------------------------------------------------------------------
			// entryType attribute
			codeWriter.write(" " + PmDslDefs.ENTRY_TYPE__ATTRIBUTE_NAME + "=\"" + entryType.getEntryTypeName() + "\"");
			codeWriter.writeln(">");
			//--------------------------------------------------------------------------------
			// eprops 
			codeWriter.incInd();
			codeWriter.writeInd("<" + PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(entryType.getEPropsTypes().
					toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */)));
			codeWriter.writeln("</" + PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME + ">");
			codeWriter.decInd();
			//--------------------------------------------------------------------------------
			codeWriter.writelnInd("</" + PmDslDefs.ENTRY_TYPE__ELEMENT_NAME + ">");
		}
		//================================================================================
		// PEER TYPES:
		//================================================================================
		//--------------------------------------------------------------------------------
		// peer types
		for(int i = 0; i < pmmm.getPmmmType().getPeerTypes().size(); i++) {
			PeerType peerType = pmmm.getPmmmType().getPeerTypes().get(i);
			errMsg = PmDslDefs.ERR_CANNOT_GEN_CODE_FOR + "PEER TYPE ";
			try {
				//--------------------------------------------------------------------------------
				// gen code for peer type
				new PeerType2DslXml(codeWriter, replacementsDirector, peerType).generateCode();
				//--------------------------------------------------------------------------------
			} catch (SyntaxException e1) {
				try {
					this.codeWriter.closeFiles();
				} catch (SNHException e2) {
					errMsg = errMsg + "; also can't close files (SNH:846521)";
				}
				throw new CodeGenException(errMsg, m, e1);
			} catch (SNHException e3) {
				try {
					this.codeWriter.closeFiles();
				} catch (SNHException e4) {
					errMsg = errMsg + "; also can't close files (SNH:321521)";
				}
				throw new SNHException(365436, errMsg, m, e3);
			}
		}
		//--------------------------------------------------------------------------------
		// closing tag for TYPES
		codeWriter.decInd();
		codeWriter.writelnInd("</" + PmDslDefs.TYPES__ELEMENT_NAME + ">");
		//--------------------------------------------------------------------------------
		// CONFIGS:
		//--------------------------------------------------------------------------------
		// opening tag for CONFIGS
		codeWriter.writelnInd("<" + PmDslDefs.CONFIGS__ELEMENT_NAME + ">");
		codeWriter.incInd();
		//--------------------------------------------------------------------------------
		// do it only for the desired config !!!
//		for(int i = 0; i <  configuredPmmm.getPmmmType().getConfigs().size(); i++) {
			Config config = pmmm.getConfig();
			codeWriter.writeInd("<" + PmDslDefs.CONFIG__ELEMENT_NAME);
			codeWriter.write(" name=\"" + config.getConfigName() + "\"");
			codeWriter.writeln(">");
			//--------------------------------------------------------------------------------
			codeWriter.incInd();
			//--------------------------------------------------------------------------------
			// pmmm props
			s = pmmm.getProcessedPmmmPropsDefsTokenExpression().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
			if(! util.Util.isEmptyString(s)) {
				codeWriter.writelnInd("<" + PmDslDefs.PMMM__ELEMENT_NAME + ">");
				codeWriter.incInd();
				codeWriter.writeInd("<" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
				codeWriter.write(replacementsDirector.string2X(s));
				codeWriter.writeln("</" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
				codeWriter.decInd();
				codeWriter.writelnInd("</" + PmDslDefs.PMMM__ELEMENT_NAME + ">");
			}
			//--------------------------------------------------------------------------------
			// peers
			for(int j = 0; j < config.getPeers().size(); j++) {
				PeerInstance peer = pmmm.getPeerInstances().get(j);
				//--------------------------------------------------------------------------------
				// opening tag
				codeWriter.writeInd("<" + PmDslDefs.PEER__ELEMENT_NAME);
				codeWriter.write(" " + PmDslDefs.NAME__ATTRIBUTE_NAME + "=\"" + peer.getPeerInstanceName() + "\"");
				codeWriter.write(" " + PmDslDefs.TYPE__ATTRIBUTE_NAME + "=\"" + peer.getPeerTypeName() + "\"");
				codeWriter.writeln(">");
				//--------------------------------------------------------------------------------
				// pprops
				s = peer.getProcessedPPropsDefsTokenExpression().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
				if(! util.Util.isEmptyString(s)) {
					codeWriter.incInd();
					codeWriter.writeInd("<" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
					codeWriter.write(s);
					codeWriter.writeln("</" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
					codeWriter.decInd();
				}
				//--------------------------------------------------------------------------------
				// closing tag
				codeWriter.writelnInd("</" + PmDslDefs.PEER__ELEMENT_NAME + ">");
			}
			//--------------------------------------------------------------------------------
			// closing tag for CONFIG
			codeWriter.decInd();
			codeWriter.writelnInd("</" + PmDslDefs.CONFIG__ELEMENT_NAME + ">");
			//--------------------------------------------------------------------------------
//		}
		//--------------------------------------------------------------------------------
		// opening tag for CONFIGS
		codeWriter.decInd();
		codeWriter.writelnInd("</" + PmDslDefs.CONFIGS__ELEMENT_NAME + ">");
		//--------------------------------------------------------------------------------
		// closing tag for PM_DSL
		codeWriter.decInd();
		codeWriter.writelnInd("</" + PmDslDefs.PMMM__ELEMENT_NAME + ">");
		codeWriter.nl();
		//--------------------------------------------------------------------------------
		// close files
		codeWriter.closeFiles();
		//--------------------------------------------------------------------------------
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
