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
// CREATED:   January 2021
//================================================================================

package eval.tokens;

import java.util.Vector;

import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.Tracer;

//================================================================================
// token vector vector data structure & CORE functionality
public class TVV {
	//--------------------------------------------------------------------------------
	// tracer
	private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// vector of token vectors
	public Vector<TV> tokenVV = new Vector<TV>();
	//--------------------------------------------------------------------------------
	// phase of the TVV -- i.e. which parsing phase has already been applied to it (for debug)
	// - TBD: hard coded
	//    "MINCING" 
	//    "MINCED" 
	//    "MELTING" 
	//    "MELTED" 
	//    "EVALUATING" // data types
	//    "EVALUATED"  // data types
	//    "UNDEFINED"    
	// - CAUION: keep raw up-to-date with any change of TVV!!!
	protected String phase = "UNDEFINED";

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public TVV() {
	}

	//================================================================================
	// CLEAR
	//================================================================================
	//--------------------------------------------------------------------------------
	public void clear() {
		this.tokenVV = new Vector<TV>();
		phase = "UNDEFINED";
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	// get size
	protected int size() {
		return tokenVV.size();
	}
	//--------------------------------------------------------------------------------
	// get TV at index
	protected TV get(int i) {
		return tokenVV.get(i);
	}
	//--------------------------------------------------------------------------------
	// get phase
	protected String getPhase() {
		return phase;
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	// set phase
	protected void setPhase(String phase) {
		/**/ tracer.println("PHASE " + phase, Level.NO, new Object(){});		
		this.phase = phase;
	}

	//================================================================================
	// TEST
	//================================================================================
	//--------------------------------------------------------------------------------
	// any tokens out there?
	public boolean isTVVEmpty() {
		if(tokenVV.size() <= 0)
			return true;
		return tokenVV.get(0).isEmpty();
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add token vector
	public void add(TV tv) {
		tokenVV.add(tv);
	}

	//--------------------------------------------------------------------------------
	// clone token vector at index and return it
	public TV copy(int i) throws Exception {
		return tokenVV.get(i).copy();
	}

	//================================================================================
	// COPY
	//================================================================================
	//--------------------------------------------------------------------------------
	// deep clone me from given tvv 
	protected void deepCloneFromTokenVV(TVV fromTvv) {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// clear me
		this.clear();
		//--------------------------------------------------------------------------------
		// if not null
		if(fromTvv != null) {
			//--------------------------------------------------------------------------------
			// copy phase
			this.phase = fromTvv.phase;
			//--------------------------------------------------------------------------------
			// copy tvv 
			for(int i = 0; i < fromTvv.tokenVV.size(); i++) {
				TV tv = fromTvv.tokenVV.get(i);
				//--------------------------------------------------------------------------------
				for(int j = 0; j < tv.size(); j++) {
					//--------------------------------------------------------------------------------
					// deep copy
					Token clonedToken = tv.get(j).deepCopy();
					//--------------------------------------------------------------------------------
					// add
					this.add(new TV(clonedToken));
				}
			}
		}
	}
	//--------------------------------------------------------------------------------
	// not deep copying: only the vector structs are copied, but the tokens are shared
	protected void shallowCloneFromTokenVV(TVV tvvToBeCloned) {
		//--------------------------------------------------------------------------------
		// debug:
		// /**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		// clear
		this.tokenVV = new Vector<TV>();
		// check if not empty
		if(tvvToBeCloned != null) {
			// phase
			this.phase = tvvToBeCloned.phase;
			// copy tvv to my tokenVV
			for(int i = 0; i < tvvToBeCloned.tokenVV.size(); i++) {
				TV tv = tvvToBeCloned.tokenVV.get(i);
				for(int j = 0; j < tv.size(); j++) {
					this.add(new TV(tv.get(j)));
				}
			}
		}
	}
	
	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	public String toString() {
		return toUserInfo();
	}
	//--------------------------------------------------------------------------------
	public String toUserInfo() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < tokenVV.size(); i++) {
			try {
				buf.append(get(i).getRootToken().toUserInfo() + "; ");
			} catch (SyntaxException e) {
				buf.append("???; ");
			}
		}
		return new String(buf);
	}
		

} // END OF CLASS


//================================================================================
//EOF
//================================================================================
