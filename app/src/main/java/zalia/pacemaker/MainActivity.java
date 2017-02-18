package zalia.pacemaker;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Spinner;

import static android.R.attr.mode;

public class MainActivity extends AppCompatActivity {

    private View root;
    private View modeView;
    private PacemakerMode active_mode;

    private Spinner spinner;
    private Button commit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.pacemaker_layout);

        //setup mode selection dropdown menu
        spinner = (Spinner) findViewById(R.id.modes_dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                ViewGroup modeViewHolder = (ViewGroup)findViewById(R.id.frame_layout);
                if(modeView != null){
                    modeViewHolder.removeView(modeView);
                }

                //load current mode's layout
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch(index){
                    case 0:
                        active_mode = new EffectMode();
                        break;
                    case 1: default:
                        active_mode = new FullRainbowMode();
                        break;
                    case 2:
                        active_mode = new ColorPickerMode();
                        break;
                }
                ft.replace(R.id.frame_layout, active_mode);
                ft.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        commit_button = (Button)findViewById(R.id.commit_button);
        commit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String config = active_mode.generate_configs();
                send_config(config);
            }
        });

    }

    public void change_background(int color){
        root.setBackgroundColor(color);
    }

    //bluetooth stuff
    private void send_config(String config){
        toast("Config commited: '" + config + "'");
        return;
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //utility method for all fragments that use progress bars
    public static int normalize_progress(int progress, int min, int max){
        double dmin = min;
        double dmax = max;
        double dprog = progress;
        double result = dmin + (dmax-dmin)/(100/dprog);
        return (int) Math.round(result);
    }

}
