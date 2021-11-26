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

import pmmm.Link;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// LINK .... GUARD | ACTION
//================================================================================
public class Link2DslXml {
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private PmDslCodeWriter codeWriter;
	private String linkKind;
	private Link link;
	private util.replace.Director replacementsDirector;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Link2DslXml(PmDslCodeWriter codeWriter, util.replace.Director replacementsDirector, String linkKind, Link link) {
		this.codeWriter = codeWriter;
		this.linkKind = linkKind;
		this.link = link;
		this.replacementsDirector = replacementsDirector;
	}

	//================================================================================
	// GEN CODE FOR LINK
	//================================================================================
	//--------------------------------------------------------------------------------
	// nb: caller: let link number starts from 1 for both guards and actions
	public void generateCode(int linkNumber) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String s;
		//--------------------------------------------------------------------------------
		// opening tag
		codeWriter.writeInd("<" + linkKind);
		//--------------------------------------------------------------------------------
		// link number
		codeWriter.write(" " + PmDslDefs.NUMBER__ATTRIBUTE_NAME + "=\"" + linkNumber + "\"");
		//--------------------------------------------------------------------------------
		// container
		s = link.getContainerName();
		// - is empty for NOOP 
		if(! util.Util.isEmptyString(s)) {
			codeWriter.write(" " + PmDslDefs.CONTAINER__ATTRIBUTE_NAME + "=\"" + s + "\"");
		}
		//--------------------------------------------------------------------------------
		// op
		codeWriter.write(" op=\"" + linkSpaceOpName2Go(link.getSpaceOpName()) + "\"");
		//--------------------------------------------------------------------------------
		codeWriter.incInd();
		//--------------------------------------------------------------------------------
		// not NOOP?
		if(! link.getIsNoopLinkFlag()) {
			//--------------------------------------------------------------------------------
			// entry type
			codeWriter.write(" " + PmDslDefs.ENTRY_TYPE__ATTRIBUTE_NAME + "=\"" + link.getEntryTypeName() + "\"");
			//--------------------------------------------------------------------------------
			// count
			codeWriter.write(" " + PmDslDefs.COUNT__ATTRIBUTE_NAME + "=\"" + link.getCountTokenExpression().
					toUserInfo(false /* printSemiColonAfterTv */, true /* removeOuterBrackets */) + "\"");
		}
		//--------------------------------------------------------------------------------
		codeWriter.writeln(">");
		//--------------------------------------------------------------------------------
		// query
		s = link.getQueryTokenExpression().toUserInfo(false /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		if(! util.Util.isEmptyString(s)) {
			codeWriter.writeInd("<" + PmDslDefs.QUERY__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(s));
			codeWriter.writeln("</" + PmDslDefs.QUERY__ELEMENT_NAME + ">");
		}
		//--------------------------------------------------------------------------------
		// var/props/set/get
		// - nb: use the not-merged var/props/set/get expression!
		s = link.getVarPropSetGetTokenExpression().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		if(! util.Util.isEmptyString(s)) {
			codeWriter.writeInd("<" + PmDslDefs.VAR_PROPS_SET_GET__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(s));
			codeWriter.writeln("</" + PmDslDefs.VAR_PROPS_SET_GET__ELEMENT_NAME + ">");
		}
		//--------------------------------------------------------------------------------
		// lprops
		s = link.getLPropsDefsTokenExpression().toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		if(! util.Util.isEmptyString(s)) {
			codeWriter.writeInd("<" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
			codeWriter.write(replacementsDirector.string2X(s));
			codeWriter.writeln("</" + PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME + ">");
		}
		//--------------------------------------------------------------------------------
		codeWriter.decInd();
		//--------------------------------------------------------------------------------
		// closing tag
		codeWriter.writelnInd("</" + linkKind + ">");
		// codeWriter.decInd();
	}

	//================================================================================
	// TRANSLATE LINK OP NAME
	//================================================================================
	//--------------------------------------------------------------------------------
	// verify and transform link OP name (lower/uppercase is ok)
	// - MOVE --> TAKE
	// - COPY --> READ
	// - REMOVE --> DELETE
	// - TAKE | READ | DELETE | CREATE | NOOP is ok
	// TBD: use defines....
	private String linkSpaceOpName2Go(String opName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// op must not be empty
		if(opName == null || opName.length() <= 0) {
			throw new SyntaxException("link operator is empty", m);
		}
		//--------------------------------------------------------------------------------
		// check op
		if(opName.toUpperCase().equals("MOVE"))
			return("TAKE");
		else if(opName.toUpperCase().equals("COPY"))
			return("READ");
		else if(opName.toUpperCase().equals("REMOVE"))
			return("DELETE");
		else if((opName.toUpperCase().equals("CREATE")) || (opName.toUpperCase().equals("DELETE")) ||
				(opName.toUpperCase().equals("TAKE")) || (opName.toUpperCase().equals("READ")) || 
				(opName.toUpperCase().equals("TEST")) || (opName.toUpperCase().equals("NOOP")))
			return(opName);
		//--------------------------------------------------------------------------------
		// ill. op name
		throw new SyntaxException("ill. link operator name = '" + opName + "'", m);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

