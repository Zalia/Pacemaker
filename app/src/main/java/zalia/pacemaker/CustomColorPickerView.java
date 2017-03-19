package zalia.pacemaker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.flask.colorpicker.ColorPickerView;

/**
 * Created by Zalia on 19.03.2017.
 */

public class CustomColorPickerView extends ColorPickerView {
    public CustomColorPickerView(Context context) {
        super(context);
    }

    public CustomColorPickerView(Context context, AttributeSet attrs){
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
