package zalia.pacemaker;

import android.support.v4.app.Fragment;

/**
 * Created by Zalia on 17.02.2017.
 */

public abstract class PacemakerMode extends Fragment {

    //send a complete state of the current mode using
    //((MainActivity)getActivity).send_config(String config);
    public abstract void send_configs();

    //store the current state of the mode into a PacemakerModeConfig object
    //MANDATORY
    protected abstract PacemakerModeConfig store_configs();

}
