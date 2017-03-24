package com.kyuwankim.android.camerabasic;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {


    private final int REQ_PERMISSION = 100; //권한요청코드
    private final int REQ_CAMERA = 101; //카메라요청코드
    private final int REQ_GALLERY = 102; //갤러리요청코드
    Button btn_camera;
    ImageView imageview;
    Button btn_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWidget();
        buttonDisable();
        setListener();
        checkPermission();


    }

    private void buttonDisable() {
        btn_camera.setEnabled(false);
    }

    private void buttonEnable() {
        btn_camera.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_GALLERY:
                if(data.getData() != null) {
                    fileUri = data.getData();
                    Glide.with(this).load(fileUri)
                            .into(imageview);
                }
                break;



        }
        if (requestCode == REQ_CAMERA) {
            // 누가 일경우만 getData()에 null 이 넘어올것이다
//            if(data.getData() != null){
//                fileUri = data.getData();
//            }
            imageview.setImageURI(fileUri);
        }
    }

    private void init() {

        //권한처리가 활성화되었을때만 버튼을 활성화 시켜준다
        buttonEnable();

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermission(this, REQ_PERMISSION)) {
                //프로그램 실행
                init();
            }
        } else {
            //프로그램 실행
            init();
        }
    }

    //위젯세팅
    private void setWidget() {
        btn_camera = (Button) findViewById(R.id.btn_camera);
        imageview = (ImageView) findViewById(R.id.imageView);
        btn_gallery = (Button) findViewById(R.id.btn_gallery);
    }

    //리스너세팅
    private void setListener() {
        btn_camera.setOnClickListener(clickListener);
        btn_gallery.setOnClickListener(clickListener);
    }


    Uri fileUri = null;

    //리스너정의
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override

        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()) {
                case R.id.btn_camera:

                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // 누가는 아래 코드를 반영해야 한다.
                    // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
//                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                    ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    }
                    // --- 여기 까지 컨텐트 uri 강제세팅 ---

                    startActivityForResult(intent, REQ_CAMERA);
                    break;

                case R.id.btn_gallery:

                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*"); // 외부저장소에있는 이미지만 가져오기위한 필터링
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"),REQ_CAMERA);
                    break;
            }
        }
    };


    // 권한처리수정
    //  1. 요청할 권한 목록 작성


    //  2. 권한체크 후 콜백 < 사용자 확인 후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            //배열에 넘긴 런타임 권한을 체크해서 승인이 되었으면
            if (PermissionControl.onCheckResult(grantResults)) {

                init();
                // 프로그램 실행
                //loadData();
            }
        } else {
            Toast.makeText(this, "권한을 허용하지 않으면 프로그램을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();

            finish();

        }
    }

}
