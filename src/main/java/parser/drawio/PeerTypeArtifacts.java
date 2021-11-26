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
public class PeerTypeArtifacts extends Artifacts {
	// -------------------------------------------------------
	// for debug:
	/**/ private Tracer tracer = new Tracer();
	// -------------------------------------------------------
	// peer name
	String peerTypeName = "";

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// -------------------------------------------------------
	// pick out all artifacts from the given node list that are marked with <PMARTIFACT>
	// - nb node list represents the elements of one drawio diagram 
	public PeerTypeArtifacts(String peerTypeName, NodeList mxCellNodeList) {
		// build up the artifacts collection
		super(mxCellNodeList);
		// set peer type name
		this.peerTypeName = peerTypeName;
	}

	//================================================================================
	// TRANSLATE
	//================================================================================
	//--------------------------------------------------------------------------------
	// recognize peer type related artifacts and translate them to PeerType;
	// - nb: is one peer type in the diagram; 
	public PeerType translate() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("parse peer's artifacts", Level.NO, m);
		//--------------------------------------------------------------------------------
		PeerType peerType = new PeerType(peerTypeName);
		//--------------------------------------------------------------------------------
		// local vars
		Vector<String> subArtifactNames;
		Vector<HashMap<String,String>> artifactsSubArtifactValues;
		HashMap<String,String> subArtifactValues;
		Wiring wiring;
		String name;

		// ==================================================================
		// PASS 1: PEER_TYPE__SPECIFICATION: 
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE: unique sub artifacts of peer type
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.PEER_TYPE__NAME);
		subArtifactNames.add(Defines.PEER_TYPE__PPROPS_TYPES);
		subArtifactNames.add(Defines.PEER_TYPE__PPROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values 
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.PEER_TYPE__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PLAUSI CHECK:
		// - nb: there must be zero or one peer type specifications in the diagram
		if(artifactsSubArtifactValues.size() > 1)
			throw new SyntaxException("more than one peer type declaration for " + peerTypeName, m);
		// -------------------------------------------------------
		// PROCESS:
		// - nb: in this diagram is at most one peer type specification
		if(artifactsSubArtifactValues.size() == 1) {
			// -------------------------------------------------------
			// get the one peer type's sub artifact values
			subArtifactValues = artifactsSubArtifactValues.get(0); 
			// -------------------------------------------------------
			// NAME: just verify whether it is the same name as in the diagram name
			try {
				peerType.verifyPeerTypeName(subArtifactValues.get(Defines.PEER_TYPE__NAME));
			} catch (Exception e) {
				throw new SyntaxException("ill. peer type name: " + subArtifactValues.get(Defines.PEER_TYPE__NAME) + 
						"(found in diagram PeerType:'" + peerTypeName + "' does not correlate with peer type name specified in diagram name", m);
			}
			// -------------------------------------------------------
			// PPROPS TYPES: set them in peer type
			peerType.setRawPPropsTypes(subArtifactValues.get(Defines.PEER_TYPE__PPROPS_TYPES));
			// -------------------------------------------------------
			// PPROPS DEFS: set them in peer type
			peerType.setRawPeerTypePPropsDefs(subArtifactValues.get(Defines.PEER_TYPE__PPROPS_DEFS));
		}

		// ==================================================================
		// PASS 2: WIRING_CONFIG__SPECIFICATION  
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE unique sub artifacts of wiring config
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.WIRING_CONFIG__NAME);
		subArtifactNames.add(Defines.WIRING_CONFIG__TYPE);
		subArtifactNames.add(Defines.WIRING_CONFIG__WPROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.WIRING_CONFIG__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PROCESS: nb: a peer type might have 0 or more wiring config specifications 
		for(int i = 0; i < artifactsSubArtifactValues.size(); i++) {
			subArtifactValues = artifactsSubArtifactValues.get(i); 
			// -------------------------------------------------------
			// create new wiring
			wiring = new Wiring();
			// -------------------------------------------------------
			// PLAUSI CHECK: there must be a wiring config name
			name = subArtifactValues.get(Defines.WIRING_CONFIG__NAME);
			if(name == null || name.length() == 0)
				throw new SyntaxException("wiring config specification: missing wiring config name (in peer type " + peerTypeName + ")", m);
			// -------------------------------------------------------
			/**/ tracer.println("WIRING_NAME = " + name, Level.NO, m);
			/**/ tracer.println("WIRING_CONFIG__TYPE = " + subArtifactValues.get(Defines.WIRING_CONFIG__TYPE), Level.NO, m);
			// -------------------------------------------------------
			// set values
			wiring.setRawWiringNames(name);
			wiring.setWiringTypeName(subArtifactValues.get(Defines.WIRING_CONFIG__TYPE));
			wiring.setRawWiringWPropsDefs(subArtifactValues.get(Defines.WIRING_CONFIG__WPROPS_DEFS));
			// .......................................................
			// add wiring config to peer
			peerType.addWiring(wiring); 
		}

		// ==================================================================
		// PASS 3: WIRING__SPECIFICATION
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE unique sub artifacts of wiring 
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.WIRING__NAME);
		subArtifactNames.add(Defines.WIRING__SERVICE);
		subArtifactNames.add(Defines.WIRING__WPROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.WIRING__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PROCESS: nb: a peer type might have 0 or more wiring specifications 
		for(int i = 0; i < artifactsSubArtifactValues.size(); i++) {
			subArtifactValues = artifactsSubArtifactValues.get(i); 
			// -------------------------------------------------------
			// create new wiring
			wiring = new Wiring();
			// -------------------------------------------------------
			// PLAUSI CHECK: there must be a wiring name
			name = subArtifactValues.get(Defines.WIRING__NAME);
			if(name == null || name.length() == 0)
				throw new SyntaxException("wiring specification: missing wiring name (in peer type " + peerTypeName + ")", m);
			// -------------------------------------------------------
			// set values
			wiring.setRawWiringNames(name);
			// - convention: set wiring type of inline wiring to "_"<wiring name>; TBD: hard coded... 
			wiring.setWiringTypeName("_" + name);
			wiring.setServiceName(subArtifactValues.get(Defines.WIRING__SERVICE));
			wiring.setRawWiringWPropsDefs(subArtifactValues.get(Defines.WIRING__WPROPS_DEFS));
			// .......................................................
			// add wiring to peer
			peerType.addWiring(wiring); 
		}

		// ==================================================================
		// PASS 4: parse guards and actions
		// ==================================================================
		// create new object for translation; pass it my artifacts; share my wirings;
		// - nb: translate enriches the shared wiring types by all guards and actions;
		// -- found for any wiring types in the artifacts;
		// -- caution: wirings must be set to null;
		(new LinkArtifacts(artifacts)).translate(peerType.getWirings(), null /* wiringTypes */);

		// ==================================================================
		// done
		// ==================================================================
		return peerType;  
	}

	//================================================================================
	// debug
	//================================================================================
	//--------------------------------------------------------------------------------
	// toString
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("--------\n");
		buf.append("Peer Type " + peerTypeName + ":\n"); 
		for(int i = 0; i < artifacts.size(); i++) {
			buf.append(artifacts.get(i).toString());
		}
		return new String(buf);		
	}
	

} // END OF CLASS


//================================================================================
//EOF
//================================================================================

