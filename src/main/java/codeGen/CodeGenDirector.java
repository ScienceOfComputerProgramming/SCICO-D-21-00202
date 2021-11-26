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
// CREATED:   January 2021 
//================================================================================
// CODE REVIEWS: 
//================================================================================

package codeGen;

import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// builder pattern for code generator and code writer 
public class CodeGenDirector {
	//--------------------------------------------------------------------------------
	// for debugging
	Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	BasicCodeGen codeGenerator;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public CodeGenDirector(BasicCodeGen codeGenerator) {
		this.codeGenerator = codeGenerator;
	}
	
	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setParser(BasicCodeGen codeGenerator) {
		this.codeGenerator = codeGenerator;
	}
	
	//================================================================================
	// CALL THE RIGHT CODE GENERATOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generate() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(codeGenerator == null)
			throw new SNHException(346785, "code generator not initialized", m);
		codeGenerator.generateUcCode();
	}
	
	
} // END OF CLASS


//================================================================================
// EOF
//================================================================================
