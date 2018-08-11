package com.youbim.app.OPCClient;

import java.util.HashMap;
import java.util.List;

import com.mongodb.MongoClient;
import com.youbim.app.ORM.Configurations;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class OPCClientManager {

    /*
    *
    */
    private static OPCClientManager singleton;


    /*
    *
    */
    public static synchronized OPCClientManager get () {
        if (singleton == null)  {
            singleton = new OPCClientManager();
        }

        return singleton;
    }

    /*
	*
	*/
	private HashMap<Integer, OPCClient> clients;

    /*
    *
    */
    public OPCClientManager () {

        //
        clients = new HashMap<Integer, OPCClient>();

        //
		final Morphia morphia = new Morphia();

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.mapPackage("com.youbim.app.ORM");

        // create the Datastore connecting to the database running on the default port on the local host
        final Datastore datastore = morphia.createDatastore(new MongoClient(), "opc-server");

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		datastore.ensureIndexes();

		// Fetch all configurations from the database
		final List<Configurations> clients_configurations = datastore.find(Configurations.class)
													  .asList();

		// Create a client for every configuration
		for (Configurations configurations : clients_configurations) {
			clients.put(configurations.getBuildingId(), new OPCClient(configurations));
        }
        
    }

    /*
    *
    */
    public OPCClient get (int building_id) {
        return clients.get(building_id);
    }

    /*
    *
    */
    public void add (int building_id, OPCClient client) {
        clients.put(building_id, client);
    }

    /*
    *
    */
    public Boolean exists (int building_id) {
        return clients.containsKey(building_id);
    }

}