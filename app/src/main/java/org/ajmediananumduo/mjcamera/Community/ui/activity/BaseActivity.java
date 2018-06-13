package org.ajmediananumduo.mjcamera.Community.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import org.ajmediananumduo.mjcamera.MainActivity;
import org.ajmediananumduo.mjcamera.R;
import butterknife.ButterKnife;  //library
import butterknife.BindView;

public class BaseActivity extends AppCompatActivity {


    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.ivLogo) //logo
    ImageView ivLogo;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bindViews();
    }

    protected void bindViews() {
        ButterKnife.bind(this);
        setupToolbar();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.camera);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ImageView getIvLogo() {
        return ivLogo;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Toast.makeText(getApplicationContext(),id+"",Toast.LENGTH_SHORT).show();
        switch (id)
        {
            case android.R.id.home:
            {
                //카메라액티비티로
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            case R.id.upload_item:
            {
                //업로드액티비티로
                Intent intent2 = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent2);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }



}
