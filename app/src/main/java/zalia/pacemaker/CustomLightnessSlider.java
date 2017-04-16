package zalia.pacemaker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.flask.colorpicker.slider.LightnessSlider;

/**
 * Created by Zalia on 16.04.2017.
 */

public class CustomLightnessSlider extends LightnessSlider {

    public CustomLightnessSlider(Context context){
        super(context);
    }

    public CustomLightnessSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN){
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }
}
