package com.zomato.photofilters.utils;

import com.zomato.photofilters.imageprocessors.Filter;


public interface ThumbnailCallback {

    void onThumbnailClick(Filter filter);
}
