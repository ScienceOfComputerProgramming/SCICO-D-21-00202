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

package codeGen.LaTeX;

import codeGen.*;
import pmmm.*;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// generate use case dsl xml code from a Peer Model Meta Model
//================================================================================
public class LaTeXCodeGen extends BasicCodeGen implements IBasicCodeGen {
	//--------------------------------------------------------------------------------
	// use xml replacements
	private util.replace.Director replacementsDirector = 
			new util.replace.Director(new util.replace.latex.LaTeXReplacements());
	//--------------------------------------------------------------------------------
	// my code writer
	protected codeGen.LaTeX.LaTeXCodeWriter codeWriter;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// dslXmlUseCaseDirFileName ... the directory name, where the compiled use cases are generated in
	// caution: do not use tracer in constructor!
	//--------------------------------------------------------------------------------
	public LaTeXCodeGen(PmmmInstance pmmmInstance, String targetPath, boolean writeAlsoToConsoleFlag) throws CodeGenException {
		//--------------------------------------------------------------------------------
		super(pmmmInstance, targetPath, writeAlsoToConsoleFlag);
		//--------------------------------------------------------------------------------
		this.codeWriter = new codeGen.LaTeX.LaTeXCodeWriter(useCaseNameAndConfigName, targetPath, replacementsDirector, writeAlsoToConsoleFlag);
		try {
			codeWriter.openFiles();
		} catch (CodeGenException e1) {
			throw e1;
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// CODE GENERATION 
	//================================================================================
	//--------------------------------------------------------------------------------
	// translation of pmmm 2 code
	public void generateUcCode() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// full use case name = pmmm name + config name 
		Pmmm2LaTeX fixCodeGen = new Pmmm2LaTeX(codeWriter, pmmm.getPmmmType());
		//--------------------------------------------------------------------------------
		// XML header
		// - nb: generate fus switch automatically to right file !!! keep this code as is !!!
		fixCodeGen.generateUseCaseHeader();
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(LaTeXDefs.LATEX_FILE_ID);
		//--------------------------------------------------------------------------------
		try {
			// gen code
			fixCodeGen.writeTheUseCase();
		} catch (SyntaxException e) {
			throw new CodeGenException("LaTeX code gen", m, e);
		} 
		// pass SNH exc
		//--------------------------------------------------------------------------------
		// close files
		codeWriter.closeFiles();
		//--------------------------------------------------------------------------------
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
