package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
//import java.sql.Date;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;
    private File imgFile;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 101;
    //读写权限
    private String[] mPermissionsArrays = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            };
    //请求状态码
    //private final static int REQUEST_PERMISSION = 123;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                if (!checkPermissionAllGranted(mPermissionsArrays)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(mPermissionsArrays, REQUEST_EXTERNAL_STORAGE);
                    } else {
                        // TODO
                    }
                } else {
                    Toast.makeText(TakePictureActivity.this, "已经获取所有所需权限", Toast.LENGTH_SHORT).show();
                }
            } else {
                takePicture();
            }
        });

    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        // 6.0以下不需要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for(String permission:permissions){
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }
    private void takePicture() {
        //todo 打开相机
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 指定照片存储位置为sd卡本目录下
//        mTempPhotoPath = Environment.getExternalStorageDirectory() +File.separator + "photo.jpeg";
//        //获得图片所在位置的Uri路径
//        imageUri = FileProvider.getUriForFile(TakePictureActivity.this,TakePictureActivity.this
//        .getApplicationContext().getPackageName()+".my.provider",new File(mTempPhotoPath));
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        imgFile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        if(imgFile !=null){
            Uri fileUri = Uri.fromFile(imgFile);
//            getUriForFile(this,"com.bytedance.camera.com",imgFile);
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);//图片显示
            setPic();
        }
    }

    private void setPic() {
        //todo 根据imageView裁剪
        //todo 根据缩放比例读取文件，生成Bitmap
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options bmOption = new BitmapFactory.Options();
        bmOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOption);
        int photoW = bmOption.outWidth;
        int photoH = bmOption.outHeight;

        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);
        bmOption.inJustDecodeBounds = false;
        bmOption.inSampleSize = scaleFactor;
        bmOption.inPurgeable = true;

        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOption);
        imageView.setImageBitmap(bmp);
        //todo 如果存在预览方向改变，进行图片旋转
        //todo 如果存在预览方向改变，进行图片旋转
        //Utils.rotateImage(bmp,Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE).getPath());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
                Toast.makeText(this, "已经授权" + Arrays.toString(permissions), Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}
