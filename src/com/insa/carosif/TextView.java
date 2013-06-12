package com.insa.carosif;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView{

	public TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/r2014.ttf"));
	}
	
}
