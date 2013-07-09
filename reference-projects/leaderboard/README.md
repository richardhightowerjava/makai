# Leaderboard!

## Overview
Leaderboard is a reference implementation example for Caucho Makai. It is a simple leaderboard application modeled after
the meteor.js sample app.

For clarity, this application is intentionally built with an absolute minimum of external backend dependencies.

The front end uses a popular front end JS Framework called jQuery, purely for simplicity of dom management.

The other dependency is a library we've built to abstract the websocket details to increase the clarity
of the example.

This version requires a page refresh in browsers other than the one being used to edit. For real-time updating,
see our leaderboard-events project. This is in js/makai.js


## What Does It Do?
Maintains a shared leaderboard list on the client which represents data living in a Makai service on the server.

## Why Should I Care?
This represents a very surface level example of Makai. What you are seeing is a websockets meets Makai actor/message protocol and a
ring buffer managed service.  This means that the client version of these messages is a near-real-time representation. The
implications range wide for interactive applications of nearly infinite variety, massive scaling, and high volume transactional
systems.


## So What?
There's lots of things wrong with this code, we've intentionally left much to be desired architecturally. The point here
is to be able to show the simplest possible application with minimal dependencies, which we have done. Analyze
what's here, play with it, and then move on to other tutorials.

The next recommended step is the leaderboard-events app, followed by the TODO app.


## The Code

### Backend

@Service/@Export

The LeaderboardService is a very simple Java Pojo Service with a couple of Makai annotations that allow it to
be executed in the context of a ring buffer/actor model.

@Service says "treat me like a singleton bean, instance me on startup, and register me with the MakaiManager so
other things can look me up".  That parameter is a reference name for lookups, and potentially an export point
in the case of exposing the service 'on the wire'.

@Export says put me on the wire. This makes the service available via remoting protocols, websockets being the
one we care about in this case.

'''java
package com.caucho.makai.example;

import io.makai.core.Export;
import io.makai.core.Service;

import java.util.*;
import java.util.logging.Logger;

@Service("/leaderboardService")
@Export
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
'''

The BoardEntry is a simple pojo/model bean, probably not worth discussing.

There are a couple of things to note regarding making this service available under Resin.

The first is a beans.xml, a CDI enabling file to have present in your webapp. This tells the container
to look for CDI beans.

The second is a resin-web.xml, a resin webapp artifact which in this case idenifies that the Makai/Jamp servlet
ought to be registered in this webapp context and made available to front end callers

That's pretty much it.  4 very simple files.

### Frontend
There's even less to the front end. Most of the "work" is probably in the makai.js file, a simple library
providing the namespace MAKAI for simplifying access to the Makai services.

The most interesting part is the MAKAI library websocket initializer.
'''javascript
//Open a websocket connection to the Makai service
/**
 *   Register the websocket, which returns a deferred,
 *   and add an 'initializer methods the app requires for
 *   backend communication once the url is open.
 */
/**
 *  Aside from the wssUrlThis object can have configured callbacks via additional
 * parameter, including
 * {
 *      logNode: a div reference where you want log messages to spit out, in addition to console
 *      onMessage: function,
 *      onError: function,
 *      onOpen: function,
 *      onClose: function
 * }
 */
var options = { logNode: '#messages'};
MAKAI.Server
        .registerSocket(wssUrl, options)
        .done(function() {
            //Socket is open, get the initial list.
            //"list" is a method in the LeaderboardService.java which returns all active data.
            list();
        })
        .fail(function() {
            console.log("oops");
        });

'''

Basically we are passing the url we want the websocket to talk to.

'''javascript
   var wssUrl = "ws://localhost:8080/leaderboard/jamp";
'''
This is the url that is hosting
the jamp servlet. Once that websocket is in place, the rest is just talking to the services hosted
on it, in this case leaderboardService. For this app, as soon as the socket is available we want
to go ahead and list the contents in the leaderboard.

'''javascript
function list() {
    MAKAI.Server.invokeMakaiMethod("/leaderboardService", "list", updateScoreboard);
}
'''

That's pretty straightforward.  Invoke some backend makai method, in this case "list", and when you get
a response, invoke updateScoreboard with the data that came back. The updateScoreboard method is just
a jQuery .each handler that generates the div contents so I won't go into detail on that here.

Feel free to dive into the guts of makai.js if you want to learn the dirtier details of directly interfacing
with the websocket to talk to the servlet. This isn't necessary to be productive but the true geeks will
want the deep dive.



