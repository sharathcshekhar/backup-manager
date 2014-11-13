package com.example.backupmanager;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class BackupService extends Service {

		class IncomingHandler extends Handler {
	        @Override
	        public void handleMessage(Message msg) {
	            
	        	Bundle data = msg.getData();        	
	        	String filename = data.getString("filename");
	        	Log.d("BACKUP_SERVICE", filename);
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