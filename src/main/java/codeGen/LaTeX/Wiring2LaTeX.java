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
//CREATED:   January 2021 
//================================================================================

package codeGen.LaTeX;

import pmmm.Action;
//import debug.*;
import pmmm.Guard;
import pmmm.Wiring;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// CAUTION: keen in sync with go code for latex gen !!!!!!
public class Wiring2LaTeX {
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private codeGen.LaTeX.LaTeXCodeWriter codeWriter;
	private Wiring wiring;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Wiring2LaTeX(LaTeXCodeWriter codeWriter, Wiring wiring) {
		this.codeWriter = codeWriter;
		this.wiring = wiring;
	}

	//================================================================================
	// WIRING
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		LaTeXConfig latexConfig = new LaTeXConfig();
		//--------------------------------------------------------------------------------
		codeWriter.writelnInd("%%-----------------------------------------------------------------------");
		codeWriter.writelnInd("\\begin{flushleft}");
		//--
		codeWriter.writeInd("\\scalebox{");
		codeWriter.writeDouble(latexConfig.Scalebox);
		codeWriter.writeln("}{");
		//--
		codeWriter.incInd();
		// artificial alignment to the left to gain more space
		codeWriter.writelnInd("\\hspace*{-0.5cm}");
		codeWriter.writelnInd("\\begin{peerless}");
		codeWriter.incInd();
		// artificial alignment to adapt the line spacing in the wiring
		codeWriter.writeInd("\\setstretch{0.60}"); // continuation below

		//================================================================================
		// LINKS
		//================================================================================
		//--------------------------------------------------------------------------------
		int nPicLinks = wiring.getNLinks("PIC");
		int nPocLinks = wiring.getNLinks("POC");
		//--------------------------------------------------------------------------------
		// are there any pic links? or poc links; the test is not really good... 
		// it tests whether a PIC or POC is involved from my own peer (ie not from a sub peer) in any link
		// TBD: "PIC" and "POD" are hardcoded
		// the concrete number is needed for graphical heuristics below
		if(nPicLinks == 0) {
			codeWriter.write("\\noPIC]");
			// // optimization: adjust arrow length
			// latexConfig.WiringArrowRightWidth = latexConfig.WiringArrowLeftWidth + latexConfig.WiringArrowRightWidth
		}
		if(nPocLinks == 0) {
			codeWriter.write("[\\noPOC]");
			// // optimization: adjust arrow length
			// latexConfig.wiringArrowLeftWidth = latexConfig.WiringArrowLeftWidth + latexConfig.WiringArrowRightWidth
		}
		codeWriter.writeln("");

		//--------------------------------------------------------------------------------
		// optimization for graphical output - a bit experimental...:
		//--------------------------------------------------------------------------------
		// optimization for services:
		// TBD: there are no service in/out links supported yet .. only artificially generated are 1 SIN and 1 SOUT link ...
		// if there are more than 5 service related links, shrink a bit the service space and
		// if more than 10 -> shrink also the right and left link lenght
		//		int k = 0;
		//		for(int i = 0; i < len(w.Links); i++) {
		//			l := w.Links[i]
		//			if SERVICE_IN == l.Type || SERVICE == l.Type || SERVICE_OUT == l.Type {
		//				k++
		//				}
		//			}
		//		if k >= 5 {
		//			latexConfig.WiringArrowServiceSpace = latexConfig.WiringArrowServiceSpace * 0.6
		//			latexConfig.WiringArrowLeftWidth = latexConfig.WiringArrowLeftWidth * 0.7
		//			latexConfig.WiringArrowRightWidth = latexConfig.WiringArrowRightWidth * 0.7
		//		}
		//		if k >= 10 {
		//			latexConfig.WiringArrowServiceSpace = latexConfig.WiringArrowServiceSpace * 0.3
		//			latexConfig.WiringArrowLeftWidth = latexConfig.WiringArrowLeftWidth * 0.3
		//			latexConfig.WiringArrowRightWidth = latexConfig.WiringArrowRightWidth * 0.3
		//		}

		//--------------------------------------------------------------------------------
		// optimization for number of pic/poc links:
		// if there is <= 1 pic and <= 1 poc link -> increase the box height
		// so that the docu fits into the wiring box
		if(1 >= nPicLinks && 1 >= nPocLinks) {
			latexConfig.SlotHeight = latexConfig.SlotHeight * 2;
		}

		//--------------------------------------------------------------------------------
		// configuration of latex layout:
		//--------------------------------------------------------------------------------
		// hight of guard and action boxes
		codeWriter.writeInd("\\setSlotHeight[");
		codeWriter.writeDouble(latexConfig.SlotHeight);
		codeWriter.writeln("]");
		// width of guard and action boxes
		codeWriter.writeInd("\\setSlotWidth[");
		codeWriter.writeDouble(latexConfig.SlotWidth); 
		codeWriter.writeln("]");  
		// guard arrows length
		codeWriter.writeInd("\\setWiringArrowLeftWidth[");
		codeWriter.writeDouble(latexConfig.WiringArrowLeftWidth);
		codeWriter.writeln("]"); 
		// action arrows length
		codeWriter.writeInd("\\setWiringArrowRightWidth[");       
		codeWriter.writeDouble(latexConfig.WiringArrowRightWidth);  
		codeWriter.writeln("]"); 
		// height of in and out service arrows
		codeWriter.writeInd("\\setWiringArrowServiceHeight["); 
		codeWriter.writeDouble(latexConfig.WiringArrowServiceHeight); 
		codeWriter.writeln("]"); 
		// horizontal distance between service arrows
		codeWriter.writeInd("\\setWiringArrowServiceSpace[");  
		codeWriter.writeDouble(latexConfig.WiringArrowServiceSpace);  
		codeWriter.writeln("]"); 
		// width of PIC and POC containers
		codeWriter.writeInd("\\setContainerWidth[");
		codeWriter.writeDouble(latexConfig.ContainerWidth);   
		codeWriter.writeln("]"); 
		// minimal width of wiring box
		codeWriter.writeInd("\\setMinWiringWidth[");  
		codeWriter.writeDouble(latexConfig.MinWiringWidth); 
		codeWriter.writeln("]"); 
		// nl
		codeWriter.writelnInd("");
		//================================================================================
		// WIRING START
		//================================================================================
		//--
		codeWriter.writelnInd("\\BeginWiring{");
		codeWriter.incInd();
		//--
		codeWriter.writeInd("\\wiringDefinition{}{");
		codeWriter.write(wiring.getRawWiringNames());
		codeWriter.writeln("}");
		//--
		codeWriter.writelnInd("\\wiringDocuSpace");

		//================================================================================
		// PROPS
		//================================================================================
		//--------------------------------------------------------------------------------
		// wiring docu: WProps: 
		// - generate "\wiringDocuLine{<prop>}" for all props
		//--------------------------------------------------------------------------------
		// get the wprops as nice text
		String wPropsText = wiring.getWiringWPropsDefs().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		//--------------------------------------------------------------------------------
		// if wpropos are not empty
		if((wPropsText != null) && (wPropsText.length() > 0)) {
			//--------------------------------------------------------------------------------
			// helpers:
			String curInd = codeWriter.getCurInd();
			// - super tricky: but otherwise the "\" is wrong...
			String docuLineStartFirst = "\\wiringDocuLine{";
			String docuLineStart = "\\\\wiringDocuLine{";
			String docuLineEnd = "}";
			//--------------------------------------------------------------------------------
			// add artificial ";" at the beginning and 
			// replace special chars in wprops now, ie before enhancing the string with latex code
			String s = docuLineStartFirst.concat(codeWriter.replace(wPropsText));
			//--------------------------------------------------------------------------------
			// nb: all props are terminated by toUserInfo() with ";" ... we rely on that 
			// tricky: replace ";" by ";<docuLineEnd>\n<ind><docuLineStart>"
			// tricky; remove blank after ";" (there should be one, anyhow, always)...
			s = s.replaceAll(";[ ]*", ";" + docuLineEnd + "\n" + curInd + docuLineStart);
			//--------------------------------------------------------------------------------
			// - add "<docuLineEnd>" ... last comment is empty, no problem...
			s = s.concat("" + docuLineEnd);
			//--------------------------------------------------------------------------------
			// do write
			codeWriter.writelnInd(s);
		}		
		//--------------------------------------------------------------------------------
		codeWriter.writelnInd("\\wiringDocuSpace");
		codeWriter.writelnInd("\\wiringDocuEnd");
		//--------------------------------------------------------------------------------
		// end of wiring
		codeWriter.decInd();
		codeWriter.writelnInd("}");

		//================================================================================
		// SERVICE
		//================================================================================
		//--------------------------------------------------------------------------------
		// output possibly services first (in the right order ... nb: there is at most one service anyhow... ):
		String serviceName = wiring.getServiceName();
		if((serviceName != null) && (serviceName.length() > 0)) {
			//--------------------------------------------------------------------------------
			// begin service
			codeWriter.writeInd("\\BeginService{");
			codeWriter.writeLaTeXCode(serviceName);
			// not needed: 
			// - codeWriter.write(":");
			// - codeWriter.writeLaTeXCode(service wrapper...);
			codeWriter.writeln("}");
			codeWriter.incInd();
			//--------------------------------------------------------------------------------
			// SERVICE INs/OUTs:
			// TBD: for now, do (artifical) service IN: i.e. take everything and put it into the service 
			// TBD: differentiate between:
			// - codeWriter.writelnInd("    \\inCopyServiceArrow");
			// - codeWriter.writelnInd("    \\inMoveServiceArrow");
			// - codeWriter.writelnInd("    \\inTestServiceArrow");
			// - codeWriter.writelnInd("    \\inCreateServiceArrow");
			// - codeWriter.writelnInd("    \\inNoopServiceArrow");
			//--------------------------------------------------------------------------------
			// SERVICE INs:
			codeWriter.writeInd("\\inMoveServiceArrow");
			// TBD: MOVE et al. are hard coded
			// - {etype}{cnt}{sel}{eprops}{vars}{lprops}:
			codeWriter.writelnLaTeXCode("{" + "*" + "}" + "{" + "ALL" + "}" + "{" + "" + "}" + "{" + "" + "}" +  "{" + "" + "}" + "{" + "" + "}");
			//--------------------------------------------------------------------------------
			// SERVICE CALL:
			codeWriter.writeInd("\\callServiceArrow");
			// TBD: this is an artifical CALL link (with empty link kind):
			// TBD: CALL is hard coded ... super artificial...
			// - {etype}{cnt}{sel}{eprops}{vars}{lprops}:
			codeWriter.writelnLaTeXCode("{" + "\\bf CALL" + "}" + "{" + "" + "}" + "{" + "" + "}" + "{" + "" + "}" +  "{" + "" + "}" + "{" + "" + "}");
			//--------------------------------------------------------------------------------
			// SERVICE OUTs:
			// only move exists for out
			codeWriter.writeInd("\\outServiceArrow");
			// TBD: this is an artifical SOUT link (with empty link kind):
			// TBD: MOVE is hard coded
			// - {etype}{cnt}{sel}{eprops}{vars}{lprops}:
			codeWriter.writelnLaTeXCode("{" + "*" + "}" + "{" + "ALL" + "}" + "{" + "" + "}" + "{" + "" + "}" +  "{" + "" + "}" + "{" + "" + "}");
			//--------------------------------------------------------------------------------
			// end service
			codeWriter.writelnInd("\\EndService \n");
			codeWriter.decInd();
		}

		//================================================================================
		// GUARDS
		//================================================================================
		//--------------------------------------------------------------------------------
		for(int i = 0; i < wiring.getGuards().size(); i++) {
			Guard guard = wiring.getGuards().get(i);
			errMsg = "GUARD " + wiring.getRawWiringNames();
			try {
				new Link2LaTeX(codeWriter, guard).generateCode();
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			} catch (SNHException e) {
				throw new SNHException(568302, errMsg, m, e);
			}
		}

		//================================================================================
		// ACTIONS
		//================================================================================
		//--------------------------------------------------------------------------------
		for(int i = 0; i < wiring.getActions().size(); i++) {
			Action action = wiring.getActions().get(i);
			errMsg = "ACTION " + wiring.getRawWiringNames();
			try {
				new Link2LaTeX(codeWriter, action).generateCode();
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			} catch (SNHException e) {
				throw new SNHException(599302, errMsg, m, e);
			}
		}

		//--------------------------------------------------------------------------------
		codeWriter.writelnInd("\\EndWiring");
		codeWriter.decInd();
		codeWriter.writelnInd("\\end{peerless}");
		codeWriter.decInd();
		codeWriter.writelnInd("}");
		codeWriter.writelnInd("\\end{flushleft}\n");
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

