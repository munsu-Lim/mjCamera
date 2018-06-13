package org.ajmediananumduo.mjcamera.Community.ui.activity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.ajmediananumduo.mjcamera.Community.Utils;
import org.ajmediananumduo.mjcamera.Community.ui.adapter.FeedAdapter;
import org.ajmediananumduo.mjcamera.Community.ui.adapter.FeedItemAnimator;
import org.ajmediananumduo.mjcamera.R;
import butterknife.BindView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener{
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
 //   private List<String> uidLists = new ArrayList<>();
    @BindView(R.id.rvFeed)
    RecyclerView mjFeed;
    @BindView(R.id.content)
    CoordinatorLayout clContent;
    private FeedAdapter feedAdapter;
    private List<ImageDTO> imageDTOs = new ArrayList<>();
    private boolean pendingIntroAnimation;
    private List<String> uidLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
           //
        }
        //realtime changing
        final ValueEventListener images = database.getReference().child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageDTOs.clear();
                uidLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    String uidKey = snapshot.getKey();   //map 형식
                    imageDTOs.add(0,imageDTO);
                    uidLists.add(0,uidKey);
                }
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setupFeed();
    }

    //매니저를 통해 피드생성
    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mjFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(getApplicationContext(),imageDTOs, uidLists);
        feedAdapter.setOnFeedItemClickListener(this);
        mjFeed.setAdapter(feedAdapter);
        mjFeed.setItemAnimator(new FeedItemAnimator());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }
    }

    private void showFeedLoadingItemDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mjFeed.smoothScrollToPosition(0);
                feedAdapter.showLoadingView();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
        }
        return true;
    }



    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
        Intent dIntent = new Intent(getApplicationContext()
                ,CommentsActivity.class);
        startActivity(dIntent);
    }

}