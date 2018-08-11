package com.youbim.app.ORM;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@Entity("configurations")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Configurations {

    @Id
    private String _id;

    @Property("class_name")
    private String class_name;

    @Property("building_id")
    private int building_id;

    @Property("server_url")
    private String server_url;

    @Embedded("nodes")
    private Node [] nodes; 

    public int getBuildingId () {
        return building_id;
    }

    public String getServerURL () {
        return server_url;
    }

    public Node [] getNodes () {
        return nodes;
    }

}