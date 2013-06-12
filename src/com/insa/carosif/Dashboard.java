package com.insa.carosif;

import com.insa.carosif.Joystick.OnStateChangeListener;
import com.insa.carosif.TCPClient.OnMessageReceived;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;


public class Dashboard extends Activity {

	public static TCPClient mTcpClient;
	
	//private int mTemp = 0;
	//private int mSpeed = 0;
	//private int mAngle = 0;
	
	private float mSpeed = 0;
	private float mTemp = 0;
	private float mAngle = 0;
	
	private ImageView mSpeedArrow;
	private ImageView mTempArrow;
	private ImageView mCompassArrow;
	private Joystick mJoystick;
	
	private Animation mSpeedRotate;
	private Animation mTempRotate;
	private Animation mCompassRotate;
	
	private TextView mSpeedText;
	private TextView mTempText;
	private TextView mCompassText;
	private TextView mObstacleText;
	
	
	private boolean test = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		mTcpClient = Home.mTcpClient;
		
		init();
		
		if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE) {
			initUI();
		}
		
		mJoystick = (Joystick)findViewById(R.id.joystick);
		mJoystick.setOnStateChangeListener(new OnStateChangeListener() {
			
			@Override
			public void onStateChange(String state) {
				mTcpClient.sendMessage(state);
				LoggerView.messageSent(state, R.id.logger);
			}
		});
		
		mTcpClient.setOnMessageReceived(new OnMessageReceived() {

			@Override
			public void messageReceived(String message) {
				parseFrame(message);
				final String mess = message; 
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						LoggerView.messageReceived(mess, R.id.logger);
					}
				});
			}

			@Override
			public void changeState(int state) {
				
			}
		
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}
	
	public void init() {
		mSpeedText = (TextView)findViewById(R.id.speed_text);
		mTempText = (TextView)findViewById(R.id.temp_text);
		mCompassText = (TextView)findViewById(R.id.compass_text);
		mObstacleText = (TextView)findViewById(R.id.obstacle);
	}
	
	public void initUI() {
		mCompassArrow = (ImageView)findViewById(R.id.compass_arrow);
		mSpeedArrow = (ImageView)findViewById(R.id.speed_arrow);
		mTempArrow = (ImageView)findViewById(R.id.temp_arrow);
	}
	
	public void demo(int state) {
		
	}
	
	
	private void parseFrame(String frame) {
		final float angle, temp, distance, speed;
		
		try {
			int index = frame.lastIndexOf("Angle : ");
			angle = Float.parseFloat(frame.substring(index+8, index+8+6));
			
			index = frame.lastIndexOf("Temperature : ");
			temp = Float.parseFloat(frame.substring(index+14, index+14+5));
			
			index = frame.lastIndexOf("Distance : ");
			distance = Float.parseFloat(frame.substring(index+11, index+11+3));
	
			index = frame.lastIndexOf("Speed : ");
			speed = Float.parseFloat(frame.substring(index+8, index+8+4));
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					displayFeedback(angle, temp, distance, speed);
				}
			});
			
		}catch(Exception e) {
			Log.e("Parse", e.getMessage());
		}
		
		
		/*for(int i=0; i<info.length; i++) {
			if(info[i].contains("Angle")) {
				angle = Float.parseFloat(info[i].split(":")[1].trim());
			}
			else if(info[i].contains("Temperature")) {
				temp = Float.parseFloat(info[i].split(":")[1].trim());
			}
			else if(info[i].contains("Distance")) {
				distance = Float.parseFloat(info[i].split(":")[1].trim());
			}
			else if(info[i].contains("Speed")) {
				speed = Float.parseFloat(info[i].split(":")[1].trim());
			}
		}*/
		//displayFeedback(angle, temp, distance, speed);
	}

	public void displayFeedback(float angle, float temp, float distance, float speed) {
		mSpeedText.setText((int)speed+"");
		mTempText.setText((int)temp+"¡C");
		mObstacleText.setText(Html.fromHtml("Closest obstacle at <font color='red'>" + distance  + "</font> mm"));
		String northAngle ="";
		if(angle < 45) {
			northAngle = (int)angle+"¡N";
		}
		else if(angle >= 315) {
			northAngle = (int)angle-360+"¡N";
		}
		else if(angle >= 45 && angle < 135) {
			northAngle = (int)angle-90+"¡E";
		}
		else if(angle >= 135 && angle < 225) {
			northAngle = (int)angle-180+"¡S";
		}
		else if(angle >= 225 && angle < 315) {
			northAngle = (int)angle-270+"¡O";
		}
		mCompassText.setText(northAngle);
		
		if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE) {
			setSpeed(speed);
			setTemperature(temp);
			setCompass(angle);
		}
	}
	
	public void setTemperature(float temp) {
		temp = temp*4 - 120f;
		final float fromDegrees = mTemp;
		final float toDegrees = temp;
		mTempRotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.52f, Animation.RELATIVE_TO_SELF, 0.53f){
			protected void applyTransformation(float interpolatedTime,Transformation t) {
				mTemp = fromDegrees+(toDegrees-fromDegrees)*interpolatedTime;
                super.applyTransformation(interpolatedTime, t);
            }
        };
        mTempRotate.setDuration(Math.abs((int)(mTemp-toDegrees)));
        mTempRotate.setFillAfter(true);
        mTempArrow.startAnimation(mTempRotate);
	}
	
	private void setSpeed(float speed) {
		//angleMax - angleMin = 227 - 0 => 1cm/s = 1.185¡
		speed = speed*1.261f;
		final float fromDegrees = mSpeed;
		final float toDegrees = speed;
		mSpeedRotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.55f){
			protected void applyTransformation(float interpolatedTime,Transformation t) {
				mSpeed = fromDegrees+(toDegrees-fromDegrees)*interpolatedTime;
                super.applyTransformation(interpolatedTime, t);
            }
        };
        mSpeedRotate.setDuration(Math.abs((int)(mSpeed-toDegrees)));
        mSpeedRotate.setFillAfter(true);
        mSpeedArrow.startAnimation(mSpeedRotate);
	}
	
	private void setCompass(float angle) {
		final float fromDegrees = mAngle;
		final float toDegrees = angle;
		mCompassRotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.51f, Animation.RELATIVE_TO_SELF, 0.5f){
			protected void applyTransformation(float interpolatedTime,Transformation t) {
				mAngle = fromDegrees+(toDegrees-fromDegrees)*interpolatedTime;
                super.applyTransformation(interpolatedTime, t);
            }
        };
        mCompassRotate.setDuration(Math.abs((int)(mAngle-toDegrees)));
        mCompassRotate.setFillAfter(true);
        mCompassArrow.startAnimation(mCompassRotate);
	}
	
}
