package zalia.pacemaker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.R.attr.button;
import static android.R.attr.id;
import static android.R.attr.key;
import static zalia.pacemaker.MainActivity.CREATIVE;


/**
 * Created by Zalia on 11.03.2017.
 */

public class CreativeMode extends PacemakerMode {

    private final int ID = CREATIVE;
    private final int NUM_LEDS = 120;

    private int num_buttons;
    int active_color;
    private Button color_picker_dialog_button;
    private List<Integer> button_ids;
    //Note: the actual name of the view an id belongs to can be retreived like this:
    //String id = view.getResources().getResourceName(child.getId());
    private Map<Integer, Integer> button_colors;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.creative_layout, parent, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set background to white
        ((MainActivity) getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(Color.WHITE);

        //get a list of all buttons
        button_ids = new LinkedList<>();
        RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.creative_layout);
        for(int index=0; index<layout.getChildCount(); index++){
            View child = layout.getChildAt(index);
            String id = view.getResources().getResourceName(child.getId());
            if(id.startsWith("zalia.pacemaker:id/button")) {
                button_ids.add(child.getId());
            }
        }
        num_buttons = button_ids.size();

        //default settings
        active_color = Color.rgb(204,65,36);
        button_colors = new HashMap<>();
        for(int bid : button_ids){
            button_colors.put(bid, Color.rgb(204,65,36));
        }
        load_configs(((MainActivity)getActivity()).get_config(ID));

        //setup color picker dialog
        color_picker_dialog_button = (Button) view.findViewById(R.id.color_picker_dialog_button);
        color_picker_dialog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(v.getContext())
                        .setTitle("Farbe wählen:")
                        .lightnessSliderOnly()
                        .initialColor(active_color)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
//                                toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("Übernehmen", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                active_color = selectedColor;
                                color_picker_dialog_button.getBackground().setColorFilter(active_color, PorterDuff.Mode.SRC_IN);
                                Log.d("CM", "set color to " + active_color);
                            }
                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
        color_picker_dialog_button.getBackground().setColorFilter(active_color, PorterDuff.Mode.SRC_IN);

        //setup LED section buttons
        for (int id : button_ids) {
            Button button = (Button) view.findViewById(id);
            //listeners
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(active_color != (int)v.getTag()) {
                        button_colors.put(v.getId(), active_color);
//                        v.getBackground().setColorFilter(active_color, PorterDuff.Mode.MULTIPLY);
                        color_segment(v.getId(), active_color);
                        ((Button) v).getBackground().setColorFilter(active_color, PorterDuff.Mode.SRC_IN);
                        v.setTag(active_color);
                    }
                }
            });
            //set default or loaded color
            button.setTag(button_colors.get(id));
            button.getBackground().setColorFilter(button_colors.get(id), PorterDuff.Mode.SRC_IN);
        }
        send_configs();
    }

    //set all leds that correspond to the current Button
    private void color_segment(int id, int color){
        String id_string = getResources().getResourceName(id);
        id_string = id_string.split("/")[1];
        int led_start=0;
        int led_end;
        double segmentsize = (double)NUM_LEDS / (double)num_buttons;
        if(id_string.equals("buttonT")) {
            led_start = 0;
        } else if(id_string.contains("L")){
            led_start = (int)Math.round((double)Integer.parseInt(id_string.substring(7)) * segmentsize);
        }else if(id_string.contains("R")){
            led_start = (int) Math.round(((num_buttons / 2.0)* segmentsize) + //left side buttons and top button
                    //since my numbering pattern starts at the top, but the led strip wraps around the
                    //bottom and starts from there, the button id has to be inverted
                    Math.round(((num_buttons / 2.0))-(double)Integer.parseInt(id_string.substring(7))) * segmentsize);
        }else if(id_string.equals("buttonB")){
            led_start = (int)Math.round((num_buttons / 2.0)*segmentsize);
        }else{
            //should never happen
            Log.d("CM", "ERROR: unknown id string: " + id_string);
        }
        led_end = (int)Math.round(led_start + segmentsize);
        for(int i=led_start; i<led_end; i++) {
            ((MainActivity) getActivity()).send_config("setpixel:" + i + " " + color + "\n");
        }
    }

    @Override
    public void send_configs() {
        for (int bid : button_ids) {
            color_segment(bid, button_colors.get(bid));
        }
    }

    protected PacemakerModeConfig store_configs(){
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        conf.setImap(button_colors);
        conf.setIval1(active_color);
        return conf;
    }

    protected void load_configs(PacemakerModeConfig config){
        if(config != null) {
            this.button_colors = config.getImap();
            this.active_color = config.getIval1();
            for(int bid : button_ids){
                //during development something broke and this repaired it
                //this code snippet should not be needed, but better be safe than sorry
                if(button_colors.get(bid) == null){
                    String id_string = getResources().getResourceName(bid);
                    Log.d("CM", "loading of color for " + id_string + " failed, replacing with default color");
                    button_colors.put(bid, Color.rgb(204,65,36));
                }
            }
        }
    }
}
