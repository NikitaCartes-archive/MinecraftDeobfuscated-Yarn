package net.minecraft;

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

public class class_3515 {
	private static final Logger field_15652 = LogManager.getLogger();

	@Environment(EnvType.CLIENT)
	public static SecretKey method_15239() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			return keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException var1) {
			throw new Error(var1);
		}
	}

	public static KeyPair method_15237() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException var1) {
			var1.printStackTrace();
			field_15652.error("Key pair generation failed!");
			return null;
		}
	}

	public static byte[] method_15240(String string, PublicKey publicKey, SecretKey secretKey) {
		try {
			return method_15236("SHA-1", string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (UnsupportedEncodingException var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private static byte[] method_15236(String string, byte[]... bs) {
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

		field_15652.error("Public key reconstitute failed!");
		return null;
	}

	public static SecretKey method_15234(PrivateKey privateKey, byte[] bs) {
		return new SecretKeySpec(method_15243(privateKey, bs), "AES");
	}

	@Environment(EnvType.CLIENT)
	public static byte[] method_15238(Key key, byte[] bs) {
		return method_15244(1, key, bs);
	}

	public static byte[] method_15243(Key key, byte[] bs) {
		return method_15244(2, key, bs);
	}

	private static byte[] method_15244(int i, Key key, byte[] bs) {
		try {
			return method_15241(i, key.getAlgorithm(), key).doFinal(bs);
		} catch (IllegalBlockSizeException var4) {
			var4.printStackTrace();
		} catch (BadPaddingException var5) {
			var5.printStackTrace();
		}

		field_15652.error("Cipher data failed!");
		return null;
	}

	private static Cipher method_15241(int i, String string, Key key) {
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

		field_15652.error("Cipher creation failed!");
		return null;
	}

	public static Cipher method_15235(int i, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(i, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (GeneralSecurityException var3) {
			throw new RuntimeException(var3);
		}
	}
}
