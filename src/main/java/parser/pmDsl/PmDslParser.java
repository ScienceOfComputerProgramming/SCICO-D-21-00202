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

package parser.pmDsl;

import parser.*;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import codeGen.PmDsl.*;
import pmmm.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;
import util.replace.xml.XmlReplacements;

//================================================================================
// translate PM DSL to a "raw" PMMM, i.e. with still raw and unevaluated expressions;
// steps of the xml parser:
// 1.) xml file 2 Document
//     - use existing package DocumentBuilder for that
// 2.) Document 2 "raw" PMMM
//     -- assembles everything to the respective PMMM component (e.g.: EntryTypes, PeerTypes, ...)
public class PmDslParser extends Parser implements IParser {
	//--------------------------------------------------------------------------------
	// debug
	/**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmDslParser(String path, String fileName, String fileExtension /* e.g: ".xml" */) {
		super(path, fileName, fileExtension);
	}

	//================================================================================
	// PARSE
	//================================================================================
	//--------------------------------------------------------------------------------
	// generate PMMM from file
	public PmmmComponents parse() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local vars
		String errMsg1 = "";
		String errMsg2 = "";
		String errMsg3 = "";
		//--------------------------------------------------------------------------------
		String fullFileName = sourcePath + ucName + extension;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("PM DSL XML Parser start", Level.HI, m);
		//--------------------------------------------------------------------------------
		// open file and convert it into DOM model document
		Document doc;
		try {
			doc = xml2Document(fullFileName);
		} catch (Exception e1) {
			throw new SyntaxException("can't open file and convert it into DOM model document: " + e1.getMessage(), m);
		}
		//--------------------------------------------------------------------------------
		// get root element -- should be "PMMM"
		Element pmmmElement = doc.getDocumentElement();
		/**/ tracer.println("pmmmElement = " + pmmmElement.getNodeName(), Level.HI, m);
		// - verify name of the element
		if(! pmmmElement.getNodeName().equals(PmDslDefs.PMMM__ELEMENT_NAME)) 
			throw new SyntaxException("main element tag mut be " + PmDslDefs.PMMM__ELEMENT_NAME, m);
		// - get its "name" attribute
		String pmmmTypeName = pmmmElement.getAttribute(PmDslDefs.NAME__ATTRIBUTE_NAME);
		if(pmmmTypeName == null || pmmmTypeName.length() == 0) 
			throw new SyntaxException("PMMM name missing", m);
		// - create PMMM
		// -- just pass on the exceptions
		PmmmComponents pmmmComponents = new PmmmComponents();
		// - set pmmm type name
		pmmmComponents.pmmmTypeName = pmmmTypeName;
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			//================================================================================
			// PMMM PROPS DEFS
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg1 = "pmmm props defs";
			//--------------------------------------------------------------------------------
			// TBD: da stimmt was nicht...
			String rawPmmmTypeProps = getRawTextOfChildElement(pmmmElement, PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME);
			PropsTypes pmmmPropsTypes = new PropsTypes();
			pmmmPropsTypes.setRaw(rawPmmmTypeProps);
			pmmmComponents.pmmmTypePmmmPropsTypes = pmmmPropsTypes;
			//================================================================================
			// PMMM PROPS TYPES
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg1 = "pmmm props types";
			//--------------------------------------------------------------------------------
			// get all "Types" elements from the "pmmm" element as node list
			// - there should be exactly one
			NodeList typesNodeList = pmmmElement.getElementsByTagName(PmDslDefs.TYPES__ELEMENT_NAME);
			// get number of Types
			int nTypes = typesNodeList.getLength();
			/**/ tracer.println("nTypes = " + nTypes, Level.HI, m);
			if(nTypes != 1)
				throw new SyntaxException("no peer types found", m);
			// get the one "Types" element
			Element typesElement = getIthElement(typesNodeList, 0);
			//================================================================================
			// ENTRY TYPES
			//================================================================================
			//--------------------------------------------------------------------------------
			// get all "EntryType" elements from the "Types" element as node list
			NodeList entryTypesNodeList = typesElement.getElementsByTagName(PmDslDefs.ENTRY_TYPE__ELEMENT_NAME);
			// get number of EntryTypes
			int nEntryTypes = entryTypesNodeList.getLength();
			//--------------------------------------------------------------------------------
			// create entry types
			EntryTypes entryTypes = new EntryTypes();
			//--------------------------------------------------------------------------------
			// iterate over all EntryTypes
			for (int i = 0; i < nEntryTypes; i++) {
				//--------------------------------------------------------------------------------
				// NEXT ENTRY TYPE:
				//--------------------------------------------------------------------------------
				// get i-th element
				Element entryTypeElement = getIthElement(entryTypesNodeList, i);
				//--------------------------------------------------------------------------------
				// get "entryType" attribute
				String entryTypeName = entryTypeElement.getAttribute(PmDslDefs.ENTRY_TYPE__ATTRIBUTE_NAME);
				/**/ tracer.println("entryTypeName = " + entryTypeName, Level.HI, m);	
				//--------------------------------------------------------------------------------
				errMsg1 = "; entry type '" + entryTypeName + "'";
				//--------------------------------------------------------------------------------
				// get raw props declarations
				String rawEntryTypeProps = getRawTextOfChildElement(entryTypeElement, PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME);
				//--------------------------------------------------------------------------------
				// create EntryType 
				EntryType entryType = new EntryType();
				//--------------------------------------------------------------------------------
				// assemble
				entryType.setEntryTypeName(entryTypeName);
				entryType.setRawEPropsTypes(rawEntryTypeProps);
				//--------------------------------------------------------------------------------
				// add entry type to EntryTypes
				entryTypes.add(entryType);
			}
			//--------------------------------------------------------------------------------
			// assemble
			pmmmComponents.entryTypes = entryTypes;
			//================================================================================
			// PEER TYPES
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg1 = "peer types";
			//--------------------------------------------------------------------------------
			// get all "PeerType" elements from the "Types" element as node list
			NodeList peerTypesNodeList = typesElement.getElementsByTagName(PmDslDefs.PEER_TYPE__ELEMENT_NAME);
			// get number of PeerTypes
			int nPeerTypes = peerTypesNodeList.getLength();
			//--------------------------------------------------------------------------------
			// create peer types
			Vector<PeerType> peerTypes = new Vector<PeerType>();
			//================================================================================
			// PEER TYPES:
			//================================================================================
			//--------------------------------------------------------------------------------
			for (int i = 0; i < nPeerTypes; i++) {
				//--------------------------------------------------------------------------------
				// get i-th element
				Element peerTypeElement = getIthElement(peerTypesNodeList, i);
				//--------------------------------------------------------------------------------
				// get "name" attribute
				String peerTypeName = peerTypeElement.getAttribute(PmDslDefs.NAME__ATTRIBUTE_NAME);
				/**/ tracer.println("peerTypeName = " + peerTypeName, Level.HI, m);	
				//--------------------------------------------------------------------------------
				errMsg2 = "; peer type '" + peerTypeName + "'";
				//--------------------------------------------------------------------------------
				// create PeerType 
				PeerType peerType = new PeerType(peerTypeName);
				//--------------------------------------------------------------------------------
				//================================================================================
				// PPROPS
				//================================================================================
				//--------------------------------------------------------------------------------
				errMsg3 = "; pprops";
				//--------------------------------------------------------------------------------
				// get
				String rawPeerTypeProps = getRawTextOfChildElement(peerTypeElement, PmDslDefs.PROPS_DECLARATION__ELEMENT_NAME);
				//--------------------------------------------------------------------------------
				// assemble
				peerType.setRawPPropsTypes(rawPeerTypeProps);
				//================================================================================
				// WIRINGS
				//================================================================================
				//--------------------------------------------------------------------------------
				// get all "Wiring" elements from the "peerTypeElement" element as node list
				NodeList wiringsNodeList = peerTypeElement.getElementsByTagName(PmDslDefs.WIRING__ELEMENT_NAME);
				// get number of Wirings
				int nWirings = wiringsNodeList.getLength();
				/**/ tracer.println("nWirings = " + nWirings, Level.HI, m);	
				//--------------------------------------------------------------------------------
				for(int j = 0; j < nWirings; j++) {
					//--------------------------------------------------------------------------------
					// create Wiring 
					Wiring wiring = new Wiring();
					//--------------------------------------------------------------------------------
					// get i-th element
					Element wiringElement = getIthElement(wiringsNodeList, j);
					//--------------------------------------------------------------------------------
					// get "name" attribute
					String wiringName = wiringElement.getAttribute(PmDslDefs.NAME__ATTRIBUTE_NAME);
					/**/ tracer.println("wiringName = " + wiringName, Level.HI, m);	
					// set
					wiring.setRawWiringNames(wiringName);
					//--------------------------------------------------------------------------------
					// get "service" attribute
					String serviceName = wiringElement.getAttribute(PmDslDefs.SERVICE__ATTRIBUTE_NAME);
					/**/ tracer.println("serviceName = " + serviceName, Level.HI, m);	
					// set
					wiring.setServiceName(serviceName);
					//--------------------------------------------------------------------------------
					// wprops
					String rawWProps = getRawTextOfChildElement(wiringElement, PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME); 
					// set
					wiring.setRawWiringWPropsDefs(rawWProps);
					//--------------------------------------------------------------------------------
					// GUARDS:
					// get all "Guard" elements from the "wiringElement" element as node list
					NodeList guardsNodeList = wiringElement.getElementsByTagName(PmDslDefs.GUARD__ELEMENT_NAME);
					//--------------------------------------------------------------------------------
					// get number of Guards
					int nGuards = guardsNodeList.getLength();
					/**/ tracer.println("nGuards = " + nGuards, Level.HI, m);	
					//--------------------------------------------------------------------------------
					for(int k = 0; k < nGuards; k++) {
						errMsg3 = "; wiring '" + wiringName + "'; guard " + (k+1);
						// get k-th element
						Element guardElement = getIthElement(guardsNodeList, k);
						// translate & add
						wiring.addGuard((Guard) translateLinkElement(guardElement, new Guard(), k+1));
					}
					//--------------------------------------------------------------------------------
					// ACTIONS:
					// get all "Action" elements from the "wiringElement" element as node list
					NodeList actionsNodeList = wiringElement.getElementsByTagName(PmDslDefs.ACTION__ELEMENT_NAME);
					//--------------------------------------------------------------------------------
					// get number of Actions
					int nActions = actionsNodeList.getLength();
					/**/ tracer.println("nActions = " + nActions, Level.HI, m);	
					//--------------------------------------------------------------------------------	
					for(int k = 0; k < nActions; k++) {
						errMsg3 = "; wiring '" + wiringName + "'; action " + (k+1);
						// get k-th element
						Element actionElement = getIthElement(actionsNodeList, k);
						// translate & add
						wiring.addAction((Action) translateLinkElement(actionElement, new Action(), k+1));
					}
					//--------------------------------------------------------------------------------
					// assemble
					peerType.addWiring(wiring);
				}
				//--------------------------------------------------------------------------------
				// add PeerType to PeerTypes
				peerTypes.add(peerType);
				//--------------------------------------------------------------------------------
				// assemble
				pmmmComponents.peerTypes = peerTypes;
			}
			//================================================================================
			// CONFIGS:
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg1 = "";
			errMsg2 = "";
			errMsg3 = "";
			//--------------------------------------------------------------------------------
			// create
			Vector<Config> configs = new Vector<Config>();
			//--------------------------------------------------------------------------------
			// get all "Configs" elements from the "pmmm" element as node list
			NodeList configsNodeList = pmmmElement.getElementsByTagName(PmDslDefs.CONFIGS__ELEMENT_NAME);
			// get number of Configs
			int nConfigss = configsNodeList.getLength();
			/**/ tracer.println("nConfigss = " + nConfigss, Level.HI, m);
			if(nConfigss > 1)
				throw new SyntaxException("more than one " + PmDslDefs.CONFIGS__ELEMENT_NAME + " element found in Config", m);
			// get it
			Element configsElement = getIthElement(configsNodeList, 0);
			// its pmmm props
			//================================================================================
			// CONFIG:
			//================================================================================
			//--------------------------------------------------------------------------------
			// get all "Config" elements
			// - there shall be at most one
			NodeList configNodeList = configsElement.getElementsByTagName(PmDslDefs.CONFIG__ELEMENT_NAME);
			// get number of Config nodes
			int nConfigs = configNodeList.getLength();
			/**/ tracer.println("nConfigs = " + nConfigs, Level.HI, m);
			//--------------------------------------------------------------------------------
			for(int i = 0; i < nConfigs; i++) {
				//--------------------------------------------------------------------------------
				// get i-th "Config" element
				Element configElement = getIthElement(configNodeList, i);
				//--------------------------------------------------------------------------------
				// get its "name" attribute
				String configName = configElement.getAttribute(PmDslDefs.NAME__ATTRIBUTE_NAME);
				/**/ tracer.println("configName = " + configName, Level.HI, m);
				//--------------------------------------------------------------------------------
				errMsg1 = "config '" + configName + "'";
				//--------------------------------------------------------------------------------
				// new config
				Config config = new Config(configName);
				//================================================================================
				// PMMM:
				//================================================================================
				//--------------------------------------------------------------------------------
				errMsg2 = "; pmmm elements";
				//--------------------------------------------------------------------------------
				// get all "PMMM" elements
				// - there shall be at most one
				NodeList pmmmNodeList = configElement.getElementsByTagName(PmDslDefs.PMMM__ELEMENT_NAME);
				// get number of PMMM nodes
				int nPMMMs = pmmmNodeList.getLength();
				/**/ tracer.println("nPMMMs = " + nPMMMs, Level.HI, m);
				if(nPMMMs > 1)
					throw new SyntaxException("more than one PMMM element found in Config", m);
				// get it
				Element pmmmInConfigElement = getIthElement(pmmmNodeList, 0);
				// get its pmmm props
				String pmmmInConfigProps = getRawTextOfChildElement(pmmmInConfigElement, PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME); 
				// set it
				config.setRawPmmmPropsDefsTokenExpression(pmmmInConfigProps);
				//================================================================================
				// PEERS:
				//================================================================================
				//--------------------------------------------------------------------------------
				// get all "Peer" elements
				// - there shall be at most one
				NodeList peerNodeList = configElement.getElementsByTagName(PmDslDefs.PEER__ELEMENT_NAME);
				// get number of Peer nodes
				int nPeers = peerNodeList.getLength();
				/**/ tracer.println("nPeers = " + nPeers, Level.HI, m);
				//--------------------------------------------------------------------------------
				for(int j = 0; j < nPeers; j++) {
					//--------------------------------------------------------------------------------
					// create
					PeerConfig peerConfig = new PeerConfig();
					//--------------------------------------------------------------------------------
					// get it
					Element peerElement = getIthElement(peerNodeList, j);
					//--------------------------------------------------------------------------------
					// get its "name" attribute & set it
					String peerName = peerElement.getAttribute(PmDslDefs.NAME__ATTRIBUTE_NAME);
					/**/ tracer.println("peerName = " + peerName, Level.HI, m);
					peerConfig.setRawPeerNames(peerName);
					//--------------------------------------------------------------------------------
					errMsg2 = "; peer '" + peerName + "'";
					//--------------------------------------------------------------------------------
					// get its "type" attribute & set it
					String peerTypeName = peerElement.getAttribute(PmDslDefs.TYPE__ATTRIBUTE_NAME);
					/**/ tracer.println("peerTypeName = " + peerTypeName, Level.HI, m);
					peerConfig.setPeerTypeName(peerTypeName);
					//--------------------------------------------------------------------------------
					// get & set peer props
					peerConfig.setRawPeerPPropsDefs(getRawTextOfChildElement(peerElement, PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME));
					//--------------------------------------------------------------------------------
					// assemble
					config.addPeerConfig(peerConfig);
				}
				//--------------------------------------------------------------------------------
				// assemble
				configs.add(config);
			}
			//--------------------------------------------------------------------------------
			// set
			pmmmComponents.configs = configs;
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2 + errMsg3, m);
		} catch (SNHException e) {
			throw new SNHException(900020, errMsg1 + errMsg2 + errMsg3, m);
		}
		//--------------------------------------------------------------------------------
		// return
		return pmmmComponents;
	}

	//--------------------------------------------------------------------------------
	// translate a link element
	private Link translateLinkElement(Element linkElement, Link link, int number) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// number
		// - pass exception
		link.setLinkNumberAsString(Integer.toString(number));
		//--------------------------------------------------------------------------------
		// entry type
		// - pass exception
		link.setEntryTypeName(linkElement.getAttribute(PmDslDefs.ENTRY_TYPE__ATTRIBUTE_NAME));
		//--------------------------------------------------------------------------------
		// count
		link.setRawEntryCount(linkElement.getAttribute(PmDslDefs.COUNT__ATTRIBUTE_NAME));
		//--------------------------------------------------------------------------------
		// op
		link.setSpaceOpName(linkElement.getAttribute(PmDslDefs.SPACE_OP__ATTRIBUTE_NAME));
		//................................................................................
		// - set NOOP flag!!!
		if(link.getSpaceOpName().equals("NOOP")) { // hard coded; TBD..."
			link.setIsNoopLinkFlag(true);
		}
		//--------------------------------------------------------------------------------
		// container
		link.setContainerName(linkElement.getAttribute(PmDslDefs.CONTAINER__ATTRIBUTE_NAME));
		//--------------------------------------------------------------------------------
		// query
		link.setRawQuery(getRawTextOfChildElement(linkElement, PmDslDefs.QUERY__ELEMENT_NAME)); 
		//--------------------------------------------------------------------------------
		// var props set get
		link.setRawVarPropSetGet(getRawTextOfChildElement(linkElement, PmDslDefs.VAR_PROPS_SET_GET__ELEMENT_NAME)); 
		//--------------------------------------------------------------------------------
		// lprops
		link.setRawLPropsDefs(getRawTextOfChildElement(linkElement, PmDslDefs.PROPS_DEFINITION__ELEMENT_NAME)); 
		return link;
	}

	//--------------------------------------------------------------------------------
	// get i-th element from node list
	private Element getIthElement(NodeList nodeList, int i) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assertion
		if(nodeList.getLength() < i)
			throw new SNHException(555555, "node list index out of range; i = " + i, m);
		//--------------------------------------------------------------------------------
		// get i-the node
		Node node = nodeList.item(i);
		/**/ tracer.println(i + ". " + node.getNodeName(), Level.HI, m);
		//--------------------------------------------------------------------------------
		// node type must be "ELEMENT_NODE" anyhow...
		if (node.getNodeType() != Node.ELEMENT_NODE) 
			throw new SNHException(557775, "ill. node type = " + node.getNodeType(), m);
		//--------------------------------------------------------------------------------
		// convert node to element
		return (Element) node;
	}


	//================================================================================
	// PPROPS
	//================================================================================
	//--------------------------------------------------------------------------------
	// propsElementName = Def.PROPS_DECLARATION__ELEMENT_NAME | Def.PROPS_DEFINITION__ELEMENT_NAME | 
	//                    Def.QUERY__ELEMENT_NAME | Def.PROPS_VAR_SET_GET__ELEMENT_NAME | ...
	private static String getRawTextOfChildElement(Element element, String subElementName) throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer(); 
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String rawText = "";
		//--------------------------------------------------------------------------------
		// get direct child nodes
		NodeList childrenNodeList = element.getChildNodes();
		boolean foundFlag = false;
		for(int i = 0; i < childrenNodeList.getLength(); i++) {
			//--------------------------------------------------------------------------------
			// get next node
			Node nextNode = childrenNodeList.item(i);
			//--------------------------------------------------------------------------------
			// node type must be "ELEMENT_NODE" anyhow...
			if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
				//--------------------------------------------------------------------------------
				// node 2 element
				Element nextElement = (Element) nextNode;
				/**/ tracer.println("nextElement.getNodeName() = " + nextElement.getNodeName(), Level.HI, m);	
				//--------------------------------------------------------------------------------
				if(nextElement.getNodeName().equals(subElementName)) {
					//--------------------------------------------------------------------------------
					// do it this way to have a plausi check
					if(foundFlag) {
						throw new SyntaxException("more than one " + subElementName + " element found for " + element.getNodeName(), m);
					}
					//--------------------------------------------------------------------------------
					// get raw props 
					rawText = nextElement.getTextContent();
					//--------------------------------------------------------------------------------
					foundFlag = true;	
				}
			}
		}
		/**/ tracer.println("rawText = " + rawText, Level.HI, m);	
		// replace special chars
		return (new util.replace.Director(new XmlReplacements())).x2String(rawText);		
	}

} // END OF CLASS


//================================================================================
//EOF
//================================================================================

