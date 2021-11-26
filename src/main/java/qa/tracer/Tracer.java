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

package qa.tracer;

//================================================================================
// !!! CAUTION: tracer does not work in constructors !!!
public class Tracer {
	//--------------------------------------------------------------------------------
	// tracing level: print all traces that have a higher or equal level specified than Level
	static int level = Level.HI;
	//--------------------------------------------------------------------------------
	// error message tab position; just a guess; should be the longest package + java file name;
	// if too short no problem; just some lines will be ragged...
	static int errMsgTabPosition = 50;
	//--------------------------------------------------------------------------------
	// trace only the elements that start with the listed strings which are prefixes of the following syntax: package.class.method
	// nb: limited mechanism because of possibly subsuming method names...
	// CAUTION: keep up to date -- especially when you change a package name...mnb
	//--------------------------------------------------------------------------------
	static String[] traceElements = {
			"main",
			//"parser",
			"parser.drawio.A",
			// "evaluator.types.Token",
			// "evaluator.types.Context",
			// "codeGenerator", 
			"pmmm", 
			"--------- the end ----------"
	};
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Tracer() {
	}

	//================================================================================
	// PRINT TRACE
	//================================================================================
	//--------------------------------------------------------------------------------
	// TBD: !!! CAUTION: does not work in constructor !? just silently crashes ??!! VERY DANGEROUS !!!
	// nb: tricky: must be called with new method-object created ***in the very method*** to be debugged (= 3rd arg)
	// CAUTION: Define m in the very method - not outside -> this will lead to silent "null" exception
	//--------------------------------------------------------------------------------
	// usage: 
	// - import debug.*;
	// // debug
	// - /**/ private Tracer tracer = new Tracer(); 
	// - /**/ Object m = new Object(){}; 
	// - /**/ tracer.println("xxx", Level.ME, m);
	//--------------------------------------------------------------------------------

	//--------------------------------------------------------------------------------
	// convert message to string and check before if it shall be traced
	public static String toString(boolean nlFlag, String message, int level, Object method) {
		//--------------------------------------------------------------------------------
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		// assertion
		// - eg if tracer is used in constructor, then the getEnclosingMethod() gives null...
		// - eg if method param is ill, then a null pointer exception might arise
		if((null == method) || (null == method.getClass()) || (null == method.getClass().getEnclosingMethod()) ||
				(null == method.getClass().getEnclosingClass().getName())) {
			/**/ System.out.print("*** PANIC: ill. use of tracing mechanism; level = " + level + "; method = " + method + "; msg = " + message);
		}
		//--------------------------------------------------------------------------------
		// get class name of method
		String className = method.getClass().getEnclosingClass().getName();
		//--------------------------------------------------------------------------------
		String methodName = method.getClass().getEnclosingMethod().getName();
		String element = className + "." + methodName;
		//--------------------------------------------------------------------------------
		// shall element be traced?
		// - for error and debug level do it in any case
		boolean doTrace = false;
		if(level == Level.ERROR || level == Level.DEBUG) {
			doTrace = true;
		}		
		// - else check if this element is subject to be traced now
		else if(level >= Tracer.level) {
			for(int i = 0; i < traceElements.length; i++) { 
				if(element.startsWith(traceElements[i])) {
					doTrace = true;
					break;
				}
			}
		}
		//--------------------------------------------------------------------------------
		// HERE: do the trace !!!
		if(doTrace) {
			if(nlFlag) {
				buf.append("\n");
			}
			buf.append(ui.Out.addAlignmentInbetween("[" + element + "]: ", ' ', errMsgTabPosition, message));
		}
		//--------------------------------------------------------------------------------
		// done
		return new String(buf);
	}

	//--------------------------------------------------------------------------------
	public void print_intern(boolean nlFlag, String message, int level, Object method) {
		/**/ System.out.print(toString(nlFlag, message, level, method));
	}

	//--------------------------------------------------------------------------------
	public void nl() {
		/**/ System.out.println();
	}
	//--------------------------------------------------------------------------------
	public void print(String message, int level, Object method) {
		print_intern(false, message, level, method);
	}
	//--------------------------------------------------------------------------------
	public void nlprint(String message, int level, Object method) {
		print_intern(true, message, level, method);
	}
	//--------------------------------------------------------------------------------
	public void println(String message, int level, Object o) {
		/**/ print(message + "\n", level, o);
	}
	//--------------------------------------------------------------------------------
	public void emptyLine() {
		/**/ System.out.println();
	}
	//--------------------------------------------------------------------------------
	public void setLevel(int level) {
		Tracer.level = level;
	}
	//--------------------------------------------------------------------------------
	public void nlprintln(String message, int level, Object o) {
		/**/ nlprint(message + "\n", level, o);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
