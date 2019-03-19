package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.persistence.QueryFactory;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

import java.util.List;

public class MessageFactory {


    public static Message createMessage(Message clientMessage) {

        Message message;
        String[] arr = clientMessage.getText().split(" ", 2);
        if (arr.length > 1) {
            String type = getType(arr[0]);
            String restOfMessageText = arr[1];

            if (type.equalsIgnoreCase(MessageType.REGISTER.toString())) {
                message = constructCustomRegisterMessage(restOfMessageText);
            }
            else if (type.equalsIgnoreCase(MessageType.DIRECT.toString())) {
                message = constructCustomDirectMessage(restOfMessageText);
            }
            else if (type.equalsIgnoreCase(MessageType.LOGIN.toString())) {
                message = constructCustomLoginMessage(restOfMessageText);
            }
            else if (type.equalsIgnoreCase(MessageType.GROUP.toString())){
                message = constructCustomGroupMessage(restOfMessageText);
            }
            else if (type.equalsIgnoreCase(MessageType.DELETE.toString())) {
                message = constructCustomDeleteMessage(restOfMessageText);
            }
            else if (type.equalsIgnoreCase(MessageType.GET_USERS.toString())){
                message = constructCustomGetUsersMessage(restOfMessageText);
            }
            else {
                message = Message.makeErrorMessage(clientMessage.getName(),
                        ClientRunnableConstants.UNKNOWN_MESSAGE_TYPE_ERR);
            }
        }
        else {
            message = Message.makeErrorMessage(clientMessage.getName(),
                    ClientRunnableConstants.EMPTY_MESSAGE_ERR);
        }
        return  message;
    }

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

        return Message.makeDirectMessage(sender, receiver, sender + " : " + actualContent);
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

    private static Message constructCustomGetUsersMessage(String restOfMessageText) {
        String sender = restOfMessageText;
        IQueryHandler queryHandler = QueryFactory.getQueryHandler();
        List<User> userList = queryHandler.getAllUsers();

        StringBuilder sb = new StringBuilder();
        sb.append("List of users:\n");
        for(User user: userList) {
            sb.append(user.getUserName() + "\n");
        }

        return Message.makeGetUsersMessage(sender, sender, sb.toString());
    }

    /**
     * Extracts message type from the message text which has the type prefixed in it.
     *
     */
    private static String getType(String s) {
        String messageTypeAsString = "";
        if(s.length() > 2) {
//            removing $$ at the beginning and # at the end
            messageTypeAsString = s.substring(2, s.length()-1);
        }
        return messageTypeAsString;
    }
}
