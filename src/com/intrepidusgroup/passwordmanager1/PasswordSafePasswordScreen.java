package com.intrepidusgroup.passwordmanager1;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordSafePasswordScreen extends Activity {

public final String TAG = "IG Password Manager";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_safe_password_screen);

		// Check shared prefs - if there's no key saved suggest registration
		Context context = this.getApplicationContext();
		SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preferences_file_string), Context.MODE_PRIVATE);

		if (!sharedPrefs.contains(getString(R.string.prefs_password_test_string))) {
			// there's no key, send them off to a password entry activity
			Intent intent = new Intent();
			intent.setClassName("com.intrepidusgroup.passwordmanager1", "com.intrepidusgroup.passwordmanager1.PasswordRegistrationActivity");
		    if(this.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
	            this.startActivity(intent);
	        } else {
	            Log.d(TAG, "Could not start PasswordRegistrationActivity");
	        }		
		}
		

	}
	
	public void openSafePasswordEntryOnSubmitClick(View v) throws Exception {
		
		// read in the password value in the box
		EditText keyedInValueBox = (EditText) findViewById(R.id.editPasswordText);
		Context context = this.getApplicationContext();
		SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preferences_file_string), Context.MODE_PRIVATE);
		
		String encryptedPassword = sharedPrefs.getString(getString(R.string.prefs_password_test_string), "");
		String password = keyedInValueBox.getText().toString();
		String decryptedData = "";
		
		try {
			decryptedData = PBKDF2Helper.decryptData(encryptedPassword, password);
		}
		catch (Exception e) {
			Log.e(TAG, "Can't decrypt entered password because of an exception: " + e.getMessage());
		}
		
		
		//if (1 == 2){
		//	Log.e(TAG, "password " + password + ", decrypted data: " + decryptedData); 
		//}
		
		/*
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    Log.e(TAG, "Bluetooth adapter not found");
		} else {
			Log.i(TAG, "Bluetooth found: " + decryptedData);
		}
		*/
		 
		
		if (decryptedData.equals("Intrepidus")) {
			// the password is valid, success, now can safely open the safe with the password
			//Toast.makeText(getApplicationContext(), "Password correct", Toast.LENGTH_SHORT).show();
			Intent sendIntent = new Intent();
			sendIntent.setClassName("com.intrepidusgroup.passwordmanager1", "com.intrepidusgroup.passwordmanager1.MainPasswordSafeActivity");
			sendIntent.putExtra(getString(R.string.intentExtraPasswordString), password);
			//sendIntent.putExtra(getString(R.string.intentExtraPasswordString), "Intrepidus");
			sendIntent.setType("text/plain");
			
			try {
				startActivity(sendIntent);
			}
			catch (android.content.ActivityNotFoundException e) {
				Log.e(TAG, "Problem sending intent: " + e.getMessage());
			}
		}
		else {
			// failed to open safe - wrong password
			Toast.makeText(getApplicationContext(), getString(R.string.popupOpenSafeFailure), Toast.LENGTH_SHORT).show();
		}
	
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password_safe_password_screen, menu);
		return true;
	}

}
