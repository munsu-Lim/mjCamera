package org.ajmediananumduo.mjcamera.Community.ui.activity;


import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.ajmediananumduo.mjcamera.R;
import butterknife.BindView;
import butterknife.BindDimen;

public class BaseDrawerActivity extends BaseActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout; //액자틀

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer); //binding
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
    }


}
