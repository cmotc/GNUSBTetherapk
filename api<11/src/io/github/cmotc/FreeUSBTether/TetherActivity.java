package io.github.cmotc.FreeUSBTether;

import android.app.Activity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.os.Bundle;

public class TetherActivity extends PreferenceActivity
{
    private static final String DEBUG = "FREE_USB_TETHER";
    private class ManageTetherSettings extends PreferenceFragment{
        private static final String SETTING_DEBUG = "_SETTINGS";
        private SwitchPreference tetherMe;
        private SwitchPreference tetherAuto;
        public ManageTetherSettings(){
            
        }
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.usb_tether_preferences);
            //Initialize Preferences
            PreferenceManager.setDefaultValues(getActivity().
                getApplicationContext(), R.xml.usb_tether_preferences, false);
            tetherMe = (SwitchPreference) findPreference(usbTetherOn);
            tetherAuto = (SwitchPreference) findPreference(usbTetherAuto);
            
        }
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
