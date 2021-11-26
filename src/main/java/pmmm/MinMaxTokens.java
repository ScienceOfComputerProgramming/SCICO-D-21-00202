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
// CODE REVIEWS: 20210113 (eK);
//================================================================================

package pmmm;

import eval.tokens.*;

//================================================================================
// represents a count
public class MinMaxTokens {
	//--------------------------------------------------------------------------------
	// <min expr> .. <max expr>
	// - if there is no range specification, max is empty
	private Token minToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
	private Token maxToken = new Token(IToken.Kind.EMPTY, "" /* raw */);

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public MinMaxTokens() {
	}

	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setMinToken(Token minToken) {
		this.minToken = minToken;
	}
	//--------------------------------------------------------------------------------
	public void setMaxToken(Token maxToken) {
		this.maxToken = maxToken;
	}

	//================================================================================
	// COPY
	//================================================================================
	//--------------------------------------------------------------------------------
	public MinMaxTokens deepCopy() {
		MinMaxTokens newMinMaxTokenExpression = new MinMaxTokens();
		if(this.minToken != null)
			newMinMaxTokenExpression.minToken = this.minToken.deepCopy();
		if(this.maxToken != null)
			newMinMaxTokenExpression.maxToken = this.maxToken.deepCopy();		
		return newMinMaxTokenExpression;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public Token getMinToken() {
		return minToken;
	}
	//--------------------------------------------------------------------------------
	public Token getMaxToken() {
		return maxToken;
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
		if(minToken == null)
			return "";
		//--------------------------------------------------------------------------------
		StringBuffer buf = new StringBuffer();
		String minS = minToken.toInfo();
		//--------------------------------------------------------------------------------
		if(maxToken != minToken) {
			buf.append("[");
			buf.append(minS);
			buf.append(IOpDefs.DOTS);
			buf.append(maxToken.toInfo());
			buf.append("]");
		}
		else
			buf.append(minS);
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

