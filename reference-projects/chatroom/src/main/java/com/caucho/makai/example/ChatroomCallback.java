package com.caucho.makai.example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 * Date: 8/9/13
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ChatroomCallback {

    void sendMessage(String roomId, Message message);

}