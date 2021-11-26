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
// CODE REVIEWS: 
//================================================================================

package parser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// read PMMM from file and generate a "raw" PMMM where all expressions are raw and unevaluated strings
public abstract class Parser implements IParser {
	//--------------------------------------------------------------------------------
	// for debugging
	protected Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	protected String sourcePath = "";
	protected String ucName = "";
	protected String extension = ""; // e.g. ".xml"

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Parser(String absoluteUcPath, String fileName, String fileExtension /* e.g: ".xml" */) {
		this.sourcePath = absoluteUcPath;
		this.ucName = fileName;
		this.extension = fileExtension;
	}

	//================================================================================
	// XML UTILS
	//================================================================================
	//--------------------------------------------------------------------------------
	// translate xml file into Document
	// - nb: code is copied and adapted from the web;
	public static Document xml2Document(String fileNameAndPath) throws Exception {
		Document doc = null;
		//--------------------------------------------------------------------------------
		File inputFile = new File(fileNameAndPath);
		//--------------------------------------------------------------------------------
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		//--------------------------------------------------------------------------------
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			//--------------------------------------------------------------------------------
			try {
				doc = dBuilder.parse(inputFile);
			} catch (SAXException | IOException e) {
				throw e;
			}
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		//--------------------------------------------------------------------------------
		return(doc);
	}

	//--------------------------------------------------------------------------------
	// retrieve first sub-element with given name from given element;
	// there should be exactly one;
	public static Element getFirstSubElementWithName(String subElementName, Element element) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assert that element is not null
		if(element == null) {
			throw new SyntaxException("expected element " + subElementName + " not found in XML; " + 
					"maybe you have exported the XML as compressed XML?", m);
		}
		//--------------------------------------------------------------------------------
		// get all elements from the "diagram" element with subElementName
		NodeList nodeList = element.getElementsByTagName(subElementName);
		/**/ tracer.println("nodeList = " + nodeList, Level.NO, m);
		//--------------------------------------------------------------------------------
		// convert first node to element and return it 
		return((Element) nodeList.item(0));
	}


}

//================================================================================
// EOF
//================================================================================
