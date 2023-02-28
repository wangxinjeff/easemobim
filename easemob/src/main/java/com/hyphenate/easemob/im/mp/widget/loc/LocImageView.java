package com.hyphenate.easemob.im.mp.widget.loc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class LocImageView extends ImageView {

    private String username;
    private double lat;
    private double lng;
    private float radius;
    private float direction;

    public LocImageView(Context context) {
        super(context);
    }

    public LocImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
