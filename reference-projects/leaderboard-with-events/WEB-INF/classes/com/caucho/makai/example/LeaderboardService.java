package com.caucho.makai.example;

import io.makai.core.Remote;
import io.makai.core.Service;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("/leaderboardServiceEvents")
@Remote
public class LeaderboardService {

    private Logger logger = Logger.getLogger("LeaderboardService for events");

    public LeaderboardService() {
        logger.info("LeaderboardService for events instantiated");
    }



    /** CALLBACK WORK **/

    private List<Callback> callbacks = new ArrayList<>();

    public void addCallback(@Service Callback cb) {
        logger.info("Callback added.");
        callbacks.add(cb);
    }

    public void fireMessage(List<BoardEntry> entries) {
        logger.info("fireMessage called. Callback count: " + callbacks.size());

        for (Callback cb : callbacks) {
            logger.info("fireMessage sent callback");
            try {
                cb.messageFromServer(entries);
            } catch (Throwable t) {
                logger.log(Level.ALL, "Problem calling callback:" + t.getMessage(), t);
            }

        }
    }

    /** END CALLBACK WORK **/


    Set<BoardEntry> boardEntries = new HashSet<>();

    {
        boardEntries.add(new BoardEntry("Chris", 15));
        boardEntries.add(new BoardEntry("Nam", 35));
        boardEntries.add(new BoardEntry("Rick", 75));
        boardEntries.add(new BoardEntry("Scott", 90));
    }

    /** Entry CRUD with notification by callback
     *  The registered callback-able websocket connections
     *  will get a list() update with every board entry change
     * **/
    public void addBoardEntry(BoardEntry boardEntry) {
        logger.info("addBoardEntry called with:" + boardEntry.toString());
        boardEntries.remove(boardEntry);
        boardEntries.add(boardEntry);
        fireMessage(list());
    }

    public void removeBoardEntry(BoardEntry boardEntry) {
        logger.info("removeBoardEntry called with:" + boardEntry.toString());
        boardEntries.remove(boardEntry);
        fireMessage(list());
    }

    public List<BoardEntry> list() {
        logger.info("list (BoardEntry) called");
        List<BoardEntry> sortedEntries = new ArrayList<>();
        sortedEntries.addAll(boardEntries);
        Collections.sort(sortedEntries, new Comparator<BoardEntry>() {
            @Override
            public int compare(BoardEntry e1, BoardEntry e2) {
                return e1.getScore() < e2.getScore() ? 1 : -1;
            }
        });
        return sortedEntries;
    }

}
