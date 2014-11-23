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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class BackupService extends Service {
		DropboxAPI<AndroidAuthSession> mDBApi = null;	
		public BackupService() {
			Log.d("CSE622", "Instantiating service");
			mDBApi = MainActivity.mDBApi;
		}
		
		/*
		
		public int onStartCommand (Intent intent, int flags, int startId) {
			
		}
		*/
		
		class IncomingHandler extends Handler {
			@Override
	        public void handleMessage(Message msg) {
	            
	        	Bundle data = msg.getData();        	
	        	String filename = data.getString("filename");
	        	Log.d("BACKUP_SERVICE", filename != null ? filename : "null");
	           	new DropBoxUpload().execute();
	         }
	     }
		
		class DropBoxUpload extends AsyncTask <Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				InputStream inputStream = new ByteArrayInputStream("test".getBytes());
				Entry response = null;
				try {
					Log.d("BACKUP_SERVICE", MainActivity.mDBApi.accountInfo().toString());
					
					response = MainActivity.mDBApi.putFile("/test.txt", inputStream,
							"test".length(), null, null);
				} catch (DropboxException e) {
					e.printStackTrace();
				} 
				Log.i("DbExampleLog", "The uploaded file's rev is: " + response != null ? response.rev : "null");
				
				return null;
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