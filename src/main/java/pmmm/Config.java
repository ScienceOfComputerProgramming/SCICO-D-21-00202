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
//SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
//AUTHOR:    Eva Maria Kuehn
//CREATED:   December 2020 
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
// CONFIG
public class Config implements IEvaluation, IMta {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// CONFIG NAME: 
	protected String configName = "";
	//--------------------------------------------------------------------------------
	// PMMM PROPS DEFS:
	protected TokenExpression pmmmPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// PEERS CONFIGS:
	protected Vector<PeerConfig> peerConfigs = new Vector<PeerConfig>(); 
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Config(String configName) {
		this.configName = configName;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getConfigName() {
		return configName;
	}
	//--------------------------------------------------------------------------------
	public Vector<PeerConfig> getPeers() {
		return peerConfigs;
	}

	//================================================================================
	// SET / ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setRawPmmmPropsDefsTokenExpression(String raw) {
		pmmmPropsDefsTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void addPeerConfig(PeerConfig peerConfig) {
		peerConfigs.add(peerConfig);
	}

	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	//================================================================================
	//================================================================================

	//================================================================================
	// TOKENIZE
	//================================================================================
	//--------------------------------------------------------------------------------
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// PMMM PROPS DEFS:
		pmmmPropsDefsTokenExpression.tokenize();
		/**/ tracer.println("pmmm props defs = " + pmmmPropsDefsTokenExpression, Level.NO, m);
		//--------------------------------------------------------------------------------
		// PEERS CONFIGS: 
		for(int i = 0; i < peerConfigs.size(); i++) {
			//--------------------------------------------------------------------------------
			// get next peer config
			PeerConfig peerConfig = peerConfigs.get(i);
			/**/ tracer.println("tokenize peer config; peer name(s) = " + peerConfig.peerNamesTokenExpression.getRaw(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// tokenize
			peerConfig.tokenize();
		}
		//--------------------------------------------------------------------------------
		/**/ tracer.println("tokenized CONFIG: \n" + this.toString(), Level.NO, m);
	}

	//================================================================================
	// DATA TYPE EVAL
	//================================================================================
	//--------------------------------------------------------------------------------
	// keep the order! 
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("eval data types of PMMM", Level.NO, m);
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PMMM PROPS DEFS:
			errMsg = "pmmm props defs";
			// - resolve qualifier types 
			pmmmPropsDefsTokenExpression.resolveQualifierTypes(context.setPmmmQualifierContext(context.pmmmTypePmmmPropsTypes, context.pmmmTypeName).getQualifierContext());
			// - eval data types
			pmmmPropsDefsTokenExpression.evalDataTypes(context.switch2PmmmType_PmmmPropsTypes().switch2Config_Pmmm_PropsDefs());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("pmmm props defs = " + pmmmPropsDefsTokenExpression, Level.NO, m);
			//--------------------------------------------------------------------------------
			// PEERS:
			//--------------------------------------------------------------------------------
			for(int i = 0; i < peerConfigs.size(); i++) {
				//--------------------------------------------------------------------------------
				// get next peer config
				PeerConfig peerConfig = peerConfigs.get(i);
				//--------------------------------------------------------------------------------
				errMsg = "config of peer '" + peerConfig.peerNamesTokenExpression.getRaw() + "'";
				/**/ tracer.println("peerConfig: " + peerConfig.toString(false /* print peer type */), Level.NO, m);
				/**/ tracer.println("peerConfig raw peer name = '" + peerConfig.getRawPeerNames() + "'", Level.NO, m);
				/**/ tracer.println("peerConfig peer type name = '" + peerConfig.peerTypeName + "'", Level.NO, m);
				//--------------------------------------------------------------------------------
				// eval
				peerConfig.evalDataTypes(context.switch2Config_PeerConfig(peerConfig.peerTypeName, peerConfig.getRawPeerNames() /* docu */));
			}
			//--------------------------------------------------------------------------------
			/**/ tracer.println("EVALUATED PEER CONFIG: \n" + this, Level.NO, m);
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg, m, e);
		} catch (SNHException e) {
			throw new SNHException(772945, errMsg, m, e);
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// PLAUSI CHECK
	//--------------------------------------------------------------------------------
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("plausiCheck CONFIG:", Level.NO, m);
		//--------------------------------------------------------------------------------
		String errMsg1 = "config " + this.configName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PMMM PROPS DEFS:
			errMsg2 = "/pmmm props";
			pmmmPropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// PEER CONFIGS:
			for(int i = 0; i < peerConfigs.size(); i++) {
				errMsg2 = "/peer config";
				peerConfigs.get(i).plausiCheck();
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(707324, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	//================================================================================
	// MTA INTERFACE IMPLEMENTATION (!!! OVERWRITE ALL FUS !!!)
	//================================================================================
	//================================================================================

	//================================================================================
	// MTA
	//================================================================================
	//--------------------------------------------------------------------------------
	// nb: only curPMMM != null
	public void mta(PmmmInstance curPMMM, PeerInstance curPeer, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// PMMM PROPS DEFS:
		// - CAUTION: done by pmmm which integrated these with those of the pmmm type (= defaults) into its processed ones
		//--------------------------------------------------------------------------------
		// PEER CONFIGS:
		for(int i = 0; i < peerConfigs.size(); i++) {
			peerConfigs.get(i).mta(curPMMM, curPeer, curWiringInstance, curEntryType);
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
		buf.append("\n\n");
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.banner("CONFIG:", '*'));
		buf.append(ui.Out.addAlignmentInbetween("CONFIG:", configName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("pmmm props def", pmmmPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		for(int i = 0; i < peerConfigs.size(); i++) {
			buf.append(ui.Out.borderline('-'));
			buf.append(peerConfigs.get(i).toString());
		}
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.banner("END OF CONFIG", '*'));
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================