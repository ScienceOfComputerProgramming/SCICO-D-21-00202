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
//SYSTEM:    Practical Peer Model Toolchain "for the poor woman/man"
//AUTHOR:    Eva Maria Kuehn
//CREATED:   December 2020 
//================================================================================

package codeGen.Go;

// import debug.*;
import pmmm.LinkInstance;
import pmmm.WiringInstance;
import qa.exceptions.CodeGenException;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.O;


//================================================================================

//================================================================================
public class Wiring2Go {
	//--------------------------------------------------------------------------------
	// debug
	// /**/ private Tracer tracer = new Tracer(); 
	//--------------------------------------------------------------------------------
	private GoCodeWriter codeWriter;
	private WiringInstance wiringInstance;

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	//--------------------------------------------------------------------------------
	public Wiring2Go(GoCodeWriter codeWriter, WiringInstance wiringInstance) {
		this.codeWriter = codeWriter;
		this.wiringInstance = wiringInstance;
	}

	//================================================================================
	// WIRING
	//================================================================================
	//--------------------------------------------------------------------------------
	public void generateCode() throws CodeGenException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		//--------------------------------------------------------------------------------
		codeWriter.writelnInd("//------------------------------------------------------------");
		codeWriter.writelnInd("// WIRING " + wiringInstance.getWiringInstanceName() + ":");
		codeWriter.writelnInd("//------------------------------------------------------------");
		codeWriter.writelnInd("w = NewWiring(\"" + wiringInstance.getWiringInstanceName() + "\")");
		//--------------------------------------------------------------------------------
		// gen code for service (if defined)
		String serviceName = wiringInstance.getServiceName();
		boolean serviceFlag = false;
		String serviceId = "";
		if((serviceName != null) && (serviceName.length() > 0)) {
			serviceFlag = true;
			//--------------------------------------------------------------------------------
			// add service wrapper
			codeWriter.writelnInd("//............................................................");
			codeWriter.writelnInd("// add service wrapper:");
			//--------------------------------------------------------------------------------
			// TBD: service id is "SID_<service name>", assuming that wiring call this service only once 
			// - nb: there is only one service call supported anyhow...
			serviceId = "\"SID_" + serviceName + "\"";
			codeWriter.writelnInd("w.AddServiceWrapper(" + serviceId + ", NewServiceWrapper(" + serviceName + ", " + " \"" + serviceName + "\"))");
		}
		//--------------------------------------------------------------------------------
		// speciality of the go automaton: needs "commit = true" on the last link's lprop
		// - how many links does wiring have?
		int n = wiringInstance.getNLinks();
		//--------------------------------------------------------------------------------
		// gen code for guard instances
		for(int i = 0; i < wiringInstance.getGuardInstances().size(); i++) {
			LinkInstance guardInstance = wiringInstance.getGuardInstances().get(i);
			try {
				codeWriter.writelnInd("//............................................................");
				codeWriter.writelnInd("// Guard " + (i+1) + ":");  
				new Link2Go(codeWriter, "Guard", guardInstance).generateCode(--n == 0 /* last link flag */); 
			} catch (CodeGenException e) {
				throw new CodeGenException("guard instance " + guardInstance.getNumberAsString(), m, e);
			} catch (SNHException e) {
				throw new SNHException(299292, "guard instance " + guardInstance.getNumberAsString(), m, e);
			}
		}
		//--------------------------------------------------------------------------------
		// gen code for service IN/OUT
		if(serviceFlag) {
			codeWriter.writelnInd("//............................................................");
			codeWriter.writelnInd("// service wrapper:");
			//--------------------------------------------------------------------------------
			// SINs: TBD: for now: take all entries via SIN 1
			// - SIN 1
			codeWriter.writelnInd("// SIN1:");
			codeWriter.writelnInd("w.AddSin(TAKE, Query{Typ: SVal(\"*\"), Count: IVal(ALL)}, " +
					serviceId + ", LProps{}, EProps{}, Vars{})");
			//--------------------------------------------------------------------------------
			// - CALL service
			codeWriter.writelnInd("// CALL SERVICE:");
			codeWriter.writelnInd("w.AddScall(" + serviceId + ", LProps{}, EProps{}, Vars{})");
			//--------------------------------------------------------------------------------
			// SOUTs: TBD: for now: emit all entries via SOUT 1
			// - SOUT 1
			codeWriter.writelnInd("// SOUT1:");
			codeWriter.writelnInd("w.AddSout(Query{Typ: SVal(\"*\"), Count: IVal(ALL)}, " +
					serviceId + ", LProps{}, EProps{}, Vars{})");
		}
		//--------------------------------------------------------------------------------
		// gen code for action instances
		for(int i = 0; i < wiringInstance.getActionInstances().size(); i++) {
			LinkInstance actionInstance = wiringInstance.getActionInstances().get(i);
			try {
				codeWriter.writelnInd("//............................................................");
				codeWriter.writelnInd("// Action " + (i+1) + ":");
				new Link2Go(codeWriter, "Action", actionInstance).generateCode(--n == 0 /* last link flag */);
			} catch (CodeGenException e) {
				throw new CodeGenException("action instance " + actionInstance.getNumberAsString(), m, e);
			} catch (SNHException e) {
				throw new SNHException(929292, "action instance " + actionInstance.getNumberAsString(), m, e);
			}
		}
		//--------------------------------------------------------------------------------
		// gen code for wprops
		codeWriter.writelnInd("//............................................................");
		codeWriter.writelnInd("// set wprops:");
		codeWriter.writeInd("w.WProps = ");
		try {
			(new Props2Go(codeWriter, wiringInstance.getProcessedWiringWPropsDefs(), "W" /* witing props */)).generateCode();
		} catch (SyntaxException e) {
			throw new CodeGenException("wprops", m, e);
		} catch (SNHException e) {
			throw new SNHException(838587, "wprops", m, e);
		}
		codeWriter.nl();
		//--------------------------------------------------------------------------------
		// gen code for adding wiring to peer
		codeWriter.writelnInd("//............................................................");
		codeWriter.writelnInd("// add wiring to peer & resolve names:");
		codeWriter.writelnInd("p.AddWiring(w)");
		codeWriter.nl();
		//--------------------------------------------------------------------------------
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

