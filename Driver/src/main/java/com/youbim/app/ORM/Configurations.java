package com.youbim.app.ORM;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity("configurations")
public class Configurations {

    @Id
    private String _id;

    @Property("class_name")
    private String class_name;

    @Property("building_id")
    private int building_id;

    @Property("server_url")
    private String server_url;

    @Property("application_uri")
    private String application_uri;

    @Embedded("nodes")
    private List<Node> nodes; 

    public int getBuildingId () {
        return building_id;
    }

    public String getServerURL () {
        return server_url;
    }

    public List<Node> getNodes () {
        return nodes;
    }

    public String getApplicationURI () {
        return application_uri;
    }

}