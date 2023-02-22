package net.minecraft.network.encryption;

import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.network.PacketByteBuf;

/**
 * Utilities for encryption, decryption, signing, and hashing of data.
 * 
 * @apiNote Methods in this class usually throw {@link NetworkEncryptionException}
 * when the operation fails. This is a checked exception and thus must be caught
 * when using the methods.
 */
public class NetworkEncryptionUtils {
	private static final String AES = "AES";
	private static final int AES_KEY_LENGTH = 128;
	private static final String RSA = "RSA";
	private static final int RSA_KEY_LENGTH = 1024;
	private static final String ISO_8859_1 = "ISO_8859_1";
	private static final String SHA1 = "SHA-1";
	public static final String SHA256_WITH_RSA = "SHA256withRSA";
	public static final int SHA256_BITS = 256;
	private static final String RSA_PRIVATE_KEY_PREFIX = "-----BEGIN RSA PRIVATE KEY-----";
	private static final String RSA_PRIVATE_KEY_SUFFIX = "-----END RSA PRIVATE KEY-----";
	public static final String RSA_PUBLIC_KEY_PREFIX = "-----BEGIN RSA PUBLIC KEY-----";
	private static final String RSA_PUBLIC_KEY_SUFFIX = "-----END RSA PUBLIC KEY-----";
	public static final String LINEBREAK = "\n";
	public static final Encoder BASE64_ENCODER = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));
	/**
	 * The codec for RSA public keys.
	 * 
	 * @implNote The key is encoded using the PEM format.
	 * 
	 * @see #encodeRsaPublicKey(PublicKey)
	 * @see #decodeRsaPublicKeyPem(String)
	 */
	public static final Codec<PublicKey> RSA_PUBLIC_KEY_CODEC = Codec.STRING.comapFlatMap(key -> {
		try {
			return DataResult.success(decodeRsaPublicKeyPem(key));
		} catch (NetworkEncryptionException var2) {
			return DataResult.error(var2::getMessage);
		}
	}, NetworkEncryptionUtils::encodeRsaPublicKey);
	/**
	 * The codec for RSA private keys.
	 * 
	 * @implNote The key is encoded using the PEM format.
	 * 
	 * @see #encodeRsaPrivateKey(PrivateKey)
	 * @see #decodeRsaPrivateKeyPem(String)
	 */
	public static final Codec<PrivateKey> RSA_PRIVATE_KEY_CODEC = Codec.STRING.comapFlatMap(key -> {
		try {
			return DataResult.success(decodeRsaPrivateKeyPem(key));
		} catch (NetworkEncryptionException var2) {
			return DataResult.error(var2::getMessage);
		}
	}, NetworkEncryptionUtils::encodeRsaPrivateKey);

	/**
	 * {@return a new {@value AES_KEY_LENGTH} bit AES secret key}
	 * 
	 * @throws NetworkEncryptionException when generation fails
	 */
	public static SecretKey generateSecretKey() throws NetworkEncryptionException {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			return keyGenerator.generateKey();
		} catch (Exception var1) {
			throw new NetworkEncryptionException(var1);
		}
	}

	/**
	 * {@return a new {@value RSA_KEY_LENGTH} bit RSA public/private key pair}
	 * 
	 * @throws NetworkEncryptionException when generation fails
	 */
	public static KeyPair generateServerKeyPair() throws NetworkEncryptionException {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception var1) {
			throw new NetworkEncryptionException(var1);
		}
	}

	/**
	 * {@return the computed server ID}
	 * 
	 * @implNote The server ID is a SHA-1 hash of ISO-8859-1 encoded {@code baseServerId},
	 * {@code publicKey}, and {@code secretKey}.
	 * 
	 * @throws NetworkEncryptionException when computation fails
	 */
	public static byte[] computeServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) throws NetworkEncryptionException {
		try {
			return hash(baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
		} catch (Exception var4) {
			throw new NetworkEncryptionException(var4);
		}
	}

	/**
	 * {@return the SHA-1 hash of {@code bytes}}
	 */
	private static byte[] hash(byte[]... bytes) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

		for (byte[] bs : bytes) {
			messageDigest.update(bs);
		}

		return messageDigest.digest();
	}

	/**
	 * Decodes a PEM-formatted string key.
	 * 
	 * <p>A PEM-formatted key is {@code prefix}, followed by Base64 encoded key,
	 * followed by {@code suffix}. Note that this method also allows Base64 encoded
	 * keys that have neither the prefix or the suffix.
	 * 
	 * @throws NetworkEncryptionException when the {@code decoder} throws, e.g. due to
	 * invalid key content
	 */
	private static <T extends Key> T decodePem(String key, String prefix, String suffix, NetworkEncryptionUtils.KeyDecoder<T> decoder) throws NetworkEncryptionException {
		int i = key.indexOf(prefix);
		if (i != -1) {
			i += prefix.length();
			int j = key.indexOf(suffix, i);
			key = key.substring(i, j + 1);
		}

		try {
			return decoder.apply(Base64.getMimeDecoder().decode(key));
		} catch (IllegalArgumentException var6) {
			throw new NetworkEncryptionException(var6);
		}
	}

	/**
	 * Decodes a PEM-formatted RSA private key.
	 * 
	 * <p>A PEM-formatted RSA private key is {@value #RSA_PRIVATE_KEY_PREFIX}, followed
	 * by Base64 encoded PCKS #8 encoded key, followed by {@value #RSA_PRIVATE_KEY_SUFFIX}.
	 * Note that this method also allows Base64 encoded keys that have neither the prefix
	 * or the suffix.
	 * 
	 * @throws NetworkEncryptionException when the key is malformed
	 * 
	 * @see #encodeRsaPrivateKey(PrivateKey)
	 * @see #encodeRsaPublicKey(PublicKey)
	 * @see #decodeRsaPublicKeyPem(String)
	 */
	public static PrivateKey decodeRsaPrivateKeyPem(String key) throws NetworkEncryptionException {
		return decodePem(key, "-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----", NetworkEncryptionUtils::decodeEncodedRsaPrivateKey);
	}

	/**
	 * Decodes a PEM-formatted RSA public key.
	 * 
	 * <p>A PEM-formatted RSA public key is {@value #RSA_PUBLIC_KEY_PREFIX}, followed
	 * by Base64 encoded X.509 encoded key, followed by {@value #RSA_PUBLIC_KEY_SUFFIX}.
	 * Note that this method also allows Base64 encoded keys that have neither the prefix
	 * or the suffix.
	 * 
	 * @throws NetworkEncryptionException when the key is malformed
	 * 
	 * @see #encodeRsaPrivateKey(PrivateKey)
	 * @see #encodeRsaPublicKey(PublicKey)
	 * @see #decodeRsaPrivateKeyPem(String)
	 */
	public static PublicKey decodeRsaPublicKeyPem(String key) throws NetworkEncryptionException {
		return decodePem(key, "-----BEGIN RSA PUBLIC KEY-----", "-----END RSA PUBLIC KEY-----", NetworkEncryptionUtils::decodeEncodedRsaPublicKey);
	}

	/**
	 * Encodes an RSA public {@code key} to a PEM-formatted key string.
	 * 
	 * <p>A PEM-formatted RSA public key is {@value #RSA_PUBLIC_KEY_PREFIX}, followed
	 * by Base64 encoded X.509 encoded key, followed by {@value #RSA_PUBLIC_KEY_SUFFIX}.
	 * 
	 * @throws IllegalArgumentException when non-RSA key is passed
	 * 
	 * @see #encodeRsaPrivateKey(PrivateKey)
	 * @see #decodeRsaPrivateKeyPem(String)
	 * @see #decodeRsaPublicKeyPem(String)
	 */
	public static String encodeRsaPublicKey(PublicKey key) {
		if (!"RSA".equals(key.getAlgorithm())) {
			throw new IllegalArgumentException("Public key must be RSA");
		} else {
			return "-----BEGIN RSA PUBLIC KEY-----\n" + BASE64_ENCODER.encodeToString(key.getEncoded()) + "\n-----END RSA PUBLIC KEY-----\n";
		}
	}

	/**
	 * Encodes an RSA private {@code key} to a PEM-formatted key string.
	 * 
	 * <p>A PEM-formatted RSA private key is {@value #RSA_PRIVATE_KEY_PREFIX}, followed
	 * by Base64 encoded PCKS #8 encoded key, followed by {@value #RSA_PRIVATE_KEY_SUFFIX}.
	 * 
	 * @throws IllegalArgumentException when non-RSA key is passed
	 * 
	 * @see #encodeRsaPublicKey(PublicKey)
	 * @see #decodeRsaPrivateKeyPem(String)
	 * @see #decodeRsaPublicKeyPem(String)
	 */
	public static String encodeRsaPrivateKey(PrivateKey key) {
		if (!"RSA".equals(key.getAlgorithm())) {
			throw new IllegalArgumentException("Private key must be RSA");
		} else {
			return "-----BEGIN RSA PRIVATE KEY-----\n" + BASE64_ENCODER.encodeToString(key.getEncoded()) + "\n-----END RSA PRIVATE KEY-----\n";
		}
	}

	/**
	 * Decodes a PCKS #8-encoded RSA private key.
	 * 
	 * @throws NetworkEncryptionException when the key is malformed
	 * 
	 * @see #decodeRsaPrivateKeyPem(String)
	 */
	private static PrivateKey decodeEncodedRsaPrivateKey(byte[] key) throws NetworkEncryptionException {
		try {
			EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(encodedKeySpec);
		} catch (Exception var3) {
			throw new NetworkEncryptionException(var3);
		}
	}

	/**
	 * Decodes a X.509-encoded RSA public key.
	 * 
	 * @throws NetworkEncryptionException when the key is malformed
	 * 
	 * @see #decodeRsaPublicKeyPem(String)
	 */
	public static PublicKey decodeEncodedRsaPublicKey(byte[] key) throws NetworkEncryptionException {
		try {
			EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(encodedKeySpec);
		} catch (Exception var3) {
			throw new NetworkEncryptionException(var3);
		}
	}

	/**
	 * Decrypts RSA-encrypted AES secret key.
	 * 
	 * @throws NetworkEncryptionException when the secret key is malformed
	 */
	public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) throws NetworkEncryptionException {
		byte[] bs = decrypt(privateKey, encryptedSecretKey);

		try {
			return new SecretKeySpec(bs, "AES");
		} catch (Exception var4) {
			throw new NetworkEncryptionException(var4);
		}
	}

	/**
	 * Encrypts a data. The algorithm is determined from the key used.
	 * 
	 * @throws NetworkEncryptionException when encryption fails, e.g. due to invalid key
	 * 
	 * @see #decrypt(Key, byte[])
	 * 
	 * @param key encryption key (e.g. AES secret key or RSA public key)
	 */
	public static byte[] encrypt(Key key, byte[] data) throws NetworkEncryptionException {
		return crypt(1, key, data);
	}

	/**
	 * Decrypts an encrypted data. The algorithm is determined from the key used.
	 * 
	 * @throws NetworkEncryptionException when decryption fails, e.g. due to invalid key
	 * 
	 * @see #encrypt(Key, byte[])
	 * 
	 * @param key decryption key (e.g. AES secret key or RSA private key)
	 */
	public static byte[] decrypt(Key key, byte[] data) throws NetworkEncryptionException {
		return crypt(2, key, data);
	}

	/**
	 * Low-level API to perform encryption or decryption operation.
	 * 
	 * @throws NetworkEncryptionException when the operation fails
	 */
	private static byte[] crypt(int opMode, Key key, byte[] data) throws NetworkEncryptionException {
		try {
			return createCipher(opMode, key.getAlgorithm(), key).doFinal(data);
		} catch (Exception var4) {
			throw new NetworkEncryptionException(var4);
		}
	}

	/**
	 * Creates a cipher to perform encryption or decryption operation.
	 */
	private static Cipher createCipher(int opMode, String algorithm, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(opMode, key);
		return cipher;
	}

	/**
	 * Creates an AES cipher from the key.
	 * 
	 * @throws NetworkEncryptionException when creation fails, e.g. due to invalid key
	 * 
	 * @param key the AES secret key
	 */
	public static Cipher cipherFromKey(int opMode, Key key) throws NetworkEncryptionException {
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
			return cipher;
		} catch (Exception var3) {
			throw new NetworkEncryptionException(var3);
		}
	}

	/**
	 * A functional interface that decodes an encoded key.
	 * 
	 * @apiNote Implementations should throw {@link NetworkEncryptionException}
	 * when the key is malformed.
	 * 
	 * @see NetworkEncryptionUtils#decodeEncodedRsaPrivateKey(byte[])
	 * @see NetworkEncryptionUtils#decodeEncodedRsaPublicKey(byte[])
	 */
	interface KeyDecoder<T extends Key> {
		T apply(byte[] key) throws NetworkEncryptionException;
	}

	/**
	 * Utilities for working with a secure random number generator.
	 */
	public static class SecureRandomUtil {
		private static final SecureRandom SECURE_RANDOM = new SecureRandom();

		/**
		 * {@return a random number generated with a cryptographically secure
		 * random number generator}
		 */
		public static long nextLong() {
			return SECURE_RANDOM.nextLong();
		}
	}

	/**
	 * A record holding a signature of a data and the salt added while signing. Note that
	 * the signature might not be actually present.
	 */
	public static record SignatureData(long salt, byte[] signature) {
		/**
		 * The signature data for data without signatures.
		 */
		public static final NetworkEncryptionUtils.SignatureData NONE = new NetworkEncryptionUtils.SignatureData(0L, ByteArrays.EMPTY_ARRAY);

		public SignatureData(PacketByteBuf buf) {
			this(buf.readLong(), buf.readByteArray());
		}

		/**
		 * {@return whether the signature data has a signature}
		 * 
		 * @apiNote This <strong>does not validate</strong> the signature itself.
		 */
		public boolean isSignaturePresent() {
			return this.signature.length > 0;
		}

		public static void write(PacketByteBuf buf, NetworkEncryptionUtils.SignatureData signatureData) {
			buf.writeLong(signatureData.salt);
			buf.writeByteArray(signatureData.signature);
		}

		public byte[] getSalt() {
			return Longs.toByteArray(this.salt);
		}
	}
}
