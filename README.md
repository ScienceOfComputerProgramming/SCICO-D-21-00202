# translator 
The Peer Model is a modeling tool for distribution, concurrency and blackboard-based collaboration and coordination.
This repository contains the translator that converts a use case into go-code that can be executed by the simulator.

Installation, Translation and Simulation:

1) Download the most recent jar file of the peermodel-translator from https://github.com/peermodel/translator/releases and store it in a local directory. Let us refer to the absolute path of this local directory with LDIR in the following.

2) Check out the examples found in https://github.com/peermodel/examples into LDIR.

3) Open a powershell and change directory to LDIR, i.e:

  cd LDIR

4) Execute the following command to translate the ClientServer use case, modeled with DRAWIO, into go-code. The use case may have many configurations, concretely we will compile the configuration termed "One". The use case must be exported as xml, concretely this xml is found in "Apps":  

  java -jar ./peermodel-translator-2.0.0.jar DRAWIO ./examples/ Apps/ ClientServer One GO-CODE
  
  Note: In LDIR/examples/_GO-AUTOMATON/src/useCases the compiled use case .go-files can be found. The first time you translate a use case, the directory LDIR/examples/_GO-AUTOMATON is created.
  
  Info: The ClientServer example starts two peers termed client1 and superServer. Client1 sends one request to the superServer which in turn sends an answer back. When client1 receives the answer, the system is stopped.

5) Execute once the following commands (starting in LDIR) in order to create all dependencies for the simulator:

  cd LDIR/examples/_GO-AUTOMATON/src/useCases
  
  go mod tidy
  
  cd LDIR

6) Now you can run the desired use case. E.g. if you want to run the simulator with the just compiled ClientServer use case, you have to type:

  cd LDIR/examples/_GO-AUTOMATON/src/useCases/Apps/ClientServer_One/test
  
  Info: The simulator output for the example use case contains:
  - Traces of services called by peers:
   Client1 peer reports that it was started which means that it sent a request to the superServer peer.
   SuperServer peer reports that it was initialized.
   SuperServer peer reports that it replied an answer to client1 peer.
   Client1 reports that it stopped and displays the entry representing the answer from superServer peer
   System service "STOP" was called.
  - SYS INFO traces that inform about STOP of all automata machines, and about some system modi flags.
  - The result space after use case execution, namely that in the output container of the client1 peer (termed "client1-POC, whereby POC stands for peer output container) is one entry that is the answer that client1 received from superServer.
  - Some system statistics about the simulation run.
	
7) Analogously you may start other use cases.
	
8) For how to model your own use cases please read the readme file in examples.

--
