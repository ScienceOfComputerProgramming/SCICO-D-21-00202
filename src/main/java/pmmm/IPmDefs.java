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
// CODE REVIEWS: 20210113 (eK);
//================================================================================

package pmmm;

//================================================================================
// peer space related names that are supported by the Peer Model;
// TBD: the name(s) to be used in the input are fixed here...
// respective parsers/code generators might allow also lower or Camel case - it is up to them;
// caution: if you add new ones -> check all occurrences manually...
public interface IPmDefs {
	//================================================================================
	// container names:
	//================================================================================
	//--------------------------------------------------------------------------------
	public static final String PIC = "PIC";
	public static final String POC = "POC";
	public static final String SIN = "SIN";
	public static final String SOUT = "SOUT";
	public static final String UNUSED = "UNUSED"; // TBD: for NOOP and CREATE
	//================================================================================
	// space operators used on links:
	//================================================================================
	//--------------------------------------------------------------------------------
	public static final String READ = "READ";
	public static final String COPY = "COPY";
	public static final String TAKE = "TAKE";
	public static final String MOVE = "MOVE";
	public static final String CREATE = "CREATE";
	public static final String TEST = "TEST";
	public static final String DELETE = "DELETE";
	public static final String REMOVE = "REMOVE";
	public static final String NOOP = "NOOP";
	//================================================================================
	// qualifiers
	//================================================================================
	//--------------------------------------------------------------------------------
	public static final String PEER_QUALIFIER = "PEER";
	public static final String PMMM_QUALIFIER = "PMMM";
	public static final String WIRING_QUALIFIER = "WIRING";
	//--------------------------------------------------------------------------------
	public static final String INDEX_QUALIFIER = "INDEX";
	public static final String PINDEX_QUALIFIER = "PINDEX";
	public static final String WINDEX_QUALIFIER = "WINDEX";
	
	
} // END OF INTERFACE


//================================================================================
//EOF
//================================================================================
