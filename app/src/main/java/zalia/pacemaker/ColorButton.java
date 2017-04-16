package zalia.pacemaker;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import static zalia.pacemaker.R.id.color_picker_dialog_button;

/**
 * Created by Zalia on 05.04.2017.
 */

public class ColorButton extends android.support.v7.widget.AppCompatButton {

    private String id;

    public ColorButton(Context context) {
        super(context);
    }

    public ColorButton(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    protected String get_id(){
        return id;
    }

    public void change_color(int color){
        this.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
