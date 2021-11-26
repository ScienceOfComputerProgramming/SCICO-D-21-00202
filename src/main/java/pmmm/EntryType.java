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

import eval.IData;
import eval.tokens.TokenExpression;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// definition of an entry
// - consists of entry name and entry properties (which are name/type pairs)
public class EntryType implements IEvaluation {
	//--------------------------------------------------------------------------------
	// for debugging
	private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// "entry name" aka "entry type"
	protected String entryTypeName = "";
	//--------------------------------------------------------------------------------
	// EPROPS TYPES:
	protected PropsTypes epropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// EPROPS DEFS (defaults):
	protected TokenExpression epropsDefsTokenExpression = new TokenExpression("");
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public EntryType() {
	}

	//================================================================================
	// CLONE
	//================================================================================
	//--------------------------------------------------------------------------------
	// deep clone me from given entry type
	public void deepCloneFromEntryType(EntryType fromEntryType) {
		this.entryTypeName = new String(fromEntryType.entryTypeName);
		this.epropsTypes.deepCloneFromPropsTypes(fromEntryType.epropsTypes);
		this.epropsDefsTokenExpression.deepCloneFromTokenExpression(fromEntryType.epropsDefsTokenExpression);
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setEntryTypeName(String entryTypeName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			// name validity check 
			PmUtil.isValidName(entryTypeName, false /* emptyIsAllowedFlag */, true /* wildcardIsAllowedFlag */);
			// set
			this.entryTypeName = entryTypeName;
		} catch (SyntaxException e) {
			throw new SyntaxException("entry type name", m, e);
		}
	}
	//--------------------------------------------------------------------------------
	public void setRawEPropsTypes(String raw) {
		this.epropsTypes.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setRawEPropsDefsTokenExpression(String raw) {
		this.epropsDefsTokenExpression.setRaw(raw);
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getEntryTypeName() {
		return entryTypeName;
	}
	//--------------------------------------------------------------------------------
	public PropsTypes getEPropsTypes() {
		return epropsTypes;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getPropsDefsTokenExpression() {
		return epropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getEPropsDefsTokenExpression() {
		return epropsDefsTokenExpression;
	}

	//================================================================================
	// TEST
	//================================================================================
	//--------------------------------------------------------------------------------
	public boolean isEmpty() {
		if(util.Util.isEmptyString(entryTypeName))
			return true;
		return false;
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
	// tokenize raw eprops defs
	// - nb: meld ... might throw exception;
	// - produces also "usable form" of props, i.e. name/type pairs 
	// throws (user)exceptions
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "entry type " + this.entryTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// EPROPS TYPES:
			errMsg2 = "/eprops types";
			epropsTypes.tokenize();
			//--------------------------------------------------------------------------------
			// EPROPS DEFS:
			errMsg2 = "/eprops defs";
			epropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(103060, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// EVAL DATA TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate all token types
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "entry type " + this.entryTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// EPROPS TYPES:
			errMsg2 = "/eprops types";
			epropsTypes.evalDataTypes(context.switch2EntryTypesEPropsTypes());
			//--------------------------------------------------------------------------------
			// EPROPS DEFS:
			// - resolve qualifier types
			errMsg2 = "/eprops defs qualifier types";
			epropsDefsTokenExpression.resolveQualifierTypes(context.getQualifierContext());
			//................................................................................
			/**/ tracer.println("PROPS DEFS (raw) = " + epropsDefsTokenExpression.getRaw(), Level.NO, m); 
			/**/ tracer.println("PROPS DEFS (tvv) = " + epropsDefsTokenExpression.toInfo(), Level.NO, m); 
			//................................................................................
			// - eval data types
			errMsg2 = "/eprops defs";
			epropsDefsTokenExpression.evalDataTypes(context.switch2EntryTypesEPropsDefs(epropsTypes));
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(222204, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// PLAUSI CHECK
	//================================================================================
	//--------------------------------------------------------------------------------
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "entry type " + this.entryTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// EPROPS TYPES:
			errMsg2 = "/eprops types";
			epropsTypes.plausiCheck();
			//--------------------------------------------------------------------------------
			// EPROPS DEFS:
			errMsg2 = "/eprops defs";
			epropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
		} catch(SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch(SNHException e) {
			throw new SNHException(369920, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	//================================================================================
	// QUERIES
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// retrieve eprop type by name
	public IData.Type getEPropType(String epropName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return epropsTypes.getType(epropName);
		} catch (NotFoundException e) {
			throw new NotFoundException("eprop", m, e);
		}
	}

	//================================================================================
	//================================================================================
	// DEBUG
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu
	public String toString() {
		return toStructuredString(true);
	}
	//--------------------------------------------------------------------------------
	public String toString(boolean showProcessedFieldsFlag) {
		return toStructuredString(showProcessedFieldsFlag);
	}
	//--------------------------------------------------------------------------------
	// print: 
	// - entry name 
	// - each entry property per row
	public String toStructuredString(boolean showProcessedFieldsFlag) {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("ENTRY TYPE:", entryTypeName + "\n"));		
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.propsTypes2StructuredString("eprops types", epropsTypes));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("eprops defs", epropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		return new String(buf);
	}

} // END OF CLASS


//================================================================================
//EOF
//================================================================================