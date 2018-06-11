package org.ajmediananumduo.mjcamera.Filter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import org.ajmediananumduo.mjcamera.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class filterActivity extends AppCompatActivity implements ThumbnailCallback {

    static {
        System.loadLibrary("NativeImageProcessor");
    }
    private String filtername;
    private Activity activity;
    private RecyclerView thumbListView;
    private ImageView placeHolderImageView;
    private Bitmap imageBitmap;
    final int REQ_CODE_SELECT_IMAGE=100;
    private int imageCondition=0;
    private boolean isChange;
    private Bitmap ChangedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        activity = this;
        isChange=false;
        initUIWidgets();
        ImageButton button = findViewById(R.id.filterback);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton button1 = findViewById(R.id.btnSave);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChange){
                    try {
                        String desc_filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
                        FileOutputStream outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/Download/"+
                                desc_filename));
                        ChangedBitmap = Bitmap.createScaledBitmap(ChangedBitmap,ChangedBitmap.getWidth()-1,ChangedBitmap.getHeight()-1,false);
                        ChangedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory()+"/Download/"+desc_filename)));
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isChange=false;
                    imageBitmap=ChangedBitmap;
                }
            }
        });
    }

    private void initUIWidgets() {
        thumbListView = (RecyclerView) findViewById(R.id.thumbnails);
        placeHolderImageView = (ImageView) findViewById(R.id.place_holder_imageview);
        if(imageCondition==0) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
        }
        else{
            initHorizontalList();
        }

    }

    private void initHorizontalList() {
        Toast.makeText(getApplicationContext(),"initHorizontalList",Toast.LENGTH_SHORT).show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        //thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage = Bitmap.createScaledBitmap(imageBitmap, 680, 480, false);
                ThumbnailsManager.clearThumbs();
                List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

                for (Filter filter : filters) {
                    ThumbnailItem thumbnailItem = new ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) activity);
                thumbListView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        int imageWith =imageBitmap.getWidth();
        int imageHeight =imageBitmap.getHeight();
        ChangedBitmap=filter.processFilter(Bitmap.createScaledBitmap(imageBitmap,imageWith+1,imageHeight+1,false));
        filtername=filter.getName();
        placeHolderImageView.setImageBitmap(ChangedBitmap);
        isChange=true;
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {





        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();



        if(requestCode == REQ_CODE_SELECT_IMAGE)

        {

            if(resultCode==-1)

            {

                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    placeHolderImageView.setImageBitmap(imageBitmap);
                    imageCondition=1;
                    initHorizontalList();
                    Toast.makeText(this,"진입함",Toast.LENGTH_SHORT).show();





                } catch (FileNotFoundException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IOException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (Exception e)

                {

                    e.printStackTrace();

                }

            }
            else if(resultCode==0){
                finish();
            }

        }

    }



    public String getImageNameToUri(Uri data)

    {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(data, proj, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);



        cursor.moveToFirst();



        String imgPath = cursor.getString(column_index);

        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);



        return imgName;

    }


}
