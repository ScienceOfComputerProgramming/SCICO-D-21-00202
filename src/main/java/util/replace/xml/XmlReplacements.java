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

package util.replace.xml;

import util.replace.*;


//================================================================================
// used for 2 directions of replacement;
// caution: they must be treated separately... do not unify them...
public class XmlReplacements extends Replacements implements IReplacements {
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public XmlReplacements() {
		init();
	}
	
	//================================================================================
	// INIT
	//================================================================================
	//--------------------------------------------------------------------------------
	// TBD: what else is missing?
	// TBD numeric symbols for these chars...
	private void init() {
		//================================================================================
		// direction 1 = string 2 latex:
		//================================================================================
		//--------------------------------------------------------------------------------
		// &	
		addX2StringReplacement("&amp;", "&");
		addX2StringReplacement("&#38;", "&");
		//--------------------------------------------------------------------------------
		// >
		addX2StringReplacement("&gt;", ">");
		addX2StringReplacement("&#62;", ">");
		//--------------------------------------------------------------------------------
		// <
		addX2StringReplacement("&lt;", "<");
		addX2StringReplacement("&#60;", "<");
		//--------------------------------------------------------------------------------
		// "
		addX2StringReplacement("&quot;", "\"");
		addX2StringReplacement("&#34;", "\"");
		//--------------------------------------------------------------------------------
		// '
		addX2StringReplacement("&apos;", "'");
		addX2StringReplacement("&#39;", "'");
		//--------------------------------------------------------------------------------
		// <blank>
		addX2StringReplacement("&nbsp;", " ");
		
		//================================================================================
		// direction 2 = latex 2 string: not needed
		//================================================================================
		//--------------------------------------------------------------------------------
		// CAUTION: do "&" first, because otherwise all already generated "&"s will be replaced....
		addString2XReplacement("&", "&amp;");
		addString2XReplacement(">", "&gt;");
		addString2XReplacement("<", "&lt;");
//		addString2XReplacement("\"", "&quot;"); // TBD
//		addString2XReplacement("'", "&apos;"); // TBD
//		// addString2XReplacement(" ", "&nbsp;"); // no good idea... do not replace blanks...
	}
	

} // END OF CLASS


//================================================================================
//EOF
//================================================================================

