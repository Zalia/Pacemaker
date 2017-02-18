package zalia.pacemaker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;

/**
 * Created by Zalia on 17.02.2017.
 */

public class ColorPickerMode extends PacemakerMode {

    private ColorPickerView colorPickerView;
    private View root;
    private int current_color;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.color_picker_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //default settings
        current_color = Color.WHITE;
        change_background(current_color);

        //setup listeners
        colorPickerView = (ColorPickerView) view.findViewById(R.id.color_picker_view);
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                change_background(selectedColor);
            }
        });
    }

    private void change_background(int color){
        ((MainActivity)getActivity()).findViewById(R.id.pacemaker_layout).setBackgroundColor(color);
    }

    @Override
    public String generate_configs() {
        return "colorwheel: " + current_color;
    }
}
