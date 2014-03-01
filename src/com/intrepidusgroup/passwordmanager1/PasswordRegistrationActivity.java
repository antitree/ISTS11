package com.intrepidusgroup.passwordmanager1;

import android.os.Bundle;
import android.provider.UserDictionary;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class PasswordRegistrationActivity extends Activity {
	
	public final String TAG = "IG Password Manager";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_registration);
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.password_registration, menu);
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
	
	@SuppressWarnings("deprecation")
	public void saveNewPasswordOnSubmitClick(View v) {
		// Compare two passwords
		EditText password = (EditText) findViewById(R.id.editTextEnterNewPassword);
		EditText confirmPassword = (EditText) findViewById(R.id.editTextConfirmNewPassword);
		
		if (password.getText().toString().length() == 0 || confirmPassword.getText().toString().length() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.popupEmptyValue), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
			// if passwords do not match, display error message and exit
			Toast.makeText(getApplicationContext(), getString(R.string.popupPasswordsDoNotMatch), Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		//!!!
		UserDictionary.Words.addWord( this , password.getText().toString(), 1, UserDictionary.Words.LOCALE_TYPE_CURRENT);
		
		//!!!
		// AES all the things
		AESHelper crypt = new AESHelper();
		String cryptpwerd = "";
		try {
			cryptpwerd = AESHelper.bytesToHex(crypt.encrypt(password.getText().toString()));
			Log.d(TAG, "Don't worry the password is encrypted: " + cryptpwerd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Put it to prefs
		Context aescontext = this.getApplicationContext();
		SharedPreferences sharedPrefs1 = aescontext.getSharedPreferences(getString(R.string.preferences_file_string), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor1 = sharedPrefs1.edit();
		//editor1.putString(getString(R.string.prefs_aes_string), cryptpwerd);
		editor1.putString(getString(R.string.prefs_aes_string), password.getText().toString());
		editor1.commit();
				
			
		// proceed with registration - encrypt the password and save it to Shared Prefs
		String encryptedString = "";
		
		try {
			encryptedString = PBKDF2Helper.encryptData(password.getText().toString(), "Intrepidus");
		}
		catch (Exception e) {
			Log.e(TAG, "Problem encrypting data: " + e.getMessage() + "\n" + Log.getStackTraceString(e));
			return;
		}
	
		// if encryption was successful, save in saved prefs
		Context context = this.getApplicationContext();
		SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string.preferences_file_string), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(getString(R.string.prefs_password_test_string), encryptedString);
		editor.commit();
		Toast.makeText(getApplicationContext(), getString(R.string.popupPINSavedSuccessfully), Toast.LENGTH_SHORT).show();
		this.finish();

	}

}
