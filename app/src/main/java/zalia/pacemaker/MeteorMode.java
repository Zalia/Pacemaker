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

import static zalia.pacemaker.MainActivity.METEOR;

/**
 * Created by Zalia on 18.03.2017.
 */

public class MeteorMode extends ColorPickerMode {

    protected final int ID = METEOR;

    private static final int MIN_SPEED = 1010;
    private static final int MAX_SPEED = 10;
    private static final int SPEED_STEP = 50;
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 100;
    private static final int LENGTH_STEP = 5;

    private SeekBar speed_bar;
    private SeekBar length_bar;
    private AppCompatCheckBox split_box;

    private int speed;
    private int length;
    private String split;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //default settings
        speed = 400;
        length = 40;
        split = "split:";
//        current_color = Color.rgb(255, 255, 255);
        //load configs if present
        load_configs(((MainActivity) getActivity()).get_config(ID));
        return inflater.inflate(R.layout.meteor_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //register speed seekbar
        speed_bar = (SeekBar) view.findViewById(R.id.meteor_speed_slider);
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
        speed_bar.setProgress((MIN_SPEED - speed) / SPEED_STEP);

        //register length seekbar
        length_bar = (SeekBar) view.findViewById(R.id.meteor_length_slider);
        length_bar.setMax((MAX_LENGTH - MIN_LENGTH) / LENGTH_STEP);
        length_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                length = MIN_LENGTH + progress * LENGTH_STEP;
                send_configs();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        length_bar.setProgress((length - MIN_LENGTH) / LENGTH_STEP);

        //register split checkbox
        split_box = (AppCompatCheckBox) view.findViewById(R.id.split_checkbox);
        split_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split = ((AppCompatCheckBox) v).isChecked() ? "split:" : "";
                send_configs();
            }
        });
        if (split.equals("split:")) {
            split_box.setChecked(true);
            split_box.jumpDrawablesToCurrentState();
        }

        change_background(current_color);
    }

    @Override
    public void change_background(int color) {
        super.change_background(color);

        //change color of ui elements if background is too dark/light
        int ui_element_color = ((MainActivity) getActivity()).find_viable_ui_color(color);

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
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                },
                new int[]{
                        ui_element_color,
                        ui_element_color
                }
        );
        split_box.setTextColor(ui_element_color);
        split_box.setSupportButtonTintList(colorStateList);
    }

    //currently does NOT include color and random state!
    public void send_configs() {
        ((MainActivity) getActivity()).send_config(split + "meteor:" + speed + " " + length + " " + getRGB() + "\n");
    }

    @Override
    protected PacemakerModeConfig store_configs() {
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setIval1(current_color);
        conf.setIval2(speed);
        conf.setIval3(length);
        conf.setSval1(split);

        return conf;
    }

    @Override
    protected void load_configs(PacemakerModeConfig conf) {
        if (conf != null) {
            this.current_color = conf.getIval1();
            this.speed = conf.getIval2();
            this.length = conf.getIval3();
            this.split = conf.getSval1();
        }
    }

    @Override
    protected int getID() {
        return ID;
    }
}
