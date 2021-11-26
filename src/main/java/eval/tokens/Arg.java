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
// CREATED:   December 2020 
//================================================================================

package eval.tokens;

import qa.tracer.Tracer;

//================================================================================
// argument
public class Arg {
	//--------------------------------------------------------------------------------
		// VAL, LABEL, VAR, EXPR, FU (obligatory field that must be set):
		IArg.Kind kind;
		//--------------------------------------------------------------------------------
		// INT, STRING, BOOL (set for all kinds except for expr);
		// - set for all kinds except for expr
		IArg.DataType dataType;
		//--------------------------------------------------------------------------------
		// NORMAL, URL, ENTRY_TYPE
		IArg.StringSubType stringSubType;
		//--------------------------------------------------------------------------------
		// name label or variable name:
		String name;
		//--------------------------------------------------------------------------------
		// function name
		IArg.SystemFunction fuName;
		//--------------------------------------------------------------------------------
		// value (where available):
		// - nb: entry type and url use string val!
		// - TBD: better use lambda here to simulate a union
		//--------------------------------------------------------------------------------
		int intVal;
		String stringVal;
		boolean boolVal;
		//--------------------------------------------------------------------------------
		// expression
		ArgExpr expr;
		//--------------------------------------------------------------------------------
		// for debugging
		Tracer tracer = new Tracer();

	//--------------------------------------------------------------------------------
	public Arg() {
	}
	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================

