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

import eval.tokens.TokenExpression;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
public class PeerType implements IEvaluation {
	//--------------------------------------------------------------------------------
	// for debug
	private Tracer tracer = new Tracer();
	//================================================================================
	//--------------------------------------------------------------------------------
	// PEER TYPE NAME:
	protected String peerTypeName = "";
	//--------------------------------------------------------------------------------
	// PEER TYPE PPROPS TYPES: 
	PropsTypes peerTypePPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// PEER TYPE PPROPS DEFS (defaults): 
	TokenExpression peerTypePPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// WIRINGS:
	protected Vector<Wiring> wirings = new Vector<Wiring>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PeerType() {
	}
	//--------------------------------------------------------------------------------
	public PeerType(String peerTypeName) throws SyntaxException {
		setPeerTypeName(peerTypeName);
	}


	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setPeerTypeName(String peerTypeName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// name validity check 
		try {
			PmUtil.isValidName(peerTypeName, false /* emptyIsAllowedFlag */, false /* wildcardIsAllowedFlag */);
		} catch (SyntaxException e) {
			throw new SyntaxException("ill. peer type name", m, e);
		}
		// set
		this.peerTypeName = peerTypeName;
	}
	//--------------------------------------------------------------------------------
	public void setRawPPropsTypes(String raw) {
		this.peerTypePPropsTypes.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setRawPeerTypePPropsDefs(String raw) {
		peerTypePPropsDefsTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setWirings(Vector<Wiring> wirings) {
		this.wirings = wirings;
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getPeerTypeName() {
		return peerTypeName;
	}
	//--------------------------------------------------------------------------------
	public Vector<Wiring> getWirings() {
		return wirings;
	}
	//--------------------------------------------------------------------------------
	public PropsTypes getPeerTypePPropsTypes() {
		return peerTypePPropsTypes;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getPeerTypePPropsDefsTokenExpression() {
		return peerTypePPropsDefsTokenExpression;
	}

	//================================================================================
	// VERIFY PEER TYPE NAME
	//================================================================================
	//--------------------------------------------------------------------------------
	// is my peer type name the given one?
	// - info: needed to verify whether the peer type name in drawio diagram name equals the modeled one in the diagram...
	public void verifyPeerTypeName(String peerTypeName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(! this.peerTypeName.equals(peerTypeName)) {
			throw new SyntaxException("peer type name inconsistency: this.peerTypeName '" + "' vs. '" + peerTypeName + "'", m);
		}
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add wiring
	public void addWiring(Wiring wiring) {
		wirings.add(wiring);
	}
	//--------------------------------------------------------------------------------
	// add guard to wiring;
	// nb: the ordering and denseness of guard numbers is checked later in an extra pass;
	// CAUTION: wiring name could be a complex expression
	// TBD: maybe there is a better way to find the wiring?!
	public void addGuardToWiring(String wiringName, Guard guard) {
		for(int i = 0; i < wirings.size(); i++) {
			Wiring wiring = wirings.get(i);
			if(wiring.getRawWiringNames().equals(wiringName)) {
				wiring.wiringType.addGuard(guard);
			}
		}
	}
	//--------------------------------------------------------------------------------
	// add action to wiring;
	// nb: the ordering and denseness of action numbers is checked later in an extra pass;
	// CAUTION: wiring name could be a complex expression
	// TBD: maybe there is a better way to find the wiring?!
	public void addActionToWiring(String wiringName, Action action) {
		for(int i = 0; i < wirings.size(); i++) {
			Wiring wiring = wirings.get(i);
			if(wiring.getRawWiringNames().equals(wiringName)) {
				wiring.wiringType.addAction(action);
			}
		}
	}

	//================================================================================
	// ADD WIRING TYPE TO NON-INLINE WIRINGS
	//================================================================================
	//--------------------------------------------------------------------------------
	// for all peers types: add wiring type to non-inline wirings 
	// - ie wiring that is defined via "wiring config" based on a wiring type;
	public void addWiringTypeToNonInlineWirings(Vector<WiringType> wiringTypes) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// for all wirings
		for(int j = 0; j < wirings.size(); j++) {
			//--------------------------------------------------------------------------------
			// get next wiring
			Wiring wiring = this.wirings.get(j);
			//--------------------------------------------------------------------------------
			// get next wiring type name
			String wiringTypeName = wiring.wiringType.getWiringTypeName();
			//--------------------------------------------------------------------------------
			// non-inline wiring?
			if(! wiringTypeName.startsWith("_")) {
				/**/ tracer.println("NON-INLINE WIRING = " + wiring.getRawWiringNames() + "; of WIRING TYPE = " + wiringTypeName, Level.NO, m);	
				//--------------------------------------------------------------------------------
				// search the wiring type 
				boolean wiringTypeFoundFlag = false;
				for(int k = 0; (! wiringTypeFoundFlag) && (k < wiringTypes.size()); k++) {
					//--------------------------------------------------------------------------------
					// get next wiring type
					WiringType wiringType = wiringTypes.get(k);
					/**/ tracer.println("next wiringType = " + wiringType.getWiringTypeName(), Level.NO, m);	
					//--------------------------------------------------------------------------------
					// check if its name fits
					if(wiringType.getWiringTypeName().equals(wiringTypeName)) {
						//--------------------------------------------------------------------------------
						// wiring type found 
						wiringTypeFoundFlag = true;
						//--------------------------------------------------------------------------------
						// set it in wiring
						wiring.setWiringType(wiringType);
					}
				}
				//--------------------------------------------------------------------------------
				// if wiring type not found -> error
				if(! wiringTypeFoundFlag) {
					throw new SyntaxException("missing wiring type '" + wiringTypeName + "'", m);
				}
			}
			// for inline wirings: nothing needs to be done; just keep them "as is"
		}
	}

	//================================================================================
	// CHEK LINK ORDER
	//================================================================================
	//--------------------------------------------------------------------------------
	// check & sort all links
	public void adaptLinkOrder() throws SyntaxException {
		for(int i = 0; i < wirings.size(); i++) {
			// just pass on exception
			wirings.get(i).wiringType.adaptLinkOrder(peerTypeName /* peer name; for debug only */);
		}
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
	// tokenize all values in the meta model
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "peer type " + this.peerTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PEER TYPE PPROPS TYPES: 
			errMsg2 = "/pprops types";
			peerTypePPropsTypes.tokenize();
			//--------------------------------------------------------------------------------
			// PEER TYPE PPROPS DEFS (defaults): 
			errMsg2 = "/pprops defs";
			peerTypePPropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// WIRINGS:
			errMsg2 = "";
			for(int i = 0; i < wirings.size(); i++) {
				wirings.get(i).tokenize();
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(550524, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// EVAL TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate all token types
	// nb: also transforms props types into name declaration pairs
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "peer type = " + this.peerTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PEER TYPE PPROPS TYPES: 
			errMsg2 = " / pprops types";
			peerTypePPropsTypes.evalDataTypes(context.switch2PeerType_PPropsTypes());
			//--------------------------------------------------------------------------------
			// PEER TYPE PPROPS DEFS (defaults): 
			errMsg2 = " / pprops defs";
			// - resolve qualifier types
			peerTypePPropsDefsTokenExpression.resolveQualifierTypes(context.setPeerQualifierContext(peerTypePPropsTypes, this.peerTypeName).getQualifierContext());
			// - eval data types
			peerTypePPropsDefsTokenExpression.evalDataTypes(context.switch2PeerType_PPropsDefs(peerTypePPropsTypes));
			//--------------------------------------------------------------------------------
			// WIRINGS: 
			// - nb: wiring cares for its necessary context switches by itself
			// -- and also calls eval of its integrated wiring type
			errMsg2 = " / wirings";
			for(int i = 0; i < wirings.size(); i++) {
				/**/ tracer.println("WIRING " + wirings.get(i).getRawWiringNames(), Level.NO, m);
				wirings.get(i).evalDataTypes(context);
				//--------------------------------------------------------------------------------
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(320023, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// PLAUSI CHECK
	//================================================================================
	//--------------------------------------------------------------------------------
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "peer type " + this.peerTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PEER TYPE PPROPS TYPES: 
			errMsg2 = "/pprops types";
			peerTypePPropsTypes.plausiCheck();
			//--------------------------------------------------------------------------------
			// PEER TYPE PPROPS DEFS (defaults): 
			errMsg2 = "/pprops defs";
			peerTypePPropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// WIRINGS
			errMsg2 = "/wirings";
			for(int i = 0; i < wirings.size(); i++) {
				wirings.get(i).plausiCheck();
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(467890, errMsg1 + errMsg2, m, e);
		}
	}

	//	//================================================================================
	//	//================================================================================
	//	// MTA INTERFACE IMPLEMENTATION (!!! OVERWRITE ALL FUS !!!)
	//	//================================================================================
	//	//================================================================================
	//
	//	//================================================================================
	//	// MTA
	//	//================================================================================
	//	//--------------------------------------------------------------------------------
	//	// nb: mta is called by peer by pmmm; ie if the model is instantiated!!!
	//	public void mta(PMMM curPMMM, PeerInstance curPeer, Wiring curWiring, EntryType curEntryType) throws SyntaxException, SNHException {
	//		//--------------------------------------------------------------------------------
	//		// /**/ O m = new O(){}; // debug
	//		//--------------------------------------------------------------------------------
	//		// PEER TYPE PPROPS DEFS (defaults): 
	//		// done by peer
	//		//--------------------------------------------------------------------------------
	//		// WIRINGS:
	//		for(int i = 0; i < wirings.size(); i++) {
	//			Wiring wiring = wirings.get(i);
	//			wiring.mta(curPMMM, curPeer, wiring /* curWiring */, curEntryType);
	//		}
	//	}

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
	// print in a structured way: 
	public String toStructuredString() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("Peer Type:", peerTypeName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.propsTypes2StructuredString("peer type pprops types", peerTypePPropsTypes));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("peer type pprops defs", peerTypePPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		for(int i = 0; i < wirings.size(); i++) {
			buf.append(ui.Out.banner("WIRING:", '-'));
			buf.append(wirings.get(i).toStructuredString());
		}
		if(wirings.size() > 0)
			buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------

		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================

