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
// CODE REVIEWS: 20201229 (eK);
//================================================================================

package util.replace;

import java.util.Vector;

//================================================================================
// replacement data structs: for 2 directions
// e.g. <x> = xml | latex | go-code
public class Replacements implements IReplacements {
	//--------------------------------------------------------------------------------
	// - <xxx> 2 string
	protected Vector<Replacement> x2StringReplacements = new Vector<Replacement>();
	//--------------------------------------------------------------------------------
	// - string 2 <xxx> 
	protected Vector<Replacement> string2XReplacements = new Vector<Replacement>();
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Replacements() {
		this.x2StringReplacements = new Vector<Replacement>();
		this.string2XReplacements = new Vector<Replacement>();
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	public void addX2StringReplacement(String s1, String s2) {
		x2StringReplacements.add(new Replacement(s1, s2));
	}
	//--------------------------------------------------------------------------------
	public void addString2XReplacement(String s1, String s2) {
		string2XReplacements.add(new Replacement(s1, s2));
	}

	//================================================================================
	// APPLY
	//================================================================================
	//--------------------------------------------------------------------------------
	// apply replacements to given <x> and return the replaced string code
	public String x2String(String s) {
		return apply(x2StringReplacements, s);
	}
	//--------------------------------------------------------------------------------
	// apply replacements to given code string and return the replaced <x> code
	public String string2X(String s) {
		return apply(string2XReplacements, s);
	}
	//--------------------------------------------------------------------------------
	// apply replacements 
	private String apply(Vector<Replacement> replacements, String s) {
		Replacement r;
		for(int i = 0; i < replacements.size(); i++) {
			r = replacements.get(i);
			s = s.replace(r.s1, r.s2);
		}
		return s;
	}
	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================
