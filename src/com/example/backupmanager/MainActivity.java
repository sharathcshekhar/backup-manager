package com.example.backupmanager;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class MainActivity extends Activity {
	
	static final String CACHE_PATH = "/sdcard/data/data/backup_cache/";
	
	final static private String APP_KEY = "3kwvg49qlt1ielo";
	final static private String APP_SECRET = "5gbohd30l6hkzrq";
	
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	// And later in some initialization function:
	
	static AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
	
	static AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
	
	static public DropboxAPI<AndroidAuthSession> mDBApi = new DropboxAPI<AndroidAuthSession>(session);
	boolean serviceStarted = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
	//	startService(service);
		//Intent intent = new Intent(getApplicationContext(), BackupService.class);
		//Log.d("CSE622", "Starting service");
		//getApplicationContext().
		//Bundle b = new Bundle();
		//b.putSerializable("db", mDBApi);
		//dbObj param = new dbObj();
		//param.mDBApi = mDBApi;
		//intent.putExtra("db", param);
		/*
		InputStream inputStream = new ByteArrayInputStream("test".getBytes());
		Entry response = null;
		try {
			response = mDBApi.putFile("/test.txt", inputStream,
					"test".length(), null, null);
		} catch (DropboxException e) {
			Log.i("CSE622", "Dropbox Exception in onCreate");
			e.printStackTrace();
		}
    	Log.i("DbExampleLog", "The uploaded file's rev is: " + ((response != null)? response.rev : "null"));
		
		startService(intent);
		serviceStarted = true;
		*/
		//Intent intent = new Intent(getApplicationContext(), BackupService.class);
		//startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onResume() {
	    super.onResume();
	    
	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            // Required to complete auth, sets the access token on the session
	            mDBApi.getSession().finishAuthentication();
	            Log.i("DbAuthLog", "Successfully authenticated");
	            /*
	            InputStream inputStream = new ByteArrayInputStream("test".getBytes());
	    		Entry response = null;
	    		try {
	    			response = mDBApi.putFile("/test.txt", inputStream,
	    					"test".length(), null, null);
	    		} catch (DropboxException e) {
	    			Log.i("CSE622", "Dropbox Exception in onResume");
	    			e.printStackTrace();
	    		}
	    		
	        	Log.i("DbExampleLog", "The uploaded file's rev is: " + ((response != null)? response.rev : "null"));
	            */
	            String accessToken = mDBApi.getSession().getOAuth2AccessToken();
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	        if (!serviceStarted) {
	        	Intent intent = new Intent(getApplicationContext(), BackupService.class);
	        	Log.i("DbAuthLog", "Starting Service");
	        	startService(intent);
	    		serviceStarted = true;
	        }
	    }
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
class dbObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DropboxAPI<AndroidAuthSession> mDBApi;
}