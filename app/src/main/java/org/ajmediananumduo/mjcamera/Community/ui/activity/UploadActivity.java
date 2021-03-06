package org.ajmediananumduo.mjcamera.Community.ui.activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.ajmediananumduo.mjcamera.R;
import java.io.File;
import butterknife.BindView;


public class UploadActivity extends BaseActivity {

    private static final int GALLERY_CODE = 1111;
    private static final String TAG ="MJTEST" ;
    private FirebaseAuth auth= FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String imagePath;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    @Nullable
    @BindView(R.id.imageView)  //업로드 시 이미지
            ImageView imageView;
    @Nullable
    @BindView(R.id.filterName)  //업로드시 필터명
            TextView filterName;
    @Nullable
    @BindView(R.id.description)  //업로드시 텍스트
            EditText description;
    @Nullable
    @BindView(R.id.upload_button)  //업로드시 텍스트
            Button uploadBtn;

    @Nullable
    @BindView(R.id.search_button)  //업로드시 텍스트
            ImageButton searchBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        //업로드 버튼 리스너
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(imagePath);
            }
        });
        //검색 버튼 리스너
        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //사진을 받아온다
                // Toast.makeText(getApplicationContext(),id+"",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_CODE);
                //권한
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},0 );
                }

            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_CODE) {
            if(resultCode==-1) {
                imagePath = getPath(data.getData());
                File f = new File(imagePath);
                imageView.setImageURI(Uri.fromFile(f));

            }
        }

    }
    //경로를 얻어온다
    public String getPath(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cusorLoader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = cusorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    //업로드 버튼
    private void upload(String uri){
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://mjcamera-2aa3a.appspot.com");
        Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        Log.i(TAG, "upload: "+"images/"+file.getLastPathSegment());
        //파일명을 통해 필터명 파싱
        String fName = file.getLastPathSegment();
        if(fName.contains("Starlit"))
            filterName.setText(filterName.getText()+" #Starlit");
        else if(fName.contains("Bluemess"))
            filterName.setText(filterName.getText()+" #Bluemess");
        else if(fName.contains("Struck"))
            filterName.setText(filterName.getText()+" #Struck");
        else if(fName.contains("Lime"))
            filterName.setText(filterName.getText()+" #Lime");
        else if(fName.contains("Whisper"))
            filterName.setText(filterName.getText()+" #Whisper");
        else if(fName.contains("Amazon"))
            filterName.setText(filterName.getText()+" #Amazon");
        else if(fName.contains("Adele"))
            filterName.setText(filterName.getText()+" #Adele");
        else if(fName.contains("Cruz"))
            filterName.setText(filterName.getText()+" #Cruz");
        else if(fName.contains("Metropolis"))
            filterName.setText(filterName.getText()+" #Metropolis");
        else if(fName.contains("Audrey"))
            filterName.setText(filterName.getText()+" #Audrey");
        else if(fName.contains("Rise"))
            filterName.setText(filterName.getText()+" #Rise");
        else if(fName.contains("Mars"))
            filterName.setText(filterName.getText()+" #Mars");
        else if(fName.contains("Haan"))
            filterName.setText(filterName.getText()+" #Haan");
        else if(fName.contains("OldMan"))
            filterName.setText(filterName.getText()+" #OldMan");
        else if(fName.contains("Clarendon"))
            filterName.setText(filterName.getText()+" #Claredon");
        else if(fName.contains("April"))
            filterName.setText(filterName.getText()+" #April");
        else  //필터 적용X 사진일시
            filterName.setText("#Default");

        final StorageReference ref = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.i(TAG, "onComplete: "+downloadUri);
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.imageUrl = downloadUri.toString();
                    imageDTO.title = filterName.getText().toString();
                    imageDTO.description = description.getText().toString();
                    imageDTO.userId = auth.getCurrentUser().getEmail();
                    database.getReference().child("images").push().setValue(imageDTO);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        //사진 올린후 다시 커뮤니티로
        Intent cIntent = new Intent(getApplicationContext(),
                org.ajmediananumduo.mjcamera.Community.ui.activity.MainActivity.class);
        startActivity(cIntent);
        finish();
    }
}


