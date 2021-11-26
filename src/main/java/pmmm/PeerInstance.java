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

import java.util.Vector;

import eval.tokens.IToken;
import eval.tokens.Token;
import eval.tokens.TokenExpression;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// peer instance
public class PeerInstance implements IMta {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// pointer to my PEER TYPE (shared): 
	protected PeerType peerType;
	//--------------------------------------------------------------------------------
	// PEER INSTANCE NAME:
	// - as string
	protected String peerInstanceName = "";
	// - as token
	// -- needed to resolve PINDEX.<i> expressions
	protected Token peerInstanceNameToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
	//--------------------------------------------------------------------------------
	// PEER PPROPS DEFS: 
	// - !!! shared with peer config, where they were alread evaluated !!!
	protected TokenExpression peerPPropsDefsTokenExpression = new TokenExpression();
	// - processed
	protected TokenExpression processedPeerPPropsDefsTokenExpression = new TokenExpression();
	//================================================================================
	// COMPUTED:
	//================================================================================
	//--------------------------------------------------------------------------------
	// WIRING INSTANCES:
	protected Vector<WiringInstance> wiringInstances = new Vector<WiringInstance>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PeerInstance() {
		super();
	}
	
	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getPeerInstanceName() {
		return peerInstanceName;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedPPropsDefsTokenExpression() {
		return processedPeerPPropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public String getPeerTypeName() {
		return peerType.peerTypeName;
	}
	//--------------------------------------------------------------------------------
	public Vector<WiringInstance> getWiringInstances() {
		return wiringInstances;
	}
	//--------------------------------------------------------------------------------
	public Token getPeerInstanceNameToken() {
		return peerInstanceNameToken;
	}

	//================================================================================
	//================================================================================
	// QUERY
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// get value of a pprop def from the processed ones
	// nb: mta has already been applied!
	public Token getProcessedPPropDefsValueToken(Token propNameToken) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.nlprintln("searching for " + propNameToken + " in " + this.processedPeerPPropsDefsTokenExpression, Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			// search for propNameToken
			Token token = processedPeerPPropsDefsTokenExpression.getPropValueToken(propNameToken);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("processed props defs: " + processedPeerPPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			/**/ tracer.println("found: '" + propNameToken + "'; prop def: " + token.toUserInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			return token;
		} catch(NotFoundException e) {
			throw new SyntaxException("pmmm prop def", m, e);
		}
	}

	//================================================================================
	// SET / ENHANCE
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setRawPeerPPropsDefsTokenExpression(String raw) {
		peerPPropsDefsTokenExpression.setRaw(raw);
	}

	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	// !!! nb: peer type of peer has already been evaluated so no need to do it again !!!
	//================================================================================
	//================================================================================
	
	//================================================================================
	// TOKENIZE
	//================================================================================
	//--------------------------------------------------------------------------------
	// tokenize all expressions in the meta model
	// - also verify the peer type / name specification and set the peer's type and name fields
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// PEER PPROPS DEFS:
		// TBD: not needed
		peerPPropsDefsTokenExpression.tokenize();
	}

	//================================================================================
	// EVAL TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate all token types
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// PEER PPROPS DEFS:
		// TBD: not needed
		// - resolve qualifier types
		/**/ tracer.println("peer: resolve qualifier types in: " + peerPPropsDefsTokenExpression, Level.NO, m);
		peerPPropsDefsTokenExpression.resolveQualifierTypes(context.setPeerQualifierContext(peerType.peerTypePPropsTypes, peerType.peerTypeName).getQualifierContext());
		// - eval types
		peerPPropsDefsTokenExpression.evalDataTypes(context.switch2Config_Peer_PropsDefs());
	}

	//================================================================================
	// PLAUSI CHECK
	//================================================================================
	//--------------------------------------------------------------------------------
	public void plausiCheck(String docu /* debug */) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// PEER PPROPS DEFS:
		// TBD: not needed
		peerPPropsDefsTokenExpression.plausiCheck();
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
	// nb: constructs wiring instances
	public void mta(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		String what = "";
		/**/ tracer.println("peer instance '" + this.peerInstanceName + "'", Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PROCESSED PEER PPROPS DEFS:
			what = "pprops defs";
			processedPeerPPropsDefsTokenExpression.deepCloneMergeMta_Assignments(
					peerPPropsDefsTokenExpression /* orig */, peerType.peerTypePPropsDefsTokenExpression /* defaults */, 
					curPmmmInstance, this /* curPeerInstance */, curWiringInstance, curEntryType);
			/**/ tracer.println("=== processedPeerPPropsDefsTokenExpression " + processedPeerPPropsDefsTokenExpression.toInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// CONSTRUCT WIRING INSTANCES:
			what = "construct wiring instance";
			constructWiringInstances(curPmmmInstance, this /* curPeerInstance */, curWiringInstance, curEntryType);
			//--------------------------------------------------------------------------------
			// MTA WIRING INSTANCES:
			for(int i = 0; i < wiringInstances.size(); i++) {
				WiringInstance wiringInstance = wiringInstances.get(i);
				what = "wiring instance = " + wiringInstance.wiringInstanceName;
				wiringInstance.mta(curPmmmInstance, curPeerInstance, wiringInstance /* curWiringInstance */, curEntryType);
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(what, m, e);
		} catch (SNHException e) {
			throw new SNHException(111117, what, m, e);
		}
	}
	
	//================================================================================
	// CONSTRUCT WIRING INSTANCES:
	//================================================================================
	//--------------------------------------------------------------------------------
	// construct wiring instances
	// - one for each wiring name
	public void constructWiringInstances(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) 
			throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// for all wirings of my peer type 
		for(int i = 0; i < peerType.wirings.size(); i++) {
			//--------------------------------------------------------------------------------
			// get next wiring
			Wiring wiring = peerType.wirings.get(i);
			//--------------------------------------------------------------------------------
			// construct wiring config 
			//................................................................................
			// - create
			WiringConfig wiringConfig = new WiringConfig();
			//................................................................................
			// - set pointer to wiring
			wiringConfig.wiring = wiring;
			//................................................................................
			// - mta it; needed to resolve wiring names
			wiringConfig.mta(curPmmmInstance, this /* curPeerInstance */, null /* curWiringInstance */, null /* curEntryType */);
			//--------------------------------------------------------------------------------
			// construct wiring instance for each wiring name
			for(int j = 0; j < wiringConfig.processedWiringNames.size(); j++) {
				//--------------------------------------------------------------------------------
				// create new wiring instance
				WiringInstance wiringInstance = new WiringInstance();
				//--------------------------------------------------------------------------------
				// init wiring instance
				//................................................................................
				// - wiring instance name (as string)
				wiringInstance.wiringInstanceName = wiringConfig.processedWiringNames.get(j);
				//................................................................................
				// - wiring instance name (as token)
				wiringInstance.wiringInstanceNameToken = wiringConfig.processedWiringNamesTokenExpression.tokenVV.get(j).getRootToken(); 
				//................................................................................
				// - set pointer to wiring config
				wiringInstance.wiringConfig = wiringConfig;
				//................................................................................
				// - PROCESSED WIRING WPROPS DEFS:
				// -- !!! CAUTION: must be constructed and mta-ed here !!!
				wiringInstance.processedWPropsDefsTokenExpression.deepCloneMergeMta_Assignments(
						wiring.wiringWPropsDefsTokenExpression /* orig */, 
						wiring.wiringType.wiringTypeWPropsDefsTokenExpression /* defaults */, 
						curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
				//................................................................................
				/**/ tracer.println("processed wiring wprops defs = " + wiringInstance.processedWPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
				//................................................................................
				// - construct link instances for guards and actions (caution: needs set link pointer!)
				wiringInstance.constructLinkInstances();
				//--------------------------------------------------------------------------------
				// add wiring instance
				wiringInstances.add(wiringInstance);
				//--------------------------------------------------------------------------------
				/**/ tracer.println("wiring instance " + wiringInstance.wiringInstanceName + " of wiring type " + wiring.wiringType.wiringTypeName + " added", Level.NO, m);
			}
		}
	}

	//================================================================================
	//================================================================================
	// DEBUG
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu
	public String toString() {
		return toStructuredString();
	}
	//--------------------------------------------------------------------------------
	public String toStructuredString() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("Peer:", peerInstanceName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("pprops defs", peerPPropsDefsTokenExpression));
		buf.append(PmUtil.tokenExpression2StructuredString(" - processed", processedPeerPPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		// wiring instances
		for(int i = 0; i < wiringInstances.size(); i++) {
			buf.append(ui.Out.banner("WIRING INSTANCE: " + wiringInstances.get(i).wiringInstanceName, '='));
			buf.append(wiringInstances.get(i).toStructuredString());
		}
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================
