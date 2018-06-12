package org.ajmediananumduo.mjcamera.Camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import org.ajmediananumduo.mjcamera.MainActivity;
import org.ajmediananumduo.mjcamera.R;

public class optionActivity extends AppCompatActivity{
    Boolean getteristhree;
    Boolean changePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        final Switch switch1 = findViewById(R.id.switch1);
        final Switch switch2 = findViewById(R.id.switch2);
        ImageView imageView = findViewById(R.id.optionBack);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getteristhree =((MainActivity)MainActivity.mContext).isfourthree;
        final mjCamera mjCamera = ((MainActivity)MainActivity.mContext).getCamer();
        changePicture=mjCamera.changePicture;
        switch1.setChecked(changePicture);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePicture=!changePicture;
                mjCamera.changePicture=changePicture;
                switch1.setChecked(changePicture);
            }
        });
        switch2.setChecked(getteristhree);
        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getteristhree=!getteristhree;
                ((MainActivity)MainActivity.mContext).isfourthree=getteristhree;
                switch2.setChecked(getteristhree);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainActivity)MainActivity.mContext).optionchanged();
    }
}
