//================================================================================
//Peer Model Tool Chain
//Copyright (C) 2021 Eva Maria Kuehn
//--------------------------------------------------------------------------------
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as
//published by the Free Software Foundation, either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
//================================================================================
// SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
// AUTHOR:    Eva Maria Kuehn
// CREATED:   December 2020 
//================================================================================
// CODE REVIEWS: 20210113 (eK);
//================================================================================

package pmmm;

import java.util.HashMap;

import eval.IData;
import eval.tokens.*;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// propy types  
// - serve for optimized access
// - which are special expressions of the form "<typeName> : <propName>"
// - the super class maintains the props defs in form of raw strings and in form of tokens
// !!! CAUTION: keep TVV also up-to-date with hash map -- needed for code gen !!!
public class PropsTypes extends TokenExpression implements IEvaluation {
	//--------------------------------------------------------------------------------
	// for debug
	private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// PROPS TYPES MAP:
	// "overlay": label props types in form of a hash map 
	// - with key = propLabel and value = token of the ***full*** prop type expression
	HashMap<String,Token> propsTypesMap = new HashMap<String,Token>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PropsTypes() {
		super();
	}

	//================================================================================
	// COPY / CLONE
	//================================================================================
	//--------------------------------------------------------------------------------
	// deep copy me from the given PropsTypes
	// - deep copying of both: hash map plus tvv
	public void deepCloneFromPropsTypes(PropsTypes propsTypesToBeCloned) {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// first clone hash map
		// - clear
		propsTypesMap = new HashMap<String,Token>();
		// - copy
		for (HashMap.Entry<String, Token> entry : propsTypesToBeCloned.propsTypesMap.entrySet()) {
			Token copiedToken = entry.getValue().deepCopy(); 
			this.propsTypesMap.put(entry.getKey(), copiedToken);
		}		
		//--------------------------------------------------------------------------------
		/**/ tracer.println("copied hash map:" +
				" \n- my props defs = " + this.toPairs() + ";\n-- as tvv = " + this.tokenVV + 
				";\n- propsTypessToBeCloned = " + propsTypesToBeCloned.toPairs() + ";\n-- as tvv = " + propsTypesToBeCloned.tokenVV, Level.NO, m);
		//--------------------------------------------------------------------------------
		// second copy tvv 
		super.deepCloneFromTokenExpression(propsTypesToBeCloned);
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add prop type if not yet
	public void add(String propLabel, Token token) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// if not yet there
		if(propsTypesMap.get(propLabel) == null) {
			//--------------------------------------------------------------------------------
			// first add to my hash map
			propsTypesMap.put(propLabel, token);
			//--------------------------------------------------------------------------------
			// second add to my tvv
			this.tokenVV.add(new TV(token));
		}
		else
			throw new SyntaxException("duplicate prop type '" + propLabel + "'", m); 
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	public int getSize() {
		return propsTypesMap.size();
	}
	//--------------------------------------------------------------------------------
	// get type by name
	// - throws exception if name does not exist
	public IData.Type getType(String propLabel) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ Object m = new Object(){}; // debug
		//--------------------------------------------------------------------------------
		Token token = propsTypesMap.get(propLabel);
		if(token == null) {
			// this fu is also used by implementation as a test, so do not raise user error
			throw new NotFoundException("type of '" + propLabel + "' not defined", m);
		}
		// TBD: assert that "=" op
		return token.getLeft().getType();
	}
	//--------------------------------------------------------------------------------
	// test if a labelName
	public boolean nameExists(String labelName) {
		if(propsTypesMap.get(labelName) == null)
			return false;
		return true;
	}
	
	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	// !!! nb: the rest of the interface fus is inherited !!!
	//================================================================================
	//================================================================================

	//================================================================================
	// OVERWRITE DATA TYPE EVAL 
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate all token types
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg = "eval data types of props types: ";
		try {
			//--------------------------------------------------------------------------------
			// first eval types of all tokens
			super.evalDataTypes(context);
			//--------------------------------------------------------------------------------
			// !!! generate name declaration pairs !!!
			propsTypesMap = tvv2PropsTypesHashMap();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e1) {
			// just pass on the exception
			throw e1;
		} catch (SNHException e2) {
			throw new SNHException(100007, errMsg, m, e2);
		}
	}	

	//================================================================================
	//================================================================================
	// DEBUG
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select the default fu here
	public String toString() {
		return toStringIntelligence();
	}
	//--------------------------------------------------------------------------------
	// return depending on evaluation phase 
	public String toStringIntelligence() {
		return super.toStringIntelligence();
	}
	//--------------------------------------------------------------------------------
	// return name/value pairs 
	public String toPairs() {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		for (HashMap.Entry<String,Token> entry : propsTypesMap.entrySet()) {
			/// buf.append(entry.getKey() + ": " + entry.getValue().toUserInfo() + "; ");
			buf.append(entry.getValue().toUserInfo() + "; ");
		}
		buf.append("}");
		return new String(buf);
	}	


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
