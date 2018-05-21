package org.ajmediananumduo.mjcamera.Camera;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.ajmediananumduo.mjcamera.databinding.ActivityPropertyBinding;

import android.widget.TextView;

import org.ajmediananumduo.mjcamera.R;


public class PropertyActivity extends AppCompatActivity {

    private ActivityPropertyBinding propertyBinding;
    private CameraParameter cameraParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyBinding = DataBindingUtil.setContentView(this, R.layout.activity_property);

        cameraParameter = (CameraParameter) getIntent().getSerializableExtra("cameraParameters");

        for(Size size : cameraParameter.getSupportedPreviewSizes()) {
            propertyBinding.textViewSupportedPreviewSize.setText(propertyBinding.textViewSupportedPreviewSize.getText().toString() +
                    String.valueOf(size.width) + "*" + String.valueOf(size.height) + "\n");
        }

        for(Size size : cameraParameter.getSupportedPictureSizes()) {
            propertyBinding.textViewSupportedPictureSize.setText(propertyBinding.textViewSupportedPictureSize.getText().toString() +
                    String.valueOf(size.width) + "*" + String.valueOf(size.height) + "\n");
        }
    }
}
