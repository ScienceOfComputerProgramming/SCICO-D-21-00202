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

//================================================================================
// align everything with go automaton, e.g. Args, SystemFunctionEnum
public interface IArg {
	//--------------------------------------------------------------------------------
	public enum Kind {VAL, LABEL, VAR, EXPR, FU};
	public enum DataType {INT, STRING, BOOL};
	public enum StringSubType {NORMAL, URL, ENTRY_TYPE};
	public enum SystemFunction {CLOCK_FUNCTION, FID_FUNCTION};
}


//================================================================================
// EOF
//================================================================================
