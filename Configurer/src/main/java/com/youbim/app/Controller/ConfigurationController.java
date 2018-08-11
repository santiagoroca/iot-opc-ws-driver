package com.youbim.app.Controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.mongodb.MongoClient;
import com.youbim.app.MessageBroker.Message;
import com.youbim.app.MessageBroker.Publisher;
import com.youbim.app.ORM.Configurations;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {

    /*
    *
    */
    final Morphia morphia = new Morphia();

    /*
    *
    */
    private Datastore datastore;

    /*
    *
    */
    public ConfigurationController () {

        //
        morphia.mapPackage("com.youbim.app.ORM.Configurations");

        //
        datastore = morphia.createDatastore(new MongoClient(), "opc-server");

        //
        datastore.ensureIndexes();

    }

    @RequestMapping("/configuration/{building_id}")
    public Configurations get(@PathVariable(value="building_id") int building_id) {
        return datastore.find(Configurations.class).filter("building_id", building_id).asList().get(0);
    }

    @RequestMapping(value="/configuration/{building_id}", method=RequestMethod.POST)
    public ResponseEntity<Configurations> post(
        @PathVariable(value="building_id") int building_id,
        @RequestBody Configurations configurations
    ) {

        // Delete previous Configuration entry
        datastore.delete(datastore.createQuery(Configurations.class).field("building_id").equal(building_id));

        // Create New one
        datastore.save(configurations);

        // Notify this configuration has changed
        try {
			Publisher.publish(new Message(0, building_id));
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}

        return ResponseEntity.ok(configurations);

    }

}