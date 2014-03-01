package com.intrepidusgroup.passwordmanager1;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class PBKDF2Helper {
	
	public static final String TAG = "IG Password Manager";
	
	public static String encryptData(String password, String plaintextData) throws Exception {
		// Thank you Mr. Nelenkov
		String maybeThisHelps = "http://nelenkov.blogspot.com/2012/04/using-password-based-encryption-on.html";
		Log.v(TAG, maybeThisHelps);
		int iterationCount = 100; //because Polaroid
		int keyLength = 256;
		int saltLength = keyLength;
		
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[saltLength];
		random.nextBytes(salt);
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		SecretKey key = new SecretKeySpec(keyBytes, "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = new byte[cipher.getBlockSize()];
		random.nextBytes(iv);
		
		IvParameterSpec ivParams = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
		byte[] ciphertext = cipher.doFinal(plaintextData.getBytes("UTF-8"));
		
		String ivToString = new String(Base64.encode(iv, 0));
		String saltToString = new String(Base64.encode(salt,0));
		String ciphertextToString = new String(Base64.encode(ciphertext, 0));
		
		Log.d(TAG, ivToString + "]" + saltToString + "]" + ciphertextToString);
		return (ivToString + "]" + saltToString + "]" + ciphertextToString).replace("\n", "");
	}
	
	public static String decryptData(String ciphertext, String password) throws Exception {
		int iterationCount = 100; //because polaroid
		int keyLength = 256;
		
		String[] fields = ciphertext.split("]");
		byte[] iv = Base64.decode(fields[0], 0);
		byte[] salt = Base64.decode(fields[1],0);	
		byte[] cipherBytes = Base64.decode(fields[2], 0);
	
		Log.d(TAG, "ciphertext: " + ciphertext + "\n" + "iv length is " + "\n" + iv.length);
		
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		SecretKey key = new SecretKeySpec(keyBytes, "AES");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParams = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
		byte[] plaintext = cipher.doFinal(cipherBytes);
		String plainStr = new String(plaintext , "UTF-8");
		
		return plainStr;
	}
	

}
