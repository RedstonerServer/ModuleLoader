package com.redstoner.modules.loginsecurity;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CryptographyHandler {
	public static String hash(String password, String salt) {
		String algorithm = "PBKDF2WithHmacSHA256";
		int derivedKeyLength = 256;
		int iterations = 200000;
		byte[] decodedSalt = Base64.getDecoder().decode(salt.getBytes());
		
		KeySpec spec = new PBEKeySpec(password.toCharArray(), decodedSalt, iterations, derivedKeyLength);
		
		byte[] hashed = null;
		
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
			
			hashed = f.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return Base64.getEncoder().encodeToString(hashed).substring(0, 43);
	}
	
	public static boolean verify(String password, String salt, String hash) {
		return hash(password, salt).equals(hash);
	}
	
	public static boolean verify(String password, String stored) {
		String[] split = stored.split("\\$");
		
		return verify(password, split[3], split[4]);
	}
	
	public static String generateSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
		return Base64.getEncoder().encodeToString(salt).substring(0, 22);
	}
}
