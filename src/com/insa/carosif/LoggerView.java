package com.insa.carosif;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LoggerView extends android.widget.TextView{
	
	private static int NB_CHAR = 1000;
	private static Map<Integer, LoggerView> mLogs = new HashMap<Integer, LoggerView>();
	
	public LoggerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLogs.put(getId(), this);
		super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/r2014.ttf"));
		setMovementMethod(new ScrollingMovementMethod());
	}
	
	synchronized
	public static void error(String module, String msg, int idLog) {
		LoggerView log = mLogs.get(idLog);
		if(log != null) {
			clean(log);
			log.append(Html.fromHtml("<font color='red'>-|" + module + ": " + msg + "</font><br/>"));
			focus(log);
		}
	}
	
	synchronized
	public static void info(String module, String msg, int idLog) {
		LoggerView log = mLogs.get(idLog);
		if(log != null) {
			clean(log);
			//log.setTextColor(log.getResources().getColor(R.color.yellow));
			log.append(Html.fromHtml("<font color='yellow'>-|" + module + ": " + msg + "</font><br/>"));
			focus(log);
		}
	}
	
	synchronized
	public static void messageSent(String msg, int idLog) {
		LoggerView log = mLogs.get(idLog);
		if(log != null) {
			clean(log);
			//log.setTextColor(log.getResources().getColor(R.color.blue));
			log.append(Html.fromHtml("<font color='#00A2FF'>-|SENT: " + msg + "</font><br/>"));
			focus(log);
		}
	}
	
	synchronized
	public static void messageReceived(String msg, int idLog) {
		LoggerView log = mLogs.get(idLog);
		if(log != null) {
			clean(log);
			log.append(Html.fromHtml("<font color='white'>-|RECEIVED: " + msg + "</font><br/>"));
			focus(log);
		}
	}
	
	synchronized
	private static void focus(LoggerView log) {
		final int scrollAmount = log.getLayout().getLineTop(log.getLineCount())
	            -log.getHeight();
	    // if there is no need to scroll, scrollAmount will be <=0
	    if(scrollAmount>0)
	        log.scrollTo(0, scrollAmount);
	    else
	        log.scrollTo(0,0);
	}
	
	synchronized
	private static void clean(LoggerView log) {
		CharSequence rest = log.getText();
		int length = rest.length();
		if(length>NB_CHAR) {
			String test = Html.toHtml((Spanned)rest).substring(length-NB_CHAR, length-1);
			int index = test.toString().indexOf("</p>", 0);
			test = test.substring(index+4);
			log.setText(Html.fromHtml(test));
		}
		focus(log);
	}
	
}
