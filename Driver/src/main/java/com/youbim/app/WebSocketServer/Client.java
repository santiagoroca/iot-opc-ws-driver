package com.youbim.app.WebSocketServer;

import java.util.HashMap;

import org.java_websocket.WebSocket;

public class Client {

    private WebSocket connection;

    private HashMap<String, String> filters;

    private String externalId;

    public Client (WebSocket connection, String externalId) {
        this.connection = connection;
        this.filters = new HashMap<>();

        // TODO Refactor here
        filters.put("externalId", externalId);
    }

    public void onMessage (String message) {
        System.out.println(message);
    }

    public void send (String message) {
        connection.send(message);
    }

    public HashMap<String, String> getFilters () {
        return filters;
    }

}