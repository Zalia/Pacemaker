package zalia.pacemaker;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import static zalia.pacemaker.MainActivity.RAINBOW;

/**
 * Created by Zalia on 17.02.2017.
 */

public class FullRainbowMode extends PacemakerMode {

    protected final int ID = RAINBOW;

    private static final int MIN_SPEED = 1000;
    private static final int MAX_SPEED = 50;
    private static final int SPEED_STEP = 50;
    private static final int MIN_BRIGHTNESS = 0;
    private static final int MAX_BRIGHTNESS = 1;
    private static final double BRIGHTNESS_STEP = 0.05;
    private static final int MIN_RAINBOWNESS = 0;
    private static final int MAX_RAINBOWNESS = 5;
    private static final int RAINBOWNESS_STEP = 1;

    //default settings
    private int speed = 400;
    private int rainbowness = 0;
    private double brightness = MAX_BRIGHTNESS;
    private String split = "";

    private SeekBar rainbow_bar;
    private SeekBar speed_bar;
    private SeekBar brightness_bar;
    private AppCompatCheckBox split_box;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.full_rainbow_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //get configs if present
        load_configs(((MainActivity) getActivity()).get_config(ID));

        //setup listeners
        speed_bar = (SeekBar) view.findViewById(R.id.rainbow_speed_slider);
        //max and min are the other way round here due to max being the smaller number
        speed_bar.setMax((MIN_SPEED - MAX_SPEED) / SPEED_STEP);
        speed_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = MIN_SPEED - progress * SPEED_STEP;
                send_configs();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        brightness_bar = (SeekBar) view.findViewById(R.id.rainbow_brightness_slider);
        brightness_bar.setMax((int) Math.round((MAX_BRIGHTNESS - MIN_BRIGHTNESS) / BRIGHTNESS_STEP));
        brightness_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = MIN_BRIGHTNESS + progress * BRIGHTNESS_STEP;
                send_configs();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        rainbow_bar = (SeekBar) view.findViewById(R.id.rainbow_slider);
        rainbow_bar.setMax((MAX_RAINBOWNESS - MIN_RAINBOWNESS) / RAINBOWNESS_STEP);
        rainbow_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rainbowness = MIN_RAINBOWNESS + progress * RAINBOWNESS_STEP;
                send_configs();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        split_box = (AppCompatCheckBox) view.findViewById(R.id.mirror_button);
        split_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                split = isChecked ? "split:" : "";
                send_configs();
            }
        });

        //set current config
        speed_bar.setProgress((MIN_SPEED - speed) / SPEED_STEP);
        brightness_bar.setProgress((int) Math.round((brightness - MIN_BRIGHTNESS) / BRIGHTNESS_STEP));
        rainbow_bar.setProgress((rainbowness - MIN_RAINBOWNESS) / RAINBOWNESS_STEP);
        if (split.equals("split:")) {
            split_box.setChecked(true);
            split_box.jumpDrawablesToCurrentState();
            Log.d("FCM", "setting split checked: " + split_box.isChecked());
        }
        change_background();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void change_background() {
        //draw gui elements white
        speed_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        speed_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brightness_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brightness_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        rainbow_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        rainbow_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        split_box.setTextColor(Color.WHITE);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                },
                new int[]{
                        Color.WHITE,
                        Color.WHITE
                }
        );
        split_box.setSupportButtonTintList(colorStateList);

        //draw background in rainbow colors
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED});
        ((MainActivity) getActivity()).findViewById(R.id.pacemaker_layout).setBackground(rainbow);
    }

    public void send_configs() {
        ((MainActivity) getActivity()).send_config(split + "rainbow:" + speed + " " + rainbowness + " " + brightness + "\n");
    }

    protected PacemakerModeConfig store_configs() {
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setDval1(brightness);
        conf.setIval1(rainbowness);
        conf.setIval2(speed);
        conf.setSval1(split);
        return conf;
    }

    protected void load_configs(PacemakerModeConfig conf) {
        if (conf != null) {
            this.brightness = conf.getDval1();
            this.rainbowness = conf.getIval1();
            this.speed = conf.getIval2();
            this.split = conf.getSval1();
        }
    }

    protected int getID(){
        return ID;
    }

}
