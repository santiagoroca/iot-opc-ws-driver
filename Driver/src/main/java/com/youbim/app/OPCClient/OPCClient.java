package com.youbim.app.OPCClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.youbim.app.ORM.Configurations;
import com.youbim.app.OPCClient.Nodes.Node;
import com.youbim.app.WebSocketServer.Client;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

public class OPCClient {

    /*
    *
    */
    private OpcUaClient client;

    /*
    *
    */
    private List<Node> nodes;

    /*
    *
    */
    private List<Client> wsClients;

    /*
    *
    */
    public OPCClient (Configurations configurations) {
        deploy(configurations);

        /*
        *
        */
        this.wsClients = new ArrayList<Client>();

    }

    /*
    *
    */
    public void deploy (Configurations configurations) {

        /*
        *
        */
        this.nodes = new ArrayList<Node>();

        /*
        * If the client is alread open, we close it
        * to avoid errors.
        */
        if (client != null) {
            client.disconnect();
        }

        try {

            EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(configurations.getServerURL()).get();
			EndpointDescription endpoint = Arrays.stream(endpoints)
			    .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));
                
            OpcUaClientConfig config = OpcUaClientConfig
                .builder()
                .setEndpoint(endpoint)
                .setApplicationUri(configurations.getApplicationURI())
                .setIdentityProvider(new AnonymousProvider())
                .build();
    
            client = new OpcUaClient(config);
            client.connect().get();

            // If there's nodes on the node List
            if (configurations.getNodes() != null) {

                for (com.youbim.app.ORM.Node node : configurations.getNodes()) {
                    nodes.add(new Node(client, node));
                }

            }

            //
            if (wsClients != null) {
                for (Client client : wsClients) {
                    subscribe(client);
                }
            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    public void subscribe (Client client) {

        //
        wsClients.add(client);

        /*
        //
        Stream<Node> stream = nodes.stream();

        //
        if (client.getFilters() != null) {
            for ( String key : client.getFilters().keySet() ) {
                stream.filter(node -> {
    
                    try {
                        return Node.class.getField(key).get(node) == client.getFilters().get(key);
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    
                    return false;
                });
            }
        }

        //
        List<Node> nodes = stream.collect(Collectors.toList());

        //
        for (Node node : nodes) {
            node.subscribe(client);
        }
        */

        for (Node node : nodes) {
            if (node.data.getExternalId().equals(client.getFilters().get("externalId"))) {
                node.subscribe(client);
            }
        }

    }

}