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

//================================================================================
// TBD: CODE REVIEW !!! auch von Context !!!
public interface IContext {
	//--------------------------------------------------------------------------------
	// usage of context:
	// -- for parsing of what is the cur context used for by the evaluator/parser?
	public static enum Usage {
		//================================================================================
		// TYPES:
		//================================================================================
		//--------------------------------------------------------------------------------
		// PMMM:
		//--------------------------------------------------------------------------------
		PMMM_TYPE__PMMM_PROPS_TYPES,
		PMMM_TYPE_AND_PMMM__PMMM_PROPS_DEFS, 
		//--------------------------------------------------------------------------------
		// ENTRY:
		//--------------------------------------------------------------------------------
		ENTRY_TYPES,
		ENTRY_TYPES__EPROPS_TYPES,
		ENTRY_TYPES__EPROPS_DEFS,
		//--------------------------------------------------------------------------------
		// PEER:
		//--------------------------------------------------------------------------------
		PEER_TYPE,
		PEER_NAMES,
		PPROPS_TYPES,
		PEER_TYPE__PPROPS_DEFS, 
		//--------------------------------------------------------------------------------
		// WIRING:
		//--------------------------------------------------------------------------------
		WIRING, 
		WIRING_NAMES, 
		WIRING_TYPE, 
		WPROPS_TYPES,
		WIRING_TYPE_AND_WIRING__WPROPS_DEFS, 
		//--------------------------------------------------------------------------------
		// LINK:
		//--------------------------------------------------------------------------------
		LINK__COUNT, 
		LINK__QUERY, 
		LINK__VAR_PROP_SET_GET, 
		LINK__LPROPS_DEFS,
		//================================================================================
		// CONFIG:
		//================================================================================
		//--------------------------------------------------------------------------------
		CONFIG, 
		//--------------------------------------------------------------------------------
		// PMMM:
		//--------------------------------------------------------------------------------
		// - PMMM_PROPS_DEFS ... see above (PMMM TYPE)
		//--------------------------------------------------------------------------------
		// PEER CONFIG:
		//--------------------------------------------------------------------------------
		PEER_CONFIG, 
		PEER__PPROPS_DEFS, 
		// - TBD: PEER_AND_PEER_TYPE__PPROPS_DEFS ... see above (PEER TYPE)
		
		//================================================================================
		// not (yet) defined:
		//================================================================================
		//--------------------------------------------------------------------------------
		UNDEFINED
	};
}

//================================================================================
// EOF
//================================================================================
