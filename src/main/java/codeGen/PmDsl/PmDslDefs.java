//================================================================================
// Peer Model Tool Chain
// Copyright (C) 2021 Eva Maria Kuehn
//--------------------------------------------------------------------------------
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
// 
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//================================================================================
//SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
//AUTHOR:    Eva Maria Kuehn
//CREATED:   December 2020 
//================================================================================

package codeGen.PmDsl;

//================================================================================
// useful defines for code generation
public interface PmDslDefs {
	//--------------------------------------------------------------------------------
	// err msg prefix
	static final String ERR_CANNOT_GEN_CODE_FOR = "*** ERROR: CANNOT GENERATE CODE FOR ";
	//--------------------------------------------------------------------------------
	// file id
	public final int DSL_XML_FILE_ID = 3;
	//--------------------------------------------------------------------------------
	// xml element names
	static final String ACTION__ELEMENT_NAME = "Action"; // TBD; hard coded...
	static final String CONFIG__ELEMENT_NAME = "Config";
	static final String CONFIGS__ELEMENT_NAME = "Configurations";
	static final String ENTRY_TYPE__ELEMENT_NAME = "EntryType";
	static final String GUARD__ELEMENT_NAME = "Guard"; // TBD; hard coded...
	static final String PEER__ELEMENT_NAME = "Peer";
	static final String PEER_TYPE__ELEMENT_NAME = "PeerType";
	static final String PMMM__ELEMENT_NAME = "PMMM";
	static final String PROPS_DEFINITION__ELEMENT_NAME = "PropsDefinition";
	static final String PROPS_DECLARATION__ELEMENT_NAME = "PropsDeclaration";
	static final String QUERY__ELEMENT_NAME = "Query";
	static final String TYPES__ELEMENT_NAME = "Types";
	static final String VAR_PROPS_SET_GET__ELEMENT_NAME = "VarPropsSetGet";
	static final String WIRING__ELEMENT_NAME = "Wiring";
	//--------------------------------------------------------------------------------
	// xml attribute names
	static final String CONTAINER__ATTRIBUTE_NAME = "container";
	static final String COUNT__ATTRIBUTE_NAME = "count";
	static final String ENTRY_TYPE__ATTRIBUTE_NAME = "entryType";
	static final String NAME__ATTRIBUTE_NAME = "name";
	static final String NUMBER__ATTRIBUTE_NAME = "number";
	static final String SPACE_OP__ATTRIBUTE_NAME = "op";
	static final String SERVICE__ATTRIBUTE_NAME = "service";
	static final String TYPE__ATTRIBUTE_NAME = "type";
	//--------------------------------------------------------------------------------
	// useful things for code writing
	static final String TAB = "    ";
	static final String COMMA = ", ";
	static final String LB = "(";
	static final String RB = ")";
	//--------------------------------------------------------------------------------

	
} // END OF INTERFACE


//================================================================================
// EOF
//================================================================================
