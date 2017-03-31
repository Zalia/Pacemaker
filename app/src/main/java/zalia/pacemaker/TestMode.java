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

    private final int ID = TEST;

    private View root;
    private RadioGroup radio_group;
    private String active_color, mirror;
    private boolean initialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.test_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(!initialized) {
            //default settings
            active_color = "r";
            mirror = "";

            change_background(active_color);

            //setup listeners
            radio_group = (RadioGroup) view.findViewById(R.id.effect_color);
            radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.button_red:
                        default:
                            active_color = "r";
                            break;
                        case R.id.button_green:
                            active_color = "g";
                            break;
                        case R.id.button_blue:
                            active_color = "b";
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
                    mirror = isChecked ? "split:" : "";
                    send_configs();
                }
            });
            initialized = true;
        } else{
            change_background(active_color);
        }
    }

    private void change_background(String color){
        int c = Color.RED;
        switch(color){
            case "r":
                c = Color.RED;
                break;
            case "g":
                c = Color.GREEN;
                break;
            case "b":
                c = Color.BLUE;
                break;
        }
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(c);
    }

    @Override
    public void send_configs() {
        ((MainActivity)getActivity()).send_config(mirror + "fillcolour:" + active_color + "\n");
    }

    protected PacemakerModeConfig store_configs(){
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        return conf;
    }

}