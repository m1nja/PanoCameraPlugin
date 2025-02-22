package com.slicejobs.panacamera.cameralibrary.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.slicejobs.panacamera.R;
import com.slicejobs.panacamera.cameralibrary.CameraSnap;
import com.slicejobs.panacamera.cameralibrary.fresco.zoomable.ZoomableDraweeView;
import com.slicejobs.panacamera.cameralibrary.model.bean.IntentKey;
import com.slicejobs.panacamera.cameralibrary.widget.ScaleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class StitchingActivity extends Activity {

    private final int CLICK_PHOTO = 10001;
    private Uri fileUri;
    private ScaleImageView panoImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stitching);
        CameraSnap.initApplication(getApplication());

        panoImage = (ScaleImageView) findViewById(R.id.panoImage);

        Button btOpenCamera = (Button)findViewById(R.id.bt_open_camera);

        btOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File imagesFolder = new File(FILE_LOCATION);
//                imagesFolder.mkdirs();
//                File image = new File(imagesFolder, "panorama_"+ (clickedImages.size()+1) + ".jpg");
//                fileUri = Uri.fromFile(image);
//                Log.d("StitchingActivity", "File URI = " + fileUri.toString());
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//
//                // start the image capture Intent
//                startActivityForResult(intent, CLICK_PHOTO);

                CameraSnap.getInstance().startCamera(StitchingActivity.this, CLICK_PHOTO,new Bundle());

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case CLICK_PHOTO:
                if(resultCode == RESULT_OK ){

                    Bundle bundle = imageReturnedIntent.getExtras();
                    String panaPath = bundle.getString(IntentKey.IMAGE_PICTURE);
                    Bitmap panoBitmap = BitmapFactory.decodeFile(panaPath);
                    panoImage.setImageBitmap(panoBitmap);
                    /*List<String> mImagePaths = bundle.getStringArrayList(IntentKey.IMAGES);


                    for (String str : mImagePaths) {

//                        File file = new File(str);
//
//                        Log.d("-------------", "文件"+str);
//
//                        Uri fileUri;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            Log.d("------------", "么么");
//                            fileUri = FileProvider.getUriForFile(StitchingActivity.this.getApplicationContext(), "com.keller.myopencvstitching.fileProvider",file );
//
//                        } else {
//                            Log.d("------------", "嗯嗯");
//                            fileUri = Uri.fromFile(file);
//                        }
//
//                        fileUri = Uri.parse("content://com.keller.myopencvstitching.fileProvider/sliicejobs/1544067393338.jpg");
//                        grantUriPermission(getPackageName(), fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                        if(file.exists()) {
//                            Log.d("-------------","文件存在");
//
//                        } else {
//                            Log.d("---------", "文件不存在");
//                        }

                        try {
                            final Uri fileUri = Uri.parse(str);
                            Log.e("-------------照片返回", fileUri.toString());
                            final InputStream imageStream = getContentResolver().openInputStream(fileUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                            ivImage.setImageBitmap(selectedImage);

                        } catch (FileNotFoundException e) {
                            Log.d("--------------", "异常了："+e.toString());
                            e.printStackTrace();
                        }
                    }*/

                }
                break;
        }
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this,
//                mOpenCVCallBack);
//    }


    public void onResume() {
        super.onResume();
    }


}
