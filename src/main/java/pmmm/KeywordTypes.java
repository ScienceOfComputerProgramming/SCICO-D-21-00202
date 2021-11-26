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

import eval.IData;
import qa.exceptions.NotFoundException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// reserved names and their types including vars !!! 
// TBD: complete the lists ....
public class KeywordTypes {
	//--------------------------------------------------------------------------------
	// for debugging
	private Tracer tracer = new Tracer();
	//--------------------------------------------------------------------------------
	// sys consts
	protected NameTypeMap sysConstTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// sys fus
	protected NameTypeMap sysFuTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system vars
	protected NameTypeMap sysVarTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system peers
	// - nb: data type not really needed... is always URL
	protected NameTypeMap sysPeerTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system pmmm properties
	protected NameTypeMap sysPmmmPropTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system peer properties
	protected NameTypeMap sysPPropTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system wiring properties
	protected NameTypeMap sysWPropTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system link properties
	protected NameTypeMap sysLPropTypeMap = new NameTypeMap(); 
	//--------------------------------------------------------------------------------
	// system entry properties
	protected NameTypeMap sysEPropTypeMap = new NameTypeMap(); 

	//================================================================================
	// each group shall be sorted in alphabetical order... to easier check duplicates with a group
	public KeywordTypes() throws SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			//--------------------------------------------------------------------------------
			// system constants (incl. built-in types)
			//--------------------------------------------------------------------------------
			sysConstTypeMap.add("ALL", IData.Type.INT);
			sysConstTypeMap.add("BOOLEAN", IData.Type.BOOLEAN);
			sysConstTypeMap.add("BOOLEANS", IData.Type.BOOLEAN_ARRAY); 
			sysConstTypeMap.add("INFINITE", IData.Type.INT);
			sysConstTypeMap.add("INT", IData.Type.INT);
			sysConstTypeMap.add("INTS", IData.Type.INT_ARRAY); 
			sysConstTypeMap.add("NONE", IData.Type.INT);
			sysConstTypeMap.add("off", IData.Type.BOOLEAN);
			sysConstTypeMap.add("on", IData.Type.BOOLEAN);
			sysConstTypeMap.add("PCC", IData.Type.STRING);
			sysConstTypeMap.add(IPmDefs.PMMM_QUALIFIER, IData.Type.STRING);
			sysConstTypeMap.add(IPmDefs.PEER_QUALIFIER, IData.Type.STRING);
			sysConstTypeMap.add("RANGE", IData.Type.INT);
			sysConstTypeMap.add("STRING", IData.Type.STRING);
			sysConstTypeMap.add("STRINGS", IData.Type.STRING_ARRAY);
			sysConstTypeMap.add("URL", IData.Type.URL);
			sysConstTypeMap.add("URLS", IData.Type.URL_ARRAY); // array
			sysConstTypeMap.add(IPmDefs.WIRING_QUALIFIER, IData.Type.STRING);
			//--------------------------------------------------------------------------------
			// system fus
			//--------------------------------------------------------------------------------
			sysFuTypeMap.add("fid", IData.Type.STRING);
			sysFuTypeMap.add("FID", IData.Type.STRING);
			sysFuTypeMap.add("clock", IData.Type.INT);
			sysFuTypeMap.add("CLOCK", IData.Type.INT);
			sysFuTypeMap.add("uuid", IData.Type.STRING);
			sysFuTypeMap.add("FUUID", IData.Type.STRING);
			//--------------------------------------------------------------------------------
			// system vars
			//--------------------------------------------------------------------------------
			sysVarTypeMap.add("$$CNT", IData.Type.INT);
			sysVarTypeMap.add("$$FID", IData.Type.STRING);
			sysVarTypeMap.add("$$PID", IData.Type.URL);
			sysVarTypeMap.add("$$WID", IData.Type.STRING);
			//--------------------------------------------------------------------------------
			// system peers
			//--------------------------------------------------------------------------------
			sysPeerTypeMap.add("Stop", IData.Type.URL);
			//--------------------------------------------------------------------------------
			// system pmmm properties
			//--------------------------------------------------------------------------------
			// - TBD
			//--------------------------------------------------------------------------------
			// system peer properties
			//--------------------------------------------------------------------------------
			// - TBD
			//--------------------------------------------------------------------------------
			// system wiring properties
			//--------------------------------------------------------------------------------
			sysWPropTypeMap.add("flow", IData.Type.BOOLEAN);
			sysWPropTypeMap.add("max_threads", IData.Type.INT);
			sysWPropTypeMap.add("repeat_count", IData.Type.INT);
			sysWPropTypeMap.add("ttl", IData.Type.INT);
			sysWPropTypeMap.add("tts", IData.Type.INT);
			sysWPropTypeMap.add("txcc", IData.Type.STRING);
			//--------------------------------------------------------------------------------
			// system entry properties
			//--------------------------------------------------------------------------------
			sysEPropTypeMap.add("fid", IData.Type.STRING);
			sysEPropTypeMap.add("dest", IData.Type.URL);
			sysEPropTypeMap.add("ttl", IData.Type.INT);
			sysEPropTypeMap.add("tts", IData.Type.INT);
			sysEPropTypeMap.add("type", IData.Type.STRING);
			//--------------------------------------------------------------------------------
			// system link properties
			//--------------------------------------------------------------------------------
			sysLPropTypeMap.add("commit", IData.Type.BOOLEAN);
			sysLPropTypeMap.add("dest", IData.Type.URL);
			sysLPropTypeMap.add("flow", IData.Type.BOOLEAN);
			sysLPropTypeMap.add("mandatory", IData.Type.BOOLEAN);
			sysLPropTypeMap.add("source", IData.Type.URL);
			sysLPropTypeMap.add("tts", IData.Type.INT);
			sysLPropTypeMap.add("ttl", IData.Type.INT);
			//--------------------------------------------------------------------------------
		} catch(SyntaxException e) {
			// ERROR: there was a duplicate name
			throw new SNHException(991191, "duplicate sys keyword: ", m, e);
		}
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	// is it an integer?
	// - if not, throw exception
	// caution: keep up to date with above sys consts!
	// TBD: names are hard coded;
	public static void checkIntSysConst(String intAsString, boolean infiniteIsAllowedFlag) throws Exception {
		if(! (intAsString.equals("ALL") || intAsString.equals("NONE") || 
				(infiniteIsAllowedFlag && intAsString.equals("INFINITE"))))
			throw new Exception("'" + intAsString + "' is not a sys const");
	} 
	//--------------------------------------------------------------------------------
	// test if name is a sys const;
	// throws exception if not found;
	public boolean isSysConst(String name) {
		try {
			sysConstTypeMap.getType(name);
			// found
			return true;
		} catch (NotFoundException e) {
			return false;
		}		
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	// get sys const type
	public IData.Type getSysConstType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return sysConstTypeMap.getType(name);
		} catch (NotFoundException e) {
			throw new NotFoundException("sys const", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys var type
	public IData.Type getSysVarType(String sysVarName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("sysVarTypeMap = " + sysVarTypeMap.toPairs(), Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			return sysVarTypeMap.getType(sysVarName);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys var", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys peer type
	// - TBD: type must be URL... 
	public IData.Type getSysPeerType(String sysPeerName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("sysPeerTypeMap = " + sysPeerTypeMap.toPairs(), Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			return sysPeerTypeMap.getType(sysPeerName);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys peer", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys fu type
	public IData.Type getSysFuType(String sysFuName) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("sysFuTypeMap" + sysFuTypeMap, Level.NO, m);
		//--------------------------------------------------------------------------------
		try {
			return sysFuTypeMap.getType(sysFuName);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys fu", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys pmmm prop type
	public IData.Type getSysPmmmPropType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return sysPmmmPropTypeMap.getType(name);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys pmmm prop", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys pprop type
	public IData.Type getSysPPropType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return sysPPropTypeMap.getType(name);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys pprop", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys wprop type
	public IData.Type getSysWPropType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return sysWPropTypeMap.getType(name);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys wprop", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys eprop type
	public IData.Type getSysEPropType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return sysEPropTypeMap.getType(name);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys eprop", m, e);
		}
	} 
	//--------------------------------------------------------------------------------
	// get sys lprop type
	public IData.Type getSysLPropType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		try {
			return sysLPropTypeMap.getType(name);
		} catch (NotFoundException e) {
			throw new NotFoundException("is not sys wlprop", m, e);
		}
	} 

	
} // END OF CLASS


//================================================================================
//EOF
//================================================================================