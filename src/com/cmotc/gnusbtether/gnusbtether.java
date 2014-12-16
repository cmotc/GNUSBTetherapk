package com.cmotc.gnusbtether;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FilePermission;

public class gnusbtether extends Activity{
	private FileOutputStream sLiRPLocation;
	private ProcessBuilder sLiRPProcess;
	private	Process sLiRPNative;
	/** Called when the activity is first created. 
	*/
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		installSLiRP();
	}
	/**Returns the SLiRP Program in the assets as an InputStream to be 
	copied to /data/local/bin/
	*/
	private InputStream getSLiRPAsset(){
		InputStream temp = getResources().openRawResource(R.raw.slirp); 
		return temp;
	}
	/**Checks if SLiRP is installed(in the data folder), if it is installed 
	the function returns 0, if it needed to be installed it returns 1, and 
	if it can't be installed it returns -1
	*/
	private int installSLiRP(){
		int temp = 0;
		try{
			sLiRPLocation = openFileOutput(getString(R.string.slirp_dir), Context.MODE_PRIVATE);
		}catch(IOException e){
			Log.e("tag", e.getMessage());
			temp = -2;
		}
		try{
			temp = copy(getSLiRPAsset(),sLiRPLocation);
		}catch(IOException e){
			Log.e("tag", e.getMessage());
			temp = -1;
		}
		return temp;
	}
	/**Copies the InputStream provided by the asset to the File descriptor by
	path
	*/
	private int copy(InputStream src, OutputStream dst) throws IOException {
		InputStream in = src;
		int temp = 0;
		if(src != null){
			FileOutputStream out = openFileOutput(getString(R.string.slirp_dir), Context.MODE_PRIVATE);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
//			File SLiRP = new File(getString(R.string.slirp_dir));
//			SLiRP.setExecutable(true);
			temp = 1;
		}else{
			temp = -1;
		}
		return temp;
	}
	/**This starts SLiRP and listens for a connection
	*/
	private int startSLiRP(){
		int temp = installSLiRP();
		if( temp >= 0){
			sLiRPProcess = new ProcessBuilder(getString(R.string.slirp_dir),
				getString(R.string.sppp), getString(R.string.smtu), getString(R.string.snum));
			File dir = new File(getString(R.string.slirp_root));
			sLiRPProcess.directory(dir);
			try{
				sLiRPNative = sLiRPProcess.start();
			}catch(IOException e){
				temp = -2;
				Log.e("tag", e.getMessage());			
			}
		}else{
			temp = -1;
		}
		return temp;
	}
	/**This stops SLiRP and ?reloads firewall settings
	*/
	private int stopSLiRP(){
		sLiRPNative.destroy();
		int temp = sLiRPNative.exitValue();
		return temp;
	}
	/**whenever a function returns less than zero, a failure has occurred.
	cleanup whatever you can and exit.
	*/
	/**Handle the checkbox event
	*/
	public void onToggleCheckBox(View view){
		((CheckBox) view).toggle();
		boolean on = ((CheckBox) view).isChecked();
		if(on){
			startSLiRP();
		}else{
			stopSLiRP();
		}
	}
}
