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

//================================================================================
// strings used to identify Peer Model artifacts in the xml file;
// these strings must align with the Peer Model shape definitions in drawio;
// TBD: align drawio names in the shapes with the java defines... marked with (*) below
// - is no problem as long as the names are unique...
public interface Defines {
	//--------------------------------------------------------------------------------
	// diagram name (parts)
	static final String DIAGRAM__PMMM_TYPE = "PmmmType";
	static final String DIAGRAM__PEER_TYPE_PREFIX = "PeerType";
	static final String DIAGRAM__ENTRY_TYPES = "EntryTypes";
	static final String DIAGRAM__WIRING_TYPES = "WiringTypes";
	static final String DIAGRAM__CONFIG_PREFIX = "Config";
	//--------------------------------------------------------------------------------
	// left side of marker
	static final String PMARTIFACT = "PMARTIFACT";
	//--------------------------------------------------------------------------------
	// pmmm type
	static final String PMMM_TYPE__SPECIFICATION = "PMMMTypeSpecification";
	static final String PMMM_TYPE__NAME = "PMMMTypeName";
	static final String PMMM_TYPE__PMMM_PROPS_TYPES = "PMMMTypeProps"; // (*)
	static final String PMMM_TYPE__PMMM_PROPS_DEFS = "PMMMTypePropsDefs"; // (*)
	//--------------------------------------------------------------------------------
	// entry type
	static final String ENTRY_TYPE__SPECIFICATION = "EntryTypeSpecification";
	static final String ENTRY_TYPE__NAME = "EntryTypeName";
	static final String ENTRY_TYPE__EPROPS_TYPES = "EntryTypeProps"; // (*)
	static final String ENTRY_TYPE__EPROPS_DEFS = "EntryTypePropsDefs"; // (*)
	//--------------------------------------------------------------------------------
	// peer type
	static final String PEER_TYPE__SPECIFICATION = "PeerTypeSpecification";
	static final String PEER_TYPE__NAME = "PeerTypeName";
	static final String PEER_TYPE__PPROPS_TYPES = "PeerTypeProps"; // (*)
	static final String PEER_TYPE__PPROPS_DEFS = "PeerTypePropsDefs"; // (*)
	//--------------------------------------------------------------------------------
	// wiring type
	static final String WIRING_TYPE__SPECIFICATION = "WiringTypeSpecification";
	static final String WIRING_TYPE__NAME = "WiringTypeName";
	static final String WIRING_TYPE__WPROPS_TYPES = "WiringTypeWPropsTypes";
	static final String WIRING_TYPE__SERVICE = "WiringTypeService";
	//--------------------------------------------------------------------------------
	// wiring
	static final String WIRING__SPECIFICATION = "WiringSpecification";
	static final String WIRING__NAME = "WiringName";
	static final String WIRING__SERVICE = "WiringService";
	static final String WIRING__WPROPS_DEFS = "WiringProps"; // (*)
	//--------------------------------------------------------------------------------
	// wiring config
	static final String WIRING_CONFIG__SPECIFICATION = "WiringConfigSpecification";
	static final String WIRING_CONFIG__NAME = "WiringConfigName";
	static final String WIRING_CONFIG__TYPE = "WiringConfigType";
	static final String WIRING_CONFIG__WPROPS_DEFS = "WiringConfigWPropsDefs"; 
	//--------------------------------------------------------------------------------
	// guard
	static final String NOOP_GUARD__SPECIFICATION = "NOOPGuardSpecification";
	static final String PIC_GUARD__SPECIFICATION = "PICGuardSpecification";
	static final String POC_GUARD__SPECIFICATION = "POCGuardSpecification";
	//................................................................................
	static final String GUARD__NUMBER = "GuardNumber";
	static final String GUARD__SPACE_OP = "GuardOp";
	static final String GUARD__ENTRY_TYPE = "GuardEntryType";
	static final String GUARD__ENTRY_COUNT = "GuardEntryCount";
	static final String GUARD__QUERY = "GuardQuery";
	static final String GUARD__VAR_PROP_SET_GET = "GuardVarPropSetGet";
	static final String GUARD__LPROPS_DEFS = "GuardLinkProps"; // (*)
	static final String GUARD__WIRING_CONNECTOR = "GuardWiringConnector";
	static final String GUARD__CONTAINER = "GuardContainer";
	//--------------------------------------------------------------------------------
	// action
	static final String NOOP_ACTION__SPECIFICATION = "NOOPActionSpecification";
	static final String PIC_ACTION__SPECIFICATION = "PICActionSpecification";
	static final String POC_ACTION__SPECIFICATION = "POCActionSpecification";
	//................................................................................
	static final String ACTION__NUMBER = "ActionNumber";
	static final String ACTION__SPACE_OP = "ActionOp";
	static final String ACTION__ENTRY_TYPE = "ActionEntryType";
	static final String ACTION__ENTRY_COUNT = "ActionEntryCount";
	static final String ACTION__QUERY = "ActionQuery";
	static final String ACTION__VAR_PROP_SET_GET = "ActionVarPropSetGet";
	static final String ACTION__LPROPS_DEFS = "ActionLinkProps"; // (*)
	static final String ACTION__WIRING_CONNECTOR = "ActionWiringConnector";
	static final String ACTION__CONTAINER = "ActionContainer";
	//--------------------------------------------------------------------------------
	// pmmm (config)
	static final String PMMM__SPECIFICATION = "PMMMSpecification";
	static final String PMMM__NAME = "PMMMName";
	static final String PMMM__PROPS_DEFS = "PMMMProps"; // (*)
	//--------------------------------------------------------------------------------
	// peer (config)
	static final String PEER__SPECIFICATION = "PeerSpecification";
	static final String PEER__NAME = "PeerName";
	static final String PEER__TYPE_NAME = "PeerType"; // (*)
	static final String PEER__PROPS_DEFS = "PeerProps"; // (*)
	//--------------------------------------------------------------------------------
	// beschriftung aka labeling
	static final String BESCHRIFTUNG = "Beschriftung";	
	//--------------------------------------------------------------------------------

} // END OF INTERFACE


//================================================================================
// EOF
//================================================================================
