package org.ajmediananumduo.mjcamera.Community.ui.activity;

import android.graphics.Bitmap;

public class FeedItem {
    public Bitmap bitmap;
    public int likesCount;
    public boolean isLiked;
    public FeedItem(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.isLiked = false;
        this.likesCount=0;

    }

    public FeedItem(int likesCount, boolean isLiked) {
        this.likesCount = likesCount;
        this.isLiked = isLiked;
    }
}
