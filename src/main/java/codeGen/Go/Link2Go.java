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

import eval.IData;
import eval.tokens.*;
import pmmm.IPmDefs;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;
import pmmm.*;

//================================================================================
// LINK .... GUARD | ACTION
//================================================================================
public class Link2Go {
	//--------------------------------------------------------------------------------
	// debug
	/**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private GoCodeWriter codeWriter;
	private String linkKind;
	private LinkInstance linkInstance;
	//--------------------------------------------------------------------------------
	Token commitIsTruePropDefToken = null;
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Link2Go(GoCodeWriter codeWriter, String linkKind, LinkInstance linkInstance) {
		this.codeWriter = codeWriter;
		this.linkKind = linkKind;
		this.linkInstance = linkInstance;
		//--------------------------------------------------------------------------------
		// create a static token for "commit = true" if not yet
		if(null == commitIsTruePropDefToken) {
			//--------------------------------------------------------------------------------
			// - create assign token
			Token assignOpToken = new Token(IToken.Kind.BOP, IOpDefs.ASSIGN);
			assignOpToken.setType(IData.Type.BOOLEAN);
			//--------------------------------------------------------------------------------
			// - create "true" token
			Token trueToken = new Token(IToken.Kind.TRUE, "true");
			trueToken.setType(IData.Type.BOOLEAN);
			//--------------------------------------------------------------------------------
			// - create "commit" token
			Token commitToken = new Token(IToken.Kind.NAME, "commit");
			commitToken.setType(IData.Type.BOOLEAN);
			//--------------------------------------------------------------------------------
			// - construct
			assignOpToken.setLeft(commitToken);
			assignOpToken.setRight(trueToken);
			//--------------------------------------------------------------------------------
			// - set my static class var
			commitIsTruePropDefToken = assignOpToken;
		}
		//--------------------------------------------------------------------------------

	}

	//================================================================================
	// GEN CODE FOR LINK
	//================================================================================
	//--------------------------------------------------------------------------------
	// speciality of the go automaton: needs "commit = true" on the last link's lprop
	public void generateCode(boolean lastLinkFlag) throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String s;
		TokenExpression expression;
		//--------------------------------------------------------------------------------
		codeWriter.writeInd("w.Add" + linkKind);
		codeWriter.write(GoDefs.LB);
		//--------------------------------------------------------------------------------
		// TBD: subpid; not yet implemented
		codeWriter.write("\"\"" + GoDefs.COMMA);
		//================================================================================
		// CONTAINER
		//================================================================================
		// is empty for NOOP 
		s = linkInstance.getContainerName();
		if(util.Util.isEmptyString(s)) {
			// TBD: what shall be generated in that case?
			s = "PIC";
		}
		codeWriter.write(s + GoDefs.COMMA);
		//================================================================================
		// OP
		//================================================================================
		try {
			codeWriter.writeln(linkSpaceOpName2Go(linkInstance.getSpaceOpName()) + GoDefs.COMMA);
		} catch (SyntaxException e1) {
			throw new CodeGenException("space op on link", m, e1);
		} catch (SNHException e1) {
			throw new SNHException(340955, "space op on link", m, e1);
		}
		//================================================================================
		// QUERY
		//================================================================================
		// Query{Typ: SEtype("<entryName>"), Count: IVal(<int>), Sel: <expression>}
		// Query{Typ: SEtype("<entryName>"), Min: IVal(<int>), Max: IVal(<int>), Sel: <expression>}
		//--------------------------------------------------------------------------------
		// - Typ and Count are mandatory
		// - Sel is optional ... i.e. query can be empty
		//--------------------------------------------------------------------------------
		codeWriter.incInd();
		codeWriter.writeInd("Query{");
		codeWriter.incInd();
		//--------------------------------------------------------------------------------
		// - TYP:
		//--------------------------------------------------------------------------------
		// is empty for NOOP
		if(! linkInstance.getIsNoopLinkFlag()) {
			s = linkInstance.getEntryTypeName();
			if(! util.Util.isEmptyString(s))
				codeWriter.write("Typ: SEtype(\"" + s + "\")" + GoDefs.COMMA);
		}
		//--------------------------------------------------------------------------------
		// - COUNT:
		//--------------------------------------------------------------------------------
		// is empty for NOOP
		// - use to min/max resolved count
		if(! linkInstance.getIsNoopLinkFlag()) {
			count2Go(linkInstance.getProcessedMinMaxIntVals());
		}
		//--------------------------------------------------------------------------------
		// - SEL: aka query in the PMMM
		// -- is optional
		//--------------------------------------------------------------------------------
		expression = linkInstance.getProcessedQueryTokenExpression();
		if(! expression.isTVVEmpty()) {
			/**/ tracer.nlprintln("query = " + expression, Level.NO, m);
			try {
				//--------------------------------------------------------------------------------
				Token rootToken = expression.getRootToken();
				if(! linkInstance.getIsNoopLinkFlag()) 
					codeWriter.writeln(GoDefs.COMMA);
				codeWriter.writeInd("Sel: ");
				new Token2Go(codeWriter, rootToken, true /* yes -- isTopLevelSelArgP applies to Sel !!! */).generateCode();
				//--------------------------------------------------------------------------------
			} catch (SyntaxException e) {
				throw new CodeGenException("query", m, e);
			} catch (SNHException e) {
				throw new SNHException(853333, "query", m, e);
			}
		}
		//--------------------------------------------------------------------------------
		// end of query 
		codeWriter.write("}");
		codeWriter.writeln(GoDefs.COMMA);
		codeWriter.decInd();
		//================================================================================
		// LPROPS
		//================================================================================
		codeWriter.writeInd("");
		try {
			//--------------------------------------------------------------------------------
			TokenExpression enhancedLPropsDefsTokenExpression = linkInstance.getProcessedLPropsDefsTokenExpression();
			if(lastLinkFlag) {
				//--------------------------------------------------------------------------------
				// artificial code: if last link, add "commit = true" to current LPROPS DEFS;
				// but it is sufficient to do it for the first peer of a peer type only,
				// - because it changes the shared peer type and otherwise we have several "commit = true" props...
				// - ie, check that prop does not yet exist; 
				// - TBD: optimze that code...;  create the prop once...
				try {
					//--------------------------------------------------------------------------------
					// add to both hash map and tvv; checks for duplicates
					enhancedLPropsDefsTokenExpression.addPropDef(commitIsTruePropDefToken);
					/**/ tracer.nlprintln("enhancedLPropsDefsTokenExpression = '" + enhancedLPropsDefsTokenExpression.toUserInfo() + "'", Level.NO, m);
					//--------------------------------------------------------------------------------
				} catch (Exception e) {
					// ok -> commit exists already in the props list
				}
			}
			//--------------------------------------------------------------------------------
			// lprops
			(new Props2Go(codeWriter, enhancedLPropsDefsTokenExpression, "L" /* link props */)).generateCode();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new CodeGenException("add 'commit=true' to lprops", m, e);
		} catch (SNHException e) {
			throw new SNHException(444333, "add 'commit=true' to lprops", m, e);
		}	
		codeWriter.writeln(GoDefs.COMMA);
		//================================================================================
		// VAR/PROP/SET/GET:
		// - CAUTION: go automaton: separates eprop and var set/get so this must be splitted....
		//================================================================================
		//--------------------------------------------------------------------------------
		// - EPRPOS
		//--------------------------------------------------------------------------------		
		codeWriter.writeInd("");
		try {
			(new Props2Go(codeWriter, linkInstance.getProcessedVarPropSetGetTokenExpression(), "E" /* entry */)).generateCode();
		} catch (SyntaxException e) {
			throw new CodeGenException("eprops", m, e);
		} catch (SNHException e) {
			throw new SNHException(333444, "eprops", m, e);
		}	
		codeWriter.writeln(GoDefs.COMMA);
		//--------------------------------------------------------------------------------
		// - VARS
		//--------------------------------------------------------------------------------
		codeWriter.writeInd("");
		try {
			(new Vars2Go(codeWriter, linkInstance.getProcessedVarPropSetGetTokenExpression())).generateCode();
		} catch (CodeGenException e) {
			throw new CodeGenException("vars", m, e);
		} catch (SNHException e) {
			throw new SNHException(334344, "vars", m, e);
		}	
		//--------------------------------------------------------------------------------
		codeWriter.writeln(GoDefs.RB);
		// decrement indentation
		codeWriter.decInd();
	}

	//================================================================================
	// COUNT
	//================================================================================
	//--------------------------------------------------------------------------------
	// <min> | <min> : <max> 
	// in case 1: generate "Count: IVal(<min>)"
	// in case 2: generate "Min: IVal(<min>), Max: IVal(<max>)"
	private void count2Go(MinMaxIntVals minMax) throws SNHException {
		int min = minMax.getMin();
		int max = minMax.getMax();
		//--------------------------------------------------------------------------------
		if(min == max) {
			//--------------------------------------------------------------------------------
			// generate "Count: IVal(<min>),"
			codeWriter.write("Count: IVal(");
			countVal2Go(min);
			codeWriter.write(")");
		} 
		else {
			//--------------------------------------------------------------------------------
			// generate "Min: IVal(<min>), Max: IVal(<max>),"
			codeWriter.write("Min: IVal(");
			countVal2Go(min);
			codeWriter.write(")");
			codeWriter.write(GoDefs.COMMA);
			codeWriter.write("Max: IVal(");
			countVal2Go(max);
			codeWriter.write(")");
		}
	}
	//--------------------------------------------------------------------------------
	// check for ALL or NONE
	// TBD: hard coded
	private void countVal2Go(int val) throws SNHException {
		if(val == IntTokenValEval.ALL)
			codeWriter.write("ALL");
		else if(val == IntTokenValEval.NONE)
			codeWriter.write("NONE");
		else
			codeWriter.write(Integer.toString(val));
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
		String upperOpName = opName.toUpperCase();
		// - TBD: hard coded...
		if(upperOpName.equals(IPmDefs.MOVE)) return("TAKE");
		else if(upperOpName.equals(IPmDefs.COPY)) return("READ");
		else if(upperOpName.equals(IPmDefs.REMOVE)) return("DELETE");
		else {
			if(upperOpName.equals(IPmDefs.CREATE) || 
					upperOpName.equals(IPmDefs.DELETE) ||
					upperOpName.equals(IPmDefs.TAKE) || 
					upperOpName.equals(IPmDefs.READ) || 
					upperOpName.equals(IPmDefs.NOOP) ||
				upperOpName.equals(IPmDefs.TEST))
				return(upperOpName);
		}
		//--------------------------------------------------------------------------------
		// ill. op name
		throw new SyntaxException("ill. link operator name = '" + opName + "'", m);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

