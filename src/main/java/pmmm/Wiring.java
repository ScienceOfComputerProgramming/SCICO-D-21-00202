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
import qa.tracer.O;

//================================================================================
// wiring specification
public class Wiring implements IEvaluation {
	//--------------------------------------------------------------------------------
	// /**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// pointer to my WIRING TYPE (shared): 
	protected WiringType wiringType = new WiringType();
	//--------------------------------------------------------------------------------
	// WIRING NAMES:
	// - nb: could be a complex expression resolving to many names
	protected TokenExpression wiringNamesTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// WIRING WPROPS DEFS (orig): 
	protected TokenExpression wiringWPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public Wiring() {
	}

	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setWiringTypeName(String wiringTypeName) throws SyntaxException {
		// name validity check 
		// - pass on exc
		PmUtil.isValidName(wiringTypeName, false /* emptyIsAllowedFlag */, false /* wildcardIsAllowedFlag */);
		wiringType.wiringTypeName = wiringTypeName;
	}
	//--------------------------------------------------------------------------------
	public void setWiringType(WiringType wiringType) {
		this.wiringType = wiringType;
	}
	//--------------------------------------------------------------------------------
	public void setServiceName(String serviceName) throws SyntaxException {
		wiringType.setServiceName(serviceName);
	}	

	//================================================================================
	// GET 
	// - caution: add "Wiring" in the fu names to differentiate them from the inherited ones of the wiring type
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getRawWiringWPropsDefs() {
		return wiringWPropsDefsTokenExpression.getRaw();
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getWiringWPropsDefs() {
		return wiringWPropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public String getRawWiringNames() {
		return this.wiringNamesTokenExpression.getRaw();
	}
	//--------------------------------------------------------------------------------
	public String getServiceName() {
		return wiringType.serviceName;
	}
	//--------------------------------------------------------------------------------
	public Vector<Guard> getGuards() {
		return wiringType.guards;
	}
	//--------------------------------------------------------------------------------
	public Vector<Action> getActions() {
		return wiringType.actions;
	}
	//--------------------------------------------------------------------------------
	// get number of links for the given container name;
	// TBD: incl. NOOP and CREATE links...
	public int getNLinks(String containerName) {
		return wiringType.getNLinks(containerName);
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	public void addGuard(Guard guard) {
		wiringType.guards.add(guard);
	}	
	//--------------------------------------------------------------------------------
	public void addAction(Action action) {
		wiringType.actions.add(action);
	}

	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setRawWiringNames(String raw) {
		this.wiringNamesTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setRawWiringWPropsDefs(String raw) {
		this.wiringWPropsDefsTokenExpression.setRaw(raw);
	}	

	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	// - caution: do not change the order!
	//================================================================================
	//================================================================================

	//================================================================================
	// TOKENIZE 
	//================================================================================
	//--------------------------------------------------------------------------------
	// tokenize all values
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "wiring " + this.getRawWiringNames();
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// WIRING NAMES:
			errMsg2 = "/wiring names " + this.getRawWiringNames();
			wiringNamesTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// WIRING WPROPS DEFS: 
			errMsg2 = "/wprops defs " + this.wiringWPropsDefsTokenExpression.toInfo();
			wiringWPropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// WIRING TYPE:
			errMsg2 = "/wiring type";
			wiringType.tokenize();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// DATA TYPE EVAL 
	//================================================================================
	//--------------------------------------------------------------------------------
	// eval all token types
	// - and merge wiring wprops defs with those of its wiring type (defaults); 
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {	
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "wiring = " + this.getRawWiringNames();
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// WIRING NAMES:
			errMsg2 = " / wiring name(s) " + this.getRawWiringNames();
			// - resolve qualifier types
			wiringNamesTokenExpression.resolveQualifierTypes(context.setWiringQualifierContext(wiringType.wiringTypeWPropsTypes, wiringType.wiringTypeName).getQualifierContext()); 
			// - eval data types
			wiringNamesTokenExpression.evalDataTypes(context.switch2WiringNames());
			//--------------------------------------------------------------------------------
			// WIRING WPROPS DEFS:
			errMsg2 = " / wprops defs";
			// - resolve qualifier types
			wiringWPropsDefsTokenExpression.resolveQualifierTypes(context.setWiringQualifierContext(wiringType.wiringTypeWPropsTypes, wiringType.wiringTypeName).getQualifierContext());
			// - eval data types
			wiringWPropsDefsTokenExpression.evalDataTypes(context.switch2WiringOrWiringType_WPropsDefs(wiringType.getWPropsTypes()));
			//--------------------------------------------------------------------------------
			// WIRING TYPE:
			// TBD: only necessary for inline wirings? TBD '_' is hardcoded
			if(! this.wiringType.wiringTypeName.startsWith("_")) 
				errMsg2 = " / wiring type = " + this.wiringType.wiringTypeName;
			this.wiringType.evalDataTypes(context.switch2WiringType(this.getRawWiringNames()));  
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw new SyntaxException(errMsg1, m, e);
		} catch (SNHException e) {
			throw new SNHException(390001, errMsg1 + errMsg2, m, e);
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
		String errMsg1 = "wiring " + this.getRawWiringNames();
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// WIRING NAMES:
			errMsg2 = "/wiring name(s) " + this.getRawWiringNames();
			wiringNamesTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// WIRING WPROPS DEFS:
			errMsg2 = "/wprops defs";
			wiringWPropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// WIRING TYPE:
			errMsg2 = "/wiring type";
			this.wiringType.plausiCheck();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(865432, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	//================================================================================
	// STATIC FUS
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// add guard to wiring given by its name, in the wirings vector;
	// add only if the wiring fits; otherwise do nothing...
	// nb: the ordering and denseness of guard numbers is checked later in an extra pass;
	// CAUTION: wiring name could be a complex expression
	// TBD: maybe there is a better way to find the wiring?!
	public static Vector<Wiring> addGuardToWirings(Vector<Wiring> wirings, String wiringName, Guard guard) {
		for(int i = 0; i < wirings.size(); i++) {
			Wiring wiring = wirings.get(i);
			if(wiring.getRawWiringNames().equals(wiringName)) {
				wiring.wiringType.addGuard(guard);
			}
		}
		return wirings;
	}

	//--------------------------------------------------------------------------------
	// add action to wiring given by its name, in the wirings vector;
	// add only if the wiring fits; otherwise do nothing...
	// nb: the ordering and denseness of guard numbers is checked later in an extra pass;
	// CAUTION: wiring name could be a complex expression
	// TBD: maybe there is a better way to find the wiring?!
	public static Vector<Wiring> addActionToWirings(Vector<Wiring> wirings, String wiringName, Action action) {
		for(int i = 0; i < wirings.size(); i++) {
			Wiring wiring = wirings.get(i);
			if(wiring.getRawWiringNames().equals(wiringName)) {
				wiring.wiringType.addAction(action);
			}
		}
		return wirings;
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
		buf.append(ui.Out.addAlignmentInbetween("Wiring:", getRawWiringNames() + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("wprops defs", wiringWPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(wiringType.toStructuredString());
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
