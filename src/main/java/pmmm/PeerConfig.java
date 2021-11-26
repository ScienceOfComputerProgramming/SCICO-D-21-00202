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
// peer configuration
public class PeerConfig implements IEvaluation, IMta {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// PEER NAME(S):
	// nb: could be ";" separated list of peer names which in turn can be expressions with qualifier and array access 
	// - pure (as token expression):
	protected TokenExpression peerNamesTokenExpression = new TokenExpression();
	// - processed (as token expression):
	protected TokenExpression processedPeerNamesTokenExpression = new TokenExpression();
	// - processed (as strings)
	protected Vector<String> processedPeerNames = new Vector<String>();
	//--------------------------------------------------------------------------------
	// PEER TYPE NAME:
	String peerTypeName = "";
	//--------------------------------------------------------------------------------
	// PEER CONFIG PPROPS DEFS: 
	// - nb: the processed ones are stored in the peer!!!
	protected TokenExpression ppropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PeerConfig() {
		super();
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	// nb: raw can be complex expression
	public void setRawPeerNames(String raw) {
		this.peerNamesTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setPeerTypeName(String peerTypeName) {
		this.peerTypeName = peerTypeName;
	}
	//--------------------------------------------------------------------------------
	public void setRawPeerPPropsDefs(String raw) {
		this.ppropsDefsTokenExpression.setRaw(raw);
	}

	//================================================================================
	// GET
	//================================================================================
	public String getRawPeerNames() {
		return peerNamesTokenExpression.getRaw();
	}
	//--------------------------------------------------------------------------------
	public String getPeerTypeName() {
		return peerTypeName;
	}
	//--------------------------------------------------------------------------------
	public String getRawPPropsDefs() {
		return ppropsDefsTokenExpression.getRaw();
	}
	
	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	//================================================================================
	//================================================================================

	//================================================================================
	// TOKENIZE
	//--------------------------------------------------------------------------------
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// peer name(s) must nor be empty:
		if(util.Util.isEmptyString(this.peerNamesTokenExpression.getRaw()))
			throw new SyntaxException("empty peer name", m);
		// peer type name must not be empty
		if(util.Util.isEmptyString(peerTypeName))
			throw new SyntaxException("empty peer type name", m);
		//--------------------------------------------------------------------------------
		String errMsg1 = "peer config";
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PEER NAME(S):
			errMsg2 = "/peer name(s) " + this.getRawPeerNames();
			peerNamesTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// PEER CONFIG PPROPS DEFS:
			errMsg2 = "pprops defs";
			ppropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			/**/ tracer.println("pmmm config props defs = " + ppropsDefsTokenExpression, Level.NO, m);
			/**/ tracer.println("peer config peer name = " + peerNamesTokenExpression, Level.NO, m);
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(628282, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// DATA TYPE EVAL
	//================================================================================
	//--------------------------------------------------------------------------------
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("peer name(s) = " + peerNamesTokenExpression.getRaw(), Level.NO, m);
		/**/ tracer.println("peer type = " + this.peerTypeName, Level.NO, m);
		/**/ tracer.println("cur user props types = " + context.getCurUserPropsTypes(), Level.NO, m);
		//--------------------------------------------------------------------------------
		String errMsg1 = "peer config " + this.getRawPeerNames();
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PEER NAME(S):
			errMsg2 = "peer name(s)";
			// resolve qualifier types
			peerNamesTokenExpression.resolveQualifierTypes(context.setPeerQualifierContext(context.getCurUserPropsTypes(), this.peerTypeName).getQualifierContext()); 
			// eval data types
			peerNamesTokenExpression.evalDataTypes(context.switch2Config_PeerNames());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("type eval ok of peer name(s) = " + peerNamesTokenExpression.getRaw(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// PEER CONFIG PPROPS DEFS:
			errMsg2 = "pprops defs";
			// - resolve qualifier types pprops types
			ppropsDefsTokenExpression.resolveQualifierTypes(context.setPeerQualifierContext(context.getCurUserPropsTypes() /* pprops types */, peerTypeName).getQualifierContext());
			// - eval types
			ppropsDefsTokenExpression.evalDataTypes(context.switch2Config_Peer_PropsDefs());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("evaluated pprops defs: " + ppropsDefsTokenExpression.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(360034, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// PLAUSI CHECK
	//--------------------------------------------------------------------------------
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "peer config " + this.getRawPeerNames();
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PEER NAME(S):
			errMsg2 = "/peer name(s)";
			peerNamesTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// PEER CONFIG PPROPS DEFS:
			errMsg2 = "/pprops defs";
			ppropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(367334, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	//================================================================================
	// MTA INTERFACE IMPLEMENTATION
	//================================================================================
	//================================================================================

	//================================================================================
	// MTA
	//================================================================================
	//--------------------------------------------------------------------------------
	public void mta(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// PEER NAMES:
		// - clone & mta
		processedPeerNamesTokenExpression.deepCloneAndMta(peerNamesTokenExpression, curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
		// - convert tvv to processed peer names
		/**/ tracer.println("peer name expression = " + processedPeerNamesTokenExpression.toUserInfo(true, true), Level.NO, m);
		processedPeerNames = processedPeerNamesTokenExpression.tvv2Names();
		//--------------------------------------------------------------------------------
		/**/ tracer.println("peer names = " + processedPeerNames.toString(), Level.NO, m);
	}
	
	//================================================================================
	//================================================================================
	// DEBUG
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu
	public String toString() {
		return toStructuredString(true);
	}
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu
	public String toString(boolean printPeerTypeFlag) {
		return toStructuredString(printPeerTypeFlag);
	}
	//--------------------------------------------------------------------------------
	public String toStructuredString(boolean toStructuredString) {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("PeerConfig:", "\n"));
		buf.append(ui.Out.addAlignmentInbetween("peer name(s):", peerNamesTokenExpression.getRaw() + "\n"));
		buf.append(ui.Out.addAlignmentInbetween(" - processed:", processedPeerNames + "\n"));
		buf.append(ui.Out.addAlignmentInbetween("peer type name:", peerTypeName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("peer config pprops defs", ppropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
