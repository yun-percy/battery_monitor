package com.yusun.batterymonitor;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgress extends View
{
    private CircleAttribute mCircleAttribute;
    private int mMaxProgress = 100;
    private int mSubCurProgress;

    public static final int ARC=0;
    public static final int SECTOR=1;
    public static final int ROUND=2;

    private int type=ARC;

    public CircleProgress(Context paramContext)
    {
        this(paramContext,null);
    }

    public CircleProgress(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        defaultParam();
    }

    private void defaultParam()
    {
        this.mCircleAttribute = new CircleAttribute();
        this.mMaxProgress = 100;
        this.mSubCurProgress = 0;
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
                float top=this.mCircleAttribute.mRoundOval.height()-this.mCircleAttribute.mRoundOval.height()*this.mSubCurProgress/100;
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mBottomPaint);
                canvas.save();
                RectF rectF=new RectF();
                rectF.set(this.mCircleAttribute.mRoundOval.left,top,this.mCircleAttribute.mRoundOval.right,this.mCircleAttribute.mRoundOval.bottom);
                canvas.clipRect(rectF);
                canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, true, this.mCircleAttribute.mSubPaint);
               canvas.restore();

    }

    /**
     * 设置进度
     * @param progress   取值范围0-100
     */
    public void setmSubCurProgress(int progress){
        this.mSubCurProgress=progress;
        invalidate();
    }

    /**
     *  设置圆形进度条的样式
     * @param type    ARC,SECTOR,ROUND
     */
    public void setType(int type) {
        this.type = type;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int i = View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSize(i, widthMeasureSpec), resolveSize(i, heightMeasureSpec));
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        this.mCircleAttribute.autoFix(paramInt1, paramInt2);
    }


    class CircleAttribute
    {
        public boolean mBRoundPaintsFill = true;
        public Paint mBottomPaint;
        public int mDrawPos = -90;
        public Paint mMainPaints ;
        public int mSubPaintColor = Color.parseColor("#15b609");
        public int mBottomPaintColor=Color.parseColor("#20000000");
        public int mMainPaintColor=Color.WHITE;
        public int mPaintWidth = 0;
        public RectF mRoundOval = new RectF();
        public RectF inRoundOval = new RectF();
        public int mSidePaintInterval = 8;
        public Paint mSubPaint;

        public CircleAttribute()
        {
            this.mMainPaints=new Paint();
            this.mMainPaints.setAntiAlias(true);
            this.mMainPaints.setStyle(Paint.Style.FILL);
            this.mMainPaints.setStrokeWidth(this.mPaintWidth);
            this.mMainPaints.setColor(this.mMainPaintColor);
            this.mSubPaint = new Paint();
            this.mSubPaint.setAntiAlias(true);
            this.mSubPaint.setStyle(Paint.Style.FILL);
            this.mSubPaint.setStrokeWidth(this.mPaintWidth);
            this.mSubPaint.setColor(this.mSubPaintColor);
            this.mBottomPaint = new Paint();
            this.mBottomPaint.setAntiAlias(true);
            this.mBottomPaint.setStyle(Paint.Style.FILL);
            this.mBottomPaint.setStrokeWidth(this.mPaintWidth);
            this.mBottomPaint.setColor(this.mBottomPaintColor);
        }

        public void autoFix(int width, int height)
        {
            this.inRoundOval.set(this.mPaintWidth / 2 + this.mSidePaintInterval, this.mPaintWidth / 2 + this.mSidePaintInterval, width - this.mPaintWidth / 2 - this.mSidePaintInterval, height - this.mPaintWidth / 2 - this.mSidePaintInterval);
            int left = CircleProgress.this.getPaddingLeft();
            int right = CircleProgress.this.getPaddingRight();
            int top = CircleProgress.this.getPaddingTop();
            int bottom = CircleProgress.this.getPaddingBottom();
            this.mRoundOval.set(left + this.mPaintWidth / 2, top + this.mPaintWidth / 2, width - right - this.mPaintWidth / 2, height - bottom - this.mPaintWidth / 2);
        }
    }
}