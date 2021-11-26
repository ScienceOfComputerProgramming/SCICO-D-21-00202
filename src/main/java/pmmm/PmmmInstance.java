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
package pmmm;

import java.util.Vector;

import eval.tokens.Token;
import eval.tokens.TokenExpression;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// PMMM instance according to a desired config;
// NB: the fields needed by the mta are included in all classes and denoted with "processed"
public class PmmmInstance {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// pointer to my PMMM Type (shared):
	PmmmType pmmmType;
	//--------------------------------------------------------------------------------
	// pointer to my CONFIG:
	// - nb: here my pmmm props defs and my peer *configs* are found
	Config config;
	//================================================================================
	// COMPUTED:
	//================================================================================
	//--------------------------------------------------------------------------------
	// PROCESSED PMMM PROPS DEFS:
	// - caution: pmmm props defs info is distributed
	// -- 1) defaults are in pmmm type
	// -- 2) origs are in config
	// -- 3) processed are here
	protected TokenExpression processedPmmmPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// PEERS:
	// - create a peer instance for each peer name
	protected Vector<PeerInstance> peerInstances = new Vector<PeerInstance>(); 
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmmmInstance(PmmmType pmmmType, Config config) throws SyntaxException, SNHException {
		this.pmmmType = pmmmType;
		this.config = config;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public Config getConfig() {
		return config;
	}
	//--------------------------------------------------------------------------------
	public PmmmType getPmmmType() {
		return pmmmType;
	}
	//--------------------------------------------------------------------------------
	public Vector<PeerInstance> getPeerInstances() {
		return peerInstances;
	}
	//--------------------------------------------------------------------------------
	public String getConfigName() {
		return config.configName;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedPmmmPropsDefsTokenExpression() {
		return processedPmmmPropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public PropsTypes getPmmmTypePropsTypes() {
		return pmmmType.pmmmTypePmmmPropsTypes;
	}
	//--------------------------------------------------------------------------------
	public String getPmmmTypeName() {
		return pmmmType.pmmmTypeName;
	}

	//================================================================================
	//================================================================================
	// EVALUATE AND TRANSFORMM
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// 1) type evaluate config (tokenize, eval data types, plausi check) 
	// 2) mta pmmm props defs
	//    - caution: this must be done *before* config is mta-ed, because config uses qualifiers of the form PMMM.<prop>
	// 3) mta config
	//    - must be done before constructing peer instances, because peer names must be resolved 
	// 4) construct peer instances
	// 5) mta peer instances
	// CAUTION: must be clearly done *after* data type eval of pmmm type
	// CAUTION: do not change the order
	public void evaluateAndTransform() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		try {
			//================================================================================
			// 1) EVALUATE *CONFIG*
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg = "data type eval of config";
			//--------------------------------------------------------------------------------
			// create new context with the global pmmm type facts 
			Context context = new Context(pmmmType.keywordTypes, pmmmType.pmmmTypeName, pmmmType.pmmmTypePmmmPropsTypes, pmmmType.peerTypeNames, pmmmType.peerTypes, pmmmType.entryTypes);
			//--------------------------------------------------------------------------------
			// TOKENIZE:
			config.tokenize();
			/**/ tracer.println("config tokenized", Level.NO, m);
			//--------------------------------------------------------------------------------
			// EVAL DATA TYPES:
			config.evalDataTypes(context.switch2Config(config.getConfigName()));		
			/**/ tracer.println("config data type evaluated", Level.NO, m);
			//--------------------------------------------------------------------------------
			// PLAUSI CHECK:
			config.plausiCheck();	
			/**/ tracer.println("config plausi checked", Level.NO, m);
			//================================================================================
			// 2) MTA PMMM PROPS DEFS
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg = "pmmm props defs";
			/**/ tracer.println("pmmm props defs: " + config.pmmmPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			/**/ tracer.println("default pmmm props defs: " + pmmmType.pmmmTypePmmmPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			//--------------------------------------------------------------------------------
			// mta processed pmmm props defs
			processedPmmmPropsDefsTokenExpression.deepCloneMergeMta_Assignments(config.pmmmPropsDefsTokenExpression /* orig */, 
					pmmmType.pmmmTypePmmmPropsDefsTokenExpression /* defaults */, this /* curPMMM */, null /* curPeer */, null /* curWiring */, null /* curEntryType */);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("processed pmmm props defs: " + processedPmmmPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			/**/ tracer.println("processed pmmm props defs (raw): " + processedPmmmPropsDefsTokenExpression.getRaw(), Level.NO, m);
			//================================================================================
			// 3) MTA *CONFIG*
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg = "eval of config";
			//--------------------------------------------------------------------------------
			// mta config
			config.mta(this /* curPMMM */, null /* curPeer */, null /* curWiring */, null /* curEntryType */);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("evaluated config: " + config.toString(), Level.NO, m);
			//================================================================================
			// 4) CONSTRUCT PEER INSTANCES
			//================================================================================
			//--------------------------------------------------------------------------------
			// create a peer for each configure peer name
			// - that points to its peer type
			// - for each configured wiring name create a wiring instance
			constructPeerInstances();
			/**/ tracer.println("peers constructed", Level.NO, m);
			//================================================================================
			// 5) MTA PEER INSTANCES
			//================================================================================
			//--------------------------------------------------------------------------------
			// mta peer instances
			// - nb: constructs wiring instances
			for(int i = 0; i < peerInstances.size(); i++) {
				//--------------------------------------------------------------------------------
				// get next peer instance
				PeerInstance peerInstance = peerInstances.get(i);
				//--------------------------------------------------------------------------------
				errMsg = "peer instance '" + peerInstance.getPeerInstanceName() + "'";
				//--------------------------------------------------------------------------------
				// mta it
				peerInstance.mta(this /* curPmmmInstance */, peerInstance /* curPeerInstance */, null /* curWiringInstance */, null /* curEntryType */);  
				//--------------------------------------------------------------------------------
			}
			//================================================================================
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg, m, e);
		} catch (SNHException e) {
			throw new SNHException(700007, errMsg, m, e);
		}
	}

	//================================================================================
	// CONSTRUCT PEER INSTANCES:
	//================================================================================
	//--------------------------------------------------------------------------------
	// construct peer instances from config
	// CAUTION: config must have been evaluated and mta-ed before!
	public void constructPeerInstances() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// for all configured peers 
		for(int i = 0; i < config.peerConfigs.size(); i++) {
			//--------------------------------------------------------------------------------
			// get next peer config
			PeerConfig peerConfig = config.peerConfigs.get(i);
			//--------------------------------------------------------------------------------
			// construct peer instance for each peer name of the peer config
			for(int j = 0; j < peerConfig.processedPeerNames.size(); j++) {
				//--------------------------------------------------------------------------------
				// create peer instance
				PeerInstance peerInstance = new PeerInstance();
				//--------------------------------------------------------------------------------
				// set peer name
				// - as string
				peerInstance.peerInstanceName = peerConfig.processedPeerNames.get(j);
				// - as token (nb: needed for resolving PINDEX expressions!)
				peerInstance.peerInstanceNameToken = peerConfig.processedPeerNamesTokenExpression.tokenVV.get(j).getRootToken();;
				//--------------------------------------------------------------------------------
				// find peer type
				PeerType peerType = pmmmType.getPeerType(peerConfig.peerTypeName);
				//--------------------------------------------------------------------------------
				// set pointer to peer type in peer
				peerInstance.peerType = peerType;
				//--------------------------------------------------------------------------------
				// clone pprops defs
				/**/ tracer.println("pprops defs BEFORE CLONING: " + peerConfig.ppropsDefsTokenExpression.toTypedInfo(), Level.NO, m);
				peerInstance.peerPPropsDefsTokenExpression.deepCloneFromTokenExpression(peerConfig.ppropsDefsTokenExpression);
				/**/ tracer.println("pprops defs AFTER CLONING: " + peerInstance.peerPPropsDefsTokenExpression.toTypedInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// add peer
				peerInstances.add(peerInstance);
				//--------------------------------------------------------------------------------
				/**/ tracer.println("peer " + peerInstance.getPeerInstanceName() + " of peer type " + peerInstance.getPeerTypeName() + " added", Level.NO, m);
			}
		}
	}

	//================================================================================
	//================================================================================
	// QUERY
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// is name a sys const name?
	public boolean isSysConst(String name) {
		return pmmmType.isSysConst(name);
	}
	//--------------------------------------------------------------------------------
	// get value of a pmmm prop def from the processed ones
	// nb: mta has already been applied!
	public Token getProcessedPmmmPropDefValueToken(Token propNameToken) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("search for " + propNameToken + " in " + processedPmmmPropsDefsTokenExpression, Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			// search for propNameToken
			Token token = processedPmmmPropsDefsTokenExpression.getPropValueToken(propNameToken);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("processed props defs: " + processedPmmmPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			/**/ tracer.println("found: '" + propNameToken + "'; prop def: " + token.toUserInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			return token;
		} catch(NotFoundException e) {
			throw new SyntaxException("pmmm prop def", m, e);
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
	// print: 
	// - entry name 
	// - each entry property per row
	public String toStructuredString() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append("\n");
		buf.append(ui.Out.banner("PMMM INSTANCE: " + pmmmType.pmmmTypeName, '*'));
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("PMMM Name:", pmmmType.pmmmTypeName + "\n"));
		buf.append(ui.Out.addAlignmentInbetween("config:", config.configName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.propsTypes2StructuredString("pmmm type props types", pmmmType.pmmmTypePmmmPropsTypes));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("pmmm type props defs", pmmmType.pmmmTypePmmmPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("pmmm props defs", config.pmmmPropsDefsTokenExpression));
		buf.append(PmUtil.tokenExpression2StructuredString(" - default", pmmmType.pmmmTypePmmmPropsDefsTokenExpression));
		buf.append(PmUtil.tokenExpression2StructuredString(" - processed", processedPmmmPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		if(pmmmType.entryTypes.entryTypes.size() > 0) {
			buf.append(ui.Out.banner("ENTRY TYPES:", '*'));
			buf.append(pmmmType.entryTypes.toString());
		}
		//--------------------------------------------------------------------------------
		// all peers with their individually processed peer types
		for(int i = 0; i < peerInstances.size(); i++) {
			PeerInstance peerInstance = peerInstances.get(i);
			buf.append("\n\n");
			buf.append(ui.Out.banner("PEER INSTANCE: " + peerInstance.peerInstanceName, '*'));
			buf.append(ui.Out.borderline('-'));
			buf.append(peerInstance.toStructuredString());
		}
		//--------------------------------------------------------------------------------
		buf.append("\n");
		buf.append(ui.Out.banner("PMMM END", '*'));
		buf.append("\n");
		//--------------------------------------------------------------------------------
		return new String(buf);
	}
	

} // END OF CLASS


//================================================================================
//EOF
//================================================================================
