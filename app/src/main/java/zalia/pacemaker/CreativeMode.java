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

    int active_color;
    private Button color_picker_dialog_button;
    private List<Integer> button_ids;
    //Note: the actual name of the view an id belongs to can be retreived like this:
    //String id = view.getResources().getResourceName(child.getId());
    RelativeLayout layout;
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
            if (child instanceof Button && child != view.findViewById(R.id.color_picker_dialog_button)) {
                button_ids.add(child.getId());
            }
        }

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
                    button_colors.put(v.getId(), active_color);
//                    v.getBackground().setColorFilter(active_color, PorterDuff.Mode.MULTIPLY);
                    color_segment(v.getId());
                    ((Button) v).getBackground().setColorFilter(active_color, PorterDuff.Mode.SRC_IN);
                }
            });
            //set default or loaded color
            button.getBackground().setColorFilter(button_colors.get(id), PorterDuff.Mode.SRC_IN);
        }
        send_configs();
    }

    //set all leds that correspond to the current Button
    private void color_segment(int id){
        ((MainActivity) getActivity()).send_config("setpixel " + id + " " + active_color + "\n");
    }

    @Override
    public void send_configs() {
        for (Map.Entry<Integer, Integer> entry : button_colors.entrySet()) {
            int button_id = entry.getKey();
            int color = entry.getValue();
            ((MainActivity) getActivity()).send_config("setpixel " + button_id + " " + color + "\n");
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
        }
    }
}
