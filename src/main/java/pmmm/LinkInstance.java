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

import eval.tokens.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
public class LinkInstance implements IMta {
	//--------------------------------------------------------------------------------
	/**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// pointer to my LINK
	Link link;
	//================================================================================
	//--------------------------------------------------------------------------------
	// COUNT:
	// - processed: 
	protected TokenExpression processedCountTokenExpression = new TokenExpression("");
	// - converted to min/max token expressions (yet another form of representation)
	// -- orig:
	protected MinMaxTokens minMaxTokens = new MinMaxTokens();
	// -- processed: 
	protected MinMaxTokens processedMinMaxTokens = new MinMaxTokens();
	// - converted to min/max int expressions (yet another form of representation; done after qualifier values were resolved)
	// -- only in processed form: ie qualifier values resolved; otherwise we cannot compute the values...
	protected MinMaxIntVals processedMinMaxIntVals = new MinMaxIntVals(IntTokenValEval.MINMAX_NULL, IntTokenValEval.MINMAX_NULL);
	//--------------------------------------------------------------------------------
	// QUERY:
	// - processed, where qualifier values are resolved
	protected TokenExpression processedQueryTokenExpression = new TokenExpression("");
	//--------------------------------------------------------------------------------
	// VAR/PROP/SET/GET:
	// - processed (nb: also enhanced by entry type's default eprops defs, if op is create entry)
	protected TokenExpression processedVarPropSetGetTokenExpression = new TokenExpression("");
	//--------------------------------------------------------------------------------
	// LPROPS DEFS: 
	// - processed: just a clone of my lprops defs;
	// -- so that certain code gens cann add here their specialities (by themselves!)
	// -- e.g. code gen for go automation will add the prop "commit = true" on each last link; 
	protected TokenExpression processedLPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public LinkInstance() {	
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	public MinMaxIntVals getProcessedMinMaxIntVals() {
		return processedMinMaxIntVals;
	}
	//--------------------------------------------------------------------------------
	public MinMaxTokens getMinMaxTokens() {
		return minMaxTokens;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedQueryTokenExpression() {
		return processedQueryTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedVarPropSetGetTokenExpression() {
		return processedVarPropSetGetTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public TokenExpression getProcessedLPropsDefsTokenExpression() {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// /**/ tracer.println("processedLPropsDefsTokenExpression = " + processedLPropsDefsTokenExpression.toUserInfo(), Level.NO, m);
		return processedLPropsDefsTokenExpression;
	}
	//--------------------------------------------------------------------------------
	public String getNumberAsString() {
		return link.linkNumberAsString;
	}
	//--------------------------------------------------------------------------------
	public String getContainerName() {
		return link.containerName;
	}
	//--------------------------------------------------------------------------------
	public String getSpaceOpName() {
		return link.spaceOpName;
	}
	//--------------------------------------------------------------------------------
	public boolean getIsNoopLinkFlag() {
		return link.isNoopLinkFlag;
	}
	//--------------------------------------------------------------------------------
	public INames.LinkKind getLinkKind() {
		return link.linkKind;
	}
	//--------------------------------------------------------------------------------
	public String getEntryTypeName() {
		return link.entryTypeName;
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
		// local vars
		String errMsg1 = link.linkKind + link.linkNumberAsString;
		String errMsg2 = "";
		//--------------------------------------------------------------------------------
		// assertion
		if(curPmmmInstance == null)
			throw new SNHException(123409, errMsg1 + "cur pmmm is null", m);
		if(curPeerInstance == null)
			throw new SNHException(123408, errMsg1 + "cur pmmm is null", m);
		if(curWiringInstance == null)
			throw new SNHException(123407, errMsg1 + "cur wiring is null", m);
		if(curEntryType != null)
			throw new SNHException(123406, errMsg1 + "cur entry type is not null", m);
		//--------------------------------------------------------------------------------
		//================================================================================
		// COUNT 
		//================================================================================
		//--------------------------------------------------------------------------------
		// do it only for non-NOOP links
		if(! link.isNoopLinkFlag) {
			errMsg2 = "/count";
			//--------------------------------------------------------------------------------
			// assert that count is defined
			if(link.countTokenExpression.isTVVEmpty()) {
				throw new SNHException(486000, link.linkKind + link.linkNumberAsString + ": countExpression is empty; raw = " + link.countTokenExpression.getRaw(), m);
			}
			//--------------------------------------------------------------------------------
			try {
				//--------------------------------------------------------------------------------
				// mta
				processedCountTokenExpression.deepCloneAndMta(link.countTokenExpression, curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
				/**/ tracer.println("processedCountTokenExpression:" + processedCountTokenExpression.toUserInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// !! NOW: resolve count to min/max !!
				// - CAUTION: do it only after mta-ing 
				/**/ tracer.println("resolve count to min/max:", Level.NO, m);
				//--------------------------------------------------------------------------------
				// convert to min/max token expression
				//................................................................................
				// - orig
				errMsg2 = "/count (1)"; // TBD: for debug only
				minMaxTokens = processedCountTokenExpression.resolveCount();
				/**/ tracer.println("resolved count: minMax = " + minMaxTokens.toStructuredString(), Level.NO, m);
				//................................................................................
				// - processed
				errMsg2 = "/count (2)"; // TBD: for debug only
				processedMinMaxTokens = processedCountTokenExpression.resolveCount();
				/**/ tracer.println("resolved count: minMax = " + processedMinMaxTokens.toStructuredString(), Level.NO, m);
				/**/ tracer.println(" - min token = " + processedMinMaxTokens.getMinToken().toUserInfo(), Level.NO, m);
				/**/ tracer.println(" - max token = " + processedMinMaxTokens.getMaxToken().toUserInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// compute int vals of min/max
				// - and set int vals in processed min/max int vals
				errMsg2 = "/count (3)"; // TBD: for debug only
				processedMinMaxIntVals.setMin(IntTokenValEval.compute(processedMinMaxTokens.getMinToken()));
				errMsg2 = "/count (4)"; // TBD: for debug only
				processedMinMaxIntVals.setMax(IntTokenValEval.compute(processedMinMaxTokens.getMaxToken()));
				//--------------------------------------------------------------------------------
				/**/ tracer.println(" -- min/max int vals = " + processedMinMaxIntVals.toStructuredString(), Level.NO, m);
				/**/ tracer.println(" -- min/max tokens = " + processedMinMaxTokens.toStructuredString(), Level.NO, m);
				//................................................................................
			} catch(SyntaxException e) {
				throw new SyntaxException(errMsg1 + errMsg2, m, e);
			} catch(SNHException e) {
				throw new SNHException(196345, errMsg1 + errMsg2, m, e);
			}
		}
		//================================================================================
		// FETCH my entry type which was set by data type eval
		//================================================================================
		//--------------------------------------------------------------------------------
		// do it only for non-NOOP links
		if(! link.isNoopLinkFlag) {
			//--------------------------------------------------------------------------------
			/**/ tracer.println("linkEntryType = " + link.linkEntryType.entryTypeName, Level.NO, m);
			//--------------------------------------------------------------------------------
			// assertion
			if(link.linkEntryType.isEmpty()) {
				throw new SNHException(642098, errMsg1 + "; link entry type is empty; op = " + link.spaceOpName + "; " + link.linkKind + 
						link.linkNumberAsString + "; peer = " + curPeerInstance.peerInstanceName + "; wiring = " + curWiringInstance.getWiringInstanceName(), m);
			}
		}
		//--------------------------------------------------------------------------------
		try {
			//================================================================================
			// QUERY 
			//================================================================================
			errMsg2 = "; query";
			processedQueryTokenExpression.deepCloneAndMta(link.queryTokenExpression, curPmmmInstance, curPeerInstance, curWiringInstance, link.linkEntryType /* curEntryType */);
			//================================================================================
			// VAR/PROP/SET/GET: 
			//================================================================================
			errMsg2 = "; var/prop/set/get";
			processedVarPropSetGetTokenExpression.deepCloneAndMta(link.varPropSetGetTokenExpression, curPmmmInstance, curPeerInstance, curWiringInstance, link.linkEntryType /* curEntryType */);
			//================================================================================
			// LPROPS DEFS: 
			//================================================================================
			errMsg2 = "; lprops defs";
			processedLPropsDefsTokenExpression.deepCloneAndMta(link.lpropsDefsTokenExpression, curPmmmInstance, curPeerInstance, curWiringInstance, link.linkEntryType /* curEntryType */);
			//--------------------------------------------------------------------------------
		} catch(SyntaxException e) {
			throw new SyntaxException(errMsg1 + errMsg2, m, e);
		} catch(SNHException e) {
			throw new SNHException(482004, errMsg1 + errMsg2, m, e);
		}
		//================================================================================
		// FOR CREATED ENTRIES: ADD DEFAULT EPROPS TO VAR/PROP/SET/GET 
		//================================================================================
		// add default eprops (of respective entry type) to var/props/set/get of newly created entries;
		// - enhance processed var/prop/set/get by entry default defs 
		// -- ***provided that entry is not wildcard***
		// -- and provided that the defs do not yet exist in var/prop/set/get
		// NB: var/prop/set/get were already mta-ed
		// TBD: gehoert nicht hierher... sondern in token expression
		if(link.spaceOpName.equals(IPmDefs.CREATE) && (! link.linkEntryType.entryTypeName.equals("*"))) {
			errMsg2 = "; add default eprops to created entry/entries";
			//--------------------------------------------------------------------------------
			// get its entry type name
			String entryTypeName = link.linkEntryType.getEntryTypeName();
			//--------------------------------------------------------------------------------
			// nb: this has been asserted already, but just to go sure... TBD: '*' is hard coded
			if(entryTypeName.equals("*")) {
				throw new SyntaxException(errMsg1 + errMsg2 + IPmDefs.CREATE + " and wildcard ('*') are not allowed", m);
			}
			//--------------------------------------------------------------------------------
			/**/ tracer.println("---\n enriching newly created entry " + entryTypeName + " by default eprops defs", Level.NO, m);
			//--------------------------------------------------------------------------------
			// get default props defs of entry type 
			TokenExpression defaultEPropsDefsTokenExpression = link.linkEntryType.getEPropsDefsTokenExpression();		
			//--------------------------------------------------------------------------------
			/**/ tracer.println("enriching newly created entry " + entryTypeName + " by default eprops defs", Level.NO, m);
			/**/ tracer.println("old VAR/PROP/SET/GET = " + processedVarPropSetGetTokenExpression.toUserInfo(true, true), Level.NO, m);
			//--------------------------------------------------------------------------------
			// construct the merge and perform mta on merged props
			processedVarPropSetGetTokenExpression.deepCloneMergeMta_Assignments(processedVarPropSetGetTokenExpression, defaultEPropsDefsTokenExpression, 
					curPmmmInstance, curPeerInstance, curWiringInstance, curEntryType);
			/**/ tracer.println("enhanced processed VAR/PROP/SET/GET = " + this.processedVarPropSetGetTokenExpression.toUserInfo(true, true), Level.NO, m);
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
		buf.append(link.toString());
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("processed:", "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString(" - count", processedCountTokenExpression));
		// keep for debug
		/*
		buf.append(PmUtil.token2StructuredString(" - min token", minMaxTokenExpression.getMinToken()));
		buf.append(PmUtil.token2StructuredString(" - max token", minMaxTokenExpression.getMaxToken()));
		buf.append(PmUtil.token2StructuredString(" - processed min token", processedMinMaxTokenExpression.getMinToken()));
		buf.append(PmUtil.token2StructuredString(" - processed max token", processedMinMaxTokenExpression.getMaxToken()));
		 */
		buf.append(ui.Out.addAlignmentInbetween(" - count (min/max):", processedMinMaxIntVals.toStructuredString() + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString(" - query", processedQueryTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString(" - var/prop/set/get", processedVarPropSetGetTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString(" - lprops", processedLPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		return new String(buf);
	}


} // END OF CLASS



//================================================================================
//EOF
//================================================================================

