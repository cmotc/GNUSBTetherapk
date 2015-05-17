package io.github.cmotc.FreeUSBTether;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.net.ConnectivityManager;
import android.util.Log;

public class TetherActivity extends PreferenceActivity{
    private static final String DEBUG = "FREE_USB_TETHER";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public class UsbConnectivityManager{
        public static final int TETHER_ERROR_NO_ERROR = 0;
        public static final int TETHER_ERROR_UNKNOWN_IFACE = 1;
        public static final int TETHER_ERROR_SERVICE_UNAVAIL = 2;
        public static final int TETHER_ERROR_UNSUPPORTED = 3;
        public static final int TETHER_ERROR_UNAVAIL_IFACE = 4;
        public static final int TETHER_ERROR_MASTER_ERROR = 5;
        public static final int TETHER_ERROR_TETHER_IFACE_ERROR = 6;
        public static final int TETHER_ERROR_UNTETHER_IFACE_ERROR = 7;
        public static final int TETHER_ERROR_ENABLE_NAT_ERROR = 8;
        public static final int TETHER_ERROR_DISABLE_NAT_ERROR = 9;
        public static final int TETHER_ERROR_IFACE_CFG_ERROR = 10;
        public static final String ACTION_TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";
        public static final String EXTRA_AVAILABLE_TETHER = "availableArray";
        public static final String EXTRA_ACTIVE_TETHER = "activeArray";
        public static final String EXTRA_ERRORED_TETHER = "erroredArray";
        public static final String ACTION_MEDIA_SHARED = Intent.ACTION_MEDIA_SHARED;
        public static final String ACTION_MEDIA_UNSHARED = "android.intent.action.MEDIA_UNSHARED";
        public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
        public static final String USB_CONNECTED = "connected";
        public static final String USB_CONFIGURED = "configured";
        private static final String MANAGER_DEBUG = "_MANAGER";
        private final ConnectivityManager cm;
        private boolean isUsbTetheringActive = false;
        public UsbConnectivityManager(Context ctx){
            this((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE));
        }
        public UsbConnectivityManager(ConnectivityManager cm){
            this.cm = cm;
        }
        public boolean hasAllTetheringPermissions(Context ctx){
            return hasPermission(ctx, "android.permission.ACCESS_NETWORK_STATE") 
                && hasPermission(ctx, "android.permission.CHANGE_NETWORK_STATE") 
                && hasPermission(ctx, "android.permission.MANAGE_USB");
        }
        public boolean hasPermission(Context ctx, String perm){
            if (ctx.checkCallingOrSelfPermission(perm) == PackageManager.PERMISSION_GRANTED){
                Log.d(DEBUG+MANAGER_DEBUG, "permission is granted: " + perm);
                return true;
            }else{
                Log.d(DEBUG+MANAGER_DEBUG, "permission is denied: " + perm);
                return false;
            }
        }
        public int setUsbTethering(boolean flag) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            Method m = cm.getClass().getMethod("setUsbTethering", Boolean.TYPE);
            Integer err = (Integer) m.invoke(cm, Boolean.valueOf(flag));
            isUsbTetheringActive = flag;
            Log.d(DEBUG+MANAGER_DEBUG, "setUsbTethering to " + flag + " returns " + err);
            return err;
        }
        public boolean flipUsbTethering() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            return setUsbTethering(!isUsbTetheringActive) == 0;
        }
        private String[] invokeSimple(String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
            Method m = cm.getClass().getMethod(name);
            String[] result = (String[]) m.invoke(cm);
            Log.d(DEBUG+MANAGER_DEBUG, name + " returns " + Arrays.asList(result));
            return result;
        }
        public String[] getTetherableUsbRegexs() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            return invokeSimple("getTetherableUsbRegexs");
        }
        public String[] getTetheringErroredIfaces() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            return invokeSimple("getTetheringErroredIfaces");
        }
        public String[] getTetheredIfaces() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            return invokeSimple("getTetheredIfaces");
        }
        public String[] getTetherableIfaces() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            return invokeSimple("getTetherableIfaces");
        }
        public int getLastTetherError(String s) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            Method m = cm.getClass().getMethod("getLastTetherError", String.class);
            Integer err = (Integer) m.invoke(cm, s);
            Log.d(DEBUG+MANAGER_DEBUG, "getLastTetherError for " + s + " returns " + err);
            return err;
        }
    }
    private class ManageTetherSettings extends PreferenceFragment{
        private static final String SETTING_DEBUG = "_SETTINGS";
        private String[] usbRegex = new String[] {};
        private boolean massStorageActive;
        private boolean usbConnected;
        private SwitchPreference tetherMe;
        private SwitchPreference tetherAuto;
        private TetherChangeReceiver usbConnectReciever;
        private UsbConnectivityManager ucm;
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.usb_tether_preferences);
            //Initialize Preferences
            PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.usb_tether_preferences, false);
            tetherMe = (SwitchPreference) findPreference("usbTetherOn");
            tetherAuto = (SwitchPreference) findPreference("usbTetherAuto");
            ucm = new UsbConnectivityManager(getActivity().getApplicationContext());
            String perm = ucm.hasAllTetheringPermissions(getActivity().getApplicationContext()) ? "Gathered permissions" : "Failed to gather permissions";
            if(perm=="Gathered permissions"){
                Log.i(DEBUG, perm);
            }else{
                Log.e(DEBUG, perm);
            }
            if (!isUsbAvailable()){
                tetherMe.setEnabled(false);
                tetherMe.setSummary("No USB Available");
                Log.e(DEBUG, "no USB available");
            }
        }
        @Override
        public void onStart(){
            super.onStart();
            massStorageActive = Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());
            usbConnectReciever = new TetherChangeReceiver();
            IntentFilter filter = new IntentFilter(UsbConnectivityManager.ACTION_TETHER_STATE_CHANGED);
            Intent intent = getActivity().registerReceiver(usbConnectReciever, filter);
            filter = new IntentFilter();
            filter.addAction(UsbConnectivityManager.ACTION_USB_STATE);
            getActivity().registerReceiver(usbConnectReciever, filter);
            filter = new IntentFilter();
            filter.addAction(UsbConnectivityManager.ACTION_MEDIA_SHARED);
            filter.addAction(UsbConnectivityManager.ACTION_MEDIA_UNSHARED);
            filter.addDataScheme("file");
            getActivity().registerReceiver(usbConnectReciever, filter);
            if (intent != null)usbConnectReciever.onReceive(getActivity(), intent);
            updateState(null, null, null);
        }
        @Override
        public void onStop(){
            getActivity().unregisterReceiver(usbConnectReciever);
            usbConnectReciever = null;
            super.onStop();
        }
        private void updateState(String[] available, String[] tethered, String[] errored){
            try{
                if (available == null && tethered == null && errored == null){
                    available = ucm.getTetherableIfaces();
                    tethered = ucm.getTetheredIfaces();
                    errored = ucm.getTetheringErroredIfaces();
                }
                updateUsbState(available, tethered, errored);
            }catch (Exception e){
                Log.e(DEBUG, "failed to update state" + e.getMessage());
            }
        }
        private void updateUsbState(String[] available, String[] tethered, String[] errored) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
            boolean usbAvailable = usbConnected && !massStorageActive;
            int usbError = UsbConnectivityManager.TETHER_ERROR_NO_ERROR;
            for (String s : available){
                for (String regex : usbRegex){
                    if (s.matches(regex)){
                        if (usbError == UsbConnectivityManager.TETHER_ERROR_NO_ERROR){
                            usbError = ucm.getLastTetherError(s);
                        }
                    }
                }
            }
            boolean usbTethered = false;
            for (String s : tethered){
                for (String regex : usbRegex){
                    if (s.matches(regex))    usbTethered = true;
                }
            }
            boolean usbErrored = false;
            for (String s : errored){
                for (String regex : usbRegex){
                if (s.matches(regex))
                    usbErrored = true;
                }
            }
            if (usbTethered){
                tetherMe.setSummary("USB tethering active");
                tetherMe.setEnabled(true);
                tetherMe.setChecked(true);
            }else if (usbAvailable){
                if (usbError == UsbConnectivityManager.TETHER_ERROR_NO_ERROR){
                    tetherMe.setSummary("USB connected, check to tether");
                }else{
                    tetherMe.setSummary("USB tethering error");
                }
                tetherMe.setEnabled(true);
                tetherMe.setChecked(false);
            }else if (usbErrored){
                tetherMe.setSummary("USB tethering error");
                tetherMe.setEnabled(false);
                tetherMe.setChecked(false);
            }else if (massStorageActive){
                tetherMe.setSummary("Cannot tether with torage active");
                tetherMe.setEnabled(false);
                tetherMe.setChecked(false);
            }else{
                tetherMe.setSummary("USB not Connected");
                tetherMe.setEnabled(false);
                tetherMe.setChecked(false);
            }
        }
        private boolean isUsbAvailable(){
            boolean usbAvailable = false;
            try{
                usbRegex = ucm.getTetherableUsbRegexs();
                usbAvailable = usbRegex.length > 0;
            }catch (Exception e){
                Log.e(DEBUG, "getTetherableUsbRegexs failed " + e.getMessage());
            }
            return usbAvailable;
        }
        private void setUsbTethering(boolean enabled){
            String subtext = "err";
            try{
                if (ucm.setUsbTethering(enabled) == UsbConnectivityManager.TETHER_ERROR_NO_ERROR){
                    subtext = (ucm.setUsbTethering(enabled) == UsbConnectivityManager.TETHER_ERROR_NO_ERROR) ? null : "err";
                    Log.i(DEBUG+SETTING_DEBUG, "USB tethering set to " + enabled);
                }
            }catch (Exception e){
                Log.e(DEBUG+SETTING_DEBUG, "setUsbTethering failed" + e.getMessage());
            }
            if (subtext != null){
                tetherMe.setSummary(subtext);
            }else{
                tetherMe.setSummary("...");
                tetherMe.setChecked(enabled);
            }
        }
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference){
            if (preference == tetherMe){
                Log.i(DEBUG+SETTING_DEBUG, "user changed USB tethering to " + tetherMe.isChecked());
                setUsbTethering(tetherMe.isChecked());
            }else if (preference == tetherAuto){
                Log.d(DEBUG+SETTING_DEBUG, "user changed auto start to " + tetherAuto.isChecked());
            }
                return super.onPreferenceTreeClick(screen, preference);
        }
        private class TetherChangeReceiver extends BroadcastReceiver{
            private static final String RECIEVER_DEBUG = "_RECIEVER";
            @Override
            public void onReceive(Context context, Intent intent){
                Log.e(DEBUG+SETTING_DEBUG+RECIEVER_DEBUG, "Broadcast Reciever recieved new intent" + intent.getAction());
                if (intent.getAction().equals(UsbConnectivityManager.ACTION_TETHER_STATE_CHANGED)){
                // TODO - this should understand the interface types
                    ArrayList<String> available = intent.getStringArrayListExtra(UsbConnectivityManager.EXTRA_AVAILABLE_TETHER);
                    ArrayList<String> active = intent.getStringArrayListExtra(UsbConnectivityManager.EXTRA_ACTIVE_TETHER);
                    ArrayList<String> errored = intent.getStringArrayListExtra(UsbConnectivityManager.EXTRA_ERRORED_TETHER);
                    updateState(available.toArray(new String[available.size()]),active.toArray(new String[active.size()]),errored.toArray(new String[errored.size()]));
                }else if (intent.getAction().equals(UsbConnectivityManager.ACTION_MEDIA_SHARED)){
                    massStorageActive = true;
                    updateState(null, null, null);
                }else if (intent.getAction().equals(UsbConnectivityManager.ACTION_MEDIA_UNSHARED)){
                    massStorageActive = false;
                    updateState(null, null, null);
                }else if (intent.getAction().equals(UsbConnectivityManager.ACTION_USB_STATE)){
                    boolean USB_STATE = intent.getBooleanExtra(UsbConnectivityManager.USB_CONNECTED, false);
                    Log.i(DEBUG+SETTING_DEBUG+RECIEVER_DEBUG, "intent " + intent.getAction() + " usbConnect is " + USB_STATE);
                    if (USB_STATE && !tetherMe.isChecked() && tetherAuto.isChecked()){
                        Log.w(DEBUG+SETTING_DEBUG+RECIEVER_DEBUG, "auto-starting USB tethering");
                        setUsbTethering(true);
                    }
                usbConnected = USB_STATE;
                updateState(null, null, null);
                }
            }
        }
    }
}
