package com.example.profilecloudexample;

import com.emotiv.emotivcloud.EmotivCloudClient;
import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;
import com.emotiv.insight.IEdk.IEE_Event_t;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean lock = false;
	int userId;
	boolean cloudConnected                 = false;
	boolean headsetConnected               = false;
	int engineUserID           			   = 0;
	int  userCloudID                       = -1;
	Button Save_profile,Load_profile,Delete_profile,Login_btn;
	EditText user_name, pass_word;
	TextView status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        
        Save_profile   = (Button)findViewById(R.id.button2);
        Load_profile   = (Button)findViewById(R.id.button3);
        Delete_profile = (Button)findViewById(R.id.button1);
        Login_btn      = (Button)findViewById(R.id.button4);
        user_name      = (EditText)findViewById(R.id.editText_name);
        pass_word      = (EditText)findViewById(R.id.editText_pass);
        status         = (TextView)findViewById(R.id.textView4);
        
        Save_profile.setEnabled(false);
        Load_profile.setEnabled(false);
        Delete_profile.setEnabled(false);
        
        Save_profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 if(!headsetConnected) {
					 status.setText("Connect headset first");
				        return;
				    }
				    if(userCloudID < 0) {
				    	status.setText("Login first");
				        return;
				    }
				    
				    if(EmotivCloudClient.EC_SaveUserProfile(userCloudID, engineUserID, "test", EmotivCloudClient.profileFileType.TRAINING.ToInt() )) {
				    	status.setText("Save new profile successfully");
				    }
				    else {
				    	status.setText("Profile is existed or can't create new profile");
				    }

			}
		});
        Load_profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!headsetConnected) {
					status.setText("Connect headset first");
			        return;
			    }
			    if(userCloudID < 0) {
			    	status.setText("Login first");
			        return;
			    }
			    int profileID = EmotivCloudClient.EC_GetProfileId(userCloudID, "test");
			    if ( profileID < 0) {
			    	status.setText("Profile isnt existed");
			        return;
			    }
			    if(EmotivCloudClient.EC_LoadUserProfile(userCloudID, engineUserID, profileID,-1)) {
			    	status.setText("Load profile successfully");
			    }
			    else {
			    	status.setText("Cant load this profile");
			    }

			}
		});
        Delete_profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!headsetConnected) {
					status.setText("Connect headset first");
			        return;
			    }
			    if(userCloudID < 0) {
			    	status.setText("Login first");
			        return;
			    }
			    int profileID = EmotivCloudClient.EC_GetProfileId(userCloudID, "test");
			    if ( profileID < 0) {
			    	status.setText("Profile isnt existed");
			        return;
			    }
			    if(EmotivCloudClient.EC_DeleteUserProfile(userCloudID, profileID)){
			    	status.setText("Remove profile successfully");
			    }
			    else {
			    	status.setText("Cant load this profile");
			    }
			}
		});
        Login_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!cloudConnected) {
			        cloudConnected = EmotivCloudClient.EC_Connect(MainActivity.this);
			        if(!cloudConnected) {
			            status.setText("Please check internet connection and connect again");
			            return;
			        }
			    }
			    if(user_name.getTextSize() == 0 || pass_word.getTextSize() == 0) {
			    	status.setText("Enter username and password");
			        return;
			    }
			    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(Login_btn.getWindowToken(), 
	                                      InputMethodManager.RESULT_UNCHANGED_SHOWN);
			    if(EmotivCloudClient.EC_Login(user_name.getText().toString(),pass_word.getText().toString())) {
			    	status.setText("Login successfully");
			        userCloudID = EmotivCloudClient.EC_GetUserDetail();
			        if(EmotivCloudClient.EC_GetUserDetail() != -1) {
			            Save_profile.setEnabled(true);
			            Load_profile.setEnabled(true);
			            Delete_profile.setEnabled(true);
			        }
			        else {
			        	status.setText("Cant get user detail. Please try again");
			        }
			    }
			    else {
			    	status.setText("Username or password is wrong. Check again");
			    }
			}
		});
      //Connect to emoEngine
      		IEdk.IEE_EngineConnect(this,"");
      		Thread processingThread=new Thread()
      		{
      			@Override
      			public void run() {
      				// TODO Auto-generated method stub
      				super.run();
      				while(true)
      				{
      					try
      					{
      						handler.sendEmptyMessage(0);
      						handler.sendEmptyMessage(1);
      						Thread.sleep(5);
      					}
      					
      					catch (Exception ex)
      					{
      						ex.printStackTrace();
      					}
      				}
      			}
      		};		
      		processingThread.start();
      	}
      	
      	Handler handler = new Handler() {
      		@Override
      		public void handleMessage(Message msg) {
      			switch (msg.what) {

      			case 0:
      				int state = IEdk.IEE_EngineGetNextEvent();
      				if (state == IEdkErrorCode.EDK_OK.ToInt()) {
      					int eventType = IEdk.IEE_EmoEngineEventGetType();
      				    userId = IEdk.IEE_EmoEngineEventGetUserId();
      					if(eventType == IEE_Event_t.IEE_UserAdded.ToInt()){
      						Log.e("SDK","User added");
      						headsetConnected = true;
      					}
      					if(eventType == IEE_Event_t.IEE_UserRemoved.ToInt()){
      						Log.e("SDK","User removed");
      						headsetConnected = false;
      					}
      				}
      				
      				break;
      			case 1:
					/*Connect device with Insight headset*/
      				int number = IEdk.IEE_GetInsightDeviceCount();
      				if(number != 0) {
      					if(!lock){
      						lock = true;
      						IEdk.IEE_ConnectInsightDevice(0);
      					}
      				}
					/*************************************/
					/*Connect device with Epoc Plus headset*/
//					int number = IEdk.IEE_GetEpocPlusDeviceCount();
//					if(number != 0) {
//						if(!lock){
//							lock = true;
//							IEdk.IEE_ConnectEpocPlusDevice(0,false);
//						}
//					}
					/*************************************/
      				else lock = false;
      				break;
      			}
      		   }
      		};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
