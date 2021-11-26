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

package main;

import java.util.Vector;

import pmmm.*;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// auto generated XML; can be "re-consumed" by translator :-)
public class Main {
	//--------------------------------------------------------------------------------
	/**/ O m = new O(){}; // debug
	//--------------------------------------------------------------------------------
	//================================================================================
	// MAIN ARGS:
	//================================================================================
	//--------------------------------------------------------------------------------
	// SOURCE-KIND: [ DRAWIO | PM-DSL ]
	static String sourceKind = ""; 
	//--------------------------------------------------------------------------------
	// TOOLCHAIN PATH: e.g: E:/_CURRRENT/_PEER-MODEL/
	static String absolutePeerModelToolchainPath = ""; 
	//--------------------------------------------------------------------------------
	// RELATIVE USE CASE PATH (relative to <toolchain-path>/_USE-CASES/_<sourceKind>/): e.g., _DRAWIO/PeerCompetition/Demo/ 
	static String relativeUcPath = ""; 
	//--------------------------------------------------------------------------------
	// ABSOLUTE USE CASE PATH 
	static String absoluteUcPath = ""; 
	//--------------------------------------------------------------------------------
	// USE CASE NAME: e.g., PeerComp_V5, TomatoFactory, GemueseOrchester, ProducerConsumer ... 
	// - nb: extension ".xml" is automatically added to file name;
	static String ucName = ""; 
	//--------------------------------------------------------------------------------
	// CONFIG NAME:
	static String configName = ""; 
	//--------------------------------------------------------------------------------
	// TARGET-KINDS: vector of TARGETS = [ GO-CODE | PM-DSL | LATEX ]
	static Vector<String> targetKinds = new Vector<String>(); 
	//================================================================================
	//--------------------------------------------------------------------------------
	static final String DRAWIO = "DRAWIO";
	static final String PM_DSL = "PM-DSL";
	static final String GO_CODE = "GO-CODE";
	static final String LATEX = "LATEX";

	//================================================================================
	// MAIN
	//================================================================================
	//--------------------------------------------------------------------------------
	// directory structure and file names are based on conventions
	public static void main(String[] args) {
		boolean userErrFlag = false;
		boolean snhErrFlag = false;
		String errMsg = "";
		try {
			//--------------------------------------------------------------------------------
			// verify & check args
			checkArgs(args);
			//--------------------------------------------------------------------------------
			// doIt
			translate();
			//--------------------------------------------------------------------------------
		} catch (CodeGenException e) {
			userErrFlag = true;
			errMsg = e.getPmErrorMsg();
		} catch (SNHException e) {
			snhErrFlag = true;
			errMsg = e.getPmErrorMsg();
		} catch (SyntaxException e) {
			userErrFlag = true;
			errMsg = e.getPmErrorMsg();
		}
		//--------------------------------------------------------------------------------
		if(userErrFlag) {
			/**/ System.out.println("\n*** USER ERROR: \n" + errMsg);
		}
		else if (snhErrFlag) {
			/**/ System.out.println("\n*** PANIC: \n" + errMsg);
		}
		else {
			/**/ System.out.println("\n" + ui.Out.banner("SUCCESS", '!'));
		}
		//--------------------------------------------------------------------------------
		// /**/ System.out.print(ui.Out.borderline('_'));
		//--------------------------------------------------------------------------------
	}

	//--------------------------------------------------------------------------------
	// directory structure and file names are based on conventions
	private static void translate() throws SyntaxException, CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		// for debug
		/**/ Tracer tracer = new Tracer();
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// local vars:
		parser.PmmmComponents pmmmComponents;
		pmmm.PmmmInstance pmmmInstance; 
		Vector<String> targetPaths = new Vector<String>();
		String extension = ".xml";
		String errMsg = "";
		//--------------------------------------------------------------------------------

		//================================================================================
		// PARSER: SOURCE 2 PMMM TYPE
		//================================================================================
		/**/ tracer.println(ui.Out.border('-') + sourceKind + " 2 'RAW' PMMM", Level.INFO, m);	
		//--------------------------------------------------------------------------------
		// builder pattern
		parser.Parser parser;
		//--------------------------------------------------------------------------------
		// drawio
		if(sourceKind.equals(DRAWIO)) {
			absoluteUcPath = absolutePeerModelToolchainPath + "_USE-CASES/_DRAWIO/" + relativeUcPath; 
			parser = new parser.drawio.DrawioParser(absoluteUcPath, ucName, extension);
		}
		//--------------------------------------------------------------------------------
		// pm dsl
		else if(sourceKind.equals(PM_DSL)) {
			absoluteUcPath = absolutePeerModelToolchainPath + "_USE-CASES/_PM_DSL/" + relativeUcPath; 
			parser = new parser.pmDsl.PmDslParser(absoluteUcPath, ucName, extension);
		}
		//--------------------------------------------------------------------------------
		// cannot happen, because args were testes, but anyhow
		else return;
		//--------------------------------------------------------------------------------
		// do the parsing from the right source
		errMsg = sourceKind + " parser error";
		try {
			pmmmComponents = new parser.Director(parser).parse();
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg, m, e);
		} catch (SNHException e) {
			throw new SNHException(600000, errMsg, m, e);
		}
		//--------------------------------------------------------------------------------
		/**/ tracer.println("source = " + absoluteUcPath + ucName + extension, Level.INFO, new Object(){});
		/**/ tracer.println("use case file successfully parsed", Level.INFO, new Object(){});
		/**/ tracer.println("'Raw' Pmmm:\n" + pmmmComponents.toString(false /* n/a: showProcessedFieldsFlag */), Level.NO, new Object(){});
		//--------------------------------------------------------------------------------

		//================================================================================
		// CREATE PMMM TYPE AND EVALUATE IT (TOKENIZE, TYPE CHECK & PLAUSI CHECK)
		//================================================================================
		//--------------------------------------------------------------------------------
		/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
		/**/ tracer.println("'RAW' PMMM TYPE 2 EVALUATED PMMM TYPE", Level.INFO, m);	
		PmUtil.setShowTvvFlag(false /* showTvvFlag */); // <<<<<<<<<<<<<<<<<<<< configure here; if true, traces will display also the tvv info
		//--------------------------------------------------------------------------------
		PmmmType pmmmType;
		try {
			//--------------------------------------------------------------------------------
			// create pmmm type from components
			pmmmType = new PmmmType(pmmmComponents); 
			// evaluate the pmmm type 
			pmmmType.evaluate();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException("user model error", m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(188888, "system error", m, e);
		}
		//--------------------------------------------------------------------------------
		// success
		/**/ tracer.println("Pmmm Type:\n" + pmmmComponents.toString(false /* showProcessedFieldsFlag */), Level.INFO, new Object(){});
		/**/ tracer.println("successfully evaluated", Level.NO, new Object(){});
		//--------------------------------------------------------------------------------

		//================================================================================
		// INSTANTIATION OF A PMMM FOR THE DESIRED CONFIG
		//================================================================================
		//--------------------------------------------------------------------------------
		/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
		/**/ tracer.println("INSTANTIATE PMMM FROM CONFIG '" + configName + "'", Level.INFO, m);	
		//--------------------------------------------------------------------------------
		// find config
		// - just pass the exception on (ie if config is not found)
		Config config = pmmmComponents.getConfig(configName);
		// create pmmm instance
		pmmmInstance = new PmmmInstance(pmmmType, config);
		// evaluate and transform it
		pmmmInstance.evaluateAndTransform();
		//--------------------------------------------------------------------------------
		// success
		/**/ tracer.println("Pmmm for Config '" + configName + "':\n" + pmmmInstance.toString(), Level.NO, new Object(){});
		/**/ tracer.println("successfully instantiated", Level.NO, new Object(){});
		//--------------------------------------------------------------------------------

		//================================================================================
		// CODE GENERATOR: PMMM 2 ALL TARGETS
		//================================================================================
		//--------------------------------------------------------------------------------
		// create code gen
		codeGen.BasicCodeGen codeGenerator;
		// write output also to console
		boolean writeAlsoToConsoleFlag = false; // <<<<<<<<<<<<<<<<<<<< configure here
		//--------------------------------------------------------------------------------
		for(int i = 0; i < targetKinds.size(); i++) {
			String targetKind = targetKinds.get(i);
			String absoluteUcTargetPath = "";
			//--------------------------------------------------------------------------------
			try {
				//--------------------------------------------------------------------------------
				// go code
				if(targetKind.equals(GO_CODE)) {
					String dir = "useCases"; 
					absoluteUcTargetPath = absolutePeerModelToolchainPath + "_GO-AUTOMATON/src/" + dir + "/" + relativeUcPath; 					
					codeGenerator = new codeGen.Go.GoCodeGen(pmmmInstance, relativeUcPath, absoluteUcTargetPath, dir, writeAlsoToConsoleFlag);
				}
				//--------------------------------------------------------------------------------
				// pm dsl
				else if(targetKind.equals(PM_DSL)) {
					absoluteUcTargetPath = absolutePeerModelToolchainPath + "_USE-CASES/_AUTO_GENERATED/_PM_DSL/" + relativeUcPath; 
					codeGenerator = new codeGen.PmDsl.PmDslCodeGen(pmmmInstance, absoluteUcTargetPath, writeAlsoToConsoleFlag);
				}
				//--------------------------------------------------------------------------------
				// latex
				else if(targetKind.equals(LATEX)) {
					absoluteUcTargetPath = absolutePeerModelToolchainPath + "_USE-CASES/_AUTO_GENERATED/_LATEX/" + relativeUcPath; 
					codeGenerator = new codeGen.LaTeX.LaTeXCodeGen(pmmmInstance, absoluteUcTargetPath, writeAlsoToConsoleFlag);
				}
				//--------------------------------------------------------------------------------
				else {
					// should not happen -- was checked by args
					// exc would be catched below...
					throw new SNHException(101010, "ill. target kind = " + targetKind, m);
				}
				//--------------------------------------------------------------------------------
				// do it
				(new codeGen.CodeGenDirector(codeGenerator)).generate();
				//--------------------------------------------------------------------------------
			} catch (CodeGenException e) {
				throw new CodeGenException(targetKind + ": can't generate code", m, e);
			} catch (SNHException e) {
				throw new SNHException(375215, targetKind + ": can't generate code", m, e);
			}
			targetPaths.add(absoluteUcTargetPath);
			/**/ tracer.println("use case successfully translated to " + targetKind, Level.INFO, new Object(){});
		}

		//================================================================================
		// INFO: MESSAGES TO THE USER
		//================================================================================
		//--------------------------------------------------------------------------------
		/**/ System.out.println();
		/**/ System.out.print(ui.Out.borderline('-'));
		// /**/ System.out.println("!!! SUCCESS !!!\n");
		/**/ System.out.println("input:  " + absoluteUcPath + ucName + extension + " (" + sourceKind + ")");
		/**/ System.out.println("config: " + configName);
		//--------------------------------------------------------------------------------
		for(int i = 0; i < targetKinds.size() && i < targetPaths.size(); i++) {
			String targetKind = targetKinds.get(i);
			// TBD: tricky....
			/**/ System.out.println("output: " + targetPaths.get(i) + 
					((targetKind.equals(GO_CODE)) ? "..." : (ucName + "_" + configName + 
							(targetKind.equals(LATEX) ? ".tex" : ".xml"))) + 
					" (" + targetKind + ")");
		}
		//--------------------------------------------------------------------------------
		/**/ System.out.print(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// CHECK ARGS
	//================================================================================
	//--------------------------------------------------------------------------------
	// call: main [DRAWIO|PM-DSL] <peer-model-path> <use-case-name> <config-name> {GO-CODE} {PM-DSL} {LATEX}
	private static void checkArgs(String[] args) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ Tracer tracer = new Tracer();  // debug
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// check args
		if(args.length >= 5) {
			//--------------------------------------------------------------------------------
			// source kind
			sourceKind = args[0];
			if(! (sourceKind.equals("DRAWIO") || sourceKind.equals("PM-DSL")))
				throw new SyntaxException("ill. sourceKind \n", m);
			//--------------------------------------------------------------------------------
			// tool chain path
			absolutePeerModelToolchainPath = args[1];
			//--------------------------------------------------------------------------------
			// uc path
			relativeUcPath = args[2];
			//--------------------------------------------------------------------------------
			// uc name
			ucName = args[3];
			//--------------------------------------------------------------------------------
			// config name
			configName = args[4];
			//--------------------------------------------------------------------------------
			// targets:
			for(int i = 5; i < args.length; i++) {
				String targetKind = args[i];
				if(! (targetKind.equals(GO_CODE) || targetKind.equals(PM_DSL) || targetKind.equals(LATEX)))
					throw new SyntaxException("ill. targetKind1 \n", m);
				targetKinds.add(targetKind);
			}
			//--------------------------------------------------------------------------------
			/**/ tracer.println("sourceKind = " + sourceKind, Level.NO, m);
			/**/ tracer.println("absolutePeerModelToolchainPath = " + absolutePeerModelToolchainPath, Level.NO, m);
			/**/ tracer.println("relativeUcPath = " + relativeUcPath, Level.NO, m);
			/**/ tracer.println("ucName = " + ucName, Level.NO, m);
			/**/ tracer.println("configName = " + configName, Level.NO, m);
			//--------------------------------------------------------------------------------
		}
		else {
			StringBuffer buf = new StringBuffer();
			buf.append("Wrong command line arguments: \n");
			for(int i = 0; i < args.length; i++) {
				buf.append("  arg[" + i + "] = " + args[i] + "\n");
			}
			buf.append("\ncall with: <SourceKind> <PeerModelToolchainPath> <UseCasePath> <UseCaseName> {GO-CODE|PM-DSL|LATEX}\n\nwith:\n\n" +
					"<SourceKind>\n" + 
					"  kind of source for the Peer Model Meta Model: \n" +
					"    DRAWIO ... uncompressed drawio xml file \n" +
					"    PM-DSL ... Peer Model (= PM) Domain Specific Language xml file \n" +
					"<PeerModelToolchainPath>\n" + 
					"   absolute path to the Peer Model toolchain for the poor wo/man \n" +
					"   path must end with slash \n" +
					"<UseCasePath>\n" + 
					"  relative path of the use case \n" + 
					"  relative to <PeerModelToolchainPath>/_USE_CASES/_<SourceKind> \n" +
					"  path must end with slash \n" +
					"<UseCaseName>\n" + 
					"  name of the use case which is also the name of the use case file \n" + 
					"  without \".xml\" extension \n" +
					"<ConfigName>\n" + 
					"  name of the configuration to be generated \n" +
					"  a configuration with this name must be contained in the Peer Model Meta Model (= PMMM) \n" +
					"  in drawio: a diagram termed \"Config : <config-name>\" \n" +
					"  in PM-DSL: an element termed \"<Config name=<config-name>\" \n" +
					"{GO-CODE}\n" + 
					"  optional: generate go code for the go automaton \n" +
					"{PM-DSL}\n" + 
					"  optional: generate PM-DSL \n" +
					"{LATEX}\n" + 
					"  optional: generate LATEX \n");
			throw new SyntaxException(new String(buf), m);
		}
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
