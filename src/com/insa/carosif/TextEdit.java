package com.insa.carosif;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class TextEdit extends EditText{

	private Rect mRect;
	private Paint mPaint;
	
	public TextEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/r2014.ttf"));
		this.setTextColor(Color.WHITE);
		
	}
	
}
