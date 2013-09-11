package com.caucho.makai.example;

import io.baratine.core.*;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 *
 * This might well be called ChatroomController as well but it really is
 * more API than 'controller'...front end holds the control...
 */
@Startup
@Service({"/chatroomAPI"})
@Remote
public class ChatroomAPI {

    private Logger logger = Logger.getLogger("ChatroomAPI");

    @Inject
    private ChatroomService chatroomService;

    public ChatroomAPI() {
        logger.info("ChatroomAPI constructor.");
    }

    /** List of active clients, meaning browsers hitting the service **/
    private List<ChatroomCallbacks> activeClients = new ArrayList<>();

    /** List of clients in a room, by room id  **/
    private Map<String, List<ChatroomCallbacks>> activeRooms = new HashMap<>();

    /** CALLBACK WORK
     *
     * REGISTRATION -
     * These methods perform the work of subscribing a client socket by storing
     * a reference to that socket in a service/actor wrapper. This can later
     * be used by invoking the methods in the callback interfaces, which passes
     * any data recieved to the client in a registered callback listener.
     *
     * In the subscribeActiveUser case the client passes a single arg which gets wrapped
     * in the @Service along with the ChatroomCallbacks object.
     *
     *
     * In the joinChat case the client passes 2 args to this, one is the publishChannel, which gets wrapped
     * in this @Service along with the ChatroomCallbacks interfaces. Makai binds
     * these together into a messaging channel..."pump any interfaces methods+data back
     * to this client address when invoked.
     *
     * The second arg is just the room id...
     *
     * e.g. [channel:///jamp/CDpFZ+9ASNZAAAAUEK7WLc/reply-address, 3e960264-cece-45e3-95f6-718caafa9ece]
     *
     * **/

    /** This says create a wrapper around an actor who knows how to invoke a client websocket **/
    public void subscribeActiveUser(@Service ChatroomCallbacks callback) {
        logger.info("CHATROOMAPI (callback) added.");
        activeClients.add(callback);
    }

    public void joinChat(@Service ChatroomCallbacks callback, String roomId) {
        Chatroom room = chatroomService.read(roomId);

        if (room == null) {
            //somehow we are trying to join a bogus room.

            //consider creating it...
            return;
        }
        logger.info("CHATROOMAPI: client joined chatroom:" + room.getName());

        List<ChatroomCallbacks> listeners = activeRooms.get(roomId);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(callback);
        activeRooms.put(roomId, listeners);
    }


    /**
     * CALLBACK WORK
     *
     * INVOCATIONS -
     *
     * These methods leverage the above registrations, using the wrapped interfaces
     * to invoke methods 'over the wire' on the client. In fact there is only
     * ever one invocation to the registered listener, but the client-side-method invoked
     * recieves the name of the interface (callback) method invoked and the data
     * passed to it.     *
     *
     */
    /**
     * Sends the list of rooms back to all registered clients.
     * This is invoked when a client ui calls createChat()
     */
    public void sendRoomList() {
        logger.info("CHATROOMAPI-sendRoomList called.");

        Collection<Chatroom> rooms = chatroomService.list().values();

        for (ChatroomCallbacks callback : activeClients) {
            logger.info("CHATROOMAPI-sendRoomList sent:" + rooms.size() + " rooms");//TODO: Make user part of this
            try {
                callback.sendRoomList(rooms);
            } catch (Throwable t) {
                logger.log(Level.ALL, "CHATROOMAPI-Problem sending room list to callback", t);
            }

        }
    }

    public void broadcastMessage(Chatroom room, Message message) {
        logger.info("CHATROOMAPI-sendMessage called.");

        List<ChatroomCallbacks> messageRecipients = activeRooms.get(room.getId());
        for (ChatroomCallbacks callback : messageRecipients) {
            logger.info("CHATROOMAPI-sendMessage sent message:" + message.message + "for: ");// + message.user.getHandle());
            try {
                callback.sendMessage(room.getId(), message);
            } catch (Throwable t) {
                logger.log(Level.ALL, "CHATROOMAPI-Problem sending new message to callback:" + t.getMessage(), t);
            }

        }
    }

    /** END CALLBACK WORK **/

    /** "query" **/
    public Collection<Chatroom> list() {
        logger.info("CHATROOMAPI-list - listing chats");
        return chatroomService.list().values();
    }

    /** "query" **/
    public Collection<Message> getMessages(String roomId) {
        logger.info("CHATROOMAPI-getMessages - sending messages");
        return chatroomService.read(roomId).getMessages();
    }


    /** "send" **/
    public void createChat(Chatroom chatroom) {
        logger.info("CHATROOMAPI-Creating chat room:" + chatroom.getName());
        chatroomService.create(chatroom);
        sendRoomList();
    }

    /** "send" **/
    public void deleteChat(String roomId) {
        logger.info("CHATROOMAPI-deleting chat room with id:" + roomId);
        chatroomService.delete(roomId);
        sendRoomList();
    }

    /** "send" **/
    public void sendMessage(Message messageObj, String roomId) {
        Chatroom room = chatroomService.read(roomId);
//        Message messageObj = new Message();
//        messageObj.message = message;
        room.getMessages().add(messageObj);
        chatroomService.update(room);
        logger.info("CHATROOMAPI-Sending message" + messageObj.message + " to room " + room.getName());
        broadcastMessage(room, messageObj);
    }


    /**
     * Need a "user exited room" hook and remove from all rooms on server
     */

    /**
     * Need a user sent message hook, update all room participants
     */
}
