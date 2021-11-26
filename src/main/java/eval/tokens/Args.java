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

import java.util.Vector;

import qa.tracer.Tracer;

//================================================================================
// translate a vector of tokens into args structure
public class Args {
	// input string to be minced
	String input;
	// resulting tokens
	Vector<Arg> args = new Vector<Arg>();
	// for debugging
	Tracer tracer = new Tracer();
	
	//--------------------------------------------------------------------------------
	public Args(String input) {
		this.input = input;
	}

//	//--------------------------------------------------------------------------------
//	private void add(Arg arg) {
//		this.add(arg);
//	}

	//--------------------------------------------------------------------------------
	public Arg get(int i) {
		return args.get(i);
	}

}


//================================================================================
//EOF
//================================================================================

