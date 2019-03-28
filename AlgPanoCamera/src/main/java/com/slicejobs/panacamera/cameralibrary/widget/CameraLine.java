package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.slicejobs.panacamera.R;
import com.slicejobs.panacamera.cameralibrary.helper.SystemUtil;

public class CameraLine extends View {
    private Paint mLinePaint;
    private Paint mLineCrossPaint;
    final int LINE_STYLE_HIDE_LINE = 0;
    final int LINE_STYLE_SHOW_LINES = 1;
    final int LINE_STYLE_SHOW_LINES_LINE_IS_WIDE = 2;
    int nowLineStyle = 0;
    boolean showLines;
    boolean lineIsWide = false;
    float crossLineLength = 0.0F;

    public CameraLine(Context context) {
        super(context);
        Log.i("XXX", "louis=xx:CameraLine(Context context)");
        this.init();
    }

    public CameraLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("XXX", "louis=xx:CameraLine(Context context, AttributeSet attrs)");
        this.getAttrsAndInit(context, attrs);
    }

    public CameraLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("XXX", "louis=xx:CameraLine(Context context, AttributeSet attrs, int defStyleAttr)");
        this.getAttrsAndInit(context, attrs);
    }

    private void getAttrsAndInit(Context context, AttributeSet attrs) {
        this.init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CameraLine_Attrs);
        boolean lineIsWide = typedArray.getBoolean(R.styleable.CameraLine_Attrs_lineIsWide, false);
        int lineColor = typedArray.getInt(R.styleable.CameraLine_Attrs_lineColor, 0);
        float lineWidth = typedArray.getDimension(R.styleable.CameraLine_Attrs_lineWidth, 0.0F);
        float lineCrossLength = typedArray.getDimension(R.styleable.CameraLine_Attrs_lineCrossLength, 0.0F);
        float lineCrossWidth = typedArray.getDimension(R.styleable.CameraLine_Attrs_lineCrossWidth, 0.0F);
        int lineCrossColor = typedArray.getInt(R.styleable.CameraLine_Attrs_lineCrossColor, 0);
        boolean lineIsShow = typedArray.getBoolean(R.styleable.CameraLine_Attrs_lineIsShow, true);
        typedArray.recycle();
        Log.i("XXX", "louis=xx:lineColor:" + lineColor + "lineWidth:" + lineWidth);
        this.lineIsWide = lineIsWide;
        this.showLines = lineIsShow;
        if (lineColor != 0) {
            this.mLinePaint.setColor(lineColor);
        }

        if (lineWidth != 0.0F) {
            this.mLinePaint.setStrokeWidth(lineWidth);
        }

        if (lineCrossLength != 0.0F) {
            this.crossLineLength = (float) SystemUtil.dp2px(this.getContext(), lineCrossLength);
        }

        if (lineCrossWidth != 0.0F) {
            this.mLineCrossPaint.setStrokeWidth((float)SystemUtil.dp2px(this.getContext(), 1.0F));
        }

        if (lineCrossColor != 0) {
            this.mLineCrossPaint.setColor(lineColor);
        }

    }

    public void changeLineStyle() {
        switch(this.nowLineStyle) {
            case 0:
                this.nowLineStyle = 1;
                this.lineIsWide = true;
                this.showLines = true;
                break;
            case 1:
                this.nowLineStyle = 2;
                this.lineIsWide = false;
                this.showLines = true;
                break;
            case 2:
                this.nowLineStyle = 0;
                this.showLines = false;
        }

        this.invalidate();
    }

    private void init() {
        this.mLinePaint = new Paint();
        this.mLinePaint.setAntiAlias(true);
        this.mLinePaint.setColor(Color.parseColor("#60E0E0E0"));
        this.mLinePaint.setStrokeWidth((float)SystemUtil.dp2px(this.getContext(), 1.0F));
        this.mLineCrossPaint = new Paint();
        this.mLineCrossPaint.setColor(Color.parseColor("#55000000"));
        this.mLineCrossPaint.setStrokeWidth((float)SystemUtil.dp2px(this.getContext(), 1.0F));
    }

    protected void onDraw(Canvas canvas) {
        if (this.showLines) {
            int screenWidth = SystemUtil.getScreenWH(this.getContext()).widthPixels;
            int screenHeight = SystemUtil.getScreenWH(this.getContext()).heightPixels;
            byte lineCount;
            int width;
            int height;
            int centerLineNum;
            int j;
            if (this.lineIsWide) {
                lineCount = 3;
                width = screenWidth / (lineCount + 1);
                height = screenHeight / (lineCount + 1);
                centerLineNum = lineCount / 2;
                j = width;

                int i;
                for(i = 0; j < screenWidth && i < lineCount; ++i) {
                    if (centerLineNum != i) {
                        canvas.drawLine((float)j, 0.0F, (float)j, (float)screenHeight, this.mLinePaint);
                    }

                    j += width;
                }

                j = height;

                for(i = 0; j < screenHeight && i < lineCount; ++i) {
                    if (centerLineNum != i) {
                        canvas.drawLine(0.0F, (float)j, (float)screenWidth, (float)j, this.mLinePaint);
                    }

                    j += height;
                }
            } else {
                lineCount = 2;
                width = screenWidth / (lineCount + 1);
                height = screenHeight / (lineCount + 1);
                centerLineNum = width;

                for(j = 0; centerLineNum < screenWidth && j < lineCount; ++j) {
                    canvas.drawLine((float)centerLineNum, 0.0F, (float)centerLineNum, (float)screenHeight, this.mLinePaint);
                    centerLineNum += width;
                }

                centerLineNum = height;

                for(j = 0; centerLineNum < screenHeight && j < lineCount; ++j) {
                    canvas.drawLine(0.0F, (float)centerLineNum, (float)screenWidth, (float)centerLineNum, this.mLinePaint);
                    centerLineNum += height;
                }
            }

            if (this.crossLineLength != 0.0F) {
                float centerX = (float)(canvas.getWidth() / 2);
                float centerY = (float)(canvas.getHeight() / 2);
                canvas.drawLine(centerX - this.crossLineLength, centerY, centerX + this.crossLineLength, centerY, this.mLineCrossPaint);
                canvas.drawLine(centerX, centerY - this.crossLineLength, centerX, centerY + this.crossLineLength, this.mLineCrossPaint);
            }
        }

    }
}
