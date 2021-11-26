//================================================================================
// Peer Model Tool Chain
// Copyright (C) 2021 Eva Maria Kuehn
//--------------------------------------------------------------------------------
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
// 
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//================================================================================
// SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
// AUTHOR:    Eva Maria Kuehn
// CREATED:   December 2020 
//================================================================================

package eval.tokens;

import java.util.Stack;

import eval.IData;
import pmmm.IOpDefs;
import pmmm.IPmDefs;
import pmmm.OpDefs;
import pmmm.QualifierContext;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
public class Token {
	//--------------------------------------------------------------------------------
	// for debugging
	Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// e.g: INT, STRING, NAME, VAR, FU, UOP, BOP, LB, RB -- check for actual defines!!!
	protected IToken.Kind kind;
	//--------------------------------------------------------------------------------
	// if token has kind NAME: additional flag whether token is a label
	protected boolean isLabelFlag = false;
	//--------------------------------------------------------------------------------
	// text
	protected String rawText;
	//--------------------------------------------------------------------------------
	// build up by melding:
	// tree
	protected Token left = null; 
	protected Token right = null; 
	//--------------------------------------------------------------------------------
	// token type ... set by token evaluator *after* tokenization has taken place on the PMMM
	protected IData.Type type = IData.Type.UNDEFINED;
	//--------------------------------------------------------------------------------
	// op was treated
	protected boolean treatedFlag = false;
	//--------------------------------------------------------------------------------
	// token was integrated into a subtree
	// -nb: it is not deallocated it as it is still used in subtree
	protected boolean integratedFlag = false;
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Token(IToken.Kind kind, String rawText) {
		this.kind = kind;
		this.rawText = rawText;
	}

	//================================================================================
	// COPY
	//================================================================================
	//--------------------------------------------------------------------------------
	// return a deep copy of me
	public Token deepCopy() {
		Token clonedToken = new Token(this.kind, this.rawText);
		clonedToken.isLabelFlag = this.isLabelFlag;
		if(this.left != null) {
			clonedToken.left = this.left.deepCopy();
		}
		if(this.right != null) {
			clonedToken.right = this.right.deepCopy();
		}
		clonedToken.type = this.type;
		clonedToken.treatedFlag = this.treatedFlag;
		clonedToken.integratedFlag = this.integratedFlag;
		return clonedToken;
	}
	//--------------------------------------------------------------------------------
	// flat clone me from given token
	public void flatClone(Token cloneToken) throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assertion:
		if(cloneToken == null)
			throw new SNHException(777277, "empty clone token", m);
		//--------------------------------------------------------------------------------
		// clone:
		this.kind = cloneToken.kind;
		this.rawText = cloneToken.rawText;
		this.isLabelFlag = cloneToken.isLabelFlag;
		this.left = cloneToken.left;
		this.right = cloneToken.right;
		this.type = cloneToken.type;
		this.treatedFlag = cloneToken.treatedFlag;
		this.integratedFlag = cloneToken.integratedFlag;
	}

	//================================================================================
	// GET / QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	public IToken.Kind getKind() {
		return kind;
	}	
	//--------------------------------------------------------------------------------
	public String getRawText() {
		return rawText;
	}
	//--------------------------------------------------------------------------------
	public Token getLeft() {
		return left;
	}
	//--------------------------------------------------------------------------------
	public Token getRight() {
		return right;
	}
	//--------------------------------------------------------------------------------
	public boolean getIsLabelFlag() {
		return isLabelFlag;
	}
	//--------------------------------------------------------------------------------
	public IData.Type getType() {
		return type;
	}
	//--------------------------------------------------------------------------------
	// if token has type INT -> return its raw text
	// caution: kind could be INT or NAME (used for ALL and NONE)
	// caution: do not use int as ret val, because of ALL and NONE, which are INT sys consts
	public String resolveIntRaw() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		if(((kind == IToken.Kind.INT) || (kind == IToken.Kind.NAME)) && 
				(type == IData.Type.INT)) {
			return rawText;
		}
		throw new SyntaxException("ill. <INT> specification", m);
	}
	//--------------------------------------------------------------------------------
	// is token the ".." op of type INT?
	public boolean isIntDotsBOP() {
		return (kind == IToken.Kind.BOP) && (OpDefs.isDotsBOPName(rawText) && (type == IData.Type.INT));
	}

	//================================================================================
	// SET
	//================================================================================
	//--------------------------------------------------------------------------------
	public void setType(IData.Type type) {
		this.type = type;
	}	
	//--------------------------------------------------------------------------------
	public void setLeft(Token token) {
		left = token;
	}	
	//--------------------------------------------------------------------------------
	public void setRight(Token token) {
		right = token;
	}	
	//--------------------------------------------------------------------------------
	// sys or user label?
	public void setIsLabelFlag(boolean isLabelFlag) {
		this.isLabelFlag = isLabelFlag;
	}
	//================================================================================
	//--------------------------------------------------------------------------------
	// convert (nested) '#'-expression of the form '(...((<name> # <index1>) # <index1>) # ... # <indexN>)' into a flat string
	// - resolve the entire expression into a flat string with '#' chars
	// CAUTION: my token must have been mta-ed before (ie all indices must represent ints - through static analysis!)
	// CAUTION: do not test that type is STRING, because for names of peers and wirings type eval could not check the type and they have type FLEX !!!
	// NB: checks validity of the resulting name
	public String getFlattenedHashLabel() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// recursion
		try {
			String s = _getFlattenedHashLabel();
			/**/ tracer.println("flattened hash label = " + s, Level.NO, m);
			return s;
		} catch (SyntaxException e) {
			throw new SyntaxException("ill. name '" + this.toUserInfo() + "'", m, e);
		}
	}
	//================================================================================
	//--------------------------------------------------------------------------------
	// help fu for recursion
	// - convert (nested) '#'-expression into a flat string
	public String _getFlattenedHashLabel() throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// NAME:
		// - caution: type might not be given (ie for peer names or wiring names) !!! so do not check it !!!
		if(kind == IToken.Kind.NAME) {
			return new String(rawText);
		}
		//--------------------------------------------------------------------------------
		// '#':
		else if((kind == IToken.Kind.BOP) && OpDefs.isHashBOPName(rawText)) {
			// left: ('#'-)label expression
			String s1 = left._getFlattenedHashLabel();
			// right: array index number
			// - verify that it is an integer
			if(right.kind != IToken.Kind.INT)
				throw new SyntaxException("index of '#' expression must be INT val, but found '" + right.toUserInfo() + "'", m);
			// - get its string representation
			String s2 = right.rawText;
			// concat s1 + '#' + s2
			String s = s1.concat(OpDefs.HASH.concat(s2));
			// ok
			return s;
		}
		//--------------------------------------------------------------------------------
		else
			throw new SyntaxException(this.kind + " must not be used as name", m);
	}

	//================================================================================
	// SUPER SPECIALIZED CODE -- needed by code generator for resolving "." operator expression
	// FYI: because all NAME kinds in the right hand side of "." must be resolved by kind = STRING
	//================================================================================
	//--------------------------------------------------------------------------------
	// !!!!!! replace all "NAME" Kind appearances by "STRING" (see above explanation)
	public void replaceNAMEbySTRING() {
		if(this.kind == IToken.Kind.NAME)
			this.kind = IToken.Kind.STRING;
		if(left != null) 
			left.replaceNAMEbySTRING();
		if(right != null)
			right.replaceNAMEbySTRING();
	}

	//================================================================================
	// GET PROP LABEL TOKEN
	//================================================================================
	//--------------------------------------------------------------------------------
	// return my label token;
	// analyse token to be a PROP TYPE or a PROP DEF;
	// - for prop types of the form "<type>:<propLabelToken>" the key is <propLabel>
	// - for prop defs of the form "<propLabelToken>=<any token expr>" the lable is the <left side>
	// -- nb: left side could be a '#' expression!
	// - return propLabelToken token, if ok
	public Token getPropLabelToken(boolean isPropTypeFlag) throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		Token propLabelToken;
		//--------------------------------------------------------------------------------
		// check root token to be correct operator
		//--------------------------------------------------------------------------------
		// - PROP TYPE:
		if(isPropTypeFlag) {
			//--------------------------------------------------------------------------------
			// ":"
			if(! OpDefs.isColonBOPName(this.rawText))
				throw new SyntaxException("prop: expected '" + IOpDefs.COLON + "', but found: '" + rawText + "'", m); 
			//--------------------------------------------------------------------------------
			// set prop label token to correct side
			propLabelToken = this.right;
		}
		//--------------------------------------------------------------------------------
		// - PROP DEF:
		else {
			//--------------------------------------------------------------------------------
			// "="
			if(! OpDefs.isAssignmenBOPName(this.rawText))
				throw new SyntaxException("prop: expected '" + IOpDefs.ASSIGN + "', but found: '" + rawText + "'", m); 
			//--------------------------------------------------------------------------------
			// set prop label token to correct side
			propLabelToken = this.left;
		}
		//--------------------------------------------------------------------------------
		// plausi check
		if(null == propLabelToken)
			throw new SyntaxException("empty prop " + ((isPropTypeFlag) ? "type" : "def") + "specification", m);
		//--------------------------------------------------------------------------------
		// ok
		return propLabelToken;
	}
	//================================================================================
	//--------------------------------------------------------------------------------
	// get basic label name token of a label token
	// - nb: could be a nested '#' expression; in this case return leftmost label name
	// -- eg: (((<labelName> # <int>) # <int>) # <int>) --> return <labelName>
	public Token getBasicLabelNameToken() throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// NAME?
		if(this.kind == IToken.Kind.NAME)
			return this;
		//--------------------------------------------------------------------------------
		// INT? 
		// - nb: used for INDEX.<int>
		if(this.kind == IToken.Kind.INT)
			return this;
		//--------------------------------------------------------------------------------
		// '#'?
		else if(OpDefs.isHashBOPName(this.rawText)) {
			// continue on left side
			return left.getBasicLabelNameToken();
		}
		//--------------------------------------------------------------------------------
		// error: no valid label name
		else
			throw new SyntaxException("ill. label specification: '" + this.toUserInfo() + "'", m);
	}
	//================================================================================
	//--------------------------------------------------------------------------------
	// get type of basic label name token
	// - nb: could be a nested '#' expression; return type of leftmost label name token
	// -- eg: (((<labelName> # <int>) # <int>) # <int>) --> return <labelName>
	public IData.Type getBasicLabelType() throws SyntaxException { 
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// NAME?
		if(this.kind == IToken.Kind.NAME)
			return this.type;
		//--------------------------------------------------------------------------------
		// INT? 
		// - nb: used for INDEX.<int>
		if(this.kind == IToken.Kind.INT)
			return this.type;
		//--------------------------------------------------------------------------------
		// '#'?
		else if(OpDefs.isHashBOPName(this.rawText)) {
			// continue on left side
			return left.getBasicLabelType();
		}
		//--------------------------------------------------------------------------------
		// error: no valid label name
		else
			throw new SyntaxException("ill. label spec.: '" + this.toUserInfo() + "'", m);
	}

	//================================================================================
	// GET K-TH HASH INDEX TOKEN
	//================================================================================
	//--------------------------------------------------------------------------------
	// my token has the form ( ... ( ( <name> # <i1> ) # <i2> ) # ... ) # <ik> ) # ... ) # <in> )
	// - return the k-th INT token <ik>
	////Token wiringInstanceNameToken = curWiringInstance.getWiringInstanceNameToken();
	public Token getKthHashIndexToken(int k) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("k = " + k, Level.NO, m);
		/**/ tracer.println("token = " + this.toUserInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// stack for indices <i1>, <i2>, <i3> ... <in>
		Stack<Token> indexStack = new Stack<Token>();
		//--------------------------------------------------------------------------------
		// local var: start with my token
		Token token = this;
		//--------------------------------------------------------------------------------
		while(token.kind == IToken.Kind.BOP && OpDefs.isHashBOPName(token.rawText)) {
			/**/ tracer.println("push " + this.right.rawText, Level.NO, m);
			// push <ij>
			indexStack.push(token.right);
			// continue withleft side
			token = token.left;
		}
		//--------------------------------------------------------------------------------
		// TBD: assertions.......
		//--------------------------------------------------------------------------------
		// caution: compute stack size before shrinking it
		int stackSize = indexStack.size();
		/**/ tracer.println("stack = " + indexStack, Level.NO, m);
		/**/ tracer.println("stackSize = " + stackSize, Level.NO, m);
		for(int i = 1; i <= stackSize; i++) {
			Token retToken = indexStack.pop();
			if(i == k) {
				/**/ tracer.println("retToken = " + retToken.toUserInfo(), Level.NO, m);
				/**/ tracer.println(k + ". index token = " + retToken.toUserInfo(), Level.NO, m);
				return retToken;
			}
		}
		throw new SyntaxException("ill. access to " + k + ". '#'-index in " + this.toUserInfo(), m);	
	}

	//================================================================================
	// RESOLVE QUALIFIER TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// in all my props defs resolve the types of all qualifiers namely of 
	// - (a) "<PEER>.<propName>", (b) "<PMMM>.<propName>", (c) "<WIRING>.<propName>", (d) <WINDEX>.<int>, (e) <PINDEX>.<int> or (f) <INDEX>.<int>
	// - all qualifiers have kind NAME and their type is simply resolved to STRING here
	// - the type of the <propName> is resolved by means of the given context
	// - CAUTION: <propName> could be a nested '#'-expression; in this case evaluate its leftmost basic label <name>!!!
	// -- eg: <name> # <int> # <int> # <int>
	// - (d): me plus right side are INT 
	// - (e): me plus right side are INT 
	// - (f): me plus right side are INT 
	//--------------------------------------------------------------------------------		
	public void resolveQualifierTypes(QualifierContext qualifierContext) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		/**/ tracer.println(qualifierContext.toMsg() + "token = " + this.toTypedInfo(), Level.NO, m);
		/**/ tracer.println("resolve qualifier types: " + this.toUserInfo(), Level.NO, m);
		//--------------------------------------------------------------------------------
		// left .... qualifier, i.e. <PEER>, <PMMM> or <WIRING>
		// right .... <propName>
		if((this.kind == IToken.Kind.BOP) && OpDefs.isDotBOPName(rawText)) {
			/**/ tracer.println(qualifierContext.toMsg() + "resolving type of DOT OP = " + this.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// !!! CAUTION: '.' could be nested !!!
			// - eg: $who == (PMMM . (players # (WINDEX . 1)))
			// !!! so recursively resolve right part first !!!
			right.resolveQualifierTypes(qualifierContext);
			/**/ tracer.println("resolved right: " + right.toTypedInfo(), Level.NO, m);
			//--------------------------------------------------------------------------------
			// check left to have kind NAME
			if(left.getKind() == IToken.Kind.NAME) {
				//--------------------------------------------------------------------------------
				/**/ tracer.println("left is NAME -- fine!", Level.NO, m);
				/**/ tracer.println(qualifierContext.toMsg() + "resolving QUALIFIER type = " + this.toTypedInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// get qualifier name
				String qualifierName = left.getRawText();
				//--------------------------------------------------------------------------------
				// !!! get the *basic* label name (see comment above)
				// - nb: right must be a (complex) prop label specification
				/**/ tracer.println("right = " + right.toTypedInfo(), Level.NO, m);
				Token propNameToken = right.getBasicLabelNameToken();
				String propName = propNameToken.getRawText();
				/**/ tracer.println("qualifierName = " + qualifierName + "; propNameToken = " + propName, Level.NO, m);
				//--------------------------------------------------------------------------------
				// find type in the respective context:
				try {
					// (a) <PMMM>?
					if(qualifierName.equals(IPmDefs.PMMM_QUALIFIER)) {
						this.type = qualifierContext.pmmmQualifierGetType(propName);
					}
					//--------------------------------------------------------------------------------
					// (b) <PEER>?
					else if(qualifierName.equals(IPmDefs.PEER_QUALIFIER)) {
						this.type = qualifierContext.peerQualifierGetType(propName);
					}
					//--------------------------------------------------------------------------------
					// (c) <WIRING>?
					else if(qualifierName.equals(IPmDefs.WIRING_QUALIFIER)) {
						this.type = qualifierContext.wiringQualifierGetType(propName);
					}
					//--------------------------------------------------------------------------------
					// (d) <WINDEX>?
					else if(qualifierName.equals(IPmDefs.WINDEX_QUALIFIER)) {
						this.type = IData.Type.INT;
					}
					//--------------------------------------------------------------------------------
					// (e) <PINDEX>?
					else if(qualifierName.equals(IPmDefs.PINDEX_QUALIFIER)) {
						this.type = IData.Type.INT;
					}
					//--------------------------------------------------------------------------------
					// (f) <INDEX>?
					else if(qualifierName.equals(IPmDefs.INDEX_QUALIFIER)) {
						this.type = IData.Type.INT;
					}
					//--------------------------------------------------------------------------------
					else {
						// trick: this exception is catched below...
						throw new SyntaxException(qualifierContext.toMsg() + "left side of qualifier '.' operator must be '" + 
								IPmDefs.PMMM_QUALIFIER + "', '" + IPmDefs.PEER_QUALIFIER + "', or '" + 
								IPmDefs.WIRING_QUALIFIER + "'; but is '" + left.getRawText() + "'; ", m);
					}
					//--------------------------------------------------------------------------------
					/**/ tracer.println("computed type = " + type, Level.NO, m);
				//--------------------------------------------------------------------------------
				} catch(NotFoundException e) {
					throw new SyntaxException(qualifierContext.toMsg() + "can't resolve qualifier expression " + this.toUserInfo(), m, e);
				}
				//--------------------------------------------------------------------------------
				// my type has been successfully resolved !!! 
				// set also type of left and right sides of the token
				left.type = IData.Type.STRING;
				right.type = type;
				/**/ tracer.println(qualifierContext.toMsg() + qualifierName + "." + right + " resolved to " + type, Level.NO, m);
				//--------------------------------------------------------------------------------
				// !!! TRICKY !!!
				// if right is a complex prop label, then inherit the type to it
				// - eg: ((PMMM . (players # (WINDEX . 1))) == $$PID)
				// -- here players is still undefined....
				if(propNameToken.type == IData.Type.UNDEFINED) {
					propNameToken.type = type;
					/**/ tracer.println("FIX TYPE of complex label " + propName + " to " + type, Level.NO, m);
				}
			}
			else {
				throw new SyntaxException("left side must be valid qualifier name, but found '" + left.toUserInfo() + "' in: " + this.toUserInfo(), m);
			}
		}
		//--------------------------------------------------------------------------------
		// recursion
		else {
			if(left != null) {
				left.resolveQualifierTypes(qualifierContext);
			}
			if(right != null) {
				right.resolveQualifierTypes(qualifierContext);
			}
		}
	}

	//================================================================================
	// RESOLVE FLEX TYPES
	//================================================================================
	//--------------------------------------------------------------------------------
	// convert FLEX_ARRAY_REF types to URLs, if used on right side of assignment to a URL
	// - eg: <URL<NAME>>who  <URL_ARRAY<BOP>>'=' ( <FLEX<NAME>>player  <FLEX_ARRAY_REF<BOP>>'#'  <INT<INT>>1 )
	// - eg: (who = (((player # 1) # 2) ... #k)); 
	// - eg: <URL<NAME>>dest  <URL<BOP>>'=' ( <FLEX<NAME>>player  <FLEX_ARRAY_REF<BOP>>'#'  <INT<INT>>1
	// - eg: <FLEX<NAME>>player  <URL_ARRAY_REF<BOP>>'#'  <INT<INT>>1
	// TBD: pass all peer names for verification...
	// TBD: review...
	// TBD: too complicated logic --> just change any occurrence of FLEX_ARRAY_REF to URL_ARRAY_REF, and of FLEX to URL
	public void resolveFlexTypes() {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// REPLACE FLEX BY URL
		if(type == IData.Type.FLEX)
			type = IData.Type.URL;
		//--------------------------------------------------------------------------------
		// REPLACE FLEX_ARRAY_REF BY URL_ARRAY_REF
		if(type == IData.Type.FLEX_ARRAY_REF)
			type = IData.Type.URL_ARRAY_REF;
		//--------------------------------------------------------------------------------
		// BOP?
		if(kind == IToken.Kind.BOP) {
			this.left.resolveFlexTypes();
			this.right.resolveFlexTypes();
		}
		//--------------------------------------------------------------------------------
		// UOP?
		if(kind == IToken.Kind.BOP) {
			this.left.resolveFlexTypes();
		}
	}
	
	//================================================================================
	// VERIFICATION
	//================================================================================
	//--------------------------------------------------------------------------------
	// all types must be DEFINED
	public void verifyTypeDefinedness() throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// check me
		if(this.type == IData.Type.UNDEFINED)
			throw new SNHException(552844, "type of " + this.toUserInfo() + " is UNDEFINED", m);
		//--------------------------------------------------------------------------------
		// recursion:
		// - BOP?
		if(kind == IToken.Kind.BOP) {
			this.left.verifyTypeDefinedness();
			this.right.verifyTypeDefinedness();
		}
		//--------------------------------------------------------------------------------
		// - UOP?
		if(kind == IToken.Kind.BOP) {
			this.left.verifyTypeDefinedness();
		}
	}

	//================================================================================
	// CONVERT TO USER INFO
	//================================================================================
	//--------------------------------------------------------------------------------
	// !!! CAUTION: do not change: is also used for PM-DSL code gen !!!
	// for user messages: print flat node in bracketed, nice and human readable form;
	// caution: use blank before/after ops, because ops can be names like "CONCAT";
	public String toUserInfo() {
		if (kind == IToken.Kind.EMPTY) 
			return "";	
		if(kind == IToken.Kind.BOP) {
			return "(" + left.toUserInfo() + " " + this.toQuotedOrNotQuotedRaw() + " " + right.toUserInfo() + ")";
		}
		if(kind == IToken.Kind.UOP) {
			// CAUTION: we assume that uop has right arg!
			return "(" + this.toQuotedOrNotQuotedRaw() + " " + right.toUserInfo() + ")";
		}
		return this.toQuotedOrNotQuotedRaw();
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString: select default fu here
	public String toString() {
		return toRaw();
	}
	//--------------------------------------------------------------------------------
	// display this token as raw text;
	// to be used before tokenization;
	public String toRaw() {
		return rawText;
	}
	//--------------------------------------------------------------------------------
	// return value correctly quoted if needed;
	// - nb: depends on *kind*
	public String toQuotedOrNotQuotedRaw() {
		if(kind == IToken.Kind.STRING) {
			return "\"" + this.rawText + "\"";
		}
		return this.rawText;
	}
	//--------------------------------------------------------------------------------
	// flat node info containing of the form:
	// - NODE: [<left args as raw text>, <op>, <right args as raw text>]
	// to be used after mince phase of tokenizer;
	public String toNode() {
		String leftText = (left != null) ? left.rawText : " ";
		String rightText = (left != null) ? right.rawText : " ";

		return(new String("[" + leftText + ", " + rawText + ", " + rightText + "]"));
	}
	//--------------------------------------------------------------------------------
	// flat node info containing kind + raw text
	// - without "NODE" at the beginning
	// - with additional info whether note has already been treated or integrated by meld phase of tokenizer
	public String toNode_plus_Kind() {
		return(new String("[<" + kind + ">:\"" +  rawText  + "\"]"));
	}
	//--------------------------------------------------------------------------------
	// flat node info consisting of: kind + raw text + parser flags
	// - without "NODE" at the beginning
	// - with additional info whether note has already been treated or integrated by meld phase of tokenizer
	public String toNode_plus_TypeInProgress_Kind_Flags() {
		// treated flag
		String x = (treatedFlag) ? "/t" : "";
		// integrated flag
		String y = (integratedFlag) ? "/i" : "";
		return(new String("[" + " <" + type + "<" + kind + ">>\"" +  rawText  + "\"]" + x + y));
	}
	//--------------------------------------------------------------------------------
	// intelligently display this token as expression with all terms in brackets (recursive);
	// to be used after meld phase of tokenizer;
	public String toInfo() {
		return toInfo(false /* enforceDisplayOfTypeInfo */);
	}
	//--------------------------------------------------------------------------------
	// display this token as expression with all terms in brackets (recursive);
	// enforce the display of types (even if not evaluated yet...);
	// to be used after meld phase of tokenizer or for debugging;
	public String toTypedInfo() {
		return toInfo(true /* enforceDisplayOfTypeInfo */);
	}
	//--------------------------------------------------------------------------------
	// intelligently display this token as expression with all terms in brackets (recursive);
	// depending on flag enforce the display of the type info even if not evaluated yet;
	public String toInfo(boolean enforceDisplayOfTypeInfo) {
		StringBuffer buf = new StringBuffer();
		//--------------------------------------------------------------------------------
		// my node empty?
		if (this.kind == IToken.Kind.EMPTY) 
			return "";
		//--------------------------------------------------------------------------------
		// display left
		if(left != null) 
			buf.append("(" + left.toInfo(enforceDisplayOfTypeInfo));
		else if (right != null)
			buf.append("(");
		//--------------------------------------------------------------------------------
		// display me
		if(enforceDisplayOfTypeInfo) {
			buf.append(" <" + type + "<" + kind + ">>");
		}
		else {
			buf.append(" <" + kind + ">");
		}
		if((kind == IToken.Kind.UOP) || (kind == IToken.Kind.BOP)) 
			buf.append("'" + rawText + "'");
		else 
			buf.append(rawText);
		buf.append(" ");
		//--------------------------------------------------------------------------------
		// display right
		if(right != null) 
			buf.append(right.toInfo(enforceDisplayOfTypeInfo) + ")");
		//--------------------------------------------------------------------------------
		return new String(buf);
	}
	
	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================

