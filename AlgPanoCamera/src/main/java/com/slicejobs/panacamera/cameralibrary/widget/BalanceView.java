package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.slicejobs.panacamera.R;


public class BalanceView extends SurfaceView implements Runnable{
    private float rotaErrorSize = 1.0f;//前后误差大小
    private float mAngle = 90.0f;//左右倾斜角度
    private float lineLength = 200;//平衡线半径
    private float testAngle = 10;//测试线的角度
    private float testRadian = 0;//测试线弧度
    private float testWidth = 0;
    private float testHeight = 0;


    private Context mContext;
    private float layoutWidth;
    private float layoutHeight;

    private float rollingTag = 0;

    private boolean isblool = false;
    private float posi = 0;


    private Bitmap mReticle;
    private Bitmap mTarget;
    private Bitmap mLine;



    public SurfaceHolder surfaceholder;

    private Canvas canvas;

    private boolean h_result = false;
    private boolean v_result = false;




    public void setSurfaceHolder(SurfaceHolder surfaceholder) {
        this.surfaceholder = surfaceholder;

    }

    public BalanceView(Context context, float w, float h) {
        super(context);
        mContext = context;

        layoutWidth = w;
        layoutHeight = h;
        rollingTag = layoutHeight/2;

        mReticle = BitmapFactory.decodeResource(getResources(), R.mipmap.cv_reticle);
        mTarget = BitmapFactory.decodeResource(getResources(), R.mipmap.cv_target);

        //设置缩小比例
        //产生resize后的Bitmap对象
        Matrix matrix=new Matrix();
        matrix.postScale(0.5f, 0.5f);
        mReticle = Bitmap.createBitmap(mReticle, 0, 0, mReticle.getWidth(), mReticle.getHeight(), matrix, true);

        Matrix matrix2=new Matrix();
        matrix2.postScale(0.2f, 0.2f);
        mTarget = Bitmap.createBitmap(mTarget, 0, 0, mTarget.getWidth(), mTarget.getHeight(), matrix2, true);

        //测试线长度
        lineLength = layoutWidth/4;

        //计算测试线位置
        testRadian = (float)(testAngle * (Math.PI/180));
        testHeight = (float) (Math.sin(testRadian) * lineLength);
        testWidth = (float) (Math.cos(testRadian) * lineLength);

    }

    public synchronized void colseThread() {
        if (!isblool) {
            isblool = true;
        }
    }


    public BalanceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            mContext = context;
    }

    /**
     *
     * @param z 控制前后
     * @param y 控制左右
     */
    public synchronized void updateBubble(float z, float y) {
        if (z > 0 && (z - rotaErrorSize) > 0) {//向前倾斜
            this.posi = z - rotaErrorSize;
        } else if (z < 0 && (z + rotaErrorSize) < 0) {//向后倾斜
            this.posi = z + rotaErrorSize;
        } else {//正常
            this.posi = 0;
        }


        if (y < 0) {//向左倾斜,
            mAngle = 90 * (y/10);
        } else if (y > 0) {//向右倾斜
            mAngle = 90 * (y/10);
        }
    }


        public void doDraw() {
            if (surfaceholder != null) try {
                canvas = surfaceholder.lockCanvas();

                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                Paint paint = new Paint();
                paint.setColor(getResources().getColor(R.color.widget_option_active));

                computeBallHeight();

                Rect rect = new Rect((int)(layoutWidth/2)-(mReticle.getWidth()/2), (int) ((layoutHeight/2)-(mReticle.getHeight()/2)), (int)(layoutWidth/2)+(mReticle.getWidth()/2), (int) ((layoutHeight/2)+(mReticle.getHeight()/2)));

                Rect rect1 = new Rect((int)(layoutWidth/2)-20, (int)(rollingTag)-20, (int)(layoutWidth/2)+20, (int)(rollingTag)+20);

                canvas.drawBitmap(mReticle, (layoutWidth / 2) - (mReticle.getWidth() / 2), (layoutHeight / 2) - (mReticle.getHeight() / 2), paint);

                if (rect.contains(rect1)) {//包含
                    v_result = true;
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle((layoutWidth/2),rollingTag, 20, paint);//中心，半径为20
                } else {
                    v_result = false;
                    paint.setColor(getResources().getColor(R.color.widget_option_active));
                    canvas.drawCircle((layoutWidth/2),rollingTag, 20, paint);//中心，半径为20
                }


                //横向测试线水平
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(4.0f);

                canvas.drawLine(layoutWidth / 2, layoutHeight / 2, layoutWidth / 2 + testWidth, layoutHeight / 2 - testHeight, paint);
                canvas.drawLine(layoutWidth / 2, layoutHeight / 2, layoutWidth / 2 - testWidth, layoutHeight / 2 + testHeight, paint);
                canvas.drawLine(layoutWidth / 2, layoutHeight / 2 , layoutWidth / 2 + testWidth, layoutHeight / 2 + testHeight, paint);
                canvas.drawLine(layoutWidth / 2, layoutHeight / 2 , layoutWidth / 2 - testWidth, layoutHeight / 2 - testHeight, paint);
                //结果
                float mRadian = (float)(mAngle * (Math.PI/180));

                float lineHeight = (float) (Math.sin(mRadian) * lineLength);
                float lineWeidth = (float) (Math.cos(mRadian) * lineLength);


                //横向角度检测
                Rect testR = new Rect(0, (int)(layoutHeight / 2 - testHeight), (int)(layoutWidth), (int)(layoutHeight / 2 + testHeight));

                Rect resR;
                if (lineHeight >=0 ) {
                    resR = new Rect((int)(layoutWidth / 2), (int)(layoutHeight / 2 - lineHeight), (int)(layoutWidth / 2 + lineWeidth), (int)(layoutHeight/2));
                } else {
                    resR = new Rect((int)(layoutWidth / 2), (int)(layoutHeight / 2), (int)(layoutWidth / 2 + lineWeidth), (int)(layoutHeight/2-lineHeight));
                }


                if (testR.contains(resR)) {
                    h_result = true;
                    paint.setColor(Color.GREEN);
                } else {
                    h_result = false;
                    paint.setColor(getResources().getColor(R.color.widget_option_active));
                }

                canvas.drawLine(layoutWidth / 2, layoutHeight / 2, layoutWidth / 2 + lineWeidth, layoutHeight / 2-lineHeight, paint);

                canvas.drawLine(layoutWidth / 2, layoutHeight / 2, layoutWidth / 2 - lineWeidth, layoutHeight / 2+lineHeight, paint);

            } catch (Exception e) {

            } finally {
                if (canvas != null)
                    surfaceholder.unlockCanvasAndPost(canvas);
            }
        }


    private void computeBallHeight() {

        if (posi > 0 ) {//上移动-10 ~ 0
            if (rollingTag > 60) {
                rollingTag = rollingTag - 20;
            }
        } else if (posi < 0) {//下移
            if (rollingTag < layoutHeight - 60) {
                rollingTag = rollingTag + 20;
            }

        } else {//中间滚动
            if (rollingTag < ((layoutHeight/2)-20 )) {//快速向中心靠拢
                rollingTag = rollingTag+20;
            } else if (rollingTag > ((layoutHeight/2)+20)) {
                rollingTag = rollingTag-20;
            } else {//有效区域(让他有个波动效果)
                if (rollingTag < (layoutHeight/2)) {
                    rollingTag+=2;
                } else if (rollingTag > (layoutHeight/2)) {
                    rollingTag-=2;
                }
            }
        }
    }





    @Override
    public void run() {
        while (!isblool) {
            doDraw();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    //返回水平是否满足
    public synchronized boolean isHorizontal() {
       return h_result;
    }

    //返回垂直是否满足
    public synchronized boolean isVertical() {
       return v_result;
    }



}
