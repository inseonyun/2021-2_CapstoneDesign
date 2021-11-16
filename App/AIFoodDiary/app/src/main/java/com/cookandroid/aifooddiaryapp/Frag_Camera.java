package com.cookandroid.aifooddiaryapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Frag_Camera extends Fragment {
    private View view;
    File file;
    ImageButton btn_camera,btn_handwrite;
    String mCurrentPhotoPath;
    TextView tv_AddFood;
    String date,meal;
    String flag="camera";
    Bundle bundle = new Bundle();
    final private static String TAG = "CAMERA";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_camera, container, false);
        btn_camera=(ImageButton)view.findViewById(R.id.imageButton2);
        btn_handwrite=(ImageButton)view.findViewById(R.id.imageButton);
        File sdcard = Environment.getExternalStorageDirectory();
        file= new File(sdcard,"capture.jpg");
        tv_AddFood=view.findViewById(R.id.tv_AddFood);

        //이전 프래그먼트에서 받아온 날짜정보 끼니정보 저장
        Bundle bundle_pre = getArguments();
        if(bundle_pre!=null){
            //bundle 통해서 파일 경로 얻어오기
            mCurrentPhotoPath= bundle_pre.getString("file_path");
            meal=bundle_pre.getString("meal");
            date=bundle_pre.getString("date");
            tv_AddFood.setText(date+" 식단추가");
        }




        //다음 프래그먼트에도 날짜 정보 끼니 정보 전달
        bundle.putString("meal",meal);
        bundle.putString("date",date);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            }
            else {
                Log.d(TAG, "권한 설정 요청"); requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        btn_handwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Frag_Add_HandWrite frag_add_handWrite = new Frag_Add_HandWrite();
                frag_add_handWrite.setArguments(bundle);
                transaction.replace(R.id.main_frame, frag_add_handWrite);
                transaction.commit();
            }
        });

        return view;
    }

    private void dispatchTakePictureIntent() {
        PackageManager pm= getContext().getPackageManager();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(pm) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            } if(photoFile != null) {
                //여기서부터 고쳐야함
                Uri photoURI = FileProvider.getUriForFile(this.getContext(), "com.cookandroid.aifooddiaryapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 101);
            }
        }
    }



    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // 카메라 촬영을 하면 이미지뷰에 사진 삽입

        if(requestCode == 101 && resultCode == Activity.RESULT_OK) {
            /////////////////////////////////
            // 학습모델로 이미지 보내는 코드/////////
            /////////////////////////////////

            //사진 파일명을 bundle통해서 다음 프래그먼트로 넘겨준다
            bundle.putString("file_path",mCurrentPhotoPath);
            bundle.putString("flag",flag);

            //사진촬영이 완료되었을 경우 frag_add_camera로 이동
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Frag_Add_Camera frag_add_camera = new Frag_Add_Camera();
            frag_add_camera.setArguments(bundle);
            transaction.replace(R.id.main_frame, frag_add_camera);
            transaction.commit();



        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = date+meal;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



}
