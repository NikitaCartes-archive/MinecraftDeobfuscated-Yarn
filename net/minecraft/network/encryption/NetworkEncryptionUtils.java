/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.encryption;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.network.encryption.NetworkEncryptionException;

public class NetworkEncryptionUtils {
    private static final String field_29830 = "AES";
    private static final int field_29831 = 128;
    private static final String field_29832 = "RSA";
    private static final int field_29833 = 1024;
    private static final String field_29834 = "ISO_8859_1";
    private static final String field_29835 = "SHA-1";

    public static SecretKey generateKey() throws NetworkEncryptionException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(field_29830);
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static KeyPair generateServerKeyPair() throws NetworkEncryptionException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(field_29832);
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static byte[] generateServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) throws NetworkEncryptionException {
        try {
            return NetworkEncryptionUtils.hash(baseServerId.getBytes(field_29834), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    private static byte[] hash(byte[] ... bs) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(field_29835);
        for (byte[] cs : bs) {
            messageDigest.update(cs);
        }
        return messageDigest.digest();
    }

    public static PublicKey readEncodedPublicKey(byte[] bs) throws NetworkEncryptionException {
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bs);
            KeyFactory keyFactory = KeyFactory.getInstance(field_29832);
            return keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) throws NetworkEncryptionException {
        byte[] bs = NetworkEncryptionUtils.decrypt(privateKey, encryptedSecretKey);
        try {
            return new SecretKeySpec(bs, field_29830);
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static byte[] encrypt(Key key, byte[] data) throws NetworkEncryptionException {
        return NetworkEncryptionUtils.crypt(1, key, data);
    }

    public static byte[] decrypt(Key key, byte[] data) throws NetworkEncryptionException {
        return NetworkEncryptionUtils.crypt(2, key, data);
    }

    private static byte[] crypt(int opMode, Key key, byte[] data) throws NetworkEncryptionException {
        try {
            return NetworkEncryptionUtils.crypt(opMode, key.getAlgorithm(), key).doFinal(data);
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
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
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }
}

