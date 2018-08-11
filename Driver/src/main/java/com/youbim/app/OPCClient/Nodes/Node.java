package com.youbim.app.OPCClient.Nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import javax.xml.crypto.Data;

import com.google.gson.Gson;
import com.youbim.app.OPCClient.OPCClient;
import com.youbim.app.WebSocketServer.Client;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;


public class Node {

    class Envelope {
        private com.youbim.app.ORM.Node node;
        private DataValue data_value;

        public Envelope (com.youbim.app.ORM.Node node, DataValue data_value) {
            this.node = node;
            this.data_value = data_value;
        }
    }

    /*
    *
    */
    private Gson gson = new Gson();

    /*
    *
    */
    public com.youbim.app.ORM.Node data;

    /*
    *
    */
    private ArrayList<Client> clients;

    /*
    *
    */
    public Node (OpcUaClient client, com.youbim.app.ORM.Node node) {

        /*
        *
        */
        data = node;

        /*
        *
        */
        clients = new ArrayList<Client>();

        /*
        *
        */
        try {

            // The actual consumer
            Consumer<DataValue> consumer = value -> {
                for (Client wsClient : clients) {  

                    try {
                        wsClient.send(getEnvelope(node, value));
                    } catch (WebsocketNotConnectedException exception) {
                        clients.remove(wsClient);
                    }
                    
                }
            };

            MonitoringParameters parameters = new MonitoringParameters(uint(123456789), node.getSamplingInterval(), null, uint(10), true);

            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
                new ReadValueId(NodeId.parse(node.getNodeID()), AttributeId.Value.uid(), null, null), 
                MonitoringMode.Reporting, 
                parameters
            );
            
            // setting the consumer after the subscription creation
            BiConsumer<UaMonitoredItem, Integer> onItemCreated = (monitoredItem, id) -> monitoredItem.setValueConsumer((a, b) -> {
                consumer.accept(b);                
            });

            // creating the subscription
            UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

            //
            subscription.createMonitoredItems(TimestampsToReturn.Both, Arrays.asList(request), onItemCreated).get();

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to subscribe to " + node.getNodeID());
        }

    }

    private String getEnvelope (com.youbim.app.ORM.Node node, DataValue data_value) {
        return gson.toJson(new Envelope(node, data_value));
    }

    public void subscribe (Client client) {
        clients.add(client);
    }

}