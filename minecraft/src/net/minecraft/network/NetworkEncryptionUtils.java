package net.minecraft.network;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkEncryptionUtils {
	private static final Logger LOGGER = LogManager.getLogger();

	@Environment(EnvType.CLIENT)
	public static SecretKey generateKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			return keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException var1) {
			throw new Error(var1);
		}
	}

	public static KeyPair generateServerKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException var1) {
			var1.printStackTrace();
			LOGGER.error("Key pair generation failed!");
			return null;
		}
	}

	public static byte[] method_15240(String string, PublicKey publicKey, SecretKey secretKey) {
		try {
			return hash("SHA-1", string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (UnsupportedEncodingException var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private static byte[] hash(String string, byte[]... bs) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(string);

			for (byte[] cs : bs) {
				messageDigest.update(cs);
			}

			return messageDigest.digest();
		} catch (NoSuchAlgorithmException var7) {
			var7.printStackTrace();
			return null;
		}
	}

	public static PublicKey method_15242(byte[] bs) {
		try {
			EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bs);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(encodedKeySpec);
		} catch (NoSuchAlgorithmException var3) {
		} catch (InvalidKeySpecException var4) {
		}

		LOGGER.error("Public key reconstitute failed!");
		return null;
	}

	public static SecretKey method_15234(PrivateKey privateKey, byte[] bs) {
		return new SecretKeySpec(decrypt(privateKey, bs), "AES");
	}

	@Environment(EnvType.CLIENT)
	public static byte[] encrypt(Key key, byte[] bs) {
		return crypt(1, key, bs);
	}

	public static byte[] decrypt(Key key, byte[] bs) {
		return crypt(2, key, bs);
	}

	private static byte[] crypt(int i, Key key, byte[] bs) {
		try {
			return crypt(i, key.getAlgorithm(), key).doFinal(bs);
		} catch (IllegalBlockSizeException var4) {
			var4.printStackTrace();
		} catch (BadPaddingException var5) {
			var5.printStackTrace();
		}

		LOGGER.error("Cipher data failed!");
		return null;
	}

	private static Cipher crypt(int i, String string, Key key) {
		try {
			Cipher cipher = Cipher.getInstance(string);
			cipher.init(i, key);
			return cipher;
		} catch (InvalidKeyException var4) {
			var4.printStackTrace();
		} catch (NoSuchAlgorithmException var5) {
			var5.printStackTrace();
		} catch (NoSuchPaddingException var6) {
			var6.printStackTrace();
		}

		LOGGER.error("Cipher creation failed!");
		return null;
	}

	public static Cipher cipherFromKey(int i, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(i, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (GeneralSecurityException var3) {
			throw new RuntimeException(var3);
		}
	}
}
