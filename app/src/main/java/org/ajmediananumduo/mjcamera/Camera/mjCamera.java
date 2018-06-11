package org.ajmediananumduo.mjcamera.Camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class mjCamera implements View.OnTouchListener, Camera.AutoFocusCallback {

    private final String TAG = mjCamera.class.getSimpleName();
    private int cameraRotation=0;
    private Context context;
    private Camera camera;
    private int previewheight =0;
    private int previewwidth =0;
    private int direction;
    private Camera.ShutterCallback shutterCallback;

    public mjCamera(Context context,int direction) {
        this.context = context;
        this.direction = direction;
        init(direction);
    }

    private void init(int direction) {
            camera = Camera.open(direction);
        }

    public void start(SurfaceView surfaceView) {
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.startPreview();
            surfaceView.setOnTouchListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        camera.stopPreview();
        camera.release();
    }

    public void setShutterCallback(Camera.ShutterCallback shutterCallback) {
        this.shutterCallback = shutterCallback;
    }

    public CameraParameter getCameraParameters() {

        Camera.Parameters parameters = camera.getParameters();

        List<Size> sizeList = new ArrayList<>();
        CameraParameter cameraParameter = new CameraParameter();

        for(Camera.Size size : parameters.getSupportedPreviewSizes()) {
            sizeList.add(new Size(size.width, size.height));
        }

        cameraParameter.setSupportedPreviewSizes(sizeList);
        sizeList = new ArrayList<>();

        for(Camera.Size size : parameters.getSupportedPictureSizes()) {
            sizeList.add(new Size(size.width, size.height));
        }

        cameraParameter.setSupportedPictureSizes(sizeList);

        return cameraParameter;
    }

    public void setPictureSize(Size pass) {
        Camera.Parameters parameters = camera.getParameters();
        int height=0;
        int width=0;
        List<Size> sizeList = new ArrayList<>();

        for(Camera.Size size : parameters.getSupportedPictureSizes()) {
            sizeList.add(new Size(size.width, size.height));
        }
        for(int i =0;i<sizeList.size();i++) {
            if(sizeList.get(i).width==2880&&sizeList.get(i).height==2160){
                height = sizeList.get(i).height;
                width = sizeList.get(i).width;
                break;
            }
        }
        if(width==0){
            width = sizeList.get(0).width;
            height = sizeList.get(0).height;
        }
        parameters.setPictureSize(width, height);
        camera.setParameters(parameters);
    }

    public void setPreviewSize(Size pass){
        Camera.Parameters parameters = camera.getParameters();

        List<Size> sizeList = new ArrayList<>();

        for(Camera.Size size : parameters.getSupportedPreviewSizes()) {
            sizeList.add(new Size(size.width, size.height));
        }
        for(int i = 0;i<sizeList.size();i++) {
            if(sizeList.get(i).width==1440&&sizeList.get(i).height==1080){
                previewheight = sizeList.get(i).height;
                previewwidth = sizeList.get(i).width;
                break;
            }
        }
        if(previewwidth ==0){
            Log.d(TAG,"1440:1080 Failed");

            previewwidth = sizeList.get(0).width;
            previewheight = sizeList.get(0).height;
        }
        parameters.setPreviewSize(previewwidth, previewheight);
        camera.setParameters(parameters);
    }
    public double getRatio(){
        return (previewwidth /(double) previewheight);
    }

    public void takePicture(int rotation) {
        camera.autoFocus(this);
        cameraRotation=rotation;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                camera.autoFocus(null);
                break;
        }

        return true;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        Log.d(TAG, "onAutoFocus");
        camera.takePicture(shutterCallback, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    String desc_filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
                    FileOutputStream outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/Download/"+
                    desc_filename));
                    /*
                    int exifOrientation;
                    int exifDegree;
                    exif
                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegrees(exifOrientation);
                    } else {
                        exifDegree = 0;
                    }
                    */
                    bitmap=rotate(bitmap,cameraRotation);
                    Log.i("Test" , "orientation : " +cameraRotation);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory()+"/Download/"+desc_filename)));

                    outputStream.close();


                    camera.startPreview();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        if(degree==270&&direction==0){
            degree=90;
        }
        else if(degree==90&&direction==0){
            degree=270;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


}
