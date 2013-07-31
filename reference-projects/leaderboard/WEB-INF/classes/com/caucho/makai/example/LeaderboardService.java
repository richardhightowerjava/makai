package com.caucho.makai.example;

import io.makai.core.Remote;
import io.makai.core.Service;

import java.util.*;
import java.util.logging.Logger;

@Service("/leaderboardService")
@Remote
public class LeaderboardService {

    private Logger logger = Logger.getLogger("LeaderboardService");

    Set<BoardEntry> boardEntries = new HashSet<>();

    {
        boardEntries.add(new BoardEntry("Chris", 15));
        boardEntries.add(new BoardEntry("Nam", 35));
        boardEntries.add(new BoardEntry("Rick", 75));
        boardEntries.add(new BoardEntry("Scott", 90));
    }

    public List<BoardEntry> addBoardEntry(BoardEntry boardEntry) {
        logger.info("addBoardEntry called with:" + boardEntry.toString());
        boardEntries.remove(boardEntry);
        boardEntries.add(boardEntry);
        return list();
    }

    public List<BoardEntry> removeBoardEntry(BoardEntry boardEntry) {
        logger.info("removeBoardEntry called with:" + boardEntry.toString());
        boardEntries.remove(boardEntry);
        return list();
    }

    public List<BoardEntry> list() {
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
