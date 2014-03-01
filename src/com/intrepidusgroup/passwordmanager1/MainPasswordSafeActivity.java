package com.intrepidusgroup.passwordmanager1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainPasswordSafeActivity extends Activity {

	public final String TAG = "IG Password Manager";
	private String filepath = "/encrypted_security.txt";
	private String password = ""; //declaring as global as the entire activity uses this for various purposes
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_password_safe);
		// Show the Up button in the action bar.
		setupActionBar();
		TextView viewCurrentSecret = (TextView) findViewById(R.id.textViewSecret);
		
		// get the Intent that was sent here
		Intent intent = getIntent();
		Log.d(TAG, "deBUG " + password);
		if (intent.getExtras().containsKey(getString(R.string.intentExtraPasswordString))) {
			Log.d(TAG, "deBUG INSIDE " + password);
			password = intent.getExtras().getString(getString(R.string.intentExtraPasswordString));
			Log.d(TAG, "deBUG INSIDE again" + password);
			String currentSecret = retrieveCurrentSecret(password);
			viewCurrentSecret.setText(currentSecret);
			viewCurrentSecret.setTextColor(Color.RED);
			
		}
		else {
			// We got no extra in the intents that we were expecting
			Log.e(TAG, "Weird. Empty intent arrived");
		}		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_password_safe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveNewSecretOnSubmitClick(View v) {
		// Pull the credentials
		EditText txtUser = (EditText) findViewById(R.id.editTextEnterSecret);
		EditText txtPass = (EditText) findViewById(R.id.editTextReenterSecret);
		
		if (txtUser.getText().toString().length() == 0 || txtPass.getText().toString().length() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.popupEmptyValue), Toast.LENGTH_SHORT).show();
			return;
		}
		
		String theSecret = txtUser.getText().toString() + ":" + txtPass.getText().toString();
		
		// proceed encrypt the secret and save it to Shared Prefs
		String encryptedSecret = "";
		
		try {
			// Password comes from the Intent extra that the global variable has been set to in onCreate()
			encryptedSecret = PBKDF2Helper.encryptData(password, theSecret);
			//encryptedSecret = PBKDF2Helper.encryptData("Intrepidus", theSecret);
		}
		catch (Exception e) {
			Log.e(TAG, "Problem encrypting current secret: " + e.getMessage() + "\n" + Log.getStackTraceString(e));
			return;
		}
		
		//save to sdcard
		String txtData = encryptedSecret;
		File myFile = new File(Environment.getExternalStorageDirectory().getPath() + filepath);
        try {
			myFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       
        try {
        	FileOutputStream fOut;
        	fOut = new FileOutputStream(myFile);
        	OutputStreamWriter myOutWriter = 
                     new OutputStreamWriter(fOut);
			myOutWriter.append(txtData);
			myOutWriter.close();
	        fOut.close();
	        Log.d(TAG, "sHIT Is on the sdcard yo");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "OMG Such sdcard based death");
		}
        
	
		// if encryption was successful, save in saved prefs
		Context context = this.getApplicationContext();
		SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preferences_file_string), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(getString(R.string.prefs_key_1_string), encryptedSecret);
		editor.commit();
		
		Toast.makeText(getApplicationContext(), getString(R.string.popupSecretSavedSuccessfully), Toast.LENGTH_SHORT).show();
		this.finish();

	}
	
	public String retrieveCurrentSecret(String password) {
		String decryptedSecret = "";
		Context context = this.getApplicationContext();
		SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preferences_file_string), Context.MODE_PRIVATE);
		String encryptedSecret = sharedPrefs.getString(getString(R.string.prefs_key_1_string), "");
		
		
		try {
			File myFile = new File(Environment.getExternalStorageDirectory().getPath() + filepath);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
            
			Log.d(TAG, "File buffer output is " + aBuffer);
            myReader.close();
        } catch (Exception e) {
            Log.d(TAG, "GAH - no likey file open buffer");
            e.printStackTrace();
        }
		
		// Try to decrypt the shared prefs value with the given password
		try {
			decryptedSecret = PBKDF2Helper.decryptData(encryptedSecret, password);
		}
		catch (Exception e) {
			Log.e(TAG, "Problem decrypting current secret: " + e.getMessage() + "\n" + Log.getStackTraceString(e));
		}
		

		
		return decryptedSecret;
	}
}
