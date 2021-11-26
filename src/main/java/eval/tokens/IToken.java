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
public interface IToken {
	//--------------------------------------------------------------------------------
	// nb: LABEL can be label of entry, name of a peer or any other reserved word
	// use super short names to optimze debugging output
	public enum Kind {
		INT, 
		STRING, 
		// url or label
		NAME, 
		VAR, 
		FU, 
		// binary operator
		UOP, 
		// unary operator
		BOP, 
		// left bracket "("
		LB, 
		// right bracket ")"
		RB, 
		TRUE, 
		FALSE, 
		// used for unused left args of unary ops
		EMPTY,
	};
}

//================================================================================
//EOF
//================================================================================
