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
import pmmm.OpDefs;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// translate one token to go code
//--------------------------------------------------------------------------------
// expression examples:
// - IVal(123)
// - IVar("$n")
// - SVal("hallo")
// - SVar("$text")
// - SUrl("Baker")
// - BVal(true)
// - BVal("$done")
// - XValP(IVar("$n"), GREATER, IVal(0))
// - XVal(BLabel("done"), NOT, Arg{} /* unused */)
// - XValP(	XVal(ILabel("ttd"), EQUAL, IVar("$ttd")),
// - 	   	AND, 
// - 	   	XVal(ILabel("mno"), LESS, IVar("$mno")))
// - XVal(	XVal(ILabel("ttd"), EQUAL, IVar("$ttd")),
// - 	   	AND, 
// - 	   	XVal(ILabel("mno"), LESS, IVar("$mno")))
// - XValP(SLabel("typ"), EQUAL, SVal("lastCreated"))}
// - XVal(ILabel("ttd"), EQUAL, IVar("$ttd"))}
// - XValP(BLabel("done"), NOT, Arg{} /* unused */)}
// - XValP(	XVal(SLabel("tid"), EQUAL, SVar("$rep_tid")),
// - 		AND,
// - 		XVal(ILabel("mno"), LESS, IVar("$rep_mno")))}
// - XValP(SLabel("typ"), EQUAL, SVal("lastCreated"))}
// - XValP(	XVal(IVar("$rep_headPos"), GREATER, ILabel("startPos")),
// - 		AND,
// - 		XVal(IVar("$rep_headPos"), LESS_EQUAL, ILabel("endPos")))}
// - XValP(	XVal(IVar("$newTailPos"), GREATER, ILabel("startPos")),
// - 		AND,
// - 		XVal(IVar("$newTailPos"), LESS_EQUAL, ILabel("endPos")))}
// - XValP(	XVal(SLabel("wid"), EQUAL, SVar("$wid")),
// - 		AND,
// - 		XVal(ILabel("treated"), EQUAL, IVal(NOT_TREATED)))}
// - XValP(	XVal(SLabel("tid"), EQUAL, SVar("$evt_tid")),
// - 		AND,
// - 		XVal( XVal(ILabel("mno"), EQUAL, IVar("$evt_mno")),
// - 			 AND,
// - 			 XVal(SLabel("fromFrontVss"), LESS_EQUAL, SLabel("toFrontVss"))))}
// - ...
// CAUTION: no linebreaks in XValP statement before ")"
// CAUTION: XValP is used only as outmost fu for Sel !!! all others are of type Arg, but Sel is *Arg !!!
//================================================================================
public class Token2Go {
	//--------------------------------------------------------------------------------
	// debug
	/**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private GoCodeWriter codeWriter;
	// token to be translated
	private Token token;
	// flag needed to check if XValP or XVal should be used; 
	// - nb: XValP is only to be used at top level for Sel;
	private boolean isTopLevelSelArgP;
	// all operators
	// - STATIC!
	static OpDefs opDefs = new OpDefs();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Token2Go(GoCodeWriter codeWriter, Token token, boolean isTopLevelSelArgP) {
		this.codeWriter = codeWriter;
		this.token = token;
		this.isTopLevelSelArgP = isTopLevelSelArgP;
	}

	//================================================================================
	// GENERATE CODE FOR ONE TOKEN
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		// caution: compute it only for certain kinds, not here...
		String typeLetter;
		//--------------------------------------------------------------------------------
		// get kind 
		IToken.Kind kind = token.getKind();
		/**/ tracer.nlprintln("kind = " + kind, Level.NO, m);
		//--------------------------------------------------------------------------------
		// get type
		/**/ tracer.nlprintln("token = " + token.toNode_plus_TypeInProgress_Kind_Flags(), Level.NO, m);
		IData.Type type = token.getType();
		/**/ tracer.nlprintln("type = " + type, Level.NO, m);
		//--------------------------------------------------------------------------------
		// get raw text
		String rawText = token.getRawText();
		boolean isSysConst = GoCodeGen.pmmm.isSysConst(rawText);
		//--------------------------------------------------------------------------------
		// translate token 
		// - TBD: div. assertions that type correlates with kind
		switch(kind) {

		case INT:
			//--------------------------------------------------------------------------------
			codeWriter.write("IVal(" + rawText + ")");
			break;

		case STRING:
			//--------------------------------------------------------------------------------
			// could have type "STRING" or "URL"
			codeWriter.write("SVal(" + "\"" + rawText + "\")");
			break;

		case TRUE:
		case FALSE:
			//--------------------------------------------------------------------------------
			codeWriter.write("BVal(" + rawText + ")");
			break;

		case VAR:
			//--------------------------------------------------------------------------------
			// - translate type to one letter ("I", "S" or "B")
			typeLetter = translateTypeToLetter(type);
			/**/ tracer.nlprintln("typeLetter = " + typeLetter, Level.NO, m);
			//--------------------------------------------------------------------------------
			// <typeLetter>Var("<rawText>")
			codeWriter.write(typeLetter + "Var" + "(\"" + rawText + "\")");
			break;

		case NAME:
			//--------------------------------------------------------------------------------
			// - translate type to one letter ("I", "S" or "B")
			typeLetter = translateTypeToLetter(type);
			/**/ tracer.nlprintln("typeLetter = " + typeLetter, Level.NO, m);
			//--------------------------------------------------------------------------------
			translateName(isSysConst, typeLetter, type, rawText);
			break;	

		case UOP:
		case BOP:
			//--------------------------------------------------------------------------------
			// !!! SUPER SPECIALIZED AND COMPLICATED CODE: !!!
			// !!! RESOLVE QUALIFIERS "." !!!
			// - resolve "." op expression to its value
			// -- namely (a) <PMMM>.<propName> or (b) <PEER>.<propName> or (c) <WIRING>.<propName> 
			// -- (a): get value of <propName> from (CONFIG-><PMMM>)'s pmmm props defs
			// -- (b): get value of <propName> from current peer's pprops defs
			// -- (c): get value of <propName> from current wirings's wprops defs
			//--------------------------------------------------------------------------------		
			// TBD: DEPRECATED CODE, because this is alreay resolved by MTA
			if(OpDefs.isDotBOPName(token.getRawText())) {
				/**/ tracer.nlprintln("qualifier expression: " + token.toTypedInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// qualifier, i.e. <PEER>, <PMMM> or <WIRING>
				Token qualifierToken = token.getLeft(); 
				String qualifierName = qualifierToken.getRawText();
				// <propName>
				Token propLabelToken = token.getRight(); 
				//--------------------------------------------------------------------------------
				// check qualifier to be NAME
				if(qualifierToken.getKind() == IToken.Kind.NAME) {
					//--------------------------------------------------------------------------------
					// fetch value of prop def from current context
					Token propDefValueToken = null;
					//--------------------------------------------------------------------------------
					/**/ tracer.nlprintln("verify QUALIFIER = " + qualifierName + " and searching for " + propLabelToken, Level.NO, m);
					try {
						//--------------------------------------------------------------------------------
						// (a) is left side <PMMM>?
						if(qualifierName.equals(IPmDefs.PMMM_QUALIFIER)) {
							/**/ tracer.nlprintln("search in pmmm props defs: " + GoCodeGen.pmmm.getProcessedPmmmPropsDefsTokenExpression().toUserInfo(true, true), Level.NO, m);
							// search in PMMM's props defs 
							propDefValueToken = GoCodeGen.pmmm.getProcessedPmmmPropDefValueToken(propLabelToken);
						}
						//--------------------------------------------------------------------------------
						// (b) is left side <PEER>?
						else if(qualifierToken.getRawText().equals(IPmDefs.PEER_QUALIFIER)) {
							/**/ tracer.nlprintln("search in pprops defs: " + GoCodeGen.currentPeerInstance.getProcessedPPropsDefsTokenExpression(), Level.NO, m);
							// search in props defs of current peer (statically stored as context info in GoCodeGen class) 
							propDefValueToken = GoCodeGen.currentPeerInstance.getProcessedPPropDefsValueToken(propLabelToken);
						}
						//--------------------------------------------------------------------------------
						// (c) is left side <WIRING>?
						else if(qualifierToken.getRawText().equals(IPmDefs.WIRING_QUALIFIER)) {
							/**/ tracer.nlprintln("search in wprops defs: " + GoCodeGen.currentWiringInstance.getProcessedWiringWPropsDefs(), Level.NO, m);
							// search in props defs of current wiring (statically stored as context info in GoCodeGen class) 
							propDefValueToken = GoCodeGen.currentWiringInstance.getProcessedWPropDefsValueToken(propLabelToken);
						}
						//--------------------------------------------------------------------------------
					} catch (SyntaxException e) {
						throw new SyntaxException("QUALIFIER '" + qualifierName + "." + propLabelToken + "'; ", m, e);
					}
					//--------------------------------------------------------------------------------
					// plausi check
					/**/ tracer.nlprintln("propDefToken: " + propDefValueToken, Level.NO, m);
					if(null == propDefValueToken) {
						throw new SyntaxException("empty resolvedValueToken", m);
					}
					/**/ tracer.nlprintln(" resolved to " + propDefValueToken.toTypedInfo(), Level.NO, m);
					/**/ tracer.nlprintln(qualifierToken + "." + propLabelToken + " resolved to " + propDefValueToken.toTypedInfo(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// continue with code gen for right side = the resolved value !!!
					/**/ tracer.nlprintln("generate code for  " + propDefValueToken.toTypedInfo(), Level.NO, m);
					new Token2Go(codeWriter, propDefValueToken, false /* not top level sel arg pointer */).generateCode();
				}
				//--------------------------------------------------------------------------------
				else {
					throw new SyntaxException("left side of qualifier '.' expression must be '" + IPmDefs.PMMM_QUALIFIER + "', '" + 
							IPmDefs.PEER_QUALIFIER + "', or '" + IPmDefs.WIRING_QUALIFIER + "'; but is '" + qualifierName + "'", m);
				}
				//--------------------------------------------------------------------------------
				// ok done
				break; // from case of switch
			}

			//--------------------------------------------------------------------------------
			// TREATMENT OF '#' BOP
			//--------------------------------------------------------------------------------
			if(OpDefs.isHashBOPName(token.getRawText())) {
				/**/ tracer.nlprintln("HASH expression: " + token.toTypedInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// - translate array ref type to one letter ("I", "S" or "B")
				typeLetter = this.translateArrayRefTypeToLetter(type);
				// /**/ tracer.nlprintln("typeLetter = " + typeLetter, Level.NO, m);
				codeWriter.write(typeLetter);	
				//--------------------------------------------------------------------------------
				// get type of basic label token
				IData.Type basicLabelType = token.getBasicLabelType();
				/**/ tracer.println("token: " + token.toTypedInfo() + "; basicLabelType = " + basicLabelType, Level.NO, m);
				//--------------------------------------------------------------------------------
				// <X>ArrayVal(...) ...... if basic label has basic type
				// <X>ArrayLabel(...) .... if basic label has ARRAY type
				// TBD: review; basic type can only be URL namely a peer name !!!
				switch(basicLabelType) {
				//--------------------------------------------------------------------------------
				// basic type URL, ie name of a peer
				case URL:	
					codeWriter.write("ArrayVal(");
					break;
					//--------------------------------------------------------------------------------
					// other basic types
				case INT:
				case STRING:
				case BOOLEAN:
				case FLEX:	
					throw new SNHException(122211, "ill. type of array access: " + token.toTypedInfo() + "; basicLabelType = " + basicLabelType, m);
					//--------------------------------------------------------------------------------
					// array
				case STRING_ARRAY:
				case INT_ARRAY:
				case BOOLEAN_ARRAY:
				case URL_ARRAY:
				case FLEX_ARRAY:
				case UNDEFINED:
					codeWriter.write("ArrayLabel(");
					break;
					//--------------------------------------------------------------------------------
					// array ref 
				case INT_ARRAY_REF:
				case STRING_ARRAY_REF:
				case BOOLEAN_ARRAY_REF:
				case URL_ARRAY_REF:
				case FLEX_ARRAY_REF:
					throw new SNHException(144411, "ill. type of array access: " + token.toTypedInfo() + "; basicLabelType = " + basicLabelType, m);
					//--------------------------------------------------------------------------------
				default:
					throw new SNHException(116661, "ill. type of array access: " + token.toTypedInfo() + "; basicLabelType = " + basicLabelType, m);
				}
				//--------------------------------------------------------------------------------
				// label name (could contain '#'s)
				GoUtil.writeCodeForLabelToken(false /* leftSideOfAssignment */, codeWriter, token);
				//--------------------------------------------------------------------------------
				codeWriter.write(")");			
				break;
			}

			//--------------------------------------------------------------------------------
			// TREATMENT OF "NORMAL" BOP
			//--------------------------------------------------------------------------------
			else {
				// start of op-expression
				if(isTopLevelSelArgP) 
					codeWriter.write("XValP(");
				else 
					codeWriter.write("XVal(");
				//--------------------------------------------------------------------------------
				// left arg
				if(kind == IToken.Kind.BOP) {
					errMsg = "ill. left arg of binary op = " + token.getRawText() + " in " + token.toInfo();
					try {
						new Token2Go(codeWriter, token.getLeft(), false /* not top level */).generateCode();
						codeWriter.write(GoDefs.COMMA);
					} catch (SyntaxException e) {
						throw new SyntaxException(errMsg, m, e);
					} catch (SNHException e) {
						throw new SNHException(299999, errMsg, m, e);
					}
				}
				//--------------------------------------------------------------------------------
				// op
				// - TBD: stimmen die op namen?
				errMsg = "ill. op = " + token.getRawText() + " in " + token.toInfo();
				try {
					codeWriter.write(translateTokenOpName(rawText));
					codeWriter.write(GoDefs.COMMA);
				} catch (SyntaxException e) {
					throw new SyntaxException(errMsg, m, e);
				} catch (SNHException e) {
					throw new SNHException(295599, errMsg, m, e);
				}
				//--------------------------------------------------------------------------------
				// right arg
				errMsg = "ill. right arg of binary op = " + token.getRawText() + " in " + token.toInfo();
				try {
					new Token2Go(codeWriter, token.getRight(), false /* not top level */).generateCode();
				} catch (SyntaxException e) {
					throw new SyntaxException(errMsg, m, e);
				} catch (SNHException e) {
					throw new SNHException(295699, errMsg, m, e);
				}
				//--------------------------------------------------------------------------------
				// end of (x)val op-expression
				codeWriter.write(")");
			}
			break;

		case FU:
			//--------------------------------------------------------------------------------
			// - translate type to one letter ("I", "S" or "B")
			typeLetter = translateTypeToLetter(type);
			/**/ tracer.nlprintln("typeLetter = " + typeLetter, Level.NO, m);
			//--------------------------------------------------------------------------------
			// IFu(funame)
			// SFu(funame)
			// BFu(funame)
			// whereby funame is:
			// FID_FUNCTION aka "fid()"
			// CLOCK_FUNCTION aka "clock()"
			// - TBD: hard coded
			codeWriter.write(typeLetter + "Fu(");
			String fuName = rawText;
			//--------------------------------------------------------------------------------
			// fid() is string fu
			if(rawText.toUpperCase().equals("FID")) {
				codeWriter.write("FID_FUNCTION");
			}
			//--------------------------------------------------------------------------------
			// clock() is int fu
			else if(rawText.toUpperCase().equals("CLOCK")) {
				codeWriter.write("CLOCK_FUNCTION");
			}
			//--------------------------------------------------------------------------------
			// uuid() is string fu
			else if(rawText.toUpperCase().equals("UUID")) {
				codeWriter.write("UUID_FUNCTION");
			}
			//--------------------------------------------------------------------------------
			else {
				throw new SyntaxException("not supported fu name = " + fuName, m);
			}
			codeWriter.write(")");
			break;

		case EMPTY:
			//--------------------------------------------------------------------------------
			throw new SNHException(730600, "empty argument kind", m);

		default:
			//--------------------------------------------------------------------------------
			throw new SNHException(772288, "ill. token kind = " + token.getKind(), m);
			//--------------------------------------------------------------------------------
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// TRANSLATE BASIC TYPE TO "I" | "S" | "B"
	//================================================================================
	//--------------------------------------------------------------------------------
	private String translateTypeToLetter(IData.Type type) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		switch(type) {
		case INT:
			return "I";
		case STRING:
		case URL:
			return "S";
		case BOOLEAN:
			return "B";
		default:
			throw new SyntaxException("ill. type = " + type, m);
		}
	}

	//================================================================================
	// TRANSLATE ARRAY REF TYPE TO "I" | "S" | "B"
	//================================================================================
	//--------------------------------------------------------------------------------
	private String translateArrayRefTypeToLetter(IData.Type type) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		switch(type) {
		case INT_ARRAY_REF:
			return "I";
		case STRING_ARRAY_REF:
		case URL_ARRAY_REF:
			return "S";
		case BOOLEAN_ARRAY_REF:
			return "B";
		default:
			throw new SyntaxException("ill. type = " + type, m);
		}
	}

	//================================================================================
	// TRANSLATE NAME 
	//================================================================================
	//--------------------------------------------------------------------------------
	// called for kind = NAME
	private void translateName(boolean isSysConst, String typeLetter, IData.Type type, String val) throws SNHException {
		//--------------------------------------------------------------------------------
		// is val a sys const name?
		if(isSysConst) {
			codeWriter.write(typeLetter + "Val" + "(" + val + ")");
		}
		//--------------------------------------------------------------------------------
		// user prop label?
		// CAUTION: check this ***before** URL and STRING
		else if(token.getIsLabelFlag()) {
			// generate <X>Label
			codeWriter.write(typeLetter + "Label" + "(\"" + val + "\")");
		}
		//--------------------------------------------------------------------------------
		// type == URL?
		else if(type == IData.Type.URL) {
			codeWriter.write("SUrl" + "(\"" + val + "\")");
		}
		//--------------------------------------------------------------------------------
		// type == STRING ?
		else if(type == IData.Type.STRING) {
			codeWriter.write("SVal" + "(\"" + val + "\")");
		}
	}

	//================================================================================
	// TRANSLATE EXPRESSION OP NAME
	//================================================================================
	//--------------------------------------------------------------------------------
	// verify and transform expression op name 
	// TBD: code can be optimized... use switch with int defines or enums...
	private String translateTokenOpName(String opName) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// op must not be empty
		if(opName == null || opName.length() <= 0) {
			throw new SyntaxException("operator in expression is empty", m);
		}
		//--------------------------------------------------------------------------------
		// arithmetic operators:
		// - binary:
		if(OpDefs.isArithmeticAddBOPName(opName)) return ("ADD");
		if(OpDefs.isArithmeticSubBOPName(opName)) return ("SUB");
		if(OpDefs.isArithmeticMulBOPName(opName)) return ("MUL");
		if(OpDefs.isArithmeticDivBOPName(opName)) return ("DIV");
		if(OpDefs.isArithmeticModBOPName(opName)) return ("MOD");
		// - unary:
		if(OpDefs.isArithmeticPlusUOPName(opName)) return ("PLUS");
		if(OpDefs.isArithmeticMinusUOPName(opName)) return ("MINUS");		
		//--------------------------------------------------------------------------------
		// relational operators:
		if(OpDefs.isRelationalEqualBOPName(opName)) return ("EQUAL");
		if(OpDefs.isRelationalNotEqualBOPName(opName)) return ("NOT_EQUAL");
		if(OpDefs.isRelationalLessBOPName(opName)) return ("LESS");
		if(OpDefs.isRelationalLessEqualBOPName(opName)) return ("LESS_EQUAL");
		if(OpDefs.isRelationalGreaterBOPName(opName)) return ("GREATER");
		if(OpDefs.isRelationalGreaterEqualBOPName(opName)) return ("GREATER_EQUAL");
		//--------------------------------------------------------------------------------
		// boolean operators:
		// - binary:
		if(OpDefs.isBooleanAndBOPName(opName)) return ("AND");
		if(OpDefs.isBooleanOrBOPName(opName)) return ("OR");
		// - unary:
		if(OpDefs.isBooleanNotUOPName(opName)) return ("NOT");
		//--------------------------------------------------------------------------------
		// assignment operator:
		// TBD: implemented by go automaton?
		if(OpDefs.isAssignmenBOPName(opName)) return ("ASSIGN");
		//--------------------------------------------------------------------------------
		// colon operator:
		// TBD: semantics?
		if(OpDefs.isColonBOPName(opName)) return ("COLON");
		//--------------------------------------------------------------------------------
		// concat operator:
		if(OpDefs.isConcatBOPName(opName)) return ("CONCAT");
		//--------------------------------------------------------------------------------
		// dots operator: ".."
		// - is treated separately...
		if(OpDefs.isDotsBOPName(opName)) throw new SNHException(393993, "problem with dots operator", m);
		//--------------------------------------------------------------------------------
		// dot operator: "."
		// - is treated separately...
		if(OpDefs.isDotBOPName(opName)) throw new SNHException(222777, "problem with dot operator", m);
		//--------------------------------------------------------------------------------
		// ill. op name
		else throw new SyntaxException("ill. operator in expression  = '" + opName + "'", m);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

