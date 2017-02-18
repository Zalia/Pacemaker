package zalia.pacemaker;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private SeekBar speed_bar;
    private SeekBar brightness_bar;
    private SeekBar rainbow_bar;
    private int speed, brightness, rainbowness;

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

        change_background(Color.WHITE);

        //setup listeners
        speed_bar = (SeekBar) view.findViewById(R.id.rainbow_speed_slider);
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

        brightness_bar = (SeekBar) view.findViewById(R.id.rainbow_brightness_slider);
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

        rainbow_bar = (SeekBar) view.findViewById(R.id.rainbow_slider);
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
    }

    private void change_background(int color){
        ((MainActivity)getActivity()).change_background(color);
    }

    public String generate_configs() {
        return "rainbow:" + speed + " " + rainbowness;
    }
}
