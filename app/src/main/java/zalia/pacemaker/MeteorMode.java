package zalia.pacemaker;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.R.attr.thumb;
import static zalia.pacemaker.MainActivity.get_progress_respecting_range;
import static zalia.pacemaker.MainActivity.normalize_progress;

/**
 * Created by Zalia on 18.03.2017.
 */

public class MeteorMode extends ColorPickerMode {

    private static final int MIN_SPEED = 1000;
    private static final int MAX_SPEED = 10;
    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 100;

    private SeekBar speed_bar;
    private SeekBar length_bar;
    private AppCompatCheckBox split_box;

    //default settings
    private int speed = 400;
    private int length = 40;
    private String split = "split:";
    //change default color in onCreateView!

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
//        current_color = Color.rgb(255, 255, 255);
        return inflater.inflate(R.layout.meteor_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //register speed seekbar
        speed_bar = (SeekBar) view.findViewById(R.id.meteor_speed_slider);
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
        speed_bar.setProgress(get_progress_respecting_range(speed, MIN_SPEED, MAX_SPEED));

        //register length seekbar
        length_bar = (SeekBar) view.findViewById(R.id.meteor_length_slider);
        length_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                length = (int) Math.round(normalize_progress(progress, MIN_LENGTH, MAX_LENGTH));
                send_configs();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        length_bar.setProgress(get_progress_respecting_range(length, MIN_LENGTH, MAX_LENGTH));

        //register split checkbox
        split_box = (AppCompatCheckBox) view.findViewById(R.id.split_checkbox);
        split_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split = ((AppCompatCheckBox)v).isChecked() ? "split:" : "";
                send_configs();
            }
        });
        if(split.equals("split:")){
            split_box.setChecked(true);
        }

        change_background(current_color);
    }

    @Override
    public void change_background(int color) {
        super.change_background(color);

        //change color of ui elements if background is too dark/light
        int ui_element_color = ((MainActivity)getActivity()).find_viable_ui_color(color);

        //change colors of progressbar texts
        TextView speed_text = (TextView) this.getView().findViewById(R.id.meteor_speed_text);
        speed_text.setTextColor(ui_element_color);
        TextView length_text = (TextView) this.getView().findViewById(R.id.meteor_length_text);
        length_text.setTextColor(ui_element_color);

        //change colors of progressbars
        speed_bar.getProgressDrawable().setColorFilter(ui_element_color, PorterDuff.Mode.SRC_IN);
        speed_bar.getThumb().setColorFilter(ui_element_color, PorterDuff.Mode.SRC_IN);
        length_bar.getProgressDrawable().setColorFilter(ui_element_color, PorterDuff.Mode.SRC_IN);
        length_bar.getThumb().setColorFilter(ui_element_color, PorterDuff.Mode.SRC_IN);

        //change colors of checkboxes
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },
                new int[] {
                        ui_element_color,
                        ui_element_color
                }
        );
        split_box.setTextColor(ui_element_color);
        split_box.setSupportButtonTintList(colorStateList);
    }

    //currently does NOT include color and random state!
    public void send_configs(){
        ((MainActivity)getActivity()).send_config("split:meteor:" + speed + " " + length + " " + getRGB() + "\n");
    }

}
