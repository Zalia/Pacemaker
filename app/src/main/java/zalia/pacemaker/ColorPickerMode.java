package zalia.pacemaker;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.slider.LightnessSlider;

/**
 * Created by Zalia on 17.02.2017.
 */

public class ColorPickerMode extends PacemakerMode {

    private ColorPickerView colorPickerView;
    private LightnessSlider lightness_slider;
    private View root;
    private int current_color;
    private boolean initialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.color_picker_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        if(!initialized) {
            //default settings
            current_color = Color.WHITE;
            change_background(current_color);

            //setup listeners
            colorPickerView = (ColorPickerView) view.findViewById(R.id.color_picker_view);
            lightness_slider = (LightnessSlider) view.findViewById(R.id.color_picker_lightness);
            colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
                @Override
                public void onColorChanged(int selectedColor) {
                    current_color = selectedColor;
                    change_background(selectedColor);
                    send_configs();
                }
            });
            colorPickerView.setLightnessSlider(lightness_slider);
            lightness_slider.setColorPicker(colorPickerView);
            lightness_slider.setColor(colorPickerView.getSelectedColor());
            initialized = true;

            //LinearLayout colorPickerWrapper = (LinearLayout) view.findViewById(R.id.color_picker_wrapper);
            //colorPickerWrapper.setBackgroundResource(R.drawable.rounded_corners_background);
        } else{
            change_background(current_color);
        }
    }

    protected void change_background(int color){
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(color);
    }

    @Override
    public void send_configs() {
        ((MainActivity)getActivity()).send_config("constant: " + current_color + "\n");
    }

}