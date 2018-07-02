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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    public final String TAG = "XSDdaJK";
    private Button button, choose_pho, xxx;
    private ImageView imageView;
    public static final int TAKEN_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int ACTIVITY_REQUEST_SELECT_PHOTO = 2;
    public static final int SELECT_PHOTO_MAX = 9;
    public static final String MY_AUTHORITY = "com.example.administrator.cameraalbum.fileprovider";
    private ArrayList<AlbumFile> mAlbumFiles;
    private Uri imageUri;
    private RecyclerView recyclerView;
    private List<MyImage> myImageList = new ArrayList<>();
    private MyImage imag = new MyImage(MyImage.ADDIM, R.drawable.add_w);
    private MyImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onClick: te52t1");
        button = findViewById(R.id.turnto_camera);
        choose_pho = findViewById(R.id.choose_photo);
        xxx = findViewById(R.id.xxx);
        imageView = findViewById(R.id.mytest_image);
        myImageList.add(imag);

        recyclerView = findViewById(R.id.my_recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyImageAdapter(myImageList);
        adapter.setListener(new MyImageAdapter.MyClickItem() {
            @Override
            public void onIClick(View view, int position) {

                if((position+1)==adapter.getItemCount()) {
                    selectAlbum();
                    /**
                     * 点提交之后再清除mAlbumFiles
                     */
                   // Toast.makeText(MainActivity.this, "添加" + position, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onDClick(int position) {

                 //   mAlbumFiles.remove(position);
                //Toast.makeText(MainActivity.this, "删除" + position, Toast.LENGTH_SHORT).show();
                //Log.d(TAG, "remove file:"+mAlbumFiles.get(position).getPath());
                //Log.d(TAG, "position:"+position);

                if(position<mAlbumFiles.size()) {

                  /*  Log.d(TAG, "remove file:"+mAlbumFiles.get(position).getPath());
                    Log.d(TAG, "size:"+mAlbumFiles.size());*/
                    mAlbumFiles.remove(position);
                }
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);

        //初始化
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);

                } else {
                    openCarma();//打开相机

                }


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
        xxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAlbum();
            }
        });

    }
   //相册多图选择和返回
   private void selectAlbum(){

       Album.image(MainActivity.this)
               .multipleChoice()
               .columnCount(3)
               .camera(true)
               .selectCount(SELECT_PHOTO_MAX).checkedList(mAlbumFiles)//显示mAlbumFiles
               .onResult(new Action<ArrayList<AlbumFile>>() {
                   @Override
                   public void onAction(@NonNull ArrayList<AlbumFile> result) {
                       mAlbumFiles = result;

                       if (mAlbumFiles.size() > 0) {
                           myImageList.clear();
                           for (int i = 0; i < mAlbumFiles.size(); i++) {
                               String mPath = mAlbumFiles.get(i).getPath();
                               MyImage im = new MyImage(mPath, MyImage.MYIMAGE);
                               myImageList.add(im);
                           }
                           myImageList.add(imag);
                           adapter.notifyDataSetChanged();
                       }

                       //设置好之后再清除这个文件
                      // mAlbumFiles.clear();
                   }
               })
               .onCancel(new Action<String>() {
                   @Override
                   public void onAction(@NonNull String result) {

                   }
               })
               .start();

   }
    //loader
    public class MediaLoader implements AlbumLoader {

        @Override
        public void load(ImageView imageView, AlbumFile albumFile) {
            load(imageView, albumFile.getPath());
        }

        @Override
        public void load(ImageView imageView, String url) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TAKEN_PHOTO:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "原来路径:"+mCurrentPhotoPath);
                    //将拍摄的照片显示出来
                   String deco =  BitmapUtil.compressImage(mCurrentPhotoPath);
                    Log.d(TAG, "压缩后路径:"+deco);
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
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上就用这个方法处理图片
                        handImageOnUp(data);
                    } else {
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
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCarma();

                } else {
                    Toast.makeText(this, "You Clicked the permission", Toast.LENGTH_SHORT).show();
                }
                break;


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

    private void openCarma() {
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
        File imagePath = new File(cachePath, "images");

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
            Log.d(TAG, "filePath:" + file_output.getAbsolutePath());
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

    }//打开相机

    //打开相册
    private void openAlbum() {

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handImageOnUp(Intent data) {
        //4.4以上版本解析Uri
        String imagPath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri,则通过document id 处理
            String document_id = DocumentsContract.getDocumentId(uri);

            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
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

    private void handImageOnDown(Intent data) {

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
