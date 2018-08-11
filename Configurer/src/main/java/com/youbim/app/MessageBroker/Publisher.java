package com.youbim.app.MessageBroker;

import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Publisher {

    public static void publish (Message message) throws java.io.IOException, TimeoutException {
        
        //
        Gson gson = new Gson();

        //
        ConnectionFactory factory = new ConnectionFactory();

        //
        factory.setHost("localhost");

        //
        Connection connection = factory.newConnection();

        //
        Channel channel = connection.createChannel();

        //
        channel.exchangeDeclare("opc_config_update_ack", "fanout");

        //
        channel.basicPublish("opc_config_update_ack", "", null, gson.toJson(message).getBytes());

        //
        channel.close();

        //
        connection.close();

    }

}