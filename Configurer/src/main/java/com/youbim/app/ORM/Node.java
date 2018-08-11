package com.youbim.app.ORM;

import org.mongodb.morphia.annotations.Property;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Node {

    @Property("node_id")
    private String node_id;  

    @Property("external_id")
    private String external_id;

    @Property("sampling_interval")
    private double sampling_interval = 1000.0;

    @Property("node_type")
    private String node_type;

}