# team-209-SP19
Team repo for team-209-SP19 (akhegde, vinayak, sethuramane, varsharaom) 

**Student repository URLs**:<br>
Akhilesh Hegde: https://github.ccs.neu.edu/cs5500/Student-223-SP19<br>
Elavazhagan Sethuraman: https://github.ccs.neu.edu/cs5500/Student-259-SP19<br>
Varsha Muroor Rao: https://github.ccs.neu.edu/cs5500/Student-244-SP19<br>
Vinayakaram Nagasubramanian: https://github.ccs.neu.edu/cs5500/Student-235-SP19<br>

## Youtube Videos
- [System Setup](https://youtu.be/N6ODQkEy_kQ)
- [System Demo](https://youtu.be/OxmLVMbMJ9s)
- [Final Presentation](https://youtu.be/lPfVFpD00o8)

## Messaging application with a scalable backend(Written in Java and persisted in MySQL)
- Extensible command line client interface
- Supports direct and group messaging.
- Deletions of messages.
- Supports special message types such as forwarded, group subset and time out messages.
- Allows users to obtain messages translated in a (limited set of) language of their choice.
- Allow users to enable chat filtering to mask abusive content.

## Documentation
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

## How to use the application
* This is a command line b$$LGN# <username> <password>ased application. The commands typed in the terminal by the client are parsed and interpreted in the server side. To have an overview of all the commands in the application, use the following command:
          * $$HELP#
 
* This application is classified in to two modes - operation and messaging modes. In messaging mode, the user can send messages to another user, another group, a subset of users from the group and forward messages. In operation mode, the user can obtain information about users and groups, track messages, delete messages, add a moderator, remove a person from the group, add a person to the group and the like. To toggle in to those modes, the following commands are used:
          * OPT
          * MSG
          
* To register as a new user, the command is:
          * $$RGSTR# <username> <password>
 
* To login as an existing user:
          * $$LGN# <username> <password>
 
* Various information about other users and groups can be obtained by the following commands:
           * GT_USRS
           * GT_MY_USRS
           * GT_GRPS
           * GT_MY_GRPS
           
* To change the language,
           * CHANGE_LANG <language>
           Note: es for Espanol, ar for Arabic. For more details on supported languages by this application, please visit 
 [https://cloud.ibm.com/docs/services/language-translator?topic=language-translator-identifiable-languages#identifiable-languages] 
 
 * To reset to the english language,
           * RESET_LANG <language>
 
 * To track a message from its origin, 
           * TRACK_MSG <MessageId>
 
 * To send a direct message to another user in the application,
           * $$DRCT# <username> 
 
 * To send a group message to a group,
           * $$GRP# <username> 
