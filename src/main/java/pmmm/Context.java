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

package pmmm;

import java.util.Vector;

import eval.IData;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// stores current context needed for type evaluation
// - TBD: the order in which the fus are called is relevant! 
// -- assure that maybe via assertions based on a state automaton
public class Context {
	//================================================================================
	// for debug:
	private Tracer tracer = new Tracer();
	//================================================================================
	// GLOBAL PMMM CONTEXT:
	//================================================================================
	//--------------------------------------------------------------------------------
	// pmmm type: name
	String pmmmTypeName;
	//--------------------------------------------------------------------------------
	// pmmm type: props types
	// - user props types
	PropsTypes pmmmTypePmmmPropsTypes = new PropsTypes();
	// - TBD: sys pmmm props types (needs extension of go automaton)
	NameTypeMap sysPmmmPropTypeMap = new NameTypeMap();
	//--------------------------------------------------------------------------------
	// entry types
	private EntryTypes entryTypes;
	//--------------------------------------------------------------------------------
	// sys data types
	private KeywordTypes keywordTypes;
	//--------------------------------------------------------------------------------
	// peer type names 
	private Vector<String> peerType_Names;
	//--------------------------------------------------------------------------------
	// peer types
	Vector<PeerType> peerTypes = new Vector<PeerType>();
	//--------------------------------------------------------------------------------
	// artificial props vector with URL:peerTypeName pairs
	// - for all peer type names!
	NameTypeMap peerTypeNameMap = new NameTypeMap();
	//================================================================================
	// CURRENT CONTEXT:
	// - unused ones are empty
	// - nb: user must not define lprops types, so no user props context needed for them
	// - nb: wiring types are integrated part of wirings, so no user props context needed for them
	//--------------------------------------------------------------------------------
	// what is the context used for?
	private IContext.Usage curUsage = IContext.Usage.UNDEFINED;
	//================================================================================
	// for PMMM:
	//================================================================================
	//--------------------------------------------------------------------------------
	private String curPeerType_Name = ""; // caution: do not mix up with curPeer_PeerTypeName and curPeer_RawPeerName
	//--------------------------------------------------------------------------------
	// caution: could be complex expression with many names...
	private String curWiring_NameOrNames = "";
	private String curWiringType_Name = "";
	//--------------------------------------------------------------------------------
	// for debug: linkKind + Number
	private String curLinkKindInfo = ""; 
	//--------------------------------------------------------------------------------
	private boolean curIsNoopLinkFlag = false;
	//--------------------------------------------------------------------------------
	// entry type name of entry transported by a noop-link
	private String curEntryType_Name = "";
	//--------------------------------------------------------------------------------
	// entry type of entry transported by a noop-link
	private EntryType curEntryType = new EntryType();
	//--------------------------------------------------------------------------------
	// user vars
	// - CAUTION: clear means reinit with new object !!!
	private NameTypeMap curUserVarTypeMap = new NameTypeMap();
	//--------------------------------------------------------------------------------
	// currently relevant user props types
	// - set depending on context usage to user defined: eprops, pmmmprops or pprops 
	// - nb: user cannot define new wprop or lprop types!
	private PropsTypes curUserPropsTypes = new PropsTypes(); 
	//--------------------------------------------------------------------------------
	// currently relevant sys props types
	// - set depending on context usage to sys defined: wprops, eprops, lprops, pprops or pmmmprops  
	// - nb: they are taken from the respective sys props of pmmmKeywordTypes, i.e. set ref to it
	private NameTypeMap curSysPropTypeMap = new NameTypeMap();
	//--------------------------------------------------------------------------------
	//================================================================================
	// for CONFIG: 
	//================================================================================
	//--------------------------------------------------------------------------------
	// - treated beyond the clearing/resetting mechanism applied below...
	// -- config name
	private String curConfigName = "";
	//--------------------------------------------------------------------------------
	// - peer:
	// -- peer type name 
	private String curPeer_PeerTypeName = "";
	// -- raw peer name, ie this could be an expression; is used for docu only...
	private String curPeer_RawPeerName = "";
	//================================================================================
	// QUALIFIER CONTEXT:
	//================================================================================
	//--------------------------------------------------------------------------------
	private QualifierContext qualifierContext;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// pass the essential global context forn evaluation
	// CAUTION: tracer does not work in constructor!!
	public Context(KeywordTypes keywordTypes, 
			String pmmmTypeName, 
			PropsTypes pmmmTypePmmmPropsTypes,
			Vector<String> pmmmTypePeerNames, 
			Vector<PeerType> pmmmPeerTypes,
			EntryTypes pmmmEntryTypes) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// reset entire current context
		this.resetTypesContext();
		//--------------------------------------------------------------------------------
		// init global context
		this.keywordTypes = keywordTypes;
		this.pmmmTypeName = pmmmTypeName;
		this.pmmmTypePmmmPropsTypes = pmmmTypePmmmPropsTypes;
		this.peerType_Names = pmmmTypePeerNames;
		this.peerTypes = pmmmPeerTypes;
		this.entryTypes = pmmmEntryTypes;
		//--------------------------------------------------------------------------------
		this.qualifierContext = new QualifierContext(keywordTypes);
		//--------------------------------------------------------------------------------
		// help fu because tracer mechanism can't be used in constructor
		this.construct_Url_PeerTypeName_Pairs();
	}

	//--------------------------------------------------------------------------------
	private void construct_Url_PeerTypeName_Pairs() throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// construct artificial vector with URL:<peerTypeName> pairs for all peer type names
		for(int i = 0; i < peerType_Names.size(); i++) {
			String peerTypeName = peerType_Names.get(i);
			try {
				// add to my map
				peerTypeNameMap.add(peerTypeName, IData.Type.URL);
			} catch (SyntaxException e) {
				throw new SyntaxException("peer type", m, e);
			}
		}	
	}

	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	// enhance ***global*** context by pmmm props types as soon as they have been data type evaluated
	public void setPmmmPropsTypes(PropsTypes pmmmTypePmmmPropsTypes) {
		this.pmmmTypePmmmPropsTypes = pmmmTypePmmmPropsTypes;
	}

	//================================================================================
	// GET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public IContext.Usage getCurUsage() {
		return curUsage;
	}
	//--------------------------------------------------------------------------------
	public PropsTypes getCurUserPropsTypes() {
		return curUserPropsTypes;
	}
	//--------------------------------------------------------------------------------
	public NameTypeMap getCurSysPropTypeMap() {
		return curSysPropTypeMap;
	}
	//--------------------------------------------------------------------------------
	public KeywordTypes getKeywordTypes() {
		return keywordTypes;
	}
	//--------------------------------------------------------------------------------
	public QualifierContext getQualifierContext() {
		return qualifierContext;
	}
	//--------------------------------------------------------------------------------
	public EntryType getCurEntryType() {
		return curEntryType;
	}


	//================================================================================
	// CLEAR CURRENT CONTEXT FOR ***TYPES***
	// - keep global PMMM context
	// - some vars are only used for docu purposes
	//================================================================================
	//--------------------------------------------------------------------------------
	// reset entire current context for ***TYPES***
	// - nb: for config some extra fields are needed and treated seperately
	public void resetTypesContext()  {
		curUsage = IContext.Usage.UNDEFINED;
		curPeerType_Name = "";
		curWiring_NameOrNames = "";	
		curWiringType_Name = "";	
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		curUserPropsTypes = new PropsTypes();	
		curSysPropTypeMap = new NameTypeMap();	
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	// for CONFIG:
	// - refine context
	public void setUserProps(PropsTypes userProps) {
		// TBD: assert that usage is CONFIG... (either peer defs or entry defs)
		this.curUserPropsTypes = userProps;
	}

	//================================================================================
	//================================================================================
	// SWITCH CURRENT CONTEXT 
	// - to what kind of artifact(s) shall be treated now (i.e. by type eval) and set
	// -- all sys and user props types which are visible and needed when evaluating this very artifact;
	// - very sophisticated logic !!!
	// - possibly "inheriting" parts of a previous context (cf. "keep" context comments)
	// - nb: sys consts and sys vars are always fix and need not be changed between contexts 
	// -- they are part of the global context, ie contained in keyword types
	// - caution: set usage after clearing the entire context
	// - return switched context so that e.g.  context switch can be used as fu arg, 
	//  -- or in fluent interface by caller, etc.
	//================================================================================
	//================================================================================

	//================================================================================
	//================================================================================
	// ***TYPES***
	//================================================================================
	//================================================================================

	//================================================================================
	// PMMM__PMMM_TYPE__PMMM_PROPS_TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2PmmmType_PmmmPropsTypes() {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear everything:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PMMM_TYPE__PMMM_PROPS_TYPES;
		curSysPropTypeMap = keywordTypes.sysPmmmPropTypeMap; // for duplicate check	
		//--------------------------------------------------------------------------------
		/**/ tracer.println("curUsage = " + curUsage, Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__PMMM_TYPE__PMMM_PROPS_TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2PmmmType_PmmmPropsDefs() {
		//--------------------------------------------------------------------------------
		// clear everything:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS;
		curUserPropsTypes = pmmmTypePmmmPropsTypes;	
		curSysPropTypeMap = keywordTypes.sysPmmmPropTypeMap;	
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__ENTRY_TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2EntryTypes() {
		//--------------------------------------------------------------------------------
		// clear everything:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.ENTRY_TYPES;
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__ENTRY_TYPES__EPROPS_TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2EntryTypesEPropsTypes() {
		//--------------------------------------------------------------------------------
		// clear everything:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.ENTRY_TYPES__EPROPS_TYPES;
		curSysPropTypeMap = keywordTypes.sysEPropTypeMap; // for duplicate check
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__ENTRY_TYPES__EPROPS_DEFS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2EntryTypesEPropsDefs(PropsTypes epropsTypes) { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curPeerType_Name = "";
		curWiring_NameOrNames = "";		
		curWiringType_Name = "";		
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		//--------------------------------------------------------------------------------
		// keep:
		// - curSysPropTypeMap
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.ENTRY_TYPES__EPROPS_DEFS;
		curUserPropsTypes = epropsTypes;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("epropsTypes = " + epropsTypes.toPairs(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__PEER_TYPE
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2PeerType(String peerTypeName) { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear everything:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PEER_TYPE;
		curPeerType_Name = peerTypeName;
		curSysPropTypeMap = keywordTypes.sysPPropTypeMap;
		/**/ tracer.println("peerTypeName = '" + curPeerType_Name + "'", Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__PEER_TYPE__PPROPS_TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2PeerType_PPropsTypes() { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curWiring_NameOrNames = "";		
		curWiringType_Name = "";		
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		curUserPropsTypes = new PropsTypes();
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PPROPS_TYPES;
		curSysPropTypeMap = keywordTypes.sysPPropTypeMap;	
		/**/ tracer.println("peerTypeName = '" + curPeerType_Name + "'", Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PEER_TYPE__PROPS_DEFS
	//================================================================================
	// nb: must be clearly called after eval of peer type pprops types
	public Context switch2PeerType_PPropsDefs(PropsTypes ppropsTypes) { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curWiring_NameOrNames = "";		
		curWiringType_Name = "";		
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name
		// - curSysPropTypeMap
		//--------------------------------------------------------------------------------
		// set:
		// - usage
		curUsage = IContext.Usage.PEER_TYPE__PPROPS_DEFS;
		//		// TBD:
		//		// - set sys props to pmmm type props
		//		// -- tricky: because "PMMM." qualifier can be used in peer pprops !!!
		//		curSysPropTypeMap = this.pmmmType_UserPropsTypes;
		// TBD:
		curSysPropTypeMap = keywordTypes.sysPPropTypeMap;	
		// - use the given pprops types
		curUserPropsTypes = ppropsTypes;
		/**/ tracer.println("ppropsTypes = '" + ppropsTypes.toUserInfo(true /* printSemiColonAfterTv */, true /* removeOuterBrackets */) + "'", Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__WIRING
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2Wiring(String wiringName, String wiringTypeName) { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		curUserPropsTypes = new PropsTypes();	
		curSysPropTypeMap = new NameTypeMap();	
		//--------------------------------------------------------------------------------
		// keep: peer type context:
		// - curPeerType_Name
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.WIRING;
		curWiring_NameOrNames = wiringName;
		curWiringType_Name = wiringTypeName;
		/**/ tracer.println("curPeerType_Name = '" + curPeerType_Name + "'; " +
				"curWiring_Name = '" + curWiring_NameOrNames + "'; curWiringType_Name = '" + curWiringType_Name, Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__WIRING_TYPE
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2WiringType(String wiringTypeName) { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curWiring_NameOrNames = "";
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		curUserPropsTypes = new PropsTypes();	
		curSysPropTypeMap = new NameTypeMap();	
		//--------------------------------------------------------------------------------
		// keep: peer type context:
		// - curPeerType_Name
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.WIRING_TYPE;
		curWiringType_Name = wiringTypeName;
		/**/ tracer.println("curPeerType_Name = '" + curPeerType_Name + "'; " +
				"curWiring_Name = '" + curWiring_NameOrNames + "'; curWiringType_Name = '" + curWiringType_Name, Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__WIRING_TYPE__WPROPS_TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2WiringType_WPropsTypes() { /**/O m=new O(){}/*debug*/;
	//--------------------------------------------------------------------------------
	// clear:
	curLinkKindInfo = ""; 
	curIsNoopLinkFlag = false; 
	curEntryType_Name = "";
	curEntryType = new EntryType();
	curUserVarTypeMap = new NameTypeMap();
	curUserPropsTypes = new PropsTypes();	
	//--------------------------------------------------------------------------------
	// keep:
	// - curPeerType_Name 
	// - curWiring_Name 	
	// - curWiringType_Name	
	//--------------------------------------------------------------------------------
	// set:
	curUsage = IContext.Usage.WPROPS_TYPES;
	curSysPropTypeMap = keywordTypes.sysWPropTypeMap; // for duplicate check
	//--------------------------------------------------------------------------------
	/**/ tracer.println("CONTEXT SWITCH to " + toMsg() + "...sys props = " + curSysPropTypeMap, Level.NO, m);
	//--------------------------------------------------------------------------------
	// return me
	return this;
	}

	//================================================================================
	// PMMM__WIRING__NAMES 
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2WiringNames() { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		curUserPropsTypes = new PropsTypes();
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name  
		// - curWiring_Name 		
		// - curWiringType_Name		
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.WIRING_NAMES;
		curSysPropTypeMap = keywordTypes.sysWPropTypeMap;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("CONTEXT SWITCH to " + toMsg() + "...sys props = " + curSysPropTypeMap + 
				"; user props = " + curUserPropsTypes, Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__WIRING__WPROPS_DEFS (FOR BOTH: WIRING AND WIRING TYPE)
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2WiringOrWiringType_WPropsDefs(PropsTypes wpropsTypes) { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curLinkKindInfo = ""; 
		curIsNoopLinkFlag = false; 
		curEntryType_Name = "";
		curEntryType = new EntryType();
		curUserVarTypeMap = new NameTypeMap();
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name  
		// - curWiring_Name 		
		// - curWiringType_Name		
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.WIRING_TYPE_AND_WIRING__WPROPS_DEFS;
		curUserPropsTypes = wpropsTypes;
		curSysPropTypeMap = keywordTypes.sysWPropTypeMap;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("CONTEXT SWITCH to " + toMsg() + "...sys props = " + curSysPropTypeMap + 
				"; user props = " + curUserPropsTypes, Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__LINK
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2Link(String linkIdentifier, boolean isNoopLinkFlag, String linkEntryTypeName, String spaceOpName) 
			throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name 
		// - curWiring_Name 	
		// - curWiringType_Name 	
		// - curUserVarTypeMap
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.LINK__COUNT;
		curLinkKindInfo = linkIdentifier;
		curIsNoopLinkFlag = isNoopLinkFlag;
		//--------------------------------------------------------------------------------
		// set or clear:
		// - if not noop link, set curEntryType_Name, curEntryType, curUserPropsTypes and curSysPropTypeMap
		if(! curIsNoopLinkFlag) {
			//--------------------------------------------------------------------------------
			// set curEntryType_Name:
			curEntryType_Name = linkEntryTypeName;
			//--------------------------------------------------------------------------------
			// set curSysPropTypeMap to sys defined eprops types:
			// NB: these also hold for type "*"
			curSysPropTypeMap = keywordTypes.sysEPropTypeMap;
			//--------------------------------------------------------------------------------
			// if linkEntryTypeName != "*" aka WILDCARD
			// - set curEntryType, curUserPropsTypes, and curSysPropTypeMap
			// - TBD: use define for wildcard string
			if(! linkEntryTypeName.equals("*")) {
				try {
					//--------------------------------------------------------------------------------
					// set curEntryType:
					curEntryType = entryTypes.getEntryType(linkEntryTypeName);
					//--------------------------------------------------------------------------------
					// set curUserPropsTypes to user defined eprop types:
					curUserPropsTypes = curEntryType.epropsTypes;
					//--------------------------------------------------------------------------------
					/**/ tracer.println(linkEntryTypeName + " --> has eprop types " + curUserPropsTypes, Level.LO, m);
				} catch (NotFoundException e) { 
					throw new SyntaxException(this.toMsg() + "entry type definition missing", m, e);
				}
			}
			else {
				// set at least the entry type name!
				curEntryType.setEntryTypeName(linkEntryTypeName);
			}
		}
		//--------------------------------------------------------------------------------
		// noop link:
		else {
			//--------------------------------------------------------------------------------
			// check: wildcard and CREATE are not compatible!
			if(spaceOpName.equals(IPmDefs.CREATE)) {
				throw new SyntaxException(this.toMsg() + "wildcard ('*') and " + IPmDefs.CREATE + " are not compatible", m);
			}
			//--------------------------------------------------------------------------------
			// clear
			curEntryType_Name = "";
			curEntryType = new EntryType();
			curUserPropsTypes = new PropsTypes();	
			curSysPropTypeMap = new NameTypeMap();	
		}
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__LINK__QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// only for docu
	public Context switch2Link_Query() {
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name 
		// - curWiring_Name 	
		// - curWiringType_Name 	
		// - curLinkKindInfo
		// - curIsNoopLinkFlag
		// - curEntryType_Name
		// - curEntryType
		// - curUserVarTypeMap
		// - curUserPropsTypes
		// - curSysPropTypeMap
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.LINK__QUERY;
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__LINK__VAR_PROP_SET_GET
	//================================================================================
	//--------------------------------------------------------------------------------
	// only for docu
	public Context switch2Link_VarPropSetGet() {
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name 
		// - curWiring_Name 	
		// - curWiringType_Name 	
		// - curLinkKindInfo
		// - curIsNoopLinkFlag
		// - curEntryType_Name
		// - curEntryType
		// - curUserVarTypeMap
		// - curUserPropsTypes
		// - curSysPropTypeMap
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.LINK__VAR_PROP_SET_GET;
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM__LINK__LPROPS_DEFS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2Link_LPropsDefs() { 
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		curEntryType_Name = "";
		curEntryType = new EntryType();
		//--------------------------------------------------------------------------------
		// keep:
		// - curPeerType_Name 
		// - curWiring_Name 	
		// - curWiringType_Name 	
		// - curLinkKindInfo
		// - curIsNoopLinkFlag
		// - curUserVarTypeMap
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.LINK__LPROPS_DEFS;
		curSysPropTypeMap = keywordTypes.sysLPropTypeMap;
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}


	//================================================================================
	//================================================================================
	// ***CONFIG***
	//================================================================================
	//================================================================================

	//================================================================================
	// CONFIG
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context switch2Config(String configName) {
		//--------------------------------------------------------------------------------
		// clear types context:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// clear beyond the types reset mechanism:
		curPeer_PeerTypeName = "";
		curPeer_RawPeerName = "";
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.CONFIG;
		//--------------------------------------------------------------------------------
		// - set (beyond the usual mechanism ... just stays until next config is encountered ...)
		curConfigName = configName; 
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS
	//================================================================================
	public Context switch2Config_Pmmm_PropsDefs() { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear:
		// - clear types context:
		resetTypesContext();
		// clear beyond the types reset mechanism:
		curPeer_PeerTypeName = "";
		curPeer_RawPeerName = "";
		//--------------------------------------------------------------------------------
		// keep:
		// - curConfigName
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS;
		curUserPropsTypes = pmmmTypePmmmPropsTypes;
		curSysPropTypeMap = keywordTypes.sysPmmmPropTypeMap;	
		//--------------------------------------------------------------------------------
		/**/ tracer.println("curUserPropsTypes = " + curUserPropsTypes.toPairs(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PEER_CONFIG
	//================================================================================
	// nb: rawPeerName is the not yet processed peer name(s); used for docu only...
	public Context switch2Config_PeerConfig(String peerTypeName, String rawPeerName) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		/**/ tracer.println("curPeer_PeerTypeName = '" + peerTypeName + "'; curPeer_RawPeerName = '" + rawPeerName + "'", Level.NO, m);
		//--------------------------------------------------------------------------------
		// clear types context:
		resetTypesContext();
		//--------------------------------------------------------------------------------
		// keep:
		// - curConfigName
		//--------------------------------------------------------------------------------
		// assertions
		if (util.Util.isEmptyString(peerTypeName))
			throw new SNHException(662288, "peer name empty", m);
		if (util.Util.isEmptyString(rawPeerName))
			throw new SNHException(983474, "peer type name empty", m);
		//--------------------------------------------------------------------------------
		// set:
		//................................................................................
		// - curUsage
		curUsage = IContext.Usage.PEER_CONFIG;
		//................................................................................
		// - curUserPropsTypes:
		boolean foundFlag = false; 
		//................................................................................
		// -- find peer type's pprops types
		for(int i = 0; !foundFlag && i < peerTypes.size(); i++) {
			//................................................................................
			// get next peer type
			PeerType nextPeerType = peerTypes.get(i);
			//................................................................................
			// same name?
			if(nextPeerType.peerTypeName.equals(peerTypeName)) {
				//................................................................................
				// set cur user props types to peer type's pprops
				curUserPropsTypes = nextPeerType.peerTypePPropsTypes;
				/**/ tracer.println("curUserPropsTypes = " + curUserPropsTypes.toPairs(), Level.NO, m);
				//................................................................................
				// found
				foundFlag = true;
			}
		}
		if(! foundFlag) 
			throw new SyntaxException("missing peer type " + peerTypeName, m);
		//--------------------------------------------------------------------------------
		// set (beyond the usual mechanism ... just stays until next peer is encountered ...)
		this.curPeer_PeerTypeName = peerTypeName;
		this.curPeer_RawPeerName = rawPeerName;
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PEER__NAMES
	//================================================================================
	public Context switch2Config_PeerNames() throws SNHException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// keep everything from types context, especially curUserPropsTypes
		//--------------------------------------------------------------------------------
		// keep:
		// - curConfigName
		// - curPeer_PeerTypeName
		// - curPeer_RawPeerName
		//--------------------------------------------------------------------------------
		// assertions
		if (util.Util.isEmptyString(curPeer_RawPeerName))
			throw new SNHException(746533, "cur peer name empty", m);
		if (util.Util.isEmptyString(this.curPeer_PeerTypeName))
			throw new SNHException(785223, "cur peer type name empty", m);
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PEER_NAMES;
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// PEER__PPROPS_DEFS
	//================================================================================
	public Context switch2Config_Peer_PropsDefs() throws SNHException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// keep everything from types context, especially curUserPropsTypes
		//--------------------------------------------------------------------------------
		// keep:
		// - curConfigName
		// - curPeer_PeerTypeName
		// - curPeer_RawPeerName
		//--------------------------------------------------------------------------------
		// assertions
		if (util.Util.isEmptyString(curPeer_RawPeerName))
			throw new SNHException(111039, "cur peer name empty", m);
		if (util.Util.isEmptyString(this.curPeer_PeerTypeName))
			throw new SNHException(143239, "cur peer type name empty", m);
		//--------------------------------------------------------------------------------
		// set:
		curUsage = IContext.Usage.PEER__PPROPS_DEFS;
		//--------------------------------------------------------------------------------
		/**/ tracer.println("curPeer_PeerTypeName = '" + curPeer_PeerTypeName + "'; curPeer_RawPeerName = '" + curPeer_RawPeerName + "'", Level.NO, m);
		/**/ tracer.println("curUserPropsTypes = " + curUserPropsTypes.toPairs(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	//================================================================================
	// QUALIFIER CONTEXT 
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	public Context setPmmmQualifierContext(PropsTypes pmmmPropsTypes, String pmmmTypeName) {
		qualifierContext.setPmmmQualifierContext(pmmmPropsTypes, pmmmTypeName);
		return this;
	}
	//--------------------------------------------------------------------------------
	public Context setPeerQualifierContext(PropsTypes ppropsTypes, String peerTypeName) {
		qualifierContext.setPeerQualifierContext(ppropsTypes, peerTypeName);
		return this;
	}
	//--------------------------------------------------------------------------------
	public Context setWiringQualifierContext(PropsTypes wpropsTypes, String wiringTypeName) {
		qualifierContext.setWiringQualifierContext(wpropsTypes, wiringTypeName);
		return this;
	}
	//--------------------------------------------------------------------------------

	//================================================================================
	//================================================================================
	// OTHER FUNCTIONS
	//================================================================================
	//================================================================================

	//================================================================================
	// ADD TO CURRENT CONTEXT: 
	//================================================================================
	//--------------------------------------------------------------------------------
	// add user var type;
	// throws user error exception on duplicate;
	// nb: var name with $
	public Context addUserVarType(String varName, IData.Type varType) throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println(this.toMsg() + "add user var type: " + varName + "<"+  varType+ ">" + " to context", Level.NO, m);		
		//--------------------------------------------------------------------------------
		try {
			// add checks for duplicates
			curUserVarTypeMap.add(varName, varType); 
			/**/ tracer.println(this.toMsg() + "all current user var types = " + curUserVarTypeMap, Level.NO, m);		
		} catch(SyntaxException e) {
			// duplicate var
			throw new SyntaxException(this.toMsg() + "duplicate user var type for '" + varName + "'", m);
		}
		//--------------------------------------------------------------------------------
		// return me
		return this;
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// does peer type name exist in the PMMM?
	// - nb: considers also built-in peer types
	public boolean peerTypeNameExists(String peerTypeName) {
		//--------------------------------------------------------------------------------
		// user peer type?
		for(int i = 0; i < peerType_Names.size(); i++) {
			if(peerType_Names.get(i).equals(peerTypeName)) {
				return true;
			}
		}
		//--------------------------------------------------------------------------------
		// built-in peer type?
		try {
			keywordTypes.getSysPeerType(peerTypeName);
			return true;
		} catch(NotFoundException e) {
			// no peer type
		}
		//--------------------------------------------------------------------------------
		return false;
	}

	//--------------------------------------------------------------------------------
	// get type of sys const;
	public IData.Type getSysConstType(String name) throws NotFoundException {
		return keywordTypes.getSysConstType(name);
	}
	//--------------------------------------------------------------------------------
	// test if it is a user prop depending on the context:
	// - eprops, pprops, pmprops
	// - nb: lprop and wprop types cannot be defined by user
	public boolean isUserPropType(String propName) {
		// just pass on the exception without extra info
		return curUserPropsTypes.nameExists(propName);
	}
	//--------------------------------------------------------------------------------
	// get type of property of the currently used user defined property depending on the context:
	// - eprops, pprops, pmprops
	// - nb: lprop and wprop types cannot be defined by user
	public IData.Type getUserPropType(String propName) throws NotFoundException {
		// just pass on the exception without extra info
		return curUserPropsTypes.getType(propName);
	}
	//--------------------------------------------------------------------------------
	// get type of property of the current sys props;
	public IData.Type getSysPropType(String propName) throws NotFoundException {
		// just pass on the exception without extra info
		return curSysPropTypeMap.getType(propName);
	}
	//--------------------------------------------------------------------------------
	// test if name is a current sys prop;
	public boolean isSysProp(String name) {
		return curSysPropTypeMap.nameExists(name);
	}
	//--------------------------------------------------------------------------------
	// get type of user var;
	// var name includes the starting '$';
	public IData.Type getUserVarType(String varName) throws NotFoundException {
		// just pass on the exception without extra info
		return curUserVarTypeMap.getType(varName);
	}
	//--------------------------------------------------------------------------------
	// get type of sys fu;
	public IData.Type getSysFuType(String sysFuName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		/**/ tracer.println("searching for " + sysFuName, Level.NO, m);
		//--------------------------------------------------------------------------------
		// just pass on the exception without extra info
		return keywordTypes.getSysFuType(sysFuName);
	}
	//--------------------------------------------------------------------------------
	// get type of sys var;
	// nb: var name includes the starting '$$';
	public IData.Type getSysVarType(String sysVarName) throws NotFoundException {
		// just pass on the exception without extra info
		return keywordTypes.getSysVarType(sysVarName);
	}
	//--------------------------------------------------------------------------------
	// test if name is a peer type name
	// - if so, return the type URL
	public IData.Type getPeerType(String peerTypeName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		for(int i = 0; i < peerType_Names.size(); i++) {
			if(peerType_Names.get(i).equals(peerTypeName))
				return IData.Type.URL;
		}
		throw new NotFoundException("not found", m);
	}

	//--------------------------------------------------------------------------------
	// for CONFIG
	//--------------------------------------------------------------------------------

	//--------------------------------------------------------------------------------
	// get pprops declarations of a peer type
	public PropsTypes getUserPeerTypePPropsTypes(String peerTypeName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("searching for " + peerTypeName, Level.NO, m);
		/**/ tracer.println("peerTypes = " + peerTypes, Level.NO, m);
		//--------------------------------------------------------------------------------
		for(int i = 0; i < peerTypes.size(); i++) {
			PeerType peerType = peerTypes.get(i);
			if(peerType.peerTypeName.equals(peerTypeName)) {
				/**/ tracer.println("found!", Level.NO, m);
				return peerType.peerTypePPropsTypes;
			}
		}
		throw new NotFoundException("peer type" + peerTypeName + "not found", m);
	}

	//--------------------------------------------------------------------------------
	// get wprops types of a peer type's wiring;
	// caution: do not use wiring types any more... they have been integrated into wirings.. wiring types are not type evaluated!
	public PropsTypes getUserWiringWPropsTypes(String peerTypeName, String wiringTypeName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		/**/ tracer.println("searching for " + peerTypeName, Level.NO, m);
		for(int i = 0; i < peerTypes.size(); i++) {
			PeerType peerType = peerTypes.get(i);
			//--------------------------------------------------------------------------------
			if(peerType.peerTypeName.equals(peerTypeName)) {
				/**/ tracer.println("peer type '" + peerTypeName + "' found; search for wiring with type '" + wiringTypeName + "'", Level.NO, m);
				//--------------------------------------------------------------------------------
				// search for wiring type with given name
				for(int j = 0; j < peerType.wirings.size(); j++ ) {
					Wiring wiring = peerType.wirings.get(j);
					if(wiring.wiringType.wiringTypeName.equals(wiringTypeName)) {
						//--------------------------------------------------------------------------------
						// found
						/**/ tracer.println("wprops types = " + wiring.wiringType.wiringTypeWPropsTypes, Level.NO, m);
						return wiring.wiringType.wiringTypeWPropsTypes;
					}
				}
			}
		}
		throw new NotFoundException("wiring type '" + wiringTypeName + "' of peer type '" + peerTypeName + "' not found", m);
	}

	//--------------------------------------------------------------------------------
	// get eprops declarations of an entry type
	public PropsTypes getUserEPropsTypes(String entryTypeName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		EntryType entryType;
		try {
			entryType = entryTypes.getEntryType(entryTypeName);
			/**/ tracer.println(entryTypeName + " EPROPS: " + entryType.epropsTypes, Level.NO, m);
		} catch (NotFoundException e) {
			throw new NotFoundException("eprop def", m, e);
		}
		return entryType.epropsTypes;
	}

	//--------------------------------------------------------------------------------
	// get type of user or sys pmmm prop of current PMMM
	public IData.Type getUserOrSysPmmmPropType(String pmmmPropName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("searching for " + pmmmPropName, Level.NO, m);
		//--------------------------------------------------------------------------------
		IData.Type type = IData.Type.UNDEFINED;
		//--------------------------------------------------------------------------------
		// user pmmm prop?
		try {
			type = pmmmTypePmmmPropsTypes.getType(pmmmPropName);
			/**/ tracer.println("type = " + type, Level.NO, m);
		} catch (NotFoundException e1) {
			//--------------------------------------------------------------------------------
			// sys pmmm prop?
			try {
				return sysPmmmPropTypeMap.getType(pmmmPropName);
			} catch (NotFoundException e2) {
				throw new NotFoundException("user or sys pmmm prop", m, e2);
			}
		}
		return type;
	}

	//--------------------------------------------------------------------------------
	// get type of user or sys pprop of current peer
	public IData.Type getUserOrSysPPropType(String ppropName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("searching for " + ppropName, Level.NO, m);
		//--------------------------------------------------------------------------------
		IData.Type type = IData.Type.UNDEFINED;
		//--------------------------------------------------------------------------------
		// user pprop?
		try {
			PropsTypes userPPropsTypes = getUserPeerTypePPropsTypes(curPeerType_Name); // TBD: treat this exception extra...
			/**/ tracer.println("userPPropsDeclarations = " + userPPropsTypes, Level.NO, m);
			type = userPPropsTypes.getType(ppropName);
		} catch (Exception e1) {
			//--------------------------------------------------------------------------------
			// sys pprop?
			try {
				type = keywordTypes.getSysPPropType(ppropName);
			} catch (NotFoundException e2) {
				throw new NotFoundException("user or sys pprop", m, e2);
			}
		}
		return type;
	}

	//--------------------------------------------------------------------------------
	// get type of user or sys wprop of current wiring of current peer
	public IData.Type getUserOrSysWPropType(String wpropName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("searching for " + wpropName + "; curPeerType_Name = " + curPeerType_Name + "; curWiringType_Name = " + curWiringType_Name, Level.NO, m);
		//--------------------------------------------------------------------------------
		IData.Type type = IData.Type.UNDEFINED;
		//--------------------------------------------------------------------------------
		// user wprop?
		try {
			PropsTypes userWPropsDeclarations = getUserWiringWPropsTypes(curPeerType_Name, curWiringType_Name); // TBD: treat this exception separately
			/**/ tracer.println("userWPropsDeclarations = " + userWPropsDeclarations, Level.NO, m);
			type = userWPropsDeclarations.getType(wpropName);
		} catch (NotFoundException e1) {
			//--------------------------------------------------------------------------------
			// sys wprop?
			try {
				type = keywordTypes.getSysWPropType(wpropName);
			} catch (NotFoundException e2) {
				throw new NotFoundException("user or sys wprop", m, e2);
			}
		}
		return type;
	}

	//--------------------------------------------------------------------------------
	// is the usage "<X>PROPS TYPES"?
	// - caution: keep up-to-date
	public boolean curUsageisPropsTypes() {
		switch(this.curUsage) {
		case PMMM_TYPE__PMMM_PROPS_TYPES:
		case PPROPS_TYPES: 
		case ENTRY_TYPES__EPROPS_TYPES:
		case WPROPS_TYPES:
			return true;
		case ENTRY_TYPES:
		case ENTRY_TYPES__EPROPS_DEFS:
		case PEER_TYPE:
		case PEER_NAMES:
		case PEER_TYPE__PPROPS_DEFS: 
		case WIRING: 
		case WIRING_NAMES: 
		case WIRING_TYPE: 
		case WIRING_TYPE_AND_WIRING__WPROPS_DEFS: 
		case LINK__COUNT: 
		case LINK__QUERY: 
		case LINK__VAR_PROP_SET_GET: 
		case LINK__LPROPS_DEFS:
		case CONFIG: 
		case PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS: 
		case PEER_CONFIG: 
		case PEER__PPROPS_DEFS: 
		case UNDEFINED:
			break;
		};
		return false;
	}

	//--------------------------------------------------------------------------------
	// is the usage "<X>PROPS TYPES"?
	// - caution: keep up-to-date
	public boolean curUsageIsPropsDefs() {
		switch(this.curUsage) {
		case PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS: 
		case ENTRY_TYPES__EPROPS_DEFS:
		case WIRING_TYPE_AND_WIRING__WPROPS_DEFS: 
		case PEER__PPROPS_DEFS: 
			return true;
		case PMMM_TYPE__PMMM_PROPS_TYPES:
		case ENTRY_TYPES:
		case ENTRY_TYPES__EPROPS_TYPES:
		case PEER_TYPE:
		case PEER_NAMES:
		case PPROPS_TYPES: 
		case PEER_TYPE__PPROPS_DEFS: 
		case WIRING: 
		case WIRING_NAMES: 
		case WIRING_TYPE: 
		case WPROPS_TYPES:
		case LINK__COUNT: 
		case LINK__QUERY: 
		case LINK__VAR_PROP_SET_GET: 
		case LINK__LPROPS_DEFS:
		case CONFIG: 
		case PEER_CONFIG: 
		case UNDEFINED:
			break;
		};
		return false;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// return String with context info for user message
	// - message ends with ": "
	public String toMsg() {
		StringBuffer buf = new StringBuffer();
		boolean inPeerType = false;

		//--------------------------------------------------------------------------------
		buf.append(pmmmTypeName);
		//--------------------------------------------------------------------------------
		if(curPeerType_Name != null && curPeerType_Name.length() > 0) {
			buf.append(" / " + curPeerType_Name);
			inPeerType = true;
		}
		//--------------------------------------------------------------------------------
		// nb: there are wiring types beyond those contained in in-line peer type wirings...
		// - output only non-inline wirings; recognition by "_"; TBD: hardcoded
		if((curWiringType_Name != null) && (curWiringType_Name.length() > 0) && ! (curWiringType_Name.startsWith("_"))) {
			buf.append(" / ");
			buf.append(curWiringType_Name);
		}
		//--------------------------------------------------------------------------------
		if(inPeerType && (curWiring_NameOrNames != null) && (curWiring_NameOrNames.length() > 0)) {
			buf.append(" / " + curWiring_NameOrNames);
		}
		//--------------------------------------------------------------------------------
		if(curLinkKindInfo != null && curLinkKindInfo.length() > 0) {
			buf.append(" / " + curLinkKindInfo);
			//--------------------------------------------------------------------------------
			if(curEntryType_Name != null && curEntryType_Name.length() > 0) {
				buf.append(" / " + curEntryType_Name);
			}
		}

		//--------------------------------------------------------------------------------
		if(curUsage != IContext.Usage.UNDEFINED){
			//--------------------------------------------------------------------------------
			// - for config: display also the config name (if set)
			// TBD: weak code ... but not dangerous ... keep up-to-date ...
			if(! util.Util.isEmptyString(curConfigName)) {
				buf.append(" / config = '" + curConfigName + "'");
			}
			//--------------------------------------------------------------------------------
			// - for peer props: display also peer name (if set)
			// -- nb: not possible for CONFIG__PEER__NAME, because at this point the name is not yet evaluated...
			if(curPeer_RawPeerName != null && curPeer_RawPeerName.length() > 0) {
				buf.append(" / peer = '" + curPeer_RawPeerName + "'");
				buf.append(" / peer type = '" + this.curPeerType_Name + "'");
			}
			//--------------------------------------------------------------------------------
			buf.append(" (" + usageToString().toLowerCase() + ")");
		}
		//--------------------------------------------------------------------------------
		return new String(buf + ": ");
	}

	//--------------------------------------------------------------------------------
	// for debug
	public String userVarsToString() {
		return this.curUserVarTypeMap.toString();
	} 

	//--------------------------------------------------------------------------------
	// for debug
	public String usageToString() {
		switch(curUsage) {
		//--------------------------------------------------------------------------------
		case PMMM_TYPE__PMMM_PROPS_TYPES:
			return("PMMM PROPS TYPES");
			//--------------------------------------------------------------------------------
		case ENTRY_TYPES:
			return("ENTRY TYPES");
		case ENTRY_TYPES__EPROPS_TYPES:
			return("EPROPS TYPES");
		case ENTRY_TYPES__EPROPS_DEFS:
			return("EPROPS DEFS");
			//--------------------------------------------------------------------------------
		case PEER_TYPE:
			return("PEER TYPE");
		case PEER_NAMES:
			return("PEER NAMES");
		case PPROPS_TYPES: 
			return("PPROPS TYPES");
		case PEER_TYPE__PPROPS_DEFS: 
			return("PPROPS DEFS OF PEER TYPE");
			//--------------------------------------------------------------------------------
		case WIRING: 
			return("WIRING");
		case WIRING_NAMES: 
			return("WIRING NAMES");
		case WIRING_TYPE: 
			return("WIRING TYPE");
		case WPROPS_TYPES:
			return("WPROPS TYPES");
		case WIRING_TYPE_AND_WIRING__WPROPS_DEFS: 
			return("WPROPS DEFS");
		case LINK__COUNT: 
			return("LINK");
		case LINK__QUERY: 
			return("QUERY");
		case LINK__VAR_PROP_SET_GET: 
			return("VAR/PROP SET/GET");
		case LINK__LPROPS_DEFS:
			return("LPROPS DEFS");
			//--------------------------------------------------------------------------------
		case CONFIG: 
			return("CONFIG");
		case PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS: 
			return("PMMM PROPS DEFS");
		case PEER_CONFIG: 
			return("PEER CONFIG");
		case PEER__PPROPS_DEFS: 
			return("PPROPS DEFS OF PEER");
			//--------------------------------------------------------------------------------
		case UNDEFINED:
			return("UNDEFINED");
		};
		return("???");
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
