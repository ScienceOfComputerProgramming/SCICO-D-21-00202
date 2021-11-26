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

import eval.tokens.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// nb: raw ... not yet tokenized string;
// !!! CAUTION: keep the copy fus up to date in case of changes !!!
public class Link implements IEvaluation {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// ENTRY TYPE: local var
	// - caution: is set by data type eval, because it is needed for preprocessor
	EntryType linkEntryType = new EntryType();; 
	//================================================================================
	//--------------------------------------------------------------------------------
	// LINK KIND: GUARD | ACTION | SIN | CALL | SOUT
	// - TBD: service links not yet implemented
	protected INames.LinkKind linkKind = INames.LinkKind.UNDEFINED;
	//--------------------------------------------------------------------------------
	// NUMBER OF LINK: with regard to it link kind
	protected String linkNumberAsString = "";
	//--------------------------------------------------------------------------------
	// CONTAINER:
	protected String containerName = "";
	//--------------------------------------------------------------------------------
	// SPACE OP: e.g. MOVE, COPY, CREATE, NOOP ...
	protected String spaceOpName = "";
	//................................................................................
	// - noop link flag
	// -- CAUTION: resolved ***after*** type eval!!
	protected boolean isNoopLinkFlag = false;
	//--------------------------------------------------------------------------------
	// ENTRY TYPE NAME:
	protected String entryTypeName = "";
	//--------------------------------------------------------------------------------
	// COUNT:
	// - number of entries that the link shall transport
	protected TokenExpression countTokenExpression = new TokenExpression("");
	//--------------------------------------------------------------------------------
	// QUERY:
	protected TokenExpression queryTokenExpression = new TokenExpression("");
	//--------------------------------------------------------------------------------
	// VAR/PROP/SET/GET:
	protected TokenExpression varPropSetGetTokenExpression = new TokenExpression("");
	//--------------------------------------------------------------------------------
	// LPROPS DEFS: 
	// - contains "values" for user and sys props
	// -- keep intact, as it is needed for code gen
	protected TokenExpression lpropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Link(INames.LinkKind linkKind) {	
		// set
		this.linkKind = linkKind;
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setIsNoopLinkFlag(boolean isNoopLinkFlag) {
		this.isNoopLinkFlag = isNoopLinkFlag;
	}
	//--------------------------------------------------------------------------------
	// nb: NO GOOD IDEA to check validity here: better wait until parsing is done, because then a better error message can be delivered...
	public void setLinkNumberAsString(String linkNumberAsString) throws SyntaxException {
		this.linkNumberAsString = linkNumberAsString;
	}
	//--------------------------------------------------------------------------------
	public void setContainerName(String containerName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// name validity check 
		PmUtil.isContainerName(containerName);
		//--------------------------------------------------------------------------------
		// set
		this.containerName = containerName;
	}
	//--------------------------------------------------------------------------------
	public void setSpaceOpName(String spaceOpName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// name validity check 
		PmUtil.isSpaceOpName(spaceOpName);
		//--------------------------------------------------------------------------------
		// set
		this.spaceOpName = spaceOpName;
	}
	//--------------------------------------------------------------------------------
	public void setEntryTypeName(String entryTypeName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// name validity check: "*" (wildcard) or type name
		// TBD: wildcard is hard coded...
		if(! entryTypeName.equals("*")) {
			try {
				PmUtil.isValidName(entryTypeName, false /* emptyIsAllowedFlag */, true /* wildcardIsAllowedFlag */);
			} catch (SyntaxException e) {
				throw new SyntaxException("entry type name", m, e);
			}
		}
		//--------------------------------------------------------------------------------
		// set
		this.entryTypeName = entryTypeName;
	}
	//--------------------------------------------------------------------------------
	// no plausi checks possible because count could be a range specification 
	public void setRawEntryCount(String raw) {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println(this.linkKind + this.linkNumberAsString + " raw count = " + raw, Level.NO, m);
		//--------------------------------------------------------------------------------
		this.countTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setRawQuery(String raw) {
		this.queryTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setRawVarPropSetGet(String raw) {
		this.varPropSetGetTokenExpression.setRaw(raw);
	}
	//--------------------------------------------------------------------------------
	public void setRawLPropsDefs(String raw) {
		this.lpropsDefsTokenExpression.setRaw(raw);
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public String getContainerName() {
		return containerName;
	}
	//--------------------------------------------------------------------------------
	public String getSpaceOpName() {
		return spaceOpName;
	}
	//--------------------------------------------------------------------------------
	public String getEntryTypeName() {
		return entryTypeName;
	}
	//--------------------------------------------------------------------------------
	public String getNumberAsString() {
		return linkNumberAsString;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getCountTokenExpression() {
		return countTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getQueryTokenExpression() {
		return queryTokenExpression;
	}
	//--------------------------------------------------------------------------------
	// CAUTION: this is the orig and not merged one!
	public TokenExpression getVarPropSetGetTokenExpression() {
		return varPropSetGetTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getLPropsDefsTokenExpression() {
		return lpropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public boolean getIsNoopLinkFlag() {
		return isNoopLinkFlag;
	}
	//--------------------------------------------------------------------------------
	public INames.LinkKind getLinkKind() {
		return linkKind;
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
	// and translate all expressions into nested token trees;
	public void tokenize() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		String errMsg1 = linkKind + linkNumberAsString;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// COUNT:
			// - do it only for non noop links
			if(! isNoopLinkFlag) {
				errMsg2 = "/count";
				countTokenExpression.tokenize();
				/**/ tracer.println(errMsg1 + "/countTokenExpression = " + countTokenExpression.toUserInfo(true, true), Level.NO, m);
			}
			//--------------------------------------------------------------------------------
			// QUERY:
			errMsg2 = "/query";
			queryTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
			// VAR/PROP/SET/GET:
			errMsg2 = "/var/prop/set/get";
			varPropSetGetTokenExpression.tokenize(); 
			//--------------------------------------------------------------------------------
			// LPROPS DEFS:
			errMsg2 = "/lprops defs";
			lpropsDefsTokenExpression.tokenize();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(121277, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
	}

	//================================================================================
	// DATA TYPE EVAL 
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate token types in correct order;
	public void evalDataTypes(Context context) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg1 = this.linkKind + "-" + this.linkNumberAsString;
		if(! isNoopLinkFlag) {
			errMsg1 += " / entry = " + this.entryTypeName;
		}
		String errMsg2 = "";
		//================================================================================
		// CONTEXT SWITCH TO MY LINK:
		//================================================================================
		//--------------------------------------------------------------------------------
		context.switch2Link(linkKind + linkNumberAsString, isNoopLinkFlag, entryTypeName, spaceOpName);
		//================================================================================
		// TRICK:
		// !!! set local link entry type, which has been set in the context after the context switch to my link !!!
		// !!! needed for the resolving of qualifier values for this link !!!
		// !!! caution: extra treatment for wildcard "*" needed, as context sets entry type only if not wildcard !!!
		// TBD: name is hardcoded
		//================================================================================
		//--------------------------------------------------------------------------------
		// do it only for non-NOOP links
		if(! isNoopLinkFlag) {
			//--------------------------------------------------------------------------------
			// fetch entry type from context
			EntryType ctxtEntryType = context.getCurEntryType();
			//--------------------------------------------------------------------------------
			// wildcard?
			if(ctxtEntryType.entryTypeName.equals("*")) {
				//--------------------------------------------------------------------------------
				// at least set entry type name!
				linkEntryType.setEntryTypeName("*");
			}
			//--------------------------------------------------------------------------------
			// no wildcard
			else {
				//--------------------------------------------------------------------------------
				// !!! deep copy, because the context vars will be overwritten !!!
				linkEntryType.deepCloneFromEntryType(ctxtEntryType);
				//--------------------------------------------------------------------------------
				// assertion: 
				if(linkEntryType.isEmpty())
					throw new SNHException(686868, "link entry type is empty; entry type name = " + linkEntryType.entryTypeName, m);
			}
			/**/ tracer.println(context.toMsg() + "link entry type := '" + linkEntryType.entryTypeName + "'; op = " + this.spaceOpName + "; " + linkKind + linkNumberAsString, Level.NO, m);
		}
		//================================================================================
		// ASSERTION:
		//================================================================================
		//--------------------------------------------------------------------------------
		// check that count is defined on non-noop link
		if((! isNoopLinkFlag) && (countTokenExpression.isTVVEmpty())) {
			throw new SyntaxException(errMsg1 + "/count must not be empty", m);
		}		
		//--------------------------------------------------------------------------------
		try {
			//================================================================================
			// COUNT:
			//================================================================================
			/**/ tracer.println("eval count; raw = " + countTokenExpression.getRaw(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// do it only for non-NOOP links
			if(! isNoopLinkFlag) {
				//--------------------------------------------------------------------------------
				// orig: resolve qualifier types
				errMsg2 = "/qualifier in count";
				countTokenExpression.resolveQualifierTypes(context.getQualifierContext());
				//--------------------------------------------------------------------------------
				// orig: eval data types
				errMsg2 = " / count";
				countTokenExpression.evalDataTypes(context);
			}
			/**/ tracer.println(context.toMsg() + "count = " + countTokenExpression.toUserInfo(true, true), Level.NO, m);
			//================================================================================
			// QUERY:
			//================================================================================
			//--------------------------------------------------------------------------------
			// caution: eval query before var/prop/set/get !!!
			// nb: there must be max. 1 root token for query... this has been verified by the plausi check
			/**/ tracer.println("eval query", Level.NO, m);
			/**/ tracer.println(context.toMsg() + " type eval of query = '" + queryTokenExpression.getRaw() + "'", Level.NO, m);
			//--------------------------------------------------------------------------------
			// orig: resolve qualifier types
			errMsg2 = " / qualifier in query";
			/**/ tracer.println("eval qualifier in query: " + queryTokenExpression.toUserInfo() + queryTokenExpression.toTypedInfo(), Level.NO, m);
			queryTokenExpression.resolveQualifierTypes(context.getQualifierContext());
			/**/ tracer.println("evaluated qualifier in query: " + queryTokenExpression.toUserInfo() + queryTokenExpression.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// orig: eval data types
			errMsg2 = " / query";
			queryTokenExpression.evalDataTypes(context.switch2Link_Query());
			/**/ tracer.println("evaluated query: " + queryTokenExpression.toUserInfo() + queryTokenExpression.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			//================================================================================
			// VAR/PROP/SET/GET: 
			//================================================================================
			//--------------------------------------------------------------------------------
			// caution: eval var/prop/set/set before lprops defs
			/**/ tracer.println("eval var/prop/set/get", Level.NO, m);
			//--------------------------------------------------------------------------------
			// orig: first resolve qualifiers
			errMsg2 = " / qualifier in var/prop/set/get";
			varPropSetGetTokenExpression.resolveQualifierTypes(context.getQualifierContext());
			//--------------------------------------------------------------------------------
			// orig: eval types
			errMsg2 = " / var/prop/set/get";
			varPropSetGetTokenExpression.evalDataTypes(context.switch2Link_VarPropSetGet());
			//================================================================================
			// LPROPS DEFS: 
			//================================================================================
			/**/ tracer.println("eval lprops", Level.NO, m);
			//--------------------------------------------------------------------------------
			// - resolve qualifiers
			errMsg2 = " / qualifier in lprops defs";
			lpropsDefsTokenExpression.resolveQualifierTypes(context.getQualifierContext());
			//................................................................................
			// - eval types
			errMsg2 = " / lprops defs";
			lpropsDefsTokenExpression.evalDataTypes(context.switch2Link_LPropsDefs());
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(300555, errMsg1 + errMsg2, m, e);
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
		String errMsg1 = linkKind + linkNumberAsString;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		// LINK KIND:
		if(linkKind == null || linkKind == INames.LinkKind.UNDEFINED) {
			throw new SyntaxException("ill. link kind = '" + linkKind + "' in LINK = \n" + this.toString(), m);
		}
		//--------------------------------------------------------------------------------
		// OP:
		// - opUpper is also needed below for container
		String opUpper = spaceOpName.toUpperCase(); 
		if(! (opUpper.equals(IPmDefs.MOVE) || 
				opUpper.equals(IPmDefs.COPY) || 
				opUpper.equals(IPmDefs.READ) || 
				opUpper.equals(IPmDefs.TAKE) ||
				opUpper.equals(IPmDefs.CREATE) || 
				opUpper.equals(IPmDefs.DELETE) || 
				opUpper.equals(IPmDefs.NOOP)  ||
				opUpper.equals(IPmDefs.TEST) || 
				opUpper.equals(IPmDefs.REMOVE))) {
			throw new SyntaxException("ill. op = '" + spaceOpName + "' in LINK = \n" + this.toString(), m);
		}
		//--------------------------------------------------------------------------------
		// CONTAINER:
		// - TBD: NOOP logic: 
		// - for NOOP any name for the container is accepted...
		String containerUpper = containerName.toUpperCase(); 
		if(! (opUpper.equals(IPmDefs.NOOP) || 
				containerUpper.equals(IPmDefs.PIC) || containerUpper.equals(IPmDefs.POC))) {
			throw new SyntaxException("ill. container = '" + containerName + "' in LINK = \n" + this.toString(), m);
		}
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// COUNT:
			errMsg2 = "/entry count";
			countTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// QUERY:
			errMsg2 = "/query";
			queryTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
			// VAR/PROP SET/GET:
			errMsg2 = "/var/props/set/get";
			varPropSetGetTokenExpression.plausiCheck(); 
			//--------------------------------------------------------------------------------
			// LINK PROPS:
			errMsg2 = "/lprops";
			lpropsDefsTokenExpression.plausiCheck();
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch (SNHException e) {
			throw new SNHException(712345, errMsg1 + errMsg2, m, e);
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
	// return each var in a new row
	public String toStructuredString() {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween(linkKind.toString(), linkNumberAsString + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("container:", containerName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("operation:", spaceOpName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("entry type:", entryTypeName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("entry count", countTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("query", queryTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("var/prop/set/get", varPropSetGetTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("lprops", lpropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS



//================================================================================
//EOF
//================================================================================

