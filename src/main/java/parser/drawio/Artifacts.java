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
// CODE REVIEWS: 20201229 (eK);
//================================================================================
package parser.drawio;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.Tracer;

//================================================================================
// vector of artifacts
public class Artifacts {
	//--------------------------------------------------------------------------------
	// debug
	/**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	// artifacts collection
	Vector<Artifact> artifacts = new Vector<Artifact>();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Artifacts() {
	}
	//--------------------------------------------------------------------------------
	// search and return all Peer Model related artifacts contained in the given node list;
	// - nb: they are marked with <PMARTIFACT>=<artifactName> (cf. drawio shapes) as part of their "style" attribute;
	// - nb: they typically will belong to one diagram;
	public Artifacts(NodeList mxCellNodeList) {
		// this fu is 1) support fu for constructor so that tracer can be used and 2) needed also from outside
		parseAndAddArtifacts(mxCellNodeList);
	}

	//================================================================================
	// CONSTRUCTOR HELP FU
	//================================================================================
	//--------------------------------------------------------------------------------
	public void parseAndAddArtifacts(NodeList mxCellNodeList) {
		//--------------------------------------------------------------------------------
		// debug
		/**/ Object m = new Object(){};  
		/**/ tracer.println("mxCellNodeList" + mxCellNodeList, Level.NO, m);
		//--------------------------------------------------------------------------------
		// if node list is null do nothing
		if(mxCellNodeList == null)
			return;
		//--------------------------------------------------------------------------------
		// get number of "nMxCell" nodes
		int nMxCell = mxCellNodeList.getLength();
		/**/ tracer.println("number of MxCells = " + nMxCell, Level.NO, m);
		//--------------------------------------------------------------------------------
		// process all mxCell nodes of this diagram 
		// - and add the found PM related artifacts (marked with <PMARTIFACT>) to my artifacts collection
		//--------------------------------------------------------------------------------
		for (int j = 0; j < nMxCell; j++) {
			//--------------------------------------------------------------------------------
			// convert next mxCell node to Element
			Element mxCellElement = (Element) mxCellNodeList.item(j);
			//--------------------------------------------------------------------------------
			// get value of "style" attribute from the "mxCell" Element
			String style = mxCellElement.getAttribute("style");
			//--------------------------------------------------------------------------------
			// check for existence of the style marker
			int k1 = style.indexOf(Defines.PMARTIFACT);
			// does the substring <PMARTIFACT> occur in style?
			if (k1 >= 0) {
				/**/ tracer.println("style = " + style, Level.NO, m);
				//--------------------------------------------------------------------------------
				// nb: the syntax is: ' <PMARTIFACT> = <ArtifactName> ; '
				// get first occurrence of "=" and of ";" after <PMARTIFACT>, i.e. after position k1
				int k2 = style.indexOf("=", k1);
				int k3 = style.indexOf(";", k1);
				// artifact name is the string between '=' and ';' (exclusive)
				String artifactName = style.substring(k2 + 1, k3);
				// remove leading and trailing blanks and here we go
				artifactName.trim();
				/**/ tracer.println("artifact name = " + artifactName, Level.NO, m);
				//--------------------------------------------------------------------------------
				// construct new artifact with the required fields
				Artifact artifact = new Artifact(mxCellElement, artifactName);
				/**/ tracer.println("xml artifact = " + artifact, Level.NO, m);
				//--------------------------------------------------------------------------------
				// add artifact to my artifacts
				artifacts.add(artifact);
			}
		}
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// search for given artifact; for each one:
	// - search for its sub artifacts (their names are given in the vector)  
	// -- and return their raw values as strings in a vector in the same order as required;
	// - caution: there must be at *most one* sub artifact with the given name
	// -- otherwise exception is raised
	// - nb: if the sub artifact does not occur, put "" in the return vector on its place !!! VERY IMPORTANT 
	// -- because the raw value must be string and not null !!!
	// TBD: the mechanism is a bit inefficient... but easy to maintain...
	protected Vector<HashMap<String,String>> getArtifactsUniqueSubArtifactValues (String artifactName, Vector<String> subArtifactNames) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// debug
		/**/ Tracer tracer = new Tracer(); 
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// local vars
		String nextSubArtifactValue;
		String nextSubArtifactName;
		//--------------------------------------------------------------------------------
		// create return vector
		Vector<HashMap<String,String>> artifactsSubArtifactValues = new Vector<HashMap<String,String>>();
		// -------------------------------------------------------
		// iterate over all artifacts and search for artifactName
		for(int i = 0; i < artifacts.size(); i++) {
			Artifact a1 = artifacts.get(i);
			/**/ tracer.println("a1 = " + a1.artifactName + "; desired artifactName = " + artifactName, Level.LO, m);
			// -------------------------------------------------------
			// desired artifactName?
			if(a1.artifactName.equals(artifactName)) {
				//--------------------------------------------------------------------------------
				// next one found: 
				// - create a hash map for the sub artifacts values for this occurrence of artifact
				HashMap<String,String> subArtifactValues = new HashMap<String,String>();
				// -------------------------------------------------------
				// iterate over given sub artifacts name vector
				// - nb: must be done before the following iteration over all artifacts 
				// -- so that result vector's order correlates with the input vector
				for(int j = 0; j < subArtifactNames.size(); j++) {
					nextSubArtifactName = subArtifactNames.get(j);
					nextSubArtifactValue = "";
					// -------------------------------------------------------
					// iterate over all artifacts and search for current sub artifact name
					// - i.e. where parent id == id of a1
					for(int k = 0; k < artifacts.size(); k++) {
						Artifact a2 = artifacts.get(k);
						/**/ tracer.println("a2 = " + a2.artifactName, Level.NO, m);
						// -------------------------------------------------------
						// is a2 a sub artifact of a1 AND with the desired name?
						if(a2.parentId.equals(a1.id) && a2.artifactName.equals(nextSubArtifactName)) {
							/**/ tracer.println("found: a2 = " + a2.artifactName, Level.LO, m);
							// -------------------------------------------------------
							// found -> set key + value
							nextSubArtifactValue = a2.value;
							// -------------------------------------------------------
							// ok done; nb: there is max. 1 sub artifact with this name allowed
							break;
						}
						// -------------------------------------------------------
					} // for loop over all artifacts
					// -------------------------------------------------------
					// add result for this sub artifact name to the return vector; possibly ""
					// - first: plausi check that key does not yet exist
					if(subArtifactValues.get(nextSubArtifactName) != null)
						throw new SyntaxException("duplicate sub artifact " + nextSubArtifactName + " found for " + artifactName, m);
					// - now: put
					subArtifactValues.put(nextSubArtifactName, nextSubArtifactValue);
					// -------------------------------------------------------
				} // for loop over all sub artifact names
				// -------------------------------------------------------
				// add subArtifactValues for this occurrence of artifact to retVector
				artifactsSubArtifactValues.add(subArtifactValues);
				// -------------------------------------------------------
			} // if desired artifactName
		}
		return artifactsSubArtifactValues;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < this.artifacts.size(); i++) {
			buf.append(artifacts.get(i).toString());
		}
		
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
