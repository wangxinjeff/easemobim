package com.hyphenate.easemob.imlibs.mp.events;

public class EventLocChanged {

    private double lat;
    private double lng;
    private float radius;
    private float direction;
    private String from;
    private String to;
    private int chatType;

    public EventLocChanged(double lat, double lng, float radius, float direction, String from, String to, int chatType) {
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.direction = direction;
        this.from = from;
        this.to = to;
        this.chatType = chatType;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }
}
