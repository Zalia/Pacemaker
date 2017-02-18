package zalia.pacemaker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import static zalia.pacemaker.MainActivity.normalize_progress;

/**
 * Created by Zalia on 17.02.2017.
 */

public class FullRainbowMode extends PacemakerMode {

    private static final int MIN_SPEED = 1000;
    private static final int MAX_SPEED = 10;
    private static final int MIN_BRIGHTNESS = 0;
    private static final int MAX_BRIGHTNESS = 10;
    private static final int MIN_RAINBOWNESS = 0;
    private static final int MAX_RAINBOWNESS = 10;

    private int speed, brightness, rainbowness;
    private String mirror;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.full_rainbow_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //default config
        speed = MIN_SPEED;
        brightness = MIN_BRIGHTNESS;
        rainbowness = MIN_RAINBOWNESS;
        mirror = "";

        change_background();

        //setup listeners
        SeekBar speed_bar = (SeekBar) view.findViewById(R.id.rainbow_speed_slider);
        speed_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = normalize_progress(progress, MIN_SPEED, MAX_SPEED);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        SeekBar brightness_bar = (SeekBar) view.findViewById(R.id.rainbow_brightness_slider);
        brightness_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = normalize_progress(progress, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        SeekBar rainbow_bar = (SeekBar) view.findViewById(R.id.rainbow_slider);
        rainbow_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rainbowness = MainActivity.normalize_progress(progress, MIN_RAINBOWNESS, MAX_RAINBOWNESS);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        CheckBox mirror_box = (CheckBox) view.findViewById(R.id.mirror_button);
        mirror_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mirror = isChecked ? "split:" : "";
            }
        });
    }

    private void change_background(){
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[] {Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED });
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackground(rainbow);
    }

    public String generate_configs() {
        return  mirror + "rainbow:" + speed + " " + rainbowness;
    }
}
