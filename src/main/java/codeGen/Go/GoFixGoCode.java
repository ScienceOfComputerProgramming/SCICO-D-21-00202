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

package codeGen.Go;

import qa.exceptions.SNHException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

//================================================================================
// automatically generate go code
// - nb: most parts are fixed
// - in some parts my use case name needs to be inserted (all marked with "<<<<<<")
public class GoFixGoCode {	
	//--------------------------------------------------------------------------------
	// for writing code to respective file
	GoCodeWriter codeWriter;
	//--------------------------------------------------------------------------------
	private final String NL = "\n";
	//--------------------------------------------------------------------------------
	private String relativeUcPath = "";
	private String useCaseAndConfigName = "";
	private String dir = "";

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// goUseCaseDirFileName ... the directory name under "src" in go automaton, where the compiled use cases are generated in
	public GoFixGoCode(GoCodeWriter codeWriter, String relativeUcPath, String absoluteUcTargetPath, String useCaseAndConfigName, String dir) {
		this.relativeUcPath = relativeUcPath;
		this.useCaseAndConfigName = useCaseAndConfigName;
		this.codeWriter = codeWriter;
		// local dir in go path struct... eg "useCases" where the use cases are found under "src"
		this.dir = dir;
	}

	//================================================================================
	// GO Module File Generation
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateModuleFile() throws SNHException {
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(GoDefs.MODULE_FILE_ID);
		//--------------------------------------------------------------------------------
		// write code
		codeWriter.write(""
				+      "module useCases"
				+NL+   ""
				+NL+   "go 1.14"
				+NL+   ""
				+NL+   "require github.com/peermodel/simulator v0.1.0"
				+NL+   ""
				+NL+   "");
	}

	//================================================================================
	// USE CASE FILE PARTS
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateUseCaseHeader() throws SNHException {
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(GoDefs.USE_CASE_FILE_ID);
		//--------------------------------------------------------------------------------
		// write code
		writeAutoFileHeader(GoDefs.USE_CASE_FILE_ID);
		codeWriter.write(""
				+      "//////////////////////////////////////////////////////////////"
				+NL+   "// System: PMMM Use Case Go Code for Peer Model State Machine"
				+NL+   "// Author: Eva Maria Kuehn"
				+NL+   "// Date:   2015; 2021" 																			
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "package pmUseCases " 
				+NL+   ""
				+NL+   "import ( " 
				+NL+   "	. \"github.com/peermodel/simulator/controller\" "
				+NL+   "	. \"github.com/peermodel/simulator/debug\" "
				+NL+   "	. \"github.com/peermodel/simulator/pmModel\" "
				+NL+   "	. \"github.com/peermodel/simulator/scheduler\" "
				+NL+   "	\"fmt\" " 
				+NL+   ") "
				+NL+   ""
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   "// type"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// config: empty for auto generated code, but needed by go automaton"
				+NL+   "type UseCase" + useCaseAndConfigName + " struct { "
				+NL+   "}"
				+NL+   ""
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   "// constructor"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// alloc new use case"
				+NL+   "// if use case is manually coded, vars could be shared here by the use case peers "
				+NL+   "func NewUseCase" + useCaseAndConfigName + "() *UseCase" + useCaseAndConfigName + " { // <<<<<<"
				+NL+   "	uc := new(UseCase" + useCaseAndConfigName + ")  // <<<<<<"
				+NL+   "	return uc" 
				+NL+   "}"
				+NL+   ""
				+NL+   "");
	}

	//--------------------------------------------------------------------------------
	public void generateUseCaseInit() throws SNHException {
		//--------------------------------------------------------------------------------
		// switch file
		// - not really needed...
		codeWriter.switch2File(GoDefs.USE_CASE_FILE_ID);
		//--------------------------------------------------------------------------------
		// write code
		codeWriter.write(""
				+      "//////////////////////////////////////////////////////////////"
				+NL+   "// Init use case: create and write INIT entry into all peer's PICs"
				+NL+   "////////////////////////////////////////////////////////////// "
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// create & send an INIT entry to all user peers"
				+NL+   "// - nb: this is a convention introduced by the Peer Model tool-chain"
				+NL+   "func (uc *UseCase" + useCaseAndConfigName + ") Init(ps *PeerSpace, scheduler *Scheduler, controllerChannel ControllerChannel) {"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "	// create INIT entry (without FID)"
				+NL+   "	initE := NewEntry(\"INIT\")"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "	// write it to the PIC of of all user peers"
				+NL+   "    for _, p := range ps.Peers {"
				+NL+   "       if p.Id == \"IOP\" || p.Id == \"Stop\" {"
				+NL+   "           continue"
				+NL+   "       }"
				+NL+   "       ps.Write(p.Pic, initE, nil /* no vars */, scheduler)"
				+NL+   "    }"
				+NL+   "}" 
				+NL+   ""
				+NL+   "");
	}

	//--------------------------------------------------------------------------------
	public void generateBuiltInServices() throws SNHException {
		//--------------------------------------------------------------------------------
		// switch file
		// - not really needed...
		codeWriter.switch2File(GoDefs.USE_CASE_FILE_ID);
		//--------------------------------------------------------------------------------
		// write code
		codeWriter.write("" 
				+      "//////////////////////////////////////////////////////////////"
				+NL+   "// Built-in Service \"Consume\" "
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// print all entries of the wiring container and remove them"
				+NL+   "func Consume(ps *PeerSpace, wfid string, vars Vars, scheduler *Scheduler, inCid string, outCid string, controllerChannel ControllerChannel) {"	
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // print service name and cid of container"
				+NL+   "    String2TraceFile(\"\\n\")"
				+NL+   "    String2TraceFile(fmt.Sprintf(\"%s: CONSUME (time = %d):\\n\", inCid, CLOCK))" 
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // take all entries from the wiring container and print them"
				+NL+   "    for {"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // take entry"
				+NL+   "        entry := ps.Take(inCid, \"*\", nil /* no selector */, vars)"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // done?"
				+NL+   "        if nil == entry {"	
				+NL+   "            break"
				+NL+   "        }"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // print entry"
				+NL+   "        /**/ String2TraceFile(\"  CONSUMED ENTRY = \")"
				+NL+   "        /**/ entry.Println(0 /* ind */)"
				+NL+   "    }"
				//+NL+ "    /**/ String2TraceFile(\"\\n\")" 
				+NL+   "}" 
				+NL+   ""
				+NL+   "");
		//--------------------------------------------------------------------------------
		// write code
		codeWriter.write(""
				+      "//////////////////////////////////////////////////////////////"
				+NL+   "// Built-in Service \"Watch\" "
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// print all entries of the wiring container"
				+NL+   "func Watch(ps *PeerSpace, wfid string, vars Vars, scheduler *Scheduler, inCid string, outCid string, controllerChannel ControllerChannel) {"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // print service name and cid of container"
				+NL+   "    String2TraceFile(\"\\n\")"
				+NL+   "    String2TraceFile(fmt.Sprintf(\"%s: WATCH (time = %d):\\n\", inCid, CLOCK))"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // take all entries from the wiring container, print them and emit them back"
				+NL+   "    for {"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // take entry"
				+NL+   "        entry := ps.Take(inCid, \"*\", nil /* no selector */, vars)"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // done?"
				+NL+   "        if nil == entry {"
				+NL+   "            break"
				+NL+   "        }"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // print entry"
				+NL+   "        /**/ String2TraceFile(\"    \")"
				+NL+   "        /**/ entry.Println(0 /* ind */)"
				+NL+   "        //------------------------------------------------------------"
				+NL+   "        // emit entry back to wiring container"
				+NL+   "        ps.Emit(outCid, entry, vars, scheduler)"
				+NL+   "    }"	
				//+NL+ "    /**/ String2TraceFile(\"\\n\")"
				+NL+   "}"
				+NL+   ""
				+NL+   "");
	}


	//================================================================================
	// TEST FILE
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateTestFile() throws SNHException {
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(GoDefs.TEST_FILE_ID);
		//--------------------------------------------------------------------------------
		// write code
		writeAutoFileHeader(GoDefs.TEST_FILE_ID);
		codeWriter.write(""
				+      "//////////////////////////////////////////////////////////////"
				+NL+   "// System: Peer Model State Machine"
				+NL+   "// Author: Eva Maria Kuehn" 
				+NL+   "// Date:   2015; 2021"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "package pmsm_test "
				+NL+   ""
				+NL+   "import ( "
				+NL+   "    . \"github.com/peermodel/simulator/config\" "
				+NL+   "    . \"github.com/peermodel/simulator/debug\" "
				+NL+   "    . \"github.com/peermodel/simulator/framework\" "
				+NL+   "    . \"github.com/peermodel/simulator/pmAutomata\" "
				+NL+   "    . \"github.com/peermodel/simulator/pmModel\" "
				+NL+   "    . \"github.com/peermodel/simulator/runtime\" "
				+NL+   "    \"fmt\" "
				+NL+   "    \"testing\" "
				+NL+   "	. \"" + dir + "/" + relativeUcPath + useCaseAndConfigName + "/use-case\" // <<<<<< "
				+NL+   ")"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// just any number"
				+NL+   "const TEST_CASE_ID TestCaseIdEnum = 123456 "
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// call: go test -bench=." 
				+NL+   "func Benchmark_PeerModel_TestCase_1(b *testing.B) {"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "	// init the overall shared system status for the test case" 
				+NL+   "	s := testPreparation()"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // set fu pointer to re-create status for simulation runs"
				+NL+   "	s.InitAppUseCaseFu = testPreparation"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // start"
				+NL+   "	for i := 0; i < b.N; i++ {"
				+NL+   "		Run(s, ActualTestCase.TestCaseId.String(), ActualTestCase.LatexConfig, SYSTEM_TTL)"
				+NL+   "	}" 
				+NL+   "}"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// call: go test"
				+NL+   "func Test_PeerModel_TestCase(t *testing.T) {"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "	// init the overall shared system status for the test case" 
				+NL+   "	s := testPreparation()"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // set fu pointer to re-create status for simulation runs"
				+NL+   "	s.InitAppUseCaseFu = testPreparation"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // start"
				+NL+   "	Run(s, ActualTestCase.TestCaseId.String(), ActualTestCase.LatexConfig, SYSTEM_TTL)"
				+NL+   "}" 
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// create and init the overall shared system status:"
				+NL+   "// - add meta model for system peers and use case"
				+NL+   "// - init the runtime model and the test case"
				+NL+   "// - start all machines"
				+NL+   "func testPreparation() *Status {"
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// create status that is shared between all machines"
				+NL+   "	// - adds slot at system ttl to tell scheduler to shut down if system ttl has expired"
				+NL+   "	s := NewStatus(SYSTEM_TTL, NewMetaContext())"
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// create new test case and get its name"
				+NL+   "	// - alloc"
				+NL+   "	ActualTestCase = NewTestCase(TEST_CASE_ID)"
				+NL+   "	// - get test case id"
				+NL+   "	testCaseName := ActualTestCase.TestCaseId.String()"
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// add meta model"
				+NL+   "	// - for the use case"
				+NL+   "	// - for all system peers needed by the use case"
				+NL+   "	// to the PeerSpace which is contained in the meta context of status"	
				+NL+   "	// - (casted to the MetaContext struct of the pmModel package)"
				+NL+   "	AddMetaModelForTestCaseAndSystemPeers(s.MetaContext.(*MetaContext).PeerSpace, ActualTestCase)"
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// check the plausibility of the meta model"
				+NL+   "	// - raises user error upon failure"
				+NL+   "	// - nb: test case name is passed only for docu/debug puposes"
				+NL+   "	s.MetaContext.(*MetaContext).PeerSpace.MetaModelCheck(testCaseName)" 
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// init the runtime model" 
				+NL+   "	// - create the peer model automata generator"
				+NL+   "	a := NewPeerModelAutomataGenerator()"
				+NL+   "	// - create containers"
				+NL+   "	a.CreateContainers4RuntimeModel(s)"
				+NL+   "	// - start all wirings"
				+NL+   "	a.StartWiringMachines4RuntimeModel(s)"
				+NL+   "	//------------------------------------------------------------" 
				+NL+   "	// debug"
				+NL+   "	if RUN_TRACE.DoTrace() { // DEBUG"
				+NL+   "		/**/ String2TraceFile(fmt.Sprintf(\"init use case %s\\n\", testCaseName)) // DEBUG"
				+NL+   "	} // DEBUG"
				+NL+   "	//------------------------------------------------------------" 
				+NL+   "	// init the test case"
				+NL+   "	// - i.e. inject start entries for the test case"
				+NL+   "	ActualTestCase.Init(s.MetaContext.(*MetaContext).PeerSpace, &s.Scheduler, s.ControllerChannel)"
				+NL+   "	//------------------------------------------------------------" 
				+NL+   "	// debug"
				+NL+   "	if RUN_TRACE.DoTrace() { // DEBUG"
				+NL+   "		/**/ String2TraceFile(\"use case init done\\n\") // DEBUG"
				+NL+   "	} // DEBUG"
				+NL+   "	//------------------------------------------------------------" 
				+NL+   "    // return"
				+NL+   "    return s"
				+NL+   "}"
				+NL+   ""	
				+NL+   "");
		writeAutoFileTail();
	}

	//================================================================================
	// TESTCASE FILE
	//================================================================================
	public void generateTestCaseFile() throws SNHException {
		//--------------------------------------------------------------------------------
		// switch file
		codeWriter.switch2File(GoDefs.TESTCASE_FILE_ID);
		//--------------------------------------------------------------------------------
		// generate use case id
		String ucId = "USE_CASE_" + relativeUcPath + useCaseAndConfigName;
		// replace possible "/" by "_"
		ucId = ucId.replace('/', '_');
		ucId = ucId.replace('\\', '_');
		//--------------------------------------------------------------------------------
		// write code
		writeAutoFileHeader(GoDefs.TESTCASE_FILE_ID);
		codeWriter.write(""
				+      "//////////////////////////////////////////////////////////////" 
				+NL+   "// System: Peer Model State Machine" 
				+NL+   "// Author: Eva Maria Kuehn"
				+NL+   "// Date:   2015, 2016; 2021"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "package pmUseCases"
				+NL+   ""
				+NL+   "import ("
				+NL+   "	. \"github.com/peermodel/simulator/config\" "
				+NL+   "	. \"github.com/peermodel/simulator/controller\" "
				+NL+   "	. \"github.com/peermodel/simulator/debug\" "
				+NL+   "	. \"github.com/peermodel/simulator/latex\" "
				+NL+   "	. \"github.com/peermodel/simulator/pmModel\" "
				+NL+   "	. \"github.com/peermodel/simulator/scheduler\" "
				+NL+   ") " 
				+NL+   ""
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   "// types and consts"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "type UseCaseIdEnum int "
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// just any number"
				+NL+   "const " + ucId + " UseCaseIdEnum = 123456"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "type TestCaseIdEnum int "
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// interface type "
				+NL+   "type TestCase struct { "
				+NL+   "	UseCaseId  UseCaseIdEnum "
				+NL+   "	TestCaseId TestCaseIdEnum "
				+NL+   "	// name of required system peers: key = IOP_PEER, STOP_PEER "
				+NL+   "	SystemPeers map[string]bool "
				+NL+   "	// current use case " 
				+NL+   "	UseCase" + useCaseAndConfigName + "  // <<<<<< "
				+NL+   "	// config for latex docu "
				+NL+   "	LatexConfig "
				+NL+   ""
				+NL+   "} "
				+NL+   ""
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   "// global var"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "// *the* actual test case"
				+NL+   "var ActualTestCase *TestCase = nil "
				+NL+   ""
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   "// constructor"
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   ""
				+NL+   "//------------------------------------------------------------"
				+NL+   "func NewTestCase(testCaseId TestCaseIdEnum) *TestCase { "
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // alloc test case"
				+NL+   "	tc := new(TestCase) "
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // set all fields"
				+NL+   "    // - TestCaseId"
				+NL+   "	tc.TestCaseId = testCaseId "
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // - SystemPeers"
				+NL+   "	tc.SystemPeers = map[string]bool{} "
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // NewLatexConfig"
				+NL+   "	tc.LatexConfig = NewLatexConfig() " 
				+NL+   "    //------------------------------------------------------------"
				+NL+   "	// unused for auto generated code"
				+NL+   "	tc.UseCase" + useCaseAndConfigName + " = *NewUseCase" + useCaseAndConfigName + "()  // <<<<<<<< " 
				+NL+   "    //------------------------------------------------------------"
				+NL+   "	// by default: use all system peers "
				+NL+   "	tc.SystemPeers[IOP_PEER] = true " 
				+NL+   "	tc.SystemPeers[STOP_PEER] = true "
				+NL+   "	tc.UseCaseId = " + ucId + "  // <<<<<<<<<"
				+NL+   "    //------------------------------------------------------------"
				+NL+   "    // return test case"
				+NL+   "	return tc "
				+NL+   "} "
				+NL+   ""
				+NL+   "//////////////////////////////////////////////////////////////"
				+NL+   "// methods"
				+NL+   "//////////////////////////////////////////////////////////////" 
				+NL+   ""
				+NL+   "//============================================================" 
				+NL+   "// add meta model to the peer space" 
				+NL+   "// - for all needed system peers" 
				+NL+   "// - for the test case"
				+NL+   "func AddMetaModelForTestCaseAndSystemPeers(ps *PeerSpace, tc *TestCase) { "
				+NL+   ""
				+NL+   "	//------------------------------------------------------------" 
				+NL+   "	// add meta model for IOP peer, if needed for the uc: "
				+NL+   "	if tc.SystemPeers[IOP_PEER] { "
				+NL+   "		if INIT_TRACE.DoTrace() { "
				+NL+   "			/**/ String2TraceFile(\"add meta model for iop peer\\n\") " 
				+NL+   "		} "
				+NL+   "		ps.AddMetaModel_IOP_PEER(WProps{ " 
				+NL+   "			TTL:          IVal(SYSTEM_TTL), "
				+NL+   "			TXCC:         SVal(PCC), " 
				+NL+   "			MAX_THREADS:  IVal(1), "
				+NL+   "			REPEAT_COUNT: IVal(INFINITE)}) "
				+NL+   "	} "
				+NL+   ""
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// add meta model for Stop peer, if needed for the uc:"
				+NL+   "	if tc.SystemPeers[STOP_PEER] {"
				+NL+   "		if INIT_TRACE.DoTrace() {"
				+NL+   "			/**/ String2TraceFile(\"add meta model for Stop peer\\n\")"
				+NL+   "		}"
				+NL+   "		ps.AddMetaModel_STOP_PEER(WProps{"
				+NL+   "			TTL:          IVal(SYSTEM_TTL),"
				+NL+   "			TXCC:         SVal(PCC),"
				+NL+   "			MAX_THREADS:  IVal(1),"
				+NL+   "			REPEAT_COUNT: IVal(INFINITE)})"
				+NL+   "	}"
				+NL+   "	//------------------------------------------------------------"
				+NL+   "	// add meta model for test case:"
				+NL+   "	if INIT_TRACE.DoTrace() {"
				+NL+   "		/**/ String2TraceFile(\"add meta model for test case\\n\")"
				+NL+   "	}"
				+NL+   "	tc.AddMetaModel(ps)"
				+NL+   "}"	
				+NL+   ""
				+NL+   "//============================================================"
				+NL+   "// init test case:"
				+NL+   "func (tc *TestCase) Init(ps *PeerSpace, scheduler *Scheduler, controllerChannel ControllerChannel) {"
				+NL+   "	tc.UseCase" + useCaseAndConfigName + ".Init(ps, scheduler, controllerChannel)  // <<<<<<<"
				+NL+   "}"	
				+NL+   ""
				+NL+   "//============================================================"
				+NL+   "// add meta model for test case to status:"
				+NL+   "func (tc *TestCase) AddMetaModel(ps *PeerSpace) {"	
				+NL+   "	tc.UseCase" + useCaseAndConfigName + ".AddMetaModel(ps)  // <<<<<<"
				+NL+   "}"
				+NL+   ""
				+NL+   "//============================================================"
				+NL+   "func (id UseCaseIdEnum) String() string {"
				+NL+   "	return \"USE_CASE_" + useCaseAndConfigName + "\"  // <<<<<<<"
				+NL+   "}"
				+NL+   ""
				+NL+   "//============================================================"
				+NL+   "func (id TestCaseIdEnum) String() string {"
				+NL+   "	return \"TEST_CASE_" + useCaseAndConfigName + "\"  // <<<<<<<"
				+NL+   "}"
				+NL+   ""
				+NL+   "//============================================================"
				+NL+   ""
				+NL+   "");
		writeAutoFileTail();
	}


	//================================================================================
	// FILE HEADER & TAIL
	//================================================================================
	//--------------------------------------------------------------------------------
	// write header info that this code is autogenerated
	public void writeAutoFileHeader(int fileId) throws SNHException {
		//--------------------------------------------------------------------------------
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//--------------------------------------------------------------------------------
		codeWriter.writeln("//************************************************************");
		codeWriter.writeln("//************************************************************");
		codeWriter.writeln("//** AUTO GENERATED FILE START -- DO NOT CHANGE !!!");
		codeWriter.writeln("//************************************************************");
		codeWriter.writeln("//** Copyright: Eva Maria Kuehn (C) 2021");
		codeWriter.writeln("//** File:      " + codeWriter.fileId2Info(fileId));
		codeWriter.writeln("//** Generated: " + sdf.format(cal.getTime()));
		codeWriter.writeln("//************************************************************");
		codeWriter.writeln("//************************************************************");
		//--------------------------------------------------------------------------------
		codeWriter.writeln("// Peer Model Tool Chain");
		codeWriter.writeln("// Copyright (C) 2021 Eva Maria Kuehn");
		codeWriter.writeln("// -----------------------------------------------------------");
		codeWriter.writeln("// This program is free software: you can redistribute it and/or modify");
		codeWriter.writeln("// it under the terms of the GNU Affero General Public License as");
		codeWriter.writeln("// published by the Free Software Foundation, either version 3 of the");
		codeWriter.writeln("// License, or (at your option) any later version.");
		codeWriter.writeln("// ");
		codeWriter.writeln("// This program is distributed in the hope that it will be useful,");
		codeWriter.writeln("// but WITHOUT ANY WARRANTY; without even the implied warranty of");
		codeWriter.writeln("// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
		codeWriter.writeln("// GNU Affero General Public License for more details.");
		codeWriter.writeln("// ");
		codeWriter.writeln("// You should have received a copy of the GNU Affero General Public License");
		codeWriter.writeln("// along with this program.  If not, see <http://www.gnu.org/licenses/>.");
		codeWriter.writeln("//************************************************************");
		codeWriter.writeln("//************************************************************");
		//--------------------------------------------------------------------------------
		codeWriter.writeln("");
	}
	//--------------------------------------------------------------------------------
	// write header info that this code is autogenerated
	public void writeAutoFileTail() throws SNHException {
		codeWriter.writeln("");
		codeWriter.writeln("//////////////////////////////////////////////////////////////");
		codeWriter.writeln("// END OF AUTO GENERATED FILE");
		codeWriter.writeln("//////////////////////////////////////////////////////////////");
		codeWriter.writeln("");
		codeWriter.writeln("");
	}


}


//================================================================================
// EOF
//================================================================================
