package zalia.pacemaker;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import static android.R.attr.id;

/**
 * Created by Zalia on 17.04.2017.
 */

public class CustomRelativeLayout extends RelativeLayout {
    public CustomRelativeLayout(Context context) {
        super(context);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean onInterceptTouchEvent(MotionEvent event) {
        return find_and_call_colorbutton(event);
    }

    public boolean onTouchEvent(MotionEvent event){
        find_and_call_colorbutton(event);
        return true;
    }

    private boolean find_and_call_colorbutton(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                float x = event.getRawX();
                float y = event.getRawY();
                View foundView = findViewAt(this, Math.round(x), Math.round(y));
                if (foundView instanceof Button && foundView != findViewById(R.id.color_picker_dialog_button)) {
                    Log.d("CM", "found color circle");
                    ((Button) foundView).callOnClick();
                    return true;
                }
                break;
        }
        return false; //foundView is not a color circle -> do not intercept
    }

    private View findViewAt(ViewGroup viewGroup, int x, int y) {
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                View foundView = findViewAt((ViewGroup) child, x, y);
                if (foundView != null && foundView.isShown()) {
                    return foundView;
                }
            } else {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(), location[1] + child.getHeight());
                if (rect.contains(x, y)) {
                    return child;
                }
            }
        }

        return null;
    }

}
