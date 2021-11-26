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

package pmmm;

import eval.*;
import qa.exceptions.NotFoundException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.Tracer;

import java.util.HashMap;

//================================================================================
// hash map with nam/type pairs
public class NameTypeMap {
	//--------------------------------------------------------------------------------
	// debug
	/**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	HashMap<String,IData.Type> map = new HashMap<String,IData.Type>();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public NameTypeMap() {
	}

	//================================================================================
	// ADD
	//================================================================================
	//--------------------------------------------------------------------------------
	// add, if not yet
	public void add(String name, IData.Type type) throws SyntaxException {
		//--------------------------------------------------------------------------------
		// debug:
		/**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		if(map.get(name) == null)
			map.put(name, type);
		else
			throw new SyntaxException("duplicate name = " + name, m);
	}

	//================================================================================
	// GET
	//================================================================================
	//--------------------------------------------------------------------------------
	// get type by name, if exists
	// otherwise throw exception
	public IData.Type getType(String name) throws NotFoundException {
		//--------------------------------------------------------------------------------
		/**/ Object m = new Object(){}; // debug
		//--------------------------------------------------------------------------------
		/**/ tracer.println("map = " + map, Level.NO, m);
		IData.Type type = map.get(name);
		if(type == null) {
			throw new NotFoundException("type of '" + name + "' not found", m);
		}
		return type;
	}

	//================================================================================
	// TEST
	//================================================================================
	//--------------------------------------------------------------------------------
	// does name exist in the map
	public boolean nameExists(String name) {
		return map.get(name) != null;
	}

	//================================================================================
	// DEBUG
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString()
	public String toString() {
		return toPairs();
	}
	//--------------------------------------------------------------------------------
	// to pairs
	public String toPairs() {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		for(HashMap.Entry<String,IData.Type> entry : map.entrySet()) {
			buf.append(entry.getKey() + ":" + entry.getValue() + "; ");
			// buf.append(entry.getValue() + "; ");
		}
		buf.append("}");
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
// ADD
//================================================================================
