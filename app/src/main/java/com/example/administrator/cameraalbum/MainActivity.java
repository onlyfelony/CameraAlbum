package com.example.administrator.cameraalbum;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    public final String TAG = "XSDJK";
    private Button button, choose_pho;
    private ImageView imageView;
    public static final int TAKEN_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final String MY_AUTHORITY = "com.example.administrator.cameraalbum.fileprovider";

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onClick: te52t1");
        button = findViewById(R.id.turnto_camera);
        choose_pho = findViewById(R.id.choose_photo);
        imageView = findViewById(R.id.mytest_image);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cachePath = null;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {

                   // cachePath = getExternalCacheDir().getPath();
                   // cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
                    cachePath = getExternalCacheDir().getAbsolutePath() + File.separator;
                   // Log.d(TAG, "dew");
                } else {
                    cachePath = getCacheDir().getPath();
                   // Log.d(TAG, "dsc");
                }
                //创建File对象，用于存储拍照后的照片
               File imagePath = new File(cachePath,"images");

              //  File imagePath = new File(cachePath,"teimage");
               // File imagePath = new File(cachePath,"imagepath");
               /* Log.d(TAG, getCacheDir().getAbsolutePath().toString());
                Log.d(TAG, getExternalCacheDir().getAbsolutePath().toString());
                Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath().toString());*/


                if (!imagePath.exists()) {
                    imagePath.mkdirs();
                    Log.d(TAG, "onClick: test2");
                }
                File file_output = new File(imagePath, "output_image.jpg");
                // File file_output = new File(imagePath,"output_image.jpg");
                Log.d(TAG, "onClick: test3");
                try {
                    if (file_output.exists()) {
                        file_output.delete();
                    }
                    file_output.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mCurrentPhotoPath = file_output.getAbsolutePath();

                if (Build.VERSION.SDK_INT >= 24) {
                    //如果在Android 7.0以上
                    imageUri = FileProvider.getUriForFile(MainActivity.this, MY_AUTHORITY, file_output);
                } else {
                    imageUri = Uri.fromFile(file_output);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, TAKEN_PHOTO);//启动相机程序

            }
        });


        choose_pho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                } else {
                    openAlbum();//打开相册

                }


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TAKEN_PHOTO:
                if (resultCode == RESULT_OK) {
                    //将拍摄的照片显示出来

                    // 将拍摄的照片显示出来
                    imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
                   /* Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                        Log.d(TAG, "onClick: test4");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }*/
                }


                break;

            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        //4.4以上就用这个方法处理图片
                        handImageOnUp(data);
                    }else {
                        handImageOnDown(data);

                    }


                }
                break;



            default:
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();

                } else {
                    Toast.makeText(this, "You Clicked the permission", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;

        }
    }

    //打开相册
    private void openAlbum() {

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handImageOnUp(Intent data){
   //4.4以上版本解析Uri
        String imagPath = null;
        Uri uri = data.getData();

        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的uri,则通过document id 处理
            String document_id = DocumentsContract.getDocumentId(uri);

            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = document_id.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(document_id));
                imagPath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagPath = uri.getPath();
        }
        displayImage(imagPath); // 根据图片路径显示图片


    }

    private void handImageOnDown(Intent data){

        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);

    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
