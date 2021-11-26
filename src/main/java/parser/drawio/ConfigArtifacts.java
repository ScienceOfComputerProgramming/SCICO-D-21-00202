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
//SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
//AUTHOR:    Eva Maria Kuehn
//CREATED:   December 2020 
//================================================================================
// CODE REVIEWS: 20201229 (eK); 20210118 (eK);
//================================================================================

package parser.drawio;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.NodeList;

import pmmm.*;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// translate config related artifacts to one config
// - namely pmmm props and peers
// - nb: config name is part of the diagram name
public class ConfigArtifacts extends Artifacts {
	//--------------------------------------------------------------------------------
	// for debug
	/**/ Tracer tracer = new Tracer();
	// -------------------------------------------------------
	// config name
	String configName = "";

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public ConfigArtifacts() {
	}
	//--------------------------------------------------------------------------------
	// pick out all artifacts from the given node list that are marked with <PMARTIFACT>
	// - nb node list represents the elements of one drawio diagram 
	public ConfigArtifacts(String configName, NodeList mxCellNodeList) {
		// build up the artifacts collection 
		super(mxCellNodeList);
		// set config name
		this.configName = configName;
	}

	//================================================================================
	// TRANSLATE
	//================================================================================
	//--------------------------------------------------------------------------------
	// recognize config related artifacts and translate them to Config;
	public Config translate() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assemble all config parts here
		Config config = new Config(configName);
		//--------------------------------------------------------------------------------
		// local vars
		Vector<String> subArtifactNames;
		Vector<HashMap<String,String>> artifactsSubArtifactValues;
		HashMap<String,String> subArtifactValues;

		// ==================================================================
		// PASS 1: PMMM_SPECIFICATION 
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE: unique sub artifacts of peer type
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.PMMM__PROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values 
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.PMMM__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PLAUSI CHECK:
		// - there must be at most one pmmm specification in the diagram
		if(artifactsSubArtifactValues.size() > 1)
			throw new SyntaxException("more than one pmmm definition ", m);
		// -------------------------------------------------------
		// PROCESS:
		if(artifactsSubArtifactValues.size() == 1) {
			// -------------------------------------------------------
			// get the one pmmm's sub artifact values
			subArtifactValues = artifactsSubArtifactValues.get(0); 
			// -------------------------------------------------------
			// PPROPS DEFS: set them 
			config.setRawPmmmPropsDefsTokenExpression(subArtifactValues.get(Defines.PMMM__PROPS_DEFS));
		}

		// ==================================================================
		// PASS 2: PEER SPECIFICATION(s)
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE: unique sub artifacts of peer type
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.PEER__NAME);
		subArtifactNames.add(Defines.PEER__TYPE_NAME);
		subArtifactNames.add(Defines.PEER__PROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values 
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.PEER__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PROCESS: nb: there can be zero or more peer specifications in the diagram
		for(int i = 0; i < artifactsSubArtifactValues.size(); i++) {
			//--------------------------------------------------------------------------------
			// create new peer
			PeerConfig peerConfig = new PeerConfig();
			// -------------------------------------------------------
			// get the one peers's sub artifact values
			subArtifactValues = artifactsSubArtifactValues.get(i); 
			// -------------------------------------------------------
			peerConfig.setRawPeerNames(subArtifactValues.get(Defines.PEER__NAME));
			peerConfig.setPeerTypeName(subArtifactValues.get(Defines.PEER__TYPE_NAME));
			peerConfig.setRawPeerPPropsDefs(subArtifactValues.get(Defines.PEER__PROPS_DEFS));
			// -------------------------------------------------------
			/**/ tracer.println("peer name = '" + peerConfig.getRawPeerNames() + "'; " + "peer type name = '" + peerConfig.getPeerTypeName() + "'", Level.NO, m);
			// -------------------------------------------------------
			// add peer to config
			config.addPeerConfig(peerConfig);
		}		

		// ==================================================================
		// DONE
		// ==================================================================
		/**/ tracer.println("config = \n" + config, Level.NO, m);
		return config;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("--------\n");
		buf.append("Config:\n");
		for(int i = 0; i < artifacts.size(); i++) {
			buf.append(artifacts.get(i).toString());
		}
		return new String(buf);		
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

