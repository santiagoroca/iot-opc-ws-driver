package  com.youbim.app.MessageBroker;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.youbim.app.OPCClient.OPCClient;
import com.youbim.app.OPCClient.OPCClientManager;
import com.youbim.app.ORM.Configurations;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;;

public class Subscriber {

    public static synchronized void subscribe () throws java.io.IOException, java.lang.InterruptedException {

      try {

        //
        final Morphia morphia = new Morphia();

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.mapPackage("com.youbim.app.ORM");

        // create the Datastore connecting to the database running on the default port on the local host
        final Datastore datastore = morphia.createDatastore(new MongoClient(
          "54.202.244.55", 27017
        ), "opc-server");

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        datastore.ensureIndexes();

        //
        Gson gson = new Gson();

        //
        ConnectionFactory factory = new ConnectionFactory();

        //
        factory.setUsername("remote");
        factory.setPassword("remote");
        factory.setVirtualHost("/opc");
        factory.setHost("172.31.25.143");
        factory.setPort(5672);

        //
        Connection connection = factory.newConnection();
        
        //
        Channel channel = connection.createChannel();

        //
        channel.exchangeDeclare("opc_config_update_ack", "fanout");

        //
        String queueName = channel.queueDeclare().getQueue();

        //
        channel.queueBind(queueName, "opc_config_update_ack", "");

        //
        Consumer consumer = new DefaultConsumer(channel) {

          @Override
          public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            //
            Message message = gson.fromJson(new String(body, "UTF-8"), Message.class);

            // Fetch all configurations from the database
            final List<Configurations> configurations = datastore.find(Configurations.class)
                                      .field("building_id").equal(message.getBuildingId())
                                      .asList();

            // If there's any configuration for that building
            if (configurations.size() > 0) {

              // If the client already exists, update the connection.
              System.out.println(message.getBuildingId());
              if (OPCClientManager.get().exists(message.getBuildingId())) {
                OPCClientManager.get().get(message.getBuildingId()).deploy(configurations.get(0));
              }

              // If it doesn't, create the new connection to the server.
              else {
                System.out.println("Doesnt");
                OPCClientManager.get().add(message.getBuildingId(), new OPCClient(configurations.get(0)));
              }

            }

          }

        };

        channel.basicConsume(queueName, true, consumer);

      } catch (TimeoutException e) {
        e.printStackTrace();
      }

    }

}