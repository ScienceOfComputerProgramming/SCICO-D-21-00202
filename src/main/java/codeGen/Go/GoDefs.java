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

//================================================================================
// useful defines for code generation
public interface GoDefs {
	//--------------------------------------------------------------------------------
	// file id
	public final int TEST_FILE_ID = 1;
	public final int TESTCASE_FILE_ID = 2;
	public final int USE_CASE_FILE_ID = 3;
	//--------------------------------------------------------------------------------
	// useful things for code writing
	static final String TAB = "    ";
	static final String COMMA = ", ";
	static final String LB = "(";
	static final String RB = ")";
	//--------------------------------------------------------------------------------

	
} // END OF INTERFACE


//================================================================================
// EOF
//================================================================================
