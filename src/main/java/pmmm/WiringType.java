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
// CODE REVIEWS: 
//================================================================================

package pmmm;

import java.util.Vector;

import eval.tokens.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// NB: wiring type is independent of peer type
public class WiringType implements IEvaluation {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//================================================================================
	//--------------------------------------------------------------------------------
	// WIRING TYPE NAME:
	protected String wiringTypeName = "";
	//--------------------------------------------------------------------------------
	// WIRING TYPE WPROPS TYPES:
	protected PropsTypes wiringTypeWPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// WIRING TYPE WPROS DEFS (= defaults)
	protected TokenExpression wiringTypeWPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// SERVICE NAME:
	protected String serviceName = "";
	//--------------------------------------------------------------------------------
	// GUARDS:
	protected Vector<Guard> guards = new Vector<Guard>();
	//--------------------------------------------------------------------------------
	// ACTIONS:
	protected Vector<Action> actions = new Vector<Action>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTOR
	//================================================================================
	//--------------------------------------------------------------------------------
	public WiringType() {
	}

	//================================================================================
	// GET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getRawWPropsDefs_ofWiringType() {
		return wiringTypeWPropsDefsTokenExpression.getRaw();
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getWiringTypeWPropsDefs_ofWiringType() {
		return wiringTypeWPropsDefsTokenExpression;
	}

	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setWiringTypeName(String wiringTypeName) throws SyntaxException {
		// name validity check 
		// - pass on exc
		PmUtil.isValidName(wiringTypeName, false /* emptyIsAllowedFlag */, false /* wildcardIsAllowedFlag */);
		this.wiringTypeName = wiringTypeName;
	}
	//--------------------------------------------------------------------------------
	public void setRawWPropsTypes(String raw) {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		/**/ tracer.println("wpropsTypes = " + raw, Level.NO, m);
		//--------------------------------------------------------------------------------
		this.wiringTypeWPropsTypes.setRaw(raw);
	}	
	//--------------------------------------------------------------------------------
	public void setServiceName(String serviceName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// name validity check 
			PmUtil.isValidName(serviceName, true /* emptyIsAllowedFlag */, false /* wildcardIsAllowedFlag */);
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(wiringTypeName + ": service", m, e);
		}
		//--------------------------------------------------------------------------------
		// set
		this.serviceName = serviceName;
	}	
	//--------------------------------------------------------------------------------
	public void setGuards(Vector<Guard> guards) {
		this.guards = guards;
	}	
	//--------------------------------------------------------------------------------
	public void setActions(Vector<Action> actions) {
		this.actions = actions;
	}	
	//--------------------------------------------------------------------------------
	public void setRawWiringTypeWPropsDefs(String raw) {
		this.wiringTypeWPropsDefsTokenExpression.setRaw(raw);
	}	

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	public void addGuard(Guard guard) {
		guards.add(guard);
	}	
	//--------------------------------------------------------------------------------
	public void addAction(Action action) {
		actions.add(action);
	}

	//================================================================================
	// GET / QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getWiringTypeName() {
		return wiringTypeName;
	}
	//--------------------------------------------------------------------------------
	public String getServiceName() {
		return serviceName;
	}
	//--------------------------------------------------------------------------------
	public String getRawWPropsTypes() {
		return wiringTypeWPropsTypes.getRaw();
	}	
	//--------------------------------------------------------------------------------
	public Vector<Guard> getGuards() {
		return guards;
	}
	//--------------------------------------------------------------------------------
	public Vector<Action> getActions() {
		return actions;
	}
	//--------------------------------------------------------------------------------
	public PropsTypes getWPropsTypes() {
		return wiringTypeWPropsTypes;
	}
	//--------------------------------------------------------------------------------
	// get number of guard and action links
	// TBD: service links are not counted
	public int getNLinks() {
		return guards.size() + actions.size();
	}
	//--------------------------------------------------------------------------------
	// get number of links for the given container name;
	// TBD: incl. NOOP and CREATE links...
	public int getNLinks(String containerName) {
		int nLinks = 0;
		for(int i = 0; i < guards.size(); i++) {
			if(guards.get(i).containerName.equals(containerName))
				nLinks++;
		}
		for(int i = 0; i < actions.size(); i++) {
			if(actions.get(i).containerName.equals(containerName))
				nLinks++;
		}
		return nLinks;
	}

	//================================================================================
	// CHECK LINK ORDER 
	//================================================================================
	//--------------------------------------------------------------------------------
	// check & sort all guard/action links: all numbers must be there & dense
	// TBD: super doofes sort...
	public void adaptLinkOrder(String peerTypeName /* for debug only */) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// sort GUARDS
		Vector<Guard> sortedGuards = new Vector<Guard>();
		for(int i = 0; i < guards.size(); i++) {
			boolean found = false;
			// nb: human readable numbering starts with 1 and not with 0
			String intS = Integer.toString(i + 1); 
			for(int j = 0; ! found && j < guards.size(); j++) {
				Guard guard = guards.get(j);
				if(guard.linkNumberAsString.equals(intS)) {
					sortedGuards.add(guard);
					found = true;
				}
			}
			if(! found)
				throw new SyntaxException("ill. guard numbering; wiring type = " + wiringTypeName + "; missing guard no. " + (i+1) + ".", m);
		}
		// replace guards by sorted guards
		guards = sortedGuards;
		//--------------------------------------------------------------------------------
		// sort ACTIONS
		Vector<Action> sortedActions = new Vector<Action>();
		for(int i = 0; i < actions.size(); i++) {
			boolean found = false;
			// nb: human readable numbering starts with 1 and not with 0
			String intS = Integer.toString(i + 1);
			for(int j = 0; ! found && j < actions.size(); j++) {
				Action action = actions.get(j);
				if(action.linkNumberAsString.equals(intS)) {
					sortedActions.add(action);
					found = true;
				}
			}
			if(! found) 
				throw new SyntaxException("ill. action numbering; wiring type = " + wiringTypeName + "; missing action no. " + (i+1) + ".", m);
		}
		// replace actions by sorted actions
		actions = sortedActions;
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
		//----------------------------------------------------------------------s----------
		String errMsg1 = "wiring type " + this.wiringTypeName ;
		String errMsg2 = "";
		//----------------------------------------------------------------------s----------
		try {
			//--------------------------------------------------------------------------------
			// WIRING TYPE WPROPS TYPES:
			errMsg2 = ", wprops types";
			wiringTypeWPropsTypes.tokenize();
			//--------------------------------------------------------------------------------
			// WIRING TYPE WPROS DEFS (= defaults)
			errMsg2 = ", wprops defs";
			wiringTypeWPropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// GUARDS:
			errMsg2 = "";
			for(int i = 0; i < guards.size(); i++) {
				guards.get(i).tokenize();
			}
			//--------------------------------------------------------------------------------
			// ACTIONS:
			errMsg2 = "";
			for(int i = 0; i < actions.size(); i++) {
				actions.get(i).tokenize();
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(300099, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		/**/ tracer.println("tokenized wiring type: \n" + this, Level.NO, m);
	}

	//================================================================================
	// DATA TYPE EVAL 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {	
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("wprops types = " + wiringTypeWPropsTypes.getRaw(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// local var
		String errMsg = "";
		//--------------------------------------------------------------------------------
		try {
			//================================================================================
			// WIRING TYPE WPROPS TYPES:
			//================================================================================
			//--------------------------------------------------------------------------------
			// - nb: must be evaluated *before* the links, because qualifier expression WIRING.<wprop> might be used in a lprop
			errMsg = "wiring type wprops types";
			// do it
			wiringTypeWPropsTypes.evalDataTypes(context.switch2WiringType_WPropsTypes());
			//================================================================================
			// WIRING TYPE WPROS DEFS (= defaults)
			//================================================================================
			//--------------------------------------------------------------------------------
			errMsg = "wiring type wprops defs";
			/**/ tracer.println("wiring type wprops defs = " + wiringTypeWPropsDefsTokenExpression.getRaw(), Level.NO, m);
			// - resolve qualifier types
			wiringTypeWPropsDefsTokenExpression.resolveQualifierTypes(context.setWiringQualifierContext(wiringTypeWPropsTypes, wiringTypeName).getQualifierContext());
			// - eval types 
			wiringTypeWPropsDefsTokenExpression.evalDataTypes(context.switch2WiringOrWiringType_WPropsDefs(wiringTypeWPropsTypes));
			//--------------------------------------------------------------------------------
			/**/ tracer.println("wiring type wprops defs = " + wiringTypeWPropsDefsTokenExpression + ";  as pairs = " + wiringTypeWPropsDefsTokenExpression, Level.NO, m);
			//================================================================================
			// GUARDS:
			//================================================================================
			//--------------------------------------------------------------------------------
			// - nb: they do the link context switch by themselves...
			errMsg = "guards";
			for(int i = 0; i < guards.size(); i++) {
				guards.get(i).evalDataTypes(context);
			}
			//================================================================================
			// ACTIONS:
			//================================================================================
			//--------------------------------------------------------------------------------
			// - nb: they do the link context switch by themselves...
			for(int i = 0; i < actions.size(); i++) {
				errMsg = "actions";
				actions.get(i).evalDataTypes(context);
			}
			/**/ tracer.println("TYPED WIRING TYPE: \n" + this, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch(SyntaxException e) {
			// throw new SyntaxException(errMsg, m, e); // too much info
			throw e;
		} catch(SNHException e) {
			throw new SNHException(820045, errMsg, m, e);
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// PLAUSI CHECK 
	//================================================================================
	//--------------------------------------------------------------------------------
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "wiring type " + this.wiringTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// WIRING TYPE WPROPS TYPES:
			errMsg2 = "/wprops types";
			wiringTypeWPropsTypes.plausiCheck();
			//--------------------------------------------------------------------------------
			// WIRING TYPE WPROS DEFS (= defaults)
			errMsg2 = "/wprops defs";
			wiringTypeWPropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			errMsg2 = "";
			//--------------------------------------------------------------------------------
			// GUARDS;
			for(int i = 0; i < guards.size(); i++) {
				guards.get(i).plausiCheck();
			}
			//--------------------------------------------------------------------------------
			// ACTIONS;
			for(int i = 0; i < actions.size(); i++) {
				actions.get(i).plausiCheck();
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(954765, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	//================================================================================
	// VERIFY WIRING TYPE NAME
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// is my wiring type name the given one?
	// - info: needed to verify whether the wiring type name in drawio diagram name equals the modeled one in the diagram...
	public void verifyWiringTypeName(String wiringTypeName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(! this.wiringTypeName.equals(wiringTypeName)) {
			throw new SyntaxException("wiring type name inconsistency: this.wiringTypeName '" + "' vs. '" + wiringTypeName + "'", m);
		}
	}

	//================================================================================
	// STATIC
	//================================================================================
	//--------------------------------------------------------------------------------
	// add guard to wiring type given by its name, in the wiring types vector;
	// add only if the wiring type fits; otherwise do nothing...
	// nb: the ordering and denseness of guard numbers is checked later in an extra pass;
	public static Vector<WiringType> addGuardToWiringTypes(Vector<WiringType> wiringTypes, String wiringTypeName, Guard guard) {
		for(int i = 0; i < wiringTypes.size(); i++) {
			WiringType wiringType = wiringTypes.get(i);
			if(wiringType.getWiringTypeName().equals(wiringTypeName)) {
				wiringType.addGuard(guard);
			}
		}
		return wiringTypes;
	}

	//--------------------------------------------------------------------------------
	// add action to wiring type given by its name, in the wiring types vector;
	// add only if the wiring type fits; otherwise do nothing...
	// nb: the ordering and denseness of guard numbers is checked later in an extra pass;
	public static Vector<WiringType> addActionToWiringTypes(Vector<WiringType> wiringTypes, String wiringTypeName, Action action) {
		for(int i = 0; i < wiringTypes.size(); i++) {
			WiringType wiringType = wiringTypes.get(i);
			if(wiringType.getWiringTypeName().equals(wiringTypeName)) {
				wiringType.addAction(action);
			}
		}
		return wiringTypes;
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
		// WIRING TYPE NAME:
		// TBD: do not print if it starts with "_" ... i.e. it is the type of an inline wiring; hard coded...
		buf.append(ui.Out.addAlignmentInbetween("Wiring Type:", wiringTypeName + "\n"));
		//--------------------------------------------------------------------------------
		// WPROPS TYPES:
		buf.append(PmUtil.propsTypes2StructuredString("wprops types", wiringTypeWPropsTypes));
		//--------------------------------------------------------------------------------
		// WIRING TYPE WPROPS DEFS:
		buf.append(PmUtil.tokenExpression2StructuredString("wiring type wprops defs", wiringTypeWPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		// SERVICE:
		buf.append(ui.Out.addAlignmentInbetween("service:", serviceName + "\n"));
		//--------------------------------------------------------------------------------
		// GUARDS:
		for(int i = 0; i < guards.size(); i++) {
			if(i == 0)
				buf.append(ui.Out.borderline('.'));
			buf.append(guards.get(i).toString());
			buf.append(ui.Out.borderline('.'));
		}
		//--------------------------------------------------------------------------------
		// ACTIONS:
		for(int i = 0; i < actions.size(); i++) {
			buf.append(actions.get(i).toString());
			buf.append(ui.Out.borderline('.'));
		}
		//--------------------------------------------------------------------------------

		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
