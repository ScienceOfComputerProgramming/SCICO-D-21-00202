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
// CREATED:   February 2021
//================================================================================

package qa.exceptions;

import qa.tracer.Level;

//================================================================================
public class BasicException extends Exception {
	//--------------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	//--------------------------------------------------------------------------------
	// error Message
	String msg = "";
	//--------------------------------------------------------------------------------
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	// excSuffix is info to be added at the end of the constructed message
	protected BasicException(String newMsg, String excSuffix, Object method) {
		//--------------------------------------------------------------------------------
		// verify excSuffix
		if(null == excSuffix)
			excSuffix = "";
		//--------------------------------------------------------------------------------
		// construct msg and set it 
		msg = constructMsg(newMsg, excSuffix, method);
	}
	//--------------------------------------------------------------------------------
	protected BasicException(String newMsg, String excSuffix, Object method, BasicException prevException) {
		//--------------------------------------------------------------------------------
		// create new exc
		this(newMsg, excSuffix, method);
		//--------------------------------------------------------------------------------
		// append msg of prev exc
		if(null != prevException)
			addStringAtEnd_intern(prevException.getPmErrorMsg());
	}

	//================================================================================
	// HELP FUs
	//================================================================================
	//--------------------------------------------------------------------------------
	private String prepareExcSuffic(String excSuffix) {
		if(util.Util.isEmptyString(excSuffix))
			return "";
		return " [" + excSuffix + "]";
	}
	//--------------------------------------------------------------------------------
	// help fu: prefixes the message with the package + filename info
	// - useful for debugging; otherwise just return msg;
	private String constructMsg(String newMsg, String excSuffix, Object method) {
		return qa.tracer.Tracer.toString(false /* nlFlag */, newMsg, Level.ERROR, method) + prepareExcSuffic(excSuffix) + "\n";
	}

	//================================================================================
	// ADD STRING TO CUR MESSAGE
	//================================================================================
	//--------------------------------------------------------------------------------
	// add a string in front of my msg
	protected void addStringInFront_intern(String s) {
		msg = s.concat(msg);
	}
	//--------------------------------------------------------------------------------
	// add a string at end of my msg
	protected void addStringAtEnd_intern(String s) {
		msg = msg.concat(s);
	}

	//================================================================================
	// RETURN ERROR MESSAGE
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getPmErrorMsg() { 
		return msg;
	}
	//--------------------------------------------------------------------------------
	public String getNlPmErrorMsg() { 
		return "\n" + msg;
	}
	
	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================
