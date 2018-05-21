package org.ajmediananumduo.mjcamera.Camera;

import java.io.Serializable;


public class Size implements Serializable {
    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
