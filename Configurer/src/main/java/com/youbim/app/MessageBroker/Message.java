package com.youbim.app.MessageBroker;

public class Message {

    private int code;

    private int building_id;

    public Message (int code, int building_id) {
        this.code = code;
        this.building_id = building_id;
    }

    public int getCode () {
        return code;
    }

    public int getBuildingId () {
        return building_id;
    }

}