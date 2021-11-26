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
//SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
//AUTHOR:    Eva Maria Kuehn
//CREATED:   February 2021
//================================================================================

package qa.exceptions;

//================================================================================
// panic: this should not happen; is a program error;
public class SNHException extends BasicException {
	//--------------------------------------------------------------------------------
	private static final long serialVersionUID = 5L;
	//--------------------------------------------------------------------------------
	private static String excSuffix = "SYSTEM ERROR";
	//--------------------------------------------------------------------------------
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public SNHException(int codeLocation, String msg, Object method) {
		super(msg, prepareExcSuffix(codeLocation), method);
	}
	//--------------------------------------------------------------------------------
	public SNHException(int codeLocation, String msg, Object method, BasicException prevException) {
		super(msg, prepareExcSuffix(codeLocation), method, prevException);
	}

	//================================================================================
	// HELP FU
	//================================================================================
	//--------------------------------------------------------------------------------
	private static String prepareExcSuffix(int codeLocation) {
		return excSuffix + ":" + String.valueOf(codeLocation);
	}

	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================
