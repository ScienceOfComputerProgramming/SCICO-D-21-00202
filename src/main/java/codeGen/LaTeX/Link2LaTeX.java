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

//================================================================================
package codeGen.LaTeX;

import pmmm.INames;
import pmmm.Link;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;
import pmmm.IPmDefs;

//================================================================================
// CAUTION: keen in sync with go code for latex gen !!!!!!
public class Link2LaTeX {
	//--------------------------------------------------------------------------------
	// /**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	private codeGen.LaTeX.LaTeXCodeWriter codeWriter;
	private Link link;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Link2LaTeX(LaTeXCodeWriter codeWriter, Link link) {
		this.codeWriter = codeWriter;
		this.link = link;
	}

	//================================================================================
	// LINK
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// LaTeXConfig latexConfig = new LaTeXConfig();

		codeWriter.writeInd("\\");
		//================================================================================
		// CONTAINER
		//================================================================================
		//--------------------------------------------------------------------------------
		// TBD: "PIC", "POC" ... hard coded...
		if(link.getContainerName().equals("PIC")) {
			codeWriter.write("pic");
		}
		else if(link.getContainerName().equals("POC")) {
			codeWriter.write("poc");
		}
		// TBD: else if is sub peer:
		// - codeWriter.write("subPeer");
		//================================================================================
		// OP
		//================================================================================
		//--------------------------------------------------------------------------------
		// Op: 
		// - Copy, Move, Test, Delete or Create ... this writing is required by latex
		String spaceOpName = link.getSpaceOpName().toUpperCase();
		//--------------------------------------------------------------------------------
		// hard coded...
		//--------------------------------------------------------------------------------
		if(spaceOpName.equals(IPmDefs.MOVE) || spaceOpName.equals(IPmDefs.TAKE)) 
			spaceOpName = "Move";
		//--------------------------------------------------------------------------------
		else if(spaceOpName.equals(IPmDefs.COPY) || spaceOpName.equals(IPmDefs.READ)) 
			spaceOpName = "Copy";
		//--------------------------------------------------------------------------------
		else if(spaceOpName.equals(IPmDefs.DELETE) || spaceOpName.equals(IPmDefs.REMOVE)) 
			spaceOpName = "Delete";
		//--------------------------------------------------------------------------------
		else if(spaceOpName.equals(IPmDefs.CREATE)) 
			spaceOpName = "Create";
		//--------------------------------------------------------------------------------
		else if(spaceOpName.equals(IPmDefs.TEST)) 
			spaceOpName = "Test";
		//--------------------------------------------------------------------------------
		// NOOP:
		// tricky: add artificial container for latex command...
		// for guards do pic and for actions do poc
		else if(spaceOpName.equals(IPmDefs.NOOP)) {
			if(link.getLinkKind() == INames.LinkKind.GUARD)
				spaceOpName = "picNoop";
			else
				spaceOpName = "pocNoop";
		}
		//--------------------------------------------------------------------------------
		else 
			throw new SyntaxException("ill. Op name on link = " + link.getSpaceOpName(), m);
		// write
		//--------------------------------------------------------------------------------
		codeWriter.write(spaceOpName);
		//================================================================================
		// LINK KIND
		//================================================================================
		//--------------------------------------------------------------------------------
		// ltype: 
		// - "Action" or "Guard
		codeWriter.write(linkKind2LaTeX(link.getLinkKind()));
		//================================================================================
		// SUB PEER
		//================================================================================
		//--------------------------------------------------------------------------------
		// TBD: if sub peer:
		// - codeWriter.writelnInd("{%s}", l.SubPid);
		//================================================================================
		// CNT, QUERY, EPROPS, LPROPS
		//================================================================================
		//--------------------------------------------------------------------------------
		// TBD: var/props/set/get is put to eProps and vars are just skipped....
		// - {etype}{cnt}{sel}{eprops}{vars}{lprops}:
		// nb: use the not-merged var/props/set/get expression!
		codeWriter.writelnLaTeXCode("{" + 
				link.getEntryTypeName() + "}" + 
				"{" + link.getCountTokenExpression().toUserInfo(false /* printSemiColonAfterTv */, true /* removeOuterBrackets */) + "}" + 
				"{" + link.getQueryTokenExpression().toUserInfo(false /* printSemiColonAfterTv */, true /* removeOuterBrackets */) + "}" + 
				"{" + link.getVarPropSetGetTokenExpression().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */) + "}" +  
				"{" + "" + "}" + // empty...
				"{" + link.getLPropsDefsTokenExpression().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */) + 
				"}");
		//--------------------------------------------------------------------------------

	}

	//--------------------------------------------------------------------------------
	// convert link kind to right string as required by go code
	private String linkKind2LaTeX(INames.LinkKind linkKind) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		switch(linkKind) {
		case GUARD:
			return "Guard";
		case ACTION:
			return "Action";
		case SIN:
			return "Sin";
		case CALL:
			return "Scall";
		case SOUT:
			return "Sout";
		case UNDEFINED:
			break;
		}
		throw new SNHException(204030, "ill. link kind = " + linkKind, m);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

