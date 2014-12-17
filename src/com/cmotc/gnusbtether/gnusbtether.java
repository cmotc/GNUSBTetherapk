package com.cmotc.gnusbtether;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FilePermission;

public class gnusbtether extends Activity{
	private OutputStream sLiRPLocation;
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
			temp = copy(getResources().openRawResource(R.raw.slirp),sLiRPLocation);
			sLiRPLocation.close();
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
		int temp = 0;
		final int size = 1024 * 2;
		byte[] buf = new byte[size];
		if(src != null){
			// Transfer bytes from in to out
			BufferedInputStream in = new BufferedInputStream(src, size);
			BufferedOutputStream out = new BufferedOutputStream(dst, size);
			int count = 0, n = 0;
			try {
				while((n = in.read(buf, 0, size)) != -1){
					out.write(buf, 0, n);
					count += n;
				}
				out.flush();
			}finally{
				try{
					out.close();
				}catch(IOException e){
					Log.e("tag", e.getMessage());
				}
				try{
					in.close();
				}catch(IOException e){
					Log.e("tag", e.getMessage());
				}
			}
			File SLiRP = new File(getString(R.string.slirp_dir));
			SLiRP.setExecutable(true);
			temp = 1;
		}else{
			temp = -1;
		}
		return temp;
	}
	/**This starts SLiRP and listens for a connection
	*/
	private int startSLiRP(){
		int temp = 0;
		installSLiRP();
/*		sLiRPProcess = new ProcessBuilder(getString(R.string.slirp_dir),
			getString(R.string.sppp), getString(R.string.smtu), getString(R.string.snum));
		File dir = new File(getString(R.string.slirp_root));
		sLiRPProcess.directory(dir);
		try{
			sLiRPNative = sLiRPProcess.start();
		}catch(IOException e){
			temp = -1;
			Log.e("tag", e.getMessage());			
		}*/
		return temp;
	}
	/**This stops SLiRP and ?reloads firewall settings
	*/
	private int stopSLiRP(){
		sLiRPNative.destroy();
		int temp = sLiRPNative.exitValue();
		return temp;
	}
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
