package org.ajmediananumduo.mjcamera.Community.ui.activity;

import java.util.HashMap;
import java.util.Map;

public class ImageDTO {

    public String description;
    public String imageUrl;
    public String title;  //finter name
    public String userId;
    public int likesCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
}
