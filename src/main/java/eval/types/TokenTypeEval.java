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

package eval.types;

import eval.IData;
import eval.tokens.*;
import pmmm.Context;
import pmmm.IContext;
import pmmm.OpDefs;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// determines data type of given token (nested);
// assumes that all ops, except of unary op, are left associative! 
// uses this.context;
// nb: if a user var is not initialized, this is recognized by its type being not defined!
// a) the token can have a context, i.e. it belongs to a wiring;
//     - e.g. it belongs to an action/guard and is linkProps, query, varPropSetGet
//     - e.g. it is wiringProps;
//    a list of all variables must be built up;
//    the type of entry properties can be queried with "pmmm.getEntryPropertyType(entryType, propLabel);";
// b) or the token belongs directly to peer, i.e. it is peerProps;
public class TokenTypeEval {
	//--------------------------------------------------------------------------------
	// for debugging
	Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// root of the token tree to be type-evaluated
	private Token rootToken;
	//--------------------------------------------------------------------------------
	// current context
	Context context;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// constructor
	public TokenTypeEval(Context context, Token rootToken) {
		this.rootToken = rootToken;
		this.context = context;
	}

	//================================================================================
	// START TYPE EVAL WITH ROOT TOKEN
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate the rootToken (nested) and set types
	public void typeEval() throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// assert that root token is not null
		if(rootToken == null)
			throw new SNHException(893666, "root token must not be null", m);
		//--------------------------------------------------------------------------------
		/**/ tracer.println(context.toMsg() + "TOKEN TREE = " + rootToken.toTypedInfo(), Level.LO, m); 
		/**/ tracer.println("___________________________________________", Level.HI, m);		
		/**/ tracer.println("CONTEXT: " + context.toMsg(), Level.HI, m);		
		/**/ tracer.println("EVAL: " + rootToken.toUserInfo() + " <=> "  + rootToken.toTypedInfo(), Level.HI, m);		
		//--------------------------------------------------------------------------------
		// do it recursively
		String errMsg = "type eval of '" + rootToken.toUserInfo() + "' failed";
		try { 
			_typeEval(rootToken);
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(564738, errMsg, m, e);
		}
		/**/ tracer.println("CONTEXT: " + context.toMsg(), Level.NO, m);		
		/**/ tracer.println("EVALUATED: " + rootToken.toTypedInfo(), Level.HI, m); 
	}

	//================================================================================
	// RECURSIVE TYPE EVAL
	//================================================================================
	//--------------------------------------------------------------------------------
	// evaluate the token (nested) and set its type and the type of all its subtokens;
	// returns resolved token type and raises exception on type error;
	private IData.Type _typeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// local var
		String errMsg;
		//--------------------------------------------------------------------------------
		// check that token is not null
		if(token == null) {
			throw new SNHException(785235, "token must not be null", m);
		}
		/**/ tracer.println("eval " + token.toTypedInfo(), Level.LO, m);		
		//--------------------------------------------------------------------------------
		// even if type is defined, continue with eval, because some sub-tokens could still be undefined 
		// eg: right side of '.' might need further evaluation
		// nb: token is already evaluated in the following cases
		// - right arg of <quantifier> op
		// - right arg of '#' op and '#' op 
		//--------------------------------------------------------------------------------
		// init return type with undefined
		IData.Type type = IData.Type.UNDEFINED;
		//--------------------------------------------------------------------------------
		// get token kind
		IToken.Kind kind = token.getKind();
		/**/ tracer.println("eval next token: kind = " + kind + "; raw = '" + token.getRawText() + "'", Level.LO, m);		
		//--------------------------------------------------------------------------------
		// analyze token by its kind:
		switch(kind) {

		case INT: 
			//********************************************************************************
			// INT INT INT INT INT INT INT INT INT INT INT INT INT INT INT INT INT INT INT INT
			//********************************************************************************
			//--------------------------------------------------------------------------------
			type = IData.Type.INT;
			break;
			//--------------------------------------------------------------------------------

		case STRING: 
			//********************************************************************************
			// STRING STRING STRING STRING STRING STRING STRING STRING STRING STRING STRING ST
			//********************************************************************************
			//--------------------------------------------------------------------------------
			type = IData.Type.STRING;
			break;
			//--------------------------------------------------------------------------------

		case NAME:
			//********************************************************************************
			// NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME NAME
			//********************************************************************************
			//--------------------------------------------------------------------------------
			try {
				type = nameTypeEval(token);
			} catch (SyntaxException e3) {
				// throw new SyntaxException("can't eval type of NAME", m, e3); too much info
				throw e3;
			}
			//--------------------------------------------------------------------------------
			break;

		case VAR: 
			//********************************************************************************
			// VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR VAR
			//********************************************************************************
			//--------------------------------------------------------------------------------
			String varName = token.getRawText();
			//--------------------------------------------------------------------------------
			// sys var? 
			//--------------------------------------------------------------------------------
			try {
				type = context.getSysVarType(varName);
				break;
			} catch(Exception e1) {}
			//--------------------------------------------------------------------------------
			// user var?
			//--------------------------------------------------------------------------------
			try {
				type = context.getUserVarType(varName);
			} catch(Exception e2) {
				// just do nothing for now and return undefined type; type will be treated by assignment later;
			}
			break;
			//--------------------------------------------------------------------------------

		case FU: 
			//********************************************************************************
			// FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU FU F
			//********************************************************************************
			//--------------------------------------------------------------------------------
			String fuName = token.getRawText();
			//--------------------------------------------------------------------------------
			// sys fu? (i.e. a reserved name)
			//--------------------------------------------------------------------------------
			try {
				type = context.getSysFuType(fuName);
				/**/ tracer.println("FU = " + fuName + "()", Level.NO, m);
			} catch(NotFoundException e2) {
				throw new SyntaxException("ill. fu = " + fuName + "()", m, e2);
			}
			break;
			//--------------------------------------------------------------------------------

		case UOP: 
			//********************************************************************************
			// UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP UOP
			//********************************************************************************
			//--------------------------------------------------------------------------------
			// unary op can be resolved immediately;
			// then verify with arg's type;
			//--------------------------------------------------------------------------------
			String uopName = token.getRawText();
			//--------------------------------------------------------------------------------
			// arithmetic UOP:
			if(OpDefs.isArithmeticUOPName(uopName)) {
				type = IData.Type.INT;
			}
			//--------------------------------------------------------------------------------
			// boolean UOP:
			else if(OpDefs.isBooleanUOPName(uopName)) {
				type = IData.Type.BOOLEAN;
			}
			//--------------------------------------------------------------------------------
			// <quantifier>, ie RANGE, FORALL or EXISTS UOP: 
			// - requires an int expression
			// - the right arg is a name local to the FOR* expression -> set its type to INT!!!
			// - eg: FORALL k IN 1..5
			else if(OpDefs.isRangeUOPName(uopName) || OpDefs.isForAllUOPName(uopName) || OpDefs.isExistsUOPName(uopName)) {
				// FOR* type is INT
				type = IData.Type.INT;
				// type of its arg, too!
				token.getRight().setType(type);
				/**/ tracer.println("FOR*: " + token.getRight().toTypedInfo(), Level.NO, m);
			}
			//--------------------------------------------------------------------------------
			// snh: the UOP was analyzed before so its name must be valid
			else 
				throw new SNHException(732548, "ill. UOP name = " + uopName, m);
			//--------------------------------------------------------------------------------
			// eval right arg and verify that it has the same type
			IData.Type argType;
			errMsg = "can't eval type of right arg of unary op '" + uopName + "'";
			try {
				argType = _typeEval(token.getRight());
			} catch (SyntaxException e) {
				throw new SyntaxException(errMsg, m, e);
			} catch (SNHException e) {
				throw new SNHException(298765, errMsg, m, e);
			}
			if(type != argType) {
				throw new SyntaxException("right arg of unary op '" + uopName + "' has wrong type " + argType + "; should be " + type, m);
			}
			break;
			//--------------------------------------------------------------------------------

		case BOP: 
			//********************************************************************************
			// BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP BOP
			//********************************************************************************
			//--------------------------------------------------------------------------------
			String bopName = token.getRawText();
			//--------------------------------------------------------------------------------
			// eval BOP
			// - depending on what BOP it is
			//--------------------------------------------------------------------------------
			errMsg = "binary operator '" + bopName + "'";
			//--------------------------------------------------------------------------------
			if(OpDefs.isArithmeticBOPName(bopName)) {
				try {
					type = arithmeticBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isRelationalBOPName(bopName)) {
				try {
					type = relationalBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isBooleanBOPName(bopName)) {
				try {
					type = booleanBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isAssignmenBOPName(bopName)) {
				try {
					type = assignmentBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isDotsBOPName(bopName)) {
				try {
					type = dotsBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isColonBOPName(bopName)) {
				try {
					type = colonBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isConcatBOPName(bopName)) {
				try {
					type = concatBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isHashBOPName(bopName)) {
				try {
					type = hashBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isInBOPName(bopName)) {
				try {
					type = inBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isImpliesBOPName(bopName)) {
				try {
					type = impliesBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			else if(OpDefs.isCommaBOPName(bopName)) {
				try {
					type = commaBopTypeEval(token);
				} catch (SyntaxException e) {
					// throw new SyntaxException(errMsg, m, e); // too much info
					throw e;
				}
			}
			//--------------------------------------------------------------------------------
			// !!! special treatment for qualifier op: 
			// - namely its qualifier types has already been resolved via resolveQualifierTypes !!!
			// - BUT: do data type eval recursively here, as right side of '.' could be complex and nested '#' and '.' expression
			// - !!! complicated !!!
			else if(OpDefs.isDotBOPName(bopName)) {
				//--------------------------------------------------------------------------------
				/**/ tracer.println("is DOT OP", Level.NO, m);
				//--------------------------------------------------------------------------------
				// get type
				type = token.getType();
				//--------------------------------------------------------------------------------
				// assertion
				if(type == IData.Type.UNDEFINED)
					throw new SNHException(437601, "unevaluated '.' op: " + token.toTypedInfo(), m);
				//--------------------------------------------------------------------------------
				// !!! recursion for right side only if a complex type ie BOP!!!
				// - nb: left side must be qualifier name...
				// - TBD: UOP
				if((token.getRight().getKind() == IToken.Kind.BOP) || (token.getRight().getKind() == IToken.Kind.UOP)) {
					/**/ tracer.println("eval DOT's right side" + token.getRight().toTypedInfo(), Level.NO, m);
					_typeEval(token.getRight());
				}
			}
			//--------------------------------------------------------------------------------
			// snh: the BOP was analyzed before so its name must be valid
			else {
				throw new SNHException(444497, "ill. BOP name = " + bopName, m);
			}
			//--------------------------------------------------------------------------------
			// type must now be defined!
			if(type == IData.Type.UNDEFINED)
				throw new SyntaxException("type eval error: ill. specification: " + token.toUserInfo(), m);
			//--------------------------------------------------------------------------------
			/**/ tracer.println("type of " + bopName + " = " + type, Level.NO, m);
			//--------------------------------------------------------------------------------
			break;

			//*********************
			//*** TRUE or FALSE ***
			//*********************
		case TRUE: 
		case FALSE: 
			//********************************************************************************
			// TRUE or FALSE or TRUE or FALSE or TRUE or FALSE or TRUE or FALSE or TRUE or FAL
			//********************************************************************************
			//--------------------------------------------------------------------------------
			// can be resolved immediately
			type = IData.Type.BOOLEAN;
			break;
			//--------------------------------------------------------------------------------

		case EMPTY: 
			//********************************************************************************
			// EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY E
			//********************************************************************************
			//--------------------------------------------------------------------------------
			// used for unused left args of unary ops
			break;
			//--------------------------------------------------------------------------------

		default:
			//********************************************************************************
			// DEFAULT DEFAULT DEFAULT DEFAULT DEFAULT DEFAULT DEFAULT DEFAULT DEFAULT DEFAULT
			//********************************************************************************
			//--------------------------------------------------------------------------------
			// snh: token was parsed before
			throw new SNHException(7234567, "ill. token kind", m);
			//--------------------------------------------------------------------------------
		}
		//--------------------------------------------------------------------------------
		// set my token's type and also return it
		/**/ tracer.println("result = <" + type + ">", Level.LO, m);		
		token.setType(type);
		return type;
	}

	//================================================================================
	// NAME TYPE EVAL
	//================================================================================
	//--------------------------------------------------------------------------------
	// analyse token of kind NAME
	// - nb: this fu sets the type and the isUserLabelFlag on the token
	// - nb: caller gets type and sets it on the token, too
	private IData.Type nameTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// type (to be returned) 
		IData.Type type = IData.Type.UNDEFINED;			
		//--------------------------------------------------------------------------------
		String name = token.getRawText();
		/**/ tracer.println("NAME: " + name, Level.LO, m);
		//--------------------------------------------------------------------------------
		// assertion: must have kind NAME
		if(token.getKind() != IToken.Kind.NAME)
			throw new SNHException(288347, "not a NAME: " + token.toTypedInfo(), m);
		//--------------------------------------------------------------------------------
		// if type has already been early determined (namely for nested '.' and '#' label names) -> done
		// - eg: (PMMM . (players # (WINDEX . 1))) == $$PID 
		// -- ... player is determined already and cannot be determined here without qualifier context
		// !! COMPLICATED !!
		if(token.getType() != IData.Type.UNDEFINED) {
			/**/ tracer.println("type of NAME " + name + " has already been early determined as " + token.getType(), Level.LO, m);
			return token.getType();
		}
		//================================================================================
		// SYS CONST?	
		//================================================================================
		try {
			type = context.getSysConstType(name); 
			/**/ tracer.println("...is SYS CONST of type " + type, Level.LO, m);
		} catch (NotFoundException e1) {
			//================================================================================
			// SYS PROP?
			//================================================================================
			try {
				type = context.getSysPropType(name); 
				/**/ tracer.println("...is SYS PROP of type " + type, Level.LO, m);
				// yes -> set flag !!!!!!
				token.setIsLabelFlag(true);
			} catch (NotFoundException e2) {
				//================================================================================
				// USER PROP?
				//================================================================================
				// pmmm props, eprop, pprop, wprop, lprops
				try {
					// user prop?
					type = context.getUserPropType(name);
					/**/ tracer.println("...is USER PROP of type " + type, Level.LO, m);
					// yes -> set flag !!!!!!
					token.setIsLabelFlag(true);
				} catch (NotFoundException e3) {
					//================================================================================
					// URL?
					//================================================================================
					// name of a user peer from the PMMM or of a built-in sys peer?
					if(context.peerTypeNameExists(name)) {
						type = IData.Type.URL;
						// /**/ tracer.println(name + " is peer type (URL)", Level.DEBUG, m);
					}
					//--------------------------------------------------------------------------------
					// if parsing any kind of PROPS TYPES:
					// - still chance to be resolved by type eval, namely it
					// -- could be right hand side of "<type>:<name>" definition, where <name> will inherit the <type> 
					// -- this is checked & resolved when returning to parsing of ":" in the recursion
					// -- so skip the type eval for the moment
					else if(context.curUsageisPropsTypes()) {
						// delay type eval of name
						/**/ tracer.println("...DELAY EVAL ", Level.LO, m);
						type = IData.Type.FLEX;
					}
					//--------------------------------------------------------------------------------
					// if parsing any kind of PROPS DEFS: 
					// - still chance to be resolved by type eval, namely it
					// -- could be right hand side of "<xxx> = <name>" expression, where <name> will inherit the <type>
					// nb: peer defs have the syntax: 
					// - <peerName>
					// - <comma separated peer names>
					// check peer type
					else if(context.curUsageIsPropsDefs()) {
						// delay type eval of name
						/**/ tracer.println("...DELAY EVAL ", Level.LO, m);
						type = IData.Type.FLEX;
					}			
					//--------------------------------------------------------------------------------
					// if parsing definition of peer name (in config) --> 
					// - still chance to be resolved by type eval, namely it
					// -- could be right hand side of "<peerType> : <name>" expression, where <name> will inherit the <type>
					else if(context.getCurUsage() == IContext.Usage.PEER_CONFIG) {
						// delay type eval of name
						/**/ tracer.println("...DELAY EVAL ", Level.LO, m);
						type = IData.Type.FLEX;
					}		
					//--------------------------------------------------------------------------------
					// peer name? 
					else if(context.getCurUsage() == IContext.Usage.PEER_NAMES) {
						// delay type eval of name
						/**/ tracer.println("...DELAY EVAL ", Level.LO, m);
						type = IData.Type.FLEX;
					}		
					//--------------------------------------------------------------------------------
					// wiring name? 
					else if(context.getCurUsage() == IContext.Usage.WIRING_NAMES) {
						// delay type eval of name
						/**/ tracer.println("...DELAY EVAL ", Level.LO, m);
						type = IData.Type.FLEX;
					}		
					//================================================================================
					// TBD: if entry type name is wildcard '*': 
					// - TBD: if so, we could assume that it is a label
					// - TBD: ??? but problem: which type
					//================================================================================
					//--------------------------------------------------------------------------------
					//================================================================================
					// otherwise: 
					//================================================================================
					// - if parsing entry type defs and peer type defs -> ill. entry name
					else {
						/**/ tracer.println(context.getCurUsage() + "; name is undefined; type = " + type, Level.LO, m);
						/**/ tracer.println(context.getCurUsage() + "; sys props = " + context.getCurSysPropTypeMap().toString(), Level.LO, m);
						//--------------------------------------------------------------------------------
						throw new SyntaxException(
								// "<" + token.getKind() + ">" + // too much info
								"'" + name + "' " + "undefined (neither sys keyword nor user prop)", m);
					}
				}
			}
		}
		return type;
	}

	//================================================================================
	//================================================================================
	// COMPUTE TYPE COMPATIBILITY, COMPUTE "RELEASED" TYPE
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// compute "released" basic type
	// for array refs its the respective basic type
	// for basic type it is the basic type
	// otherwise return UNDEFINED
	// TBD: $nextPeer = (PMMM . (peers # (WINDEX . 1))); ... fkt nicht
	// - daher muss auch array resolved werden...
	private static IData.Type releaseToBasicType(IData.Type type) {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		switch(type) {
		//--------------------------------------------------------------------------------
		// basic
		case INT:
		case STRING:
		case BOOLEAN:
		case URL:	
		case FLEX:	
			return type;
			//--------------------------------------------------------------------------------
			// array ref -> release
		case INT_ARRAY_REF:
			return IData.Type.INT;
		case STRING_ARRAY_REF:
			return IData.Type.STRING;
		case BOOLEAN_ARRAY_REF:
			return IData.Type.BOOLEAN;
		case URL_ARRAY_REF:
			return IData.Type.URL;
			// TBD:
		case FLEX_ARRAY_REF:
			return IData.Type.FLEX;
			//--------------------------------------------------------------------------------
			// these cannot be release
			// TBD: s.o.
		case INT_ARRAY:
			return IData.Type.INT;
		case STRING_ARRAY:
			return IData.Type.STRING;
		case BOOLEAN_ARRAY:
			return IData.Type.BOOLEAN;
		case URL_ARRAY:
			return IData.Type.URL;
		case FLEX_ARRAY:
			return IData.Type.FLEX;
		case UNDEFINED:
		default:
			return IData.Type.UNDEFINED;
		}
	}

	//--------------------------------------------------------------------------------
	// for BOPs: compute whether 2 types are compatible
	// - returns UNDEFINED if incompatible
	// CAUTION: treat '#' separately beyond this function !!!
	// CAUTION: type eval of 'firstPlayer = (PMMM . (players # 1))' ... requires that URL and URL_ARRAY are compatible...
	// TBD .... especially compatibility of array / array ref; and of flex ....
	private static IData.Type computeCompatibleType(IData.Type type1, IData.Type type2) {
		//--------------------------------------------------------------------------------
		// /**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		// check flex of right arg
		if(IData.Type.FLEX == type2 || IData.Type.FLEX_ARRAY == type2 || IData.Type.FLEX_ARRAY_REF == type2)
			return type1;
		//--------------------------------------------------------------------------------
		switch(type1) {
		//--------------------------------------------------------------------------------
		case INT:
		case INT_ARRAY:
		case INT_ARRAY_REF:
			if(IData.Type.INT == type2 || IData.Type.INT_ARRAY == type2 || IData.Type.INT_ARRAY_REF == type2)
				return IData.Type.INT;
			break;
			//--------------------------------------------------------------------------------
		case STRING:
		case STRING_ARRAY:
		case STRING_ARRAY_REF:
			if(IData.Type.STRING == type2 || IData.Type.STRING_ARRAY == type2 || IData.Type.STRING_ARRAY_REF == type2)
				return IData.Type.STRING;
			break;
			//--------------------------------------------------------------------------------
		case BOOLEAN:
		case BOOLEAN_ARRAY:
		case BOOLEAN_ARRAY_REF:
			if(IData.Type.BOOLEAN == type2 || IData.Type.BOOLEAN_ARRAY == type2 || IData.Type.BOOLEAN_ARRAY_REF == type2)
				return IData.Type.BOOLEAN;
			break;
			//--------------------------------------------------------------------------------
			// !!! nb: flex array ref ist used for array access to peer or wiring name !!!
			// TBD: FLEX?!
		case URL:	
		case URL_ARRAY:
		case URL_ARRAY_REF:
			if(IData.Type.URL == type2 || IData.Type.URL_ARRAY == type2 || IData.Type.URL_ARRAY_REF == type2 || 
			IData.Type.FLEX == type2 || IData.Type.FLEX_ARRAY == type2 || IData.Type.FLEX_ARRAY_REF == type2)
				return IData.Type.URL;
			break;
			//--------------------------------------------------------------------------------
			// !!! nb: flex array ref is used for (array access of) peer or wiring name !!!
			// TBD: ...
		case FLEX:	
		case FLEX_ARRAY:
		case FLEX_ARRAY_REF:
			return type2;
			//--------------------------------------------------------------------------------
			// undefined or delayed type just "stays" as is
		case DELAYED: // TBD
		case UNDEFINED:
			break;
		}
		//--------------------------------------------------------------------------------
		return IData.Type.UNDEFINED;
	}

	//================================================================================
	//================================================================================
	// BOP TYPE EVAL
	//================================================================================
	//================================================================================

	//================================================================================
	// ARITHMETIC BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// arithmetic operator "+", "-", ...
	// - type must be int
	private IData.Type arithmeticBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is ARITHMETIC BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(663676, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// verify that both args types are defined and are INT compatible
		if(! (IData.Type.INT == TokenTypeEval.computeCompatibleType(leftArgType, IData.Type.INT)) &&
				(IData.Type.INT == TokenTypeEval.computeCompatibleType(rightArgType, IData.Type.INT))) {
			throw new SyntaxException("left argument of binary operator '" + token.getRawText() + "' must be INT, but is " + leftArgType, m);
		}
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("arithmetic op type = " + IData.Type.INT, Level.LO, m);							
		return IData.Type.INT;
	}

	//================================================================================
	// RELATIONAL BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// relational operator "==", "<", ">", ...
	// - type must be boolean
	private IData.Type relationalBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is RELATIONAL BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(364674, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// check that both arg's types are defined and have compatible type
		if(IData.Type.UNDEFINED == computeCompatibleType(leftArgType, rightArgType)) {
			throw new SyntaxException("type " + leftArgType + " of left argument '" + token.getLeft().getRawText() + "' of binary operator '" + token.getRawText() + 
					"' differs from type " + rightArgType + " of right argument '" + token.getRight().getRawText() + "'", m);
		}
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("relational op '" + token.getRawText() + "': type = " + IData.Type.BOOLEAN, Level.NO, m);							
		return IData.Type.BOOLEAN;
	}

	//================================================================================
	// BOOLEAN BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// boolean operator "AND", "OR"
	// - type must be boolean
	private IData.Type booleanBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is BOOLEAN BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(835254, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// check that both arg's types are defined and are BOOLEAN compatible
		if(IData.Type.BOOLEAN != computeCompatibleType(leftArgType, IData.Type.BOOLEAN)) {
			throw new SyntaxException("left argument of binary operator '" + token.getRawText() + "' must be BOOLEAN, but is " + leftArgType, m);
		}
		if(IData.Type.BOOLEAN != computeCompatibleType(rightArgType, IData.Type.BOOLEAN)) {
			throw new SyntaxException("right argument of binary operator '" + token.getRawText() + "' must be BOOLEAN, but is " + rightArgType, m);
		}
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("boolean op type = " + IData.Type.BOOLEAN, Level.LO, m);							
		return IData.Type.BOOLEAN;
	}

	//================================================================================
	// ASSIGNMENT BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// TBD: kann man die Logik vereinfachen, sprich ohne Abhaengigkeit von context? 
	// TBD: checken: ist context.curUsageIsPropsDefs() true für var/prop/set/get?
	// assignment operator "="
	private IData.Type assignmentBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is ASSIGNMENT BOP", Level.LO, m);	
		/**/ tracer.println("ASSIGNMENT BOP: " + token.toTypedInfo(), Level.NO, m);	
		//--------------------------------------------------------------------------------
		// local vars
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(923468, errMsg1 + errMsg2, m, e);
		}
		//================================================================================
		// LEFT SIDE IS LABEL OF A PROP:
		// - <propName> = <right side>
		// - where <propName> is (structured) name of a pmmm prop, pprop, wprop, eprop or lprop
		//================================================================================
		//--------------------------------------------------------------------------------
		if(context.curUsageIsPropsDefs()) {
			//--------------------------------------------------------------------------------
			// left arg must be a valid sys or user prop name, i.e. its type must be defined
			// - compare or inherit it to right side
			if(leftArgType == IData.Type.UNDEFINED) {
				throw new SyntaxException("props def: type of '" + token.getLeft().getRawText() + "' is undeclared; \n" +
						"  not fond in: \n    1) sys props types: " + context.getCurSysPropTypeMap().toPairs() + "; \n" + 
						"    2) current user props types: " + context.getCurUserPropsTypes().toPairs() + ";", m);
			}
			//--------------------------------------------------------------------------------
			// TBD: fuer welchen fall wird das benötigt???
			//--------------------------------------------------------------------------------
			// nb: no vars possible here
			//--------------------------------------------------------------------------------
			// inherit left type to right side if undefined or flex
			if(rightArgType == IData.Type.UNDEFINED || rightArgType == IData.Type.FLEX) {
				//--------------------------------------------------------------------------------
				// set right arg's type
				token.getRight().setType(leftArgType);	
				rightArgType = leftArgType;
			}
			//--------------------------------------------------------------------------------
			// else verify
			IData.Type type = computeCompatibleType(leftArgType, rightArgType);
			if(type == IData.Type.UNDEFINED) {
				/**/ tracer.println(token.toTypedInfo(), Level.NO, m);
				throw new SyntaxException("prop def: type <" + leftArgType + "> of left arg '" + token.getLeft().toUserInfo() + "' " + 
						"is not compatible with type <" + rightArgType + "> of right arg '" + token.getRight().toUserInfo() + "' ", m);
			}
			//--------------------------------------------------------------------------------
			// ok: both args have compatible type
			return type;
		}
		//================================================================================
		// OTHER KINDS OF LEFT SIDES:
		// - user var or
		// - array ref
		//================================================================================
		//--------------------------------------------------------------------------------	
		// left arg type undefined?
		if(leftArgType == IData.Type.UNDEFINED) {
			//--------------------------------------------------------------------------------
			// left must be a USER VAR
			// - must be user var, because type of sys var would be defined
			// - implicitly define its type with the type of the right arg
			if(token.getLeft().getKind() == IToken.Kind.VAR) {
				/**/ tracer.println("VAR: " + token.toTypedInfo(), Level.NO, m);
				//--------------------------------------------------------------------------------
				// "release" right type
				IData.Type releasedType = TokenTypeEval.releaseToBasicType(rightArgType);
				/**/ tracer.println("releasedType = " + releasedType, Level.NO, m);
				//--------------------------------------------------------------------------------
				// if it could be released to a basic type -> set left type to right type
				// CAUTION: vars must not be set to arrays (LIMITATION!)
				if(releasedType != IData.Type.UNDEFINED) {
					token.getLeft().setType(releasedType);	
				}
				//--------------------------------------------------------------------------------
				// error: vars must not point to arrays
				else {
					/**/ tracer.println(token.getRight().toInfo(true), Level.ERROR, m);
					throw new SyntaxException("var must not be set to non-basic type " + rightArgType, m);
				}
				//--------------------------------------------------------------------------------
				// !!! add var's type to the current user var context !!!
				context.addUserVarType(token.getLeft().getRawText(), releasedType);
				//--------------------------------------------------------------------------------
				// done -> return releasedType
				return releasedType;
			}
			//--------------------------------------------------------------------------------
			// no USER VAR -> error, because in this case the type must be defined
			else {
				throw new SyntaxException("cannot assign to '" + token.getLeft().getRawText() + "'; undefined type", m);
			}
		}
		//--------------------------------------------------------------------------------
		// left arg type is defined:
		// - verify whether it has compatible type with right arg
		else {
			// compute compatible type
			IData.Type compatibleType = computeCompatibleType(leftArgType, rightArgType);
			// compatible?
			if(compatibleType == IData.Type.UNDEFINED) {
				throw new SyntaxException("type " + leftArgType + " of left argument '" + token.getLeft().getRawText() + "' of assignment operator '" + 
						token.getRawText() + "' is incompatible with type " + rightArgType + " of right argument " + token.getRight().getRawText() + "'", m);
			}
			//--------------------------------------------------------------------------------
			// left + right types are compatible -> ok my type, is clearly the same like the one of either arg
			/**/ tracer.println("assignment op type = " + rightArgType, Level.LO, m);
			return rightArgType;
		}
	}

	//================================================================================
	// DOTS BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// range operator ".."
	// - type of '..' is INT
	// - both args must be INT-compatible; eg: 1 .. PMMM.hi; eg: scores#1 .. scores#10; eg: INDEX.1 .. WINDEX.10;
	// nb: whether the int-expressions can be statically evaluated or not is checked by mta later
	private IData.Type dotsBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is DOTS BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(333399, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// check that both arg's types are defined and are INT compatible
		if(IData.Type.INT != computeCompatibleType(leftArgType, IData.Type.INT)) {
			throw new SyntaxException("left argument of binary operator '" + token.getRawText() + "' must be INT, but is " + leftArgType, m);
		}
		if(IData.Type.INT != computeCompatibleType(rightArgType, IData.Type.INT)) {
			throw new SyntaxException("right argument of binary operator '" + token.getRawText() + "' must be INT, but is " + rightArgType, m);
		}
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("dots op type = " + IData.Type.INT, Level.LO, m);	
		return IData.Type.INT;
	}

	//================================================================================
	// COLON BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// colon operator ":"
	// used for user prop type specification of the form <type>:<propName>
	// - if both args have determined, compatible type -> fine (TBD: can this case occur (see below)?)
	// - if right arg's type is undefined, it inherits the type of the left arg
	// -- check that both args are defined afterwards
	private IData.Type colonBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is COLON BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		// type 
		IData.Type type = IData.Type.UNDEFINED;			
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(298718, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		if(leftArgType != IData.Type.UNDEFINED) {
			//--------------------------------------------------------------------------------
			// do both args have compatible defined type?
			// - eg: 'URL : <peerName>' .... peer name is FLEX as it could not yet be determined
			// - nb: throws exception if not compatible
			type = computeCompatibleType(leftArgType, rightArgType);
			/**/ tracer.println("compatible type = " + type, Level.NO, m);	
			//--------------------------------------------------------------------------------
			// ok -> compatible
			if(type != IData.Type.UNDEFINED) {
				;
			}
			// not compatible?
			// - left is defined
			// - is right undefined or flex?
			else if (rightArgType == IData.Type.UNDEFINED || rightArgType == IData.Type.FLEX) {				
				//--------------------------------------------------------------------------------
				// inherit left arg's type to right arg
				token.getRight().setType(leftArgType);
				type = leftArgType;
			}
			//--------------------------------------------------------------------------------
			// syntax error
			else {
				throw new SyntaxException("right argument of '" + token.getRawText() + "' has type '" + rightArgType + "' that is incompatible" +
						" with type '" + leftArgType + "' of left arg", m);
			}
		}
		else {
			throw new SyntaxException("left argument of '" + token.getRawText() + "' must be a valid type, but is \"" + token.getLeft().getRawText() + "\"", m);
		}
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("colon op type = " + type, Level.LO, m);	
		return type;
	}

	//================================================================================
	// CONCAT BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// string concatenation operator "CONCAT"
	// - set type to string independent of args
	// CAUTION: target runtime system must convert all args to strings
	private IData.Type concatBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is CONCAT BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(734567, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// ok:
		/**/ tracer.println("concat op type = " + IData.Type.STRING, Level.LO, m);	
		return IData.Type.STRING;
	}

	//================================================================================
	// HASH BOP ('#')
	//================================================================================
	//--------------------------------------------------------------------------------
	// array access of the form <name>#<int expression>
	// - nb: can be nested: ( ( ( <name> # <int expression> ) # <int expression> ) # <int expression> )
	// - right arg must be INT 
	// - left arg must be 
	// -- prop name: its type must be array or array ref 
	// -- or peer name or wiring name: the its type is not determined and HASH op type is set to "MIXED_REF" (TBD)
	// CAUTION: complex scenario
	// - ((PMMM . (players # (WINDEX . 1))) == $$PID) ... here type of players can't be evaluated, but can be any type
	private IData.Type hashBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is '#' BOP: " + token.toUserInfo(), Level.NO, m);	
		//--------------------------------------------------------------------------------
		// default
		IData.Type type = IData.Type.FLEX_ARRAY_REF; // default for '#'	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(562901, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// verify that right arg type is INT and if not set it to INT!!!
		// CAUTION tricky:
		// example: ((FORALL INDEX.1) IN (1 .. (PMMM . nPlayers))) -> ((scores # INDEX.1) = 0)
		// ie: if right arg can't be evaluated, assume that its type is INT!!!
		if(rightArgType == IData.Type.UNDEFINED) {
			token.getRight().setType(IData.Type.INT);
		}
		//--------------------------------------------------------------------------------
		// !!! TRICKY !!! 
		// evaluate type of left arg (see comments above)
		// if prop name: left arg type is array or array ref; inherit it to '#' as array type !!!
		// if peer or wiring name: no type can be determined... so do nothing... (TBD: can this happen?)
		// nb: nested '#' expressions are possible, eg <array>#1#2#3#4, so left arg can be either array or array ref
		switch(leftArgType) {
		//--------------------------------------------------------------------------------
		// all array and array ref types::
		case INT_ARRAY:
		case INT_ARRAY_REF:
			type = IData.Type.INT_ARRAY_REF;
			break;
		case STRING_ARRAY:
		case STRING_ARRAY_REF:
			type = IData.Type.STRING_ARRAY_REF;
			break;
		case BOOLEAN_ARRAY:
		case BOOLEAN_ARRAY_REF:
			type = IData.Type.BOOLEAN_ARRAY_REF;
			break;
		case URL_ARRAY:	
		case URL_ARRAY_REF:
			type = IData.Type.URL_ARRAY_REF;
			break;
		case FLEX_ARRAY:	
		case FLEX_ARRAY_REF:
			// TBD: FLEX is needed here for eg definition of wiring names; 
			// - eg: ((RANGE (INDEX . 1)) IN (1 .. (PMMM . nPlayers))) -> (statistik # (INDEX . 1))
		case FLEX: 
			type = IData.Type.FLEX_ARRAY_REF;
			break;
			//--------------------------------------------------------------------------------
			// basic types are not allowed of left side
		case STRING:
		case INT:
		case BOOLEAN:
		case URL:
			throw new SyntaxException("left arg of '#' must be an array type but is " + leftArgType, m);
			//--------------------------------------------------------------------------------
		case DELAYED: // TBD
		case UNDEFINED:
			// set left's type to DELAYED ... can't do more right now (see comment above)
			token.getLeft().setType(IData.Type.DELAYED);
			/**/ tracer.println("FIX left type of '#' to DELAYED", Level.NO, m);							
			// peer or wiring name (TBD: can this happen?)
			type = IData.Type.FLEX;
			break;
		}
		//--------------------------------------------------------------------------------
		token.setType(type); // for debug output...
		/**/ tracer.println("### token = " + token.toTypedInfo(), Level.NO, m);	
		/**/ tracer.println("context: cur user props types = " + this.context.getCurUserPropsTypes().toPairs(), Level.NO, m);	
		/**/ tracer.println("#'s type := left arg type = " + leftArgType, Level.NO, m);	
		//--------------------------------------------------------------------------------
		// can't do more...
		/**/ tracer.println("'#' op type = " + type, Level.NO, m);							
		return type;
	}

	//================================================================================
	// 'IN' BOP
	//================================================================================
	//--------------------------------------------------------------------------------
	// used for array accessof the form (FORALL INDEX.<k>) IN <range>
	// - type of both args must be "INT" and therefore also 'IN' is INT
	private IData.Type inBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is 'IN' BOP", Level.LO, m);	
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(975355, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// verify that boths agrs have type INT
		if(leftArgType != IData.Type.INT) {
			throw new SyntaxException("left arg of 'IN' op must be INT, but is " + leftArgType, m);
		}
		if(rightArgType != IData.Type.INT) {
			throw new SyntaxException("right arg of 'IN' op must be INT, but is " + rightArgType, m);
		}
		//--------------------------------------------------------------------------------
		// ok
		/**/ tracer.println("'IN' op type = " + IData.Type.INT, Level.LO, m);							
		return IData.Type.INT;
	}

	//================================================================================
	// IMPLIES BOP ('->')
	//================================================================================
	//--------------------------------------------------------------------------------
	// "->" is used for resolving index-based array access on right side, where left side is a (nested) '<quantifier> expression
	// - eg ((<quantifierExpr1>, <quantifierExpr2>), <quantifierExpr1>) -> <do something>
	// - left side must be INT
	// - if type of right side cannot be determined, the type of '->' type is set to "FLEX"
	private IData.Type impliesBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is '->' BOP", Level.LO, m);	
		/**/ tracer.println("token: " + token.toUserInfo(), Level.NO, m);	
		//--------------------------------------------------------------------------------
		// type is FLEX, if expression is ok
		IData.Type type = IData.Type.FLEX;			
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(800345, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// verify that left arg type is INT
		if(leftArgType != IData.Type.INT) {
			throw new SyntaxException("left arg of '->' op must be INT, but is " + leftArgType, m);
		}
		//--------------------------------------------------------------------------------
		// can't do more...
		/**/ tracer.println("'->' op type = " + type, Level.LO, m);							
		return type;
	}

	//================================================================================
	// IMPLIES BOP ('->')
	//================================================================================
	//--------------------------------------------------------------------------------
	// "," is used for nested '<quantifier> expression
	// - eg: (((<quantifier> <index> IN <range>) , (<quantifier> <index> IN <range>)) , (<quantifier> <index> IN <range>)) -> <do something>
	// - left side must be INT
	// - if type of right side cannot be determined, the type of '->' type is set to "FLEX"
	// -- otherwise (in case of nested expressions) it is set to right sides type (which is INT in this case)
	private IData.Type commaBopTypeEval(Token token) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("is ',' BOP", Level.LO, m);	
		/**/ tracer.println("token: " + token.toUserInfo(), Level.NO, m);	
		//--------------------------------------------------------------------------------
		// type is INT
		IData.Type type = IData.Type.INT;			
		//--------------------------------------------------------------------------------
		String errMsg1 = "";
		String errMsg2 = "'" + token.getRawText() + "'";
		//--------------------------------------------------------------------------------
		// evaluate data type of args
		IData.Type leftArgType;
		IData.Type rightArgType;
		try {
			//--------------------------------------------------------------------------------
			errMsg1 = "left arg of ";
			leftArgType = _typeEval(token.getLeft());
			//--------------------------------------------------------------------------------
			errMsg1 = "right arg of ";
			rightArgType = _typeEval(token.getRight());
			//--------------------------------------------------------------------------------
			/**/ tracer.println("left type = " + leftArgType + ", right type = " + rightArgType, Level.NO, m);	
			//--------------------------------------------------------------------------------
		} catch (SyntaxException e) {
			// throw new SyntaxException(errMsg1 + errMsg2, m, e); // too much info
			throw e;
		} catch (SNHException e) {
			throw new SNHException(801235, errMsg1 + errMsg2, m, e);
		}
		//--------------------------------------------------------------------------------
		// verify that left arg type is INT
		if(leftArgType != IData.Type.INT) {
			throw new SyntaxException("left arg of ',' op must be INT, but is " + leftArgType, m);
		}
		//--------------------------------------------------------------------------------
		// verify that right arg type is INT
		if(leftArgType != IData.Type.INT) {
			throw new SyntaxException("left arg of ',' op must be INT, but is " + leftArgType, m);
		}
		//--------------------------------------------------------------------------------
		// can't do more...
		/**/ tracer.println("'->' op type = " + type, Level.LO, m);							
		return type;
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================