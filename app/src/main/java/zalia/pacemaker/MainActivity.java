package zalia.pacemaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.Spinner;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;

public class MainActivity extends AppCompatActivity {
    private View root;
    private View modeView;
    private int currentBackgroundColor = 0xffffffff;

    private Spinner spinner;
    private ColorPickerView colorPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.color_screen);
        changeBackgroundColor(currentBackgroundColor);

        spinner = (Spinner) findViewById(R.id.modes_dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup modeViewHolder = (ViewGroup)findViewById(R.id.frame_layout);

                toast("handling selected item");
                if(modeView != null){
                    modeViewHolder.removeView(modeView);
                }

                int modeViewResId;
                switch(position){
                    case 0: default:
                        modeViewResId = R.layout.effect_layout;
                        break;
                    case 1:
                        modeViewResId = R.layout.full_rainbow_layout;
                        break;
                    case 2:
                        modeViewResId = R.layout.color_picker_layout;
                        break;
                }

                modeView = getLayoutInflater().inflate(modeViewResId, null);
                modeViewHolder.addView(modeView);
                if(position == 2){
                    toast("attempting to initialize color picker");
                    colorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
                    colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "selectedColor: " + Integer.toHexString(selectedColor).toUpperCase(),
                                    Toast.LENGTH_SHORT).show();
                            changeBackgroundColor(selectedColor);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void changeBackgroundColor(int selectedColor) {
        currentBackgroundColor = selectedColor;
        root.setBackgroundColor(selectedColor);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}
