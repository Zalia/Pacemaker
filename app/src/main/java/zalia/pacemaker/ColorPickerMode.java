package zalia.pacemaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;
import com.flask.colorpicker.slider.OnValueChangedListener;

/**
 * Created by Zalia on 17.02.2017.
 */

public class ColorPickerMode extends PacemakerMode {

    protected ColorPickerView colorPickerView;
    protected LightnessSlider lightness_slider;
    private View root;
    private boolean initialized = false;

    //default settings
    protected int current_color = Color.rgb(204,65,36);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.color_picker_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
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
        final ViewTreeObserver lightness_observer = lightness_slider.getViewTreeObserver();
        lightness_observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lightness_slider.setColor(current_color);
                Log.d("CPM", "onGlobalLayout() call");
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        change_background(current_color);
    }

    protected void change_background(int color){
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(color);
    }

    protected String getRGB() {
        return Color.red(current_color) + " " + Color.green(current_color) + " " + Color.blue(current_color);
    }

    @Override
    public void send_configs() {
        ((MainActivity)getActivity()).send_config("constant: " + getRGB() + "\n");
    }
}