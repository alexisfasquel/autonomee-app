package com.insa.carosif;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



public class Joystick extends View {

	
	private Bitmap button = BitmapFactory.decodeResource(getResources(), R.drawable.direction_button);
	private Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.direction_base);
	private double mDx = 0;
	private double mDy = 0;
	private int mOldX;
	private int mOldY;
	
	private int mCenterBackground;
	private int mCenterButton;
	
	private OnStateChangeListener mListener;
	
	private String mAction = STOP;
	
	public static final String GO_FORWARD = "01#000000#000000";
	public static final String GO_BACKWARD = "-1#000000#000000";
	public static final String GO_RIGHT = "02#000000#000000";
	public static final String GO_LEFT = "-2#000000#000000";
	public static final String STOP = "00#000000#000000";
	
	public Joystick(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					mOldX = (int) event.getX();
					mOldY = (int) event.getY();
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE) {
					int newX = (int) event.getX();
					int newY = (int) event.getY();
					  
					mDx = newX - mOldX;
					mDy = newY - mOldY;
					
					String action = STOP;
					String logAction = "";
					double angleR = Math.atan2(mDx, mDy);
					//After some observation ^^
					//If the angle is between 45¡ et -45¡ were hitting bottom
					if(angleR < 0.7853d && angleR > -0.7853d) {
						action = GO_BACKWARD;
						logAction = "DOWN";
					}
					//If the angle is between -45¡ et -135¡ were hitting left
					else if(angleR < -0.7853d && angleR > -2.3561d) {
						action = GO_LEFT;
						logAction = "LEFT";
					}
					//If the angle is between 45 et 135¡ were hitting right
					else if(angleR > 0.7853d && angleR < 2.3561d) {
						action = GO_RIGHT;
						logAction = "RIGHT";
					}
					//If the angle is between -135¡ et 135¡ were hitting top
					else if(angleR < -2.3561d || angleR > 2.3561d) {
						action = GO_FORWARD;
						logAction = "UP";
					}
					//mListener.onStateChange(action);
					//Log.e("ACTION", logAction);
					
					
					if(mAction!=action && mListener != null) {
						mAction = action;
						mListener.onStateChange(mAction);
						LoggerView.info("JOYSTICK", logAction, R.id.logger);
					}
					
					double res = mDx*mDx + mDy*mDy;
					if (Math.abs(mDy) > mCenterBackground-mCenterButton || Math.abs(mDx) > mCenterBackground-mCenterButton) {

						double angle = (double)mDx/Math.sqrt(res);
						double teta = Math.acos(angle);
						mDx = (mCenterButton) * Math.cos(teta);
						if(mDy<0) {
							mDy = (mCenterButton) * Math.sin(teta) * (-1d);
						}
						else {
							mDy = (mCenterButton) * Math.sin(teta);
						}

					}
					postInvalidate();
				}
				else if(event.getAction() == MotionEvent.ACTION_UP) {
					mDy = 0;
					mDx = 0;
					mAction = STOP;
					postInvalidate();
					LoggerView.info("JOYSTICK", "STOP", R.id.logger);
					if(mListener != null) {
						mListener.onStateChange(mAction);
					}
		        }
				else if(event.getAction() == MotionEvent.ACTION_CANCEL) {
					mDy = 0;
					mDx = 0;
					mAction = STOP;
					postInvalidate();
					LoggerView.info("JOYSTICK", "STOP", R.id.logger);
					if(mListener != null) {
						mListener.onStateChange(mAction);
					}
				}
		        return true;
			}
		});
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setFilterBitmap(true);
		p.setDither(true);
		float offsetCenter = button.getWidth()/2;
		int offsetCenterb = background.getWidth()/2;

		canvas.drawBitmap(background, 0, 0, p);
		//double teta = Math.acos(mDx/100.0);
		//Log.e("joystick", mDx+"px");
		//Log.e("joystick", teta+"¡");
		//int maxX = (int) (100 * Math.cos(teta));
		//int maxY = (int) (100 * Math.sin(teta));
		//Log.e("joystick", "MAX("+mDx+","+maxY+")");
		double res = mDx*mDx + mDy*mDy;
		float squareR = (100f-offsetCenter)*(100f-offsetCenter);

		//Dashboard.mTcpClient.sendMessage("("+mDx+","+mDy+")");
		
		/*if (res <= squareR) {
			canvas.drawBitmap(background, 100-offsetCenter+mDx, 100-offsetCenter+mDy, p);
		}
		else {*/
			
			canvas.drawBitmap(button, mCenterButton+(float)mDx, mCenterButton+(float)mDy, p);
		//}
		super.onDraw(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mCenterBackground = background.getWidth()/2;
		mCenterButton = button.getWidth()/2;
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(background.getWidth(), MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(background.getWidth(), MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setOnStateChangeListener (OnStateChangeListener listener) {
		mListener = listener;
	}
	
	public interface OnStateChangeListener {
		public void onStateChange(String state);
	}
	
}
