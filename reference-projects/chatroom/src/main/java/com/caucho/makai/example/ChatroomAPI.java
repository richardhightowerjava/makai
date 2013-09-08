package com.caucho.makai.example;

import io.makai.core.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    private List<User> activeUsers = new ArrayList<>();
    private List<ChatroomCallback> activeRooms = new ArrayList<>();

    @Inject
    ServiceManager _manager;

//    @Startup
    public void startup() {

//        _manager.service(new ChatroomServiceImpl());
//        ServiceRef svc = _manager.lookup("chatroomService");
//        svc.
    }

    /** CALLBACK WORK **/

    /** This says create a wrapper around an actor who knows how to invoke a client websocket **/
    public void registerActiveRoomWithUser(@Service ChatroomCallback room) {
        logger.info("CHATROOMAPI-User (callback) added.");         //TODO: How do we "service-ize" these on store reload?
        activeRooms.add(room);
    }

    public void sendMessage(Chatroom room, Message message) {
        logger.info("CHATROOMAPI-sendMessage called.");

        for (ChatroomCallback callback : activeRooms) {
            logger.info("CHATROOMAPI-sendMessage sent message:" + message.message + "for: " + message.user.getHandle());
            try {
                callback.sendMessage(room.getId(), message);
            } catch (Throwable t) {
                logger.log(Level.ALL, "CHATROOMAPI-Problem sending new message to callback:" + t.getMessage(), t);
            }

        }
//        for (User user : room.getUsers()) {
//            logger.info("CHATROOMAPI-sendMessage sent message:" + message.message + "for: " + message.user.getHandle());
//            try {
//                room.sendMessage(room.getId(), message);
//            } catch (Throwable t) {
//                logger.log(Level.ALL, "CHATROOMAPI-Problem sending new message to callback:" + t.getMessage(), t);
//            }
//
//        }
    }

    /** END CALLBACK WORK **/

    public Chatroom createChat(Chatroom chatroom) {
        logger.info("CHATROOMAPI-Creating chat room:" + chatroom.getName());
        return new Chatroom();//chatroomService.create(chatroom);
    }

    public Collection<Chatroom> list() {
        logger.info("CHATROOMAPI-ChatroomAPI - listing chats");
        return chatroomService.list().values();
    }

    public void join(User user, Chatroom room) {
        logger.info("CHATROOMAPI-User" + user.getHandle() + " joining room " + room.getName());

//        room.
    }

    public void send(Message message, Chatroom room) {
        logger.info("CHATROOMAPI-Sending message" + message.message + " to room " + room.getName());

    }


    /**
     * Need a "user exited room" hook and remove from all rooms on server
     */

    /**
     * Need a user sent message hook, update all room participants
     */
}
