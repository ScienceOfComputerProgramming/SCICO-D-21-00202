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

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import parser.*;
import pmmm.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// translate drawio to a "raw" PMMM, i.e. with still raw and unevaluated expressions;
// steps of the xml parser:
// 1.) xml file (exported from drawio) 2 Document
//     - use existing package DocumentBuilder for that
// 2.) Document 2 Artifacts
//     - each component of the PM has an own artifacts class that inherits from Artifacts
//     - for each diagram call the respective component's artifacts class 
//     -- that collects all artifacts of this very diagram marked with <PMARTIFACT> 
//     - see docu of Peer Model shapes where you also find all used names
// 3.) Artifacts 2 "raw" PMMM
//     - call the translate method of the respective component's artifacts class
//     -- which picks out from all artifacts of this very diagram those artifacts it is interested in 
//     -- and assembles them to the respective PMMM component (e.g.: EntryTypes, PeerTypes, ...)
// usage: pmmm.PMMM pmmm = (new drawio.XmlParser(<path>, <fileName>, <fileExtension>).parse());
public class DrawioParser extends Parser implements IParser {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	//================================================================================
	// ALL PMMM ARTIFACTS
	//--------------------------------------------------------------------------------
	// - PMMM TYPE
	PmmmType_NameAndPropsArtifacts pmmmType_NameAndPropsArtifacts = new PmmmType_NameAndPropsArtifacts();
	//--------------------------------------------------------------------------------
	// - PEER TYPES
	Vector<PeerTypeArtifacts> peerTypesArtifacts = new Vector<PeerTypeArtifacts>();
	//--------------------------------------------------------------------------------
	// - WIRING TYPES
	WiringTypesArtifacts wiringTypesArtifacts = new WiringTypesArtifacts();
	//--------------------------------------------------------------------------------
	// - ENTRY TYPES
	EntryTypesArtifacts entryTypesArtifacts = new EntryTypesArtifacts();
	//--------------------------------------------------------------------------------
	// - CONFIGS: PMMM and PEERS
	Vector<ConfigArtifacts> configsArtifacts = new Vector<ConfigArtifacts>();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public DrawioParser(String absoluteUcPath, String ucName, String fileExtension /* e.g: ".xml" */) {
		super(absoluteUcPath, ucName, fileExtension);
	}

	//================================================================================
	// PARSE
	//================================================================================
	//--------------------------------------------------------------------------------
	// generate PMMM from file
	public PmmmComponents parse() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// debug:
		// /**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// PASS 1: pick up all artifacts relevant for the PMMM from file
		// - store them in my class
		drawio_2_artifacts();
		//--------------------------------------------------------------------------------
		// PASS 2: translate the artifacts into a "raw" PMMM	
		return artifacts_2_PmmmType();
	}

	//================================================================================
	// PASS 1: DRAWIO XML 2 ARTIFACTS
	//================================================================================
	// translate draw.io xml into a collection of relevant artifacts;
	// collect all artifacts (--> see Peer Model shapes for draw.io) for a Peer Model Meta Model;
	// the PM artifacts are marked by the string <PMARTIFACT>=<artifactName> contained in their style attribute;
	public void drawio_2_artifacts() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("Draw.IO XML Parser start", Level.NO, m);
		/**/ tracer.println("sourcePath = " + sourcePath, Level.NO, m);
		/**/ tracer.println("fileName = " + ucName, Level.NO, m);
		/**/ tracer.println("extension = " + extension, Level.NO, m);
		//--------------------------------------------------------------------------------
		String fullFileName = sourcePath + ucName + extension;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("fullFileName = " + fullFileName, Level.NO, m);
		//--------------------------------------------------------------------------------
		// open file and convert it into DOM model document
		Document doc;
		try {
			doc = xml2Document(fullFileName);
		} catch (Exception e1) {
			throw new SyntaxException("can't open file and convert it into DOM model document: " + e1.getMessage(), m);
		}
		//--------------------------------------------------------------------------------
		// get root element -- should be "mxfile"
		// - TBD: verify name of the element
		Element mxfileElement = doc.getDocumentElement();
		/**/ tracer.println("mxfileElementName = " + mxfileElement.getNodeName(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// get all "diagram" elements from the "mxfile" element as node list
		NodeList diagramNodeList = mxfileElement.getElementsByTagName("diagram");
		//--------------------------------------------------------------------------------
		// get number of diagrams in the xml file
		int nDiagrams = diagramNodeList.getLength();
		/**/ tracer.println("nDiagrams = " + nDiagrams, Level.LO, m);
		//--------------------------------------------------------------------------------
		// iterate over all diagrams
		for (int i = 0; i < nDiagrams; i++) {
			//--------------------------------------------------------------------------------
			// NEXT DIAGRAM:
			//--------------------------------------------------------------------------------
			// get its node
			// - TBD: assert that node name is "diagram" ...
			Node diagramNode = diagramNodeList.item(i);
			/**/ tracer.println(i + ". " + diagramNode.getNodeName(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// node type must be "ELEMENT_NODE" anyhow...
			if (diagramNode.getNodeType() == Node.ELEMENT_NODE) {
				//--------------------------------------------------------------------------------
				// convert diagram node to element
				Element diagramElement = (Element) diagramNode;
				//--------------------------------------------------------------------------------
				// get diagram name
				String diagramName = diagramElement.getAttribute("name");
				/**/ tracer.println("Diagram = " + diagramName, Level.NO, m);	
				//--------------------------------------------------------------------------------
				// is there a root element?
				Element rootElement = null;
				try {
					//--------------------------------------------------------------------------------
					// get the diagram's "mxGraphModel" element
					Element mxGraphModelElement = getFirstSubElementWithName("mxGraphModel", diagramElement);
					//--------------------------------------------------------------------------------
					// get the mxGraphModel's "root" element
					rootElement = getFirstSubElementWithName("root", mxGraphModelElement);
				} catch (Exception e) {
					//--------------------------------------------------------------------------------
					// diagram could be empty; it will be checked later if this is a problem;
					// just skip for now
					// TBD: better give a warning.... maybe compressed xml
					continue; // for loop
				}
				//--------------------------------------------------------------------------------
				// get nodes of all "mxCell" elements from the "root" element ... there may be many
				NodeList mxCellNodeList = rootElement.getElementsByTagName("mxCell");
				//--------------------------------------------------------------------------------
				// remove leading and trailing blanks from the diagram name
				diagramName = diagramName.trim();
				/**/ tracer.println((i+1) + ".DIAGRAM = '" + diagramName + "'", Level.NO, m);
				//--------------------------------------------------------------------------------
				// treat diagram depending on its name:

				//================================================================================
				// PMMM TYPE 
				//================================================================================
				//--------------------------------------------------------------------------------
				// nb: there can be at most one pmmm type diagram
				// - this is implicitly asserted as there cannot be two diagrams with same name
				// TBD: not true; drawio allows diagrams with same name ... is a general issue
				//--------------------------------------------------------------------------------
				if(diagramName.startsWith(Defines.DIAGRAM__PMMM_TYPE)) {
					/**/ tracer.println("fileName = " + ucName, Level.NO, m);
					//--------------------------------------------------------------------------------
					// parse and set
					pmmmType_NameAndPropsArtifacts = new PmmmType_NameAndPropsArtifacts(ucName /* convention: pmmm name == xml file name */, mxCellNodeList);
				}
				//================================================================================
				// ENTRY TYPES 
				//================================================================================
				//--------------------------------------------------------------------------------
				// nb: there can be many entry types diagrams
				// each must start with "<DIAGRAM__ENTRY_TYPES> ... "
				// collect all artifacts of all diagrams in entryTypesArtifacts
				if(diagramName.startsWith(Defines.DIAGRAM__ENTRY_TYPES)) {
					//--------------------------------------------------------------------------------
					// parse and set
					entryTypesArtifacts.parseAndAddArtifacts(mxCellNodeList);
				}
				//================================================================================
				// PEER TYPE 
				//================================================================================
				//--------------------------------------------------------------------------------
				// diagramName == "<DIAGRAM__PEER_TYPE_PREFIX> : <peerTypeName>"
				// - nb: there can be several peer type diagrams
				else if(diagramName.startsWith(Defines.DIAGRAM__PEER_TYPE_PREFIX)) {
					//--------------------------------------------------------------------------------
					// get name
					String peerTypeName = extractName(diagramName, Defines.DIAGRAM__PEER_TYPE_PREFIX);
					//--------------------------------------------------------------------------------
					// parse 
					PeerTypeArtifacts peerTypeArtifacts = new PeerTypeArtifacts(peerTypeName, mxCellNodeList);
					//--------------------------------------------------------------------------------
					// add  
					peerTypesArtifacts.add(peerTypeArtifacts);
				}
				//================================================================================
				// WIRING TYPES 
				//================================================================================
				//--------------------------------------------------------------------------------
				// nb: there can be many wiring types diagrams
				// each must start with "<DIAGRAM__WIRING_TYPES> ... "
				// collect all artifacts of all diagrams in entryTypesArtifacts
				if(diagramName.startsWith(Defines.DIAGRAM__WIRING_TYPES)) {
					/**/ tracer.println("WIRING TYPE DIAGRAM = " + diagramName, Level.NO, m);
					//--------------------------------------------------------------------------------
					// parse and set
					wiringTypesArtifacts.parseAndAddArtifacts(mxCellNodeList);
				}
				//================================================================================
				// CONFIG
				//================================================================================
				//--------------------------------------------------------------------------------
				// diagramName == "<DIAGRAM__CONFIG_PREFIX> : <configName>"
				// - nb: there can be several config diagrams
				else if(diagramName.startsWith(Defines.DIAGRAM__CONFIG_PREFIX)) {
					//--------------------------------------------------------------------------------
					// get name
					String configName = extractName(diagramName, Defines.DIAGRAM__CONFIG_PREFIX);
					//--------------------------------------------------------------------------------
					// parse 
					ConfigArtifacts configArtifacts = new ConfigArtifacts(configName, mxCellNodeList);
					//--------------------------------------------------------------------------------
					// add 
					configsArtifacts.add(configArtifacts);
				}
				//================================================================================
				// ALL OTHER DIAGRAMS
				//================================================================================
				//--------------------------------------------------------------------------------
				// - just skip
			}
		}
	}

	//================================================================================
	// PASS 2: ARTIFACTS 2 "RAW" PMMM Type (with still raw expressions in the tokens)
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmmmComponents artifacts_2_PmmmType() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		//================================================================================
		// PMMM TYPE: 
		//================================================================================
		/**/ tracer.println("\n" + ui.Out.banner("PMMM TYPE:", '>'), Level.NO, m);
		//--------------------------------------------------------------------------------
		// create new PMMM type and assemble everything there
		// sets name, props types and props defs; rest is done below
		// just pass on exceptions
		PmmmComponents pmmmComponents = pmmmType_NameAndPropsArtifacts.translate();
		//--------------------------------------------------------------------------------
		// if there was no diagram for the pmmm type -> just set the pmmm name to the file name
		// - TBD: warning
		if(util.Util.isEmptyString(pmmmComponents.pmmmTypeName)) {
			pmmmComponents.pmmmTypeName = ucName;
		}
		//--------------------------------------------------------------------------------
		//================================================================================
		// ENTRY TYPES:
		//================================================================================
		/**/ tracer.println("\n" + ui.Out.banner("ENTRY TYPES:", '>'), Level.NO, m);
		//--------------------------------------------------------------------------------
		// just pass on exceptions
		pmmmComponents.entryTypes = entryTypesArtifacts.translate();
		//--------------------------------------------------------------------------------
		//================================================================================
		// WIRING TYPES:
		//================================================================================
		//--------------------------------------------------------------------------------
		/**/ tracer.println("\n" + ui.Out.banner("WIRING TYPES:", '>'), Level.NO, m);
		// just pass on exceptions
		pmmmComponents.wiringTypes = wiringTypesArtifacts.translate();
		//--------------------------------------------------------------------------------
		//================================================================================
		// PEER TYPES:
		//================================================================================
		/**/ tracer.println("\n" + ui.Out.banner("PEER TYPES:", '>'), Level.NO, m);
		//--------------------------------------------------------------------------------
		Vector<PeerType> peerTypes = new Vector<PeerType>();
		// iterate over all peer artifacts
		for(int i = 0; i < peerTypesArtifacts.size(); i++) {
			/**/ tracer.println("next peer artifact", Level.NO, m);
			// just pass on exceptions
			PeerType peer = peerTypesArtifacts.get(i).translate();
			/**/ tracer.println("peer = " + peer, Level.NO, m);
			peerTypes.add(peer);
		}
		pmmmComponents.peerTypes = peerTypes;
		//--------------------------------------------------------------------------------
		//================================================================================
		// CONFIGS:
		//================================================================================
		/**/ tracer.println("\n" + ui.Out.banner("CONFIGS:", '>'), Level.NO, m);
		//--------------------------------------------------------------------------------
		Vector<Config> configs = new Vector<Config>();
		// iterate over all config artifactss
		for(int i = 0; i < configsArtifacts.size(); i++) {
			/**/ tracer.println("next config artifact = configsArtifacts.get(i)", Level.NO, m);
			// just pass on exceptions
			Config config = configsArtifacts.get(i).translate();
			/**/ tracer.println("config = " + config, Level.NO, m);
			configs.add(config);
		}
		pmmmComponents.configs = configs;
		//--------------------------------------------------------------------------------
		//================================================================================
		// ok done
		//================================================================================
		return pmmmComponents;
	}

	//================================================================================
	// UTIL
	//================================================================================
	//--------------------------------------------------------------------------------
	// diagram name has the form " <keyword> : <name> "
	// - extract the name without leading/trailing blanks
	private String extractName(String diagramName, String keyword) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// skip leading/trailing blanks
		String name = diagramName.trim();
		//--------------------------------------------------------------------------------
		// skip <keyword>
		name = name.substring(keyword.length());
		//--------------------------------------------------------------------------------
		// skip leading/trailing blanks
		name = name.trim();
		//--------------------------------------------------------------------------------
		// check for and skip ":"
		if(! name.startsWith(":"))
			throw new SyntaxException("ill. diagram name: " + diagramName, m);
		name = name.substring(":".length()); 
		//--------------------------------------------------------------------------------
		// remove leading blanks and we have the desired name
		name = name.trim();
		//--------------------------------------------------------------------------------
		return name;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString
	public String toString() {
		//--------------------------------------------------------------------------------
		StringBuffer buf = new StringBuffer();	
		//--------------------------------------------------------------------------------
		// ENTRIES
		buf.append(entryTypesArtifacts.toString());
		//--------------------------------------------------------------------------------
		// PEERS
		for(int i = 0; i < peerTypesArtifacts.size(); i++) {
			buf.append(peerTypesArtifacts.get(i).toString());
		}
		//--------------------------------------------------------------------------------
		return new String(buf);		
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

