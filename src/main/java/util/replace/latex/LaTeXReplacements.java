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

package util.replace.latex;

import util.replace.*;

//================================================================================
// used for 2 directions of replacement;
// caution: they must be treated separately... do not unify them...
public class LaTeXReplacements extends Replacements implements IReplacements {

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public LaTeXReplacements() {
		init();
	}
	
	//================================================================================
	// INIT
	//================================================================================
	//--------------------------------------------------------------------------------
	// TBD: what else is missing?
	public void init() {
		//================================================================================
		// direction 1 = string 2 latex:
		//================================================================================
		//--------------------------------------------------------------------------------
		addString2XReplacement("==", "\\tteqs\\tteqs ");
		addString2XReplacement("<=", "\\ttlab\\tteqs ");
		addString2XReplacement(">=", "\\ttrab\\tteqs ");
		addString2XReplacement("\"", "\\ttdqt ");
		addString2XReplacement("$", "\\ttdlr ");
		addString2XReplacement("=", "\\tteqs ");
		addString2XReplacement("<", "\\ttlab ");
		addString2XReplacement(">", "\\ttrab ");
		addString2XReplacement("EXCEPTION_WRAP", "exception"); // caution: must be done before "_" is replaced
		addString2XReplacement("_", "\\ttusc ");
		// - these might occur in user strings
		addString2XReplacement("&", "\\& ");
		addString2XReplacement("%", "\\% ");
		addString2XReplacement("#", "\\# ");

		//================================================================================
		// direction 2 = latex 2 string: not needed
		//================================================================================
		//--------------------------------------------------------------------------------
	}

	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================

