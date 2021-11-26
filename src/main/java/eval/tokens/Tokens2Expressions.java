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

import pmmm.IOpDefs;
import pmmm.Op;
import pmmm.OpDefs;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// transforms token sequences into expression trees;
// uses expression automaton (for documentation: see drawio picture)
public class Tokens2Expressions {
	// -------------------------
	// tracer
	private Tracer tracer = new Tracer();
	// -------------------------
	// all peer model ops
	// - STATIC!!
	private static OpDefs opDefs = new OpDefs();
	// -------------------------
	// sequence of tokens: 
	// - this is the current working vector for melding!
	// - for internal use only!!!!
	private TV tokenV = new TV();
	// -------------------------
	// OUTPUT:
	// vector of token vectors
	private TokenExpression tokenVV = new TokenExpression();
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Tokens2Expressions(TokenExpression tokenVV) {
		this.tokenVV = tokenVV;
	}
	//--------------------------------------------------------------------------------
	// get tokenVV
	public TokenExpression getTokenVV() {
		return tokenVV;
	}
	

	//================================================================================
	// MELD
	//================================================================================
	
	//--------------------------------------------------------------------------------
	// reduce all tokens into token trees;
	// treat bracketed tokens first and then meld remaining tokens;
	// super easy algorithm:
	// ******************************************
	// *  LOOP over tokenVV                     *
	// *    LOOP over tokenV from right to left *
	// *      if '('                            *
	// *         find matching ')'              *
	// *         remove both brackets           *
	// *         meld bracketed tokens          *
	// *    meld remaining tokens               *
	// ******************************************
	public TokenExpression meld() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("====================================================================================================================", Level.ME, m);
		//--------------------------------------------------------------------------------
		// set phase of TVV and store phase in local var
		String myPhase = "MELTING";
		tokenVV.setPhase(myPhase);
		// ---------------------------------------
		// iterate over token vector vector
		// CAUTION: do it from right to left, because vector will shrink;
		for(int i = 0; i < tokenVV.size(); i++) { 
			//--------------------------------------------------------------------------------
			// get next tokenV
			this.tokenV = tokenVV.get(i);
			/**/ tracer.println("next tokenV = " + tokenV.toStringIntelligence(myPhase), Level.NO, m);						
			//--------------------------------------------------------------------------------
			// LOOP over tokenV from right to left
			for(int lbIndex = tokenV.size() - 1; lbIndex >= 0; lbIndex--) {
				//--------------------------------------------------------------------------------
				// get next token
				Token token = tokenV.get(lbIndex);
				//--------------------------------------------------------------------------------
				// LB?
				if(token.kind == IToken.Kind.LB) {
					/**/ tracer.println("LB found at index = " + lbIndex, Level.ME, m);						
					//--------------------------------------------------------------------------------
					// find next RB to the right 
					// - get stack size
					int rbIndex = -1;
					for(rbIndex = lbIndex + 1; rbIndex < tokenV.size(); rbIndex++) {
						if(tokenV.get(rbIndex).kind == IToken.Kind.RB) {
							// found -> break from for loop
							break;
						}
					}
					if(rbIndex < 0)
						throw new SyntaxException("ill. bracketing structure: ')' is missing", m);
					//--------------------------------------------------------------------------------
					// remove both brackets in correct order
					// - nb: remove shifts everything to the left, so start with RB and then remove LB
					/**/ tracer.println("remove brackets at indices " + rbIndex + " and " + lbIndex + ", nTokens = " + tokenV.size(), Level.ME, m);
					tokenV.remove(rbIndex);
					tokenV.remove(lbIndex);
					//--------------------------------------------------------------------------------
					// meld the bracketed tokens within tokenV 
					// - nb: 2 brackets were removes
					meldRange(lbIndex, rbIndex - 1);
				}				
			}
			// finally meld all remaining tokens within tokenV, i.e. from index 0 to the end
			// - needed as the entire term is not necessarily bracketed 
			meldRange(0, tokenV.size());
		}
		/**/ tracer.println("RESULT: " + tokenVV.size() + " TV(s) ----> " + tokenVV, Level.ME, m);
		//--------------------------------------------------------------------------------
		// set phase of TVV
		tokenVV.setPhase("MELTED");
		//--------------------------------------------------------------------------------
		return tokenVV;
	}
	

	//--------------------------------------------------------------------------------
	// reduce current tokens between given indices (including the firs one, and excluding the second one) into token tree;
	// assumptions: 
	// - binary ops are left associative 
	// - unary ops stand on the left hand side of the arg 
	// super simple algorithm: 
	// LOOP:
	// - get next highest op priority
	// -- loop over all tokens from left to right
	// --- if op has the required priority
	// ----  if unary: meld it with its right args into subtree
	// ----  if binary: meld it with its left + right args into subtree
	// ----  replace these 3 tokens by the new subtree
	private void meldRange(int fromIndex, int toIndex) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// current token, left token, right token;
		Token token, lToken, rToken = null;
		// current op;
		Op op;
		IOpDefs.Arity arity;
		
		/**/ tracer.println("start: __________________________________________________________", Level.NO, m);
		/**/ tracer.println("input tokens = " + tokenVV, Level.LO, m);
		/**/ tracer.println("fromIndex = " + fromIndex + ", toIndex = " + toIndex + ", nTokens = " + tokenV.size(), Level.LO, m);
		// TBD: assert that indices are in range
		if((fromIndex < 0) || (fromIndex > tokenV.size()) || (toIndex < 0) || (toIndex > tokenV.size())) {
			throw new SNHException(897342, "index out of range", m);
		}
		// init prio with not existing high operator priority
		int prio = opDefs.getHighestOpPriority() + 1;
		// ---------------------------------------
		// iterate over all priorities from highest to lowest
		for (;;) {
			// ---------------------------------------
			// get next highest op priority
			prio = opDefs.getNextHighestOpPriority(prio);
			/**/ tracer.println("next priority ====================> " + prio + ":", Level.LO, m);
			if(prio < 0) {
				// all priorities have been processed, break from loop over op priorities
				break;
			}
			// ---------------------------------------
			// meld all operators with this priority with their args
			// - do it it from start for each operator until no one was found any more
			int nIntegratedTokens;
			do {
				nIntegratedTokens = 0;
				// ---------------------------------------
				// iterate over tokens 
				for(int j = fromIndex; j < toIndex; j++) {
					token = tokenV.get(j);
					/**/ tracer.println("next token = " + token.toString(), Level.NO, m);
					// ---------------------------------------
					// OP:
					// - not yet melted op, which is unary or binary and has desired priority
					// -- meld right hand side token into op
					if((! token.treatedFlag) && ((token.kind == IToken.Kind.BOP) || (token.kind == IToken.Kind.UOP))) {
						// ---------------------------------------
						// check its arity first
						arity = (token.kind == IToken.Kind.BOP) ? IOpDefs.Arity.BINARY : IOpDefs.Arity.UNARY;
						// ---------------------------------------
						// retrieve its operator spec depending on its arity!
						// - nb: there could be ops with same name but different arity, so it must be done this way
						try {
							op = opDefs.getOp(arity, token.rawText);
							/**/ tracer.println("is OPERATOR " + op, Level.LO, m);
						} catch (NotFoundException e) {
							throw new SyntaxException("ill. op", m, e);
						}
						if (op.getPriority() == prio) {
							/**/ tracer.println("trying to meld " + op + "", Level.LO, m);  // !!!
							// ---------------------------------------
							// TREAT ARG(S):
							// RIGHT ARG
							/**/ tracer.println("curTokens.size() = " + tokenV.size() + ", j = " + j, Level.NO, m);
							if(tokenV.size() > j + 1) {
								// ---------------------------------------
								// get right token
								rToken = tokenV.get(j + 1);
								/**/ tracer.println("get right token " + rToken + " into op", Level.NO, m);
								// ---------------------------------------
								// meld right arg into op
								token.right = rToken;
								// ---------------------------------------
								// mark right arg as integrated
								rToken.integratedFlag = true;
								nIntegratedTokens++;
								// ---------------------------------------
								// LEFT ARG (if binary op):
								if(token.kind == IToken.Kind.BOP) {
									// ---------------------------------------
									// get left token
									if(j > 0) 
										lToken = tokenV.get(j - 1);
									else 
										throw new SyntaxException(j + ".Token: missing left argument of " + op + ".", m);
									// ---------------------------------------
									// meld left token into op
									token.left = lToken;
									// ---------------------------------------
									// mark left argument as integrated
									lToken.integratedFlag = true;
									nIntegratedTokens++;
								}
								// ---------------------------------------
								// set left arg of unary op to empty
								else {
									token.left = new Token(IToken.Kind.EMPTY, "");
								}
								// ---------------------------------------
								// mark this operator-token as melted
								token.treatedFlag = true;
								// ---------------------------------------
								/**/ tracer.println("" + token.toNode(), Level.LO, m);		
							} else {
								/**/ tracer.println("tokens: " + tokenV, Level.LO, m);		
								throw new SyntaxException(j + ".Token: missing right argument of " + op + ".", m);
							}
						} // if prio
					} // if binary/unary op	
					// ---------------------------------------
					// remove integrated tokens from vector 
					/**/ tracer.println("removing " + nIntegratedTokens + " integrated tokens", Level.NO, m);		
					for(int n = 0; n < nIntegratedTokens; n++) {
						// start at beginning for each in order not to destroy the vector
						// - nb tokens are only removed from vector but not deallocated as they are still used in the tree
						for(int q = fromIndex; q < toIndex; q++) {
							if(tokenV.get(q).integratedFlag) {
								/**/ tracer.println("removing " + tokenV.get(q), Level.NO, m);	
								// ---------------------------------------
								// remove token
								tokenV.remove(q);
								break;
							}
						} 
					}
					// ---------------------------------------
					// adapt toIndex due to shrinking !!
					toIndex -= nIntegratedTokens;
					// ---------------------------------------
					////////////////// tokensVector.set(i, curTokens);
					// ---------------------------------------
					// if vector was modified brake from loop an start again at from index
					if(nIntegratedTokens > 0) {
						break;
					}
				} // for j ... meld tokens
			} while (nIntegratedTokens > 0); // do while there were integrated tokens
		} // for (;;)	
		/**/ tracer.println("output tokens = " + tokenVV, Level.LO, m);
	}
	

} // END OF CLASS


//================================================================================
// EOF
//================================================================================
