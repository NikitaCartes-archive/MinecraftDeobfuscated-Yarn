package net.minecraft.network;

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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5525;

public class NetworkEncryptionUtils {
	@Environment(EnvType.CLIENT)
	public static SecretKey generateKey() throws class_5525 {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			return keyGenerator.generateKey();
		} catch (Exception var1) {
			throw new class_5525(var1);
		}
	}

	public static KeyPair generateServerKeyPair() throws class_5525 {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception var1) {
			throw new class_5525(var1);
		}
	}

	public static byte[] generateServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) throws class_5525 {
		try {
			return hash(baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (Exception var4) {
			throw new class_5525(var4);
		}
	}

	private static byte[] hash(byte[]... bs) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

		for (byte[] cs : bs) {
			messageDigest.update(cs);
		}

		return messageDigest.digest();
	}

	@Environment(EnvType.CLIENT)
	public static PublicKey readEncodedPublicKey(byte[] bs) throws class_5525 {
		try {
			EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bs);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(encodedKeySpec);
		} catch (Exception var3) {
			throw new class_5525(var3);
		}
	}

	public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) throws class_5525 {
		byte[] bs = decrypt(privateKey, encryptedSecretKey);

		try {
			return new SecretKeySpec(bs, "AES");
		} catch (Exception var4) {
			throw new class_5525(var4);
		}
	}

	@Environment(EnvType.CLIENT)
	public static byte[] encrypt(Key key, byte[] data) throws class_5525 {
		return crypt(1, key, data);
	}

	public static byte[] decrypt(Key key, byte[] data) throws class_5525 {
		return crypt(2, key, data);
	}

	private static byte[] crypt(int opMode, Key key, byte[] data) throws class_5525 {
		try {
			return crypt(opMode, key.getAlgorithm(), key).doFinal(data);
		} catch (Exception var4) {
			throw new class_5525(var4);
		}
	}

	private static Cipher crypt(int opMode, String algorithm, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(opMode, key);
		return cipher;
	}

	public static Cipher cipherFromKey(int opMode, Key key) throws class_5525 {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (Exception var3) {
			throw new class_5525(var3);
		}
	}
}
