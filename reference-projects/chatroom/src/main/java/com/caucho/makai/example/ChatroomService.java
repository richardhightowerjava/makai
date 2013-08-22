package com.caucho.makai.example;

import io.makai.core.*;
import io.makai.store.MakaiStore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Journal /** Make this service Journaled **/
public class ChatroomService {


    private static Logger logger = Logger.getLogger("ChatroomService");

    /** This is the persisted entity **/
    private Map<String, ChatRoom> rooms;

    @Inject
    private MakaiStore<String, ChatRoom> store;

    public ChatroomService() {
        logger.info("ChatroomService instantiated");
    }

    /** CRUD STUFF **/

    public Map<String, ChatRoom> list() {
        logger.info("ChatroomService list");
        return rooms;
    }

    public ChatRoom create(ChatRoom room) {
        logger.info("ChatroomService create");
        room.setId(UUID.randomUUID().toString());
        rooms.put(room.getId(), room);
        store.store(room.getId(), room);
        return room;
    }

    public ChatRoom read(String id) {
        logger.info("ChatroomService read");
        return rooms.get(id);
    }

    public void update(ChatRoom room) {
        logger.info("ChatroomService update");
        rooms.put(room.getId(), room);
        store.store(room.getId(), room);
    }

    public ChatRoom delete(String id) {
        store.remove(id);
        return rooms.remove(id);
    }

    /** Persistence Pieces **/
    @OnStart
    public void init() {

        logger.info("ChatroomService init (@OnStart) called");
        rooms = new HashMap<>();


        store.loadAll(new ChatRoomOnLoad(), new ChatRoomResult(), new ChatRoomFailure());

        if (rooms.isEmpty()) {
            //one for fun.
            ChatRoom room = new ChatRoom();
            room.setName("Chat Room Testeroni");
            room.setWelcomeMessage("I'm so glad you're here...");
            room = create(room);
            rooms.put(room.getId(), room);
        }
    }

    /** TODO: Explain the need for this and the programmatic value **/

    class ChatRoomOnLoad implements MakaiStore.OnLoad<String, ChatRoom> {
        public void onLoad(String key, ChatRoom value)
        {
            logger.info("Starting ChatRooms restore from Store");
            if (rooms == null) {
                rooms = new HashMap<>();
            }
            rooms.put(key, value);
        }
    }

    class ChatRoomResult implements MakaiResult<Boolean> {
        public void completed(Boolean v)
        {
            logger.info("ChatRooms restored from Store");
        }
    }

    class ChatRoomFailure implements Failure {
        public void failed(Throwable exn)
        {
            logger.log(Level.SEVERE, "Unable to restore rooms from Store", exn);
        }
    }

    /** Store was restored, and the journal was replayed against it, now **/
    @AfterReplay
    public void doSomethingUsefulAFterReplay() {
        logger.info("ChatroomService @AfterReplay");
        //Perhaps you have to calculate some values or make some connections or....
    }


    /** Weve got some free time and want to persist **/
    @OnCheckpoint
    public void syncToStore() {
        logger.info("ChatroomService Checkpoint called - syncing state to Store");
        for (String id : rooms.keySet()) {
            store.store(id, rooms.get(id));
        }
    }


}
