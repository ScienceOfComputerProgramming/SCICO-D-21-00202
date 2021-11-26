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

import org.w3c.dom.NodeList;

import parser.PmmmComponents;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.Tracer;

//================================================================================
// translate artifacts to one pmmm type
public class PmmmType_NameAndPropsArtifacts extends Artifacts {
	// -------------------------------------------------------
	// for debug:
	private Tracer tracer = new Tracer();
	// -------------------------------------------------------
	// pmmm type name will only be verified
	// - has namely already been set to the file name
	String pmmmTypeName = "";
	//--------------------------------------------------------------------------------
	// local vars
	Vector<String> subArtifactNames;
	Vector<HashMap<String,String>> artifactsSubArtifactValues;
	HashMap<String,String> subArtifactValues;
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmmmType_NameAndPropsArtifacts() {
	}
	// -------------------------------------------------------
	// pick out all artifacts from the given node list that are marked with <PMARTIFACT>
	// - nb node list represents the elements of one drawio diagram 
	public PmmmType_NameAndPropsArtifacts(String pmmmTypeName, NodeList mxCellNodeList) {
		// build up the artifacts collection
		super(mxCellNodeList);
		// set pmmm name
		this.pmmmTypeName = pmmmTypeName;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getPmmmName() {
		return pmmmTypeName;
	}

	//================================================================================
	// TRANSLATE
	//================================================================================
	//--------------------------------------------------------------------------------
	// recognize pmmm type related artifacts and translate them;
	// - therese are pmmm props types and pmmm props defs
	// - nb: all other artifacts are assembled seperately by the caller
	// - nb: convention: file name must be the same name like the modeled pmmm type name, which is set already; 
	// -- in addition, this is verified below;
	public PmmmComponents translate() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// create new PmmmType
		PmmmComponents pmmmComponents = new PmmmComponents();
		// set its name
		pmmmComponents.pmmmTypeName = pmmmTypeName;
		
		// ==================================================================
		// PASS 1: PMMM_TYPE__SPECIFICATION
		// ==================================================================
		// -------------------------------------------------------
		// REQUIRE: unique sub artifacts of peer type
		subArtifactNames = new Vector<String>();
		subArtifactNames.add(Defines.PMMM_TYPE__NAME);
		subArtifactNames.add(Defines.PMMM_TYPE__PMMM_PROPS_TYPES);
		subArtifactNames.add(Defines.PMMM_TYPE__PMMM_PROPS_DEFS);
		// -------------------------------------------------------
		// RETRIEVE: their values 
		artifactsSubArtifactValues = getArtifactsUniqueSubArtifactValues(Defines.PMMM_TYPE__SPECIFICATION /* artifactName */, subArtifactNames);
		// -------------------------------------------------------
		// PLAUSI CHECK:
		// - nb: there must be at most one pmmm type specifications in the diagram
		/**/ tracer.println("artifactsSubArtifactValues.size()" + "=" + artifactsSubArtifactValues.size(), Level.NO, m);
		if(artifactsSubArtifactValues.size() > 1)
			throw new SyntaxException("more than one pmmm type declaration found in the diagram", m);
		// -------------------------------------------------------
		// PROCESS:
		// - nb: in this diagram is at most one pmmm type specification
		if(artifactsSubArtifactValues.size() == 1) {
			// -------------------------------------------------------
			// get the one pmmm type's sub artifact values
			subArtifactValues = artifactsSubArtifactValues.get(0); 
			// -------------------------------------------------------
			// PMMM NAME: 
			// - just compare the names...
			if(! subArtifactValues.get(Defines.PMMM_TYPE__NAME).equals(pmmmTypeName)) {
				throw new SyntaxException("file name '" + pmmmTypeName + "' must equal specified PMMM name '" + 
						subArtifactValues.get(Defines.PMMM_TYPE__NAME) + " 'in PmmmType diagram", m);
			}
			// -------------------------------------------------------
			// PMMM PROPS TYPES: 
			pmmmComponents.pmmmTypePmmmPropsTypes.setRaw(subArtifactValues.get(Defines.PMMM_TYPE__PMMM_PROPS_TYPES));
			// -------------------------------------------------------
			// PMMM PROPS DEFS: 
			pmmmComponents.pmmmTypePmmmPropsDefsTokenExpression.setRaw(subArtifactValues.get(Defines.PMMM_TYPE__PMMM_PROPS_DEFS));
			// -------------------------------------------------------
			/**/ tracer.println("PMMM_TYPE__NAME = " + pmmmTypeName, Level.NO, m);
			/**/ tracer.println("PMMM_TYPE__PMMM_PROPS_TYPES = " + pmmmComponents.pmmmTypePmmmPropsTypes.getRaw(), Level.NO, m);
			/**/ tracer.println("PMMM_TYPE__PMMM_PROPS_DEFS  = " + pmmmComponents.pmmmTypePmmmPropsDefsTokenExpression.getRaw(), Level.NO, m);
		}
		//================================================================================
		// DONE
		//================================================================================
		return pmmmComponents;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// toString
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("--------\n");
		buf.append("PMMM:\n");
		for(int i = 0; i < artifacts.size(); i++) {
			buf.append(artifacts.get(i).toString());
		}
		return new String(buf);		
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

