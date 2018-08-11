package com.youbim.app.ORM;

/*
* Create this calss node and use it with morphia
* On the other class Node, use this one
*/

import org.mongodb.morphia.annotations.Property;

public class Node {

    @Property("node_id")
    private String node_id;  

    @Property("external_id")
    private String external_id;

    @Property("sampling_interval")
    private double sampling_interval = 1000.0;

    @Property("node_type")
    private String node_type; 

    public String getNodeID () {
        return node_id;
    }

    public String getExternalId () {
        return external_id;
    }

    public double getSamplingInterval () {
        return sampling_interval;
    }

    public String getNodeType () {
        return node_type;
    }

}