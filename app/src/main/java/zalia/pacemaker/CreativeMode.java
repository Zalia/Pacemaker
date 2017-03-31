package zalia.pacemaker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static zalia.pacemaker.MainActivity.CREATIVE;


/**
 * Created by Zalia on 11.03.2017.
 */

public class CreativeMode extends PacemakerMode {

    private final int ID = CREATIVE;

    int active_color;
    private boolean initialized = false;
    private List<Button> buttons;
    private Button color_picker_dialog_button;
    private static final int[] BUTTON_IDS = {
            R.id.buttonT,
            R.id.buttonB,
            R.id.buttonl1,
            R.id.buttonr1,
            R.id.buttonl2,
            R.id.buttonr2,
            R.id.buttonl3,
            R.id.buttonr3,
    };
    private Map<Integer, Integer> button_colors;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.creative_layout, parent, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(Color.WHITE);

        if(!initialized) {
            //default settings
            active_color = Color.WHITE;
            if (buttons == null) buttons = new ArrayList<>();
            button_colors = new HashMap<>();
            for(int bid : BUTTON_IDS){
                button_colors.put(bid, Color.BLACK);
            }
            send_configs();

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
                                    Drawable bgShape = color_picker_dialog_button.getBackground();
                                    bgShape.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
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

            //setup LED section buttons
            for (int id : BUTTON_IDS) {
                Button button = (Button) view.findViewById(id);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = "";
                        button_colors.put(v.getId(), active_color);
                        switch (v.getId()) {
                            case R.id.buttonT:
                                id = "top";
                                break;
                            case R.id.buttonl1:
                                id = "l1";
                                break;
                            case R.id.buttonl2:
                                id = "l2";
                                break;
                            case R.id.buttonl3:
                                id = "l3";
                                break;
                            case R.id.buttonB:
                                id = "bot";
                                break;
                            case R.id.buttonr1:
                                id = "r1";
                                break;
                            case R.id.buttonr2:
                                id = "r2";
                                break;
                            case R.id.buttonr3:
                                id = "r3";
                                break;
                        }
                        Drawable bgShape = v.getBackground();
                        bgShape.setColorFilter(active_color, PorterDuff.Mode.MULTIPLY);
                        ((MainActivity) getActivity()).send_config("setpixel " + id + " " + active_color + "\n");
                    }
                });
                buttons.add(button);
            }
            initialized = true;
        }
    }

    @Override
    public void send_configs() {
        for (Map.Entry<Integer, Integer> entry : button_colors.entrySet()) {
            int button_id = entry.getKey();
            int color = entry.getValue();
            ((MainActivity) getActivity()).send_config("setpixel " + button_id + " " + color + "\n");
        }
    }

    public PacemakerModeConfig store_configs(){
        PacemakerModeConfig conf = new PacemakerModeConfig(ID);
        return conf;
    }

}
