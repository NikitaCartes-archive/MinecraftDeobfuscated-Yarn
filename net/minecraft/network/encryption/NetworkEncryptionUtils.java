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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.encryption.NetworkEncryptionException;

public class NetworkEncryptionUtils {
    @Environment(value=EnvType.CLIENT)
    public static SecretKey generateKey() throws NetworkEncryptionException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static KeyPair generateServerKeyPair() throws NetworkEncryptionException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static byte[] generateServerId(String baseServerId, PublicKey publicKey, SecretKey secretKey) throws NetworkEncryptionException {
        try {
            return NetworkEncryptionUtils.hash(baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    private static byte[] hash(byte[] ... bs) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        for (byte[] cs : bs) {
            messageDigest.update(cs);
        }
        return messageDigest.digest();
    }

    @Environment(value=EnvType.CLIENT)
    public static PublicKey readEncodedPublicKey(byte[] bs) throws NetworkEncryptionException {
        try {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(bs);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(encodedKeySpec);
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    public static SecretKey decryptSecretKey(PrivateKey privateKey, byte[] encryptedSecretKey) throws NetworkEncryptionException {
        byte[] bs = NetworkEncryptionUtils.decrypt(privateKey, encryptedSecretKey);
        try {
            return new SecretKeySpec(bs, "AES");
        } catch (Exception exception) {
            throw new NetworkEncryptionException(exception);
        }
    }

    @Environment(value=EnvType.CLIENT)
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

