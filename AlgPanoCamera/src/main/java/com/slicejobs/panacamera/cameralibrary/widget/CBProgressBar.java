package com.slicejobs.panacamera.cameralibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.slicejobs.panacamera.R;

public class CBProgressBar extends View {
    private static final int STYLE_HORIZONTAL = 0;
    private static final int STYLE_ROUND = 1;
    private static final int STYLE_SECTOR = 2;
    private int strokeWidth;
    private int centerX;
    private int centerY;
    private int percenttextsize;
    private int percenttextcolor;
    private int progressBarBgColor;
    private int progressColor;
    private int sectorColor;
    private int unSweepColor;
    private int orientation;
    private int radius;
    private int max;
    private double progress;
    private String progressText;
    private boolean isHorizonStroke;
    private int rectRound;
    private boolean showPercentSign;
    private Paint mPaint;

    public CBProgressBar(Context context) {
        this(context, (AttributeSet)null);
    }

    public CBProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CBProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.strokeWidth = 10;
        this.percenttextsize = 18;
        this.percenttextcolor = -16737587;
        this.progressBarBgColor = -10263709;
        this.progressColor = -16726579;
        this.sectorColor = -1426063361;
        this.unSweepColor = -1436656034;
        this.orientation = 0;
        this.radius = 30;
        this.max = 100;
        this.progress = 0.0D;
        this.progressText = "";
        this.rectRound = 5;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.cbprogressbar);
        this.percenttextcolor = array.getColor(R.styleable.cbprogressbar_percent_text_color, this.percenttextcolor);
        this.progressBarBgColor = array.getColor(R.styleable.cbprogressbar_progressBarBgColor, this.progressBarBgColor);
        this.progressColor = array.getColor(R.styleable.cbprogressbar_progressColor, this.progressColor);
        this.sectorColor = array.getColor(R.styleable.cbprogressbar_sectorColor, this.sectorColor);
        this.unSweepColor = array.getColor(R.styleable.cbprogressbar_unSweepColor, this.unSweepColor);
        this.percenttextsize = (int)array.getDimension(R.styleable.cbprogressbar_percent_text_size, (float)this.percenttextsize);
        this.strokeWidth = (int)array.getDimension(R.styleable.cbprogressbar_stroke_width, (float)this.strokeWidth);
        this.rectRound = (int)array.getDimension(R.styleable.cbprogressbar_rect_round, (float)this.rectRound);
        this.orientation = array.getInteger(R.styleable.cbprogressbar_orientation, 0);
        this.isHorizonStroke = array.getBoolean(R.styleable.cbprogressbar_isHorizonStroke, false);
        this.showPercentSign = array.getBoolean(R.styleable.cbprogressbar_showPercentSign, true);
        array.recycle();
        this.mPaint = new Paint(1);
    }

    protected void onDraw(Canvas canvas) {
        this.centerX = this.getWidth() / 2;
        this.centerY = this.getHeight() / 2;
        this.radius = this.centerX - this.strokeWidth / 2;
        if (this.orientation == 0) {
            this.drawHoriRectProgressBar(canvas, this.mPaint);
        } else if (this.orientation == 1) {
            this.drawRoundProgressBar(canvas, this.mPaint);
        } else {
            this.drawSectorProgressBar(canvas, this.mPaint);
        }

    }

    private void drawRoundProgressBar(Canvas canvas, Paint piant) {
        piant.setColor(this.progressBarBgColor);
        piant.setStyle(Paint.Style.STROKE);
        piant.setStrokeWidth((float)this.strokeWidth);
        canvas.drawCircle((float)this.centerX, (float)this.centerY, (float)this.radius, piant);
        piant.setColor(this.progressColor);
        piant.setStyle(Paint.Style.STROKE);
        piant.setStrokeWidth((float)this.strokeWidth);
        RectF oval = new RectF((float)(this.centerX - this.radius), (float)(this.centerY - this.radius), (float)(this.radius + this.centerX), (float)(this.radius + this.centerY));
        canvas.drawArc(oval, -90.0F, (float)(360.0D * this.progress / (double)this.max), false, piant);
        piant.setStyle(Paint.Style.FILL);
        piant.setColor(this.percenttextcolor);
        piant.setTextSize((float)this.percenttextsize);
        String percent = (int)(this.progress * 100.0D / (double)this.max) + "%";
        Rect rect = new Rect();
        piant.getTextBounds(percent, 0, percent.length(), rect);
        float textWidth = (float)rect.width();
        float textHeight = (float)rect.height();
        if (textWidth >= (float)(this.radius * 2)) {
            textWidth = (float)(this.radius * 2);
        }

        Paint.FontMetrics metrics = piant.getFontMetrics();
        float baseline = ((float)this.getMeasuredHeight() - metrics.bottom + metrics.top) / 2.0F - metrics.top;
        canvas.drawText(percent, (float)this.centerX - textWidth / 2.0F, baseline, piant);
    }

    private void drawHoriRectProgressBar(Canvas canvas, Paint piant) {
        piant.setColor(this.progressBarBgColor);
        if (this.isHorizonStroke) {
            piant.setStyle(Paint.Style.STROKE);
            piant.setStrokeWidth(1.0F);
        } else {
            piant.setStyle(Paint.Style.FILL);
        }

        canvas.drawRoundRect(new RectF((float)(this.centerX - this.getWidth() / 2), (float)(this.centerY - this.getHeight() / 2), (float)(this.centerX + this.getWidth() / 2), (float)(this.centerY + this.getHeight() / 2)), (float)this.rectRound, (float)this.rectRound, piant);
        piant.setStyle(Paint.Style.FILL);
        piant.setColor(this.progressColor);
        if (this.isHorizonStroke) {
            canvas.drawRoundRect(new RectF((float)(this.centerX - this.getWidth() / 2), (float)(this.centerY - this.getHeight() / 2), (float)(this.progress * 100.0D / (double)this.max * (double)this.getWidth() / 100.0D), (float)(this.centerY + this.getHeight() / 2)), (float)this.rectRound, (float)this.rectRound, piant);
        } else {
            piant.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawRoundRect(new RectF((float)(this.centerX - this.getWidth() / 2), (float)(this.centerY - this.getHeight() / 2), (float)(this.progress * 100.0D / (double)this.max * (double)this.getWidth() / 100.0D), (float)(this.centerY + this.getHeight() / 2)), (float)this.rectRound, (float)this.rectRound, piant);
            piant.setXfermode((Xfermode)null);
        }

        piant.setStyle(Paint.Style.FILL);
        piant.setColor(this.percenttextcolor);
        piant.setTextSize((float)this.percenttextsize);
        String percent = (int)(this.progress * 100.0D / (double)this.max) + "%";
        Rect rect = new Rect();
        piant.getTextBounds(this.progressText, 0, percent.length(), rect);
        float textWidth = (float)rect.width();
        float textHeight = (float)rect.height();
        if (textWidth >= (float)this.getWidth()) {
            textWidth = (float)this.getWidth();
        }

        Paint.FontMetrics metrics = piant.getFontMetrics();
        float baseline = ((float)this.getMeasuredHeight() - metrics.bottom + metrics.top) / 2.0F - metrics.top;
        canvas.drawText(percent, (float)this.centerX - textWidth / 2.0F, baseline, piant);
    }

    private void drawSectorProgressBar(Canvas canvas, Paint piant) {
        piant.setColor(this.sectorColor);
        piant.setStyle(Paint.Style.STROKE);
        piant.setStrokeWidth(2.0F);
        canvas.drawCircle((float)this.centerX, (float)this.centerY, (float)this.radius, piant);
        piant.setColor(this.unSweepColor);
        piant.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float)this.centerX, (float)this.centerY, (float)(this.radius - 2), piant);
        piant.setColor(this.sectorColor);
        RectF oval = new RectF((float)(this.centerX - this.radius + 2), (float)(this.centerY - this.radius + 2), (float)(this.radius + this.centerX - 2), (float)(this.radius + this.centerY - 2));
        canvas.drawArc(oval, -90.0F, (float)(360.0D * this.progress / (double)this.max), true, piant);
    }

    public void setProgress(double progress) {
        this.progressText = this.progressText;
        if (progress > (double)this.max) {
            progress = (double)this.max;
        } else {
            this.progress = progress;
            this.postInvalidate();
        }

    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getPercenttextsize() {
        return this.percenttextsize;
    }

    public void setPercenttextsize(int percenttextsize) {
        this.percenttextsize = percenttextsize;
    }

    public int getPercenttextcolor() {
        return this.percenttextcolor;
    }

    public void setPercenttextcolor(int percenttextcolor) {
        this.percenttextcolor = percenttextcolor;
    }

    public int getProgressBarBgColor() {
        return this.progressBarBgColor;
    }

    public void setProgressBarBgColor(int progressBarBgColor) {
        this.progressBarBgColor = progressBarBgColor;
    }

    public int getProgressColor() {
        return this.progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isHorizonStroke() {
        return this.isHorizonStroke;
    }

    public void setHorizonStroke(boolean isHorizonStroke) {
        this.isHorizonStroke = isHorizonStroke;
    }

    public int getRectRound() {
        return this.rectRound;
    }

    public void setRectRound(int rectRound) {
        this.rectRound = rectRound;
    }

    public int getMax() {
        return this.max;
    }

    public double getProgress() {
        return this.progress;
    }
}
