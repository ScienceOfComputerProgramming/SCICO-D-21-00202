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
// CODE REVIEWS: 20201229 (eK); 20210118 (eK);
//================================================================================

package parser.drawio;

import java.util.HashMap;
import java.util.Vector;

import pmmm.EntryType;
import pmmm.EntryTypes;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.Tracer;

//================================================================================
// translate artifacts to (many) entry types
public class EntryTypesArtifacts extends Artifacts {
	//--------------------------------------------------------------------------------
	// for debugging
	/**/ Tracer tracer = new Tracer();
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public EntryTypesArtifacts() {
	}
//	//--------------------------------------------------------------------------------
//	// pick out all artifacts from the given node list that are marked with <PMARTIFACT>
//	// - nb node list represents the elements of one drawio diagram 
//	public EntryTypesArtifacts(NodeList mxCellNodeList) {
//		// build up the artifacts collection
//		super(mxCellNodeList);
//	}

	//================================================================================
	// TRANSLATE
	//================================================================================
	//--------------------------------------------------------------------------------
	// recognize entry type related artifacts and translate them to EntryTypes;
	public EntryTypes translate() throws SyntaxException {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// collect entry types here
		EntryTypes entryTypes = new EntryTypes();
		/**/ tracer.println("parse all entry types artifacts", Level.NO, m);
		//--------------------------------------------------------------------------------
		// local vars
		Vector<String> subArtifactNames;
		Vector<HashMap<String,String>> artifactsSubArtifactValues;
		HashMap<String,String> subArtifactValues;

		// ==================================================================
		// PASS 1: ENTRY_TYPE__SPECIFICATION
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE: unique sub artifacts of entry type
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.ENTRY_TYPE__NAME);
		subArtifactNames.add(Defines.ENTRY_TYPE__EPROPS_TYPES);
		subArtifactNames.add(Defines.ENTRY_TYPE__EPROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values 
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.ENTRY_TYPE__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		/**/ tracer.println("n = " + artifactsSubArtifactValues.size(), Level.NO, m);
		/**/ tracer.println("artifacts = " + this.toString(), Level.NO, m);
		// -------------------------------------------------------
		// PROCESS: nb: there can be 0 or more entry type specifications in the diagram
		for(int i = 0; i < artifactsSubArtifactValues.size(); i++) {
			// -------------------------------------------------------
			// create new entry type
			EntryType entryType = new EntryType();
			// -------------------------------------------------------
			// get the entry type's sub artifact values
			subArtifactValues = artifactsSubArtifactValues.get(i); 
			// -------------------------------------------------------
			/**/ tracer.println("entry type name = " + subArtifactValues.get(Defines.ENTRY_TYPE__NAME), Level.NO, m);
			/**/ tracer.println("eprops types = " + subArtifactValues.get(Defines.ENTRY_TYPE__EPROPS_TYPES), Level.NO, m);
			/**/ tracer.println("eprops defs = " + subArtifactValues.get(Defines.ENTRY_TYPE__EPROPS_DEFS), Level.NO, m);
			// -------------------------------------------------------
			entryType.setEntryTypeName(subArtifactValues.get(Defines.ENTRY_TYPE__NAME));
			entryType.setRawEPropsTypes(subArtifactValues.get(Defines.ENTRY_TYPE__EPROPS_TYPES));
			entryType.setRawEPropsDefsTokenExpression(subArtifactValues.get(Defines.ENTRY_TYPE__EPROPS_DEFS));
			// -------------------------------------------------------
			/**/ tracer.println("assembled entryType = \n" + entryType, Level.NO, m);
			// -------------------------------------------------------
			// add entry type to entry types
			try {
				entryTypes.add(entryType);
			} catch (Exception e1) {
				throw new SyntaxException("duplicate entry in entry definitions; entry = " + entryType.getEntryTypeName(), m);
			}
		}
		
		//================================================================================
		// DONE
		//================================================================================
		return entryTypes;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// toString
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("--------\n");
		buf.append("Entries:\n");
		for(int i = 0; i < artifacts.size(); i++) {
			buf.append(artifacts.get(i).toString());
		}
		return new String(buf);		
	}

	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================

