/**
 * This is a simple library for communicating with the Makai WebSocket
 */

/**
 * TODO: Error checking on websocket and re-connection
 */
MAKAI = window.MAKAI || {};

MAKAI.Server = (function() {
    var server = {};
    var client = {};

    client.messageId = 0;
    client.makaiSocket = undefined;
    client.serverInvocationFromServerAddress = undefined;

    //This is expected to have a signature of function(serverMethodNameThatGeneratedData, generatedData);
    client.serverMessagesCallbackFunction = undefined;

    var callQueue = [];

    var defaultLogNode = "#messages";

    //Basic logger, div and console.
    function log(message, object) {
        if ($(defaultLogNode) && $(defaultLogNode).length) {
            $(defaultLogNode).append("<table id='makaiLogging' class='table-striped'>");
//            $(defaultLogNode)
            $('#makaiLogging').append("<tr><td>message</td><td style='width: 2px; background-color:black'></td><td>object</td></tr>");
            $(defaultLogNode).append("</table>");
        }
        if (object) {
            console.log(message, object);
        } else {
            console.log(message);
        }

        if ($('#makaiLogging') && $('#makaiLogging').length) {
            $('#makaiLogging tr:first').after('<tr><td>' + message + '</td><td width="2px" style="background-color:black"></td><td>' + JSON.stringify(object) + '</td></tr>');
        }
    }


    /** server.XXX are the API methods here **/

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
    server.registerSocket = function(wsUrl, options, serverMessages) {

        var registered = new $.Deferred();

        if (!options) options = {};
        if (options.logNode) defaultLogNode = options.logNode;

        var keepAlive = false;
        if (options.keepAlive) keepAlive = true;
        var onMessage = options.onMessage;
        var onError = options.onError;
        var onOpen = options.onOpen;
        var onClose = options.onClose;

        client.serverMessagesCallbackFunction = serverMessages;
        client.makaiSocket = new WebSocket(wsUrl);

        client.makaiSocket.onmessage = function(messageEvent) {
            log("socket.onmessage:", messageEvent);

            //Parse response
            var list = JSON.parse(messageEvent.data);
            var responseType = list[0];
            var incomingMessageId = list[3];
            var data = list.slice(4);

            if (responseType == "reply") {

                //Check for listener
                var call = callQueue[incomingMessageId];

                //Firefox the .returnValue is not presented/is undefined
                //This is a hack workaround....to figure out later
                if (navigator.userAgent.indexOf("Firefox") > -1) {
                    if (messageEvent.returnValue === undefined) {
                        messageEvent.returnValue = true;
                    }
                }


                if (call && messageEvent.returnValue) {
                    if (call.callback) call.callback(data);
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
            } else if (responseType == "send") {
                if (client.serverMessagesCallbackFunction) {
                    client.serverMessagesCallbackFunction(incomingMessageId, data);
                }
            } else {
                //TODO: What else?
                log("response type not accounted for:" + data);
            }

        }

        client.makaiSocket.onerror = function(errorEvent) {
            log("socket.onerror:", errorEvent);
            if (onError) {
                onError(errorEvent);
            }
            registered.reject(server.webSocket);
            if (getMeDeferred) {
                getMeDeferred.reject();
            }
        }

        client.makaiSocket.onopen = function() {
            log("socket.onopen: Opened successfully, we can assume");

            //get a handle to local address from server perspective.
            getMe()
                .done(function() {
                    if (onOpen) {
                        onOpen();
                    }
                    registered.resolve(server.webSocket);
                }).fail(function() {
                    //then what?
                });

        }
        client.makaiSocket.onclose = function(closeEvent) {
            log("socket.onclose:", closeEvent);
            if (keepAlive) {
                client.makaiSocket = new WebSocket(wsUrl);
            } else {
                if (onClose) {
                    onClose(closeEvent);
                }
            }
            closeEvent.stopPropagation();
        }

        server.webSocket = client.makaiSocket;

        return registered;

    }

    server.registerClientListener = function(serviceAddress, methodToCall, args) {
        //Request notification (register for) certain methods
        var config = {
            messageType: "send",
            serviceName: serviceAddress,
            methodName: methodToCall,
            clientAddress: client.serverInvocationFromServerAddress,
            arguments: args
        }
        invokeMakaiSend(config);
    }

    server.sendServerMakaiMessage = function(serviceAddress, methodToCall, args) {
        var config = {
            messageType: "send",
            serviceName: serviceAddress,
            methodName: methodToCall,
            arguments: args
        }
        invokeMakaiSend(config);

    }

    server.invokeMakaiMethod = function(serviceAddress, methodToCall, callback, args) {
        var config = {
            messageType: "query",
            serviceName: serviceAddress,
            methodName: methodToCall,
            callback: callback,
            arguments: args
        }
        invokeMakaiQuery(config);
    }

    /** Private Methods **/
    function invokeMakaiQuery(config) {


        /**
         * public void query(RampHeaders headers, String fromAddress, long qid,
         String address, String methodName, Object[] args)
         */

        var thisMessageId = client.messageId++;
        var list = [];
        list.push(config.messageType);         //query or send
        list.push({});              //rampHeaders
        list.push("me");            //fromAddress
        list.push(thisMessageId);   //qid
        list.push(config.serviceName);     //address
        list.push(config.methodName);      //methodName
        list.push(config.arguments);

        log("Sending[" + thisMessageId + "]:", list);
        if (client.makaiSocket.readyState == WebSocket.OPEN) {
            client.makaiSocket.send(JSON.stringify(list));
            addCall(thisMessageId, config.callback);
        } else {
            log("Unable to communicate with web socket: " + server.webSocket.readyState);
        }
    }

    function invokeMakaiSend(config) {

        var thisMessageId = client.messageId++;
        var list = [];
        list.push(config.messageType);         //query or send
        list.push({});              //rampHeaders
        list.push(config.serviceName);     //address
        list.push(config.methodName);      //methodName

        if (config.clientAddress) {
            list.push(config.clientAddress);//typically a 'callback registration' scenario
        }

        if (config.arguments) {
            if($.isArray(config.arguments)) {
                for (var index=0; index < config.arguments.length; index++) {
                    var obj = config.arguments[index];
                    list.push(obj);
                }
            } else {
                list.push(config.arguments);
            }
        }

        log("Sending[" + thisMessageId + "]:", list);
        if (client.makaiSocket.readyState == WebSocket.OPEN) {
            client.makaiSocket.send(JSON.stringify(list));
            //TODO: I don't think 'send' returns anything...addCall(thisMessageId, config.callback);
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
        client.serverInvocationFromServerAddress = data[0];
        log("Client side address" + client.serverInvocationFromServerAddress);
        getMeDeferred.resolve();
    }

    return server;
})();