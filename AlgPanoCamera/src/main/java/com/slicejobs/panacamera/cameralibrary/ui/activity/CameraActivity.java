package com.slicejobs.panacamera.cameralibrary.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.eyes.zero.helper.ImageStitcher;
import com.facebook.drawee.view.SimpleDraweeView;
import com.slicejobs.panacamera.R;
import com.slicejobs.panacamera.cameralibrary.helper.BitmapUtils;
import com.slicejobs.panacamera.cameralibrary.helper.CameraOrientationListener;
import com.slicejobs.panacamera.cameralibrary.helper.CameraUtil;
import com.slicejobs.panacamera.cameralibrary.helper.DensityUtil;
import com.slicejobs.panacamera.cameralibrary.helper.ImageLoader;
import com.slicejobs.panacamera.cameralibrary.helper.RxBus;
import com.slicejobs.panacamera.cameralibrary.helper.RxUtil;
import com.slicejobs.panacamera.cameralibrary.helper.SensorControler;
import com.slicejobs.panacamera.cameralibrary.helper.SystemUtil;
import com.slicejobs.panacamera.cameralibrary.helper.ToastUtil;
import com.slicejobs.panacamera.cameralibrary.model.bean.IntentKey;
import com.slicejobs.panacamera.cameralibrary.model.event.TakePictureResult;
import com.slicejobs.panacamera.cameralibrary.utils.ImageUtil;
import com.slicejobs.panacamera.cameralibrary.widget.BalanceView;
import com.slicejobs.panacamera.cameralibrary.widget.RectImageView;
import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import me.drakeet.materialdialog.MaterialDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class CameraActivity extends Activity implements Callback, SensorControler.CameraFocusListener, OnClickListener, SensorEventListener{
    private static final String TAG = "CameraActivity";
    private SurfaceView mSurfaceView;
    private ImageView btnClose;
    private ImageView btnTakePhoto;
    private ImageView imgDelete;
    private TextView tvCloumn;
    private RectImageView imgGuideRect;
    private SimpleDraweeView imgPanorama;
    private ImageView imgSureCamera;
    private SimpleDraweeView imgThumbnail;
    private LinearLayout llThumbnail;
    private RelativeLayout guideLayout;
    RelativeLayout rlHBubble;
    private MaterialDialog mVagueDialog;
    private MaterialDialog mDeleteLastImageDialog;
    private int cameraPosition = 0;
    private SurfaceHolder holder;
    private Camera mCamera;
    private String mImagePath = "";
    private SensorControler mSensorControler;
    private CameraOrientationListener mOrientationListener;
    private int mDisplayOrientation;
    private int mLayoutOrientation;
    private List<String> mNewImagePaths = new ArrayList();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String[] errorHints = null;
    boolean isProcessPreview = false;
    private int mIsTakeImage = 0;
    private int mProcessingImage = 0;
    private int mFirstImage = 1;
    private int mCameraState = -1;
    private CameraInfo mCameraInfo;
    private int mBufferFormat = 2;
    private int mImageHeight;
    private int mImageWidth;
    private CameraActivity.PreViewFrameThread mPreViewFrameThread;
    String mBaseName;
    String mBasePath;
    private Bundle mBundle;
    private Activity mContext;
    private int mValidHint = 0;
    private int mValidOverlap = 0;
    private String panoImagPath = null;

    private BalanceView hBalanceView;
    private SurfaceHolder surfaceHolder;
    private SensorManager sensorManager;

    public Handler mHanlder = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 0:
                    BitmapUtils.deleteImageWithPath(CameraActivity.this.mContext, CameraActivity.this.mImagePath);
                    CameraActivity.this.showVagueDialog();
                    break;
                case 1:
                    Integer[] message = (Integer[])((Integer[])msg.obj);
                    CameraActivity.this.handlerJNIResultError(message[0], message[1]);
                    CameraActivity.this.isProcessPreview = false;
                    return;
                case 2:
                    CameraActivity.this.isProcessPreview = false;
                    boolean valid = (Boolean)msg.obj;
                    if (valid) {
                        CameraActivity.this.btnTakePhoto.setEnabled(true);
                        CameraActivity.this.btnTakePhoto.setImageResource(R.mipmap.take_picture);
                        if (CameraActivity.this.mNewImagePaths.size()  > 0) {
                            guideLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        CameraActivity.this.btnTakePhoto.setEnabled(false);
                        CameraActivity.this.btnTakePhoto.setImageResource(R.mipmap.take_picture_disabled);
                        guideLayout.setVisibility(View.INVISIBLE);
                    }

                    return;
                case 3:
                    boolean updatePano = (Boolean)msg.obj;
                    if (updatePano) {
                        CameraActivity.this.renderPanorama();
                    }

                    CameraActivity.this.isProcessPreview = false;
                    return;
                case 4:
                    return;
                case 5:
                    CameraActivity.this.updateHintFrame();
                    CameraActivity.this.isProcessPreview = false;
                    return;
                case 6:
                    Integer[] flags = (Integer[])((Integer[])msg.obj);
                    CameraActivity.this.isProcessPreview = false;
                    int isTakingPicture = flags[0];
                    int takingPictureSuccess = flags[1];
                    int return_value = flags[2];
                    if (isTakingPicture != 0) {
                        if (takingPictureSuccess != 0) {
                            CameraActivity.this.renderPanorama();
                            CameraActivity.this.mFirstImage = 0;
                        }

                        if (CameraActivity.this.mProcessingImage != 0) {
                            CameraActivity.this.mProcessingImage = 0;
                        }
                    }

                    CameraActivity.this.getHintBox();
                    if (CameraActivity.this.mFirstImage == 0 && (CameraActivity.this.mProcessingImage != 0 || CameraActivity.this.mValidHint == 0 || CameraActivity.this.mValidOverlap == 0)) {
                        CameraActivity.this.btnTakePhoto.setEnabled(false);
                        CameraActivity.this.btnTakePhoto.setImageResource(R.mipmap.take_picture_disabled);
                        guideLayout.setVisibility(View.INVISIBLE);
                    } else {
                        CameraActivity.this.btnTakePhoto.setEnabled(true);
                        CameraActivity.this.btnTakePhoto.setImageResource(R.mipmap.take_picture);
                        if (CameraActivity.this.mNewImagePaths.size()  > 0) {
                            guideLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 7:
                    Integer[] rets = (Integer[])((Integer[])msg.obj);
                    CameraActivity.this.isProcessPreview = false;
                    int ret = rets[0];
                    if (ret >= 0) {
                        CameraActivity.this.renderPanorama();
                        if (ret > 0) {
                            CameraActivity.this.mFirstImage = 1;
                        } else {
                            CameraActivity.this.mFirstImage = 0;
                        }

                        if (CameraActivity.this.mProcessingImage != 0) {
                            CameraActivity.this.mProcessingImage = 0;
                        }
                    }
                    break;
                default:
                    return;
            }

        }
    };
    private Size mPreviewSize;
    private boolean isFinish = false;
    private final AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            CameraActivity.this.mSensorControler.unlockFocus();
            if (success) {
                if (CameraActivity.this.mCameraState == 0) {
                    CameraActivity.this.takePhotoNew();
                }

                KLog.i("CameraActivity", new Object[]{"focus success"});
            } else {
                if (CameraActivity.this.mCameraState == 0) {
                    CameraActivity.this.mCameraState = -1;
                }

                KLog.i("CameraActivity", new Object[]{"focus failed"});
            }

        }
    };
    private int mPictureRotation = 0;
    private Bitmap panoramaBitmap;

    public CameraActivity() {
    }

    private void getHintBox() {
        float[] result = ImageStitcher.getoverlaprect();
        int size = result.length;
        int num_points = (size - 2) / 2;
        if (num_points > 0) {
            this.mValidOverlap = (int)result[size - 2];
            this.mValidHint = result[size - 1] > 0.0F ? 1 : 0;
            if (this.mValidOverlap > 50) {
                this.mValidOverlap = 1;
            } else {
                this.mValidOverlap = 0;
            }
        } else {
            this.mValidOverlap = 0;
            this.mValidHint = 0;
        }

        Log.d("DEADBEEF", "getHintBox() overlap " + this.mValidOverlap + " hint " + this.mValidHint);
        this.imgGuideRect.setPoints(this.mValidHint, this.mValidOverlap, num_points, result);
    }

    public static void start(final Activity context, final int resquestCode, final Bundle bundle) {
        RxPermissions rxPermissions = new RxPermissions(context);
        rxPermissions.request(new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}).subscribe(new Consumer<Boolean>() {
            public void accept(Boolean permission) throws Exception {
                if (permission) {
                    Intent intent = new Intent(context, CameraActivity.class);
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, resquestCode);
                    context.overridePendingTransition(R.anim.slicejobs_slide_bottom_in, 0);
                } else {
                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    materialDialog.setTitle("提示");
                    materialDialog.setMessage("您未授权相关权限,将无法打开相机,请在权限管理中开启存储权限");
                    materialDialog.setPositiveButton("确定", new OnClickListener() {
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.show();
                }

            }
        });
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_camera);
        this.mContext = this;
        this.initEventAndData();
    }

    private void initView() {
        this.mSurfaceView = (SurfaceView)this.findViewById(R.id.my_surfaceView);
        this.btnClose = (ImageView)this.findViewById(R.id.btnClose);
        this.btnTakePhoto = (ImageView)this.findViewById(R.id.btnTakePhoto);
        this.tvCloumn = (TextView)this.findViewById(R.id.tvCloumn);
        this.imgGuideRect = (RectImageView)this.findViewById(R.id.img_guide_rect);
        this.imgPanorama = (SimpleDraweeView)this.findViewById(R.id.img_panorama_preview);
        this.imgSureCamera = (ImageView)this.findViewById(R.id.imgSureCamera);
        this.imgThumbnail = (SimpleDraweeView)this.findViewById(R.id.imgThumbnail);
        this.llThumbnail = (LinearLayout)this.findViewById(R.id.llThumbnail);
        this.imgDelete = (ImageView)this.findViewById(R.id.imgDelete);
        rlHBubble = (RelativeLayout) findViewById(R.id.rl_h_bubble);

        guideLayout = findViewById(R.id.rlMask);
        this.btnClose.setOnClickListener(this);
        this.btnTakePhoto.setOnClickListener(this);
        this.mSurfaceView.setOnClickListener(this);
        this.imgSureCamera.setOnClickListener(this);
        this.llThumbnail.setOnClickListener(this);
        this.imgDelete.setOnClickListener(this);

        initSpiri();
        initSensor();

    }

    private void initSpiri() {
        hBalanceView = new BalanceView(this, DensityUtil.dip2px(this, 300), DensityUtil.dip2px(this, 300));
        surfaceHolder = hBalanceView.getHolder();
        hBalanceView.setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

            }

            //一切都准备好
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                hBalanceView.setSurfaceHolder(surfaceHolder);
                Thread thread = new Thread(hBalanceView);
                thread.start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        rlHBubble.addView(hBalanceView);
    }

    private void initSensor() {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);//初始化传感器
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//获得加速度传感器
//        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);//陀螺仪
        if (null != sensor && sensorManager != null) {
            //根据不同应用，需要的反应速率不同，具体根据实际情况设定
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void initEventAndData() {
        this.mBundle = this.getIntent().getExtras();
        this.initView();
        this.errorHints = this.getResources().getStringArray(R.array.camera_error_hint);
        this.getWindow().addFlags(128);
        CameraUtil.init(this);
        this.holder = this.mSurfaceView.getHolder();
        this.holder.setType(3);
        this.holder.addCallback(this);
        this.mSensorControler = SensorControler.getInstance();
        this.mOrientationListener = new CameraOrientationListener(this.mContext);
        this.mOrientationListener.enable();
        this.mSensorControler.setCameraFocusListener(this);
        this.mPreViewFrameThread = new CameraActivity.PreViewFrameThread();
        this.mPreViewFrameThread.setProcessState(true);
        this.mPreViewFrameThread.start();
        this.mBasePath = this.getExternalFilesDir("pictures").getAbsolutePath() + File.separator;
        this.panoImagPath = genPanoImageFileName();
        this.registerTakePictureState();
        ImageStitcher.start();
    }

    protected void onResume() {
        super.onResume();
        if (this.mCamera == null) {
            this.mCamera = this.getCamera(this.cameraPosition);
            if (this.holder != null) {
                this.startPreview(this.mCamera, this.holder);
            }
        }

    }

    protected void onPause() {
        super.onPause();
        this.releaseCamera();
    }

    private Camera getCamera(int id) {
        Camera camera = null;

        try {
            camera = Camera.open(id);
        } catch (Exception var4) {
            ;
        }

        return camera;
    }

    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            this.mSensorControler.restFoucs();
            this.setupCamera(camera);
            this.mSensorControler.unlockFocus();
            camera.setPreviewDisplay(holder);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, this.cameraPosition, camera);
            camera.setPreviewCallback(new PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (CameraActivity.this.mPreViewFrameThread != null && !CameraActivity.this.isProcessPreview) {
                        Log.d("DEADBEEF", "preview callback() thread " + CameraActivity.this.mPreViewFrameThread + " processing preview " + CameraActivity.this.isProcessPreview);
                        CameraActivity.this.isProcessPreview = true;
                        CameraActivity.this.mPreViewFrameThread.updateImageByte(data, CameraActivity.this.mIsTakeImage);
                    }

                }
            });
            camera.startPreview();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    private void releaseCamera() {
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback((PreviewCallback)null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }

    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnClose) {
            this.onBackPressedSupport();
        } else if (id == R.id.btnTakePhoto) {
            if (this.mCameraState == -1) {
                this.mCameraState = 0;
                this.onCameraFocus(false, (Point)null);
            }
        } else if (id == R.id.my_surfaceView) {
            this.onCameraFocus(false, (Point)null);
        } else if (id == R.id.imgDelete) {
            this.showDeleteLastImageDialog();
        } else if (id == R.id.imgSureCamera) {
            this.completePic();
        } else if (id == R.id.llThumbnail) {
            ImagePreviewActivity.start(this.mContext, (ArrayList)this.mNewImagePaths, this.mNewImagePaths.size() - 1, 1003, -1, this.mOrientationListener.getRememberOriginalOrientation(), 1);
        }

    }

    private void completePic() {
        float[] parameters = ImageStitcher.getStitchingParameter();
        int num_images = (int)parameters[0];
        int image_width = (int)parameters[1];
        int image_height = (int)parameters[2];
        String vender_tag = "Lingmou";
        String version_tag = "0.0.3";

        for(int i = 0; i < num_images; ++i) {
            Uri uri = null;

            try {
                uri = Uri.parse((String)this.mNewImagePaths.get(i));
            } catch (IndexOutOfBoundsException var11) {
                var11.printStackTrace();
                this.isFinish = false;
                break;
            }

            this.isFinish = true;

            try {
                String imageDescription = "Lingmou:0.0.3:" + image_width + ":" + image_height + ":" + num_images + ":" + i + ":" + this.mCameraInfo.orientation + ":" + this.mPictureRotation;

                for(int j = 0; j < 9; ++j) {
                    imageDescription = imageDescription + ":" + parameters[3 + i * 9 + j];
                }

                ExifInterface exif = new ExifInterface(uri.getPath());
                exif.setAttribute("ImageDescription", imageDescription);
                exif.saveAttributes();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }

        if (!this.isFinish) {
            ToastUtil.shortShow("图片处理中");
        } else {
            Intent intent = new Intent();
            this.mBundle.putSerializable(IntentKey.IMAGES, (ArrayList)this.mNewImagePaths);
            this.mBundle.putInt(IntentKey.ORIENTATION, this.mOrientationListener.getRememberOriginalOrientation());
            this.mBundle.putString(IntentKey.IMAGE_PICTURE, panoImagPath);
            intent.putExtras(this.mBundle);
            this.setResult(-1, intent);
            this.back();
        }
    }

    public void onBackPressed() {
        this.onBackPressedSupport();
    }

    public void onBackPressedSupport() {
        for(int i = 0; i < this.mNewImagePaths.size(); ++i) {
            BitmapUtils.deleteImageWithPath(this.mContext, (String)this.mNewImagePaths.get(i));
        }

        this.back();
    }

    private void back() {
        this.finish();
        this.overridePendingTransition(0, R.anim.slicejobs_slide_bottom_out);
    }

    private void setupCamera(Camera camera) {
        this.determineDisplayOrientation();
        Parameters parameters = camera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains("auto")) {
            parameters.setFocusMode("auto");
        }

        this.mPreviewSize = CameraUtil.findBestPreviewResolution(camera);
        KLog.d("CameraActivity", new Object[]{"mPreviewSize,width=" + this.mPreviewSize.width + "height=" + this.mPreviewSize.height});
        parameters.setPreviewSize(this.mPreviewSize.width, this.mPreviewSize.height);
        this.mImageWidth = Math.min(this.mPreviewSize.width, this.mPreviewSize.height);
        this.mImageHeight = Math.max(this.mPreviewSize.width, this.mPreviewSize.height);
        this.mSensorControler.restFoucs();
        camera.setParameters(parameters);
        this.mSensorControler.unlockFocus();
        int picHeight = CameraUtil.screenWidth * this.mPreviewSize.width / this.mPreviewSize.height;
        LayoutParams params = new LayoutParams(CameraUtil.screenWidth, picHeight);
        this.mSurfaceView.setLayoutParams(params);
        this.mCameraInfo = new CameraInfo();
        Camera.getCameraInfo(0, this.mCameraInfo);
    }

    private void determineDisplayOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(0, cameraInfo);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch(rotation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
        }

        int displayOrientation;
        if (cameraInfo.facing == 1) {
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        this.mDisplayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        this.mLayoutOrientation = degrees;
        this.mCamera.setDisplayOrientation(displayOrientation);
        KLog.i("CameraActivity", new Object[]{"displayOrientation:" + displayOrientation});
    }

    public void onCameraFocus(boolean needDelay, Point point) {
        if (this.focus(this.autoFocusCallback)) {
            this.mSensorControler.lockFocus();
        }

    }

    private void takePhotoNew() {
        this.mHanlder.postDelayed(new Runnable() {
            public void run() {
                if (CameraActivity.this.mCameraState == 0) {
                    CameraActivity.this.mIsTakeImage = 1;
                    CameraActivity.this.mProcessingImage = 1;
                    CameraActivity.this.mCameraState = -1;
                }

            }
        }, 200L);
    }

    private boolean focus(AutoFocusCallback callback) {
        try {
            this.mCamera.autoFocus(callback);
            return true;
        } catch (Exception var3) {
            this.mSensorControler.unlockFocus();
            var3.printStackTrace();
            return false;
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.startPreview(this.mCamera, this.holder);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        this.mCamera.stopPreview();
        this.startPreview(this.mCamera, this.holder);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.releaseCamera();
    }

    public int getPicRotation() {
        return (this.mDisplayOrientation + this.mOrientationListener.getRememberedNormalOrientation() + this.mLayoutOrientation) % 360;
    }

    public boolean saveImageToGallery(String fname) {
        File pictureFile = new File(fname);
        this.mImagePath = "file://" + pictureFile;
        this.mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + pictureFile)));
        RxBus.getInstance().post(new TakePictureResult(this.mImagePath, true));
        return true;
    }

    private void registerTakePictureState() {
        RxBus.getInstance().toFlowable(TakePictureResult.class).compose(RxUtil.rxSchedulerHelper()).subscribe(new Consumer() {
            public void accept(Object o) throws Exception {
                if(o instanceof TakePictureResult){
                    TakePictureResult takePictureResult = (TakePictureResult) o;
                    if (takePictureResult.isAdd) {
                        CameraActivity.this.mNewImagePaths.add(takePictureResult.mPath);
                    } else {
                        BitmapUtils.deleteImageWithPath(CameraActivity.this.mContext, (String)CameraActivity.this.mNewImagePaths.get(CameraActivity.this.mNewImagePaths.size() - 1));
                        CameraActivity.this.mNewImagePaths.remove(CameraActivity.this.mNewImagePaths.size() - 1);
                    }

                    CameraActivity.this.tvCloumn.setText(CameraActivity.this.mNewImagePaths.size() + "张");
                    if (CameraActivity.this.mNewImagePaths.size() > 0) {
                        CameraActivity.this.imgThumbnail.setVisibility(View.VISIBLE);
                        CameraActivity.this.imgSureCamera.setVisibility(View.VISIBLE);
                        CameraActivity.this.llThumbnail.setVisibility(View.VISIBLE);
                        ImageLoader.onDisplayImage(CameraActivity.this.mContext, CameraActivity.this.imgThumbnail, (String)CameraActivity.this.mNewImagePaths.get(CameraActivity.this.mNewImagePaths.size() - 1), 100, 100);
                        CameraActivity.this.imgDelete.setVisibility(View.VISIBLE);
                    } else {
                        CameraActivity.this.imgDelete.setVisibility(View.INVISIBLE);
                        CameraActivity.this.imgThumbnail.setVisibility(View.INVISIBLE);
                        CameraActivity.this.imgSureCamera.setVisibility(View.INVISIBLE);
                        CameraActivity.this.llThumbnail.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        this.mSensorControler.onStart();
        this.mOrientationListener.enable();
    }

    protected void onStop() {
        super.onStop();
        this.mOrientationListener.disable();
        this.mSensorControler.onStop();
    }

    protected void onDestroy() {
        if (this.panoramaBitmap != null) {
            this.panoramaBitmap.recycle();
        }

        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback((PreviewCallback)null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }

        ImageStitcher.stop();
        this.mHanlder.removeCallbacksAndMessages((Object)null);
        if (this.mPreViewFrameThread != null) {
            this.mPreViewFrameThread.setProcessState(false);
            this.mPreViewFrameThread.notifyThread();
        }
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
        if (hBalanceView != null) {
            hBalanceView.colseThread();//停止线程
        }

        super.onDestroy();
    }

    public void onFocus() {
        this.onCameraFocus(false, (Point)null);
    }

    private void showVagueDialog() {
        if (this.mVagueDialog == null) {
            this.mVagueDialog = new MaterialDialog(this.mContext);
            this.mVagueDialog.setMessage("图片清晰度不够，请重新拍摄");
            this.mVagueDialog.setPositiveButton("确定", new OnClickListener() {
                public void onClick(View v) {
                    CameraActivity.this.mVagueDialog.dismiss();
                }
            });
        }

        this.mVagueDialog.show();
    }

    private void showDeleteLastImageDialog() {
        if (this.mDeleteLastImageDialog == null) {
            this.mDeleteLastImageDialog = new MaterialDialog(this.mContext);
            this.mDeleteLastImageDialog.setMessage("是否删除上一张照片？");
        }

        this.mDeleteLastImageDialog.setNegativeButton("取消", new OnClickListener() {
            public void onClick(View v) {
                CameraActivity.this.mDeleteLastImageDialog.dismiss();
            }
        });
        this.mDeleteLastImageDialog.setPositiveButton("确定", new OnClickListener() {
            public void onClick(View v) {
                CameraActivity.this.mDeleteLastImageDialog.dismiss();
                CameraActivity.this.mIsTakeImage = 2;
            }
        });
        this.mDeleteLastImageDialog.show();
    }

    public void handlerJNIResultError(int resultInt, int isUnitImage) {
        this.setHintError(resultInt);
        if (this.btnTakePhoto != null) {
            this.btnTakePhoto.setEnabled(false);
            this.btnTakePhoto.setImageResource(R.mipmap.take_picture_disabled);
            guideLayout.setVisibility(View.INVISIBLE);
        }

    }

    public void handlerOverlapAreaError() {
        this.btnTakePhoto.setEnabled(false);
    }

    public void updateHintFrame() {
        if (this.imgGuideRect != null) {
            this.imgGuideRect.updateRect();
        }
    }

    public void setHintError(int resultInt) {
        String hintError = this.errorHints[0];
        switch(resultInt) {
            case -1009:
                hintError = this.errorHints[8];
                break;
            case -1008:
                hintError = this.errorHints[7];
                break;
            case -1007:
                hintError = this.errorHints[6];
                break;
            case -1006:
            case -1002:
                hintError = this.errorHints[2];
                break;
            case -1005:
                hintError = this.errorHints[5];
                break;
            case -1004:
                hintError = this.errorHints[4];
                break;
            case -1003:
                hintError = this.errorHints[3];
                break;
            case -1001:
                hintError = this.errorHints[1];
        }

        KLog.e("CameraActivity", new Object[]{hintError + "---------"});
    }

    public void renderPanorama() {
        Log.d("DEADBEEF", "renderPanorama() =====");
        List var1 = this.mNewImagePaths;
        synchronized(this.mNewImagePaths) {
            if (this.mNewImagePaths.size() == 0) {
                this.imgPanorama.setImageDrawable((Drawable)null);
                if (this.panoramaBitmap != null && !this.panoramaBitmap.isRecycled()) {
                    this.panoramaBitmap.recycle();
                    this.panoramaBitmap = null;
                }

                System.gc();
            }

            Log.d("DEADBEEF", "renderPanorama() +++++");
            if (this.panoramaBitmap == null) {
                int max_width = SystemUtil.getScreenWH(this.mContext).widthPixels;
                int height = SystemUtil.getScreenWH(this.mContext).heightPixels;
                Config conf = Config.ARGB_8888;
                this.panoramaBitmap = Bitmap.createBitmap(max_width, height, conf);
            }

            ImageStitcher.renderPanorama(this.panoramaBitmap);
            this.imgPanorama.setImageBitmap(this.panoramaBitmap);
            try {
                ImageUtil.saveBitmapToPath(this,panoImagPath, this.panoramaBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("DEADBEEF", "renderPanorama() -----");
        }
    }

    private final String genFileName() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "algPanoPart");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName1 = System.currentTimeMillis() + ".jpg";
        File tmpFile = new File(appDir, fileName1);
        return tmpFile.getAbsolutePath();
    }

    private final String genPanoImageFileName() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "algPano");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName1 = "pano_image_" + System.currentTimeMillis() + ".jpg";
        File tmpFile = new File(appDir, fileName1);
        return tmpFile.getAbsolutePath();
    }

    public class PreViewFrameThread extends Thread {
        byte[] mBytes = new byte[0];
        boolean isProcess = false;
        int mIsTakingPicture = 0;
        byte[] proccessData = null;
        int processSize = 0;

        public PreViewFrameThread() {
            this.isProcess = true;
        }

        public void setProcessState(boolean isProcess) {
            this.isProcess = isProcess;
            this.notifyThread();
        }

        public void updateImageByte(byte[] bytes, int isTakingPicture) {
            Log.d("DEADBEEF", "operation +++++" + isTakingPicture + " status == " + this.mIsTakingPicture);
            if (this.mIsTakingPicture != 0) {
                switch(this.mIsTakingPicture) {
                    case 1:
                        KLog.d("CameraActivity", new Object[]{"正在处理拍摄图片中,请稍后..."});
                        return;
                    case 2:
                        KLog.d("CameraActivity", new Object[]{"当前正在回退照片中,请稍后..."});
                        return;
                    case 3:
                        KLog.d("CameraActivity", new Object[]{"正在初始化相机,请稍后.."});
                        return;
                    default:
                }
            } else {
                CameraActivity.this.mOrientationListener.remeberOriginalOrientation();
                CameraActivity.this.mOrientationListener.rememberOrientation();
                this.mBytes = bytes;
                this.mIsTakingPicture = isTakingPicture;
                CameraActivity.this.mIsTakeImage = 0;
                this.notifyThread();
                ++this.processSize;
            }
        }

        public synchronized void waitThread() {
            try {
                this.wait();
            } catch (Throwable var2) {
                var2.printStackTrace();
            }

        }

        public synchronized void notifyThread() {
            try {
                Log.d("DEADBEEF", "notifyThread() ++++");
                this.notify();
                Log.d("DEADBEEF", "notifyThread() ----");
            } catch (Throwable var2) {
                var2.printStackTrace();
            }

        }

        public void run() {
            while(this.isProcess) {
                Log.d("DEADBEEF", "run() ======");
                if (this.mBytes != null && this.mBytes.length > 0) {
                    this.proccessData = this.mBytes;
                    int w = CameraActivity.this.mImageWidth;
                    int h = CameraActivity.this.mImageHeight;
                    if (CameraActivity.this.mCameraInfo.orientation == 90 || CameraActivity.this.mCameraInfo.orientation == 270) {
                        w = CameraActivity.this.mImageHeight;
                        h = CameraActivity.this.mImageWidth;
                    }

                    String fname = "";
                    if (this.mIsTakingPicture == 1) {
                        CameraActivity.this.mPictureRotation = CameraActivity.this.getPicRotation();
                        fname = CameraActivity.this.genFileName();
                    }

                    Log.d("DEADBEEF", "run() ++++");
                    int ret = ImageStitcher.track(this.mBytes, w, h, CameraActivity.this.mCameraInfo.orientation, 2, 0, this.mIsTakingPicture, fname, CameraActivity.this.mPictureRotation, 80);
                    Log.d("liujiandong", "DEADBEEF run() 返回状态:" + ret + "，拍照状态" + this.mIsTakingPicture);
                    synchronized(CameraActivity.this.mNewImagePaths) {
                        if (this.mIsTakingPicture == 2) {
                            if (ret >= 0) {
                                RxBus.getInstance().post(new TakePictureResult((String)null, false));
                            } else {
                                Log.d("DEADBEEF", "cancel failed");
                            }

                            CameraActivity.this.mHanlder.sendMessage(CameraActivity.this.mHanlder.obtainMessage(7, new Integer[]{ret}));
                            this.mIsTakingPicture = 0;
                        } else {
                            boolean takingPictureSuccess = this.mIsTakingPicture == 1 && ret > 0;
                            CameraActivity.this.mHanlder.sendMessage(CameraActivity.this.mHanlder.obtainMessage(6, new Integer[]{this.mIsTakingPicture == 1 ? 1 : 0, takingPictureSuccess ? 1 : 0, ret}));
                            this.mIsTakingPicture = 0;
                            if (takingPictureSuccess) {
                                KLog.e("DEADBEEF", new Object[]{"output size " + this.mBytes.length + " actual " + ret});
                                CameraActivity.this.saveImageToGallery(fname);
                            }
                        }
                    }
                }

                if (this.mIsTakingPicture == 0) {
                    if (!this.isProcess) {
                        return;
                    }

                    this.waitThread();
                    KLog.d("DEADBEEF", new Object[]{"waited"});
                }
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float x = event.values[0];
        final float y = event.values[1];
        final float z = event.values[2];

        if (y > 5) {//才是侧面, 10正侧面
            hBalanceView.updateBubble(z, x);
            if (hBalanceView.isHorizontal()) {
                if (hBalanceView.isVertical()) {

                } else {
                    ToastUtil.show("设备请不要前后倾斜");
                }
            } else {
                ToastUtil.show("设备请不要前后倾斜");
            }

        } else {
            ToastUtil.show("请将设备拿正！");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
