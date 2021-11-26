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

package codeGen.Go;

import java.nio.file.*;
import java.io.BufferedWriter;

import java.io.File;
import java.io.IOException;

import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.tracer.O;

//================================================================================
// WRITE CODE TO FILE
//================================================================================
// file management for the go code
// - following a naming convention
// - let ucname be PMMM Type name
// - creates the following directories & files:
// <ucname>                 ... dir
//   test                   ... dir
//     myuc_test.go         ... file with fixed go code
//   use_case               ... dir
//     testcase.go          ... file with fixed go code
//     use_case_<ucname>.go ... go code generated from the PMMM !!! 
//--------------------------------------------------------------------------------
public class GoCodeWriter extends codeGen.BasicCodeWriter implements codeGen.IBasicCodeWriter {
	//--------------------------------------------------------------------------------
	// /**/ tracer.println("xxx", Level.DEBUG, m);
	//--------------------------------------------------------------------------------
	// files:
	// - nb: the semantics of the (bit wired) file names correlates with go maton convention
	File useCase_File; 
	File testcase_File; 
	File test_File; 
	//--------------------------------------------------------------------------------
	// file writers:
	BufferedWriter useCase_BufferedWriter;
	BufferedWriter testcase_BufferedWriter;
	BufferedWriter test_BufferedWriter;
	//--------------------------------------------------------------------------------
	// cur fileId
	int curFileId = -1; // undefined
	//--------------------------------------------------------------------------------
	// indentation
	String curInd = "";
	//--------------------------------------------------------------------------------
	// file name infos:
	String useCase_FileName = "";
	String test_FileName = "";
	String testcase_FileName = "";
	//--------------------------------------------------------------------------------
	// for writing code to respective file
	protected codeGen.Go.GoCodeWriter codeWriter;
	//--------------------------------------------------------------------------------
	String relativeUcPath = "";

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public GoCodeWriter(String useCaseAndConfigName, String relativeUcPath, String absoluteUcTargetPath, boolean writeAlsoToConsoleFlag) {	
		super(useCaseAndConfigName, absoluteUcTargetPath, writeAlsoToConsoleFlag);
		this.relativeUcPath = relativeUcPath;
	}

	//================================================================================
	// CREATE/OPEN DIRECTORIES/FILES FOR GO CODE GENERATION
	//================================================================================
	//--------------------------------------------------------------------------------
	// create & open all needed files
	//--------------------------------------------------------------------------------
	public void openFiles() throws SNHException, CodeGenException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local vars
		String what = "";
		String fileName = "";
		//--------------------------------------------------------------------------------
		try {
			//================================================================================
			// CREATE DIRECTORIES
			//================================================================================
			//--------------------------------------------------------------------------------
			// <MYUC> ... full name of my use case
			String myuc_Dir = absoluteUcTargetPath + useCaseAndConfigName;
			String myuc_Path = myuc_Dir + "/";
			// create also intermediadiary directories
			java.nio.file.Files.createDirectories(Paths.get(myuc_Dir));
			//--------------------------------------------------------------------------------
			// <MYUC>/test
			String test_Dir = myuc_Path + "test";
			String test_Path = test_Dir + "/";
			// create also intermediadiary directories
			java.nio.file.Files.createDirectories(Paths.get(test_Dir));
			//--------------------------------------------------------------------------------
			// <MYUC>/use-case
			String useCase_Dir = myuc_Path + "use-case";
			String useCase_Path = useCase_Dir + "/";
			// create also intermediadiary directories
			java.nio.file.Files.createDirectories(Paths.get(useCase_Dir));

			//================================================================================
			// CREATE FILES & SET FILE NAMES 
			//================================================================================
			//--------------------------------------------------------------------------------
			// test/<useCaseAndConfigName>_test.go
			what = "test file";
			fileName = test_Path + "" + useCaseAndConfigName + "_test.go";
			test_FileName = fileName;
			test_BufferedWriter = openAutoFile(test_FileName); 
			//--------------------------------------------------------------------------------
			// use_case/use_case_<useCaseAndConfigName>.go
			what = "use-case file";
			fileName = useCase_Path + "use-case_" + useCaseAndConfigName + ".go";
			useCase_FileName = fileName;
			useCase_BufferedWriter = openAutoFile(useCase_FileName); 
			//--------------------------------------------------------------------------------
			// use_case/testcase.go
			what = "testcase file";
			fileName = useCase_Path + "testcase" + ".go";
			testcase_FileName = fileName;
			testcase_BufferedWriter = openAutoFile(testcase_FileName); 
			//--------------------------------------------------------------------------------
			cur_BufferedWriter = null;
		} catch (IOException e) {
			throw new CodeGenException("FILE ERROR " + what + " '" + fileName + "'; " + e.getMessage(), m);
		} catch (CodeGenException e) {
			// do not try to close files... causes exception
			throw new SNHException(255355, "FILE ERROR " + what + " '" + fileName + "'; ", m, e);
		}
	}

	//================================================================================
	// SWITCH FILE
	//================================================================================
	//--------------------------------------------------------------------------------
	// switch to file id (see defines)
	public void switch2File(int fileId) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		switch(fileId) {
		case codeGen.Go.GoDefs.TEST_FILE_ID:
			cur_BufferedWriter = test_BufferedWriter;
			break;
		case codeGen.Go.GoDefs.TESTCASE_FILE_ID:
			cur_BufferedWriter = testcase_BufferedWriter;
			break;
		case codeGen.Go.GoDefs.USE_CASE_FILE_ID:
			cur_BufferedWriter = useCase_BufferedWriter;
			break;
		default:
			throw new SNHException(111789, "ill. fileId = " + fileId, m);
		}			
		curFileId = fileId;
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// return info about file id (see defines)
	public String fileId2Info(int fileId) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		switch(fileId) {
		case codeGen.Go.GoDefs.TEST_FILE_ID:
			return(test_FileName);
		case codeGen.Go.GoDefs.TESTCASE_FILE_ID:
			return(testcase_FileName);
		case codeGen.Go.GoDefs.USE_CASE_FILE_ID:
			return(useCase_FileName);
		default:
			throw new SNHException(811677, "ill. fileId = " + fileId, m);
		}			
	}

	//================================================================================
	// CLOSE ALL FILES
	//================================================================================
	// close files
	public void closeFiles() throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			if(useCase_BufferedWriter != null)
				useCase_BufferedWriter.close();
			if(testcase_BufferedWriter != null)
				testcase_BufferedWriter.close();
			if(test_BufferedWriter != null)
				test_BufferedWriter.close();
		} catch (IOException e) {
			throw new SNHException(878877, "can't close files", m);
		}
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
