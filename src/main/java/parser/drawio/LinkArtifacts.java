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

package parser.drawio;

import java.util.Vector;

import pmmm.*;
import qa.exceptions.SNHException;
import qa.exceptions.SyntaxException;
import qa.tracer.Level;
import qa.tracer.O;
import qa.tracer.Tracer;

//================================================================================
// translate artifacts of a wiring or a wiring type to guards and actions
public class LinkArtifacts extends Artifacts {
	// -------------------------------------------------------
	// for debug:
	/**/ private Tracer tracer = new Tracer();

	//================================================================================
	// CONSTRUCTORS
	//================================================================================
	// -------------------------------------------------------
	//nb: the artifacts have already been collected! they are passed to the constructor
	// - no super() must be called !!!
	public LinkArtifacts(Vector<Artifact> artifacts) {
		this.artifacts = artifacts;
	}

	//================================================================================
	// TRANSLATE
	//================================================================================
	//--------------------------------------------------------------------------------
	// either for wirings or for wiring types:
	// - recognize guard and action related artifacts and translate them to Guards and Actions;
	// - add them to the respective wiring or wiring type (enhancement!);
	// nb: either wirings or wiringTypes is given; recognized by the fact that the other one is null!
	public void translate(Vector<Wiring> wirings, Vector<WiringType> wiringTypes) throws SyntaxException, SNHException {
		//--------------------------------------------------------------------------------
		/**/ O m = new O(){}; // debug
		/**/ tracer.println("parse peer's artifacts", Level.NO, m);
		//--------------------------------------------------------------------------------
		// assertion
		if((wirings == null && wiringTypes == null) || (wirings != null && wiringTypes != null))
			throw new SNHException(622228, "ill. usage of translate of guards and actions", m);
		//--------------------------------------------------------------------------------
		// flag ... for simplicity and readability of the code
		boolean operationModeIsWirings = (wirings == null) ? false : true;
		//--------------------------------------------------------------------------------
		String nameDef = "";
		if(operationModeIsWirings) {
			nameDef = Defines.WIRING__NAME;
		}
		else {
			nameDef = Defines.WIRING_TYPE__NAME;
		}

		//================================================================================
		// PASS A: parse guards and actions
		//================================================================================
		// adds them to the correct wiring type (searches the wiring type...)
		/**/ tracer.println("PASS A:  parse guards and actions", Level.NO, m);
		for(int i1 = 0; i1 < artifacts.size(); i1++) {
			Artifact a1 = artifacts.get(i1);
			switch(a1.artifactName) {
			//================================================================================
			// GUARD: 
			//================================================================================
			case Defines.GUARD__WIRING_CONNECTOR:
				//--------------------------------------------------------------------------------
				// create a guard
				Guard guard = new Guard(); 
				//--------------------------------------------------------------------------------
				// the source of a1 indirectly refers to the guard, more precisely:
				// get artifact a2 with a2.id == a1.source ... this is any component of the guard 
				// - depending on where the connector points to, e.g. the guard number
				for(int i2 = 0; i2 < artifacts.size(); i2++) {
					Artifact a2 = artifacts.get(i2);
					if(a2.id.equals(a1.sourceId)) {
						//--------------------------------------------------------------------------------
						// get artifact a3 with a3.id == a2.parent 
						// - nb: a3.artifact == "XxxGuardSpecification" (Xxx = PIC, POC or NOOP)
						for(int i3 = 0; i3 < artifacts.size(); i3++) {
							Artifact a3 = artifacts.get(i3);
							if(a3.id.equals(a2.parentId)) {
								if(a3.artifactName.equals(Defines.NOOP_GUARD__SPECIFICATION)) {
									guard.setIsNoopLinkFlag(true);
								}
								//--------------------------------------------------------------------------------
								// get all artifacts a4 that belong to this guard, 
								// - i.e. where a4.parent == a3.id, and construct the guard
								for(int i4 = 0; i4 < artifacts.size(); i4++) {
									Artifact a4 = artifacts.get(i4);
									if(a4.parentId.equals(a3.id)) {
										/**/ tracer.println("GUARD ARTIFACT = " + a4.toString(), Level.NO, m);
										//--------------------------------------------------------------------------------
										// construct the guard
										switch(a4.artifactName) {
										case Defines.GUARD__NUMBER:
											try {
												guard.setLinkNumberAsString(a4.value);
											} catch (SyntaxException e) {
												throw new SyntaxException("guard number of " + a3.artifactName, m, e);
											}
											break;
										case Defines.GUARD__SPACE_OP:
											try {
												guard.setSpaceOpName(a4.value);
											} catch (SyntaxException e) {
												throw new SyntaxException("guard operation of " + a3.artifactName, m, e);
											}
											break;
										case Defines.GUARD__ENTRY_TYPE:
											guard.setEntryTypeName(a4.value);
											break;
										case Defines.GUARD__ENTRY_COUNT:
											guard.setRawEntryCount(a4.value);
											break;
										case Defines.GUARD__QUERY:
											guard.setRawQuery(a4.value);
											break;
										case Defines.GUARD__VAR_PROP_SET_GET:
											guard.setRawVarPropSetGet(a4.value);
											break;
										case Defines.GUARD__LPROPS_DEFS:
											guard.setRawLPropsDefs(a4.value);
											break;
										case Defines.GUARD__CONTAINER:
											guard.setContainerName(a4.value);
											// TBD: assert that  
											// - a3.artifactName is Defines.NOOP_GUARD_SPECIFICATION if a4.value is empty
											// - a3.artifactName is Defines.Defines.PIC_GUARD_SPECIFICATION if a4.value is PIC
											// - a3.artifactName is Defines.Defines.POC_GUARD_SPECIFICATION if a4.value is POC
											break;
										default:
											// beschriftungen
											break;
										}
									}
								}
							}
						}
						/**/ tracer.println("GUARD = " + guard.toString(), Level.NO, m);
						// break from loop a there is only one a2 
						break;
					}
				}
				//--------------------------------------------------------------------------------
				// the target  of a1 indirectly refers to the wiring, more precisely:
				// get artifact a5 with id == a1.target ... this is any component of the wiring
				// - depending on where the connector point to
				boolean wiring1Found = false;
				for(int i5 = 0; i5 < artifacts.size() && !wiring1Found; i5++) {
					Artifact a5 = artifacts.get(i5);
					if(a5.id.equals(a1.targetId)) {
						//--------------------------------------------------------------------------------
						// get artifact a6 with id == a5.parent ... this is the "XxxWiringSpecification"
						for(int i6 = 0; i6 < artifacts.size() && !wiring1Found; i6++) {
							Artifact a6 = artifacts.get(i6);
							if(a6.id.equals(a5.parentId)) {
								//--------------------------------------------------------------------------------
								// get artifact a7 "Wiring(Type)Name" with a7.parent == a6.id, 
								// - and add the guard to this wiring (type) (at correct place i.e. guard number!)
								for(int i7 = 0; i7 < artifacts.size() && !wiring1Found; i7++) {
									Artifact a7 = artifacts.get(i7);
									//--------------------------------------------------------------------------------
									if((a7.parentId.equals(a6.id)) && (a7.artifactName.equals(nameDef))) { 
										/**/ tracer.println((operationModeIsWirings ? "WIRING" : "WIRING TYPE") + " = " + a7.value, Level.NO, m);
										//--------------------------------------------------------------------------------
										// add above guard to this wiring or wiring type (find it by its name!)	
										if(operationModeIsWirings) {
											wirings = Wiring.addGuardToWirings(wirings, a7.value /* wiringName */, guard);
										}
										else {
											wiringTypes = WiringType.addGuardToWiringTypes(wiringTypes, a7.value /* wiringTypeName */, guard);
										}
										//--------------------------------------------------------------------------------
										// break from loop int i5: TBD ....
										wiring1Found = true;
										break;
									}
								}
							}
						}
					}
				}
				break;

				//================================================================================
				// ACTION
				//================================================================================
			case Defines.ACTION__WIRING_CONNECTOR:
				//--------------------------------------------------------------------------------
				// create an action
				Action action = new Action(); 
				//--------------------------------------------------------------------------------
				// the target of a1 indirectly refers to the action, more precisely:
				// get artifact a2 with a2.id == a1.target ... this is any component of the action 
				// - depending on where the connector points to, e.g. the action number
				for(int i2 = 0; i2 < artifacts.size(); i2++) {
					Artifact a2 = artifacts.get(i2);
					if(a2.id.equals(a1.targetId)) {
						//--------------------------------------------------------------------------------
						// get artifact a3 with a3.id == a2.parent 
						// - nb: a3.artifact == "XxxActionSpecification" (Xxx = PIC, POC or NOOP)
						for(int i3 = 0; i3 < artifacts.size(); i3++) {
							Artifact a3 = artifacts.get(i3);
							if(a3.id.equals(a2.parentId)) {
								if(a3.artifactName.equals(Defines.NOOP_GUARD__SPECIFICATION)) {
									action.setIsNoopLinkFlag(true);
								}
								//--------------------------------------------------------------------------------
								// get all artifacts a4 that belong to this action, 
								// - i.e. where a4.parent == a3.id, and construct the action
								for(int i4 = 0; i4 < artifacts.size(); i4++) {
									Artifact a4 = artifacts.get(i4);
									if(a4.parentId.equals(a3.id)) {
										/**/ tracer.println("ACTION ARTIFACT = " + a4.toString(), Level.NO, m);
										//--------------------------------------------------------------------------------
										// construct the action
										switch(a4.artifactName) {
										case Defines.ACTION__NUMBER:
											try {
												action.setLinkNumberAsString(a4.value);
											} catch (SyntaxException e) {
												throw new SyntaxException("action number of " + a3.artifactName, m, e);
											}
											break;
										case Defines.ACTION__SPACE_OP:
											try {
												action.setSpaceOpName(a4.value);
											} catch (SyntaxException e) {
												throw new SyntaxException("action operation of " + a3.artifactName, m, e);
											}
											break;
										case Defines.ACTION__ENTRY_TYPE:
											action.setEntryTypeName(a4.value);
											break;
										case Defines.ACTION__ENTRY_COUNT:
											action.setRawEntryCount(a4.value);
											break;
										case Defines.ACTION__QUERY:
											action.setRawQuery(a4.value);
											break;
										case Defines.ACTION__VAR_PROP_SET_GET:
											action.setRawVarPropSetGet(a4.value);
											break;
										case Defines.ACTION__LPROPS_DEFS:
											action.setRawLPropsDefs(a4.value);
											break;
										case Defines.ACTION__CONTAINER:
											action.setContainerName(a4.value);
											// TBD: assert that  
											// - a3.artifactName is Defines.NOOP_ACTION_SPECIFICATION if a4.value is empty
											// - a3.artifactName is Defines.Defines.PIC_ACTION_SPECIFICATION if a4.value is PIC
											// - a3.artifactName is Defines.Defines.POC_ACTION_SPECIFICATION if a4.value is POC
											break;
										default:
											// beschriftungen
											break;
										}
									}
								}
							}
						}
						/**/ tracer.println("ACTION = " + action.toString(), Level.NO, m);
						//--------------------------------------------------------------------------------
						// break from loop a there is only one a2 
						break;
					}
				}
				//--------------------------------------------------------------------------------
				// the source of a1 indirectly refers to the wiring, more precisely:
				// get artifact a5 with id == a1.source ... this is any component of the wiring
				// - depending on where the connector point to
				boolean wiring2Found = false;
				for(int i5 = 0; i5 < artifacts.size() && !wiring2Found; i5++) {
					Artifact a5 = artifacts.get(i5);
					if(a5.id.equals(a1.sourceId)) {
						//--------------------------------------------------------------------------------
						// get artifact a6 with id == a5.parent ... this is the "XxxWiringSpecification"
						for(int i6 = 0; i6 < artifacts.size() && !wiring2Found; i6++) {
							Artifact a6 = artifacts.get(i6);
							if(a6.id.equals(a5.parentId)) {
								//--------------------------------------------------------------------------------
								// get artifact a7 "WiringName" with a7.parent == a6.id, 
								// - and add the action to this wiring (at correct place i.e. action number!)
								for(int i7 = 0; i7 < artifacts.size() && !wiring2Found; i7++) {
									Artifact a7 = artifacts.get(i7);
									//--------------------------------------------------------------------------------
									if((a7.parentId.equals(a6.id)) && (a7.artifactName.equals(nameDef))) { 
										/**/ tracer.println((operationModeIsWirings ? "WIRING" : "WIRING TYPE") + " = " + a7.value, Level.NO, m);
										//--------------------------------------------------------------------------------
										// add above action to this wiring or wiring type (find it by its name!)
										if(operationModeIsWirings) {
											wirings = Wiring.addActionToWirings(wirings, a7.value /* wiringTypeName */, action);
										}
										else {
											wiringTypes = WiringType.addActionToWiringTypes(wiringTypes, a7.value /* wiringTypeName */, action);
										}
										//--------------------------------------------------------------------------------
										// break from loop int i5: TBD ....
										wiring2Found = true;
										break;
									}
								}
							}
						}
					}
				}
			default:
				break;
			}
		}	
	}


} // END OF CLASS


//================================================================================
// EOF
//================================================================================

