# Leaderboard With Events

## Overview
Leaderboard with events is a follow on project from the Leaderboard! project. It is all the same code
 plus some extra features to leverage the client registration and callback mechanism. Please see the [Leaderboard!
 README.md](https://github.com/revelfire/makai/tree/master/reference-projects/leaderboard) for full code details,
 project setup...etc.  [https://github.com/revelfire/makai/blob/master/docs/videos/ProjectX-Leaderboard.swf?raw=true](See it in action.)

 This document will only describe the differences between this project and Leaderboard!

## Callbacks
For websocket messaging based on server events (in this case triggered by other clients) the client must first register
itself with the server, and store it's address for the server locally.  The makai.js library abstracts some of this. From
the page author perspective it really just looks like this:

```javascript
MAKAI.Server.registerSocket(wssUrl, options, serverMessages);
```
In the above code, what's new here is the passing "serverMessages", which is telling the library to go ahead and
setup a function to call whenever the server calls out
to the client in response to a non-client-generated event.  This all happens in the setup of the websocket.
The details in the library for managing this look like:

```javascript
//get a handle to local address from server perspective.
getMe();

/**
 * This is to get my "reply address" to be used for callback registrations so the server
 * has knowledge of an address to invoke. This allows to register for "events".
 *
 * This is a convenience to simplify adding callbacks on the client side.
 */
var getMeDeferred;
function getMe() {
    //Need to get the address from the server
    //Then call publishChannel with the channel being channel: + me address
    getMeDeferred = new $.Deferred();
    MAKAI.Server.invokeMakaiMethod("channel:", "publishChannel", registerReturned, "/reply-address");
    return getMeDeferred;
}

function registerReturned(data) {
    //Retreive and store my local address from server perspective.
    client.serverInvocationFromServerAddress = data;
    log("Client side address" + client.serverInvocationFromServerAddress);
    getMeDeferred.resolve();
}
```

There's a little more plumbing to this but this is the general idea. That "/reply-address" and "publishChannel" are
 Makai wire protocol messages that ask the server to tell us "who we are" from its perspective.  Once the socket is
 opened successfully, ask the server for my address so I can register for messages.

Once we have a method registered to take in server-triggered messages (serverMessages()) we then need to tell the
server what messages we care about by registering with some callback mechanism.  The Javascript code looks like this:

```javascript

/** Method to have server messages pushed into **/
function serverMessages(method, data) {
    console.log("server side method " + method + " returned ", data);
    if (method == "messageFromServer") {
        updateScoreboard(data);
    }
}

/** Setup of makai library/websocket to register for specific server method invocations **/
MAKAI.Server.registerClientListener("/leaderboardServiceEvents", "addCallback");
/** This is typically done immediately after the registerWebSocket initialization call, in the .done block
of the deferred return.**/
```

So that's the plumbing to say "server send messages to this function when server method X is executed",
then "Parse and use server messages".

In the Java code we have some changes to support this.  The leaderboard service much implement the addCallback
method, and that other code in the service must use that callback in response to some invocation.

Here's the callback class:

```java
public interface Callback {

    void messageFromServer(List<BoardEntry> entries);

}
```
Not too much to it - it's really just a data wrapper for knowing on the client what type of message was coming
back, and a data holder (List<BoardEntry entries) for the deserialization to a JSON/javascript response.  If you noticed
above in the serverMessages, we are looking explicitly for 'messageFromServer' - since the server response will
tell the client that this is the "method invoked" and the parameters to the method are the data.

So now that we have a way to plumb responses to the client we need a way to invoke it.

Here we have the method that gets triggered by the callback registration:

```java
public class LeaderboardService {

    /** CALLBACK WORK **/

    private List<Callback> callbacks = new ArrayList<>();

    public void addCallback(@Service Callback cb) {
        logger.info("Callback added.");
        callbacks.add(cb);
    }

}
```

Basically hold a list of registered clients to 'call back'.

Here we have a way to message out to those clients based on some action:

```java
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
```

...and here's the final connection - if you followed along with the leaderboard example you saw that the
 add and remove methods just returned a rerun of the list() method.  That worked fine but now we need to change
 that behavior slightly, and message all the client listeners about the update.

```java
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
```

So the main difference is that after add and remove, we don't just call list(), we wrap it in fireMessage(list()) which
says "ok rerun the list and then TELL EVERY CLIENT YOU KNOW ABOUT WITH AN OPEN SOCKET that the list has been updated and
they should re-render their scoreboard against the latest model"!!!

Here's the whole class
```java

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
```

So that's the basics of it. We can now see server side data changes triggered by a single client on any
other registered clients pretty much in realtime.

So cool.


