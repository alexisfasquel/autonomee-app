package com.insa.carosif;

import com.insa.carosif.TCPClient.OnMessageReceived;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

public class Home extends Activity {

	public static TCPClient mTcpClient;
	
	private String mValidIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]{0,5}$";
	private TextEdit mEt;
	
	private Animation mLightOut = new AlphaAnimation(1.0f, 0.0f);
	private Animation mLightIn = new AlphaAnimation(0.0f, 1.0f);
	private View mEffect;
	
	private String mServerIp;
	private int mServerPort;
	
	private SpannableString mLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mEt = (TextEdit)findViewById(R.id.ip);
		mEt.setText("-|192.168.43.169:4242");
		
		mLightIn.setDuration(1000);
		mLightOut.setDuration(1000);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	
	public void connect(View view) {
		mEffect = view.findViewById(R.id.connexion_effect);
		mEffect.setVisibility(View.VISIBLE);

		mLightIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				mEffect.startAnimation(mLightOut);
				
			}
		});
		
		mLightOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mEffect.startAnimation(mLightIn);
			}
		});
		
		mEffect.startAnimation(mLightIn);
		
		
		
        String ip = mEt.getText().toString().substring(2);
        if(ip.matches(mValidIpAddressRegex)) {
        	String[] ips = ip.split(":");
        	mServerIp = ips[0];
        	mServerPort = Integer.parseInt(ips[1]);
        	
        	mTcpClient = new TCPClient(new OnMessageReceived() {
    			
    			@Override
    			public void messageReceived(String message) {}

				@Override
				public void changeState(int state) {
					
					if(state == TCPClient.CONNECTED) {
						
						mEt.post(new Runnable() {
							
							@Override
							public void run() {
								SpannableString str = new SpannableString(mEt.getEditableText()+"\n-|Granted!");
								str.setSpan(new ForegroundColorSpan(Color.GREEN), mEt.getText().length(), str.length(), 
							                                               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							    mEt.setText(str);	
									
							}
						});
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Intent myIntent = new Intent(getBaseContext(), Dashboard.class);
					        	startActivityForResult(myIntent, 0);
								
							}
						});
						/*Handler handler = new Handler();
			        	handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								Intent myIntent = new Intent(getBaseContext(), Dashboard.class);
					        	startActivityForResult(myIntent, 0);
								
							}
						}, 1000);*/
					}
					else if(state == TCPClient.FAILED) {
						mEt.post(new Runnable() {
							
							@Override
							public void run() {
								SpannableString str = new SpannableString(mEt.getEditableText()+"\n-|Failed");
								str.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), 
							                                               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							    mEt.setText(str);
							}
						});
					}
				}
    		}, mServerIp, mServerPort);
        	
        	mEt.post(new Runnable() {
				
				@Override
				public void run() {
					SpannableString str = new SpannableString(mEt.getEditableText()+"\n-|Connecting...");
					str.setSpan(new ForegroundColorSpan(Color.YELLOW), mEt.getText().length(), str.length(), 
				                                               Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				    mEt.setText(str);	
					//mEt.setText(Html.fromHtml(mEt.getText()+"<br/><font color='yellow'>-|Connecting...</font>"));	
				}
			});
        	
        	new Thread(mTcpClient).start();
        	
        }
	}

}
