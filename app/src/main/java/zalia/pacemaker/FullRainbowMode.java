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
import static zalia.pacemaker.MainActivity.get_progress_respecting_range;
import static zalia.pacemaker.MainActivity.normalize_progress;

/**
 * Created by Zalia on 17.02.2017.
 */

public class FullRainbowMode extends PacemakerMode {

    private final int ID = RAINBOW;

    private static final int MIN_SPEED = 1000;
    private static final int MAX_SPEED = 50;
    private static final int MIN_BRIGHTNESS = 0;
    private static final int MAX_BRIGHTNESS = 1;
    private static final int MIN_RAINBOWNESS = 0;
    private static final int MAX_RAINBOWNESS = 5;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.full_rainbow_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        //get configs if present
        load_configs(((MainActivity)getActivity()).get_config(ID));

        //setup listeners
        speed_bar = (SeekBar) view.findViewById(R.id.rainbow_speed_slider);
        speed_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = (int) Math.round(normalize_progress(progress, MIN_SPEED, MAX_SPEED));
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
        brightness_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = normalize_progress(progress, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
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
        rainbow_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rainbowness = (int) Math.round(MainActivity.normalize_progress(progress, MIN_RAINBOWNESS, MAX_RAINBOWNESS));
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
        speed_bar.setProgress(get_progress_respecting_range(speed, MIN_SPEED, MAX_SPEED));
        brightness_bar.setProgress(get_progress_respecting_range(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS));
        rainbow_bar.setProgress(get_progress_respecting_range(rainbowness, MIN_RAINBOWNESS, MAX_RAINBOWNESS));
        if(split.equals("split:")){
            split_box.setChecked(true);
            split_box.jumpDrawablesToCurrentState();
            Log.d("FCM", "setting split checked: " + split_box.isChecked());
        }
        change_background();
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    private void change_background(){
        //draw gui elements white
        speed_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        speed_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brightness_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brightness_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        rainbow_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        rainbow_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        split_box.setTextColor(Color.WHITE);
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },
                new int[] {
                        Color.WHITE,
                        Color.WHITE
                }
        );
        split_box.setSupportButtonTintList(colorStateList);

        //draw background in rainbow colors
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[] {Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED });
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackground(rainbow);
    }

    public void send_configs() {
        ((MainActivity)getActivity()).send_config(split + "rainbow:" + speed + " " + rainbowness + " " + brightness + "\n");
    }

    protected PacemakerModeConfig store_configs() {
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setDval1(brightness);
        conf.setIval1(rainbowness);
        conf.setIval2(speed);
        conf.setSval1(split);
        return conf;
    }

    protected void load_configs(PacemakerModeConfig conf){
        if(conf != null){
            this.brightness = conf.getDval1();
            this.rainbowness = conf.getIval1();
            this.speed = conf.getIval2();
            this.split = conf.getSval1();
        }
    }

}
