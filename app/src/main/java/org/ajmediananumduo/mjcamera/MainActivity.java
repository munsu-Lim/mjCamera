package org.ajmediananumduo.mjcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.ajmediananumduo.mjcamera.Camera.PropertyActivity;
import org.ajmediananumduo.mjcamera.Camera.Size;
import org.ajmediananumduo.mjcamera.Camera.mjCamera;
import org.ajmediananumduo.mjcamera.Filter.filterActivity;
import org.ajmediananumduo.mjcamera.databinding.ActivityMainBinding;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.ShutterCallback{
    ImageView changeButton;
    private final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mainBinding;
    private mjCamera mjCamera;
    private SurfaceHolder surfaceHolder;
    private TextView textView;
    boolean[] state;
    private int rotation;
    private boolean isBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        state = new boolean[4];
        for(int i= 0;i<state.length;i++) {
            state[0] = false;
        }
        rotation=270;
        super.onCreate(savedInstanceState);
        isBack=true;
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        textView= mainBinding.textView1;
        mainBinding.galleryButton.setRotation(270);
        setTheme(android.R.style.Theme_Holo_NoActionBar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        else {
            init();
        }

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setCamera();
        }
        else {
            setCameraAPI21();
        }*/
        mainBinding.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent filterintent=new Intent(getApplicationContext(),filterActivity.class);
                    startActivity(filterintent);
            }
        });
        mainBinding.communityButto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), org.ajmediananumduo.mjcamera.Community.ui.activity.MainActivity.class));
                finish();
            }
        });
        mainBinding.textViewProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PropertyActivity.class).putExtra("cameraParameters", mjCamera.getCameraParameters()));
            }
        });
        mainBinding.changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBack) {
                    mjCamera.stop();
                    setCamera(1);
                    isBack=false;
                }
                else {
                    mjCamera.stop();
                    setCamera(0);
                    isBack=true;
                }
            }
        });
        OrientationEventListener oel = new OrientationEventListener(getApplicationContext()) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 80&&orientation<=100&&state[0]==false) {
                   // Toast.makeText(getApplicationContext(), "LandScape2",         Toast.LENGTH_SHORT).show();
                    rotation=180;
                    settingrotation(rotation);
                    state[0]=true;
                    state[1]=false;
                    state[2]=false;
                    state[3]=false;
                } else if ((orientation <= 10||orientation>=350)&&state[1]==false) {
                   // Toast.makeText(getApplicationContext(), "Portrait1",         Toast.LENGTH_SHORT).show();
                    rotation=270;
                    settingrotation(rotation);
                    state[0]=false;
                    state[1]=true;
                    state[2]=false;
                    state[3]=false;
                } else if((orientation >= 260&&orientation<=280&&state[2]==false)){
                    //Toast.makeText(getApplicationContext(), "LandScape1",         Toast.LENGTH_SHORT).show();
                    rotation=0;
                    settingrotation(rotation);
                    state[0]=false;
                    state[1]=false;
                    state[2]=true;
                    state[3]=false;
                } else if((orientation >= 170&&orientation<=190&&state[3]==false)){
                    //Toast.makeText(getApplicationContext(), "Portrait2",         Toast.LENGTH_SHORT).show();
                    rotation=90;
                    settingrotation(rotation);
                    state[0]=false;
                    state[1]=false;
                    state[2]=false;
                    state[3]=true;
                }
            }
        };
        oel.enable();
    }

    private void settingrotation(int rotation) {
        mainBinding.galleryButton.setRotation(rotation);
        mainBinding.changeButton.setRotation(rotation);
        mainBinding.communityButto.setRotation(rotation);
    }

    private void init() {
        surfaceHolder =  mainBinding.surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        mainBinding.buttonShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mjCamera.takePicture(rotation);
                Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink_animation);
                mainBinding.surfaceView.startAnimation(startAnimation);
            }
        });
    }

    private void setCamera(int direction) {
        mjCamera = new mjCamera(this,direction);
        Size previewsize = new Size(1440,1080);
        mjCamera.setPictureSize(new Size(2880, 2160));
        mjCamera.setPreviewSize(previewsize);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        textView.setText(""+ mjCamera.getRatio());
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.framelay);
        frameLayout.getLayoutParams().height=height;
        frameLayout.getLayoutParams().width=(int)(height* mjCamera.getRatio());
        frameLayout.requestLayout();
        //셔터 callback 처리와 동시에 셔터음을 발생시킬 수 있다.

        mjCamera.setShutterCallback(this);
        mjCamera.start(mainBinding.surfaceView);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        setCamera(0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mjCamera.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                setCamera(0);
            }
            else {
                finish();
            }
        }
    }

    @Override
    public void onShutter() {
        //처리
    }
}
