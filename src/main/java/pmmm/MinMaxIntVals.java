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
// CODE REVIEWS: 20210113 (eK);
//================================================================================

package pmmm;

import eval.tokens.IntTokenValEval;

//================================================================================
// represents a count
// - CAUTION: is min or max is -1 ... caller must recognize that it is probably a NAME, ie ALL or NONE
// -- and take the token representation for code generation
public class MinMaxIntVals {
	//--------------------------------------------------------------------------------
	// <min> .. <max>
	// - if there is no range specification, max is min
	private int min; 
	private int max; 
	
	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public MinMaxIntVals(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	//================================================================================
	// COPY 
	//================================================================================
	//--------------------------------------------------------------------------------
	public MinMaxIntVals deepCopy() {
		return new MinMaxIntVals(this.min, this.max);
	}
	
	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setMin(int min) {
		this.min = min;
	}
	//--------------------------------------------------------------------------------
	public void setMax(int max) {
		this.max = max;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public int getMin() {
		return min;
	}
	//--------------------------------------------------------------------------------
	public int getMax() {
		return max;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString
	public String toString() {
		return toStructuredString();
	}
	//--------------------------------------------------------------------------------
	// to structured string
	public String toStructuredString() {
		if(min == max) {
			// empty string, if undefined
			if(min == IntTokenValEval.MINMAX_NULL)
				return "";
			else
				return(Integer.toString(min));
		}
		else 
			return("[" + Integer.toString(min) + IOpDefs.DOTS + Integer.toString(max) + "]");
	}

	
} // END OF CLASS


//================================================================================
// EOF
//================================================================================

