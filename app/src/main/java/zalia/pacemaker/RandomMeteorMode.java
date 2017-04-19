package zalia.pacemaker;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import static zalia.pacemaker.MainActivity.RANDOM;

/**
 * Created by Zalia on 27.03.2017.
 */

public class RandomMeteorMode extends PacemakerMode {

    protected static final int ID = RANDOM;

    private static final int MIN_INTENSITY = 1;
    private static final int MAX_INTENSITY = 43;
    private static final int INTENSITY_STEP = 2;

    //default config
    private int intensity = 4;
    private String split = "";


    private SeekBar intensity_bar;
    private AppCompatCheckBox split_box;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.random_meteor_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        load_configs(((MainActivity) getActivity()).get_config(ID));

        intensity_bar = (SeekBar) view.findViewById(R.id.intensity_slider);
        intensity_bar.setMax((MAX_INTENSITY - MIN_INTENSITY) / INTENSITY_STEP);
        intensity_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intensity = MIN_INTENSITY + progress * INTENSITY_STEP;
                send_configs();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //register split checkbox
        split_box = (AppCompatCheckBox) view.findViewById(R.id.split_checkbox);
        split_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                split = ((AppCompatCheckBox) v).isChecked() ? "split:" : "";
                send_configs();
            }
        });

        //set current config
        intensity_bar.setProgress((intensity - MIN_INTENSITY) / INTENSITY_STEP);
        if (split.equals("split:")) {
            split_box.setChecked(true);
            split_box.jumpDrawablesToCurrentState();
        }

        change_background();
    }

    private void change_background() {
        //change background to a different rainbow pattern
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN});
        ((MainActivity) getActivity()).findViewById(R.id.pacemaker_layout).setBackground(rainbow);

        //change colors of progressbar
        intensity_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        intensity_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        //change colors of checkboxes
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
        split_box.setTextColor(Color.WHITE);
        split_box.setSupportButtonTintList(colorStateList);
    }

    @Override
    public void send_configs() {
        ((MainActivity) getActivity()).send_config(split + "comethail:" + intensity + "\n");
    }

    protected PacemakerModeConfig store_configs() {
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setIval1(intensity);
        conf.setSval1(split);
        return conf;
    }

    protected void load_configs(PacemakerModeConfig conf) {
        if (conf != null) {
            this.intensity = conf.getIval1();
            this.split = conf.getSval1();
        }
    }

    protected int getID(){
        return ID;
    }
}
