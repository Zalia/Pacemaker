package zalia.pacemaker;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import static zalia.pacemaker.MainActivity.RANDOM;
import static zalia.pacemaker.MainActivity.get_progress_respecting_range;
import static zalia.pacemaker.MainActivity.normalize_progress;

/**
 * Created by Zalia on 27.03.2017.
 */

public class RandomMeteorMode extends PacemakerMode {

    private static final int ID = RANDOM;

    private static final int MIN_BRIGHTNESS = 0;
    private static final int MAX_BRIGHTNESS = 1;

    //default config
    private double brightness = 1.0;


    private SeekBar brightness_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.random_meteor_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        load_configs(((MainActivity)getActivity()).get_config(ID));

        brightness_bar = (SeekBar) view.findViewById(R.id.brightness_slider);
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

        //set current config
        brightness_bar.setProgress(get_progress_respecting_range(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS));
        change_background();
    }

    private void change_background(){
        //change background to a different rainbow pattern
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN });
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackground(rainbow);

        //change colors of progressbar
        brightness_bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brightness_bar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void send_configs() {
        ((MainActivity)getActivity()).send_config("shower:" + brightness + "\n");
    }

    protected PacemakerModeConfig store_configs(){
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setBrightness(brightness);
        return conf;
    }

    protected void load_configs(PacemakerModeConfig conf){
        if(conf != null){
            this.brightness = conf.getBrightness();
        }
    }
}
