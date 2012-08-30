package com.carit.imhere;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

public class PinButton extends ImageButton {

    public PinButton(Context context) {
        super(context);
        init();
    }

    public PinButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public PinButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    private void init(){
        this.setBackgroundResource(R.drawable.ic_location_big_pin);
        LayoutParams params= new LayoutParams(40,40);
        this.setLayoutParams(params);
    }

}
