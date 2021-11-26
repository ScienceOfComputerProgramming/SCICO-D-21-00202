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
// CREATED:   January 2021 
//================================================================================
// CODE REVIEWS: 20210118 (eK);
//================================================================================

package parser.drawio;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.NodeList;

import pmmm.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// translate artifacts to one peer type
public class WiringTypesArtifacts extends Artifacts {
	// -------------------------------------------------------
	// for debug:
	/**/ private Tracer tracer = new Tracer();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// -------------------------------------------------------
	public WiringTypesArtifacts() {
	}
	// -------------------------------------------------------
	// pick out all artifacts from the given node list that are marked with <PMARTIFACT>
	// - nb  node list represents the elements of one drawio diagram 
	public WiringTypesArtifacts(NodeList mxCellNodeList) {
		// build up the artifacts collection
		super(mxCellNodeList);
	}

	//================================================================================
	// TRANSLATE
	//================================================================================
	//--------------------------------------------------------------------------------
	// recognize wiring type related artifacts and translate them to WiringTypes;
	// - nb: ignore not needed artifacts; 
	public Vector<WiringType> translate() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("parse " + artifacts.size() + " wirings's artifacts", Level.NO, m);
		//--------------------------------------------------------------------------------
		Vector<WiringType> wiringTypes = new Vector<WiringType>();
		//--------------------------------------------------------------------------------
		// local vars
		Vector<String> subArtifactNames;
		Vector<HashMap<String,String>> artifactsSubArtifactValues;
		HashMap<String,String> subArtifactValues;

		// ==================================================================
		// PASS 1: WIRING_TYPE__SPECIFICATION
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE unique sub artifacts of wiring 
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.WIRING_TYPE__NAME);
		subArtifactNames.add(Defines.WIRING_TYPE__SERVICE);
		subArtifactNames.add(Defines.WIRING_TYPE__WPROPS_TYPES);
		subArtifactNames.add(Defines.WIRING__WPROPS_DEFS); // !!! name shared with wiring... 
		// -------------------------------------------------------
		// RETRIEVE: their values
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.WIRING_TYPE__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PROCESS: nb: there may be 0 or more wiring specifications 
		for(int i = 0; i < artifactsSubArtifactValues.size(); i++) {
			subArtifactValues = artifactsSubArtifactValues.get(i); 
			// -------------------------------------------------------
			// create new wiring type
			WiringType wiringType = new WiringType();
			// -------------------------------------------------------
			/**/ tracer.println("WIRING_TYPE__NAME = " + wiringType.getWiringTypeName(), Level.NO, m);
			// -------------------------------------------------------
			// set values
			// - just pass on exceptions
			wiringType.setWiringTypeName(subArtifactValues.get(Defines.WIRING_TYPE__NAME));
			wiringType.setServiceName(subArtifactValues.get(Defines.WIRING_TYPE__SERVICE));
			wiringType.setRawWPropsTypes(subArtifactValues.get(Defines.WIRING_TYPE__WPROPS_TYPES));
			wiringType.setRawWiringTypeWPropsDefs(subArtifactValues.get(Defines.WIRING__WPROPS_DEFS));
			// .......................................................
			// add wiring type to wiring types
			wiringTypes.add(wiringType);
		}

		// ==================================================================
		// PASS 2: parse guards and actions
		// ==================================================================
		// create new object for translation; pass it my artifacts; share my wiring types;
		// - nb: translate enriches the shared wiring types by all guards and actions; 
		// -- found for any wiring types in the artifacts;
		// -- caution: wirings must be set to null;
		// -- pass on exc
		(new LinkArtifacts(artifacts)).translate(null /* wirings */, wiringTypes);
		
		// ==================================================================
		// done
		// ==================================================================
		return wiringTypes;  
	}
		
	//================================================================================
	// debug
	//================================================================================
	//--------------------------------------------------------------------------------
	// toString
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("--------\n");
		buf.append("Wiring Type:\n");  
		for(int i = 0; i < artifacts.size(); i++) {
			buf.append(artifacts.get(i).toString());
		}
		return new String(buf);		
	}
	

} // END OF CLASS


//================================================================================
//EOF
//================================================================================

