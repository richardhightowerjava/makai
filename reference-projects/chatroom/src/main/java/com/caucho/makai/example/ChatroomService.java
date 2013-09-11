package com.caucho.makai.example;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 * Date: 9/2/13
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChatroomService {

    Map<String, Chatroom> list();

    Chatroom create(Chatroom room);

    Chatroom read(String id);

    void update(Chatroom room);

    Chatroom delete(String id);
}
