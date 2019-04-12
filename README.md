# team-209-SP19
Team repo for team-209-SP19 (akhegde, vinayak, sethuramane, varsharaom) 

**Student repository URLs**:<br>
Akhilesh Hegde: https://github.ccs.neu.edu/cs5500/Student-223-SP19<br>
Elavazhagan Sethuraman: https://github.ccs.neu.edu/cs5500/Student-259-SP19<br>
Varsha Muroor Rao: https://github.ccs.neu.edu/cs5500/Student-244-SP19<br>
Vinayakaram Nagasubramanian: https://github.ccs.neu.edu/cs5500/Student-235-SP19<br>

## Messaging application with a scalable backend. Written in Java and persisted in MySQL.
- Extensible command line client interface
- Supports direct and group messaging.
- Supports special message types such as forwarded, group subset and time out messages.
- Allows users to obtain messages translated in a (limited set of) language of their choice.
- Allow users to enable chat filtering to mask abusive content.

Documentation
- [Installation](#installation)
- [Components](#components)
- [Requirements](#requirements)

### Installation
* We need a client to run the chat application. For this purpose, we have added an extensive command line based client packaged as a JAR, which can be found in Development/ClientJAR.
* The JAR has to be executed by providing the server endpoint to which the client must connect.
* The JAR can be executed as: java -jar Chatter.jar <endpoint>

### Components
* Chatter
  * Extended Chatter client to provide the functionalities implemented in the chat server.
* Prattle
  * Extended Prattle client to implement full chat functionality.
* Deployment server
  * AWS instance to deploy the prattle server. Exposes a public endpoint to which any user having the Chatter client can connect.
  
### Requirements
* Java 1.8

