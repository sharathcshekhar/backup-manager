package com.example.backupmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

public class BackupService extends Service {
		public static final int REMOTE_READ = 0;
		public static final int REMOTE_WRITE = 1;
		public static final int REMOTE_READ_DONE = 2;
		public static final int REMOTE_WRITE_DONE = 3;
		public static final int REMOTE_READ_FAILED = 4;
		
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
				int msgType = msg.what;
				Bundle data = msg.getData();
				String filename = data.getString("filename");
				Messenger replyMsg = msg.replyTo;
			    switch(msgType) {
			    case REMOTE_READ:
			    	Log.d("BACKUP_SERVICE", "READ msg filename: " + filename != null ? filename : "null");
			    	DropBoxParams r_params = new DropBoxParams();
			    	r_params.mFilename = filename;
			    	r_params.mResp = replyMsg;
			    	new DropBoxDownload().execute(r_params);
			    	break;
			    case REMOTE_WRITE:
			    	Log.d("BACKUP_SERVICE", "WRITE msg filename: " + filename != null ? filename : "null");
			    	DropBoxParams w_params = new DropBoxParams();
			    	w_params.mFilename = filename;
			    	w_params.mResp = replyMsg;
		           	new DropBoxUpload().execute(w_params);
		           	break;
			    default:
			    	Log.d("BACKUP_SERVICE", "Unknown command");
			    	break;
			    }
	         }
	     }

		class DropBoxParams {
			String mFilename;
			Messenger mResp;
		}
		
		class DropBoxDownload extends AsyncTask <DropBoxParams, Void, Void> {

			@Override
			protected Void doInBackground(DropBoxParams... params) {
				String filename = params[0].mFilename;
				Messenger replyMsg = params[0].mResp;
				try {
					File file = new File(MainActivity.CACHE_PATH, filename);
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					String rev;
					try {
						Entry metadata = MainActivity.mDBApi.metadata(filename, 1, null, true, null);
						rev = metadata.rev;
					} catch (DropboxServerException e) {
						rev = null;
					}
					DropboxFileInfo info = MainActivity.mDBApi.getFile(filename, rev, fos, null);
					Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
					replyMsg.send(Message.obtain(null, REMOTE_READ_DONE));
				} catch (DropboxServerException e) {
					if (e.error == 404) {
						try {
							replyMsg.send(Message.obtain(null, REMOTE_READ_FAILED));
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						e.printStackTrace();
					}
				}
				 catch (DropboxException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			
		}
		
		class DropBoxUpload extends AsyncTask <DropBoxParams, Void, Void> {

			@Override
			protected Void doInBackground(DropBoxParams... params) {
				String filename = params[0].mFilename;
				Messenger replyMsg = params[0].mResp;
				
				try {
					File file = new File(MainActivity.CACHE_PATH, filename);
					FileInputStream fs = new FileInputStream(file);

					//	InputStream inputStream = new ByteArrayInputStream("test".getBytes());
					Entry response = null;

					Log.d("BACKUP_SERVICE", MainActivity.mDBApi.accountInfo().toString());

					response = MainActivity.mDBApi.putFile(filename, fs,
							file.length(), null, null);

					Log.i("DbExampleLog", "The uploaded file's rev is: " + response != null ? response.rev : "null");
					fs.close();
					replyMsg.send(Message.obtain(null, REMOTE_WRITE_DONE));
				} catch (DropboxException e) {
					e.printStackTrace();

				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				return null;
			}
		}
	
		final Messenger myMessenger = new Messenger(new IncomingHandler());

		@Override
		public IBinder onBind(Intent intent) {
			return myMessenger.getBinder();
		}
}