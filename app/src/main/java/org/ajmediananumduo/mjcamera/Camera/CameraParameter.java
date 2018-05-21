package org.ajmediananumduo.mjcamera.Camera;

import java.io.Serializable;
import java.util.List;


public class CameraParameter implements Serializable {
    private List<Size> supportedPreviewSizes;
    private List<Size> supportedPictureSizes;

    public void setSupportedPreviewSizes(List<Size> supportedPreviewSizes) {
        this.supportedPreviewSizes = supportedPreviewSizes;
    }

    public void setSupportedPictureSizes(List<Size> supportedPictureSizes) {
        this.supportedPictureSizes = supportedPictureSizes;
    }

    public List<Size> getSupportedPreviewSizes() {
        return supportedPreviewSizes;
    }

    public List<Size> getSupportedPictureSizes() {
        return supportedPictureSizes;
    }

}
