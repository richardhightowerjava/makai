/**
 * This is a simple library for communicating with the Makai WebSocket
 */

/**
 * TODO: Error checking on websocket and re-connection
 */
MAKAI = window.MAKAI || {};

MAKAI.Server = (function() {
    var server = {};

    var messageId = 0;
    var makaiSocket = undefined;
    var callQueue = [];

    var defaultLogNode = "#messages";

    //Basic logger, div and console.
    function log(message, object) {
        if (object) {
            console.log(message, object);
        } else {
            console.log(message);
        }

        if ($(defaultLogNode).length) {
            $(defaultLogNode).append("<div style='border: solid ; overflow: auto'>");
            $(defaultLogNode).append(message).append(JSON.stringify(object));
            $(defaultLogNode).append("</div><br />");
        }
    }

    /**
     * The following are websocket connection support functions.
     *
     * This object can have configured callbacks via additional
     * parameter, including
     * {
     *      logNode: "#messages", //a div reference where you want log messages to spit out, in addition to console
     *      keepAlive: [true/false], //whether to try to keep the connection alive if a close is detected
     *      onMessage: function,
     *      onError: function,
     *      onOpen: function,
     *      onClose: function
     * }
     */
    server.registerSocket = function(wsUrl, options) {

        var registered = new $.Deferred();

        if (!options) options = {};
        if (options.logNode) defaultLogNode = options.logNode;

        var keepAlive = false;
        if (options.keepAlive) keepAlive = true;
        var onMessage = options.onMessage;
        var onError = options.onError;
        var onOpen = options.onOpen;
        var onClose = options.onClose;

        makaiSocket = new WebSocket(wsUrl);

        makaiSocket.onmessage = function(messageEvent) {
            log("socket.onmessage:", messageEvent);

            //Parse response
            var list = JSON.parse(messageEvent.data);
            var responseType = list[0];
            var incomingMessageId = list[3];
            var data = list[4];

            if (responseType == "reply") {

                //Check for listener
                var call = callQueue[incomingMessageId];

                if (call && messageEvent.returnValue) {
                    call.callback(data);
                    delete callQueue[incomingMessageId];
                } else {
                    throw Error("Unable to locate a call in the queue waiting for message id " + incomingMessageId
                        + " with response data \n" + data);
                }
                if (onMessage) {
                    onMessage(data);
                }

            } else if (responseType == "error") {
                var errorMsg = list[6];
                log("Problem Occurred!" + data + " - " + errorMsg);
            } else {
                //TODO: What else?
                log("response type not accounted for:" + data);
            }

        }

        makaiSocket.onerror = function(errorEvent) {
            log("socket.onerror:", errorEvent);
            if (onError) {
                onError(errorEvent);
            }
            registered.reject(server.webSocket);
        }

        makaiSocket.onopen = function() {
            log("socket.onopen: Opened successfully, we can assume");
            if (onOpen) {
                onOpen();
            }
            registered.resolve(server.webSocket);
        }
        makaiSocket.onclose = function(closeEvent) {
            log("socket.onclose:", closeEvent);
            if (keepAlive) {
                makaiSocket = new WebSocket(wsUrl);
            } else {
                if (onClose) {
                    onClose(closeEvent);
                }
            }
        }

        server.webSocket = makaiSocket;

        return registered;

    }

    server.sendServerMakaiMessage = function(serviceName, methodName, callback, args) {
        invokeMakai("send", serviceName, methodName, callback, args);

    }

    server.invokeMakaiMethod = function(serviceName, methodName, callback, args) {
        invokeMakai("query", serviceName, methodName, callback, args);
    }

    function invokeMakai(messageType, serviceName, methodName, callback, args) {


        /**
         * public void query(RampHeaders headers, String fromAddress, long qid,
         String address, String methodName, Object[] args)
         */

        var thisMessageId = messageId++;
        var list = [];
        list.push(messageType);         //query
        list.push({});              //rampHeaders
        list.push("me");            //fromAddress
        list.push(thisMessageId);   //qid
        list.push(serviceName);     //address
        list.push(methodName);      //methodName
        list.push(args);

//        if (args) {
//            for (var index=0; index < args.length; index++) {
//                var obj = args[index];
//                list.push(obj);
//            }
//        }

        log("Sending:", list);
        if (makaiSocket.readyState == WebSocket.OPEN) {
            makaiSocket.send(JSON.stringify(list));
            addCall(thisMessageId, callback);
        } else {
            log("Unable to communicate with web socket: " + server.webSocket.readyState);
        }
    }

    function addCall(id, callback) {
        var call = {
            id: id,
            callback: callback
        };

        callQueue[id]=call;
    }

    return server;
})();