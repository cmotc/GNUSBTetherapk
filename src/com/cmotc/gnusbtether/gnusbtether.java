package com.cmotc.gnusbtether;

import android.app.Activity;
import android.os.Bundle;
import android.content.res.AssetManager;
import android.util.Log;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FilePermission;

public class gnusbtether extends Activity
{
	private File testIfSLiRPExists;
	private ProcessBuilder sLiRPProcess;
	/** Called when the activity is first created. 
	*/
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		int err = installSLiRP();
		if(err < 0){
			removeSLiRP();
		}
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
			OutputStream out = new FileOutputStream(dst);
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
			sLiRPProcess = new ProcessBuilder(getString(R.string.slirp_dir),"");
			try{
				Process sLiRPNative = sLiRPProcess.start();
			}catch(IOException e){
				temp = -2;
				Log.e("tag", e.getMessage());			
			}
		}else{
			temp = -1;
		}
		return temp;
	}
	/**This stops SLiRP and reloads firewall settings
	*/
	private int stopSLiRP(){
		int temp = 0;
/*		if(){
			sLiRPProcess.
		}
*/		return temp;
	}
}
