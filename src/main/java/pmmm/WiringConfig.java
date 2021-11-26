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
// CREATED:   February 2021 
//================================================================================

package pmmm;

import java.util.Vector;

import eval.tokens.TokenExpression;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// wiring specification belonging to a peer type
public class WiringConfig implements IMta {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// pointer to WIRING (shared):
	Wiring wiring;
	//================================================================================
	// COMPUTED:
	//================================================================================
	//--------------------------------------------------------------------------------
	// WIRING NAMES:
	// - processed (as token expression):
	protected TokenExpression processedWiringNamesTokenExpression = new TokenExpression();
	// - processed (as strings)
	protected Vector<String> processedWiringNames = new Vector<String>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public WiringConfig() {
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setWiring(Wiring wiring) {
		this.wiring = wiring;
	}

	//================================================================================
	//================================================================================
	// MTA INTERFACE IMPLEMENTATION 
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	public void mta(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		String what = "";
		//--------------------------------------------------------------------------------
		try {
			what = "wiring name(s)";
			//--------------------------------------------------------------------------------
			// WIRING NAMES:
			//................................................................................
			// - clone & mta
			/**/ tracer.println("wiring names expression = " + wiring.wiringNamesTokenExpression.toUserInfo(true, true), Level.NO, m);
			processedWiringNamesTokenExpression.deepCloneAndMta(wiring.wiringNamesTokenExpression, curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
			what += " '" + processedWiringNamesTokenExpression.toUserInfo(true, true) + "'";
			//................................................................................
			// - convert tvv to processed wiring names
			/**/ tracer.println("wiring names expression = " + processedWiringNamesTokenExpression.toUserInfo(true, true), Level.NO, m);
			processedWiringNames = processedWiringNamesTokenExpression.tvv2Names();
			//--------------------------------------------------------------------------------
			/**/ tracer.println("wiring names = " + processedWiringNames, Level.NO, m);
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(what, m, e);
		} catch (SNHException e) {
			throw new SNHException(564564, what, m, e);
		}
	}
	
	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu
	public String toString() {
		return toStructuredString();
	}
	//--------------------------------------------------------------------------------
	// print each artifact per row 
	public String toStructuredString() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("Wiring Config:", "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("processed wiring names", processedWiringNamesTokenExpression));
		buf.append(ui.Out.addAlignmentInbetween(" - wiring names:", processedWiringNames + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(wiring.toStructuredString());
		//--------------------------------------------------------------------------------
		return new String(buf);
	}

	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================
