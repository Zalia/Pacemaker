package zalia.pacemaker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.slider.LightnessSlider;

import static zalia.pacemaker.MainActivity.COLORPICKER;

/**
 * Created by Zalia on 17.02.2017.
 */

public class ColorPickerMode extends PacemakerMode {

    private final int ID = COLORPICKER;

    protected ColorPickerView colorPickerView;
    protected LightnessSlider lightness_slider;

    //default settings
    protected int current_color = Color.rgb(204,65,36);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //load configs if present
        load_configs(((MainActivity)getActivity()).get_config(ID));
        return inflater.inflate(R.layout.color_picker_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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
                colorPickerView.setInitialColor(current_color, false);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        send_configs();
        change_background(current_color);
    }

    protected void change_background(int color){
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(color);
    }

    protected String getRGB() {
        return Color.red(current_color) + " " + Color.green(current_color) + " " + Color.blue(current_color);
    }

    public void send_configs() {
        ((MainActivity)getActivity()).send_config("constant: " + getRGB() + "\n");
    }

    protected PacemakerModeConfig store_configs(){
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setColor(current_color);
        return conf;
    }

    protected void load_configs(PacemakerModeConfig conf){
        if(conf != null) {
            this.current_color = conf.getColor();
//            Log.d("CMP", "loading configs: " + Color.red(conf.getColor()) + " " + Color.blue(conf.getColor()) + " " + Color.green(conf.getColor()));
        }
    }
}