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
	private File testIfSLiRPExists;
	private ProcessBuilder sLiRPProcess;
	private	Process sLiRPNative;
	/** Called when the activity is first created. 
	*/
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		int err = checkKill(installSLiRP());
	}
	/**Returns the SLiRP Program in the assets as an InputStream to be 
	copied to /data/local/bin/
	*/
	private InputStream getSLiRPAsset(){
		InputStream temp = null; //= ;
		AssetManager assetManager = getAssets();
		try{
			temp = assetManager.open("slirp");
		}catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		return temp;
	}
	/**Copies the InputStream provided by the asset to the File descriptor by
	path
	*/
	private int copy(InputStream src, File dst) throws IOException {
		InputStream in = src;
		int temp = 0;
		if(src != null){
			FileOutputStream out = openFileOutput(getString(R.string.slirp_dir), Context.MODE_PRIVATE);
//			openFileOutput();
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			File SLiRP = new File(getString(R.string.slirp_dir));
			SLiRP.setExecutable(true);
			temp = 1;
		}else{
			temp = -1;
		}
		return temp;
	}
	/**Checks if SLiRP is installed, if it is installed the function returns 
	0, if it needed to be installed it returns 1, and if it can't be 
	installed it returns -1*/
	private int installSLiRP(){
		int temp = 0;
		testIfSLiRPExists = new File(getString(R.string.slirp_dir));
		File testIfSLiRPDirExists = new File(getString(R.string.slirp_root));
		if(!testIfSLiRPDirExists.exists()){
			testIfSLiRPDirExists.mkdir();
		}
		if(!testIfSLiRPExists.exists()){
			try{
				temp = copy(getSLiRPAsset(),testIfSLiRPExists);
			}catch(IOException e){
				Log.e("tag", e.getMessage());
				temp = -1;
			}
		}
		return temp;
	}
	/**This removes SLiRP from the /data/local/bin folder and from any 
	custom folder the user may have specified*/
	private int removeSLiRP(){
		int temp = 0;
		if(!testIfSLiRPExists.delete()){
			if(testIfSLiRPExists.exists()){
				temp = -2;
			}else{
				temp = 1;
			}
		}
		File defaultSLiRPFile = new File("/data/local/bin/slirp");
		if(defaultSLiRPFile.exists()){
			if(defaultSLiRPFile.delete()){
				temp = temp + 1;
			}
		}
		return temp;
	}
	/**This starts SLiRP and listens for a connection
	*/
	private int startSLiRP(){
		int temp = installSLiRP();
		if( temp >= 0){
			///ppp mtu 1500 nodetach noauth noipdefault defaultroute usepeerdns notty 10.0.2.15:10.64.64.64
			sLiRPProcess = new ProcessBuilder(getString(R.string.slirp_dir),
				getString(R.string.sppp),
				getString(R.string.smtu),
				getString(R.string.snum)
				/*getString(R.string.snod),
				getString(R.string.snoi),
				getString(R.string.sdef),
				getString(R.string.suse),
				getString(R.string.snot),
				getString(R.string.sadd)*/);
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
	private int checkKill(int err){
//		if(err<0){
//			removeSLiRP();
//		}
		return err;
	}
	/**Handle the checkbox event
	*/
	public void onToggleCheckBox(View view){
		((CheckBox) view).toggle();
		boolean on = ((CheckBox) view).isChecked();
		if(on){
			checkKill(startSLiRP());
		}else{
			checkKill(stopSLiRP());
		}
	}
}
