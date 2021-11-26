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
package parser;

import java.util.Vector;

import eval.tokens.TokenExpression;
import pmmm.*;
import qa.exceptions.SyntaxException;
import qa.tracer.O;

//================================================================================
// result of parsing: assembly of all PMMM components
// nb: all fields are public
// the class serves to transport data between parser and pmmm 
public class PmmmComponents {
	//--------------------------------------------------------------------------------
	// /**/ private Tracer tracer = new Tracer();  // debug
	//--------------------------------------------------------------------------------
	// PMMM TYPE NAME:
	public String pmmmTypeName = "";
	//--------------------------------------------------------------------------------
	// PMMM TYPE PMMM PROPS TYPES:
	public PropsTypes pmmmTypePmmmPropsTypes = new PropsTypes();
	//--------------------------------------------------------------------------------
	// PMMM TYPE PMMM PROPS DEFS: 
	public TokenExpression pmmmTypePmmmPropsDefsTokenExpression = new TokenExpression();
	//--------------------------------------------------------------------------------
	// ENTRY TYPES:
	public EntryTypes entryTypes = new EntryTypes();
	//--------------------------------------------------------------------------------
	// WIRING TYPES:
	public Vector<WiringType> wiringTypes = new Vector<WiringType>();
	//--------------------------------------------------------------------------------
	// PEER TYPES:
	public Vector<PeerType> peerTypes = new Vector<PeerType>();
	//--------------------------------------------------------------------------------
	// CONFIGS:
	public Vector<Config> configs = new Vector<Config>();
	//--------------------------------------------------------------------------------

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public PmmmComponents() {
	}

	//================================================================================
	// QUERY
	//================================================================================
	//--------------------------------------------------------------------------------
	public Config getConfig(String configName) throws SyntaxException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		for(int i = 0; i < configs.size(); i++) {
			Config config = configs.get(i);
			if(config.getConfigName().equals(configName))
				return config;
		}
		throw new SyntaxException("config '" + configName + "' does not exist", m);
	}

	//================================================================================
	//================================================================================
	// DEBUG
	//================================================================================
	//================================================================================
	//--------------------------------------------------------------------------------
	// overwrite toString() with default fu
	public String toString() {
		return toString(true);
	}
	//--------------------------------------------------------------------------------
	public String toString(boolean showProcessedFieldsFlag) {
		//--------------------------------------------------------------------------------
		// debug:
		// /**/ Object m = new Object(){};  
		//--------------------------------------------------------------------------------
		StringBuffer buf = new StringBuffer();
		//================================================================================
		//--------------------------------------------------------------------------------
		buf.append("\n");
		buf.append(ui.Out.banner("PMMM COMPONENTS:", '*'));
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.addAlignmentInbetween("PMMM:", pmmmTypeName + "\n"));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.propsTypes2StructuredString("pmmm type pmmm props types", pmmmTypePmmmPropsTypes));
		//--------------------------------------------------------------------------------
		buf.append(PmUtil.tokenExpression2StructuredString("pmmm type props defs", pmmmTypePmmmPropsDefsTokenExpression));
		//--------------------------------------------------------------------------------
		buf.append(ui.Out.borderline('-'));
		//--------------------------------------------------------------------------------
		if(entryTypes.getSize() > 0) {
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
		}	//--------------------------------------------------------------------------------
		if(configs.size() > 0) {
			buf.append(ui.Out.banner("CONFIGS:", '*'));
			for(int i = 0; i < configs.size(); i++) {
				buf.append(configs.get(i).toStructuredString());
			}
			buf.append(ui.Out.borderline('='));

		}
		buf.append(ui.Out.banner("END OF PMMM TYPE", '*'));
		return new String(buf);
	}


} // END OF CLASS


//================================================================================
//EOF
//================================================================================
