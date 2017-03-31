package zalia.pacemaker;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static zalia.pacemaker.MainActivity.FADING;
import static zalia.pacemaker.MainActivity.get_progress_respecting_range;
import static zalia.pacemaker.MainActivity.normalize_progress;

/**
 * Created by Zalia on 25.03.2017.
 */

public class FadingMode extends ColorPickerMode {

    private final int ID = FADING;

    private static final int MIN_SPEED = 1000;
    private static final int MAX_SPEED = 50;

    private SeekBar speed_bar;
    private AppCompatCheckBox heartbeat_box;

    private int speed;
    private String heartbeat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        //default settings
        this.speed = 400;
        this.heartbeat = "0";
//        current_color = Color.rgb(255, 255, 255);
        //load settings if present
        load_configs(((MainActivity)getActivity()).get_config(ID));
        return inflater.inflate(R.layout.fading_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //register speed seekbar
        speed_bar = (SeekBar) view.findViewById(R.id.fading_speed_slider);
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

        //register heartbeat checkbox
        heartbeat_box = (AppCompatCheckBox) view.findViewById(R.id.heartbeat_checkbox);
        heartbeat_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartbeat = ((AppCompatCheckBox)v).isChecked() ? "1" : "0";
                send_configs();
            }
        });
        if(heartbeat.equals("1")){
            heartbeat_box.setChecked(true);
            heartbeat_box.jumpDrawablesToCurrentState();
        }
        change_background(current_color);
    }

    @Override
    public void change_background(int color) {
        super.change_background(color);

        //change color of ui elements if background is too dark/light
        int ui_element_color = ((MainActivity) getActivity()).find_viable_ui_color(color);

        //change colors of progressbar texts
        TextView speed_text = (TextView) this.getView().findViewById(R.id.fading_speed_text);
        speed_text.setTextColor(ui_element_color);

        //change colors of progressbars
        speed_bar.getProgressDrawable().setColorFilter(ui_element_color, PorterDuff.Mode.SRC_IN);
        speed_bar.getThumb().setColorFilter(ui_element_color, PorterDuff.Mode.SRC_IN);


        //change colors of checkboxes
        heartbeat_box.setTextColor(ui_element_color);
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
        heartbeat_box.setSupportButtonTintList(colorStateList);

    }

    //currently does NOT include color and random state!
    public void send_configs(){
        ((MainActivity)getActivity()).send_config("fillcolor:" + speed + " " + this.getRGB() + " " + heartbeat + "\n");
    }

    @Override
    protected PacemakerModeConfig store_configs(){
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setColor(current_color);
        conf.setSpeed(speed);
        conf.setHeartbeat(heartbeat);
        return conf;
    }

    @Override
    protected void load_configs(PacemakerModeConfig conf){
        if(conf != null) {
            this.current_color = conf.getColor();
            this.speed = conf.getSpeed();
            this.heartbeat = conf.getHeartbeat();
        }
    }

}
