package com.insa.carosif;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class SpeedMeter extends View{

	private Bitmap mBase = BitmapFactory.decodeResource(getResources(), R.drawable.speed_base);
	private Bitmap mArrow = BitmapFactory.decodeResource(getResources(), R.drawable.speed_arrow);
	private Bitmap mCenterWheel = BitmapFactory.decodeResource(getResources(), R.drawable.speed_center_wheel);
	
	private Paint mPaint;
	
	public SpeedMeter(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setDither(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.scale(0.7f, 0.7f);
		canvas.drawBitmap(mBase, 0, 0, mPaint);
		Matrix m = new Matrix();
		m.setRotate(40, (float)getWidth()/2, (float)getWidth()/2);
		canvas.drawBitmap(mArrow, m, mPaint);
		canvas.drawBitmap(mCenterWheel, 0, 0, mPaint);

		canvas.restore();
		super.onDraw(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(400, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(400, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
