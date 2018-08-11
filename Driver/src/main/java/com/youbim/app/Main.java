package com.youbim.app;

import java.io.IOException;

import com.youbim.app.MessageBroker.Subscriber;
import com.youbim.app.OPCClient.OPCClientManager;
import com.youbim.app.WebSocketServer.Server;

public class Main {

    public static void main (String[] args) {
		
		//
		try {
			Subscriber.subscribe();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}

		//
		OPCClientManager.get();

		//
        try {
			new Server(8081).start();
        } catch (Exception e) {
            e.printStackTrace();
		}
			
	}

}