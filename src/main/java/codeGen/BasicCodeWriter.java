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
//CREATED:   January 2021 
//================================================================================

package codeGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import codeGen.PmDsl.PmDslDefs;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// WRITE CODE TO FILE
//================================================================================
// file management 
//--------------------------------------------------------------------------------
public abstract class BasicCodeWriter implements IBasicCodeWriter {
	//--------------------------------------------------------------------------------
	// debug
	/**/ protected Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	protected String useCaseAndConfigName = "";
	//--------------------------------------------------------------------------------
	// convention: path names always end with "/"
	protected String absoluteUcTargetPath = "";
	//--------------------------------------------------------------------------------
	boolean writeAlsoToConsoleFlag = true;
	//--------------------------------------------------------------------------------
	// files:
	protected File cur_File; 
	//--------------------------------------------------------------------------------
	// file writers:
	protected BufferedWriter cur_BufferedWriter;
	//--------------------------------------------------------------------------------
	// cur fileId
	protected int curFileId = -1; // undefined
	//--------------------------------------------------------------------------------
	// indentation
	protected String curInd = "";
	//--------------------------------------------------------------------------------
	// file name infos:
	protected String cur_FileName = "";

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// write generated code to file
	// if writeAlsoToConsoleFlag -> write it also to console 
	public BasicCodeWriter(String useCaseAndConfigName, String absoluteUcTargetPath, boolean writeAlsoToConsoleFlag) {	
		this.useCaseAndConfigName = useCaseAndConfigName;
		this.absoluteUcTargetPath = absoluteUcTargetPath;
		this.writeAlsoToConsoleFlag = writeAlsoToConsoleFlag;
	}

	//================================================================================
	// BASIC FILE FUs
	//================================================================================
	//--------------------------------------------------------------------------------
	// create dir
	public void createDir(String dirPath) throws CodeGenException {
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		File f;
		try {
			//--------------------------------------------------------------------------------
			// create "abstract" file object for dirPath to be created
			f = new File(dirPath);
		} catch (Exception e2) {
			throw new CodeGenException("ILL. DIRECTORY NAME \"" + dirPath + "\"" + "; " + e2.getMessage(), m);
		}
		//--------------------------------------------------------------------------------
		// create dir and check success
		// - obviously also returns true if dir already exists...
		if(! f.mkdir()) {
			/**/ tracer.println(dirPath + " exists already", Level.LO, m);  
		}
		//--------------------------------------------------------------------------------
	}
	
	//--------------------------------------------------------------------------------
	// creates file or opens it if it exists already
	// writes info that file is autogenerated
	// caution: sets cur_BufferedWriter to newly opened file
	public BufferedWriter openAutoFile(String fileName) throws CodeGenException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		BufferedWriter bw;
		/**/ tracer.println("CREATE/OPEN AUTO GEN FILE " + fileName, Level.LO, m);
		try {
			File f = new File(fileName); 			
			bw = new BufferedWriter(new FileWriter(f));
			cur_BufferedWriter = bw;
		} catch (Exception e) {
			throw new CodeGenException("CAN'T CREATE FILE: " + fileName + "; " + e.getMessage() + " " + e.getCause(), m);
		}
		/**/ tracer.println("ok: " + fileName, Level.NO, m);
		return bw;
	}	

	//================================================================================
	// BASIC WRITE FUs
	//================================================================================
	//--------------------------------------------------------------------------------
	// write code without indentation
	public void write(String text) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(cur_BufferedWriter == null) {
			/**/ throw new SNHException(984762, " Buffered Writer is null", m);
		}
		// write to console
		if(writeAlsoToConsoleFlag) {
			/**/ System.out.print(text);
		}
		try {
			// write to file
			cur_BufferedWriter.write(text);
		} catch (IOException e) {
			// TBD
			/**/ System.out.println("*** FILE WRITE ERROR " + e);
			e.printStackTrace();
		}
	}
	//--------------------------------------------------------------------------------
	// writeln code without indentation
	public void writeln(String code) throws SNHException {
		write(code);
		write("\n");
	}
	//--------------------------------------------------------------------------------
	// write code with indentation
	public void writeInd(String code) throws SNHException {
		write(curInd);
		write(code);
	}
	//--------------------------------------------------------------------------------
	// writeln code with indentation
	public void writelnInd(String code) throws SNHException {
		write(curInd);
		write(code);
		write("\n");
	}
	//--------------------------------------------------------------------------------
	// write nl() {
	public void nl() throws SNHException {
		write("\n");
	}

	//================================================================================
	// UTIL
	//================================================================================
	//--------------------------------------------------------------------------------
	public void incInd() {
		curInd = curInd.concat(PmDslDefs.TAB);
	}
	//--------------------------------------------------------------------------------
	public void decInd() throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(curInd.length() < codeGen.Go.GoDefs.TAB.length()) 
			/**/ throw new SNHException(999476, "ill. use of indentation; cant dec cur ind = " + curInd.length(), m);
		else
			curInd = curInd.substring(PmDslDefs.TAB.length());
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
