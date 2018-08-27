package com.youbim.app.WebSocketServer;

import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.youbim.app.OPCClient.OPCClientManager;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Server extends WebSocketServer {

	/*
    *
    */
    private Gson gson = new Gson();

	/*
	*
	*/
    public Server( int port ) throws Exception {
		super( new InetSocketAddress( port ) );														  
	}

	@Override
	public void onOpen(WebSocket connection, ClientHandshake handshake) {

	}

	@Override
	public void onClose(WebSocket connection, int code, String reason, boolean remote) {
		System.out.println("Closed Connection");
	}

	@Override
	public void onMessage(WebSocket connection, String message) {

		/*
		*
		*/
		SubscriptionMessage subscriptionMessage = gson.fromJson(message, SubscriptionMessage.class);

		/*
		*
		*/
		OPCClientManager.get().get(subscriptionMessage.getBuildingId()).subscribe(
			new Client(connection, subscriptionMessage.getExternalId())
		);

	}

	@Override
	public void onError(WebSocket connection, Exception exception) {
		exception.printStackTrace();
	}

	@Override
	public void onStart() {
		
	}

}