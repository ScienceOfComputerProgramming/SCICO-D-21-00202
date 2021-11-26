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

package eval.qualifiers;

import pmmm.*;
import eval.tokens.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
public class QualifierValEval {
	//--------------------------------------------------------------------------------
	// for debugging
	Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// root of the token tree in which all qualifier values shall be resolved
	protected static Token rootToken;
	// shared return token
	protected static Token retToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
	// context info
	protected static PmmmInstance curPmmmInstance;
	protected static PeerInstance curPeerInstance;
	protected static WiringInstance curWiringInstance;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// constructor
	// - CAUTION: those cur-parameters that do not apply in the context must be set to null
	public QualifierValEval(Token rootToken, Token retToken, PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance) {
		QualifierValEval.rootToken = rootToken;
		QualifierValEval.retToken = retToken;
		QualifierValEval.curPmmmInstance = curPmmmInstance;
		QualifierValEval.curPeerInstance = curPeerInstance;
		QualifierValEval.curWiringInstance = curWiringInstance;
	}

	//================================================================================
	// START WITH ROOT TOKEN
	//================================================================================
	//--------------------------------------------------------------------------------
	public void qualifierValEval() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("resolve qualifier value in: " + rootToken.toTypedInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// assert that root token is not null
		if(rootToken == null)
			throw new SNHException(772254, "root token must not be null", m);
		//--------------------------------------------------------------------------------
		// do it 
		try { 
			//--------------------------------------------------------------------------------
			// do it
			_qualifierValEval(rootToken, retToken);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("QUALIFIER VALUES RESOLVED: orig = " + rootToken.toTypedInfo() + "; resolved = " + 
					retToken.toTypedInfo(), Level.NO, m); 
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException("eval of qualifier values failed", m, e);
			//--------------------------------------------------------------------------------
		}
	}

	//================================================================================
	// RECURSIVE QUALIFIER RESOLVING
	//================================================================================
	//--------------------------------------------------------------------------------
	// recursively investigate token for qualifier expressions and resolve them;
	// constructs deep copy of the resolved token in the shared retToken;
	// CAUTION: curRetToken is a return parameter !!!
	private static void _qualifierValEval(Token token, Token curRetToken) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// eval qualifiers in token -- they are recognize by "." BOP
		switch(token.getKind()) {
		//================================================================================
		// BOP 
		//================================================================================
		case BOP:
			//--------------------------------------------------------------------------------
			// !!! SPECIALIZED CODE: RESOLVE QUALIFIERS !!!
			// resolve "." op expressions to their values
			// (a) <PMMM>.<propName>	
			//     - get value of <propName> from (CONFIG-><PMMM>)'s pmmm props defs
			// (b) <PEER>.<propName>
			//     - get value of <propName> from current peer's pprops defs
			// (c) <WIRING>.<propName>
			//     - get value of <propName> from current wirings's wprops defs
			// (d) <WINDEX>.<int>
			// (e) <PINDEX>.<int>
			// (f) <INDEX>.<int>
			//--------------------------------------------------------------------------------					
			//================================================================================
			// '.' 
			//================================================================================
			if(OpDefs.isDotBOPName(token.getRawText())) {
				/**/ tracer.println("DOT expression: " + token.toUserInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// !!! CAUTION: recursion for right side, because '.' could be nested !!!
				Token tempRetToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
				_qualifierValEval(token.getRight(), tempRetToken);
				token.setRight(tempRetToken);
				//--------------------------------------------------------------------------------
				// qualifier, i.e. <PEER>, <PMMM> or <WIRING>
				Token left = token.getLeft(); 
				String qualifierName = left.getRawText();
				//--------------------------------------------------------------------------------
				// <propName>
				Token right = token.getRight(); 
				//--------------------------------------------------------------------------------
				// check left to be of KIND = NAME
				if(left.getKind() == IToken.Kind.NAME) {
					/**/ tracer.println("*** resolve qualifier value in: " + token.toTypedInfo(), Level.NO, m);
					//--------------------------------------------------------------------------------
					// token which resolves the entire qualifier expression
					Token solutionToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
					//--------------------------------------------------------------------------------
					// set prop label token to right side (nb could be '#' expression)
					Token propLabelToken = right;
					//--------------------------------------------------------------------------------
					/**/ tracer.println("QUALIFIER '" + qualifierName + "'; search for " + propLabelToken, Level.NO, m);
					//--------------------------------------------------------------------------------
					// for debug
					boolean somethingWasChangedFlag = false;
					try {
						//================================================================================
						// (a) <PMMM>
						//================================================================================
						if(qualifierName.equals(IPmDefs.PMMM_QUALIFIER)) {
							//--------------------------------------------------------------------------------
							if(curPmmmInstance == null) {
								throw new SyntaxException("ill. context, where " + token.toUserInfo() + " qualifier is used: no pmmm", m);
							}
							//--------------------------------------------------------------------------------
							/**/ tracer.println("processed pmmm props defs = " + curPmmmInstance.getProcessedPmmmPropsDefsTokenExpression().toUserInfo(true, true), Level.NO, m);
							//--------------------------------------------------------------------------------
							solutionToken = curPmmmInstance.getProcessedPmmmPropDefValueToken(propLabelToken);
							//--------------------------------------------------------------------------------
							somethingWasChangedFlag = true;
						}
						//================================================================================
						// (b) <PEER>
						//================================================================================
						else if(left.getRawText().equals(IPmDefs.PEER_QUALIFIER)) {
							//--------------------------------------------------------------------------------
							if(curPeerInstance == null) {
								throw new SyntaxException("ill. context, where " + token.toUserInfo() + " qualifier is used: no peer", m);
							}
							//--------------------------------------------------------------------------------
							/**/ tracer.println("processed peer props defs = " + curPeerInstance.getProcessedPPropsDefsTokenExpression().toUserInfo(true, true), Level.NO, m);
							//--------------------------------------------------------------------------------
							solutionToken = curPeerInstance.getProcessedPPropDefsValueToken(propLabelToken);
							//--------------------------------------------------------------------------------
							/**/ tracer.println("solutionToken: " + solutionToken, Level.NO, m);
							somethingWasChangedFlag = true;
						}
						//================================================================================
						// (c) <WIRING>
						//================================================================================
						else if(left.getRawText().equals(IPmDefs.WIRING_QUALIFIER)) {
							//--------------------------------------------------------------------------------
							if(curWiringInstance == null) {
								throw new SyntaxException("ill. context, where " + token.toUserInfo() + " qualifier is used: no wiring", m);
							}
							//--------------------------------------------------------------------------------
							solutionToken = curWiringInstance.getProcessedWPropDefsValueToken(propLabelToken);
							//--------------------------------------------------------------------------------
							/**/ tracer.println("solutionToken: " + curWiringInstance.getProcessedWiringWPropsDefs(), Level.NO, m);
							somethingWasChangedFlag = true;
						}
						//================================================================================
						// (d) <WINDEX>
						//================================================================================
						else if(left.getRawText().equals(IPmDefs.WINDEX_QUALIFIER)) {
							//--------------------------------------------------------------------------------
							// my token has the form <WINDEX>.<k>
							// - get name of cur wiring instance which must have the form <wiringName> # <i1> # <i2> # ... # <ik> # ... # <in> as token
							// - the solution token is the INT token <ik>
							solutionToken = curWiringInstance.getWiringInstanceNameToken().getKthHashIndexToken(Integer.parseInt(right.getRawText()));
							somethingWasChangedFlag = true;
						}
						//================================================================================
						// (e) <PINDEX>
						//================================================================================
						else if(left.getRawText().equals(IPmDefs.PINDEX_QUALIFIER)) {
							//--------------------------------------------------------------------------------
							// my token has the form <WINDEX>.<k>
							// - get name of cur wiring instance which must have the form <wiringName> # <i1> # <i2> # ... # <ik> # ... # <in> as token
							// - the solution token is the INT token <ik>
							solutionToken = curPeerInstance.getPeerInstanceNameToken().getKthHashIndexToken(Integer.parseInt(right.getRawText()));
							somethingWasChangedFlag = true;
						}
						//================================================================================
						// (f) <INDEX>
						//================================================================================
						else if(left.getRawText().equals(IPmDefs.INDEX_QUALIFIER)) {
							// do nothing, must be resolved by treatment of '->' operator
							// - just pass on the token
							solutionToken = token;
							somethingWasChangedFlag = false;
						}
						//================================================================================
						//--------------------------------------------------------------------------------
						else 
							throw new SyntaxException("ill. qualifier expression = " + token.toUserInfo(), m);
						//--------------------------------------------------------------------------------
						// assertion
						if(solutionToken.getKind() == IToken.Kind.EMPTY)
							throw new SNHException(536773, "empty solution token" + propLabelToken.toUserInfo(), m);
						//--------------------------------------------------------------------------------
					} catch (SyntaxException e) { 
						// - TBD: use toUserInfo()
						throw new SyntaxException("can't statically resolve qualifier val '" + qualifierName + "." + propLabelToken.toTypedInfo(), m, e);
					} catch (SNHException e) { 
						// TBD: use toUserInfo()
						throw new SNHException(826456, "can't resolve qualifier val '" + qualifierName + "." + propLabelToken.toTypedInfo(), m, e);
					}
					//--------------------------------------------------------------------------------
					// caution: it is a shared return parameter, so copy the contents to it instead of re-setting it! flat copy is ok the rest is shared
					curRetToken.flatClone(solutionToken);
					//--------------------------------------------------------------------------------
					if(somethingWasChangedFlag) {
						/**/ if(curPmmmInstance != null) tracer.println("pmmm = " + curPmmmInstance.getPmmmTypeName() + "; config = " + curPmmmInstance.getConfigName(), Level.NO, m);
						/**/ if(curPeerInstance != null) tracer.println("peer = " + curPeerInstance.getPeerInstanceName() + "; peer type = " + curPeerInstance.getPeerTypeName(), Level.NO, m);
						/**/ if(curWiringInstance != null) tracer.println("wiring = " + curWiringInstance.getWiringInstanceName() + "; wiring type = " + curWiringInstance.getWiringTypeName(), Level.NO, m);
						/**/ tracer.println("QUALIFIER " + left + "." + right.toUserInfo() + " resolved to " + curRetToken.toTypedInfo(), Level.NO, m);
					}
				}
				//--------------------------------------------------------------------------------
				else {
					throw new SyntaxException("left side of qualifier '.' expression must be '" + IPmDefs.PMMM_QUALIFIER + "', '" + 
							IPmDefs.PEER_QUALIFIER + "', or '" + IPmDefs.WIRING_QUALIFIER + "'; but is '" + qualifierName + "'", m);
				}
			}
			//================================================================================
			// other BOP: recursively resolve qualifier values in left + right
			//================================================================================
			else {
				//--------------------------------------------------------------------------------
				/**/ tracer.println("other BOP = " + token.toTypedInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// copy only token but not its tree, because args will be set below
				curRetToken.flatClone(token);
				//--------------------------------------------------------------------------------
				// do it for left arg
				if(token.getLeft() != null) {
					try {
						Token tempRetToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
						_qualifierValEval(token.getLeft(), tempRetToken);
						curRetToken.setLeft(tempRetToken);
					} catch (SyntaxException e) {
						throw new SyntaxException("left arg of BOP '" + token.getRawText() + "'", m, e);
					}
				}
				//--------------------------------------------------------------------------------
				// do it for right arg
				if(token.getRight() != null) {
					try {
						Token tempRetToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
						_qualifierValEval(token.getRight(), tempRetToken);
						curRetToken.setRight(tempRetToken);
					} catch (SyntaxException e) {
						throw new SyntaxException("right arg of BOP '" + token.getRawText() + "'", m, e);
					}
				}
			}
			break;
			//================================================================================
			// UOP: recursively resolve in arg
			//================================================================================
		case UOP:
			//--------------------------------------------------------------------------------
			// TBD: we assume that UOP has a *right* arg
			/**/ tracer.println("UOP = " + token.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// copy only token but not its tree, because arg will be set below
			curRetToken.flatClone(token);
			//--------------------------------------------------------------------------------
			// do it for right arg
			if(null != token.getLeft()) {
				try {
					Token tempRetToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
					_qualifierValEval(token.getRight(), tempRetToken);
					curRetToken.setRight(tempRetToken);
				} catch (SyntaxException e) {
					throw new SyntaxException("arg of UOP '" + token.getRawText() + "'", m, e);
				}
			}
			break;

			//================================================================================
			// FOR ALL OTHER KINDS JUST RETURN A FLAT COPY OF THE TOKEN 
			//================================================================================
		default:
			//--------------------------------------------------------------------------------
			/**/ tracer.println("DEFAULT: token = " + token.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// copy only token but not its tree
			// - nb: no recursion needed, because the token is not an op expr, but a basic data type
			curRetToken.flatClone(token);
			break;
		}
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================