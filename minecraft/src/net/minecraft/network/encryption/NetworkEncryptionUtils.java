package net.minecraft.network.encryption;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class NetworkEncryptionUtils {
	private static final String AES = "AES";
	private static final int AES_KEY_LENGTH = 128;
	private static final String RSA = "RSA";
	private static final int RSA_KEY_LENGTH = 1024;
	private static final String ISO_8859_1 = "ISO_8859_1";
	private static final String SHA1 = "SHA-1";

	public static SecretKey generateKey() throws NetworkEncryptionException {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			return keyGenerator.generateKey();
		} catch (Exception var1) {
			throw new NetworkEncryptionException(var1);
		}
	}

	public static KeyPair generateServerKeyPair() throws NetworkEncryptionException {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception var1) {
			throw new NetworkEncryptionException(var1);
		}
	}

	public static byte[] generateServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) throws NetworkEncryptionException {
		try {
			return hash(baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (Exception var4) {
			throw new NetworkEncryptionException(var4);
		}
	}

	private static byte[] hash(byte[]... bytes) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

		for (byte[] bs : bytes) {
			messageDigest.update(bs);
		}

		return messageDigest.digest();
	}

	public static PublicKey readEncodedPublicKey(byte[] bytes) throws NetworkEncryptionException {
		try {
			EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(encodedKeySpec);
		} catch (Exception var3) {
			throw new NetworkEncryptionException(var3);
		}
	}

	public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) throws NetworkEncryptionException {
		byte[] bs = decrypt(privateKey, encryptedSecretKey);

		try {
			return new SecretKeySpec(bs, "AES");
		} catch (Exception var4) {
			throw new NetworkEncryptionException(var4);
		}
	}

	public static byte[] encrypt(Key key, byte[] data) throws NetworkEncryptionException {
		return crypt(1, key, data);
	}

	public static byte[] decrypt(Key key, byte[] data) throws NetworkEncryptionException {
		return crypt(2, key, data);
	}

	private static byte[] crypt(int opMode, Key key, byte[] data) throws NetworkEncryptionException {
		try {
			return crypt(opMode, key.getAlgorithm(), key).doFinal(data);
		} catch (Exception var4) {
			throw new NetworkEncryptionException(var4);
		}
	}

	private static Cipher crypt(int opMode, String algorithm, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(opMode, key);
		return cipher;
	}

	public static Cipher cipherFromKey(int opMode, Key key) throws NetworkEncryptionException {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (Exception var3) {
			throw new NetworkEncryptionException(var3);
		}
	}
}
