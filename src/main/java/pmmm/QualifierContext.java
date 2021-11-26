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

package pmmm;

import eval.IData;
import qa.exceptions.NotFoundException;
import qa.tracer.O;

// import debug.Tracer;

//================================================================================
// sets context to evaluate the data type of qualifier expressions, namely:
// - "<PMMM>.<propName>" ........ requires: sys pmmm props types AND pmmm type props types
// - "<PEER>.<propName>" ........ requires: sys pprops types     AND peer type pprops types
// - "<WIRING>.<propName>" ...... requires: sys wprops types     AND wiring type wprops types
//--------------------------------------------------------------------------------
// nb: qualifiers can be used in:
// - pmmm props defs ............ uses: "<PMMM>.<propName>"
// - pprops defs ................ uses: "<PMMM>.<propName>", "<PEER>.<propName>"
// - wprops defs ................ uses: "<PMMM>.<propName>", "<PEER>.<propName>", "<WIRING>.<propName>"
// - link queries ............... uses: "<PMMM>.<propName>", "<PEER>.<propName>", "<WIRING>.<propName>"
// - link var/props/set/get ..... uses: "<PMMM>.<propName>", "<PEER>.<propName>", "<WIRING>.<propName>"
// - lprops ..................... uses: "<PMMM>.<propName>", "<PEER>.<propName>", "<WIRING>.<propName>"
//--------------------------------------------------------------------------------
public class QualifierContext {
	//================================================================================
	// for debug:
	// private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// sys keyword types:
	KeywordTypes keywordTypes;
	//--------------------------------------------------------------------------------
	// user props types:
	// - <PMMM>
	PropsTypes curPmmmPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// - <PEER>
	PropsTypes curPPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// - <WIRING>
	PropsTypes curWPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// docu
	String curPmmmTypeName = "";
	String curPeerTypeName = "";
	String curWiringTypeName = "";
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public QualifierContext(KeywordTypes keywordTypes) {
		this.keywordTypes = keywordTypes;
	}
	
	//================================================================================
	// SET
	// - return "this" to support fluent interface
	//================================================================================
	//--------------------------------------------------------------------------------
	public QualifierContext setPmmmQualifierContext(PropsTypes propsTypes, String pmmmTypeName) {
		this.curPmmmPropsTypes = propsTypes;
		this.curPmmmTypeName = pmmmTypeName;
		return this;
	}
	//--------------------------------------------------------------------------------
	public QualifierContext setPeerQualifierContext(PropsTypes propsTypes, String peerTypeName) {
		this.curPPropsTypes = propsTypes;
		this.curPeerTypeName = peerTypeName;
		return this;
	}
	//--------------------------------------------------------------------------------
	public QualifierContext setWiringQualifierContext(PropsTypes propsTypes, String wiringTypeName) {
		this.curWPropsTypes = propsTypes;
		this.curWiringTypeName = wiringTypeName;
		return this;
	}
	
	//================================================================================
	// CLEAR
	// - return "this" to support fluent interface
	//================================================================================
	//--------------------------------------------------------------------------------
	public QualifierContext clearPmmmTypePropsTypes() {
		curPmmmPropsTypes = new PropsTypes();
		curPmmmTypeName = "";
		return this;
	}
	//--------------------------------------------------------------------------------
	public QualifierContext clearPeerTypePropsTypes() {
		curPPropsTypes = new PropsTypes();
		curPeerTypeName = "";
		return this;
	}
	//--------------------------------------------------------------------------------
	public QualifierContext clearWiringTypePropsTypes() {
		curWPropsTypes = new PropsTypes();
		curWiringTypeName = "";
		return this;
	}
	
	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	public IData.Type pmmmQualifierGetType(String propName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		IData.Type type;
		try {
			// search in user pmmm props types
			type = curPmmmPropsTypes.getType(propName);
		} catch (NotFoundException e) {
			try {
				// otherwise search in sys pmmm props types
				type = keywordTypes.getSysPmmmPropType(propName);
			} catch (NotFoundException e1) {
				throw new NotFoundException("pmmm qualifier: missing type of '" + propName + "'", m);
			}
		}
		return type;
	}
	//--------------------------------------------------------------------------------
	public IData.Type peerQualifierGetType(String propName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		IData.Type type;
		try {
			// search in user pprops types
			type = this.curPPropsTypes.getType(propName);
		} catch (NotFoundException e) {
			try {
				// otherwise search in sys pprops types
				type = keywordTypes.getSysPPropType(propName);
			} catch (NotFoundException e1) {
				throw new NotFoundException("right side '" + propName + "' of peer qualifier: " + e.getPmErrorMsg(), m, e1);
			}
		}
		return type;
	}
	//--------------------------------------------------------------------------------
	public IData.Type wiringQualifierGetType(String propName) throws NotFoundException {
		IData.Type type;
		try {
			// search in user wprops types
			type = this.curWPropsTypes.getType(propName);
		} catch (NotFoundException e) {
			// otherwise search in sys wprops types
			type = keywordTypes.getSysWPropType(propName);
		}
		return type;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// return String with context info for user message - there can be several ones set
	public String toMsg() {
		StringBuffer buf = new StringBuffer();
		buf.append("Qualifier Context: \n");
		//--------------------------------------------------------------------------------
		if(! curPmmmPropsTypes.isTVVEmpty()) {
			buf.append("  --> " + IPmDefs.PMMM_QUALIFIER + " " + curPmmmTypeName + " actual props types = " + this.curPmmmPropsTypes.toPairs() + "\n");
		}
		//--------------------------------------------------------------------------------
		if(! curPPropsTypes.isTVVEmpty()) {
			buf.append("  --> " + IPmDefs.PEER_QUALIFIER + " " + curPeerTypeName + " actual props types = " + this.curPPropsTypes.toPairs() + "\n");
		}
		//--------------------------------------------------------------------------------
		if(! curWPropsTypes.isTVVEmpty()) {
			buf.append("  --> " + IPmDefs.WIRING_QUALIFIER + " " + curWiringTypeName + " actual props types = " + this.curWPropsTypes.toPairs() + "\n");
		}
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
