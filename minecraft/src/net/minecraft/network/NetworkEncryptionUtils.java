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

	public static byte[] generateServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) {
		try {
			return hash("SHA-1", baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (UnsupportedEncodingException var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private static byte[] hash(String algorithm, byte[]... data) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);

			for (byte[] bs : data) {
				messageDigest.update(bs);
			}

			return messageDigest.digest();
		} catch (NoSuchAlgorithmException var7) {
			var7.printStackTrace();
			return null;
		}
	}

	public static PublicKey readEncodedPublicKey(byte[] bs) {
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

	public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) {
		return new SecretKeySpec(decrypt(privateKey, encryptedSecretKey), "AES");
	}

	@Environment(EnvType.CLIENT)
	public static byte[] encrypt(Key key, byte[] data) {
		return crypt(1, key, data);
	}

	public static byte[] decrypt(Key key, byte[] data) {
		return crypt(2, key, data);
	}

	private static byte[] crypt(int opMode, Key key, byte[] data) {
		try {
			return crypt(opMode, key.getAlgorithm(), key).doFinal(data);
		} catch (IllegalBlockSizeException var4) {
			var4.printStackTrace();
		} catch (BadPaddingException var5) {
			var5.printStackTrace();
		}

		LOGGER.error("Cipher data failed!");
		return null;
	}

	private static Cipher crypt(int opMode, String algorithm, Key key) {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(opMode, key);
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

	public static Cipher cipherFromKey(int opMode, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (GeneralSecurityException var3) {
			throw new RuntimeException(var3);
		}
	}
}
