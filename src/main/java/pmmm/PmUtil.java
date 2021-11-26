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

package pmmm;

import eval.tokens.*;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// caution: keep everything up-to-date with the ISpaceDefs
public class PmUtil implements IPmDefs {
	//--------------------------------------------------------------------------------
	// CONFIG OF THIS CLASS
	//--------------------------------------------------------------------------------
	// display also tvv
	static boolean showTvvFlag = false; // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	//--------------------------------------------------------------------------------

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public static void setShowTvvFlag(boolean showTvvFlag) {
		PmUtil.showTvvFlag = showTvvFlag;
	}
	
	//================================================================================
	// PLAUSI CHECKS
	//================================================================================
	//--------------------------------------------------------------------------------
	// valid container name?
	// if not, throw exception
	// - TBD: SIN, SOUT
	public static void isContainerName(String containerName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(! (containerName.equals(IPmDefs.PIC) || containerName.equals(IPmDefs.POC)))
			throw new SyntaxException("ill. container name '" + containerName + "'", m);
	}

	//--------------------------------------------------------------------------------
	// valid space op name?
	// if not, throw exception
	public static void isSpaceOpName(String spaceOpName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(! (spaceOpName.equals(IPmDefs.COPY) || 
				spaceOpName.equals(IPmDefs.CREATE) || 
				spaceOpName.equals(IPmDefs.DELETE) || 
				spaceOpName.equals(IPmDefs.MOVE) || 
				spaceOpName.equals(IPmDefs.NOOP) || 
				spaceOpName.equals(IPmDefs.READ) || 
				spaceOpName.equals(IPmDefs.REMOVE) || 
				spaceOpName.equals(IPmDefs.TAKE) || 
				spaceOpName.equals(IPmDefs.TEST)
				))
			throw new SyntaxException("ill. op name '" + spaceOpName + "'", m);
	}

	//--------------------------------------------------------------------------------
	// name check
	// - throws exception is name contains chars beyond letters, numbers (but not at first place) and underline
	public static void isValidName(String s, boolean emptyIsAllowedFlag, boolean wildcardIsAllowedFlag) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg = "";
		char c = ' ';
		//--------------------------------------------------------------------------------
		// not empty
		if(s != null && s.length() > 0) {
			//--------------------------------------------------------------------------------
			// s is wildcard 
			// - TBD: wildcard is hardcoded
			if(s.equals("*")) {
				// wildcard is not allowed
				if(! wildcardIsAllowedFlag) {
					// error
					errMsg = "no wildcard allowed";
				}
				// else ok
			}
			//--------------------------------------------------------------------------------
			// s is no wildcard
			else {
				if(s.contains(" ") || s.contains("\n"))
					errMsg = "must not contain white space";
				for(int i = 0; i < s.length(); i++) {
					c = s.charAt(i);
					// first char
					if(i == 0) {
						if(! isValidFirstCharOfName(c)) {
							errMsg = "ill. first char '" + c + "'";
							break;							
						}
					}
					// middle char
					else {
						if(! isValidCharOfName(c)) {
							errMsg = "ill. char '" + c + "'";
							break;							
						}
					}
				}
			}
		}
		//--------------------------------------------------------------------------------
		// empty
		else if(! emptyIsAllowedFlag)
			errMsg = "empty";
		//--------------------------------------------------------------------------------
		// check if there was an error
		if(errMsg.length() > 0)
			throw new SyntaxException("ill. name '" + s + "' " + errMsg, m);
	}
	//--------------------------------------------------------------------------------
	// allowed char at first place or within a name
	public static boolean isValidFirstCharOfName(char c) {
		if(((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == '_')) 
			return true;
		return false;
	}
	//--------------------------------------------------------------------------------
	// allowed char within a name
	public static boolean isValidCharOfName(char c) {
		if(isValidFirstCharOfName(c) || ((c >= '0') && (c <= '9'))) 
			return true;
		return false;
	}
	
	//================================================================================
	// NICE MESSAGE DISPLAY
	//================================================================================
	//--------------------------------------------------------------------------------
	// docu ... eg: pmmm props, pprops, eprops, wprops, lprops
	public static String propsTypes2StructuredString(String docu, PropsTypes propsTypes) {
		StringBuffer buf = new StringBuffer();
		buf.append(ui.Out.addAlignment(docu + ":"));
		//--------------------------------------------------------------------------------
		// raw
		buf.append(propsTypes.getRaw());
		//--------------------------------------------------------------------------------
		// tvv
		if(showTvvFlag && ! propsTypes.isTVVEmpty()) {
			buf.append("\n");	
			buf.append(ui.Out.addAlignmentInbetween("", "==> "));
			buf.append(propsTypes.toPairs() + "\n");
			buf.append(propsTypes);
		}
		else 
			buf.append("\n");	
		return new String(buf);
	}

	//--------------------------------------------------------------------------------
	public static String tokenExpression2StructuredString(String docu, TokenExpression tokenExpression) {
		StringBuffer buf = new StringBuffer();
		buf.append(ui.Out.addAlignment(docu + ":"));
		//--------------------------------------------------------------------------------
		// raw
		buf.append(tokenExpression.getRaw());
		//--------------------------------------------------------------------------------
		// tvv
		if(showTvvFlag && ! tokenExpression.isTVVEmpty()) {
			// buf.append("\n");	
			buf.append(tokenExpression);
		}
		else
			buf.append("\n");
		return new String(buf);
	}

	//--------------------------------------------------------------------------------
	public static String token2StructuredString(String docu, Token token) {
		StringBuffer buf = new StringBuffer();
		buf.append(ui.Out.addAlignment(docu + ":"));
		if(token != null) {
			buf.append(token.toInfo());
		}
		buf.append("\n");
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
