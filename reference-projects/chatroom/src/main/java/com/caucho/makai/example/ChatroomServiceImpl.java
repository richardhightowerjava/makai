package com.caucho.makai.example;

import io.baratine.core.*;
import io.baratine.store.StoreService;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

@Startup
@Service
//@Journal /** Make this service Journaled. Requires Makai PRO **/
public class ChatroomServiceImpl implements ChatroomService {


    private static Logger logger = Logger.getLogger("ChatroomService");

    /** This is the persisted entity **/
    private Map<String, Chatroom> rooms;


    @Inject
    private StoreService<String, Chatroom> chatRoomStore;

    public ChatroomServiceImpl() {

        logger.info("CHATROOMSERVICE- instantiated");
    }

    /** CRUD STUFF **/

    @Override
    public Map<String, Chatroom> list() {
        logger.info("CHATROOMSERVICE-ChatroomService list");
        return rooms;
    }

    @Override
    public Chatroom create(Chatroom room) {
        logger.info("CHATROOMSERVICE-ChatroomService create");
        room.setId(UUID.randomUUID().toString());
        rooms.put(room.getId(), room);
        chatRoomStore.store(room.getId(), room);
        return room;
    }

    @Override
    public Chatroom read(String id) {
        logger.info("CHATROOMSERVICE-ChatroomService read");
        return rooms.get(id);
    }

    @Override
    public void update(Chatroom room) {
        logger.info("CHATROOMSERVICE-ChatroomService update");
        rooms.put(room.getId(), room);
        chatRoomStore.store(room.getId(), room);
    }

    @Override
    public Chatroom delete(String id) {
        chatRoomStore.remove(id);
        return rooms.remove(id);
    }

    /** Persistence Pieces **/
    @OnStart
    public void init() {
        logger.info("CHATROOMSERVICE-ChatroomService init (@OnStart) called");
        intializeAndLoadRooms();
    }

    private void intializeAndLoadRooms() {
        logger.info("CHATROOMSERVICE-Reloading all stores.");
        rooms = new HashMap<>();
        chatRoomStore.loadAll(new ChatroomOnLoad(), new ChatroomResult());
    }

    private void createFakeOneForDemoPurposes() {
        if (rooms.isEmpty()) {
            //one for fun.
            Chatroom room = new Chatroom();
            room.setName("Chat Room Testeroni");
            room.setWelcomeMessage("I'm so glad you're here...");
            room = create(room);
            rooms.put(room.getId(), room);
            chatRoomStore.store(room.getId(), room);
        }
    }

    /** TODO: Explain the need for this and the programmatic value **/

    class ChatroomOnLoad implements StoreService.OnLoad<String, Chatroom> {
        public void onLoad(String key, Chatroom value)
        {
            logger.info("CHATROOMSERVICE-Starting ChatRooms restore from Store");
            if (rooms == null) {
                rooms = new HashMap<>();
            }               //TODO: Find out why key is always null here....
            assert key == null;
            rooms.put(value.getId(), value);
        }
    }

    class ChatroomResult implements Result<Boolean> {
        @Override
        public void completed(Boolean aBoolean) {
            logger.info("CHATROOMSERVICE-ChatRoomResult - probably called from loadAll? result:" + aBoolean);
            createFakeOneForDemoPurposes();
        }
    }


    /**
     * Something occurred which left us in an indeterminate state.
     *
     * To restore us to the correct state, we first need to
     * load the data from the "database" @StoreService, then
     * need, possibly, to do something else before the journal
     * is replayed against the chatRoomStore, bringing us back to the prior
     * in-memory state.
     *
     */
    @BeforeReplay
    public void doSomethingUsefulBeforeReplay() {

        logger.info("CHATROOMSERVICE-ChatroomService @BeforeReplay");
        //Perhaps you have to perform some initialization or....

        //To be safe in this case we will reload from scratch
        intializeAndLoadRooms();
    }


    /** Weve got some free time and want to persist **/
    @OnCheckpoint
    public void syncToStore() {
        logger.info("CHATROOMSERVICE-ChatroomService Checkpoint called - syncing state to Store");
        for (String id : rooms.keySet()) {
            chatRoomStore.store(id, rooms.get(id));
        }
    }


}
