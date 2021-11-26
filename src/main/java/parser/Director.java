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

import qa.tracer.O;
import qa.tracer.Tracer;
import qa.exceptions.*;

//================================================================================
// read PMMM from file and generate a "raw" PMMM where all expressions are raw and unevaluated strings
public class Director {
	//--------------------------------------------------------------------------------
	// for debugging
	Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	Parser parser;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Director(Parser parser) {
		this.parser = parser;
	}
	
	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setParser(Parser parser) {
		this.parser = parser;
	}
	
	//================================================================================
	// CALL THE RIGHT PARSER
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmmmComponents parse() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assertion that parser is initialized
		if(parser == null)
			throw new SNHException(490274, "parser not initialized", m);
		//--------------------------------------------------------------------------------
		// parse
		return parser.parse();
	}
	
	
} // END OF CLASS


//================================================================================
// EOF
//================================================================================
