package org.ajmediananumduo.mjcamera.Camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Build;


public class MjCameraAPI21 {

    private CameraManager cameraManager;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MjCameraAPI21(Context context) {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    private void init() {

    }
}
