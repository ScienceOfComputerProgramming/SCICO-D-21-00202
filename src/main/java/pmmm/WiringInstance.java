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
public class WiringInstance implements IMta {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// pointer to WIRING CONFIG (only shared between respective wiring instances of this config):
	WiringConfig wiringConfig; 
	//--------------------------------------------------------------------------------
	// WIRING INSTANCE NAME:
	// - as string
	String wiringInstanceName = "";
	// - as token
	// -- needed to resolve WINDEX.<i> expressions
	Token wiringInstanceNameToken = new Token(IToken.Kind.EMPTY, "" /* raw */);
	//================================================================================
	// COMPUTED:
	//================================================================================
	//--------------------------------------------------------------------------------
	// - WIRING WPROPS DEFS (processed): 
	protected TokenExpression processedWPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// - GUARD INSTANCES:
	protected Vector<LinkInstance> guardInstances = new Vector<LinkInstance>();
	//--------------------------------------------------------------------------------
	// - GUARD INSTANCES:
	protected Vector<LinkInstance> actionInstances = new Vector<LinkInstance>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public WiringInstance() {
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getWiringInstanceName() {
		return wiringInstanceName;
	}
	//--------------------------------------------------------------------------------
	public Token getWiringInstanceNameToken() {
		return wiringInstanceNameToken;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedWiringWPropsDefs() {
		return processedWPropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public String getWiringTypeName() {
		return wiringConfig.wiring.wiringType.wiringTypeName;
	}
	//--------------------------------------------------------------------------------
	public String getServiceName() {
		return wiringConfig.wiring.wiringType.serviceName;
	}
	//--------------------------------------------------------------------------------
	public int getNLinks() {
		return wiringConfig.wiring.wiringType.getNLinks();
	}
	//--------------------------------------------------------------------------------
	public Vector<LinkInstance> getGuardInstances() {
		return guardInstances;
	}
	//--------------------------------------------------------------------------------
	public Vector<LinkInstance> getActionInstances() {
		return actionInstances;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedWiringNamesTokenExpression() {
		return wiringConfig.processedWiringNamesTokenExpression;
	}
 	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// get value of a wprop def from the processed ones
	// nb: mta has already been applied!
	public Token getProcessedWPropDefsValueToken(Token propNameToken) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.nlprintln("searching for " + propNameToken + " in " + this.processedWPropsDefsTokenExpression, Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			// search for propNameToken
			Token token = processedWPropsDefsTokenExpression.getPropValueToken(propNameToken);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("processed props defs: " + processedWPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			/**/ tracer.println("found: '" + propNameToken + "'; prop def: " + token.toUserInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			return token;
		} catch(NotFoundException e) {
			throw new SyntaxException("pmmm prop def", m, e);
		}
	}

	//================================================================================
	// CONSTRUCT LINK INSTANCES:
	//================================================================================
	//--------------------------------------------------------------------------------
	// construct link instances for guards and wirink´gs
	public void constructLinkInstances() 
			throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		// guards
		for(int i = 0; i < wiringConfig.wiring.wiringType.guards.size(); i++) {
			// create new link instance
			LinkInstance linkInstance = new LinkInstance();
			// set pointer to GUARD link
			linkInstance.link = wiringConfig.wiring.wiringType.guards.get(i);
			// add
			guardInstances.add(linkInstance);
		}
		//--------------------------------------------------------------------------------
		// actions
		for(int i = 0; i < wiringConfig.wiring.wiringType.actions.size(); i++) {
			// create new link instance
			LinkInstance linkInstance = new LinkInstance();
			// set pointer to ACTION link
			linkInstance.link = wiringConfig.wiring.wiringType.actions.get(i);
			// add
			actionInstances.add(linkInstance);
		}
	}

	//================================================================================
	//================================================================================
	// MTA INTERFACE IMPLEMENTATION
	// - nb: do also apply to my personal wiring type clone
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	public void mta(PmmmInstance curPmmmInstance, PeerInstance curPeerInstance, WiringInstance curWiringInstance, EntryType curEntryType) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		String errMsg = "wiring instance = '" + wiringInstanceName + "'";
		//--------------------------------------------------------------------------------
		// assertions:
		if(curPmmmInstance == null)
			throw new SNHException(913764, "curPMMM is null", m);
		if(curPeerInstance == null)
			throw new SNHException(977444, "curPeer is null", m);
		if(curWiringInstance == null)
			throw new SNHException(226772, "curWiring is null", m);
		if(curWiringInstance != this)
			throw new SNHException(220212, "curWiring is not me", m);
		if(curEntryType != null)
			throw new SNHException(220002, "curEntryType is not null", m);
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PROCESSED WIRING WPROPS DEFS
			// - iteratively resolve qualifier values and array access until nothing needs to be done any more
			processedWPropsDefsTokenExpression.deepCloneMergeMta_Assignments(
					wiringConfig.wiring.wiringWPropsDefsTokenExpression /* orig */, 
					wiringConfig.wiring.wiringType.wiringTypeWPropsDefsTokenExpression /* defaults */, 
					curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
			/**/ tracer.println("processed wiring wprops defs = " + this.processedWPropsDefsTokenExpression.toUserInfo(true, true), Level.NO, m);
			//--------------------------------------------------------------------------------
			// GUARD INSTANCES:
			for(int i = 0; i < guardInstances.size(); i++) {
				guardInstances.get(i).mta(curPmmmInstance, curPeerInstance, this /* curWiringInstance */, curEntryType);
			}	
			//--------------------------------------------------------------------------------
			// ACTION INSTANCES:
			for(int i = 0; i < actionInstances.size(); i++) {
				actionInstances.get(i).mta(curPmmmInstance, curPeerInstance, this /* curWiringInstance */, curEntryType);
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg, m, e);
		} catch (SNHException e) {
			throw new SNHException(789012, errMsg, m, e);
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
		buf.append(ui.Out.addAlignmentInbetween("WIRING Instance:", wiringInstanceName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("wprops defs", this.wiringConfig.wiring.wiringWPropsDefsTokenExpression));
		buf.append(PmUtil.tokenExpression2StructuredString(" - processed", processedWPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		for(int i = 0; i < guardInstances.size(); i++) {
			if(i == 0)
				buf.append(ui.Out.borderline('.'));
			buf.append(guardInstances.get(i).toStructuredString());
			buf.append(ui.Out.borderline('.'));
		}
		//--------------------------------------------------------------------------------
		for(int i = 0; i < actionInstances.size(); i++) {
			if(i == 0)
				buf.append(ui.Out.borderline('.'));
			buf.append(actionInstances.get(i).toStructuredString());
			buf.append(ui.Out.borderline('.'));
		}
		//--------------------------------------------------------------------------------
		return new String(buf);
	}

} // END OF CLASS


//================================================================================
//EOF
//================================================================================
