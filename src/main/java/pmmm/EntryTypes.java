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

import java.util.Vector;

import eval.IData;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// collection of entry definitions
public class EntryTypes implements IEvaluation {
	//--------------------------------------------------------------------------------
	// /**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// ENTRY TYPES:
	protected Vector<EntryType> entryTypes = new Vector<EntryType>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// add sys entry types
	public EntryTypes() {	
		this.addRawSysEntryTypes();
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public Vector<EntryType> getEntryTypes() {
		return entryTypes;
	}
	//--------------------------------------------------------------------------------
	public int getSize() {
		return entryTypes.size();
	}
	//--------------------------------------------------------------------------------
	public EntryType get(int i) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assertion
		if(i < 0 || i >= entryTypes.size())
			throw new SNHException(377773, "entry types index out of range; i = " + i, m);
		//--------------------------------------------------------------------------------
		// get it
		return entryTypes.get(i);
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add one entry type
	// check for duplicates and throw exception if entry type name exists already
	public void add(EntryType entryType) throws SyntaxException {	
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		for(int i = 0; i < entryTypes.size(); i++) {
			if(entryTypes.get(i).entryTypeName.equals(entryType.entryTypeName)) {
				throw new SyntaxException("duplicate entry type " + entryType.entryTypeName, m);
			}
		}
		entryTypes.add(entryType);
	}
	
	//--------------------------------------------------------------------------------
	// create and add raw info for system entry types;
	public void addRawSysEntryTypes() {
		//--------------------------------------------------------------------------------
		// SYS ENTRY TYPES:
		// - TBD: entry names are hardcoded 
		//--------------------------------------------------------------------------------
		// INIT: artificial sys entry used by preprocessor
		addSysEntryType("INIT");
		//--------------------------------------------------------------------------------
		// STOP: sys entry used by Stop Peer in go automaton
		addSysEntryType("STOP");
		//--------------------------------------------------------------------------------
		// EXCEPTION_WRAP: sys entry that exists in go automaton
		EntryType entryType = addSysEntryType("EXCEPTION_WRAP");
		//................................................................................
		// - add all prop types of exc wrap that exist in the go automaton so that user can use them
		// -- nb: clearly the orig entry's props cannot be added... 
		// --- TBD: access maybe possible via "EXCEPTION_WRAP." qualifier
		entryType.epropsTypes.setRaw("STRING : etype; INT : exc_time; INT : ettl;"); 
	}
	//--------------------------------------------------------------------------------
	// help fu: add sys entry type
	// - return entry type for possible further manipulations
	private EntryType addSysEntryType(String entryTypeName) {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// create new entry
		EntryType entryType = new EntryType();
		try {
			// set entry type name
			entryType.setEntryTypeName(entryTypeName);
		} catch (SyntaxException e) {
			// ignore, because the sys entry types have valid names
		}
		// add
		this.entryTypes.add(entryType);
		// ok
		return entryType;
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
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// ENTRY TYPES:
		for(int i = 0; i < entryTypes.size(); i++) {
			entryTypes.get(i).tokenize();
		}
	}

	//================================================================================
	// EVAL DATA TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// ENTRY TYPES:
		for(int i = 0; i < entryTypes.size(); i++) {
			entryTypes.get(i).evalDataTypes(context);
		}
	}
	
	//================================================================================
	// PLAUSI CHECK
	//================================================================================
	//--------------------------------------------------------------------------------
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// ENTRY TYPES:
		for(int i = 0; i < entryTypes.size(); i++) {
			entryTypes.get(i).plausiCheck();
		}
	}
	
	//================================================================================
	//================================================================================
	// QUERY 
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// get entry type for given entry type name
	// throws exception if not found
	public EntryType getEntryType(String entryTypeName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ Object m = new Object(){}; // debug
		//--------------------------------------------------------------------------------
		EntryType entryType;
		for(int i = 0; i < entryTypes.size(); i++) {
			entryType = entryTypes.get(i);
			if(entryType.entryTypeName.equals(entryTypeName))
				return entryType; // found
		}
		throw new NotFoundException("entry type '" + entryTypeName + "' not found", m);
	}
	//--------------------------------------------------------------------------------
	// get eprop type by given entry type name and eprop name
	// throws exception if not found
	public IData.Type getEPropType(String entryTypeName, String epropName) throws Exception {
		for(int i = 0; i < entryTypes.size(); i++) {
			EntryType entryType = entryTypes.get(i);
			if(entryType.entryTypeName.equals(entryTypeName)) {
				return entryType.getEPropType(epropName);
			}
		}
		throw new Exception("entry prop '" + entryTypeName + "." + epropName + "' not found");
	}
	//--------------------------------------------------------------------------------
	// search for entry prop with given prop name
	// throws exception if not found
	// returns entry name if found
	// nb: needed for plausi check only
	public String searchAnyEPropAndReturnEntryTypeName(String epropName) throws Exception {
		for(int i = 0; i < entryTypes.size(); i++) {
			EntryType entryType = entryTypes.get(i);
			try {
				// trick: reuse type search fu here; 
				// - raises exception if not found
				entryType.getEPropType(epropName);
				// return corresponding entry name, if prop was found, ie no exception
				return entryType.entryTypeName;
			} catch (Exception e) {
				// continue
			}
		}
		throw new Exception("eprop not found in any entry type");
	}

	//================================================================================
	// DEBUG
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
	// return entry definitions in structured form
	public String toStructuredString(boolean showProcessedFieldsFlag) {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		for(int i = 0; i < entryTypes.size(); i++) {
			//--------------------------------------------------------------------------------
			if(i == 0)
				buf.append(ui.Out.borderline('-'));
			//--------------------------------------------------------------------------------
			buf.append(entryTypes.get(i).toStructuredString(showProcessedFieldsFlag));
			//--------------------------------------------------------------------------------
			buf.append(ui.Out.borderline('-'));
		}
		//--------------------------------------------------------------------------------
		return new String(buf);		
	}

} // END OF CLASS


//================================================================================
//EOF
//================================================================================