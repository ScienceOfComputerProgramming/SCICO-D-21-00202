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
// CODE REVIEWS: 20210113 (eK); 20210209 (eK);
//================================================================================

package pmmm;
import java.util.Vector;
import parser.*;

import eval.tokens.TokenExpression;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// PMMM ... Peer Model Meta Model;
public class PmmmType implements IEvaluation {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer(); // debug
	//--------------------------------------------------------------------------------
	// peer type names vector that is artificially constructed for data type evaluation
	protected Vector<String> peerTypeNames = new Vector<String>();
	//--------------------------------------------------------------------------------
	// keyword declarations 
	protected KeywordTypes keywordTypes;
	//================================================================================
	//--------------------------------------------------------------------------------
	// PMMM TYPE NAME:
	protected String pmmmTypeName = "";
	//--------------------------------------------------------------------------------
	// PMMM TYPE PMMM PROPS TYPES:
	protected PropsTypes pmmmTypePmmmPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// PMMM TYPE PMMM PROPS DEFS (defaults):
	protected TokenExpression pmmmTypePmmmPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// ENTRY TYPES:
	// - init entry types with sys entry types
	protected EntryTypes entryTypes = new EntryTypes();
	//--------------------------------------------------------------------------------
	// WIRING TYPES:
	protected Vector<WiringType> wiringTypes = new Vector<WiringType>();
	//--------------------------------------------------------------------------------
	// PEER TYPES:
	protected Vector<PeerType> peerTypes = new Vector<PeerType>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmmmType() throws SNHException {
		//--------------------------------------------------------------------------------
		// init keywords
		this.keywordTypes = new KeywordTypes();
	}
	//--------------------------------------------------------------------------------
	public PmmmType(PmmmComponents pmmmComponents) throws SNHException {
		//--------------------------------------------------------------------------------
		// init keywords
		this.keywordTypes = new KeywordTypes();
		//--------------------------------------------------------------------------------
		// copy all pmmm type relevant components into my object
		// - caution: keep up-to-date
		//--------------------------------------------------------------------------------
		// PMMM TYPE NAME:
		this.pmmmTypeName = pmmmComponents.pmmmTypeName;
		//--------------------------------------------------------------------------------
		// PMMM TYPE PMMM PROPS TYPES:
		this.pmmmTypePmmmPropsTypes = pmmmComponents.pmmmTypePmmmPropsTypes;
		//--------------------------------------------------------------------------------
		// PMMM TYPE PMMM PROPS DEFS: 
		this.pmmmTypePmmmPropsDefsTokenExpression = pmmmComponents.pmmmTypePmmmPropsDefsTokenExpression;
		//--------------------------------------------------------------------------------
		// ENTRY TYPES:
		this.entryTypes = pmmmComponents.entryTypes;
		//--------------------------------------------------------------------------------
		// WIRING TYPES:
		this.wiringTypes = pmmmComponents.wiringTypes;
		//--------------------------------------------------------------------------------
		// PEER TYPES:
		this.peerTypes = pmmmComponents.peerTypes;
	}

	//================================================================================
	// SET 
	//================================================================================
	//--------------------------------------------------------------------------------
	// PMMM TYPE NAME:
	//--------------------------------------------------------------------------------
	public void setPmmmTypeName(String pmmmTypeName) {
		this.pmmmTypeName = pmmmTypeName;
	}
	//--------------------------------------------------------------------------------
	// PMMM TYPE PROPS TYPES:
	//--------------------------------------------------------------------------------
	public void setRawPmmmPropsTypes(String raw) {
		this.pmmmTypePmmmPropsTypes.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setPmmmPropsTypes(PropsTypes pmmmPropsTypes) {
		this.pmmmTypePmmmPropsTypes = pmmmPropsTypes;
	}
	//--------------------------------------------------------------------------------
	// PMMM TYPE PROPS DEFS:
	//--------------------------------------------------------------------------------
	public void setRawPmmmPropsDefs_ofPmmmType(String raw) {
		this.pmmmTypePmmmPropsDefsTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	// ENTRY TYPES:
	//--------------------------------------------------------------------------------
	public void setEntryTypes(EntryTypes entryTypes) throws SyntaxException {
		this.entryTypes = entryTypes;
	}
	//--------------------------------------------------------------------------------
	// WIRING TYPES:
	//--------------------------------------------------------------------------------
	public void setWiringTypes(Vector<WiringType> wiringTypes) {
		this.wiringTypes = wiringTypes;
	}
	//--------------------------------------------------------------------------------
	// PEER TYPES:
	//--------------------------------------------------------------------------------
	public void setPeerTypes(Vector<PeerType> peerTypes) {
		this.peerTypes = peerTypes;
	}

	//================================================================================
	// GET / QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// PMMM TYPE NAME:
	//--------------------------------------------------------------------------------
	public String getPmmmTypeName() {
		return this.pmmmTypeName;
	}
	//--------------------------------------------------------------------------------
	// PMMM TYPE PROPS TYPES:
	//--------------------------------------------------------------------------------
	public PropsTypes getPmmmTypePropsTypes() {
		return pmmmTypePmmmPropsTypes;
	}
	//--------------------------------------------------------------------------------
	// PMMM TYPE PROPS DEFS:
	//--------------------------------------------------------------------------------
	public TokenExpression getPmmmTypePmmmPropsDefs() {
		return pmmmTypePmmmPropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	// ENTRY TYPES:
	//--------------------------------------------------------------------------------
	public EntryTypes getEntryTypes() {
		return entryTypes;
	}
	//--------------------------------------------------------------------------------
	// WIRING TYPES:
	//--------------------------------------------------------------------------------
	// tbd: unused
	public Vector<WiringType> getWiringTypes() {
		return wiringTypes;
	}
	//--------------------------------------------------------------------------------
	// PEER TYPES:
	//--------------------------------------------------------------------------------
	public Vector<PeerType> getPeerTypes() {
		return peerTypes;
	}
	//--------------------------------------------------------------------------------
	// PEER TYPE:
	//--------------------------------------------------------------------------------
	public PeerType getPeerType(String peerTypeName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		for(int i = 0; i < this.peerTypes.size(); i++) {
			if(peerTypes.get(i).peerTypeName.equals(peerTypeName))
				return peerTypes.get(i);
		}
		throw new SyntaxException("peer type " + peerTypeName + "not found", m);
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// is name a sys const name?
	public boolean isSysConst(String name) {
		return keywordTypes.isSysConst(name);
	}

	//================================================================================
	// EVALUATE 
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluation control center: 
	// - translate a 'raw' PMMM Type into one that is tokenized, type evaluated and plausi checked,
	// - and where wiring types are integrated into their individual wirings by deep copying;
	// - CAUTION: do not change the evaluation order;
	public void evaluate() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// check pmmm type name expression not to be empty
		if(util.Util.isEmptyString(pmmmTypeName))
			throw new SyntaxException("empty Peer Model Meta Model (PMMM) name; possible cause: \n" + 
					"(*) is the XML export ok (nb: must not be compressed xml)?", m);
		//================================================================================
		// CREATE ARTIFICIAL VECTOR WITH ALL PEER NAMES:
		//================================================================================
		// CAUTION: 
		// - needed for url check by eval;
		// - needed for config treatment;
		for(int i = 0; i < peerTypes.size(); i++) {
			peerTypeNames.add(peerTypes.get(i).getPeerTypeName());
		}
		//================================================================================
		// RAW INTEGRATE WIRING TYPES INTO NON-INLINE WIRINGS
		//================================================================================
		/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
		/**/ tracer.println("'RAW' PMMM TYPE: INTEGRATE WIRING TYPES", Level.INFO, m);	
		//--------------------------------------------------------------------------------
		// for all peer types
		for(int i = 0; i < peerTypes.size(); i++) {
			//--------------------------------------------------------------------------------
			// get next peer type
			PeerType peerType = peerTypes.get(i);
			/**/ tracer.println("peerType = " + peerType.getPeerTypeName(), Level.NO, m);	
			//--------------------------------------------------------------------------------
			// integrate *raw info* of wiring types into peer type's non-inline wirings
			// - nb: we are *before* tokenization
			peerType.addWiringTypeToNonInlineWirings(wiringTypes);
		}
		/**/ tracer.println("'RAW' PMMM WITH WIRINGS ENHANCED BY WIRING TYPES:\n" + this, Level.NO, new Object(){});
		//--------------------------------------------------------------------------------
		// local var:
		String errMsg = "";
		try {
			//================================================================================
			// TOKENIZE
			//================================================================================
			/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
			/**/ tracer.println("'RAW' PMMM TYPE: TOKENIZE", Level.INFO, m);	
			errMsg = "ill. expression syntax";
			//--------------------------------------------------------------------------------
			// tokenize all expressions
			tokenize();
			//================================================================================
			// SORT LINKS
			//================================================================================
			/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
			/**/ tracer.println("PMMM TYPE: SORT LINKS", Level.INFO, m);	
			errMsg = "ill. link numbering";
			//--------------------------------------------------------------------------------
			// sort guard and actions links and check for duplicates and/or missing links
			adaptLinkOrder();
			//================================================================================
			// EVAL DATA TYPES
			//================================================================================
			/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
			/**/ tracer.println("PMMM TYPE: EVAL DATA TYPES", Level.INFO, m);	
			errMsg = "data type error";
			//--------------------------------------------------------------------------------
			// create new context with the global pmmm type facts 
			// nb: pmmmTypePmmmPropsTypes are still not type evaluated... but they are shared so context will be changed implicitly!!!
			Context context = new Context(keywordTypes, pmmmTypeName, pmmmTypePmmmPropsTypes, peerTypeNames, peerTypes, entryTypes);
			//--------------------------------------------------------------------------------
			// eval data types
			evalDataTypes(context);
			//================================================================================
			// PLAUSI CHECK
			//================================================================================
			/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
			/**/ tracer.println("PMMM TYPE: PLAUSI CHECK", Level.INFO, m);	
			errMsg = "plausibility check";
			//--------------------------------------------------------------------------------
			// check plausibility of the pmmm type
			plausiCheck();
			//================================================================================
			// SUCCESS
			//================================================================================
			/**/ tracer.println(ui.Out.border('-'), Level.INFO, m);	
			/**/ tracer.println("PMMM TYPE: SUCCESSFULLY EVALUATED", Level.INFO, m);	
			/**/ tracer.println(this.toString(), Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {		
			// throw new SyntaxException(errMsg, m, e); // too much info 
			throw e;
		} catch (SNHException e) {
			throw new SNHException(101010, errMsg, m, e);
		}	

	}

	//================================================================================
	// VERIFY LINK ODER
	//================================================================================
	//--------------------------------------------------------------------------------
	// check & sort all guard/action links
	private void adaptLinkOrder() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		for(int i = 0; i < peerTypes.size(); i++) {
			try {
				peerTypes.get(i).adaptLinkOrder();
			} catch (SyntaxException e) {
				throw new SyntaxException("peer type '" + peerTypes.get(i).getPeerTypeName() + "'", m, e);
			}
		}
	}

	//================================================================================
	//================================================================================
	// EVALUATION INTERFACE IMPLEMENTATION
	// - nb: processed fields do not yet exist
	// - nb: keep the order! 
	//================================================================================
	//================================================================================

	//================================================================================
	// TOKENIZATION
	//================================================================================
	//--------------------------------------------------------------------------------
	// tokenize all expressions
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "pmmm type = " + this.pmmmTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PMMM TYPE PMMM PROPS TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = " / pmmm props types";
			// - tokenize
			pmmmTypePmmmPropsTypes.tokenize();
			//--------------------------------------------------------------------------------
			// PMMM TYPE PMMM PROPS DEFS (defaults):
			//--------------------------------------------------------------------------------
			errMsg2 = " / pmmm props defs (defaults)";
			// - tokenize
			pmmmTypePmmmPropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// ENTRY TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = "";
			// - tokenize
			entryTypes.tokenize();
			//--------------------------------------------------------------------------------
			// WIRING TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = " / wiring types";
			// - tokenize
			for(int i = 0; i < wiringTypes.size(); i++) {
				wiringTypes.get(i).tokenize();
			}
			//--------------------------------------------------------------------------------
			// PEER TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = "";
			// - tokenize
			for(int i = 0; i < peerTypes.size(); i++) {
				peerTypes.get(i).tokenize();
			}
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(920029, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// DATA TYPE EVALUATION
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate data type of all tokens in the meta model
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		String errMsg1 = "pmmm type = " + this.pmmmTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// PMMM TYPE PMMM PROPS TYPES:
			//--------------------------------------------------------------------------------
			// - nb: needed for PMMM.<pmmmprop> qualifier expressions
			errMsg2 = " / pmmm props types";
			// - eval data types
			pmmmTypePmmmPropsTypes.evalDataTypes(context.switch2PmmmType_PmmmPropsTypes());
			//--------------------------------------------------------------------------------
			// PMMM TYPE PMMM PROPS DEFS (defaults):
			//--------------------------------------------------------------------------------
			errMsg2 = " / pmmm props defs";
			// - resolve qualifier types
			pmmmTypePmmmPropsDefsTokenExpression.resolveQualifierTypes(context.setPmmmQualifierContext(pmmmTypePmmmPropsTypes, pmmmTypeName).getQualifierContext());
			// - eval data types
			pmmmTypePmmmPropsDefsTokenExpression.evalDataTypes(context.switch2PmmmType_PmmmPropsDefs());
			//--------------------------------------------------------------------------------
			// ENTRY TYPES:
			//--------------------------------------------------------------------------------
			// - nb: needed for type evaluation of links
			errMsg2 = " / entry types";
			// - eval data types
			entryTypes.evalDataTypes(context.switch2EntryTypes());
			/**/ tracer.println("entryTypes: \n" + entryTypes, Level.NO, m);	
			//--------------------------------------------------------------------------------
			// WIRING TYPES:
			//--------------------------------------------------------------------------------
			for(int i = 0; i < wiringTypes.size(); i++) {
				// get next wiring type
				WiringType wiringType = wiringTypes.get(i);
				errMsg2 = " / wiring type = " + wiringType.wiringTypeName;
				// eval data types
				wiringType.evalDataTypes(context.switch2WiringType(wiringType.getWiringTypeName()));
			}
			//--------------------------------------------------------------------------------
			// PEER TYPES:
			//--------------------------------------------------------------------------------
			for(int i = 0; i < peerTypes.size(); i++) {
				// get next peer type
				PeerType peerType = peerTypes.get(i);
				errMsg2 = " / peer type = " + peerType.peerTypeName;
				// eval data types
				peerType.evalDataTypes(context.switch2PeerType(peerType.getPeerTypeName()));
			}	
			/**/ tracer.println("TYPED PEER TYPES: \n" + peerTypes, Level.NO, m);
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(505050, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	// PLAUSI CHECK
	// TBD: was noch / was wurde eh schon gecheckt?
	// - zb: 
	// -- benutzer types duerfen sys types nicht overrulen
	// -- alle types muessen defined sein
	// -- var/prop set/get expression muss eine liste von assignments sein, wobei linke seite prop oder user var ist
	// -- urls muessen existierende peers sein
	// -- each tv has at most 1 root token
	//================================================================================
	public void plausiCheck() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = "pmmm type " + this.pmmmTypeName;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		// TBD:
		// CHECK: SYS EPROPS TYPES MUST NOT BE "OVERRULED" BY ENTRY TYPES
		//--------------------------------------------------------------------------------
		//		for(int i = 0; i < keywordTypes.getSysEProps().getSize(); i++) {
		//			NameDeclaration keyPair = keywordTypes.getSysEProps().get(i);
		//			String sysEPropName = keyPair.getName();
		//			String entryName = "";
		//			try {
		//				entryName = entryTypes.searchAnyEPropAndReturnEntryTypeName(sysEPropName);
		//				// found --> error; means that entry def contains epropo that is a sys eprop
		//			} catch(Exception e) {
		//				// not found --> ok 
		//			}
		//			// do it outside of try block!!! otherwise it is also catched...
		//			if(entryName.length() > 0) {
		//				throw new Exception(tracer.userError("in definition of entry " + entryName + ": property '" + sysEPropName + 
		//						"' tries to overrule system property with same name"));
		//			}
		//		}
		//================================================================================
		try {
			//--------------------------------------------------------------------------------
			// PMMM TYPE PMMM PROPS TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = "/pmmm props types";
			pmmmTypePmmmPropsTypes.plausiCheck();
			//--------------------------------------------------------------------------------
			// PMMM TYPE PMMM PROPS DEFS (defaults):
			//--------------------------------------------------------------------------------
			errMsg2 = "/pmmm props defs";
			pmmmTypePmmmPropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// ENTRY TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = "/entry types";
			entryTypes.plausiCheck();
			//--------------------------------------------------------------------------------
			// WIRING TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = "/wiring types";
			for(int i = 0; i < wiringTypes.size(); i++) {
				wiringTypes.get(i).plausiCheck();
			}
			//--------------------------------------------------------------------------------
			// PEER TYPES:
			//--------------------------------------------------------------------------------
			errMsg2 = "/peer types";
			for(int i = 0; i < peerTypes.size(); i++) {
				peerTypes.get(i).plausiCheck();
			}	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(661010, errMsg1 + errMsg2, m, e);
		}
	}

	//================================================================================
	//================================================================================
	// DEBUG
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString() with default fu
	public String toString() {
		return toStructuredString();
	}
	//--------------------------------------------------------------------------------
	public String toStructuredString() {
		//--------------------------------------------------------------------------------
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append("\n");
		buf.append(ui.Out.banner("PMMM TYPE:", '*'));
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("PMMM TYPE:", pmmmTypeName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.propsTypes2StructuredString("pmmm type props types", pmmmTypePmmmPropsTypes));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("pmmm type props defs", pmmmTypePmmmPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		if(entryTypes.entryTypes.size() > 0) {
			buf.append(ui.Out.banner("ENTRY TYPES:", '*'));
			buf.append(entryTypes.toString());
		}
		//--------------------------------------------------------------------------------
		if(wiringTypes.size() > 0) {
			buf.append(ui.Out.banner("WIRING TYPES:", '*'));
			for(int i = 0; i < wiringTypes.size(); i++) {
				if(i == 0)
					buf.append(ui.Out.borderline('-'));
				buf.append(wiringTypes.get(i).toString());
				buf.append(ui.Out.borderline('-'));
			}
		}
		//--------------------------------------------------------------------------------
		if(peerTypes.size() > 0) {
			buf.append(ui.Out.banner("PEER TYPES:", '*'));
			for(int i = 0; i < peerTypes.size(); i++) {
				buf.append(ui.Out.banner("PEER TYPE:", '='));
				buf.append(peerTypes.get(i).toString());
			}
			buf.append(ui.Out.borderline('='));
		}
		return new String(buf);
	}


}  // END OF CLASS


//================================================================================
// EOF
//================================================================================
