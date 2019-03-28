package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.serverim.Group;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.User;

import java.util.*;

public class MessageFactory {


    /**
     * Creates a new Message object.
     *
     * @param clientMessage the message received from the client
     * @param queryHandler the query handler
     * @return the message
     */
    public static Message createMessage(Message clientMessage, IQueryHandler queryHandler) {

        Message message;
        String[] arr = clientMessage.getText().split(" ", 2);
        if (arr.length > 1) {
            String type = getType(arr[0]);
            String restOfMessageText = arr[1];

            switch(type) {
                case MessageConstants.REGISTER_MSG_IDENTIFIER:
                    message = constructCustomRegisterMessage(restOfMessageText);
                    break;
                case MessageConstants.DIRECT_MSG_IDENTIFIER:
                    message = constructCustomDirectMessage(restOfMessageText);
                    break;
                case MessageConstants.LOGIN_MSG_IDENTIFIER:
                    message = constructCustomLoginMessage(restOfMessageText);
                    break;
                case MessageConstants.GROUP_MSG_IDENTIFIER:
                    message = constructCustomGroupMessage(restOfMessageText);
                    break;
                case MessageConstants.DELETE_MESSAGE_IDENTIFIER:
                    message = constructCustomDeleteMessage(restOfMessageText);
                    break;
                case MessageConstants.GET_INFO_IDENTIFIER:
                    message = constructCustomGetInfoMessage(restOfMessageText, queryHandler);
                    break;
                case MessageConstants.FORWARD_MSG_IDENTIFIER:
                    message = constructCustomForwardMessage(restOfMessageText, queryHandler);
                    break;
                case MessageConstants.ACTION_MSG_IDENTIFIER:
                    message = constructActionMessage(type, restOfMessageText);
                    break;
                default:
                    message = Message.makeErrorMessage(clientMessage.getName(),
                            MessageConstants.UNKNOWN_MESSAGE_TYPE_ERR);
                    break;
            }
        }
        else {
            message = Message.makeErrorMessage(clientMessage.getName(),
                    MessageConstants.EMPTY_MESSAGE_ERR);
        }
        return  message;
    }

    /**
     * Construct an action message.
     *
     * @param type the type
     * @param restOfMessageText the rest of message text
     * @return the message
     */
    private static Message constructActionMessage(String type, String restOfMessageText) {

        String[] contents = restOfMessageText.split(" ");
        String sender = contents[contents.length-1];

        String actionContent = restOfMessageText.substring(0,
                restOfMessageText.length()-(sender.length()+1));
        return Message.makeActionMessage(sender, actionContent);
    }

    /**
     * Construct a custom delete message.
     *
     * @param restOfMessageText the rest of message text
     * @return the message
     */
    private static Message constructCustomDeleteMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 3);

        String senderName = arr[0];
        String receiverName = arr[1];
        long messageId = Long.parseLong(arr[2]);

        return Message.makeDeleteMessage(messageId, senderName, receiverName);
    }

    /**
     * Construct a login message based on the parsed input message.
     *
     */
    private static Message constructCustomLoginMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 2);

        String userName = arr[0];
        String password = arr[1];

        return Message.makeLoginMessage(userName, password);
    }

    /**
     * Construct a register message based on the parsed input message.
     *
     */
    private static Message constructCustomRegisterMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 2);

        String userName = arr[0];
        String password = arr[1];

        return Message.makeRegisterMessage(userName, password);

    }

    /**
     * Construct a direct message based on the parsed input message.
     *
     */
    private static Message constructCustomDirectMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 3);

        String sender = arr[0];
        String receiver = arr[1];
        String actualContent = arr[2];

        return Message.makeDirectMessage(sender, receiver,  actualContent);
    }

    /**
     * Construct a group message based on the parsed input message.
     *
     */
    private static Message constructCustomGroupMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 3);

        String sender = arr[0];
        String groupName = arr[1];
        String actualContent = arr[2];

        return Message.makeGroupMessage(sender, groupName, actualContent);
    }

    /**
     * Construct a custom get info message.
     *
     * @param restOfMessageText the rest of message text
     * @param queryHandler the query handler
     * @return the message
     */
    private static Message constructCustomGetInfoMessage(String restOfMessageText, IQueryHandler queryHandler) {
        String[] content = restOfMessageText.split(" ", 2 );
        String commandType  = content[0];
        String sender = content[1];

        String result = getInfo(sender, commandType, queryHandler);

        return Message.makeGetInfoMessage(sender, sender, result);
    }

    /**
     * Construct a custom forward message.
     *
     * @param restOfMessagetext the rest of messagetext
     * @param queryHandler the query handler
     * @return the message
     */
    private static Message constructCustomForwardMessage (String restOfMessagetext, IQueryHandler queryHandler) {
        String[] content = restOfMessagetext.split(" ");
        String receiver = content[0];
        long messageId = Long.parseLong(content[1]);
        String sender = content[2];

        Message actualMessage = queryHandler.getMessage(messageId);
        String messageOriginator = actualMessage.getName();

        String text = actualMessage.getText() + " <<< FORWARDED MESSAGE FROM  " + messageOriginator
                + " >>>";

        return Message.makeDirectMessage(sender, receiver, text);
    }

    /**
     * Handle get info command query calls.
     *
     * @param senderName the sender name
     * @param commandType the command type
     * @param queryHandler the query handler
     * @return the requested info results
     */
    private static String getInfo(String senderName, String commandType, IQueryHandler queryHandler) {

        String info = null;
        if(commandType.equalsIgnoreCase(MessageConstants.GET_USERS_IDENTIFIER)) {
            List<User> users = queryHandler.getAllUsers();
            info =  handleGetUsers(MessageConstants.GET_USERS_CONSOLE_INFO, users);
        }
        else if (commandType.equalsIgnoreCase(MessageConstants.GET_GROUPS_IDENTIFIER)) {
            List<Group> groups = queryHandler.getAllGroups();
            info = handleGetGroups(MessageConstants.GET_GROUPS_CONSOLE_INFO, groups);
        }
        else if (commandType.equalsIgnoreCase(MessageConstants.GET_MY_USERS_IDENTIFIER)) {
            List<User> users = queryHandler.getMyUsers(senderName);
            info = handleGetUsers(MessageConstants.GET_MY_USERS_CONSOLE_INFO, users);
        }
        else if (commandType.equalsIgnoreCase(MessageConstants.GET_MY_GROUPS_IDENTIFIER)) {
            List<Group> groups = queryHandler.getMyGroups(senderName);
            info = handleGetGroups(MessageConstants.GET_MY_GROUPS_CONSOLE_INFO, groups);
        }
        else{
//            TODO - an error message saying type of info is wrong
        }
        return info;
    }

    /**
     * Handle get users.
     *
     * @param consoleInfo the console info
     * @param users the users
     * @return the string with concatenated user names
     */
    private static String handleGetUsers(String consoleInfo, List<User> users) {

        StringBuilder sb = new StringBuilder();
        sb.append(consoleInfo + "\n");
        for(User user: users) {
            sb.append(user.getUserID() + " - " + user.getUserName() + "\n");
        }
        return sb.toString();
    }

    /**
     * Handle get groups.
     *
     * @param consoleInfo the console info
     * @param groups the groups
     * @return the string with concatenated group names
     */
    private static String handleGetGroups(String consoleInfo, List<Group> groups) {
        StringBuilder sb = new StringBuilder();
        sb.append(consoleInfo + "\n");
        for(Group group: groups) {
            sb.append(group.getId() + " - " + group.getName() + "\n");
        }
        return sb.toString();
    }

    /**
     * Extracts message type from the message text which has the type prefixed in it.
     *
     */
    private static String getType(String s) {
        String messageTypeAsString = "";
        if(s.length() > 2) {
        		// removing $$ at the beginning and # at the end
            messageTypeAsString = s.substring(2, s.length()-1);
        }
        return messageTypeAsString;
    }
}
