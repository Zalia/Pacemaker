package zalia.pacemaker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import static zalia.pacemaker.MainActivity.TEST;

/**
 * Created by Zalia on 18.02.2017.
 */

public class TestMode extends PacemakerMode {

    protected final int ID = TEST;

    private int active_color;
    private String split;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //default settings
        active_color = Color.RED;
        split = "";

        //if possible, load settings
        load_configs(((MainActivity) getActivity()).get_config(ID));

        //initialize GUI elements and setup listeners
        RadioGroup radio_group = (RadioGroup) view.findViewById(R.id.effect_color);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button_red:
                    default:
                        active_color = Color.RED;
                        break;
                    case R.id.button_green:
                        active_color = Color.GREEN;
                        break;
                    case R.id.button_blue:
                        active_color = Color.BLUE;
                        break;
                }
                change_background(active_color);
                send_configs();
            }
        });

        CheckBox mirror_box = (CheckBox) view.findViewById(R.id.mirror_button);
        mirror_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                split = isChecked ? "split:" : "";
                send_configs();
            }
        });

        //restore default/loaded configs
        switch (active_color) {
            case Color.RED:
                radio_group.check(R.id.button_red);
                break;
            case Color.GREEN:
                radio_group.check(R.id.button_green);
                break;
            case Color.BLUE:
                radio_group.check(R.id.button_blue);
                break;
        }
        radio_group.jumpDrawablesToCurrentState(); //skip animations
        if (split.equals("split:")) {
            mirror_box.setChecked(true);
            mirror_box.jumpDrawablesToCurrentState(); //very important for buttons to not bug out
        }
        change_background(active_color);
    }

    private void change_background(int color) {
        getActivity().findViewById(R.id.pacemaker_layout).setBackgroundColor(color);
    }

    @Override
    public void send_configs() {
        ((MainActivity) getActivity()).send_config(split + "fillcolour:" + Color.red(active_color) +
                " " + Color.green(active_color) + " " + Color.blue(active_color) + "\n");
    }

    protected PacemakerModeConfig store_configs() {
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setIval1(active_color);
        conf.setSval1(split);
        return conf;
    }

    private void load_configs(PacemakerModeConfig config) {
        if (config != null) {
            this.active_color = config.getIval1();
            this.split = config.getSval1();
        }
    }

    protected int getID(){
        return ID;
    }

}
