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
// CODE REVIEWS: 20201229 (eK);
//================================================================================
package parser.drawio;

import org.w3c.dom.Element;

import qa.tracer.Level;
import qa.tracer.Tracer;
import util.replace.xml.XmlReplacements;

//================================================================================
// artifact extracts selected attributes of an XML Element (from the XML file) that is relevant for the model;
// - nb: these selected attributes are needed for the translation into the PMMM;
// - nb: relevant elements are marked by "<PMARTIFACT>=<artifactName>" contained in their style attribute;
// - nb: the recognizion of the marker must be done by the caller
// - nb: this marking is done by the Peer Model drawio XML shape style sheets; 
public class Artifact {
	//--------------------------------------------------------------------------------
	// debug
	/**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	// the "value" of the <PMARTIFACT> marker (see style attribute in Peer Model drawio shapes)
	String artifactName = "";
	//--------------------------------------------------------------------------------
	// these are the attributes of interest, that are picked up from the xml elements
	// - not needed fields simply remain empty
	// my element id
	String id = "";
	//--------------------------------------------------------------------------------
	// id of my parent xml element
	String parentId = "";
	//--------------------------------------------------------------------------------
	// for connector (line/arrows) connecting a source and a target element
	// - source id
	String sourceId = "";
	// - target id
	String targetId = "";
	//--------------------------------------------------------------------------------
	// value of my element
	String value = "";
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	// extract certain attributes of a mxCellElement element and return them as a new xml artifact;
	// artifactName is the already retrieved name of the Peer Model xml artifact;
	// some attributes might be unused -- but just retrieve all of them and leave the unused ones empty;
	// nb: if an attribute does not exist, the getAttribute method simply returns an empty string;
	public Artifact(Element mxCellElement, String artifactName) {
		// help fu needed because tracer mechanism does not work in constructor
		constructXmlArtifact(mxCellElement, artifactName);
	}

	//--------------------------------------------------------------------------------
	// this help fu is a trick so that tracer can be used - which is not possible in a constructor...
	private void constructXmlArtifact(Element mxCellElement, String artifactName) {
		// for debug:
		/**/ Object m = new Object(){};
		//================================================================================
		// artifactName
		this.artifactName = artifactName;
		//================================================================================
		// "id" 
		id = mxCellElement.getAttribute("id");
		//================================================================================
		// "parent"  
		parentId = mxCellElement.getAttribute("parent");
		//================================================================================
		// "source" 
		sourceId = mxCellElement.getAttribute("source"); 
		//================================================================================
		// "target" 
		targetId = mxCellElement.getAttribute("target");
		//================================================================================
		// "value" 
		value = mxCellElement.getAttribute("value");			
		//--------------------------------------------------------------------------------
		// - remove all html tags from value
		// -- a bit tricky :-)
		// -- CAUTION replace by " " otherwise names might peck into each other
		value = value.replaceAll("\\<.*?\\>", " ");
		//--------------------------------------------------------------------------------
		// - replace special html characters in xml value
		// -- TBD: check if these are all needed ones ....
		value = (new util.replace.Director(new XmlReplacements())).x2String(value);		
		//--------------------------------------------------------------------------------
		// strip leading and trailing white space of value
		value = value.trim();
		//================================================================================
		/**/ tracer.println(artifactName, Level.NO, m);
	}

	//================================================================================
	// is the string s defined?
	// - i.e. not null and not "" (empty)
	private boolean defined(String s) {
		if(s != null && s.length() > 0) 
			return true;
		return false;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString;
	// - nb: prints only defined attributes;
	// - nb: name + id are obligatory;
	public String toString() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		// if(! artifactName.contains(BESCHRIFTUNG)) {
		buf.append("ARTIFACT NAME: " + artifactName + "\n");
		buf.append(" - id = " + id + "\n");
		if(defined(parentId)) 
			buf.append(" - parent = " + parentId + "\n");
		if(defined(sourceId)) 
			buf.append(" - source = " + sourceId + "\n");
		if(defined(id)) 
			buf.append(" - target = " + targetId + "\n");
		if(defined(value)) 
			buf.append(" - value = " + value + "\n");
		// }
		//--------------------------------------------------------------------------------
		return new String(buf);
	}

} // END OF CLASS


//================================================================================
// EOF
//================================================================================

