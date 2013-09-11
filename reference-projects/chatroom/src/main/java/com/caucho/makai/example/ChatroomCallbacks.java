package com.caucho.makai.example;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 */
public interface ChatroomCallbacks {

    void sendMessage(String roomId, Message message);
    void sendRoomList(Collection<Chatroom> rooms);

}