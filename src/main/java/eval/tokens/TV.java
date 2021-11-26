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

// import debug.*;
import java.util.Vector;

import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// token vector
public class TV {

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public TV() {
	}
	//--------------------------------------------------------------------------------
	// info: used for merging of props which needs to build up a new tokenV...
	public TV(Token rootToken) {
		tokenV.add(rootToken);
	}
	//--------------------------------------------------------------------------------
	// debug
	// private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// vector of tokens
	protected Vector<Token> tokenV = new Vector<Token>();

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	// get size
	public int size() {
		return tokenV.size();
	}
	//--------------------------------------------------------------------------------
	// get token at index
	public Token get(int i) {
		return tokenV.get(i);
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add token 
	public void add(Token t) {
		tokenV.add(t);
	}

	//================================================================================
	// REMOVE
	//================================================================================
	//--------------------------------------------------------------------------------
	// remove token at index and return removed token
	public Token remove(int i) throws SNHException {
		return tokenV.remove(verifyIndex(i));
	}
	
	//================================================================================
	// CHECK
	//================================================================================
	//--------------------------------------------------------------------------------
	// check validity of index i;
	// raises exception if not ok;
	// otherwise returns i;
	private int verifyIndex(int i) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if((i < 0) || (i >= tokenV.size())) {
			throw new SNHException(100399, "index out of range", m);
		}
		return i;
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// any tokens out there?
	public boolean isEmpty() {
		if(tokenV.size() <= 0)
			return true;
		return false;
	}

	//================================================================================
	// COPY
	//================================================================================
	//--------------------------------------------------------------------------------
	// copy vector but reuse token object!
	public TV copy() {
		TV cloneTV = new TV();

		for(int i = 0; i < tokenV.size(); i++) {
			cloneTV.add(tokenV.get(i));
		}
		return cloneTV;
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// check that the TV has exactly one one "root" token and return this token
	public Token getRootToken() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(tokenV.isEmpty())
			throw new SyntaxException("empty root token", m);
		if(tokenV.size() != 1) 
			throw new SyntaxException("more than one root tokens", m);
		return tokenV.get(0);
	}

	//================================================================================
	// CONVERT TO USER INFO
	//================================================================================
	//--------------------------------------------------------------------------------
	// CAUTION: do not change: is also used for PM-DSL code gen!!!
	// for user messages: print flat info in bracketed, nice and human readable form
	// - nb: there should be only one token in the tvv
	public String toUserInfo() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenV.size(); i++) {
			if(i > 0)
				buf.append(";  "); // snh...
			buf.append(tokenV.get(i).toUserInfo());
		}
		return new String(buf);
	}
	
	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu here;
	public String toString() {
		return toRaw();
	}	
	//--------------------------------------------------------------------------------
	// depending on the current phase of parsing select the best suitable function;
	// - tokens numbered (using their index!); one token per line
	public String toStringIntelligence(String phase) {
		StringBuffer buf = new StringBuffer();
		if(phase.equals("UNDEFINED")) {
			buf.append(toRaw());
		}
		else if(phase.equals("MINCING")) {
			buf.append(toNode_plus_Kind());
		}
		else if(phase.equals("MINCED")) {
			buf.append(toNode_plus_Kind());
		}
		if(phase.equals("MELTING")) {
			buf.append(toNode());
		}
		if(phase.equals("MELTED")) {
			buf.append(toInfo(false /*enforceDisplayOfTypeInfo */));
		}
		if(phase.equals("EVALUATING")) {
			buf.append(toInfo(true /*enforceDisplayOfTypeInfo */));
		}
		if(phase.equals("EVALUATED")) {
			buf.append(toInfo(true /*enforceDisplayOfTypeInfo */));
		}
		return new String(buf);
	}
	//--------------------------------------------------------------------------------
	// raw info about each token in 1 line
	// - tokens numbered (using their index!); tokens are separated by blanks
	// to be used before tokenization;
	public String toRaw() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenV.size(); i++) 
			buf.append(i + ":[" + tokenV.get(i).toRaw() + "]  ");
		return new String(buf);
	}
	//--------------------------------------------------------------------------------
	// flat node info about each token
	// - tokens numbered (using their index!); one token per line
	// to be used after mince phase of tokenizer;
	public String toNode() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenV.size(); i++) 
			buf.append(i + ":[" + tokenV.get(i).toNode() + "]  ");
		return new String(buf);
	}
	//--------------------------------------------------------------------------------
	// flat node info info about each token containing kind + raw text
	// - tokens numbered (using their index!); one token per line
	// to be used after mince phase of tokenizer;
	public String toNode_plus_Kind() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenV.size(); i++) 
			buf.append(i + ":[" + tokenV.get(i).toNode_plus_Kind() + "]  ");
		return new String(buf);
	}
	//--------------------------------------------------------------------------------
	// flat node info consisting of: kind + raw text + parser flags
	// - tokens numbered (using their index!); one token per line
	// to be used during melting phase of tokenizer;
	public String toNode_plus_TypeInProgress_Kind_Flags() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenV.size(); i++) 
			buf.append(i + ":[" + tokenV.get(i).toNode_plus_TypeInProgress_Kind_Flags() + "]  ");
		return new String(buf);
	}
	
	//--------------------------------------------------------------------------------
	public String toInfo() {
		return toInfo(false /* enforceDisplayOfTypeInfo */);
	}
	//--------------------------------------------------------------------------------
	public String toTypedInfo() {
		return toInfo(true /* enforceDisplayOfTypeInfo */);
	}
	//--------------------------------------------------------------------------------
	// intelligently display this tv;
	// - display type info if enforceDisplayOfTypeInfo is set or if status is EVALUATED
	// - tokens numbered (using their index!);
	// - one token per line
	// to be used after meld phase of tokenizer;
	public String toInfo(boolean enforceDisplayOfTypeInfo) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenV.size(); i++) 
			buf.append(i + ":[" + tokenV.get(i).toInfo(enforceDisplayOfTypeInfo) + "]  ");
		return new String(buf);
	}
	
	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================
