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
// SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
// AUTHOR:    Eva Maria Kuehn
// CREATED:   December 2020 
//================================================================================

package eval;


//================================================================================
// CAUTION: check for all usages if you change these defines....
public interface IData {

	//--------------------------------------------------------------------------------
	// basic data types
	// - nb: FLEX means 'not yet determined'; it is used only by preprocessor
	// -- eg: for BOP where left and right arg type can have different types 
	// -- eg: for peer and wiring names 
	// array ....... INTS, STRINGS, URLS, BOOLEANS
	// array_ref ... array access with '#'
	// DELAYED means that type could not be evaluated yet, namely for nested '.' and '#' expressions like
	// -- PMMM.(players # (WINDEX.1)) ... here the type of "players" cannot be evaluated
	// -- this must be done by mta then when resolving the qualifier values
	public enum Type {
		// above types
		STRING, URL, INT, BOOLEAN, FLEX, 
		STRING_ARRAY, URL_ARRAY, INT_ARRAY, BOOLEAN_ARRAY, FLEX_ARRAY,
		STRING_ARRAY_REF, URL_ARRAY_REF, INT_ARRAY_REF, BOOLEAN_ARRAY_REF, FLEX_ARRAY_REF,
		DELAYED,
		UNDEFINED
	};

}


//================================================================================
//EOF
//================================================================================