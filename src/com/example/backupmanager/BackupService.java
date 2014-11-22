package com.example.backupmanager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.http.entity.SerializableEntity;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class BackupService extends Service {
	//	DropboxAPI<AndroidAuthSession> mDBApi = null;	
		//final static private String APP_KEY = "3kwvg49qlt1ielo";
		//final static private String APP_SECRET = "5gbohd30l6hkzrq";
		
	//	final static private AccessType ACCESS_TYPE = AccessType.AUTO;
		
		// And later in some initialization function:
		
	//	AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		
//		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		
		public DropboxAPI<AndroidAuthSession> mDBApi = null;
		
		public BackupService() {
			Log.d("CSE622", "Instantiating service");
			mDBApi = MainActivity.mDBApi;
			try {
				Log.d("BACKUP_SERVICE", MainActivity.mDBApi.accountInfo().toString());
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//mDBApi.getSession().startOAuth2Authentication(BackupService.this);
		}
	/*
		public int onStartCommand (Intent intent, int flags, int startId) {
			
			dbObj param = (dbObj) intent.getSerializableExtra("db"); 
			mDBApi = param.mDBApi;
			Log.d("CSE622", "OnStartCommand");
			return android.app.Service.START_STICKY;
		}
		*/
		
		class IncomingHandler extends Handler {
			/*
			public IncomingHandler() {
				Log.d("CSE622", "Instantiating Handler");
			}
			*/
			@Override
	        public void handleMessage(Message msg) {
	            
	        	Bundle data = msg.getData();        	
	        	String filename = data.getString("filename");
	        	Log.d("BACKUP_SERVICE", "filename");
	        	
	        	//File file = new File(MainActivity.CACHE_PATH, filename);
	        	
	        	//File file = new File("working-draft.txt");
	        	//file.
	        	//FileInputStream inputStream;
	        	Entry response = null;
	        	InputStream inputStream = new ByteArrayInputStream("test".getBytes());
	        	try {
					//inputStream = new FileInputStream(file);
	        		Log.d("BACKUP_SERVICE", MainActivity.mDBApi.toString());
	        		//MainActivity.mDBApi.
					response = MainActivity.mDBApi.putFile("/test.txt", inputStream,
							"test".length(), null, null);
				} catch (DropboxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} /*catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
	        	Log.i("DbExampleLog", "The uploaded file's rev is: " + ((response != null)? response.rev : "null"));
	        	
	        }
	     }
		
		public void BackupToDropBox(String filename) {
			
		}

		final Messenger myMessenger = new Messenger(new IncomingHandler());

		@Override
		public IBinder onBind(Intent intent) {
			return myMessenger.getBinder();
		}

}