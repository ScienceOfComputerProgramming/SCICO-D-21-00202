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
// CREATED:   December 2020 
//================================================================================

package eval.tokens;

import java.util.HashMap;
import java.util.Stack;

import eval.qualifiers.*;
import eval.types.TokenTypeEval;

import java.util.Vector;

import eval.IData;
import pmmm.*;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// adds peer model syntax logic to TVV
// - CAUION: keep phase up-to-date at any change of TVV!!!
public class TokenExpression extends TVV implements IEvaluation, IMta {
	//--------------------------------------------------------------------------------
	// tracer
	public Tracer tracer = new Tracer();
	//================================================================================
	//--------------------------------------------------------------------------------
	// different forms of representing the expression according to the results of the parsing phases:
	// - raw .... after input parsing: 
	// - tokenized (super TVV) ... after tokenization 
	// - typed token trees (same TVV) ... after type eval
	// - qualifier expressions are replaced by their values (this.qualifiersResolvedTVV)
	//--------------------------------------------------------------------------------
	// raw:
	// - CAUION: keep raw up-to-date with any change of TVV!!!
	protected String raw = "";

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public TokenExpression() {
		super();
	}
	//--------------------------------------------------------------------------------
	public TokenExpression(String raw) {
		super();
		this.raw = raw;
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setRaw(String raw) {
		this.raw = raw;
	}
	//--------------------------------------------------------------------------------
	public void setTVV(Vector<TV> tvv) {
		this.tokenVV = tvv;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getRaw() {
		return raw;
	}
	//--------------------------------------------------------------------------------
	// TBD: needed for go code gen
	public Vector<TV> getTVV() {
		return tokenVV;
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add prop def if not yet
	// - the full prop def is contained in token
	// - the label is just for convenience...
	public void addPropDef(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// TRICK
		// - construct an artificial token expression that contains token
		TokenExpression te1 = new TokenExpression();
		te1.phase = "EVALUATED";
		te1.add(new TV(token));
		// - clone me
		TokenExpression te2 = new TokenExpression();
		te2.deepCloneFromTokenExpression(this);
		// - clone me from me plus the artificial token expression
		this.mergeAssignments(te2, te1);
		/**/ tracer.println("merged = '" + this.toUserInfo(true, true) + "'", Level.NO, m);
	}

	//================================================================================
	// COPY
	//================================================================================
	//--------------------------------------------------------------------------------
	// deep copy
	public void deepCloneFromTokenExpression(TokenExpression tokenExpressionToBeCloned) {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		this.raw = new String(tokenExpressionToBeCloned.raw);
		this.phase = new String(tokenExpressionToBeCloned.phase);
		super.deepCloneFromTokenVV(tokenExpressionToBeCloned);
		//--------------------------------------------------------------------------------
		/**/ tracer.println("token expression: " + tokenExpressionToBeCloned.toUserInfo(true, true), Level.NO, m);
		/**/ tracer.println("cloned: " + this.toUserInfo(true, true), Level.NO, m);
		/**/ tracer.println("cloned raw: " + this.raw, Level.NO, m);
	}
	//--------------------------------------------------------------------------------
	// not deep cloning
	public void shallowCloneFromTokenExpression(TokenExpression tokenExpressionToBeCloned) {
		this.raw = new String(tokenExpressionToBeCloned.raw);
		this.phase = tokenExpressionToBeCloned.phase;
		super.shallowCloneFromTokenVV(tokenExpressionToBeCloned);
	}

	//================================================================================
	// TEST
	//================================================================================
	//--------------------------------------------------------------------------------
	// is raw empty string?
	public boolean isRawEmpty() {
		return util.Util.isEmptyString(raw);
	}
	//--------------------------------------------------------------------------------
	// any tvv tokens out there?
	public boolean isTVVEmpty() {
		return super.isTVVEmpty();
	}

	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	//================================================================================
	//================================================================================

	//================================================================================
	// TOKENIZE
	//================================================================================
	//--------------------------------------------------------------------------------
	// translate raw string into nested token tree(s);
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// empty?
		if(raw.length() == 0)
			return;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("========================================================="
				+ "===========================================================", Level.NO, new Object(){});
		/**/ tracer.println("TOKENIZE: " + raw, Level.NO, new Object(){}); 
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// mince + meld
			TokenExpression tokenExpression = new Tokens2Expressions(new Raw2Tokens(raw).mince()) . meld();
			//--------------------------------------------------------------------------------
			// set
			this.tokenVV = tokenExpression.tokenVV;
			//--------------------------------------------------------------------------------
			/**/ tracer.println("TOKENIZED: " + tokenVV, Level.ME, new Object(){}); 
		} catch(SyntaxException e) {
			// just pass te exception further; there is no extra news...
			throw e;
		} catch(SNHException e) {
			throw new SNHException(713900, e.getPmErrorMsg(), m);
		}
	}

	//================================================================================
	// RESOLVE QUALIFIER TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// in all my props defs resolve the types of all qualifiers
	public void resolveQualifierTypes(QualifierContext qualifierContext) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// go through my tvv 
		for(int i = 0; i < tokenVV.size(); i++) {
			TV tv = tokenVV.get(i);
			//--------------------------------------------------------------------------------
			if(! tv.isEmpty()) {
				//--------------------------------------------------------------------------------
				// get root token
				Token rootToken;
				try {
					rootToken = tv.getRootToken();
				} catch (SyntaxException e) {
					throw new SyntaxException("'" + tv.toUserInfo() + "'", m, e);
				}
				//--------------------------------------------------------------------------------
				// resolve qualifier types for the root token
				try {
					rootToken.resolveQualifierTypes(qualifierContext);
				} catch (SyntaxException e) {
					throw new SyntaxException("'" + tv.toUserInfo() + "'", m, e);
				}
				//--------------------------------------------------------------------------------
			}
		}
	}

	//================================================================================
	// DATA TYPE EVAL 
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate all token types
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println(this.toInfo(), Level.NO, m); 
		//--------------------------------------------------------------------------------
		setPhase("EVALUATING");
		//--------------------------------------------------------------------------------
		if (! isTVVEmpty()) { 
			/**/ tracer.println(context.toMsg() + "----> TOKEN TREE " + this, Level.NO, new Object(){}); 
			for(int i = 0; i < size(); i++) {
				//--------------------------------------------------------------------------------
				// nb: after tokenization each TV has max. 1 root token
				// - this is implicitly secured here by taking only the first tv from tvv 
				Token token = get(i).get(0);
				//--------------------------------------------------------------------------------
				// do the eval
				String errMsg = "in: '" + this.toUserInfo(true, true) + "'";
				try {
					(new TokenTypeEval(context, token)).typeEval();
				} catch (SyntaxException e) {
					throw new SyntaxException(errMsg, m, e);
				} catch (SNHException e) {
					throw new SNHException(268965, context.toMsg(), m, e);
				}
			}
			/**/ this.setPhase("EVALUATED"); // redundant, but good for the following trace
			/**/ tracer.println(context.toMsg() + "----> TYPED TOKEN TREE " + this, Level.NO, new Object(){}); 
		}
		//--------------------------------------------------------------------------------
		this.setPhase("EVALUATED");
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// PLAUSI CHECK
	//================================================================================
	//--------------------------------------------------------------------------------
	// verify that
	// (A) each TV must have exactly 1 root token
	// (B) no UNDEFINED types exist
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("start: ", Level.NO, new Object(){});
		//--------------------------------------------------------------------------------
		// if empty -> nothing to be checked
		if(isTVVEmpty())
			return;
		//--------------------------------------------------------------------------------
		// (A)
		for(int i = 0; i < size(); i++) {
			if(get(i).size() != 1) {
				throw new SyntaxException("ill. expression: '" + raw + "'; transformed to: '" + toUserInfo(true, true) + "'", m);
			}
		}
		//--------------------------------------------------------------------------------
		// (B)
		try {
			for(int i = 0; i < size(); i++) {
				Token token = get(i).getRootToken();
				token.verifyTypeDefinedness();
			}
		} catch (SNHException e) {
			throw new SNHException(340202, "UNDEFINED type in: " + toUserInfo() + " " + toTypedInfo() + "'", m, e);
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	//================================================================================
	// MTA INTERFACE IMPLEMENTATION
	//================================================================================
	//================================================================================

	//================================================================================
	// MTA: RESOLVE QUALIFLIER VALUES AND QUANTIFIER EXPRESSIONS
	//================================================================================
	//--------------------------------------------------------------------------------
	// iteratively (could be nested) resolve my tvv
	// - resolve values of qualifier operators ('.') 
	// - resolve implies operators ('->') of quantifier expressions
	// - resolve comma (',') separated quantifier expressions 
	// finally resolve FLEX types;
	// LIMITATION: in left left side of '->' all qualifiers must be *statically* resolvable
	// caution: finally refresh raw !!!
	public void mta(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("before mta: " + this.toUserInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// iteration
		this._mta(curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
		/**/ tracer.println("after resolve qualifiers: " + this.toUserInfo(true, true), Level.NO, m);
		//--------------------------------------------------------------------------------
		// resolve FLEX types 
		this.resolveFlexTypes();
		//--------------------------------------------------------------------------------
		// refresh raw from tvv
		this.raw = this.toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		/**/ tracer.println("after mta: " + this.toUserInfo(), Level.NO, m);
	}

	//================================================================================
	// RESOLVE QUALIFLIER VALUES AND QUANTIFIER EXPRESSIONS
	//================================================================================
	//--------------------------------------------------------------------------------
	// caution: finally refresh raw !!!
	public void _mta(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		// nb: assertions on params not possible... depend on context
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "resolve qualifier values in '" + this.toUserInfo() + "'";
		//--------------------------------------------------------------------------------
		// generate a new TVV
		TokenExpression resultTokenExpression = new TokenExpression();
		// - TBD: hardcoded
		resultTokenExpression.phase = "EVALUATED";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			for(int i = 0; i < this.tokenVV.size(); i++) {
				//--------------------------------------------------------------------------------
				// get root token
				Token rootToken = get(i).getRootToken();
				//--------------------------------------------------------------------------------
				// create return param !!!
				Token retToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
				//================================================================================
				// BOP
				//================================================================================
				//--------------------------------------------------------------------------------
				if(rootToken.kind == IToken.Kind.BOP) {
					//================================================================================
					// '.'
					//================================================================================
					if(OpDefs.isDotBOPName(rootToken.getRawText())) {
						//--------------------------------------------------------------------------------
						/**/ tracer.println("is DOT op", Level.NO, m);
						//--------------------------------------------------------------------------------
						// resolve qualifier value
						(new QualifierValEval(rootToken, retToken, curPmmmInstance, curPeerInstance, curWiringInstance)).qualifierValEval();
						//--------------------------------------------------------------------------------
						// add
						for(int j = 0; j < this.size(); j++) {
							resultTokenExpression.add(new TV(retToken));
						}
						/**/ tracer.println("retToken: " + retToken.toUserInfo(), Level.NO, m);
					}
					//================================================================================
					// '->'
					//================================================================================
					//--------------------------------------------------------------------------------
					// !!! CAUTION: there must be at most one '->' in a token !!!
					else if(OpDefs.isImpliesBOPName(rootToken.getRawText())) {
						//--------------------------------------------------------------------------------
						/**/ tracer.println("is IMPLIES op", Level.NO, m);
						//--------------------------------------------------------------------------------
						// 1.) resolve qualifier values in left side (ie in quantifiers)
						Token leftRetToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
						(new QualifierValEval(rootToken.left, leftRetToken, curPmmmInstance, curPeerInstance, curWiringInstance)).qualifierValEval();
						//--------------------------------------------------------------------------------
						// 2.) resolve '->'
						TokenExpression tempTokenExpression = resolveImply(leftRetToken, rootToken.right, 
								curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
						/**/ tracer.println("tempTokenExpression: " + tempTokenExpression.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// 3.) resolve qualifier values in result (aka the transformed right side)
						tempTokenExpression._mta(curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
						//--------------------------------------------------------------------------------
						// 4.) add
						for(int j = 0; j < tempTokenExpression.size(); j++) {
							resultTokenExpression.add(tempTokenExpression.get(j));
						}
					}
					//================================================================================
					// other BOP
					//================================================================================
					//--------------------------------------------------------------------------------
					// nb: left and right side must reduce to exactly one root token
					// nb: boolean ops might in turn contain '-'
					// - eg (EXISTS...) AND (FORALL ...) 
					else {					
						//--------------------------------------------------------------------------------
						/**/ tracer.println("other BOP: " + rootToken.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// resolve qualifier values in left side
						// - construct artificial token expression
						/**/ tracer.println("rootToken.left: " + rootToken.left.toUserInfo(), Level.NO, m);
						TokenExpression leftTokenExpression = new TokenExpression();
						leftTokenExpression.phase = "EVALUATED"; // TBD: hardcoded
						leftTokenExpression.add(new TV(rootToken.left));
						/// leftTokenExpression.setRaw(leftTokenExpression.toUserInfo());
						// - mta it
						leftTokenExpression._mta(curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
						/**/ tracer.println("leftTokenExpression: " + leftTokenExpression.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// resolve qualifier values in right side
						// - construct artificial token expression
						/**/ tracer.println("rootToken.right: " + rootToken.right.toUserInfo(), Level.NO, m);
						TokenExpression rightTokenExpression = new TokenExpression();
						rightTokenExpression.phase = "EVALUATED"; // TBD: hardcoded
						rightTokenExpression.add(new TV(rootToken.right));
						// - mta it
						rightTokenExpression._mta(curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
						/**/ tracer.println("rightTokenExpression: " + rightTokenExpression.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// verify and combine
						// - both must have been reduced to one root token
						if(leftTokenExpression.tokenVV.size() != 1 || rightTokenExpression.tokenVV.size() != 1) {
							// TBD: better user error message...
							throw new SyntaxException("ill. expression: " + rootToken.toUserInfo(), m);
						}
						// - get TVs
						TV leftTV = leftTokenExpression.tokenVV.get(0);
						TV rightTV = rightTokenExpression.tokenVV.get(0);
						// - verify their size to be 1
						if(leftTV.size() != 1 || rightTV.size() != 1) {
							// TBD: better user error message...
							throw new SyntaxException("ill. expr.: " + rootToken.toUserInfo(), m);
						}
						// - combine
						rootToken.left = leftTV.get(0);
						rootToken.right = rightTV.get(0);
						//--------------------------------------------------------------------------------
						// add
						resultTokenExpression.add(new TV(rootToken));
					}
				}
				//================================================================================
				// UOP
				//================================================================================
				//--------------------------------------------------------------------------------
				// nb: right side must reduce to exactly one root token
				// nb: boolean op might in turn contain '-'
				// -  eg NOT ((EXISTS...)) 
				else if(rootToken.kind == IToken.Kind.UOP) {
					//--------------------------------------------------------------------------------
					// resolve qualifier values in arg (= right side)
					// - construct artificial token expression
					TokenExpression rightTokenExpression = new TokenExpression();
					rightTokenExpression.phase = "EVALUATED"; // TBD: hardcoded
					rightTokenExpression.add(new TV(rootToken.right));
					// - mta it
					rightTokenExpression._mta(curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
					//--------------------------------------------------------------------------------
					// verify and combine
					// - must have been reduced to one root token
					if(rightTokenExpression.tokenVV.size() != 1) {
						// TBD: better user error message...
						throw new SyntaxException("ill. expression found: " + this.toUserInfo(), m);
					}
					// - get TV
					TV rightTV = rightTokenExpression.tokenVV.get(0);
					// - verify size to be 1
					if(rightTV.size() != 1) {
						// TBD: better user error message...
						throw new SyntaxException("ill. expr. found: " + this.toUserInfo(), m);
					}
					// - combine
					rootToken.right = rightTV.get(0);
					//--------------------------------------------------------------------------------
					// add
					resultTokenExpression.add(new TV(rootToken));
				}
				//================================================================================
				// BASIC TERM
				//================================================================================
				//--------------------------------------------------------------------------------
				// nothing to be done
				else {
					//--------------------------------------------------------------------------------
					// add
					resultTokenExpression.add(new TV(rootToken));
				}
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg, m, e);
		} catch (SNHException e) {
			throw new SNHException(953872, errMsg, m, e);
		}
		//--------------------------------------------------------------------------------
		// exchange my TVV
		this.tokenVV = resultTokenExpression.tokenVV;
		//--------------------------------------------------------------------------------
		// refresh raw from tvv
		this.raw = this.toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
	}

	//================================================================================
	// RESOLVE IMPLY ('->') EXPRESSIONS IN MY TOKEN EXPRESSION
	//================================================================================
	//--------------------------------------------------------------------------------
	// apply quantifierToken to token
	// quantifierTokenExpression has the form <quantifier> INDEX.<n> IN <range>, <quantifier> INDEX.<n> IN <range>, ..., <quantifier> INDEX.<n> IN <range>
	// - <quantifier> = RANGE | FORALL | EXISTS 
	// - eg: ( ( ( ( RANGE INDEX.<n1> IN <range1> ),
	//                  RANGE INDEX.<n2> IN <range3> ),
	//                     RANGE INDEX.<n3> IN <range4> ) -> <expr with INDEX.<n1>, INDEX.<n2> and INDEX.<n3>> )
	// return newly constructed TVV
	// TBD: gehoert nicht hierher
	// CAUTION: recompute raw 
	private static TokenExpression resolveImply(Token quantifierToken, Token applyToToken, 
			PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) 
					throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ tracer.println("quantifierToken: " + quantifierToken.toUserInfo(), Level.NO, m);
		/**/ tracer.println("apply to token:  " + applyToToken.toUserInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "resolve '->' expression";
		//--------------------------------------------------------------------------------
		// build up new TVV
		TokenExpression retTokenExpression = new TokenExpression();
		// - TBD: hardcoded
		retTokenExpression.phase = "EVALUATED";
		//--------------------------------------------------------------------------------
		// stack with quantifier terms 
		Stack<QuantifierTerm> quantifierTermStack = new Stack<QuantifierTerm>();
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// decompose left side
			quantifierTermStack = decomposeQuantifiers(quantifierToken, quantifierTermStack);
			//--------------------------------------------------------------------------------
			// apply to right side
			// - wrap it into a TVV
			TVV tempTVV = new TVV();
			tempTVV.add((new TV(applyToToken)));
			// - apply
			TVV resolvedTVV = applyImply(quantifierTermStack, tempTVV);
			//--------------------------------------------------------------------------------
			// add to retTokenExpression's TVV
			for(int j = 0; j < resolvedTVV.size(); j++) {
				retTokenExpression.tokenVV.add(resolvedTVV.get(j));
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg, m, e);
		} catch (SNHException e) {
			throw new SNHException(474734, errMsg, m, e);
		} 
		//--------------------------------------------------------------------------------
		// set raw
		retTokenExpression.raw = retTokenExpression.toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("result: " + retTokenExpression.toUserInfo(), Level.NO, m);
		return retTokenExpression;
	}

	//================================================================================
	// DECOMPOSE (recursion)
	// - decompose left side of '->' of root token (could be nested!) and build up a stack
	// - nb: can be nested: ((((<quantifierExpr1>) , <quantifierExpr2>) , <quantifierExpr3>) , <quantifierExpr4>)
	//================================================================================
	//--------------------------------------------------------------------------------
	private static Stack<QuantifierTerm> decomposeQuantifiers(Token token, Stack<QuantifierTerm> quantifierTermStack) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ tracer.println("token: " + token.toUserInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// ',' ?
		if(OpDefs.isCommaBOPName(token.rawText)) {
			//--------------------------------------------------------------------------------
			// LEFT RECURSION!!!
			// - first do left
			quantifierTermStack = decomposeQuantifiers(token.left, quantifierTermStack);
			// - then do left
			quantifierTermStack = decomposeQuantifiers(token.right, quantifierTermStack);
		}
		//--------------------------------------------------------------------------------
		// leftmost <quantifier> reached
		else {
			//--------------------------------------------------------------------------------
			// create new <quantifier>Term
			QuantifierTerm quantifierTerm = new QuantifierTerm();
			//--------------------------------------------------------------------------------
			// get left side, ie: ((RANGE INDEX.<n1>) IN <range1>')
			Token inToken = token;
			/**/ tracer.println("inToken: " + inToken.toUserInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// 'IN'?
			if(OpDefs.isInBOPName(inToken.rawText)) {
				//--------------------------------------------------------------------------------
				// get left side of 'IN', ie: (<quantifier> INDEX.<n1>)
				Token quantifierToken = inToken.left;
				/**/ tracer.println("quantifier token: " + quantifierToken.toUserInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// 'quantifier?
				// - TBD: FORALL and EXISTS need separate treatment
				if(OpDefs.isRangeUOPName(quantifierToken.rawText) || 
						OpDefs.isForAllUOPName(quantifierToken.rawText) || 
						OpDefs.isExistsUOPName(quantifierToken.rawText)) {
					quantifierTerm.opName = quantifierToken.rawText;
					//--------------------------------------------------------------------------------
					// get its arg (unary, ie right side), ie: (INDEX.<n1>)
					Token dotToken = quantifierToken.right;
					/**/ tracer.println("dotToken: " + dotToken.toUserInfo(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// '.'?
					if(OpDefs.isDotBOPName(dotToken.rawText)) {
						//--------------------------------------------------------------------------------
						/**/ tracer.println("dotToken : " + dotToken.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// check if left side is "INDEX"
						// - tbd: hardcoded
						if(! dotToken.left.rawText.equals("INDEX")) 
							throw new SyntaxException("'INDEX' expected, but found: '" + quantifierToken.right.rawText + "' in " + token.toUserInfo() + "'", m);
						//--------------------------------------------------------------------------------
						// get right side, ie: (<n1>) and set it in forStarTerm (shared string is ok)
						quantifierTerm.indexId = dotToken.right.rawText;
						//--------------------------------------------------------------------------------
						// verify that indexId is ok
						String errMsg = "ill. index number; indices must be increasing and dense, starting with 1: '"  + dotToken.toUserInfo() + "'";
						try {
							if(Integer.parseInt(quantifierTerm.indexId) != (quantifierTermStack.size() + 1))
								throw new SyntaxException("", m); // catched below...
						} catch (NumberFormatException e) {
							throw new SyntaxException(errMsg, m);
						} catch (SyntaxException e) {
							throw new SyntaxException(errMsg, m);
						}
					}						
				}						
				//--------------------------------------------------------------------------------
				// syntax error
				else 
					throw new SyntaxException("ill. quantifier expression: '" + token.toUserInfo() + "'", m);
				//--------------------------------------------------------------------------------
				// get right side of 'IN', ie, range: (<min> .. <max>)
				// - CAUTION: must already have been *statically* be resolved to int vals
				Token rangeToken = inToken.right;
				/**/ tracer.println("rangeArg: " + rangeToken.toUserInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// '..'?
				if(OpDefs.isDotsBOPName(rangeToken.rawText)) {
					/**/ tracer.println("min: " + rangeToken.left.rawText, Level.NO, m);
					/**/ tracer.println("max: " + rangeToken.right.rawText, Level.NO, m);
					//--------------------------------------------------------------------------------
					try {
						// get left side, ie, (<min>) and set it in stack entry
						/**/ tracer.println("eval min: " + rangeToken.left.toInfo(), Level.NO, m);
						quantifierTerm.min = IntTokenValEval.compute(rangeToken.left);
						/**/ tracer.println("result = " + quantifierTerm.min, Level.NO, m);
					} catch (NumberFormatException e) {
						throw new SyntaxException("ill. min val in range expression: '" + rangeToken.toUserInfo() + "'", m);
					}
					//--------------------------------------------------------------------------------
					try {
						// get right side, ie, (<max>) and set it in stack entry
						/**/ tracer.println("eval max: " + rangeToken.right.toInfo(), Level.NO, m);
						quantifierTerm.max = IntTokenValEval.compute(rangeToken.right);
						/**/ tracer.println("result = " + quantifierTerm.max, Level.NO, m);
					} catch (NumberFormatException e) {
						throw new SyntaxException("ill. max val in range expression: '" + rangeToken.toUserInfo() + "'", m);
					}
				}
				//--------------------------------------------------------------------------------
				else {  
					throw new SyntaxException("range expression expected in '" + token.toUserInfo() + ", but found: '" + rangeToken.toUserInfo() + "'", m);
				}
			}
			//--------------------------------------------------------------------------------
			// push on stack
			quantifierTermStack.push(quantifierTerm);
			/**/ tracer.println("push quantifier term: " + quantifierTerm.toString(), Level.NO, m);
			/**/ tracer.println("token expression: " + token.toUserInfo(), Level.NO, m);
		} 
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("quantifierTermStack: " + quantifierTermStack, Level.NO, m);
		return quantifierTermStack;
	}

	//================================================================================
	// APPLY '->' 
	//================================================================================
	//--------------------------------------------------------------------------------
	// apply quantifiers (represented as stack) to given tvv
	// - nb: can be nested: ((((<quantifierExpr1>) , <quantifierExpr2>) , <quantifierExpr3>) , <quantifierExpr4>) -> <!!apply here!!>
	// - CAUTION: is resolved in a *right* associative way
	// -- namly as: (<applyToToken> , (<quantifierExpr2> , (<quantifierExpr3> , (<quantifierExpr4> -> <!!apply here!!>))))
	// nb: FORALL and EXISTS shall generate exactly one root token in the TVV!
	private static TVV applyImply(Stack<QuantifierTerm> quantifierTermStack, TVV tvv) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ Tracer tracer = new Tracer();  // debug
		//--------------------------------------------------------------------------------
		// cur TVV to apply the stack info to and that holds the final result
		// - start with given arg tvv
		TVV curTVV = tvv;
		//--------------------------------------------------------------------------------
		// iterate over the stack (this means "right to left", see comment above with regard to right associativity)
		for(;;) {
			//--------------------------------------------------------------------------------
			// pop next entry from stack 
			QuantifierTerm quantifierTerm;
			try {
				quantifierTerm = quantifierTermStack.pop();
				/**/ tracer.println("pop quantifier term: " + quantifierTerm.toString(), Level.NO, m);
			} catch (Exception e) {
				// done -> stack is empty now
				break;
			}
			//--------------------------------------------------------------------------------
			// build up result tvv 
			TVV resultTVV = new TVV();
			//================================================================================
			// RANGE
			//================================================================================
			//--------------------------------------------------------------------------------
			/**/ tracer.println(quantifierTerm.toString(), Level.NO, m);
			//--------------------------------------------------------------------------------
			if(quantifierTerm.opName.equals(OpDefs.RANGE)) {
				//--------------------------------------------------------------------------------
				// do for r in range
				/**/ tracer.println("do for " + quantifierTerm.min + " .. " + quantifierTerm.max, Level.NO, m);
				for(int r = quantifierTerm.min; r <= quantifierTerm.max; r++) {
					/**/ tracer.println("r = " + r, Level.NO, m);
					/**/ tracer.println("apply to curTVV = " + curTVV.toString(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// apply to all root tokens in the curTVV
					for(int i = 0; i < curTVV.tokenVV.size(); i++) {
						//--------------------------------------------------------------------------------
						// get next root token
						Token rootToken = curTVV.get(i).getRootToken();
						/**/ tracer.println("token: " + rootToken.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// clone next root token
						Token clonedRootToken = rootToken.deepCopy();
						//--------------------------------------------------------------------------------
						// transform actual index (from the stack entry)
						// - tbd hardcoded
						/**/ tracer.println("quantifierTerm.indexId = " + quantifierTerm.indexId, Level.NO, m);
						/**/ tracer.println("r = " + Integer.toString(r), Level.NO, m);
						clonedRootToken = replaceIndexExpressions(clonedRootToken, "INDEX", quantifierTerm.indexId, Integer.toString(r));
						/**/ tracer.println("replaced token': " + clonedRootToken.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// add to result TVV
						resultTVV.add(new TV(clonedRootToken));
					}
				}
				//--------------------------------------------------------------------------------
			}
			//================================================================================
			// FORALL and EXISTS
			//================================================================================
			//================================================================================
			//--------------------------------------------------------------------------------
			// CAUTION: should be used only in queries... must be checked later;
			// for FORALL: 
			// - generate an "A AND B AND C" ... expression
			// - generate exactly one root token which is either "A" (if range is 1..1) or "A AND B AND C" (otherwise)
			// for EXISTS: 
			// - generate an "A OR B OR C" ... expression
			// - generate exactly one root token which is either "A" (if range is 1..1) or "A OR B OR C" (otherwise)
			else if(quantifierTerm.opName.equals(OpDefs.FORALL) || quantifierTerm.opName.equals(OpDefs.EXISTS)) {
				//--------------------------------------------------------------------------------
				// result token for the query AND-expression
				Token queryToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
				//--------------------------------------------------------------------------------
				// apply to the one root token in the curTVV which represents the query
				// - assertion
				if(curTVV.tokenVV.size() != 1)
					throw new SyntaxException("ill. usage of FORALL: '" + quantifierTerm.opName + "'", m);
				// - get the exactly one root token
				Token rootToken = curTVV.get(0).getRootToken();
				/**/ tracer.println("token: " + rootToken.toUserInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// do for r in range
				// - generate the AND expression
				/**/ tracer.println("do for " + quantifierTerm.min + " .. " + quantifierTerm.max, Level.NO, m);
				for(int r = quantifierTerm.min; r <= quantifierTerm.max; r++) {
					/**/ tracer.println("r = " + r, Level.NO, m);
					/**/ tracer.println("apply to curTVV = " + curTVV.toString(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// clone it
					Token clonedRootToken = rootToken.deepCopy();
					//--------------------------------------------------------------------------------
					// transform actual index (from the stack entry)
					// - tbd hardcoded
					/**/ tracer.println("quantifierTerm.indexId = " + quantifierTerm.indexId, Level.NO, m);
					/**/ tracer.println("r = " + Integer.toString(r), Level.NO, m);
					clonedRootToken = replaceIndexExpressions(clonedRootToken, "INDEX", quantifierTerm.indexId, Integer.toString(r));
					/**/ tracer.println("replaced token': " + clonedRootToken.toUserInfo(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// construct the resulting AND expression:
					//--------------------------------------------------------------------------------
					// - if first term: set queryToken to clonedRootToken
					if(r == quantifierTerm.min) {
						queryToken = clonedRootToken;
					}
					//--------------------------------------------------------------------------------
					// - else: construct AND-expression A AND B, where A is current queryToken and B is clonedRootToken
					else {
						//--------------------------------------------------------------------------------
						// build up a new temp token
						Token tempToken;
						//--------------------------------------------------------------------------------
						// - AND?
						if(quantifierTerm.opName.equals(OpDefs.FORALL)) 
							tempToken = new Token(IToken.Kind.BOP, IOpDefs.AND /* raw */);	
						//--------------------------------------------------------------------------------
						// - otherwise OR
						else 
							tempToken = new Token(IToken.Kind.BOP, IOpDefs.OR /* raw */);	
						//--------------------------------------------------------------------------------
						// - init the token
						tempToken.setType(IData.Type.BOOLEAN);
						tempToken.setLeft(queryToken);
						tempToken.setRight(clonedRootToken);
						//--------------------------------------------------------------------------------
						// - set query token to the temp token
						queryToken = tempToken;
					}
				}
				//--------------------------------------------------------------------------------
				// add the query token to the result TVV
				resultTVV.add(new TV(queryToken));
			}
			//================================================================================
			// EXISTS
			//================================================================================
			//--------------------------------------------------------------------------------
			// CAUTION: should be used only in queries... must be checked later
			// - generates an A OR B OR C ... expression
			else if(quantifierTerm.opName.equals(OpDefs.EXISTS)) {
				/**/ tracer.println("*** TBD: EXISTS -- NOT YET IMPLEMENTED", Level.ERROR, m);
			}			
			//================================================================================
			//--------------------------------------------------------------------------------
			// error
			else
				throw new SNHException(734622, "ill. quantifier: '" + quantifierTerm.opName + "'", m);
			//================================================================================
			// for next iteration
			//--------------------------------------------------------------------------------
			curTVV = resultTVV;
		}
		//--------------------------------------------------------------------------------
		// ok
		return curTVV;
	}

	//================================================================================
	// REPLACE INDEX 
	//================================================================================
	//--------------------------------------------------------------------------------
	// replace all occurrences of '<indexName>.<indexId>' in token by INT Token with value = numberAsString
	private static Token replaceIndexExpressions(Token token, String indexName, String indexId, String numberAsString) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("replace " + indexName + "." + indexId + " by " + numberAsString + " in token: " + token.toUserInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// create new return token
		Token newToken = new Token(IToken.Kind.EMPTY, "" /* Raw */);
		//--------------------------------------------------------------------------------
		// very INDEX '.' <indexId> expression that shall be replaced? 
		// - tbd: "INDEX" is hard coded
		if(token.kind == IToken.Kind.BOP && OpDefs.isDotBOPName(token.rawText) && 
				token.left.rawText.equals("INDEX") && token.right.rawText.equals(indexId)) {
			newToken.kind = IToken.Kind.INT;
			newToken.isLabelFlag = false;
			newToken.rawText = numberAsString;
			newToken.left = null; 
			newToken.right = null; 
			newToken.type = IData.Type.INT;
			newToken.treatedFlag = true;
			newToken.integratedFlag = true;
		}
		//--------------------------------------------------------------------------------
		// OTHER '.' OR OTHER BOP
		else if(token.kind == IToken.Kind.BOP) {
			newToken.flatClone(token);
			newToken.left = replaceIndexExpressions(token.left, indexName, indexId, numberAsString);
			newToken.right = replaceIndexExpressions(token.right, indexName, indexId, numberAsString);
		}
		//--------------------------------------------------------------------------------
		// UOP?
		else if(token.kind == IToken.Kind.UOP) {
			newToken.flatClone(token);
			newToken.left = null; 
			newToken.right = replaceIndexExpressions(token.right, indexName, indexId, numberAsString);
		}
		//--------------------------------------------------------------------------------
		// BASIC VAL: done -> no further recursion needed
		else
			newToken = token;
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("--> result token: " + newToken.toUserInfo(), Level.NO, m);
		return newToken;
	}

	//================================================================================
	// RESOLVE FLEX TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// convert FLEX_ARRAY_REF types to URLs, if used on right side of assignment to a URL
	// - eg: (who = (player # 1)); 
	// -- detailed info: ( <URL<NAME>>who  <URL_ARRAY<BOP>>'=' ( <FLEX<NAME>>player  <FLEX_ARRAY_REF<BOP>>'#'  <INT<INT>>1 ))
	// - eg: (who = (((player # 1) # 2) ... #k)); 
	public void resolveFlexTypes() throws SyntaxException {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// for all root tokens of my TVs
		for(int i = 0; i < tokenVV.size(); i++) {
			//--------------------------------------------------------------------------------
			// get next root token
			Token token = tokenVV.get(i).getRootToken();
			//--------------------------------------------------------------------------------
			// resolve ts FLEX types
			token.resolveFlexTypes();
		}
	}

	//================================================================================
	//================================================================================
	// MTA PLUS PIGGYPACKED CLONING (AND MERGING)
	//================================================================================
	//================================================================================

	//--------------------------------------------------------------------------------
	// 1) deeply clone me from token expression
	// 2) apply mta
	public void deepCloneAndMta(TokenExpression tokenExpression, PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clone
		this.deepCloneFromTokenExpression(tokenExpression);
		//--------------------------------------------------------------------------------
		// iteratively resolve
		this.mta(curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
		//--------------------------------------------------------------------------------
		/**/ tracer.println("cloned = " + this, Level.NO, m);
		/**/ tracer.println("after mta = " + this, Level.NO, m);
	}

	//--------------------------------------------------------------------------------
	// deep clone, merge and mta origs + defaults which represent assignments: 
	// - origs may contain props defs *and* var assignments
	// - defaults may contain only props defs
	// - CAUTION: both may contain 'FOR* ... -> ...' expressions
	// 1) deep copy everything
	// 2) apply mta (resolve qualifier values, resolve array access, resolve '->')
	// 3) merge the copies
	// 4) set me to the result
	public void deepCloneMergeMta_Assignments(TokenExpression origs, TokenExpression defaults, 
			PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException{
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		/**/ tracer.println("origs      : " + origs.toUserInfo(true, true), Level.NO, m);
		/**/ tracer.println("origs      : " + origs.toInfo(), Level.NO, m);
		/**/ tracer.println("defaults      : " + defaults.toUserInfo(true, true), Level.NO, m);
		//================================================================================
		// DEEP CLONE & MTA ORIGS:
		//================================================================================
		TokenExpression tempOrigs = new TokenExpression();
		tempOrigs.deepCloneAndMta(origs, curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
		//--------------------------------------------------------------------------------
		/**/ tracer.println("mta-ed origs: " + tempOrigs.toUserInfo(true, true), Level.NO, m);
		//================================================================================
		// DEEP CLONE & MTA DEFAULTS:
		//================================================================================
		// - deep clone:
		TokenExpression tempDefaults = new TokenExpression();
		tempDefaults.deepCloneAndMta(defaults, curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
		//--------------------------------------------------------------------------------
		/**/ tracer.println("mta-ed origs defaults: " + tempDefaults.toUserInfo(true, true), Level.NO, m);
		//================================================================================
		// MERGE:
		//================================================================================
		// - caution: do it *after* mta-ing so that duplicates can be recognized (at least on a static basis...)
		this.mergeAssignments(tempOrigs, tempDefaults);
		// - TBD: mta again... namely if the own props defs list may serve for resolving, too...
		//--------------------------------------------------------------------------------
		/**/ tracer.println("merged mta-ed props defs: " + this.toUserInfo(true, true), Level.NO, m);
	}

	//================================================================================
	// MERGE ORIG AND DEFAULT PROPS DEFS
	//================================================================================
	//--------------------------------------------------------------------------------
	// construct me from origs + defaults 
	// - origs may contain props defs *and* var assignments
	// - defaults may contain only props defs
	// - CAUTION: both may contain 'FOR* ... -> ...' expressions
	// check prop def syntax
	// check for duplicates as far as possible
	// - duplicates can occur in both; but be careful: duplicate var assignments are ok (in var/prop/set/get)
	// - !! nb: duplicates of array refs on the left hand side can only be checked for statically evaluable values!! 
	// - !! nb: duplicates of 'FOR ... -> ...' expressions cannot be checked now !! 
	// add defaults, if not yet contained in origs
	// CAUTION: also recompute a merged raw!!!
	// CAUTION: also set phase!!!
	// CAUTION: call only *after* tokenization
	private void mergeAssignments(TokenExpression origs, TokenExpression defaults) throws SNHException, SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("origs = " + origs, Level.NO, m);
		/**/ tracer.println("defaults = " + defaults, Level.NO, m);
		//--------------------------------------------------------------------------------
		// alias used first for origs and then for defaults 
		TokenExpression nexts;
		//--------------------------------------------------------------------------------
		// assertion
		// - tokenVV pointer must not be null
		if(tokenVV == null)
			throw new SNHException(103028, "tvv is null", m); 
		// if raw is set but tvv is emtpy -> ill. call namely before tokenization
		if(! this.isRawEmpty() && this.isTVVEmpty())
			throw new SNHException(486299, "ill. usage", m); 
		//--------------------------------------------------------------------------------
		// clear me
		super.clear(); 
		//--------------------------------------------------------------------------------
		// first add origs and then defaults (if not yet)
		// - nb: because there could be duplicates in orig as well...
		for(int n = 1; n < 3; n++) {
			//--------------------------------------------------------------------------------
			// programming trick using "nexts" alias:
			// - 1...origs, 2...defaults
			if(n == 1)
				nexts = origs;
			else
				nexts = defaults;
			//--------------------------------------------------------------------------------
			// go through nexts list
			for(int i = 0; i < nexts.tokenVV.size(); i++) {
				//--------------------------------------------------------------------------------
				// get root token
				Token nextRootToken = nexts.tokenVV.get(i).getRootToken();
				//--------------------------------------------------------------------------------
				// '->' expression? 
				// - must be resolved by mta later
				if(OpDefs.isImpliesBOPName(nextRootToken.getRawText())) {
					//--------------------------------------------------------------------------------
					// add new tv with root token to my tvv
					this.add(new TV(nextRootToken));
					/**/ tracer.println("add implies expression = " + nextRootToken.toUserInfo(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// done
					continue;
				}				
				//--------------------------------------------------------------------------------
				// assignment?
				if(! OpDefs.isAssignmenBOPName(nextRootToken.getRawText())) 
					throw new SyntaxException("assignment expected, however found '" + nextRootToken.rawText + "'-expression: '" + nextRootToken.toUserInfo() + "'", m);
				//--------------------------------------------------------------------------------
				// VAR assignment?
				if(nextRootToken.left.kind == IToken.Kind.VAR) {
					//--------------------------------------------------------------------------------
					// add new tv with root token to my tvv
					this.add(new TV(nextRootToken));
					/**/ tracer.println("add var assignment = " + nextRootToken.toUserInfo(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// done
					continue;
				}
				//--------------------------------------------------------------------------------
				// <propLabel> = <right side>
				else {
					//--------------------------------------------------------------------------------
					boolean equalLabelFound = false;
					//--------------------------------------------------------------------------------
					// go through my list (ie all assignments that have already been verified and added to me)
					for(int j = 0; j < this.tokenVV.size(); j++) {
						//--------------------------------------------------------------------------------
						// get root token
						Token thisRootToken = this.tokenVV.get(j).getRootToken();
						//--------------------------------------------------------------------------------
						// skip '->' expression
						if(OpDefs.isImpliesBOPName(thisRootToken.getRawText()))
							continue;
						//--------------------------------------------------------------------------------
						// assignment?
						if(! OpDefs.isAssignmenBOPName(thisRootToken.getRawText())) 
							throw new SyntaxException("(b) assignment expected, but found '" + thisRootToken.toUserInfo() + "'", m);
						//--------------------------------------------------------------------------------
						// skip VAR assignment
						if(thisRootToken.left.kind == IToken.Kind.VAR) 
							continue;
						//--------------------------------------------------------------------------------
						/**/ tracer.println("this = " + thisRootToken.toUserInfo(), Level.NO, m);
						/**/ tracer.println("next = " + nextRootToken.toUserInfo(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// both must be assignments to prop labels
						// - nb: labels are found on the left side of '='
						// - are left sides valid prop labels?
						if(isValidLabel(nextRootToken.left) && isValidLabel(thisRootToken.left)) {
							//--------------------------------------------------------------------------------
							/**/ tracer.println("valid prop labels", Level.NO, m);
							//--------------------------------------------------------------------------------
							try {
								//--------------------------------------------------------------------------------
								// are they equal (regarding that labels might contain '#'s)?
								if(equalLabels(nextRootToken.left, thisRootToken.left)) {
									//--------------------------------------------------------------------------------
									// found
									equalLabelFound = true;
									/**/ tracer.println("equal prop labels", Level.NO, m);
									break;
								}	
								//--------------------------------------------------------------------------------
							} catch (SyntaxException e) {
								throw new SyntaxException("ill. label specification", m);
							}
							//--------------------------------------------------------------------------------
							/**/ tracer.println("not equal prop labels", Level.NO, m);
						}
					}
					//--------------------------------------------------------------------------------
					// if not found -> add new tv with root token to my tvv
					if(! equalLabelFound)
						this.add(new TV(nextRootToken));
				}
			}
		}
		//--------------------------------------------------------------------------------
		// !!! set phase !!!
		// - TBD: hard coded
		this.phase = "EVALUATED";
		//--------------------------------------------------------------------------------
		// !!! recompute raw !!!
		this.raw = this.toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */);
	}

	//================================================================================
	//================================================================================
	// TVV 2 STRING VECTOR
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// convert my tvv (found in super class) to a vector with valid names;
	// tv must contain a <name> which is either
	// - a NAME 
	// -- caution: do not test that type is STRING, because for names of peers and wirings type eval could not check the type and they have type FLEX !!!
	// - a (nested) expression of the form '(...((<name> # <index1>) # <index1>) # ... # <indexN>)'
	// -- in this case resolve the entire expression into a string with '#' chars !!!
	// nb: checks valifity of the name
	public Vector<String> tvv2Names() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assertion
		if(tokenVV == null)
			throw new SNHException(100283, "tvv is null", m); 
		//--------------------------------------------------------------------------------
		// create vector
		Vector<String> names = new Vector<String>();
		//--------------------------------------------------------------------------------
		// go through my tvv and add valid props types resp. props defs to the hash map
		// - nb: tokens are shared - need not be copied
		for(int i = 0; i < tokenVV.size(); i++) {
			//--------------------------------------------------------------------------------
			// get root token of tv
			Token rootToken = tokenVV.get(i).getRootToken();
			/**/ tracer.println("token = " + rootToken.toUserInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// get its flattened label
			String flattenedLabel = rootToken.getFlattenedHashLabel();
			//--------------------------------------------------------------------------------
			// add
			names.add(flattenedLabel);
		}
		//--------------------------------------------------------------------------------
		// ok
		return names;
	}

	//================================================================================
	//================================================================================
	// TVV 2 HASH MAP FOR PROPS TYPES OR PROPS DEFS
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// assume that my tvv contains prop type specifications;
	// convert my tvv (found in super class) to a hash map for all root tokens in each tv as an overlay for optimized access;
	// nb: prop types have the form "<type>:<propLabel>"; the key is <propLabel>;
	// put key = <propLabel> and value = <entire prop type/def specification>;
	public HashMap<String,Token> tvv2PropsTypesHashMap() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assertion
		if(tokenVV == null)
			throw new SNHException(100283, "tvv is null", m); 
		//--------------------------------------------------------------------------------
		// create new hash map
		HashMap<String,Token> map = new HashMap<String,Token>();
		//--------------------------------------------------------------------------------
		// go through my tvv and add valid props types resp. props defs to the hash map
		// - nb: tokens are shared - need not be copied
		for(int i = 0; i < tokenVV.size(); i++) {
			//--------------------------------------------------------------------------------
			// get next TV
			TV tv = tokenVV.get(i);
			/**/ tracer.println("tv:" + tv.toTypedInfo(), Level.LO, new Object(){});			
			//--------------------------------------------------------------------------------
			// get root token
			Token token = tv.getRootToken();
			/**/ tracer.println("token: " + token.toNode() + " <====> " + token.toNode_plus_TypeInProgress_Kind_Flags(), Level.LO, new Object(){});			
			//--------------------------------------------------------------------------------
			// must be the correct BOP
			if((token.getKind() == IToken.Kind.BOP) && OpDefs.isColonBOPName(token.getRawText())) {
				//--------------------------------------------------------------------------------
				// verify the token to be a correct prop type and get its label
				Token propNameToken;
				try {
					propNameToken = token.getPropLabelToken(true /* propTypeFlag */);
				} catch (SyntaxException e) {
					// caution: this fu is also used for var/prop/set/get analyses purposes...
					// so no extra info, but just pass on the exception
					throw e;
				}
				//--------------------------------------------------------------------------------
				// left side must be name (ie the name of the type)
				if(propNameToken.kind != IToken.Kind.NAME) {
					throw new SyntaxException("ill. prop type '" + token.toUserInfo() + "'", m);
				}
				String typeName = propNameToken.rawText;
				//--------------------------------------------------------------------------------
				// add pair and OVERWRITE if it exists already... (TBD)
				// TBD: no warning if user specifies duplicates...
				//   if(map.get(propName) != null) 
				//	   throw new SyntaxException("duplicate prop label name '" + propName + "'", m);
				//	 else ...
				map.put(typeName, token);
			}
		}	
		return map;
	}

	//================================================================================
	//================================================================================
	// QUERY
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// check that the TVV has exactly one TV with one "root" token , i.e:
	// - if tvv contains exactly one tv and
	// -- if tv contains exactly one token
	// --- return this token
	public Token getRootToken() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(tokenVV.isEmpty())
			throw new SyntaxException("empty", m);
		if(tokenVV.size() != 1) 
			throw new SyntaxException("more than one", m);
		//--------------------------------------------------------------------------------
		// get root token of first tv
		try {
			return tokenVV.get(0).getRootToken();
		} catch (SyntaxException e) {
			throw new SyntaxException("ill. expression", m, e);
		}
	}

	//--------------------------------------------------------------------------------
	// assume that token expression contains props defs of the form '<propNameToken>=<valueToken>'
	// search for <propNameToken> and return <valueToken>
	// CAUTION: <propNameToken> might be either a label or a '#' expression
	// CAUTION: token expression must already have been mta-ed
	// CAUTION: verification is only possible for statically evaluable vales; ie not possible if vars are used in '#' expression
	public Token getPropValueToken(Token propNameToken) throws NotFoundException, SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("search for: " + propNameToken.toTypedInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// local var
		Token nextPropNameToken;
		//--------------------------------------------------------------------------------
		for(int i = 0; i < this.tokenVV.size(); i++) {
			//--------------------------------------------------------------------------------
			// get root token
			Token rootToken = this.tokenVV.get(i).getRootToken();
			/**/ tracer.println("next: " + rootToken.toUserInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			try {
				// verify the root token to be a prop def and get its label
				nextPropNameToken = rootToken.getPropLabelToken(false /* propTypeFlag */);
			} catch (SyntaxException e) {
				throw new SyntaxException("ill. prop def specification", m, e);
			}
			//--------------------------------------------------------------------------------
			// equal labels?
			// - nb: regards also '#'s
			if(equalLabels(propNameToken, nextPropNameToken)) {
				// yes -- found! return value, ie right side of root token 
				/**/ tracer.println("equal labels: " + propNameToken.toUserInfo() + " AND " + nextPropNameToken.toUserInfo(), Level.NO, m);
				return rootToken.getRight();
			}
			//--------------------------------------------------------------------------------
		}	
		// not found
		throw new NotFoundException("prop label '" + propNameToken.toUserInfo() + "' not found in '" + this.toUserInfo(true, true) + "'", m);
	}

	//--------------------------------------------------------------------------------
	// compare two tokens to be labels and to be equal labels
	// CAUTION: could be strings or (nested) '#' expressions
	// CAUTION: we assume that both were mta-ed
	// CAUTION: verification is only possible for statically evaluable vales; ie not possible if vars are used in '#' expression
	private static boolean equalLabels(Token token1, Token token2) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// are both NAMES?
		if((token1.kind == IToken.Kind.NAME) && (token2.kind == IToken.Kind.NAME)) {
			//--------------------------------------------------------------------------------
			String name1 = token1.rawText;
			String name2 = token2.rawText;
			//--------------------------------------------------------------------------------
			// equal names?
			if(name1.equals(name2)) {
				return true;
			}
		}
		//--------------------------------------------------------------------------------
		// are both '#'?
		else if((token1.kind == IToken.Kind.BOP) && (token2.kind == IToken.Kind.BOP) &&
				OpDefs.isHashBOPName(token1.rawText) && OpDefs.isHashBOPName(token2.rawText)) {
			//--------------------------------------------------------------------------------
			/**/ tracer.println("both are '#'", Level.NO, m);
			/**/ tracer.println("token1 right side type = " + token1.right.type, Level.NO, m);
			/**/ tracer.println("token2 right side type = " + token2.right.type, Level.NO, m);
			//--------------------------------------------------------------------------------
			// CHECK RIGHT SIDE of '#'
			// - right side must be equal ints
			if((IData.Type.INT == token1.right.type) && (IData.Type.INT == token2.right.type)) {
				//--------------------------------------------------------------------------------
				/**/ tracer.println("both right sides are INT", Level.NO, m);
				//--------------------------------------------------------------------------------
				// nb: compute int val might also accept ALL or NONE, which are negative values and must be checked
				// - CAUTION: this is a static check, ie eg no vars possible !!! 
				int val1 = IntTokenValEval.compute(token1.right);
				int val2 = IntTokenValEval.compute(token2.right);
				//--------------------------------------------------------------------------------
				/**/ tracer.println("val1 = " + val1 + "; val2 = " + val2, Level.NO, m);
				//--------------------------------------------------------------------------------
				// difference found? or reserved name like ALL or NONE which are not allowed here
				if(val1 != val2 || val1 < 0) {
					return false;
				}
				//--------------------------------------------------------------------------------
				/**/ tracer.println("EQUAL INT VALS: val1 = " + val1 + "; val2 = " + val2, Level.NO, m);
				//--------------------------------------------------------------------------------
				// CHECK LEFT SIDE of '#'
				// - left side must be equal labels
				return equalLabels(token1.left, token2.left);
			}
		}
		//--------------------------------------------------------------------------------
		// not equal
		return false;
	}

	//--------------------------------------------------------------------------------
	// is valid label?
	// CAUTION: could be string or (nested) '#' expression
	private boolean isValidLabel(Token labelToken) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// NAME?
		if(labelToken.kind == IToken.Kind.NAME) 
			return true;
		//--------------------------------------------------------------------------------
		// '#'?
		if(OpDefs.isHashBOPName(labelToken.rawText)) 
			return true;
		//--------------------------------------------------------------------------------
		// no label
		return false;
	}

	//================================================================================
	//================================================================================
	// CONVERT TVV TO USER INFO
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// CAUTION: do not change: is used for PM-DSL code gen!!!
	// and also used for user messages: print flat info in bracketed, nice and human readable form
	// - removeOuterBrackets == true .. remove outer "(" ... ")" brackets
	// - printSemiColonAfterTv == true ... print "; " after each tv;
	// -- caution: other fus rely on that we separate here the tokens by "; ", namely the latex code gen...
	public String toUserInfo(boolean printSemiColonAfterTv, boolean removeOuterBrackets) {
		StringBuffer buf = new StringBuffer();
		String s;
		int len;
		for(int i = 0; i < tokenVV.size(); i++) {
			//--------------------------------------------------------------------------------
			// nb: toUserInfo returns expression starting with '(' and ending with ')'
			s = tokenVV.get(i).toUserInfo();
			len = s.length();
			//--------------------------------------------------------------------------------
			// remove outer brackets?
			if(removeOuterBrackets) {
				if((len >= 3) && (s.startsWith("(")) && (s.endsWith(")"))) {
					s = s.substring(1, len - 1);
				}
			}
			//--------------------------------------------------------------------------------
			// add to buf
			buf.append(s);
			//--------------------------------------------------------------------------------
			// append ";"?
			if(printSemiColonAfterTv)
				buf.append("; "); 
			//--------------------------------------------------------------------------------
		}
		return new String(buf);
	}

	//================================================================================
	// RESOLVE TVV TO COUNT 
	//================================================================================
	//--------------------------------------------------------------------------------
	// count expression must be either <min> or <min>..<max>;
	// split into min and max, if it is a range specification;
	// otherwise min and max are equal;
	// it must be an int expression;
	// info: both sides might contain qualifier expressions;
	public MinMaxTokens resolveCount() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// construct new min/max expression
		MinMaxTokens minMaxTokens = new MinMaxTokens();
		//--------------------------------------------------------------------------------
		if(isTVVEmpty())
			throw new SyntaxException("empty count", m);
		if(size() != 1)
			throw new SyntaxException("count cannot consist of several expressions", m);
		TV tv = get(0);
		if(tv.isEmpty())
			throw new SyntaxException("empty count", m);
		if(tv.size() != 1)
			throw new SyntaxException("count cannot consist of several expressions", m);
		Token token = tv.get(0);
		//--------------------------------------------------------------------------------
		// check type to be INT
		if(token.getType() != IData.Type.INT) {
			throw new SyntaxException("count expression must be integer", m);
		}
		//--------------------------------------------------------------------------------
		// ".."? (ie range spec)
		if (token.isIntDotsBOP()) {
			//--------------------------------------------------------------------------------
			// assertion
			if(null == token.getLeft() || null == token.getRight())
				throw new SyntaxException("ill. count: in a range expression both sides must be given", m);
			//--------------------------------------------------------------------------------
			// set min and max
			minMaxTokens.setMinToken(token.getLeft());
			minMaxTokens.setMaxToken(token.getRight());
		}
		//--------------------------------------------------------------------------------
		else {
			//--------------------------------------------------------------------------------
			// set min and max to same val
			minMaxTokens.setMinToken(token);
			minMaxTokens.setMaxToken(token);
		}
		//--------------------------------------------------------------------------------
		/**/ tracer.println("minMax expression = " + minMaxTokens.toStructuredString(), Level.NO, m);
		return minMaxTokens;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu here;
	public String toString() {
		return toStringIntelligence();
	}	
	//--------------------------------------------------------------------------------
	// depending on the current phase of parsing select the best suitable function;
	// - tokens numbered (using their index!); one token per line
	public String toStringIntelligence() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		if(! tokenVV.isEmpty()) {
			//--------------------------------------------------------------------------------
			buf.append(ui.Out.addAlignmentInbetween("", ">>> status " + phase + ":   \n"));
			//--------------------------------------------------------------------------------
			for(int i = 0; i < tokenVV.size(); i++) { 
				//--------------------------------------------------------------------------------
				buf.append(ui.Out.addAlignmentInbetween("", "--> TV-" + i + ":  "));
				//--------------------------------------------------------------------------------
				TV tv = tokenVV.get(i);
				buf.append(tv.toStringIntelligence(phase));
				//--------------------------------------------------------------------------------
				buf.append("\n");
			}
		}
		else {
			buf.append("<empty>");
		}
		//--------------------------------------------------------------------------------
		return new String(buf);
	}	

	//--------------------------------------------------------------------------------
	// intelligently display details
	// - if already evaluated, display also type info
	public String toInfo() {
		return toInfo(false /* enforceDisplayOfTypeInfo */);
	}	

	//--------------------------------------------------------------------------------
	// display details with type info (enforced)
	public String toTypedInfo() {
		return toInfo(true /* enforceDisplayOfTypeInfo */);
	}	
	//--------------------------------------------------------------------------------
	// display details
	public String toInfo(boolean enforceDisplayOfTypeInfo) {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		if(! tokenVV.isEmpty()) {
			//--------------------------------------------------------------------------------
			buf.append("\n");
			buf.append(ui.Out.addAlignmentInbetween("", ">>> status " + phase + ":   \n"));
			//--------------------------------------------------------------------------------
			for(int i = 0; i < tokenVV.size(); i++) { 
				//--------------------------------------------------------------------------------
				buf.append(ui.Out.addAlignmentInbetween("", "--> TV-" + i + ":  "));
				//--------------------------------------------------------------------------------
				TV tv = tokenVV.get(i);
				buf.append(tv.toInfo(enforceDisplayOfTypeInfo));
				//--------------------------------------------------------------------------------
				buf.append("\n");
			}
		}
		else {
			buf.append("<empty>");
		}
		//--------------------------------------------------------------------------------
		return new String(buf);
	}	


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
